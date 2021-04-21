package com.komli.prime.service.reporting.querybuilder.pojo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.pojo.BaseElement;
import com.komli.prime.service.reporting.pojo.VerticaElement;
import com.komli.prime.service.reporting.utils.ReportingUtil;


public class FilterParameter {
	
	private static final Logger logger = LoggerFactory.getLogger(VerticaElement.class);
	
	private CLAUSES clause;
	private String value;
	private String elementId;
	
	public FilterParameter(String elementId, String value, CLAUSES clause) {
		setElementId(elementId);
		this.value = value;
		this.clause = clause;
	}
	public CLAUSES getClause() {
		return clause;
	}
	public String getValue() {
		return value;
	}
	public String getElementId() {
		return elementId;
	}
	public void setElementId(String elementId) {
		if(elementId !=null){
			this.elementId = elementId.trim();
		}
	}

	public boolean allValuePopulated() {
		if(clause !=null && StringUtils.isNotBlank(value) && StringUtils.isNotBlank(elementId)){
			return true;
		}
		return false;
	}
	
	public enum CLAUSES{
		LIKE(""," ILIKE '%","%'"),
		EQUAL(""," = '","'"),
		LT(""," < '","'"),
		GT(""," > '","'"),
		LTE(""," <= '","'"),
		GTE(""," >= '","'"),
		IN(""," IN(",")"),
		NOTIN(""," NOT IN(",")");
		private CLAUSES(String pre, String mid, String post) {
			this.pre = pre;
			this.mid = mid;
			this.post = post;
		}
		public String getClauseStr(String element, String value, int elementType, String elementId) throws QueryBuilderException{
			if(StringUtils.isNotBlank(value) && StringUtils.isNotBlank(value.trim()) && StringUtils.isNotBlank(element)){
				if(!ReportingUtil.isAllowableString(value)){
					logger.warn("Filter value["+value+"] is not allowed for element["+elementId+"]");
					throw new QueryBuilderException(ReportingError.FILTER_VALUE_NOT_ALLOWED);
				}
				value = value.trim();
				if(this!= NOTIN && this!= IN &&(BaseElement.TYPE_LONG == elementType || BaseElement.TYPE_LONG == elementType )){
					if(!ReportingUtil.isNumeric(value)){
						logger.warn("Filter value["+value+"] is not numeric for element["+elementId+"]");
						throw new QueryBuilderException(ReportingError.FILTER_VALUE_NOT_NUMERIC);
					}
				}
				if(this == LT || this == LTE){
					return  "("+pre+element+mid+value+post +" OR "+element +" is NULL)";
				}
				return pre+element+mid+value+post;
			}else{
				throw new QueryBuilderException(ReportingError.NOT_PROPER_FILTER);
			}
		}
		String pre;
		String mid;
		String post;
	}

}
