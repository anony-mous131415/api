/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.controller.catalog;


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
import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.catalog.CatalogService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * The Class CatalogController.
 */
@RestController
@RequestMapping(ApiConstant.catalog)
@Api(value = "Catalog Controller", tags = {"Catalog Controller"},
    description = "Rest API's for catalog  creation,update etc.")
public class CatalogController {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(CatalogController.class);

  /** The catalog service. */
  @Autowired
  CatalogService catalogService;

  /**
   * GET /v2/api/catalog/macros.
   *
   * @param advertiserId the advertiser id
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param sort the sort
   * @param refresh the refresh
   * @param search the search
   * @return the macros
   * @throws Exception the exceptioncx
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG + GraphiteConstants.GETMACROS)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG + GraphiteConstants.GETMACROS)
  @ApiOperation("get List of catalog macros for given advertiserId")
  @PostMapping(path = ApiConstant.macros, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiListResponse<Macro>> getMacros(
      @RequestParam(value = ApiConstant.advertiserId) Long advertiserId,
      @RequestParam(value = ApiConstant.PAGENUMBER, defaultValue = "1") Integer pageNum,
      @RequestParam(value = ApiConstant.PAGESIZE, defaultValue = "10") Integer resultPerPage,
      @RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestBody(required = false) SearchRequest search) throws Exception {
    ApiListResponse<Macro> response = null;
    response =
        catalogService.getMacros(advertiserId, pageNum, search, resultPerPage, sort, refresh);
    return ResponseEntity.ok().body(response);
  }


  /**
   * POST /v2/api/catalog/feeds.
   *
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param advertiserId the advertiser id
   * @param sort the sort
   * @param refresh the refresh
   * @param search the search
   * @return the catalog feeds
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG + GraphiteConstants.GETFEEDS)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG + GraphiteConstants.GETFEEDS)
  @ApiOperation("get List of catalog Feeds for given advertiserId")
  @PostMapping(path = ApiConstant.feeds, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ApiListResponse<CatalogFeed>>> getCatalogFeeds(
      @RequestParam(value = ApiConstant.PAGENUMBER, defaultValue = "1") Integer pageNum,
      @RequestParam(value = ApiConstant.PAGESIZE, defaultValue = "10") Integer resultPerPage,
      @RequestParam(value = ApiConstant.advertiserId, required = true) Long advertiserId,
      @RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestBody(required = false) SearchRequest search) throws Exception {
    ApiListResponse<CatalogFeed> response = null;
    response = catalogService.getFeeds(advertiserId, pageNum, search, resultPerPage, sort, refresh);
    ApiResponseObject<ApiListResponse<CatalogFeed>> responseObject = new ApiResponseObject<>();
    responseObject.setRespObject(response);
    return ResponseEntity.ok().body(responseObject);
  }


  /**
   * POST /v2/api/catalog/variables.
   *
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param sort the sort
   * @param refresh the refresh
   * @param search the search
   * @param feedId the feed id
   * @return the variable mappings
   * @throws Exception the exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG
      + GraphiteConstants.GETVARIABLEMAPPINGS)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG
      + GraphiteConstants.GETVARIABLEMAPPINGS)
  @ApiOperation("get List of catalog Variable Mappings for given feedId")
  @PostMapping(path = ApiConstant.variableMappings, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ApiListResponse<VariablesMappingDTO>>> getVariableMappings(
      @RequestParam(value = ApiConstant.PAGENUMBER, defaultValue = "1") Integer pageNum,
      @RequestParam(value = ApiConstant.PAGESIZE, defaultValue = "10") Integer resultPerPage,
      @RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestBody(required = false) SearchRequest search,
      @RequestParam(required = true) Long feedId) throws Exception {
    ApiListResponse<VariablesMappingDTO> response = null;
    response =
        catalogService.getVariableMappings(feedId, pageNum, resultPerPage, search, sort, refresh);
    ApiResponseObject<ApiListResponse<VariablesMappingDTO>> responseObject = new ApiResponseObject<>();
    responseObject.setRespObject(response);
    return ResponseEntity.ok().body(responseObject);
  }


  /**
   * GET /v2/api/catalog/feeds/{id}.
   *
   * @param id the id
   * @return the by id
   * @throws Exception the exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG
      + GraphiteConstants.GETFEEDSBYID)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CATALOG + GraphiteConstants.GETFEEDSBYID)
  @ApiOperation("get feed by feed Id")
  @GetMapping(path = ApiConstant.feedsById, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<CatalogFeed>> getById(
      @PathVariable(ApiConstant.ID) Long id) throws Exception {
    logger.info("Feed Id : {} ", id);
    ApiResponseObject<CatalogFeed> resp = catalogService.getbyId(id);
    return ResponseEntity.ok().body(resp);
  }

}

