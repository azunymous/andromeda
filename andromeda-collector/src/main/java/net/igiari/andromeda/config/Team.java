package net.igiari.andromeda.config;

import java.util.List;

public class Team {
  private String name;
  private List<Application> applications;

  public Team() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Application> getApplications() {
    return applications;
  }

  public void setApplications(List<Application> applications) {
    this.applications = applications;
  }
}
