package io.revx.api.mysql.entity.audit;

import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.model.BaseEntity;


@Entity
@Table(name = "AuditChange")
public class AuditChange implements BaseEntity {

  /**
   * 
   */
  private static final long serialVersionUID = -4506339674044209347L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "auc_id", unique = true, nullable = false)
  private Long id;

  @Column(name = "auc_aul_id")
  private Long auditLogId;

  @Column(name = "auc_field_name", nullable = false, length = 30)
  private String fieldName;

  @Column(name = "auc_old_value", length = 20)
  private String oldValue;

  @Column(name = "auc_new_value", length = 20)
  private String newValue;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAuditLogId() {
    return auditLogId;
  }

  public void setAuditLogId(Long auditLogId) {
    this.auditLogId = auditLogId;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }



}
