package io.revx.core.model.creative;

public enum DcoAttributeType {
  flash(1), html(2), image(3);

  private int value;

  private DcoAttributeType(int value) {
    this.value = value;
  }

  public static DcoAttributeType get(int value) {
    switch (value) {
      case 1:
        return DcoAttributeType.flash;
      case 2:
        return DcoAttributeType.html;
      case 3:
        return DcoAttributeType.image;
      default:
        return null;
    }
  }

  public int getValue() {
    return value;
  }
}

