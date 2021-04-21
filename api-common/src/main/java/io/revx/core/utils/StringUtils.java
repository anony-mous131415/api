package io.revx.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.revx.core.model.BaseModel;
import io.revx.core.model.targetting.ExtendedBaseModel;
import io.revx.core.search.filter.SearchFilterConstants;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

	/**
	 * Returns true if string is empty/null.<br/>
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0)
			return true;
		else
			return false;
	}

	public static String trimIt(String str) {
		if (str == null)
			return "";
		else
			return str.trim();
	}

	public static String getOperator(String operatorName) {
		String operator = null;

		if (operatorName.equals(SearchFilterConstants.EQUALS))
			operator = " = ";
		else if (operatorName.equals(SearchFilterConstants.NOT_EQUALS))
			operator = " <> ";
		else if (operatorName.equals(SearchFilterConstants.LESS_THAN))
			operator = " < ";
		else if (operatorName.equals(SearchFilterConstants.LESS_THAN_OR_EQUALS))
			operator = " <= ";
		else if (operatorName.equals(SearchFilterConstants.GREATER_THAN))
			operator = " > ";
		else if (operatorName.equals(SearchFilterConstants.GREATER_THAN_OR_EQUALS))
			operator = " >= ";
		else if (operatorName.equals(SearchFilterConstants.IN))
			operator = " in ";
		else if (operatorName.equals(SearchFilterConstants.LIKE))
			operator = " like ";
		return operator;
	}

	/*
	 * replaces all occurrences of character s (not escaped with '\') with character
	 * r
	 */
	public static String replaceUnEscapedCharacter(String str, Character s, Character r) {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while (i < str.length()) {
			if (str.charAt(i) == s)
				sb.append(r);
			else if (str.charAt(i) == '\\') {
				sb.append(str.charAt(i++));
				if (i < str.length())
					sb.append(str.charAt(i));
			} else
				sb.append(str.charAt(i));
			i++;
		}
		return sb.toString();
	}

	// method to convert list of BaseModel into comma separated string
	public static String formatBaseModel(List<BaseModel> bmList) {

		if (bmList == null || bmList.isEmpty()) {
			return "NA";
		}

		List<String> values = new ArrayList<String>();
		for (BaseModel bm : bmList) {
			values.add(bm.getName() + "(" + bm.getId() + ")");
		}

		return String.join(",", values);
	}

	// method to convert list of ExtendedBaseModel into comma separated string
	public static String formatExtendedBaseModel(List<ExtendedBaseModel> bmList) {

		if (bmList == null || bmList.isEmpty()) {
			return "NA";
		}

		List<String> values = new ArrayList<String>();
		for (ExtendedBaseModel bm : bmList) {

			List<String> properties = new ArrayList<String>();
			if (bm.properties != null && bm.properties.size() > 0) {
				for (Map.Entry<String, BaseModel> entry : bm.properties.entrySet()) {
					String key = entry.getKey();
					BaseModel value = entry.getValue();
					properties.add(key + ": " + value.getName());
				}
			}
			String value = bm.getName() + "(" + bm.getId() + ")";
			if (properties != null && properties.size() > 0) {
				value += "-" + String.join(",", properties);
			}

			values.add(value);
		}

		return String.join(",", values);
	}
}
