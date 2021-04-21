package com.komli.prime.service.reporting.querybuilder.api;

import com.komli.prime.service.reporting.exceptions.ReportGeneratingException;
import com.komli.prime.service.reporting.pojo.QueryResult;

public interface Query {
	public void buildQuery() throws ReportGeneratingException;
	public QueryResult executeQuery() throws ReportGeneratingException;
	public QueryResult exportQuery() throws ReportGeneratingException;
	public QueryResult executeAdhocQuery() throws ReportGeneratingException;
	public boolean checkConfiguration() throws ReportGeneratingException;
}
