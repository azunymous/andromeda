package net.igiari.andromeda.aggregator.clients;

import static net.igiari.andromeda.aggregator.TeamUtilities.createTeam;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.google.gson.Gson;
import com.pgssoft.httpclient.HttpClientMock;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import net.igiari.andromeda.collector.cluster.Team;
import org.junit.jupiter.api.Test;

class CollectorClientTest {

  @Test
  void collect() {
    String jsonFromCollector =
        "{\"teamName\":\"andromeda\",\"applications\":[{\"name\":\"collector\",\"environments\":[{\"environmentName\":\"-dev\",\"namespaceName\":\"andromeda-dev\",\"canaryPodController\":{\"name\":\"\", \"pods\"=[], \"type\"=\"DEPLOYMENT\"},\"podController\":{\"name\":\"collector\",\"pods\":[{\"name\":\"collector-d9b96ffdb-jw26m\",\"version\":\"bb930eea\",\"status\":\"READY\"}],\"type\":\"DEPLOYMENT\",\"version\":\"bb930eea\",\"status\":\"READY\"}}]}], \"clusterEnvironments\":[\"-dev\"]}";
    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock.onGet("http://collector.local/team/andromeda").doReturnJSON(jsonFromCollector);
    CollectorClient collectorClient =
        new CollectorClient(httpClientMock, URI.create("http://collector.local"), new Gson());
    CompletableFuture<Team> andromedaCollectorResponse = collectorClient.collect("andromeda");

    Team expectedTeam = createTeam();

    assertThat(andromedaCollectorResponse).isCompleted();
    assertThat(andromedaCollectorResponse).isCompletedWithValue(expectedTeam);
  }
}
