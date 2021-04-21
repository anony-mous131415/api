package io.revx.api.mysql.entity.strategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "BidStrategy")
public class BidStrategy implements BaseEntity {

  private static final long serialVersionUID = 5983266740928766272L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bid_id", nullable = false)
  private Long id;

  @Column(name = "bid_str_type_id")
  private Integer typeId;

  @Column(name = "bid_strategy_params")
  private String params;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Integer getTypeId() {
    return typeId;
  }

  public void setTypeId(Integer typeId) {
    this.typeId = typeId;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }



}
