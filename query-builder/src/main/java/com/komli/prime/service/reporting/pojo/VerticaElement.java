package com.komli.prime.service.reporting.pojo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.exceptions.QueryBuilderException;
import com.komli.prime.service.reporting.exceptions.ReportGeneratingException.ReportingError;
import com.komli.prime.service.reporting.querybuilder.pojo.FilterParameter.CLAUSES;
import com.komli.prime.service.reporting.querybuilder.pojo.OrderByParameter.Order;

public class VerticaElement extends BaseElement {

	private static final Logger logger = LoggerFactory
			.getLogger(VerticaElement.class);

	private String managedSelectQueryStr;
	private String rtbSelectQueryStr;
	private String outerSelectQueryStr;

	private String exportSelectQueryStr;

	private String metadataSelectQueryStr;
	private String metadataTable;
	private String metadataJoinClause;

	private boolean managedElement = false;
	private boolean rtbElement = false;
	private boolean isMetadata = false;
	private boolean isReporting = false;
	private boolean isDependentReporting = false;

	private CLAUSES clause;
	private String whereByClauseValue;

	private Order order;

	private OuterJoin joinType = OuterJoin.LEFT;

	private String dependencyParamStr;
	private List<String> dependencyParams = new ArrayList<String>();
	private boolean dependencyOnOtherParams = false;

	private boolean isKey = false;

	private String reportingEquivalent;
	private boolean hasReportingEquivalent;

	// audience
	private boolean audienceElement = false;
	private String audienceSelectQueryStr;

	// bidfunnel
	private boolean bidfunnelElement = false;
	private String bidfunnelSelectQueryStr;

	// unqUser
	private boolean unqUserElement = false;
	private String unqUserSelectQueryStr;
	
	// paramConvReport
	private boolean paramConvReportElement = false;
	private String paramConvReportSelectQueryStr;

	@Override
	public VerticaElement getCopy() {
		return new VerticaElement(getElementId(), getType(),
				managedSelectQueryStr, rtbSelectQueryStr, outerSelectQueryStr,
				metadataSelectQueryStr, exportSelectQueryStr,
				dependencyParamStr, metadataTable, metadataJoinClause, isKey,
				isMetadata, getDisplayStr(), reportingEquivalent,
				audienceSelectQueryStr, bidfunnelSelectQueryStr,
				unqUserSelectQueryStr, paramConvReportSelectQueryStr);
	}

	public VerticaElement(String elementId, int type,
			String managedSelectQueryStr, String rtbSelectQueryStr,
			String outerSelectQueryStr, String metadataSelectQueryStr,
			String exportSelectQueryStr, String dependencyParamStr,
			String metadataTable, String metadataJoinClause, boolean isKey,
			boolean isMetaData, String displayStr, String reportingEquivalent,
			String audienceSelectQueryStr, String bidfunnelSelectQueryStr,
			String unqUserSelectQueryStr, String paramConvReportSelectQueryStr) {
		super(elementId, type, displayStr);
		this.isKey = isKey;
		this.isMetadata = isMetaData;
		if (this.isMetadata) {
			this.isKey = true;
		}
		this.managedSelectQueryStr = managedSelectQueryStr;
		if (StringUtils.isNotBlank(managedSelectQueryStr)) {
			managedElement = true;
		}
		this.rtbSelectQueryStr = rtbSelectQueryStr;
		if (StringUtils.isNotBlank(rtbSelectQueryStr)) {
			rtbElement = true;
		}
		this.audienceSelectQueryStr = audienceSelectQueryStr;
		if (StringUtils.isNotBlank(audienceSelectQueryStr)) {
			audienceElement = true;
		}
		this.bidfunnelSelectQueryStr = bidfunnelSelectQueryStr;
		if (StringUtils.isNotBlank(bidfunnelSelectQueryStr)) {
			bidfunnelElement = true;
		}

		this.unqUserSelectQueryStr = unqUserSelectQueryStr;
		if (StringUtils.isNotBlank(unqUserSelectQueryStr)) {
			unqUserElement = true;
		}
		
		this.paramConvReportSelectQueryStr = paramConvReportSelectQueryStr;
		if (StringUtils.isNotBlank(paramConvReportSelectQueryStr)) {
			paramConvReportElement = true;
		}

		if (!isMetaData
				&& (managedElement || rtbElement || audienceElement
						|| bidfunnelElement || unqUserElement || paramConvReportElement)) {
			isReporting = true;
		}

		if (!isMetaData && !isReporting) {
			isDependentReporting = true;
		}

		this.outerSelectQueryStr = getElementId();
		if (StringUtils.isNotBlank(outerSelectQueryStr)) {
			this.outerSelectQueryStr = outerSelectQueryStr;
		}

		this.exportSelectQueryStr = getElementId();
		if (StringUtils.isNotBlank(exportSelectQueryStr)) {
			this.exportSelectQueryStr = exportSelectQueryStr;
		}

		this.metadataSelectQueryStr = metadataSelectQueryStr;
		this.metadataJoinClause = metadataJoinClause;
		this.metadataTable = metadataTable;

		if (StringUtils.isNotBlank(reportingEquivalent)) {
			this.reportingEquivalent = reportingEquivalent.trim();
			this.hasReportingEquivalent = true;
		}

		setDependencyParams(dependencyParamStr);
	}

