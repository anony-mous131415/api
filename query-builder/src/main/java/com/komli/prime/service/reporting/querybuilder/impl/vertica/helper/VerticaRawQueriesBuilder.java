package com.komli.prime.service.reporting.querybuilder.impl.vertica.helper;

import static com.komli.prime.service.reporting.cache.VerticaCache.getAudienceDailyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getAudienceMonthlyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getBidfunnelDailyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getManagedDailyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getManagedHourlyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getManagedMonthlyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getRTBDailyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getRTBHourlyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getRTBMonthlyTableName;
import static com.komli.prime.service.reporting.cache.VerticaCache.getTableDailyParamConvReport;
import static com.komli.prime.service.reporting.cache.VerticaCache.getUnqUserDailyTableName;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.pojo.InputParameters.Interval;
import com.komli.prime.service.reporting.pojo.InputParameters.ReportType;
import com.komli.prime.service.reporting.utils.ReportingDateUtil;
import com.komli.prime.service.reporting.utils.ReportingUtil;

public class VerticaRawQueriesBuilder
{

    private static final Logger logger = LoggerFactory.getLogger(VerticaRawQueriesBuilder.class);

    private static final String HOURLY_TIMESTAMP_HOURLY = "ts_utc_hour";

    private static final String HOURLY_TIMESTAMP_DAILY = "ts_utc_day";

    private static final String HOURLY_TIMESTAMP_WEEKLY = "ts_utc_week";

    private static final String HOURLY_TIMESTAMP_MONTHLY = "ts_utc_month";

    private static final String DAILY_TIMESTAMP_DAILY = "timestamp";

    private static final String DAILY_TIMESTAMP_WEEKLY = "ts_utc_week";

    private static final String DAILY_TIMESTAMP_MONTHLY = "ts_utc_month";

    private static final String MONTHLY_TIMESTAMP_MONTHLY = "timestamp";

    private static final String CONST_STARTTIMESTAMP = "$$starttimestamp";

    private static final String CONST_ENDTIMESTAMP = "$$endtimestamp";

    private VerticaQueryStrings verticaQueryStrings;

    private VerticaQueryInputParameters inputParams;

    private String startTimestampQueryStr = "";

    private String endTimestampQueryStr = "";

    private StringBuilder reportingQuery;

    public VerticaRawQueriesBuilder(VerticaQueryStrings queryStrings, VerticaQueryInputParameters inputParams)
        throws QueryBuilderException
    {
        this.verticaQueryStrings = queryStrings;
        this.inputParams = inputParams;
        buildRawQueries();
    }

