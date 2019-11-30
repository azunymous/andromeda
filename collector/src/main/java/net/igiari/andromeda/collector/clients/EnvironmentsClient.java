package net.igiari.andromeda.collector.clients;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.PodControllerType;
import net.igiari.andromeda.collector.config.CanaryConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static net.igiari.andromeda.collector.cluster.PodController.empty;

public class EnvironmentsClient {
  private KubernetesClient kubernetesClient;
  private PodControllersClient podControllersClient;
  private PodsClient podsClient;
  private CanaryConfiguration canaryConfiguration;

  public EnvironmentsClient(
      KubernetesClient kubernetesClient,
      PodControllersClient podControllersClient,
      PodsClient podsClient,
      CanaryConfiguration canaryConfiguration) {
    this.kubernetesClient = kubernetesClient;
    this.podControllersClient = podControllersClient;
    this.podsClient = podsClient;
    this.canaryConfiguration = canaryConfiguration;
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
        getPodController(
            namespaceName, type, selector, canaryConfiguration.getSelector(), containerName);
    podController.ifPresent(
        pc ->
            pc.setPods(
                podsClient.getPods(
                    namespaceName, selector, canaryConfiguration.getSelector(), containerName)));

    Environment environment =
        new Environment(environmentName, namespaceName, podController.orElse(empty()));

    if (canaryConfiguration.isEnabled()) {
      Map<String, String> canarySelector = new HashMap<>(selector);
      canaryConfiguration.getSelector().forEach((k, v) -> canarySelector.merge(k, v, (a, b) -> b));
      Optional<PodController> canary =
          getPodController(namespaceName, type, canarySelector, emptyMap(), containerName);
      canary.ifPresent(
          c -> c.setPods(podsClient.getPods(namespaceName, canarySelector, emptyMap(), containerName)));
      canary.ifPresent(environment::setCanaryPodController);
    }

    return Optional.of(environment);
  }

  private Optional<PodController> getPodController(
      String namespaceName,
      PodControllerType type,
      Map<String, String> selector,
      Map<String, String> withoutSelector,
      String containerName) {
    switch (type) {
      case DEPLOYMENT:
        return podControllersClient.getDeployment(
            namespaceName, selector, withoutSelector, containerName);
      case STATEFULSET:
        return podControllersClient.getStatefulSet(
            namespaceName, selector, withoutSelector, containerName);
      default:
        throw new IllegalStateException();
    }
  }

  private boolean namespaceExists(String namespaceName) {
    try {
      return kubernetesClient.namespaces().withName(namespaceName).get() != null;
    } catch (KubernetesClientException kce) {
      return false;
    }
  }
}
