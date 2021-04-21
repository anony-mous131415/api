package io.revx.querybuilder.enums;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import io.revx.core.model.creative.CreativeStatus;

public enum Filter {

	LICENSEE_ID("licensee_id", "licenseeId", "licensee_id",FilterType.TABLE_COLUMN, Long.class),
	CAMPAIGN_ID("io_id", "campaignId", "advertiser_io_id",FilterType.TABLE_COLUMN, Long.class),
	ADVERTISER_ID("adv_id", "advertiserId", "advertiser_id",FilterType.TABLE_COLUMN, Long.class),
	STRATEGY_ID("li_id", "strategyId", "advertiser_li_id",FilterType.TABLE_COLUMN, Long.class),
	CREATIVE_ID("creative_id", "creativeId", "creativeId",FilterType.TABLE_COLUMN, Long.class),
	CRSTATUS("status", "status", "status",FilterType.TABLE_COLUMN, CreativeStatus.class),
	STATUS("active", "active", "active",FilterType.DATA_FIELD, Boolean.class),
	NAME("name", "name", "name",FilterType.DATA_FIELD, String.class),
	ID("id", "id", "id",FilterType.DATA_FIELD, Long.class),
	USER_DATA_TYPE("user_data_type", "user_data_type", "user_data_type",FilterType.DATA_FIELD, String.class),
	AGGREGATOR_ID("id", "aggregator_id", "aggregator_id",FilterType.DATA_FIELD, Long.class),
	AGGREGATOR_TYPE("ragType", "ragType", "ragType",FilterType.DATA_FIELD, Long.class),
	SKAD_TARGET("skad_target","skadTarget","skadn_target",FilterType.DATA_FIELD,Boolean.class),
	CLICK_DESTINATION_ID("click_destination_id","clickDestination","clickDestination",FilterType.DATA_FIELD,Long.class);

	private final String columnNameInTable;
	private final String column;
	private final String bigQueryPerfTableColumnName;
	private final FilterType filterType;
	private final Class<?> valueType;

	private static Map<String, Filter> columnMap;

	static {
		columnMap = new HashMap<>();
		for (Filter ele : Filter.values()) {
			columnMap.put(ele.getColumn(), ele);
		}
	}

	private Filter(String text, String column, String bigQueryColumn,FilterType filterType, Class<?> valueType) {
		this.columnNameInTable = text;
		this.column = column;
		this.filterType = filterType;
		this.valueType = valueType;
		this.bigQueryPerfTableColumnName = bigQueryColumn;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public String getColumnNameInTable() {
		return columnNameInTable;
	}

	public String getColumn() {
		return column;
	}

	public String getBigQueryPerfTableColumnName() {
		return bigQueryPerfTableColumnName;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public static Filter fromString(String column) {
		if (StringUtils.isNotBlank(column)) {
			return columnMap.get(column);
		}
		return null;
	}

}
