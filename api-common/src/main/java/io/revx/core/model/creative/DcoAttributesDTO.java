package io.revx.core.model.creative;

import java.io.Serializable;

public class DcoAttributesDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1454707879168988374L;

  private Long id;

  private Long creativeId;

  private Integer noOfSlots;

  private String macroList;

  private Long fallbackCreativeId;

  private DcoAttributeType dcoAttribute;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreativeId() {
    return creativeId;
  }

  public void setCreativeId(Long creativeId) {
    this.creativeId = creativeId;
  }

  public Integer getNoOfSlots() {
    return noOfSlots;
  }

  public void setNoOfSlots(Integer noOfSlots) {
    this.noOfSlots = noOfSlots;
  }

  public String getMacroList() {
    return macroList;
  }

  public void setMacroList(String macroList) {
    this.macroList = macroList;
  }

  public Long getFallbackCreativeId() {
    return fallbackCreativeId;
  }

  public void setFallbackCreativeId(Long fallbackCreativeId) {
    this.fallbackCreativeId = fallbackCreativeId;
  }

  public DcoAttributeType getDcoAttribute() {
    return dcoAttribute;
  }

  public void setDcoAttribute(DcoAttributeType dcoAttribute) {
    this.dcoAttribute = dcoAttribute;
  }

  @Override
  public String toString() {
    return "DcoAttributes [id=" + id + ", creativeId=" + creativeId + ", noOfSlots=" + noOfSlots
        + ", macroList=" + macroList + ", fallbackCreativeId=" + fallbackCreativeId
        + ", dcoAttribute=" + dcoAttribute + "]";
  }

}
