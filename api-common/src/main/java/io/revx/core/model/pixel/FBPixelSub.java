package io.revx.core.model.pixel;

import io.revx.core.model.BaseModel;

public class FBPixelSub {

  public BaseModel fbPixel;

  public BaseModel fbApp;

  public BaseModel fbAppEvent;

  public Boolean createNewFlag;

  public BaseModel getFbPixel() {
    return fbPixel;
  }

  public void setFbPixel(BaseModel fbPixel) {
    this.fbPixel = fbPixel;
  }

  public BaseModel getFbApp() {
    return fbApp;
  }

  public void setFbApp(BaseModel fbApp) {
    this.fbApp = fbApp;
  }

  public BaseModel getFbAppEvent() {
    return fbAppEvent;
  }

  public void setFbAppEvent(BaseModel fbAppEvent) {
    this.fbAppEvent = fbAppEvent;
  }

  public Boolean getCreateNewFlag() {
    return createNewFlag;
  }

  public void setCreateNewFlag(Boolean createNewFlag) {
    this.createNewFlag = createNewFlag;
  }

  @Override
  public String toString() {
    return "FBPixelSub [fbPixel=" + fbPixel + ", fbApp=" + fbApp + ", fbAppEvent=" + fbAppEvent
        + ", createNewFlag=" + createNewFlag + "]";
  }
  

}
