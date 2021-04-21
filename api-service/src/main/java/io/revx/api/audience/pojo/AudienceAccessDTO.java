package io.revx.api.audience.pojo;

public class AudienceAccessDTO {

  public AudienceAccessDTO() {
    super();
  }

  private Boolean isDmpAccess;
  
  private Boolean isPlatformAccess;

  public Boolean getIsDmpAccess() {
    return isDmpAccess;
  }

  public void setIsDmpAccess(Boolean isDmpAccess) {
    this.isDmpAccess = isDmpAccess;
  }

  public Boolean getIsPlatformAccess() {
    return isPlatformAccess;
  }

  public void setIsPlatformAccess(Boolean isPlatformAccess) {
    this.isPlatformAccess = isPlatformAccess;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AudienceAccessDTO [isDmpAccess=").append(isDmpAccess)
        .append(", isPlatformAccess=").append(isPlatformAccess).append("]");
    return builder.toString();
  }
  
  
}
