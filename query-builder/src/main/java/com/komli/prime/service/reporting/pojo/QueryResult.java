package com.komli.prime.service.reporting.pojo;

import java.util.List;
import java.util.Map;

import com.komli.prime.service.reporting.Session.State;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;


public class QueryResult {
	
	public QueryResult(ReportingError err, State state) {
		this.setReportError(err);
		this.state = state;
	}
	public QueryResult(List<Map<String, Object>> records, int rows) {
		totalRows = rows;
		result = records;		
	}
	public QueryResult(List<Map<String, Object>> records) {
		result = records;		
	}
	public QueryResult() {
	}
	public QueryResult(String sessionId) {
		this.reportId= sessionId;
	}
	public static final String QUERY_FAILED = null;

	//data related
	private List<Map<String,Object>> result;
	private String reportId;
	private String tempTable;
	private int totalRows;
	
	//error Message and State
	private ReportingError reportError;
	private State state;
	
	//timetaken
	private long queryBuildTime;
	private long queryExecutionTime;

	//csv file
	public String csvFileName;
	public List<Map<String, Object>> getResult() {
		return result;
	}
	public void setResult(List<Map<String, Object>> result) {
		this.result = result;
	}
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getTempTable() {
		return tempTable;
	}
	public void setTempTable(String tempTable) {
		this.tempTable = tempTable;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public void populateValues(QueryResult tmp) {
		setTotalRows(tmp.getTotalRows());
		setResult(tmp.getResult());
		setTempTable(tmp.getTempTable());
		setReportId(tmp.getReportId());
		setCsvFileName(tmp.getCsvFileName());
	}
	public String toString(){
		return "State:" + state + "|RepId:"+reportId+"Rows:"+totalRows+ "|CSVFile:"+csvFileName+ "|Exception:"+reportError;
	}
	public long getQueryBuildTime() {
		return queryBuildTime;
	}
	public void setQueryBuildTime(long queryBuildTime) {
		this.queryBuildTime = queryBuildTime;
	}
	public long getQueryExecutionTime() {
		return queryExecutionTime;
	}
	public void setQueryExecutionTime(long queryExecutionTime) {
		this.queryExecutionTime = queryExecutionTime;
	}
	public ReportingError getReportError() {
		return reportError;
	}
	public void setReportError(ReportingError reportError) {
		this.reportError = reportError;
	}
	public int getRecordSize(){
		if(result == null){
			return 0;
		}else{
			return result.size();
		}
	}
	public String getCsvFileName() {
		return csvFileName;
	}
	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}
}
