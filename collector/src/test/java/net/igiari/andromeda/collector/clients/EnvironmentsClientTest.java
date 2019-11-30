package net.igiari.andromeda.collector.clients;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.PodControllerType;
import net.igiari.andromeda.collector.config.CanaryConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static net.igiari.andromeda.collector.config.CanaryConfiguration.defaultCanaryConfiguration;
import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentsClientTest {
  public static final String ENV = "env";
  private static final String NAMESPACE = "ns1-env";
  private static final String CONTROLLER_NAME = "deploymentName";
  private static final String CONTAINER_NAME = "deploymentName";
  private static final Map<String, String> APP_LABEL = singletonMap("app", "appLabel");
  @Rule public KubernetesServer server = new KubernetesServer(true, true);

  private EnvironmentsClient environmentsClient;

  @BeforeEach
  void setUp() {
    server.before();
    PodControllersClientStub podControllersStub = new PodControllersClientStub(server.getClient());

    PodsClient podsClientStub = new PodsClientStub(server.getClient());
    environmentsClient =
        new EnvironmentsClient(
            server.getClient(), podControllersStub, podsClientStub, defaultCanaryConfiguration());
  }

  @Test
  void getEnvironment() {
    createNamespace(NAMESPACE);

    Optional<Environment> gotEnvironment =
        environmentsClient.getEnvironment(
            ENV, NAMESPACE, PodControllerType.DEPLOYMENT, APP_LABEL, CONTAINER_NAME);
    Environment expectedEnvironment =
        new Environment(
            ENV,
            NAMESPACE,
            new PodController(CONTROLLER_NAME, emptyList(), PodControllerType.DEPLOYMENT));
    assertThat(gotEnvironment).contains(expectedEnvironment);
  }

  @Test
  void getEnvironmentWithoutNamespace() {
    Assertions.assertThat(
            environmentsClient.getEnvironment(
                ENV, NAMESPACE, PodControllerType.DEPLOYMENT, APP_LABEL, CONTAINER_NAME))
        .isEmpty();
  }

  @Test
  void getEnvironmentWithCanary() {
    createNamespace(NAMESPACE);

    environmentsClient =
        new EnvironmentsClient(
            server.getClient(),
            new PodControllersClientStub(server.getClient()),
            new PodsClientStub(server.getClient()),
            new CanaryConfiguration(true, Map.of("canary", "enabled")));

    Optional<Environment> gotEnvironment =
        environmentsClient.getEnvironment(
            ENV, NAMESPACE, PodControllerType.DEPLOYMENT, APP_LABEL, CONTAINER_NAME);
    assertThat(gotEnvironment)
        .get()
        .extracting(Environment::getCanaryPodController)
        .isEqualTo(
            new PodController(
                CONTROLLER_NAME + "-canary", emptyList(), PodControllerType.DEPLOYMENT));
  }

  @Test
  void getEnvironmentWithoutCanary() {
    createNamespace(NAMESPACE);

    Optional<Environment> gotEnvironment =
        environmentsClient.getEnvironment(
            ENV, NAMESPACE, PodControllerType.DEPLOYMENT, APP_LABEL, CONTAINER_NAME);
    assertThat(gotEnvironment)
        .get()
        .extracting(Environment::getCanaryPodController)
        .isEqualTo(PodController.empty());
  }

  // TODO Add tests for calling pod client correctly with and without canary selectors.

  private void createNamespace(String namespace) {
    server
        .getClient()
        .namespaces()
        .createNew()
        .withNewMetadata()
        .withName(namespace)
        .endMetadata()
        .done();
  }

  private static class PodControllersClientStub extends PodControllersClient {
    PodControllersClientStub(KubernetesClient kubernetesClient) {
      super(kubernetesClient);
    }

    @Override
    public Optional<PodController> getDeployment(
        String namespaceName,
        Map<String, String> selector,
        Map<String, String> withoutSelector,
        String containerName) {
      if (selector.get("canary") != null) {
        return Optional.of(
            new PodController(CONTROLLER_NAME + "-canary", emptyList(), PodControllerType.DEPLOYMENT));
      }
      return Optional.of(
          new PodController(CONTROLLER_NAME, emptyList(), PodControllerType.DEPLOYMENT));
    }

    @Override
    public Optional<PodController> getStatefulSet(
        String namespaceName,
        Map<String, String> selector,
        Map<String, String> withoutSelector,
        String containerName) {
      return Optional.of(
          new PodController(CONTROLLER_NAME, emptyList(), PodControllerType.STATEFULSET));
    }
  }

  private class PodsClientStub extends PodsClient {
    public PodsClientStub(KubernetesClient kubernetesClient) {
      super(kubernetesClient);
    }
  }
}
