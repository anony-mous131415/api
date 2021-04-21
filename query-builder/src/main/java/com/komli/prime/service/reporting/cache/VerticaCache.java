package com.komli.prime.service.reporting.cache;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.constants.SLConstants;
import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.pojo.VerticaElement;
import com.komli.prime.service.reporting.utils.SLJsonReader;
import com.sun.jersey.impl.ApiMessages;

public class VerticaCache {
	private static final Logger logger = LoggerFactory
			.getLogger(VerticaCache.class);

	private static String TABLE_DAILY_PARAM_CONV_REPORT = "parametricConversionReport";
	private static String TABLE_DAILY_BIDFUNNEL = "biddaily";
	private static String TABLE_DAILY_UNQUSER = "uudaily";

	private static String TABLE_DAILY_AUDIENCE = "audiencedaily";
	private static String TABLE_MONTHLY_AUDIENCE = "audiencemonthly";

	private static String TABLE_HOURLY_RDB = "rdbhourly";
	private static String TABLE_DAILY_RDB = "rdbdaily";
	private static String TABLE_MONTHLY_RDB = "rdbmonthly";
	private static String TABLE_HOURLY_RTB = "hourly";
	private static String TABLE_DAILY_RTB = "daily";
	private static String TABLE_MONTHLY_RTB = "monthly";


	public static String getTableDailyParamConvReport() {
		return TABLE_DAILY_PARAM_CONV_REPORT;
	}
	
	public static final String getBidfunnelDailyTableName() {
		return TABLE_DAILY_BIDFUNNEL;
	}

	public static final String getUnqUserDailyTableName() {
		return TABLE_DAILY_UNQUSER;
	}

	public static final String getAudienceDailyTableName() {
		return TABLE_DAILY_AUDIENCE;
	}

	public static final String getAudienceMonthlyTableName() {
		return TABLE_MONTHLY_AUDIENCE;
	}

	public static final String getManagedHourlyTableName() {
		return TABLE_HOURLY_RDB;
	}

	public static final String getManagedDailyTableName() {
		return TABLE_DAILY_RDB;
	}

	public static final String getManagedMonthlyTableName() {
		return TABLE_MONTHLY_RDB;
	}

	public static final String getRTBHourlyTableName() {
		return TABLE_HOURLY_RTB;
	}

	public static final String getRTBDailyTableName() {
		return TABLE_DAILY_RTB;
	}

	public static final String getRTBMonthlyTableName() {
		return TABLE_MONTHLY_RTB;
	}

	private HashMap<String, VerticaElement> verticaElementMapping = new HashMap<String, VerticaElement>();
	private static VerticaCache _cache;
	public static boolean cachePopulated = false;

	/**
	 * @return singleton VerticaCache object
	 */
	public static VerticaCache getInstance() {
		if (_cache != null) {
			return _cache;
		}
		synchronized (VerticaCache.class) {
			if (_cache == null) {
				_cache = new VerticaCache(
						SLConstants.BASE_SERVICELAYER_DIR_PATH);
			}
		}
		return _cache;
	}

	/**
	 * @return singleton VerticaCache object
	 */
	public static VerticaCache getInstance(String path) {
		if (_cache != null) {
			return _cache;
		}
		synchronized (VerticaCache.class) {
			if (_cache == null) {
				_cache = new VerticaCache(path);
			}
		}
		return _cache;
	}

	private VerticaCache(String path) {
		initTableNames(path);
		populateVerticaElementMapping(path);
	}

	private void initTableNames(String path) {
		Properties properties = new Properties();
		String file = path + "conf/querybuilder_tables.properties";
		try {
			FileReader reader = new FileReader(file);
			properties.load(reader);
			
			TABLE_DAILY_PARAM_CONV_REPORT = properties.getProperty(
					"TABLE_DAILY_PARAM_CONV_REPORT", TABLE_DAILY_PARAM_CONV_REPORT);
			TABLE_DAILY_BIDFUNNEL = properties.getProperty(
					"TABLE_DAILY_BIDFUNNEL", TABLE_DAILY_BIDFUNNEL);
			TABLE_DAILY_UNQUSER = properties.getProperty("TABLE_DAILY_UNQUSER",
					TABLE_DAILY_UNQUSER);
			TABLE_DAILY_AUDIENCE = properties.getProperty(
					"TABLE_DAILY_AUDIENCE", TABLE_DAILY_AUDIENCE);
			TABLE_MONTHLY_AUDIENCE = properties.getProperty(
					"TABLE_MONTHLY_AUDIENCE", TABLE_MONTHLY_AUDIENCE);
			TABLE_HOURLY_RDB = properties.getProperty("TABLE_HOURLY_RDB",
					TABLE_HOURLY_RDB);
			TABLE_DAILY_RDB = properties.getProperty("TABLE_DAILY_RDB",
					TABLE_DAILY_RDB);
			TABLE_MONTHLY_RDB = properties.getProperty("TABLE_MONTHLY_RDB",
					TABLE_MONTHLY_RDB);
			TABLE_HOURLY_RTB = properties.getProperty("TABLE_HOURLY_RTB",
					TABLE_HOURLY_RTB);
			TABLE_DAILY_RTB = properties.getProperty("TABLE_DAILY_RTB",
					TABLE_DAILY_RTB);
			TABLE_MONTHLY_RTB = properties.getProperty("TABLE_MONTHLY_RTB",
					TABLE_MONTHLY_RTB);
			reader.close();
		} catch (FileNotFoundException e) {
			logger.warn(file + " not found");
		} catch (IOException e) {
			logger.warn("IOException while accessing file " + file);
		}

	}

	private void populateVerticaElementMapping(String path) {
		logger.trace("Entering populateVerticaElementMapping");

		verticaElementMapping = SLJsonReader.getVerticaElementMapping(path);

		if (verticaElementMapping != null && verticaElementMapping.size() > 0) {
			cachePopulated = true;
		}
		logger.trace("Exiting populateVerticaElementMapping");
	}

	public VerticaElement getVerticaElement(String element_id)
			throws QueryBuilderException {
		VerticaElement elem = verticaElementMapping.get(element_id);
		if (elem == null) {
			logger.error("Unknown element in SELECTS, Element/Column [" + element_id
					+ "] is not supported.");
			throw new QueryBuilderException(ReportingError.UNKNOWN_SELECT);
		}
		return elem.getCopy();

	}


}
