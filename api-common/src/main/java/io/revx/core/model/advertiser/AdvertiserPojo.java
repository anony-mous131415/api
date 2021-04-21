package io.revx.core.model.advertiser;

import java.util.Arrays;
import java.util.List;

import io.revx.core.model.AppSettingsDTO;
import io.revx.core.model.BaseModel;

public class AdvertiserPojo extends io.revx.core.model.Advertiser {

  private BaseModel region;
  private BaseModel timeZone;
  private String domain;
  private String androidPhoneBundle;
  private String androidTabletBundle;
  private String iosPhoneBundle;
  private String iosTabletBundle;
  private BaseModel category;
  private String webDeclareUrl;
  private String iosDeclareUrl;
  private String androidDeclareUrl;
  private String email;
  private String contactAddress;
  private String contactNumber;
  private BaseModel language;
  private String skuAllowedChars;
  private List<String> nonEditableFields;
  private AppSettingsDTO defaultLogoDetails;
  private BaseModel mmp;


  public AdvertiserPojo() {
      String[] filedls = {"id", "currency", "licensee", "language"};
      nonEditableFields = Arrays.asList(filedls);
    }

  public AdvertiserPojo(int i, String string) {
    this();
  }

  public BaseModel getRegion() {
    return region;
  }

  public void setRegion(BaseModel region) {
    this.region = region;
  }

