package io.revx.core.model.audience;

public class PlatformAudienceDTO {

  private String container_id;
  
  private String url;
  
  private String api_key;
  
  private Integer segment_type;

  public String getContainer_id() {
    return container_id;
  }

  public void setContainer_id(String container_id) {
    this.container_id = container_id;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getApi_key() {
    return api_key;
  }

  public void setApi_key(String api_key) {
    this.api_key = api_key;
  }

  public Integer getSegment_type() {
    return segment_type;
  }

  public void setSegment_type(Integer segment_type) {
    this.segment_type = segment_type;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PlatformAudienceDTO [container_id=").append(container_id).append(", url=")
        .append(url).append(", api_key=").append(api_key).append(", segment_type=")
        .append(segment_type).append("]");
    return builder.toString();
  } 
  
  
}
