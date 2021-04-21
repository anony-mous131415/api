package com.komli.prime.service.reporting.utils;

import java.util.UUID;

public class SessionUtils {
	public static String generateSessionId(){
		return UUID.randomUUID().toString();
	}	
}
