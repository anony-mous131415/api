package io.revx.core.model.requests;

import java.util.List;
import io.revx.core.model.SlicexGridData;

public class SlicexListResponse {

  protected int totalNoOfRecords;
  protected List<SlicexGridData> data;
  
  protected Long startTimestamp;
  protected Long endTimestamp;

  protected Long compareStartTimestamp;
  protected Long compareEndTimestamp;
  
  public int getTotalNoOfRecords() {
    return totalNoOfRecords;
  }

  public void setTotalNoOfRecords(int totalNoOfRecords) {
    this.totalNoOfRecords = totalNoOfRecords;
  }

  public List<SlicexGridData> getData() {
    return data;
  }

  public void setData(List<SlicexGridData> data) {
    this.data = data;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SlicexListResponse [totalNoOfRecords=");
    builder.append(totalNoOfRecords);
    builder.append(", data=");
    builder.append(data);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }
}
