package net.igiari.andromeda.collector.cluster;

import net.igiari.andromeda.collector.cluster.comparers.Nameable;

import java.util.Objects;

public class Dependency implements Nameable {
  private String name;
  private boolean up;

  public Dependency(String name, boolean up) {
    this.name = name;
    this.up = up;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isUp() {
    return up;
  }

  public void setUp(boolean up) {
    this.up = up;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Dependency that = (Dependency) o;
    return up == that.up && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, up);
  }

  @Override
  public String toString() {
    return "Dependency{" + "name='" + name + '\'' + ", up=" + up + '}';
  }
}
