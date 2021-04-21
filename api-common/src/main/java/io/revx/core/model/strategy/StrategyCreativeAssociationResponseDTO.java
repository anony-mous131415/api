package io.revx.core.model.strategy;

import java.util.List;
import io.revx.core.model.BaseModel;

public class StrategyCreativeAssociationResponseDTO {

  public String operationStatus;

  public List<CreativeStrategyAssociationStatus> creatives;

  public class CreativeStrategyAssociationStatus {

    public long id;

    public String name;

    public List<BaseModel> successfulStrategies;

    public List<BaseModel> failedStrategies;
  }
}
