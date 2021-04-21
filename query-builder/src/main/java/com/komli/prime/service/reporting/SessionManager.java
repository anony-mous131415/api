package com.komli.prime.service.reporting;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.cache.VerticaCache;
import com.komli.prime.service.reporting.constants.SLConstants;
import com.komli.prime.service.reporting.export.vertica.VerticaExportManager;
import com.komli.prime.service.reporting.jmx.SLRerportingData;
import com.komli.prime.service.reporting.jmx.SimpleAgent;
import com.komli.prime.service.reporting.pojo.InputParameters;
import com.komli.prime.service.reporting.pojo.QueryResult;
import com.komli.prime.service.reporting.utils.JDBCConnectionUtils;

public class SessionManager implements Serializable {

	private static final long serialVersionUID = -7508275269233316593L;
	private static Logger logger = LoggerFactory.getLogger(SessionManager.class);
	
	public static long TIME_BUDGET_IN_MILLIS = 60000;
	public static long EXPORT_TIME_BUDGET_IN_MILLIS = 120000;
	public static long ADHOC_QUERY_TIME_BUDGET_IN_MILLIS = 30000;
	
	private static int THREADS = 50;
	private static int EXPXORT_THREADS = 10;
	private static int ADHOC_QUERY_THREADS = 5;
	private static SessionManager _manager = null;
	private static SimpleAgent _jmxAgent;
	private static CompletionService<QueryResult> _service = null;
	private static CompletionService<QueryResult> _exportservice = null;
	private static CompletionService<QueryResult> _adhocQueryservice = null;
	
	/**
	 * @return singleton SessionManager object
	 */
	public synchronized static SessionManager getInstance(){
		if (_manager!= null) {
			return _manager;
		}
		synchronized (SessionManager.class) {
			if (_manager == null) {
				_manager = new SessionManager(SLConstants.BASE_SERVICELAYER_DIR_PATH);
			}
		}
		return _manager;		
	}
	
	/**
	 * @return singleton SessionManager object
	 */
	public synchronized static SessionManager getInstance(Properties prop ){
		if (_manager!= null) {
			return _manager;
		}
		synchronized (SessionManager.class) {
			if (_manager == null) {
				_manager = new SessionManager(prop);
			}
		}
		return _manager;		
	}
	
	private SessionManager(String path){
		initializeComponents(path);
	}

	private SessionManager(Properties prop){
		String path = prop.getProperty(SLConstants.PROPKEY_BASE_SERVICELAYER_DIR_PATH, 
				SLConstants.BASE_SERVICELAYER_DIR_PATH);
		initializeComponents(path);
	}
	
	private void initializeComponents(String path) {
		readSessionManagmentProperties(path);
		ExecutorService executor = Executors.newFixedThreadPool(THREADS);
		_service = new ExecutorCompletionService<QueryResult>(
				executor);
		ExecutorService csvExecutor = Executors.newFixedThreadPool(EXPXORT_THREADS);
		_exportservice = new ExecutorCompletionService<QueryResult>(
				csvExecutor);
		ExecutorService adhocQueryExecutor = Executors.newFixedThreadPool(ADHOC_QUERY_THREADS);
		_adhocQueryservice = new ExecutorCompletionService<QueryResult>(
				adhocQueryExecutor);
		JDBCConnectionUtils.getInstance(path);
		_jmxAgent = new SimpleAgent();
		VerticaCache.getInstance(path);	
		VerticaExportManager.getInstance(path);
	}

	public Session getSession(InputParameters inputParameters){
		return new Session(inputParameters);
	}
	
	
	private void readSessionManagmentProperties(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path + "conf/sl-session.properties");
			Properties props = new Properties();
				props.load(is);
			TIME_BUDGET_IN_MILLIS = Long.parseLong(props.getProperty("TIME_BUDGET_IN_MILLIS"));
			THREADS = Integer.parseInt(props.getProperty("MAX_SESSIONS"));
			EXPORT_TIME_BUDGET_IN_MILLIS = Long.parseLong(props.getProperty("EXPORT_TIME_BUDGET_IN_MILLIS"));
			EXPXORT_THREADS = Integer.parseInt(props.getProperty("MAX_EXPORT_SESSIONS"));
			ADHOC_QUERY_TIME_BUDGET_IN_MILLIS = Long.parseLong(props.getProperty("ADHOC_QUERY_TIME_BUDGET_IN_MILLIS"));
			ADHOC_QUERY_THREADS = Integer.parseInt(props.getProperty("MAX_ADHOC_QUERY_SESSIONS"));
		} catch (IOException e) {
			logger.error("Properties not found: sl-session.properties",e);
		} catch(NumberFormatException e){
			logger.error("Number Format Exception while parsing Session Attributes",e);
		}finally{
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * @param session
	 * @return future
	 */
	public Future<QueryResult> registerAndFetchResult(Session session){
		logger.info("Registering session: "+ session.getSessionId());
		SLRerportingData.numSessionCreations++;
		if(session.isExport()){
			return _exportservice.submit(session);
		}else if(session.isAdhocQuery()){
			return _adhocQueryservice.submit(session);
		}else{
			return _service.submit(session);
		}
	}
}
