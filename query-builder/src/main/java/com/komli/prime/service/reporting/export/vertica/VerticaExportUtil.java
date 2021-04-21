package com.komli.prime.service.reporting.export.vertica;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.QueryExecutionException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.querybuilder.impl.vertica.VerticaQueryBuilder;
import com.komli.prime.service.reporting.utils.ReportingUtil;


public class VerticaExportUtil  {
	
	private static final Logger logger = LoggerFactory.getLogger(VerticaExportUtil.class);

	private VerticaQueryBuilder query;
	private String reportId;
	
	public VerticaExportUtil(VerticaQueryBuilder query, String reportId) {
		this.query = query;
		this.reportId = reportId;
	}

	public VerticaQueryBuilder getQuery() {
		return query;
	}

	public void setQuery(VerticaQueryBuilder query) {
		this.query = query;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	
	public static void generateCSVFile(String queryCSVStr, String reportId) throws QueryExecutionException {
		String str = VerticaExportManager.getInstance().VSQLCommand;
		String fileName = ReportingUtil.getUniqueCSVFile(reportId);
		str = str.replace("$FILENAME", fileName);
		str = str.replace("$QUERY", queryCSVStr);
		logger.info("Report Id: "+reportId+"|Runing export CSV VSQL command: ["+str+"| FileName: "+fileName);
		try {
		    int reportStatus = ReportingUtil.executeCommand(str);
//			logger.info("Reporting util status: "+ReportingUtil.executeCommand(str));
			logger.info("Reporting util status: "+reportStatus);
		} catch (IOException e) {
			//logger.error(e.getMessage(),e);
			logger.info("generateCSVFile Exception: ",e);
			throw new QueryExecutionException(ReportingError.CSV_FAILED);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
			throw new QueryExecutionException(ReportingError.CSV_FAILED);
		} 		
	}
	
	public static void generateTempCSVFile(String queryCSVStr, String reportId,String createTableQueryStr) throws QueryExecutionException {
		String str = VerticaExportManager.getInstance().VSQLCommand;
		String fileName = ReportingUtil.getUniqueCSVFile(reportId);
		str = str.replace("$FILENAME", fileName);
		str = str.replace("$QUERY", createTableQueryStr+";"+queryCSVStr);
		logger.info("in generateCSVfile: "+queryCSVStr);
		//str = str.replace("$QUERY", "select count(1) from "+"TEMP"+reportId.replace("-", ""));
		//str = str.replace("$QUERY","select timestamp as ts,entity_type from hourly limit 1");
		logger.info("Report Id: "+reportId+"|Runing export Temp CSV VSQL command: ["+str+"| FileName: "+fileName);
		try {
			int reportStatus = ReportingUtil.executeCommand(str);
//			logger.info("Reporting util status: "+ReportingUtil.executeCommand(str));
			logger.info("Reporting util status: "+reportStatus);
		} catch (IOException e) {
			//logger.error(e.getMessage(),e);
			logger.info("generateCSVFile Exception: ",e);
			throw new QueryExecutionException(ReportingError.CSV_FAILED);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(),e);
			throw new QueryExecutionException(ReportingError.CSV_FAILED);
		} 		
	}
	
	public static void main(String[] args) {
		String str = "/opt/vertica/bin/vsql -hvertica-dbserver -Udbadmin -wabc123  -A -o /home/viswanathan/$FILENAME -c \"$QUERY\"";
		str = str.replace("$FILENAME", "abc.csv");
		str = str.replace("$QUERY", "select * from verticaelement where element_id ILIKE '%IMP%' limit 10;");
		try {
			int returnCode = ReportingUtil.executeCommand(str);
			System.out.println("returnCode: "+returnCode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	


}