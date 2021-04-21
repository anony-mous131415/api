package com.komli.prime.service.reporting.exceptions;


public class QueryExecutionException extends ReportGeneratingException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8654187343842460250L;

	/**
	 * @param errMsg
	 */
	public QueryExecutionException(String errMsg) {
		super(errMsg);		
	}

	public QueryExecutionException(ReportingError repErr) {
		super(repErr);
	}

}
