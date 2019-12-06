package net.igiari.andromeda.collector.controllers;

import io.fabric8.kubernetes.client.KubernetesClient;
import net.igiari.andromeda.collector.clients.ApplicationsClient;
import net.igiari.andromeda.collector.clients.Client;
import net.igiari.andromeda.collector.clients.EnvironmentsClient;
import net.igiari.andromeda.collector.clients.PodControllersClient;
import net.igiari.andromeda.collector.clients.PodsClient;
import net.igiari.andromeda.collector.clients.TeamsClient;
import net.igiari.andromeda.collector.cluster.Team;
import net.igiari.andromeda.collector.config.ClusterConfig;
import net.igiari.andromeda.collector.config.GlobalConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AndromedaController {
  private final GlobalConfig globalConfig;
  private final ClusterConfig clusterConfig;
  private final TeamsClient teamsClient;

  public AndromedaController(
      GlobalConfig globalConfig, ClusterConfig clusterConfig, Client client) {
    this.globalConfig = globalConfig;
    this.clusterConfig = clusterConfig;

    KubernetesClient kubernetesClient = client.getKubernetesClient();
    PodControllersClient podControllersClient = new PodControllersClient(kubernetesClient);
    PodsClient podsClient = new PodsClient(kubernetesClient);
    EnvironmentsClient environmentsClient =
        new EnvironmentsClient(
            kubernetesClient, podControllersClient, podsClient, globalConfig.getCanary());
    ApplicationsClient applicationsClient =
        new ApplicationsClient(
            environmentsClient, globalConfig.getDefaultSelectorKey(), clusterConfig.getPriority());
    this.teamsClient = new TeamsClient(globalConfig, clusterConfig, applicationsClient);
  }

  @RequestMapping("/")
  public String index() {
    return "Greetings from Kubernetes!";
  }

  @GetMapping("/team/{teamName}")
  public Team team(@PathVariable String teamName) {
    return teamsClient.getTeam(teamName);
  }

  @GetMapping("/config/global")
  public GlobalConfig globalConfig() {
    return globalConfig;
  }

  @GetMapping("/config/cluster")
  public ClusterConfig clusterConfig() {
    return clusterConfig;
  }
}
