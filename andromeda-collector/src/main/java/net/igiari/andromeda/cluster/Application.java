package net.igiari.andromeda.cluster;

import java.util.List;

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
}
