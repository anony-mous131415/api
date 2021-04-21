package io.revx.core.model.creative;

import java.io.Serializable;

public class VideoProperties implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long durationInSec;

  private String bitRate;
  
  private Integer companionPath;
  
  private VideoCampanionType campanionType;

  private Integer companionWidth;

  private Integer companionHeight;

  public VideoProperties() {

  }

  public Long getDurationInSec() {
    return durationInSec;
  }

  public void setDurationInSec(Long durationInSec) {
    this.durationInSec = durationInSec;
  }

  public String getBitRate() {
    return bitRate;
  }

  public void setBitRate(String bitRate) {
    this.bitRate = bitRate;
  }
  
  public Integer getCompanionPath() {
    return companionPath;
  }

  public void setCompanionPath(Integer companionPath) {
    this.companionPath = companionPath;
  }

  public VideoCampanionType getCampanionType() {
    return campanionType;
  }

  public void setCampanionType(VideoCampanionType campanionType) {
    this.campanionType = campanionType;
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

  @Override
  public String toString() {
    return "VideoProperties [durationInSec=" + durationInSec + ", bitRate=" + bitRate
        + ", companionPath=" + companionPath + ", campanionType=" + campanionType
        + ", companionWidth=" + companionWidth + ", companionHeight=" + companionHeight + "]";
  }


}
