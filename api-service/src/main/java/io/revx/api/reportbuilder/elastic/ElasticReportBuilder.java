package io.revx.api.reportbuilder.elastic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import io.revx.api.reportbuilder.ReportBuilder;
import io.revx.core.exception.QueryBuilderException;
import io.revx.core.model.reporting.ReportingRequest;
import io.revx.core.model.reporting.ReportingResponse;

@Component
public class ElasticReportBuilder implements ReportBuilder {
	private static Logger logger = LogManager.getLogger(ElasticReportBuilder.class);

	@Override
	public ReportingResponse fetchReport(ReportingRequest reportingRequest) throws QueryBuilderException {
		return null;
	}

	@Override
	public ReportingResponse fetchReportCSV(ReportingRequest reportingRequest) throws QueryBuilderException {
		return null;
	}

}
