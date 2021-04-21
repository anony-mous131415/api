package io.revx.api.audience.pojo;

public class RuleOperatorDto {

  private static final long serialVersionUID = 1L;
  
  private Long id;
  
  private String name;
  
  private String displayName;
  
  private String fbxName;
  
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

}
