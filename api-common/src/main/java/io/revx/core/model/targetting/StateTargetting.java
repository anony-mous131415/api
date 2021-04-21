package io.revx.core.model.targetting;

import java.util.HashMap;
import java.util.Map;
import io.revx.core.model.BaseModel;

public class StateTargetting extends BaseModel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public boolean selectAllChildren;
  public boolean selectAnyChildren;
  public Map<Integer, CityTargetting> includedCities;
  public Map<Integer, CityTargetting> excludedCities;

  public StateTargetting() {
    includedCities = new HashMap<Integer, CityTargetting>();
    excludedCities = new HashMap<Integer, CityTargetting>();
  }

  public String toString() {
    String stringValue = "";

    stringValue += " \"id\" : ";
    stringValue += id;

    stringValue += " , \"selectAllChildren\" : ";
    stringValue += Boolean.toString(selectAllChildren);
    stringValue += " , \"includedCities\" : [ ";

    for (Map.Entry<Integer, CityTargetting> entry : includedCities.entrySet()) {
      CityTargetting city = entry.getValue();
      stringValue += " { ";
      stringValue += city.toString();
      stringValue += " } , ";
    }

    stringValue += " ] , \"excludedCities\" : [ ";

    for (Map.Entry<Integer, CityTargetting> entry : excludedCities.entrySet()) {
      CityTargetting city = entry.getValue();
      stringValue += " { ";
      stringValue += city.toString();
      stringValue += " } , ";
    }

    stringValue += " ] ";

    return stringValue;
  }
}
