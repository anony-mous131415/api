package com.komli.prime.service.reporting.querybuilder.impl.vertica.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.cache.VerticaCache;
import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.pojo.InputParameters;
import com.komli.prime.service.reporting.pojo.InputParameters.Interval;
import com.komli.prime.service.reporting.pojo.InputParameters.ReportType;
import com.komli.prime.service.reporting.pojo.InputParameters.SLOutput;
import com.komli.prime.service.reporting.pojo.VerticaElement;
import com.komli.prime.service.reporting.querybuilder.pojo.FilterParameter;
import com.komli.prime.service.reporting.querybuilder.pojo.OrderByParameter;
import com.komli.prime.service.reporting.querybuilder.pojo.SelectParameter;

public class VerticaQueryInputParameters {

	private static final Logger logger = LoggerFactory
			.getLogger(VerticaQueryInputParameters.class);

	private static final String VERTICA_TIMESTAMP_SELECT = "ts";

	private InputParameters inputObj;
	private String logSession;
	private String sessionId;

	private Interval interval = Interval.SUMMARY;
	private long startTimestamp;
	private long endTimestamp;
	private DateTimeZone tz = DateTimeZone.UTC;
	
    private ReportType reportType;
	private SLOutput output;
	private String reportId;
	private boolean toBeCached = false;
	private boolean isRightJoinQuery = false;
	
	private int limitRows;
	private int offsetRows;

	private List<VerticaElement> reportingSelectList = new ArrayList<VerticaElement>();
	private List<VerticaElement> reportingOuterSelectList = new ArrayList<VerticaElement>();
	private List<VerticaElement> outerSelectList = new ArrayList<VerticaElement>();
	private List<VerticaElement> groupByList = new ArrayList<VerticaElement>();
	private List<VerticaElement> reportingKeyFilterList = new ArrayList<VerticaElement>();
	private List<VerticaElement> metaDataFilterList = new ArrayList<VerticaElement>();
	private List<VerticaElement> valueFilterList = new ArrayList<VerticaElement>();
	private List<VerticaElement> orderByList = new ArrayList<VerticaElement>();
	private List<VerticaElement> joinByElementList = new ArrayList<VerticaElement>();

	public VerticaQueryInputParameters(InputParameters inpParams,
			String sessionId) throws QueryBuilderException {
		this.sessionId = sessionId;
		this.logSession = "Session :[" + sessionId + "]";
		this.inputObj = inpParams;

		startTimestamp = inputObj.getStarttime();
		endTimestamp = inputObj.getEndtime();
		interval = inputObj.getInterval();
		reportType = inputObj.getReportType();
		output = inputObj.getOutput();

		reportId = inputObj.getReportId();
		if (StringUtils.isBlank(reportId)) {
			reportId = sessionId;
		}
		toBeCached = inputObj.isToBeCached();
		if (toBeCached && SLOutput.CSV == inputObj.getOutput()) {
			toBeCached = false;
		}
		limitRows = inpParams.getLimitrows();
		offsetRows = inpParams.getOffset();
		populateVerticaTransformedInputParams();
	}

	private void populateVerticaTransformedInputParams() throws QueryBuilderException {
		VerticaCache cache = VerticaCache.getInstance();

		populateSelectElements(cache);
		populateOrderByElements(cache);
		populateFilters(cache);	
		populateGroupByElements(cache);
		
		differentiateSelectElements(cache);
		if (Interval.SUMMARY != getInterval() && ReportType.PARAM_CONV_REPORT != getReportType() ) {
			reportingSelectList.add(cache.getVerticaElement(VERTICA_TIMESTAMP_SELECT));
			groupByList.add(cache.getVerticaElement(VERTICA_TIMESTAMP_SELECT));
		}

	}

	private void differentiateSelectElements(VerticaCache cache) throws QueryBuilderException {
		for(VerticaElement elem : selectList){
			if (elem.isDependencyOnOtherParams()) {
				for (String dElem : elem.getDependencyParams()) {
					if(!unqSelects.contains(dElem)){
						VerticaElement vElem = cache.getVerticaElement(dElem);
						if(vElem.isReporting()){
							reportingSelectList.add(vElem);
						}
						if(!vElem.isMetadata()){
							reportingOuterSelectList.add(vElem);
						}
						unqSelects.add(dElem);
					}
				}
			}
			if(elem.isReporting()){
				reportingSelectList.add(elem);
			}
			if(!elem.isMetadata()){
				reportingOuterSelectList.add(elem);
			}else{
				joinByElementList.add(elem);
			}
		}		
	}

	private void populateOrderByElements(VerticaCache cache)
			throws QueryBuilderException {
		for (OrderByParameter orderBy : inputObj.getOrderByList()) {
			if (orderBy != null && orderBy.allValuePopulated()) {
				if (!unqOrderBys.contains(orderBy.getElementId())) {
					VerticaElement elem = cache.getVerticaElement(orderBy
							.getElementId());
					elem.setOrderByParams(orderBy.getOrder());
					orderByList.add(elem);
					unqOrderBys.add(elem.getElementId());
				}
			} else {
				logger.warn(logSession + "OrderBys are not defined properly");
				throw new QueryBuilderException(ReportingError.UNKNOWN_ORDERBY);
			}
		}

	}

