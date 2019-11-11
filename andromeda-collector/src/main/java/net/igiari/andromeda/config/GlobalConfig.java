package net.igiari.andromeda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("global")
public class GlobalConfig {
  private List<TeamConfig> teams;

  public GlobalConfig() {}

  public List<TeamConfig> getTeams() {
    return teams;
  }

  public void setTeams(List<TeamConfig> teamConfigs) {
    this.teams = teamConfigs;
  }
}
