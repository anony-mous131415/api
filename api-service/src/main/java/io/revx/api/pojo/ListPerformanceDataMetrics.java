package io.revx.api.pojo;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ListPerformanceDataMetrics extends PerformanceDataMetrics {

  private static final long serialVersionUID = 1L;

  private BigDecimal day;

  private BigDecimal hour;

  private BigInteger campaignid;

  private BigInteger advertiserid;

  private BigInteger strategyid;

  private BigInteger creativeid;

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

  public BigInteger getCampaignid() {
    return campaignid;
  }

  public void setCampaignid(BigInteger campaignId) {
    this.campaignid = campaignId;
  }

  public BigInteger getAdvertiserid() {
    return advertiserid;
  }

  public void setAdvertiserid(BigInteger advertiserId) {
    this.advertiserid = advertiserId;
  }

  public BigInteger getStrategyid() {
    return strategyid;
  }

  public void setStrategyid(BigInteger strategyId) {
    this.strategyid = strategyId;
  }

  public BigInteger getCreativeid() {
    return creativeid;
  }

  public void setCreativeid(BigInteger creativeId) {
    this.creativeid = creativeId;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ListPerformanceDataMatrix [day=");
    builder.append(day);
    builder.append(", hour=");
    builder.append(hour);
    builder.append(", campaignid=");
    builder.append(campaignid);
    builder.append(", advertiserid=");
    builder.append(advertiserid);
    builder.append(", strategyid=");
    builder.append(strategyid);
    builder.append(", creativeid=");
    builder.append(creativeid);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }

}
