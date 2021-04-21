package io.revx.api.mysql.entity.strategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.enums.InventoryType;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "InventorySource")
public class InventorySource implements BaseEntity {

  private static final long serialVersionUID = 5983266740928766272L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ivs_id", nullable = false)
  private Long id;

  @Column(name = "ivs_ali_id")
  private Long strategyId;

  @Column(name = "ivs_name")
  private String name;

  @Column(name = "ivs_bid_strategy_id")
  private Long bidStrategyId;

  @Column(name = "ivs_targeting_expression", columnDefinition = "text")
  private String targetingExpression;

  @Column(name = "ivs_type", columnDefinition = "ENUM", nullable = false)
  @Enumerated(EnumType.STRING)
  private InventoryType type;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getBidStrategyId() {
    return bidStrategyId;
  }

  public void setBidStrategyId(Long bidStrategyId) {
    this.bidStrategyId = bidStrategyId;
  }

  public String getTargetingExpression() {
    return targetingExpression;
  }

  public void setTargetingExpression(String targetingExpression) {
    this.targetingExpression = targetingExpression;
  }

  public InventoryType getType() {
    return type;
  }

  public void setType(InventoryType type) {
    this.type = type;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("InventorySource [id=").append(id).append(", strategyId=").append(strategyId)
        .append(", name=").append(name).append(", bidStrategyId=").append(bidStrategyId)
        .append(", targetingExpression=").append(targetingExpression).append(", type=").append(type)
        .append("]");
    return builder.toString();
  }


}
