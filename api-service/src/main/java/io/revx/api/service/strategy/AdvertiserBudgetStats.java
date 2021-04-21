package io.revx.api.service.strategy;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author praveen
 */
@Entity
@Table(name = "AdvertiserBudgetStats")
public class AdvertiserBudgetStats implements Serializable {
  /*
   * private static final long serialVersionUID = 1L;
   * 
   * @EmbeddedId protected AdvertiserBudgetStatsPK advertiserBudgetStatsPK;
   * 
   * @Basic(optional = true)
   * 
   * @Column(name = "abs_impressions") private Integer impressionCount;
   * 
   * @Basic(optional = true)
   * 
   * @Column(name = "abs_clicks") private Integer clickCount;
   * 
   * @Basic(optional = true)
   * 
   * @Column(name = "abs_conversions") private Integer conversionCount;
   * 
   * @Basic(optional = true)
   * 
   * @Column(name = "abs_budget_spent", precision = 19, scale = 9) private BigDecimal budgetSpent;
   * 
   * @JoinColumn(name = "abs_adv_li_id", referencedColumnName = "al_id", nullable = false,
   * insertable = false, updatable = false)
   * 
   * @ManyToOne(optional = false, fetch = FetchType.LAZY) private AdvertiserLineItemTemp
   * advertiserLineItem;
   * 
   * @JoinColumn(name = "abs_adv_id", referencedColumnName = "av_id", nullable = false, insertable =
   * false, updatable = false)
   * 
   * @ManyToOne(optional = false, fetch = FetchType.LAZY) private BaseModel advertiser;
   * 
   * @JoinColumn(name = "abs_adv_io_id", referencedColumnName = "ai_id", nullable = false,
   * insertable = false, updatable = false)
   * 
   * @ManyToOne(optional = false, fetch = FetchType.LAZY) private BaseModel advertiserIO;
   * 
   * public AdvertiserBudgetStats() { }
   * 
   * public AdvertiserBudgetStats(AdvertiserBudgetStatsPK advertiserBudgetStatsPK) {
   * this.advertiserBudgetStatsPK = advertiserBudgetStatsPK; }
   * 
   * public AdvertiserBudgetStats(AdvertiserBudgetStatsPK advertiserBudgetStatsPK, Integer
   * impressionCount, Integer clickCount, Integer conversionCount, BigDecimal budgetSpent) {
   * this.advertiserBudgetStatsPK = advertiserBudgetStatsPK; this.impressionCount = impressionCount;
   * this.clickCount = clickCount; this.conversionCount = conversionCount; this.budgetSpent =
   * budgetSpent; }
   * 
   * public AdvertiserBudgetStats(Integer advertiserLineItemId, Long intervalStartTime, boolean
   * isLearning) { this.advertiserBudgetStatsPK = new AdvertiserBudgetStatsPK(advertiserLineItemId,
   * intervalStartTime, isLearning); }
   * 
   * public AdvertiserBudgetStatsPK getAdvertiserBudgetStatsPK() { return advertiserBudgetStatsPK; }
   * 
   * public void setAdvertiserBudgetStatsPK(AdvertiserBudgetStatsPK advertiserBudgetStatsPK) {
   * this.advertiserBudgetStatsPK = advertiserBudgetStatsPK; }
   * 
   * public Integer getImpressionCount() { return impressionCount; }
   * 
   * public void setImpressionCount(Integer impressionCount) { this.impressionCount =
   * impressionCount; }
   * 
   * public Integer getClickCount() { return clickCount; }
   * 
   * public void setClickCount(Integer clickCount) { this.clickCount = clickCount; }
   * 
   * public Integer getConversionCount() { return conversionCount; }
   * 
   * public void setConversionCount(Integer conversionCount) { this.conversionCount =
   * conversionCount; }
   * 
   * public BigDecimal getBudgetSpent() { return budgetSpent; }
   * 
   * public void setBudgetSpent(BigDecimal budgetSpent) { this.budgetSpent = budgetSpent; }
   * 
   * public AdvertiserLineItemTemp getAdvertiserLineItem() { return advertiserLineItem; }
   * 
   * public void setAdvertiserLineItem(AdvertiserLineItemTemp advertiserLineItem) {
   * this.advertiserLineItem = advertiserLineItem; }
   * 
   * public BaseModel getAdvertiser() { return advertiser; }
   * 
   * public void setAdvertiser(BaseModel advertiser) { this.advertiser = advertiser; }
   * 
   * public BaseModel getAdvertiserIO() { return advertiserIO; }
   * 
   * public void setAdvertiserIO(BaseModel advertiserIO) { this.advertiserIO = advertiserIO; }
   * 
   * @Override public int hashCode() { int hash = 0; hash += (advertiserBudgetStatsPK != null ?
   * advertiserBudgetStatsPK.hashCode() : 0); return hash; }
   * 
   * @Override public boolean equals(Object object) { if (!(object instanceof
   * AdvertiserBudgetStats)) { return false; } AdvertiserBudgetStats other = (AdvertiserBudgetStats)
   * object; if ((this.advertiserBudgetStatsPK == null && other.advertiserBudgetStatsPK != null) ||
   * (this.advertiserBudgetStatsPK != null &&
   * !this.advertiserBudgetStatsPK.equals(other.advertiserBudgetStatsPK))) { return false; } return
   * true; }
   * 
   * @Override public String toString() { return "AdvertiserBudgetStats[advertiserBudgetStatsPK=" +
   * advertiserBudgetStatsPK + "]"; }
   * 
   */}
