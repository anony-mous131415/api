package io.revx.core.model.targetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.revx.core.model.BaseModel;

public class Segment extends BaseModel {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public boolean selectAllChildren;
  public List<Integer> includedSegments;
  public List<Integer> excludedSegments;
  public Map<Integer, Segment> partiallySelectedSegments;

  public Segment() {
    includedSegments = new ArrayList<Integer>();
    excludedSegments = new ArrayList<Integer>();
    partiallySelectedSegments = new HashMap<Integer, Segment>();
  }
}
