package net.igiari.andromeda.collector.cluster;

import net.igiari.andromeda.collector.cluster.comparers.Nameable;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

public class PodController implements Nameable {
  public static final String UNKNOWN_VERSION = "UNKNOWN";
  private String name;
  private List<Pod> pods;
  private PodControllerType type;
  private String version = UNKNOWN_VERSION;
  private Status status = Status.UNKNOWN;

  public PodController(String name, List<Pod> pods, PodControllerType type) {
    this.name = name;
    this.pods = pods;
    this.type = type;
  }

  public static PodController empty() {
    return new PodController("", emptyList(), PodControllerType.DEPLOYMENT);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Pod> getPods() {
    return pods;
  }

  public void setPods(List<Pod> pods) {
    this.pods = pods;
  }

  public PodControllerType getType() {
    return type;
  }

  public void setType(PodControllerType type) {
    this.type = type;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PodController that = (PodController) o;
    return Objects.equals(name, that.name) && Objects.equals(pods, that.pods) && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, pods, type);
  }

  @Override
  public String toString() {
    return "PodController{" + "name='" + name + '\'' + ", pods=" + pods + ", type=" + type + '}';
  }

  public PodController withTypeToStatefulSet() {
    this.setType(PodControllerType.STATEFULSET);
    return this;
  }
}
