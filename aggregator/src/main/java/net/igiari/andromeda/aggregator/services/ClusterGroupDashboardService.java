package net.igiari.andromeda.aggregator.services;

import net.igiari.andromeda.aggregator.clients.CollectorClient;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class ClusterGroupDashboardService {
  private List<CollectorClient> collectorClients;

  private Logger logger = LoggerFactory.getLogger(ClusterGroupDashboardService.class);

  public ClusterGroupDashboardService(List<CollectorClient> collectorClients) {
    this.collectorClients = collectorClients;
  }

  private static ClusterGroupDashboard combine(ClusterGroupDashboard t1, ClusterGroupDashboard t2) {
    List<Application> applications = new ArrayList<>();
    Stream.of(t1.getApplications(), t2.getApplications()).forEach(applications::addAll);
    return new ClusterGroupDashboard(applications);
  }

  public Optional<ClusterGroupDashboard> createTeam(String teamName, String clusterGroup) {
    return collectorClients.stream()
        .map(collectorClient -> collectorClient.collect(teamName))
        .map(cf -> cf.orTimeout(2, SECONDS))
        .filter(cf -> !cf.isCompletedExceptionally())
        .map(cf -> cf.thenApply(ClusterGroupDashboard::create))
        .reduce((cf1, cf2) -> cf1.thenCombine(cf2, ClusterGroupDashboardService::combine))
        .map(cf -> cf.exceptionally(throwable -> logAndIgnore(throwable, teamName, clusterGroup)))
        .map(CompletableFuture::join)
        .map(ClusterGroupDashboardService::squashApplications)
        .map(clusterGroupDashboard -> clusterGroupDashboard.withClusterGroup(clusterGroup));
  }

  private ClusterGroupDashboard logAndIgnore(
      Throwable throwable, String teamName, String clusterGroup) {
    logger.warn(
        "Failed to get team " + teamName + " for cluster group " + clusterGroup + ": " + throwable);
    return null;
  }

  private static ClusterGroupDashboard squashApplications(
      ClusterGroupDashboard clusterGroupDashboard) {
    List<Application> squashedApplications =
        clusterGroupDashboard.getApplications().stream()
            .collect(
                Collectors.toMap(
                    Application::getName,
                    Application::getEnvironments,
                    ClusterGroupDashboardService::squashEnvironments))
            .entrySet()
            .stream()
            .map(ClusterGroupDashboardService::toApplication)
            .sorted(Application::byName)
            .collect(toList());
    clusterGroupDashboard.setApplications(squashedApplications);
    return clusterGroupDashboard;
  }

  private static Application toApplication(Map.Entry<String, List<Environment>> applicationEntry) {
    return new Application(applicationEntry.getKey(), applicationEntry.getValue());
  }

  private static List<Environment> squashEnvironments(
      List<Environment> environments1, List<Environment> environments2) {
    List<Environment> environments = new ArrayList<>();
    environments.addAll(environments1);
    environments.addAll(environments2);
    return environments;
  }
}
