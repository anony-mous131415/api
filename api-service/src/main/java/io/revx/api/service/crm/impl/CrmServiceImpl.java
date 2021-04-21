package io.revx.api.service.crm.impl;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.revx.api.mysql.crmdb.entity.PixelDataFileEntity;
import io.revx.api.mysql.crmdb.entity.ServerFetchConfigEntity;
import io.revx.api.mysql.crmdb.entity.ServerSyncActionEntity;
import io.revx.api.mysql.crmdb.entity.ServerSyncCoordinatorEntity;
import io.revx.api.mysql.crmdb.repo.PixelDataFileRepository;
import io.revx.api.mysql.crmdb.repo.ServerFetchConfigRepository;
import io.revx.api.mysql.crmdb.repo.ServerSyncActionEntityRepository;
import io.revx.api.mysql.crmdb.repo.ServerSyncCoordinatorRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.crm.CrmService;
import io.revx.api.service.crm.CrmServiceUtils;
import io.revx.core.enums.CrmStatus;
import io.revx.core.exception.ApiException;
import io.revx.core.model.audience.PixelDataFileDTO;
import io.revx.core.model.crm.RemoteFileDTO;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;

@Component
public class CrmServiceImpl implements CrmService{

  private static Logger logger = LogManager.getLogger(CrmServiceImpl.class);

  @Autowired
  PixelDataFileRepository pixelDataFileRepository;
  
  @Autowired
  ServerFetchConfigRepository serverFetchConfigRepository;
  
  @Autowired
  ServerSyncCoordinatorRepository serverSyncCoordinatorRepository;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;
  
  @Autowired
  ServerSyncActionEntityRepository serverSyncActionEntityRepository;
  
  @Override
  public ServerSyncCoordinatorDTO updateSyncCoordinator(
      ServerSyncCoordinatorDTO dto, Long pixelId) {
    ServerSyncCoordinatorEntity entity = getCoordinatorEntity(pixelId);
    CrmServiceUtils.updateEntityFromDto(entity, dto);
    serverSyncCoordinatorRepository.save(entity);
    return CrmServiceUtils.getDtoFromEntity(entity);
  }

  @Override
  public PixelDataFileDTO createPixelDataFile(PixelDataFileDTO pixelDataFile) {
    logger.info("Inside createPixelDataFile method. Called with createPixelDataFile: {}", pixelDataFile);
    List<PixelDataFileEntity> previousFiles = pixelDataFileRepository.findByPixelId(pixelDataFile.getPixelId());
       
    PixelDataFileEntity dto = CrmServiceUtils.getEntityFromDto(pixelDataFile);
    dto.setStatus(CrmStatus.CREATED);
    dto.setCreatedAt(CrmServiceUtils.epochNow());
    dto.setNewFile(true);
    dto.setLicenseeId(loginUserDetailsService.getLicenseeId());
    dto = pixelDataFileRepository.save(dto);
    for (PixelDataFileEntity oldDto : previousFiles) {
        oldDto.setNewFile(false);
        oldDto.setUpdatedAt(CrmServiceUtils.epochNow());
        pixelDataFileRepository.save(oldDto);
    }
    return CrmServiceUtils.getDtoFromEntity(dto);
}

  @Override
  public ServerSyncCoordinatorDTO createSyncCoordinator(
      ServerSyncCoordinatorDTO coordinator) throws ApiException {
    if (getConfigEntity(coordinator.getConfig().getPixelId()) != null) {
      throw new ApiException(
              "A schedule already exists for this pixel");
    }
    ServerSyncCoordinatorEntity dto = CrmServiceUtils.getEntityFromDto(coordinator);
    ServerFetchConfigEntity configDto = dto.getConfig();
    configDto.setUserDataType(coordinator.getConfig().getUserDataType());
    configDto.setCoordinator(dto);
    ServerSyncCoordinatorEntity dtoSaved = serverSyncCoordinatorRepository.save(dto);
    return CrmServiceUtils.getDtoFromEntity(dtoSaved);
  }

  private ServerFetchConfigEntity getConfigEntity(Long pixelId) {
    ServerFetchConfigEntity configDto = new ServerFetchConfigEntity();
    configDto.setPixelId(pixelId);
    return serverFetchConfigRepository.findByPixelId(pixelId);
  }
  
  @Transactional
  @Override
  public CrmStatus forceSyncAction(Long pixelId) {
    logger.info("inside forceSyncAction for pixelId : {}", pixelId);
    ServerSyncCoordinatorEntity coordinator = getCoordinatorEntity(pixelId);
    ServerSyncActionEntity lastActionDto = coordinator.getLastAction();
    if (lastActionDto != null && CrmStatus.isRunning(lastActionDto.getStatus())) {
      return CrmStatus.RUNNING;
    } else {
      coordinator.setNextRunTime(CrmServiceUtils.epochNow());
      syncNow(coordinator);
      return CrmStatus.SUCCESS;
    }
  }

  @Transactional
  private void syncNow(ServerSyncCoordinatorEntity dto) {
    ServerSyncActionEntity actionDto = new ServerSyncActionEntity();
      actionDto.setNominalTime(dto.getNextRunTime());
      actionDto.setCoordinator(dto.getNewWithId());
      actionDto.setStatus(CrmStatus.CREATED);
      serverSyncActionEntityRepository.save(actionDto);
      dto.setLastAction(actionDto.getNewWithId());
      dto.setNextRunTime(null);
      serverSyncCoordinatorRepository.save(dto);
  }

  
  @Override
  public CrmStatus s2sCredentialsCheck(RemoteFileDTO remoteFileDTO) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<PixelDataFileDTO> getPixelDataFilesByLicensee(Long licenseeId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<ServerSyncCoordinatorDTO> getSyncCoordinatorsByLicensee(Long licenseeId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public PixelDataFileDTO getPixelDataFileByPixelId(Long pixelId) {
    logger.info("Inside getPixelDataFileByPixelId method. For pixel Id : "+pixelId);
    PixelDataFileDTO pixelDataFileDTO = null;
    
    PixelDataFileEntity entity = pixelDataFileRepository.findByPixelIdAndNewFile(pixelId, true);
    if(entity != null)
      pixelDataFileDTO = CrmServiceUtils.getDtoFromEntity(entity);
    return pixelDataFileDTO;
  }

  @Override
  public ServerSyncCoordinatorDTO getSyncCoordinatorByPixelId(Long pixelId) {
    ServerSyncCoordinatorDTO dto = null;
    ServerSyncCoordinatorEntity entity = getCoordinatorEntity(pixelId);
    if(entity != null)
      dto = CrmServiceUtils.getDtoFromEntity(entity);
    return dto;
  }


  private ServerSyncCoordinatorEntity getCoordinatorEntity(Long pixelId) {
    ServerFetchConfigEntity serverFetchConfigEntity = new ServerFetchConfigEntity();
    serverFetchConfigEntity = serverFetchConfigRepository.findByPixelId(pixelId);
    return serverFetchConfigEntity != null ? serverFetchConfigEntity.getCoordinator() : null;
}
}
