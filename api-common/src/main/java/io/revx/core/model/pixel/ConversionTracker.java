package io.revx.core.model.pixel;

import io.revx.core.model.BaseModel;

@SuppressWarnings("serial")
public class ConversionTracker extends BaseModel {


  private Long modifiedOn;

  private Integer piggyBack;

  private Integer status;

  private Integer clickValidityWindow;

  private Integer viewValidityWindow;

  private Integer fcapDuration;

  private Integer userFcap;

  private String type;

  private Long pixelHitCount;

  public Long getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(Long modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public Integer getPiggyBack() {
    return piggyBack;
  }

  public void setPiggyBack(Integer piggyBack) {
    this.piggyBack = piggyBack;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Integer getClickValidityWindow() {
    return clickValidityWindow;
  }

  public void setClickValidityWindow(Integer clickValidityWindow) {
    this.clickValidityWindow = clickValidityWindow;
  }

  public Integer getViewValidityWindow() {
    return viewValidityWindow;
  }

  public void setViewValidityWindow(Integer viewValidityWindow) {
    this.viewValidityWindow = viewValidityWindow;
  }

  public Integer getFcapDuration() {
    return fcapDuration;
  }

  public void setFcapDuration(Integer fcapDuration) {
    this.fcapDuration = fcapDuration;
  }

  public Integer getUserFcap() {
    return userFcap;
  }

  public void setUserFcap(Integer userFcap) {
    this.userFcap = userFcap;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Long getPixelHitCount() {
    return pixelHitCount;
  }

  public void setPixelHitCount(Long pixelHitCount) {
    this.pixelHitCount = pixelHitCount;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public String toString() {
    return "ConversionTrackerDTO [modifiedOn=" + modifiedOn + ", piggyBack=" + piggyBack
        + ", status=" + status + ", clickValidityWindow=" + clickValidityWindow
        + ", viewValidityWindow=" + viewValidityWindow + ", fcapDuration=" + fcapDuration
        + ", userFcap=" + userFcap + ", type=" + type + ", pixelHitCount=" + pixelHitCount + "]";
  }


}
