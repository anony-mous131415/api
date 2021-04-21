package com.komli.prime.service.reporting.constants;

public class ErrorMessages {

	public static final String START_DATE_INCORRECT = "StartDate is missing/incorrect, expected in YYYY-MM-DD format. Please update to proceed.";
	public static final String END_DATE_INCORRECT = "EndDate is missing/incorrect, expected in YYYY-MM-DD format. Please update to proceed.";
	public static final String START_TIME_INCORRECT = "StartTime is incorrect, expected in HH:00 format. Please update to proceed.";
	public static final String END_TIME_INCORRECT = "EndTime is incorrect, expected in HH:59 format. Please update to proceed.";
	public static final String SEGMENT_ID_INCORRECT = "One or more Segment Ids passed are incorrect. Please update to proceed.";
	public static final String GROUPBY_PARAMETER_INCORRECT = "GroupBy is incorrect, expected value is AdLI only. Please update to proceed.";
	public static final String INTERVAL_MISSING = "Interval is missing, expected one of the values from {None, Hourly, Daily, Weekly, Monthly}. Please update to proceed";
	public static final String INTERVAL_INCORRECT = "Interval is incorrect, expected one of the values from {None, Hourly, Daily, Weekly, Monthly}. Please update to proceed";
	public static final String TIME_BOUNDARY_NOT_CORRECT = "Time boundary for given Interval is incorrect. Please update to proceed.";
	public static final String START_DATE_TIME_GREATER_THAN_END_DATE_TIME = "StartDateTime cannot be later than the EndDateTime. Please update to proceed.";
	public static final String UNABLE_TO_CONNECT_TO_DATABASE = "Unable to Connect to Database";
	public static final String REQUEST_ID_INCORRECT = "Request id is incorrect";
	public static final String SORT_ON_FIELD_NAME_ORDER_INCORRECT="Field name is incorrect";
	public static final String SORT_ORDER_INCORRECT="Sort order is incorrect";
	
	
	public enum QueryErrorResponse {
		//_ENUM_CONST(_MESSAGE,_CODE);
		
		//AUTH RELATED 1000 -1999
		AUTH_FAILURE("Authentication Failed"),
		
		
		//QUERY RELATED 2000 - 2999
		INVALID_COLUMNS("Invalid Columns"),
		INTERVAL_MISSING("Interval missing."),
		INTERVAL_INCORRECT("Interval incorrect."),
		STARTDATE_INCORRECT("Start Date incorrect."),
		ENDDATE_INCORRECT("End Date incorrect."),
		STARTTIME_INCORRECT("Start Time incorrect."),
		ENDTIME_INCORRECT("End Time incorrect."), 
		TIMEBOUNDARY_INCORRECT("Time Boundary incorrect."),
		STARTTIME_AFTER_ENDTIME("Start Date is after end date.");

		private QueryErrorResponse(String message) {
			this.message = message;
		}
		
		public String toString(){
			return message;
		}
		
		private String message;
	}
}