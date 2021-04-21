package io.revx.core.model;

import java.math.BigDecimal;



public class OSVersionMaster extends BaseModel {

  private static final long serialVersionUID = 8539786293019942378L;


  private BigDecimal version;



  private Long osId;

  private boolean active;

  public BigDecimal getVersion() {
    return version;
  }

  public void setVersion(BigDecimal version) {
    this.version = version;
  }

  public Long getOsId() {
    return osId;
  }

  public void setOsId(Long osId) {
    this.osId = osId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }


}
