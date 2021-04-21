package io.revx.api.mysql.entity.pixel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Pixel")
public class ConversionPixelEntity {

  public enum PixelType {
    VIEW_CONV(0), CLICK_CONV(1), HYBRID_CONV(2);

    private Integer id;

    private PixelType(Integer id) {
      this.id = id;
    }

    public Integer getId() {
      return id;
    }

    public static PixelType getPixelTypeFromId(int id) {
      switch (id) {
        case 0:
          return VIEW_CONV;
        case 1:
          return CLICK_CONV;
        case 2:
          return HYBRID_CONV;
        default:
          return null;
      }
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "xl_id", nullable = false)
  private Long id;

  @Column(name = "xl_advertiser_id")
  private Long advertiserId;

  @Column(name = "xl_name")
  private String name;

  @Column(name = "xl_validity_window")
  private Long validityWindow;

  @Column(name = "xl_user_fcap")
  private Long fcap;

  @Column(name = "xl_fcap_duration")
  private Long fcapDuration;

  @Column(name = "xl_type", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private PixelType type;

  @Column(name = "xl_is_active", columnDefinition = "BIT", length = 1)
  private boolean active;

  @Column(name = "xl_clk_validity_window")
  private Long clkValidityWindow;

  @Column(name = "xl_view_validity_window")
  private Long viewValidityWindow;

  @Column(name = "xl_licensee_id", nullable = false)
  private Long licenseeId;

  @Column(name = "xl_created_by")
  private Long createdBy;

  @Column(name = "xl_created_on")
  private Long createdOn;

  @Column(name = "xl_modified_by")
  private Long modifiedBy;

  @Column(name = "xl_modified_on")
  private Long modifiedOn;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getValidityWindow() {
    return validityWindow;
  }

  public void setValidityWindow(Long validityWindow) {
    this.validityWindow = validityWindow;
  }

  public Long getFcap() {
    return fcap;
  }

  public void setFcap(Long fcap) {
    this.fcap = fcap;
  }

  public Long getFcapDuration() {
    return fcapDuration;
  }

  public void setFcapDuration(Long fcapDuration) {
    this.fcapDuration = fcapDuration;
  }

  public PixelType getType() {
    return type;
  }

  public void setType(PixelType type) {
    this.type = type;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Long getClkValidityWindow() {
    return clkValidityWindow;
  }

  public void setClkValidityWindow(Long clkValidityWindow) {
    this.clkValidityWindow = clkValidityWindow;
  }

  public Long getViewValidityWindow() {
    return viewValidityWindow;
  }

  public void setViewValidityWindow(Long viewValidityWindow) {
    this.viewValidityWindow = viewValidityWindow;
  }

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  public Long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Long createdOn) {
    this.createdOn = createdOn;
  }

  public Long getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public Long getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(Long modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  @Override
  public String toString() {
    return "PixelEntity [id=" + id + ", advertiserId=" + advertiserId + ", name=" + name
        + ", validityWindow=" + validityWindow + ", fcap=" + fcap + ", fcapDuration=" + fcapDuration
        + ", type=" + type + ", active=" + active + ", clkValidityWindow=" + clkValidityWindow
        + ", viewValidityWindow=" + viewValidityWindow + ", licenseeId=" + licenseeId
        + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", modifiedBy=" + modifiedBy
        + ", modifiedOn=" + modifiedOn + "]";
  }


}
