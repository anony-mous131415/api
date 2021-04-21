package io.revx.core.search.filter;

import java.util.List;

public class ComplexExpression {

  private String logicalOperator;

  private List<Object> operands;

  public String getLogicalOperator() {
    return logicalOperator;
  }

  public void setLogicalOperator(String logical_operator) {
    this.logicalOperator = logical_operator;
  }

  public List<Object> getOperands() {
    return operands;
  }

  public void setOperands(List<Object> operands) {
    this.operands = operands;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    if (this.logicalOperator != null) {
      sb.append(this.logicalOperator).append("[");

      for (Object obj : this.operands) {
        if (obj instanceof ComplexExpression) {
          ComplexExpression ce = (ComplexExpression) obj;
          sb.append(ce.toString());
          sb.append("]");
        } else if (obj instanceof Expression) {
          Expression e = (Expression) obj;
          sb.append(e.getField()).append(":");
          sb.append(e.getOperator().getOperatorName()).append(":");
          sb.append(e.getValue());

          if (sb.length() > 0) {
            sb.append(",");
          }
        }
      }
      sb.append("]");
    }
    return sb.toString();
  }

}
