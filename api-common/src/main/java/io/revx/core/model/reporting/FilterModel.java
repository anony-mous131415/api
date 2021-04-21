package io.revx.core.model.reporting;

import java.io.Serializable;

import io.revx.core.enums.reporting.OperatorModel;



public class FilterModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String column;
	private OperatorModel operator;
	private Object value;

	public FilterModel() {
		super();
	}

	public FilterModel(String column, OperatorModel operator, Object value) {
		super();
		this.column = column;
		this.operator = operator;
		this.value = value;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public OperatorModel getOperator() {
		return operator;
	}

	public void setOperator(OperatorModel operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "FilterModal [column=" + column + ", operator=" + operator + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterModel other = (FilterModel) obj;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		if (operator != other.operator)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	

}
