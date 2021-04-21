package io.revx.core.model;

@SuppressWarnings("serial")
public class Advertiser extends StatusTimeModel {

  private long licenseeId;
  private String currencyCode;
  private BaseModel currency;
  private BaseModel licensee;
  private long timeZoneId;

  public Advertiser() {
    super();
  }

  public Advertiser(long id, String name) {
    super(id, name);
  }

  public Advertiser(long id, String name, String currencyCode) {
    super(id, name);
    this.currencyCode = currencyCode;
  }

  public long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  public BaseModel getCurrency() {
    return currency;
  }

  public void setCurrency(BaseModel currency) {
    this.currency = currency;
  }


  public BaseModel getLicensee() {
    return licensee;
  }

  public void setLicensee(BaseModel licensee) {
    this.licensee = licensee;
  }

  public long getTimeZoneId() {
    return timeZoneId;
  }

  public void setTimeZoneId(long timeZoneId) {
    this.timeZoneId = timeZoneId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
    result = prime * result + (int) (licenseeId ^ (licenseeId >>> 32));
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
    Advertiser other = (Advertiser) obj;
    if (currencyCode == null) {
      if (other.currencyCode != null)
        return false;
    } else if (!currencyCode.equals(other.currencyCode))
      return false;
    if (licenseeId != other.licenseeId)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Advertiser [licenseeId=" + licenseeId + ", currencyCode=" + currencyCode + ", currency="
        + currency + ", licensee=" + licensee + ", timeZoneId=" + timeZoneId + ", modifiedTime="
        + modifiedTime + ", modifiedBy=" + modifiedBy + ", id=" + id + ", name=" + name + "]";
  }

  
  
  
}
