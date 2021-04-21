package io.revx.api.reportbuilder.redshift;

import com.google.cloud.bigquery.BigQueryException;
import com.google.cloud.bigquery.TableResult;
import io.micrometer.core.annotation.Timed;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.config.RedshiftConfig;
import io.revx.api.constants.ApiConstant;
import io.revx.api.reportbuilder.ReportBuilder;
import io.revx.api.reportbuilder.ReportBuilderUtil;
import io.revx.core.aop.LogMetrics;
import io.revx.core.cache.ReportBuilderCache;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.enums.reporting.CurrencyOf;
import io.revx.core.enums.reporting.Interval;
import io.revx.core.enums.reporting.OperatorModel;
import io.revx.core.enums.reporting.TableName;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.QueryBuilderException;
import io.revx.core.model.reporting.DurationModel;
import io.revx.core.model.reporting.FilterModel;
import io.revx.core.model.reporting.ReportProperty;
import io.revx.core.model.reporting.ReportingRequest;
import io.revx.core.model.reporting.ReportingResponse;
import io.revx.core.model.reporting.SortModel;
import io.revx.core.utils.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_ENDTIME;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_GROUPBY;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_JOIN;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_OUTERSELECT;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_SELECT;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_STARTTIME;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_TABLE;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_VALUES;
import static io.revx.api.reportbuilder.redshift.ReportConstants.PLACEHOLDER_WHERE;
import static io.revx.core.constant.Constants.AND_OPERATOR;
import static io.revx.core.constant.Constants.COMMA_SEPARATOR;

@Component
public class ReportBuilderImpl implements ReportBuilder {

	private static final Logger logger = LogManager.getLogger(ReportBuilderImpl.class);
	private static final String CACHE_NAME = "REPORT";

	@Autowired
	RedshiftConfig redshiftConfig;

	@Autowired
	ReportBuilderUtil reportBuilderUtil;

	@Autowired
	ReportResponseUtil reportResponseUtil;

	@Autowired
	ApplicationProperties applicationProperties;

	@Autowired
	BigQueryConnectionUtil connection;

	ReportBuilderCache cache = null;

	Map<String, ReportProperty> reportProperties = null;
	TableName tableToQuery = null;

	@Override
	public ReportingResponse fetchReport(ReportingRequest reportingRequest) throws QueryBuilderException {
		reportingRequest.setReport_id(reportResponseUtil.generateReportId(reportingRequest));
		ReportingResponse reportingResponse = new ReportingResponse(reportingRequest);
		logger.info("[ReportID: {}] starting fetch report", reportingRequest.getReport_id());

		cache = new ReportBuilderCache(CACHE_NAME);

		modifyColumnsBasedOnCurrency(reportingRequest);

		reportProperties = reportingRequest.getProperties();

		if (reportingRequest.getEntityName().equals(ApiConstant.CONVERSION_REPORT_ENTITY)) {
			tableToQuery = TableName.CONVERSION;
		} else {
			tableToQuery = getTableToQuery(reportingRequest.getDuration(), reportingRequest.getInterval(),
					reportingRequest);
		}
		logger.debug("[ReportID: {}] tableToQuery: {}", reportingRequest.getReport_id(), tableToQuery);

		boolean isNewRequest = checkIsNewRequest(reportingRequest);
		String tempTableName;
		if (isNewRequest) {
			logger.info("[ReportID: {}] New request", reportingRequest.getReport_id());
			tempTableName = reportResponseUtil.generateUniqueTempTableName(reportingRequest);
			makeNewRequest(tempTableName, reportingResponse, reportingRequest);

		} else {
			logger.info("[ReportID: {}] Reading data from cache/temptable", reportingRequest.getReport_id());
			tempTableName = cache.getFromCache(reportingRequest.getReport_id());
			logger.debug("[ReportID: {}] tempTableName: {}", reportingRequest.getReport_id(), tempTableName);

			TableResult queryResult;
			Long rowCount = connection.checkIfTemporaryTableExists(tempTableName);
			if (rowCount != null) {
				logger.info("[ReportID: {}] Temptable found", reportingRequest.getReport_id());
				String querySelectFromTempTable = buildQuerySelectTempTable(reportingRequest, tempTableName);
				logger.info("[ReportID: {}] Select Query : {}", reportingRequest.getReport_id(),
						querySelectFromTempTable);
				queryResult = executeFetchQuery(reportingRequest, querySelectFromTempTable);
				List<Object> results = reportResponseUtil.formatResponse(reportingRequest, reportProperties,
						queryResult);
				reportingResponse.setResults(results);
				reportingResponse.setTotal_results_count(rowCount);
			} else {
				makeNewRequest(tempTableName, reportingResponse, reportingRequest);
			}
		}

		return reportingResponse;
	}

