package net.igiari.andromeda.fetch;

import io.fabric8.kubernetes.client.KubernetesClient;
import net.igiari.andromeda.cluster.Environment;
import net.igiari.andromeda.cluster.PodController;
import net.igiari.andromeda.cluster.PodControllerType;

import java.util.Map;
import java.util.Optional;

public class Environments {
  private KubernetesClient kubernetesClient;
  private PodControllers podControllers;

  public Environments(KubernetesClient kubernetesClient, PodControllers podControllers) {
    this.kubernetesClient = kubernetesClient;
    this.podControllers = podControllers;
  }

  public Optional<Environment> getEnvironment(
      String namespaceName,
      PodControllerType type,
      Map<String, String> selector,
      String containerName) {
    if (!namespaceExists(namespaceName)) {
      return Optional.empty();
    }

    PodController podController =
        getPodController(namespaceName, type, selector, containerName)
            .orElse(PodController.empty());

    Environment environment = new Environment(namespaceName, podController);
    return Optional.of(environment);
  }

  private Optional<PodController> getPodController(
      String namespaceName,
      PodControllerType type,
      Map<String, String> selector,
      String containerName) {
    switch (type) {
      case DEPLOYMENT:
        return podControllers.getDeployment(namespaceName, selector, containerName);
      case STATEFULSET:
        return podControllers.getStatefulSet(namespaceName, selector, containerName);
    }
    return Optional.empty();
  }

  private boolean namespaceExists(String namespaceName) {
    return kubernetesClient.namespaces().withName(namespaceName).get() != null;
  }
}
