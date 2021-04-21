package io.revx.core.model.audience;

import io.revx.core.enums.DurationUnit;
import io.revx.core.model.BaseModel;
import io.revx.core.model.StatusTimeModel;

public class AudienceDTO extends StatusTimeModel {

  private static final long serialVersionUID = 1L;

  private BaseModel licensee;
  
  private BaseModel advertiser;

  private String remoteSegmentId;
  
  private String description;

  //private Boolean active;

  /*
   * NULL -> Rule base/CRM CLICKER -> 1 HASH_BUCKET -> 2 DMP -> 3 PLATFORM -> 4
   */
  private Integer segmentType;

  private RuleDTO ruleExpression;

  private Long duration;

  private DurationUnit durationUnit;

  /*
   * 1 -> MOBILE_APP 2 -> WEB_BROWSING
   */
  private Integer userDataType;

  private Integer dataSourceType;
  
  private PixelDataFileDTO pixelDataFile;

  private PixelDataScheduleDTO pixelDataSchedule;

  private Long totalUU;

  private Long dailyUU;

  private Long pixelId;

  // private LookalikeDTO lookalike;

  public Integer getDataSourceType() {
    return dataSourceType;
  }

  public BaseModel getLicensee() {
    return licensee;
  }

  public void setLicensee(BaseModel licensee) {
    this.licensee = licensee;
  }

  public BaseModel getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(BaseModel advertiser) {
    this.advertiser = advertiser;
  }

  public Integer getSegmentType() {
    return segmentType;
  }

  public void setSegmentType(Integer segmentType) {
    this.segmentType = segmentType;
  }

  public void setDataSourceType(Integer dataSourceType) {
    this.dataSourceType = dataSourceType;
  }

  public String getRemoteSegmentId() {
    return remoteSegmentId;
  }

  public void setRemoteSegmentId(String remoteSegmentId) {
    this.remoteSegmentId = remoteSegmentId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public RuleDTO getRuleExpression() {
    return ruleExpression;
  }

  public void setRuleExpression(RuleDTO ruleExpression) {
    this.ruleExpression = ruleExpression;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public DurationUnit getDurationUnit() {
    return durationUnit;
  }

  public void setDurationUnit(DurationUnit durationUnit) {
    this.durationUnit = durationUnit;
  }

  public Integer getUserDataType() {
    return userDataType;
  }

  public void setUserDataType(Integer userDataType) {
    this.userDataType = userDataType;
  }

  public PixelDataFileDTO getPixelDataFile() {
    return pixelDataFile;
  }

  public void setPixelDataFile(PixelDataFileDTO pixelDataFile) {
    this.pixelDataFile = pixelDataFile;
  }

  public PixelDataScheduleDTO getPixelDataSchedule() {
    return pixelDataSchedule;
  }

  public void setPixelDataSchedule(PixelDataScheduleDTO pixelDataSchedule) {
    this.pixelDataSchedule = pixelDataSchedule;
  }

  public Long getTotalUU() {
    return totalUU;
  }

  public void setTotalUU(Long totalUU) {
    this.totalUU = totalUU;
  }

  public Long getDailyUU() {
    return dailyUU;
  }

  public void setDailyUU(Long dailyUU) {
    this.dailyUU = dailyUU;
  }

  public Long getPixelId() {
    return pixelId;
  }

  public void setPixelId(Long pixelId) {
    this.pixelId = pixelId;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AudienceDTO [licensee=").append(licensee).append(", advertiser=")
        .append(advertiser).append(", remoteSegmentId=").append(remoteSegmentId)
        .append(", description=").append(description).append(", segmentType=").append(segmentType)
        .append(", ruleExpression=").append(ruleExpression).append(", duration=").append(duration)
        .append(", durationUnit=").append(durationUnit).append(", userDataType=")
        .append(userDataType).append(", dataSourceType=").append(dataSourceType)
        .append(", pixelDataFile=").append(pixelDataFile).append(", pixelDataSchedule=")
        .append(pixelDataSchedule).append(", totalUU=").append(totalUU).append(", dailyUU=")
        .append(dailyUU).append(", pixelId=").append(pixelId).append("]");
    return builder.toString();
  }


}
