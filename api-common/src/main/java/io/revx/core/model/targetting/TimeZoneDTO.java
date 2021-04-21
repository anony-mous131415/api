package io.revx.core.model.targetting;

import io.revx.core.model.BaseModel;

public class TimeZoneDTO extends BaseModel {

  /**
   * 
   */
  private static final long serialVersionUID = 649425475766829759L;

  public String zoneName;

  public Integer country_id;

  public TimeZoneDTO() {

  }

  public TimeZoneDTO(Integer id, String name, String zone, Integer countryId) {
    super(id, name);
    this.zoneName = zone;
    this.country_id = countryId;
  }

  public TimeZoneDTO(Integer id, String name, String zone) {
    super(id, name);
    this.zoneName = zone;
  }

}
