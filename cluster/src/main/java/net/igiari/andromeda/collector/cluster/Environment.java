package net.igiari.andromeda.collector.cluster;

import java.util.Objects;

public class Environment {
  private final String environmentName;
  private final String namespaceName;
  private final PodController podController;
  private PodController canaryPodController;

  public Environment(String environmentName, String namespaceName, PodController podController) {
    this.environmentName = environmentName;
    this.namespaceName = namespaceName;
    this.podController = podController;
    this.canaryPodController = PodController.empty();
  }

  public static Environment empty() {
    return new Environment("", "", PodController.empty());
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

  public PodController getCanaryPodController() {
    return canaryPodController;
  }

  public void setCanaryPodController(PodController canaryPodController) {
    this.canaryPodController = canaryPodController;
  }

  public boolean hasSameNamespace(Environment environment2) {
    return this.getNamespaceName().equals(environment2.getNamespaceName());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Environment that = (Environment) o;
    return Objects.equals(environmentName, that.environmentName)
        && Objects.equals(namespaceName, that.namespaceName)
        && Objects.equals(podController, that.podController)
        && Objects.equals(canaryPodController, that.canaryPodController);
  }

  @Override
  public int hashCode() {
    return Objects.hash(environmentName, namespaceName, podController, canaryPodController);
  }

  @Override
  public String toString() {
    return "Environment{"
        + "environmentName='"
        + environmentName
        + '\''
        + ", namespaceName='"
        + namespaceName
        + '\''
        + ", podController="
        + podController
        + ", canaryPodController="
        + canaryPodController
        + '}';
  }
}
