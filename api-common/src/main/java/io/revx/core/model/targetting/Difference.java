package io.revx.core.model.targetting;

public class Difference {
  public boolean different;
  public String oldValue;
  public String newValue;

  public boolean isDifferent() {
    return different;
  }

  public void setDifferent(boolean different) {
    this.different = different;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }

}
