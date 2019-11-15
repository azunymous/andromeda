package net.igiari.andromeda.collector.config;

import java.util.List;

public class TeamConfig {
  private String name;
  private List<ApplicationConfig> applications;

  public TeamConfig() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<ApplicationConfig> getApplications() {
    return applications;
  }

  public void setApplications(List<ApplicationConfig> applications) {
    this.applications = applications;
  }
}
