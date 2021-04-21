package io.revx.core.model.creative;

public class VideoAttributes implements java.io.Serializable {

  private static final long serialVersionUID = -7172111301883150608L;

  private Long id;

  private Long durationInSecs;
  
  private String bitRate;

  private CreativeVideoFormat format;

  private Integer isSkippable;

  private Integer videoWidth;

  private Integer videoHeight;

  private String videoPath;

  private VastProtocol vastProtocol;
  
  private Long companionCreativeId;

  private VideoCampanionType companionType;
  
  private FileType companionContentType;

  private String companionPath;

  private Boolean hasCompanion;

  private Integer companionWidth;

  private Integer companionHeight;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getDurationInSecs() {
    return durationInSecs;
  }

  public void setDurationInSecs(Long durationInSecs) {
    this.durationInSecs = durationInSecs;
  }

  public Integer getIsSkippable() {
    return isSkippable;
  }

  public void setIsSkippable(Integer isSkippable) {
    this.isSkippable = isSkippable;
  }

  public Integer getVideoWidth() {
    return videoWidth;
  }

  public void setVideoWidth(Integer videoWidth) {
    this.videoWidth = videoWidth;
  }

  public Integer getVideoHeight() {
    return videoHeight;
  }

  public void setVideoHeight(Integer videoHeight) {
    this.videoHeight = videoHeight;
  }

  public VastProtocol getVastProtocol() {
    return vastProtocol;
  }

  public void setVastProtocol(VastProtocol vastProtocol) {
    this.vastProtocol = vastProtocol;
  }
  
  public String getBitRate() {
    return bitRate;
  }

  public void setBitRate(String bitRate) {
    this.bitRate = bitRate;
  }

  public VideoCampanionType getCompanionType() {
    return companionType;
  }

  public void setCompanionType(VideoCampanionType companionType) {
    this.companionType = companionType;
  }

  public Integer getCompanionWidth() {
    return companionWidth;
  }

  public void setCompanionWidth(Integer companionWidth) {
    this.companionWidth = companionWidth;
  }

  public Integer getCompanionHeight() {
    return companionHeight;
  }

  public void setCompanionHeight(Integer companionHeight) {
    this.companionHeight = companionHeight;
  }

  public String getVideoPath() {
    return videoPath;
  }

  public void setVideoPath(String videoPath) {
    this.videoPath = videoPath;
  }

  public String getCompanionPath() {
    return companionPath;
  }

  public void setCompanionPath(String companionPath) {
    this.companionPath = companionPath;
  }

  public CreativeVideoFormat getFormat() {
    return format;
  }

  public void setFormat(CreativeVideoFormat format) {
    this.format = format;
  }

  public Boolean getHasCompanion() {
    return hasCompanion;
  }

  public Long getCompanionCreativeId() {
    return companionCreativeId;
  }
  
  public FileType getCompanionContentType() {
    return companionContentType;
  }

  public void setCompanionContentType(FileType companionContentType) {
    this.companionContentType = companionContentType;
  }

  public void setCompanionCreativeId(Long companionCreativeId) {
    this.companionCreativeId = companionCreativeId;
  }

  public void setHasCompanion(Boolean hasCompanion) {
    this.hasCompanion = hasCompanion;
  }

  @Override
  public String toString() {
    return "VideoAttributes [id=" + id + ", durationInSecs=" + durationInSecs + ", bitRate="
        + bitRate + ", format=" + format + ", isSkippable=" + isSkippable + ", videoWidth="
        + videoWidth + ", videoHeight=" + videoHeight + ", videoPath=" + videoPath
        + ", vastProtocol=" + vastProtocol + ", companionCreativeId=" + companionCreativeId
        + ", companionType=" + companionType + ", companionContentType=" + companionContentType
        + ", companionPath=" + companionPath + ", hasCompanion=" + hasCompanion
        + ", companionWidth=" + companionWidth + ", companionHeight=" + companionHeight + "]";
  }

}
