package io.revx.core.model.catalog;

import java.util.List;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;

public class VariablesMappingDTO extends BaseModel implements BaseEntity {

  public VariablesMappingDTO() {}

  public Long feedId;
  public String standardVariable;
  public String description;
  public String variablePath;
  public List samples;
  public Long sampleSize;

  @Override
  public Long getId() {
    return super.getId();
  }

  public Long getFeedId() {
    return feedId;
  }

  public void setFeedId(Long feedId) {
    this.feedId = feedId;
  }

  public Long getSampleSize() {
    return sampleSize;
  }

  public void setSampleSize(Long sampleSize) {
    this.sampleSize = sampleSize;
  }

  public String getStandardVariable() {
    return standardVariable;
  }

  public void setStandardVariable(String standardVariable) {
    this.standardVariable = standardVariable;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getVariablePath() {
    return variablePath;
  }

  public void setVariablePath(String variablePath) {
    this.variablePath = variablePath;
  }

  public List getSamples() {
    return samples;
  }

  public void setSamples(List samples) {
    this.samples = samples;
  }



  @Override
  public String toString() {
    return "VariablesMappingDTO [feedId=" + feedId + ", standardVariable=" + standardVariable
        + ", description=" + description + ", variablePath=" + variablePath + ", samples=" + samples
        + ", sampleSize=" + sampleSize + "]";
  }

}
