package io.revx.api.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.revx.api.enums.CampaignObjective;
import io.revx.api.enums.MType;
import io.revx.api.enums.QueryType;
import io.revx.api.enums.SlicexEntity;
import io.revx.api.enums.SlicexInterval;
import io.revx.api.enums.SlicexMetricsEnum;
import io.revx.api.enums.SortOrder;
import io.revx.api.pojo.SlicexFilter;
import io.revx.api.utility.Util;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.SlicexData;
import io.revx.core.model.SlicexGridData;
import io.revx.core.model.requests.Duration;

@Component("eSDataProvider")
public class EsDataProvider {

	@Autowired
	Environment env;

	@Autowired
	EsRestClient esRestClient;

	@Autowired
	LoginUserDetailsService loginService;

	@Value("${elasticsearch.hourly.index:hourlydata_prod}")
	private String HOURLY_INDEX;

	@Value("${elasticsearch.monthly.index:monthlydata_prod}")
	private String MONTHLY_INDEX;

	private static Logger logger = LogManager.getLogger(EsDataProvider.class);

	private static String DATE_COLUMN_NAME = "ts_date";
	private static String HOUR_COLUMN_NAME = "ts_hour";
	private static Integer MAX_ROWS_FOR_GRID = 150; // 150 rows
	private static Integer MAX_ROWS_FOR_GRAPH = 750; // 24*31 in case of HOURLY data

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.DATA_PROVIDER + GraphiteConstants.CHART)
	public List<? extends SlicexData> fetchGraphData(QueryType queryType, Duration duration, Set<SlicexFilter> filters,
			SlicexInterval interval) {
		List<? extends SlicexData> data = null;
		String esQuery;
		try {
			esQuery = generateGraphQuery(queryType, duration, filters, interval);
			String esIndex = "";
			if (interval.equals(SlicexInterval.DAILY)) {
				esIndex = HOURLY_INDEX;
			} else if (interval.equals(SlicexInterval.HOURLY)) {
				esIndex = HOURLY_INDEX;
			} else if (interval.equals(SlicexInterval.MONTHLY)) {
				esIndex = MONTHLY_INDEX;
			}
			String resp = callElasticClient("/" + esIndex + "/_search", esQuery);

			if (resp != null) {
				data = parseResponseForGraph(resp, interval);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching graph data: " + e.getMessage());
		}
		return data;
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.JSON_PARSE + GraphiteConstants.CHART)
	private List<? extends SlicexData> parseResponseForGraph(String resp, SlicexInterval interval) {
		ArrayList<SlicexData> chartRowList = new ArrayList<SlicexData>();
		JsonObject jsonObj = new Gson().fromJson(resp, JsonObject.class);
		// JsonObject(resp);
		jsonObj = jsonObj.getAsJsonObject("aggregations");
		jsonObj = jsonObj.getAsJsonObject("by_entity");
		JsonArray jsonArray = jsonObj.getAsJsonArray("buckets");
		String currencyId = loginService.getAdvertiserCurrencyId();
		if (loginService.isSuperAdminUser()) {
			// Platform level currency
			currencyId = "USD";
		}

		if (loginService.isAdminUser()
				&& (loginService.getAllLicenseeCount() == 0 || loginService.getAllLicenseeCount() > 1)) {
			// if admin user and has more than 1 licensee access then currency should be USD
			currencyId = "USD";
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject doc = jsonArray.get(i).getAsJsonObject();

			BigDecimal clicks = parseMetric(doc, SlicexMetricsEnum.clicks.getEsQueryAlias());
			BigDecimal impressions = parseMetric(doc, SlicexMetricsEnum.impressions.getEsQueryAlias());
			BigDecimal viewConversions = parseMetric(doc, SlicexMetricsEnum.viewConversions.getEsQueryAlias());
			BigDecimal clickConversions = parseMetric(doc, SlicexMetricsEnum.clickConversions.getEsQueryAlias());
			// For all revenue and cost field(ln,adv etc) query alias is same.
			BigDecimal revenue = parseMetric(doc, SlicexMetricsEnum.revenue.getEsQueryAlias());
			BigDecimal cost = parseMetric(doc, SlicexMetricsEnum.cost.getEsQueryAlias());

			BigDecimal impInstalls = parseMetric(doc, SlicexMetricsEnum.impInstalls.getEsQueryAlias());
			BigDecimal clickInstalls = parseMetric(doc, SlicexMetricsEnum.clickInstalls.getEsQueryAlias());

			Long timestamp = doc.get("key").getAsLong();
			if (interval.equals(SlicexInterval.HOURLY)) {
				// In case of ts_hour we are getting key in seconds in epoch
				// in case of ts_date we are getting key in milliseconds in
				// epoch
				timestamp = timestamp * 1000;
			}
			Date date = new Date(timestamp);

			timestamp = timestamp / 1000;
			Long startOfTheDay = timestamp - (timestamp % 86400);

			SimpleDateFormat jdf_hour = new SimpleDateFormat("HH");
			jdf_hour.setTimeZone(TimeZone.getTimeZone("UTC"));
			String hour = jdf_hour.format(date);

			SlicexData row = new SlicexData(new BigDecimal(startOfTheDay), new BigDecimal(Long.parseLong(hour)),
					impressions, clicks, viewConversions, clickConversions, impInstalls, clickInstalls, revenue, cost,
					currencyId);
			// In case of read only user removing cost fields
			removeCostFields(row);
			chartRowList.add(row);
		}

		return chartRowList;
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.DATA_PROVIDER + GraphiteConstants.LIST)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<? extends SlicexData> fetchGridData(QueryType queryType, SlicexEntity entity, Duration duration,
			Set<SlicexFilter> filters, SlicexMetricsEnum sortOn, SortOrder sortOrder, Duration compareToDuration) {
		long gridDataFetchStartTime = System.currentTimeMillis();
		List<? extends SlicexData> gridData = null;
		try {

			String esQuery = generateGridQuery(duration, entity, filters, sortOn, sortOrder);
			logger.info("Grid query: " + esQuery);

			gridData = fetchAndParseGridData(esQuery, duration, entity);

			if (compareToDuration != null) {
				// Handling the case of compare
				SlicexFilter extraFilterForCompareValue = new SlicexFilter();
				Set<SlicexFilter> compareToFilters = new HashSet<>();

				for (SlicexFilter filter : filters) {
					if (filter.getEntity() != entity) {
						compareToFilters.add(filter);
					}
				}

				if (gridData != null && gridData.size() > 0) {
					extraFilterForCompareValue.setEntity(entity);
					Set<Long> ids = new HashSet<>();
					for (SlicexGridData gridRow : (List<SlicexGridData>) gridData) {
						ids.add(gridRow.getId());
					}
					extraFilterForCompareValue.setIds(ids);
				}
				// For left join/inner query in case of compare.
				compareToFilters.add(extraFilterForCompareValue);

				String compareToDataJson = getCompareToData(compareToDuration, entity, sortOn, compareToFilters)
						.toString();

				Map<Long, BigDecimal> idCompareToValueMap = new HashMap();
				idCompareToValueMap = parseResponseForGridCompareTo(compareToDataJson, entity, sortOn);

				for (SlicexGridData gridRow : (List<SlicexGridData>) gridData) {
					// we are joining right hand side values manually
					gridRow.setCompareToValue(idCompareToValueMap.get(gridRow.getId()));
				}

			}
			long gridDataFetchEndTime = System.currentTimeMillis();
			logger.info("Fetch grid data time taken for entity: " + entity + ": "
					+ (gridDataFetchEndTime - gridDataFetchStartTime));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching grid data: " + e.getMessage());
		}
		return gridData;

	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.QUERY_GENERATE + GraphiteConstants.CHART)
	private String generateGraphQuery(QueryType queryType, Duration duration, Set<SlicexFilter> filters,
			SlicexInterval interval) {
		String esQuery = "{\"size\":0,__AGGREGATOR_QUERY_, __FILTER_QUERY__}";
		String aggregatorQuery = "";
		String tsColumnName = "";

		if (interval.equals(SlicexInterval.DAILY) || interval.equals(SlicexInterval.MONTHLY)) {
			tsColumnName = DATE_COLUMN_NAME;
		} else if (interval.equals(SlicexInterval.HOURLY)) {
			tsColumnName = HOUR_COLUMN_NAME;
		}
		aggregatorQuery = constructAggregatorsQuery(queryType, tsColumnName, MAX_ROWS_FOR_GRAPH, null,
				SortOrder.asc.toString());

		String filterQuery = constructFilterQuery(duration, filters);
		esQuery = esQuery.replace("__AGGREGATOR_QUERY_", aggregatorQuery).replace("__FILTER_QUERY__", filterQuery);
		logger.info("[GRAPH][generateGraphQuery] Graph query: " + esQuery);
		return esQuery;
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.QUERY_GENERATE + GraphiteConstants.LIST)
	private String generateGridQuery(Duration duration, SlicexEntity entity, Set<SlicexFilter> filters,
			SlicexMetricsEnum sortOn, SortOrder sortOrder) {

		// This method will generate query for grid.
		String esQuery = "{\"size\":0,__AGGREGATOR_QUERY_, __FILTER_QUERY__}";

		String aggregatorQuery = constructAggregatorsQuery(QueryType.slicexList, entity.getEsColumnName(),
				MAX_ROWS_FOR_GRID, sortOn, sortOrder.toString());

		String filterQuery = constructFilterQuery(duration, filters);

		esQuery = esQuery.replace("__AGGREGATOR_QUERY_", aggregatorQuery).replace("__FILTER_QUERY__", filterQuery);

		return esQuery;
	}

	private List<? extends SlicexData> fetchAndParseGridData(String gridEsQuery, Duration duration,
			SlicexEntity entity) {

		List<? extends SlicexData> data = null;
		String esIndex = "";
		if (Util.isMonthlyDataRequired(duration)) {
			esIndex = MONTHLY_INDEX;
		} else {
			esIndex = HOURLY_INDEX;
		}
		try {
			long gridQueryStartTime = System.currentTimeMillis();
			String resp = callElasticClient("/" + esIndex + "/_search", gridEsQuery);
			long gridQueryEndTime = System.currentTimeMillis();
			logger.info("Query time taken for entity " + entity.toString() + " : "
					+ (gridQueryEndTime - gridQueryStartTime));
			if (resp != null) {
				data = parseResponseForGrid(resp, entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching grid data: " + e.getMessage());
		}

		return data;
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.ELASTIC)
	private String callElasticClient(String URL, String queryString) {
		long restCallStart = System.currentTimeMillis();
		logger.info("Rest call start for thread: " + Thread.currentThread().getName() + " :" + restCallStart);
		CompletableFuture<String> response = esRestClient.postRestCall(URL, queryString);
		String responseJson = null;
		try {
			responseJson = response.get();
		} catch (Exception e) {
			logger.error("Could not get result from es: " + e.getMessage());
		}
		long restCallEnd = System.currentTimeMillis();
		logger.info("Rest call end for thread: " + Thread.currentThread().getName() + " :" + restCallEnd);
		logger.info("Rest call time taken for thread: " + Thread.currentThread().getName() + " :"
				+ (restCallEnd - restCallStart));
		return responseJson;
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.JSON_PARSE + GraphiteConstants.LIST)
	private List<? extends SlicexData> parseResponseForGrid(String resp, SlicexEntity entity) {
		// ArrayList<GridRow> gridRowList = new ArrayList<GridRow>();
		List<SlicexGridData> data = new ArrayList<SlicexGridData>();

		JsonObject jsonObj = new Gson().fromJson(resp, JsonObject.class);
		jsonObj = jsonObj.get("aggregations").getAsJsonObject();
		jsonObj = jsonObj.get("by_entity").getAsJsonObject();
		JsonArray jsonArray = jsonObj.getAsJsonArray("buckets");
		Set<Long> ids = new HashSet<Long>();
		String currencyId = loginService.getAdvertiserCurrencyId();
		// REVX-300 : internal user should see revx revenue and cost, which is in USD
		// only
		if (loginService.isSuperAdminUser() || loginService.isInternalUser()) {
			// Platform level currency
			currencyId = "USD";
		}

		if (loginService.isAdminUser()
				&& (loginService.getAllLicenseeCount() == 0 || loginService.getAllLicenseeCount() > 1)) {
			// if admin user and has more than 1 licensee access then currency should be USD
			currencyId = "USD";
		}

		for (int i = 0; i < jsonArray.size(); i++) {
			BigDecimal compareToValue = null;
			Long id = null;
			JsonObject doc = jsonArray.get(i).getAsJsonObject();

			BigDecimal clicks = parseMetric(doc, SlicexMetricsEnum.clicks.getEsQueryAlias());
			BigDecimal impressions = parseMetric(doc, SlicexMetricsEnum.impressions.getEsQueryAlias());
			BigDecimal viewConversions = parseMetric(doc, SlicexMetricsEnum.viewConversions.getEsQueryAlias());
			BigDecimal clickConversions = parseMetric(doc, SlicexMetricsEnum.clickConversions.getEsQueryAlias());
			// For all revenue and cost field(ln,adv etc) query alias is same.
			BigDecimal revenue = parseMetric(doc, SlicexMetricsEnum.revenue.getEsQueryAlias());
			BigDecimal cost = parseMetric(doc, SlicexMetricsEnum.cost.getEsQueryAlias());

			BigDecimal impInstalls = parseMetric(doc, SlicexMetricsEnum.impInstalls.getEsQueryAlias());
			BigDecimal clickInstalls = parseMetric(doc, SlicexMetricsEnum.clickInstalls.getEsQueryAlias());

			id = parseIdFromJson(doc, entity);

			ids.add(id);
			String name = null;
			SlicexGridData row = new SlicexGridData(impressions, clicks, viewConversions, clickConversions, impInstalls,
					clickInstalls, revenue, cost, id, name, currencyId, compareToValue);
			removeCostFields(row);
			data.add(row);
		}
		/*
		 * Fetch and populate name using id
		 */
		populateName(data, new ArrayList<Long>(ids), entity);

		return data;
	}

	private static String constructFilterQuery(Duration duration, Set<SlicexFilter> filters) {

		// String startDate = duration.getStartDate();
		// String endDate = duration.getEndDate();

		Date startDate = new Date(duration.getStartTimeStamp() * 1000);
		Date endDate = new Date(duration.getEndTimeStamp() * 1000);

		String startDateFormatted = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
		String endDateFormatted = new SimpleDateFormat("yyyy-MM-dd").format(endDate);

		String filterQuery = "\"query\":{\"bool\":{__COLUMN_FILTER__ __RANGE_FILTER__}}";

		String columnFilter = "";
		if (filters != null)
			columnFilter = constructColumnFilter(filters);

		// We will put date range filter on column(ts_date) in all interval
		// cases (HOURLY, DAILY, MONTHLY)
		String rangeFilter = "";
		rangeFilter = "\"filter\":{\"range\":{\"" + DATE_COLUMN_NAME
				+ "\":{\"gte\":\"__START_DATE__\",\"lt\":\"__END_DATE__\"}}}";

		rangeFilter = rangeFilter.replace("__START_DATE__", startDateFormatted).replace("__END_DATE__",
				endDateFormatted);

		filterQuery = filterQuery.replace("__COLUMN_FILTER__", columnFilter).replace("__RANGE_FILTER__", rangeFilter);

		return filterQuery;
	}

	@SuppressWarnings("unchecked")
	private static String constructColumnFilter(Set<SlicexFilter> filters) {
		String columnFilters = "\"must\":[__TERM_FILTERS__],";
		String termFilter = "{\"terms\":{\"__COLUMN_NAME__\":[__VALUES__]}}";

		String termFiltersList = "";
		if (filters == null)
			return "";
		int count = 0;

		for (SlicexFilter filter : filters) {
			count++;
			if (filter.getIds() == null || filter.getIds().size() == 0)
				continue;

			String colName = null;
			String values = "";
			colName = filter.getEntity().getEsColumnName();

			if (filter.getEntity() == SlicexEntity.creativeSize) {
				List<String> decodedCreativeSize = new ArrayList<>();
				for (Long creativeSizeId : filter.getIds()) {
					Long[] decodedSize = Util.decode(creativeSizeId);
					decodedCreativeSize.add(decodedSize[0].toString() + 'x' + decodedSize[1].toString());
				}
				
				int i;
				for (i = 0; i < decodedCreativeSize.size() - 1; i++) {
					values += "\"" + decodedCreativeSize.get(i) + "\"" + ", ";
				}
				values += "\"" + decodedCreativeSize.get(i) + "\"";
			} else if (filter.getEntity() == SlicexEntity.campaignObjective) {
				List<String> campObjValues = new ArrayList<String>();
				List<Long> campObjectiveIds = new ArrayList<Long>(filter.getIds());
				if (campObjectiveIds != null && campObjectiveIds.size() > 0) {
					for (Long id : campObjectiveIds) {
						campObjValues.add("\"" + CampaignObjective.getById(id).toString() + "\"");
					}
					values = String.join(",", campObjValues);
				}
			} else {
				values = String.join(", ", filter.getIds().stream().map(Object::toString).collect(Collectors.toList()));
			}

			termFiltersList += termFilter.replace("__COLUMN_NAME__", colName).replace("__VALUES__", values);

			if (count < filters.size()) {
				termFiltersList += ", ";
			}
		}
		if (termFiltersList.length() == 0) {
			return "";
		}
		columnFilters = columnFilters.replace("__TERM_FILTERS__", termFiltersList);
		return columnFilters;
	}

	private String constructAggregatorsQuery(QueryType queryType, String columnName, Integer totalRows,
			SlicexMetricsEnum sortOn, String orderBy) {
		String query = "\"aggs\":{\"by_entity\":{__GROUPBY_ORDERBY_QUERY__,__METRICS_AGG_QUERY__}}";
		String metricsAggQuery = "\"aggs\":{__METRICS_COLUMN_SUM__}";

		String groupbyOrderbyQuery = "";
		String metricsColumnSum = "";
		metricsColumnSum += getMetricsColumnSumQuery();
		if (sortOn != null && sortOn.getType() == MType.derived) {
			// In graph there is no role of sortOn
			// In case of grid we have to pass formula and sort in aggregation
			metricsColumnSum += getDerivedFormulaAndSortJson(sortOn, orderBy);
			// TODO Will fix above line carefully..
		}
		metricsAggQuery = metricsAggQuery.replace("__METRICS_COLUMN_SUM__", metricsColumnSum);

		try {
			switch (queryType) {
			case slicexChart:
				groupbyOrderbyQuery = "\"terms\":{\"field\":\"" + columnName
						+ "\",\"size\":__TOTAL_ROWS____SORT_ORDERBY__}";
				groupbyOrderbyQuery = groupbyOrderbyQuery.replace("__TOTAL_ROWS__", totalRows.toString());
				groupbyOrderbyQuery = groupbyOrderbyQuery.replace("__SORT_ORDERBY__",
						",\"order\":{\"__SORT_ON__\":\"__ORDER_BY__\"}");
				groupbyOrderbyQuery = groupbyOrderbyQuery.replace("__SORT_ON__", "_key").replace("__ORDER_BY__",
						orderBy);
				break;
			case slicexList:
				groupbyOrderbyQuery = "\"terms\":{\"field\":\"__COLUMN_NAME__\",\"size\":__TOTAL_ROWS____SORT_ORDERBY__}";
				groupbyOrderbyQuery = groupbyOrderbyQuery.replace("__COLUMN_NAME__", columnName)
						.replace("__TOTAL_ROWS__", totalRows.toString());

				if (sortOn != null && sortOn.getType() == MType.base) {
					groupbyOrderbyQuery = groupbyOrderbyQuery.replaceAll("__SORT_ORDERBY__",
							",\"order\":{\"__SORT_ON__\":\"__ORDER_BY__\"}");
					groupbyOrderbyQuery = groupbyOrderbyQuery.replaceAll("__SORT_ON__", sortOn.getEsQueryAlias())
							.replace("__ORDER_BY__", orderBy);
				} else {
					// in case of derived we will add sort in bucket level
					groupbyOrderbyQuery = groupbyOrderbyQuery.replaceAll("__SORT_ORDERBY__", "");
				}

				break;
			}
		} catch (Exception e) {
			logger.info("error in datadata for query : " + e.getMessage());
			// TODO Will handle exception or will handle null query string
			return null;
		}

		query = query.replace("__GROUPBY_ORDERBY_QUERY__", groupbyOrderbyQuery).replace("__METRICS_AGG_QUERY__",
				metricsAggQuery);

		return query;

	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.COMPARE_TO_DATA + GraphiteConstants.LIST)
	private StringBuilder getCompareToData(Duration compareToDuration, SlicexEntity entity,
			SlicexMetricsEnum comapreToColumn, Set<SlicexFilter> compareToFilters) {
		StringBuilder resp = new StringBuilder();

		String esQuery = "{\"size\":0,__AGGREGATOR_QUERY_, __FILTER_QUERY__}";

		try {
			// we provide compare to data for sorting column in SliceX
			String aggregatorQuery = constructAggregatorsQueryForCompareTo(entity, MAX_ROWS_FOR_GRID, comapreToColumn);

			String filterQuery = constructFilterQuery(compareToDuration, compareToFilters);
			esQuery = esQuery.replace("__AGGREGATOR_QUERY_", aggregatorQuery).replace("__FILTER_QUERY__", filterQuery);

			String esIndex = "";
			if (Util.isMonthlyDataRequired(compareToDuration)) {
				esIndex = MONTHLY_INDEX;
			} else {
				esIndex = HOURLY_INDEX;
			}

			resp.append(callElasticClient("/" + esIndex + "/_search", esQuery));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resp;
	}

	private String constructAggregatorsQueryForCompareTo(SlicexEntity entity, Integer totalRows,
			SlicexMetricsEnum compareToColumn) {
		String query = "\"aggs\":{\"by_entity\":{__GROUPBY_ORDERBY_QUERY__,__METRICS_AGG_QUERY__}}";
		String metricsAggQuery = "\"aggs\":{__METRICS_COLUMN_SUM__}";

		String groupbyOrderbyQuery = "";
		String metricsColumnSum = "";
		metricsColumnSum += getMetricsColumnSumQuery();

		if (compareToColumn != null && compareToColumn.getType() == MType.derived) {
			// we have to pass formula in aggregation, sorting is not required in case of
			// CompareToData
			metricsColumnSum += getDerivedFormulaAndSortJson(compareToColumn, null);
		}
		metricsAggQuery = metricsAggQuery.replace("__METRICS_COLUMN_SUM__", metricsColumnSum);

		try {
			groupbyOrderbyQuery = "\"terms\":{\"field\":\"__COLUMN_NAME__\",\"size\":__TOTAL_ROWS__}";
			groupbyOrderbyQuery = groupbyOrderbyQuery.replace("__COLUMN_NAME__", entity.getEsColumnName())
					.replace("__TOTAL_ROWS__", totalRows.toString());

		} catch (Exception e) {
			logger.info("error in datadata for query : " + e.getMessage());
			return null;
		}

		query = query.replace("__GROUPBY_ORDERBY_QUERY__", groupbyOrderbyQuery).replace("__METRICS_AGG_QUERY__",
				metricsAggQuery);

		return query;
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.COMPARE_JSON_PARSE + GraphiteConstants.LIST)
	private Map<Long, BigDecimal> parseResponseForGridCompareTo(String resp, SlicexEntity entity,
			SlicexMetricsEnum compareToColumn) {
		Map<Long, BigDecimal> idCompareToValueMap = new HashMap<>();

		JsonObject jsonObj = new Gson().fromJson(resp, JsonObject.class);
		jsonObj = jsonObj.getAsJsonObject("aggregations");
		jsonObj = jsonObj.getAsJsonObject("by_entity");
		JsonArray jsonArray = jsonObj.getAsJsonArray("buckets");

		for (int i = 0; i < jsonArray.size(); i++) {
			BigDecimal compareToValue = null;
			Long id = null;
			JsonObject doc = jsonArray.get(i).getAsJsonObject();

			id = parseIdFromJson(doc, entity);

			JsonObject jsonObjValue = doc.getAsJsonObject(compareToColumn.getEsQueryAlias());

			if (jsonObjValue.get("value").getAsString() == null
					|| jsonObjValue.get("value").getAsString().toLowerCase().equals("null")) {
				compareToValue = null;
			} else {
				compareToValue = new BigDecimal(jsonObjValue.get("value").getAsString());
			}

			idCompareToValueMap.put(id, compareToValue);
		}
		return idCompareToValueMap;
	}

	private static String getDerivedFormulaAndSortJson(SlicexMetricsEnum sortOn, String orderBy) {
		String derivedFormulaAndSortJson = "";
		String derivedFormula = "\"__DERIVED_METRIC__\": {\"bucket_script\": {\"buckets_path\": {__DEPENDENT_VAR_LIST__},\"script\": \"__FORMULA__\"}}";
		String sortJson = "\"final_sort\": {\"bucket_sort\": {\"sort\": [{\"__DERIVED_METRIC__\": {\"order\": \"__ORDERBY__\"}}]}}";

		derivedFormula = derivedFormula.replace("__DERIVED_METRIC__", sortOn.getEsQueryAlias());

		String dependentVars = "";
		// TODO Need to handle this somehow
		for (SlicexMetricsEnum dependentMetric : sortOn.getDependentMetrics()) {
			dependentVars += "\"" + dependentMetric.getEsQueryAlias() + "\"" + ":" + "\""
					+ dependentMetric.getEsQueryAlias() + "\",";
		}

		derivedFormula = derivedFormula
				.replace("__DEPENDENT_VAR_LIST__", dependentVars.substring(0, dependentVars.length() - 1))
				.replace("__FORMULA__", sortOn.getFormula());

		if (orderBy != null) {
			sortJson = sortJson.replace("__DERIVED_METRIC__", sortOn.getEsQueryAlias()).replace("__ORDERBY__", orderBy);
			derivedFormulaAndSortJson = "," + derivedFormula + "," + sortJson;
		} else {
			// in case of compare to query, we are not sorting and sortOn is
			// compare to column.
			derivedFormulaAndSortJson = "," + derivedFormula;
		}

		return derivedFormulaAndSortJson;
	}

	private String getMetricsColumnSumQuery() {
		int count = 0;
		String metricsColumnSum = "";
		String metricsAggUnit = "\"__METRICS__\":{\"sum\":{\"field\":\"__COLUMN_NAME__\"}}";

		List<SlicexMetricsEnum> metrics = new ArrayList<SlicexMetricsEnum>(Arrays.asList(SlicexMetricsEnum.impressions,
				SlicexMetricsEnum.clicks, SlicexMetricsEnum.clickConversions, SlicexMetricsEnum.viewConversions,
				SlicexMetricsEnum.impInstalls, SlicexMetricsEnum.clickInstalls));

		addMoneyMetrics(metrics);

		for (SlicexMetricsEnum m : metrics) {
			count++;
			metricsColumnSum += metricsAggUnit.replace("__METRICS__", m.getEsQueryAlias()).replace("__COLUMN_NAME__",
					m.getEsColumnName());
			if (count < metrics.size()) {
				metricsColumnSum += ",";
			}
		}
		return metricsColumnSum;
	}

	private BigDecimal parseMetric(JsonObject doc, String metrickey) {
		JsonObject obj = doc.getAsJsonObject(metrickey);
		BigDecimal metric = new BigDecimal(obj.get("value").toString());
		return metric;
	}

	private void addMoneyMetrics(List<SlicexMetricsEnum> metrics) {
		// REVX-300 : internal user should see revx revenue and cost
		if (loginService.isSuperAdminUser() || loginService.isInternalUser()) {
			metrics.add(SlicexMetricsEnum.revenue);
			metrics.add(SlicexMetricsEnum.cost);
		} else if (loginService.isAdminUser()
				&& (loginService.getAllLicenseeCount() == 0 || loginService.getAllLicenseeCount() > 1)) {
			metrics.add(SlicexMetricsEnum.revenue_in_pc);
			metrics.add(SlicexMetricsEnum.cost_in_pc);
		} else if (loginService.isAdvertiserLogin()) {
			metrics.add(SlicexMetricsEnum.revenue_in_ac);
			metrics.add(SlicexMetricsEnum.cost_in_ac);
		} else {
			metrics.add(SlicexMetricsEnum.revenue_in_lc);
			metrics.add(SlicexMetricsEnum.cost_in_lc);
		}
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.NAME_POPULATE + GraphiteConstants.LIST)
	private void populateName(List<SlicexGridData> data, List<Long> ids, SlicexEntity entity) {
		if (entity.equals(SlicexEntity.campaignObjective)) {
			for (SlicexGridData singleRow : data) {
				Long id = singleRow.getId();
				String name = CampaignObjective.getById(id).toString();
				singleRow.setName(name);
			}
		} else if (entity.equals(SlicexEntity.creativeSize)) {
			for (SlicexGridData singleRow : data) {
				Long id = singleRow.getId();
				Long[] creativeSize = Util.decode(id);
				Long x = creativeSize[0];
				Long y = creativeSize[1];
				String name = x.toString() + 'x' + y.toString();
				singleRow.setName(name);
			}
		} else {
			// TODO: site data is missing from elastic search
			String queryForIdName = "{\"size\": \"__SIZE__\", \"_source\":[\"id\",\"name\"],\"query\":{\"bool\":{\"filter\":{\"terms\":{\"id\":[__COMMA_SEPA_IDS__]}}}}}";

			String idsForFilter = String.join(", ", ids.stream().map(Object::toString).collect(Collectors.toList()));
			queryForIdName = queryForIdName.replace("__COMMA_SEPA_IDS__", idsForFilter);

			String idsListSize = String.valueOf(ids.size());
			queryForIdName = queryForIdName.replace("__SIZE__", idsListSize);

			String esIndex = entity.getEntityEsIndexName();
			String resp = callElasticClient("/" + esIndex + "/_search", queryForIdName);

			Map<Long, String> idNameMap = parseJsonForIdNameData(resp);
			for (SlicexGridData singleRow : data) {
				Long id = singleRow.getId();
				String name = "ANONYMOUS";
				if (idNameMap.containsKey(id)) {
					name = idNameMap.get(id);
				}
				singleRow.setName(name);
			}
		}
	}

	private Map<Long, String> parseJsonForIdNameData(String resp) {
		Map<Long, String> idNameMap = new HashMap<Long, String>();
		JsonObject jsonObj = new Gson().fromJson(resp, JsonObject.class);
		jsonObj = jsonObj.getAsJsonObject("hits");

		JsonArray jsonArray = jsonObj.getAsJsonArray("hits");
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonObject doc = jsonArray.get(i).getAsJsonObject();

			JsonObject obj = (JsonObject) doc.getAsJsonObject("_source");
			Long id = obj.get("id").getAsLong();
			String name = (obj.get("name") != null && !obj.get("name").isJsonNull()) ? obj.get("name").getAsString()
					: null;
			if (name != null) {
				idNameMap.put(id, name);
			}
		}

		return idNameMap;
	}

	private void removeCostFields(SlicexData row) {
		if (loginService.isReadOnlyUser()) {
			row.makeFieldsNullForReadUser();
		}
	}

	private Long parseIdFromJson(JsonObject doc, SlicexEntity entity) {
		Long id = null;
		if (entity.equals(SlicexEntity.creativeSize)) {
			String idString = doc.get("key").getAsString();
			String[] idStringSplit = idString.split("x");

			Long x = Long.parseLong(idStringSplit[0]);
			Long y = Long.parseLong(idStringSplit[1]);

			id = Util.encode(x, y);
		} else if (entity.equals(SlicexEntity.campaignObjective)) {
			String campObjective = doc.get("key").getAsString();
			id = CampaignObjective.valueOf(campObjective).getId();
		} else {
			id = doc.get("key").getAsLong();
		}
		return id;
	}

	// added by pritesh
	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.DATA_PROVIDER + GraphiteConstants.LIST)
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<? extends SlicexData> fetchHourlyDataForPixel(QueryType queryType, SlicexEntity entity,
			Duration duration, Set<SlicexFilter> filters, SlicexMetricsEnum sortOn, SortOrder sortOrder,
			Duration compareToDuration) {
		long gridDataFetchStartTime = System.currentTimeMillis();
		List<? extends SlicexData> gridData = null;
		try {

			String esQuery = generateGridQuery(duration, entity, filters, sortOn, sortOrder);
			logger.info("Grid query: " + esQuery);

			gridData = fetchAndParseHourlyDataForPixel(esQuery, duration, entity);

			long gridDataFetchEndTime = System.currentTimeMillis();
			logger.info("Fetch grid data time taken for entity: " + entity + ": "
					+ (gridDataFetchEndTime - gridDataFetchStartTime));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching grid data: " + e.getMessage());
		}
		return gridData;

	}

	private List<? extends SlicexData> fetchAndParseHourlyDataForPixel(String gridEsQuery, Duration duration,
			SlicexEntity entity) {

		List<? extends SlicexData> data = null;
		String esIndex = "";
		if (Util.isMonthlyDataRequired(duration)) {
			esIndex = MONTHLY_INDEX;
		} else {
			esIndex = HOURLY_INDEX;
		}
		try {
			long gridQueryStartTime = System.currentTimeMillis();
			String resp = callElasticClient("/" + esIndex + "/_search", gridEsQuery);
			long gridQueryEndTime = System.currentTimeMillis();
			logger.info("Query time taken for entity " + entity.toString() + " : "
					+ (gridQueryEndTime - gridQueryStartTime));
			if (resp != null) {
				data = parseResponseForPixelHourlyData(resp, entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while fetching grid data: " + e.getMessage());
		}

		return data;
	}

	@LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.JSON_PARSE + GraphiteConstants.LIST)
	private List<? extends SlicexData> parseResponseForPixelHourlyData(String resp, SlicexEntity entity) {
		// ArrayList<GridRow> gridRowList = new ArrayList<GridRow>();
		List<SlicexGridData> data = new ArrayList<SlicexGridData>();

		JsonObject jsonObj = new Gson().fromJson(resp, JsonObject.class);
		jsonObj = jsonObj.get("aggregations").getAsJsonObject();
		jsonObj = jsonObj.get("by_entity").getAsJsonObject();
		JsonArray jsonArray = jsonObj.getAsJsonArray("buckets");
		Set<Long> ids = new HashSet<Long>();
		String currencyId = loginService.getAdvertiserCurrencyId();
		if (loginService.isSuperAdminUser()) {
			// Platform level currency
			currencyId = "USD";
		}
		for (int i = 0; i < jsonArray.size(); i++) {
			BigDecimal compareToValue = null;
			Long id = null;
			JsonObject doc = jsonArray.get(i).getAsJsonObject();

			BigDecimal clicks = parseMetric(doc, SlicexMetricsEnum.clicks.getEsQueryAlias());
			BigDecimal impressions = parseMetric(doc, SlicexMetricsEnum.impressions.getEsQueryAlias());
			BigDecimal viewConversions = parseMetric(doc, SlicexMetricsEnum.viewConversions.getEsQueryAlias());
			BigDecimal clickConversions = parseMetric(doc, SlicexMetricsEnum.clickConversions.getEsQueryAlias());
			// For all revenue and cost field(ln,adv etc) query alias is same.
			BigDecimal revenue = parseMetric(doc, SlicexMetricsEnum.revenue.getEsQueryAlias());
			BigDecimal cost = parseMetric(doc, SlicexMetricsEnum.cost.getEsQueryAlias());

			BigDecimal impInstalls = parseMetric(doc, SlicexMetricsEnum.impInstalls.getEsQueryAlias());
			BigDecimal clickInstalls = parseMetric(doc, SlicexMetricsEnum.clickInstalls.getEsQueryAlias());

			id = parseIdFromJson(doc, entity);

			ids.add(id);
			String name = null;
			SlicexGridData row = new SlicexGridData(impressions, clicks, viewConversions, clickConversions, impInstalls,
					clickInstalls, revenue, cost, id, name, currencyId, compareToValue);
			removeCostFields(row);
			data.add(row);
		}

		return data;
	}

}
