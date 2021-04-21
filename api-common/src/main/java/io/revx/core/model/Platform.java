package io.revx.core.model;

import java.math.BigDecimal;

public class Platform extends BaseModel {
  private static final long serialVersionUID = 3161833424233301590L;



  private BigDecimal ctrFactor;

  private BigDecimal cvrFactor;



  public String getDescriptiveName() {
    return "Platform";
  }

  /**
   * @return the ctrFactor
   */
  public BigDecimal getCtrFactor() {
    return ctrFactor;
  }

  /**
   * @param ctrFactor the ctrFactor to set
   */
  public void setCtrFactor(BigDecimal ctrFactor) {
    this.ctrFactor = ctrFactor;
  }

  /**
   * @return the cvrFactor
   */
  public BigDecimal getCvrFactor() {
    return cvrFactor;
  }

  /**
   * @param cvrFactor the cvrFactor to set
   */
  public void setCvrFactor(BigDecimal cvrFactor) {
    this.cvrFactor = cvrFactor;
  }

}
