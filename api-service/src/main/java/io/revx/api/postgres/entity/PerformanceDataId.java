package io.revx.api.postgres.entity;

import java.io.Serializable;

// @Embeddable
@SuppressWarnings("serial")
public class PerformanceDataId implements Serializable {
  /*
   * 
   * private static final long serialVersionUID = 1L;
   * 
   * @Column(name = "adv_id") private int advId;
   * 
   * @Column(name = "click_conversions") private int clickConversions;
   * 
   * @Column(name = "click_installs") private int clickInstalls;
   * 
   * private int clicks;
   * 
   * @Column(name = "cost_in_advertiser_currency") private BigDecimal costInAdvertiserCurrency;
   * 
   * @Column(name = "cost_in_licensee_currency") private BigDecimal costInLicenseeCurrency;
   * 
   * @Column(name = "cost_in_platform_currency") private BigDecimal costInPlatformCurrency;
   * 
   * @Column(name = "entity_type") private byte entityType;
   * 
   * @Column(name = "imp_installs") private int impInstalls;
   * 
   * private int impressions;
   * 
   * @Column(name = "io_id") private int ioId;
   * 
   * @Column(name = "li_id") private int liId;
   * 
   * @Column(name = "licensee_id") private int licenseeId;
   * 
   * @Column(name = "revenue_in_advertiser_currency") private BigDecimal
   * revenueInAdvertiserCurrency;
   * 
   * @Column(name = "revenue_in_licensee_currency") private BigDecimal revenueInLicenseeCurrency;
   * 
   * @Column(name = "revenue_in_platform_currency") private BigDecimal revenueInPlatformCurrency;
   * 
   * private BigInteger timestamp;
   * 
   * @Column(name = "txn_amount_in_advertiser_currency") private BigDecimal
   * txnAmountInAdvertiserCurrency;
   * 
   * @Column(name = "txn_amount_in_licensee_currency") private BigDecimal
   * txnAmountInLicenseeCurrency;
   * 
   * @Column(name = "txn_amount_in_platform_currency") private BigDecimal
   * txnAmountInPlatformCurrency;
   * 
   * @Column(name = "view_conversions") private int viewConversions;
   * 
   * public int getAdvId() { return advId; }
   * 
   * public void setAdvId(int advId) { this.advId = advId; }
   * 
   * public int getClickConversions() { return clickConversions; }
   * 
   * public void setClickConversions(int clickConversions) { this.clickConversions =
   * clickConversions; }
   * 
   * public int getClickInstalls() { return clickInstalls; }
   * 
   * public void setClickInstalls(int clickInstalls) { this.clickInstalls = clickInstalls; }
   * 
   * public int getClicks() { return clicks; }
   * 
   * public void setClicks(int clicks) { this.clicks = clicks; }
   * 
   * public BigDecimal getCostInAdvertiserCurrency() { return costInAdvertiserCurrency; }
   * 
   * public void setCostInAdvertiserCurrency(BigDecimal costInAdvertiserCurrency) {
   * this.costInAdvertiserCurrency = costInAdvertiserCurrency; }
   * 
   * public BigDecimal getCostInLicenseeCurrency() { return costInLicenseeCurrency; }
   * 
   * public void setCostInLicenseeCurrency(BigDecimal costInLicenseeCurrency) {
   * this.costInLicenseeCurrency = costInLicenseeCurrency; }
   * 
   * public BigDecimal getCostInPlatformCurrency() { return costInPlatformCurrency; }
   * 
   * public void setCostInPlatformCurrency(BigDecimal costInPlatformCurrency) {
   * this.costInPlatformCurrency = costInPlatformCurrency; }
   * 
   * public byte getEntityType() { return entityType; }
   * 
   * public void setEntityType(byte entityType) { this.entityType = entityType; }
   * 
   * public int getImpInstalls() { return impInstalls; }
   * 
   * public void setImpInstalls(int impInstalls) { this.impInstalls = impInstalls; }
   * 
   * public int getImpressions() { return impressions; }
   * 
   * public void setImpressions(int impressions) { this.impressions = impressions; }
   * 
   * public int getIoId() { return ioId; }
   * 
   * public void setIoId(int ioId) { this.ioId = ioId; }
   * 
   * public int getLiId() { return liId; }
   * 
   * public void setLiId(int liId) { this.liId = liId; }
   * 
   * public int getLicenseeId() { return licenseeId; }
   * 
   * public void setLicenseeId(int licenseeId) { this.licenseeId = licenseeId; }
   * 
   * public BigDecimal getRevenueInAdvertiserCurrency() { return revenueInAdvertiserCurrency; }
   * 
   * public void setRevenueInAdvertiserCurrency(BigDecimal revenueInAdvertiserCurrency) {
   * this.revenueInAdvertiserCurrency = revenueInAdvertiserCurrency; }
   * 
   * public BigDecimal getRevenueInLicenseeCurrency() { return revenueInLicenseeCurrency; }
   * 
   * public void setRevenueInLicenseeCurrency(BigDecimal revenueInLicenseeCurrency) {
   * this.revenueInLicenseeCurrency = revenueInLicenseeCurrency; }
   * 
   * public BigDecimal getRevenueInPlatformCurrency() { return revenueInPlatformCurrency; }
   * 
   * public void setRevenueInPlatformCurrency(BigDecimal revenueInPlatformCurrency) {
   * this.revenueInPlatformCurrency = revenueInPlatformCurrency; }
   * 
   * public BigInteger getTimestamp() { return timestamp; }
   * 
   * public void setTimestamp(BigInteger timestamp) { this.timestamp = timestamp; }
   * 
   * public BigDecimal getTxnAmountInAdvertiserCurrency() { return txnAmountInAdvertiserCurrency; }
   * 
   * public void setTxnAmountInAdvertiserCurrency(BigDecimal txnAmountInAdvertiserCurrency) {
   * this.txnAmountInAdvertiserCurrency = txnAmountInAdvertiserCurrency; }
   * 
   * public BigDecimal getTxnAmountInLicenseeCurrency() { return txnAmountInLicenseeCurrency; }
   * 
   * public void setTxnAmountInLicenseeCurrency(BigDecimal txnAmountInLicenseeCurrency) {
   * this.txnAmountInLicenseeCurrency = txnAmountInLicenseeCurrency; }
   * 
   * public BigDecimal getTxnAmountInPlatformCurrency() { return txnAmountInPlatformCurrency; }
   * 
   * public void setTxnAmountInPlatformCurrency(BigDecimal txnAmountInPlatformCurrency) {
   * this.txnAmountInPlatformCurrency = txnAmountInPlatformCurrency; }
   * 
   * public int getViewConversions() { return viewConversions; }
   * 
   * public void setViewConversions(int viewConversions) { this.viewConversions = viewConversions; }
   * 
   * @Override public String toString() { StringBuilder builder = new StringBuilder();
   * builder.append("PerformanceDataLIId [advId="); builder.append(advId);
   * builder.append(", clickConversions="); builder.append(clickConversions);
   * builder.append(", clickInstalls="); builder.append(clickInstalls); builder.append(", clicks=");
   * builder.append(clicks); builder.append(", costInAdvertiserCurrency=");
   * builder.append(costInAdvertiserCurrency); builder.append(", costInLicenseeCurrency=");
   * builder.append(costInLicenseeCurrency); builder.append(", costInPlatformCurrency=");
   * builder.append(costInPlatformCurrency); builder.append(", entityType=");
   * builder.append(entityType); builder.append(", impInstalls="); builder.append(impInstalls);
   * builder.append(", impressions="); builder.append(impressions); builder.append(", ioId=");
   * builder.append(ioId); builder.append(", liId="); builder.append(liId);
   * builder.append(", licenseeId="); builder.append(licenseeId);
   * builder.append(", revenueInAdvertiserCurrency="); builder.append(revenueInAdvertiserCurrency);
   * builder.append(", revenueInLicenseeCurrency="); builder.append(revenueInLicenseeCurrency);
   * builder.append(", revenueInPlatformCurrency="); builder.append(revenueInPlatformCurrency);
   * builder.append(", timestamp="); builder.append(timestamp);
   * builder.append(", txnAmountInAdvertiserCurrency=");
   * builder.append(txnAmountInAdvertiserCurrency);
   * builder.append(", txnAmountInLicenseeCurrency="); builder.append(txnAmountInLicenseeCurrency);
   * builder.append(", txnAmountInPlatformCurrency="); builder.append(txnAmountInPlatformCurrency);
   * builder.append(", viewConversions="); builder.append(viewConversions); builder.append("]");
   * return builder.toString(); }
   * 
   */}
