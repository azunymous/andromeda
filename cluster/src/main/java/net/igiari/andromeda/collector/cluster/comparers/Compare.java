package net.igiari.andromeda.collector.cluster.comparers;

public class Compare {
  public static int byName(Nameable n1, Nameable n2) {
    return n1.getName().compareTo(n2.getName());
  }
}
