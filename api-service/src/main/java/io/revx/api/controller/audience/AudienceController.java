package io.revx.api.controller.audience;

import java.util.List;
import java.util.Map;

import io.revx.core.response.ResponseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.revx.api.audience.pojo.AudienceAccessDTO;
import io.revx.api.audience.pojo.MetaRulesDto;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.audience.AudienceServiceMockImpl;
import io.revx.api.service.audience.impl.AudienceServiceImpl;
import io.revx.api.service.audience.impl.RuleServiceImpl;
import io.revx.api.service.dmp.DmpService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.DmpAudienceDTO;
import io.revx.core.model.audience.PixelRemoteConfigDTO;
import io.revx.core.model.audience.PlatformAudienceDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Audience Controller", tags = {"Audience Controller"},
description = "Rest API's for Audience creation,update etc.")
public class AudienceController {

  private static Logger logger = LogManager.getLogger(AudienceController.class);

  @Autowired
  private AudienceServiceMockImpl audienceServiceMock;

  @Autowired
  private AudienceServiceImpl audienceService;

  @Autowired
  private RuleServiceImpl ruleService;

  @Autowired
  private DmpService dmpService;

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE)
  @ApiOperation("Create Api For Audience.")
  @PostMapping(value = ApiConstant.AUDIENCE_CREATE, produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<AudienceDTO>> createAudience(
      @RequestBody(required = true) AudienceDTO audience) throws Exception {
    ApiResponseObject<AudienceDTO> response = audienceService.createAudience(audience);
    return ResponseEntity.ok().body(response);

  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE)
  @ApiOperation("Update Api For Audience.")
  @PostMapping(value = ApiConstant.AUDIENCE_UPDATE, produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<AudienceDTO>> updateAudience(
      @RequestBody(required = true) AudienceDTO audience,
      @PathVariable(name = ApiConstant.ID) Long id) throws Exception {
    if (audience != null && !audience.getId().equals(id))
      throw new ValidationException(ErrorCode.BAD_REQUEST);
    ApiResponseObject<AudienceDTO> response = audienceService.updateAudience(audience);
    return ResponseEntity.ok().body(response);

  }

  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.GETBYID)
  @ApiOperation("Api to syncRemoteAudience by Id")
  @GetMapping(path = ApiConstant.AUDIENCE_SYNC_REMOTE)
  public ResponseEntity<ApiResponseObject<BaseModel>> syncRemoteAudience(
      @PathVariable(ApiConstant.ID) Long audienceId) throws ValidationException, ApiException {
    ApiResponseObject<BaseModel> response = audienceService.syncRemoteAudience(audienceId);
    return ResponseEntity.ok().body(response);
  }

  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.GETBYID)
  @ApiOperation("Api to sync DMP Aduience")
  @PostMapping(value = ApiConstant.AUDIENCE_SYNC, produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> syncAudience(
      @RequestBody(required = true) PlatformAudienceDTO platformAudienceDTO) {
    Boolean result = audienceService.syncPlatformAudience(platformAudienceDTO);
    if (result)
      return ResponseEntity.ok().build();
    else
      return ResponseEntity.badRequest().build();
  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE)
  @ApiOperation("Create to checkConnection.")
  @PostMapping(value = ApiConstant.CHECK_CONNECTION, produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<BaseModel>> checkConnection(
      @RequestBody(required = true) PixelRemoteConfigDTO config) {

    ApiResponseObject<BaseModel> response = audienceServiceMock.checkConnection(config);
    return ResponseEntity.ok().body(response);

  }

  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.ACTIVATE)
  @ApiOperation("Api to deactivate Lists of Audience id")
  @PostMapping(value = ApiConstant.AUDIENCE_ACTIVATE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Integer, ResponseMessage>>> activateAudience(
      @RequestParam(name = ApiConstant.ID, required = true) String id) throws ApiException {
    ApiResponseObject<Map<Integer, ResponseMessage>> response = null;
    response = audienceService.activate(id);
    return ResponseEntity.ok().body(response);

  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE
      + GraphiteConstants.DEACTIVATE)
  @ApiOperation("Api to deactivate Lists of Audience id")
  @PostMapping(value = ApiConstant.AUDIENCE_DEACTIVATE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Integer, ResponseMessage>>> deactivateAudience(
      @RequestParam(name = ApiConstant.ID, required = true) String id) throws ApiException {
    ApiResponseObject<Map<Integer, ResponseMessage>> response = null;
    response = audienceService.deactivate(id);
    return ResponseEntity.ok().body(response);

  }

  /**
   * GET /audience/dmp?
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.GETDMP)
  @ApiOperation("Api to get Affle DMP Audience List")
  @GetMapping(path = ApiConstant.DMP_AUDIENCE)
  public ResponseEntity<ApiResponseObject<DmpAudienceDTO>> getAllDmpAudience(
      @RequestParam(name = ApiConstant.advertiserId, required = true) Long advertiser_id,
      @RequestParam(name = ApiConstant.START, required = false) Integer start,
      @RequestParam(name = ApiConstant.LIMIT, required = false) Integer limit,
      @RequestParam(name = ApiConstant.DMP_SEGMENT_TYPE, required = false) Integer stype)
      throws Exception {
    logger.info("Inside getDmp method. ");
    ApiResponseObject<DmpAudienceDTO> resp =
        dmpService.getDmpAudience(advertiser_id, start, limit, stype);
    return ResponseEntity.ok().body(resp);
  }


  /**
   * GET /audience/dmp?
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.GETDMP)
  @ApiOperation("Api to get Affle DMP Synced Audience List")
  @GetMapping(path = ApiConstant.DMP_SYNCED_AUDIENCE)
  public ResponseEntity<ApiResponseObject<List<AudienceDTO>>> getSyncedDmpAudience(
      @RequestParam(name = ApiConstant.advertiserId, required = true) Long advertiser_id)
      throws Exception {
    logger.info("Inside getSyncedDmpAudience method for advertiser : {} ", advertiser_id);
    ApiResponseObject<List<AudienceDTO>> resp = audienceService.getSyncedDmpAudience(advertiser_id);
    return ResponseEntity.ok().body(resp);
  }


  /**
   * GET /audience/{id}?
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.GETBYID)
  @ApiOperation("Api to get Audience")
  @GetMapping(path = ApiConstant.AUDIENCE_GET)
  public ResponseEntity<ApiResponseObject<AudienceDTO>> getById(
      @PathVariable(ApiConstant.ID) Long id,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh)
      throws Exception {
    logger.info("Inside getById method for id : {} ", id);
    ApiResponseObject<AudienceDTO> resp = audienceService.getAudience(id, refresh);
    return ResponseEntity.ok().body(resp);
  }


  /**
   * GET /audience?
   * 
   * @param Advertiser id query param
   * @param Page No query param
   * @param Page Size query param
   * @param Refresh query param
   * @param Sort query param
   * @param Search query param
   * 
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.GETALL)
  @ApiOperation("Api to get All Audience")
  @PostMapping(path = ApiConstant.AUDIENCE_GET_ALL, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ApiListResponse<List<AudienceESDTO>>>> getAllAudience(
      @RequestParam(name = ApiConstant.advertiserId, required = false) Long advertiserId,
      @RequestParam(name = ApiConstant.PAGENUMBER, defaultValue = "1",
          required = false) int pageNumber,
      @RequestParam(name = ApiConstant.PAGESIZE, defaultValue = "10",
          required = false) int pageSize,
      @RequestParam(name = ApiConstant.SORT, required = false) String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestBody(required = false) SearchRequest search) throws Exception {
    logger.info("Inside getAllAudience method. ");
    ApiListResponse<List<AudienceESDTO>> resp =
        audienceService.getAllAudience(advertiserId, pageNumber, pageSize, sort, search, refresh);
    ApiResponseObject<ApiListResponse<List<AudienceESDTO>>> respObject = new ApiResponseObject<>();
    respObject.setRespObject(resp);
    return ResponseEntity.ok().body(respObject);
  }


  /**
   * GET /audience/rules?
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE
      + GraphiteConstants.META_RULE)
  @ApiOperation("Api to get Audience Meta Rule")
  @GetMapping(path = ApiConstant.AUDIENCE_RULES)
  public ResponseEntity<ApiResponseObject<MetaRulesDto>> getMetaRules() throws Exception {
    logger.info("Inside getMetaRules method.");
    ApiResponseObject<MetaRulesDto> resp = ruleService.getAllRules();
    return ResponseEntity.ok().body(resp);
  }


  /**
   * GET /audience/access?
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDIENCE + GraphiteConstants.AUDIENCE)
  @ApiOperation("Api to get Audience Access")
  @GetMapping(path = ApiConstant.AUDIENCE_ACCESS)
  public ResponseEntity<ApiResponseObject<AudienceAccessDTO>> getAccess(
      @PathVariable(ApiConstant.ID) Long advertiserId) throws Exception {
    logger.info("Inside getAccess method for advertiserId : {}", advertiserId);
    ApiResponseObject<AudienceAccessDTO> resp = audienceService.getAcces(advertiserId);
    return ResponseEntity.ok().body(resp);
  }

}
