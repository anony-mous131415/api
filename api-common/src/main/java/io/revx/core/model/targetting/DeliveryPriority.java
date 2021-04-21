package io.revx.core.model.targetting;


import io.revx.core.model.BaseModel;


public class DeliveryPriority extends BaseModel {
  private static final long serialVersionUID = -2515947830548686359L;

  public DeliveryPriority() {}

  public DeliveryPriority(long id) {
    this.id = id;
  }


  private String description;

  private Integer priorityClass;



  @Override
  public Long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  @Override

  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getPriorityClass() {
    return priorityClass;
  }

  public void setPriorityClass(Integer priorityClass) {
    this.priorityClass = priorityClass;
  }


  public String getDescriptiveName() {
    return "DeliveryPriority";
  }

}
