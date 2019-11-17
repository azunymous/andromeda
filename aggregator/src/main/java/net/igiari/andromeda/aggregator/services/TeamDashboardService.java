package net.igiari.andromeda.aggregator.services;

import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.aggregator.dashboard.TeamDashboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class TeamDashboardService {
  private final Map<String, ClusterGroupDashboardService> clusterGroupDashboardServices;

  public TeamDashboardService(
      Map<String, ClusterGroupDashboardService> clusterGroupDashboardServices) {
    this.clusterGroupDashboardServices = clusterGroupDashboardServices;
  }

  public TeamDashboard createTeamDashboard(String team) {
    List<ClusterGroupDashboard> clusterGroupDashboards = clusterGroupDashboardServices.entrySet().stream()
        .map(
            clusterGroupService ->
                clusterGroupService.getValue().createTeam(team, clusterGroupService.getKey()))
        .flatMap(Optional::stream)
        .collect(toList());
    return new TeamDashboard(team, clusterGroupDashboards);
  }
}
