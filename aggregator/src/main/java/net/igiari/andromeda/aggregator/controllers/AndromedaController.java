package net.igiari.andromeda.aggregator.controllers;

import net.igiari.andromeda.aggregator.provider.TeamDashboardServiceProvider;
import net.igiari.andromeda.aggregator.config.AggregatorConfig;
import net.igiari.andromeda.aggregator.dashboard.TeamDashboard;
import net.igiari.andromeda.aggregator.services.TeamDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AndromedaController {
  private final AggregatorConfig aggregatorConfig;
  private final TeamDashboardService teamDashboardService;

  public AndromedaController(
      AggregatorConfig aggregatorConfig, TeamDashboardServiceProvider teamDashboardServiceProvider) {
    this.aggregatorConfig = aggregatorConfig;
    this.teamDashboardService = teamDashboardServiceProvider.getTeamDashboardService();
  }

  @RequestMapping("/")
  public String index() {
    return "Greetings from Andromeda aggregator!";
  }

  @GetMapping("/team/{teamName}")
  public TeamDashboard team(@PathVariable String teamName) {
    return teamDashboardService.createTeamDashboard(teamName);
  }

  @GetMapping("/config")
  public AggregatorConfig aggregatorConfig() {
    return aggregatorConfig;
  }
}
