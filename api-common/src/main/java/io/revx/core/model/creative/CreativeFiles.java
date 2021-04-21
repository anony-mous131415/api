package io.revx.core.model.creative;

public class CreativeFiles {

  private String name;
  private String filePath;
  private String ContentType;
  private VideoProperties videoAttribute;
  private String size;
  private CreativeType type;
  private boolean isNative;
  private boolean isDco;
  private Integer noOfSlots;
  private String macroList;
  private Integer height;
  private Integer width;
  private String errorMsg;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getContentType() {
    return ContentType;
  }

  public void setContentType(String contentType) {
    ContentType = contentType;
  }

  public VideoProperties getVideoAttribute() {
    return videoAttribute;
  }

  public void setVideoAttribute(VideoProperties videoAttribute) {
    this.videoAttribute = videoAttribute;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }

  public CreativeType getType() {
    return type;
  }

  public void setType(CreativeType type) {
    this.type = type;
  }

  public boolean isNative() {
    return isNative;
  }

  public void setNative(boolean isNative) {
    this.isNative = isNative;
  }
  
  public boolean isDco() {
    return isDco;
  }

  public void setDco(boolean isDco) {
    this.isDco = isDco;
  }

  public Integer getNoOfSlots() {
    return noOfSlots;
  }

  public void setNoOfSlots(Integer noOfSlots) {
    this.noOfSlots = noOfSlots;
  }

  public String getMacroList() {
    return macroList;
  }

  public void setMacroList(String macroList) {
    this.macroList = macroList;
  }

  @Override
  public String toString() {
    return "CreativeFiles{" +
            "name='" + name + '\'' +
            ", location='" + filePath + '\'' +
            ", ContentType='" + ContentType + '\'' +
            ", videoAttribute=" + videoAttribute +
            ", size='" + size + '\'' +
            ", type=" + type +
            ", isNative=" + isNative +
            ", isDco=" + isDco +
            ", noOfSlots=" + noOfSlots +
            ", macroList='" + macroList + '\'' +
            ", height=" + height +
            ", width=" + width +
            ", errorMsg='" + errorMsg + '\'' +
            '}';
  }


}
