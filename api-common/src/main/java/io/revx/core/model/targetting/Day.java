
package io.revx.core.model.targetting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Day implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private Integer day;

  private List<Integer> hours;

  public Day() {
    hours = new ArrayList<Integer>();
  }

  public Day(Integer day, List<Integer> hours) {
    this.day = day;
    this.hours.addAll(hours);
  }

  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  public List<Integer> getHours() {
    return hours;
  }

  public void setHours(List<Integer> hours) {
    this.hours = hours;
  }

  public enum Days {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
  }

  public void setAllHoursForADay() {
    for (int i = 0; i < 24; i++) {
      this.hours.add(i);
    }
  }

  public void cleanUp() {
    this.hours.clear();
  }

  public String toString() {
    return "(Day: " + day + ", Hours: " + hours + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (!(o.getClass().equals(Day.class)))
      return false;
    return hashCode() == o.hashCode();
  }

  @Override
  public int hashCode() {
    int h = 0;
    h = h ^ day << 25; // bits 24,25,26 represent the day
    if (hours == null)
      return h;
    for (int i : hours) { // each bit from 0-23 represents an hour
      h = h ^ 1 << i;
    }
    return h;
  }
}
