/*
 * @author: ranjan-pritesh
 * 
 * @date: 16th Dec 2019
 */
package io.revx.api.controller.creative;


import java.util.List;
import java.util.Map;

import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeHtmlMockupDTO;

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
import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.creative.CreativeService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.CreativeCompactDTO;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeMockUpsDTO;
import io.revx.core.model.creative.CreativePerformanceData;
import io.revx.core.model.creative.CreativeThirdPartyAdTag;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@RestController
@RequestMapping(ApiConstant.creatives)
@Api(value = "Creative Controller", tags = {"Creative Controller"})
@SwaggerDefinition(tags = {@Tag(name = "Creative Controller",
description = "Rest API's for Creative CRUD operation.. ")})
public class CreativeController {

	private static Logger logger = LogManager.getLogger(CreativeController.class);

	@Autowired
	CreativeService creativeService;


	/**
	 * Creates the.
	 *
	 * @param mockupDTO the mockup DTO
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(
			name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.MOCKUPS)
	@ApiOperation(" Api to  create creatives mockups")
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.MOCKUPS)
	@PostMapping(value = ApiConstant.MOCKUPS, consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiListResponse<CreativeDTO>> createMockups(
			@RequestBody(required = true) CreativeMockUpsDTO mockupDTO) throws Exception {
		logger.info("POST creative mokups  request : {} ", mockupDTO);
		ApiListResponse<CreativeDTO> response = creativeService.mockups(mockupDTO);
		return ResponseEntity.ok().body(response);
	}



	/**
	 * Creates the.
	 *
	 * @param creativeDTO the creative DTO
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(
			name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.CREATE)
	@ApiOperation(" Api to batch create creatives")
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.CREATE)
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiListResponse<CreativeDTO>> createCreative(
			@RequestBody(required = true) List<CreativeDTO> creativeDTOs) throws Exception {
		logger.info("POST creative creation request : {} ", creativeDTOs);
		ApiListResponse<CreativeDTO> resp = creativeService.create(creativeDTOs);
		return ResponseEntity.ok().body(resp);
	}



	/**
	 * PUT /v2/api/creative/{id}.
	 *
	 * @param creative the creative
	 * @param id url path parameter
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(
			name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.UPDATE)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.UPDATE)
	@ApiOperation("update  Creative")
	@PostMapping(value = ApiConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<CreativeDTO>> updateCreative(
			@RequestBody(required = true) CreativeDTO creative, @PathVariable(ApiConstant.ID) Integer id)
					throws Exception {
		if (creative != null && creative.getId() != null && !creative.getId().equals(id.longValue()))
			throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
					new Object[] {"creative id"});
		ApiResponseObject<CreativeDTO> resp = creativeService.update(creative);
		return ResponseEntity.ok().body(resp);
	}



	/**
	 * Activate creative.
	 *
	 * @param commaSepratedIds the comma seprated ids
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.ACTIVATE)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.ACTIVATE)
	@ApiOperation("activate Lists of creative id")
	@PostMapping(value = ApiConstant.activate, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> activateCreative(
			@RequestParam(required = true) String commaSepratedIds) throws Exception {
		ApiResponseObject<Map<Long, ResponseMessage>> response =
				creativeService.activate(commaSepratedIds);
		return ResponseEntity.ok().body(response);
	}



	/**
	 * PUT /v2/api/creatives/deactivate.
	 *
	 * @param commaSepratedIds the comma seprated ids
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.DEACTIVATE)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.DEACTIVATE)
	@ApiOperation("deactivate Lists of creative id")
	@PostMapping(value = ApiConstant.deactivate, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<Map<Long, ResponseMessage>>> deactivateCreative(
			@RequestParam(required = true) String commaSepratedIds) throws Exception {
		ApiResponseObject<Map<Long, ResponseMessage>> response =
				creativeService.deactivate(commaSepratedIds);
		return ResponseEntity.ok().body(response);
	}


	/**
	 * Gets the by id.
	 *
	 * @param id the id
	 * @return the by id
	 * @throws Exception the exception
	 */
	@LogMetrics(
			name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.GETBYID)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.GETBYID)
	@ApiOperation("get Creative by Id")
	@GetMapping(path = ApiConstant.ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<CreativeDTO>> getCreativeById(
			@RequestParam(ApiConstant.ID) Long id) throws Exception {
		logger.info("Creative Id : {} ", id);

		ApiResponseObject<CreativeDTO> resp = creativeService.getbyId(id);
		return ResponseEntity.ok().body(resp);
	}


	/**
	 * Search creatives.
	 *
	 * @param search the search
	 * @param pageNum the page num
	 * @param resultPerPage the result per page
	 * @param refresh the refresh
	 * @param sort the sort
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(
			name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.GETALL)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES + GraphiteConstants.GETALL)
	@ApiOperation("get/search creatives")
	@PostMapping(path = ApiConstant.search, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<ApiListResponse<CreativeDTO>>> searchCreatives(
			@RequestBody(required = true) DashboardRequest search,                     
			@RequestParam(value = ApiConstant.PAGENUMBER, defaultValue = "1") Integer pageNum,
			@RequestParam(value = ApiConstant.PAGESIZE, defaultValue = "10") Integer resultPerPage,
			@RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
			@RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort) throws Exception {
		ApiListResponse<CreativeDTO> response =
				creativeService.searchCreatives(search, pageNum, resultPerPage, sort, refresh);
		ApiResponseObject<ApiListResponse<CreativeDTO>> responseObject = new ApiResponseObject<>();
		responseObject.setRespObject(response);
		return ResponseEntity.ok().body(responseObject);
	}


	/**
	 * Search creatives compact.
	 *
	 * @param search the search
	 * @param pageNum the page num
	 * @param resultPerPage the result per page
	 * @param refresh the refresh
	 * @param sort the sort
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.GETALLCOMPACT)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.GETALLCOMPACT)
	@ApiOperation("get/search creatives compacts")
	@PostMapping(path = ApiConstant.search + ApiConstant.COMPACT,
	consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<ApiListResponse<CreativeCompactDTO>>> searchCreativesCompact(
			@RequestBody SearchRequest search,
			@RequestParam(value = ApiConstant.PAGENUMBER, defaultValue = "1") Integer pageNum,
			@RequestParam(value = ApiConstant.PAGESIZE, defaultValue = "10") Integer resultPerPage,
			@RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
			@RequestParam(name = ApiConstant.SKAD_TARGET , required = false, defaultValue = "false") boolean isSkad,
			@RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort) throws Exception {
		ApiListResponse<CreativeCompactDTO> response =
				creativeService.searchCompactCreatives(search, pageNum, resultPerPage, sort, refresh, isSkad);
		ApiResponseObject<ApiListResponse<CreativeCompactDTO>> responseObject = new ApiResponseObject<>();
		responseObject.setRespObject(response);
		return ResponseEntity.ok().body(responseObject);
	}



	/**
	 * Gets the performance for creative by id.
	 *
	 * @param id the id
	 * @return the performance for creative by id
	 * @throws Exception the exception
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.GET_PERF_BYID)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.GET_PERF_BYID)
	@ApiOperation("get Performance Data by Creative by Id")
	@PostMapping(path = ApiConstant.PERFORMANCE + ApiConstant.ID_PATH,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<CreativePerformanceData>> getPerformanceForCreativeById(
			@PathVariable(ApiConstant.ID) Long id,
			@RequestBody(required = true) DashboardRequest search) throws Exception {
		logger.info("Creative Id : {} ", id);
		ApiResponseObject<CreativePerformanceData> resp = creativeService.getPerformanceByIdAndTimeStamp(id , search);
		return ResponseEntity.ok().body(resp);
	}



	/**
	 * Creates the.
	 *
	 * @param creativeDTOs the creative DT os
	 * @param type the type
	 * @return the response entity
	 * @throws Exception the exception
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.THIRD_PARTY_TAG)
	@ApiOperation(" Api to  get Third party adtag creatives")
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.THIRD_PARTY_TAG)
	@PostMapping(value = ApiConstant.THIRDPARTYTAG, consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<CreativeDTO>> getAdTagCreative(
			@RequestBody(required = true) CreativeThirdPartyAdTag adTag) throws Exception {
		logger.info("POST creative mokups  request : {} ", adTag);
		ApiResponseObject<CreativeDTO> response = creativeService.getAdTagCreative(adTag);
		return ResponseEntity.ok().body(response);
	}

	/**
	 * POST /v2/api/creatives/htmlmockups
	 *
	 * @param mockupDTO the mockup DTO with basic details and template details
	 * @return List of Creative files created out of HTML content
	 * @throws Exception the exception
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.TEMPLATES + GraphiteConstants.MOCKUPS)
	@ApiOperation(" Api to  create creatives mockups")
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
			+ GraphiteConstants.TEMPLATES + GraphiteConstants.MOCKUPS)
	@PostMapping(value = ApiConstant.HTML_MOCKUPS, consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiListResponse<CreativeDTO>> createHtmlMockups(
			@RequestBody CreativeHtmlMockupDTO mockupDTO) throws Exception {
		logger.info("POST creative mockups  request : {} ", mockupDTO);
		ApiListResponse<CreativeDTO> response = creativeService.htmlMockups(mockupDTO);
		return ResponseEntity.ok().body(response);
	}
}
