package io.revx.core.model.requests;

import io.revx.querybuilder.objs.FilterComponent;

public class DashboardFilters {

  public String column;
  public String operator;
  public String value;

  public DashboardFilters() {

  }

  public DashboardFilters(String column, String value) {
    this.column = column;
    this.value = value;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }


  public DashboardFilters(FilterComponent fc) {
    this.column = fc.getField().getColumn();
    this.value = fc.getValue();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DashboardFilters [column=");
    builder.append(column);
    builder.append(", value=");
    builder.append(value);
    builder.append("]");
    return builder.toString();
  }

}
