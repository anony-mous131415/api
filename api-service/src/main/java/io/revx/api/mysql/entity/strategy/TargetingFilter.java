package io.revx.api.mysql.entity.strategy;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "TargetingFilter")
public class TargetingFilter implements BaseEntity {
  private static final long serialVersionUID = -4885361364811002754L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "tf_id", nullable = false)
  private Long id;

  @Column(name = "tf_filter_name", nullable = false, length = 256)
  private String name;

  @Column(name = "tf_value_type", columnDefinition = "BIT", length = 1)
  private boolean type;

  @Column(name = "tf_is_enabled_for_buyer", nullable = false, columnDefinition = "BIT", length = 1)
  private boolean enabledForBuyer;

  @Column(name = "tf_is_enabled_for_seller", nullable = false, columnDefinition = "BIT", length = 1)
  private boolean enabledForSeller;

  @Column(name = "tf_enum_order", nullable = false)
  private Integer order;;

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

  public boolean isEnabledForBuyer() {
    return enabledForBuyer;
  }

  public void setEnabledForBuyer(boolean enabledForBuyer) {
    this.enabledForBuyer = enabledForBuyer;
  }

  public boolean isEnabledForSeller() {
    return enabledForSeller;
  }

  public void setEnabledForSeller(boolean enabledForSeller) {
    this.enabledForSeller = enabledForSeller;
  }

  public boolean isType() {
    return type;
  }

  public void setType(boolean type) {
    this.type = type;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public String getDescriptiveName() {
    return "Targeting filter";
  }

}
