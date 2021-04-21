package io.revx.api.mysql.entity.creative;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.creative.DcoAttributeType;

@Entity
@Table(name = "DcoAttribute")
public class DcoAttributesEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1454707879168988374L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "da_id", nullable = false)
  private Long id;

  @Column(name = "da_creative_id", nullable = false)
  private Long creativeId;

  @Column(name = "da_no_of_slots")
  private Integer noOfSlots;

  @Column(name = "da_macro_list")
  private String macroList;

  @Column(name = "da_fallback_creative_id")
  private Long fallbackCreativeId;

  @Column(name = "da_dco_attr_type", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
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
