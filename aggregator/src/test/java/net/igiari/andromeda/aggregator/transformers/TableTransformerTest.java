package net.igiari.andromeda.aggregator.transformers;

import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.PodController;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TableTransformerTest {

  @Test
  void transformWithEmptyEnvironmentsAdded() {
    List<String> clusterGroupEnvironments = List.of("local", "dev", "special-dev", "stage", "prod");

    Environment dev = new Environment("dev", "app-dev", PodController.empty());
    Environment stage = new Environment("stage", "app-stage", PodController.empty());
    List<Environment> environments = List.of(dev, stage);
    Application app = new Application("app", environments);
    List<Application> apps = List.of(app);
    ClusterGroupDashboard clusterGroupDashboard =
        new ClusterGroupDashboard(apps, clusterGroupEnvironments);

    TableTransformer tableTransformer = new TableTransformer();

    List<Environment> expectedEnvironments = new ArrayList<>(environments);
    expectedEnvironments.add(0, Environment.empty());
    expectedEnvironments.add(2, Environment.empty());
    expectedEnvironments.add(4, Environment.empty());
    Application expectedApp = new Application("app", expectedEnvironments);
    ClusterGroupDashboard expectedClusterGroupDashboard =
        new ClusterGroupDashboard(List.of(expectedApp), clusterGroupEnvironments);
    assertThat(tableTransformer.transform(clusterGroupDashboard))
        .isEqualTo(expectedClusterGroupDashboard);
  }
}
