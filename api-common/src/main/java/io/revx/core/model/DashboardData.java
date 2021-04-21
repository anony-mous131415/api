package io.revx.core.model;

import java.math.BigDecimal;

public class DashboardData extends DashboardMetrics {

  public DashboardData(long id, String name) {
    super(id, name);
  }

  public DashboardData() {}

  private static final long serialVersionUID = 1L;

  protected BigDecimal day;

  protected BigDecimal hour;

  protected Long startTimestamp;

  protected Long endTimestamp;

  private CampaignESDTO campaign;

  private Advertiser advertiser;

  private Strategy strategy;

  private Creative creative;

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

  public Long getStartTimestamp() {
    return startTimestamp;
  }

  public void setStartTimestamp(Long startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public Long getEndTimestamp() {
    return endTimestamp;
  }

  public void setEndTimestamp(Long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  public CampaignESDTO getCampaign() {
    return campaign;
  }

  public void setCampaign(CampaignESDTO campaign) {
    this.campaign = campaign;
  }

  public Advertiser getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(Advertiser advertiserPojo) {
    this.advertiser = advertiserPojo;
  }

  public Strategy getStrategy() {
    return strategy;
  }

  public void setStrategy(Strategy strategy) {
    this.strategy = strategy;
  }

  public Creative getCreative() {
    return creative;
  }

  public void setCreative(Creative creative) {
    this.creative = creative;
  }

  // This method is for Status Attribute Filter.Status
  public boolean isActive() {
    if (this.creative != null) {
      return this.creative.isActive();
    } else if (this.strategy != null) {
      return this.strategy.isActive();
    } else if (this.campaign != null) {
      return this.campaign.isActive();
    } else if (this.advertiser != null) {
      return this.advertiser.isActive();
    }
    return false;
  }

}
