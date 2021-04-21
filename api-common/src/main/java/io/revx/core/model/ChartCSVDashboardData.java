package io.revx.core.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

@SuppressWarnings("serial")
public class ChartCSVDashboardData extends DashboardMetrics {

  public ChartCSVDashboardData(long id, String name) {
    super(id, name);
  }

  private static String DATE_FORMAT = "yyyy-MM-dd hh:mm";
  private static String DAY_FORMAT = "EEE";

  public ChartCSVDashboardData() {}

  @CsvBindByName(column = "Start Date")
  @CsvBindByPosition(position = 0)
  protected Date date;

  @CsvBindByName(column = "Day Of Week")
  @CsvBindByPosition(position = 1)
  protected String day;


  public String getDate() {
    try {
      if (date != null) {
        String dd = new SimpleDateFormat(DATE_FORMAT).format(date);
        return dd;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

  public void setDate(Date endDate) {
    this.date = endDate;
  }

  public String getDay() {
    try {
      if (date != null) {
        String dd = new SimpleDateFormat(DAY_FORMAT).format(date);
        return dd;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return "";
  }

}
