package io.revx.api.mysql.dco.entity.catalog;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FeedInfoStats")
public class FeedInfoStatsEntity implements  Serializable {

	private static final long serialVersionUID = -2338211681883310612L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fis_id", nullable = false)
    private Long  id;
	
    @Column(name = "fis_feed_id", nullable = false)
    private Long   feedId;
	
	@Column(name = "fis_last_updated")
    private Long  lastUpdated;
	
	@Column(name = "fis_status", nullable = false)
    private Integer  status;
	
	@Column(name = "fis_objects_updated")
    private Long  objectsUpdated;
	
	@Column(name = "fis_objects_found")
    private Long  objectsFound;
	
	@Column(name = "fis_objects_parsed")
	private Long objectsParsed;

  

  public Long getId() {
    return id;
  }



  public void setId(Long id) {
    this.id = id;
  }



  public Long getFeedId() {
    return feedId;
  }



  public void setFeedId(Long feedId) {
    this.feedId = feedId;
  }



  public Long getLastUpdated() {
    return lastUpdated;
  }



  public void setLastUpdated(Long lastUpdated) {
    this.lastUpdated = lastUpdated;
  }



  public Integer getStatus() {
    return status;
  }



  public void setStatus(Integer status) {
    this.status = status;
  }



  public Long getObjectsUpdated() {
    return objectsUpdated;
  }



  public void setObjectsUpdated(Long objectsUpdated) {
    this.objectsUpdated = objectsUpdated;
  }



  public Long getObjectsFound() {
    return objectsFound;
  }



  public void setObjectsFound(Long objectsFound) {
    this.objectsFound = objectsFound;
  }



  public Long getObjectsParsed() {
    return objectsParsed;
  }



  public void setObjectsParsed(Long objectsParsed) {
    this.objectsParsed = objectsParsed;
  }



  @Override
  public String toString() {
    return "FeedInfoStats [id=" + id + ", feedId=" + feedId + ", lastUpdated=" + lastUpdated
        + ", status=" + status + ", objectsUpdated=" + objectsUpdated + ", objectsFound="
        + objectsFound + ", objectsParsed=" + objectsParsed + "]";
  }

}
