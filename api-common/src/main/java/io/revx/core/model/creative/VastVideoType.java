package io.revx.core.model.creative;

public enum VastVideoType {


  FLV("video/flv", ".flv"), MP4("video/mp4", ".mp4");

  private String type;

  private String extension;

  private VastVideoType(String type, String extension) {
    this.type = type;
    this.extension = extension;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }



}
