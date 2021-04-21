/*
 * @author: ranjan-pritesh
 * 
 * @date: 27 Nov 2019
 */
package io.revx.api.mysql.entity.clickdestination;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.creative.CampaignType;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.creative.GeneratedUrlType;

/**
 * The Class ClickDestinationEntity.
 */
@Entity
@Table(name = "ClickDestination")
public class ClickDestinationEntity {

  /** The id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cd_id", nullable = false)
  private Long id;

  /** The advertiser id. */
  @Column(name = "cd_advertiser_id")
  private Long advertiserId;

  /** The licensee id. */
  @Column(name = "cd_licensee_id")
  private Long licenseeId;

  /** The name. */
  @Column(name = "cd_dst_name", nullable = false, length = 255)
  private String name;

  /** The click url. */
  @Column(name = "cd_click_url", nullable = false, length = 2048)
  private String clickUrl;

  /** The web click url. */
  @Column(name = "cd_web_click_url", nullable = false, length = 2048)
  private String webClickUrl;

  /** The click url. */
  @Column(name = "cd_ios_click_url", nullable = false, length = 2048)
  private String iosClickUrl;

  /** The click url. */
  @Column(name = "cd_android_click_url", nullable = false, length = 2048)
  private String androidClickUrl;

  /** The click url. */
  @Column(name = "cd_web_s2s_click_tracking_url", nullable = false, length = 2048)
  private String webS2sClickTrackingUrl;

  /** The click url. */
  @Column(name = "cd_ios_s2s_click_tracking_url", nullable = false, length = 2048)
  private String iosS2sClickTrackingUrl;

  /** The click url. */
  @Column(name = "cd_android_s2s_click_tracking_url", nullable = false, length = 2048)
  private String androidS2sClickTrackingUrl;

  /** The server tracking url. */
  @Column(name = "cd_server_tracking_url", nullable = false, length = 2048)
  private String serverTrackingUrl;

  /** The click url. */
  @Column(name = "cd_web_tpt_imp_trackers", nullable = false, length = 2048)
  private String webImpressionTracker;

  /** The click url. */
  @Column(name = "cd_ios_tpt_imp_trackers", nullable = false, length = 2048)
  private String iosImpressionTracker;

  /** The click url. */
  @Column(name = "cd_android_tpt_imp_trackers", nullable = false, length = 2048)
  private String androidImpressionTracker;

  /** The created on. */
  @Column(name = "cd_created_on")
  private Long createdOn;

  /** The created by. */
  @Column(name = "cd_created_by")
  private Long createdBy;

  /** The modified on. */
  @Column(name = "cd_modified_on")
  private Long modifiedOn;

  /** The modified by. */
  @Column(name = "cd_modified_by")
  private Long modifiedBy;

  /** The is refactored. */
  @Column(name = "cd_is_refactor", nullable = false)
  private Boolean isRefactored;

  /** The is active. */
  @Column(name = "cd_is_dco", nullable = false)
  private Boolean isDco;

