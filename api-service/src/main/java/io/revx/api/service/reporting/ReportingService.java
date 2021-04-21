package io.revx.api.service.reporting;

import io.revx.api.reportbuilder.ReportBuilder;
import io.revx.api.reportbuilder.ReportBuilderFactory;
import io.revx.api.reportbuilder.ReportBuilderUtil;
import io.revx.core.model.reporting.ReportProperty;
import io.revx.core.model.reporting.ReportingRequest;
import io.revx.core.model.reporting.ReportingResponse;
import io.revx.core.response.ApiResponseObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("reportingService")
public class ReportingService {

	private static Logger logger = LogManager.getLogger(ReportingService.class);

	@Autowired
	private ReportBuilderFactory reportBuilderFactory;

	@Autowired
	private ReportingRequestValidator reportingRequestValidator;

	@Autowired
	private ReportBuilderUtil reportBuilderUtil;

	public ApiResponseObject<ReportingResponse> fetchReport(ReportingRequest reportingRequest) throws Exception {
		logger.info("fetchReport for entity : {}", reportingRequest.getEntityName());

		ReportingResponse reportingResponse = null;
		ApiResponseObject<ReportingResponse> response = new ApiResponseObject<ReportingResponse>();
		try {
			Map<String, ReportProperty> properties = reportBuilderUtil
					.readReportConfigProperties(reportingRequest.getDbType(), reportingRequest.getEntityName());
			reportingRequest.setProperties(properties);

			// validate request
			boolean isValid = reportingRequestValidator.validateRequest(reportingRequest, false);

			// if request params are valid, then proceed to report builder
			if (isValid) {
				ReportBuilder builder = reportBuilderFactory.newInstance(reportingRequest.getDbType());
				reportingResponse = builder.fetchReport(reportingRequest);

				response.setError(null);
				response.setRespObject(reportingResponse);
				response.setRespId(reportingResponse.getReport_id());
			}
		} catch (Exception e) {
			throw e;
		}

		logger.info("[ReportID: {}] Response : {}", reportingResponse.getReport_id(), reportingRequest.getEntityName(),
				reportingResponse);
		return response;
	}

	public ApiResponseObject<ReportingResponse> fetchReportCSV(ReportingRequest reportingRequest) throws Exception {
		logger.info("fetchReportCSV for entity : {}", reportingRequest.getEntityName());

		ReportingResponse reportingResponse = null;
		ApiResponseObject<ReportingResponse> response = new ApiResponseObject<ReportingResponse>();
		try {
			Map<String, ReportProperty> properties = reportBuilderUtil
					.readReportConfigProperties(reportingRequest.getDbType(), reportingRequest.getEntityName());
			reportingRequest.setProperties(properties);

			// validate request
			boolean isValid = reportingRequestValidator.validateRequest(reportingRequest, true);

			// if request params are valid, then proceed to report builder
			if (isValid) {
				ReportBuilder builder = reportBuilderFactory.newInstance(reportingRequest.getDbType());
				reportingResponse = builder.fetchReportCSV(reportingRequest);

				response.setError(null);
				response.setRespObject(reportingResponse);
			}
		} catch (Exception e) {
			throw e;
		}

		logger.info("fetchReportCSV for entity : {} | response : {}", reportingRequest.getEntityName(),
				reportingResponse);
		return response;
	}
}
