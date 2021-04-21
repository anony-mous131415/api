package io.revx.core.model.requests;

import java.util.List;
import io.revx.core.model.DashboardData;

public class DashboardResponse {

  private int totalNoOfRecords;
  private List<DashboardData> data;
  private boolean showUU;

  public int getTotalNoOfRecords() {
    return totalNoOfRecords;
  }

  public void setTotalNoOfRecords(int totalNoOfRecords) {
    this.totalNoOfRecords = totalNoOfRecords;
  }

  public List<DashboardData> getData() {
    return data;
  }

  public void setData(List<DashboardData> data) {
    this.data = data;
  }

  public boolean isShowUU() {
    return showUU;
  }

  public void setShowUU(boolean showUU) {
    this.showUU = showUU;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DashboardResponse [totalNoOfRecords=");
    builder.append(totalNoOfRecords);
    builder.append(", data=");
    builder.append(data);
    builder.append(", showUU=");
    builder.append(showUU).append("]");
    return builder.toString();
  }

}
