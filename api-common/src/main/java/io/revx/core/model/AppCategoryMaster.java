package io.revx.core.model;

public class AppCategoryMaster extends BaseModel {

  private String categoryName;


  private Integer osId;

  private Integer parentId;



  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }


  public Integer getOsId() {
    return osId;
  }

  public void setOsId(Integer osId) {
    this.osId = osId;
  }

  public Integer getParentId() {
    return parentId;
  }

  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  public AppCategoryMaster() {
    super();
  }

}
