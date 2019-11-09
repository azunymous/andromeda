package net.igiari.andromeda.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClusterConfigTest {

  @Autowired private ClusterConfig clusterConfig;

  @Test
  void allFieldsLoaded() {
    assertThat(clusterConfig.getNamespaceSuffixes()).isNotNull();
  }

  @Test
  void namespaces() {
    assertThat(clusterConfig.getNamespaceSuffixes()).first().isNotNull();
  }
}
