package net.igiari.andromeda.aggregator.clients;

import com.google.gson.Gson;
import com.pgssoft.httpclient.HttpClientMock;
import net.igiari.andromeda.collector.cluster.Dependency;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

class PrometheusClientTest {

  private static final String POD_NAME = "pod-1";
  private static final String NAMESPACE_NAME = "namespaceName";

  @Test
  void getDependencies() {
    String prometheusAPIJSON =
        "{\n"
            + "  \"status\": \"success\",\n"
            + "  \"data\": {\n"
            + "    \"resultType\": \"matrix\",\n"
            + "    \"result\": [\n"
            + "      {\n"
            + "        \"metric\": {\n"
            + "          \"__name__\": \"downstream_dependency\",\n"
            + "          \"pod_name\": \""
            + POD_NAME
            + "\",\n"
            + "          \"dependencyName\": \"prometheus\"\n"
            + "        },\n"
            + "        \"values\": [\n"
            + "          [\n"
            + "            1574365310.27,\n"
            + "            \"0\"\n"
            + "          ],\n"
            + "          [\n"
            + "            1574365320.268,\n"
            + "            \"1\"\n"
            + "          ]\n"
            + "        ]\n"
            + "      },\n"
            + "      {\n"
            + "        \"metric\": {\n"
            + "          \"__name__\": \"downstream_dependency\",\n"
            + "          \"pod_name\": \""
            + POD_NAME
            + "\",\n"
            + "          \"dependencyName\": \"collector\"\n"
            + "        },\n"
            + "        \"values\": [\n"
            + "          [\n"
            + "            1574365310.689,\n"
            + "            \"1\"\n"
            + "          ],\n"
            + "          [\n"
            + "            1574365320.689,\n"
            + "            \"0\"\n"
            + "          ]\n"
            + "        ]\n"
            + "      }\n"
            + "    ]\n"
            + "  }\n"
            + "}";

    HttpClientMock httpClientMock = new HttpClientMock();
    httpClientMock
        .onGet(
            UriComponentsBuilder.fromUriString(
                    "http://prometheus.local/api/v1/query?query=downstream_dependency{pod_name=\""
                        + POD_NAME
                        + "\", namespace=\""
                        + NAMESPACE_NAME
                        + "\"}[1m]")
                .toUriString())
        .doReturnJSON(prometheusAPIJSON);

    PrometheusClient prometheusClient =
        new PrometheusClient(
            URI.create("http://prometheus.local/api/v1/"), httpClientMock, new Gson());

    Dependency expectedDependencyCollector = new Dependency("collector", false);
    Dependency expectedDependencyPrometheus = new Dependency("prometheus", true);

    assertThat(prometheusClient.getDependencies(POD_NAME, NAMESPACE_NAME))
        .containsExactly(expectedDependencyCollector, expectedDependencyPrometheus);
  }
}
