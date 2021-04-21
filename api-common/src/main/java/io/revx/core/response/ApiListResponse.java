package io.revx.core.response;

import java.util.List;

public class ApiListResponse<T> {
  private int totalNoOfRecords;
  private List<T> data;

  public ApiListResponse() {
    super();
  }

  public ApiListResponse(int totalNoOfRecords, List<T> data) {
    super();
    this.totalNoOfRecords = totalNoOfRecords;
    this.data = data;
  }

  public int getTotalNoOfRecords() {
    return totalNoOfRecords;
  }

  public void setTotalNoOfRecords(int totalNoOfRecords) {
    this.totalNoOfRecords = totalNoOfRecords;
  }

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }



}
