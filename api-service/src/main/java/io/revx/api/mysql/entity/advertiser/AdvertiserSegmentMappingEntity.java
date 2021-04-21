package io.revx.api.mysql.entity.advertiser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AdvertiserSegmentMapping")
public class AdvertiserSegmentMappingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "asm_id", nullable = false)
  private Long id;

  @Column(name = "asm_advertiser_id", nullable = false)
  private Long advertiserId;

  @Column(name = "asm_licensee_id", nullable = false)
  private Long licenseeId;
  
  @Column(name = "asm_segment_id", nullable = false)
  private Long segmentId;

  @Column(name = "asm_remote_segment_id", nullable = false)
  private String remoteSegmentId;
  
  @Column(name = "asm_is_active", nullable = false)
  private Boolean isActive;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public String getRemoteSegmentId() {
    return remoteSegmentId;
  }

  public void setRemoteSegmentId(String remoteSegmentId) {
    this.remoteSegmentId = remoteSegmentId;
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public Long getSegmentId() {
    return segmentId;
  }

  public void setSegmentId(Long segmentId) {
    this.segmentId = segmentId;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdvertiserSegmentMappingEntity [advertiserId=").append(advertiserId)
        .append(", licenseeId=").append(licenseeId).append(", segmentId=").append(segmentId)
        .append(", remoteSegmentId=").append(remoteSegmentId).append(", isActive=").append(isActive)
        .append("]");
    return builder.toString();
  }

  
}
