package io.revx.core.model.pixel;

import java.math.BigDecimal;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.StatusTimeModel;

public class Pixel extends StatusTimeModel implements BaseEntity {

  /**
   * 
   */
  private static final long serialVersionUID = -7557252969380670347L;

  private Advertiser advertiserPojo;

  private Long clickValidityWindow;

  private Long viewValidityWindow;

  private Long userFcap;

  private Long fcapDuration;

  private BaseModel type;

  private BigDecimal conversions;

  private BigDecimal clicks;

  private BigDecimal impressions;

  @Override
  public Long getId() {
    return super.getId();
  }

  public Advertiser getAdvertiserPojo() {
    return advertiserPojo;
  }

  public void setAdvertiserPojo(Advertiser advertiserPojo) {
    this.advertiserPojo = advertiserPojo;
  }

  public Long getClickValidityWindow() {
    return clickValidityWindow;
  }

  public void setClickValidityWindow(Long clickValidityWindow) {
    this.clickValidityWindow = clickValidityWindow;
  }

  public Long getViewValidityWindow() {
    return viewValidityWindow;
  }

  public void setViewValidityWindow(Long viewValidityWindow) {
    this.viewValidityWindow = viewValidityWindow;
  }

  public Long getUserFcap() {
    return userFcap;
  }

  public void setUserFcap(Long userFcap) {
    this.userFcap = userFcap;
  }

  public Long getFcapDuration() {
    return fcapDuration;
  }

  public void setFcapDuration(Long fcapDuration) {
    this.fcapDuration = fcapDuration;
  }

  public BaseModel getType() {
    return type;
  }

  public void setType(BaseModel type) {
    this.type = type;
  }

  public BigDecimal getConversions() {
    return conversions;
  }

  public void setConversions(BigDecimal conversions) {
    this.conversions = conversions;
  }

  public BigDecimal getClicks() {
    return clicks;
  }

  public void setClicks(BigDecimal clicks) {
    this.clicks = clicks;
  }

  public BigDecimal getImpressions() {
    return impressions;
  }

  public void setImpressions(BigDecimal impressions) {
    this.impressions = impressions;
  }

  public Long getAdvertiserId() {
    if (this.getAdvertiserPojo() != null)
      return this.getAdvertiserPojo().getId();

    return null;
  }

  @Override
  public String toString() {
    return "Pixel [advertiserPojo=" + advertiserPojo + ", clickValidityWindow="
        + clickValidityWindow + ", viewValidityWindow=" + viewValidityWindow + ", userFcap="
        + userFcap + ", fcapDuration=" + fcapDuration + ", type=" + type + ", conversions="
        + conversions + ", clicks=" + clicks + ", impressions=" + impressions + ", modifiedTime="
        + modifiedTime + ", modifiedBy=" + modifiedBy + ", id=" + id + ", name=" + name + "]";
  }
  
  

}
