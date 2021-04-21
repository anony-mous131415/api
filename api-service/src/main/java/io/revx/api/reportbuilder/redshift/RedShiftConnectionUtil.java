package io.revx.api.reportbuilder.redshift;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class RedShiftConnectionUtil {

	private static final Logger logger = LogManager.getLogger(RedShiftConnectionUtil.class);

	private DataSource dataSource = null;
	private String exportCommand = null;

	private static RedShiftConnectionUtil myInstance = null;

	private RedShiftConnectionUtil(String pathToConfigFile) {
		dataSource = initConnection();
	}

	public synchronized static RedShiftConnectionUtil getInstance() {
		if (myInstance == null) {
			myInstance = new RedShiftConnectionUtil("");
		}
		return myInstance;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public String getExportCommand() {
		return exportCommand;
	}

	private DataSource initConnection() {
		DataSource dataSource = null;
		InputStream inpStream = null;
		try {
			PoolProperties poolProps = new PoolProperties();
			Properties props = new Properties();

			String configFileName = "/atom/api-service/current/" + "conf/redshift.properties";

			inpStream = new FileInputStream(configFileName);
			props.load(inpStream);
			poolProps.setUrl(props.getProperty("redshift.datasource.url"));
			poolProps.setDriverClassName(props.getProperty("redshift.datasource.driver-class-name"));
			poolProps.setUsername(props.getProperty("redshift.datasource.username"));
			poolProps.setPassword(props.getProperty("redshift.datasource.password"));

			poolProps.setJmxEnabled("true".equals(props.getProperty("redshift.datasource.jmxEnabled")));

			poolProps.setMaxActive(Integer.parseInt(props.getProperty("redshift.datasource.maxActive")));
			poolProps.setInitialSize(Integer.parseInt(props.getProperty("redshift.datasource.initialSize")));
			poolProps.setMinIdle(Integer.parseInt(props.getProperty("redshift.datasource.minIdle")));

			poolProps.setValidationQuery(props.getProperty("redshift.datasource.validationQuery"));
			poolProps.setTestWhileIdle("true".equals(props.getProperty("redshift.datasource.testWhileIdle")));

			poolProps.setTestOnBorrow("true".equals(props.getProperty("redshift.datasource.testOnBorrow")));

			poolProps.setTestOnReturn("true".equals(props.getProperty("redshift.datasource.testOnReturn")));
			poolProps.setValidationInterval(
					Integer.parseInt(props.getProperty("redshift.datasource.validationInterval")));

			poolProps.setTimeBetweenEvictionRunsMillis(
					Integer.parseInt(props.getProperty("redshift.datasource.timeBetweenEvictionRunsMillis")));
			poolProps.setMinEvictableIdleTimeMillis(
					Integer.parseInt(props.getProperty("redshift.datasource.minEvictableIdleTimeMillis")));

			// poolProps.setMaxWait(Integer.parseInt(props.getProperty("MaxWait")));
			poolProps.setRemoveAbandoned("true".equals(props.getProperty("redshift.datasource.removeAbandoned")));
			poolProps.setRemoveAbandonedTimeout(
					Integer.parseInt(props.getProperty("redshift.datasource.removeAbandonedTimeout")));
			if (props.getProperty("redshift.datasource.maxAge") != null) {
				poolProps.setMaxAge(Integer.parseInt(props.getProperty("redshift.datasource.maxAge")));
			}

			poolProps.setLogAbandoned(true);
			dataSource = new DataSource();
			dataSource.setPoolProperties(poolProps);
			
			exportCommand = props.getProperty("redshift.VsqlCommand");

		} catch (IOException e) {
			logger.error("IOException in reading properties for DB: Redshift. ERROR: {}",
					ExceptionUtils.getFullStackTrace(e));
		} catch (Exception e) {
			logger.error("Could not connect to DB:Redshift. ERROR: {}", ExceptionUtils.getFullStackTrace(e));
		} finally {
			IOUtils.closeQuietly(inpStream);
		}
		return dataSource;
	}
}
