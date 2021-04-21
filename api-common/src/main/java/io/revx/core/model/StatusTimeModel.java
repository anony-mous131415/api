
package io.revx.core.model;

@SuppressWarnings("serial")
public class StatusTimeModel extends BaseModelWithModifiedTime {

  protected boolean active;
  protected Long creationTime;
  protected Long createdBy;

  public StatusTimeModel() {
    super();
  }

  public StatusTimeModel(Long id, String name) {
    super(id, name);
  }

  public StatusTimeModel(boolean active, Long creationTime, Long createdBy) {
    super();
    this.active = active;
    this.creationTime = creationTime;
    this.createdBy = createdBy;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public Long getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Long creationTime) {
    this.creationTime = creationTime;
  }

  public Long getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Long createdBy) {
    this.createdBy = createdBy;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("StatusTimeModel [active=").append(active).append(", creationTime=")
        .append(creationTime).append(", createdBy=").append(createdBy).append(", modifiedTime=")
        .append(modifiedTime).append(", modifiedBy=").append(modifiedBy).append(", id=").append(id)
        .append(", name=").append(name).append("]");
    return builder.toString();
  }


}
