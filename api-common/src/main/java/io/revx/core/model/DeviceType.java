package io.revx.core.model;

public class DeviceType extends BaseModel {
  private static final long serialVersionUID = 1L;

  private boolean active;

  private Long channelTypeId;

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Long getChannelTypeId() {
    return channelTypeId;
  }

  public void setChannelTypeId(Long channelTypeId) {
    this.channelTypeId = channelTypeId;
  }


}
