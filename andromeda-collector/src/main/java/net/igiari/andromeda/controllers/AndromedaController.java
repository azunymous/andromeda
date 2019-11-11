package net.igiari.andromeda.controllers;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import net.igiari.andromeda.cluster.Team;
import net.igiari.andromeda.config.ClusterConfig;
import net.igiari.andromeda.config.GlobalConfig;
import net.igiari.andromeda.fetch.ApplicationsClient;
import net.igiari.andromeda.fetch.EnvironmentsClient;
import net.igiari.andromeda.fetch.PodControllersClient;
import net.igiari.andromeda.fetch.PodsClient;
import net.igiari.andromeda.fetch.TeamsClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AndromedaController {
  private final GlobalConfig globalConfig;
  private final ClusterConfig clusterConfig;
  private final TeamsClient teamsClient;

  public AndromedaController(GlobalConfig globalConfig, ClusterConfig clusterConfig) {
    this.globalConfig = globalConfig;
    this.clusterConfig = clusterConfig;
    KubernetesClient kubernetesClient = new DefaultKubernetesClient();

    PodControllersClient podControllersClient = new PodControllersClient(kubernetesClient);
    PodsClient podsClient = new PodsClient(kubernetesClient);
    EnvironmentsClient environmentsClient =
        new EnvironmentsClient(kubernetesClient, podControllersClient, podsClient);
    ApplicationsClient applicationsClient =
        new ApplicationsClient(kubernetesClient, environmentsClient, clusterConfig.getPriority());
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
