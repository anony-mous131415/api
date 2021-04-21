package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.List;
import io.revx.core.model.BaseModel;

public class TargetingComponentDTO {

  public Long id;

  public BaseModel filter;

  public BaseModel operator;

  public List<BaseModel> criteriaEntities;

  public TargetingComponentDTO() {
    this.id = new Long(0l);
    this.filter = new BaseModel();
    this.operator = new BaseModel();
    this.criteriaEntities = new ArrayList<BaseModel>();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public BaseModel getFilter() {
    return filter;
  }

  public void setFilter(BaseModel filter) {
    this.filter = filter;
  }

  public BaseModel getOperator() {
    return operator;
  }

  public void setOperator(BaseModel operator) {
    this.operator = operator;
  }

  public List<BaseModel> getCriteriaEntities() {
    return criteriaEntities;
  }

  public void setCriteriaEntities(List<BaseModel> criteriaEntities) {
    this.criteriaEntities = criteriaEntities;
  }


}
