package com.komli.prime.service.reporting.querybuilder.impl.vertica.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.utils.ReportingUtil;

public class VerticaTempQueriesBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(VerticaTempQueriesBuilder.class);

	private VerticaQueryStrings verticaQueryStrings;
	private VerticaQueryInputParameters inputParams;

	public VerticaTempQueriesBuilder(VerticaQueryStrings queryStrings,
			VerticaQueryInputParameters inputParams) {
		this.verticaQueryStrings = queryStrings;
		this.inputParams = inputParams;
	}

	public String getTempTableSizeQuery(String reportId) throws QueryBuilderException {
		String tempTable = ReportingUtil.getUniqueTempTable(reportId);
		StringBuilder finalQuery = new StringBuilder("");
		finalQuery.append("select count(1) from ").append(tempTable)
				.append(" ")
				.append(verticaQueryStrings.getFilterTempQueryStr());
		return finalQuery.toString();
	}

	public String getTempTableFetchQuery(String reportId) throws QueryBuilderException {
		String tempTable = ReportingUtil.getUniqueTempTable(reportId);
		StringBuilder finalQuery = new StringBuilder("");
		finalQuery.append("select ")
				.append(verticaQueryStrings.getSelectTempQueryStr())
				.append(" from ").append(tempTable).append(" ")
				.append(verticaQueryStrings.getFilterTempQueryStr())
				.append(" ").append(verticaQueryStrings.getOrderbyQueryStr())
				.append(" ").append(verticaQueryStrings.getLimitOffsetStr());
		return finalQuery.toString();
	}

	public String getTempTableCSVQuery(String reportId) throws QueryBuilderException {
		String tempTable = ReportingUtil.getUniqueTempTable(reportId);
		StringBuilder finalQuery = new StringBuilder("");
		finalQuery.append("select ")
				.append(verticaQueryStrings.getSelectsCSVFinalQueryStr())
				.append(" from ").append(tempTable).append(" ")
				.append(verticaQueryStrings.getFilterTempQueryStr())
				.append(" ").append(verticaQueryStrings.getOrderbyQueryStr());
		return finalQuery.toString();
	}
}
