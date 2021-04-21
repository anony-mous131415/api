package io.revx.api.audience.pojo;


public enum RuleFilterType {
  OPTIONS(1),
  TEXT(2);
  
  public final int id;

  private RuleFilterType(int id) {
      this.id = id;
  }

  public static RuleFilterType getRuleFilterType(int id) {
      for (RuleFilterType ruleFilterType : RuleFilterType.values()) {
          if (ruleFilterType.id == id)
              return ruleFilterType;
      }
      throw new RuntimeException("Undefined id.");
  }
  
  public static RuleFilterType getRuleFilterType(String literal) {
      for (RuleFilterType ruleFilterType : RuleFilterType.values()) {
          if (ruleFilterType.name().equals(literal))
              return ruleFilterType;
      }
      throw new RuntimeException("Undefined id.");
  }
  
}
