package net.igiari.andromeda.aggregator.services;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.igiari.andromeda.aggregator.clients.CollectorClient;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.comparers.Compare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterGroupDashboardService {
  private String clusterGroup;
  private List<CollectorClient> collectorClients;

  private Logger logger = LoggerFactory.getLogger(ClusterGroupDashboardService.class);

  public ClusterGroupDashboardService(String clusterGroup, List<CollectorClient> collectorClients) {
    this.clusterGroup = clusterGroup;
    this.collectorClients = collectorClients;
  }

  public Optional<ClusterGroupDashboard> createClusterGroupDashboard(String teamName) {
    return collectorClients
        .stream()
        .map(collectorClient -> collectorClient.collect(teamName))
        .map(cf -> cf.orTimeout(2, SECONDS))
        .map(cf -> cf.thenApply(ClusterGroupDashboard::create))
        .map(cf -> cf.exceptionally(throwable -> logAndIgnore(throwable, teamName)))
        .map(CompletableFuture::join)
        .filter(Objects::nonNull)
        .reduce(ClusterGroupDashboardService::combine)
        .map(ClusterGroupDashboardService::squashApplications);
  }

  private static ClusterGroupDashboard combine(ClusterGroupDashboard t1, ClusterGroupDashboard t2) {
    List<Application> applications = new ArrayList<>();
    List<String> clusterGroupEnvironments = new ArrayList<>();
    Stream.of(t1.getApplications(), t2.getApplications()).forEach(applications::addAll);
    Stream.of(t1.getClusterGroupEnvironments(), t2.getClusterGroupEnvironments())
        .forEach(clusterGroupEnvironments::addAll);
    return new ClusterGroupDashboard(applications, clusterGroupEnvironments);
  }

  private ClusterGroupDashboard logAndIgnore(Throwable throwable, String teamName) {
    logger.warn(
        "Failed to get team " + teamName + " for cluster group " + clusterGroup + ": " + throwable);
    return null;
  }

  private static ClusterGroupDashboard squashApplications(
      ClusterGroupDashboard clusterGroupDashboard) {
    List<Application> squashedApplications =
        clusterGroupDashboard
            .getApplications()
            .stream()
            .collect(
                Collectors.toMap(
                    Application::getName,
                    Application::getEnvironments,
                    ClusterGroupDashboardService::squashEnvironments))
            .entrySet()
            .stream()
            .map(ClusterGroupDashboardService::toApplication)
            .sorted(Compare::byName)
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
