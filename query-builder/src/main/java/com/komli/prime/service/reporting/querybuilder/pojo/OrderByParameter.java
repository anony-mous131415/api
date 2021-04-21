package com.komli.prime.service.reporting.querybuilder.pojo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;

public class OrderByParameter {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderByParameter.class);
	private Order order;
	private String elementId;
	private boolean caseInsensitive;
	
	public OrderByParameter(String elementId, Order order) {
		setElementId(elementId);
		this.order = order;
		this.caseInsensitive = true;
		
	}
	public String getElementId() {
		return elementId;
	}
	public Order getOrder(){
		return order;
	}
	public void setElementId(String elementId) {
		if(elementId !=null){
			this.elementId = elementId.trim();
		}
	}
	public boolean allValuePopulated() {
		if(order !=null && StringUtils.isNotBlank(elementId)){
			return true;
		}
		return false;
	}
	
	public enum Order{
		ASC("ASC", true),
		DESC("DESC", true);
		private Order(String order, boolean caseInsensitive) {
			this.order = order;
			this.caseInsensitive = caseInsensitive;
		}		
		public String getOrderByStr(String element,int  type) throws QueryBuilderException{
			if(StringUtils.isNotBlank(element)){
				if(caseInsensitive && type ==1) {
					return "lower("+element+"::varchar) "+order;
				}
				return element+" "+order;
			}else{
				throw new QueryBuilderException(ReportingError.NOT_PROPER_ORDERBY);
			}
		}
		String order;
		boolean caseInsensitive;		
	}

}
