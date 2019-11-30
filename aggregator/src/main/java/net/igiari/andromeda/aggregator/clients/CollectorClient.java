package net.igiari.andromeda.aggregator.clients;

import com.google.gson.Gson;
import net.igiari.andromeda.collector.cluster.Team;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class CollectorClient {
  private final HttpClient httpClient;
  private final URI uri;
  private final Gson gson;

  public CollectorClient(HttpClient httpClient, URI uri, Gson gson) {
    this.httpClient = httpClient;
    this.uri = uri;
    this.gson = gson;
  }

  public CompletableFuture<Team> collect(String team) {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(createURI(team))
        .build();
    return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(this::TeamFromJSON);
  }

  private Team TeamFromJSON(String input) {
    return gson.fromJson(input, Team.class);
  }

  private URI createURI(String team) {
    return UriComponentsBuilder.fromUri(uri).pathSegment("team").pathSegment(team).build().toUri();
  }
}
