package com.komli.prime.service.reporting.querybuilder.impl.vertica.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.pojo.InputParameters.ReportType;
import com.komli.prime.service.reporting.pojo.VerticaElement;
import com.komli.prime.service.reporting.pojo.VerticaElement.OuterJoin;
import com.komli.prime.service.reporting.pojo.VerticaElement.VerticaJoin;

public class VerticaQueryStrings {

	private static final Logger logger = LoggerFactory
			.getLogger(VerticaQueryStrings.class);

	private static final String COMMA = ",";
	private String selectsFinalQueryStr = "";
	private String selectsCSVFinalQueryStr = "";
	private String selectsReportingOuterQueryStr = "";
	private String filterValuesQueryStr = "";
	private String filterMetadataQueryStr = "";
	private String groupByQueryStr = "";
	private String orderbyQueryStr = "";
	private String joinQueryStr = "";
	private String limitOffsetStr = "";
	private String filterTempQueryStr = "";
	private String selectTempQueryStr = "";

	private VerticaQueryInputParameters inpParams;
	private String logSession;

	public VerticaQueryStrings(VerticaQueryInputParameters inpParams,
			String sessionId) throws QueryBuilderException {
		this.logSession = "Session :[" + sessionId + "]";
		this.inpParams = inpParams;
		populateSelectStrings();
		populateFilterStrings();
		populateGroupByStrings();
		populateOrderByStrings();
		populateJoinStr();
		populateLimitOffsetStr();
		populateTempTableQueryString();
	}

	private void populateTempTableQueryString() throws QueryBuilderException {
		HashSet<String> selects = new HashSet<String>();
		StringBuilder selectTempTableQuery = new StringBuilder("");
		StringBuilder tempTableFilter = new StringBuilder("");
		StringBuilder filters = new StringBuilder("");
		for (VerticaElement elem : inpParams.getOuterSelectList()) {
			selectTempTableQuery.append(elem.getElementId()).append(" as ").append(elem.getElementIdIndentifier()).append(",");
			selects.add(elem.getElementId());
		}
		selectTempTableQuery.deleteCharAt(selectTempTableQuery.length() - 1);
		
		for(VerticaElement elem : inpParams.getReportingKeyFilterList()){
			if(selects.contains(elem.getElementId())){
				filters.append(" "+elem.getTempWhereByStr() + " and");
			}
		}
		for(VerticaElement elem : inpParams.getMetaDataFilterList()){
			if(selects.contains(elem.getElementId())){
				filters.append(" "+elem.getTempWhereByStr() + " and");
			}
		}
		for(VerticaElement elem : inpParams.getValueFilterList()){
			if(selects.contains(elem.getElementId())){
				filters.append(" "+elem.getTempWhereByStr() + " and");
			}
		}
		if(filters.length() >0){
			filters.delete(filters.length() - 3, filters.length());
			tempTableFilter.append(" where ").append(filters);
		}
		selectTempQueryStr = selectTempTableQuery.toString();
		filterTempQueryStr = tempTableFilter.toString();
	}

	private void populateLimitOffsetStr (){
		StringBuilder limitOffSet = new StringBuilder("");
		if(inpParams.getLimitRows() > 0){
			limitOffSet.append(" limit ").append(inpParams.getLimitRows());
		}
		if(inpParams.getOffsetRows() > 0 ) {
			limitOffSet.append(" offset ").append(inpParams.getOffsetRows());
		}
		limitOffsetStr = limitOffSet.toString();
	}
	
	private void populateJoinStr() throws QueryBuilderException {
		Map<String, VerticaJoin> joinMap = new LinkedHashMap<String, VerticaJoin>();
		for (VerticaElement elem : inpParams.getJoinByElementList()) {
			VerticaJoin join = elem.getMetadataVerticaJoin();
			if(joinMap.get(join.getJoinTable())==null || join.getJoinType().equals(OuterJoin.RIGHT)){
				joinMap.put(join.getJoinTable(), join);
			}
		}
		joinQueryStr = getJoinStr(joinMap);		 
	}
	
