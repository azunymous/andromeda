package net.igiari.andromeda.aggregator.transformers;

import net.igiari.andromeda.aggregator.clients.PrometheusClient;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.FeatureFlag;
import net.igiari.andromeda.collector.cluster.Pod;

import java.util.List;

public class FeatureFlagTransformer implements ClusterGroupTransformer {
  private PrometheusClient prometheusClient;

  public FeatureFlagTransformer(PrometheusClient prometheusClient) {
    this.prometheusClient = prometheusClient;
  }

  @Override
  public ClusterGroupDashboard transform(ClusterGroupDashboard dashboard) {
    for (Application application : dashboard.getApplications()) {
      for (Environment environment : application.getEnvironments()) {
        for (Pod pod : environment.getPodController().getPods()) {
          pod.setFeatureFlags(getFeatureFlagsFor(pod, environment.getNamespaceName()));
        }
        for (Pod pod : environment.getCanaryPodController().getPods()) {
          pod.setFeatureFlags(getFeatureFlagsFor(pod, environment.getNamespaceName()));
        }
      }
    }
    return dashboard;
  }

  private List<FeatureFlag> getFeatureFlagsFor(Pod pod, String namespaceName) {
    return prometheusClient.getFeatureFlags(pod.getName(), namespaceName);
  }
}
