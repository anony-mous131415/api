package io.revx.api.controller.strategy;

import java.io.Serializable;
import java.util.ArrayList;

public class BulkstrategiesValidationResponse implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private String strategyId;

  private String campaignId;

  private String strategyName;

  private ArrayList<String> messages;

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

  public ArrayList<String> getMessages() {
    return messages;
  }

  public void setMessages(ArrayList<String> messages) {
    this.messages = messages;
  }

  public BulkstrategiesValidationResponse(String strategyId, String campaignId, String strategyName,
      ArrayList<String> messages) {
    super();
    this.strategyId = strategyId;
    this.campaignId = campaignId;
    this.strategyName = strategyName;
    this.messages = messages;
  }

  public BulkstrategiesValidationResponse() {
    super();
    // TODO Auto-generated constructor stub
  }

}
