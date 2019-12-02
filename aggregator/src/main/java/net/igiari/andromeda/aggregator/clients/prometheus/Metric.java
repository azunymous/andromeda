package net.igiari.andromeda.aggregator.clients.prometheus;

import com.google.gson.annotations.SerializedName;

public class Metric {

  @SerializedName("pod_name")
  private String podName;

  @SerializedName("__name__")
  private String name;

  @SerializedName("dependency_name")
  private String dependencyName;

  @SerializedName("feature_flag_name")
  private String featureFlagName;

  public void setPodName(String podName) {
    this.podName = podName;
  }

  public String getPodName() {
    return podName;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDependencyName(String dependencyName) {
    this.dependencyName = dependencyName;
  }

  public String getFeatureFlagName() {
    return featureFlagName;
  }

  public void setFeatureFlagName(String featureFlagName) {
    this.featureFlagName = featureFlagName;
  }

  public String getDependencyName() {
    return dependencyName;
  }

  @Override
  public String toString() {
    return "Metric{"
        + "instance = '"
        + podName
        + '\''
        + ",__name__ = '"
        + name
        + '\''
        + ",dependencyName = '"
        + dependencyName
        + '\''
        + "}";
  }
}
