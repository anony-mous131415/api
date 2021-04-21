package io.revx.api.mysql.entity.campaign;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AdvertiserIOPixel")
public class AdvertiserIOPixel {

  @Id
  @Column(name = "aip_advertiser_io_id", nullable = false)
  private Long campaignId;

  @Column(name = "aip_pixel_id", nullable = false)
  private Long pixelId;

  public Long getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(Long campaignId) {
    this.campaignId = campaignId;
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
    builder.append("AdvertiserIOPixel [campaignId=").append(campaignId).append(", pixelId=")
        .append(pixelId).append("]");
    return builder.toString();
  }

}
