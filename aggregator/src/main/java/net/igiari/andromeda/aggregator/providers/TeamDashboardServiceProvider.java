package net.igiari.andromeda.aggregator.providers;

import com.google.gson.Gson;
import net.igiari.andromeda.aggregator.clients.CollectorClient;
import net.igiari.andromeda.aggregator.clients.PrometheusClient;
import net.igiari.andromeda.aggregator.config.AggregatorConfig;
import net.igiari.andromeda.aggregator.config.ClusterConfig;
import net.igiari.andromeda.aggregator.services.ClusterGroupDashboardService;
import net.igiari.andromeda.aggregator.services.TeamDashboardService;
import net.igiari.andromeda.aggregator.transformers.ClusterGroupTransformer;
import net.igiari.andromeda.aggregator.transformers.FeatureFlagTransformer;
import net.igiari.andromeda.aggregator.transformers.PodDependencyTransformer;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class TeamDashboardServiceProvider {
  private final HttpClient httpClient;
  private final Gson gson;
  private final PrometheusClient prometheusClient;
  private final TeamDashboardService teamDashboardService;

  public TeamDashboardServiceProvider(AggregatorConfig aggregatorConfig) {
    this.httpClient = HttpClient.newHttpClient();
    this.gson = new Gson();
    this.prometheusClient =
        new PrometheusClient(URI.create(aggregatorConfig.getPrometheusURI()), httpClient, gson);
    this.teamDashboardService = createFrom(aggregatorConfig);
  }

  public TeamDashboardService getTeamDashboardService() {
    return teamDashboardService;
  }

  public HttpClient getHttpClient() {
    return httpClient;
  }

  public Gson getGson() {
    return gson;
  }

  private TeamDashboardService createFrom(AggregatorConfig aggregatorConfig) {
    final Map<String, ClusterGroupDashboardService> clusterGroupDashboardServices =
        aggregatorConfig.getClusters().entrySet().stream()
            .collect(
                toMap(
                    Map.Entry::getKey, e -> this.createClusterGroupDashBoardService(e.getKey(), e.getValue())));

    final List<ClusterGroupTransformer> transformers =
        List.of(podDependencyTransformer(), featureFlagTransformer());
    return new TeamDashboardService(clusterGroupDashboardServices, transformers);
  }

  private ClusterGroupDashboardService createClusterGroupDashBoardService(
      String clusterGroup, ClusterConfig clusterConfig) {
    return new ClusterGroupDashboardService(clusterGroup, createCollectorClients(clusterConfig));
  }

  private List<CollectorClient> createCollectorClients(ClusterConfig clusterConfig) {
    return clusterConfig.getCollectors().stream()
        .map(URI::create)
        .map(collector -> new CollectorClient(httpClient, collector, gson))
        .collect(toList());
  }

  private PodDependencyTransformer podDependencyTransformer() {
    return new PodDependencyTransformer(prometheusClient);
  }

  private ClusterGroupTransformer featureFlagTransformer() {
    return new FeatureFlagTransformer(prometheusClient);
  }
}
