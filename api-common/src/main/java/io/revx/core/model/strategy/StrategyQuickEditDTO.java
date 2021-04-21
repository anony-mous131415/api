package io.revx.core.model.strategy;

import java.math.BigDecimal;

//contains subset of feilds of StrategyDTO
public class StrategyQuickEditDTO {

	private Long id;

	private String name;

	private boolean campaignFcap; //entity-> campiagnFcap

	private Long fcapFrequency; //entity-> userFcap

	private BigDecimal pricingValue; //entity-> flowRate

	private Integer pricingType; //entity-> pricingId	

	private BigDecimal bidCapMin; //entity-> bidCapMinCpm

	private BigDecimal bidCapMax; //entity->bidCapMaxCpm

	private Long campaignId; //entity-> campianId

	private LineItemType strategyType; //entity-> type

	private BigDecimal cpaTargetValue;

	//REVX-656
	public Long fcapInterval;

	public StrategyQuickEditDTO(Long id, String name, boolean campaignFcap, Long fcapFrequency, BigDecimal pricingValue,
			Integer pricingType, BigDecimal bidCapMin, BigDecimal bidCapMax, Long campaignId, LineItemType strategyType,
			Long fcapInterval) {
		this.id = id;
		this.name = name;
		this.campaignFcap = campaignFcap;
		this.fcapFrequency = fcapFrequency;
		this.pricingValue = pricingValue;
		this.pricingType = pricingType;
		this.bidCapMin = bidCapMin;
		this.bidCapMax = bidCapMax;
		this.campaignId = campaignId;
		this.strategyType = strategyType;

		//revx-656 : converting minutes to hours
		this.fcapInterval = (fcapInterval / 60);
	}

	public StrategyQuickEditDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCampaignFcap() {
		return campaignFcap;
	}

	public void setCampaignFcap(boolean campaignFcap) {
		this.campaignFcap = campaignFcap;
	}

	public Long getFcapFrequency() {
		return fcapFrequency;
	}

	public void setFcapFrequency(Long fcapFrequency) {
		this.fcapFrequency = fcapFrequency;
	}

	public BigDecimal getPricingValue() {
		return pricingValue;
	}

	public void setPricingValue(BigDecimal pricingValue) {
		this.pricingValue = pricingValue;
	}

	public Integer getPricingType() {
		return pricingType;
	}

	public void setPricingType(Integer pricingType) {
		this.pricingType = pricingType;
	}

	public BigDecimal getBidCapMin() {
		return bidCapMin;
	}

	public void setBidCapMin(BigDecimal bidCapMin) {
		this.bidCapMin = bidCapMin;
	}

	public BigDecimal getBidCapMax() {
		return bidCapMax;
	}

	public void setBidCapMax(BigDecimal bidCapMax) {
		this.bidCapMax = bidCapMax;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public LineItemType getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(LineItemType strategyType) {
		this.strategyType = strategyType;
	}

	public BigDecimal getCpaTargetValue() {
		return cpaTargetValue;
	}

	public void setCpaTargetValue(BigDecimal cpaTargetValue) {
		this.cpaTargetValue = cpaTargetValue;
	}

	//revx-656
	public Long getFcapInterval() {
		return fcapInterval;
	}

	public void setFcapInterval(Long fcapInterval) {
		this.fcapInterval = fcapInterval;
	}

	@Override
	public String toString() {
		return "StrategyQuickEditDTO [id=" + id + ", name=" + name + ", campaignFcap=" + campaignFcap
				+ ", fcapFrequency=" + fcapFrequency + ", pricingValue=" + pricingValue + ", pricingType=" + pricingType
				+ ", bidCapMin=" + bidCapMin + ", bidCapMax=" + bidCapMax + ", campaignId=" + campaignId
				+ ", cpaTargetValue=" + cpaTargetValue + ", fcapInterval=" + fcapInterval + "]";
	}

}
