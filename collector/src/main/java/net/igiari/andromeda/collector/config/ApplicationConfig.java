package net.igiari.andromeda.collector.config;

import net.igiari.andromeda.collector.cluster.PodControllerType;

import java.util.Map;

public class ApplicationConfig {
  private String name;
  // Namespace Prefix
  private String prefix;
  // Deployment/StatefulSet Labels to select on
  private Map<String, String> selector;;
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
    // HACK: Spring will use getSelector when trying to populate selector from configuration
    // - resulting in a Map of app = null. The name != null check prevents this.
    if (selector == null && name != null) {
      return Map.of("app", name);
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
