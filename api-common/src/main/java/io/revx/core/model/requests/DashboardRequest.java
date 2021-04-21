package io.revx.core.model.requests;

import java.util.ArrayList;
import java.util.List;

public class DashboardRequest {
  public List<DashboardFilters> filters;

  public Duration duration;

  public String groupBy;

  public DashboardRequest() {
    super();
    duration = new Duration();
    filters = new ArrayList<DashboardFilters>();
  }

  public List<DashboardFilters> getFilters() {
    return filters;
  }

  public void setFilters(List<DashboardFilters> tableFilters) {
    this.filters = tableFilters;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }

  public String getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(String groupBy) {
    this.groupBy = groupBy;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DashboardRequest [tableFilters=");
    builder.append(filters);
    builder.append(", duration=");
    builder.append(duration);
    builder.append(", groupBy=");
    builder.append(groupBy);
    builder.append("]");
    return builder.toString();
  }

}
