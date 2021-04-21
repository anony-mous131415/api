package io.revx.core.model;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

@SuppressWarnings("serial")
public class ListCSVDashboardData extends DashboardMetrics {

  @CsvBindByName(column = "Id")
  @CsvBindByPosition(position = 0)
  private Long id;

  @CsvBindByName(column = "Name")
  @CsvBindByPosition(position = 1)
  private String name;


  public Long getId() {
    id = super.getId();
    return id;
  }

  public String getName() {
    name = super.getName();
    return name;
  }

}
