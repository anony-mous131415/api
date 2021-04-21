package io.revx.api.mysql.entity.strategy;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;


@Entity
@Table(name = "AdvertiserLineItemCreative")
public class AdvertiserLineItemCreativeEntity implements BaseEntity, Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ac_id")
  private Long id;

  @Column(name = "ac_advertiser_li_id")
  private long strategyId;

  @Column(name = "ac_creative_id")
  private Long creativeId;

  
  public AdvertiserLineItemCreativeEntity() {
  
  }

  public AdvertiserLineItemCreativeEntity(long strategyId, Long creativeId) {
    super();
    this.strategyId = strategyId;
    this.creativeId = creativeId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public long getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(long strategyId) {
    this.strategyId = strategyId;
  }

  public Long getCreativeId() {
    return creativeId;
  }

  public void setCreativeId(Long creativeId) {
    this.creativeId = creativeId;
  }


}
