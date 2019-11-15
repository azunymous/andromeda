package net.igiari.andromeda.collector.cluster;

import java.util.List;
import java.util.Objects;

public class Team {
  private final String teamName;
  private final List<Application> applications;

  public Team(String teamName, List<Application> applications) {
    this.teamName = teamName;
    this.applications = applications;
  }

  public String getTeamName() {
    return teamName;
  }

  public List<Application> getApplications() {
    return applications;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Team team = (Team) o;
    return Objects.equals(teamName, team.teamName) &&
        Objects.equals(applications, team.applications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamName, applications);
  }

  @Override
  public String toString() {
    return "Team{" +
        "teamName='" + teamName + '\'' +
        ", applications=" + applications +
        '}';
  }
}
