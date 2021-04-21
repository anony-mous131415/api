package io.revx.core.model;

import java.math.BigDecimal;

public class CampaignESDTO extends StatusTimeModel {

	private static final long serialVersionUID = 1L;

	private Long licenseeId;

	private Long advertiserId;

	private String currencyCode;

	// To-do : Duplicate(same as lifetimeBudget) Remove this post UI changes
	private BigDecimal budget;

	private BigDecimal dailyBudget;

	private BigDecimal lifetimeBudget;

	// To-do : Duplicate(Same as dailyUserFcap) Remove this post UI changes
	private Integer fcap;

	private Long dailyUserFcap;

	private Long userFcapDuration;

	private Long lifetimeUserFcap;

	private BigDecimal daysDuration;

	private BigDecimal daysElapsed;

	private Long startTime;

	private Long endTime;

	private Long pixelId;

	private Boolean skadTarget;
	
	private BigDecimal cpaTarget; // saving this in elastic search because it will become easier to determine if the "Campaign CPA Target" pop-up should be shown while creating /updating campaign

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

	public BigDecimal getDailyBudget() {
		return dailyBudget;
	}

	public void setDailyBudget(BigDecimal dailyBudget) {
		this.dailyBudget = dailyBudget;
	}

	public BigDecimal getLifetimeBudget() {
		return lifetimeBudget;
	}

	public void setLifetimeBudget(BigDecimal lifetimeBudget) {
		this.lifetimeBudget = lifetimeBudget;
	}

	public Integer getFcap() {
		return fcap;
	}

	public void setFcap(Integer fcap) {
		this.fcap = fcap;
	}

	public Long getDailyUserFcap() {
		return dailyUserFcap;
	}

	public void setDailyUserFcap(Long dailyUserFcap) {
		this.dailyUserFcap = dailyUserFcap;
	}

	public Long getUserFcapDuration() {
		return userFcapDuration;
	}

	public void setUserFcapDuration(Long userFcapDuration) {
		this.userFcapDuration = userFcapDuration;
	}

	public Long getLifetimeUserFcap() {
		return lifetimeUserFcap;
	}

	public void setLifetimeUserFcap(Long lifetimeUserFcap) {
		this.lifetimeUserFcap = lifetimeUserFcap;
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

	public Long getPixelId() {
		return pixelId;
	}

	public void setPixelId(Long pixelId) {
		this.pixelId = pixelId;
	}

	public BigDecimal getCpaTarget() {
		return cpaTarget;
	}

	public void setCpaTarget(BigDecimal cpaTarget) {
		this.cpaTarget = cpaTarget;
	}

	public Boolean getSkadTarget() {
		return skadTarget;
	}

	public void setSkadTarget(Boolean skadTarget) {
		this.skadTarget = skadTarget;
	}
}
