package io.revx.core.enums.reporting;

public enum OperatorModel {

	eq("eq"), in("in"), not_in("not in");

	private String operator;

	OperatorModel(String operator) {
		this.operator = operator;
	}

	public String getOperatorName() {
		return this.operator;
	}
}
