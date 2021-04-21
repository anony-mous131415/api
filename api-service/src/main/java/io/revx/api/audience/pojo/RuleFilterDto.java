package io.revx.api.audience.pojo;

public class RuleFilterDto {

  private static final long serialVersionUID = 1L;

  private Long id;
  
  private String name;
  
  private String displayName;
  
  private String fbxName;
  
  private RuleFilterType filterType;
  
  private RuleValueType valueType;
  
  public RuleFilterDto() {

  }

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

  public String getDisplayName() {
      return displayName;
  }

  public void setDisplayName(String displayName) {
      this.displayName = displayName;
  }

  public String getFbxName() {
      return fbxName;
  }

  public void setFbxName(String fbxName) {
      this.fbxName = fbxName;
  }

  public RuleFilterType getFilterType() {
      return filterType;
  }

  public void setFilterType(RuleFilterType filterType) {
      this.filterType = filterType;
  }

  public RuleValueType getValueType() {
      return valueType;
  }

  public void setValueType(RuleValueType valueType) {
      this.valueType = valueType;
  }

  @Override
  public String toString() {
      return "RuleFilter [id=" + id + ", name=" + name + ", displayName="
              + displayName + ", fbxName=" + fbxName + ", filterType="
              + filterType + ", valueType=" + valueType + "]";
  }

}
