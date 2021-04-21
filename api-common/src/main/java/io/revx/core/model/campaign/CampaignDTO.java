package io.revx.core.model.campaign;

import java.math.BigDecimal;
import io.revx.core.model.BaseModel;
import io.revx.core.model.Campaign;

@SuppressWarnings("serial")
public class CampaignDTO extends Campaign {

	private boolean isRetargeting;

	private String objective;

	private BaseModel region;

	private BigDecimal platformMargin;

	private BigDecimal ivsDistribution;

	private Long pricingId;

	private BigDecimal flowRate;

	private Long lifetimeDeliveryCap;

	private BigDecimal dailyBudget;

	private Long dailyDeliveryCap;

	private BigDecimal attributionRatio;

	private Long dailyUserFcap;

	private Long userFcapDuration;

	public BaseModel pixel;

	private BigDecimal cpaTarget;

	private Boolean skadTarget;

	public BaseModel getPixel() {
		return pixel;
	}

	public void setPixel(BaseModel pixel) {
		this.pixel = pixel;
	}

	public boolean isRetargeting() {
		return isRetargeting;
	}

	public void setRetargeting(boolean isRetargeting) {
		this.isRetargeting = isRetargeting;
	}

	public String getObjective() {
		return objective;
	}

	public void setObjective(String objective) {
		this.objective = objective;
	}

	public BaseModel getRegion() {
		return region;
	}

	public void setRegion(BaseModel region) {
		this.region = region;
	}

	public BigDecimal getPlatformMargin() {
		return platformMargin;
	}

	public void setPlatformMargin(BigDecimal platformMargin) {
		this.platformMargin = platformMargin;
	}

	public BigDecimal getIvsDistribution() {
		return ivsDistribution;
	}

	public void setIvsDistribution(BigDecimal ivsDistribution) {
		this.ivsDistribution = ivsDistribution;
	}

	public Long getPricingId() {
		return pricingId;
	}

	public void setPricingId(Long pricingId) {
		this.pricingId = pricingId;
	}

	public BigDecimal getFlowRate() {
		return flowRate;
	}

	public void setFlowRate(BigDecimal flowRate) {
		this.flowRate = flowRate;
	}

	public Long getLifetimeDeliveryCap() {
		return lifetimeDeliveryCap;
	}

	public void setLifetimeDeliveryCap(Long lifetimeDeliveryCap) {
		this.lifetimeDeliveryCap = lifetimeDeliveryCap;
	}

	public BigDecimal getDailyBudget() {
		return dailyBudget;
	}

	public void setDailyBudget(BigDecimal dailyBudget) {
		this.dailyBudget = dailyBudget;
	}

	public Long getDailyDeliveryCap() {
		return dailyDeliveryCap;
	}

	public void setDailyDeliveryCap(Long dailyDeliveryCap) {
		this.dailyDeliveryCap = dailyDeliveryCap;
	}

	public BigDecimal getAttributionRatio() {
		return attributionRatio;
	}

	public void setAttributionRatio(BigDecimal attributionRatio) {
		this.attributionRatio = attributionRatio;
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

    //getter and setter for cpaTarget

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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CampaignDTO [isRetargeting=").append(isRetargeting).append(", objective=").append(objective)
				.append(", region=").append(region).append(", platformMargin=").append(platformMargin)
				.append(", ivsDistribution=").append(ivsDistribution).append(", pricingId=").append(pricingId)
				.append(", flowRate=").append(flowRate).append(", lifetimeDeliveryCap=").append(lifetimeDeliveryCap)
				.append(", dailyBudget=").append(dailyBudget).append(", dailyDeliveryCap=").append(dailyDeliveryCap)
				.append(", attributionRatio=").append(attributionRatio).append(", dailyUserFcap=").append(dailyUserFcap)
				.append(", userFcapDuration=").append(userFcapDuration).append(", skadTarget=").append(skadTarget)
				.append(", pixel=").append(pixel).append("]");
		return builder.toString();
	}

}