	private void populateFilters(VerticaCache cache)
			throws QueryBuilderException {
		for (FilterParameter filter : inputObj.getFilters()) {
			if (filter != null && filter.allValuePopulated()) {
				VerticaElement elem = cache.getVerticaElement(filter
						.getElementId());
				elem.setWhereClauseParams(filter.getClause(), filter.getValue());
				if (elem.isKey() && elem.isMetadata()) {
					metaDataFilterList.add(elem);
					addToSelectList(elem);
					inputObj.getGroupList().add(elem.getElementId());
				
					if(elem.hasReportingEquivalent()){
						VerticaElement repElem = cache.getVerticaElement(elem
								.getReportingEquivalent());
						repElem.setWhereClauseParams(filter.getClause(), filter.getValue());
						reportingKeyFilterList.add(repElem);
					}
				}else if(elem.isKey() && !elem.isMetadata()){
					reportingKeyFilterList.add(elem);
				}else {
					valueFilterList.add(elem);
					addToSelectList(elem);
				}
			} else {
				logger.warn(logSession + "Filters are not defined properly");
				throw new QueryBuilderException(ReportingError.UNKNOWN_FILTER);
			}
		}

	}

	private void populateGroupByElements(VerticaCache cache)
			throws QueryBuilderException {
		for (String elem : inputObj.getGroupList()) {
			if (StringUtils.isNotBlank(elem)) {
				elem = elem.trim();
				VerticaElement vElem = cache.getVerticaElement(elem);
				if (!vElem.isKey()) {
					logger.warn(logSession + vElem.getElementId()
							+ " can not be a part of Groupby as it is a Value");
					throw new QueryBuilderException(
							ReportingError.VAUE_CANNOT_GROUPBY);
				}
				if (vElem.isDependencyOnOtherParams()) {
					for (String dElem : vElem.getDependencyParams()) {
						if (!unqGroupBys.contains(dElem) ) {
							VerticaElement dVelem = cache.getVerticaElement(dElem);
							if(dVelem.isMetadata()){
								continue;
							}
							groupByList.add(dVelem);
							unqGroupBys.add(dElem);
							addToSelectList(dVelem);
						}
					}
				} else if (!unqGroupBys.contains(elem) && !vElem.isMetadata()) {
					groupByList.add(vElem);
					unqGroupBys.add(elem);
				}
			} else {
				logger.warn(logSession + "GroupBys are not defined properly");
				throw new QueryBuilderException(ReportingError.UNKNOWN_GROUPBY);
			}
		}
	}

	private void populateSelectElements(VerticaCache cache)
			throws QueryBuilderException {
		for (SelectParameter selectParam : inputObj.getSelectList()) {
			if (selectParam != null && selectParam.getElementId() != null
					&& selectParam.allValuePopulated()) {
				if (!unqSelects.contains(selectParam.getElementId())) {
					VerticaElement elem = cache.getVerticaElement(selectParam
							.getElementId());
					if (selectParam.isPriority()) {
						isRightJoinQuery = true;
						elem.modifyJoinType(selectParam.isPriority());
					}
					selectList.add(elem);
					outerSelectList.add(elem);
					unqSelects.add(elem.getElementId());
					if (elem.isKey() && inputObj.isGroupby()) {
						inputObj.getGroupList().add(elem.getElementId());
					}
				}
			} else {
				logger.warn(logSession + "Selects are not defined properly");
				throw new QueryBuilderException(ReportingError.UNKNOWN_SELECT);
			}
		}
	}
	
	private void addToSelectList(VerticaElement elem){
		if (!unqSelects.contains(elem.getElementId())) {
			selectList.add(elem);
			unqSelects.add(elem.getElementId());
		}
	}

	public Interval getInterval() {
		return interval;
	}

	public void setInterval(Interval interval) {
		this.interval = interval;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public int getLimitRows() {
		return limitRows;
	}

	public int getOffsetRows() {
		return offsetRows;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public DateTimeZone getTz() {
		return tz;
	}

	public void setTz(DateTimeZone tz) {
		this.tz = tz;
	}

	public String getReportId() {
		return reportId;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	public boolean isToBeCached() {
		return toBeCached;
	}

	public void setToBeCached(boolean toBeCached) {
		this.toBeCached = toBeCached;
	}

	public boolean isRightJoinQuery() {
		return isRightJoinQuery;
	}

	public List<VerticaElement> getReportingSelectList() {
		return reportingSelectList;
	}
	
	public List<VerticaElement> getReportingOuterSelectList() {
		return reportingOuterSelectList;
	}

	public List<VerticaElement> getOuterSelectList() {
		return outerSelectList;
	}

	public List<VerticaElement> getGroupByList() {
		return groupByList;
	}

	public List<VerticaElement> getReportingKeyFilterList() {
		return reportingKeyFilterList;
	}

	public List<VerticaElement> getMetaDataFilterList() {
		return metaDataFilterList;
	}

	public List<VerticaElement> getValueFilterList() {
		return valueFilterList;
	}

	public List<VerticaElement> getOrderByList() {
		return orderByList;
	}

	public static String getVerticaTimestampSelect() {
		return VERTICA_TIMESTAMP_SELECT;
	}
	
    public ReportType getReportType() {
		return reportType;
	}

	public SLOutput getOutput() {
		return output;
	}

	public List<VerticaElement> getJoinByElementList() {
		return joinByElementList;
	}

	private HashSet<String> unqSelects = new HashSet<String>();
	private HashSet<String> unqGroupBys = new HashSet<String>();
	private HashSet<String> unqOrderBys = new HashSet<String>();
	private List<VerticaElement> selectList = new ArrayList<VerticaElement>();

}
