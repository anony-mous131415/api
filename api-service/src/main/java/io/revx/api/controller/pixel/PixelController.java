/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.controller.pixel;

/**
 * @author priteshkumar
 *
 */
import java.util.Map;

import io.revx.core.response.ResponseMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.ValidationService;
import io.revx.api.service.pixel.impl.ConversionPixelService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.pixel.Tag;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * The Class PixelController.
 */
@RestController
@RequestMapping(ApiConstant.PIXELS)
@Api(value = "Pixel Controller", tags = {"Pixel Controller"},
    description = "Rest API's for Pixel creation,update etc.")
public class PixelController {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(PixelController.class);

  /** The pixel service. */
  @Autowired
  ConversionPixelService pixelService;

  /** The validator. */
  @Autowired
  ValidationService validator;

  /**
   * create pixel POST /v2/api/pixels.
   *
   * @param pixel pixel object in payload
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.CREATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.CREATE)
  @ApiOperation("create pixel")
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Pixel>> create(@RequestBody(required = true) Pixel pixel)
      throws Exception {
    logger.info(" pixel creation request : {} ", pixel);
    validator.validatePixelRequest(pixel, null, false);
    ApiResponseObject<Pixel> resp = pixelService.create(pixel);
    return ResponseEntity.ok().body(resp);
  }



  /**
   * update pixel PUT /v2/api/pixels/{id}.
   *
   * @param pixel = pixel object in payload
   * @param id = pixel id
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.UPDATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.UPDATE)
  @ApiOperation("update  Pixel")
  @PostMapping(path = ApiConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Pixel>> update(@RequestBody(required = true) Pixel pixel,
      @RequestParam(ApiConstant.ID) Integer id) throws Exception {
    logger.info(" Pixel updation request : {} ", pixel);
    validator.validatePixelRequest(pixel, id, true);
    ApiResponseObject<Pixel> resp = pixelService.update(pixel);
    return ResponseEntity.ok().body(resp);
  }



  /**
   * activate Lists of pixel id PUT /v2/api/advertisers/activate.
   *
   * @param commaSepratedIds the comma seprated ids
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.ACTIVATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.ACTIVATE)
  @ApiOperation("activate Lists of pixel id")
  @PostMapping(path = ApiConstant.activate, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> activate(
      @RequestParam(required = true) String commaSepratedIds) throws Exception {
    ApiResponseObject<Map<Long, ResponseMessage>> response = null;
    response = pixelService.activate(commaSepratedIds);
    return ResponseEntity.ok().body(response);

  }



  /**
   * deactivate Lists of pixel id PUT /v2/api/advertisers/deactivate.
   *
   * @param commaSepratedIds the comma seprated ids
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.DEACTIVATE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.DEACTIVATE)
  @ApiOperation("deactivate Lists of Pixel id")
  @PostMapping(path = ApiConstant.deactivate, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> deactivate(
      @RequestParam(required = true) String commaSepratedIds) throws Exception {
    ApiResponseObject<Map<Long, ResponseMessage>> response = null;
    response = pixelService.deactivate(commaSepratedIds);
    return ResponseEntity.ok().body(response);

  }



  /**
   * get pixels POST /v2/api/pixels/search.pixels
   *
   * @param search the search
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param refresh the refresh
   * @param sort - query parameter
   * @param advertiserId the advertiser id
   * @return the response entity
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.GETALL)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.GETALL)
  @ApiOperation("get/search pixels")
  @PostMapping(path = ApiConstant.search, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ApiListResponse<Pixel>>> searchPixels(
      @RequestBody(required = false) SearchRequest search,
      @RequestParam(value = ApiConstant.PAGENUMBER, defaultValue = "1") Integer pageNum,
      @RequestParam(value = ApiConstant.PAGESIZE, defaultValue = "10") Integer resultPerPage,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort,
      @RequestParam(ApiConstant.advertiserId) Long advertiserId) throws Exception {
    ApiListResponse<Pixel> response = null;
    validator.isValidAdvertiserId(advertiserId);
    response =
        pixelService.searchPixels(search, pageNum, resultPerPage, sort, refresh, advertiserId);
    ApiResponseObject<ApiListResponse<Pixel>> respObject = new ApiResponseObject<>();
    respObject.setRespObject(response);
    return ResponseEntity.ok().body(respObject);
  }



  /**
   * GET /v2/api/pixels/{id}.
   *
   * @param id the id
   * @return the by id
   * @throws Exception the exception
   */
  @LogMetrics(
      name = GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.GETBYID)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.GETBYID)
  @ApiOperation("get pixel by Id")
  @GetMapping(path = ApiConstant.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Pixel>> getById(@RequestParam(ApiConstant.ID) Long id)
      throws Exception {
    logger.info("Pixel Id : {} ", id);
    ApiResponseObject<Pixel> resp = pixelService.getbyId(id);
    return ResponseEntity.ok().body(resp);
  }



  /**
   * GET /v2/api/pixels/{id}/trackingcode.
   *
   * @param id the id
   * @return the tracking code
   * @throws Exception the exception
   */
  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL
      + GraphiteConstants.GETTRACKINGCODE)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.PIXEL + GraphiteConstants.GETTRACKINGCODE)
  @ApiOperation("get tracking code")
  @GetMapping(path = ApiConstant.ID_PATH + ApiConstant.trackingCode,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<Tag>> getTrackingCode(
      @RequestParam(ApiConstant.ID) Long id) throws Exception {
    logger.info("Pixel Id : {} ", id);
    ApiResponseObject<Tag> resp = pixelService.getTrackingCode(id);
    return ResponseEntity.ok().body(resp);
  }


}
