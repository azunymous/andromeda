package net.igiari.andromeda.collector.clients;

import static java.util.function.Predicate.not;

import io.fabric8.kubernetes.api.model.extensions.HTTPIngressPath;
import io.fabric8.kubernetes.api.model.extensions.Ingress;
import io.fabric8.kubernetes.api.model.extensions.IngressRule;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IngressClient {

  private final KubernetesClient kubernetesClient;

  public IngressClient(KubernetesClient kubernetesClient) {
    this.kubernetesClient = kubernetesClient;
  }

  public List<String> getIngresses(String namespaceName) {
    return kubernetesClient
        .extensions()
        .ingresses()
        .inNamespace(namespaceName)
        .list()
        .getItems()
        .stream()
        .flatMap(this::getIngressURLs)
        .filter(not(String::isEmpty))
        .collect(Collectors.toUnmodifiableList());
  }

  private Stream<String> getIngressURLs(Ingress ingress) {
    List<String> ingressURLs = new ArrayList<>();
    for (IngressRule ingressRule : ingress.getSpec().getRules()) {
      for (HTTPIngressPath path : ingressRule.getHttp().getPaths()) {
        if (ingressRule.getHost() != null) {
          ingressURLs.add(ingressRule.getHost() + path.getPath());
        }
      }
    }
    return ingressURLs.stream();
  }
}
