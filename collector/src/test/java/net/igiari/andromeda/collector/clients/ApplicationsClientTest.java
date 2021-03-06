package net.igiari.andromeda.collector.clients;

import static java.util.Collections.singletonList;
import static net.igiari.andromeda.collector.cluster.PodControllerType.DEPLOYMENT;
import static net.igiari.andromeda.collector.config.CanaryConfiguration.defaultCanaryConfiguration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.PodControllerType;
import net.igiari.andromeda.collector.config.ApplicationConfig;
import net.igiari.andromeda.collector.config.PriorityConfig;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationsClientTest {
  private static final String NAMESPACE = "ns1";
  @Rule public KubernetesServer server = new KubernetesServer(true, true);
  ApplicationsClient applicationsClient;
  private EnvironmentsClient stubbedEnvironmentsClient;
  private PriorityConfig stubbedPriorityConfig;

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

    stubbedEnvironmentsClient = new StubbedEnvironmentsClient();
    stubbedPriorityConfig = new StubbedPriorityConfig();
    applicationsClient =
        new ApplicationsClient(stubbedEnvironmentsClient, "app", stubbedPriorityConfig);
  }

  @Test
  void getApplication() {
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setName("app");
    applicationConfig.setDeployment("container");
    applicationConfig.setPrefix("app");
    Application application =
        applicationsClient.getApplication(applicationConfig, singletonList("-env-1"));
    assertThat(application).isNotNull();
    assertThat(application.getName()).isEqualTo("app");
    assertThat(application.getEnvironments())
        .containsOnly(new Environment("-env-1", "app-env-1", PodController.empty()));
  }

  @Test
  void getApplicationsCallsEnvironmentsClientWithCorrectValuesAndDefaultSelector() {
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setName("appName");
    applicationConfig.setDeployment("container");
    applicationConfig.setPrefix("namespacePrefix");
    final EnvironmentsClient environmentsClientMock = mock(EnvironmentsClient.class);
    applicationsClient =
        new ApplicationsClient(environmentsClientMock, "app", stubbedPriorityConfig);

    applicationsClient.getApplication(applicationConfig, singletonList("-env-1"));

    verify(environmentsClientMock)
        .getEnvironment(
            "-env-1", "namespacePrefix-env-1", DEPLOYMENT, Map.of("app", "appName"), "container");
  }

  @Test
  void environmentPriority() {
    applicationsClient =
        new ApplicationsClient(
            stubbedEnvironmentsClient,
            "app",
            new StubbedPriorityConfig(
                Arrays.asList("env-3", "env-4", "env-6"), Arrays.asList("env-0", "env-1")));

    List<Environment> environmentsToBeSorted = new ArrayList<>();
    IntStream.range(0, 6)
        .forEach(
            i ->
                environmentsToBeSorted.add(
                    new Environment("env-" + i, "app-env-" + i, PodController.empty())));

    environmentsToBeSorted.sort(applicationsClient::environmentPriority);

    assertThat(environmentsToBeSorted.stream().map(Environment::getEnvironmentName))
        .containsExactly("env-3", "env-4", "env-2", "env-5", "env-0", "env-1");
  }

  private class StubbedEnvironmentsClient extends EnvironmentsClient {
    StubbedEnvironmentsClient() {
      super(null, null, null, null, defaultCanaryConfiguration());
    }

    @Override
    public Optional<Environment> getEnvironment(
        String environmentName,
        String namespaceName,
        PodControllerType type,
        Map<String, String> selector,
        String containerName) {
      return Optional.of(new Environment(environmentName, namespaceName, PodController.empty()));
    }
  }

  private class StubbedPriorityConfig extends PriorityConfig {

    private final List<String> firstList;
    private final List<String> lastList;

    public StubbedPriorityConfig() {
      firstList = Collections.emptyList();
      lastList = Collections.emptyList();
    }

    public StubbedPriorityConfig(List<String> firstList, List<String> lastList) {
      this.firstList = firstList;
      this.lastList = lastList;
    }

    @Override
    public List<String> getFirst() {
      return firstList;
    }

    @Override
    public List<String> getLast() {
      return lastList;
    }
  }
}
