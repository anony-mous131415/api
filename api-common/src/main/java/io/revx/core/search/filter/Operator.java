package io.revx.core.search.filter;

public enum Operator {

  eq("eq"), ne("ne"), lt("lt"), le("le"), gt("gt"), ge("ge"), in("in"), like("like"), nlike(
      "nlike");

  private String operator;

  Operator(String operator) {
    this.operator = operator;
  }

  public String getOperatorName() {
    return this.operator;
  }

  public static Operator getOperator(String optr) {
    if (optr != null) {
      for (Operator o : Operator.values()) {
        if (optr.equalsIgnoreCase(o.operator)) {
          return o;
        }
      }
    }
    return null;
  }
}
