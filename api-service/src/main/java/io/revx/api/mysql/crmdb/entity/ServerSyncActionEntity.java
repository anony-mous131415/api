package io.revx.api.mysql.crmdb.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import io.revx.core.enums.CrmStatus;

@Entity
@Table(name = "ServerSyncAction")
public class ServerSyncActionEntity implements IDto, IStatus, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7588364472353701L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ssa_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ssa_coordinator_id", nullable = false)
	private ServerSyncCoordinatorEntity coordinator;

	@Column(name = "ssa_nominal_time", nullable = false)
	private Long nominalTime;

	@Column(name = "ssa_start_time", nullable = true)
	private Long startTime;

	@Column(name = "ssa_last_updated", nullable = true)
	private Long lastUpdated;

	@Column(name = "ssa_pixel_data_file_id", nullable = true)
	private Long pixelDataFileId;

	@Column(name = "ssa_status", nullable = false)
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ServerSyncCoordinatorEntity getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(ServerSyncCoordinatorEntity coordinator) {
		this.coordinator = coordinator;
	}

	public Long getNominalTime() {
		return nominalTime;
	}

	public void setNominalTime(Long nominalTime) {
		this.nominalTime = nominalTime;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Long lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Long getPixelDataFileId() {
		return pixelDataFileId;
	}

	public void setPixelDataFileId(Long pixelDataFileId) {
		this.pixelDataFileId = pixelDataFileId;
	}

	public CrmStatus getStatus() {
		return CrmStatus.getById(status);
	}

	public void setStatus(CrmStatus status) {
		this.status = status == null ? null : status.id;
	}

	public ServerSyncActionEntity getNewWithId() {
		ServerSyncActionEntity dto = new ServerSyncActionEntity();
		dto.setId(this.getId());
		return dto;
	}

}