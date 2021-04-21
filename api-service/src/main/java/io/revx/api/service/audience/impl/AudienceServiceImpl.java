package io.revx.api.service.audience.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.revx.core.response.ResponseMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import io.revx.api.audience.pojo.AudienceAccessDTO;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.controller.audience.AudienceController;
import io.revx.api.enums.Status;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.amtdb.entity.RuleComponent;
import io.revx.api.mysql.amtdb.entity.SegmentPixelMap;
import io.revx.api.mysql.amtdb.entity.SegmentType;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.amtdb.repo.RuleComponentRepository;
import io.revx.api.mysql.amtdb.repo.SegmentPixelMapRepository;
import io.revx.api.mysql.amtdb.repo.SegmentsRepository;
import io.revx.api.mysql.crmdb.entity.ServerFetchConfigEntity;
import io.revx.api.mysql.crmdb.repo.ServerFetchConfigRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserSegmentMappingEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.mysql.repo.advertiser.AdvertiserSegmentMappingRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.audience.AudienceUtils;
import io.revx.api.service.crm.impl.CrmServiceImpl;
import io.revx.api.service.dmp.DmpService;
import io.revx.api.service.pixel.PixelUtils;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.constant.Constants;
import io.revx.core.enums.CompressionType;
import io.revx.core.enums.CrmStatus;
import io.revx.core.enums.DataSourceType;
import io.revx.core.enums.Protocol;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.DmpAudience;
import io.revx.core.model.audience.DmpAudienceDTO;
import io.revx.core.model.audience.PixelDataFileDTO;
import io.revx.core.model.audience.PixelRemoteConfigDTO;
import io.revx.core.model.audience.PlatformAudienceDTO;
import io.revx.core.model.audience.UserDataType;
import io.revx.core.model.crm.FetchConfigDTO;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;
import io.revx.core.model.pixel.DataPixelDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.Error;

@Component
public class AudienceServiceImpl {

  @Autowired
  SegmentsRepository segmentsRepository;
  
  @Autowired
  AudienceUtils audienceUtils;
  
  @Autowired
  PixelUtils pixelUtils;
  
  @Autowired
  AdvertiserRepository advertiserRepository;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;
  
  @Autowired
  CrmServiceImpl crmService;

  @Autowired
  EntityESService elasticSearch;

  @Autowired
  CustomESRepositoryImpl elastic;
  
  @Autowired
  DataPixelServiceImpl dataPixelServiceImpl;
  
  @Autowired
  AudienceCacheService audienceCacheService;
  
  @Autowired
  SegmentPixelMapRepository segmentPixelMapRepository;
  
  @Autowired
  RuleComponentRepository ruleComponentRepository;
  
  @Autowired
  CrmServiceImpl crmServiceImpl;
  
  @Autowired
  AdvertiserSegmentMappingRepository advertiserSegmentMappingRepository;

  @Autowired
  ApplicationProperties properties;
  
  @Autowired
  ServerFetchConfigRepository serverFetchConfigRepository;
  
  @Autowired
  DmpService dmpService;
  
  private static Logger logger = LogManager.getLogger(AudienceController.class);
  
  @Transactional
  public ApiResponseObject<AudienceDTO> createAudience(AudienceDTO audienceDTO) throws Exception {
    logger.info("Inside createAudience method. Creating Audience for  : {} ", audienceDTO);
    audienceUtils.validateDTO(audienceDTO, false);
    AudienceDTO responseAudDTO = createNewAudience(audienceDTO);
    ApiResponseObject<AudienceDTO> resp = new ApiResponseObject<>();
    resp.setRespObject(responseAudDTO);
    return resp;
  }
  
  @Transactional
  public ApiResponseObject<AudienceDTO> updateAudience(AudienceDTO audienceDTO) throws Exception {
    logger.info("Inside updateAudience method. Update Audience for  : {} ", audienceDTO);
    audienceUtils.validateDTO(audienceDTO, true);
    AudienceDTO responseAudDTO = updateExstingAudience(audienceDTO);
    ApiResponseObject<AudienceDTO> resp = new ApiResponseObject<>();
    resp.setRespObject(responseAudDTO);
    return resp;
  }

  @Transactional
  public ApiResponseObject<Map<Integer, ResponseMessage>> activate(String ids) throws ApiException {
    if(StringUtils.isBlank(ids))
      throw new ApiException("Nothing to update. Audience id list is empty.");
    
    List<String> idStrList = Arrays.asList(ids.split(","));
    List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());
    
