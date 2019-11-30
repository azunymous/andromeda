package net.igiari.andromeda.aggregator.services.health;

import net.igiari.andromeda.aggregator.config.AggregatorConfig;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class PrometheusHealthIndicator implements NamedContributor<HealthContributor>, HealthIndicator {

  private HttpClient httpClient;
  private String prometheusURI;

  public PrometheusHealthIndicator(AggregatorConfig aggregatorConfig) {
    this.httpClient = HttpClient.newHttpClient();
    this.prometheusURI = aggregatorConfig.getPrometheusURI();
  }

  public String getName() {
    return "prometheus";
  }

  @Override
  public HealthIndicator getContributor() {
    return this;
  }

  @Override
  public Health getHealth(boolean includeDetails) {
    return health();
  }

  @Override
  public Health health() {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(createHealthcheckURI(prometheusURI))
        .build();
    final Integer status = httpClient
        .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::statusCode)
        .join();
    if (status.equals(200)) {
      return Health.up().build();
    }
    return Health.down().build();
  }

  private URI createHealthcheckURI(String uri) {
    return UriComponentsBuilder.fromUriString(uri).pathSegment("-").pathSegment("ready").build().toUri();
  }
}
