package net.igiari.andromeda.aggregator.dashboard;

import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Team;

import java.util.List;
import java.util.Objects;

public class ClusterGroupDashboard {
  private String clusterGroup;
  private List<Application> applications;

  public static ClusterGroupDashboard create(Team team) {
    return new ClusterGroupDashboard(team.getApplications());
  }

  public ClusterGroupDashboard(List<Application> applications) {
    this.applications = applications;
  }

  public ClusterGroupDashboard(String clusterGroup, List<Application> applications) {
    this.clusterGroup = clusterGroup;
    this.applications = applications;
  }

  public List<Application> getApplications() {
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


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClusterGroupDashboard that = (ClusterGroupDashboard) o;
    return Objects.equals(clusterGroup, that.clusterGroup) &&
        Objects.equals(applications, that.applications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clusterGroup, applications);
  }

  @Override
  public String toString() {
    return "TeamDashboard{" +
        "clusterGroup='" + clusterGroup + '\'' +
        ", applications=" + applications +
        '}';
  }
}
