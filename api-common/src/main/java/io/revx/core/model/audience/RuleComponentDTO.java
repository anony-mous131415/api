package io.revx.core.model.audience;

import java.io.Serializable;

public class RuleComponentDTO implements Serializable {

  private static final long serialVersionUID = 1L;


  private Integer id;


  private Long filterId;


  private Long operatorId;


  private String value;


  private Boolean negate;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Long getFilterId() {
    return filterId;
  }

  public void setFilterId(Long filterId) {
    this.filterId = filterId;
  }

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Boolean getNegate() {
    return negate;
  }

  public void setNegate(Boolean negate) {
    this.negate = negate;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("RuleComponentDTO [id=").append(id).append(", filterId=").append(filterId)
        .append(", operatorId=").append(operatorId).append(", value=").append(value)
        .append(", negate=").append(negate).append("]");
    return builder.toString();
  }

}
