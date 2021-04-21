package io.revx.core.enums.reporting;

public enum PropertyType {
	object("object"), number("number"), string("string");

	private PropertyType(String type) {
		this.type = type;
	}

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
