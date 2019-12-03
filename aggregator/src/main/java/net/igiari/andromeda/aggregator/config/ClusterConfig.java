package net.igiari.andromeda.aggregator.config;

import java.util.List;

public class ClusterConfig {
  private DataCenterType type;
  private List<String> collectors;

  public ClusterConfig() {}

  public DataCenterType getType() {
    return type;
  }

  public void setType(DataCenterType type) {
    this.type = type;
  }

  public List<String> getCollectors() {
    return collectors;
  }

  public void setCollectors(List<String> collectors) {
    this.collectors = collectors;
  }
}
