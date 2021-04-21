
package io.revx.core.model;

@SuppressWarnings("serial")
public class StatusBaseObject extends BaseModel {

  public boolean active;


  public StatusBaseObject() {
    super();
  }

  public StatusBaseObject(boolean active) {
    super();
    this.active = active;
  }

  public StatusBaseObject(long id, String name, boolean active) {
    super(id, name);
    this.active = active;
  }

  public StatusBaseObject(long id, String name) {
    super(id, name);
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("StatusBaseObject [active=").append(active).append(", id=").append(id)
        .append(", name=").append(name).append("]");
    return builder.toString();
  }

 

}
