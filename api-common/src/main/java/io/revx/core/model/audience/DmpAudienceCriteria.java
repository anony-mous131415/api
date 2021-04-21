package io.revx.core.model.audience;

public class DmpAudienceCriteria {

  private String key;
  private String operator;
  private String value;
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public String getOperator() {
    return operator;
  }
  public void setOperator(String operator) {
    this.operator = operator;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DmpAudienceCriteria [key=").append(key).append(", operator=").append(operator)
        .append(", value=").append(value).append("]");
    return builder.toString();
  }
  
}
