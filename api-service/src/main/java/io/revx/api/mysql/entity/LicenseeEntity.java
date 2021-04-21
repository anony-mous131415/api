package io.revx.api.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LicenseeEntity [id=");
    builder.append(id);
    builder.append(", licenseeName=");
    builder.append(licenseeName);
    builder.append(", isActive=");
    builder.append(isActive);
    builder.append("]");
    return builder.toString();
  }

}
