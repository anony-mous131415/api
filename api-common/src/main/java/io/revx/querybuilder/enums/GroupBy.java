package io.revx.querybuilder.enums;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public enum GroupBy {

  DAY("day", "day"), HOUR("hour", "hour"), CAMPAIGN_ID("advertiser_io_id", "campaignId"), ADVERTISER_ID(
      "advertiser_id", "advertiserId"), STRATEGY_ID("advertiser_li_id",
          "strategyId"), CREATIVE_ID("creative_id", "creativeId"), NONE("", "");

  private String columnNameInTable;
  private String column;

  private static Map<String, GroupBy> columnMap;

  static {
    columnMap = new HashMap<>();
    for (GroupBy ele : GroupBy.values()) {
      columnMap.put(ele.getColumn(), ele);
    }
  }

  private GroupBy(String text, String column) {
    this.columnNameInTable = text;
    this.column = column;
  }

  public String getColumnNameInTable() {
    return columnNameInTable;
  }

  public String getColumn() {
    return column;

  }

  public static GroupBy fromString(String column) {
    if (StringUtils.isNotBlank(column)) {
      return columnMap.get(column);
    }
    return NONE;
  }

}
