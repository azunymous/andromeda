package net.igiari.andromeda.cluster;

import java.util.List;

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
}
