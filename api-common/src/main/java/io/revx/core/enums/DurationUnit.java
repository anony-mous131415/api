package io.revx.core.enums;

public enum DurationUnit {

  MINUTE(1), HOUR(2), DAY(3), WEEK(4), MONTH(5);

  public final Integer id;

  private DurationUnit(int id) {
      this.id = id;
  }

  public static DurationUnit getById(Integer id) {
      for (DurationUnit type : values()) {
          if (type.id.equals(id))
              return type;
      }
      return null;
  }

  public String getText() {
      return this.name();
  }

  public static DurationUnit fromString(String text) {
      if (text != null) {
          for (DurationUnit b : DurationUnit.values()) {
              if (text.equalsIgnoreCase(b.getText())) {
                  return b;
              }
          }
      }
      return null;
  }

  public static int getRetentionDays(int duration, DurationUnit du) {
      switch(du) {
      case MINUTE: return (int) Math.ceil(((double)duration)/(24 * 60));
      case HOUR: return (int) Math.ceil(((double)duration)/24);
      case DAY: return duration;
      case WEEK: return (duration*7);
      case MONTH: return (duration*30);
      default: return 0;
      }
  }
  
  
}
