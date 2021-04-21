
package io.revx.core.model;


@SuppressWarnings("serial")
public class ParentBasedObject extends StatusTimeModel {

  public ParentBasedObject() {

  }

  public ParentBasedObject(StatusTimeModel statusTimeModel) {
    super(statusTimeModel.getId(), statusTimeModel.getName());
    setActive(statusTimeModel.isActive());
    setModifiedTime(statusTimeModel.getModifiedTime());
    setCreationTime(statusTimeModel.getCreationTime());
  }

  private ParentBasedObject parent;

  public ParentBasedObject getParent() {
    return parent;
  }

  public void setParent(ParentBasedObject parent) {
    this.parent = parent;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ParentBasedObject [parent=");
    builder.append(parent);
    builder.append(", toString()=");
    builder.append(super.toString());
    builder.append("]");
    return builder.toString();
  }


}
