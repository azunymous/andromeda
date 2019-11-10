package net.igiari.andromeda.fetch;

import io.fabric8.kubernetes.client.KubernetesClient;
import net.igiari.andromeda.cluster.Application;
import net.igiari.andromeda.cluster.Environment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Applications {
  private KubernetesClient kubernetesClient;
  private final Environments environments;

  public Applications(KubernetesClient kubernetesClient, Environments environments) {
    this.kubernetesClient = kubernetesClient;
    this.environments = environments;
  }

  public Application getApplication(
      net.igiari.andromeda.config.Application application, List<String> namespaceSuffixes) {
    List<Environment> environments =
        namespaceSuffixes.stream()
            .map(
                namespaceSuffix ->
                    this.environments.getEnvironment(
                        namespaceNameFrom(application, namespaceSuffix),
                        namespaceSuffix.substring(1),
                        application.getControllerType(),
                        application.getSelector(),
                        application.getContainerName()))
            .flatMap(Optional::stream)
            .sorted(this::environmentPriority)
            .collect(Collectors.toList());

    return new Application(application.getName(), environments);
  }

  private String namespaceNameFrom(
      net.igiari.andromeda.config.Application application, String namespaceSuffix) {
    return application.getPrefix() + namespaceSuffix;
  }

  private int environmentPriority(Environment env1, Environment env2) {
    return env1.getNamespaceName().compareTo(env2.getNamespaceName());
  }
}
