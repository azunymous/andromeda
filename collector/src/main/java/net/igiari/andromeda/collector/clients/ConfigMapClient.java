package net.igiari.andromeda.collector.clients;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.KubernetesClient;
import net.igiari.andromeda.collector.cluster.FeatureFlag;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

// Currently unused.
public class ConfigMapClient {

  private final KubernetesClient kubernetesClient;

  public ConfigMapClient(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  public List<FeatureFlag> getConfigMaps(
      String namespaceName,
      Map<String, String> selector,
      Map<String, String> withoutSelector,
      String configMapName) {
    return kubernetesClient.configMaps().inNamespace(namespaceName).withLabels(selector)
        .withoutLabels(withoutSelector).list().getItems().stream()
        .filter(configMap -> configMap.getMetadata().getName().equals(configMapName))
        .findFirst()
        .map(this::CreateConfigMapFrom)
        .orElse(emptyList());
  }

  private List<FeatureFlag> CreateConfigMapFrom(ConfigMap configMap) {
    return configMap.getData().entrySet().stream()
        .map((e) -> new FeatureFlag(e.getKey(), e.getValue()))
        .collect(toList());
  }
}
