package net.igiari.andromeda.aggregator.dashboard;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Objects;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Team;

public class ClusterGroupDashboard {
  private List<Application> applications;
  private List<String> clusterGroupEnvironments;

  public static ClusterGroupDashboard create(Team team) {
    return new ClusterGroupDashboard(team.getApplications(), team.getClusterEnvironments());
  }

  public ClusterGroupDashboard(
      List<Application> applications, List<String> clusterGroupEnvironments) {
    this.applications = applications;
    this.clusterGroupEnvironments = clusterGroupEnvironments;
  }

  public static ClusterGroupDashboard empty() {
    return new ClusterGroupDashboard(emptyList(), emptyList());
  }

  public List<Application> getApplications() {
    if (applications == null) {
      return emptyList();
    }
    return applications;
  }

  public void setApplications(List<Application> applications) {
    this.applications = applications;
  }

  public List<String> getClusterGroupEnvironments() {
    return clusterGroupEnvironments;
  }

  public void setClusterGroupEnvironments(List<String> clusterGroupEnvironments) {
    this.clusterGroupEnvironments = clusterGroupEnvironments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClusterGroupDashboard that = (ClusterGroupDashboard) o;
    return Objects.equals(applications, that.applications)
        && Objects.equals(clusterGroupEnvironments, that.clusterGroupEnvironments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(applications, clusterGroupEnvironments);
  }

  @Override
  public String toString() {
    return "ClusterGroupDashboard{"
        + "applications="
        + applications
        + ", clusterGroupEnvironments="
        + clusterGroupEnvironments
        + '}';
  }
}
