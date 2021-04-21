package io.revx.core.model.audience;

import java.util.List;

public class DmpAudienceDTO {

  private Integer start;
  private Integer limit;
  private Long segment_count;
  private List<DmpAudience> segments;
  private Integer statuscode;
  private String msg;
  
  
  public Integer getStatuscode() {
    return statuscode;
  }
  public void setStatuscode(Integer statuscode) {
    this.statuscode = statuscode;
  }
  public String getMsg() {
    return msg;
  }
  public void setMsg(String msg) {
    this.msg = msg;
  }
  public Integer getStart() {
    return start;
  }
  public void setStart(Integer start) {
    this.start = start;
  }
  public Integer getLimit() {
    return limit;
  }
  public void setLimit(Integer limit) {
    this.limit = limit;
  }
  public Long getSegment_count() {
    return segment_count;
  }
  public void setSegment_count(Long segment_count) {
    this.segment_count = segment_count;
  }
  public List<DmpAudience> getSegments() {
    return segments;
  }
  public void setSegments(List<DmpAudience> segments) {
    this.segments = segments;
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DmpAudienceDTO [start=").append(start).append(", limit=").append(limit)
        .append(", segment_count=").append(segment_count).append(", segments=").append(segments)
        .append(", statuscode=").append(statuscode).append(", msg=").append(msg).append("]");
    return builder.toString();
  }
  
  
}
