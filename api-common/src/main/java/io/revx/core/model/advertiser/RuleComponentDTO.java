package io.revx.core.model.advertiser;

import java.io.Serializable;

public class RuleComponentDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;
	
	private Integer filterId;
	
	private Integer operatorId;
	
	private String value;
	
	private Boolean negate;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getFilterId() {
    return filterId;
  }

  public void setFilterId(Integer filterId) {
    this.filterId = filterId;
  }

  public Integer getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Integer operatorId) {
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
	
	

}