	private void populateOnlyMetaDataJoinStr() throws QueryBuilderException {
		Map<String, VerticaJoin> joinMap = new LinkedHashMap<String, VerticaJoin>();
		for (VerticaElement elem : inpParams.getJoinByElementList()) {
			VerticaJoin join = elem.getMetadataVerticaJoin();
			if(joinMap.get(join.getJoinTable())==null || join.getJoinType().equals(OuterJoin.RIGHT)){
				joinMap.put(join.getJoinTable(), join);
			}
		}
		joinQueryStr = getJoinStr(joinMap);		 
	}

	private void populateFilterStrings() throws QueryBuilderException {
		StringBuilder filterValues = new StringBuilder("");
		StringBuilder filterMetadata = new StringBuilder("");
//		if (inpParams.getValueFilterList().size() > 0) {
//			filterValues.append(" having ");
//			for (VerticaElement elem : inpParams.getValueFilterList()) {
//				filterValues.append(" ").append(elem.getReportingOuterWhereByStr())
//						.append(" and");
//			}
//			filterValues.delete(filterValues.length() - 3,
//					filterValues.length());
//		}
		if (inpParams.getMetaDataFilterList().size() > 0 || inpParams.getValueFilterList().size() > 0) {
			filterMetadata.append(" where ");
			for (VerticaElement elem : inpParams.getMetaDataFilterList()) {
				filterMetadata.append(" ").append(elem.getMetadataWhereByStr())
						.append(" and");
			}
			for (VerticaElement elem : inpParams.getValueFilterList()) {
				filterMetadata.append(" ").append(elem.getOuterValueWhereByStr())
						.append(" and");
			}
			filterMetadata.delete(filterMetadata.length() - 3,
					filterMetadata.length());
		}
		filterValuesQueryStr = filterValues.toString();
		filterMetadataQueryStr = filterMetadata.toString();
	}

	private void populateOrderByStrings() throws QueryBuilderException {
		StringBuilder orderBy = new StringBuilder("");
		if (inpParams.getOrderByList().size() > 0) {
			orderBy.append(" order by ");
			for (VerticaElement elem : inpParams.getOrderByList()) {
				orderBy.append(elem.getOrderByStr()).append(",");
			}
			orderBy.deleteCharAt(orderBy.length() - 1);
		}
		if (inpParams.getReportType().equals(ReportType.BIDFUNNEL)) {
		    orderBy.append(" order by bidtimestamp ");
		}
		if (inpParams.getReportType().equals(ReportType.UNQUSERS)) {
            orderBy.append(" order by unqtimestamp ");
        }
		orderbyQueryStr = orderBy.toString();
	}

	private void populateGroupByStrings() throws QueryBuilderException {
		StringBuilder groupBy = new StringBuilder("");
		if (inpParams.getGroupByList().size() > 0) {
			groupBy.append(" group by ");
			for (VerticaElement elem : inpParams.getGroupByList()) {
				groupBy.append(elem.getGroupBy()).append(",");
			}
			groupBy.deleteCharAt(groupBy.length() - 1);
		}
		groupByQueryStr = groupBy.toString();
	}

