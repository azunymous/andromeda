package net.igiari.andromeda.aggregator.clients.prometheus;

import com.google.gson.annotations.SerializedName;

public class PrometheusResponse {

  private static final String PROMETHEUS_SUCCESS = "success";

  @SerializedName("data")
  private Data data;

  @SerializedName("status")
  private String status;

  public void setData(Data data) {
    this.data = data;
  }

  public Data getData() {
    return data;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "PrometheusResponse{" + "data = '" + data + '\'' + ",status = '" + status + '\'' + "}";
  }

  public boolean isSuccessful() {
    return status.equals(PROMETHEUS_SUCCESS);
  }
}
