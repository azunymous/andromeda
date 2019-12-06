package net.igiari.andromeda.collector.clients;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.config.ApplicationConfig;
import net.igiari.andromeda.collector.config.PriorityConfig;

public class ApplicationsClient {

  private final EnvironmentsClient environmentsClient;
  private final String defaultSelectorKey;
  private PriorityConfig priorityConfig;

  public ApplicationsClient(
      EnvironmentsClient environmentsClient,
      String defaultSelectorKey,
      PriorityConfig priorityConfig) {
    this.environmentsClient = environmentsClient;
    this.defaultSelectorKey = defaultSelectorKey;
    this.priorityConfig = priorityConfig;
  }

  public Application getApplication(
      ApplicationConfig applicationConfig, List<String> namespaceSuffixes) {
    List<Environment> environments =
        namespaceSuffixes
            .stream()
            .map(
                namespaceSuffix ->
                    this.environmentsClient.getEnvironment(
                        namespaceSuffix,
                        namespaceNameFrom(applicationConfig, namespaceSuffix),
                        applicationConfig.getControllerType(),
                        applicationConfig.getSelectorOrDefault(defaultSelectorKey),
                        applicationConfig.getContainerName()))
            .flatMap(Optional::stream)
            .sorted(this::environmentPriority)
            .collect(Collectors.toList());

    return new Application(applicationConfig.getName(), environments);
  }

  private String namespaceNameFrom(ApplicationConfig applicationConfig, String namespaceSuffix) {
    return applicationConfig.getPrefix() + namespaceSuffix;
  }

  int environmentPriority(String env1, String env2) {
    if (priorityConfig.getFirst().contains(env1)) {
      if (priorityConfig.getFirst().contains(env2)) {
        return priorityConfig.getFirst().indexOf(env1) - priorityConfig.getFirst().indexOf(env2);
      }
      return -1;
    }
    if (priorityConfig.getFirst().contains(env2)) {
      return 1;
    }
    if (priorityConfig.getLast().contains(env1)) {
      if (priorityConfig.getLast().contains(env2)) {
        return priorityConfig.getLast().indexOf(env1) - priorityConfig.getLast().indexOf(env2);
      }
      return 1;
    }
    if (priorityConfig.getLast().contains(env2)) {
      return -1;
    }
    return env1.compareTo(env2);
  }

  int environmentPriority(Environment env1, Environment env2) {
    return environmentPriority(env1.getEnvironmentName(), env2.getEnvironmentName());
  }
}
