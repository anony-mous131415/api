package io.revx.core.search.filter;


public class Expression {
  private String field;

  private String dtoField;

  private Operator operator;

  private Object value;

  Expression() {}

  Expression(String field, String dtoField, String operator, Object value) {
    this.field = field;
    this.operator = Operator.getOperator(operator);
    this.value = value;
    this.setDtoField(dtoField);
  }

  public String getField() {
    return field;
  }

  public void setField(String field) {
    this.field = field;
  }

  public Operator getOperator() {
    return operator;
  }

  public void setOperator(Operator operator) {
    this.operator = operator;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public String getDtoField() {
    return dtoField;
  }

  public void setDtoField(String dtoField) {
    this.dtoField = dtoField;
  }

  public String toString() {
    return new StringBuilder("").append(this.getField()).append(":")
        .append(this.getOperator().getOperatorName()).append(":").append(this.getValue())
        .toString();
  }
}
