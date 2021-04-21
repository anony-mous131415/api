package io.revx.core.model.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import io.revx.core.model.targetting.TargetingComponentDTO;

public class StrategyTargetingDetails {

  public long id;

  public BigDecimal strategyBudget;

  public long strategyEndDate;

  public long campaignId;

  public BigDecimal campaignBudget;

  public long campaignEndDate;

  public String geoExpression;

  public List<TargetingComponentDTO> geoTCList;

  public StrategyTargetingDetails() {
    this.id = new Integer(0);
    this.geoExpression = new String("");
    this.geoTCList = new ArrayList<TargetingComponentDTO>();

  }

}
