package io.revx.core.enums.reporting;

public enum TableName {

    // base tables
    HOURLY("hourly", "ts_utc_hour", "ts_utc_hour"),
    DAILY("daily", "ts_utc_day", "ts_utc_day"),
    WEEKLY("daily","ts_utc_day","ts_utc_week"),
    MONTHLY("monthly", "ts_utc_month", "ts_utc_month"),
    CONVERSION("parametricconversionreport", "daytimestamp", "daytimestamp");

    String dBTableName;
    String whereDateProperty;
    String groupByProperty;

    TableName(String dBTableName, String whereDateProperty, String groupByProperty) {
        this.dBTableName = dBTableName;
        this.whereDateProperty = whereDateProperty;
        this.groupByProperty = groupByProperty;
    }

    public String getTableName() {
        return dBTableName;
    }

    public String getWhereDateProperty() {
        return whereDateProperty;
    }

    public String getGroupByProperty() {
        return groupByProperty;
    }

}
