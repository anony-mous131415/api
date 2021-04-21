package io.revx.api.reportbuilder.redshift;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FieldList;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.TableResult;
import io.revx.api.reportbuilder.ReportBuilderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.enums.reporting.CurrencyOf;
import io.revx.core.enums.reporting.PropertyType;
import io.revx.core.model.reporting.DurationModel;
import io.revx.core.model.reporting.ReportProperty;
import io.revx.core.model.reporting.ReportingRequest;
import io.revx.core.utils.StringUtils;

@Component
public class ReportResponseUtil {

    @Autowired
    ReportBuilderUtil reportBuilderUtil;

    ReportingRequest reportingRequest;

    Map<String, ReportProperty> reportProperties;

    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING
            + GraphiteConstants.REPORTING_FORMAT_RESPONSE)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.REPORTING + GraphiteConstants.REPORTING_FORMAT_RESPONSE)
    public List<Object> formatResponse(ReportingRequest reportingRequest,
                                       Map<String, ReportProperty> reportProperties, TableResult tableResult) {

        this.reportingRequest = reportingRequest;
        this.reportProperties = reportProperties;

        Gson gson = new Gson();
        List<Object> result = new ArrayList<>();
        List<String> ignoreKeys = new ArrayList<>();

        CurrencyOf currencyOf = reportingRequest.getCurrency_of();

        if (tableResult != null && tableResult.getTotalRows() > 0) {
            List<String> columnNames = getSchemaColumnName(tableResult);
            for (FieldValueList tableRow : tableResult.iterateAll()) {
                JsonObject element = new JsonObject();
                for (String columnName : columnNames) {
                    FieldValue fieldValue = tableRow.get(columnName);
                    if (!ignoreKeys.contains(columnName)) {
                        ReportProperty property = getReportProperty(
                                reportBuilderUtil.modifyColumnBasedOnCurrency(columnName, currencyOf));
                        if (property != null) {
                            PropertyType type = property.getType();
                            if (type == PropertyType.object) {
                                Map<String, String> dependents = property.getDependents();
                                JsonObject elemDependent = getDependentObject(dependents,  tableRow,
                                        ignoreKeys);
                                if (elemDependent != null) {
                                    element.add(getPropertyKey(property, currencyOf), elemDependent);
                                }
                            } else if (type == PropertyType.number) {
                                if (fieldValue != null && !fieldValue.isNull()) {

                                    element.addProperty(getPropertyKey(property, currencyOf),
                                            fieldValue.getNumericValue());
                                } else {
                                    element.addProperty(getPropertyKey(property, currencyOf), 0);
                                }
                            } else {
                                if (fieldValue != null && !fieldValue.isNull()) {

                                    element.addProperty(getPropertyKey(property, currencyOf),
                                            fieldValue.getStringValue());
                                } else {
                                    element.addProperty(getPropertyKey(property, currencyOf), "");
                                }

                            }
                        }
                    }
                }
                result.add(gson.fromJson(element, Object.class));
                ignoreKeys = new ArrayList<>();
            }
        }

        return result;
    }

    public String generateReportId(ReportingRequest reportingRequest) {

        return "" + reportingRequest.hashCode();
    }

    public String generateUniqueTempTableName(ReportingRequest reportingRequest) {

        return "temp_" + reportingRequest.getReport_id();
    }

    public String generateUniqueExportTableName(ReportingRequest reportingRequest) {

        return "export_" + reportingRequest.getReport_id();
    }

    public String getExportFileName(ReportingRequest reportingRequest) {
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);

        DurationModel duration = reportingRequest.getDuration();
        String startDate = null;
        String endDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");

        if (duration.getStart_timestamp() != null) {
            LocalDateTime start = LocalDateTime.ofEpochSecond(duration.getStart_timestamp(), 0, ZoneOffset.UTC);
            startDate = start.format(formatter);
        }

        if (duration.getEnd_timestamp() != null) {
            LocalDateTime end = LocalDateTime.ofEpochSecond(duration.getEnd_timestamp(), 0, ZoneOffset.UTC);
            endDate = end.format(formatter);
        }

        String dateStr = startDate;
        if (endDate!=null && !endDate.isEmpty()) {
            dateStr = dateStr + "_" + endDate;
        }

        return "report_" + reportingRequest.getEntityName() + "_" + dateStr + "_" + reportingRequest.getReport_id()
                + "_" + now + ".csv";
    }

    /**
     * private methods
     */
    private ReportProperty getReportProperty(String key) {
        ReportProperty property = reportProperties.get(key);
        if (property != null) {
            return property;
        }

        for (Entry<String, ReportProperty> entry : reportProperties.entrySet()) {
            String column = entry.getValue().getColumn();
            if (!StringUtils.isBlank(column) && column.equalsIgnoreCase(key)) {
                property = entry.getValue();
                break;
            }
        }
        return property;
    }

    private String getPropertyKey(ReportProperty property, CurrencyOf currencyOf) {
        if(reportingRequest.getEntityName().equals(ApiConstant.CONVERSION_REPORT_ENTITY)) {
            return property.getKey();
        }
        return CurrencyOf.removeCurrencyDependency(property.getKey(), currencyOf);
    }

    private JsonObject getDependentObject(Map<String, String> dependents,
                                          FieldValueList row, List<String> ignoreKeyList) {
        if (dependents != null) {
            JsonObject elemDependent = new JsonObject();

            for (Entry<String, String> dependent : dependents.entrySet()) {
                ignoreKeyList.add(dependent.getValue());
                FieldValue fieldValue = row.get(dependent.getValue());
                if (fieldValue != null) {
                    String value = (!fieldValue.isNull()) ? row.get(dependent.getValue()).getStringValue()
                            : "";
                    elemDependent.addProperty(dependent.getKey(), value);
                }
            }
            return elemDependent;
        }
        return null;
    }

    private List<String> getSchemaColumnName(TableResult result) {
        List<String> columnNames = new ArrayList<>();
        Schema tableSchema = result.getSchema();
        FieldList fields = tableSchema.getFields();
        for (Field field : fields) {
            columnNames.add(field.getName());
        }
        return columnNames;
    }
}
