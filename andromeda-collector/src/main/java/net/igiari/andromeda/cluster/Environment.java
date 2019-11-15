package net.igiari.andromeda.cluster;

import java.util.Objects;

public class Environment {
  private final String environmentName;
  private final String namespaceName;
  private final PodController podController;

  public Environment(String environmentName, String namespaceName, PodController podController) {
    this.environmentName = environmentName;
    this.namespaceName = namespaceName;
    this.podController = podController;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public PodController getPodController() {
    return podController;
  }

  public String getEnvironmentName() {
    return environmentName;
  }

  public boolean hasSameNamespace(Environment environment2) {
    return this.getNamespaceName().equals(environment2.getNamespaceName());
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Environment that = (Environment) o;
    return Objects.equals(namespaceName, that.namespaceName)
        && Objects.equals(podController, that.podController);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespaceName, podController);
  }

  @Override
  public String toString() {
    return "Environment{"
        + "namespaceName='"
        + namespaceName
        + '\''
        + ", podController="
        + podController
        + '}';
  }
}
