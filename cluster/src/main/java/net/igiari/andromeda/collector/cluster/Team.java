package net.igiari.andromeda.collector.cluster;

import java.util.List;
import java.util.Objects;

public class Team {
  private final String teamName;
  private final List<Application> applications;
  private final List<String> clusterEnvironments;

  public Team(String teamName, List<Application> applications, List<String> clusterEnvironments) {
    this.teamName = teamName;
    this.applications = applications;
    this.clusterEnvironments = clusterEnvironments;
  }

  public String getTeamName() {
    return teamName;
  }

  public List<Application> getApplications() {
    return applications;
  }

  public List<String> getClusterEnvironments() {
    return clusterEnvironments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Team team = (Team) o;
    return Objects.equals(teamName, team.teamName)
        && Objects.equals(applications, team.applications)
        && Objects.equals(clusterEnvironments, team.clusterEnvironments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamName, applications, clusterEnvironments);
  }

  @Override
  public String toString() {
    return "Team{"
        + "teamName='"
        + teamName
        + '\''
        + ", applications="
        + applications
        + ", clusterEnvironments="
        + clusterEnvironments
        + '}';
  }
}
