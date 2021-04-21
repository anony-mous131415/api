package io.revx.api.controller.audit;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.annotation.Timed;
import io.revx.api.audit.AuditDetails;
import io.revx.api.audit.AuditMarker;
import io.revx.api.constants.ApiConstant;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.service.audit.AuditTrailService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.constant.RoleConstants;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Audit Controller", tags = {
		"Audit Controller" }, description = "Rest API's for getting AuditLog and AuditDetail.")
public class AuditController {

	@Autowired
	AuditTrailService auditTrailService;

	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDITLOG)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.AUDITLOG)
	@ApiOperation("get Audit Log for campaign and strategy")
	@GetMapping(value = ApiConstant.ACTIVITY + ApiConstant.LOG, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<List<AuditMarker>>> getAuditLog(@RequestParam("id") long id,
			@RequestParam("startTime") long startTime, @RequestParam("endTime") long endTime,
			@PathVariable("entity") DashBoardEntity entity) throws Exception {

		ApiResponseObject<List<AuditMarker>> resp = auditTrailService.getLog(id, startTime, endTime, entity);
		return ResponseEntity.ok().body(resp);
	}

	@LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.AUDITDETAIL)
	@Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.AUDITDETAIL)
	@ApiOperation("get Audit Detail for campaign and strategy")
	@GetMapping(value = ApiConstant.ACTIVITY + ApiConstant.DETAIL, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponseObject<List<AuditDetails>>> getAuditDetails(@RequestParam("id") long id,
			@RequestParam("startTime") long startTime, @RequestParam("endTime") long endTime,
			@PathVariable("entity") DashBoardEntity entity) throws Exception {

		ApiResponseObject<List<AuditDetails>> resp = auditTrailService.getAuditDetails(id, startTime, endTime, entity);
		return ResponseEntity.ok().body(resp);

	}
}
