package io.revx.core.model;

import java.math.BigDecimal;

@SuppressWarnings("serial")
public class Campaign extends StatusTimeModel {

  // To-do : Duplicate(Base model) Remove this post UI changes
  private Long licenseeId;

  private BaseModel licensee;

  // To-do : Duplicate(Base model) Remove this post UI changes
  private Long advertiserId;

  private String currencyCode;

  private BaseModel currency;

  // To-do : Duplicate(Same as daily budget) Remove this post UI changes
  private BigDecimal budget;

  private BigDecimal lifetimeBudget;

  // To-do : Duplicate Remove this post UI changes
  private Integer fcap;

  private Long lifetimeUserFcap;

  private BigDecimal daysDuration;

  private BigDecimal daysElapsed;

  private Long startTime;

  private Long endTime;

  public Campaign(Long id, String name) {
    super(id, name);
  }

  public BigDecimal getLifetimeBudget() {
    return lifetimeBudget;
  }

  public void setLifetimeBudget(BigDecimal lifetimeBudget) {
    this.lifetimeBudget = lifetimeBudget;
  }

  public Long getLifetimeUserFcap() {
    return lifetimeUserFcap;
  }

  public void setLifetimeUserFcap(Long lifetimeUserFcap) {
    this.lifetimeUserFcap = lifetimeUserFcap;
  }

  public BaseModel getLicensee() {
    return licensee;
  }

  public void setLicensee(BaseModel licensee) {
    this.licensee = licensee;
  }

  public BaseModel getCurrency() {
    return currency;
  }

  public void setCurrency(BaseModel currency) {
    this.currency = currency;
  }

  public Campaign() {
    super();
  }


  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
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

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getEndTime() {
    return endTime;
  }

  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }



  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Campaign [licenseeId=").append(licenseeId).append(", licensee=")
        .append(licensee).append(", advertiserId=").append(advertiserId).append(", currencyCode=")
        .append(currencyCode).append(", currency=").append(currency).append(", budget=")
        .append(budget).append(", lifetimeBudget=").append(lifetimeBudget).append(", fcap=")
        .append(fcap).append(", lifetimeUserFcap=").append(lifetimeUserFcap)
        .append(", daysDuration=").append(daysDuration).append(", daysElapsed=").append(daysElapsed)
        .append(", startTime=").append(startTime).append(", endTime=").append(endTime)
        .append(", modifiedTime=").append(modifiedTime).append(", modifiedBy=").append(modifiedBy)
        .append(", id=").append(id).append(", name=").append(name).append("]");
    return builder.toString();
  }

  public float getBudgetSpent() {
    // TODO: Implemrnt This Method
    return 0;
  }


}
