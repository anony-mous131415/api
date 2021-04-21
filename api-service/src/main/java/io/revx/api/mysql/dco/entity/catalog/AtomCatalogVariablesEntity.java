package io.revx.api.mysql.dco.entity.catalog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.api.enums.CatalogVariableValueType;


@Entity
@Table(name = "AtomCatalogVariables")
public class AtomCatalogVariablesEntity implements java.io.Serializable{

	private static final long serialVersionUID = -5388622535718295830L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acv_id", nullable = false)
    private Long  id;

    @Column(name = "acv_name", length = 128, nullable = false)
    private String   name;
    
    @Column(name = "acv_description", length = 128)
    private String description;
    
    @Column(name = "acv_value_type", columnDefinition = "ENUM", nullable = false)
    @Enumerated(EnumType.STRING)
    private CatalogVariableValueType valueType;
    
    @Column(name = "acv_is_multivalued", columnDefinition = "BIT", length = 1, nullable = false)
    private Boolean isMultivalued;
    
    @Column(name = "acv_advertiser_vertical", length = 128, nullable = false)
    private String advertiserVertical;
    
    @Column(name = "acv_is_display_field", columnDefinition = "BIT", length = 1, nullable = false)
    private Boolean isDisplayField;
    
    @Column(name = "acv_macro_text", length = 128)
    private String macroText;
    
    @Column(name = "acv_is_pixel_param", columnDefinition = "BIT", length = 1)
    private Boolean isPixelParam;
    
    @Column(name = "acv_param_name", length = 50)
    private String paramName;

    public Long getId() {
      return id;
    }

    public void setId(Long id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public CatalogVariableValueType getValueType() {
      return valueType;
    }

    public void setValueType(CatalogVariableValueType valueType) {
      this.valueType = valueType;
    }

    public Boolean getIsMultivalued() {
      return isMultivalued;
    }

    public void setIsMultivalued(Boolean isMultivalued) {
      this.isMultivalued = isMultivalued;
    }

    public String getAdvertiserVertical() {
      return advertiserVertical;
    }

    public void setAdvertiserVertical(String advertiserVertical) {
      this.advertiserVertical = advertiserVertical;
    }

    public Boolean getIsDisplayField() {
      return isDisplayField;
    }

    public void setIsDisplayField(Boolean isDisplayField) {
      this.isDisplayField = isDisplayField;
    }

    public String getMacroText() {
      return macroText;
    }

    public void setMacroText(String macroText) {
      this.macroText = macroText;
    }

    public Boolean getIsPixelParam() {
      return isPixelParam;
    }

    public void setIsPixelParam(Boolean isPixelParam) {
      this.isPixelParam = isPixelParam;
    }

    public String getParamName() {
      return paramName;
    }

    public void setParamName(String paramName) {
      this.paramName = paramName;
    }

    @Override
    public String toString() {
      return "AtomCatalogVariables [id=" + id + ", name=" + name + ", description=" + description
          + ", valueType=" + valueType + ", isMultivalued=" + isMultivalued
          + ", advertiserVertical=" + advertiserVertical + ", isDisplayField=" + isDisplayField
          + ", macroText=" + macroText + ", isPixelParam=" + isPixelParam + ", paramName="
          + paramName + "]";
    }

}
