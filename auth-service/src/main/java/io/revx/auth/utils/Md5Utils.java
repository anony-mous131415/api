package io.revx.auth.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {

  public static String getMd5(String input) {
    return getMd5(input, "MD5");
  }

  public static String getMd5(String input, String algorithem) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithem);

      byte[] messageDigest = md.digest(input.getBytes());

      BigInteger no = new BigInteger(1, messageDigest);

      String hashtext = no.toString(16);
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  
  public static void main(String[] args) {
    System.out.println(getMd5("Revx@123"));
  }

}
