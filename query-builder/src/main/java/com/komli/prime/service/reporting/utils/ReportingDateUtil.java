package com.komli.prime.service.reporting.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.komli.prime.service.reporting.constants.QueryBuilderConstants;
import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.pojo.InputParameters.ReportType;

public class ReportingDateUtil {
	
	public static String getHourlyTimeRangeClauseForHourly(long startTimestamp, long endTimestamp, DateTimeZone tz) throws QueryBuilderException {
		if(tz == null){
			tz = DateTimeZone.UTC;
		}
		validateRange(startTimestamp, endTimestamp);
		return "(table.whereclausetimestamp >= "+startTimestamp+" and table.whereclausetimestamp < "+endTimestamp +")";
	}
	
	public static String getHourlyTimeRangeClause(long startTimestamp, long endTimestamp, DateTimeZone tz) throws QueryBuilderException {
		if(tz == null){
			tz = DateTimeZone.UTC;
		}
		validateRange(startTimestamp, endTimestamp);
		if(startTimestamp >= endTimestamp){
			return "";
		}
		String preClause = getPreHourlyTimeRangeClause(startTimestamp, endTimestamp, tz);
		String postClause = getPostHourlyTimeRangeClause(startTimestamp, endTimestamp, tz);

		
		if(StringUtils.isNotBlank(preClause) && StringUtils.isNotBlank(postClause)){
			return "("+ preClause + " OR " + postClause+")";
		}else {
			return preClause + postClause;
		}
	}

	private static String getPreHourlyTimeRangeClause(long startTimestamp, long endTimestamp, DateTimeZone tz) {

		long sameDayStartTimestamp = (startTimestamp/86400) *86400;
		if(sameDayStartTimestamp == startTimestamp){
			return "";
		}
		long from = startTimestamp;
		long to = Math.min(sameDayStartTimestamp + 86400, endTimestamp);		
		return "(table.whereclausetimestamp >= "+from+" and table.whereclausetimestamp < "+to +")";
	}
	
    private static String getPostHourlyTimeRangeClause(long startTimestamp, long endTimestamp, DateTimeZone tz)
    {
         long currentTime = System.currentTimeMillis()/1000;
//        long currentTime = 1454680800;
        long calculateTime = currentTime - (86400 * QueryBuilderConstants.CONFIGURABLE_DAYS);
        if (endTimestamp > calculateTime) {
            long nextDayEndTimestamp = (endTimestamp / 86400) * 86400 + 86400;
            DateTime endDT = new DateTime(endTimestamp * 1000, tz);
            long sameDayStartTimestamp = ((currentTime - (86400 * 7)) / 86400) * 86400;
            long from = Math.max(sameDayStartTimestamp, startTimestamp);
            long to = endTimestamp;
            if (to > from) {
                return "(table.whereclausetimestamp >= " + from + " and table.whereclausetimestamp < " + to + ")";
            } else {
                return "";
            }
        } else {
            long nextDayEndTimestamp = (endTimestamp / 86400) * 86400 + 86400;
            if (endTimestamp > nextDayEndTimestamp - 3600 || endTimestamp == nextDayEndTimestamp - 86400) {
                return "";
            }
            long from = Math.max(nextDayEndTimestamp - 86400, startTimestamp);
            if (from == startTimestamp && startTimestamp != nextDayEndTimestamp - 86400) {
                return "";
            }
            long to = endTimestamp;
            if (to > from) {
                return "(table.whereclausetimestamp >= " + from + " and table.whereclausetimestamp < " + to + ")";
            } else {
                return "";
            }
        }
    }
    
