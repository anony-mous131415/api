package io.revx.api.mysql.entity.pixel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "AdvertiserLineItemPixel")
public class AdvertiserLineItemPixelEntity {

  @Id
  @Column(name = "ap_advertiser_li_id")
  private Long strategyId;

  @Column(name = "ap_pixel_id")
  private Long pixelId;


  public AdvertiserLineItemPixelEntity() {
    super();
    // TODO Auto-generated constructor stub
  }

  public AdvertiserLineItemPixelEntity(Long strategyId, Long pixelId) {
    super();
    this.strategyId = strategyId;
    this.pixelId = pixelId;
  }

  public Long getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(Long strategyId) {
    this.strategyId = strategyId;
  }

  public Long getPixelId() {
    return pixelId;
  }

  public void setPixelId(Long pixelId) {
    this.pixelId = pixelId;
  }


}
