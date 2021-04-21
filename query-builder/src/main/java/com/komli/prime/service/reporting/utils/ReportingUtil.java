package com.komli.prime.service.reporting.utils;

import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class ReportingUtil {
	
	private static final Pattern doublePattern = Pattern.compile("-?\\d+(\\.\\d*)?");
	private static final Pattern stringPattern = Pattern.compile("^[a-zA-Z0-9-_.\\s(),]*$");
	

	public static String getUniqueTempTable(String sessionId){
		if(StringUtils.isNotBlank(sessionId)){
			return "TEMP"+sessionId.replace("-", "");
		}
		return null;
	}
	
	public static boolean isNumeric(String str){
		return doublePattern.matcher(str).matches();
	}
	
	public static boolean isAllowableString(String str){
		return stringPattern.matcher(str).matches();
	}	
	
	public static String getUniqueCSVFile(String reportId){
		if(StringUtils.isNotBlank(reportId)){
			return "SL"+reportId.replace("-", "")+".csv";
		}
		return null;
	}

	public static int executeCommand(String str) throws IOException, InterruptedException {
		String[] command ={ "/bin/sh","-c",str};
		Process p= Runtime.getRuntime().exec(command);
		int returnCode = p.waitFor();
		return returnCode;		
	}
}