package io.revx.core.model.targetting;

import io.revx.core.model.BaseModel;

public class CityTargetting extends BaseModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public String toString() {
    String stringValue = "";

    stringValue += " \"id\" : ";
    stringValue += id;

    return stringValue;
  }
}
