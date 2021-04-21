/*
 * @author: ranjan-pritesh
 * 
 * @date: 16th Dec
 */
package io.revx.api.mysql.entity.creative;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.creative.CreativeVideoFormat;
import io.revx.core.model.creative.VastProtocol;
import io.revx.core.model.creative.VideoCampanionType;

@Entity
@Table(name = "VideoAttributes")
public class VideoAttributeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "va_id", nullable = false)
  private Long id;

  @Column(name = "va_duration_in_secs")
  private Long duration;

  @Column(name = "va_video_format", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private CreativeVideoFormat format;

  @Column(name = "va_is_skippable", columnDefinition = "BIT", length = 1)
  private Boolean isSkippable;

  @Column(name = "va_video_width")
  private Integer width;

  @Column(name = "va_video_height")
  private Integer height;

  @Column(name = "va_video_path")
  private String videoPath;

  @Column(name = "va_vast_protocol", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private VastProtocol vastProtocol;

  @Column(name = "va_companion_type", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private VideoCampanionType companionType;

  @Column(name = "va_companion_creative_id", nullable = false)
  private Long companionCreativeId;

  @Column(name = "va_companion_path")
  private String companionPath;

  @Column(name = "va_companion_width")
  private Integer companionWidth;

  @Column(name = "va_companion_height")
  private Integer companionHeight;

  @Column(name = "va_advertiser_id")
  private Long advertiserId;

  @Column(name = "va_licensee_id")
  private Long licenseeId;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public CreativeVideoFormat getFormat() {
    return format;
  }

  public void setFormat(CreativeVideoFormat format) {
    this.format = format;
  }

  public String getVideoPath() {
    return videoPath;
  }

  public void setVideoPath(String videoPath) {
    this.videoPath = videoPath;
  }

  public String getCompanionPath() {
    return companionPath;
  }

  public void setCompanionPath(String companionPath) {
    this.companionPath = companionPath;
  }

  public Boolean getIsSkippable() {
    return isSkippable;
  }

  public void setIsSkippable(Boolean isSkippable) {
    this.isSkippable = isSkippable;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public VastProtocol getVastProtocol() {
    return vastProtocol;
  }

  public void setVastProtocol(VastProtocol vastProtocol) {
    this.vastProtocol = vastProtocol;
  }

  public VideoCampanionType getCompanionType() {
    return companionType;
  }

  public void setCompanionType(VideoCampanionType companionType) {
    this.companionType = companionType;
  }

  public Long getCompanionCreativeId() {
    return companionCreativeId;
  }

  public void setCompanionCreativeId(Long companionCreativeId) {
    this.companionCreativeId = companionCreativeId;
  }

  public Integer getCompanionWidth() {
    return companionWidth;
  }

  public void setCompanionWidth(Integer companionWidth) {
    this.companionWidth = companionWidth;
  }

  public Integer getCompanionHeight() {
    return companionHeight;
  }

  public void setCompanionHeight(Integer companionHeight) {
    this.companionHeight = companionHeight;
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

  @Override
  public String toString() {
    return "VideoAttributeEntity [id=" + id + ", duration=" + duration + ", format=" + format
        + ", isSkippable=" + isSkippable + ", width=" + width + ", height=" + height
        + ", videoPath=" + videoPath + ", vastProtocol=" + vastProtocol + ", companionType="
        + companionType + ", companionCreativeId=" + companionCreativeId + ", companionPath="
        + companionPath + ", companionWidth=" + companionWidth + ", companionHeight="
        + companionHeight + ", advertiserId=" + advertiserId + ", licenseeId=" + licenseeId + "]";
  }

}
