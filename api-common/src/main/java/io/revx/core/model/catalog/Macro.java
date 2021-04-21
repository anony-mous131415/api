package io.revx.core.model.catalog;

import java.util.List;
import io.revx.core.enums.MacroType;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;

public class Macro extends BaseModel implements BaseEntity{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Long advertiserId;
  private String macroText;
  private List<?> samples;
  private Boolean isCustomMacro;
  private MacroType macroType;
  
  public Macro() {
    
  }
  
  @Override
  public Long getId() {
    return super.getId();
  }

  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public String getMacroText() {
    return macroText;
  }

  public void setMacroText(String macroText) {
    this.macroText = macroText;
  }

  public List<?> getSamples() {
    return samples;
  }

  public void setSamples(List<?> samples) {
    this.samples = samples;
  }

  public Boolean getIsCustomMacro() {
    return isCustomMacro;
  }

  public void setIsCustomMacro(Boolean isCustomMacro) {
    this.isCustomMacro = isCustomMacro;
  }

  public MacroType getMacroType() {
    return macroType;
  }

  public void setMacroType(MacroType macroType) {
    this.macroType = macroType;
  }



}
