package io.revx.api.reportbuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.revx.api.reportbuilder.elastic.ElasticReportBuilder;
import io.revx.api.reportbuilder.redshift.ReportBuilderImpl;
import io.revx.core.enums.reporting.DBType;

@Component
public class ReportBuilderFactory {
	
	@Autowired
	ReportBuilderImpl reportBuilderImpl;
	
	@Autowired
	ElasticReportBuilder elasticReportBuilder;

	public ReportBuilder newInstance(DBType dbType) {

		switch (dbType) {
		case REDSHIFT:
			return reportBuilderImpl;
		case ELASTIC:
			return elasticReportBuilder;
		default:
			return new ReportBuilderImpl();
		}
	}

}
