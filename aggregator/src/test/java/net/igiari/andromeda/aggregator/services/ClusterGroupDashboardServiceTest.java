package net.igiari.andromeda.aggregator.services;

import static net.igiari.andromeda.aggregator.TeamUtilities.createTeamClusterWithBoth;
import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.igiari.andromeda.aggregator.TeamUtilities;
import net.igiari.andromeda.aggregator.clients.CollectorClient;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Team;
import org.junit.jupiter.api.Test;

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

  @Test
  void failingCollectorIsIgnored() {
    List<CollectorClient> aWorkingClientAndAFailingClient =
        List.of(
            new StubbedCollectorClient(CLUSTER1),
            new StubbedCollectorClient(CLUSTER2).whichIsFailing());

    ClusterGroupDashboardService clusterGroupDashboardService =
        new ClusterGroupDashboardService("clusterGroup", aWorkingClientAndAFailingClient);
    final Optional<ClusterGroupDashboard> teamData =
        clusterGroupDashboardService.createClusterGroupDashboard("andromeda");
    assertThat(teamData).isNotEmpty();
    assertThat(teamData.get().getApplications())
        .isEqualTo(TeamUtilities.createTeam().getApplications());
  }

  @Test
  void multipleFailingCollectorsReturnEmptyOptional() {
    List<CollectorClient> failingClients =
        List.of(
            new StubbedCollectorClient(CLUSTER1).whichIsFailing(),
            new StubbedCollectorClient(CLUSTER2).whichIsFailing());

    ClusterGroupDashboardService clusterGroupDashboardService =
        new ClusterGroupDashboardService("clusterGroup", failingClients);
    final Optional<ClusterGroupDashboard> teamData =
        clusterGroupDashboardService.createClusterGroupDashboard("andromeda");
    assertThat(teamData).isEmpty();
  }

  private static class StubbedCollectorClient extends CollectorClient {
    private boolean differentCluster;
    private boolean failing;

    StubbedCollectorClient(boolean differentCluster) {
      super(null, URI.create("collector.local"), null);
      this.differentCluster = differentCluster;
    }

    StubbedCollectorClient whichIsFailing() {
      failing = true;
      return this;
    }

    @Override
    public CompletableFuture<Team> collect(String team) {
      if (failing) {
        return CompletableFuture.supplyAsync(
            () -> {
              try {
                Thread.sleep(3000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return null;
            });
      }
      if (differentCluster) {
        return CompletableFuture.supplyAsync(TeamUtilities::createTeamInAnotherCluster);
      }
      return CompletableFuture.supplyAsync(TeamUtilities::createTeam);
    }
  }
}
