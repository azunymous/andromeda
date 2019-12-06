package net.igiari.andromeda.collector.config;

import static java.util.Collections.emptyMap;

import java.util.Map;
import net.igiari.andromeda.collector.cluster.PodControllerType;

public class ApplicationConfig {
  private String name;
  // Namespace Prefix
  private String prefix;
  // Deployment/StatefulSet Labels to select on
  private Map<String, String> selector = emptyMap();;
  private String deployment;
  private String statefulSet;

  public ApplicationConfig() {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public Map<String, String> getSelector() {
    return selector;
  }

  public Map<String, String> getSelectorOrDefault(String defaultSelectorKey) {
    if (selector.isEmpty()) {
      return Map.of(defaultSelectorKey, name);
    }
    return selector;
  }

  public void setSelector(Map<String, String> selector) {
    this.selector = selector;
  }

  public void setDeployment(String deployment) {
    this.deployment = deployment;
  }

  public void setStatefulSet(String statefulSet) {
    this.statefulSet = statefulSet;
  }

  public String getContainerName() {
    if (statefulSet != null) {
      return statefulSet;
    }
    if (deployment != null) {
      return deployment;
    }
    return name;
  }

  public PodControllerType getControllerType() {
    if (statefulSet != null) {
      return PodControllerType.STATEFULSET;
    }
    return PodControllerType.DEPLOYMENT;
  }
}
