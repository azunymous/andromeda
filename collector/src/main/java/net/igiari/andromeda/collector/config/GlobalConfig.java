package net.igiari.andromeda.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.igiari.andromeda.collector.config.CanaryConfiguration.defaultCanaryConfiguration;

@Component
@ConfigurationProperties("global")
public class GlobalConfig {
  private List<TeamConfig> teams;
  private CanaryConfiguration canary = defaultCanaryConfiguration();

  public GlobalConfig() {}

  public List<TeamConfig> getTeams() {
    return teams;
  }

  public void setTeams(List<TeamConfig> teamConfigs) {
    this.teams = teamConfigs;
  }

  public CanaryConfiguration getCanary() {
    return canary;
  }

  public void setCanary(CanaryConfiguration canary) {
    this.canary = canary;
  }
}
