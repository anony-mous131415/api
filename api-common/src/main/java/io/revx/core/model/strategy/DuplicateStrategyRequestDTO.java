package io.revx.core.model.strategy;

import java.io.Serializable;
import java.math.BigInteger;

public class DuplicateStrategyRequestDTO implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public String name;

  public BigInteger startTime;
  public BigInteger endTime;

  public Boolean duplicateGeoTargeting;

  public Boolean duplicateBrowserTargeting;

  public Boolean duplicateAudienceTargeting;
  
  public Boolean duplicateDmpAudienceTargeting;

  public Boolean duplicateDayPartTargeting;

  public Boolean duplicateInventoryTargeting;

  public Boolean duplicateMobileTargeting;

  public Boolean duplicatecreativesAttached;

  public Boolean duplicatePlacementTargeting;

  public Boolean duplicateConnectionTypeTargeting;

  public boolean isNative;
}
