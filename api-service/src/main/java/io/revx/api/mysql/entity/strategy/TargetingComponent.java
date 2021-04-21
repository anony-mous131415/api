package io.revx.api.mysql.entity.strategy;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;

@Entity
@Table(name = "TargetingComponent")
public class TargetingComponent implements BaseEntity {
  private static final long serialVersionUID = -2154384553683783925L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "tc_id", nullable = false)
  private Long id;

  @Column(name = "tc_targeting_criteria", columnDefinition = "longtext")
  private String criteria;


  @Column(name = "tc_targeting_filter_id", nullable = false)
  private Long targetingFilterId;

  @Column(name = "tc_operator_id")
  private Long targetingOperatorId;

  public TargetingComponent() {
  }
  
  public TargetingComponent(String criteria, Long targetingFilterId,
      Long targetingOperatorId) {
    this.criteria = criteria;
    this.targetingFilterId = targetingFilterId;
    this.targetingOperatorId = targetingOperatorId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCriteria() {
    return criteria;
  }

  public void setCriteria(String criteria) {
    this.criteria = criteria;
  }

  public Long getTargetingFilterId() {
    return targetingFilterId;
  }

  public void setTargetingFilterId(Long targetingFilterId) {
    this.targetingFilterId = targetingFilterId;
  }

  public Long getTargetingOperatorId() {
    return targetingOperatorId;
  }

  public void setTargetingOperatorId(Long targetingOperatorId) {
    this.targetingOperatorId = targetingOperatorId;
  }

  public String getDescriptiveName() {
    return "Criteria";
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("TargetingComponent [id=").append(id).append(", criteria=").append(criteria)
        .append(", targetingFilterId=").append(targetingFilterId).append(", targetingOperatorId=")
        .append(targetingOperatorId).append("]");
    return builder.toString();
  }

}
