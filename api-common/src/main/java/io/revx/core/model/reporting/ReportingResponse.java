package io.revx.core.model.reporting;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.revx.core.enums.reporting.CurrencyOf;
import io.revx.core.enums.reporting.Interval;

public class ReportingResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty(value = "columns")
	private List<String> columns;

	@JsonProperty(value = "filters")
	private List<FilterModel> filters;

	@JsonProperty(value = "group_by")
	private List<String> group_by;

	@JsonProperty(value = "sort_by")
	private List<SortModel> sortBy;

	@JsonProperty(value = "time_zone")
	private String timeZone = "GMT";

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

	@JsonProperty(value = "entity_name")
	private String entity_name;

	@JsonProperty(value = "results")
	private List<Object> results;

	@JsonProperty(value = "total_results_count")
	private Long total_results_count;

	@JsonProperty(value = "fileName")
	private String fileName;

	@JsonProperty(value = "fileDownloadUrl")
	private String fileDownloadUrl;

	public ReportingResponse() {
		super();
	}

	public ReportingResponse(ReportingRequest reportingRequest) {
		setRequestParams(reportingRequest);
	}

	public void setRequestParams(ReportingRequest reportingRequest) {
		if (reportingRequest == null)
			return;
		if (reportingRequest.getColumns() != null)
			this.columns = reportingRequest.getColumns();
		if (reportingRequest.getFilters() != null)
			this.filters = reportingRequest.getFilters();
		if (reportingRequest.getGroup_by() != null)
			this.group_by = reportingRequest.getGroup_by();
		if (reportingRequest.getSort_by() != null)
			this.sortBy = reportingRequest.getSort_by();
		if (reportingRequest.getTime_zone() != null)
			this.timeZone = reportingRequest.getTime_zone();
		if (reportingRequest.getPage_size() != null)
			this.page_size = reportingRequest.getPage_size();
		if (reportingRequest.getPage_number() != null)
			this.page_number = reportingRequest.getPage_number();
		if (reportingRequest.getDuration() != null)
			this.duration = reportingRequest.getDuration();
		if (reportingRequest.getInterval() != null)
			this.interval = reportingRequest.getInterval();
		if (reportingRequest.getCurrency_of() != null)
			this.currency_of = reportingRequest.getCurrency_of();
		if (reportingRequest.getReport_id() != null)
			this.report_id = reportingRequest.getReport_id();
		if (reportingRequest.getReport_type() != null)
			this.report_type = reportingRequest.getReport_type();
		if (reportingRequest.getEntityName() != null)
			this.entity_name = reportingRequest.getEntityName();
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

	public List<SortModel> getSortBy() {
		return sortBy;
	}

	public void setSortBy(List<SortModel> sortBy) {
		this.sortBy = sortBy;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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

	public String getEntity_name() {
		return entity_name;
	}

	public void setEntity_name(String entity_name) {
		this.entity_name = entity_name;
	}

	public List<Object> getResults() {
		return results;
	}

	public void setResults(List<Object> results) {
		this.results = results;
	}

	public Long getTotal_results_count() {
		return total_results_count;
	}

	public void setTotal_results_count(Long total_results_count) {
		this.total_results_count = total_results_count;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDownloadUrl() {
		return fileDownloadUrl;
	}

	public void setFileDownloadUrl(String fileDownloadUrl) {
		this.fileDownloadUrl = fileDownloadUrl;
	}

	@Override
	public String toString() {
		return "ReportingResponse [columns=" + columns + ", filters=" + filters + ", group_by=" + group_by + ", sortBy="
				+ sortBy + ", timeZone=" + timeZone + ", duration=" + duration + ", page_size=" + page_size
				+ ", page_number=" + page_number + ", interval=" + interval + ", currency_of=" + currency_of
				+ ", report_id=" + report_id + ", report_type=" + report_type + ", entity_name=" + entity_name
				+ ", results=" + results + ", total_results_count=" + total_results_count + ", fileName=" + fileName
				+ ", fileDownloadUrl=" + fileDownloadUrl + "]";
	}

}