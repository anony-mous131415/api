/*
 * @author: ranjan-pritesh
 * 
 * @date: 15jan2020
 */


package io.revx.api.mysql.entity.creative;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class PerformanceDataEntity.
 */
@Entity
@Table(name = "PerformanceData")
public class PerformanceDataEntity {

  /** The timestamp. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "timestamp")
  private Long timestamp;

  /** The licensee id. */
  @Column(name = "licensee_id")
  private Long licenseeId;

  /** The adv id. */
  @Column(name = "adv_id")
  private Long advId;

  /** The io id. */
  @Column(name = "io_id")
  private Long ioId;

  /** The li id. */
  @Column(name = "li_id")
  private int liId;

  /** The io id. */
  @Column(name = "creative_id")
  private Long creativeId;

  /** The entity type. */
  @Column(name = "entity_type")
  private byte entityType;

  /** The impressions. */
  @Column(name = "impressions")
  private long impressions;

  /** The clicks. */
  @Column(name = "clicks")
  private long clicks;

  /** The click conversions. */
  @Column(name = "click_conversions")
  private long clickConversions;

  /** The view conversions. */
  @Column(name = "view_conversions")
  private long viewConversions;

  /** The click installs. */
  @Column(name = "click_installs")
  private long clickInstalls;

  /** The conversion delivery. */
  @Column(name = "conversion_delivery")
  private BigDecimal conversionDelivery;

  /** The revenue in platform currency. */
  @Column(name = "revenue_in_platform_currency")
  private BigDecimal revenueInPlatformCurrency;


  /** The revenue in advertiser currency. */
  @Column(name = "revenue_in_advertiser_currency")
  private BigDecimal revenueInAdvertiserCurrency;

  /** The revenue in licensee currency. */
  @Column(name = "revenue_in_licensee_currency")
  private BigDecimal revenueInLicenseeCurrency;

  /** The cost in advertiser currency. */
  @Column(name = "cost_in_advertiser_currency")
  private BigDecimal costInAdvertiserCurrency;

  /** The cost in licensee currency. */
  @Column(name = "cost_in_licensee_currency")
  private BigDecimal costInLicenseeCurrency;

  /** The cost in platform currency. */
  @Column(name = "cost_in_platform_currency")
  private BigDecimal costInPlatformCurrency;

  /** The imp installs. */
  @Column(name = "imp_installs")
  private int impInstalls;

  /** The txn amount in platform currency. */
  @Column(name = "txn_amount_in_platform_currency")
  private BigDecimal txnAmountInPlatformCurrency;

  /** The tx amount in advertiser currency. */
  @Column(name = "txn_amount_in_advertiser_currency")
  private BigDecimal txAmountInAdvertiserCurrency;

  /** The txn amount in licensee currency. */
  @Column(name = "txn_amount_in_licensee_currency")
  private BigDecimal txnAmountInLicenseeCurrency;

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public Long getAdvId() {
    return advId;
  }

  public void setAdvId(Long advId) {
    this.advId = advId;
  }

  public Long getIoId() {
    return ioId;
  }

  public void setIoId(Long ioId) {
    this.ioId = ioId;
  }

  public int getLiId() {
    return liId;
  }

  public void setLiId(int liId) {
    this.liId = liId;
  }

  public Long getCreativeId() {
    return creativeId;
  }

  public void setCreativeId(Long creativeId) {
    this.creativeId = creativeId;
  }

  public byte getEntityType() {
    return entityType;
  }

  public void setEntityType(byte entityType) {
    this.entityType = entityType;
  }

  public long getImpressions() {
    return impressions;
  }

  public void setImpressions(long impressions) {
    this.impressions = impressions;
  }

  public long getClicks() {
    return clicks;
  }

  public void setClicks(long clicks) {
    this.clicks = clicks;
  }

  public long getClickConversions() {
    return clickConversions;
  }

  public void setClickConversions(long clickConversions) {
    this.clickConversions = clickConversions;
  }

  public long getViewConversions() {
    return viewConversions;
  }

  public void setViewConversions(long viewConversions) {
    this.viewConversions = viewConversions;
  }

  public long getClickInstalls() {
    return clickInstalls;
  }

  public void setClickInstalls(long clickInstalls) {
    this.clickInstalls = clickInstalls;
  }

  public void setClickConversions(int clickConversions) {
    this.clickConversions = clickConversions;
  }

  

  public void setClickInstalls(int clickInstalls) {
    this.clickInstalls = clickInstalls;
  }

