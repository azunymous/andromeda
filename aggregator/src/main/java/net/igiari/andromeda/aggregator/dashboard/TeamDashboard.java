package net.igiari.andromeda.aggregator.dashboard;

import net.igiari.andromeda.collector.cluster.Application;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TeamDashboard {
  private final String teamName;
  private final Map<String, List<Application>> clusterGroupDashboardList;

  public TeamDashboard(String teamName, List<ClusterGroupDashboard> clusterGroupDashboardList) {
    this.teamName = teamName;
    this.clusterGroupDashboardList =
        clusterGroupDashboardList.stream()
            .collect(
                Collectors.toMap(
                    ClusterGroupDashboard::getClusterGroup,
                    ClusterGroupDashboard::getApplications));
  }

  public String getTeamName() {
    return teamName;
  }

  public Map<String, List<Application>> getClusterGroupDashboardList() {
    return clusterGroupDashboardList;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TeamDashboard that = (TeamDashboard) o;
    return Objects.equals(teamName, that.teamName) &&
        Objects.equals(clusterGroupDashboardList, that.clusterGroupDashboardList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamName, clusterGroupDashboardList);
  }

  @Override
  public String toString() {
    return "TeamDashboard{" +
        "teamName='" + teamName + '\'' +
        ", clusterGroupDashboardList=" + clusterGroupDashboardList +
        '}';
  }
}