	private void populateSelectStrings() throws QueryBuilderException {
		StringBuilder selectsFinal = new StringBuilder("");
		StringBuilder selectsCSVFinal = new StringBuilder("");
		for (VerticaElement elem : inpParams.getOuterSelectList()) {
			selectsFinal.append(elem.getFinalSelectQueryStr()).append(COMMA);
			selectsCSVFinal.append(elem.getOuterSelectCSVStr()).append(COMMA);
		}
		if (selectsFinal.length() < 1) {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		StringBuilder selectsReportingOuter = new StringBuilder("");
		for (VerticaElement elem : inpParams.getReportingOuterSelectList()) {
			selectsReportingOuter.append(elem.getReportingOuterSelectStr()).append(
					COMMA);
		}
		selectsFinal.deleteCharAt(selectsFinal.length() - 1);
		selectsCSVFinal.deleteCharAt(selectsCSVFinal.length() - 1);
		selectsReportingOuter.deleteCharAt(selectsReportingOuter.length() - 1);

		selectsFinalQueryStr = selectsFinal.toString();
		selectsCSVFinalQueryStr = selectsCSVFinal.toString();
		selectsReportingOuterQueryStr = selectsReportingOuter.toString();
	}

	public String prepareRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder();
		switch (inpParams.getReportType()) {
		case MANAGED:
			rawStr.append("(").append(getManagedRawSubQuery()).append(")");
			break;
		case RTB:
			rawStr.append("(").append(getRtbRawSubQuery()).append(")");
			break;
		case RTBnMANAGED:
			/*rawStr.append("(").append(getManagedRawSubQuery())
			.append(") UNION ALL (").append(getRtbRawSubQuery())
			.append(")");*/
			rawStr.append("(").append(getRtbRawSubQuery())
			.append(")");
			break;
		case AUDIENCE:
			rawStr.append("(").append(getAudienceRawSubQuery()).append(")");
			break;	
		case BIDFUNNEL:
			rawStr.append("(").append(getBidfunnelRawSubQuery()).append(")");
			break;
		case UNQUSERS:
			rawStr.append("(").append(getUnqUserRawSubQuery()).append(")");
			break;
		case UFRECORDCOUNT:
			rawStr.append("(").append(getUfRecordCountRawSubQuery()).append(")");
			break;
		case PARAM_CONV_REPORT:
			rawStr.append("(").append(getParamConvReportRawSubQuery()).append(")");
			break;
		default:
			break;
		// throw new QueryBuilderException("ReportType not supported");
		}
		return rawStr.toString();
	}

	private String getManagedRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select ");
		StringBuilder whereByStr = new StringBuilder(
				" where timestampwhereclause and");
		// SELECTS
		if (inpParams.getReportingSelectList().size() > 0) {
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getReportingSelectList()) {
				selectListSet.add(elem.getManagedSelectStr());
			}
			for (String select : selectListSet) {
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		} else {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from managedtable");

		// WHERE
		for (VerticaElement elem : inpParams.getReportingKeyFilterList()) {
			whereByStr.append(" " + elem.getManagedWhereByStr() + " and");
		}
		whereByStr.delete(whereByStr.length() - 3, whereByStr.length());
		rawStr.append(whereByStr);
		
		// GROUP BY
		rawStr.append(" ").append(groupByQueryStr).append(" ");
		return rawStr.toString();
	}

	private String getRtbRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select ");
		StringBuilder whereByStr = new StringBuilder(
				" where timestampwhereclause and");
		// SELECTS
		if (inpParams.getReportingSelectList().size() > 0) {
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getReportingSelectList()) {
				selectListSet.add(elem.getRtbSelectStr());
			}
			for (String select : selectListSet) {
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		} else {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from rtbtable");
		
		// WHERE
		for (VerticaElement elem : inpParams.getReportingKeyFilterList()) {
			whereByStr.append(" " + elem.getRtbWhereByStr() + " and");
		}
		whereByStr.delete(whereByStr.length() - 3, whereByStr.length());
		rawStr.append(whereByStr);

		// GROUP BY
		rawStr.append(" ").append(groupByQueryStr).append(" ");
		return rawStr.toString();
	}

