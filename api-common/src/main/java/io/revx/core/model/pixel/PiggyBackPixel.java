package io.revx.core.model.pixel;

import io.revx.core.model.BaseModel;

public class PiggyBackPixel {

  public enum TagType {
    Image, JavaScript
  };

  public enum TagUserType {
    SegmentPixel, PublisherPixel, ThirdPartyPixel
  };

  public enum LoadType {
    AlwaysAllPubs, AlwaysSelectPubs, ConverionRecordedAllPubs, ConverionRecordedSelectPubs
  };

  public Integer id;

  public BaseModel pixel;

  public String tag;

  public TagType tagType;

  public TagUserType tagUserType;

  public BaseModel tagUser;

  public LoadType loadType;

  public Integer status;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public BaseModel getPixel() {
    return pixel;
  }

  public void setPixel(BaseModel pixel) {
    this.pixel = pixel;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public TagType getTagType() {
    return tagType;
  }

  public void setTagType(TagType tagType) {
    this.tagType = tagType;
  }

  public TagUserType getTagUserType() {
    return tagUserType;
  }

  public void setTagUserType(TagUserType tagUserType) {
    this.tagUserType = tagUserType;
  }

  public BaseModel getTagUser() {
    return tagUser;
  }

  public void setTagUser(BaseModel tagUser) {
    this.tagUser = tagUser;
  }

  public LoadType getLoadType() {
    return loadType;
  }

  public void setLoadType(LoadType loadType) {
    this.loadType = loadType;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "PiggyBackPixel [id=" + id + ", pixel=" + pixel + ", tag=" + tag + ", tagType=" + tagType
        + ", tagUserType=" + tagUserType + ", tagUser=" + tagUser + ", loadType=" + loadType
        + ", status=" + status + "]";
  }
  
  

}
