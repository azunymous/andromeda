package net.igiari.andromeda.collector.cluster;

import net.igiari.andromeda.collector.cluster.comparers.Nameable;

import java.util.Objects;

public class FeatureFlag implements Nameable {
  private String name;
  private String strategy;

  public FeatureFlag(String name, String strategy) {
    this.name = name;
    this.strategy = strategy;
  }

  public FeatureFlag(String name, double strategy) {
    this.name = name;
    this.strategy = String.valueOf(strategy);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStrategy() {
    return strategy;
  }

  public void setStrategy(String strategy) {
    this.strategy = strategy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FeatureFlag that = (FeatureFlag) o;
    return Objects.equals(name, that.name) && Objects.equals(strategy, that.strategy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, strategy);
  }

  @Override
  public String toString() {
    return "FeatureFlag{" + "name='" + name + '\'' + ", strategy='" + strategy + '\'' + '}';
  }
}
