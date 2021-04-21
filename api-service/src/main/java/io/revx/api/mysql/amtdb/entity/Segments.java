package io.revx.api.mysql.amtdb.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.api.enums.Status;
import io.revx.core.enums.DurationUnit;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.audience.UserDataType;

/**
 * The persistent class for the Segments database table.
 * 
 */
@Entity
@Table(name = "Segments")
public class Segments implements Serializable, BaseEntity{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(name = "created_on")
	private Long createdOn;

	@Column(length = 1000)
	private String description;

	private Long duration;

	@Column(name = "duration_unit", length = 5)
	@Enumerated(EnumType.STRING)
	private DurationUnit durationUnit;

	@Column(nullable = false, length = 255, unique = true)
	private String name;

	@Column(name = "licensee_id")
	private Long licenseeId;

	@Column(name = "remote_segment_id")
	private String remoteSegmentId;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@Column(name = "segment_type")
    @Enumerated(EnumType.STRING)
    private SegmentType segmentType;

	// bi-directional many-to-one association to SegmentTypeMaster
	//@ManyToOne
	//@JoinColumn(name = "type_id")
	//private SegmentTypeMaster segmentTypeMaster;

	// @OneToMany(mappedBy = "segment", fetch = FetchType.EAGER, cascade = {
	// CascadeType.ALL })
	//@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	//@JoinColumn(name = "segment_id")
	//private List<SegmentPixelMap> segmentPixelExpressions;

	@Column(name = "user_data_type")
	@Enumerated(EnumType.STRING)
	private UserDataType userDataType;
	
	@Column(name="last_hard_refresh_time",nullable = true)
	private Long lastHardRefreshTime;
	
	public Segments() {
	}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Long createdOn) {
    this.createdOn = createdOn;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Long getDuration() {
    return duration;
  }

  public void setDuration(Long duration) {
    this.duration = duration;
  }

  public DurationUnit getDurationUnit() {
    return durationUnit;
  }

  public void setDurationUnit(DurationUnit durationUnit) {
    this.durationUnit = durationUnit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getLicenseeId() {
    return licenseeId;
  }

  public void setLicenseeId(Long licenseeId) {
    this.licenseeId = licenseeId;
  }

  public String getRemoteSegmentId() {
    return remoteSegmentId;
  }

  public void setRemoteSegmentId(String remoteSegmentId) {
    this.remoteSegmentId = remoteSegmentId;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public SegmentType getSegmentType() {
    return segmentType;
  }

  public void setSegmentType(SegmentType segment_type) {
    this.segmentType = segment_type;
  }

  public UserDataType getUserDataType() {
    return userDataType;
  }

  public void setUserDataType(UserDataType userDataType) {
    this.userDataType = userDataType;
  }

  public Long getLastHardRefreshTime() {
    return lastHardRefreshTime;
  }

  public void setLastHardRefreshTime(Long lastHardRefreshTime) {
    this.lastHardRefreshTime = lastHardRefreshTime;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Segments [id=").append(id).append(", createdOn=").append(createdOn)
        .append(", description=").append(description).append(", duration=").append(duration)
        .append(", durationUnit=").append(durationUnit).append(", name=").append(name)
        .append(", licenseeId=").append(licenseeId).append(", remoteSegmentId=")
        .append(remoteSegmentId).append(", status=").append(status).append(", segment_type=")
        .append(segmentType).append(", userDataType=").append(userDataType)
        .append(", lastHardRefreshTime=").append(lastHardRefreshTime).append("]");
    return builder.toString();
  }

	
}