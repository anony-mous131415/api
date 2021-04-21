package com.komli.prime.service.reporting.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.komli.prime.service.reporting.pojo.VerticaElement;

public class SLJsonReader {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SLJsonReader.class);

	public static HashMap<String, VerticaElement> getVerticaElementMapping(
			String path) {
		HashMap<String, VerticaElement> result = new HashMap<String, VerticaElement>();
		try {
			String dirPath = path + "conf/querybuilder";
			File dir = new File(dirPath);
			if (dir.exists() && dir.isDirectory()) {
				for (File file : dir.listFiles())
					if (!file.isDirectory() && file.getName().endsWith(".json")) {
						addVerticaElements(result, file);

					}
			} else {
				throw new RuntimeException(dirPath
						+ " conf directory doesn't exist.");
			}
			if (result.size() == 0) {
				throw new RuntimeException(
						"No mapping found in conf json or mapping json doesn't exist in directory "
								+ dirPath);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, VerticaElement> addVerticaElements(
			HashMap<String, VerticaElement> result, File jsonFile) {
		LOGGER.info("Reading json file : " + jsonFile.getAbsolutePath());
		try {
			List<HashMap<String, Object>> elements = new ArrayList<HashMap<String, Object>>();
			InputStream stream = new FileInputStream(jsonFile);
			ObjectMapper mapper = new ObjectMapper();
			elements = mapper.readValue(stream, elements.getClass());
			stream.close();
			for (HashMap<String, Object> rs : elements) {
				String elementId = (String) rs.get("elementId");
				int element_type = (Integer) rs.get("elementType");
				String managedSelectQueryStr = (String) rs.get("rdbSelect");
				String rtbSelectQueryStr = (String) rs.get("rtbSelect");
				String dependencyParamStr = (String) rs.get("dependency");
				String outerSelectQueryStr = (String) rs.get("outerSelect");
				String displayStr = (String) rs.get("display");
				String exportSelectQueryStr = (String) rs.get("exportSelect");
				int isKey = (Integer) rs.get("isKey");
				int isMetaData = (Integer) rs.get("isMetaData");
				String metadataSelectQueryStr = (String) rs
						.get("metadataSelect");
				String metadataTable = (String) rs.get("metadataTable");
				String metadataJoinClause = (String) rs
						.get("metadataJoinclause");
				String reportingEquivalent = (String) rs
						.get("reportingEquivalent");
				String audienceSelectQueryStr = (String) rs
						.get("audienceSelect");
				String bidfunnelSelectQueryStr = (String) rs.get("bidSelect");
				String unqUserSelectQueryStr = (String) rs.get("unqUserSelect");
				String paramConvReportSelectQueryStr = (String) rs.get("convReportSelect");
				if (result.containsKey(elementId))
					LOGGER.warn("Replacing {"
							+ elementId
							+ "} already found from  one of the file processed earlier with entry in "
							+ jsonFile.getName());
				else
					LOGGER.info("Adding {" + elementId + "} from "
							+ jsonFile.getName());
				result.put(elementId, new VerticaElement(elementId,
						element_type, managedSelectQueryStr, rtbSelectQueryStr,
						outerSelectQueryStr, metadataSelectQueryStr,
						exportSelectQueryStr, dependencyParamStr,
						metadataTable, metadataJoinClause, isKey == 1,
						isMetaData == 1, displayStr, reportingEquivalent,
						audienceSelectQueryStr, bidfunnelSelectQueryStr,
						unqUserSelectQueryStr, paramConvReportSelectQueryStr));
				if (0 == isKey) {
					result.put("min(" + elementId + ")", new VerticaElement(
							"min(" + elementId + ")", element_type,
							managedSelectQueryStr, rtbSelectQueryStr,
							outerSelectQueryStr, metadataSelectQueryStr,
							exportSelectQueryStr, dependencyParamStr,
							metadataTable, metadataJoinClause, isKey == 1,
							isMetaData == 1, "min" + displayStr ,
							reportingEquivalent, audienceSelectQueryStr,
							bidfunnelSelectQueryStr, unqUserSelectQueryStr, paramConvReportSelectQueryStr));
					result.put("max(" + elementId + ")", new VerticaElement(
							"max(" + elementId + ")", element_type,
							managedSelectQueryStr, rtbSelectQueryStr,
							outerSelectQueryStr, metadataSelectQueryStr,
							exportSelectQueryStr, dependencyParamStr,
							metadataTable, metadataJoinClause, isKey == 1,
							isMetaData == 1, "max" + displayStr ,
							reportingEquivalent, audienceSelectQueryStr,
							bidfunnelSelectQueryStr, unqUserSelectQueryStr, paramConvReportSelectQueryStr));
				}
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
