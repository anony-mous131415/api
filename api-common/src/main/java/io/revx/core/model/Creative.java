package io.revx.core.model;

@SuppressWarnings("serial")
public class Creative extends StatusTimeModel {
  public Creative(long id, String name) {
    super(id, name);
  }

  public Creative() {

  }

  private long licenseeId;

  private long advertiserId;

  private Integer height;

  private Integer width;

  private String imageUrl;

  private String creativeType;

  private boolean refactor;

  public long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(long advertiserId) {
    this.advertiserId = advertiserId;
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

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getCreativeType() {
    return creativeType;
  }

  public void setCreativeType(String type) {
    this.creativeType = type;
  }

  public boolean isRefactor() {
    return refactor;
  }

  public void setRefactor(boolean refactor) {
    this.refactor = refactor;
  }


}
