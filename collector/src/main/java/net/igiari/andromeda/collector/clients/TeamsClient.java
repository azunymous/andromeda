package net.igiari.andromeda.collector.clients;

import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Team;
import net.igiari.andromeda.collector.config.GlobalConfig;
import net.igiari.andromeda.collector.config.TeamConfig;
import net.igiari.andromeda.collector.config.ClusterConfig;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

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
        teamConfig.stream()
            .map(TeamConfig::getApplications)
            .flatMap(Collection::stream)
            .map(
                application ->
                    applicationsClient.getApplication(
                        application, clusterConfig.getNamespaceSuffixes()))
            .collect(toList());
    return new Team(teamName, teamApplications);
  }

  private Optional<TeamConfig> getTeamConfig(String teamName) {
    return globalConfig.getTeams().stream()
        .filter(teamConfig -> teamConfig.getName().equals(teamName))
        .findFirst();
  }
}