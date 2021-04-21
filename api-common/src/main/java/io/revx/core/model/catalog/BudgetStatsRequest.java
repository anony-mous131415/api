package io.revx.core.model.catalog;

import java.util.List;
import io.revx.core.model.BaseModel;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.Duration;

public class BudgetStatsRequest extends BaseModel {

  private static final long serialVersionUID = 1L;

  private List<String> columns;

  private List<DashboardFilters> filters;

  private Duration duration;

  private String interval;

  private String currency;

  private List<String> groupBy;

  private List<StatsSorter> sortBy;

  private String timeZone;

  private Integer advertiserId;

  public List<String> getColumns() {
    return columns;
  }

  public void setColumns(List<String> columns) {
    this.columns = columns;
  }

  public List<DashboardFilters> getFilters() {
    return filters;
  }

  public void setFilters(List<DashboardFilters> filters) {
    this.filters = filters;
  }

  public Duration getDuration() {
    return duration;
  }

  public void setDuration(Duration duration) {
    this.duration = duration;
  }

  public String getInterval() {
    return interval;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public List<String> getGroupBy() {
    return groupBy;
  }

  public void setGroupBy(List<String> groupBy) {
    this.groupBy = groupBy;
  }

  public List<StatsSorter> getSortBy() {
    return sortBy;
  }

  public void setSortBy(List<StatsSorter> sortBy) {
    this.sortBy = sortBy;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public Integer getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Integer advertiserId) {
    this.advertiserId = advertiserId;
  }

}
