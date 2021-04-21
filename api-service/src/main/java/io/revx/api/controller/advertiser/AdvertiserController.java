/*
 * @author: ranjan-pritesh
 * 
 * @date: 27th Nov 2019
 */
package io.revx.api.controller.advertiser;


import java.io.IOException;
import java.util.Map;

import io.revx.core.model.requests.SearchRequest;
import io.revx.core.model.requests.SkadTargetPrivileges;

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
import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.advertiser.AdvertiserService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.pixel.Tag;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static io.revx.core.constant.GraphiteConstants.SKAD_PRIVILEGES;


@RestController
@Api(value = "Advertiser Controller", tags = {"Advertiser Controller"},
    description = "Rest API's for Advertisers creation,update etc.")
public class AdvertiserController {

  private static Logger logger = LogManager.getLogger(AdvertiserController.class);

  @Autowired
  AdvertiserService advertiserService;

  /**
   * POST /v2/api/advertisers
   * 
   * @param advertiserPojo Adv object in payload
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.CREATE)
  @ApiOperation(" Api to create Advertiser")
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.CREATE)
  @PostMapping(value = ApiConstant.ADVERTISERS, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<AdvertiserPojo>> createAdvertiser(
      @RequestBody(required = true) AdvertiserPojo advertiser) throws Exception {
    logger.info("POST Advertiser creation request : {} ", advertiser);
    ApiResponseObject<AdvertiserPojo> resp = advertiserService.create(advertiser);
    return ResponseEntity.ok().body(resp);
  }

  /**
   * PUT /v2/api/advertisers/{id}
   * 
   * @param advertiserPojo Adv object in payload
   * @param id url path parameter
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.UPDATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.UPDATE)
  @ApiOperation("update  Advertiser")
  @PostMapping(value = ApiConstant.ADVERTISERS+ ApiConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<AdvertiserPojo>> updateAdvertiser(
      @RequestBody(required = true) AdvertiserPojo advertiser,
      @PathVariable(ApiConstant.ID) Integer id) throws Exception {
    logger.info("PUT  Advertiser updation request : {} ", advertiser);
    if (advertiser != null && advertiser.getId() != null
        && !advertiser.getId().equals(id.longValue()))
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"AdvertiserId in query params. "});
    ApiResponseObject<AdvertiserPojo> resp = advertiserService.update(advertiser);
    return ResponseEntity.ok().body(resp);
  }


  /**
   * PUT /v2/api/advertisers/settings/{id}
   * 
   * @param advertiserPojo Adv object in payload
   * @param id url path parameter
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.UPDATESETTINGS)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.UPDATESETTINGS)
  @ApiOperation("update  Advertiser Settings")
  @PostMapping(value = ApiConstant.ADVERTISERS+ ApiConstant.SETTINGS + ApiConstant.ID_PATH,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<AdvertiserSettings>> updateSettings(
      @RequestBody(required = true) AdvertiserSettings settings,
      @PathVariable(ApiConstant.ID) Long id) throws Exception {
    logger.info(" Advertiser updation request : {} ", settings);

    ApiResponseObject<AdvertiserSettings> resp = advertiserService.update(settings, id);
    return ResponseEntity.ok().body(resp);
  }

  /**
   * GET /v2/api/advertisers/settings/{id}
   * 
   * @param advertiserPojo Adv object in payload
   * @param id url path parameter
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.UPDATESETTINGS)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.UPDATESETTINGS)
  @ApiOperation("get Advertiser Settings by Id")
  @GetMapping(value = ApiConstant.ADVERTISERS+ApiConstant.SETTINGS + ApiConstant.ID_PATH,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<AdvertiserSettings>> getAdvertiserSettings(
      @PathVariable(ApiConstant.ID) Long id) throws Exception {
    logger.info("Advertiser Id : {} ", id);

    ApiResponseObject<AdvertiserSettings> resp = advertiserService.getSettingsById(id);
    return ResponseEntity.ok().body(resp);
  }


  /**
   * GET /v2/api/advertisers/{id}
   * 
   * @param id advertiser id in payload
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.GETBYID)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.GETBYID)
  @ApiOperation("get Advertiser by Id")
  @GetMapping(value = ApiConstant.ADVERTISERS + ApiConstant.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<AdvertiserPojo>> getById(
      @PathVariable(ApiConstant.ID) Long id,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh) throws Exception {
    logger.info("Advertiser Id : {} ", id);
    ApiResponseObject<AdvertiserPojo> resp = advertiserService.getById(id,refresh);
    return ResponseEntity.ok().body(resp);
  }


  /**
   * POST /v2/api/advertisers/{id}/update_ast
   * 
   * @throws IOException
   * @throws ApiException
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.UPDATE_AST_ID)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.UPDATE_AST_ID)
  @ApiOperation("generate AST for given Advertiser id")
  @PostMapping(value = ApiConstant.ADVERTISERS+ ApiConstant.ID_PATH + ApiConstant.updateAST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Boolean>> updateAST(@PathVariable(ApiConstant.ID) Long id)
      throws ApiException, IOException {
    ApiResponseObject<Boolean> response = advertiserService.generateAstForAdvertiser(id);
    return ResponseEntity.ok().body(response);
  }

  /**
   * PUT /v2/advertisers/update_ast
   * 
   * @throws IOException
   * @throws ApiException
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.UPDATE_AST)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.UPDATE_AST)
  @ApiOperation("generate AST for all advertisers")
  @PostMapping(value = ApiConstant.ADVERTISERS+ ApiConstant.updateAST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Boolean>> updateAST() throws ApiException, IOException {
    ApiResponseObject<Boolean> response = advertiserService.generateAstForAllAdvertiser();
    return ResponseEntity.ok().body(response);
  }

  
  /**
   * GET /v2/api/advertisers/smarttag/{id}.
   *
   * @param id the id
   * @return the smart tag
   * @throws Exception the exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.GETSMARTTAG)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.GETSMARTTAG)
  @ApiOperation("get smart tag by advertiser Id")
  @GetMapping(value = ApiConstant.ADVERTISERS+ ApiConstant.smarttag + ApiConstant.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Tag>> getSmartTag(
      @PathVariable(ApiConstant.ID) Long id) throws Exception {
    logger.info("Advertiser Id : {} ", id);
    ApiResponseObject<Tag> resp = advertiserService.getSmartTag(id);
    return ResponseEntity.ok().body(resp);
  }
  

  /**
   * PUT /v2/api/advertisers/activate
   * 
   * @throws Exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.ACTIVATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.ACTIVATE)
  @ApiOperation("activate Lists of Advertisers id")
  @PostMapping(value = ApiConstant.ADVERTISERS+ ApiConstant.activate, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> activateAdvertiser(
      @RequestParam(required = true) String commaSepratedIds) throws Exception {
    ApiResponseObject<Map<Long, ResponseMessage>> response = null;
    response = advertiserService.activate(commaSepratedIds);
    return ResponseEntity.ok().body(response);

  }


  /**
   * PUT /v2/api/advertisers/Deactivate
   * 
   * @throws Exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER
      + GraphiteConstants.DEACTIVATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + GraphiteConstants.DEACTIVATE)
  @ApiOperation("deactivate Lists of Advertisers id")
  @PostMapping(value = ApiConstant.ADVERTISERS + ApiConstant.deactivate, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> deactivateAdvertiser(
      @RequestParam(required = true) String commaSepratedIds) throws Exception {
    ApiResponseObject<Map<Long, ResponseMessage>> response = null;
    response = advertiserService.deactivate(commaSepratedIds);
    return ResponseEntity.ok().body(response);

  }


  /**
   * POST /v2/api/advertisers/skadtargetprivilege
   *
   * @param searchRequest - Entity for which the skad privileges needs to be checked
   * @return - flag and list of created entities
   * @throws ValidationException - If the search request is not valid
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + SKAD_PRIVILEGES)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.ADVERTISER + SKAD_PRIVILEGES)
  @ApiOperation("Fetch privileges for creating SKAD Target")
  @PostMapping(value = ApiConstant.ADVERTISERS + ApiConstant.SKAD_TARGET_PRIVILEGE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<SkadTargetPrivileges>> getSkadTargetPrivileges(
           @RequestBody SearchRequest searchRequest) throws ValidationException {
    ApiResponseObject<SkadTargetPrivileges> privileges =
            advertiserService.getSkadTargetPrivileges(searchRequest);
    return ResponseEntity.ok(privileges);
  }
}
