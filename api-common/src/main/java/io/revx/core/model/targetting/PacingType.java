package io.revx.core.model.targetting;


import io.revx.core.model.BaseModel;



public class PacingType extends BaseModel {
  private static final long serialVersionUID = -2917522233477750171L;

  public PacingType() {}

  public PacingType(long id) {
    this.id = id;
  }


  private String description;

  private Integer tTL;

  private Integer order;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer gettTL() {
    return tTL;
  }

  public void settTL(Integer tTL) {
    this.tTL = tTL;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }


  public String getDescriptiveName() {
    return "PacingType";
  }

}
