package io.revx.api.controller.strategy;

import java.io.Serializable;
import java.util.ArrayList;

public class StrategyUpdateResponse implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String strategyId;

  private String campaignId;

  private String campaignName;

  private String strategyName;

  // private String rowJson;

  private ArrayList<String> message;

  private Boolean isUpdated;

  private Boolean noChange;


  public String getCampaignName() {
    return campaignName;
  }

  public void setCampaignName(String campaignName) {
    this.campaignName = campaignName;
  }

  public String getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(String strategyId) {
    this.strategyId = strategyId;
  }

  public String getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(String campaignId) {
    this.campaignId = campaignId;
  }

  public String getStrategyName() {
    return strategyName;
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }

  // public String getRowJson() {
  // return rowJson;
  // }
  //
  // public void setRowJson(String rowJson) {
  // this.rowJson = rowJson;
  // }

  public ArrayList<String> getMessage() {
    return message;
  }

  public void setMessage(ArrayList<String> message) {
    this.message = message;
  }

  public Boolean getIsUpdated() {
    return isUpdated;
  }

  public void setIsUpdated(Boolean isUpdated) {
    this.isUpdated = isUpdated;
  }

  public Boolean getNoChange() {
    return noChange;
  }

  public void setNoChange(Boolean noChange) {
    this.noChange = noChange;
  }

  public StrategyUpdateResponse() {
    super();
    // TODO Auto-generated constructor stub
  }

  public StrategyUpdateResponse(String strategyId, String campaignId, String campaignName,
      String strategyName, ArrayList<String> message, Boolean isUpdated, Boolean noChange) {
    super();
    this.strategyId = strategyId;
    this.campaignId = campaignId;
    this.campaignName = campaignName;
    this.strategyName = strategyName;
    // this.rowJson = rowJson;
    this.message = message;
    this.isUpdated = isUpdated;
    this.noChange = noChange;
  }


}
