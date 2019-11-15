package net.igiari.andromeda.fetch;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

@Component
public class Client {
  private final KubernetesClient kubernetesClient;

  public Client() {
    this.kubernetesClient = new DefaultKubernetesClient();
  }

  public Client(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  public KubernetesClient getKubernetesClient() {
    return kubernetesClient;
  }
}
