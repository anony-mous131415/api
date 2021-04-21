package io.revx.core.model.targetting;

import java.util.List;
import io.revx.core.model.BaseModel;

public class TargetingDTO {
  private List<BaseModel> countries;
  private List<BaseModel> regions;
  private List<BaseModel> cities;

  public List<BaseModel> getCountries() {
    return countries;
  }

  public void setCountries(List<BaseModel> countries) {
    this.countries = countries;
  }

  public List<BaseModel> getRegions() {
    return regions;
  }

  public void setRegions(List<BaseModel> regions) {
    this.regions = regions;
  }

  public List<BaseModel> getCities() {
    return cities;
  }

  public void setCities(List<BaseModel> cities) {
    this.cities = cities;
  }

}
