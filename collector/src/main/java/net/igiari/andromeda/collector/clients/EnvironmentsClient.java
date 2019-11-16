package net.igiari.andromeda.collector.clients;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.PodControllerType;

import java.util.Map;
import java.util.Optional;

import static net.igiari.andromeda.collector.cluster.PodController.empty;

public class EnvironmentsClient {
  private KubernetesClient kubernetesClient;
  private PodControllersClient podControllersClient;
  private PodsClient podsClient;

  public EnvironmentsClient(
      KubernetesClient kubernetesClient, PodControllersClient podControllersClient, PodsClient podsClient) {
    this.kubernetesClient = kubernetesClient;
    this.podControllersClient = podControllersClient;
    this.podsClient = podsClient;
  }

  public Optional<Environment> getEnvironment(
      String environmentName,
      String namespaceName,
      PodControllerType type,
      Map<String, String> selector,
      String containerName) {
    if (!namespaceExists(namespaceName)) {
      return Optional.empty();
    }

    Optional<PodController> podController =
        getPodController(namespaceName, type, selector, containerName);
    podController.ifPresent(pc -> pc.setPods(podsClient.getPods(namespaceName, selector, containerName)));

    Environment environment = new Environment(environmentName, namespaceName, podController.orElse(empty()));
    return Optional.of(environment);
  }

  private Optional<PodController> getPodController(
      String namespaceName,
      PodControllerType type,
      Map<String, String> selector,
      String containerName) {
    switch (type) {
      case DEPLOYMENT:
        return podControllersClient.getDeployment(namespaceName, selector, containerName);
      case STATEFULSET:
        return podControllersClient.getStatefulSet(namespaceName, selector, containerName);
    }
    return Optional.empty();
  }

  private boolean namespaceExists(String namespaceName) {
    try {
      return kubernetesClient.namespaces().withName(namespaceName).get() != null;
    } catch (KubernetesClientException kce) {
      return false;
    }
  }
}
