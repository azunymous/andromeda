package net.igiari.andromeda.aggregator.clients;

import com.google.gson.Gson;
import net.igiari.andromeda.aggregator.config.AggregatorConfig;
import net.igiari.andromeda.aggregator.config.ClusterConfig;
import net.igiari.andromeda.aggregator.services.ClusterGroupDashboardService;
import net.igiari.andromeda.aggregator.services.TeamDashboardService;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class TeamDashboardServiceClient {
  private final TeamDashboardService teamDashboardService;
  private final HttpClient httpClient;
  private final Gson gson;

  public TeamDashboardServiceClient(AggregatorConfig aggregatorConfig) {
    this.httpClient = HttpClient.newHttpClient();
    this.gson = new Gson();
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
                    Map.Entry::getKey, e -> this.createClusterGroupDashBoardService(e.getValue())));
    return new TeamDashboardService(clusterGroupDashboardServices);
  }

  private ClusterGroupDashboardService createClusterGroupDashBoardService(
      ClusterConfig clusterConfig) {
    return new ClusterGroupDashboardService(createCollectorClients(clusterConfig));
  }

  private List<CollectorClient> createCollectorClients(ClusterConfig clusterConfig) {
    return clusterConfig.getCollectors().stream()
        .map(URI::create)
        .map(collector -> new CollectorClient(httpClient, collector, gson))
        .collect(toList());
  }
}
