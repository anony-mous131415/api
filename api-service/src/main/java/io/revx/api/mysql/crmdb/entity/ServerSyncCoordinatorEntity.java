package io.revx.api.mysql.crmdb.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import io.revx.core.enums.CrmStatus;
import io.revx.core.enums.DurationUnit;
import io.revx.core.model.crm.Frequency;

@Entity
@Table(name = "ServerSyncCoordinator")
public class ServerSyncCoordinatorEntity implements IDto, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7588364472353701L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "ssc_id", nullable = false)
	private Long id;

	@Column(name = "ssc_frequency_value", nullable = false)
	private Integer frequencyValue;

	@Column(name = "ssc_frequency_unit", nullable = false)
	@Enumerated(EnumType.STRING)
	private DurationUnit frequencyUnit;

	@Column(name = "ssc_start_time", nullable = false)
	private Long startTime;

	@Column(name = "ssc_status", nullable = false)
	private Integer status;

	@Column(name = "ssc_end_time", nullable = true)
	private Long endTime;

	@Column(name = "ssc_next_run_time", nullable = true)
	private Long nextRunTime;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ssc_last_action_id", nullable = true)
	private ServerSyncActionEntity lastAction;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ssc_last_success_action_id", nullable = true)
	private ServerSyncActionEntity lastSuccessAction;

	@OneToOne(mappedBy = "coordinator", cascade = CascadeType.ALL)
	private ServerFetchConfigEntity config;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Frequency getFrequency() {
		return new Frequency(frequencyValue == null ? 0 : frequencyValue,
				frequencyUnit);
	}

	public void setFrequency(Frequency frequency) {
		this.frequencyUnit = frequency.getUnit();
		this.frequencyValue = frequency.getValue();
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public CrmStatus getStatus() {
		return CrmStatus.getById(status);
	}

	public void setStatus(CrmStatus status) {
		this.status = status == null ? null : status.id;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public Long getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Long nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	public ServerSyncActionEntity getLastAction() {
		return lastAction;
	}

	public void setLastAction(ServerSyncActionEntity lastAction) {
		this.lastAction = lastAction;
	}

	public ServerSyncActionEntity getLastSuccessAction() {
		return lastSuccessAction;
	}

	public void setLastSuccessAction(ServerSyncActionEntity lastSuccessAction) {
		this.lastSuccessAction = lastSuccessAction;
	}

	public ServerFetchConfigEntity getConfig() {
		return config;
	}

	public void setConfig(ServerFetchConfigEntity config) {
		this.config = config;
	}

	public ServerSyncCoordinatorEntity getNewWithId() {
		ServerSyncCoordinatorEntity dto = new ServerSyncCoordinatorEntity();
		dto.setId(this.getId());
		return dto;
	}

	@Override
	public String toString() {
		return "ServerSyncCoordinatorDto [id=" + id + ", frequencyValue=" + frequencyValue + ", frequencyUnit="
				+ frequencyUnit + ", startTime=" + startTime + ", status=" + status + ", endTime=" + endTime
				+ ", nextRunTime=" + nextRunTime + ", lastAction=" + lastAction + ", lastSuccessAction="
				+ lastSuccessAction + ", config=" + config + "]";
	}

}