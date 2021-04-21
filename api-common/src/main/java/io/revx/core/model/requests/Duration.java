package io.revx.core.model.requests;

import java.io.Serializable;

public class Duration implements Serializable {

  private static final long serialVersionUID = 1L;

  public Long startTimeStamp;

  public Long endTimeStamp;

  public Long getStartTimeStamp() {
    return startTimeStamp;
  }

  public void setStartTimeStamp(Long startTimeStamp) {
    this.startTimeStamp = startTimeStamp;
  }

  public Long getEndTimeStamp() {
    return endTimeStamp;
  }

  public void setEndTimeStamp(Long endTimeStamp) {
    this.endTimeStamp = endTimeStamp;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Duration [startTimeStamp=");
    builder.append(startTimeStamp);
    builder.append(", endTimeStamp=");
    builder.append(endTimeStamp);
    builder.append("]");
    return builder.toString();
  }

}
