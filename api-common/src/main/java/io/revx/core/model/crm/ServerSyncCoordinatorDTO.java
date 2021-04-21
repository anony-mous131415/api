package io.revx.core.model.crm;


import io.revx.core.enums.CrmStatus;

public class ServerSyncCoordinatorDTO {

	Long id;

	Long startTime;

	Frequency frequency;

	FetchConfigDTO config;

	Long nextRunTime;

	Long lastSuccessTime;

	CrmStatus lastActionStatus;

	Long lastActionTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Frequency getFrequency() {
		return frequency;
	}

	public void setFrequency(Frequency frequency) {
		this.frequency = frequency;
	}

	public FetchConfigDTO getConfig() {
		return config;
	}

	public void setConfig(FetchConfigDTO config) {
		this.config = config;
	}

	public Long getNextRunTime() {
		return nextRunTime;
	}

	public void setNextRunTime(Long nextRunTime) {
		this.nextRunTime = nextRunTime;
	}

	public Long getLastSuccessTime() {
		return lastSuccessTime;
	}

	public void setLastSuccessTime(Long lastSuccessTime) {
		this.lastSuccessTime = lastSuccessTime;
	}

	public CrmStatus getLastActionStatus() {
		return lastActionStatus;
	}

	public void setLastActionStatus(CrmStatus crmStatus) {
		this.lastActionStatus = crmStatus;
	}

	public Long getLastActionTime() {
		return lastActionTime;
	}

	public void setLastActionTime(Long lastActionTime) {
		this.lastActionTime = lastActionTime;
	}
}
