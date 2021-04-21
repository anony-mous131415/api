package io.revx.core.model.requests;


public class SlicexRequest extends DashboardRequest {

  private Duration compareToDuration;

  public SlicexRequest() {
    super();
  }

  public Duration getCompareToDuration() {
    return compareToDuration;
  }

  public void setCompareToDuration(Duration compareToDuration) {
    this.compareToDuration = compareToDuration;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("SlicexRequest [compareToDuration=");
    builder.append(compareToDuration);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }

}
