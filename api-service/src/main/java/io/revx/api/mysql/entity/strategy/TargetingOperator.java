package io.revx.api.mysql.entity.strategy;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "TargetingOperator")
public class TargetingOperator implements BaseEntity {
  private static final long serialVersionUID = 6628598980752734664L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "to_id", nullable = false)
  private Long id;

  @Column(name = "to_name", nullable = false, length = 256)
  private String name;

  @Column(name = "to_enum_order", nullable = false)
  private Long order;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getOrder() {
    return order;
  }

  public void setOrder(Long order) {
    this.order = order;
  }

  public String getDescriptiveName() {
    return "Targeting Operator";
  }
}
