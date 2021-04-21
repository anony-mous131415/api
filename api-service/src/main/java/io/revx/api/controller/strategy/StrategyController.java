package io.revx.api.controller.strategy;

import java.util.Map;

import io.revx.core.response.ResponseMessage;

import io.micrometer.core.annotation.Timed;
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
import io.revx.api.service.strategy.StrategyService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ApiException;
import io.revx.core.model.strategy.DuplicateStrategyRequestDTO;
import io.revx.core.model.strategy.StrategyCreativeAssociationRequestDTO;
import io.revx.core.model.strategy.StrategyCreativeAssociationResponseDTO;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.StrategyQuickEditDTO;
import io.revx.core.model.targetting.SiteListDTO;
import io.revx.core.model.targetting.SiteListRequest;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import static io.revx.core.constant.GraphiteConstants.SKAD_SETTINGS;

@RestController
@RequestMapping(ApiConstant.STRATEGIES)
@Api(value = "Strategy Controller", tags = {"Strategy Controller"},
    description = "Rest API's for Strategy creation,update etc.")
public class StrategyController {


  @Autowired
  StrategyService strategyService;



  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + GraphiteConstants.CREATE)
  @ApiOperation("Api to create Strategy")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<StrategyDTO>> createStrategy(
      @RequestBody(required = true) StrategyDTO strategy) throws Exception {
    ApiResponseObject<StrategyDTO> resp = strategyService.createStrategy(strategy);
    return ResponseEntity.ok().body(resp);

  }


  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + GraphiteConstants.UPDATE)
  @ApiOperation("Api to update Strategy")
  @PostMapping(path = ApiConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<StrategyDTO>> updateStrategy(
      @RequestBody(required = true) StrategyDTO strategy, @PathVariable(ApiConstant.ID) Long id)
      throws Exception {
    ApiResponseObject<StrategyDTO> resp = strategyService.updateStrategy(strategy);
    resp.getRespObject().setId(id);
    return ResponseEntity.ok().body(resp);

  }

  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + GraphiteConstants.ACTIVATE)
  @ApiOperation("Api to activate Lists of Strategy id")
  @PostMapping(path = ApiConstant.activate, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> activateStrategy(
      @RequestParam(name = ApiConstant.ID, required = true) String ids) throws Exception {
    ApiResponseObject<Map<Long, ResponseMessage>> resp = strategyService.activate(ids);
    return ResponseEntity.ok().body(resp);

  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY
      + GraphiteConstants.DEACTIVATE)
  @ApiOperation("Api to deactivate Lists of Strategy ids")
  @PostMapping(path = ApiConstant.deactivate, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> deactivateStrategy(
      @RequestParam(name = ApiConstant.ID, required = true) String ids) throws Exception {
    ApiResponseObject<Map<Long, ResponseMessage>> resp = strategyService.deactivate(ids);
    return ResponseEntity.ok().body(resp);

  }


  @ApiOperation("Api to Create duplicate Strategy")
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY
      + GraphiteConstants.DUPLICATE)
  @PostMapping(path = ApiConstant.DUPLICATE_STRATEGY, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<StrategyDTO>> duplicateStrategy(
      @PathVariable(ApiConstant.ID) Long strategyId, @RequestBody(required = true) DuplicateStrategyRequestDTO dto)
      throws Exception {
    ApiResponseObject<StrategyDTO> resp = strategyService.duplicateStrategy(strategyId, dto);
    return ResponseEntity.ok().body(resp);
  }


  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY
      + GraphiteConstants.VALDATE_SITES)
  @ApiOperation("Api to Validating Sites")
  @PostMapping(path = ApiConstant.VALIDATE_DOMAINS, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<SiteListDTO>> validateSites(
      @RequestBody(required = true) SiteListRequest sites) throws ApiException {
    ApiResponseObject<SiteListDTO> resp = strategyService.validateSiteList(sites.sites);
    return ResponseEntity.ok().body(resp);
  }

  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + GraphiteConstants.GETBYID)
  @ApiOperation("Api to get Startegy by Id")
  @GetMapping(path = ApiConstant.ID_PATH)
  public ResponseEntity<ApiResponseObject<StrategyDTO>> getStrategyById(
      @PathVariable(ApiConstant.ID) Long id,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh)
      throws Exception {
    ApiResponseObject<StrategyDTO> resp = new ApiResponseObject<>();
    resp = strategyService.get(id, refresh);
    return ResponseEntity.ok().body(resp);
  }


  /*
   * @LogMetrics( name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY +
   * GraphiteConstants.TILL_NOW)
   * 
   * @ApiOperation("Api to get Startegy by getStrategyTargetingTillNow")
   * 
   * @GetMapping(path = ApiConstant.STRATEGY_TARGETTING_TILL_NOW) public
   * ResponseEntity<ApiResponseObject<Map<String, Object>>> getStrategyTargetingDataTillNow(
   * 
   * @PathVariable(ApiConstant.ID) Long strategyId) throws ApiException {
   * ApiResponseObject<Map<String, Object>> resp =
   * strategyService.getStrategyTargetingTillNow(strategyId); return ResponseEntity.ok().body(resp);
   * }
   * 
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY
      + GraphiteConstants.ASSOCIATE)
  @ApiOperation("Api to associate strategy to creative ")
  @PostMapping(path = ApiConstant.ASSOCIATE_CREATIVE_STRATEGY,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<StrategyCreativeAssociationResponseDTO>> associateCreativesWithStrategies(
      @RequestBody(required = true) StrategyCreativeAssociationRequestDTO request)
      throws ApiException {
    ApiResponseObject<StrategyCreativeAssociationResponseDTO> resp =
        strategyService.associateCreativesToStrategies(request);
    return ResponseEntity.ok().body(resp);
  }
  
  
  
  //Quick-edit-get
  
  @LogMetrics(
		  name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + GraphiteConstants.GET_QUICK_EDIT_BY_ID)
  @ApiOperation("Api to get Startegy Quick Edit Details by Id")
  @GetMapping(path = ApiConstant.QUICK_EDIT)
  public ResponseEntity<ApiResponseObject<StrategyQuickEditDTO>> getStrategyQuickEditDetails(
		  @PathVariable(ApiConstant.ID) Long id)
				  throws Exception {
	  ApiResponseObject<StrategyQuickEditDTO> resp = new ApiResponseObject<>();
	  resp = strategyService.getStrategyQuickEditDetails(id);
	  return ResponseEntity.ok().body(resp);
  }


  //Quick-edit-update
  @LogMetrics(
        name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + GraphiteConstants.UPDATE_QUICK_EDIT_BY_ID)
  @ApiOperation("Api to update Startegy Quick Edit Details by Id")
  @PostMapping(path = ApiConstant.QUICK_EDIT)
  public ResponseEntity<ApiResponseObject<StrategyDTO>> saveStrategyQuickEditDetails(
        @PathVariable(ApiConstant.ID) Long id , @RequestBody(required = true) StrategyQuickEditDTO strategyQuickEditDTO)
                throws Exception {

          ApiResponseObject<StrategyDTO> resp = new ApiResponseObject<>();
          resp = strategyService.updateQuickEditDetails(strategyQuickEditDTO);
          return ResponseEntity.ok().body(resp);
  }

  /**
   * v2/api/strategies/skadsettings
   *
   * To retrieve skad settings when creating a skad strategy, The settings are fetched from a file
   * and are utilized in the UI when creating skad strategy on which properties to show/hide wile
   * targeting
   */
  @LogMetrics( name = GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + SKAD_SETTINGS)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.STRATEGY + SKAD_SETTINGS)
  @ApiOperation("API to fetch settings for a SKAD Strategy")
  @GetMapping(path = ApiConstant.SKAD_SETTINGS, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<String>> getSkadSettings() {
    ApiResponseObject<String> responseObject = strategyService.getSkadSettings();
    return ResponseEntity.ok(responseObject);
  }

}
