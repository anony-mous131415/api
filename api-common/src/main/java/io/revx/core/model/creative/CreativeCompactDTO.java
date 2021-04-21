package io.revx.core.model.creative;

import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.StatusTimeModel;

public class CreativeCompactDTO extends StatusTimeModel implements BaseEntity {

  private static final long serialVersionUID = 1L;

  private Size size;
  private String content;
  private CreativeType type;
  private BaseModel advertiser;
  private String urlPath;
  private boolean dcoAd;
  private boolean nativeAd;
  private boolean isRefactored;

  public Size getSize() {
    return size;
  }

  public void setSize(Size size) {
    this.size = size;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public CreativeType getType() {
    return type;
  }

  public void setType(CreativeType type) {
    this.type = type;
  }

  public BaseModel getAdvertiser() {
    return advertiser;
  }

  public void setAdvertiser(BaseModel advertiser) {
    this.advertiser = advertiser;
  }

  public String getUrlPath() {
    return urlPath;
  }

  public void setUrlPath(String urlPath) {
    this.urlPath = urlPath;
  }

  public boolean isDcoAd() {
    return dcoAd;
  }

  public void setDcoAd(boolean dcoAd) {
    this.dcoAd = dcoAd;
  }

  public boolean isNativeAd() {
    return nativeAd;
  }

  public void setNativeAd(boolean nativeAd) {
    this.nativeAd = nativeAd;
  }
  
  public boolean isRefactored() {
    return isRefactored;
  }

  public void setRefactored(boolean isRefactored) {
    this.isRefactored = isRefactored;
  }

  @Override
  public String toString() {
    return "CreativeCompactDTO [size=" + size + ", content=" + content + ", type=" + type
        + ", advertiser=" + advertiser + ", urlPath=" + urlPath + ", dcoAd=" + dcoAd + ", nativeAd="
        + nativeAd + ", isRefactored=" + isRefactored + "]";
  }

}
