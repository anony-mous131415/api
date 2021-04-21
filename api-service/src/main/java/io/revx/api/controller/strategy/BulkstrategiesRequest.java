package io.revx.api.controller.strategy;

import java.io.Serializable;
import java.util.ArrayList;
import io.revx.core.model.BaseModel;

public class BulkstrategiesRequest extends BaseModel implements Serializable {

  private static final long serialVersionUID = 1L;

  public Long startTimestamp;

  public Long endTimestamp;

  public ArrayList<Long> campaignIds;

  public String filePath;

}
