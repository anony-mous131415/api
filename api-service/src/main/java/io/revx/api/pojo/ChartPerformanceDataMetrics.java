package io.revx.api.pojo;

import java.math.BigDecimal;

public class ChartPerformanceDataMetrics extends PerformanceDataMetrics {

  private static final long serialVersionUID = 1L;

  private BigDecimal day;
  private BigDecimal hour;

  public BigDecimal getDay() {
    return day;
  }

  public void setDay(BigDecimal day) {
    this.day = day;
  }

  public BigDecimal getHour() {
    return hour;
  }

  public void setHour(BigDecimal hour) {
    this.hour = hour;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ChartPerformanceDataMatrix [day=");
    builder.append(day);
    builder.append(", hour=");
    builder.append(hour);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }

}
