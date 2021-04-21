/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.mysql.entity.advertiser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * The Class AdvertiserEntity.
 */
@Entity
@Table(name = "Advertiser")
@Document(indexName = "advertiser", type = "advertiser")
public class AdvertiserEntity {

  /** The id. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "av_id")
  private Long id;

  /** The advertiser name. */
  @Column(name = "av_advertiser_name")
  private String advertiserName;

  /** The is active. */
  @Column(name = "av_is_active", nullable = false)
  private Boolean isActive;

  /** The system entry time. */
  @Column(name = "av_system_entry_time")
  private Long systemEntryTime;

  /** The adv address. */
  @Column(name = "av_advertiser_address")
  private String advAddress;

  /** The adv email. */
  @Column(name = "av_advertiser_contact_email")
  private String advEmail;

  /** The adv contact number. */
  @Column(name = "av_advertiser_contact_nos")
  private String advContactNumber;

  /** The created by. */
  @Column(name = "av_created_by")
  private Long createdBy;

  /** The modified on. */
  @Column(name = "av_modified_on")
  private Long modifiedOn;

  /** The modified by. */
  @Column(name = "av_modified_by")
  private Long modifiedBy;

  /** The ios phone bundle. */
  @Column(name = "av_ios_phone_bundle", length = 400)
  private String iosPhoneBundle;

  /** The ios tablet bundle. */
  @Column(name = "av_ios_tablet_bundle", length = 400)
  private String iosTabletBundle;

  /** The android phone bundle. */
  @Column(name = "av_android_phone_bundle", length = 400)
  private String androidPhoneBundle;

  /** The android tablet bundle. */
  @Column(name = "av_android_tablet_bundle", length = 400)
  private String androidTabletBundle;

  /** The licensee id. */
  @Column(name = "av_licensee_id", nullable = false)
  private Long licenseeId;

  /** The region id. */
  @Column(name = "av_region_id", nullable = false)
  private Long regionId;

  /** The category id. */
  @Column(name = "av_cat_id", nullable = false)
  private Long categoryId;

  /** The currency id. */
  @Column(name = "av_currency_id", nullable = false)
  private Long currencyId;

  /** The language id. */
  @Column(name = "av_language_id", nullable = false)
  private Long languageId;

  /** The mmp id. */
  @Column(name = "av_mmp_id")
  private Long mmpId;

  /** The domain. */
  @Column(name = "av_domain")
  private String domain;

  /** The time zone id. */
  @Column(name = "av_timezone")
  private Long timeZoneId;

  /** The date format. */
  @Column(name = "av_date_format")
  private String dateFormat;

  /** The is lift test active. */
  @Column(name = "av_is_lift_test_active", nullable = false, columnDefinition = "integer default 0")
  private Boolean isLiftTestActive;

  /** The is event filter allowed. */
  @Column(name = "av_event_filter_allowed", nullable = false,
      columnDefinition = "boolean default true")
  private Boolean isEventFilterAllowed;

  /** The fb ad account id. */
  @Column(name = "av_fb_ad_account_id")
  private String fbAdAccountId;

  /** The sku allowed characters. */
  @Column(name = "av_sku_allowed_chars", nullable = false)
  private String skuAllowedCharacters;

  /** The transaction currency. */
  @Column(name = "av_transaction_currency")
  private String transactionCurrency;

  /** The feed key. */
  @Column(name = "av_feed_key")
  private String feedKey;

  @Column(name = "av_web_declare_url")
  private String webDeclareUrl;

  @Column(name = "av_ios_declare_url")
  private String iosDeclareUrl;

  @Column(name = "av_android_declare_url")
  private String androidDeclareUrl;

  @Column(name = "av_is_dmp_aud_support")
  private Boolean isDmpAudienceSupport;
  
  @Column(name = "av_is_platform_aud_support")
  private Boolean isPlatformAudienceSupport;

public Boolean getIsDmpAudienceSupport() {
    return isDmpAudienceSupport;
  }

  public void setIsDmpAudienceSupport(Boolean isDmpAudienceSupport) {
    this.isDmpAudienceSupport = isDmpAudienceSupport;
  }

  public Boolean getIsPlatformAudienceSupport() {
    return isPlatformAudienceSupport;
  }

