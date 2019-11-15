package net.igiari.andromeda.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("cluster")
public class ClusterConfig {
  private List<String> namespaceSuffixes;
  private PriorityConfig priority;

  public ClusterConfig() {}

  public List<String> getNamespaceSuffixes() {
    return namespaceSuffixes;
  }

  public void setNamespaceSuffixes(List<String> namespaceSuffixes) {
    this.namespaceSuffixes = namespaceSuffixes;
  }

  public PriorityConfig getPriority() {
    if (priority == null) {
      return new PriorityConfig();
    }
    return priority;
  }

  public void setPriority(PriorityConfig priority) {
    this.priority = priority;
  }
}