    ApiResponseObject<Map<Integer, ResponseMessage>> responseObject = new ApiResponseObject<>();
    Map<Integer, ResponseMessage> response = updateStatus(idList, Status.ACTIVE);   
    responseObject.setRespObject(response);
    return responseObject;
  }

  @Transactional
  public ApiResponseObject<Map<Integer, ResponseMessage>> deactivate(String ids) throws ApiException {
    if(StringUtils.isBlank(ids))
      throw new ApiException("Nothing to update. Audience id list is empty.");
    
    List<String> idStrList = Arrays.asList(ids.split(","));
    List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());
    
    ApiResponseObject<Map<Integer, ResponseMessage>> responseObject = new ApiResponseObject<>();
    Map<Integer, ResponseMessage> response = updateStatus(idList, Status.INACTIVE); 
    responseObject.setRespObject(response);
    return responseObject;
  }

  @Transactional
  private AudienceDTO createNewAudience(AudienceDTO audienceDTO) throws ApiException {

    Segments segmentsEntity = null;
    Boolean audAlreadyExist = false;
    if(audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType().equals(SegmentType.DMP.id)) {
      segmentsEntity = segmentsRepository.findByRemoteSegmentId(audienceDTO.getRemoteSegmentId());
      if(segmentsEntity != null) {
        logger.debug("DMP Audinece already exist for this id. Adding mapping for this advertiser.");
        audAlreadyExist = true;
      }else {
        logger.debug("DMP Audinece is not exist for this id. Sending request to dmp for sync.");
        dmpService.syncDmpAudience(audienceDTO.getRemoteSegmentId());
      }
    }
    
    if(!audAlreadyExist) {
      DataSourceType sourceType = DataSourceType.getById(audienceDTO.getDataSourceType());
      Long pixelId = dataPixelServiceImpl.createAdvertiserToPixel(audienceDTO.getAdvertiser(), sourceType);
      audienceDTO.setPixelId(pixelId);
      
      segmentsEntity = audienceUtils.getEntityFromDto(audienceDTO);
      segmentsRepository.save(segmentsEntity);
      
      createRuleExpression(audienceDTO, segmentsEntity, null);
      createCRMAudienceDetails(audienceDTO, false);
    }
    
    if(audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType().equals(SegmentType.DMP.id)) {
      AdvertiserSegmentMappingEntity mappingEntity = audienceUtils.getEntity(audienceDTO.getAdvertiser().getId(), segmentsEntity.getId(), audienceDTO.getRemoteSegmentId());
      advertiserSegmentMappingRepository.save(mappingEntity);
    }
    
    AudienceDTO responseAudDTO = new AudienceDTO();
    audienceUtils.populateAudienceDTO(segmentsEntity, responseAudDTO);
    
    elastic.save(AudienceUtils.getESDTO(responseAudDTO), TablesEntity.AUDIENCE);
    if(responseAudDTO.getSegmentType() != null && responseAudDTO.getSegmentType() == SegmentType.DMP.id)
      elastic.save(AudienceUtils.getDmpESDTO(responseAudDTO), TablesEntity.DMP_AUDIENCE);
    
    audienceCacheService.remove();
    return responseAudDTO;
  }
  
  @Transactional
  public void createRuleExpression(AudienceDTO audienceDTO, Segments segmentsEntity, Long pixelMapId) {

    SegmentPixelMap segmentPixelMap = audienceUtils.getMapEntityFromDto(audienceDTO, segmentsEntity.getId());
    if(pixelMapId != null)
      segmentPixelMap.setId(pixelMapId);
    else
      segmentPixelMapRepository.save(segmentPixelMap);
    
    if(StringUtils.isNoneBlank(segmentPixelMap.getRuleExpression())) {
      List<RuleComponent> ruleComponents = audienceUtils.getEntityFromDto(audienceDTO, segmentPixelMap.getId());
      if(pixelMapId != null)
        ruleComponentRepository.deleteBySegmentPixelExpressionId(pixelMapId);
      ruleComponentRepository.saveAll(ruleComponents);
      
      String finalExpressions = AudienceUtils.prepareExpression(segmentPixelMap.getRuleExpression(), ruleComponents);
      segmentPixelMap.setRuleExpression(finalExpressions);
      segmentPixelMapRepository.save(segmentPixelMap);
    }
    
  }
  
  @Transactional
  public void createCRMAudienceDetails(AudienceDTO audienceDTO, boolean isUpdate) throws ApiException
  {
      Long pixelId = audienceDTO.getPixelId();
      UserDataType userDataType = UserDataType.getById(audienceDTO.getUserDataType());
      if (audienceDTO.getDataSourceType() == DataSourceType.AUDIENCE_FEED.id) {
          ServerSyncCoordinatorDTO coordinator = audienceDTO.getPixelDataSchedule().getSchedule(loginUserDetailsService.getLicenseeId());
          FetchConfigDTO config = coordinator.getConfig();
          config.setPixelId(pixelId);
          config.setUserDataType(userDataType);
          if(isUpdate)
            crmService.updateSyncCoordinator(coordinator, pixelId);
          else
            crmService.createSyncCoordinator(coordinator);
      } else if (audienceDTO.getDataSourceType() == DataSourceType.FILE_UPLOAD.id) {
          PixelDataFileDTO file = audienceDTO.getPixelDataFile();
          file.setPixelId(pixelId);
          file.setUserDataType(userDataType);
          file.setSourceType(DataSourceType.FILE_UPLOAD);
          crmService.createPixelDataFile(file);
      } else {
          // Do nothing
      }
  }

  public AudienceDTO updateExstingAudience(AudienceDTO audienceDTO) throws ApiException {

    Segments segmentEntity = audienceCacheService.fetchAudience(audienceDTO.getId(), true);

    if (segmentEntity == null)
      throw new ValidationException("Invalid Audience Id");

    if(segmentEntity.getSegmentType() != null && segmentEntity.getSegmentType().equals(SegmentType.DMP))
      throw new ValidationException("DMP Audience update is not supported.");
    
    List<SegmentPixelMap> segmentPixelMapEntity =
        segmentPixelMapRepository.findBySegmentId(segmentEntity.getId());
   
    Long pixelId = segmentPixelMapEntity.get(0).getPixelId();
    DataPixelDTO dataPixelDTO = dataPixelServiceImpl.getDataPixel(pixelId);
    
    if(dataPixelDTO == null)
      throw new ValidationException("Valid pixel not present for this audience");
    if(!dataPixelDTO.getSourceType().equals(DataSourceType.getById(audienceDTO.getDataSourceType())))
      throw new ValidationException("Updated Source type is different than actual");
    
    AudienceUtils.updateEntityFromDto(audienceDTO, segmentEntity);
    segmentsRepository.save(segmentEntity);

    createRuleExpression(audienceDTO, segmentEntity, segmentPixelMapEntity.get(0).getId());
    createCRMAudienceDetails(audienceDTO, true);

    AudienceDTO responseAudDTO = new AudienceDTO();
    audienceUtils.populateAudienceDTO(segmentEntity, responseAudDTO);
    elastic.save(AudienceUtils.getESDTO(responseAudDTO), TablesEntity.AUDIENCE);
    audienceCacheService.remove();
    return responseAudDTO;
  }
  
  @Transactional
  public ApiResponseObject<BaseModel> syncRemoteAudience(Long audienceId) throws ValidationException,ApiException  {
    ApiResponseObject<BaseModel> resp =  new ApiResponseObject<BaseModel>();
    try {
        Segments segmentEntity = audienceCacheService.fetchAudience(audienceId, true);

        if (segmentEntity == null)
          throw new ValidationException("Invalid Audience Id");

        if(segmentEntity.getSegmentType() != null && segmentEntity.getSegmentType().equals(SegmentType.DMP))
          throw new ValidationException("DMP Audience Sync is not supported.");
        
        List<SegmentPixelMap> segmentPixelMapEntity =
            segmentPixelMapRepository.findBySegmentId(segmentEntity.getId());
       
        Long pixelId = segmentPixelMapEntity.get(0).getPixelId();
        
        CrmStatus status = crmServiceImpl.forceSyncAction(pixelId);
        BaseModel model = new BaseModel();
        model.setId(status.id.longValue());
        model.setName(status.name());
        
        resp.setRespObject(model);
        
     } catch(Exception e) {
       resp.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR.getValue(), e.getMessage()));
     }
    return resp;
}

  public ApiResponseObject<BaseModel> checkConnection(PixelRemoteConfigDTO config) {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Transactional
  public Map<Integer, ResponseMessage> updateStatus(List<Long> idList, Status status) {
    Map<Integer, ResponseMessage> response = new HashMap<>();

    logger.info("Inside updateStatus method to update status as : {}. audience ids : {}", status, idList);
    for (Long id : idList) {
      Segments entity = audienceCacheService.fetchAudience(id, true);
      logger.debug("Inside updateStatus method. Got Audience entity : " + entity);

      if (entity == null) {
        ResponseMessage responseMessage =
            new ResponseMessage(Constants.EC_ID_MISSING, Constants.MSG_ID_INVALID);
        response.put(id.intValue(), responseMessage);
        continue;
      } 
      
      if (entity.getSegmentType() != null
          && entity.getSegmentType().equals(SegmentType.DMP)) {
        ResponseMessage responseMessage = new ResponseMessage(Constants.EC_OPERATION_NOT_SUPPORTED,
            Constants.MSG_OPERATION_NOT_SUPPORTED);
        response.put(id.intValue(), responseMessage);
        continue;
      }

      if (entity.getStatus().equals(status)) {
        ResponseMessage responseMessage = null;
        if (status.equals(Status.ACTIVE))
          responseMessage =
              new ResponseMessage(Constants.ID_ALREADY_ACTIVE, Constants.MSG_ID_ALREADY_ACTIVE);
        else
          responseMessage =
              new ResponseMessage(Constants.ID_ALREADY_INACTIVE, Constants.MSG_ID_ALREADY_INACTIVE);

        response.put(id.intValue(), responseMessage);
      } else {
        Integer rowUpdated = segmentsRepository.updateStatus(status, id);
        logger.debug("Updated audience status in DB. Total rows updated : {}", rowUpdated);
        if (rowUpdated < 1) {
          ResponseMessage responseMessage =
              new ResponseMessage(Constants.FAILERE, Constants.MSG_DB_ERROR);
          response.put(id.intValue(), responseMessage);
        } else {
          ResponseMessage responseMessage =
              new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS);
          response.put(id.intValue(), responseMessage);
          updateStatusInES(id, status.equals(Status.ACTIVE) ? Boolean.TRUE : Boolean.FALSE);
        }
      }

    }
    return response;
  }
  
  public ApiResponseObject<AudienceDTO> getAudience(Long id, Boolean refresh) throws Exception {
    AudienceDTO audienceDTO = new AudienceDTO();
    ApiResponseObject<AudienceDTO> response = new ApiResponseObject<>();
    
    logger.info("Inside getAudience method. audience id : {} ",id);
    Segments audience = audienceCacheService.fetchAudience(id, refresh);

    logger.debug("Inside getbyId method. Got audience entity : "+audience);
    if (audience == null)
      throw new ValidationException("audience id is not valid");

    audienceUtils.populateAudienceDTO(audience, audienceDTO);
    
    logger.info("Inside getAudience method. Sending audience : {} ",audienceDTO);
    
    response.setRespObject(audienceDTO);
    return response;
  }
  

  public ApiListResponse<List<AudienceESDTO>> getAllAudience(Long advertiserId, int  pageNumber,int  pageSize,String  sort,SearchRequest  search, Boolean refresh) throws ApiException {
    ApiListResponse<List<AudienceESDTO>> response = new ApiListResponse<>();
    try {
      response = audienceCacheService.fetchAllAudience(advertiserId, pageNumber, pageSize, sort, search, refresh);
    }catch (Exception e) {
      response.setTotalNoOfRecords(0);
    }
    return response;
  }
  
  
  private void updateStatusInES(Long i, Boolean status) {
    try {
      AudienceESDTO segment = (AudienceESDTO) elastic.findDetailById(TablesEntity.AUDIENCE.getElasticIndex(),
          String.valueOf(i), TablesEntity.AUDIENCE.getElasticPojoClass());
      logger.debug("Updating status with : {} for audinece in ES : {}", status, segment);
      if (segment == null)
        return;

      segment.setActive(status);
      elastic.save(segment, TablesEntity.AUDIENCE);
      audienceCacheService.remove();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  

  public ApiResponseObject<AudienceAccessDTO> getAcces(Long advertiserId) throws ValidationException {

    AdvertiserEntity advertiserEntity = advertiserRepository.findByIdAndLicenseeId(advertiserId, loginUserDetailsService.getLicenseeId());
    
    if (advertiserEntity == null) {
      throw new ValidationException("Invalid advertiser id : " + advertiserId);
    }
    
    AudienceAccessDTO accessDTO = new AudienceAccessDTO();
    accessDTO.setIsDmpAccess(advertiserEntity.getIsDmpAudienceSupport());
    accessDTO.setIsPlatformAccess(advertiserEntity.getIsPlatformAudienceSupport());
    
    ApiResponseObject<AudienceAccessDTO> resp = new ApiResponseObject<>();
    resp.setRespObject(accessDTO);
    return resp;
  }
 

  public ApiResponseObject<DmpAudienceDTO> getDmpAudience(Long advertiser_id, Integer start, Integer limit, Integer stype) {
    DmpAudienceDTO dmpAudienceDTO = new DmpAudienceDTO();
    dmpAudienceDTO.setLimit(limit != null ? limit : -1);
    dmpAudienceDTO.setStart(start != null ? start : 1);
    
    ApiResponseObject<DmpAudienceDTO> resp = new ApiResponseObject<>();
    if(advertiser_id%2 == 0) {
      dmpAudienceDTO.setSegment_count(0L);
      resp.setRespObject(dmpAudienceDTO);
      return resp;
    }
    
    dmpAudienceDTO.setSegment_count(85L);
    
    if(start == null)
      start = 1;
    if(limit == null || limit == -1)
      limit = 10;
    
    
    List<DmpAudience> list = new ArrayList<>();
    for(Integer i=0; i< limit ; i++) {
      DmpAudience aud = new DmpAudience();
      aud.setSid(String.valueOf(i));
      aud.setSname("Segmetn-"+i);
      aud.setScount(i*12345L);
      if(stype != null)
        aud.setStype(stype);
      else
        aud.setStype(i%2 == 0 ? 1 : 2);
      
      aud.setIsSynced(i%2 == 0 ? true : false);
      
      list.add(aud);
    }
    dmpAudienceDTO.setSegments(list);
    
    
    resp.setRespObject(dmpAudienceDTO);
    
    return resp;
  }

  public ApiResponseObject<List<AudienceDTO>> getSyncedDmpAudience(Long advertiserId) {
    ApiResponseObject<List<AudienceDTO>> resp = new ApiResponseObject<>();
    List<AudienceDTO> audienceDTOs = new ArrayList<>();
    logger.info("Got DMP audience get request for advertiserId : {}", advertiserId);
    try {
      List<Long> segmentIds = advertiserSegmentMappingRepository.findSegmentIdByAdvertiserIdAndLicenseeId(advertiserId, loginUserDetailsService.getLicenseeId());
      
      List<Segments> audiences = new ArrayList<>();
      if(CollectionUtils.isNotEmpty(segmentIds)) {
        audiences = segmentsRepository.findAllById(segmentIds);
      }
      
      for(Segments audience : audiences) {
        AudienceDTO audienceDTO = new  AudienceDTO();
        audienceUtils.populateAudienceDTO(audience, audienceDTO);
        audienceDTOs.add(audienceDTO);
      }
      
      resp.setRespObject(audienceDTOs);
   }catch (Exception e) {
     resp.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR.getValue(), Constants.MSG_ERROR));
   }
  
   return resp;
    
  }
  
  @Transactional
  public Boolean syncPlatformAudience(PlatformAudienceDTO platformAudienceDTO) {
    if(StringUtils.isBlank(platformAudienceDTO.getUrl()) || platformAudienceDTO.getContainer_id() == null)
      return false;
    logger.info("Got platform audience sync call with platformAudienceDTO : {} "+platformAudienceDTO);
    String remoteSegmentId = platformAudienceDTO.getContainer_id();
    String s3url = platformAudienceDTO.getUrl();
    
    Segments audience = segmentsRepository.findByRemoteSegmentId(remoteSegmentId);
    if(audience == null)
      return false;
    
    List<SegmentPixelMap> segmentPixelMaps = segmentPixelMapRepository.findBySegmentId(audience.getId());
    
    if(CollectionUtils.isEmpty(segmentPixelMaps))
      return false;
    
    ServerFetchConfigEntity serverFetchConfigEntity = serverFetchConfigRepository.findByPixelId(segmentPixelMaps.get(0).getPixelId());
    serverFetchConfigEntity.setCompressionType(CompressionType.GZIP);
    serverFetchConfigEntity.setProtocol(Protocol.HTTP);
    serverFetchConfigEntity.setHost(properties.getS3host());
    serverFetchConfigEntity.setPathTemplate(s3url.replace(properties.getS3SegmentBucketUrlPath(), ""));
    
    serverFetchConfigRepository.save(serverFetchConfigEntity);
    
    audience.setStatus(Status.ACTIVE);
    segmentsRepository.save(audience);
    
    return true;
  }
}
