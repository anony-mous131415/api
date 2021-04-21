package io.revx.api.mysql.entity.campaign;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "AdvertiserIO")
public class CampaignEntity implements BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ai_id", nullable = false)
	private Long id;

	@Column(name = "ai_advertiser_id", nullable = false)
	private Long advertiserId;

	@Column(name = "ai_is_active", nullable = false, columnDefinition = "BIT", length = 1)
	private boolean active;

	@Column(name = "ai_system_entry_time")
	private Long creationTime;

	@Column(name = "ai_io_name", nullable = false)
	private String name;

	@Column(name = "ai_is_retargeting", columnDefinition = "BIT", length = 1)
	private boolean isRetargeting;

	@Column(name = "ai_objective")
	private String objective;

	@Column(name = "ai_start_time")
	private Long startTime;

	@Column(name = "ai_end_time")
	private Long endTime;

	@Column(name = "ai_region_id", nullable = false)
	private Long regionId;

	@Column(name = "ai_currency_id", nullable = false)
	private Long currencyId;

	@Column(name = "ai_platform_margin", precision = 5, scale = 4)
	private BigDecimal platformMargin;

	@Column(name = "ai_ivs_distribution", precision = 19, scale = 9)
	private BigDecimal ivsDistribution;

	@Column(name = "ai_modified_by")
	private Long modifiedBy;

	@Column(name = "ai_modified_on")
	private Long modifiedTime;

	@Column(name = "ai_licensee_id")
	private Long licenseeId;

	@Column(name = "ai_created_by")
	private Long createdBy;

	@Column(name = "ai_pricing_id", nullable = false)
	private Long pricingId;

	@Column(name = "ai_flow_rate", nullable = false, precision = 19, scale = 9)
	private BigDecimal flowRate;

	@Column(name = "ai_io_budget", precision = 19, scale = 9)
	private BigDecimal lifetimeBudget;

	@Column(name = "ai_delivery_cap")
	private Long lifetimeDeliveryCap;

	@Column(name = "ai_io_budget_daily", precision = 19, scale = 9)
	private BigDecimal dailyBudget;

	@Column(name = "ai_delivery_cap_daily")
	private Long dailyDeliveryCap;

	@Column(name = "ai_attribution_ratio", precision = 5, scale = 4)
	private BigDecimal attributionRatio;

	@Column(name = "ai_user_fcap")
	private Long dailyUserFcap;

	@Column(name = "ai_user_fcap_duration")
	private Long userFcapDuration;

	@Column(name = "ai_user_fcap_lifetime")
	private Long lifetimeUserFcap;

	@Column(name = "ai_cpa_target", nullable = true, precision = 19, scale = 9)
	private BigDecimal cpaTarget;

	@Column(name = "ai_skadn_target", nullable = false, columnDefinition = "tinyint(1) default 0")
	private Boolean skadTarget;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAdvertiserId() {
		return advertiserId;
	}

	public void setAdvertiserId(Long advertiserId) {
		this.advertiserId = advertiserId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean isActive) {
		this.active = isActive;
	}

	public Long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

	public Long getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Long currencyId) {
		this.currencyId = currencyId;
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

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Long modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Long getLicenseeId() {
		return licenseeId;
	}

	public void setLicenseeId(Long licenseeId) {
		this.licenseeId = licenseeId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
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

	public BigDecimal getLifetimeBudget() {
		return lifetimeBudget;
	}

	public void setLifetimeBudget(BigDecimal lifetimeBudget) {
		this.lifetimeBudget = lifetimeBudget;
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

	public Long getLifetimeUserFcap() {
		return lifetimeUserFcap;
	}

	public void setLifetimeUserFcap(Long lifetimeUserFcap) {
		this.lifetimeUserFcap = lifetimeUserFcap;
	}

	// getter and setter for cpaTarget

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
		return "CampaignEntity [id=" + id + ", advertiserId=" + advertiserId + ", isActive=" + active
				+ ", creationTime=" + creationTime + ", name=" + name + ", isRetargeting=" + isRetargeting
				+ ", objective=" + objective + ", startTime=" + startTime + ", endTime=" + endTime + ", regionId="
				+ regionId + ", currencyId=" + currencyId + ", platformMargin=" + platformMargin + ", ivsDistribution="
				+ ivsDistribution + ", modifiedBy=" + modifiedBy + ", modifiedTime=" + modifiedTime + ", licenseeId="
				+ licenseeId + ", createdBy=" + createdBy + ", pricingId=" + pricingId + ", flowRate=" + flowRate
				+ ", lifetimeBudget=" + lifetimeBudget + ", lifetimeDeliveryCap=" + lifetimeDeliveryCap
				+ ", dailyBudget=" + dailyBudget + ", dailyDeliveryCap=" + dailyDeliveryCap + ", attributionRatio="
				+ attributionRatio + ", dailyUserFcap=" + dailyUserFcap + ", userFcapDuration=" + userFcapDuration
				+ ", lifetimeUserFcap=" + lifetimeUserFcap + ", isSKADTarget" + skadTarget + "]";
	}

}