  public BaseModel getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(BaseModel timeZone) {
    this.timeZone = timeZone;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
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

  public BaseModel getCategory() {
    return category;
  }

  public void setCategory(BaseModel category) {
    this.category = category;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getContactAddress() {
    return contactAddress;
  }

  public void setContactAddress(String contactAddress) {
    this.contactAddress = contactAddress;
  }

  public String getContactNumber() {
    return contactNumber;
  }

  public void setContactNumber(String contactNumber) {
    this.contactNumber = contactNumber;
  }

  public BaseModel getLanguage() {
    return language;
  }

  public void setLanguage(BaseModel language) {
    this.language = language;
  }

  public List<String> getNonEditableFields() {
    return nonEditableFields;
  }

  public void setNonEditableFields(List<String> nonEditableFields) {
    this.nonEditableFields = nonEditableFields;
  }

  public BaseModel getMMP() {
	  return mmp;
  }

  public void setMMP(BaseModel mmp) {
	  this.mmp = mmp;
  }

  public AppSettingsDTO getDefaultLogoDetails() {
    return defaultLogoDetails;
  }

  public void setDefaultLogoDetails(AppSettingsDTO defaultLogoDetails) {
    this.defaultLogoDetails = defaultLogoDetails;
  }

  public String getSkuAllowedChars() {
    return skuAllowedChars;
  }

  public void setSkuAllowedChars(String skuAllowedChars) {
    this.skuAllowedChars = skuAllowedChars;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((androidDeclareUrl == null) ? 0 : androidDeclareUrl.hashCode());
    result = prime * result + ((androidPhoneBundle == null) ? 0 : androidPhoneBundle.hashCode());
    result = prime * result + ((androidTabletBundle == null) ? 0 : androidTabletBundle.hashCode());
    result = prime * result + ((category == null) ? 0 : category.hashCode());
    result = prime * result + ((contactAddress == null) ? 0 : contactAddress.hashCode());
    result = prime * result + ((contactNumber == null) ? 0 : contactNumber.hashCode());
    result = prime * result + ((domain == null) ? 0 : domain.hashCode());
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((iosDeclareUrl == null) ? 0 : iosDeclareUrl.hashCode());
    result = prime * result + ((iosPhoneBundle == null) ? 0 : iosPhoneBundle.hashCode());
    result = prime * result + ((iosTabletBundle == null) ? 0 : iosTabletBundle.hashCode());
    result = prime * result + ((language == null) ? 0 : language.hashCode());
    result = prime * result + ((nonEditableFields == null) ? 0 : nonEditableFields.hashCode());
    result = prime * result + ((region == null) ? 0 : region.hashCode());
    result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
    result = prime * result + ((webDeclareUrl == null) ? 0 : webDeclareUrl.hashCode());
    result = prime * result + ((defaultLogoDetails == null) ? 0 : defaultLogoDetails.hashCode());
    result = prime * result + ((mmp == null) ? 0 : mmp.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    AdvertiserPojo other = (AdvertiserPojo) obj;
    if (androidDeclareUrl == null) {
      if (other.androidDeclareUrl != null)
        return false;
    } else if (!androidDeclareUrl.equals(other.androidDeclareUrl))
      return false;
    if (androidPhoneBundle == null) {
      if (other.androidPhoneBundle != null)
        return false;
    } else if (!androidPhoneBundle.equals(other.androidPhoneBundle))
      return false;
    if (androidTabletBundle == null) {
      if (other.androidTabletBundle != null)
        return false;
    } else if (!androidTabletBundle.equals(other.androidTabletBundle))
      return false;
    if (category == null) {
      if (other.category != null)
        return false;
    } else if (!category.equals(other.category))
      return false;
    if (contactAddress == null) {
      if (other.contactAddress != null)
        return false;
    } else if (!contactAddress.equals(other.contactAddress))
      return false;
    if (contactNumber == null) {
      if (other.contactNumber != null)
        return false;
    } else if (!contactNumber.equals(other.contactNumber))
      return false;
    if (domain == null) {
      if (other.domain != null)
        return false;
    } else if (!domain.equals(other.domain))
      return false;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (iosDeclareUrl == null) {
      if (other.iosDeclareUrl != null)
        return false;
    } else if (!iosDeclareUrl.equals(other.iosDeclareUrl))
      return false;
    if (iosPhoneBundle == null) {
      if (other.iosPhoneBundle != null)
        return false;
    } else if (!iosPhoneBundle.equals(other.iosPhoneBundle))
      return false;
    if (iosTabletBundle == null) {
      if (other.iosTabletBundle != null)
        return false;
    } else if (!iosTabletBundle.equals(other.iosTabletBundle))
      return false;
    if (language == null) {
      if (other.language != null)
        return false;
    } else if (!language.equals(other.language))
      return false;
    if (nonEditableFields == null) {
      if (other.nonEditableFields != null)
        return false;
    } else if (!nonEditableFields.equals(other.nonEditableFields))
      return false;
    if (region == null) {
      if (other.region != null)
        return false;
    } else if (!region.equals(other.region))
      return false;
    if (timeZone == null) {
      if (other.timeZone != null)
        return false;
    } else if (!timeZone.equals(other.timeZone))
      return false;
    if (webDeclareUrl == null) {
      if (other.webDeclareUrl != null)
        return false;
    } else if (!webDeclareUrl.equals(other.webDeclareUrl))
      return false;
    if (defaultLogoDetails == null) {
        if (other.defaultLogoDetails != null)
          return false;
      } else if (!defaultLogoDetails.equals(other.defaultLogoDetails))
        return false;

    if (mmp == null) {
    	if (other.mmp != null)
    		return false;
    } else if (!mmp.equals(other.mmp))
    	return false;

    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdvertiserPojo [region=").append(region).append(", timeZone=").append(timeZone)
        .append(", domain=").append(domain).append(", androidPhoneBundle=")
        .append(androidPhoneBundle).append(", androidTabletBundle=").append(androidTabletBundle)
        .append(", iosPhoneBundle=").append(iosPhoneBundle).append(", iosTabletBundle=")
        .append(iosTabletBundle).append(", category=").append(category).append(", webDeclareUrl=")
        .append(webDeclareUrl).append(", iosDeclareUrl=").append(iosDeclareUrl)
        .append(", androidDeclareUrl=").append(androidDeclareUrl).append(", email=").append(email)
        .append(", contactAddress=").append(contactAddress).append(", contactNumber=")
        .append(contactNumber).append(", language=").append(language).append(", skuAllowedChars=")
        .append(skuAllowedChars).append(", nonEditableFields=").append(nonEditableFields)
        .append(", defaultLogoDetails=").append(defaultLogoDetails)
        .append(", mmp=").append(mmp)
        .append("]");
    return builder.toString();
  }



}
