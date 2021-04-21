package io.revx.core.model;

@SuppressWarnings("serial")
public class State extends StatusTimeModel {

  private String code;
  private Long countryId;
  private String countryName;

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

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }



}
