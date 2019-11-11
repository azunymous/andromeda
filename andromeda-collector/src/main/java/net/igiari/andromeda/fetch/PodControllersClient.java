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

public class PodControllersClient {
  private static final Pattern versionRegex = Pattern.compile("(\\d+\\.\\d+\\.\\d+)");
  private static final Pattern hashRegex = Pattern.compile(".*:(.{4,8}).*");
  private KubernetesClient kubernetesClient;

  public PodControllersClient(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }
  // TODO Refactor to combine shared logic with a passed in function

  static Optional<String> determineVersionFrom(List<Container> containers, String containerName) {
    if (containers.size() == 1 || containerName == null) {
      return determineVersionFromImage(containers.get(0).getImage());
    }

    return containers.stream()
        .filter(container -> container.getName().equals(containerName))
        .findFirst()
        .map(Container::getImage)
        .flatMap(PodControllersClient::determineVersionFromImage);
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
    Integer specReplicas = controller.getSpec().getReplicas();
    Integer unavailableReplicas = controller.getStatus().getUnavailableReplicas();
    Integer availableReplicas = controller.getStatus().getAvailableReplicas();
    Integer readyReplicas = controller.getStatus().getReadyReplicas();

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
      Integer specReplicas,
      Integer unavailableReplicas,
      Integer availableReplicas,
      Integer readyReplicas) {
    // If the desired spec replicas is 0, it is likely the deployment has been scaled down.
    if (specReplicas.equals(0)) {
      return Status.SCALED_DOWN;
    }

    if (unavailableReplicas != null && unavailableReplicas.equals(specReplicas)) {
      return Status.UNAVAILABLE;
    }

    if (readyReplicas != null && readyReplicas.equals(specReplicas)) {
      return Status.READY;
    }

    if (availableReplicas != null && availableReplicas > 0) {
      return Status.LIVE;
    }

    return Status.UNKNOWN;
  }
}
