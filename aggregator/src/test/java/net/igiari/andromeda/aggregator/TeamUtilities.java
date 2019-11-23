package net.igiari.andromeda.aggregator;

import net.igiari.andromeda.aggregator.dashboard.ClusterGroupDashboard;
import net.igiari.andromeda.collector.cluster.Application;
import net.igiari.andromeda.collector.cluster.Environment;
import net.igiari.andromeda.collector.cluster.Pod;
import net.igiari.andromeda.collector.cluster.PodController;
import net.igiari.andromeda.collector.cluster.PodControllerType;
import net.igiari.andromeda.collector.cluster.Status;
import net.igiari.andromeda.collector.cluster.Team;

import java.util.List;

public class TeamUtilities {
  public static Team createTeam() {
    Environment environment = createEnvironment("collector-d9b96ffdb-jw26m", Status.READY, "-dev", "andromeda-dev");
    Application collector = new Application("collector", List.of(environment));
    List<Application> applicationList = List.of(collector);
    return new Team("andromeda", applicationList, List.of("-dev"));
  }

  public static Team createTeamInAnotherCluster() {
    Environment environment = createEnvironment("collector-diff-pod", Status.LIVE, "-test", "andromeda-test");
    Application collector = new Application("collector", List.of(environment));
    List<Application> applicationList = List.of(collector);
    return new Team("andromeda", applicationList, List.of("-test"));
  }

  private static Environment createEnvironment(String podName, Status ready, String environmentName, String namespaceName) {
    Pod pod = new Pod(podName, "bb930eea", ready);
    PodController podController =
        new PodController("collector", List.of(pod), PodControllerType.DEPLOYMENT);
    podController.setStatus(ready);
    podController.setVersion("bb930eea");
    return new Environment(environmentName, namespaceName, podController);
  }

  public static ClusterGroupDashboard createTeamClusterWithBoth() {
    Environment devEnv = createEnvironment("collector-d9b96ffdb-jw26m", Status.READY, "-dev", "andromeda-dev");
    Environment testEnv = createEnvironment("collector-diff-pod", Status.LIVE, "-test", "andromeda-test");

    Application collector = new Application("collector", List.of(devEnv, testEnv));
    List<Application> applicationList = List.of(collector);
    return new ClusterGroupDashboard("clusterGroup", applicationList, List.of("-dev", "-test"));
  }
}
