package com.komli.prime.service.reporting;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.Session.State;
import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.QueryExecutionException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.pojo.QueryResult;
import com.komli.prime.service.reporting.querybuilder.QueryBuilderFactory;
import com.komli.prime.service.reporting.querybuilder.api.Query;

public class ReportGenerationUtil {

	private static Logger logger = LoggerFactory.getLogger(ReportGenerationUtil.class);
	
	private Session session;
	private Query query;

	public ReportGenerationUtil(Session session) {
		this.session = session;
	}

	/**
	 * @param session
	 * @return	
	 */
	public QueryResult buildAndExecute() {
		QueryResult qr = new QueryResult();
		
		if(!buildQuery(session, qr)){
			session.setState(qr.getState());
			return qr;
		}
		if(!executeQuery(session, qr)){
			session.setState(qr.getState());
			return qr;
		}
		session.setState(qr.getState());
		return qr;
	}

	private  boolean buildQuery(Session session, QueryResult qr) {
		logger.info("Session["+session.getSessionId()+"]"+" Building Query ");
		try {
			long start = System.currentTimeMillis();
			buildQuery();
			qr.setState(State.QUERY_GENERATED);
			qr.setQueryBuildTime(System.currentTimeMillis()-start);
			logger.info("Session["+session.getSessionId()+"]"+" Exiting Building Query ");
			return true;
		} catch (QueryBuilderException e) {
			logger.warn("Session["+session.getSessionId()+"]"+"Query Generation Failed");
			qr.setReportError(e.getRepErr());
			qr.setState(State.QUERY_GENERATION_FAILED);
		} catch(Exception e){
			logger.warn("Session["+session.getSessionId()+"]"+"Query Generation Failed");
			logger.warn(e.getMessage(),e);
			qr.setReportError(ReportingError.INTERNAL_ERROR);
			qr.setState(State.QUERY_GENERATION_FAILED);
		}
		return false;
	}
	
	private boolean executeQuery(Session session, QueryResult qr) {
		logger.info("Session["+session.getSessionId()+"]"+" Entering Executing Query ");
		try {
			long start = System.currentTimeMillis();
			QueryResult tmp = executeQuery();
			qr.setState(State.QUERY_EXECUTED);
			qr.populateValues(tmp);
			qr.setQueryExecutionTime(System.currentTimeMillis()-start);
			logger.info("Session["+session.getSessionId()+"]"+" Exiting Executing Query ");
			return true;
		}catch(ReportGeneratingException e) {
			logger.warn("Query Failed with reason "+e.getRepErr().toString(), e);
			qr.setReportError(e.getRepErr());
			if(e instanceof QueryBuilderException){
				qr.setState(State.QUERY_GENERATION_FAILED);	
			}else {
				qr.setState(State.QUERY_EXECUTION_FAILED);	
			}		
		} catch(Exception e){
			logger.warn("Session["+session.getSessionId()+"]"+"Query Execution Failed",e);
			qr.setReportError(ReportingError.UNKNOWN_ERROR);
			qr.setState(State.QUERY_EXECUTION_FAILED);			
		}
		return false;
	}
	
	public void buildQuery() throws ReportGeneratingException {
		query = QueryBuilderFactory.getQuery(session.getInputParameters(), session.getSessionId());
		if(query !=null){
			query.checkConfiguration();
			query.buildQuery();	
		}else{
			logger.warn("Session["+session.getSessionId()+"]"+" Query Object is coming as null");
			throw new QueryBuilderException(ReportingError.QUERY_GENERATION_FAILED);
		}
	}

	public QueryResult executeQuery() throws ReportGeneratingException {
		if(query != null){
			if(session.isExport()){
				return query.exportQuery();
			}else if(session.isAdhocQuery()){
				return query.executeAdhocQuery();
			}
			return query.executeQuery();
		}else{
			logger.warn("Cannot be executed becuase:"+session.getState());
			throw new QueryExecutionException(ReportingError.QUERY_GENERATION_FAILED);
		}
	}

}
