package io.revx.api.mysql.dco.entity.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "FeedInfo")
public class FeedInfoEntity implements Serializable {

  private static final long serialVersionUID = -712298494407450025L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "fi_id", nullable = false)
  private Long id;

  @Column(name = "fi_name", length = 50, nullable = false)
  private String name;

  @Column(name = "fi_advertiser_id", nullable = false)
  private Long advertiserId;

  @Column(name = "fi_feed_location", length = 100, nullable = false)
  private String feedLocation;

  @Column(name = "fi_update_frequency_secs", nullable = false)
  private Long updateFrequencyInSeconds;

  @Column(name = "fi_is_active", columnDefinition = "BIT", length = 1, nullable = false)
  private Boolean isActive;

  @Column(name = "fi_next_processing")
  private Long nextProcessing;

  @Column(name = "fi_is_auth_required", columnDefinition = "BIT", length = 1, nullable = false)
  private Boolean isAuthRequired; // default value = 0

  @Column(name = "fi_auth_user", length = 30)
  private String authUser;

  @Column(name = "fi_auth_password", length = 30)
  private String authPassword;

  @Column(name = "fi_latest_md5sum", length = 60)
  private String latestMd5Sum;

  @Column(name = "fi_last_modified_ms")
  private Long lastModifiedInMillis;

  @Column(name = "fi_status", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private ProcessingStatus status;

  @Column(name = "fi_api_based", columnDefinition = "BIT", length = 1, nullable = false)
  private Boolean isApiBased;

  
  public Long getId() {
    return id;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public Long getAdvertiserId() {
    return advertiserId;
  }


  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }


  public String getFeedLocation() {
    return feedLocation;
  }


  public void setFeedLocation(String feedLocation) {
    this.feedLocation = feedLocation;
  }


  public Long getUpdateFrequencyInSeconds() {
    return updateFrequencyInSeconds;
  }


  public void setUpdateFrequencyInSeconds(Long updateFrequencyInSeconds) {
    this.updateFrequencyInSeconds = updateFrequencyInSeconds;
  }


  public Boolean getIsActive() {
    return isActive;
  }


  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }


  public Long getNextProcessing() {
    return nextProcessing;
  }


  public void setNextProcessing(Long nextProcessing) {
    this.nextProcessing = nextProcessing;
  }


  public Boolean getIsAuthRequired() {
    return isAuthRequired;
  }


  public void setIsAuthRequired(Boolean isAuthRequired) {
    this.isAuthRequired = isAuthRequired;
  }


  public String getAuthUser() {
    return authUser;
  }


  public void setAuthUser(String authUser) {
    this.authUser = authUser;
  }


  public String getAuthPassword() {
    return authPassword;
  }


  public void setAuthPassword(String authPassword) {
    this.authPassword = authPassword;
  }


  public String getLatestMd5Sum() {
    return latestMd5Sum;
  }


  public void setLatestMd5Sum(String latestMd5Sum) {
    this.latestMd5Sum = latestMd5Sum;
  }


  public Long getLastModifiedInMillis() {
    return lastModifiedInMillis;
  }


  public void setLastModifiedInMillis(Long lastModifiedInMillis) {
    this.lastModifiedInMillis = lastModifiedInMillis;
  }


  public ProcessingStatus getStatus() {
    return status;
  }


  public void setStatus(ProcessingStatus status) {
    this.status = status;
  }


  public Boolean getIsApiBased() {
    return isApiBased;
  }


  public void setIsApiBased(Boolean isApiBased) {
    this.isApiBased = isApiBased;
  }


  public enum ProcessingStatus {
    COMPLETED(0), SCHEDULED(1), PROCESSING(2);

    private int value;

    private ProcessingStatus(int value) {
      this.value = value;
    }

    public ProcessingStatus get(int value) {
      switch (value) {
        case 0:
          return ProcessingStatus.COMPLETED;
        case 1:
          return ProcessingStatus.SCHEDULED;
        case 2:
          return ProcessingStatus.PROCESSING;
        default:
          return null;
      }
    }

    public int getValue() {
      return this.value;
    }
  }

  @Override
  public String toString() {
    return "FeedInfoEntity [id=" + id + ", name=" + name + ", advertiserId=" + advertiserId
        + ", feedLocation=" + feedLocation + ", updateFrequencyInSeconds="
        + updateFrequencyInSeconds + ", isActive=" + isActive + ", nextProcessing=" + nextProcessing
        + ", isAuthRequired=" + isAuthRequired + ", authUser=" + authUser
        + ", latestMd5Sum=" + latestMd5Sum + ", lastModifiedInMillis="
        + lastModifiedInMillis + ", status=" + status + ", isApiBased=" + isApiBased + "]";
  }


}
