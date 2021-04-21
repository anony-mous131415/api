package io.revx.api.enums;

public enum CatalogVariableValueType {
	INT(0), DOUBLE(1), STRING(2);

    private int value;
    
    private CatalogVariableValueType(int value) {
    	this.value = value;
    }
    
	public static CatalogVariableValueType get(int value) {
		switch (value) {
		case 0:
			return CatalogVariableValueType.INT;
		case 1:
			return CatalogVariableValueType.DOUBLE;
		case 2:
			return CatalogVariableValueType.STRING;
		default:
			return null;
		}
	}
    
    public int getValue() {
    	return this.value;
    }
}
