package io.revx.core.model.reporting;

import java.io.Serializable;

public class SortModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String column;
	private boolean ascending;

	public SortModel() {
		super();
	}
	
	public SortModel(String column, boolean ascending) {
		super();
		this.column = column;
		this.ascending = ascending;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	@Override
	public String toString() {
		return "SortModel [column=" + column + ", ascending=" + ascending + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (ascending ? 1231 : 1237);
		result = prime * result + ((column == null) ? 0 : column.hashCode());
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
		SortModel other = (SortModel) obj;
		if (ascending != other.ascending)
			return false;
		if (column == null) {
			if (other.column != null)
				return false;
		} else if (!column.equals(other.column))
			return false;
		return true;
	}
	
	

}
