package io.revx.core.model.pixel;

import io.revx.core.model.BaseModel;

public class Tag extends BaseModel {

  private static final long serialVersionUID = 1L;

  private String jsSource;

  private String imgSource;

  private String appImgSource;

  public String getJsSource() {
    return jsSource;
  }

  public void setJsSource(String jsSource) {
    this.jsSource = jsSource;
  }

  public String getImgSource() {
    return imgSource;
  }

  public void setImgSource(String imgSource) {
    this.imgSource = imgSource;
  }

  public String getAppImgSource() {
    return appImgSource;
  }

  public void setAppImgSource(String appImgSource) {
    this.appImgSource = appImgSource;
  }

  @Override
  public String toString() {
    return "Tag [jsSource=" + jsSource + ", imgSource=" + imgSource + ", appImgSource="
        + appImgSource + "]";
  }

}
