package io.revx.core.enums.reporting;

public enum Entity {
	RTB("rtb"), CONVERSION_REPORT("conversionreport");

	private String entity;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	Entity(String entity) {
		this.entity = entity;
	}
}
