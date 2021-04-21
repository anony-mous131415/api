package io.revx.api.controller.campaign;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.campaign.CampaignService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.campaign.CampaignDTO;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(ApiConstant.CAMPAIGNS)
@Api(value = "Campaign Controller", tags = {"Campaign Controller"},
description = "Rest API's for Campaign creation,update etc.")
public class CampaignController {

  private static Logger logger = LogManager.getLogger(CampaignController.class);

  @Autowired
  CampaignService campaignService;

  /**
   * POST /campaigns
   * 
   * @param CampaignDTO object needs to be pass in payload
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.CAMPAIGN + GraphiteConstants.CREATE)
  @ApiOperation("Api to create Campaign")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<CampaignDTO>> createCampaign(
      @RequestBody(required = true) CampaignDTO campaign) throws Exception {
    logger.info("Campaign creation request : {} ", campaign);
    ApiResponseObject<CampaignDTO> resp = campaignService.create(campaign);
    return ResponseEntity.ok().body(resp);
  }

  /**
   * POST /campaigns/{id}?
   * 
   * @param CampaignDTO object needs to be pass in payload 
   * @param campaign id needs to be pass in query path variable
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.CAMPAIGN + GraphiteConstants.CREATE)
  @ApiOperation("Api to update Campaign")
  @PostMapping(value = ApiConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<CampaignDTO>> updateCampaign(
      @RequestBody(required = true) CampaignDTO campaign, @PathVariable(ApiConstant.ID) Integer id)
      throws Exception {
    logger.info("campaign updation request : {} ", campaign);
    if (campaign.getId() != null && !campaign.getId().equals(id.longValue()))
      throw new ValidationException("campaign is missing");
    ApiResponseObject<CampaignDTO> resp = campaignService.update(campaign);
    return ResponseEntity.ok().body(resp);
  }
 
  /**
   * GET /campaigns/{id}?
   * 
   * @param campaign id needs to be pass in query param
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CAMPAIGN
      + GraphiteConstants.GETBYID)
  @ApiOperation("Api to get Campaign by Id")
  @GetMapping(path = ApiConstant.ID_PATH)
  public ResponseEntity<ApiResponseObject<CampaignDTO>> getCampaignById(
      @PathVariable(ApiConstant.ID) Long id,  @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh) throws Exception {
    logger.info("Inside getById method. Campaign Id : {} ", id);
    ApiResponseObject<CampaignDTO> resp = campaignService.getbyId(id, refresh);
    return ResponseEntity.ok().body(resp);
  }
 
  /**
   * GET /campaigns?
   * 
   * @param Advertiser id query param
   * @param Page No query param
   * @param Page Size query param
   * @param Refresh query param
   * @param Sort query param
   * @param Search  query param
   * 
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CAMPAIGN
      + GraphiteConstants.GETALL)
  @ApiOperation("Api to get All Campaign for given Adv Id")
  @GetMapping
  public ResponseEntity<ApiResponseObject<List<CampaignDTO>>> getAllCampaign(
      @RequestParam(name = ApiConstant.advertiserId) Long advertiserId,  @RequestParam(name = ApiConstant.PAGENUMBER, defaultValue = "1", required = false) int pageNumber,  
      @RequestParam(name = ApiConstant.PAGESIZE, defaultValue = "10", required = false) int pageSize,  @RequestParam(name = ApiConstant.SORT, required = false) String sort, 
      @RequestParam(name = ApiConstant.SEARCH, required = false) String search,  @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh) throws Exception {
    logger.info("Inside getAllCampaign method. Advertiser Id : {} ", advertiserId);
    ApiResponseObject<List<CampaignDTO>> resp = campaignService.getbyAdvertiserId(advertiserId, pageNumber, pageSize, sort, search, refresh);
    return ResponseEntity.ok().body(resp);
  }
 
  /**
   * POST /campaigns/activate?id=123,456
   * @param id  query parameter
   * @throws ApiException 
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CAMPAIGN
      + GraphiteConstants.ACTIVATE)
  @ApiOperation("Api to activate Lists of Campaigns id")
  @PostMapping(value = ApiConstant.activate, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Integer, ResponseMessage>>> activateCampaign(
      @RequestParam(name = ApiConstant.ID, required=true) String id) throws ApiException {
    ApiResponseObject<Map<Integer, ResponseMessage>> response = null;
    response = campaignService.activate(id);
    return ResponseEntity.ok().body(response);

  }

  /**
   * POST /campaigns/deactivate?id=123,456
   * @param id  query parameter
   * @throws ApiException 
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CAMPAIGN
      + GraphiteConstants.DEACTIVATE)
  @ApiOperation("Api to deactivate Lists of Campaigns id")
  @PostMapping(value = ApiConstant.deactivate, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Integer, ResponseMessage>>> deactivateCampaign(
      @RequestParam(name = ApiConstant.ID, required=true) String id) throws ApiException {
    ApiResponseObject<Map<Integer, ResponseMessage>> response = null;
    response = campaignService.deactivate(id);
    return ResponseEntity.ok().body(response);

  }
}
