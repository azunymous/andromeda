package net.igiari.andromeda.collector.config;

import static java.util.Collections.emptyMap;

import java.util.Map;
import java.util.Optional;

public class CanaryConfiguration {
  private boolean enabled;
  private Map<String, String> selector;
  private String appendSuffix;

  public CanaryConfiguration(boolean enabled, Map<String, String> selector) {
    this.enabled = enabled;
    this.selector = selector;
  }

  public static CanaryConfiguration defaultCanaryConfiguration() {
    return new CanaryConfiguration(false, emptyMap());
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Map<String, String> getSelector() {
    return selector;
  }

  public void setSelector(Map<String, String> selector) {
    this.selector = selector;
  }

  public Optional<String> getAppendSuffix() {
    return Optional.ofNullable(appendSuffix);
  }

  public void setAppendSuffix(String appendSuffix) {
    this.appendSuffix = appendSuffix;
  }
}
