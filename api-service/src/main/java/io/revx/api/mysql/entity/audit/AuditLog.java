package io.revx.api.mysql.entity.audit;

import static javax.persistence.GenerationType.IDENTITY;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import io.revx.core.model.BaseEntity;


@Entity
@Table(name = "AuditLog")
public class AuditLog implements BaseEntity {

  private static final long serialVersionUID = -5063363943822591324L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "aul_id", unique = true, nullable = false)
  private Long id;

  @Column(name = "aul_entity_id")
  private Long entityId;



  @Column(name = "aul_entity_type")
  private String entityType;

  @Column(name = "aul_timestamp")
  private Long timestamp;

  @Column(name = "aul_user_id", nullable = false)
  private long userId;

  /*
   * type = 1 - entity audit log type = 2 - entity user msg type = 3 - system msg
   */
  @Column(name = "aul_type")
  private Long type;

  @Column(name = "aul_msg", length = 500)
  private String msg;
  
  @Transient
  private Set<AuditChange> auditChanges = new HashSet<AuditChange>();


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public Long getType() {
    return type;
  }

  public void setType(Long type) {
    this.type = type;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Set<AuditChange> getAuditChanges() {
    return auditChanges;
  }

  public void setAuditChanges(Set<AuditChange> auditChanges) {
    this.auditChanges = auditChanges;
  }



}
