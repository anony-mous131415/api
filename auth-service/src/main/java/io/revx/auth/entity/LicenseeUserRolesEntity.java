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
@Table(name = "UserRoles")
public class LicenseeUserRolesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ur_id")
  private long id;

  @Column(name = "ur_user_id")
  private long userId;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "ur_licensee_id", referencedColumnName = "ln_id")
  private LicenseeEntity licenseeEntity;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "ur_role_id", referencedColumnName = "ro_id")
  private RolesEntity rolesEntity;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "ur_adv_id", referencedColumnName = "av_id")
  private AdvertiserEntity advertiserEntity;

  /**
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * @return the userId
   */
  public long getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(long userId) {
    this.userId = userId;
  }

  /**
   * @return the licenseeEntity
   */
  public LicenseeEntity getLicenseeEntity() {
    return licenseeEntity;
  }

  /**
   * @param licenseeEntity the licenseeEntity to set
   */
  public void setLicenseeEntity(LicenseeEntity licenseeEntity) {
    this.licenseeEntity = licenseeEntity;
  }

  /**
   * @return the rolesEntity
   */
  public RolesEntity getRolesEntity() {
    return rolesEntity;
  }

  /**
   * @param rolesEntity the rolesEntity to set
   */
  public void setRolesEntity(RolesEntity rolesEntity) {
    this.rolesEntity = rolesEntity;
  }

  /**
   * @return the advertiserEntity
   */
  public AdvertiserEntity getAdvertiserEntity() {
    return advertiserEntity;
  }

  /**
   * @param advertiserEntity the advertiserEntity to set
   */
  public void setAdvertiserEntity(AdvertiserEntity advertiserEntity) {
    this.advertiserEntity = advertiserEntity;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("LicenseeUserRolesEntity [id=");
    builder.append(id);
    builder.append(", userId=");
    builder.append(userId);
    builder.append(", licenseeEntity=");
    builder.append(licenseeEntity);
    builder.append(", rolesEntity=");
    builder.append(rolesEntity);
    builder.append(", advertiserEntity=");
    builder.append(advertiserEntity);
    builder.append("]");
    return builder.toString();
  }

}
