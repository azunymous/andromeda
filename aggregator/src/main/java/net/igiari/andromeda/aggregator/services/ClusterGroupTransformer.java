package net.igiari.andromeda.aggregator.services;

import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;

@FunctionalInterface
public interface ClusterGroupTransformer {
  public abstract ClusterGroupDashboard transform(ClusterGroupDashboard dashboard);
}
