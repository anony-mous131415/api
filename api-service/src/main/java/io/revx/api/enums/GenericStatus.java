/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.enums;


public enum GenericStatus {
  active(1), inactive(0);

  private Integer id; 
  private boolean is_active; 
  
  private GenericStatus(Integer id) {
    this.is_active = (id == 1);
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public boolean isActive() {
    return this.is_active;
  }

  /**
   * Gets the status from id.
   *
   * @param id the id
   * @return the status from id
   */
  public static GenericStatus getStatusFromId(Integer id) {
    if (id == null)
      return null;
    switch (id) {
      case 1:
        return active;
      case 0:
        return inactive;
      default:
        return null;
    }
  }

  public static GenericStatus getStatusFromBoolean(Boolean isActive) {
    return isActive ? active : inactive;
  }
}
