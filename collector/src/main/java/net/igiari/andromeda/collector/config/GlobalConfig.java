package net.igiari.andromeda.collector.config;

import static net.igiari.andromeda.collector.config.CanaryConfiguration.defaultCanaryConfiguration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("global")
public class GlobalConfig {
  private List<TeamConfig> teams;
  private CanaryConfiguration canary = defaultCanaryConfiguration();
  private String defaultSelectorKey = "app";

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

  public String getDefaultSelectorKey() {
    return defaultSelectorKey;
  }

  public void setDefaultSelectorKey(String defaultSelectorKey) {
    this.defaultSelectorKey = defaultSelectorKey;
  }
}
