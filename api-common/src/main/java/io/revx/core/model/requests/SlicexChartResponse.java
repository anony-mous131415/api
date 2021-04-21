package io.revx.core.model.requests;

import java.util.List;
import io.revx.core.model.SlicexData;

public class SlicexChartResponse {

  protected int totalNoOfRecords;
  protected List<SlicexData> data;
  protected List<SlicexData> compareData;

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

  public List<SlicexData> getData() {
    return data;
  }

  public void setData(List<SlicexData> data) {
    this.data = data;
  }

  public List<SlicexData> getCompareData() {
    return compareData;
  }

  public void setCompareData(List<SlicexData> compareData) {
    this.compareData = compareData;
  }

  public Long getStartTimestamp() {
    return startTimestamp;
  }

  public void setStartTimestamp(Long startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public Long getEndTimestamp() {
    return endTimestamp;
  }

  public void setEndTimestamp(Long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  public Long getCompareStartTimestamp() {
    return compareStartTimestamp;
  }

  public void setCompareStartTimestamp(Long compareStartTimestamp) {
    this.compareStartTimestamp = compareStartTimestamp;
  }

  public Long getCompareEndTimestamp() {
    return compareEndTimestamp;
  }

  public void setCompareEndTimestamp(Long compareEndTimestamp) {
    this.compareEndTimestamp = compareEndTimestamp;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SlicexChartResponse [totalNoOfRecords=");
    builder.append(totalNoOfRecords);
    builder.append(", data=");
    builder.append(data);
    builder.append(", compareData=");
    builder.append(compareData);
    builder.append(", startTimestamp=");
    builder.append(startTimestamp);
    builder.append(", endTimestamp=");
    builder.append(endTimestamp);
    builder.append(", compareStartTimestamp=");
    builder.append(compareStartTimestamp);
    builder.append(", compareEndTimestamp=");
    builder.append(compareEndTimestamp);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }
}
