package io.revx.core.model;

import io.revx.core.enums.GeoType;

public class GeoModel extends BaseModel {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private GeoType geoType;

  public GeoType getGeoType() {
    return geoType;
  }

  public void setGeoType(GeoType geoType) {
    this.geoType = geoType;
  }


}
