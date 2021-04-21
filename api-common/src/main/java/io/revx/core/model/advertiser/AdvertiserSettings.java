package io.revx.core.model.advertiser;

import io.revx.core.model.BaseModel;

public class AdvertiserSettings {

  private Long advertiserId;
  private String feedKey;
  private String dateFormat;
  private boolean isLiftTestActive;
  private boolean isEventFilterAllowed;
  private String skuAllowedChars;
  private BaseModel transactionCurrency;
  private BaseModel mmp;
  private Boolean isPlatformAudienceSupport;
  private Boolean isDmpAudienceSupport;

  public Boolean getIsPlatformAudienceSupport() {
    return isPlatformAudienceSupport;
  }

  public void setIsPlatformAudienceSupport(Boolean isPlatformAudienceSupport) {
    this.isPlatformAudienceSupport = isPlatformAudienceSupport;
  }

  public Boolean getIsDmpAudienceSupport() {
    return isDmpAudienceSupport;
  }

  public void setIsDmpAudienceSupport(Boolean isDmpAudienceSupport) {
    this.isDmpAudienceSupport = isDmpAudienceSupport;
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public String getFeedKey() {
    return feedKey;
  }

  public void setFeedKey(String feedKey) {
    this.feedKey = feedKey;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public boolean isLiftTestActive() {
    return isLiftTestActive;
  }

  public void setLiftTestActive(boolean isLiftTestActive) {
    this.isLiftTestActive = isLiftTestActive;
  }

  public boolean isEventFilterAllowed() {
    return isEventFilterAllowed;
  }

  public void setEventFilterAllowed(boolean isEventFilterAllowed) {
    this.isEventFilterAllowed = isEventFilterAllowed;
  }

  public String getSkuAllowedChars() {
    return skuAllowedChars;
  }

  public void setSkuAllowedChars(String skuAllowedChars) {
    this.skuAllowedChars = skuAllowedChars;
  }

  public BaseModel getTransactionCurrency() {
    return transactionCurrency;
  }

  public void setTransactionCurrency(BaseModel transactionCurrency) {
    this.transactionCurrency = transactionCurrency;
  }

  public BaseModel getMmp() {
    return mmp;
  }

  public void setMmp(BaseModel mmp) {
    this.mmp = mmp;
  }

  @Override
  public String toString() {
    return "AdvertiserSettings [advertiserId=" + advertiserId + ", feedKey=" + feedKey
        + ", dateFormat=" + dateFormat + ", isLiftTestActive=" + isLiftTestActive
        + ", isEventFilterAllowed=" + isEventFilterAllowed + ", skuAllowedChars=" + skuAllowedChars
        + ", transactionCurrency=" + transactionCurrency + ", mmp=" + mmp + "]";
  }

}
