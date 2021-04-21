package io.revx.api.service.strategy;

import io.revx.core.model.BaseEntity;


public class AdSafetyBWList implements BaseEntity {

  private static final long serialVersionUID = 2797846987958164168L;

  public AdSafetyBWList() {}

  public AdSafetyBWList(Long id) {
    this.id = id;
  }

  private Long id;

  private Long licenseeId;

  private boolean active;

  private boolean isBlackList;

  private Integer bwType;

  private String bwList;

  @Override
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isBlackList() {
    return isBlackList;
  }

  public void setBlackList(boolean isBlackList) {
    this.isBlackList = isBlackList;
  }

  public Integer getBwType() {
    return bwType;
  }

  public void setBwType(Integer bwType) {
    this.bwType = bwType;
  }

  public String getBwList() {
    return bwList;
  }

  public void setBwList(String bwList) {
    this.bwList = bwList;
  }



}