  @Column(name = "cd_status", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private CreativeStatus status;

  /** The campaign type. */
  @Column(name = "cd_campaign_type", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private CampaignType campaignType;
  
  /** The generated url type. */
  @Column(name = "cd_generated_url_type", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private GeneratedUrlType generatedUrlType;

  @Column(name = "cd_skadn_target", nullable = false, columnDefinition = "tinyint(1) default 0")
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

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public void setClickUrl(String clickUrl) {
    this.clickUrl = clickUrl;
  }

  public String getIosClickUrl() {
    return iosClickUrl;
  }

  public void setIosClickUrl(String iosClickUrl) {
    this.iosClickUrl = iosClickUrl;
  }

  public String getAndroidClickUrl() {
    return androidClickUrl;
  }

  public void setAndroidClickUrl(String androidClickUrl) {
    this.androidClickUrl = androidClickUrl;
  }

  public String getWebS2sClickTrackingUrl() {
    return webS2sClickTrackingUrl;
  }

  public void setWebS2sClickTrackingUrl(String webS2sClickTrackingUrl) {
    this.webS2sClickTrackingUrl = webS2sClickTrackingUrl;
  }

  public String getIosS2sClickTrackingUrl() {
    return iosS2sClickTrackingUrl;
  }

  public void setIosS2sClickTrackingUrl(String iosS2sClickTrackingUrl) {
    this.iosS2sClickTrackingUrl = iosS2sClickTrackingUrl;
  }

  public String getAndroidS2sClickTrackingUrl() {
    return androidS2sClickTrackingUrl;
  }

  public void setAndroidS2sClickTrackingUrl(String androidS2sClickTrackingUrl) {
    this.androidS2sClickTrackingUrl = androidS2sClickTrackingUrl;
  }

  public String getWebImpressionTracker() {
    return webImpressionTracker;
  }

  public void setWebImpressionTracker(String webImpressionTracker) {
    this.webImpressionTracker = webImpressionTracker;
  }

  public String getIosImpressionTracker() {
    return iosImpressionTracker;
  }

  public void setIosImpressionTracker(String iosImpressionTracker) {
    this.iosImpressionTracker = iosImpressionTracker;
  }

  public String getAndroidImpressionTracker() {
    return androidImpressionTracker;
  }

  public void setAndroidImpressionTracker(String androidImpressionTracker) {
    this.androidImpressionTracker = androidImpressionTracker;
  }

  public Long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Long createdOn) {
    this.createdOn = createdOn;
  }

  public Long getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(Long modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  public Long getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getWebClickUrl() {
    return webClickUrl;
  }

  public void setWebClickUrl(String webClickUrl) {
    this.webClickUrl = webClickUrl;
  }

  public String getServerTrackingUrl() {
    return serverTrackingUrl;
  }

  public void setServerTrackingUrl(String serverTrackingUrl) {
    this.serverTrackingUrl = serverTrackingUrl;
  }

  public Boolean getIsRefactored() {
    return isRefactored;
  }

  public void setIsRefactored(Boolean isRefactored) {
    this.isRefactored = isRefactored;
  }

  public CreativeStatus getStatus() {
    return status;
  }

  public void setStatus(CreativeStatus status) {
    this.status = status;
  }

  public Boolean getIsDco() {
    return isDco;
  }

  public void setIsDco(Boolean isDco) {
    this.isDco = isDco;
  }

  public CampaignType getCampaignType() {
    return campaignType;
  }

  public void setCampaignType(CampaignType campaignType) {
    this.campaignType = campaignType;
  }
  
  
  public GeneratedUrlType getGeneratedUrlType() {
	  return generatedUrlType;
  }

  public void setGeneratedUrlType(GeneratedUrlType genUrl) {
	  this.generatedUrlType = genUrl;
  }

  public Boolean getSkadTarget() {
    return skadTarget;
  }

  public void setSkadTarget(Boolean skadTarget) {
    this.skadTarget = skadTarget;
  }

  @Override
  public String toString() {
    return "ClickDestinationEntity{" +
            "id=" + id +
            ", advertiserId=" + advertiserId +
            ", licenseeId=" + licenseeId +
            ", name='" + name + '\'' +
            ", clickUrl='" + clickUrl + '\'' +
            ", webClickUrl='" + webClickUrl + '\'' +
            ", iosClickUrl='" + iosClickUrl + '\'' +
            ", androidClickUrl='" + androidClickUrl + '\'' +
            ", webS2sClickTrackingUrl='" + webS2sClickTrackingUrl + '\'' +
            ", iosS2sClickTrackingUrl='" + iosS2sClickTrackingUrl + '\'' +
            ", androidS2sClickTrackingUrl='" + androidS2sClickTrackingUrl + '\'' +
            ", serverTrackingUrl='" + serverTrackingUrl + '\'' +
            ", webImpressionTracker='" + webImpressionTracker + '\'' +
            ", iosImpressionTracker='" + iosImpressionTracker + '\'' +
            ", androidImpressionTracker='" + androidImpressionTracker + '\'' +
            ", createdOn=" + createdOn +
            ", createdBy=" + createdBy +
            ", modifiedOn=" + modifiedOn +
            ", modifiedBy=" + modifiedBy +
            ", isRefactored=" + isRefactored +
            ", isDco=" + isDco +
            ", status=" + status +
            ", campaignType=" + campaignType +
            ", generatedUrlType=" + generatedUrlType +
            ", skadTarget=" + skadTarget +
            '}';
  }

}
