package io.revx.core.model.audience;

public enum Operator {
  AND("&"), OR("|"), NOT("!");

  private String op;

  private Operator(String operator) {
    this.op = operator;
  }

  public String getValue() {
    return op;
  }

  public static Operator getOperator(String operator) {
    for (Operator op : Operator.values()) {
      if (op.op.equals(operator)) {
        return op;
      }
    }
    return null;
  }
}
