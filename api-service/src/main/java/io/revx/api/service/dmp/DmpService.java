package io.revx.api.service.dmp;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.amtdb.repo.SegmentsRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserSegmentMappingEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserSegmentMappingRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.exception.ErrorCode;
import io.revx.core.model.audience.DmpAudience;
import io.revx.core.model.audience.DmpAudienceCriteria;
import io.revx.core.model.audience.DmpAudienceDTO;
import io.revx.core.model.audience.DmpSyncAudienceDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.Error;
import io.revx.core.restclient.service.RestServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;

@Component
public class DmpService {

  private static String AID = "aid";
  private static String AUTHENTICATION_HEADER = "token";
  private static String SID = "sid";
  
  @Autowired
  ApplicationProperties applicationProperties;
  
  @Autowired
  RestServiceClient restServiceClient;
  
  @Autowired
  AdvertiserSegmentMappingRepository advertiserSegmentMappingRepository;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;
  
  @Autowired
  SegmentsRepository segmentsRepository;
  
  private static Logger logger = LogManager.getLogger(DmpService.class);
  
  public ApiResponseObject<DmpAudienceDTO> getDmpAudience(Long advertiserId, Integer start, Integer limit, Integer stype) {
    ApiResponseObject<DmpAudienceDTO> resp = new ApiResponseObject<>();
    logger.info("Got DMP audience get request for stype : {}, start : {}, limit : {}", stype, start, limit);
    try {
      ObjectMapper om = new ObjectMapper();
      om.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
      
      DmpAudienceDTO dmpAudienceDTO = null;
      if(start == null || start <= 0)
        start = 1;
      if(limit == null || limit <= 0)
        limit = -1;
      
      MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
      queryParams.add(ApiConstant.START, String.valueOf(start));
      queryParams.add(ApiConstant.LIMIT, String.valueOf(limit));
      queryParams.add(AID, applicationProperties.getDmpAid());
      queryParams.add(AUTHENTICATION_HEADER, applicationProperties.getDmpToken());
      
      logger.info("Calling DMP URL : {} & Qeury params : {}", applicationProperties.getDmpUri(), queryParams);
      String response = restServiceClient.doGet(applicationProperties.getDmpUri(), queryParams, null);
      logger.info("Got DMP Response : {}", response);
      
      dmpAudienceDTO = om.readValue(response, DmpAudienceDTO.class);
      logger.info("Parsed DMP Response  : {}", dmpAudienceDTO);
      
      List<AdvertiserSegmentMappingEntity> advertiserSegmentMappingEntity = advertiserSegmentMappingRepository.findByAdvertiserIdAndLicenseeId(advertiserId, loginUserDetailsService.getLicenseeId());
      
      if(dmpAudienceDTO != null && dmpAudienceDTO.getSegments() != null) {
        List<DmpAudience> segments = new ArrayList<>();
        for(DmpAudience dmpAudience : dmpAudienceDTO.getSegments()) {
          logger.info("Iterating over DMP Audience : {}", dmpAudience);
          if(containsRemoteSegmentId(advertiserSegmentMappingEntity, dmpAudience.getSid().toString())) {
            logger.info("DMP Audience is already synced : {}", dmpAudience);
            dmpAudience.setIsSynced(true);
          }else {
            dmpAudience.setIsSynced(false);
          }
          
          if(stype != null && dmpAudience.getStype() != null &&  !dmpAudience.getStype().equals(stype)) {
            logger.info("Filtering dmp audience as segment type is not matching with requested type. dmpAudience : {}", dmpAudience);
          }else {
            String description = ""; 
            if(dmpAudience.getCriteria() != null) {
              for(DmpAudienceCriteria criteria : dmpAudience.getCriteria()) {
                if(StringUtils.isNoneBlank(description))
                  description = description + " AND ";
                if(StringUtils.isNoneBlank(criteria.getKey()) && StringUtils.isNoneBlank(criteria.getValue()) )
                  description = description + "( " +criteria.getKey() +" " + criteria.getOperator() +" " + criteria.getValue() + " )";
              }
            }
            if(StringUtils.isNotBlank(description))
              dmpAudience.setSdescription(description);
            segments.add(dmpAudience);
          }
        }
        dmpAudienceDTO.setSegments(segments);
      }
        
      resp.setRespObject(dmpAudienceDTO);
   }catch (Exception e) {
     e.printStackTrace();
     logger.error("Error while retriving dmp audience : ", e);
     resp.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR.getValue(), "Internal Error while retriving dmp audience. Please try after some time."));
   }
   logger.debug("Sending dmp audience response : {}", resp);
   return resp;
    
  }
  

  public ApiResponseObject<DmpSyncAudienceDTO> syncDmpAudience(String sid) {
    ApiResponseObject<DmpSyncAudienceDTO> resp = new ApiResponseObject<>();
    logger.info("Got DMP audience sync request for sid : {}" ,sid);
    
    try {
      ObjectMapper om = new ObjectMapper();
      om.configure(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
      
      
      MultivaluedMap<String, String> queryParams = new MultivaluedHashMap<String, String>();
      queryParams.add(SID, String.valueOf(sid));
      queryParams.add(AID, applicationProperties.getDmpAid());
      queryParams.add(AUTHENTICATION_HEADER, applicationProperties.getDmpToken());
      
      logger.info("Calling DMP sync URL : {} & Query params : {}", applicationProperties.getDmpSyncUri(), queryParams);
      String response = restServiceClient.doGet(applicationProperties.getDmpSyncUri(), queryParams, null);
      logger.info("Got DMP Response : {}", response);

      DmpSyncAudienceDTO dmpSyncAudienceDTO = om.readValue(response, DmpSyncAudienceDTO.class);
      logger.info("Formed dmpSyncAudienceDTO : {}", dmpSyncAudienceDTO);
      
      resp.setRespObject(dmpSyncAudienceDTO);
   }catch (Exception e) {
     resp.setError(new Error(ErrorCode.INTERNAL_SERVER_ERROR.getValue(), "Internal Error while retriving dmp audience. Please try after some time."));
   }
  
   return resp;
    
  }
  
 
  
  public boolean containsRemoteSegmentId(List<AdvertiserSegmentMappingEntity> list, String name){
    logger.debug("Checking segment : {} is already synced or not. AdvertiserSegmentMapping List", name, list);
    return list.stream().filter(o -> o.getRemoteSegmentId().equals(name)).findFirst().isPresent();
  }
}
