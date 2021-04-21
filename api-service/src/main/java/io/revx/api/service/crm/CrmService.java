package io.revx.api.service.crm;

import java.util.List;
import io.revx.core.enums.CrmStatus;
import io.revx.core.exception.ApiException;
import io.revx.core.model.audience.PixelDataFileDTO;
import io.revx.core.model.crm.RemoteFileDTO;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;

public interface CrmService {

  public ServerSyncCoordinatorDTO updateSyncCoordinator(
      ServerSyncCoordinatorDTO serverSyncCoordinatorDTO, Long pixelId);
  
  public PixelDataFileDTO createPixelDataFile(PixelDataFileDTO pixelDataFileDTO);
  
  public ServerSyncCoordinatorDTO createSyncCoordinator(
      ServerSyncCoordinatorDTO serverSyncCoordinatorDTO) throws ApiException;
  
  public CrmStatus forceSyncAction(Long pixelId);
  
  public CrmStatus s2sCredentialsCheck(RemoteFileDTO remoteFileDTO);
  
  public List<PixelDataFileDTO> getPixelDataFilesByLicensee(Long licenseeId);
  
  public List<ServerSyncCoordinatorDTO> getSyncCoordinatorsByLicensee(
      Long licenseeId);
  
  public PixelDataFileDTO getPixelDataFileByPixelId(Long pixelId);
  
  public ServerSyncCoordinatorDTO getSyncCoordinatorByPixelId(Long pixelId);
}
