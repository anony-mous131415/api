/*
 * @author: ranjan-pritesh
 * 
 * @date:31th dec 2019
 */
package io.revx.api.controller.clickdestination;

import javax.validation.Valid;
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
import io.revx.api.service.clickdestination.ClickDestinationService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.ClickDestinationAutomationUrls;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@RestController
@RequestMapping(ApiConstant.clickDestinations)
@Api(value = "Click Destination Controller", tags = {"Click Destination Controller"})
@SwaggerDefinition(tags = {@Tag(name = "Click Destination Controller",
    description = "Rest API's for click destination creation,update etc.")})
public class ClickDestinationController {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(ClickDestinationController.class);

  /** The click destination service. */
  @Autowired
  ClickDestinationService clickDestinationService;



  /**
   * POST /clickdestinations?authtoken=<authtoken>.
   *
   * @param clickDestination the click destination
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.CREATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.CREATE)
  @ApiOperation(" Api to create click Destination")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ClickDestination>> createClickDestination(
      @RequestBody(required = true) @Valid ClickDestination clickDestination) throws Exception {
    logger.info(" ClickDestination creation request : {} ", clickDestination);
    ApiResponseObject<ClickDestination> resp = clickDestinationService.create(clickDestination);
    return ResponseEntity.ok().body(resp);
  }



  /**
   * PUT /clickdestinations/{id}?authtoken=<authtoken>.
   *
   * @param clickDestination the click destination
   * @param id the id
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.UPDATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.UPDATE)
  @ApiOperation(" Api to update  Click Destination")
  @PostMapping(path = ApiConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ClickDestination>> updateClickDestination(
      @RequestBody(required = true) @Valid ClickDestination clickDestination,
      @RequestParam(ApiConstant.ID) Long id) throws Exception {
    logger.info(" Click Destination updation request : {} ", clickDestination);
    if (clickDestination.getId() != null && !clickDestination.getId().equals(id))
      throw new ValidationException("click destination id is not valid");
    ApiResponseObject<ClickDestination> resp = clickDestinationService.update(clickDestination);
    return ResponseEntity.ok().body(resp);
  }



  /**
   * GET /clickdestinations/{id}?authtoken=<authtoken>.
   *
   * @param id the id
   * @return the by id
   * @throws Exception the exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.GETBYID)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.GETBYID)
  @ApiOperation(" Api to get click destination by Id")
  @GetMapping(path = ApiConstant.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ClickDestination>> getClickDestinationById(
      @RequestParam(ApiConstant.ID) Long id) throws Exception {
    logger.info("Click destination Id : {} ", id);
    ApiResponseObject<ClickDestination> resp = clickDestinationService.getById(id);
    return ResponseEntity.ok().body(resp);
  }



  /**
   * GET /clickdestinations/search?authtoken=<authtoken>&pagenum=<pagenum>&rpp=<pagesize>&
   * sort=<column>&order=<asc|desc>&filters=<filters>.
   *
   * @param search the search
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param sort - query parameter
   * @return the all
   * @throws Exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.GETALL)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CLICKDESTINATION
      + GraphiteConstants.GETALL)
  @ApiOperation(" Api to get all click destination")
  @PostMapping(path = ApiConstant.search, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ApiListResponse<ClickDestination>>> getAllClickDestination(
      @RequestBody(required = false) SearchRequest search,
      @RequestParam(value = ApiConstant.PAGENUMBER, defaultValue = "1") Integer pageNum,
      @RequestParam(value = ApiConstant.PAGESIZE, defaultValue = "10") Integer resultPerPage,
      @RequestParam(value = ApiConstant.SORT, defaultValue = "id-") String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestParam(ApiConstant.advertiserId) Long advertiserId)
      throws Exception {
    ApiListResponse<ClickDestination> response =
        clickDestinationService.getAll(search, pageNum, resultPerPage, sort, refresh,advertiserId);
    ApiResponseObject<ApiListResponse<ClickDestination>> responseObject = new ApiResponseObject<>();
    responseObject.setRespObject(response);
    return ResponseEntity.ok().body(responseObject);
  }

  @GetMapping(path = ApiConstant.mmpParameters+ ApiConstant.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ClickDestinationAutomationUrls>> getMmpParameters(@PathVariable(ApiConstant.ID) Long id) throws Exception{
      ApiResponseObject<ClickDestinationAutomationUrls> responseObject = clickDestinationService.getMmpParameters(id);
      return ResponseEntity.ok().body(responseObject);
  }

}
