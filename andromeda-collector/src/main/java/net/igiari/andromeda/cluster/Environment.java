package net.igiari.andromeda.cluster;

import java.util.Objects;

public class Environment {
  private final String namespaceName;
  private final PodController podController;

  public Environment(String namespaceName, PodController podController) {
    this.namespaceName = namespaceName;
    this.podController = podController;
  }

  public String getNamespaceName() {
    return namespaceName;
  }

  public PodController getPodController() {
    return podController;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Environment that = (Environment) o;
    return Objects.equals(namespaceName, that.namespaceName) &&
        Objects.equals(podController, that.podController);
  }

  @Override
  public int hashCode() {
    return Objects.hash(namespaceName, podController);
  }

  @Override
  public String toString() {
    return "Environment{" +
        "namespaceName='" + namespaceName + '\'' +
        ", podController=" + podController +
        '}';
  }
}
