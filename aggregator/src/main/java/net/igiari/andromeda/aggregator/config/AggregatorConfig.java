package net.igiari.andromeda.aggregator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("aggregator")
public class AggregatorConfig {
  private List<ClusterConfig> clusters;

  public AggregatorConfig() {}

  public List<ClusterConfig> getClusters() {
    return clusters;
  }

  public void setClusters(List<ClusterConfig> clusters) {
    this.clusters = clusters;
  }
}
