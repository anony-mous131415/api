package com.komli.prime.service.reporting;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.jmx.SLRerportingData;
import com.komli.prime.service.reporting.pojo.InputParameters;
import com.komli.prime.service.reporting.pojo.InputParameters.SLOutput;
import com.komli.prime.service.reporting.pojo.QueryResult;
import com.komli.prime.service.reporting.querybuilder.api.Query;
import com.komli.prime.service.reporting.utils.SessionUtils;

public class Session implements Serializable, Callable<QueryResult> {

	private static final long serialVersionUID = -7508275269233316593L;
	private static Logger logger = LoggerFactory.getLogger(Session.class);
	private InputParameters inputParameters;
	private String sessionId;
	private State state;
	private Query query;
	private String logSession;
	
	private boolean isExport = false;
	
	private boolean isAdhocQuery = false;
	

	public enum State {
		// List to be extended
		SESSION_CREATED, SESSION_REGISTERED, QUERY_GENERATED, QUERY_GENERATION_FAILED, 
		QUERY_EXECUTED, QUERY_EXECUTION_FAILED, SESSION_EXECUTION_FAILED
	}

	protected Session(InputParameters inputParameters) {
		this.inputParameters = inputParameters;
		this.sessionId = SessionUtils.generateSessionId();
		this.state = State.SESSION_CREATED;
		this.logSession = "Session :["+sessionId+"]";
		if(SLOutput.CSV == inputParameters.getOutput()){
			isExport = true;
		}else if(SLOutput.ADHOC== inputParameters.getOutput()){
			isAdhocQuery = true;
		}
	}

	public InputParameters getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(InputParameters inputParameters) {
		this.inputParameters = inputParameters;
	}

	public String getSessionId() {
		return sessionId;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public Query getQuery() {
		return this.query;
	}

	@Override
	public QueryResult call() throws Exception {		
		return buildAndExecute();
	}

	private QueryResult buildAndExecute() {
		ReportGenerationUtil repGenUtil = new ReportGenerationUtil(this);
		return repGenUtil.buildAndExecute();
	}
	
	public QueryResult getQueryResult(){
		long startTime = System.currentTimeMillis();
		long endMillis = startTime + SessionManager.TIME_BUDGET_IN_MILLIS;
		if(isExport){
			endMillis = startTime + SessionManager.EXPORT_TIME_BUDGET_IN_MILLIS;
		}else if(isAdhocQuery){
			endMillis = startTime + SessionManager.ADHOC_QUERY_TIME_BUDGET_IN_MILLIS;
		}
		SessionManager mgr = SessionManager.getInstance();
		Future<QueryResult> future =  mgr.registerAndFetchResult(this);
		setState(State.SESSION_REGISTERED);
		QueryResult queryResult = new QueryResult(this.sessionId);
		try {
			// Only wait for the remaining time budget
			long timeLeft = endMillis - System.currentTimeMillis();
			queryResult = future.get(timeLeft, TimeUnit.MILLISECONDS);
		} catch (ExecutionException e) {
			logger.warn(logSession+"Execution Exception");
		} catch (TimeoutException e) {
			SLRerportingData.numSessionTimedOuts++;
			logger.warn(logSession+"Execution timed-out. Cancelling the execution");
			queryResult.setReportError(ReportingError.EXECUTION_TIMEDOUT);
			future.cancel(true);
		} catch (InterruptedException e) {
			logger.warn(e.getMessage(),e);
			queryResult.setReportError(ReportingError.INTERUPTED_EXCEPTION);
		} 
		logSessionInfo(queryResult,System.currentTimeMillis() - startTime);
		return queryResult;
	}

	public boolean isExport() {
		return isExport;
	}

	public boolean isAdhocQuery() {
		return isAdhocQuery;
	}

	public void setAdhocQuery(boolean isAdhocQuery) {
		this.isAdhocQuery = isAdhocQuery;
	}

	private void logSessionInfo(QueryResult queryResult, long sessionTime) {
		SLPerformanceLogger.logQueryResult(queryResult, sessionTime,logSession);
			
	}
	public static void main(String[] args) {
		System.out.println(State.QUERY_EXECUTED);
	}
	
}
