package io.revx.api.audience.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RuleOperatorsDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	List<RuleOperatorDto> operators;
	
	public RuleOperatorsDto() {
		operators = new ArrayList<RuleOperatorDto>();
	}
	
	public List<RuleOperatorDto> getOperators() {
		return operators;
	}

	public void addOperator(RuleOperatorDto operator) {
		this.operators.add(operator);
	}
	
	public void addOperators(List<RuleOperatorDto> operators) {
		this.operators.addAll(operators);
	}
}
