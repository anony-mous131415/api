package com.komli.prime.service.reporting.pojo;

import org.apache.commons.lang3.StringUtils;

public class BaseElement implements Cloneable {
	private String elementId;
	private String elementIdIdentifier;
	private String displayStr;
	private int type; // int or long or double or string or boolean 1: String 2:
						// long 3: double
	public static final int TYPE_STRING = 1;
	public static final int TYPE_LONG = 2;
	public static final int TYPE_DOUBLE = 3;

	public BaseElement(String elementId, int type, String displayStr) {
		this.elementId = elementId;
		this.elementIdIdentifier = toID(elementId);
		this.type = type;
		if (StringUtils.isBlank(displayStr)) {
			this.displayStr = elementId;
		} else {
			this.displayStr = displayStr;
		}

	}

	public String getElementId() {
		return elementId;
	}

	public String getElementIdIndentifier() {
		return elementIdIdentifier;
	}

	public String getDisplayStr() {
		return displayStr.replaceAll("[ .()]","");
	}

	public int getType() {
		return type;
	}

	public BaseElement getCopy() {
		return new BaseElement(elementId, type, displayStr);
	}

	public static String toID(String elementId) {
		return elementId.replace(")", "_").replace("(", "_");
	}

}
