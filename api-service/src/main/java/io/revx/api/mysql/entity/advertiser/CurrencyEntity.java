package io.revx.api.mysql.entity.advertiser;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "CurrencyMaster")
public class CurrencyEntity implements BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cu_id")
  private Long id;

  @Column(name = "cu_name")
  private String currencyName;

  @Column(name = "cu_code")
  private String currencyCode;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCurrencyName() {
    return currencyName;
  }

  public void setCurrencyName(String currencyName) {
    this.currencyName = currencyName;
  }

  public String getCurrencyCode() {
    return currencyCode;
  }

  public void setCurrencyCode(String currencyCode) {
    this.currencyCode = currencyCode;
  }

  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CurrencyEntity [id=");
    builder.append(id);
    builder.append(", currencyName=");
    builder.append(currencyName);
    builder.append(", currencyCode=");
    builder.append(currencyCode);
    builder.append("]");
    return builder.toString();
  }

}
