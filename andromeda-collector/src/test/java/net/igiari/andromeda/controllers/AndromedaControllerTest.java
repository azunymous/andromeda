package net.igiari.andromeda.controllers;

import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.ContainerStatusBuilder;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import net.igiari.andromeda.cluster.Application;
import net.igiari.andromeda.cluster.Environment;
import net.igiari.andromeda.cluster.Pod;
import net.igiari.andromeda.cluster.PodController;
import net.igiari.andromeda.cluster.PodControllerType;
import net.igiari.andromeda.cluster.Status;
import net.igiari.andromeda.cluster.Team;
import net.igiari.andromeda.config.ApplicationConfig;
import net.igiari.andromeda.config.ClusterConfig;
import net.igiari.andromeda.config.GlobalConfig;
import net.igiari.andromeda.config.PriorityConfig;
import net.igiari.andromeda.config.TeamConfig;
import net.igiari.andromeda.fetch.Client;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class AndromedaControllerTest {
  @Rule public KubernetesServer server = new KubernetesServer(true, true);

  @BeforeEach
  void setUp() {
    server.before();
    List<String> aliceNamespaces = Arrays.asList("alice-dev", "alice-test", "alice-prod");
    List<String> baneNamespaces = Arrays.asList("bane-dev", "bane-test", "bane-prod");
    List<String> charlieNamespaces = Arrays.asList("charlie-dev", "charlie-test", "charlie-prod");

    aliceNamespaces.forEach(this::createNamespace);
    baneNamespaces.forEach(this::createNamespace);
    charlieNamespaces.forEach(this::createNamespace);

    for (int i = 0; i < 3; i++) {
      createDeploymentAndPods(
          aliceNamespaces.get(i),
          "deployment-" + i,
          Collections.singletonMap("app", "alice"),
          "host/path/image:1.22." + i);
      createDeploymentAndPods(
          baneNamespaces.get(i),
          "deployment-" + i,
          Collections.singletonMap("app", "bane"),
          "host/path/image:aHash" + i + "LorumIpsum");
      createDeploymentAndPods(
          charlieNamespaces.get(i),
          "deployment-" + i,
          Collections.singletonMap("app", "charlie"),
          "host/path/image:1.22." + i);
    }
  }

  @Test
  void team() {
    GlobalConfig globalConfig = createGlobalConfig();
    ClusterConfig clusterConfig = createClusterConfig();

    AndromedaController andromedaController =
        new AndromedaController(globalConfig, clusterConfig, new Client(server.getClient()));

    assertThat(andromedaController.team("alphabet")).isNotNull();
    assertThat(andromedaController.team("alphabet")).isEqualTo(expectedTeam("alphabet", "alice"));
  }

  private Team expectedTeam(String teamName, String appName) {
    List<Pod> podList = new ArrayList<>();

    Environment dev =
        new Environment(
            "dev",
            appName + "-dev",
            new PodController("deployment-0", podsWithVersion(0), PodControllerType.DEPLOYMENT));
    Environment test =
        new Environment(
            "test",
            appName + "-test",
            new PodController("deployment-1", podsWithVersion(1), PodControllerType.DEPLOYMENT));
    Environment prod =
        new Environment(
            "prod",
            appName + "-prod",
            new PodController("deployment-2", podsWithVersion(2 ), PodControllerType.DEPLOYMENT));
    Application application = new Application(appName, List.of(dev, test, prod));

    return new Team(teamName, List.of(application));
  }

  private List<Pod> podsWithVersion(int version) {
    List<Pod> podList = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      Pod pod = new Pod("pod-abc-" + i, "1.22." + version, Status.READY);
      podList.add(pod);
    }
    return podList;
  }

  private ClusterConfig createClusterConfig() {
    ClusterConfig clusterConfig = new ClusterConfig();
    clusterConfig.setNamespaceSuffixes(Arrays.asList("-dev", "-test", "-prod"));
    PriorityConfig priority = new PriorityConfig();
    priority.setFirst(singletonList("-dev"));
    priority.setLast(singletonList("-prod"));
    clusterConfig.setPriority(priority);
    return clusterConfig;
  }

  private GlobalConfig createGlobalConfig() {
    GlobalConfig globalConfig = new GlobalConfig();
    ApplicationConfig applicationConfig = new ApplicationConfig();
    applicationConfig.setName("alice");
    applicationConfig.setPrefix("alice");
    TeamConfig teamConfig = new TeamConfig();
    teamConfig.setName("alphabet");
    teamConfig.setApplications(singletonList(applicationConfig));
    globalConfig.setTeams(singletonList(teamConfig));
    return globalConfig;
  }

  @AfterEach
  void tearDown() {
    server.after();
  }

  private void createNamespace(String name) {
    server
        .getClient()
        .namespaces()
        .createNew()
        .withNewMetadata()
        .withName(name)
        .endMetadata()
        .done();
  }

  private void createDeploymentAndPods(
      String namespaceName, String controllerName, Map<String, String> selector, String imageUri) {
    server
        .getClient()
        .apps()
        .deployments()
        .inNamespace(namespaceName)
        .createNew()
        .withNewMetadata()
        .withName(controllerName)
        .withLabels(selector)
        .endMetadata()
        .withNewSpec()
        .withReplicas(3)
        .withNewTemplate()
        .withNewSpec()
        .withContainers(new ContainerBuilder().withImage(imageUri).build())
        .endSpec()
        .endTemplate()
        .endSpec()
        .withNewStatus()
        .withUnavailableReplicas(0)
        .withAvailableReplicas(3)
        .withReadyReplicas(3)
        .endStatus()
        .done();

    IntStream.range(0, 3)
        .forEach(i -> createPod(namespaceName, "pod-abc-" + i, selector, imageUri));
  }

  private void createPod(
      String namespace, String podName, Map<String, String> selector, String imageUri) {
    server
        .getClient()
        .pods()
        .inNamespace(namespace)
        .createNew()
        .withNewMetadata()
        .withName(podName)
        .withLabels(selector)
        .endMetadata()
        .withNewSpec()
        .withContainers(new ContainerBuilder().withImage(imageUri).build())
        .endSpec()
        .withNewStatus()
        .withContainerStatuses(new ContainerStatusBuilder().withReady(true).build())
        .withPhase("Running")
        .endStatus()
        .done();
  }
}
