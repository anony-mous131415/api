package io.revx.core.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

	private final static String SALT = "NaCl";

	public static String getMD5Hash(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(SALT.getBytes("UTF-8"));
			digest.update(input.getBytes("UTF-8"));
			StringBuffer result = new StringBuffer();
			byte bytes[] = digest.digest();
			for (int i = 0; i < bytes.length; i++) {
				byte b = bytes[i];
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("This will never be seen as correct "
					+ "algorithm has already hardcoded here.", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("This will never be seen as correct "
					+ "encoding has already hardcoded here.", e);
		}
	}

	public static void main(String[] args) {
		System.out.println(HashUtil.getMD5Hash("jayant"));
	}
}
