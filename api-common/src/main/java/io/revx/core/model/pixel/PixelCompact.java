package io.revx.core.model.pixel;

import io.revx.core.model.StatusTimeModel;

public class PixelCompact extends StatusTimeModel{

  /**
   * 
   */
  private static final long serialVersionUID = -7557252969380670347L;


  private Long advertiserId;

  private Long licenseeId;

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
    return "PixelCompact [advertiserId=" + advertiserId + ", licenseeId=" + licenseeId
        + ", modifiedTime=" + modifiedTime + ", modifiedBy=" + modifiedBy + ", id=" + id + ", name="
        + name + ", getAdvertiserId()=" + getAdvertiserId() + ", getLicenseeId()=" + getLicenseeId()
        + ", isActive()=" + isActive() + ", getCreationTime()=" + getCreationTime()
        + ", getCreatedBy()=" + getCreatedBy() + ", toString()=" + super.toString()
        + ", getModifiedTime()=" + getModifiedTime() + ", getModifiedBy()=" + getModifiedBy()
        + ", getId()=" + getId() + ", getName()=" + getName() + ", hashCode()=" + hashCode()
        + ", getClass()=" + getClass() + "]";
  }

  
}
