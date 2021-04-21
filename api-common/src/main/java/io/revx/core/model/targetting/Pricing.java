package io.revx.core.model.targetting;


import io.revx.core.model.BaseModel;



public class Pricing extends BaseModel {
  private static final long serialVersionUID = -5756289831221464519L;

  public Pricing() {}

  public Pricing(Long id) {
    this.id = id;
  }

  private String desc;

  private Integer order;

  private Integer flag;

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (!(obj instanceof Pricing))
      return false;
    Pricing iobj = (Pricing) obj;
    if (iobj.getId().intValue() == this.getId().intValue())
      return true;
    else
      return false;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public String getDescriptiveName() {
    return "PricingType";
  }

  public Integer getFlag() {
    return flag;
  }

  public void setFlag(Integer flag) {
    this.flag = flag;
  }

}
