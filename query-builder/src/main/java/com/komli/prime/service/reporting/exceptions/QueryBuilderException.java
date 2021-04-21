package com.komli.prime.service.reporting.exceptions;

public class QueryBuilderException extends ReportGeneratingException {

	private static final long serialVersionUID = -6073608298877291015L;

	/**
	 * @param errMsg
	 */
	public QueryBuilderException(String errMsg) {
		super(errMsg);
	}

	public QueryBuilderException(ReportingError repErr) {
		super(repErr);
	}

}
