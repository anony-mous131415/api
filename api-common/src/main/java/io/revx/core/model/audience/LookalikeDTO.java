package io.revx.core.model.audience;

import io.revx.core.model.BaseModel;


public class LookalikeDTO {

  private int enableFacebook;


  private Integer targetSize;


  private BaseModel targetCountry;


  private BaseModel baseAudience;


  public int getEnableFacebook() {
    return enableFacebook;
  }


  public void setEnableFacebook(int enableFacebook) {
    this.enableFacebook = enableFacebook;
  }


  public Integer getTargetSize() {
    return targetSize;
  }


  public void setTargetSize(Integer targetSize) {
    this.targetSize = targetSize;
  }


  public BaseModel getTargetCountry() {
    return targetCountry;
  }


  public void setTargetCountry(BaseModel targetCountry) {
    this.targetCountry = targetCountry;
  }


  public BaseModel getBaseAudience() {
    return baseAudience;
  }


  public void setBaseAudience(BaseModel baseAudience) {
    this.baseAudience = baseAudience;
  }



}
