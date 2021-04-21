package io.revx.api.reportbuilder;

import io.revx.api.reportbuilder.redshift.ReportConstants;
import io.revx.core.enums.reporting.CurrencyOf;
import io.revx.core.enums.reporting.DBType;
import io.revx.core.enums.reporting.Entity;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.reporting.ReportProperty;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ReportBuilderUtil {

	private static final Logger logger = LogManager.getLogger(ReportBuilderUtil.class);

	private Map<String, ReportProperty> rtbReportProperties = null;
	private Map<String, ReportProperty> conversionReportProperties = null;

	// TODO: read from prop file
	List<String> candidates = new ArrayList<>(
			Arrays.asList("revenue", "roi", "spend", "margin", "publisher_ecpm", "publisher_ecpc", "publisher_ecpa",
					"txn_amount", "click_txn_amount", "view_txn_amount", "advertiser_ecpm"));

	public List<String> modifyColumnsBasedOnCurrency(List<String> columns, CurrencyOf currencyOf) {
		for (int index = 0; index < columns.size(); index++) {
			String col = columns.get(index);
			if (candidates.contains(col)) {
				columns.set(index, CurrencyOf.addCurrencyDependency(col, currencyOf));
			}
		}

		return columns;
	}

	public String modifyColumnBasedOnCurrency(String column, CurrencyOf currencyOf) {
		if (candidates.contains(column)) {
			return CurrencyOf.addCurrencyDependency(column, currencyOf);
		} else {
			return column;
		}
	}

	public Map<String, ReportProperty> readReportConfigProperties(DBType dbType, String entity)
			throws ValidationException {
		Map<String, ReportProperty> reportProperties = null;
		Entity reportEntity = getReportEntity(entity);
		String fileName = getConfigFileNameBasedOnDBType(dbType, reportEntity);

		if (fileName != null) {
			try {
				reportProperties = getReportProperties(reportEntity,fileName);
			} catch (JsonParseException e) {
				logger.error("Invalid JSON format in {} : {}", fileName, ExceptionUtils.getFullStackTrace(e));
				throw new ValidationException(ErrorCode.INVALID_JSON_FORMAT);
			} catch (JsonMappingException e) {
				logger.error("Error while mapping to ReportProperty object : {}", ExceptionUtils.getFullStackTrace(e));
				throw new ValidationException(ErrorCode.UNKNOWN_ATTRIBUTE_IN_JSON);
			} catch (IOException e) {
				logger.error("File Error : {}", ExceptionUtils.getFullStackTrace(e));
				throw new ValidationException(ErrorCode.FILE_NOT_FOUND);
			}
		} else {
			throw new ValidationException(ErrorCode.INVALID_ENTITY);
		}

		return reportProperties;
	}

	private Map<String, ReportProperty> getReportProperties(Entity entity, String fileName) throws IOException {
		if (entity == Entity.RTB) {
			synchronized (this) {
				if (rtbReportProperties == null) {
					rtbReportProperties = ReportProperty.getProperties(fileName);
				}
				return rtbReportProperties;
			}
		} else if (entity == Entity.CONVERSION_REPORT){
			synchronized (this) {
				if (conversionReportProperties == null) {
					conversionReportProperties = ReportProperty.getProperties(fileName);
				}
				return conversionReportProperties;
			}
		}
		return null;
	}

	private Entity getReportEntity(String entity) {
		if (entity.equalsIgnoreCase(Entity.RTB.getEntity())) {
			return Entity.RTB;
		} else if (entity.equalsIgnoreCase(Entity.CONVERSION_REPORT.getEntity())) {
			return Entity.CONVERSION_REPORT;
		} else {
			return Entity.RTB;
		}
	}

	private String getConfigFileNameBasedOnDBType(DBType dbType, Entity entity) {
		String fileName = null;
		switch (dbType) {
		case REDSHIFT:
			fileName = getRedShiftConfigFileName(entity);
			break;
		case ELASTIC:
			fileName = getElasticConfigFileName(entity);
			break;
		default:
			break;
		}

		return fileName;
	}

	private String getRedShiftConfigFileName(Entity entity) {
		String fileName;
		switch (entity) {
		case RTB:
			fileName = ReportConstants.CONFIG_FILE_RTB;
			break;
		case CONVERSION_REPORT:
			fileName = ReportConstants.CONFIG_FILE_CONVERSION;
			break;
		default:
			fileName = null;
			break;
		}

		return fileName;
	}

	private String getElasticConfigFileName(Entity entity) {
		String fileName;
		switch (entity) {
		case RTB:
		case CONVERSION_REPORT:
		default:
			fileName = null;
			break;
		}

		return fileName;
	}

}
