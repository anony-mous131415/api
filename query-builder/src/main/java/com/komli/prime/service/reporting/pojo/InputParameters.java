package com.komli.prime.service.reporting.pojo;

import java.util.ArrayList;
import java.util.List;

import com.komli.prime.service.reporting.querybuilder.pojo.FilterParameter;
import com.komli.prime.service.reporting.querybuilder.pojo.OrderByParameter;
import com.komli.prime.service.reporting.querybuilder.pojo.SelectParameter;

public class InputParameters {
	
	public enum ReportType {
		RTB(true, true, true), 
		MANAGED(true, true, true), 
		RTBnMANAGED(true, true, true),
		AUDIENCE(false, true, true), 
		BIDFUNNEL(false, false, false),
		UNQUSERS(false, false, false), 
		UFRECORDCOUNT(false, false, false),
		PARAM_CONV_REPORT(false, false, false);

		private ReportType(boolean hasHourlyTable, boolean hasDailyTable,
				boolean hasMonthlyTable) {
			this.hasHourlyTable = hasHourlyTable;
			this.hasDailyTable = hasDailyTable;
			this.hasMonthlyTable = hasMonthlyTable;
		}

		private boolean hasHourlyTable;
		private boolean hasDailyTable;
		private boolean hasMonthlyTable;

		public boolean hasHourlyTable() {
			return hasHourlyTable;
		}

		public boolean hasDailyTable() {
			return hasDailyTable;
		}

		public boolean hasMonthlyTable() {
			return hasMonthlyTable;
		}
	}
	
	public enum Interval{
		HOURLY, DAILY, WEEKLY, MONTHLY,SUMMARY,METADATA;
	}
	
	public enum SLOutput{
		RECORDS, CSV, ADHOC;
	}
	
	private List<SelectParameter> selectList = new ArrayList<SelectParameter>();
	private List<FilterParameter> filters = new ArrayList<FilterParameter>();
	private List<String> groupList = new ArrayList<String>();
	private List<OrderByParameter> orderByList = new ArrayList<OrderByParameter>();
	
	private String timezone = "GMT";	
	private long  starttime;
	private long  endtime;	
	private Interval interval; //hourly/daily/monthly/summary
	private boolean groupby = true;
	
	private ReportType reportType;
	
	private int limitrows = 0; // 0: no limit
	//pagination params
	private int offset= -1;
	private boolean toBeCached = false;
	
	private String reportId;
	
	private SLOutput output; 
	
	private String adhocQuery;
	
	public InputParameters(List<SelectParameter> selectList,List<FilterParameter> filters,
			List<String> groupList,List<OrderByParameter> orderByList, long starttime,
			long endtime, Interval interval, ReportType reportType, int limitrows, int offset, boolean toBeCached, String reportId, SLOutput output) {
		this.selectList = selectList;
		this.filters = filters;
		this.orderByList = orderByList;
		this.groupList = groupList;
		this.starttime = starttime;
		this.endtime = endtime;
		this.interval = interval;
		this.reportType = reportType;
		this.limitrows = limitrows;
		this.offset = offset;
		this.toBeCached = toBeCached;
		this.reportId = reportId;
		this.setOutput(output);
	}
	
	public InputParameters(String adhocQuery){
		output = SLOutput.ADHOC;
		this.adhocQuery = adhocQuery;
	}
	
	public InputParameters(List<SelectParameter> selectList,List<FilterParameter> filters,
			List<String> groupList,List<OrderByParameter> orderByList, long starttime,
			long endtime, Interval interval, ReportType reportType, int limitrows, int offset, boolean toBeCached, String reportId) {
		this(selectList, filters, groupList, orderByList, starttime, endtime, interval, reportType, limitrows, offset, toBeCached, reportId, SLOutput.RECORDS);
	}
	
	public InputParameters(List<SelectParameter> selectList,List<FilterParameter> filters,
			List<String> groupList,List<OrderByParameter> orderByList, long starttime,
			long endtime, Interval interval, ReportType reportType, int limitrows) {
		this(selectList, filters, groupList, orderByList, starttime, endtime, interval, reportType, limitrows, -1, false, null);
	}
	public InputParameters(List<SelectParameter> selectList,List<FilterParameter> filters,
			List<String> groupList,List<OrderByParameter> orderByList, long starttime,
			long endtime, Interval interval, ReportType reportType) {
		this(selectList, filters, groupList, orderByList, starttime, endtime, interval, reportType, -1, -1, false, null);
	}
	
	public List<SelectParameter> getSelectList() {
		return selectList;
	}
	public List<FilterParameter> getFilters() {
		return filters;
	}
	public List<String> getGroupList() {
		return groupList;
	}
	public String getTimezone() {
		return timezone;
	}
	public long getStarttime() {
		return starttime;
	}
	public long getEndtime() {
		return endtime;
	}
	public Interval getInterval() {
		return interval;
	}
	
	public boolean isGroupby() {
		return groupby;
	}

	public void setGroupby(boolean groupby) {
		this.groupby = groupby;
	}

	public ReportType getReportType() {
		return reportType;
	}
	public List<OrderByParameter> getOrderByList() {
		return orderByList;
	}
	public int getLimitrows() {
		return limitrows;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	
	public boolean isToBeCached() {
		return toBeCached;
	}

	public SLOutput getOutput() {
		return output;
	}

	public void setOutput(SLOutput output) {
		this.output = output;
	}

	public String getAdhocQuery() {
		return adhocQuery;
	}

	public void setAdhocQuery(String adhocQuery) {
		this.adhocQuery = adhocQuery;
	}
	
}
