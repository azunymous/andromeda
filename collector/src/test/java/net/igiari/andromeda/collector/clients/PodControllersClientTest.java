package net.igiari.andromeda.collector.clients;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static net.igiari.andromeda.collector.cluster.PodControllerType.DEPLOYMENT;
import static net.igiari.andromeda.collector.cluster.PodControllerType.STATEFULSET;
import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.LabelSelectorBuilder;
import io.fabric8.kubernetes.api.model.apps.DoneableDeployment;
import io.fabric8.kubernetes.api.model.apps.DoneableStatefulSet;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.Status;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PodControllersClientTest {
  private static final String NAMESPACE = "ns1";
  private static final String CONTROLLER_NAME = "deploymentName";
  private static final String CONTAINER_NAME = "deploymentName";
  private static final Map<String, String> SELECTOR = singletonMap("app", "appLabel");
  private static final Map<String, String> WITHOUT_SELECTOR = emptyMap();
  private static final String IMAGE = "host/path/imageName:v1.22.333";

  @Rule public KubernetesServer server = new KubernetesServer(true, true);

  private PodControllersClient podControllersClient;

  @BeforeEach
  void setUp() {
    server.before();
    server
        .getClient()
        .namespaces()
        .createNew()
        .withNewMetadata()
        .withName(NAMESPACE)
        .endMetadata()
        .done();
    podControllersClient = new PodControllersClient(server.getClient());
  }

  @Test
  void getDeployment() {
    givenDeployment().done();

    Optional<PodController> gotPodController =
        podControllersClient.getDeployment(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);

    PodController expectedPodController =
        new PodController(CONTROLLER_NAME, emptyList(), DEPLOYMENT);
    assertThat(gotPodController).contains(expectedPodController);
  }

  @Test
  void getStatefulSet() {
    givenStatefulSet().done();

    Optional<PodController> gotPodController =
        podControllersClient.getStatefulSet(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);

    PodController expectedPodController =
        new PodController(CONTROLLER_NAME, emptyList(), STATEFULSET);
    assertThat(gotPodController).contains(expectedPodController);
  }

  @Test
  void unavailableStatusWhenAllReplicasAreUnavailable_deployment() {
    givenDeployment() // with 3 desired replicas
        .withNewStatus()
        .withUnavailableReplicas(3)
        .withAvailableReplicas(0)
        .withReadyReplicas(0)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.UNAVAILABLE);
  }

  @Test
  void liveStatusWhenAtLeastSomeReplicasAreAvailableButNotReady_deployment() {
    givenDeployment() // with 3 desired replicas
        .withNewStatus()
        .withUnavailableReplicas(0)
        .withAvailableReplicas(2)
        .withReadyReplicas(0)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.LIVE);
  }

  @Test
  void liveStatusWhenOnlySomeReplicasAreReady_deployment() {
    givenDeployment() // with 3 desired replicas
        .withNewStatus()
        .withUnavailableReplicas(1)
        .withAvailableReplicas(2)
        .withReadyReplicas(1)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.LIVE);
  }

  @Test
  void readyStatusWhenAllReplicasAreReady_deployment() {
    givenDeployment() // with 3 desired replicas
        .withNewStatus()
        .withUnavailableReplicas(0)
        .withAvailableReplicas(3)
        .withReadyReplicas(3)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.READY);
  }

  @Test
  void scaledDownStatusWhenNoReplicasExpected_deployment() {
    givenDeployment().editOrNewSpec().withReplicas(0).endSpec().done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.SCALED_DOWN);
  }

  @Test
  void unavailableStatusWhenAllReplicasAreUnavailable_statefulSet() {
    givenStatefulSet() // with 3 desired replicas
        .withNewStatus()
        .withCurrentReplicas(0)
        .withReadyReplicas(0)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getStatefulSet(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.UNAVAILABLE);
  }

  @Test
  void liveStatusWhenAtLeastSomeReplicasAreAvailableButNotReady_statefulSet() {
    givenStatefulSet() // with 3 desired replicas
        .withNewStatus()
        .withCurrentReplicas(2)
        .withReadyReplicas(0)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getStatefulSet(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.LIVE);
  }

  @Test
  void liveStatusWhenOnlySomeReplicasAreReady_statefulSet() {
    givenStatefulSet() // with 3 desired replicas
        .withNewStatus()
        .withCurrentReplicas(2)
        .withReadyReplicas(1)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getStatefulSet(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.LIVE);
  }

  @Test
  void readyStatusWhenAllReplicasAreReady_statefulSet() {
    givenStatefulSet() // with 3 desired replicas
        .withNewStatus()
        .withCurrentReplicas(3)
        .withReadyReplicas(3)
        .endStatus()
        .done();

    assertThat(
            podControllersClient.getStatefulSet(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.READY);
  }

  @Test
  void scaledDownStatusWhenNoReplicasExpected_statefulSet() {
    givenStatefulSet().editOrNewSpec().withReplicas(0).endSpec().done();

    assertThat(
            podControllersClient.getStatefulSet(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("status", Status.SCALED_DOWN);
  }

  @Test
  void getVersion() {
    givenDeployment().done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("version", "1.22.333");
  }

  @Test
  void getHashVersionWithFirst8Characters() {
    givenDeployment()
        .editSpec()
        .editTemplate()
        .editSpec()
        .editContainer(0)
        .withImage("host/path/imageName:hash123456789")
        .endContainer()
        .endSpec()
        .endTemplate()
        .endSpec()
        .done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME))
        .get()
        .hasFieldOrPropertyWithValue("version", "hash1234");
  }

  @Test
  void getsCanaryDeployment() {
    final Map<String, String> canarySelector = Map.of("app", "appName", "canary", "enabled");
    givenDeployment()
        .editMetadata()
        .withName("deploymentNameCanary")
        .withLabels(canarySelector)
        .endMetadata()
        .done();
    PodController expectedPodController =
        new PodController("deploymentNameCanary", emptyList(), DEPLOYMENT);
    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, canarySelector, emptyMap(), CONTAINER_NAME))
        .contains(expectedPodController);
  }

  @Test
  void getsNonCanaryDeploymentWhenExcludingCanary() {
    final Map<String, String> canaryOnlySelector = Map.of("canary", "enabled");
    final Map<String, String> canarySelector = new HashMap<>(SELECTOR);
    canarySelector.putAll(canaryOnlySelector);

    givenDeployment()
        .editMetadata()
        .withName("deploymentNameCanary")
        .withLabels(canarySelector)
        .endMetadata()
        .done();

    givenDeployment()
        .editMetadata()
        .withName(CONTROLLER_NAME)
        .withLabels(SELECTOR)
        .endMetadata()
        .done();

    PodController expectedPodController =
        new PodController(CONTROLLER_NAME, emptyList(), DEPLOYMENT);
    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, canarySelector, canaryOnlySelector, CONTAINER_NAME))
        .contains(expectedPodController);
  }

  @Test
  void getsNoNormalDeploymentWhenOnlyCanaryExistsWhenExcludingCanary() {
    final Map<String, String> canaryOnlySelector = Map.of("canary", "enabled");
    final Map<String, String> canarySelector = new HashMap<>(SELECTOR);
    canarySelector.putAll(canaryOnlySelector);

    givenDeployment()
        .editMetadata()
        .withName("deploymentNameCanary")
        .withLabels(canarySelector)
        .endMetadata()
        .done();

    assertThat(
            podControllersClient.getDeployment(
                NAMESPACE, canarySelector, canaryOnlySelector, CONTAINER_NAME))
        .isEmpty();
  }

  @AfterEach
  void tearDown() {
    server.after();
  }

  private DoneableDeployment givenDeployment() {
    return server
        .getClient()
        .apps()
        .deployments()
        .inNamespace(NAMESPACE)
        .createNew()
        .withNewMetadata()
        .withName(CONTROLLER_NAME)
        .withLabels(SELECTOR)
        .endMetadata()
        .withNewSpec()
        .withSelector(new LabelSelectorBuilder().withMatchLabels(SELECTOR).build())
        .withReplicas(3)
        .withNewTemplate()
        .withNewSpec()
        .withContainers(new ContainerBuilder().withImage(IMAGE).build())
        .endSpec()
        .endTemplate()
        .endSpec()
        .withNewStatus()
        .withUnavailableReplicas(0)
        .withAvailableReplicas(3)
        .withReadyReplicas(3)
        .endStatus();
  }

  private DoneableStatefulSet givenStatefulSet() {
    return server
        .getClient()
        .apps()
        .statefulSets()
        .inNamespace(NAMESPACE)
        .createNew()
        .withNewMetadata()
        .withName(CONTROLLER_NAME)
        .withLabels(SELECTOR)
        .endMetadata()
        .withNewSpec()
        .withReplicas(3)
        .withNewTemplate()
        .withNewSpec()
        .withContainers(new ContainerBuilder().withImage(IMAGE).build())
        .endSpec()
        .endTemplate()
        .endSpec()
        .withNewStatus()
        .withCurrentReplicas(3)
        .withReadyReplicas(3)
        .endStatus();
  }
}
