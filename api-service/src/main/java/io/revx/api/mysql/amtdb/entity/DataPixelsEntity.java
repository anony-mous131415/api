package io.revx.api.mysql.amtdb.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.api.enums.Status;
import io.revx.core.enums.DataSourceType;

@Entity
@Table(name = "Pixels")
public class DataPixelsEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "licensee_id")
  private Long liceseeId;

  /** The description. */
  @Column(name = "description")
  private String description;

  @Column(name = "hash")
  private String hash;

  @Column(name = "name")
  private String name;
  
  @Column(name="status" , columnDefinition = "ENUM" , nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(name = "partners")
  private String partners;

  @Column(name = "data_source_type")
  @Enumerated(EnumType.STRING)
  private DataSourceType sourceType;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getLiceseeId() {
    return liceseeId;
  }

  public void setLiceseeId(Long liceseeId) {
    this.liceseeId = liceseeId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getPartners() {
    return partners;
  }

  public void setPartners(String partners) {
    this.partners = partners;
  }

  public DataSourceType getSourceType() {
    return sourceType;
  }

  public void setSourceType(DataSourceType sourceType) {
    this.sourceType = sourceType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