  public void setIsPlatformAudienceSupport(Boolean isPlatformAudienceSupport) {
    this.isPlatformAudienceSupport = isPlatformAudienceSupport;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAdvertiserName() {
    return advertiserName;
  }

  public void setAdvertiserName(String advertiserName) {
    this.advertiserName = advertiserName;
  }

  public Boolean getIsActive() {
    return isActive;
  }

  public void setIsActive(Boolean isActive) {
    this.isActive = isActive;
  }

  public Long getSystemEntryTime() {
    return systemEntryTime;
  }

  public void setSystemEntryTime(Long systemEntryTime) {
    this.systemEntryTime = systemEntryTime;
  }

  public String getAdvAddress() {
    return advAddress;
  }

  public void setAdvAddress(String advAddress) {
    this.advAddress = advAddress;
  }

  public String getAdvEmail() {
    return advEmail;
  }

  public void setAdvEmail(String advEmail) {
    this.advEmail = advEmail;
  }

  public String getAdvContactNumber() {
    return advContactNumber;
  }

  public void setAdvContactNumber(String advContactNumber) {
    this.advContactNumber = advContactNumber;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  public Long getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(Long modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public Long getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getIosPhoneBundle() {
    return iosPhoneBundle;
  }

  public void setIosPhoneBundle(String iosPhoneBundle) {
    this.iosPhoneBundle = iosPhoneBundle;
  }

  public String getIosTabletBundle() {
    return iosTabletBundle;
  }

  public void setIosTabletBundle(String iosTabletBundle) {
    this.iosTabletBundle = iosTabletBundle;
  }

  public String getAndroidPhoneBundle() {
    return androidPhoneBundle;
  }

  public void setAndroidPhoneBundle(String androidPhoneBundle) {
    this.androidPhoneBundle = androidPhoneBundle;
  }

  public String getAndroidTabletBundle() {
    return androidTabletBundle;
  }

  public void setAndroidTabletBundle(String androidTabletBundle) {
    this.androidTabletBundle = androidTabletBundle;
  }

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public Long getRegionId() {
    return regionId;
  }

  public void setRegionId(Long regionId) {
    this.regionId = regionId;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public Long getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(Long currencyId) {
    this.currencyId = currencyId;
  }

  public Long getLanguageId() {
    return languageId;
  }

  public void setLanguageId(Long languageId) {
    this.languageId = languageId;
  }

  public Long getMmpId() {
    return mmpId;
  }

  public void setMmpId(Long mmpId) {
    this.mmpId = mmpId;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public Long getTimeZoneId() {
    return timeZoneId;
  }

  public void setTimeZoneId(Long timeZoneId) {
    this.timeZoneId = timeZoneId;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public Boolean getIsLiftTestActive() {
    return isLiftTestActive;
  }

  public void setIsLiftTestActive(Boolean isLiftTestActive) {
    this.isLiftTestActive = isLiftTestActive;
  }

  public Boolean getIsEventFilterAllowed() {
    return isEventFilterAllowed;
  }

  public void setIsEventFilterAllowed(Boolean isEventFilterAllowed) {
    this.isEventFilterAllowed = isEventFilterAllowed;
  }

  public String getFbAdAccountId() {
    return fbAdAccountId;
  }

  public void setFbAdAccountId(String fbAdAccountId) {
    this.fbAdAccountId = fbAdAccountId;
  }

  public String getSkuAllowedCharacters() {
    return skuAllowedCharacters;
  }

  public void setSkuAllowedCharacters(String skuAllowedCharacters) {
    this.skuAllowedCharacters = skuAllowedCharacters;
  }

  public String getTransactionCurrency() {
    return transactionCurrency;
  }

  public void setTransactionCurrency(String transactionCurrency) {
    this.transactionCurrency = transactionCurrency;
  }

  public String getFeedKey() {
    return feedKey;
  }

  public void setFeedKey(String feedKey) {
    this.feedKey = feedKey;
  }

  public String getWebDeclareUrl() {
    return webDeclareUrl;
  }

  public void setWebDeclareUrl(String webDeclareUrl) {
    this.webDeclareUrl = webDeclareUrl;
  }

  public String getIosDeclareUrl() {
    return iosDeclareUrl;
  }

  public void setIosDeclareUrl(String iosDeclareUrl) {
    this.iosDeclareUrl = iosDeclareUrl;
  }

  public String getAndroidDeclareUrl() {
    return androidDeclareUrl;
  }

  public void setAndroidDeclareUrl(String androidDeclareUrl) {
    this.androidDeclareUrl = androidDeclareUrl;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdvertiserEntity [id=").append(id).append(", advertiserName=")
        .append(advertiserName).append(", isActive=").append(isActive).append(", systemEntryTime=")
        .append(systemEntryTime).append(", advAddress=").append(advAddress).append(", advEmail=")
        .append(advEmail).append(", advContactNumber=").append(advContactNumber)
        .append(", createdBy=").append(createdBy).append(", modifiedOn=").append(modifiedOn)
        .append(", modifiedBy=").append(modifiedBy).append(", iosPhoneBundle=")
        .append(iosPhoneBundle).append(", iosTabletBundle=").append(iosTabletBundle)
        .append(", androidPhoneBundle=").append(androidPhoneBundle).append(", androidTabletBundle=")
        .append(androidTabletBundle).append(", licenseeId=").append(licenseeId)
        .append(", regionId=").append(regionId).append(", categoryId=").append(categoryId)
        .append(", currencyId=").append(currencyId).append(", languageId=").append(languageId)
        .append(", mmpId=").append(mmpId).append(", domain=").append(domain).append(", timeZoneId=")
        .append(timeZoneId).append(", dateFormat=").append(dateFormat).append(", isLiftTestActive=")
        .append(isLiftTestActive).append(", isEventFilterAllowed=").append(isEventFilterAllowed)
        .append(", fbAdAccountId=").append(fbAdAccountId).append(", skuAllowedCharacters=")
        .append(skuAllowedCharacters).append(", transactionCurrency=").append(transactionCurrency)
        .append(", feedKey=").append(feedKey).append(", webDeclareUrl=").append(webDeclareUrl)
        .append(", iosDeclareUrl=").append(iosDeclareUrl).append(", androidDeclareUrl=")
        .append(androidDeclareUrl).append("]");
    return builder.toString();
  }


}