	public void setWhereClauseParams(CLAUSES clause, String value) {
		this.clause = clause;
		this.whereByClauseValue = value;

	}

	public String getManagedSelectStr() throws QueryBuilderException {
		checkIfManaged();
		return managedSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getRtbSelectStr() throws QueryBuilderException {
		checkIfRtb();
		return rtbSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getAudienceSelectStr() throws QueryBuilderException {
		checkIfAudience();
		return audienceSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getBidFunnelSelectStr() throws QueryBuilderException {
		checkIfBidfunnel();
		return bidfunnelSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getUnqUserSelectStr() throws QueryBuilderException {
		checkIfUnqUser();
		return unqUserSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}
	
	public String getParamConvReportSelectStr() throws QueryBuilderException {
		checkIfparamConvReport();
		return paramConvReportSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getMetadataSelectStr() throws QueryBuilderException {
		return metadataSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getOuterSelectStr() {
		return outerSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getReportingOuterSelectStr() throws QueryBuilderException {
		return outerSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public String getManagedGroupBy() throws QueryBuilderException {
		checkIfManaged();
		return getElementId();
	}

	public String getRtbGroupBy() throws QueryBuilderException {
		checkIfRtb();
		return getElementId();
	}

	public String getAudienceGroupBy() throws QueryBuilderException {
		checkIfAudience();
		return getElementId();
	}

	public String getBidfunnelGroupBy() throws QueryBuilderException {
		checkIfBidfunnel();
		return getElementId();
	}

	public String getUnqUserGroupBy() throws QueryBuilderException {
		checkIfUnqUser();
		return getElementId();
	}

	public String getGroupBy() {
		return getElementId();
	}

	public String getOrderBy() {
		return getElementId();
	}

	public String getManagedWhereByStr() throws QueryBuilderException {
		checkIfManaged();
		return getWhereByStr(managedSelectQueryStr);
	}

	public String getRtbWhereByStr() throws QueryBuilderException {
		checkIfRtb();
		return getWhereByStr(rtbSelectQueryStr);
	}

	public String getAudienceWhereByStr() throws QueryBuilderException {
		checkIfAudience();
		return getWhereByStr(audienceSelectQueryStr);
	}

	public String getBidfunnelWhereByStr() throws QueryBuilderException {
		checkIfBidfunnel();
		return getWhereByStr(bidfunnelSelectQueryStr);
	}

	public String getUnqUserWhereByStr() throws QueryBuilderException {
		checkIfUnqUser();
		return getWhereByStr(unqUserSelectQueryStr);
	}
	
	public String getParamConvReportWhereByStr() throws QueryBuilderException {
		checkIfparamConvReport();
		return getWhereByStr(paramConvReportSelectQueryStr);
	}

	public String getReportingOuterWhereByStr() throws QueryBuilderException {
		return getWhereByStr("(" + outerSelectQueryStr + ")");
	}

	public String getMetadataWhereByStr() throws QueryBuilderException {
		return getWhereByStr("(" + metadataSelectQueryStr + ")");
	}

	public String getOuterValueWhereByStr() throws QueryBuilderException {
		return getWhereByStr(getElementId());
	}

	public String getTempWhereByStr() throws QueryBuilderException {
		return getWhereByStr(getElementId());
	}

	public String getOrderByStr() throws QueryBuilderException {
		if (order == null) {
			logger.warn("Order is not specified for the element: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_PROPER_ORDERBY);
		}
		return order.getOrderByStr(getElementId(), getType());
	}

	public VerticaJoin getMetadataVerticaJoin() throws QueryBuilderException {
		return new VerticaJoin(metadataTable, joinType, metadataJoinClause);
	}

	public boolean isDependencyOnOtherParams() {
		return dependencyOnOtherParams;
	}

	public void setDependencyOnOtherParams(boolean depenencyOnOtherParams) {
		this.dependencyOnOtherParams = depenencyOnOtherParams;
	}

	public String getFinalSelectQueryStr() throws QueryBuilderException {
		logger.info("Vertica Elem getElem: "+getElementId()+" ,metadataSelectQueryStr: "+metadataSelectQueryStr);
		if (!isMetadata)
			return getElementId() + " AS " + getElementId().replaceAll("[()]","");
		else
			return metadataSelectQueryStr + " AS " + getElementId().replaceAll("[()]","");
	}

	public void setOuterSelectQueryStr(String outerSelectQueryStr) {
		this.outerSelectQueryStr = outerSelectQueryStr;
	}

	public List<String> getDependencyParams() {
		return dependencyParams;
	}

	public boolean isManagedElement() {
		return managedElement;
	}

	public boolean isRtbElement() {
		return rtbElement;
	}

	public boolean isAudienceElement() {
		return audienceElement;
	}

	public boolean isBidfunnelElement() {
		return bidfunnelElement;
	}

	public boolean isUnqUserElement() {
		return unqUserElement;
	}
	
	public boolean isParamConvReportElement() {
		return paramConvReportElement;
	}

	public void setOrderByParams(Order order) {
		this.order = order;

	}

	private void checkIfManaged() throws QueryBuilderException {
		if (!isManagedElement()) {
			logger.warn("Element doesn't belong to Managed Schema: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_COLUMN_RDB);
		}
	}

	private void checkIfRtb() throws QueryBuilderException {
		if (!isRtbElement()) {
			logger.warn("Element doesn't belong to RTB Schema: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_COLUMN_RTB);
		}
	}

	private void checkIfAudience() throws QueryBuilderException {
		if (!isAudienceElement()) {
			logger.warn("Element doesn't belong to Audience Schema: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_COLUMN_AUDIENCE);
		}
	}

	private void checkIfBidfunnel() throws QueryBuilderException {
		if (!isBidfunnelElement()) {
			logger.warn("Element doesn't belong to Bid Funnel Schema: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_COLUMN_BIDFUNNEL);
		}
	}

	private void checkIfUnqUser() throws QueryBuilderException {
		if (!isUnqUserElement()) {
			logger.warn("Element doesn't belong to Unique User Schema: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_COLUMN_UNQUSER);
		}
	}
	
	private void checkIfparamConvReport() throws QueryBuilderException {
		if (!isParamConvReportElement()) {
			logger.warn("Element doesn't belong to Param_Conv_Report Schema: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_COLUMN_PARAM_CONV_REPORT);
		}
	}

	public boolean isKey() {
		return isKey;
	}

	public boolean isReporting() {
		return isReporting;
	}

	public boolean isDependentReporting() {
		return isDependentReporting;
	}

	public boolean isMetadata() {
		return isMetadata;
	}

	private void setDependencyParams(String params) {
		this.dependencyParamStr = params;
		if (StringUtils.isNotBlank(params)) {
			List<String> paramList = new ArrayList<String>();
			for (String tmp : params.split(",")) {
				if (StringUtils.isNotBlank(tmp)) {
					paramList.add(tmp);
				}
			}
			if (paramList.size() > 0) {
				dependencyParams = paramList;
				dependencyOnOtherParams = true;
			}
		}

	}

	private String getWhereByStr(String queryStr) throws QueryBuilderException {
		if (clause == null || whereByClauseValue == null) {
			logger.warn("Filter parameters are not specified for the element: "
					+ getElementId());
			throw new QueryBuilderException(ReportingError.NOT_PROPER_FILTER);

		}
		return clause.getClauseStr(queryStr, whereByClauseValue, getType(),
				getElementId());
	}

	public void modifyJoinType(boolean priority) {
		if (priority)
			joinType = OuterJoin.RIGHT;
		else
			joinType = OuterJoin.LEFT;

	}

	public class VerticaJoin {
		private String joinTable;
		private OuterJoin joinType = OuterJoin.LEFT;
		private String clause;

		public VerticaJoin(String joinTable, OuterJoin joinType, String clause) {
			this.joinTable = joinTable;
			this.joinType = joinType;
			this.clause = clause;
		}

		public OuterJoin getJoinType() {
			return joinType;
		}

		public String getJoinTable() {
			return joinTable;
		}

		public String getJoinQueryStr() {
			StringBuilder joinStr = new StringBuilder();
			joinStr.append(joinType.getJoinStr()).append(" join ")
					.append(joinTable).append(" on ").append(clause);
			return joinStr.toString();
		}
	}

	public enum OuterJoin {
		LEFT("left"), RIGHT("right");
		private OuterJoin(String join) {
			this.join = join;
		}

		public String getJoinStr() {
			return join;
		}

		String join;
	}

	public String getOuterSelectCSVStr() {
		return exportSelectQueryStr + " AS " + getDisplayStr() + "";
	}

	public String getReportingEquivalent() {
		return reportingEquivalent;
	}

	public boolean hasReportingEquivalent() {
		return hasReportingEquivalent;
	}

	@Override
	public String toString() {
		return "VerticaElement [managedSelectQueryStr=" + managedSelectQueryStr
				+ ", rtbSelectQueryStr=" + rtbSelectQueryStr
				+ ", outerSelectQueryStr=" + outerSelectQueryStr
				+ ", exportSelectQueryStr=" + exportSelectQueryStr
				+ ", metadataSelectQueryStr=" + metadataSelectQueryStr
				+ ", metadataTable=" + metadataTable + ", metadataJoinClause="
				+ metadataJoinClause + ", managedElement=" + managedElement
				+ ", rtbElement=" + rtbElement + ", isMetadata=" + isMetadata
				+ ", isReporting=" + isReporting + ", isDependentReporting="
				+ isDependentReporting + ", clause=" + clause
				+ ", whereByClauseValue=" + whereByClauseValue + ", order="
				+ order + ", joinType=" + joinType + ", dependencyParamStr="
				+ dependencyParamStr + ", dependencyParams=" + dependencyParams
				+ ", dependencyOnOtherParams=" + dependencyOnOtherParams
				+ ", isKey=" + isKey + ", reportingEquivalent="
				+ reportingEquivalent + ", hasReportingEquivalent="
				+ hasReportingEquivalent + ", audienceElement="
				+ audienceElement + ", audienceSelectQueryStr="
				+ audienceSelectQueryStr + ", bidfunnelElement="
				+ bidfunnelElement + ", bidfunnelSelectQueryStr="
				+ bidfunnelSelectQueryStr + ", unqUserElement="
				+ unqUserElement + ", unqUserSelectQueryStr="
				+ unqUserSelectQueryStr + ", paramConvReportElement="
				+ paramConvReportElement + ", paramConvReportSelectQueryStr="
				+ paramConvReportSelectQueryStr + "]";
	}
}
