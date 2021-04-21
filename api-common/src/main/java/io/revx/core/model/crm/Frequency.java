package io.revx.core.model.crm;

import io.revx.core.enums.DurationUnit;

public class Frequency {

	private int value;
	private DurationUnit unit;

	public Frequency() {
		super();
	}

	public Frequency(int value, DurationUnit unit) {
		super();
		this.value = value;
		this.unit = unit;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public DurationUnit getUnit() {
		return unit;
	}

	public void setUnit(DurationUnit unit) {
		this.unit = unit;
	}

}
