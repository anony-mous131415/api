package io.revx.core.model.reporting;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.revx.core.constant.Constants;
import io.revx.core.enums.reporting.PropertyType;

public class ReportProperty implements Serializable {

    private static final Logger logger = LogManager.getLogger(ReportProperty.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String key;
    private String column;
    private List<String> select;
    private List<String> outerSelect;
    private List<String> exportSelect;
    private String sortColumnName;
    private String exportSortColumnName;
    private List<String> join;
    private PropertyType type;
    private Map<String, String> dependents;

    public ReportProperty() {

    }

    public ReportProperty(String key, String column, List<String> select, List<String> outerSelect,
            List<String> exportSelect, String sortColumnName, String exportSortColumnName,
            List<String> join, PropertyType type, Map<String, String> dependents) {
        this.key = key;
        this.column = column;
        this.select = select;
        this.outerSelect = outerSelect;
        this.exportSelect = exportSelect;
        this.sortColumnName = sortColumnName;
        this.exportSortColumnName = exportSortColumnName;
        this.join = join;
        this.type = type;
        this.dependents = dependents;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public List<String> getSelect() {
        return select;
    }

    public void setSelect(List<String> select) {
        this.select = select;
    }

    public List<String> getOuterSelect() {
        return outerSelect;
    }

    public void setOuterSelect(List<String> outerSelect) {
        this.outerSelect = outerSelect;
    }

    public List<String> getExportSelect() {
        return exportSelect;
    }

    public void setExportSelect(List<String> exportSelect) {
        this.exportSelect = exportSelect;
    }

    public List<String> getJoin() {
        return join;
    }

    public void setJoin(List<String> join) {
        this.join = join;
    }

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
    }

    public Map<String, String> getDependents() {
        return dependents;
    }

    public void setDependents(Map<String, String> dependents) {
        this.dependents = dependents;
    }

    public String getSortColumnName() {
        return sortColumnName;
    }

    public void setSortColumnName(String sortColumnName) {
        this.sortColumnName = sortColumnName;
    }

    public String getExportSortColumnName() {
        return exportSortColumnName;
    }

    public void setExportSortColumnName(String exportSortColumnName) {
        this.exportSortColumnName = exportSortColumnName;
    }

    @Override
    public String toString() {
        return "ReportProperty{" +
                "key='" + key + '\'' +
                ", column='" + column + '\'' +
                ", select=" + select +
                ", outerSelect=" + outerSelect +
                ", exportSelect=" + exportSelect +
                ", sortColumnName='" + sortColumnName + '\'' +
                ", exportSortColumnName='" + exportSortColumnName + '\'' +
                ", join=" + join +
                ", type=" + type +
                ", dependents=" + dependents +
                '}';
    }

    public static Map<String, ReportProperty> getProperties(String fileName) throws IOException {
        Map<String, ReportProperty> properties = new HashMap<>();

        InputStream stream = new FileInputStream(Constants.REPORT_BUILDER_DIRECTORY + fileName);
        ObjectMapper mapper = new ObjectMapper();

        // TDB: read directly to a hashmap instead of list and then to hashmap
        List<ReportProperty> reportProperties = mapper.readValue(stream, new TypeReference<List<ReportProperty>>() {
        });
        reportProperties.forEach(rp -> properties.put(rp.getKey(), rp));

        stream.close();

        return properties;
    }

}
