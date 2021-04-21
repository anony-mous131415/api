package io.revx.auth.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Advertiser")
public class AdvertiserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "av_id")
  private int id;

  @Column(name = "av_advertiser_name")
  private String advertiserName;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "av_licensee_id", referencedColumnName = "ln_id")
  private LicenseeEntity licenseeEntity;

  @Column(name = "av_is_active")
  private boolean isActive;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "av_currency_id", referencedColumnName = "cu_id")
  private CurrencyEntity currencyEntity;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getAdvertiserName() {
    return advertiserName;
  }

  public void setAdvertiserName(String advertiserName) {
    this.advertiserName = advertiserName;
  }

  public LicenseeEntity getLicenseeEntity() {
    return licenseeEntity;
  }

  public void setLicenseeEntity(LicenseeEntity licenseeEntity) {
    this.licenseeEntity = licenseeEntity;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public CurrencyEntity getCurrencyEntity() {
    return currencyEntity;
  }

  public void setCurrencyEntity(CurrencyEntity currencyEntity) {
    this.currencyEntity = currencyEntity;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("AdvertiserEntity [id=");
    builder.append(id);
    builder.append(", advertiserName=");
    builder.append(advertiserName);
    builder.append(", licenseeEntity=");
    builder.append(licenseeEntity);
    builder.append(", isActive=");
    builder.append(isActive);
    builder.append(", currencyEntity=");
    builder.append(currencyEntity);
    builder.append("]");
    return builder.toString();
  }

}
