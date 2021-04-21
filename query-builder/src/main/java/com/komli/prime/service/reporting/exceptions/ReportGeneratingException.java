package com.komli.prime.service.reporting.exceptions;

public class ReportGeneratingException extends Exception {

	private static final long serialVersionUID = -7481576721983618517L;
	private ReportingError repErr;

	
	public ReportGeneratingException(String message) {
		super(message);
	}
	
	public ReportGeneratingException(ReportingError repErr) {
		this.repErr = repErr;
	}

	public ReportingError getRepErr() {
		return repErr;
	}

	public enum ReportingError{
		UNKNOWN_SELECT(3001,"Unknown element in SELECTS"),
		UNKNOWN_GROUPBY(3002,"Unknown element in GROUPBY"),
		UNKNOWN_FILTER(3003,"FILTERs are not defined properly"),
		UNKNOWN_ORDERBY(3004,"ORDERBYs are not defined properly"),
		NOT_COLUMN_RDB(3005,"Element does not belong to Managed"),
		NOT_COLUMN_RTB(3006,"Element does not belong to RTB"),
		NOTHING_TO_SELECT(3007,"There are no selects"),
		UNDEFINED_REPORT_TYPE(3008,"Invalid Report Type"),
		UNDEFINED_INTERVAL(3009,"Invalid Interval"),
		NO_RECORDS_FOUND(3010,"No Records Found"),
		SQL_EXCEPTION(3011,"Internal Error"),
		NOT_PROPER_ORDERBY(3012,"Order By is not defined properly"), 
		NOT_PROPER_FILTER(3013,"Filter is not defined properly"), 
		INTERNAL_ERROR(3014,"Report Manager: Internal Error"),
		VAUE_CANNOT_GROUPBY(3015,"Values cannot be part of Group By"), 
		KEY_SHOULD_BE_GROUPBY(3016,"Keys should belong to Group By"), 
		INVALID_TIMERANGE(3017,"Invalid Time Range"), 
		INVALID_STARTTIME(3018,"Start time should be the start of the hour"), 
		INVALID_ENDTIME(3019,"End time should not be the start of the hour"), 
		FILTER_VALUE_NOT_NUMERIC(3020,"Filter value is not numeric."), 
		NO_VERTICA_CACHE(3021,"Setup Configuration Error."), 
		CSV_FAILED(3022,"CSV generation failed."), 
		FILTER_VALUE_NOT_ALLOWED(3023,"Filter values can only have following characters [a-z],[A-Z],[0-9],[(],[)],[_],[-],[,],[.],[white spaces]"), 
		EXECUTION_TIMEDOUT(3024,"Report timed-out"),
		INTERUPTED_EXCEPTION(3025,"Report Interrupted"),
		QUERY_GENERATION_FAILED(3026,"Query Generation Failed"),
		HOURLY_INTERVAL_NOT_SUPPORTED(3027,"Hourly interval is not supported"),
		NOT_COLUMN_AUDIENCE(3028,"Element does not belong to Audience"),
		UNKNOWN_ERROR(3029,"Internal Error"),
		NOT_COLUMN_BIDFUNNEL(3030,"Element does not belong to BidFunnel"),
		DAILY_INTERVAL_NOT_SUPPORTED(3031,"Daily interval is not supported"),
		WEEKLY_INTERVAL_NOT_SUPPORTED(3032,"Weekly interval is not supported"),
		MONTHLY_INTERVAL_NOT_SUPPORTED(3033,"Monthly interval is not supported"),
		NOT_COLUMN_UNQUSER(3034,"Element does not belong to UniqueUser Schema"),
		NOT_COLUMN_PARAM_CONV_REPORT(3035,"Element does not belong to parametricConversionReport Schema");

		private ReportingError(int errorCode, String errMessage) {
			errCode = errorCode;
			errMsg = errMessage;
		}

		public String getErrMessage() {
			return errMsg;
		}
		
		public int getErrCode() {
			return errCode;
		}

		int errCode;
		String errMsg;
	
	}

}