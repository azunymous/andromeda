package net.igiari.andromeda.aggregator.dashboard;

import com.google.gson.annotations.Expose;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Team;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

public class ClusterGroupDashboard {
  private String clusterGroup;
  @Expose private List<Application> applications;
  @Expose private List<String> clusterGroupEnvironments;

  public static ClusterGroupDashboard create(Team team) {
    return new ClusterGroupDashboard(team.getApplications(), team.getClusterEnvironments());
  }

  public ClusterGroupDashboard(
      List<Application> applications, List<String> clusterGroupEnvironments) {
    this.applications = applications;
    this.clusterGroupEnvironments = clusterGroupEnvironments;
  }

  public ClusterGroupDashboard(
      String clusterGroup, List<Application> applications, List<String> clusterGroupEnvironments) {
    this.clusterGroup = clusterGroup;
    this.applications = applications;
    this.clusterGroupEnvironments = clusterGroupEnvironments;
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

  public String getClusterGroup() {
    return clusterGroup;
  }

  public void setClusterGroup(String clusterGroup) {
    this.clusterGroup = clusterGroup;
  }

  public ClusterGroupDashboard withClusterGroup(String clusterGroup) {
    this.clusterGroup = clusterGroup;
    return this;
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
    return Objects.equals(clusterGroup, that.clusterGroup)
        && Objects.equals(applications, that.applications)
        && Objects.equals(clusterGroupEnvironments, that.clusterGroupEnvironments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clusterGroup, applications, clusterGroupEnvironments);
  }

  @Override
  public String toString() {
    return "ClusterGroupDashboard{"
        + "clusterGroup='"
        + clusterGroup
        + '\''
        + ", applications="
        + applications
        + ", clusterGroupEnvironments="
        + clusterGroupEnvironments
        + '}';
  }
}
