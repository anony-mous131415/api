package io.revx.core.model.audience;

public class DmpSyncAudienceDTO {

  public Integer id;
  public String message;
  public Integer getId() {
    return id;
  }
  public void setId(Integer id) {
    this.id = id;
  }
  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DmpSyncAudienceDTO [id=").append(id).append(", message=").append(message)
        .append("]");
    return builder.toString();
  }
  
}
