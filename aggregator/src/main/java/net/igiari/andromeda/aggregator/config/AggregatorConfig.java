package net.igiari.andromeda.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties("aggregator")
public class AggregatorConfig {
  private Map<String, ClusterConfig> clusters;

  public AggregatorConfig() {}

  public Map<String, ClusterConfig> getClusters() {
    return clusters;
  }

  public void setClusters(Map<String, ClusterConfig> clusters) {
    this.clusters = clusters;
  }
}
