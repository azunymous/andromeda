package net.igiari.andromeda.collector.config;

import java.util.List;

import static java.util.Collections.emptyList;

public class PriorityConfig {
  private List<String> first;
  private List<String> last;

  public List<String> getFirst() {
    if (first == null) {
      return emptyList();
    }
    return first;
  }

  public void setFirst(List<String> first) {
    this.first = first;
  }

  public List<String> getLast() {
    if (last == null) {
      return emptyList();
    }
    return last;
  }

  public void setLast(List<String> last) {
    this.last = last;
  }
}
