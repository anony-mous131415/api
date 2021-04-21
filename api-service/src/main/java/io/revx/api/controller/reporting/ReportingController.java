/*
 * @author: Ashwin Venkat
 * 
 * @date: April 2020
 */
package io.revx.api.controller.reporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.reporting.ReportingService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.reporting.ReportingRequest;
import io.revx.core.model.reporting.ReportingResponse;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;

@RestController
@RequestMapping(ApiConstant.REPORTING_REQUEST_BASE)
@Api(value = "Reporting Controller", tags = { "Reporting Controller" })
@SwaggerDefinition(tags = {
		@Tag(name = "Reporting Controller", description = "Rest API's for Advance and Conversion reports.") })
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReportingController {

	private static Logger logger = LogManager.getLogger(ReportingController.class);
	
	@Autowired
	ReportingService reportingService;
	
	
	/**
	 * API to fetch report data based on entity.
	 * @param reportingRequest
	 * @param reportingEntity
	 * @param options
	 * @return
	 * @throws Exception 
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING)
	@ApiOperation("API to fetch report data based on entity.")
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING)
	@PostMapping(value = ApiConstant.REPORTING_REQUEST_ENTITY, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<ReportingResponse>> customReport(
			@RequestBody(required = true) ReportingRequest reportingRequest,
			@PathVariable(ApiConstant.REPORTING_ENTITY) String reportingEntity,
			@RequestParam(name = ApiConstant.REPORTING_OPTIONS, defaultValue = "") String options) throws Exception {
		logger.info("Custom Report Request : {} | entity : {} | options : {}", reportingRequest, reportingEntity,
				options);
		reportingRequest.setEntityName(reportingEntity);
		reportingRequest.setOptions(options);
		reportingRequest.setDefaultCurrencyOf();
		
		ApiResponseObject<ReportingResponse> response = reportingService.fetchReport(reportingRequest);
		
		return ResponseEntity.ok().body(response);
	}
	
	/**
	 * API to fetch report data based on entity.
	 * @param reportingRequest
	 * @param reportingEntity
	 * @param options
	 * @return
	 * @throws Exception 
	 */
	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING_EXPORT)
	@ApiOperation("API to export report data as csv based on entity.")
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING_EXPORT)
	@PostMapping(value = ApiConstant.REPORTING_REQUEST_ENTITY_EXPORT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<ReportingResponse>> customReportCSV(
			@RequestBody(required = true) ReportingRequest reportingRequest,
			@PathVariable(ApiConstant.REPORTING_ENTITY) String reportingEntity,
			@RequestParam(name = ApiConstant.REPORTING_OPTIONS, defaultValue = "") String options) throws Exception {
		logger.info("Custom Report Request : {} | entity : {} | options : {}", reportingRequest, reportingEntity,
				options);
		reportingRequest.setEntityName(reportingEntity);
		reportingRequest.setOptions(options);
		reportingRequest.setDefaultCurrencyOf();
		
		ApiResponseObject<ReportingResponse> response = reportingService.fetchReportCSV(reportingRequest);
		
		return ResponseEntity.ok().body(response);
	}
}
