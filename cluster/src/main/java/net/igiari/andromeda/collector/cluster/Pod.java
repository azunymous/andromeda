package net.igiari.andromeda.collector.cluster;

import net.igiari.andromeda.collector.cluster.comparers.Nameable;

import java.util.List;
import java.util.Objects;

public class Pod implements Nameable {
  private final String name;
  private final String version;
  private final Status status;
  private List<Dependency> dependencies;
  private List<FeatureFlag> featureFlags;

  public Pod(String name, String version, Status status) {
    this.name = name;
    this.version = version;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public Status getStatus() {
    return status;
  }

  public void setDependencies(List<Dependency> dependencies) {
    this.dependencies = dependencies;
  }

  public List<Dependency> getDependencies() {
    return dependencies;
  }

  public List<FeatureFlag> getFeatureFlags() {
    return featureFlags;
  }

  public void setFeatureFlags(List<FeatureFlag> featureFlags) {
    this.featureFlags = featureFlags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pod pod = (Pod) o;
    return Objects.equals(name, pod.name) &&
        Objects.equals(version, pod.version) &&
        status == pod.status &&
        Objects.equals(dependencies, pod.dependencies) &&
        Objects.equals(featureFlags, pod.featureFlags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version, status, dependencies, featureFlags);
  }

  @Override
  public String toString() {
    return "Pod{" +
        "name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", status=" + status +
        ", dependencies=" + dependencies +
        ", featureFlags=" + featureFlags +
        '}';
  }
}
