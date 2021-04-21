package io.revx.core.model.catalog;

import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;

public class CatalogFeed extends BaseModel implements BaseEntity {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Long advertiserId;

  private boolean active;

  private String source;

  private Integer updateFrequency;

  private Long lastUpdated;

  private String updatedStatus;

  private Long objectsFound;

  private Long objectsUpdated;

  private Long successRate;

  private Long variablesMapped;

  private Long objectsParsed;

  private Long feedParserRan;

  private Integer isApiBased;

  @Override
  public Long getId() {
    return super.getId();
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Integer getUpdateFrequency() {
    return updateFrequency;
  }

  public void setUpdateFrequency(Integer updateFrequency) {
    this.updateFrequency = updateFrequency;
  }

  public Long getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(Long lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public String getUpdatedStatus() {
    return updatedStatus;
  }

  public void setUpdatedStatus(String updatedStatus) {
    this.updatedStatus = updatedStatus;
  }

  public Long getObjectsFound() {
    return objectsFound;
  }

  public void setObjectsFound(Long objectsFound) {
    this.objectsFound = objectsFound;
  }

  public Long getObjectsUpdated() {
    return objectsUpdated;
  }

  public void setObjectsUpdated(Long objectsUpdated) {
    this.objectsUpdated = objectsUpdated;
  }

  public Long getSuccessRate() {
    return successRate;
  }

  public void setSuccessRate(Long successRate) {
    this.successRate = successRate;
  }

  public Long getVariablesMapped() {
    return variablesMapped;
  }

  public void setVariablesMapped(Long variablesMapped) {
    this.variablesMapped = variablesMapped;
  }

  public Long getObjectsParsed() {
    return objectsParsed;
  }

  public void setObjectsParsed(Long objectsParsed) {
    this.objectsParsed = objectsParsed;
  }

  public Long getFeedParserRan() {
    return feedParserRan;
  }

  public void setFeedParserRan(Long feedParserRan) {
    this.feedParserRan = feedParserRan;
  }

  public Integer getIsApiBased() {
    return isApiBased;
  }

  public void setIsApiBased(Integer isApiBased) {
    this.isApiBased = isApiBased;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public String toString() {
    return "CatalogFeed [advertiserId=" + advertiserId + ", active=" + active + ", source=" + source
        + ", updateFrequency=" + updateFrequency + ", lastUpdated=" + lastUpdated
        + ", updatedStatus=" + updatedStatus + ", objectsFound=" + objectsFound
        + ", objectsUpdated=" + objectsUpdated + ", successRate=" + successRate
        + ", variablesMapped=" + variablesMapped + ", objectsParsed=" + objectsParsed
        + ", feedParserRan=" + feedParserRan + ", isApiBased=" + isApiBased + "]";
  }

}
