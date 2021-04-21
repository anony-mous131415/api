package io.revx.core.model;

import io.revx.core.model.creative.CampaignType;
import io.revx.core.model.creative.GeneratedUrlType;

@SuppressWarnings("serial")
public class ClickDestination extends StatusTimeModel implements BaseEntity {

  private Long advertiserId;
  private Long licenseeId;
  private String clickUrl;
  private String webClickUrl;
  private String iosCLickUrl;
  private String androidClickUrl;
  private String serverTrackingUrl;
  private String webS2sClickTrackingUrl;
  private String iosS2sClickTrackingUrl;
  private String androidS2sClickTrackingUrl;
  private String webImpressionTracker;
  private String iosImpressionTracker;
  private String androidImpressionTracker;
  private boolean isRefactored;
  private boolean isDco;
  private CampaignType campaignType;
  private GeneratedUrlType generatedUrlType;
  private boolean skadTarget;


  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public String getClickUrl() {
    return clickUrl;
  }

  public void setClickUrl(String clickUrl) {
    this.clickUrl = clickUrl;
  }

  public String getWebClickUrl() {
    return webClickUrl;
  }

  public void setWebClickUrl(String webClickUrl) {
    this.webClickUrl = webClickUrl;
  }

  public String getIosCLickUrl() {
    return iosCLickUrl;
  }

  public void setIosCLickUrl(String iosCLickUrl) {
    this.iosCLickUrl = iosCLickUrl;
  }

  public String getAndroidClickUrl() {
    return androidClickUrl;
  }

  public void setAndroidClickUrl(String androidClickUrl) {
    this.androidClickUrl = androidClickUrl;
  }

  public String getServerTrackingUrl() {
    return serverTrackingUrl;
  }

  public void setServerTrackingUrl(String serverTrackingUrl) {
    this.serverTrackingUrl = serverTrackingUrl;
  }

  public String getWebS2sClickTrackingUrl() {
    return webS2sClickTrackingUrl;
  }

  public void setWebS2sClickTrackingUrl(String webS2sClickTrackingUrl) {
    this.webS2sClickTrackingUrl = webS2sClickTrackingUrl;
  }

  public String getIosS2sClickTrackingUrl() {
    return iosS2sClickTrackingUrl;
  }

  public void setIosS2sClickTrackingUrl(String iosS2sClickTrackingUrl) {
    this.iosS2sClickTrackingUrl = iosS2sClickTrackingUrl;
  }

  public String getAndroidS2sClickTrackingUrl() {
    return androidS2sClickTrackingUrl;
  }

  public void setAndroidS2sClickTrackingUrl(String androidS2sClickTrackingUrl) {
    this.androidS2sClickTrackingUrl = androidS2sClickTrackingUrl;
  }

  public String getWebImpressionTracker() {
    return webImpressionTracker;
  }

  public void setWebImpressionTracker(String webImpressionTracker) {
    this.webImpressionTracker = webImpressionTracker;
  }

  public String getIosImpressionTracker() {
    return iosImpressionTracker;
  }

  public void setIosImpressionTracker(String iosImpressionTracker) {
    this.iosImpressionTracker = iosImpressionTracker;
  }

  public String getAndroidImpressionTracker() {
    return androidImpressionTracker;
  }

  public void setAndroidImpressionTracker(String androidImpressionTracker) {
    this.androidImpressionTracker = androidImpressionTracker;
  }

  public boolean isRefactored() {
    return isRefactored;
  }

  public void setRefactored(boolean isRefactored) {
    this.isRefactored = isRefactored;
  }

  public boolean isDco() {
    return isDco;
  }

  public void setDco(boolean isDco) {
    this.isDco = isDco;
  }

  public CampaignType getCampaignType() {
    return campaignType;
  }

  public void setCampaignType(CampaignType campaignType) {
    this.campaignType = campaignType;
  }
  
  
  public GeneratedUrlType getGeneratedUrlType() {
	  return generatedUrlType;
  }

  public void setGeneratedUrlType(GeneratedUrlType genUrl) {
	  this.generatedUrlType = genUrl;
  }

  public boolean getSkadTarget() {
    return skadTarget;
  }

