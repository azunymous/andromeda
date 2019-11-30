package net.igiari.andromeda.aggregator.services.health;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class HealthMetricConfig {
  @Bean
  MeterRegistryCustomizer<MeterRegistry> healthRegistryIndividualMetrics(
      HealthContributorRegistry healthRegistry) {
    return registry ->
        healthRegistry.stream()
            .forEach(
                contributor -> {
                  registry.gauge(
                      "downstream_dependency",
                      List.of(Tag.of("dependency_name", contributor.getName())),
                      healthRegistry,
                      health -> {
                        Status status =
                            ((HealthIndicator) contributor.getContributor())
                                .getHealth(false)
                                .getStatus();
                        if ("UP".equals(status.getCode())) {
                          return 1;
                        }
                        return 0;
                      });
                });
  }
};
