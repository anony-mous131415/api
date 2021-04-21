package io.revx.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import io.revx.core.enums.RoleName;

@Entity
@Table(name = "Roles")
public class RolesEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ro_id")
  private long id;


  @Enumerated(EnumType.STRING)
  @Column(name = "ro_name")
  private RoleName name;

  @Column(name = "ro_description")
  private String description;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public RoleName getName() {
    return name;
  }

  public void setName(RoleName name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "Role [id=" + id + ", name=" + name + ", description=" + description + "]";
  }

}