  public void setSkadTarget(boolean skadTarget) {
    this.skadTarget = skadTarget;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((advertiserId == null) ? 0 : advertiserId.hashCode());
    result = prime * result + ((androidClickUrl == null) ? 0 : androidClickUrl.hashCode());
    result = prime * result
        + ((androidImpressionTracker == null) ? 0 : androidImpressionTracker.hashCode());
    result = prime * result
        + ((androidS2sClickTrackingUrl == null) ? 0 : androidS2sClickTrackingUrl.hashCode());
    result = prime * result + ((campaignType == null) ? 0 : campaignType.hashCode());
    result = prime * result + ((clickUrl == null) ? 0 : clickUrl.hashCode());
    result = prime * result + ((iosCLickUrl == null) ? 0 : iosCLickUrl.hashCode());
    result =
        prime * result + ((iosImpressionTracker == null) ? 0 : iosImpressionTracker.hashCode());
    result =
        prime * result + ((iosS2sClickTrackingUrl == null) ? 0 : iosS2sClickTrackingUrl.hashCode());
    result = prime * result + (isDco ? 1231 : 1237);
    result = prime * result + (isRefactored ? 1231 : 1237);
    result = prime * result + ((licenseeId == null) ? 0 : licenseeId.hashCode());
    result = prime * result + ((serverTrackingUrl == null) ? 0 : serverTrackingUrl.hashCode());
    result = prime * result + ((webClickUrl == null) ? 0 : webClickUrl.hashCode());
    result =
        prime * result + ((webImpressionTracker == null) ? 0 : webImpressionTracker.hashCode());
    result =
        prime * result + ((webS2sClickTrackingUrl == null) ? 0 : webS2sClickTrackingUrl.hashCode());
    result = prime * result + ((generatedUrlType == null) ? 0 : generatedUrlType.hashCode());
    result = prime * result + (skadTarget ? 1231 : 1237);

    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ClickDestination other = (ClickDestination) obj;
    if (advertiserId == null) {
      if (other.advertiserId != null)
        return false;
    } else if (!advertiserId.equals(other.advertiserId))
      return false;
    if (androidClickUrl == null) {
      if (other.androidClickUrl != null)
        return false;
    } else if (!androidClickUrl.equals(other.androidClickUrl))
      return false;
    if (androidImpressionTracker == null) {
      if (other.androidImpressionTracker != null)
        return false;
    } else if (!androidImpressionTracker.equals(other.androidImpressionTracker))
      return false;
    if (androidS2sClickTrackingUrl == null) {
      if (other.androidS2sClickTrackingUrl != null)
        return false;
    } else if (!androidS2sClickTrackingUrl.equals(other.androidS2sClickTrackingUrl))
      return false;
    if (campaignType != other.campaignType)
      return false;
    if (clickUrl == null) {
      if (other.clickUrl != null)
        return false;
    } else if (!clickUrl.equals(other.clickUrl))
      return false;
    if (iosCLickUrl == null) {
      if (other.iosCLickUrl != null)
        return false;
    } else if (!iosCLickUrl.equals(other.iosCLickUrl))
      return false;
    if (iosImpressionTracker == null) {
      if (other.iosImpressionTracker != null)
        return false;
    } else if (!iosImpressionTracker.equals(other.iosImpressionTracker))
      return false;
    if (iosS2sClickTrackingUrl == null) {
      if (other.iosS2sClickTrackingUrl != null)
        return false;
    } else if (!iosS2sClickTrackingUrl.equals(other.iosS2sClickTrackingUrl))
      return false;
    if (isDco != other.isDco)
      return false;
    if (isRefactored != other.isRefactored)
      return false;
    if (licenseeId == null) {
      if (other.licenseeId != null)
        return false;
    } else if (!licenseeId.equals(other.licenseeId))
      return false;
    if (serverTrackingUrl == null) {
      if (other.serverTrackingUrl != null)
        return false;
    } else if (!serverTrackingUrl.equals(other.serverTrackingUrl))
      return false;
    if (webClickUrl == null) {
      if (other.webClickUrl != null)
        return false;
    } else if (!webClickUrl.equals(other.webClickUrl))
      return false;
    if (webImpressionTracker == null) {
      if (other.webImpressionTracker != null)
        return false;
    } else if (!webImpressionTracker.equals(other.webImpressionTracker))
      return false;
    if (webS2sClickTrackingUrl == null) {
      if (other.webS2sClickTrackingUrl != null)
        return false;
    } else if (!webS2sClickTrackingUrl.equals(other.webS2sClickTrackingUrl))
      return false;
    
    if (generatedUrlType != other.generatedUrlType)
    	return false;
    if ( skadTarget != other.skadTarget) {
      return false;
    }
    return true;
  }


  @Override
  public String toString() {
    return "ClickDestination{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", modifiedTime=" + modifiedTime +
            ", modifiedBy=" + modifiedBy +
            ", advertiserId=" + advertiserId +
            ", licenseeId=" + licenseeId +
            ", clickUrl='" + clickUrl + '\'' +
            ", webClickUrl='" + webClickUrl + '\'' +
            ", iosCLickUrl='" + iosCLickUrl + '\'' +
            ", androidClickUrl='" + androidClickUrl + '\'' +
            ", serverTrackingUrl='" + serverTrackingUrl + '\'' +
            ", webS2sClickTrackingUrl='" + webS2sClickTrackingUrl + '\'' +
            ", iosS2sClickTrackingUrl='" + iosS2sClickTrackingUrl + '\'' +
            ", androidS2sClickTrackingUrl='" + androidS2sClickTrackingUrl + '\'' +
            ", webImpressionTracker='" + webImpressionTracker + '\'' +
            ", iosImpressionTracker='" + iosImpressionTracker + '\'' +
            ", androidImpressionTracker='" + androidImpressionTracker + '\'' +
            ", isRefactored=" + isRefactored +
            ", isDco=" + isDco +
            ", campaignType=" + campaignType +
            ", generatedUrlType=" + generatedUrlType +
            ", isSkadTarget=" + skadTarget +
            '}';
  }

}
