package io.revx.core.model;

import java.math.BigDecimal;
import java.math.BigInteger;

@SuppressWarnings("serial")
public class Strategy extends StatusTimeModel {

  // To-do : Duplicate Remove this post UI changes

  private long licenseeId;

  protected BaseModel licensee;

  // To-do : Duplicate Remove this post UI changes

  private long advertiserId;

  protected BaseModel advertiser;

  // To-do : Duplicate Remove this post UI changes

  private long campaignId;

  protected BaseModel campaign;

  private String currencyCode;

  private BaseModel currency;

  private BigDecimal budget;

  private Integer fcap;

  private BigDecimal daysDuration;

  private BigDecimal daysElapsed;

  private BigInteger startTime;

  private BigInteger endTime;

  public Strategy(long id, String name) {
    super(id, name);
  }

  public Strategy() {

  }

  public long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public long getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(long campaignId) {
    this.campaignId = campaignId;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public BigDecimal getBudget() {
    return budget;
  }

  public void setBudget(BigDecimal budget) {
    this.budget = budget;
  }

  public Integer getFcap() {
    return fcap;
  }

  public void setFcap(Integer fcap) {
    this.fcap = fcap;
  }

  public BigDecimal getDaysDuration() {
    return daysDuration;
  }

  public void setDaysDuration(BigDecimal daysDuration) {
    this.daysDuration = daysDuration;
  }

  public BigDecimal getDaysElapsed() {
    return daysElapsed;
  }

  public void setDaysElapsed(BigDecimal daysElapsed) {
    this.daysElapsed = daysElapsed;
  }

  public BigInteger getStartTime() {
    return startTime;
  }

  public void setStartTime(BigInteger startTime) {
    this.startTime = startTime;
  }

  public BigInteger getEndTime() {
    return endTime;
  }

  public void setEndTime(BigInteger endTime) {
    this.endTime = endTime;
  }

  public BaseModel getLicensee() {
    return licensee;
  }

  public void setLicensee(BaseModel licensee) {
    if (licensee != null)
      this.licenseeId = licensee.getId();
    this.licensee = licensee;
  }

  public BaseModel getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(BaseModel advertiser) {
    if (advertiser != null)
      this.advertiserId = advertiser.getId();
    this.advertiser = advertiser;
  }

  public BaseModel getCampaign() {
    return campaign;
  }

  public void setCampaign(BaseModel campaign) {
    if (campaign != null)
      this.campaignId = campaign.getId();
    this.campaign = campaign;
  }

  public BaseModel getCurrency() {
    return currency;
  }

  public void setCurrency(BaseModel currency) {
    this.currency = currency;
  }

}
