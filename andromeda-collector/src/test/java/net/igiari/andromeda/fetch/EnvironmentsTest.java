package net.igiari.andromeda.fetch;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import net.igiari.andromeda.cluster.Environment;
import net.igiari.andromeda.cluster.PodController;
import net.igiari.andromeda.cluster.PodControllerType;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentsTest {
  private static final String NAMESPACE = "ns1-env";
  private static final String CONTROLLER_NAME = "deploymentName";
  private static final String CONTAINER_NAME = "deploymentName";
  private static final Map<String, String> APP_LABEL = singletonMap("app", "appLabel");
  public static final String ENV = "env";

  @Rule
  public KubernetesServer server = new KubernetesServer(true, true);

  private Environments environments;

  @BeforeEach
  void setUp() {
    server.before();
    PodControllersStub podControllersStub = new PodControllersStub(server.getClient());

    environments = new Environments(server.getClient(), podControllersStub);
  }

  @Test
  void getEnvironment() {
    createNamespace(NAMESPACE);

    Optional<Environment> gotEnvironment = environments.getEnvironment(ENV, NAMESPACE, PodControllerType.DEPLOYMENT, APP_LABEL, CONTAINER_NAME);
    Environment expectedEnvironment = new Environment(ENV, NAMESPACE, new PodController(CONTROLLER_NAME, emptyList(), PodControllerType.DEPLOYMENT));
    assertThat(gotEnvironment).contains(expectedEnvironment);
  }

  @Test
  void getEnvironmentWithoutNamespace() {
    assertThat(environments.getEnvironment(ENV, NAMESPACE, PodControllerType.DEPLOYMENT, APP_LABEL, CONTAINER_NAME)).isEmpty();
  }

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

  private static class PodControllersStub extends PodControllers {
    PodControllersStub(KubernetesClient kubernetesClient) {
      super(kubernetesClient);
    }

    @Override
    public Optional<PodController> getDeployment(String namespaceName, Map<String, String> selector, String containerName) {
      return Optional.of(new PodController(CONTROLLER_NAME, emptyList(), PodControllerType.DEPLOYMENT));
    }

    @Override
    public Optional<PodController> getStatefulSet(String namespaceName, Map<String, String> selector, String containerName) {
      return Optional.of(new PodController(CONTROLLER_NAME, emptyList(), PodControllerType.STATEFULSET));
    }
  }
}