    private void buildRawQueries() throws QueryBuilderException
    {
        List<SubQuery> subQueryList = new ArrayList<SubQuery>();
        long startTimestamp = inputParams.getStartTimestamp();
        long endTimestamp = inputParams.getEndTimestamp();
        long currentTime = 0;
        long archivalStartTime = 0;
        DateTimeZone tz = inputParams.getTz();
        String hourlyTimestampClause = "";
        String dailyTimestampClause = "";
        String monthlyTimestampClause = "";
        ReportType reportType = inputParams.getReportType();
        String rawSubQuery = verticaQueryStrings.prepareRawSubQuery();
        switch (inputParams.getInterval()) {
            case HOURLY:
                if (!reportType.hasHourlyTable()) {
                    throw new QueryBuilderException(ReportingError.HOURLY_INTERVAL_NOT_SUPPORTED);
                }
                currentTime = (System.currentTimeMillis() / 1000 / 3600) * 3600;
                archivalStartTime = currentTime - (86400 * 31); // Hourly supports 31 days of data

                if (startTimestamp < archivalStartTime) {
                    startTimestamp = archivalStartTime;
                    // throw new QueryBuilderException(ReportingError.HOURLY_INTERVAL_NOT_SUPPORTED);
                }
                hourlyTimestampClause =
                    ReportingDateUtil.getHourlyTimeRangeClauseForHourly(startTimestamp, endTimestamp, tz);
                if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                    subQueryList.add(new SubQuery(hourlyTimestampClause, getManagedHourlyTableName(),
                        getRTBHourlyTableName(), HOURLY_TIMESTAMP_HOURLY, HOURLY_TIMESTAMP_HOURLY));
                }
                startTimestampQueryStr = "GREATEST(" + startTimestamp + ", ts)";
                endTimestampQueryStr = "LEAST(" + endTimestamp + ",(ts + 3599))";// 60*60-1
                break;
            case DAILY:
                if (!reportType.hasHourlyTable() && !reportType.hasDailyTable()
                    && !reportType.equals(ReportType.BIDFUNNEL) && !reportType.equals(ReportType.UNQUSERS)) {
                    throw new QueryBuilderException(ReportingError.DAILY_INTERVAL_NOT_SUPPORTED);
                }
                currentTime = (System.currentTimeMillis() / 1000 / 86400) * 86400;
                archivalStartTime = currentTime - (86400 * 31 * 6); // Daily supports 6 months of data
                if (startTimestamp < archivalStartTime) {
                    startTimestamp = archivalStartTime;
                    // throw new QueryBuilderException(ReportingError.DAILY_INTERVAL_NOT_SUPPORTED);
                }
                if (reportType.hasHourlyTable()) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClause(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        subQueryList.add(new SubQuery(hourlyTimestampClause, getManagedHourlyTableName(),
                            getRTBHourlyTableName(), HOURLY_TIMESTAMP_HOURLY, HOURLY_TIMESTAMP_DAILY));
                    }
                }
                if (!reportType.equals(ReportType.BIDFUNNEL) && !reportType.equals(ReportType.UNQUSERS)) {
                    if(!reportType.equals(ReportType.AUDIENCE)){
                    	dailyTimestampClause =
                    			ReportingDateUtil.getDailyTimeRangeClauseForDailyInterval(startTimestamp, endTimestamp, tz);
                    }else{
                    	dailyTimestampClause = "(table.whereclausetimestamp >= " + startTimestamp + " and table.whereclausetimestamp < " + endTimestamp + ")";
                    }
                    if (StringUtils.isNotBlank(dailyTimestampClause)) {
                        subQueryList.add(new SubQuery(dailyTimestampClause, getManagedDailyTableName(),
                            getRTBDailyTableName(), DAILY_TIMESTAMP_DAILY, DAILY_TIMESTAMP_DAILY,
                            getAudienceDailyTableName()));
                    }
                }
                if (!reportType.hasHourlyTable() && !reportType.hasDailyTable() && !reportType.hasMonthlyTable()
                    && (reportType.equals(ReportType.BIDFUNNEL) || reportType.equals(ReportType.UNQUSERS))) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClauseForHourly(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        subQueryList.add(new SubQuery(hourlyTimestampClause, "timestamp", getBidfunnelDailyTableName(),
                            getUnqUserDailyTableName()));
                    }
                }
                startTimestampQueryStr = "GREATEST(" + startTimestamp + ", ts)";
                endTimestampQueryStr = "LEAST(" + endTimestamp + ",(ts + 86399))";// 24*60*60-1
                break;
            case WEEKLY:
                if (!reportType.hasHourlyTable() && !reportType.hasDailyTable()) {
                    throw new QueryBuilderException(ReportingError.WEEKLY_INTERVAL_NOT_SUPPORTED);
                }
                currentTime = (System.currentTimeMillis() / 1000 / 86400) * 86400;
                archivalStartTime = currentTime - (86400 * 31 * 6); // Daily supports 6 months of data
                if (startTimestamp < archivalStartTime) {
                    startTimestamp = archivalStartTime;
                    // throw new QueryBuilderException(ReportingError.WEEKLY_INTERVAL_NOT_SUPPORTED);
                }
                if (reportType.hasHourlyTable()) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClause(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        subQueryList.add(new SubQuery(hourlyTimestampClause, getManagedHourlyTableName(),
                            getRTBHourlyTableName(), HOURLY_TIMESTAMP_HOURLY, HOURLY_TIMESTAMP_WEEKLY));
                    }
                }
                if(!reportType.equals(ReportType.AUDIENCE)){
                	dailyTimestampClause =
                			ReportingDateUtil.getDailyTimeRangeClauseForDailyInterval(startTimestamp, endTimestamp, tz);
                }else{
                	dailyTimestampClause = "(table.whereclausetimestamp >= " + startTimestamp + " and table.whereclausetimestamp < " + endTimestamp + ")";
                }
                
                if (StringUtils.isNotBlank(dailyTimestampClause)) {
                    subQueryList.add(new SubQuery(dailyTimestampClause, getManagedDailyTableName(),
                        getRTBDailyTableName(), DAILY_TIMESTAMP_DAILY, DAILY_TIMESTAMP_WEEKLY,
                        getAudienceDailyTableName()));
                }
                startTimestampQueryStr = "GREATEST(" + startTimestamp + ", ts)";
                endTimestampQueryStr = "LEAST(" + endTimestamp + ",(ts + 604799))";
                break;
            case MONTHLY:
                if (!reportType.hasHourlyTable() && !reportType.hasDailyTable() && !reportType.hasMonthlyTable()) {
                    throw new QueryBuilderException(ReportingError.MONTHLY_INTERVAL_NOT_SUPPORTED);
                }
                currentTime = System.currentTimeMillis() / 1000;
                archivalStartTime = currentTime - (86400 * 31 * 12 * 8); // Monthly supports 8 years of data
                if (startTimestamp < archivalStartTime) {
                    throw new QueryBuilderException(ReportingError.MONTHLY_INTERVAL_NOT_SUPPORTED);
                }
                if (reportType.hasHourlyTable()) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClause(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        subQueryList.add(new SubQuery(hourlyTimestampClause, getManagedHourlyTableName(),
                            getRTBHourlyTableName(), HOURLY_TIMESTAMP_HOURLY, HOURLY_TIMESTAMP_MONTHLY));
                    }
                }

                dailyTimestampClause =
                    ReportingDateUtil.getDailyTimeRangeClause(startTimestamp, endTimestamp, tz, reportType);
                monthlyTimestampClause =
                    ReportingDateUtil.getMonthlyTimeRangeClause(startTimestamp, endTimestamp, tz, reportType);
                if (StringUtils.isNotBlank(dailyTimestampClause)) {
                    subQueryList.add(new SubQuery(dailyTimestampClause, getManagedDailyTableName(),
                        getRTBDailyTableName(), DAILY_TIMESTAMP_DAILY, DAILY_TIMESTAMP_MONTHLY,
                        getAudienceDailyTableName()));
                }
                if (StringUtils.isNotBlank(monthlyTimestampClause)) {
                    subQueryList.add(new SubQuery(monthlyTimestampClause, getManagedDailyTableName(),
                        getRTBMonthlyTableName(), MONTHLY_TIMESTAMP_MONTHLY, MONTHLY_TIMESTAMP_MONTHLY,
                        getAudienceMonthlyTableName()));
                }
                startTimestampQueryStr = "GREATEST(" + startTimestamp + ", ts)";
                endTimestampQueryStr =
                    "LEAST(" + endTimestamp + ",(DATE_PART( 'EPOCH' , "
                        + "LAST_DAY((TIMESTAMP 'epoch' + ts * INTERVAL '1 Second ')) ) + 86399))::INT";
                break;
            case SUMMARY:
                if (reportType.hasHourlyTable()) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClause(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        subQueryList.add(new SubQuery(hourlyTimestampClause, getManagedHourlyTableName(),
                            getRTBHourlyTableName(), HOURLY_TIMESTAMP_HOURLY, null));
                    }
                }
                if (reportType.hasDailyTable()) {
                    dailyTimestampClause =
                        ReportingDateUtil.getDailyTimeRangeClause(startTimestamp, endTimestamp, tz, reportType);
                    if (StringUtils.isNotBlank(dailyTimestampClause)) {
                        subQueryList.add(new SubQuery(dailyTimestampClause, getManagedDailyTableName(),
                            getRTBDailyTableName(), DAILY_TIMESTAMP_DAILY, null, getAudienceDailyTableName()));
                    }
                }
                if (reportType.hasMonthlyTable()) {
                    monthlyTimestampClause =
                        ReportingDateUtil.getMonthlyTimeRangeClause(startTimestamp, endTimestamp, tz, reportType);

                    if (StringUtils.isNotBlank(monthlyTimestampClause)) {
                        subQueryList.add(new SubQuery(monthlyTimestampClause, getManagedMonthlyTableName(),
                            getRTBMonthlyTableName(), MONTHLY_TIMESTAMP_MONTHLY, null, getAudienceMonthlyTableName()));
                    }
                }

                if (!reportType.hasHourlyTable() && !reportType.hasDailyTable() && !reportType.hasMonthlyTable()
                    && (reportType.equals(ReportType.BIDFUNNEL) || reportType.equals(ReportType.UNQUSERS))) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClauseForHourly(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        subQueryList.add(new SubQuery(hourlyTimestampClause, "timestamp", getBidfunnelDailyTableName(),
                            getUnqUserDailyTableName()));
                    }
                }

                if (reportType.equals(ReportType.UFRECORDCOUNT)) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClauseForHourly(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        subQueryList.add(new SubQuery(hourlyTimestampClause, "timestamp", getUnqUserDailyTableName()));
                    }
                }

                if (reportType.equals(ReportType.PARAM_CONV_REPORT)) {
                    hourlyTimestampClause =
                        ReportingDateUtil.getHourlyTimeRangeClauseForHourly(startTimestamp, endTimestamp, tz);
                    if (StringUtils.isNotBlank(hourlyTimestampClause)) {
                        SubQuery query = new SubQuery(hourlyTimestampClause, "dayTimestamp");
                        query.setParamConvTable(getTableDailyParamConvReport());
                        subQueryList.add(query);
                    }
                }
                startTimestampQueryStr = "" + startTimestamp;
                endTimestampQueryStr = "" + endTimestamp;
                break;
            case METADATA:
                return;
            default:
                break;
        }

        StringBuilder aggQuery = getAggregatedQuery(rawSubQuery, subQueryList);
        reportingQuery = getFullReportingQuery(aggQuery);

    }

    public String getRawCreateTempTableQuery() throws QueryBuilderException
    {
        StringBuilder finalQuery = new StringBuilder("");
        finalQuery.append("select ").append(verticaQueryStrings.getSelectsFinalQueryStr()).append(" into temp table ")
            .append(ReportingUtil.getUniqueTempTable(inputParams.getSessionId()))
            .append(" from (")
            // .getSessionId())).append(" ON COMMIT PRESERVE ROWS from (")
            .append(reportingQuery).append(")B ").append(verticaQueryStrings.getJoinQueryStr()).append(" ")
            .append(verticaQueryStrings.getFilterMetadataQueryStr()).append(" ")
            .append(verticaQueryStrings.getOrderbyQueryStr());
        return replaceStartAndEndTimestampQueryStr(finalQuery);
    }

    public String getRawCreateTempTableCsvQuery(String reportId) throws QueryBuilderException
    {
        StringBuilder finalQuery = new StringBuilder("");
        finalQuery.append("select ").append(verticaQueryStrings.getSelectsFinalQueryStr()).append(" into temp table ")
            .append(ReportingUtil.getUniqueTempTable(reportId))
            .append(" from (")
            // .getSessionId())).append(" ON COMMIT PRESERVE ROWS from (")
            .append(reportingQuery).append(")B ").append(verticaQueryStrings.getJoinQueryStr()).append(" ")
            .append(verticaQueryStrings.getFilterMetadataQueryStr()).append(" ")
            .append(verticaQueryStrings.getOrderbyQueryStr());
        return replaceStartAndEndTimestampQueryStr(finalQuery);
    }

    private String replaceStartAndEndTimestampQueryStr(StringBuilder finalQuery)
    {
        String finalQueryStr = finalQuery.toString();
        // logger.info("Before replacing timestamp finalQueryStr: "+finalQueryStr +
        // " ,startTimestampQueryStr: "+startTimestampQueryStr+" ,endTimestampQueryStr"+endTimestampQueryStr);
        finalQueryStr = finalQueryStr.replace(CONST_STARTTIMESTAMP, startTimestampQueryStr);
        finalQueryStr = finalQueryStr.replace(CONST_ENDTIMESTAMP, endTimestampQueryStr);
        // logger.info("After replacing timestamp in finalQueryStr: "+finalQueryStr);
        return finalQueryStr;
    }

    public String getRawFetchQuery() throws QueryBuilderException
    {
        if (Interval.METADATA == inputParams.getInterval()) {
            return verticaQueryStrings.getOnlyMetaDataQuery();
        } else {
            StringBuilder finalQuery = new StringBuilder("");
            finalQuery.append("select ").append(verticaQueryStrings.getSelectsFinalQueryStr()).append(" from (")
                .append(reportingQuery).append(")B ").append(verticaQueryStrings.getJoinQueryStr()).append(" ")
                .append(verticaQueryStrings.getFilterMetadataQueryStr()).append(" ")
                .append(verticaQueryStrings.getOrderbyQueryStr()).append(" ")
                .append(verticaQueryStrings.getLimitOffsetStr());
            return replaceStartAndEndTimestampQueryStr(finalQuery);
        }
    }

    public String getRawCSVQuery() throws QueryBuilderException
    {
        StringBuilder finalQuery = new StringBuilder("");
        finalQuery.append("select ").append(verticaQueryStrings.getSelectsCSVFinalQueryStr()).append(" from (")
            .append("select ").append(verticaQueryStrings.getSelectsFinalQueryStr()).append(" from (")
            .append(reportingQuery).append(")B ").append(verticaQueryStrings.getJoinQueryStr()).append(" ")
            .append(verticaQueryStrings.getFilterMetadataQueryStr()).append(" ")
            .append(verticaQueryStrings.getOrderbyQueryStr()).append(")C");
        return replaceStartAndEndTimestampQueryStr(finalQuery);
    }

    private StringBuilder getFullReportingQuery(StringBuilder aggQuery)
    {
        StringBuilder reportingQuery = new StringBuilder("");
        reportingQuery.append("select ").append(verticaQueryStrings.getSelectsReportingOuterQueryStr())
            .append(" from (").append(aggQuery).append(")A ").append(verticaQueryStrings.getGroupByQueryStr())
            .append(" ").append(verticaQueryStrings.getFilterValuesQueryStr());
        return reportingQuery;
    }

    private StringBuilder getAggregatedQuery(String rawSubQuery, List<SubQuery> subQueryList)
    {
        StringBuilder aggQuery = new StringBuilder("");
        for (SubQuery subQuery : subQueryList) {
            String formattedQuery = subQuery.getFormattedQuery(rawSubQuery);
            if (StringUtils.isNotBlank(formattedQuery)) {
                aggQuery.append(formattedQuery).append(" UNION ALL ");
            }
        }
        int queryLength = aggQuery.length();
        if (queryLength > 0) {
            aggQuery.delete(queryLength - 11, queryLength);
        }
        return aggQuery;
    }

    private class SubQuery
    {
        public SubQuery(String timeStampClause, String managedTable, String rtbTable, String whereTimestampColumn,
            String groupByTimestampColumn, String audienceTable)
        {

            this.timeStampClause = timeStampClause;
            this.managedTable = managedTable;
            this.rtbTable = rtbTable;
            this.audienceTable = audienceTable;
            this.whereClauseTimestampColumn = whereTimestampColumn;
            this.groupByTimestampColumn = groupByTimestampColumn;
            this.audienceTable = audienceTable;
        }

        public SubQuery(String timeStampClause, String managedTable, String rtbTable, String whereTimestampColumn,
            String groupByTimestampColumn)
        {

            this.timeStampClause = timeStampClause;
            this.managedTable = managedTable;
            this.rtbTable = rtbTable;
            this.whereClauseTimestampColumn = whereTimestampColumn;
            this.groupByTimestampColumn = groupByTimestampColumn;
        }

        public SubQuery(String timeStampClause, String whereTimestampColumn, String bidfunnelTable, String unqUserTable)
        {
            this.timeStampClause = timeStampClause;
            this.whereClauseTimestampColumn = whereTimestampColumn;
            this.bidfunnelTable = bidfunnelTable;
            this.unqUserTable = unqUserTable;
        }

        public SubQuery(String timeStampClause, String whereTimestampColumn, String unqUserTable)
        {
            this.timeStampClause = timeStampClause;
            this.whereClauseTimestampColumn = whereTimestampColumn;
            this.unqUserTable = unqUserTable;
        }

        public SubQuery(String timeStampClause, String whereTimestampColumn)
        {
            this.timeStampClause = timeStampClause;
            this.whereClauseTimestampColumn = whereTimestampColumn;
        }

        private String timeStampClause;

        private String managedTable;

        private String rtbTable;

        private String audienceTable;

        private String bidfunnelTable;

        private String unqUserTable;

        private String paramConvTable;

        private String whereClauseTimestampColumn;// 1) ts_utc_hourly 2)
                                                  // ts_utc_daily 3)

        // ts_utc_weekly 4) ts_utc_monthly
        private String groupByTimestampColumn;

        public String getFormattedQuery(String rawSubQuery)
        {
            if (StringUtils.isBlank(timeStampClause)) {
                return null;
            }
            String tmp = rawSubQuery;

            if (StringUtils.isNotBlank(managedTable)) {
                tmp = StringUtils.replace(tmp, "managedtable", managedTable);
            }

            if (StringUtils.isNotBlank(rtbTable)) {
                tmp = StringUtils.replace(tmp, "rtbtable", rtbTable);
            }

            if (StringUtils.isNotBlank(audienceTable)) {
                tmp = StringUtils.replace(tmp, "audiencetable", audienceTable);
            }

            if (StringUtils.isNotBlank(bidfunnelTable)) {
                tmp = StringUtils.replace(tmp, "bidfunneltable", bidfunnelTable);
            }

            if (StringUtils.isNotBlank(unqUserTable)) {
                tmp = StringUtils.replace(tmp, "unqusertable", unqUserTable);
            }

            if (StringUtils.isNotBlank(paramConvTable)) {
                tmp = StringUtils.replace(tmp, "paramConvTable", paramConvTable);
            }

            tmp = StringUtils.replace(tmp, "timestampwhereclause", timeStampClause);
            tmp = StringUtils.replace(tmp, "table.whereclausetimestamp", whereClauseTimestampColumn);
            if (StringUtils.isNotBlank(groupByTimestampColumn)) {
                tmp = StringUtils.replace(tmp, " ts AS", " " + groupByTimestampColumn + " AS");
                tmp = StringUtils.replace(tmp, ",ts AS", "," + groupByTimestampColumn + " AS");
            }
            return tmp;

        }

        public void setParamConvTable(String paramConvTable)
        {
            this.paramConvTable = paramConvTable;
        }

    }

}
