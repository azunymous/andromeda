package net.igiari.andromeda.aggregator.clients;

import com.google.gson.Gson;
import net.igiari.andromeda.aggregator.clients.prometheus.PrometheusResponse;
import net.igiari.andromeda.aggregator.clients.prometheus.ResultItem;
import net.igiari.andromeda.collector.cluster.Dependency;
import net.igiari.andromeda.collector.cluster.FeatureFlag;
import net.igiari.andromeda.collector.cluster.comparers.Compare;
import net.igiari.andromeda.collector.cluster.comparers.Nameable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;

public class PrometheusClient {
  private Logger logger = LoggerFactory.getLogger(PrometheusClient.class);

  private static final String DOWNSTREAM_DEPENDENCY_METRIC = "downstream_dependency";
  private static final String FEATURE_FLAG_METRIC = "feature_flag";
  private final URI uri;
  private final HttpClient httpClient;
  private final Gson gson;

  public PrometheusClient(URI uri, HttpClient httpClient, Gson gson) {
    this.uri = uri;
    this.httpClient = httpClient;
    this.gson = gson;
  }

  private URI createQueryURI(String metricName, String podName, String namespaceName) {
    return UriComponentsBuilder.fromUri(uri)
        .path("query")
        .queryParam(
            "query",
            metricName + "{pod_name=\"" + podName + "\", namespace=\"" + namespaceName + "\"}[1m]")
        .build()
        .toUri();
  }

  private <T> List<T> getApplicationInfo(
      Function<String, List<T>> createApplicationInfo,
      String metricName,
      String podName,
      String namespaceName) {
    HttpRequest httpRequest =
        HttpRequest.newBuilder().uri(createQueryURI(metricName, podName, namespaceName)).build();
    return httpClient
        .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
        .orTimeout(2, SECONDS)
        .thenApply(HttpResponse::body)
        .thenApply(createApplicationInfo)
        .exceptionally(this::logAndIgnore)
        .join();
  }

  public List<Dependency> getDependencies(String podName, String namespaceName) {
    return getApplicationInfo(
        this::toDependency, DOWNSTREAM_DEPENDENCY_METRIC, podName, namespaceName);
  }

  public List<FeatureFlag> getFeatureFlags(String podName, String namespaceName) {
    return getApplicationInfo(this::toFeatureFlag, FEATURE_FLAG_METRIC, podName, namespaceName);
  }

  private <T> List<T> logAndIgnore(Throwable throwable) {
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

  private List<FeatureFlag> toFeatureFlag(String s) {
    final PrometheusResponse prometheusResponse = gson.fromJson(s, PrometheusResponse.class);
    if (!prometheusResponse.isSuccessful()) {
      return emptyList();
    }

    return featureFlagsFrom(prometheusResponse);
  }

  private <T extends Nameable> List<T> applicationInfoFrom(
      PrometheusResponse prometheusResponse, Function<ResultItem, T> createAppInfo) {
    return prometheusResponse.getData().getResult().stream()
        .map(createAppInfo)
        .sorted(Compare::byName)
        .collect(toList());
  }

  private List<FeatureFlag> featureFlagsFrom(PrometheusResponse prometheusResponse) {
    return applicationInfoFrom(
        prometheusResponse,
        result ->
            new FeatureFlag(
                result.getMetric().getDependencyName(), getLastValue(result.getValues())));
  }

  private double getLastValue(List<List<Double>> values) {
    return values.get(values.size() - 1).get(1);
  }

  private List<Dependency> dependenciesFrom(PrometheusResponse prometheusResponse) {
    return applicationInfoFrom(
        prometheusResponse,
        result -> new Dependency(result.getMetric().getDependencyName(), isUp(result.getValues())));
  }

  private boolean isUp(List<List<Double>> values) {
    return getLastValue(values) == 1;
  }
}
