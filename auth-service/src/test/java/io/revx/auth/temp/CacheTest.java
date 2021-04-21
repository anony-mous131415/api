package io.revx.auth.temp;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import io.revx.auth.utils.LogoutCacheHolder;

public class CacheTest {

  protected static LogoutCacheHolder logoutCacheHolder = new LogoutCacheHolder();

  @SuppressWarnings("unused")
  private static void testCache() {

    List<String> list = new ArrayList<String>();
    for (int i = 0; i <= 15; i++) {
      list.add(RandomStringUtils.randomAlphanumeric(32));
    }
    for (String key : list) {
      System.out.println("Now Key Is " + key);
      System.out.println(" Printing All Key cache ");
      // logoutCacheHolder.printAllKey();
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    try {
      // generateLongdata();
      // generateStringdata();
    } catch (Exception e) {

    }
  }

  protected static void generateLongdata() throws Exception {
    PrintWriter f0 =
        new PrintWriter(new FileWriter("/home/amaurya/Desktop/mapTest/itiFileLong.txt"));
    for (int i = 0; i < 3000000; i++) {
      f0.println(genarateRandomLines());
    }
    f0.close();
  }

  protected static String genarateRandomLines() {
    StringBuffer sb = new StringBuffer();
    sb.append(RandomUtils.nextInt(101, 9999999));
    sb.append(RandomUtils.nextInt(101, 999)).append("\t");
    sb.append(RandomUtils.nextDouble(0.00001, 0.9));
    return sb.toString();
  }

  protected static void generateStringdata() throws Exception {
    PrintWriter f0 = new PrintWriter(new FileWriter("/home/amaurya/Desktop/mapTest/itiFile1.txt"));
    for (int i = 0; i < 3000000; i++) {
      f0.println(genarateRandomLinesForString());
    }
    f0.close();
  }

  private static String genarateRandomLinesForString() {
    StringBuffer sb = new StringBuffer();
    sb.append(RandomUtils.nextInt(101, 9999999)).append(":");
    sb.append(RandomUtils.nextInt(101, 9999999)).append(":");
    sb.append(RandomUtils.nextInt(101, 999)).append("\t");
    sb.append(RandomUtils.nextDouble(0.00001, 0.9));
    return sb.toString();
  }

  protected static void generateJsondata() throws Exception {
    PrintWriter f0 = new PrintWriter(
        new FileWriter("/home/amaurya/tiAnalysisData_" + System.currentTimeMillis()));
    for (int i = 0; i < 3000000; i++) {
      f0.println(genarateRandomLinesForString());
    }
    f0.close();
  }
}
