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
@Table(name = "Licensee")
public class LicenseeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ln_id")
  private int id;

  @Column(name = "ln_company_name")
  private String licenseeName;

  @Column(name = "ln_is_active")
  private boolean isActive;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "ln_currency_id", referencedColumnName = "cu_id")
  private CurrencyEntity currencyEntity;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getLicenseeName() {
    return licenseeName;
  }

  public void setLicenseeName(String licenseeName) {
    this.licenseeName = licenseeName;
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    result = prime * result + (isActive ? 1231 : 1237);
    result = prime * result + ((licenseeName == null) ? 0 : licenseeName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LicenseeEntity other = (LicenseeEntity) obj;
    if (id != other.id)
      return false;
    if (isActive != other.isActive)
      return false;
    if (licenseeName == null) {
      if (other.licenseeName != null)
        return false;
    } else if (!licenseeName.equals(other.licenseeName))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LicenseeEntity [id=");
    builder.append(id);
    builder.append(", licenseeName=");
    builder.append(licenseeName);
    builder.append(", isActive=");
    builder.append(isActive);
    builder.append(", currencyEntity=");
    builder.append(currencyEntity);
    builder.append("]");
    return builder.toString();
  }

}
