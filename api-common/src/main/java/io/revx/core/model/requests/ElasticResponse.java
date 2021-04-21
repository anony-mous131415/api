package io.revx.core.model.requests;

import java.util.List;
import io.revx.core.model.StatusBaseObject;

public class ElasticResponse {

  private long totalNoOfRecords;
  private List<StatusBaseObject> data;

  public long getTotalNoOfRecords() {
    return totalNoOfRecords;
  }

  public void setTotalNoOfRecords(long totalNoOfRecords) {
    this.totalNoOfRecords = totalNoOfRecords;
  }

  public List<StatusBaseObject> getData() {
    return data;
  }

  public void setData(List<StatusBaseObject> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DictionaryResponse [totalNoOfRecords=");
    builder.append(totalNoOfRecords);
    builder.append(", data=");
    builder.append(data);
    builder.append("]");
    return builder.toString();
  }

}