    public static String getDailyTimeRangeClauseForDailyInterval(long startTimestamp, long endTimestamp, DateTimeZone tz)
        throws QueryBuilderException
    {
        long currentTime = System.currentTimeMillis() / 1000;
//         long currentTime = 1454680800;
        long calculateTime = currentTime - (86400 * QueryBuilderConstants.CONFIGURABLE_DAYS);
        if (endTimestamp > calculateTime) {
            if (tz == null) {
                tz = DateTimeZone.UTC;
            }
            validateRange(startTimestamp, endTimestamp);
            long sameDayStartTimestamp = (startTimestamp / 86400) * 86400;
            long nextDayEndTimestamp = (calculateTime / 86400) * 86400 + 86400;
            long from = sameDayStartTimestamp;
            long to = nextDayEndTimestamp;
            if (sameDayStartTimestamp != startTimestamp) {
                from = sameDayStartTimestamp + 86400;
            }
            if (calculateTime <= nextDayEndTimestamp - 3600) {
                to = nextDayEndTimestamp - 86400;
            }
            if (to > from) {
                return "(table.whereclausetimestamp >= " + from + " and table.whereclausetimestamp < " + to + ")";
            } else {
                return "";
            }
        } else {
            if (tz == null) {
                tz = DateTimeZone.UTC;
            }
            validateRange(startTimestamp, endTimestamp);
            long sameDayStartTimestamp = (startTimestamp / 86400) * 86400;
            long nextDayEndTimestamp = (endTimestamp / 86400) * 86400 + 86400;
            long from = sameDayStartTimestamp;
            long to = nextDayEndTimestamp;
            if (sameDayStartTimestamp != startTimestamp) {
                from = sameDayStartTimestamp + 86400;
            }
            if (endTimestamp <= nextDayEndTimestamp - 3600) {
                to = nextDayEndTimestamp - 86400;
            }
            if (to > from) {
                return "(table.whereclausetimestamp >= " + from + " and table.whereclausetimestamp < " + to + ")";
            } else {
                return "";
            }
        }
    }
	
	public static String getDailyTimeRangeClauseForOnlyDaily(long startTimestamp, long endTimestamp, DateTimeZone tz) throws QueryBuilderException {
		if(tz == null){
			tz = DateTimeZone.UTC;
		}
		validateRange(startTimestamp, endTimestamp);
		long sameDayStartTimestamp = (startTimestamp/86400) *86400;
		long nextDayEndTimestamp = (endTimestamp/86400) *86400 + 86400;
		long from = sameDayStartTimestamp;
		long to = nextDayEndTimestamp;
		if(sameDayStartTimestamp != startTimestamp ){
			from = sameDayStartTimestamp + 86400;
		}
		if(endTimestamp <= nextDayEndTimestamp -3600){
			to = nextDayEndTimestamp -86400;
		}
		if( to > from){
			return "(table.whereclausetimestamp >= "+from+" and table.whereclausetimestamp < "+to+")";
		}else{
			return "";
		}
	}
	
	public static String getDailyTimeRangeClause(long startTimestamp, long endTimestamp, DateTimeZone tz, ReportType reportType) throws QueryBuilderException {
		if(tz == null){
			tz = DateTimeZone.UTC;
		}
		validateRange(startTimestamp, endTimestamp);
		String preClause = getPreDailyTimeRangeClause(startTimestamp, endTimestamp, tz, reportType);
		String postClause = getPostDailyTimeRangeClause(startTimestamp, endTimestamp, tz, reportType);
		
		if(StringUtils.isNotBlank(preClause) && StringUtils.isNotBlank(postClause)){
			return "("+ preClause + " OR " + postClause+")";
		}else {
			return preClause + postClause;
		}
	}