	private String getAudienceRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select ");
		StringBuilder whereByStr = new StringBuilder(
				" where timestampwhereclause and");
		// SELECTS
		if (inpParams.getReportingSelectList().size() > 0) {
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getReportingSelectList()) {
				selectListSet.add(elem.getAudienceSelectStr());
			}
			for (String select : selectListSet) {
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		} else {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from audiencetable");
		
		// WHERE
		for (VerticaElement elem : inpParams.getReportingKeyFilterList()) {
			whereByStr.append(" " + elem.getAudienceWhereByStr() + " and");
		}
		whereByStr.delete(whereByStr.length() - 3, whereByStr.length());
		rawStr.append(whereByStr);

		// GROUP BY
		rawStr.append(" ").append(groupByQueryStr).append(" ");
		return rawStr.toString();
	}
	
	private String getBidfunnelRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select ");
		StringBuilder whereByStr = new StringBuilder(
				" where timestampwhereclause and");
		// SELECTS
		if (inpParams.getReportingSelectList().size() > 0) {
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getReportingSelectList()) {
				selectListSet.add(elem.getBidFunnelSelectStr());
			}
			for (String select : selectListSet) {
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		} else {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from bidfunneltable");
		
		// WHERE
		for (VerticaElement elem : inpParams.getReportingKeyFilterList()) {
			whereByStr.append(" " + elem.getBidfunnelWhereByStr() + " and");
		}
		whereByStr.delete(whereByStr.length() - 3, whereByStr.length());
		rawStr.append(whereByStr);

		// GROUP BY
		rawStr.append(" ").append(groupByQueryStr).append(" ");
		return rawStr.toString();
	}
	
	private String getUnqUserRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select ");
		StringBuilder whereByStr = new StringBuilder(
				" where timestampwhereclause and");
		// SELECTS
		if (inpParams.getReportingSelectList().size() > 0) {
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getReportingSelectList()) {
				selectListSet.add(elem.getUnqUserSelectStr());
			}
			for (String select : selectListSet) {
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		} else {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from unqusertable");
		
		// WHERE
		for (VerticaElement elem : inpParams.getReportingKeyFilterList()) {
			whereByStr.append(" " + elem.getUnqUserWhereByStr() + " and");
		}
		whereByStr.delete(whereByStr.length() - 3, whereByStr.length());
		rawStr.append(whereByStr);

		// GROUP BY
		rawStr.append(" ").append(groupByQueryStr).append(" ");
		return rawStr.toString();
	}
	
	private String getUfRecordCountRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select ");
		StringBuilder whereByStr = new StringBuilder(
				" where timestampwhereclause and");
		// SELECTS
		if (inpParams.getReportingSelectList().size() > 0) {
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getReportingSelectList()) {
				selectListSet.add(elem.getUnqUserSelectStr());
			}
			for (String select : selectListSet) {
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		} else {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from unqusertable");
		
		// WHERE
		for (VerticaElement elem : inpParams.getReportingKeyFilterList()) {
			whereByStr.append(" " + elem.getUnqUserWhereByStr() + " and");
		}
		whereByStr.delete(whereByStr.length() - 3, whereByStr.length());
		rawStr.append(whereByStr);

		return rawStr.toString();
	}

