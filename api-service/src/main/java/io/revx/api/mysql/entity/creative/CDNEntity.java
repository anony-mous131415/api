/*
 * @author: ranjan-pritesh
 * 
 * @date: 16th Dec
 */
package io.revx.api.mysql.entity.creative;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CDN")
public class CDNEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "cn_id", nullable = false)
  private Long id;

  @Column(name = "cn_name")
  private String name;

  @Column(name = "cn_base_url")
  private String baseUrl;

  @Column(name = "cn_origin_server")
  private String originServer;

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

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getOriginServer() {
    return originServer;
  }

  public void setOriginServer(String originServer) {
    this.originServer = originServer;
  }

  @Override
  public String toString() {
    return "CDNEntity [id=" + id + ", name=" + name + ", baseUrl=" + baseUrl + ", originServer="
        + originServer + "]";
  }


}
