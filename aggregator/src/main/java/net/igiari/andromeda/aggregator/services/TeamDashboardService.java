package net.igiari.andromeda.aggregator.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.aggregator.dashboard.TeamDashboard;
import net.igiari.andromeda.aggregator.transformers.ClusterGroupTransformer;

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
    final Map<String, ClusterGroupDashboard> clusterGroupDashboards =
        clusterGroupDashboardServices
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    es ->
                        getTransformedClusterGroupDashboard(team, es.getValue())
                            .orElse(ClusterGroupDashboard.empty())));
    return new TeamDashboard(team, clusterGroupDashboards);
  }

  private Optional<ClusterGroupDashboard> getTransformedClusterGroupDashboard(
      String team, ClusterGroupDashboardService clusterGroupDashboardService) {
    return clusterGroupDashboardService.createClusterGroupDashboard(team).map(this::transform);
  }

  private ClusterGroupDashboard transform(ClusterGroupDashboard dashboard) {
    for (ClusterGroupTransformer transformer : transformers) {
      dashboard = transformer.transform(dashboard);
    }
    return dashboard;
  }
}
