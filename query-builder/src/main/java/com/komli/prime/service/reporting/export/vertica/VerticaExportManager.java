package com.komli.prime.service.reporting.export.vertica;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.constants.SLConstants;

public class VerticaExportManager {

	private static Logger logger = LoggerFactory.getLogger(VerticaExportManager.class);

	public String VSQLCommand = "";
	private static VerticaExportManager _manager = null;

	/**
	 * @return singleton SessionManager object
	 */
	public synchronized static VerticaExportManager getInstance(String path){
		if (_manager!= null) {
			return _manager;
		}
		synchronized (VerticaExportManager.class) {
			if (_manager == null) {
				_manager = new VerticaExportManager(path);
			}
		}
		return _manager;		
	}
	
	public synchronized static VerticaExportManager getInstance(){
		if (_manager!= null) {
			return _manager;
		}
		synchronized (VerticaExportManager.class) {
			if (_manager == null) {
				_manager = new VerticaExportManager(SLConstants.BASE_SERVICELAYER_DIR_PATH);
			}
		}
		return _manager;		
	}
	
	private VerticaExportManager(String path) {
		readSessionManagmentProperties(path);
	}
	
	private void readSessionManagmentProperties(String path) {
		InputStream is = null;
		try {
			is = new FileInputStream(path + "conf/vertica.properties");
			Properties props = new Properties();
				props.load(is);
			//TIME_BUDGET_IN_MILLIS = Long.parseLong(props.getProperty("TIME_BUDGET_IN_MILLIS"));
				VSQLCommand = props.getProperty("VsqlCommand");
				if(StringUtils.isBlank(VSQLCommand)){
					logger.error("VSQLCommand Entry is not found in vertica.properties");
				}
		} catch (IOException e) {
			logger.error("Properties not found: vertica.properties",e);
		} catch(NumberFormatException e){
			logger.error("Number Format Exception which parsing Session Attributes",e);
		}finally{
			IOUtils.closeQuietly(is);
		}
	}
}