	private String getParamConvReportRawSubQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select ");
		StringBuilder whereByStr = new StringBuilder(
				" where timestampwhereclause and");
		// SELECTS
		if (inpParams.getReportingSelectList().size() > 0) {
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getReportingSelectList()) {
				selectListSet.add(elem.getParamConvReportSelectStr());
			}
			for (String select : selectListSet) {
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		} else {
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from paramConvTable");
		
		// WHERE
		for (VerticaElement elem : inpParams.getReportingKeyFilterList()) {
			whereByStr.append(" " + elem.getParamConvReportWhereByStr() + " and");
		}
		whereByStr.delete(whereByStr.length() - 3, whereByStr.length());
		rawStr.append(whereByStr);

		return rawStr.toString();
	}
	
	
	public String getOnlyMetaDataQuery() throws QueryBuilderException {
		StringBuilder rawStr = new StringBuilder(" Select distinct ");
		StringBuilder whereByStr = new StringBuilder("");
		Map<String, VerticaJoin> joinMap = new LinkedHashMap<String, VerticaJoin>();
		VerticaJoin rightJoin =  null;
		// SELECTS
		if(inpParams.getOuterSelectList().size() > 0){
			Set<String> selectListSet = new LinkedHashSet<String>();
			for (VerticaElement elem : inpParams.getOuterSelectList()){
				if(elem.isMetadata()){
					selectListSet.add(elem.getMetadataSelectStr());
					VerticaJoin join = elem.getMetadataVerticaJoin();
					if(join.getJoinType().equals(OuterJoin.RIGHT)){
						rightJoin = join;
					}
					if(joinMap.get(join.getJoinTable())==null || join.getJoinType().equals(OuterJoin.RIGHT)){
						joinMap.put(join.getJoinTable(), join);
					}

				}
			}
			for(String select : selectListSet){
				rawStr.append(select).append(",");
			}
			rawStr.deleteCharAt(rawStr.length() - 1);
		}else{
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		if(rightJoin == null){
			throw new QueryBuilderException(ReportingError.NOTHING_TO_SELECT);
		}
		// from
		rawStr.append(" from ").append(rightJoin.getJoinTable()).append(" ");
		// WHERE
		for (VerticaElement elem : inpParams.getMetaDataFilterList()) {
			if (elem.isMetadata()) {
				VerticaJoin join = elem.getMetadataVerticaJoin();
				if(joinMap.get(join.getJoinTable())==null || join.getJoinType().equals(OuterJoin.RIGHT)){
					joinMap.put(join.getJoinTable(), join);
				}
				whereByStr.append(" "+elem.getMetadataWhereByStr() + " and");
			}
		}
		joinMap.remove(rightJoin.getJoinTable());
		// join
		rawStr.append(getJoinStr(joinMap));

		if(StringUtils.isNotBlank(whereByStr)){
			// WHERE
			rawStr.append(" where ").append(whereByStr);
			// removing extra and
			rawStr.delete(rawStr.length() - 3, rawStr.length());
		}
		return rawStr.toString();
	}
	
	private String getJoinStr(Map<String, VerticaJoin> joinMap) {
		List<String> originalTableJoins = new ArrayList<String>();
		List<String> dependentTableJoins = new ArrayList<String>();
		StringBuilder joinStr = new StringBuilder("");
		for (VerticaJoin join : joinMap.values()) {
			if(join.getJoinTable()!=null && !join.getJoinTable().trim().contains(" ")){
				originalTableJoins.add(join.getJoinQueryStr());
			}else{
				dependentTableJoins.add(join.getJoinQueryStr());
			}
		}
		Collections.sort(originalTableJoins, Collections.reverseOrder());
		for(String str : originalTableJoins){
			joinStr.append(" ").append(str);
		}
		for(String str : dependentTableJoins){
			joinStr.append(" ").append(str);
		}
		return joinStr.toString();
	}

	public String getSelectsFinalQueryStr() {
		return selectsFinalQueryStr;
	}

	public String getLimitOffsetStr() {
		return limitOffsetStr;
	}

	public String getSelectsCSVFinalQueryStr() {
		return selectsCSVFinalQueryStr;
	}

	public String getFilterValuesQueryStr() {
		return filterValuesQueryStr;
	}

	public String getFilterMetadataQueryStr() {
		return filterMetadataQueryStr;
	}

	public String getGroupByQueryStr() {
		return groupByQueryStr;
	}

	public String getOrderbyQueryStr() {
		return orderbyQueryStr;
	}

	public String getJoinQueryStr() {
		return joinQueryStr;
	}

	public String getSelectsReportingOuterQueryStr() {
		return selectsReportingOuterQueryStr;
	}
	
	public String getFilterTempQueryStr() {
		return filterTempQueryStr;
	}

	public String getSelectTempQueryStr() {
		return selectTempQueryStr;
	}
}
