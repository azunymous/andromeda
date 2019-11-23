package net.igiari.andromeda.aggregator.dashboard;

import java.util.Map;
import java.util.Objects;

public class TeamDashboard {
  private final String teamName;
  private final Map<String, ClusterGroupDashboard> clusterGroupDashboardList;

  public TeamDashboard(String teamName, Map<String, ClusterGroupDashboard> clusterGroupDashboardList) {
    this.teamName = teamName;
    this.clusterGroupDashboardList = clusterGroupDashboardList;
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
