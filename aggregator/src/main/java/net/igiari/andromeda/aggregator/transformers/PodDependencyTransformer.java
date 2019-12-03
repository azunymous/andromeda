package net.igiari.andromeda.aggregator.transformers;

import java.util.List;
import net.igiari.andromeda.aggregator.clients.PrometheusClient;
import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Dependency;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.Pod;

public class PodDependencyTransformer implements ClusterGroupTransformer {

  private PrometheusClient prometheus;

  public PodDependencyTransformer(PrometheusClient prometheus) {
    this.prometheus = prometheus;
  }

  @Override
  public ClusterGroupDashboard transform(ClusterGroupDashboard dashboard) {
    for (Application application : dashboard.getApplications()) {
      for (Environment environment : application.getEnvironments()) {
        for (Pod pod : environment.getPodController().getPods()) {
          pod.setDependencies(getDependenciesFor(pod, environment.getNamespaceName()));
        }
        for (Pod pod : environment.getCanaryPodController().getPods()) {
          pod.setDependencies(getDependenciesFor(pod, environment.getNamespaceName()));
        }
      }
    }
    return dashboard;
  }

  private List<Dependency> getDependenciesFor(Pod pod, String namespaceName) {
    return prometheus.getDependencies(pod.getName(), namespaceName);
  }
}
