package com.komli.prime.service.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.pojo.QueryResult;

public class SLPerformanceLogger {

	private static Logger logger = LoggerFactory.getLogger(SLPerformanceLogger.class);
	
	public static void logQueryResult(QueryResult queryResult, long sessionTime, String logSession) {
		StringBuilder tmp = new StringBuilder();
		tmp.append(logSession).append("State:").append(queryResult.getState()).append("|");
		tmp.append("SessionTime:").append(sessionTime).append("|");
		if(queryResult.getQueryBuildTime()>0){
			tmp.append("QueryBuildTime:").append(queryResult.getQueryBuildTime()).append("|");
		}
		if(queryResult.getQueryExecutionTime()>0){
			tmp.append("QueryExecutionTime:").append(queryResult.getQueryExecutionTime()).append("|");
		}
		if(queryResult.getQueryExecutionTime()>0){
			//tmp.append("QueryBuildTime:").append(queryResult.getQueryExecutionTime()).append("|");
			tmp.append("RowsFetched:").append(queryResult.getRecordSize()).append("|");
		}
		logger.info(tmp.toString());		
	}

	
}
