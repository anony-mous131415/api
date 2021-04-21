/*
 * Copyright 2012 Komli Media Inc. All Rights Reserved. KOMLI MEDIA PROPRIETARY/CONFIDENTIAL. Use is
 * subject to license terms.
 * 
 * @version 1.0, 08-Aug-2012
 * 
 * @author Rajat Bhushan
 */
package io.revx.core.model.targetting;

import java.util.HashMap;
import java.util.Map;

public class Geographies {

  public boolean selectAllCountries;
  public boolean selectAnyCountries;
  public Map<Integer, CountryTargetting> includedCountries;
  public Map<Integer, CountryTargetting> excludedCountries;
  public Map<Integer, CountryTargetting> partiallySelectedCountries;


  public Geographies() {
    includedCountries = new HashMap<Integer, CountryTargetting>();
    excludedCountries = new HashMap<Integer, CountryTargetting>();
    partiallySelectedCountries = new HashMap<Integer, CountryTargetting>();
  }

  public String toString() {
    String stringValue = "{ ";

    stringValue += " \"selectAllCountries\" : ";
    stringValue += Boolean.toString(selectAllCountries);
    stringValue += " , \"includedCountries\" : [ ";

    for (Map.Entry<Integer, CountryTargetting> entry : includedCountries.entrySet()) {
      CountryTargetting cntry = entry.getValue();
      stringValue += " { ";
      stringValue += cntry.toString();
      stringValue += " } , ";
    }

    stringValue += " ] , \"excludedCountries\" : [ ";

    for (Map.Entry<Integer, CountryTargetting> entry : excludedCountries.entrySet()) {
      CountryTargetting cntry = entry.getValue();
      stringValue += " { ";
      stringValue += cntry.toString();
      stringValue += " } , ";
    }

    stringValue += " ] , \"partiallySelectedCountries\" : [ ";

    for (Map.Entry<Integer, CountryTargetting> entry : partiallySelectedCountries.entrySet()) {
      CountryTargetting cntry = entry.getValue();
      stringValue += " { ";
      stringValue += cntry.toString();
      stringValue += " } , ";
    }

    stringValue += " ] }";

    return stringValue;
  }

}
