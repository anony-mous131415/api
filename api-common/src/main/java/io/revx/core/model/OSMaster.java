package io.revx.core.model;



public class OSMaster extends BaseModel {

  private static final long serialVersionUID = 7242963441037021546L;

  public OSMaster() {}

  public OSMaster(Long id) {
    this.id = id;
  }

  private Long channelType;

  private boolean active;

  public Long getChannelType() {
    return channelType;
  }

  public void setChannelType(Long channelType) {
    this.channelType = channelType;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }



}
