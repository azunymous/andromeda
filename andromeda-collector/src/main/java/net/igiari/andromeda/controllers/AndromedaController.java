package net.igiari.andromeda.controllers;

import net.igiari.andromeda.config.ClusterConfig;
import net.igiari.andromeda.config.GlobalConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AndromedaController {
  private final GlobalConfig globalConfig;
  private final ClusterConfig clusterConfig;

  public AndromedaController(GlobalConfig globalConfig, ClusterConfig clusterConfig) {
    this.globalConfig = globalConfig;
    this.clusterConfig = clusterConfig;
  }

  @RequestMapping("/")
  public String index() {
    return "Greetings from Kubernetes!";
  }

  @GetMapping("/team/{teamName}")
  public String team(@PathVariable String teamName) {
    return "NYI, will return applications for " + teamName;
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
