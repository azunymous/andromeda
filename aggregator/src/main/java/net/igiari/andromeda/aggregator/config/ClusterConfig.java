package net.igiari.andromeda.aggregator.config;

public class ClusterConfig {
  private String name;
  private String collector;

  public ClusterConfig() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCollector() {
    return collector;
  }

  public void setCollector(String collector) {
    this.collector = collector;
  }
}