  public BigDecimal getConversionDelivery() {
    return conversionDelivery;
  }

  public void setConversionDelivery(BigDecimal conversionDelivery) {
    this.conversionDelivery = conversionDelivery;
  }

  public BigDecimal getRevenueInPlatformCurrency() {
    return revenueInPlatformCurrency;
  }

  public void setRevenueInPlatformCurrency(BigDecimal revenueInPlatformCurrency) {
    this.revenueInPlatformCurrency = revenueInPlatformCurrency;
  }

  public BigDecimal getRevenueInAdvertiserCurrency() {
    return revenueInAdvertiserCurrency;
  }

  public void setRevenueInAdvertiserCurrency(BigDecimal revenueInAdvertiserCurrency) {
    this.revenueInAdvertiserCurrency = revenueInAdvertiserCurrency;
  }

  public BigDecimal getRevenueInLicenseeCurrency() {
    return revenueInLicenseeCurrency;
  }

  public void setRevenueInLicenseeCurrency(BigDecimal revenueInLicenseeCurrency) {
    this.revenueInLicenseeCurrency = revenueInLicenseeCurrency;
  }

  public BigDecimal getCostInAdvertiserCurrency() {
    return costInAdvertiserCurrency;
  }

  public void setCostInAdvertiserCurrency(BigDecimal costInAdvertiserCurrency) {
    this.costInAdvertiserCurrency = costInAdvertiserCurrency;
  }

  public BigDecimal getCostInLicenseeCurrency() {
    return costInLicenseeCurrency;
  }

  public void setCostInLicenseeCurrency(BigDecimal costInLicenseeCurrency) {
    this.costInLicenseeCurrency = costInLicenseeCurrency;
  }

  public BigDecimal getCostInPlatformCurrency() {
    return costInPlatformCurrency;
  }

  public void setCostInPlatformCurrency(BigDecimal costInPlatformCurrency) {
    this.costInPlatformCurrency = costInPlatformCurrency;
  }

  public int getImpInstalls() {
    return impInstalls;
  }

  public void setImpInstalls(int impInstalls) {
    this.impInstalls = impInstalls;
  }

  public BigDecimal getTxnAmountInPlatformCurrency() {
    return txnAmountInPlatformCurrency;
  }

  public void setTxnAmountInPlatformCurrency(BigDecimal txnAmountInPlatformCurrency) {
    this.txnAmountInPlatformCurrency = txnAmountInPlatformCurrency;
  }

  public BigDecimal getTxAmountInAdvertiserCurrency() {
    return txAmountInAdvertiserCurrency;
  }

  public void setTxAmountInAdvertiserCurrency(BigDecimal txAmountInAdvertiserCurrency) {
    this.txAmountInAdvertiserCurrency = txAmountInAdvertiserCurrency;
  }

  public BigDecimal getTxnAmountInLicenseeCurrency() {
    return txnAmountInLicenseeCurrency;
  }

  public void setTxnAmountInLicenseeCurrency(BigDecimal txnAmountInLicenseeCurrency) {
    this.txnAmountInLicenseeCurrency = txnAmountInLicenseeCurrency;
  }

  @Override
  public String toString() {
    return "PerformanceDataEntity [timestamp=" + timestamp + ", licenseeId=" + licenseeId
        + ", advId=" + advId + ", ioId=" + ioId + ", liId=" + liId + ", creativeId=" + creativeId
        + ", entityType=" + entityType + ", impressions=" + impressions + ", clicks=" + clicks
        + ", clickConversions=" + clickConversions + ", viewConversions=" + viewConversions
        + ", clickInstalls=" + clickInstalls + ", conversionDelivery=" + conversionDelivery
        + ", revenueInPlatformCurrency=" + revenueInPlatformCurrency
        + ", revenueInAdvertiserCurrency=" + revenueInAdvertiserCurrency
        + ", revenueInLicenseeCurrency=" + revenueInLicenseeCurrency + ", costInAdvertiserCurrency="
        + costInAdvertiserCurrency + ", costInLicenseeCurrency=" + costInLicenseeCurrency
        + ", costInPlatformCurrency=" + costInPlatformCurrency + ", impInstalls=" + impInstalls
        + ", txnAmountInPlatformCurrency=" + txnAmountInPlatformCurrency
        + ", txAmountInAdvertiserCurrency=" + txAmountInAdvertiserCurrency
        + ", txnAmountInLicenseeCurrency=" + txnAmountInLicenseeCurrency + "]";
  }

}
