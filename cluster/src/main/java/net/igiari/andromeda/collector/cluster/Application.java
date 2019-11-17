package net.igiari.andromeda.collector.cluster;

import java.util.List;
import java.util.Objects;

public class Application {
  private String name;
  private List<Environment> environments;

  public Application(String name, List<Environment> environments) {
    this.name = name;
    this.environments = environments;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Environment> getEnvironments() {
    return environments;
  }

  public void setEnvironments(List<Environment> environments) {
    this.environments = environments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Application that = (Application) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(environments, that.environments);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, environments);
  }

  @Override
  public String toString() {
    return "Application{" +
        "name='" + name + '\'' +
        ", environments=" + environments +
        '}';
  }

  public static int byName(Application application1, Application application2) {
    return application1.getName().compareTo(application2.getName());
  }
}
