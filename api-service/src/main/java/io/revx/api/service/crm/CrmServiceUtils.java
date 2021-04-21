package io.revx.api.service.crm;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import io.revx.api.mysql.crmdb.entity.PixelDataFileEntity;
import io.revx.api.mysql.crmdb.entity.ServerFetchConfigEntity;
import io.revx.api.mysql.crmdb.entity.ServerSyncActionEntity;
import io.revx.api.mysql.crmdb.entity.ServerSyncCoordinatorEntity;
import io.revx.core.enums.AuthMethod;
import io.revx.core.enums.CompressionType;
import io.revx.core.enums.CrmStatus;
import io.revx.core.enums.EncodingType;
import io.revx.core.model.audience.PixelDataFileDTO;
import io.revx.core.model.crm.FetchConfigDTO;
import io.revx.core.model.crm.Frequency;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;

@Component
public class CrmServiceUtils {

  public static final int MILLIS = 1000;

  public static long epochNow() {
      return System.currentTimeMillis() / MILLIS;
  }

  public static long add(long timeInSeconds, Frequency frequency) {
      DateTime time = new DateTime(timeInSeconds * MILLIS);
      switch (frequency.getUnit()) {
      case HOUR:
          time = time.plusHours(frequency.getValue());
          break;
      case DAY:
          time = time.plusDays(frequency.getValue());
          break;
      case MONTH:
          time = time.plusMonths(frequency.getValue());
          break;
      case WEEK:
          time = time.plusWeeks(frequency.getValue());
          break;
      default:
          throw new RuntimeException(frequency.getUnit()
                  + " is not implemented here.");
      }
      return time.getMillis() / MILLIS;
  }

  public static long NEVER_ENDING = -1L;
  
  public static PixelDataFileDTO getDtoFromEntity(PixelDataFileEntity pixelDataFileEntity) {
      PixelDataFileDTO file = new PixelDataFileDTO();
      file.setCreatedAt(pixelDataFileEntity.getCreatedAt());
      file.setEncodingType(pixelDataFileEntity.getEncodingType());
      file.setFilePath(pixelDataFileEntity.getFilePath());
      file.setId(pixelDataFileEntity.getId());
      file.setName(pixelDataFileEntity.getName());
      file.setPixelId(pixelDataFileEntity.getPixelId());
      file.setSourceType(pixelDataFileEntity.getSourceType());
      file.setUserDataType(pixelDataFileEntity.getUserDataType());
      file.setLicenseeId(pixelDataFileEntity.getLicenseeId());
      file.setCompressionType(pixelDataFileEntity.getCompressionType());
      file.setMd5sum(pixelDataFileEntity.getMd5sum());
      file.setLastModifiedAtServer(pixelDataFileEntity.getLastModifiedAtServer());
      file.setStatus(pixelDataFileEntity.getStatus());
      return file;
  }


  public static ServerSyncCoordinatorDTO getDtoFromEntity(
          ServerSyncCoordinatorEntity serverSyncCoordinatorEntity) {
      ServerSyncCoordinatorDTO schedule = new ServerSyncCoordinatorDTO();
      schedule.setConfig(getDtoFromEntity(serverSyncCoordinatorEntity.getConfig()));
      schedule.setFrequency(serverSyncCoordinatorEntity.getFrequency());
      schedule.setId(serverSyncCoordinatorEntity.getId());
      ServerSyncActionEntity lastAction = serverSyncCoordinatorEntity.getLastAction();
      if (lastAction != null) {
          schedule.setLastActionStatus(lastAction.getStatus());
          schedule.setLastActionTime(lastAction.getLastUpdated());
      }
      ServerSyncActionEntity lastSuccessAction = serverSyncCoordinatorEntity.getLastSuccessAction();
      if (lastSuccessAction != null) {
          schedule.setLastSuccessTime(lastSuccessAction.getLastUpdated());
      }
      schedule.setNextRunTime(serverSyncCoordinatorEntity.getNextRunTime());
      schedule.setStartTime(serverSyncCoordinatorEntity.getStartTime());
      return schedule;

  }
  

