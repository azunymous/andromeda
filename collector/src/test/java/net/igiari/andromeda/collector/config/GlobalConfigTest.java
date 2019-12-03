package net.igiari.andromeda.collector.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GlobalConfigTest {

  @Autowired private GlobalConfig globalConfig;

  @Test
  void allFieldsLoaded() {
    assertThat(globalConfig.getTeams()).isNotNull();
    assertThat(globalConfig.getTeams()).first().isNotNull();
  }

  @Test
  void teams() {
    assertThat(globalConfig.getTeams()).isNotEmpty();
  }
}
