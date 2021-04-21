package io.revx.core.model.creative;

import io.revx.core.model.ClickDestination;

public class CreativeDetails {

  private Long advertiserId;
  private String name;
  private ClickDestination clickDestination;
  private Boolean isDCO;
  
  public Long getAdvertiserId() {
    return advertiserId;
  }
  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public ClickDestination getClickDestination() {
    return clickDestination;
  }
  public void setClickDestination(ClickDestination clickDestination) {
    this.clickDestination = clickDestination;
  }
  public Boolean getIsDCO() {
    return isDCO;
  }
  public void setIsDCO(Boolean isDCO) {
    this.isDCO = isDCO;
  }
  
  @Override
  public String toString() {
    return "CreativeDetails [advertiserId=" + advertiserId + ", name=" + name
        + ", clickDestination=" + clickDestination + ", isDCO=" + isDCO + "]";
  }

  
}
