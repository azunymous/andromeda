package net.igiari.andromeda.aggregator.controllers;

import net.igiari.andromeda.aggregator.config.AggregatorConfig;
import net.igiari.andromeda.collector.cluster.Team;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AndromedaController {
  private final AggregatorConfig aggregatorConfig;
;

  public AndromedaController(AggregatorConfig aggregatorConfig) {
    this.aggregatorConfig = aggregatorConfig;
  }

  @RequestMapping("/")
  public String index() {
    return "Greetings from Kubernetes!";
  }

  @GetMapping("/team/{teamName}")
  public Team team(@PathVariable String teamName) {
    return null;
  }

  @GetMapping("/config/")
  public AggregatorConfig aggregatorConfig() {
    return aggregatorConfig;
  }
}
