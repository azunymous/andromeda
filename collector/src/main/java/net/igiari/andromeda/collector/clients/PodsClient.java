package net.igiari.andromeda.collector.clients;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.igiari.andromeda.collector.cluster.Pod;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.Status;
import net.igiari.andromeda.collector.cluster.comparers.Compare;

public class PodsClient {

  private final KubernetesClient kubernetesClient;

  public PodsClient(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  public List<Pod> getPods(
      String namespaceName,
      Map<String, String> selector,
      Map<String, String> withoutSelector,
      String containerName) {
    return kubernetesClient
        .pods()
        .inNamespace(namespaceName)
        .withLabels(selector)
        .withoutLabels(withoutSelector)
        .list()
        .getItems()
        .stream()
        .map((io.fabric8.kubernetes.api.model.Pod pod) -> createPodFrom(pod, containerName))
        .sorted(Compare::byName)
        .collect(Collectors.toUnmodifiableList());
  }

  private Pod createPodFrom(io.fabric8.kubernetes.api.model.Pod pod, String containerName) {
    String version =
        PodControllersClient.determineVersionFrom(pod.getSpec().getContainers(), containerName)
            .orElse(PodController.UNKNOWN_VERSION);
    Status status = determineStatusFrom(pod);
    return new Pod(pod.getMetadata().getName(), version, status);
  }

  private Status determineStatusFrom(io.fabric8.kubernetes.api.model.Pod pod) {
    if (pod.getStatus().getContainerStatuses().stream().allMatch(ContainerStatus::getReady)
        || pod.getStatus().getPhase().equals("Succeeded")) {
      return Status.READY;
    }

    if (pod.getStatus().getPhase().equals("Running")
        || pod.getStatus().getPhase().equals("Pending")) {
      return Status.LIVE;
    }

    return Status.UNAVAILABLE;
  }
}
