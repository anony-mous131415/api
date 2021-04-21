package io.revx.core.model.creative;

import io.revx.core.enums.TemplateVariableType;

public class TemplateVariablesDTO {

    private Long variableId;
    private String variableTitle;
    private String variableKey;
    private TemplateVariableType variableType;
    private String elasticSearchIndex;
    private Boolean isActive;
    private String hintMessage;
    private String numberRange;

    public Long getVariableId() {
        return variableId;
    }

    public void setVariableId(Long variableId) {
        this.variableId = variableId;
    }

    public String getVariableTitle() {
        return variableTitle;
    }

    public void setVariableTitle(String variableTitle) {
        this.variableTitle = variableTitle;
    }

    public String getVariableKey() {
        return variableKey;
    }

    public void setVariableKey(String variableKey) {
        this.variableKey = variableKey;
    }

    public TemplateVariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(TemplateVariableType variableType) {
        this.variableType = variableType;
    }

    public String getElasticSearchIndex() {
        return elasticSearchIndex;
    }

    public void setElasticSearchIndex(String elasticSearchIndex) {
        this.elasticSearchIndex = elasticSearchIndex;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getHintMessage() {
        return hintMessage;
    }

    public void setHintMessage(String hintMessage) {
        this.hintMessage = hintMessage;
    }

    public String getNumberRange() {
        return numberRange;
    }

    public void setNumberRange(String numberRange) {
        this.numberRange = numberRange;
    }

    @Override
    public String toString() {
        return "TemplateVariablesDTO{" +
                "variableId=" + variableId +
                ", variableTitle='" + variableTitle + '\'' +
                ", variableKey='" + variableKey + '\'' +
                ", variableType=" + variableType +
                ", elasticSearchIndex='" + elasticSearchIndex + '\'' +
                ", isActive=" + isActive +
                ", hintMessage='" + hintMessage + '\'' +
                ", numberRange='" + numberRange + '\'' +
                '}';
    }
}
