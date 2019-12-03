package net.igiari.andromeda.collector.clients;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.Team;
import net.igiari.andromeda.collector.config.ClusterConfig;
import net.igiari.andromeda.collector.config.GlobalConfig;
import net.igiari.andromeda.collector.config.TeamConfig;

public class TeamsClient {
  private GlobalConfig globalConfig;
  private ClusterConfig clusterConfig;
  private ApplicationsClient applicationsClient;

  public TeamsClient(
      GlobalConfig globalConfig,
      ClusterConfig clusterConfig,
      ApplicationsClient applicationsClient) {
    this.globalConfig = globalConfig;
    this.clusterConfig = clusterConfig;
    this.applicationsClient = applicationsClient;
  }

  public Team getTeam(String teamName) {
    Optional<TeamConfig> teamConfig = getTeamConfig(teamName);
    List<Application> teamApplications =
        teamConfig
            .stream()
            .map(TeamConfig::getApplications)
            .flatMap(Collection::stream)
            .map(
                application ->
                    applicationsClient.getApplication(
                        application, clusterConfig.getNamespaceSuffixes()))
            .collect(toList());

    List<String> clusterEnvironments = getClusterEnvironments(teamApplications);
    return new Team(teamName, teamApplications, clusterEnvironments);
  }

  private List<String> getClusterEnvironments(List<Application> teamApplications) {
    return teamApplications
        .stream()
        .flatMap(app -> app.getEnvironments().stream())
        .map(Environment::getEnvironmentName)
        .distinct()
        .sorted(applicationsClient::environmentPriority)
        .collect(toList());
  }

  private Optional<TeamConfig> getTeamConfig(String teamName) {
    return globalConfig
        .getTeams()
        .stream()
        .filter(teamConfig -> teamConfig.getName().equals(teamName))
        .findFirst();
  }
}
