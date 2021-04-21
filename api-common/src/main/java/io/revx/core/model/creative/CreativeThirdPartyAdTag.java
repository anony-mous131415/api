package io.revx.core.model.creative;

public class CreativeThirdPartyAdTag {

  private CreativeDetails basicDetails;

  private String adTag;

  public CreativeDetails getBasicDetails() {
    return basicDetails;
  }

  public void setBasicDetails(CreativeDetails basicDetails) {
    this.basicDetails = basicDetails;
  }

  public String getAdTag() {
    return adTag;
  }

  public void setAdTag(String adTag) {
    this.adTag = adTag;
  }

  @Override
  public String toString() {
    return "CreativeThirdPartyAdTag [basicDetails=" + basicDetails + ", adTag=" + adTag + "]";
  }

}
