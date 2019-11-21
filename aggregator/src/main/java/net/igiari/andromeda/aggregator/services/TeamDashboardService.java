package net.igiari.andromeda.aggregator.services;

import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.aggregator.dashboard.TeamDashboard;
import net.igiari.andromeda.aggregator.transformers.ClusterGroupTransformer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class TeamDashboardService {
  private final Map<String, ClusterGroupDashboardService> clusterGroupDashboardServices;
  private final List<ClusterGroupTransformer> transformers;

  public TeamDashboardService(
      Map<String, ClusterGroupDashboardService> clusterGroupDashboardServices,
      List<ClusterGroupTransformer> transformers) {
    this.clusterGroupDashboardServices = clusterGroupDashboardServices;
    this.transformers = transformers;
  }

  public TeamDashboard createTeamDashboard(String team) {
    List<ClusterGroupDashboard> clusterGroupDashboards =
        clusterGroupDashboardServices.entrySet().stream()
            .map(
                clusterGroupService ->
                    clusterGroupService.getValue().createTeam(team, clusterGroupService.getKey()))
            .flatMap(Optional::stream)
            .map(this::transform)
            .collect(toList());
    return new TeamDashboard(team, clusterGroupDashboards);
  }

  private ClusterGroupDashboard transform(ClusterGroupDashboard dashboard) {
    for (ClusterGroupTransformer transformer : transformers) {
      dashboard = transformer.transform(dashboard);
    }
    return dashboard;
  }
}
