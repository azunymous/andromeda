package net.igiari.andromeda.aggregator.transformers;

import java.util.ArrayList;
import java.util.List;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;

public class TableTransformer implements ClusterGroupTransformer {
  @Override
  public ClusterGroupDashboard transform(ClusterGroupDashboard dashboard) {
    for (Application application : dashboard.getApplications()) {
      List<Environment> environments = new ArrayList<>(application.getEnvironments());
      if (environments.size() != dashboard.getClusterGroupEnvironments().size()) {
        List<String> clusterGroupEnvironments = dashboard.getClusterGroupEnvironments();
        for (int i = 0; i < clusterGroupEnvironments.size(); i++) {
          String clusterGroupEnvironment = clusterGroupEnvironments.get(i);
          if (environments.size() - 1 < i) {
            environments.add(Environment.empty());
          } else if (!environments.get(i).getEnvironmentName().equals(clusterGroupEnvironment)) {
            environments.add(i, Environment.empty());
          }
        }
        application.setEnvironments(environments);
      }
    }
    return dashboard;
  }
}
