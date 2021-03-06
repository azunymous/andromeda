package net.igiari.andromeda.collector.clients;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerStatusBuilder;
import io.fabric8.kubernetes.api.model.DoneablePod;
import io.fabric8.kubernetes.api.model.apps.DoneableDeployment;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.List;
import java.util.Map;
import net.igiari.andromeda.collector.cluster.Pod;
import net.igiari.andromeda.collector.cluster.Status;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PodsClientTest {
  private static final String NAMESPACE = "ns1";
  private static final String CONTROLLER_NAME = "deploymentName";
  private static final String CONTAINER_NAME = "deploymentName";
  private static final Map<String, String> SELECTOR = singletonMap("app", "appLabel");
  private static final Map<String, String> WITHOUT_SELECTOR = emptyMap();
  private static final String IMAGE = "host/path/imageName:v1.22.333";
  private static final String POD_NAME = "pod1";

  @Rule public KubernetesServer server = new KubernetesServer(true, true);

  private PodsClient podsClient;

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
    podsClient = new PodsClient(server.getClient());
  }

  @Test
  void getPods() {
    givenDeploymentWithPods().done();

    List<Pod> pods = podsClient.getPods(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod(POD_NAME, "1.22.333", Status.READY));
  }

  @Test
  void getPodsWithHash() {
    givenDeploymentWithPods()
        .editOrNewSpec()
        .editContainer(0)
        .withImage("host/path/imageName:someHash1234")
        .endContainer()
        .endSpec()
        .done();

    List<Pod> pods = podsClient.getPods(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod(POD_NAME, "someHash", Status.READY));
  }

  @Test
  void runningButOneContainerNotReadyPodIsLiveButNotReady() {
    givenDeploymentWithPods()
        .editStatus()
        .editFirstContainerStatus()
        .withReady(false)
        .endContainerStatus()
        .addNewContainerStatus()
        .withReady(true)
        .endContainerStatus()
        .withPhase("Running")
        .endStatus()
        .done();

    List<Pod> pods = podsClient.getPods(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod(POD_NAME, "1.22.333", Status.LIVE));
  }

  @Test
  void runnningButNotReadyPodIsLiveButNotReady() {
    givenDeploymentWithPods()
        .editStatus()
        .editFirstContainerStatus()
        .withReady(false)
        .endContainerStatus()
        .withPhase("Running")
        .endStatus()
        .done();

    List<Pod> pods = podsClient.getPods(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod(POD_NAME, "1.22.333", Status.LIVE));
  }

  @Test
  void pendingPodIsLiveButNotReady() {
    givenDeploymentWithPods()
        .editStatus()
        .editFirstContainerStatus()
        .withReady(false)
        .endContainerStatus()
        .withPhase("Pending")
        .endStatus()
        .done();

    List<Pod> pods = podsClient.getPods(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod(POD_NAME, "1.22.333", Status.LIVE));
  }

  @Test
  void podInAnyOtherStateIsUnavailable() {
    givenDeploymentWithPods()
        .editStatus()
        .editFirstContainerStatus()
        .withReady(false)
        .endContainerStatus()
        .withPhase("Failed")
        .endStatus()
        .done();

    List<Pod> pods = podsClient.getPods(NAMESPACE, SELECTOR, WITHOUT_SELECTOR, CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod(POD_NAME, "1.22.333", Status.UNAVAILABLE));
  }

  @Test
  void getCanaryPods() {
    final Map<String, String> appCanarySelector = Map.of("app", "appName", "canary", "enabled");
    givenDeploymentWithPods()
        .editMetadata()
        .withName("pod-canary")
        .withLabels(appCanarySelector)
        .endMetadata()
        .done();
    final Map<String, String> appSelector = Map.of("app", "appName");
    givenDeploymentWithPods()
        .editMetadata()
        .withName("pod")
        .withLabels(appSelector)
        .endMetadata()
        .done();

    List<Pod> pods = podsClient.getPods(NAMESPACE, appCanarySelector, emptyMap(), CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod("pod-canary", "1.22.333", Status.READY));
  }

  @Test
  void getPodsWithoutCanaryLabel() {
    final Map<String, String> appCanarySelector = Map.of("app", "appName", "canary", "enabled");
    givenDeploymentWithPods()
        .editMetadata()
        .withName("pod-canary")
        .withLabels(appCanarySelector)
        .endMetadata()
        .done();
    final Map<String, String> appSelector = Map.of("app", "appName");
    givenDeploymentWithPods()
        .editMetadata()
        .withName("pod")
        .withLabels(appSelector)
        .endMetadata()
        .done();

    List<Pod> pods =
        podsClient.getPods(NAMESPACE, appSelector, Map.of("canary", "enabled"), CONTAINER_NAME);
    assertThat(pods).containsOnly(new Pod("pod", "1.22.333", Status.READY));
  }

  @Test
  void getNoMatchingPods() {
    final Map<String, String> appCanarySelector = Map.of("app", "appLabel", "canary", "enabled");
    givenDeploymentWithPods()
        .editMetadata()
        .withName("pod-canary")
        .withLabels(appCanarySelector)
        .endMetadata()
        .done();

    List<Pod> pods =
        podsClient.getPods(NAMESPACE, SELECTOR, Map.of("canary", "enabled"), CONTAINER_NAME);
    assertThat(pods).isEmpty();
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

  private DoneablePod givenDeploymentWithPods() {
    givenDeployment().done();
    return server
        .getClient()
        .pods()
        .inNamespace(NAMESPACE)
        .createNew()
        .withNewMetadata()
        .withName(POD_NAME)
        .withLabels(SELECTOR)
        .endMetadata()
        .withNewSpec()
        .withContainers(new ContainerBuilder().withImage(IMAGE).build())
        .endSpec()
        .withNewStatus()
        .withContainerStatuses(new ContainerStatusBuilder().withReady(true).build())
        .withPhase("Running")
        .endStatus();
  }
}
