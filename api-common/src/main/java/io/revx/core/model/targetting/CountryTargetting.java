package io.revx.core.model.targetting;

import java.util.HashMap;
import java.util.Map;
import io.revx.core.model.BaseModel;

public class CountryTargetting extends BaseModel {
  /**
  * 
  */
  private static final long serialVersionUID = 1L;
  public boolean selectAllChildren;
  public boolean selectAnyChildren;
  public Map<Integer, StateTargetting> includedStates;
  public Map<Integer, StateTargetting> excludedStates;
  public Map<Integer, StateTargetting> partiallySelectedStates;

  public CountryTargetting() {
    includedStates = new HashMap<Integer, StateTargetting>();
    excludedStates = new HashMap<Integer, StateTargetting>();
    partiallySelectedStates = new HashMap<Integer, StateTargetting>();
  }

  public CountryTargetting(long id, String name, boolean selectAllChildren, boolean selectAnyChildren,
      Map<Integer, StateTargetting> states, Map<Integer, StateTargetting> excludedStates) {
    this.id = id;
    this.name = name;
    this.selectAllChildren = selectAllChildren;
    this.selectAnyChildren = selectAnyChildren;
    this.includedStates.putAll(states);
    this.excludedStates.putAll(excludedStates);
  }

  public String toString() {
    String stringValue = "";

    stringValue += " \"id\" : ";
    stringValue += id;

    stringValue += " , \"selectAllChildren\" : ";
    stringValue += Boolean.toString(selectAllChildren);
    stringValue += " , \"includedStates\" : [ ";

    for (Map.Entry<Integer, StateTargetting> entry : includedStates.entrySet()) {
      StateTargetting state = entry.getValue();
      stringValue += " { ";
      stringValue += state.toString();
      stringValue += " } , ";
    }

    stringValue += " ] , \"excludedStates\" : [ ";

    for (Map.Entry<Integer, StateTargetting> entry : excludedStates.entrySet()) {
      StateTargetting state = entry.getValue();
      stringValue += " { ";
      stringValue += state.toString();
      stringValue += " } , ";
    }

    stringValue += " ] , \"partiallySelectedStates\" : [ ";

    for (Map.Entry<Integer, StateTargetting> entry : partiallySelectedStates.entrySet()) {
      StateTargetting state = entry.getValue();
      stringValue += " { ";
      stringValue += state.toString();
      stringValue += " } , ";
    }

    stringValue += " ] ";

    return stringValue;
  }
}
