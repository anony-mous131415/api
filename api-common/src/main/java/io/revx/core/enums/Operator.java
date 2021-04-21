package io.revx.core.enums;

public enum Operator {
	AND("&"),
	OR("|"),
	NOT("!");
	
	String operator;
	
	private Operator(String operator) {
		this.operator = operator;
	}
	
	public String getValue() {
		return operator;
	}
	
	public static Operator getOperator(String operator) {
		for(Operator op : Operator.values()) {
			if(op.operator.equals(operator)) {
				return op;
			}
		}
		return null;
	}
}
