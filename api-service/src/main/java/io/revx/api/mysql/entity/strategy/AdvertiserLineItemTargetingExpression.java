package io.revx.api.mysql.entity.strategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "AdvertiserLineItemTargetingExpression")
public class AdvertiserLineItemTargetingExpression implements BaseEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "alte_id", nullable = false)
  private Long id;

  @Column(name = "alte_advertiser_li_id", nullable = false)
  private Long strategyId;

  @Column(name = "alte_common_targeting_expr")
  private String commonTargetingExpression;

  @Column(name = "alte_placement_sp_targeting_expr")
  private String placementTargetingExpression;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(Long strategyId) {
    this.strategyId = strategyId;
  }

  public String getCommonTargetingExpression() {
    return commonTargetingExpression;
  }

  public void setCommonTargetingExpression(String commonTargetingExpression) {
    this.commonTargetingExpression = commonTargetingExpression;
  }

  public String getPlacementTargetingExpression() {
    return placementTargetingExpression;
  }

  public void setPlacementTargetingExpression(String placementTargetingExpression) {
    this.placementTargetingExpression = placementTargetingExpression;
  }

  public String getName() {
    return "AdvertiserLineItemTargetingExpression";
  }

  public String getDescriptiveName() {
    return "AdvertiserLineItemTargetingExpression";
  }
}
