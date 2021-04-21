package io.revx.core.model.requests;

import java.util.List;

public class Filters {
  private List<DashboardFilters> filterList;

  public Filters() {
    super();
  }

  public Filters(List<DashboardFilters> filters) {
    super();
    this.filterList = filters;
  }

  public List<DashboardFilters> getFilters() {
    return filterList;
  }

  public void setFilters(List<DashboardFilters> filters) {
    this.filterList = filters;
  }


}
