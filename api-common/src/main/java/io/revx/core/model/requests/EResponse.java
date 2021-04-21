package io.revx.core.model.requests;

import java.util.List;

public class EResponse<T> {

  private long totalNoOfRecords;
  private List<T> data;

  public long getTotalNoOfRecords() {
    return totalNoOfRecords;
  }

  public void setTotalNoOfRecords(long totalNoOfRecords) {
    this.totalNoOfRecords = totalNoOfRecords;
  }

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("EResponse [totalNoOfRecords=").append(totalNoOfRecords).append(", data=")
        .append(data).append("]");
    return builder.toString();
  }


}
