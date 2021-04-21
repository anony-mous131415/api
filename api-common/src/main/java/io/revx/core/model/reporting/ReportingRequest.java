package io.revx.core.model.reporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.revx.core.enums.reporting.CurrencyOf;
import io.revx.core.enums.reporting.DBType;
import io.revx.core.enums.reporting.Interval;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReportingRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_PAGE_NUM = 1;
	public static final String OPTIONS_PAGINATION = "pagination";
	public static final String OPTIONS_INCLUDE_ALL = "include_all";

	@JsonProperty(value = "columns")
	private List<String> columns;

	@JsonProperty(value = "filters")
	private List<FilterModel> filters;

	@JsonProperty(value = "group_by")
	private List<String> group_by;

	@JsonProperty(value = "sort_by")
	private List<SortModel> sort_by;

	@JsonProperty(value = "time_zone")
	private String time_zone = "GMT";

	@JsonProperty(value = "duration")
	private DurationModel duration;

	@JsonProperty(value = "page_size")
	private Integer page_size;

	@JsonProperty(value = "page_number")
	private Integer page_number;

	@JsonProperty(value = "interval")
	private Interval interval;

	@JsonProperty(value = "currency_of")
	private CurrencyOf currency_of;

	@JsonProperty(value = "report_id")
	private String report_id;

	@JsonProperty(value = "report_type")
	private String report_type;

	private String entityName;
	private boolean totalRequired;

	private boolean isPagination = false;
	private boolean isIncludeAll = false;

	private DBType dbType = DBType.REDSHIFT;

	private Map<String, ReportProperty> properties;

	public ReportingRequest() {
		super();
	}

	public void setOptions(String options) {
		if (options != null) {
			String opts = options.toLowerCase();
			if (opts.contains(OPTIONS_PAGINATION)) {
				setPagination(true);
			}
			if (opts.contains(OPTIONS_INCLUDE_ALL)) {
				setIncludeAll(true);
			}
		}
	}

	public void addGroupBy(String groupBy) {
		if (this.group_by == null) {
			this.group_by = new ArrayList<String>();
		}

		this.group_by.add(groupBy);
	}
	
	public void addGroupBy(String groupBy, int index) {
		if (this.group_by == null) {
			this.group_by = new ArrayList<String>();
		}

		this.group_by.add(index, groupBy);
	}

	public void addColumn(String column) {
		if (this.columns == null) {
			this.columns = new ArrayList<String>();
		}

		this.columns.add(column);
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<FilterModel> getFilters() {
		return filters;
	}

	public void setFilters(List<FilterModel> filters) {
		this.filters = filters;
	}

	public List<String> getGroup_by() {
		return group_by;
	}

	public void setGroup_by(List<String> group_by) {
		this.group_by = group_by;
	}

	public List<SortModel> getSort_by() {
		return sort_by;
	}

	public void setSort_by(List<SortModel> sort_by) {
		this.sort_by = sort_by;
	}

	public String getTime_zone() {
		return time_zone;
	}

	public void setTime_zone(String time_zone) {
		this.time_zone = time_zone;
	}

	public DurationModel getDuration() {
		return duration;
	}

	public void setDuration(DurationModel duration) {
		this.duration = duration;
	}

	public Integer getPage_size() {
		return page_size;
	}

	public void setPage_size(Integer page_size) {
		this.page_size = page_size;
	}

	public Integer getPage_number() {
		return page_number;
	}

	public void setPage_number(Integer page_number) {
		this.page_number = page_number;
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	public CurrencyOf getCurrency_of() {
		return currency_of;
	}

	public void setCurrency_of(CurrencyOf currency_of) {
		this.currency_of = currency_of;
	}

	public String getReport_id() {
		return report_id;
	}

	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}

	public String getReport_type() {
		return report_type;
	}

	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public boolean isTotalRequired() {
		return totalRequired;
	}

	public void setTotalRequired(boolean totalRequired) {
		this.totalRequired = totalRequired;
	}

	public boolean isPagination() {
		return isPagination;
	}

	public void setPagination(boolean isPagination) {
		this.isPagination = isPagination;
	}

	public boolean isIncludeAll() {
		return isIncludeAll;
	}

	public void setIncludeAll(boolean isIncludeAll) {
		this.isIncludeAll = isIncludeAll;
	}

	public DBType getDbType() {
		return dbType;
	}

	public void setDbType(DBType dbType) {
		this.dbType = dbType;
	}

	public Map<String, ReportProperty> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, ReportProperty> properties) {
		this.properties = properties;
	}
	
	public void setDefaultCurrencyOf() {
		if(this.currency_of == null) {
			this.currency_of = CurrencyOf.licensee;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((currency_of == null) ? 0 : currency_of.hashCode());
		result = prime * result + ((duration == null) ? 0 : duration.hashCode());
		result = prime * result + ((entityName == null) ? 0 : entityName.hashCode());
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + ((group_by == null) ? 0 : group_by.hashCode());
		result = prime * result + ((interval == null) ? 0 : interval.hashCode());
		return Math.abs(result);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportingRequest other = (ReportingRequest) obj;
		if (columns == null) {
			if (other.columns != null)
				return false;
		} else if (!columns.equals(other.columns))
			return false;
		if (currency_of != other.currency_of)
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
		if (group_by == null) {
			if (other.group_by != null)
				return false;
		} else if (!group_by.equals(other.group_by))
			return false;
		if (interval != other.interval)
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "ReportingRequest [columns=" + columns + ", filters=" + filters + ", group_by=" + group_by + ", sort_by="
				+ sort_by + ", time_zone=" + time_zone + ", duration=" + duration + ", page_size=" + page_size
				+ ", page_number=" + page_number + ", interval=" + interval + ", currency_of=" + currency_of
				+ ", report_id=" + report_id + ", report_type=" + report_type + ", entityName=" + entityName
				+ ", totalRequired=" + totalRequired + ", isPagination=" + isPagination + ", isIncludeAll="
				+ isIncludeAll + ", dbType=" + dbType + ", properties=" + properties + "]";
	}

}