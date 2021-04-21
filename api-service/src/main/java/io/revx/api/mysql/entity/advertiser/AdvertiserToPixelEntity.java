/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.entity.advertiser;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.api.enums.Status;

@Entity
@Table(name = "AdvertiserToPixel")
public class AdvertiserToPixelEntity {

  /** The id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ap_id", nullable = false)
  private Long id;

  @Column(name = "ap_advertiser_id" , nullable = false)
  private Long advertiserId;

  @Column(name = "ap_pixel_id", nullable = false)
  private Long pixelId;
  
  @Column(name="ap_status" , columnDefinition = "ENUM" , nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = "ap_is_auto_update" , nullable = false)
  private Long isAutoUpdate;

  @Column(name = "ap_date_created" , nullable = false)
  private Timestamp dateCreated;

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

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Long getIsAutoUpdate() {
    return isAutoUpdate;
  }

  public void setIsAutoUpdate(Long isAutoUpdate) {
    this.isAutoUpdate = isAutoUpdate;
  }

  public Long getPixelId() {
    return pixelId;
  }

  public void setPixelId(Long pixelId) {
    this.pixelId = pixelId;
  }

  public Timestamp getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Timestamp dateCreated) {
    this.dateCreated = dateCreated;
  }

  @Override
  public String toString() {
    return "AdvertiserToPixelEntity [id=" + id + ", advertiserId=" + advertiserId + ", status="
        + status + ", isAutoUpdate=" + isAutoUpdate + ", dateCreated=" + dateCreated + "]";
  }


}

