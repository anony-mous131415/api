package com.komli.prime.service.reporting.querybuilder.pojo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectParameter {

	private static final Logger logger = LoggerFactory
			.getLogger(SelectParameter.class);
	private boolean priority;
	private String elementId;

	public SelectParameter(String elementId, boolean priority) {
		setElementId(elementId);
		this.priority = priority;
	}

	public SelectParameter(String elementId) {
		setElementId(elementId);
		this.priority = false;
	}
	
	public void setElementId(String elementId) {
		if(elementId !=null){
			this.elementId = elementId.trim();
		}
	}
	
	public String getElementId() {
		return elementId;
	}

	public boolean isPriority() {
		return priority;
	}
	
	public boolean allValuePopulated() {
		if(StringUtils.isNotBlank(elementId)){
			return true;
		}
		return false;
	}

}
