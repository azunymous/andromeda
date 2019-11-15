package net.igiari.andromeda.collector.cluster;

import java.util.Objects;

public class Pod {
  private final String name;
  private final String version;
  private final Status status;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pod pod = (Pod) o;
    return Objects.equals(name, pod.name) &&
        Objects.equals(version, pod.version) &&
        status == pod.status;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, version, status);
  }

  @Override
  public String toString() {
    return "Pod{" +
        "name='" + name + '\'' +
        ", version='" + version + '\'' +
        ", status=" + status +
        '}';
  }

  public static int byName(Pod pod1, Pod pod2) {
    return pod1.getName().compareTo(pod2.getName());
  }
}
