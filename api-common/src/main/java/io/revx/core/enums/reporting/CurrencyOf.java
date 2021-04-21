package io.revx.core.enums.reporting;

public enum CurrencyOf {
	licensee("_in_licensee_currency"),

	advertiser("_in_advertiser_currency");

	private String fieldName;

	private CurrencyOf() {
		// TODO Auto-generated constructor stub
	}

	private CurrencyOf(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public static String addCurrencyDependency(String prop, CurrencyOf currency) {
		return prop + "" + currency.getFieldName();
	}
	
	public static String removeCurrencyDependency(String prop, CurrencyOf currency) {
		if(prop.contains(currency.getFieldName())) {
			return prop.replace(currency.getFieldName(), "");
		}else {
			return prop;
		}
	}

}
