
package io.revx.core.model.targetting;

import java.io.Serializable;

public class RTBSites implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public boolean selectAllSites;

  public TargetingObject rtbSites;


  public RTBSites() {
    selectAllSites = false;
    rtbSites = new TargetingObject();
  }

  public void cleanUp() {
    rtbSites.cleanUp();
  }

  @Override
  public String toString() {
    return "RTBSites [selectAllSites=" + selectAllSites + ", rtbSites=" + rtbSites + "]";
  }
  
  
}
