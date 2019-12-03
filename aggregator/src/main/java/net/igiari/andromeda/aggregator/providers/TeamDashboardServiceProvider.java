package net.igiari.andromeda.aggregator.providers;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.igiari.andromeda.aggregator.clients.CollectorClient;
import net.igiari.andromeda.aggregator.clients.PrometheusClient;
import net.igiari.andromeda.aggregator.config.AggregatorConfig;
import net.igiari.andromeda.aggregator.config.ClusterConfig;
import net.igiari.andromeda.aggregator.services.ClusterGroupDashboardService;
import net.igiari.andromeda.aggregator.services.TeamDashboardService;
import net.igiari.andromeda.aggregator.transformers.ClusterGroupTransformer;
import net.igiari.andromeda.aggregator.transformers.FeatureFlagTransformer;
import net.igiari.andromeda.aggregator.transformers.PodDependencyTransformer;
import net.igiari.andromeda.aggregator.transformers.TableTransformer;
import org.springframework.stereotype.Component;

@Component
public class TeamDashboardServiceProvider {
  private final HttpClient httpClient;
  private final Gson gson;

  private final TeamDashboardService teamDashboardService;

  public TeamDashboardServiceProvider(AggregatorConfig aggregatorConfig) {
    this.httpClient = HttpClient.newHttpClient();
    this.gson = new Gson();

    List<ClusterGroupTransformer> transformers = new ArrayList<>();
    if (aggregatorConfig.getPrometheusURI() != null) {
      PrometheusClient prometheusClient =
          new PrometheusClient(URI.create(aggregatorConfig.getPrometheusURI()), httpClient, gson);
      ClusterGroupTransformer podDependencyTransformer =
          new PodDependencyTransformer(prometheusClient);
      ClusterGroupTransformer featureFlagTransformer = new FeatureFlagTransformer(prometheusClient);
      transformers.add(podDependencyTransformer);
      transformers.add(featureFlagTransformer);
    }

    ClusterGroupTransformer tableTransformer = new TableTransformer();
    transformers.add(tableTransformer);

    this.teamDashboardService = createFrom(aggregatorConfig, transformers);
  }

  public TeamDashboardService getTeamDashboardService() {
    return teamDashboardService;
  }

  private TeamDashboardService createFrom(
      AggregatorConfig aggregatorConfig, List<ClusterGroupTransformer> transformers) {
    final Map<String, ClusterGroupDashboardService> clusterGroupDashboardServices =
        aggregatorConfig
            .getClusters()
            .entrySet()
            .stream()
            .collect(
                toMap(
                    Map.Entry::getKey,
                    e -> this.createClusterGroupDashBoardService(e.getKey(), e.getValue())));

    return new TeamDashboardService(clusterGroupDashboardServices, transformers);
  }

  private ClusterGroupDashboardService createClusterGroupDashBoardService(
      String clusterGroup, ClusterConfig clusterConfig) {
    return new ClusterGroupDashboardService(clusterGroup, createCollectorClients(clusterConfig));
  }

  private List<CollectorClient> createCollectorClients(ClusterConfig clusterConfig) {
    return clusterConfig
        .getCollectors()
        .stream()
        .map(URI::create)
        .map(collector -> new CollectorClient(httpClient, collector, gson))
        .collect(toList());
  }
}
