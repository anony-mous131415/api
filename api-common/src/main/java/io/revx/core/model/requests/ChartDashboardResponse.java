package io.revx.core.model.requests;

import io.revx.core.model.DashboardMetrics;

public class ChartDashboardResponse extends DashboardResponse {

  private DashboardMetrics widgetData;

  public DashboardMetrics getWidgetData() {
    return widgetData;
  }

  public void setWidgetData(DashboardMetrics widgetData) {
    this.widgetData = widgetData;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ChartDashboardResponse [widgetData=");
    builder.append(widgetData);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }

}
