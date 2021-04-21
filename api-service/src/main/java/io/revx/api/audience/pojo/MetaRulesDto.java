package io.revx.api.audience.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetaRulesDto implements Serializable {

  private static final long serialVersionUID = 2865110029776902770L;
  
  List<MetaRuleDto> metaRules;

  public MetaRulesDto() {
      metaRules = new ArrayList<MetaRuleDto>();
  }

  public List<MetaRuleDto> getMetaRules() {
      return metaRules;
  }

  public void addMetaRule(MetaRuleDto metaRule) {
      this.metaRules.add(metaRule);
  }
  
  public void addMetaRules(List<MetaRuleDto> metaRules) {
      this.metaRules.addAll(metaRules);
  }
  
}
