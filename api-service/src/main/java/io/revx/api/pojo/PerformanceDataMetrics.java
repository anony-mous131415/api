package io.revx.api.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class PerformanceDataMetrics implements Serializable {

  private static final long serialVersionUID = 1L;

  private BigDecimal impressions;

  private BigDecimal clicks;

  private BigDecimal clickconversions;

  private BigDecimal viewconversions;

  private BigDecimal viewinstalls;

  private BigDecimal clickinstalls;

  private BigDecimal costinadvertisercurrency;

  private BigDecimal costinlicenseecurrency;

  private BigDecimal costinplatformcurrency;

  private BigDecimal revenueinadvertisercurrency;

  private BigDecimal revenueinlicenseecurrency;

  private BigDecimal revenueinplatformcurrency;

  private BigDecimal txnamountinadvertisercurrency;

  private BigDecimal txnamountinlicenseecurrency;

  private BigDecimal txnamountinplatformcurrency;

  private BigDecimal impressionuniqusers;

  private BigDecimal eligibleuniqusers;

  protected BigDecimal eligiblebids;

  protected BigDecimal bidsplaced;

  protected BigDecimal invalidclicks;


  public BigDecimal getImpressions() {
    return impressions;
  }

  public void setImpressions(BigDecimal impressions) {
    this.impressions = impressions;
  }

  public BigDecimal getClicks() {
    return clicks;
  }

  public void setClicks(BigDecimal clicks) {
    this.clicks = clicks;
  }

  public BigDecimal getClickconversions() {
    return clickconversions;
  }

  public void setClickconversions(BigDecimal clickconversions) {
    this.clickconversions = clickconversions;
  }

  public BigDecimal getViewconversions() {
    return viewconversions;
  }

  public void setViewconversions(BigDecimal viewconversions) {
    this.viewconversions = viewconversions;
  }

  public BigDecimal getViewinstalls() {
    return viewinstalls;
  }

  public void setViewinstalls(BigDecimal impinstalls) {
    this.viewinstalls = impinstalls;
  }

  public BigDecimal getClickinstalls() {
    return clickinstalls;
  }

  public void setClickinstalls(BigDecimal clickinstalls) {
    this.clickinstalls = clickinstalls;
  }

  public BigDecimal getCostinadvertisercurrency() {
    return costinadvertisercurrency;
  }

  public void setCostinadvertisercurrency(BigDecimal costinadvertisercurrency) {
    this.costinadvertisercurrency = costinadvertisercurrency;
  }

  public BigDecimal getCostinlicenseecurrency() {
    return costinlicenseecurrency;
  }

  public void setCostinlicenseecurrency(BigDecimal costinlicenseecurrency) {
    this.costinlicenseecurrency = costinlicenseecurrency;
  }

  public BigDecimal getCostinplatformcurrency() {
    return costinplatformcurrency;
  }

  public void setCostinplatformcurrency(BigDecimal costpnplatformcurrency) {
    this.costinplatformcurrency = costpnplatformcurrency;
  }

  public BigDecimal getRevenueinadvertisercurrency() {
    return revenueinadvertisercurrency;
  }

  public void setRevenueinadvertisercurrency(BigDecimal revenueinadvertisercurrency) {
    this.revenueinadvertisercurrency = revenueinadvertisercurrency;
  }

  public BigDecimal getRevenueinlicenseecurrency() {
    return revenueinlicenseecurrency;
  }

  public void setRevenueinlicenseecurrency(BigDecimal revenueinlicenseecurrency) {
    this.revenueinlicenseecurrency = revenueinlicenseecurrency;
  }

  public BigDecimal getRevenueinplatformcurrency() {
    return revenueinplatformcurrency;
  }

  public void setRevenueinplatformcurrency(BigDecimal revenueinplatformcurrency) {
    this.revenueinplatformcurrency = revenueinplatformcurrency;
  }

  public BigDecimal getTxnamountinadvertisercurrency() {
    return txnamountinadvertisercurrency;
  }

  public void setTxnamountinadvertisercurrency(BigDecimal txnamountinadvertisercurrency) {
    this.txnamountinadvertisercurrency = txnamountinadvertisercurrency;
  }

  public BigDecimal getTxnamountinlicenseecurrency() {
    return txnamountinlicenseecurrency;
  }

  public void setTxnamountinlicenseecurrency(BigDecimal txnamountinlicenseecurrency) {
    this.txnamountinlicenseecurrency = txnamountinlicenseecurrency;
  }

  public BigDecimal getTxnamountinplatformcurrency() {
    return txnamountinplatformcurrency;
  }

  public void setTxnamountinplatformcurrency(BigDecimal txnamountinplatformcurrency) {
    this.txnamountinplatformcurrency = txnamountinplatformcurrency;
  }

  public BigDecimal getImpressionuniqusers() {
    return impressionuniqusers;
  }

  public void setImpressionuniqusers(BigDecimal impressionuniqusers) {
    this.impressionuniqusers = impressionuniqusers;
  }

  public BigDecimal getEligibleuniqusers() {
    return eligibleuniqusers;
  }

  public void setEligibleuniqusers(BigDecimal eligibleuniqusers) {
    this.eligibleuniqusers = eligibleuniqusers;
  }


  public BigDecimal getEligiblebids() {
    return eligiblebids;
  }

  public void setEligiblebids(BigDecimal eligiblebids) {
    this.eligiblebids = eligiblebids;
  }

  public BigDecimal getBidsplaced() {
    return bidsplaced;
  }

  public void setBidsplaced(BigDecimal bidsplaced) {
    this.bidsplaced = bidsplaced;
  }

  public BigDecimal getInvalidclicks() {
    return invalidclicks;
  }

  public void setInvalidclicks(BigDecimal invalidclicks) {
    this.invalidclicks = invalidclicks;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PerformanceDataMetrics [impressions=");
    builder.append(impressions);
    builder.append(", clicks=");
    builder.append(clicks);
    builder.append(", clickconversions=");
    builder.append(clickconversions);
    builder.append(", viewconversions=");
    builder.append(viewconversions);
    builder.append(", viewinstalls=");
    builder.append(viewinstalls);
    builder.append(", clickinstalls=");
    builder.append(clickinstalls);
    builder.append(", costinadvertisercurrency=");
    builder.append(costinadvertisercurrency);
    builder.append(", costinlicenseecurrency=");
    builder.append(costinlicenseecurrency);
    builder.append(", costinplatformcurrency=");
    builder.append(costinplatformcurrency);
    builder.append(", revenueinadvertisercurrency=");
    builder.append(revenueinadvertisercurrency);
    builder.append(", revenueinlicenseecurrency=");
    builder.append(revenueinlicenseecurrency);
    builder.append(", revenueinplatformcurrency=");
    builder.append(revenueinplatformcurrency);
    builder.append(", txnamountinadvertisercurrency=");
    builder.append(txnamountinadvertisercurrency);
    builder.append(", txnamountinlicenseecurrency=");
    builder.append(txnamountinlicenseecurrency);
    builder.append(", txnamountinplatformcurrency=");
    builder.append(txnamountinplatformcurrency);
    builder.append(", impressionuniqusers=");
    builder.append(impressionuniqusers);
    builder.append(", eligibleuniqusers=");
    builder.append(eligibleuniqusers);
    builder.append(", eligiblebids=");
    builder.append(eligiblebids);
    builder.append(", bidsplaced=");
    builder.append(bidsplaced);
    builder.append(", invalidclicks=");
    builder.append(invalidclicks);
    builder.append("]");
    return builder.toString();
  }

}
