package net.igiari.andromeda.aggregator.dashboard;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

public class TeamDashboard {
  private final String teamName;
  private final Map<String, ClusterGroupDashboard> clusterGroupDashboardList;

  public TeamDashboard(String teamName, List<ClusterGroupDashboard> clusterGroupDashboardList) {
    this.teamName = teamName;
    this.clusterGroupDashboardList =
        clusterGroupDashboardList.stream()
            .collect(Collectors.toMap(ClusterGroupDashboard::getClusterGroup, identity()));
  }

  public String getTeamName() {
    return teamName;
  }

  public Map<String, ClusterGroupDashboard> getClusterGroupDashboardList() {
    return clusterGroupDashboardList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TeamDashboard that = (TeamDashboard) o;
    return Objects.equals(teamName, that.teamName)
        && Objects.equals(clusterGroupDashboardList, that.clusterGroupDashboardList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamName, clusterGroupDashboardList);
  }

  @Override
  public String toString() {
    return "TeamDashboard{"
        + "teamName='"
        + teamName
        + '\''
        + ", clusterGroupDashboardList="
        + clusterGroupDashboardList
        + '}';
  }
}
