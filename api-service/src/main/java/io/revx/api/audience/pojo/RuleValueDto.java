package io.revx.api.audience.pojo;

public class RuleValueDto {

  private static final long serialVersionUID = 1L;
  
  private String value;
  
  private String displayValue;
  
  public RuleValueDto() {

  }

  public String getValue() {
      return value;
  }

  public void setValue(String value) {
      this.value = value;
  }

  public String getDisplayValue() {
      return displayValue;
  }

  public void setDisplayValue(String displayValue) {
      this.displayValue = displayValue;
  }
  
}
