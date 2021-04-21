package io.revx.core.model.catalog;

import java.util.List;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.Duration;

public class BudgetStatsResponse {

  private List<String> columns;

  private List<DashboardFilters> filters;

  private Duration duration;

  private String interval;

  private String currency;

  private List<String> groupBy;

  private List<StatsSorter> sortBy;

  private String timeZone;

  private Integer pageSize;

  private Integer pageNum;

  private String entityName;

  private Boolean totalRequired;

  private String responseId;

  private String reportId;

  private Integer totalNoOfRecords;

  private String urlFilters;

  private List<BaseDataObject> data;

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

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  public Integer getPageNum() {
    return pageNum;
  }

  public void setPageNum(Integer pageNum) {
    this.pageNum = pageNum;
  }

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public Boolean getTotalRequired() {
    return totalRequired;
  }

  public void setTotalRequired(Boolean totalRequired) {
    this.totalRequired = totalRequired;
  }

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public String getReportId() {
    return reportId;
  }

  public void setReportId(String reportId) {
    this.reportId = reportId;
  }

  public Integer getTotalNoOfRecords() {
    return totalNoOfRecords;
  }

  public void setTotalNoOfRecords(Integer totalNoOfRecords) {
    this.totalNoOfRecords = totalNoOfRecords;
  }

  public String getUrlFilters() {
    return urlFilters;
  }

  public void setUrlFilters(String urlFilters) {
    this.urlFilters = urlFilters;
  }

  public List<BaseDataObject> getData() {
    return data;
  }

  public void setData(List<BaseDataObject> data) {
    this.data = data;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((columns == null) ? 0 : columns.hashCode());
    result = prime * result + ((currency == null) ? 0 : currency.hashCode());
    result = prime * result + ((data == null) ? 0 : data.hashCode());
    result = prime * result + ((duration == null) ? 0 : duration.hashCode());
    result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
    result = prime * result + ((filters == null) ? 0 : filters.hashCode());
    result = prime * result + ((groupBy == null) ? 0 : groupBy.hashCode());
    result = prime * result + ((interval == null) ? 0 : interval.hashCode());
    result = prime * result + ((pageNum == null) ? 0 : pageNum.hashCode());
    result = prime * result + ((pageSize == null) ? 0 : pageSize.hashCode());
    result = prime * result + ((reportId == null) ? 0 : reportId.hashCode());
    result = prime * result + ((responseId == null) ? 0 : responseId.hashCode());
    result = prime * result + ((sortBy == null) ? 0 : sortBy.hashCode());
    result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
    result = prime * result + ((totalNoOfRecords == null) ? 0 : totalNoOfRecords.hashCode());
    result = prime * result + ((totalRequired == null) ? 0 : totalRequired.hashCode());
    result = prime * result + ((urlFilters == null) ? 0 : urlFilters.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BudgetStatsResponse other = (BudgetStatsResponse) obj;
    if (columns == null) {
      if (other.columns != null)
        return false;
    } else if (!columns.equals(other.columns))
      return false;
    if (currency == null) {
      if (other.currency != null)
        return false;
    } else if (!currency.equals(other.currency))
      return false;
    if (data == null) {
      if (other.data != null)
        return false;
    } else if (!data.equals(other.data))
      return false;
    if (duration == null) {
      if (other.duration != null)
        return false;
    } else if (!duration.equals(other.duration))
      return false;
    if (entityName == null) {
      if (other.entityName != null)
        return false;
    } else if (!entityName.equals(other.entityName))
      return false;
    if (filters == null) {
      if (other.filters != null)
        return false;
    } else if (!filters.equals(other.filters))
      return false;
    if (groupBy == null) {
      if (other.groupBy != null)
        return false;
    } else if (!groupBy.equals(other.groupBy))
      return false;
    if (interval == null) {
      if (other.interval != null)
        return false;
    } else if (!interval.equals(other.interval))
      return false;
    if (pageNum == null) {
      if (other.pageNum != null)
        return false;
    } else if (!pageNum.equals(other.pageNum))
      return false;
    if (pageSize == null) {
      if (other.pageSize != null)
        return false;
    } else if (!pageSize.equals(other.pageSize))
      return false;
    if (reportId == null) {
      if (other.reportId != null)
        return false;
    } else if (!reportId.equals(other.reportId))
      return false;
    if (responseId == null) {
      if (other.responseId != null)
        return false;
    } else if (!responseId.equals(other.responseId))
      return false;
    if (sortBy == null) {
      if (other.sortBy != null)
        return false;
    } else if (!sortBy.equals(other.sortBy))
      return false;
    if (timeZone == null) {
      if (other.timeZone != null)
        return false;
    } else if (!timeZone.equals(other.timeZone))
      return false;
    if (totalNoOfRecords == null) {
      if (other.totalNoOfRecords != null)
        return false;
    } else if (!totalNoOfRecords.equals(other.totalNoOfRecords))
      return false;
    if (totalRequired == null) {
      if (other.totalRequired != null)
        return false;
    } else if (!totalRequired.equals(other.totalRequired))
      return false;
    if (urlFilters == null) {
      if (other.urlFilters != null)
        return false;
    } else if (!urlFilters.equals(other.urlFilters))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "BudgetStatsResponse [columns=" + columns + ", filters=" + filters + ", duration="
        + duration + ", interval=" + interval + ", currency=" + currency + ", groupBy=" + groupBy
        + ", sortBy=" + sortBy + ", timeZone=" + timeZone + ", pageSize=" + pageSize + ", pageNum="
        + pageNum + ", entityName=" + entityName + ", totalRequired=" + totalRequired
        + ", responseId=" + responseId + ", reportId=" + reportId + ", totalNoOfRecords="
        + totalNoOfRecords + ", urlFilters=" + urlFilters + ", data=" + data + "]";
  }
  
  

}