	@Override
	public ReportingResponse fetchReportCSV(ReportingRequest reportingRequest) throws QueryBuilderException {

		logger.info("fetchReportCSV of RedShiftReportBuilder");

		ReportingResponse reportingResponse = new ReportingResponse(reportingRequest);

		modifyColumnsBasedOnCurrency(reportingRequest);

		reportProperties = reportingRequest.getProperties();

		if (reportingRequest.getEntityName().equals(ApiConstant.CONVERSION_REPORT_ENTITY)) {
			tableToQuery = TableName.CONVERSION;
		} else {
			tableToQuery = getTableToQuery(reportingRequest.getDuration(), reportingRequest.getInterval(),
					reportingRequest);
		}
		reportingRequest.setReport_id(reportResponseUtil.generateReportId(reportingRequest));

		String query = buildQuery(reportingRequest,true);
		query = appendSortConditionForExport(reportingRequest, query);
		logger.debug("Advanced/Conversion report export query : {}", query);

		String exportFileName = reportResponseUtil.getExportFileName(reportingRequest);

		String exportTableName = reportResponseUtil.generateUniqueExportTableName(reportingRequest);
		try {
			if (connection.checkIfTemporaryTableExists(exportTableName) == null) {
				connection.saveResultToTable(applicationProperties.getReportingSourceDataSet(), query, exportTableName);
				connection.setExpirationOnTableAndFetchRowCount(exportTableName);
			}
		} catch (BigQueryException | InterruptedException e) {
            logger.error("[ReportID: {}]SQLException: Error while executing query. ERROR: {}",
                    reportingRequest.getReport_id(), ExceptionUtils.getFullStackTrace(e));
            throw new QueryBuilderException(ErrorCode.SQL_QUERY_EXECUTION_FAILED);
		}
		try {
			connection.exportReportToBucket(exportTableName, exportFileName);
		} catch (BigQueryException exception) {
			logger.error("Error while exporting query result with report id : {} ERROR: {}",
					reportingRequest.getReport_id(), ExceptionUtils.getFullStackTrace(exception));
			throw new QueryBuilderException(ErrorCode.RESULT_TOO_LARGE_TO_EXPORT);
		} catch (InterruptedException exception) {
			logger.error("Error while executing exporting table job, ERROR: {}",
					ExceptionUtils.getFullStackTrace(exception));
			throw new QueryBuilderException(ErrorCode.SQL_QUERY_EXECUTION_FAILED);
		}
		reportingResponse.setResults(null);
		reportingResponse.setFileName(exportFileName);
		reportingResponse.setFileDownloadUrl(applicationProperties.getReportDownloadPath() + "/" + exportFileName);

		return reportingResponse;
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING
			+ GraphiteConstants.REPORTING_EXECUTE_QUERY)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING + GraphiteConstants.REPORTING_EXECUTE_QUERY)
	public Long executeCreationQuery(ReportingRequest reportingRequest, String queryString, String tempTableName)
			throws QueryBuilderException {
        Long rowCount;
		try {
            connection.saveResultToTable(applicationProperties.getReportingSourceDataSet(), queryString, tempTableName);
			rowCount = connection.setExpirationOnTableAndFetchRowCount(tempTableName);
            logger.info("[ReportID: {}][INSERT/UPDATE] Rows Updated: {}", reportingRequest.getReport_id(),
                    rowCount);
		} catch (InterruptedException e){
            logger.error("[ReportID: {}]SQLException: Error while executing query. ERROR: {}",
                    reportingRequest.getReport_id(), ExceptionUtils.getFullStackTrace(e));
            throw new QueryBuilderException(ErrorCode.SQL_QUERY_EXECUTION_FAILED);
		}
		return rowCount;
	}

    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING
            + GraphiteConstants.REPORTING_EXECUTE_QUERY)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING + GraphiteConstants.REPORTING_EXECUTE_QUERY)
    public TableResult executeFetchQuery(ReportingRequest reportingRequest, String queryString)
            throws QueryBuilderException {
        TableResult records;
        try {
            records = connection.fetchQueryResult(applicationProperties.getReportingDestinationDataSet(),queryString);
            logger.info("[ReportID: {}][SELECT] QueryResult : {}", reportingRequest.getReport_id(), records);
        } catch (InterruptedException e){
            logger.error("[ReportID: {}]SQLException: Error while executing query. ERROR: {}",
                    reportingRequest.getReport_id(), ExceptionUtils.getFullStackTrace(e));
            throw new QueryBuilderException(ErrorCode.SQL_QUERY_EXECUTION_FAILED);
        }
        return records;
    }

	/**
	 * private methods
	 */

	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING
			+ GraphiteConstants.REPORTING_BUILD_QUERY)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING + GraphiteConstants.REPORTING_BUILD_QUERY)
	private String buildQuery(ReportingRequest reportingRequest, boolean isExport) {

		String query = null;
		if (reportingRequest.getEntityName().equals(ApiConstant.CONVERSION_REPORT_ENTITY)) {
			query = ReportConstants.QUERY_TEMPLATE_CONVERSION;
		} else {
			query = ReportConstants.QUERY_TEMPLATE_RTB;
		}
		query = query.replace(PLACEHOLDER_TABLE, tableToQuery.getTableName());

		List<String> selects = new ArrayList<>();
		List<String> outerSelects = new ArrayList<>();
		List<String> joins = new ArrayList<>();
		List<String> groupByClause = new ArrayList<>();
		List<String> filterConditions = new ArrayList<>();

		List<String> groupBys = reportingRequest.getGroup_by();
		if (groupBys != null && !groupBys.isEmpty()) {
			for (String groupBy : groupBys) {
				addPropertyToList(groupBy, true, isExport, selects, outerSelects, joins, groupByClause);
			}
		}

		List<String> columns = reportingRequest.getColumns();
		if (columns != null && !columns.isEmpty()) {
			for (String column : reportingRequest.getColumns()) {
				addPropertyToList(column, false, isExport, selects, outerSelects, joins, groupByClause);
			}
		}

		addDurationFilter(reportingRequest.getDuration(), filterConditions);

		List<FilterModel> filters = reportingRequest.getFilters();
		if (filters != null && !filters.isEmpty()) {
			for (FilterModel filter : filters) {
				addDimensionFilters(filter, filterConditions);
			}
		}

		String outerSelectStmt = String.join(COMMA_SEPARATOR + " ", outerSelects);
		query = query.replace(PLACEHOLDER_OUTERSELECT, outerSelectStmt);

		String selectStmt = String.join(COMMA_SEPARATOR + " ", selects);
		query = query.replace(PLACEHOLDER_SELECT, selectStmt);

		String joinStmt = String.join(" ", joins);
		query = query.replace(PLACEHOLDER_JOIN, joinStmt);

		String groupByStmt = String.join(COMMA_SEPARATOR + " ", groupByClause);
		query = query.replace(PLACEHOLDER_GROUPBY, groupByStmt);

		String whereStmt = String.join(" " + AND_OPERATOR + " ", filterConditions);
		query = query.replace(PLACEHOLDER_WHERE, whereStmt);

		query = query.replace(PLACEHOLDER_STARTTIME, reportingRequest.getDuration().getStart_timestamp().toString());
		query = query.replace(PLACEHOLDER_ENDTIME, reportingRequest.getDuration().getEnd_timestamp().toString());

		if(reportingRequest.getEntityName().equals(ApiConstant.CONVERSION_REPORT_ENTITY) ) {
			if(reportingRequest.getColumns().contains(ApiConstant.CONVERSION_TIME)) {
				query = query.replace(ReportConstants.PLACEHOLDER_ORDERBY,ApiConstant.CONVERSION_TIME +" DESC");
			}
			else {
				query = query.replace(ReportConstants.PLACEHOLDER_ORDERBY, ApiConstant.AV_ADVERTISER_NAME +" DESC");
			}

		}
		return query;
	}

	private String buildQuerySelectTempTable(ReportingRequest reportingRequest, String tempTableName) {

		String query = ReportConstants.QUERY_TEMPLATE_SELECT_FROM_TEMP_TABLE;
		query = query.replace(PLACEHOLDER_TABLE, tempTableName);

		List<String> orderBys = new ArrayList<>();

		if (reportingRequest.getInterval() == Interval.none) {
			if (reportingRequest.getSort_by() != null && !reportingRequest.getSort_by().isEmpty()) {
				addSortConditions(reportingRequest.getSort_by(), orderBys, reportingRequest.getCurrency_of());
			}
		} else {
			if (reportingRequest.getSort_by() == null || reportingRequest.getSort_by().isEmpty()) {
				orderBys.add(" starttime " + "DESC");
			} else {
				addSortConditions(reportingRequest.getSort_by(), orderBys, reportingRequest.getCurrency_of());
			}
		}

		String orderByStmt = String.join(" " + COMMA_SEPARATOR + " ", orderBys);
		if (!orderByStmt.isEmpty()) {
			query = query + " " + "ORDER BY " + orderByStmt;
		}

		query = query + " " + "LIMIT " + reportingRequest.getPage_size();

		if (reportingRequest.getPage_number() != 1) {
			int offset = (reportingRequest.getPage_number() - 1) * reportingRequest.getPage_size();
			query = query + " " + " OFFSET " + offset;
		}

		return query;
	}

	private void addSortConditions(List<SortModel> sortList, List<String> orderBys, CurrencyOf currencyOf) {
		for (SortModel orderBy : sortList) {
			String modifiedColumnName = reportBuilderUtil.modifyColumnBasedOnCurrency
					(orderBy.getColumn(), currencyOf);
			ReportProperty prop = reportProperties.get(modifiedColumnName);
			if (prop != null) {
				String sort = orderBy.isAscending() ? "ASC" : "DESC";
				orderBys.add(prop.getSortColumnName() + " " + sort);
			}
		}
	}

	private void addSortConditionsForExport(List<SortModel> sortList, List<String> orderBys, CurrencyOf currencyOf) {
		for (SortModel orderBy : sortList) {
			String modifiedColumnName = reportBuilderUtil.modifyColumnBasedOnCurrency
					(orderBy.getColumn(), currencyOf);
			ReportProperty prop = reportProperties.get(modifiedColumnName);
			if (prop != null) {
				String sort = orderBy.isAscending() ? "ASC" : "DESC";
				orderBys.add(prop.getExportSortColumnName() + " " + sort);
			}
		}
	}

	private void makeNewRequest(String tempTableName, ReportingResponse reportingResponse,
								ReportingRequest reportingRequest)
			throws QueryBuilderException {
		boolean isQueryExecutionDone = false;
		int retryCount = 0;
		while (retryCount < ReportConstants.MAX_RETRY_COUNT && !isQueryExecutionDone) {
			String queryCreateTempTable = buildQuery(reportingRequest, false);
			logger.info("[ReportID: {}][Retry: {}] TempTable Query : {}", reportingRequest.getReport_id(), retryCount,
					queryCreateTempTable);

			Long rowCount = connection.checkIfTemporaryTableExists(tempTableName);
			if (rowCount == null) {
				rowCount = executeCreationQuery(reportingRequest, queryCreateTempTable, tempTableName);
			} else {
				rowCount = 0L;
			}
			if (rowCount == null) {
				logger.error("[ReportID: {}]Error while creating temp table. Retrying...",
						reportingRequest.getReport_id());
				retryCount += 1;

			} else {
				String querySelectFromTempTable = buildQuerySelectTempTable(reportingRequest, tempTableName);
				logger.info("[ReportID: {}][Retry: {}] Select Query : {}", reportingRequest.getReport_id(), retryCount,
						querySelectFromTempTable);
				TableResult queryResult = executeFetchQuery(reportingRequest, querySelectFromTempTable);
				List<Object> results = reportResponseUtil.formatResponse(reportingRequest, reportProperties,
						queryResult);
				reportingResponse.setResults(results);
				reportingResponse.setTotal_results_count(rowCount);
				saveToCache(reportingRequest.getReport_id(), tempTableName,
						Integer.parseInt(applicationProperties.getTempTableTimeToLive()));
				isQueryExecutionDone = true;
			}
		}

		if (retryCount == ReportConstants.MAX_RETRY_COUNT) {
			logger.info("[ReportID: {}][Retry: {}] Max retry reached. Failed to fetch report.",
					reportingRequest.getReport_id(), retryCount);
			throw new QueryBuilderException(ErrorCode.SQL_QUERY_EXECUTION_FAILED);
		}
	}

	private String appendSortConditionForExport(ReportingRequest reportingRequest, String query) {

		List<String> orderBys = new ArrayList<>();

		if (reportingRequest.getInterval() == Interval.none) {
			if (reportingRequest.getSort_by() != null && !reportingRequest.getSort_by().isEmpty()) {
				addSortConditionsForExport(reportingRequest.getSort_by(), orderBys,
						reportingRequest.getCurrency_of());
			}
		} else {
			if (reportingRequest.getSort_by() == null || reportingRequest.getSort_by().isEmpty()) {
				orderBys.add(" StartTime " + " DESC ");
			} else {
				addSortConditionsForExport(reportingRequest.getSort_by(), orderBys,
						reportingRequest.getCurrency_of());
			}
		}

		String orderByStmt = String.join(" " + COMMA_SEPARATOR + " ", orderBys);
		if (!orderByStmt.isEmpty()) {
			query = query + " " + "ORDER BY " + orderByStmt;
		}

		return query;
	}

	private boolean checkIsNewRequest(ReportingRequest reportingRequest) {

		String cacheKey = cache.getFromCache(reportingRequest.getReport_id());
		if (StringUtils.isBlank(cacheKey)) {
			return true;
		}

		return false;
	}

	private void saveToCache(String key, String value, int timeToLive) {
		if (!cache.containsKey(key)) {
			cache.addToCache(key, value, timeToLive);
		}
	}

	public TableName getTableToQuery(DurationModel duration, Interval interval, ReportingRequest reportingRequest)
			throws QueryBuilderException {

		TableName tableName = null;

		if (duration.getEnd_timestamp() == null) {
			// has to be today or yesterday
			tableName = TableName.HOURLY;
		} else {

			if (duration.getStart_timestamp() != null && duration.getEnd_timestamp() != null) {
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
				LocalDateTime start = LocalDateTime.ofEpochSecond(duration.getStart_timestamp(), 0, ZoneOffset.UTC);
				LocalDateTime end = LocalDateTime.ofEpochSecond(duration.getEnd_timestamp(), 0, ZoneOffset.UTC);
				LocalDateTime monthAgo = now.minusMonths(1);
				LocalDateTime sixMonthsAgo = now.minusMonths(6);
				LocalDateTime yearAgo = now.minusYears(1);
				boolean isMonthAgo = start.isBefore(monthAgo);
				boolean is6MonthsAgo = start.isBefore(sixMonthsAgo);
				boolean isYearAgo = start.isBefore(yearAgo);

				switch (interval) {
				case none:
					if (!isMonthAgo) {
						tableName = TableName.HOURLY;
					} else if (!is6MonthsAgo){
						tableName = TableName.DAILY;
					} else if (!isYearAgo) {
						tableName = TableName.MONTHLY;
					} else {
						tableName = null;
					}
					break;

				case hourly:
					tableName = TableName.HOURLY;
					validateDuration(now, duration, 1,start,end);
					reportingRequest.setDuration(duration);
					break;

				case daily:
					tableName = TableName.DAILY;
					validateDuration(now, duration, 6,start,end);
					reportingRequest.setDuration(duration);
					break;
				case weekly:
					tableName = TableName.WEEKLY;
					validateDuration(now, duration, 6,start,end);
					reportingRequest.setDuration(duration);
					break;
				case monthly:
					tableName = TableName.MONTHLY;
					break;
				default:
					tableName = TableName.HOURLY;
					break;
				}
			}
		}

		if (tableName == null) {
			logger.debug("Invalid start and end timestamps.");
			throw new QueryBuilderException(ErrorCode.DURATION_START_END_INVALID);
		}

		if (interval != Interval.none) {
			reportingRequest.addGroupBy(tableName.getGroupByProperty(), 0);
		}
		return tableName;

	}

	private void validateDuration (LocalDateTime now, DurationModel duration, int months,
							LocalDateTime start, LocalDateTime end) throws QueryBuilderException{
		LocalDateTime boundaryMonthStart = LocalDateTime
				.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0).minusMonths(months)
				.atOffset(ZoneOffset.UTC).toLocalDateTime();

		if (start.isBefore(boundaryMonthStart) && end.isBefore(boundaryMonthStart)) {
			logger.debug("Invalid start and end timestamps.");
			throw new QueryBuilderException(ErrorCode.DURATION_START_END_INVALID);
		} else if (start.isBefore(boundaryMonthStart) && end.isAfter(boundaryMonthStart)) {
			duration.setStart_timestamp(boundaryMonthStart.toEpochSecond(ZoneOffset.UTC));
		}
	}

	public void addPropertyToList(String column, boolean isGroupBy, boolean isExport, List<String> selects,
			List<String> outerSelects, List<String> joins, List<String> groupBys) {

		if (column != null && !column.isEmpty() && reportProperties != null && reportProperties.containsKey(column)) {
			ReportProperty property = reportProperties.get(column);
			if (property != null) {
				if (property.getSelect() != null && !property.getSelect().isEmpty()) {
					for (String sel : property.getSelect()) {
						if (!selects.contains(sel)) {
							selects.add(sel);
						}
					}
				}

				if (property.getOuterSelect() != null && !property.getOuterSelect().isEmpty()) {
					List<String> finalSelects = isExport ? property.getExportSelect() : property.getOuterSelect();
					for (String sel : finalSelects) {
						if (!outerSelects.contains(sel)) {
							outerSelects.add(sel);
						}
					}
				}

				if (property.getJoin() != null && !property.getJoin().isEmpty()) {
					for (String join : property.getJoin()) {
						if (!joins.contains(join)) {
							joins.add(join);
						}
					}
				}

				if (isGroupBy && !groupBys.contains(property.getColumn())) {
					groupBys.add(property.getColumn());
				}
			}
		}
	}

	private void addDurationFilter(DurationModel duration, List<String> filterConditions) {
		// date range where condition
		if (duration != null) {
			if (duration.getEnd_timestamp() == null) {
				LocalDateTime now = LocalDateTime.now();
				now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), 0);
				LocalDateTime today = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0);
				LocalDateTime yesterday = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0)
						.minusDays(1);

				LocalDate start = LocalDateTime.ofEpochSecond(duration.getStart_timestamp(), 0, ZoneOffset.UTC)
						.toLocalDate();

				if (start.isEqual(today.toLocalDate())) {
					duration.setEnd_timestamp(now.toEpochSecond(ZoneOffset.UTC));
				} else if (start.isEqual(yesterday.toLocalDate())) {
					duration.setEnd_timestamp(today.toEpochSecond(ZoneOffset.UTC));
				}
			}

			filterConditions.add(tableToQuery.getWhereDateProperty() + " >= " + duration.getStart_timestamp());
			filterConditions.add(tableToQuery.getWhereDateProperty() + " < " + duration.getEnd_timestamp());
		}
	}

	private void addDimensionFilters(FilterModel filterModel, List<String> filterConditions) {

		// dimension where condition
		if (filterModel != null) {
			String key = filterModel.getColumn();
			if (reportProperties != null && reportProperties.containsKey(key)) {
				ReportProperty property = reportProperties.get(key);
				if (property != null) {
					String column = property.getColumn();
					OperatorModel operator = filterModel.getOperator();
					Object values = filterModel.getValue();

					String whereCondition = null;
					switch (operator) {
					case eq:
						whereCondition = column + " = " + PLACEHOLDER_VALUES;
						break;
					case not_in:
						whereCondition = column + " " + OperatorModel.not_in.getOperatorName() + " ("
								+ PLACEHOLDER_VALUES + ")";
						break;
					default:
						whereCondition = column + " " + OperatorModel.in.getOperatorName() + " (" + PLACEHOLDER_VALUES
								+ ")";
						break;
					}

					String valueStr = null;
					if (values instanceof List) {
						List<String> val = new ArrayList<>();
						for (Object obj : (List<Object>) filterModel.getValue()) {
							val.add(obj.toString());
						}
						valueStr = String.join(COMMA_SEPARATOR + " ", val);

					} else if (values != null) {
						valueStr = filterModel.getValue().toString();
					}

					if (valueStr != null) {
						whereCondition = whereCondition.replace(PLACEHOLDER_VALUES,
								String.join(COMMA_SEPARATOR + " ", valueStr));
						filterConditions.add(whereCondition);
					}
				}
			}
		}
	}

	private void modifyColumnsBasedOnCurrency(ReportingRequest reportingRequest) {
		CurrencyOf currencyOf = reportingRequest.getCurrency_of();
		List<String> columns = reportingRequest.getColumns();
		columns = reportBuilderUtil.modifyColumnsBasedOnCurrency(columns, currencyOf);
		reportingRequest.setColumns(columns);

		reportingRequest.addGroupBy("licensee");

		if (currencyOf != null) {
			String currencyProp = (currencyOf == CurrencyOf.licensee) ? "licensee_currency" : "advertiser_currency";
			reportingRequest.addColumn(currencyProp);
			reportingRequest.addGroupBy(currencyProp);
		}

		// REVX-568: Removing bid_price as currency conversion is not possible now
		// if possible in future the column "bid_price_currency" should be added to query

		// REVX-381: App Category showing as Undefined in the UI
		if (reportingRequest.getGroup_by().contains("site")) {
			reportingRequest.addColumn("app_rating");
			reportingRequest.addColumn("app_categories");
			reportingRequest.addColumn("app_store_certified");
		}
	}

	public void setReportProperties(Map<String, ReportProperty> reportProperties) {
		this.reportProperties = reportProperties;
	}
}
