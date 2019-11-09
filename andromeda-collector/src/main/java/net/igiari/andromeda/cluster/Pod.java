package net.igiari.andromeda.cluster;

public class Pod {
  private final String name;
  private final String version;
  private final Status status;

  public Pod(String name, String version, Status status) {
    this.name = name;
    this.version = version;
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }

  public Status getStatus() {
    return status;
  }
}
