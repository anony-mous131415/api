
package io.revx.core.model;

@SuppressWarnings("serial")
public class BaseModelWithModifiedTime extends BaseModel {

  public Long modifiedTime;
  public Long modifiedBy;

  public BaseModelWithModifiedTime() {
    super();

  }

  public BaseModelWithModifiedTime(long id, String name) {
    super(id, name);
  }

  public BaseModelWithModifiedTime(Long modifiedTime, Long modifiedBy) {
    super();
    this.modifiedTime = modifiedTime;
    this.modifiedBy = modifiedBy;
  }

  public void populateIdAndNameAndModifiedTime(BaseModelWithModifiedTime model) {
    if (model != null) {
      this.id = model.getId();
      this.name = model.getName();
      this.modifiedTime = model.getModifiedTime();
    }
  }

  public BaseModelWithModifiedTime(long id, String name, long modifiedTime) {
    super(id, name);
    this.modifiedTime = modifiedTime;
  }

  public Long getModifiedTime() {
    return modifiedTime;
  }

  public void setModifiedTime(Long modifiedTime) {
    this.modifiedTime = modifiedTime;
  }

  public Long getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(Long modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  @Override
  public String toString() {
    return "BaseModelWithModifiedTime [modifiedTime=" + modifiedTime + ", modifiedBy=" + modifiedBy
        + ", toString()=" + super.toString() + "]";
  }

 

}
