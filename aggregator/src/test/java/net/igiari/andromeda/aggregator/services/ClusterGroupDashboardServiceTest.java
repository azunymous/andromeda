package net.igiari.andromeda.aggregator.services;

import net.igiari.andromeda.aggregator.TeamUtilities;
import net.igiari.andromeda.aggregator.clients.CollectorClient;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Team;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static net.igiari.andromeda.aggregator.TeamUtilities.createTeamClusterWithBoth;
import static org.assertj.core.api.Assertions.assertThat;

class ClusterGroupDashboardServiceTest {

  private static final boolean CLUSTER1 = false;
  private static final boolean CLUSTER2 = true;

  @Test
  void createTeam() {
    List<CollectorClient> stubbedCollectorClients =
        List.of(new StubbedCollectorClient(CLUSTER1), new StubbedCollectorClient(CLUSTER2));
    ClusterGroupDashboardService clusterGroupDashboardService =
        new ClusterGroupDashboardService("clusterGroup", stubbedCollectorClients);

    Optional<ClusterGroupDashboard> teamData =
        clusterGroupDashboardService.createClusterGroupDashboard("andromeda");
    assertThat(teamData).isNotEmpty();
    assertThat(teamData).contains(createTeamClusterWithBoth());
  }

  private static class StubbedCollectorClient extends CollectorClient {
    private boolean differentCluster;

    StubbedCollectorClient(boolean differentCluster) {
      super(null, URI.create("collector.local"), null);
      this.differentCluster = differentCluster;
    }

    @Override
    public CompletableFuture<Team> collect(String team) {
      if (differentCluster) {
        return CompletableFuture.supplyAsync(TeamUtilities::createTeamInAnotherCluster);
      }
      return CompletableFuture.supplyAsync(TeamUtilities::createTeam);
    }
  }
}
