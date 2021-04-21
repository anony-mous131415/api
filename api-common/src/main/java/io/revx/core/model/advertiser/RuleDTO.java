package io.revx.core.model.advertiser;

import java.io.Serializable;
import java.util.List;
import io.revx.core.enums.Operator;

public class RuleDTO implements Serializable {

	private static final long serialVersionUID = -4442031000511118911L;
	
	Boolean simpleExpr;
	
	RuleComponentDTO ruleElement;
	
	List<RuleDTO> ruleExpressionList;
	
	Boolean negate;
	
	Operator operator;

  public Boolean getSimpleExpr() {
    return simpleExpr;
  }

  public void setSimpleExpr(Boolean simpleExpr) {
    this.simpleExpr = simpleExpr;
  }

  public RuleComponentDTO getRuleElement() {
    return ruleElement;
  }

  public void setRuleElement(RuleComponentDTO ruleElement) {
    this.ruleElement = ruleElement;
  }

  public List<RuleDTO> getRuleExpressionList() {
    return ruleExpressionList;
  }

  public void setRuleExpressionList(List<RuleDTO> ruleExpressionList) {
    this.ruleExpressionList = ruleExpressionList;
  }

  public Boolean getNegate() {
    return negate;
  }

  public void setNegate(Boolean negate) {
    this.negate = negate;
  }

  public Operator getOperator() {
    return operator;
  }

  public void setOperator(Operator operator) {
    this.operator = operator;
  }

	
	
}
