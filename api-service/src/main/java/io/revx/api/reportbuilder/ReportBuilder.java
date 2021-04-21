package io.revx.api.reportbuilder;

import io.revx.core.exception.QueryBuilderException;
import io.revx.core.model.reporting.ReportingRequest;
import io.revx.core.model.reporting.ReportingResponse;

public interface ReportBuilder {
	
	/**
	 * Driver method to fetch report data
	 * @param reportingRequest
	 */
	public ReportingResponse fetchReport(ReportingRequest reportingRequest) throws QueryBuilderException;
	
	
	/**
	 * Driver method to generate the export csv file with report data
	 * @param reportingRequest
	 */
	public ReportingResponse fetchReportCSV(ReportingRequest reportingRequest) throws QueryBuilderException;
	
	/**
	 * Method builds the final query to be execute the the respective database.
	 * @param reportingRequest
	 * @return
	 */
	// public String buildQuery(ReportingRequest reportingRequest);
	
	/**
	 * Method executes the generated query, formats the data in the required format
	 * @param reportingRequest
	 * @param queryString
	 * @return
	 */
	// public List<Object> executeQuery(ReportingRequest reportingRequest, String queryString);
}
