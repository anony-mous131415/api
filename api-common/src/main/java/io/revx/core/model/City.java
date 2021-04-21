package io.revx.core.model;

@SuppressWarnings("serial")
public class City extends StatusTimeModel {

  private String code;
  private Long countryId;
  private Long stateId;
  private String countryName;
  private String stateName;

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Long getCountryId() {
    return countryId;
  }

  public void setCountryId(Long countryId) {
    this.countryId = countryId;
  }

  public Long getStateId() {
    return stateId;
  }

  public void setStateId(Long stateId) {
    this.stateId = stateId;
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public String getStateName() {
    return stateName;
  }

  public void setStateName(String stateName) {
    this.stateName = stateName;
  }



}
