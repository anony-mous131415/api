package io.revx.core.model;

import java.math.BigDecimal;
import io.revx.core.utils.NumberUtils;

public class SlicexData extends DashboardMetrics {

  private static final long serialVersionUID = -6477764647756574919L;

  protected BigDecimal day;

  protected BigDecimal hour;
  
  protected BigDecimal marginPercentage;
  
  protected BigDecimal impPerConversion;

  public SlicexData(BigDecimal day, BigDecimal hour, BigDecimal impressions, BigDecimal clicks,
      BigDecimal viewConversions, BigDecimal clickConversions, BigDecimal impInstalls,
      BigDecimal clickInstalls, BigDecimal revenue, BigDecimal cost, String currencyId) {
    this.day = day;
    this.hour = hour;
    setImpressions(impressions);
    setClicks(clicks);
    setViewConversions(viewConversions);
    setClickConversions(clickConversions);
    setImpInstalls(impInstalls);
    setClickInstalls(clickInstalls);
    setRevenue(revenue);
    setCost(cost);
    setCurrencyId(currencyId);
    super.setCalculatedFields();
    // In case of Slicex we are using absolute margin instead of margin%
    BigDecimal marginNum = NumberUtils.substractBigDecimal(revenue, cost);
    setMargin(marginNum);
    
    this.marginPercentage = NumberUtils.divide(NumberUtils.multiply(new BigDecimal(100), marginNum), revenue);
    this.impPerConversion = NumberUtils.divide(impressions, NumberUtils.addBigDecimal(clickConversions,viewConversions));
  }

  public SlicexData() {

  }

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
  
  public BigDecimal getMarginPercentage() {
    return marginPercentage;
  }

  public void setMarginPercentage(BigDecimal marginPercentage) {
    this.marginPercentage = marginPercentage;
  }
  
  public BigDecimal getImpPerConversion() {
    return NumberUtils.roundToNDecimalPlace(impPerConversion);
  }

  public void setImpPerConversion(BigDecimal impPerConversion) {
    this.impPerConversion = impPerConversion;
  }
  
  public void makeFieldsNullForReadUser() {
    super.makeFieldsNullForReadUser();
    this.marginPercentage = null;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SlicexData [day=");
    builder.append(day);
    builder.append(", hour=");
    builder.append(hour);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }
}
