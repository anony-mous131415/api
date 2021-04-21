package io.revx.core.model.audience;

import io.revx.core.enums.CompressionType;
import io.revx.core.enums.CrmStatus;
import io.revx.core.enums.DurationUnit;
import io.revx.core.enums.EncodingType;
import io.revx.core.model.crm.FetchConfigDTO;
import io.revx.core.model.crm.Frequency;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;

public class PixelDataScheduleDTO extends PixelRemoteConfigDTO {


  private Long id;


  private Integer encodingType;


  private Integer compressionType;


  private int frequencyValue;


  private Integer frequencyUnit;


  private Long nextRunTime;


  private Long lastSuccessTime;


  private Long lastActionTime;


  private Integer lastActionStatusCode;


  public Long getId() {
    return id;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public Integer getEncodingType() {
    return encodingType;
  }


  public void setEncodingType(Integer encodingType) {
    this.encodingType = encodingType;
  }


  public Integer getCompressionType() {
    return compressionType;
  }


  public void setCompressionType(Integer compressionType) {
    this.compressionType = compressionType;
  }


  public int getFrequencyValue() {
    return frequencyValue;
  }


  public void setFrequencyValue(int frequencyValue) {
    this.frequencyValue = frequencyValue;
  }


  public Integer getFrequencyUnit() {
    return frequencyUnit;
  }


  public void setFrequencyUnit(Integer frequencyUnit) {
    this.frequencyUnit = frequencyUnit;
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


  public Long getLastActionTime() {
    return lastActionTime;
  }


  public void setLastActionTime(Long lastActionTime) {
    this.lastActionTime = lastActionTime;
  }


  public Integer getLastActionStatusCode() {
    return lastActionStatusCode;
  }


  public void setLastActionStatusCode(Integer lastActionStatusCode) {
    this.lastActionStatusCode = lastActionStatusCode;
  }


  public ServerSyncCoordinatorDTO getSchedule(Long licenseeId) {
      ServerSyncCoordinatorDTO schedule = new ServerSyncCoordinatorDTO();
      DurationUnit durationUnit = DurationUnit.getById(this.frequencyUnit);
      schedule.setFrequency(new Frequency(this.frequencyValue, durationUnit));
      schedule.setId(this.id);
      if (this.lastActionStatusCode != null)
          schedule.setLastActionStatus(CrmStatus
                  .getById(this.lastActionStatusCode));
      schedule.setLastSuccessTime(this.lastSuccessTime);
      schedule.setNextRunTime(this.nextRunTime);
      FetchConfigDTO config = new FetchConfigDTO();
      updateRemoteFile(config);
      CompressionType compressionType = CompressionType
              .getById(this.compressionType);
      config.setCompressionType(compressionType);
      EncodingType encodingType = EncodingType.getById(this.encodingType);
      config.setEncodingType(encodingType);
      config.setLicenseeId(licenseeId);
      schedule.setConfig(config);

      return schedule;
  }

  public PixelDataScheduleDTO() {

  }

  public PixelDataScheduleDTO(ServerSyncCoordinatorDTO coordinator) {
      FetchConfigDTO config = coordinator.getConfig();
      this.protocol = null == config.getProtocol() ? null : config
              .getProtocol().id;
      this.url = getUrl(config.getHost(), config.getPort(),
              config.getPathTemplate());
      this.username = config.getUsername();
      this.password = config.getPassword();
      this.encodingType = null == config.getEncodingType() ? null : config
              .getEncodingType().id;
      this.compressionType = null == config.getCompressionType() ? null
              : config.getCompressionType().id;
      if (coordinator.getFrequency() != null) {
          this.frequencyUnit = null == coordinator.getFrequency().getUnit() ? null
                  : coordinator.getFrequency().getUnit().id;
          this.frequencyValue = coordinator.getFrequency().getValue();
      }
      this.id = coordinator.getId();
      if (coordinator.getLastActionStatus() != null) {
          this.lastActionStatusCode = null == coordinator
                  .getLastActionStatus() ? null : coordinator
                  .getLastActionStatus().id;
          this.lastActionTime = coordinator.getLastActionTime();
      }
      this.lastSuccessTime = coordinator.getLastSuccessTime();
      this.nextRunTime = coordinator.getNextRunTime();
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("PixelDataScheduleDTO [id=").append(id).append(", encodingType=")
        .append(encodingType).append(", compressionType=").append(compressionType)
        .append(", frequencyValue=").append(frequencyValue).append(", frequencyUnit=")
        .append(frequencyUnit).append(", nextRunTime=").append(nextRunTime)
        .append(", lastSuccessTime=").append(lastSuccessTime).append(", lastActionTime=")
        .append(lastActionTime).append(", lastActionStatusCode=").append(lastActionStatusCode)
        .append("]");
    return builder.toString();
  }

}
