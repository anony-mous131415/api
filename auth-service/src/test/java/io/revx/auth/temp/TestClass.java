package io.revx.auth.temp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class TestClass {

  public static long[] depair(long z) {
    long t = (int) (Math.floor((Math.sqrt(8 * z + 1) - 1) / 2));
    long x = t * (t + 3) / 2 - z;
    long y = z - t * (t + 1) / 2;
    return new long[] {x, y}; // Returning an array containing the two numbers
  }

  public static void main(String[] args) {
    System.out.println(Arrays.toString(depair(484896750)));

    List<String> list = new ArrayList<String>();
    list.add("123");
    list.add("234");
    System.out.println(StringUtils.join(list, ","));
  }

}
