package net.igiari.andromeda.collector.cluster;

import java.util.List;
import java.util.Objects;

public class Pod {
  private final String name;
  private final String version;
  private final Status status;
  private List<Dependency> dependencies;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pod pod = (Pod) o;
    return Objects.equals(name, pod.name) &&
        Objects.equals(version, pod.version) &&
        status == pod.status &&
        Objects.equals(dependencies, pod.dependencies);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version, status, dependencies);
  }

  @Override
  public String toString() {
    return "Pod{" +
        "name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", status=" + status +
        ", dependencies=" + dependencies +
        '}';
  }

  public static int byName(Pod pod1, Pod pod2) {
    return pod1.getName().compareTo(pod2.getName());
  }
}
