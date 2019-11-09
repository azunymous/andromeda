package net.igiari.andromeda.fetch;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import net.igiari.andromeda.cluster.PodController;
import net.igiari.andromeda.cluster.Status;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static net.igiari.andromeda.cluster.PodController.UNKNOWN_VERSION;
import static net.igiari.andromeda.cluster.PodControllerType.DEPLOYMENT;

public class PodControllers {
  private KubernetesClient kubernetesClient;

  private static final Pattern versionRegex = Pattern.compile("(\\d+\\.\\d+\\.\\d+)");
  private static final Pattern hashRegex = Pattern.compile(".*:(.{4,8}).*");

  public PodControllers(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }
  // TODO Refactor to combine shared logic with a passed in function

  public Optional<PodController> getDeployment(
      String namespaceName, Map<String, String> selector, String containerName) {
    return kubernetesClient.apps().deployments().inNamespace(namespaceName).withLabels(selector)
        .list().getItems().stream()
        .findFirst()
        .map(deployment -> createPodControllerFrom(deployment, containerName));
  }

  public Optional<PodController> getStatefulSet(
      String namespaceName, Map<String, String> selector, String containerName) {
    return kubernetesClient.apps().statefulSets().inNamespace(namespaceName).withLabels(selector)
        .list().getItems().stream()
        .findFirst()
        .map((statefulSet -> createPodControllerFrom(statefulSet, containerName)))
        .map(PodController::setTypeToStatefulSet);
  }

  private PodController createPodControllerFrom(Deployment controller, String containerName) {
    PodController podController =
        new PodController(controller.getMetadata().getName(), emptyList(), DEPLOYMENT);
    int specReplicas = controller.getSpec().getReplicas();
    int unavailableReplicas = controller.getStatus().getUnavailableReplicas();
    int availableReplicas = controller.getStatus().getAvailableReplicas();
    int readyReplicas = controller.getStatus().getReadyReplicas();

    podController.setStatus(
        determineStatusFrom(specReplicas, unavailableReplicas, availableReplicas, readyReplicas));
    podController.setVersion(
        determineVersionFrom(
                controller.getSpec().getTemplate().getSpec().getContainers(), containerName)
            .orElse(UNKNOWN_VERSION));
    return podController;
  }

  private PodController createPodControllerFrom(StatefulSet controller, String containerName) {
    PodController podController =
        new PodController(controller.getMetadata().getName(), emptyList(), DEPLOYMENT);
    int specReplicas = controller.getSpec().getReplicas();
    int unavailableReplicas = specReplicas - controller.getStatus().getCurrentReplicas();
    int availableReplicas = controller.getStatus().getCurrentReplicas();
    int readyReplicas = controller.getStatus().getReadyReplicas();

    podController.setStatus(
        determineStatusFrom(specReplicas, unavailableReplicas, availableReplicas, readyReplicas));
    podController.setVersion(
        determineVersionFrom(
                controller.getSpec().getTemplate().getSpec().getContainers(), containerName)
            .orElse(UNKNOWN_VERSION));
    return podController;
  }

  private Status determineStatusFrom(
      int specReplicas, int unavailableReplicas, int availableReplicas, int readyReplicas) {
    // If the desired spec replicas is 0, it is likely the deployment has been scaled down.
    if (specReplicas == 0) {
      return Status.SCALED_DOWN;
    }

    if (unavailableReplicas == specReplicas) {
      return Status.UNAVAILABLE;
    }

    if (readyReplicas == specReplicas) {
      return Status.READY;
    }

    if (availableReplicas > 0) {
      return Status.LIVE;
    }

    return Status.UNKNOWN;
  }

  static Optional<String> determineVersionFrom(List<Container> containers, String containerName) {
    if (containers.size() == 1 || containerName == null) {
      return determineVersionFromImage(containers.get(0).getImage());
    }

    return containers.stream()
        .filter(container -> container.getName().equals(containerName))
        .findFirst()
        .map(Container::getImage)
        .flatMap(PodControllers::determineVersionFromImage);
  }

  private static Optional<String> determineVersionFromImage(String imageUri) {
    Matcher matcher = versionRegex.matcher(imageUri);
    if (matcher.find()) {
      return Optional.of(matcher.group(1));
    }
    matcher = hashRegex.matcher(imageUri);
    if (matcher.find()) {
      return Optional.of(matcher.group(1));
    }
    return Optional.empty();
  }
}
