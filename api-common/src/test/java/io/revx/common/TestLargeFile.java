package io.revx.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestLargeFile {

  public static void main(String[] args) throws ParseException {
    long epoc = System.currentTimeMillis();
    SimpleDateFormat month_date = new SimpleDateFormat("MMM_dd", Locale.ENGLISH);
    String month_name = month_date.format(new Date(epoc));
    System.out.println("Month :" + month_name);

  }
}
