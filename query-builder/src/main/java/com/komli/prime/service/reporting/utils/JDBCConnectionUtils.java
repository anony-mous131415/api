package com.komli.prime.service.reporting.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.constants.SLConstants;

public class JDBCConnectionUtils {

	protected DataSource verticaDatasource = null;

	private static Logger logger = LoggerFactory.getLogger(JDBCConnectionUtils.class);
	
	private static JDBCConnectionUtils myInstance = null;
	
	private String path;
	
	public static enum DatabaseName{
		VERTICA
	}
	
	private JDBCConnectionUtils (String path){
		this.path = path;
		verticaDatasource = initConnectionPooling(DatabaseName.VERTICA);
	}

	public synchronized static JDBCConnectionUtils getInstance() {
		if (myInstance == null) {
			myInstance = new JDBCConnectionUtils(SLConstants.BASE_SERVICELAYER_DIR_PATH);
		}
		return myInstance;
	}

	public synchronized static JDBCConnectionUtils getInstance(String path) {
		if (myInstance == null) {
			myInstance = new JDBCConnectionUtils(path);
		}
		return myInstance;
	}

	public Connection getConnection(DatabaseName database){
		Connection conn = null;
		
		try{
			switch (database) {
			case VERTICA:
				if(verticaDatasource !=null){
					conn = verticaDatasource.getConnection();
				}				
				break;
			default:
				logger.error("ERROR : Database Name["+ database+ "] is not recognized.");
				break;
			}
			
		}
		catch( SQLException e ) {
			logger.error("Could not connect to " + database +". ERROR: {}", e.getMessage());
			e.printStackTrace();
		}
		return conn;
	}
	
	public DataSource getDataSource(DatabaseName database) {
		switch (database) {
		case VERTICA:
			return verticaDatasource;
		default:
			logger.error("ERROR : Database Name[" + database
					+ "] is not recognized.");
			return null;
		}

	}
	
	private DataSource initConnectionPooling(DatabaseName dbName){
		DataSource ds = null;
		InputStream is = null;
		try {
			PoolProperties poolProps = new PoolProperties();
			Properties props = new Properties();
			String configFileName = ""; 
			switch (dbName){
			case VERTICA:
				configFileName = this.path + "conf/vertica.properties";
				break;
			default:
				logger.error("ERROR : Database Name is not recognized.");
				//TODO throw exception
			}
			logger.trace("Loading config file : " + configFileName);
			is = new FileInputStream(configFileName);
			props.load(is);
			poolProps.setUrl(props.getProperty("ConnectionHost"));
			poolProps.setDriverClassName(props.getProperty("DriverClass"));
			poolProps.setUsername(props.getProperty("ConnectionUser"));
			poolProps.setPassword(props.getProperty("ConnectionPassword"));
			poolProps.setJmxEnabled("true".equals(props.getProperty("JmxEnabled")));

			poolProps.setMaxActive(Integer.parseInt(props.getProperty("MaxActive")));
			poolProps.setInitialSize(Integer.parseInt(props.getProperty("InitialSize")));
			poolProps.setMinIdle(Integer.parseInt(props.getProperty("MinIdle")));
			
			poolProps.setValidationQuery(props.getProperty("ValidationQuery"));
			poolProps.setTestWhileIdle("true".equals(props.getProperty("TestWhileIdle")));
			poolProps.setTestOnBorrow("true".equals(props.getProperty("TestOnBorrow")));
			poolProps.setTestOnReturn("true".equals(props.getProperty("TestOnReturn")));
			poolProps.setValidationInterval(Integer.parseInt(props.getProperty("ValidationInterval")));
			
			poolProps.setTimeBetweenEvictionRunsMillis(Integer.parseInt(props.getProperty("TimeBetweenEvictionRunsMillis")));
			poolProps.setMinEvictableIdleTimeMillis(Integer.parseInt(props.getProperty("MinEvictableIdleTimeMillis")));
			
			//poolProps.setMaxWait(Integer.parseInt(props.getProperty("MaxWait")));
			poolProps.setRemoveAbandoned("true".equals(props.getProperty("RemoveAbandoned")));
			poolProps.setRemoveAbandonedTimeout(Integer.parseInt(props.getProperty("RemoveAbandonedTimeout")));
			if(props.getProperty("MaxAge")!=null){
				poolProps.setMaxAge(Integer.parseInt(props.getProperty("MaxAge")));
			}			

			poolProps.setLogAbandoned(true);
			ds = new DataSource();
			ds.setPoolProperties(poolProps);
			logger.trace(ds.toString());
		} catch (IOException e) {
			logger.error("IOException in reading properties for DB:"+dbName+". ERROR: {}", e);
		} catch (Exception e) {
			logger.error("Could not connect to DB:"+dbName+". ERROR: {}", e);
		} finally{
			IOUtils.closeQuietly(is);
		}
		return ds;
	}

}