    private static String getPreDailyTimeRangeClause(long startTimestamp, long endTimestamp, DateTimeZone tz, ReportType reportType)
        throws QueryBuilderException
    {
         long currentTime = System.currentTimeMillis()/1000;
//        long currentTime = 1454680800;
        long calculateTime = currentTime - (86400 * QueryBuilderConstants.CONFIGURABLE_DAYS);
        if (endTimestamp > calculateTime && !reportType.equals(ReportType.AUDIENCE)) {
            if (startTimestamp > calculateTime) {
                return "";
            }
            long from = 0;
            long to = 0;
            DateTime startDT = new DateTime(startTimestamp * 1000, tz);
            DateTime currentstartDT = new DateTime(calculateTime * 1000, tz);
            long lastmonthStartTimestamp =
                new DateTime(currentstartDT.getYear(), currentstartDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long currentmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long nextmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1).getMillis() / 1000;
            if (startTimestamp != lastmonthStartTimestamp) {
                from = lastmonthStartTimestamp;
            } else {
                from = startTimestamp;
            }
            if (startTimestamp != currentmonthStartTimestamp) {
                from = startTimestamp;
            }
            if (lastmonthStartTimestamp != nextmonthStartTimestamp) {
                to = nextmonthStartTimestamp;
            } else {
                to = calculateTime;
            }
            if (from >= to) {
                return "";
            }
            if (to >= endTimestamp) {
                return "";
            }
            return getDailyTimeRangeClauseForOnlyDaily(from, to, tz);
        } else {
            DateTime startDT = new DateTime(startTimestamp * 1000, tz);
            long currentmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long nextmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1).getMillis() / 1000;
            if (startTimestamp == currentmonthStartTimestamp) {
                return "";
            }
            long from = startTimestamp;
            long to = Math.min(endTimestamp, nextmonthStartTimestamp);
            if (from >= to) {
                return "";
            }
            return getDailyTimeRangeClauseForOnlyDaily(from, to, tz);
        }
    }
	
    private static String getPostDailyTimeRangeClause(long startTimestamp, long endTimestamp, DateTimeZone tz, ReportType reportType)
        throws QueryBuilderException
    {
         long currentTime = System.currentTimeMillis()/1000;
//        long currentTime = 1454680800;
        long calculateTime = currentTime - (86400 * QueryBuilderConstants.CONFIGURABLE_DAYS);
        if (endTimestamp > calculateTime && !reportType.equals(ReportType.AUDIENCE)) {
            if (startTimestamp > calculateTime) {
                return "";
            }
            DateTime startDT = new DateTime(startTimestamp * 1000, tz);
            DateTime endDT = new DateTime(endTimestamp * 1000, tz);
            DateTime currentstartDT = new DateTime(calculateTime * 1000, tz);
            long currentmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long recentmonthStartTimestamp =
                new DateTime(currentstartDT.getYear(), currentstartDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long lastmonthStartTimestamp =
                new DateTime(currentstartDT.getYear(), currentstartDT.getMonthOfYear(), 1, 0, 0, 0, tz).minusMonths(1)
                    .getMillis() / 1000;
            long currentmonthEndTimestamp =
                new DateTime(endDT.getYear(), endDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long nextmonthEndTimestamp =
                new DateTime(endDT.getYear(), endDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1).getMillis() / 1000;
            if (currentmonthStartTimestamp == lastmonthStartTimestamp) {
                return "";
            }
//            if (endTimestamp == currentmonthEndTimestamp || endTimestamp > nextmonthEndTimestamp - 3600) {
//                return "";
//            }
            long from = Math.max(startTimestamp, recentmonthStartTimestamp);
//            if (from == startTimestamp && startTimestamp != currentmonthEndTimestamp) {
//                return "";
//            }
            long to = calculateTime;
            if (from >= to) {
                return "";
            }
            return getDailyTimeRangeClauseForOnlyDaily(from, to, tz);
        } else {
            DateTime endDT = new DateTime(endTimestamp * 1000, tz);
            long currentmonthEndTimestamp =
                new DateTime(endDT.getYear(), endDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long nextmonthEndTimestamp =
                new DateTime(endDT.getYear(), endDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1).getMillis() / 1000;
            if (endTimestamp == currentmonthEndTimestamp || endTimestamp > nextmonthEndTimestamp - 3600) {
                return "";
            }
            long from = Math.max(startTimestamp, currentmonthEndTimestamp);
            if (from == startTimestamp && startTimestamp != currentmonthEndTimestamp) {
                return "";
            }
            long to = endTimestamp;
            if (from >= to) {
                return "";
            }
            return getDailyTimeRangeClauseForOnlyDaily(from, to, tz);
        }
    }

    public static String getMonthlyTimeRangeClause(long startTimestamp, long endTimestamp, DateTimeZone tz, ReportType reportType)
        throws QueryBuilderException
    {
         long currentTime = System.currentTimeMillis()/1000;
//        long currentTime = 1454680800;
        long calculateTime = currentTime - (86400 * QueryBuilderConstants.CONFIGURABLE_DAYS);
        if (endTimestamp > calculateTime && !reportType.equals(ReportType.AUDIENCE)) {
            if (tz == null) {
                tz = DateTimeZone.UTC;
            }
            validateRange(startTimestamp, endTimestamp);
            if (startTimestamp >= endTimestamp) {
                return "";
            }
            DateTime startDT = new DateTime(startTimestamp * 1000, tz);
            DateTime currentstartDT = new DateTime(calculateTime * 1000, tz);
            long lastmonthStartTimestamp =
                new DateTime(currentstartDT.getYear(), currentstartDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long currentmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            if (lastmonthStartTimestamp == currentmonthStartTimestamp) {
                return "";
            }
            long nextmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1).getMillis() / 1000;
            DateTime endDT = new DateTime(endTimestamp * 1000, tz);
            long recentmonthStartTimestamp =
                new DateTime(currentstartDT.getYear(), currentstartDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long nextmonthStartofEndTimestamp =
                new DateTime(endDT.getYear(), endDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1)
                    .getMillis() / 1000;

            long from = currentmonthStartTimestamp;
            long to = nextmonthStartofEndTimestamp;
            if (currentmonthStartTimestamp != startTimestamp) {
                from = nextmonthStartTimestamp;
            }
            if (calculateTime <= nextmonthStartofEndTimestamp - 3600) {
                to = recentmonthStartTimestamp;
            }
            if (to > from) {
                return "(table.whereclausetimestamp >= " + from + " and table.whereclausetimestamp < " + to + ")";
            } else {
                return "";
            }
        } else {
            if (tz == null) {
                tz = DateTimeZone.UTC;
            }
            validateRange(startTimestamp, endTimestamp);
            if (startTimestamp >= endTimestamp) {
                return "";
            }
            DateTime startDT = new DateTime(startTimestamp * 1000, tz);
            long currentmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long nextmonthStartTimestamp =
                new DateTime(startDT.getYear(), startDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1).getMillis() / 1000;
            DateTime endDT = new DateTime(endTimestamp * 1000, tz);
            long currentmonthStartofEndTimestamp =
                new DateTime(endDT.getYear(), endDT.getMonthOfYear(), 1, 0, 0, 0, tz).getMillis() / 1000;
            long nextmonthStartofEndTimestamp =
                new DateTime(endDT.getYear(), endDT.getMonthOfYear(), 1, 0, 0, 0, tz).plusMonths(1).getMillis() / 1000;

            long from = currentmonthStartTimestamp;
            long to = nextmonthStartofEndTimestamp;
            if (currentmonthStartTimestamp != startTimestamp) {
                from = nextmonthStartTimestamp;
            }
            if (endTimestamp <= nextmonthStartofEndTimestamp - 3600) {
                to = currentmonthStartofEndTimestamp;
            }
            if (to > from) {
                return "(table.whereclausetimestamp >= " + from + " and table.whereclausetimestamp < " + to + ")";
            } else {
                return "";
            }
        }
    }
	
	private static void validateRange(long starttimestamp, long endtimestamp) throws QueryBuilderException{
		if( starttimestamp <0 ||  endtimestamp<0){
			throw new QueryBuilderException(ReportingError.INVALID_TIMERANGE);
		}
		if( starttimestamp >= endtimestamp){
			throw new QueryBuilderException(ReportingError.INVALID_TIMERANGE);
		}
		if( starttimestamp  != (starttimestamp/3600)* 3600){
			throw new QueryBuilderException(ReportingError.INVALID_STARTTIME);
		}	
	}
}
