package net.igiari.andromeda.aggregator.config;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("aggregator")
public class AggregatorConfig {
  private Map<String, ClusterConfig> clusters;
  private List<String> teams;
  private String prometheusURI;

  public AggregatorConfig() {}

  public Map<String, ClusterConfig> getClusters() {
    return clusters;
  }

  public void setClusters(Map<String, ClusterConfig> clusters) {
    this.clusters = clusters;
  }

  public String getPrometheusURI() {
    return prometheusURI;
  }

  public void setPrometheusURI(String prometheusURI) {
    this.prometheusURI = prometheusURI;
  }

  public List<String> getTeams() {
    return teams;
  }

  public void setTeams(List<String> teams) {
    this.teams = teams;
  }
}
