package net.igiari.andromeda.aggregator.clients;

import com.google.gson.Gson;
import net.igiari.andromeda.aggregator.clients.prometheus.PrometheusResponse;
import net.igiari.andromeda.collector.cluster.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class PrometheusClient {
  private Logger logger = LoggerFactory.getLogger(PrometheusClient.class);

  private static final String DOWNSTREAM_DEPENDENCY_METRIC = "downstream_dependency";
  private final URI uri;
  private final HttpClient httpClient;
  private final Gson gson;

  public PrometheusClient(URI uri, HttpClient httpClient, Gson gson) {
    this.uri = uri;
    this.httpClient = httpClient;
    this.gson = gson;
  }

  private URI createDependencyURI(String podName, String namespaceName) {
    return UriComponentsBuilder.fromUri(uri)
        .path("query")
        .queryParam(
            "query",
            DOWNSTREAM_DEPENDENCY_METRIC
                + "{instance=\""
                + podName
                + "\", namespace=\""
                + namespaceName
                + "\"}[1m]")
        .build()
        .toUri();
  }

  public List<Dependency> getDependencies(String podName, String namespaceName) {
    HttpRequest httpRequest =
        HttpRequest.newBuilder().uri(createDependencyURI(podName, namespaceName)).build();
    return httpClient
        .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
        .orTimeout(2, SECONDS)
        .thenApply(HttpResponse::body)
        .thenApply(this::toDependency)
        .exceptionally(this::logAndIgnore)
        .join();
  }

  private List<Dependency> logAndIgnore(Throwable throwable) {
    logger.info("Error connecting to prometheus " + throwable);
    return emptyList();
  }

  private List<Dependency> toDependency(String s) {
    final PrometheusResponse prometheusResponse = gson.fromJson(s, PrometheusResponse.class);
    if (!prometheusResponse.isSuccessful()) {
      return emptyList();
    }

    return dependenciesFrom(prometheusResponse);
  }

  private List<Dependency> dependenciesFrom(PrometheusResponse prometheusResponse) {
    return prometheusResponse.getData().getResult().stream()
        .map(
            result ->
                new Dependency(result.getMetric().getDependencyName(), isUp(result.getValues())))
        .sorted(Dependency::byName)
        .collect(toList());
  }

  private boolean isUp(List<List<Double>> values) {
    return values.get(values.size() - 1).get(1) == 1;
  }
}
