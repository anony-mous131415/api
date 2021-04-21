package io.revx.core.model.requests;

import java.util.ArrayList;
import java.util.List;

public class SearchRequest {
  private List<DashboardFilters> filters;

  public SearchRequest() {
    super();
    filters = new ArrayList<DashboardFilters>();
  }

  public List<DashboardFilters> getFilters() {
    return filters;
  }

  public void setFilters(List<DashboardFilters> tableFilters) {
    this.filters = tableFilters;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SearchRequest [filters=");
    builder.append(filters);
    builder.append("]");
    return builder.toString();
  }



}
