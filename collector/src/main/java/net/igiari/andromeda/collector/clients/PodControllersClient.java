package net.igiari.andromeda.collector.clients;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.AppsAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.Status;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static net.igiari.andromeda.collector.cluster.PodControllerType.DEPLOYMENT;

public class PodControllersClient {
  private static final Pattern versionRegex = Pattern.compile("v(\\d+\\.\\d+\\.\\d+)");
  private static final Pattern hashRegex = Pattern.compile(".*:(.{4,8}).*");
  private KubernetesClient kubernetesClient;

  public PodControllersClient(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  public Optional<PodController> getDeployment(
      String namespaceName,
      Map<String, String> selector,
      Map<String, String> withoutSelector,
      String containerName) {
    return getFabric8PodController(
            AppsAPIGroupDSL::deployments, namespaceName, selector, withoutSelector)
        .map(deployment -> createPodControllerFrom(deployment, containerName));
  }

  public Optional<PodController> getStatefulSet(
      String namespaceName,
      Map<String, String> selector,
      Map<String, String> withoutSelector,
      String containerName) {
    return getFabric8PodController(
            AppsAPIGroupDSL::statefulSets, namespaceName, selector, withoutSelector)
        .map((statefulSet -> createPodControllerFrom(statefulSet, containerName)))
        .map(PodController::withTypeToStatefulSet);
  }

  private <T extends HasMetadata, TList extends KubernetesResourceList<T>, DoneableT>
      Optional<T> getFabric8PodController(
          Function<
                  AppsAPIGroupDSL,
                  MixedOperation<T, TList, DoneableT, RollableScalableResource<T, DoneableT>>>
              podController,
          String namespaceName,
          Map<String, String> selector,
          Map<String, String> withoutSelector) {
    return podController.apply(kubernetesClient.apps()).inNamespace(namespaceName)
        .withLabels(selector).withoutLabels(withoutSelector).list().getItems().stream()
        .findFirst();
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
            .orElse(PodController.UNKNOWN_VERSION));
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
            .orElse(PodController.UNKNOWN_VERSION));
    return podController;
  }

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
