package io.revx.api.mysql.entity.creative;

import io.revx.core.enums.TemplateVariableType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CreativeTemplateVariables")
public class CreativeTemplateVariablesEntity {

    @Id
    @Column(name = "ctv_id")
    private long variableId;

    @Column(name = "ctv_title")
    private String variableTitle;

    @Column(name = "ctv_key")
    private String variableKey;

    @Column(name = "ctv_is_active", nullable = false, columnDefinition = "tinyint(1)")
    private boolean active;

    @Column(name = "ctv_type", columnDefinition = "ENUM", nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateVariableType variableType;

    @Column(name = "ctv_store_index")
    private String elasticSearchIndex;

    @Column(name = "ctv_hint_message")
    private String hintMessage;

    @Column(name = "ctv_number_range")
    private String numberRange;

    public long getVariableId() {
        return variableId;
    }

    public void setVariableId(long variableId) {
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
        return "CreativeTemplateVariablesEntity{" +
                "variableId=" + variableId +
                ", variableTitle='" + variableTitle + '\'' +
                ", variableKey='" + variableKey + '\'' +
                ", active=" + active +
                ", variableType=" + variableType +
                ", elasticSearchIndex='" + elasticSearchIndex + '\'' +
                ", hintMessage='" + hintMessage + '\'' +
                ", numberRange='" + numberRange + '\'' +
                '}';
    }

}
