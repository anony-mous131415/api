package io.revx.api.mysql.entity.strategy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.api.service.strategy.ProductSelectionPolicy;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.strategy.LineItemType;



@Entity
@Table(name = "AdvertiserLineItem")
public class StrategyEntity implements BaseEntity, Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "al_id", nullable = false)
  private Long id;

  @Column(name = "al_bid_cap_max_cpm")
  private BigDecimal bidCapMaxCpm;

  @Column(name = "al_bid_cap_min_cpm")
  private BigDecimal bidCapMinCpm;

  @Column(name = "al_created_by")
  private Long createdBy;

  @Column(name = "al_end_time")
  private BigInteger endTime;


  @Column(name = "al_flow_rate")
  private BigDecimal flowRate;

  @Column(name = "al_is_active", nullable = false, columnDefinition = "BIT", length = 1)
  private Boolean active;

  @Column(name = "al_is_cam_fcap", columnDefinition = "BIT", length = 1)
  private boolean campiagnFcap;


  @Column(name = "al_li_budget")
  private BigDecimal liBudget;

  @Column(name = "al_li_name")
  private String name;

  @Column(name = "al_modified_by")
  private Long modifiedBy;

  @Column(name = "al_modified_on")
  private BigInteger modifiedOn;

  @Column(name = "al_pacing_budget_limit")
  private BigDecimal pacingBudgetLimit;

  @Column(name = "al_product_selection_policy", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private ProductSelectionPolicy productSelectionPolicy = ProductSelectionPolicy.PS_DEFAULT;

  @Column(name = "al_roi_target_value")
  private BigDecimal roiTargetValue;

  @Column(name = "al_start_time")
  private BigInteger startTime;

  @Column(name = "al_system_entry_time")
  private BigInteger systemEntryTime;

  @Column(name = "al_targeting_expression")
  private String targetingExpression;

  @Column(name = "al_type")
  @Enumerated(EnumType.STRING)
  private LineItemType type;

  @Column(name = "al_user_fcap")
  private Long userFcap;

  @Column(name = "al_user_fcap_duration")
  private Long userFcapDuration;

  @Column(name = "al_advertiser_id")
  private Long advertiserId;

  @Column(name = "al_advertiser_io_id")
  private Long campianId;

  @Column(name = "al_delivery_priority_id")
  private Integer deliveryPriorityId;

  @Column(name = "al_licensee_id")
  private Long licenseeId;

  @Column(name = "al_pacing_type_id")
  private Integer pacingTypeId;

  @Column(name = "al_roi_target_type_id")
  private Integer roiPricingId;

  @Column(name = "al_pricing_id")
  private Integer pricingId;
  
  
  //REVX-501
  // @Column(name = "al_is_hourly_fcap", columnDefinition = "BIT", length = 1)
  // private boolean isHourlyFcap;
  
  // @Column(name = "al_hourly_user_fcap")
  // private Long hourlyUserFcap;
  
  // @Column(name = "al_hourly_fcap_duration")
  // private Integer hourlyFcapDuration;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BigDecimal getBidCapMaxCpm() {
    return bidCapMaxCpm;
  }

  public void setBidCapMaxCpm(BigDecimal bidCapMaxCpm) {
    this.bidCapMaxCpm = bidCapMaxCpm;
  }

  public BigDecimal getBidCapMinCpm() {
    return bidCapMinCpm;
  }

  public void setBidCapMinCpm(BigDecimal bidCapMinCpm) {
    this.bidCapMinCpm = bidCapMinCpm;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }


  public BigInteger getEndTime() {
    return endTime;
  }

  public void setEndTime(BigInteger endTime) {
    this.endTime = endTime;
  }

  public BigDecimal getFlowRate() {
    return flowRate;
  }

  public void setFlowRate(BigDecimal flowRate) {
    this.flowRate = flowRate;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public boolean isCampiagnFcap() {
    return campiagnFcap;
  }

  public void setCampiagnFcap(boolean campiagnFcap) {
    this.campiagnFcap = campiagnFcap;
  }

  public BigDecimal getLiBudget() {
    return liBudget;
  }

  public void setLiBudget(BigDecimal liBudget) {
    this.liBudget = liBudget;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public BigInteger getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(BigInteger modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public BigDecimal getPacingBudgetLimit() {
    return pacingBudgetLimit;
  }

  public void setPacingBudgetLimit(BigDecimal pacingBudgetLimit) {
    this.pacingBudgetLimit = pacingBudgetLimit;
  }

  public ProductSelectionPolicy getProductSelectionPolicy() {
    return productSelectionPolicy;
  }

  public void setProductSelectionPolicy(ProductSelectionPolicy productSelectionPolicy) {
    this.productSelectionPolicy = productSelectionPolicy;
  }

  public BigDecimal getRoiTargetValue() {
    return roiTargetValue;
  }

  public void setRoiTargetValue(BigDecimal roiTargetValue) {
    this.roiTargetValue = roiTargetValue;
  }

  public BigInteger getStartTime() {
    return startTime;
  }

  public void setStartTime(BigInteger startTime) {
    this.startTime = startTime;
  }


  public BigInteger getSystemEntryTime() {
    return systemEntryTime;
  }

  public void setSystemEntryTime(BigInteger systemEntryTime) {
    this.systemEntryTime = systemEntryTime;
  }

  public String getTargetingExpression() {
    return targetingExpression;
  }

  public void setTargetingExpression(String targetingExpression) {
    this.targetingExpression = targetingExpression;
  }

  public LineItemType getType() {
    return type;
  }

  public void setType(LineItemType type) {
    this.type = type;
  }

  public Long getUserFcap() {
    return userFcap;
  }

  public void setUserFcap(Long userFcap) {
    this.userFcap = userFcap;
  }

  public Long getUserFcapDuration() {
    return userFcapDuration;
  }

  public void setUserFcapDuration(Long userFcapDuration) {
    this.userFcapDuration = userFcapDuration;
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public Long getCampianId() {
    return campianId;
  }

  public void setCampianId(Long campianId) {
    this.campianId = campianId;
  }

  public Integer getDeliveryPriorityId() {
    return deliveryPriorityId;
  }

  public void setDeliveryPriorityId(Integer deliveryPriorityId) {
    this.deliveryPriorityId = deliveryPriorityId;
  }

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public Integer getPacingTypeId() {
    return pacingTypeId;
  }

  public void setPacingTypeId(Integer pacingTypeId) {
    this.pacingTypeId = pacingTypeId;
  }

  public Integer getRoiPricingId() {
    return roiPricingId;
  }

  public void setRoiPricingId(Integer roiPricingId) {
    this.roiPricingId = roiPricingId;
  }

  public Integer getPricingId() {
    return pricingId;
  }

  public void setPricingId(Integer pricingId) {
    this.pricingId = pricingId;
  }

  public float getBudgetSpent() {
    // TODO: Implemrnt This Method
    return 0;
  }
  
  
  // public boolean getIsHourlyFcap() {
	//   return isHourlyFcap;
  // }

  // public void setIsHourlyFcap(boolean isHourlyFcap) {
	//   this.isHourlyFcap = isHourlyFcap;
  // }

  // public Long getHourlyUserFcap() {
	//   return hourlyUserFcap;
  // }

  // public void setHourlyUserFcap(Long hourlyUserFcap) {
	//   this.hourlyUserFcap = hourlyUserFcap;
  // }

  // public Integer getHourlyFcapDuration() {
	//   return hourlyFcapDuration;
  // }

  // public void setHourlyFcapDuration(Integer hourlyFcapDuration) {
	//   this.hourlyFcapDuration = hourlyFcapDuration;
  // }
  

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("StrategyEntity [id=").append(id).append(", bidCapMaxCpm=").append(bidCapMaxCpm)
        .append(", bidCapMinCpm=").append(bidCapMinCpm).append(", createdBy=").append(createdBy)
        .append(", endTime=").append(endTime).append(", flowRate=").append(flowRate)
        .append(", active=").append(active).append(", campiagnFcap=").append(campiagnFcap)
        .append(", liBudget=").append(liBudget).append(", name=").append(name)
        .append(", modifiedBy=").append(modifiedBy).append(", modifiedOn=").append(modifiedOn)
        .append(", pacingBudgetLimit=").append(pacingBudgetLimit)
        .append(", productSelectionPolicy=").append(productSelectionPolicy)
        .append(", roiTargetValue=").append(roiTargetValue).append(", startTime=").append(startTime)
        .append(", systemEntryTime=").append(systemEntryTime).append(", targetingExpression=")
        .append(targetingExpression).append(", type=").append(type).append(", userFcap=")
        .append(userFcap).append(", userFcapDuration=").append(userFcapDuration)
        .append(", advertiserId=").append(advertiserId).append(", campianId=").append(campianId)
        .append(", accountManagerId=").append(", licenseeId=").append(licenseeId)
        .append(", pacingTypeId=").append(pacingTypeId).append(", roiPricingId=")
        .append(roiPricingId).append(", pricingId=").append(pricingId);
        // .append(", isHourlyFcap=").append(isHourlyFcap)
        // .append(", hourlyUserFcap=").append(hourlyUserFcap)
        // .append(", hourlyFcapDuration=").append(hourlyFcapDuration).append("]");

    return builder.toString();
  }





}
