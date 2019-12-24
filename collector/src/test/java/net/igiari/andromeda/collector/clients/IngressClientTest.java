package net.igiari.andromeda.collector.clients;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

import io.fabric8.kubernetes.api.model.extensions.DoneableIngress;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IngressClientTest {
  private static final String NAMESPACE = "ns1";
  private static final String CONTROLLER_NAME = "deploymentName";
  private static final String CONTAINER_NAME = "deploymentName";
  private static final Map<String, String> SELECTOR = singletonMap("app", "appLabel");
  private static final Map<String, String> WITHOUT_SELECTOR = emptyMap();
  private static final String IMAGE = "host/path/imageName:v1.22.333";
  private static final String POD_NAME = "pod1";

  @Rule public KubernetesServer server = new KubernetesServer(true, true);

  private IngressClient ingressClient;

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
    ingressClient = new IngressClient(server.getClient());
  }

  @Test
  void getIngress() {
    givenIngress()
        .withNewSpec()
        .addNewRule()
        .withHost("foo.bar.com")
        .withNewHttp()
        .addNewPath()
        .withNewPath("/path")
        .withNewBackend()
        .withNewServiceName("appService")
        .withNewServicePort(8080)
        .endBackend()
        .endPath()
        .endHttp()
        .endRule()
        .endSpec()
        .done();

    List<String> ingresses = ingressClient.getIngresses(NAMESPACE);
    assertThat(ingresses).containsExactly("foo.bar.com/path");
  }

  @Test
  void getEmptyListOnNoIngresses() {
    assertThat(ingressClient.getIngresses(NAMESPACE)).isEmpty();
  }

  @Test
  void getIngressWithoutHost() {
    givenIngress()
        .withNewSpec()
        .addNewRule()
        .withNewHttp()
        .addNewPath()
        .withNewPath("/path")
        .withNewBackend()
        .withNewServiceName("appService")
        .withNewServicePort(8080)
        .endBackend()
        .endPath()
        .endHttp()
        .endRule()
        .endSpec()
        .done();

    assertThat(ingressClient.getIngresses(NAMESPACE)).isEmpty();
  }

  @Test
  void getIngressWithoutPath() {
    givenIngress()
        .withNewSpec()
        .addNewRule()
        .withHost("foo.bar.com")
        .withNewHttp()
        .endHttp()
        .endRule()
        .endSpec()
        .done();
    assertThat(ingressClient.getIngresses(NAMESPACE)).isEmpty();
  }

  @Test
  void getMultipleIngresses() {
    givenIngress()
        .withNewSpec()
        .addNewRule()
        .withHost("foo.bar.com")
        .withNewHttp()
        .addNewPath()
        .withNewPath("/app")
        .withNewBackend()
        .withNewServiceName("appService")
        .withNewServicePort(8080)
        .endBackend()
        .endPath()
        .addNewPath()
        .withNewPath("/otherApp")
        .withNewBackend()
        .withNewServiceName("otherService")
        .withNewServicePort(9090)
        .endBackend()
        .endPath()
        .endHttp()
        .endRule()
        .endSpec()
        .done();

    givenIngress()
        .withNewMetadata()
        .withName("ingress-2")
        .endMetadata()
        .withNewSpec()
        .addNewRule()
        .withHost("different.ingress.com")
        .withNewHttp()
        .addNewPath()
        .withNewPath("/path")
        .withNewBackend()
        .withNewServiceName("appService")
        .withNewServicePort(8080)
        .endBackend()
        .endPath()
        .addNewPath()
        .withNewPath("/")
        .withNewBackend()
        .withNewServiceName("appService")
        .withNewServicePort(8081)
        .endBackend()
        .endPath()
        .endHttp()
        .endRule()
        .endSpec()
        .done();

    assertThat(ingressClient.getIngresses(NAMESPACE))
        .containsExactlyInAnyOrder(
            "foo.bar.com/app",
            "foo.bar.com/otherApp",
            "different.ingress.com/path",
            "different.ingress.com/");
  }

  private DoneableIngress givenIngress() {
    return server
        .getClient()
        .extensions()
        .ingresses()
        .inNamespace(NAMESPACE)
        .createNew()
        .withNewMetadata()
        .withName("ingress-1")
        .endMetadata();
  }
}
