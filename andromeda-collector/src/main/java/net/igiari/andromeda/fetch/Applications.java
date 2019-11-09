package net.igiari.andromeda.fetch;

import io.fabric8.kubernetes.client.KubernetesClient;
import net.igiari.andromeda.cluster.Application;

public class Applications {
  private KubernetesClient kubernetesClient;

  public Applications(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  public Application getApplication() {
    throw new RuntimeException("not yet implemented");
  }
}
