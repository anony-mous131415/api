package io.revx.querybuilder.enums;

public enum Interval {

  DAY("day", "cast((floor(timestamp/86400))*86400 as numeric)"), HOUR("hour",
      "cast((floor(timestamp/3600))*3600 as numeric) ");

  private String name;
  private String formula;

  private Interval(String text, String formula) {
    this.name = text;
    this.formula = formula;
  }

  public String getName() {
    return this.name;
  }

  public String getFormula() {
    return this.formula;
  }

  public static Interval fromString(String text) {
    if (text != null) {
      for (Interval b : Interval.values()) {
        if (text.equalsIgnoreCase(b.name)) {
          return b;
        }
      }
    }
    return null;
  }

}
