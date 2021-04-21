package io.revx.core.model.targetting;

public class Segments {

  public boolean selectAllSegments;

  public TargetingObject segments;

  public Segments() {
    selectAllSegments = false;
    segments = new TargetingObject();
  }
}
