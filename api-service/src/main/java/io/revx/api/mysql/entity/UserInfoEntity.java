package io.revx.api.mysql.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "UserInfo")
public class UserInfoEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ui_id")
  private int id;
  @Column(name = "ui_login_name")
  private String username;
  @Column(name = "ui_password")
  private String password;
  @Column(name = "ui_is_active")
  private boolean isActive;
  @Column(name = "ui_create_time")
  private Date creationTime;
  @Column(name = "ui_modified_time")
  private Date modifiedTime;
  @Column(name = "ui_created_by")
  private String createdBy;
  @Column(name = "ui_modfied_by")
  private String modfiedBy;

  @Transient
  private int licenseeId;
  @Transient
  private int advId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public Date getModifiedTime() {
    return modifiedTime;
  }

  public void setModifiedTime(Date modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getModfiedBy() {
    return modfiedBy;
  }

  public void setModfiedBy(String modfiedBy) {
    this.modfiedBy = modfiedBy;
  }

  public int getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(int licenseeId) {
    this.licenseeId = licenseeId;
  }

  public int getAdvId() {
    return advId;
  }

  public void setAdvId(int advId) {
    this.advId = advId;
  }

  @Override
  public String toString() {
    return "User [id=" + id + ", username=" + username + ", isActive="
        + isActive + ", creationTime=" + creationTime + ", modifiedTime=" + modifiedTime
        + ", createdBy=" + createdBy + ", modfiedBy=" + modfiedBy + ", licenseeId=" + licenseeId
        + ", advId=" + advId + "]";
  }
}
