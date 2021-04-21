package io.revx.api.mysql.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CSSTheme")
public class CSSThemeEntity implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ct_id")
  private int id;
  @Column(name = "ct_name")
  private String name;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("CSSTheme [id=");
    builder.append(id);
    builder.append(", name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }

}
