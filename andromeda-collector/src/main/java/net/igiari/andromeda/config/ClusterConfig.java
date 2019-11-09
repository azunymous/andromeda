package net.igiari.andromeda.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("cluster")
public class ClusterConfig {
  private List<String> namespaceSuffixes;

  public ClusterConfig() {}

  public List<String> getNamespaceSuffixes() {
    return namespaceSuffixes;
  }

  public void setNamespaceSuffixes(List<String> namespaceSuffixes) {
    this.namespaceSuffixes = namespaceSuffixes;
  }
}
