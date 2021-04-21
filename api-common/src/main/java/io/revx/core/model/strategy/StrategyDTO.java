package io.revx.core.model.strategy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.revx.core.model.BaseModel;
import io.revx.core.model.Strategy;
import io.revx.core.model.targetting.AudienceStrDTO;
import io.revx.core.model.targetting.DayPart;
import io.revx.core.model.targetting.RTBAggregators;
import io.revx.core.model.targetting.RTBSites;
import io.revx.core.model.targetting.TargetAppCategories;
import io.revx.core.model.targetting.TargetBrowsers;
import io.revx.core.model.targetting.TargetGeoDTO;
import io.revx.core.model.targetting.TargetMobileDevices;
import io.revx.core.model.targetting.TimeZoneDTO;

public class StrategyDTO extends Strategy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String strategyType;

	public BigDecimal budgetValue;

	public BaseModel pricingType;

	public BigDecimal pricingValue;

	public BaseModel roiTargetType;

	public BigDecimal roiTargetValue;

	public BaseModel deliveryPriority;

	public BaseModel pacingType;

	public BigDecimal pacingBudgetValue;

	public boolean fcapEnabled;

	public Long fcapInterval;

	public Long fcapFrequency;

	public boolean isNative;

	// 1-wifi,2-cellular data,3-both
	// ENUM
	public Set<ConnectionType> connectionTypes;

	public List<BaseModel> placements;

	// Indicates if budget is defined by money or imps, clicks, convs.

	public Integer budgetBy;

	// Validate that startDate is in future and endDate is not in past.

	public List<BaseModel> pixels;

	public List<BaseModel> creatives;

	// Managed targeting
	public List<BaseModel> apps;

	public List<BaseModel> appsStrategies;

	public List<BaseModel> sections;

	public List<BaseModel> channels;

	// Brand safety parameters - started
	public TargetAppCategories targetIosCategories;

	public TargetAppCategories targetAndroidCategories;

	public Integer targetAppRatings;

	public boolean targetOnlyPublishedApp;
	// Brand safety ends

	public RTBAggregators rtbAggregators;

	public RTBSites rtbSites;

	// Common targeting
	public TargetGeoDTO targetGeographies;

	public String oldGeoTargeting;

	public DayPart targetDays;

	// only App audience will be saved here
	public AudienceStrDTO targetAppSegments;

	// only Web audience will be saved here
	public AudienceStrDTO targetWebSegments;

	// only DMP audience will be saved here
	public AudienceStrDTO targetDmpSegments;

	// to capture the advance targeting details from the UI
	public DealCategoryDTO targetDealCategory;

	// TODO: To be deleted in future: not used in the API: DEPRECATED
	public TargetBrowsers targetBrowsers;

	public TargetMobileDevices targetMobileDevices;

	// RTB Targeting
	public float bidPercentage;

	public BigDecimal bidCapMax;

	public BigDecimal bidCapMin;

	// Added for timezone changes
	public TimeZoneDTO timezone;

	public boolean isEditable;

	public Integer adGroupCount;

	public boolean campaignFcap;

	public AuctionType auctionTypeTargeting;

	public BigDecimal cpaTargetValue;
	
	
	//REVX-501
	// public Boolean isHourlyFcap;
	// public Long hourlyUserFcap;
	// public Integer hourlyFcapDuration;



	public AuctionType getAuctionTypeTargeting() {
		return auctionTypeTargeting;
	}

	public void setAuctionTypeTargeting(AuctionType auctionTypeTargeting) {
		this.auctionTypeTargeting = auctionTypeTargeting;
	}

	public StrategyDTO() {
		this.strategyType = LineItemType.standard.name();
	}

	public String getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(String strategyType) {
		this.strategyType = strategyType;
	}

	public BigDecimal getBudgetValue() {
		return budgetValue;
	}

	public void setBudgetValue(BigDecimal budgetValue) {
		this.budgetValue = budgetValue;
	}

	public BaseModel getPricingType() {
		return pricingType;
	}

	public void setPricingType(BaseModel pricingType) {
		this.pricingType = pricingType;
	}

	public BigDecimal getPricingValue() {
		return pricingValue;
	}

	public void setPricingValue(BigDecimal pricingValue) {
		this.pricingValue = pricingValue;
	}

	public BaseModel getRoiTargetType() {
		return roiTargetType;
	}

	public void setRoiTargetType(BaseModel roiTargetType) {
		this.roiTargetType = roiTargetType;
	}

	public BigDecimal getRoiTargetValue() {
		return roiTargetValue;
	}

	public void setRoiTargetValue(BigDecimal roiTargetValue) {
		this.roiTargetValue = roiTargetValue;
	}

	public BaseModel getDeliveryPriority() {
		return deliveryPriority;
	}

	public void setDeliveryPriority(BaseModel deliveryPriority) {
		this.deliveryPriority = deliveryPriority;
	}

	public BaseModel getPacingType() {
		return pacingType;
	}

	public void setPacingType(BaseModel pacingType) {
		this.pacingType = pacingType;
	}

	public BigDecimal getPacingBudgetValue() {
		return pacingBudgetValue;
	}

	public void setPacingBudgetValue(BigDecimal pacingBudgetValue) {
		this.pacingBudgetValue = pacingBudgetValue;
	}

	public boolean isFcapEnabled() {
		return fcapEnabled;
	}

	public void setFcapEnabled(boolean fcapEnabled) {
		this.fcapEnabled = fcapEnabled;
	}

	public Long getFcapInterval() {
		return fcapInterval;
	}

	public void setFcapInterval(Long fcapInterval) {
		this.fcapInterval = fcapInterval;
	}

	public Long getFcapFrequency() {
		return fcapFrequency;
	}

	public void setFcapFrequency(Long fcapFrequency) {
		this.fcapFrequency = fcapFrequency;
	}

	public boolean isNative() {
		return isNative;
	}

	public void setNative(boolean isNative) {
		this.isNative = isNative;
	}

	public Set<ConnectionType> getConnectionTypes() {
		return connectionTypes;
	}

	public void setConnectionTypes(Set<ConnectionType> connectionType) {
		this.connectionTypes = connectionType;
	}

	public void addConnectionType(ConnectionType connectionType) {
		if (this.connectionTypes == null)
			this.connectionTypes = new HashSet<>();
		this.connectionTypes.add(connectionType);
	}

	public List<BaseModel> getPlacements() {
		return placements;
	}

	public void setPlacements(List<BaseModel> placements) {
		this.placements = placements;
	}

	public Integer getBudgetBy() {
		return budgetBy;
	}

	public void setBudgetBy(Integer budgetBy) {
		this.budgetBy = budgetBy;
	}

	public List<BaseModel> getPixels() {
		return pixels;
	}

	public void setPixels(List<BaseModel> pixels) {
		this.pixels = pixels;
	}

	public List<BaseModel> getCreatives() {
		return creatives;
	}

	public void setCreatives(List<BaseModel> creatives) {
		this.creatives = creatives;
	}

	public List<BaseModel> getApps() {
		return apps;
	}

	public void setApps(List<BaseModel> apps) {
		this.apps = apps;
	}

	public List<BaseModel> getAppsStrategies() {
		return appsStrategies;
	}

	public void setAppsStrategies(List<BaseModel> appsStrategies) {
		this.appsStrategies = appsStrategies;
	}

	public List<BaseModel> getSections() {
		return sections;
	}

	public void setSections(List<BaseModel> sections) {
		this.sections = sections;
	}

	public List<BaseModel> getChannels() {
		return channels;
	}

	public void setChannels(List<BaseModel> channels) {
		this.channels = channels;
	}

	public TargetAppCategories getTargetIosCategories() {
		return targetIosCategories;
	}

	public void setTargetIosCategories(TargetAppCategories targetIosCategories) {
		this.targetIosCategories = targetIosCategories;
	}

	public TargetAppCategories getTargetAndroidCategories() {
		return targetAndroidCategories;
	}

	public void setTargetAndroidCategories(TargetAppCategories targetAndroidCategories) {
		this.targetAndroidCategories = targetAndroidCategories;
	}

	public Integer getTargetAppRatings() {
		return targetAppRatings;
	}

	public void setTargetAppRatings(Integer targetAppRatings) {
		this.targetAppRatings = targetAppRatings;
	}

	public boolean isTargetOnlyPublishedApp() {
		return targetOnlyPublishedApp;
	}

	public void setTargetOnlyPublishedApp(boolean targetOnlyPublishedApp) {
		this.targetOnlyPublishedApp = targetOnlyPublishedApp;
	}

	public RTBAggregators getRtbAggregators() {
		return rtbAggregators;
	}

	public void setRtbAggregators(RTBAggregators rtbAggregators) {
		this.rtbAggregators = rtbAggregators;
	}

	public RTBSites getRtbSites() {
		return rtbSites;
	}

	public void setRtbSites(RTBSites rtbSites) {
		this.rtbSites = rtbSites;
	}

	public TargetGeoDTO getTargetGeographies() {
		return targetGeographies;
	}

	public void setTargetGeographies(TargetGeoDTO targetGeographies) {
		this.targetGeographies = targetGeographies;
	}

	public DayPart getTargetDays() {
		return targetDays;
	}

	public void setTargetDays(DayPart targetDays) {
		this.targetDays = targetDays;
	}

	public TargetBrowsers getTargetBrowsers() {
		return targetBrowsers;
	}

	public void setTargetBrowsers(TargetBrowsers targetBrowsers) {
		this.targetBrowsers = targetBrowsers;
	}

	public TargetMobileDevices getTargetMobileDevices() {
		return targetMobileDevices;
	}

	public void setTargetMobileDevices(TargetMobileDevices targetMobileDevices) {
		this.targetMobileDevices = targetMobileDevices;
	}

	public float getBidPercentage() {
		return bidPercentage;
	}

	public void setBidPercentage(float bidPercentage) {
		this.bidPercentage = bidPercentage;
	}

	public BigDecimal getBidCapMax() {
		return bidCapMax;
	}

	public void setBidCapMax(BigDecimal bidCapMax) {
		this.bidCapMax = bidCapMax;
	}

	public BigDecimal getBidCapMin() {
		return bidCapMin;
	}

	public void setBidCapMin(BigDecimal bidCapMin) {
		this.bidCapMin = bidCapMin;
	}

	public TimeZoneDTO getTimezone() {
		return timezone;
	}

	public void setTimezone(TimeZoneDTO timezone) {
		this.timezone = timezone;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public Integer getAdGroupCount() {
		return adGroupCount;
	}

	public void setAdGroupCount(Integer adGroupCount) {
		this.adGroupCount = adGroupCount;
	}

	public boolean isCampaignFcap() {
		return campaignFcap;
	}

	public void setCampaignFcap(boolean campaignFcap) {
		this.campaignFcap = campaignFcap;
	}

	public DealCategoryDTO getTargetDealCategory() {
		return targetDealCategory;
	}

	public void setTargetDealCategory(DealCategoryDTO targetDealCategory) {
		this.targetDealCategory = targetDealCategory;
	}

	public AudienceStrDTO getTargetDmpSegments() {
		return targetDmpSegments;
	}

	public void setTargetDmpSegments(AudienceStrDTO targetDmpSegments) {
		this.targetDmpSegments = targetDmpSegments;
	}

	public AudienceStrDTO getTargetAppSegments() {
		return targetAppSegments;
	}

	public void setTargetAppSegments(AudienceStrDTO targetAppSegments) {
		this.targetAppSegments = targetAppSegments;
	}

	public AudienceStrDTO getTargetWebSegments() {
		return targetWebSegments;
	}

	public void setTargetWebSegments(AudienceStrDTO targetWebSegments) {
		this.targetWebSegments = targetWebSegments;
	}

	// getter and setter for cpaTargetValue
	public BigDecimal getCpaTargetValue() {
		return cpaTargetValue;
	}

	public void setCpaTargetValue(BigDecimal cpaTargetValue) {
		this.cpaTargetValue = cpaTargetValue;
	}
	
	
	// public Boolean getIsHourlyFcap() {
	// 	return isHourlyFcap;
	// }

	// public void setIsHourlyFcap(Boolean isHourlyFcap) {
	// 	this.isHourlyFcap = isHourlyFcap;
	// }

	// public Long getHourlyUserFcap() {
	// 	return hourlyUserFcap;
	// }

	// public void setHourlyUserFcap(Long hourlyUserFcap) {
	// 	this.hourlyUserFcap = hourlyUserFcap;
	// }

	// public Integer getHourlyFcapDuration() {
	// 	return hourlyFcapDuration;
	// }

	// public void setHourlyFcapDuration(Integer hourlyFcapDuration) {
	// 	this.hourlyFcapDuration = hourlyFcapDuration;
	// }

	@Override
	public String toString() {
		return "StrategyDTO [strategyType=" + strategyType + ", budgetValue=" + budgetValue + ", pricingType="
				+ pricingType + ", pricingValue=" + pricingValue + ", roiTargetType=" + roiTargetType
				+ ", roiTargetValue=" + roiTargetValue + ", deliveryPriority=" + deliveryPriority + ", pacingType="
				+ pacingType + ", pacingBudgetValue=" + pacingBudgetValue + ", fcapEnabled=" + fcapEnabled
				+ ", fcapInterval=" + fcapInterval + ", fcapFrequency=" + fcapFrequency + ", isNative=" + isNative
				+ ", connectionTypes=" + connectionTypes + ", placements=" + placements + ", budgetBy=" + budgetBy
				+ ", pixels=" + pixels + ", creatives=" + creatives + ", apps=" + apps + ", appsStrategies="
				+ appsStrategies + ", sections=" + sections + ", channels=" + channels + ", targetIosCategories="
				+ targetIosCategories + ", targetAndroidCategories=" + targetAndroidCategories + ", targetAppRatings="
				+ targetAppRatings + ", targetOnlyPublishedApp=" + targetOnlyPublishedApp + ", rtbAggregators="
				+ rtbAggregators + ", rtbSites=" + rtbSites + ", targetGeographies=" + targetGeographies
				+ ", oldGeoTargeting=" + oldGeoTargeting + ", targetDays=" + targetDays + ", targetAppSegments="
				+ targetAppSegments + ", targetWebSegments=" + targetWebSegments + ", targetDmpSegments="
				+ targetDmpSegments + ", targetDealCategory=" + targetDealCategory + ", targetBrowsers="
				+ targetBrowsers + ", targetMobileDevices=" + targetMobileDevices + ", bidPercentage=" + bidPercentage
				+ ", bidCapMax=" + bidCapMax + ", bidCapMin=" + bidCapMin + ", timezone=" + timezone + ", isEditable="
				+ isEditable + ", adGroupCount=" + adGroupCount + ", campaignFcap=" + campaignFcap 
				// + ", isHourlyFcap=" + isHourlyFcap 
				// + ", hourlyUserFcap=" + hourlyUserFcap 
				// + ", hourlyFcapDuration=" + hourlyFcapDuration 
				+ "]";
	}

	

//	@Override
//	public String toString() {
//		StringBuilder builder = new StringBuilder();
//		builder.append("StrategyDTO [strategyType=").append(strategyType).append(", budgetValue=").append(budgetValue)
//				.append(", pricingType=").append(pricingType).append(", pricingValue=").append(pricingValue)
//				.append(", roiTargetType=").append(roiTargetType).append(", roiTargetValue=").append(roiTargetValue)
//				.append(", deliveryPriority=").append(deliveryPriority).append(", pacingType=").append(pacingType)
//				.append(", pacingBudgetValue=").append(pacingBudgetValue).append(", fcapEnabled=").append(fcapEnabled)
//				.append(", fcapInterval=").append(fcapInterval).append(", fcapFrequency=").append(fcapFrequency)
//				.append(", isNative=").append(isNative).append(", connectionTypes=").append(connectionTypes)
//				.append(", placements=").append(placements).append(", budgetBy=").append(budgetBy).append(", pixels=")
//				.append(pixels).append(", creatives=").append(creatives).append(", apps=").append(apps)
//				.append(", appsStrategies=").append(appsStrategies).append(", sections=").append(sections)
//				.append(", channels=").append(channels).append(", targetIosCategories=").append(targetIosCategories)
//				.append(", targetAndroidCategories=").append(targetAndroidCategories).append(", targetAppRatings=")
//				.append(targetAppRatings).append(", targetOnlyPublishedApp=").append(targetOnlyPublishedApp)
//				.append(", rtbAggregators=").append(rtbAggregators).append(", rtbSites=").append(rtbSites)
//				.append(", targetGeographies=").append(targetGeographies).append(", oldGeoTargeting=")
//				.append(oldGeoTargeting).append(", targetDays=").append(targetDays).append(", targetSegments=")
//				.append(targetSegments).append(", targetBrowsers=").append(targetBrowsers)
//				.append(", targetMobileDevices=").append(targetMobileDevices).append(", bidPercentage=")
//				.append(bidPercentage).append(", bidCapMax=").append(bidCapMax).append(", bidCapMin=").append(bidCapMin)
//				.append(", timezone=").append(timezone).append(", isEditable=").append(isEditable)
//				.append(", adGroupCount=").append(adGroupCount).append(", isCampaignFcap=").append(campaignFcap)
//				.append("]");
//		return builder.toString();
//	}

}
