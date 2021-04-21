package io.revx.api.reportbuilder.redshift;

public class ReportConstants {

	public static final int MAX_RETRY_COUNT = 5;
	public static final String COUNT_KEY = "count";

	public static final String QUERY_TEMPLATE_RTB = "SELECT |#*OUTERSELECT*#| " + "FROM ( " + " SELECT |#*SELECT*#| "
			+ " FROM |#*TABLE*#| " + " WHERE |#*WHERECONDITION*#| " + " GROUP BY |#*GROUPBY*#| " + ") A "
			+ " |#*JOINCONDITION*#| ";

	public static final String QUERY_TEMPLATE_CONVERSION = "SELECT |#*OUTERSELECT*#| " + "FROM ( " + " SELECT |#*SELECT*#| "
			+ " FROM |#*TABLE*#| " + " WHERE |#*WHERECONDITION*#| " + ") A "
			+ " |#*JOINCONDITION*#| ";

	public static final String QUERY_TEMPLATE_SELECT_FROM_TEMP_TABLE = "SELECT * FROM |#*TABLE*#| A ";
	public static final String QUERY_TEMPLATE_CHECK_TEMP_TABLE = "SELECT COUNT(1) as count FROM ";
	
	public static final String CONFIG_FILE_RTB = "rtb.json";
	public static final String CONFIG_FILE_CONVERSION = "conversionreport.json";

	public static final String PLACEHOLDER_OUTERSELECT = "|#*OUTERSELECT*#|";
	public static final String PLACEHOLDER_SELECT = "|#*SELECT*#|";
	public static final String PLACEHOLDER_TABLE = "|#*TABLE*#|";
	public static final String PLACEHOLDER_WHERE = "|#*WHERECONDITION*#|";
	public static final String PLACEHOLDER_GROUPBY = "|#*GROUPBY*#|";
	public static final String PLACEHOLDER_JOIN = "|#*JOINCONDITION*#|";
	public static final String PLACEHOLDER_VALUES = "|#*VALUES*#|";
	public static final String PLACEHOLDER_STARTTIME = "|#*STARTTIME*#|";
	public static final String PLACEHOLDER_ENDTIME = "|#*ENDTIME*#|";
	public static final String PLACEHOLDER_ORDERBY = "|#*ORDERBYCONDITION*#|";
}