  public static FetchConfigDTO getDtoFromEntity(ServerFetchConfigEntity entity) {
      FetchConfigDTO config = new FetchConfigDTO();
      if (entity != null) {
          config.setHost(entity.getHost());
          config.setUserDataType(entity.getUserDataType());
          config.setEncodingType(entity.getEncodingType());
          config.setId(entity.getId());
          config.setPassword(entity.getPassword());
          config.setPathTemplate(entity.getPathTemplate());
          config.setPixelId(entity.getPixelId());
          config.setPixelId(entity.getPixelId());
          config.setPort(entity.getPort());
          config.setProtocol(entity.getProtocol());
          config.setUsername(entity.getUsername());
          config.setAuthMethod(entity.getAuthMethod());
          config.setKeyPath(entity.getKeyPath());
          config.setLicenseeId(entity.getLicenseeId());
          config.setCompressionType(entity.getCompressionType());
      }
      return config;
  }
  
  public static ServerSyncCoordinatorEntity getEntityFromDto(ServerSyncCoordinatorDTO vo) {
    ServerSyncCoordinatorEntity coordinatorEntity = new ServerSyncCoordinatorEntity();
    ServerFetchConfigEntity configEntity = new ServerFetchConfigEntity();
    coordinatorEntity.setConfig(configEntity);
    updateEntityFromDto(coordinatorEntity, vo);
    if (StringUtils.isNoneBlank(vo.getConfig().getUsername())
            && StringUtils.isNoneBlank(vo.getConfig().getPassword())) {
        configEntity.setAuthMethod(AuthMethod.LOGIN);
    }
    configEntity.setLicenseeId(vo.getConfig().getLicenseeId());
    configEntity.setPixelId(vo.getConfig().getPixelId());
    coordinatorEntity.setEndTime(NEVER_ENDING);
    coordinatorEntity.setStartTime(epochNow());
    coordinatorEntity.setStatus(CrmStatus.CREATED);
    return coordinatorEntity;
}

  public static void updateEntityFromDto(ServerSyncCoordinatorEntity entity,
      ServerSyncCoordinatorDTO dto) {
    updateEntityFromDto(entity.getConfig(), dto.getConfig());
    entity.setFrequency(dto.getFrequency());
  }
  
  public static void updateEntityFromDto(ServerFetchConfigEntity entity, FetchConfigDTO dto) {
    entity.setHost(dto.getHost());
    //entity.setUserDataType(dto.getUserDataType());
    entity.setEncodingType(null == dto.getEncodingType() ? EncodingType.NONE
            : dto.getEncodingType());
    entity.setPassword(dto.getPassword());
    entity.setPathTemplate(dto.getPathTemplate());
    entity.setPort(dto.getPort());
    entity.setProtocol(dto.getProtocol());
    entity.setUsername(dto.getUsername());
    entity.setAuthMethod(dto.getAuthMethod());
    entity.setKeyPath(dto.getKeyPath());
    entity.setCompressionType(null == dto.getCompressionType() ? CompressionType.NONE
            : dto.getCompressionType());
  }
  
  public static PixelDataFileEntity getEntityFromDto(PixelDataFileDTO vo) {
    PixelDataFileEntity file = new PixelDataFileEntity();
    file.setCreatedAt(vo.getCreatedAt());
    file.setEncodingType(null == vo.getEncodingType() ? EncodingType.NONE
            : vo.getEncodingType());
    file.setFilePath(vo.getFilePath());
    file.setId(vo.getId());
    file.setName(vo.getName());
    file.setPixelId(vo.getPixelId());
    file.setSourceType(vo.getSourceType());
    file.setUserDataType(vo.getUserDataType());
    file.setLicenseeId(vo.getLicenseeId());
    CompressionType compressionType = vo.getCompressionType();
    if (compressionType == null)
        compressionType = CompressionType.getCompressionByExtension(vo
                .getName());
    file.setCompressionType(null == compressionType ? CompressionType.NONE
            : compressionType);
    file.setLastModifiedAtServer(vo.getLastModifiedAtServer());
    file.setMd5sum(vo.getMd5sum());
    file.setLastModifiedAtServer(vo.getLastModifiedAtServer());
    return file;
}

}