package io.revx.core.model.creative;

import java.util.List;

public class CreativeMockUpsDTO {

  private CreativeDetails basicDetails;

  private List<CreativeFiles> uploadedFiles;

  public CreativeDetails getBasicDetails() {
    return basicDetails;
  }

  public void setBasicDetails(CreativeDetails basicDetails) {
    this.basicDetails = basicDetails;
  }

  public List<CreativeFiles> getUploadedFiles() {
    return uploadedFiles;
  }

  public void setUploadedFiles(List<CreativeFiles> uploadedFiles) {
    this.uploadedFiles = uploadedFiles;
  }

  @Override
  public String toString() {
    return "CreativeMockUpsDTO [basicDetails=" + basicDetails + ", uploadedFiles=" + uploadedFiles
        + "]";
  }


}
