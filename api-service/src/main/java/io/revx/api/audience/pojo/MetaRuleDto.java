package io.revx.api.audience.pojo;

import java.util.HashSet;
import java.util.Set;

public class MetaRuleDto extends RuleFilterDto {

  private static final long serialVersionUID = 1L;

  private Set<RuleOperatorDto> ruleOperators;
  
  private Set<RuleValueDto> ruleValues;
  
  public MetaRuleDto() {
      ruleOperators = new HashSet<RuleOperatorDto>();
      ruleValues = new HashSet<RuleValueDto>();
  }

  public Set<RuleOperatorDto> getRuleOperators() {
      return ruleOperators;
  }

  public void setRuleOperators(Set<RuleOperatorDto> ruleOperators) {
      this.ruleOperators = ruleOperators;
  }

  public Set<RuleValueDto> getRuleValues() {
      return ruleValues;
  }

  public void setRuleValues(Set<RuleValueDto> ruleValues) {
      this.ruleValues = ruleValues;
  }
  
}
