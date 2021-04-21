package io.revx.core.model;

public class RegionMaster extends BaseModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public RegionMaster(Long countryId, boolean active) {
    super();
    this.countryId = countryId;
    this.active = active;
  }
  
  public RegionMaster() {}

  public Long countryId;
  private boolean active;

  public Long getCountryId() {
    return countryId;
  }

  public void setCountryId(Long countryId) {
    this.countryId = countryId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public String toString() {
    return "RegionMaster [countryId=" + countryId + ", active=" + active + ", id=" + id + ", name="
        + name + "]";
  }

}
