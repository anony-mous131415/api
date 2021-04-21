package io.revx.api.mysql.dco.entity.catalog;

import com.fasterxml.jackson.annotation.JsonTypeId;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Id;


@Entity
@Table(name = "FeedApiStats")
public class FeedApiStatusEntity implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "fas_id")
  private Long fasId;

  @Column(name = "fas_batch_id")
  private Long fasBatchId;

  @Column(name = "fas_merchant_id")
  private Long fasMerchantId;

  @Column(name = "fas_status")
  private String fasStatus;

  @Column(name = "fas_product_total")
  private Long fasProductTotal;

  @Column(name = "fas_product_inserted")
  private Long fasProductInserted;

  @Column(name = "fas_product_updated")
  private Long fasProductUpdated;

  @Column(name = "fas_product_delete")
  private Long fasProductDelete;

  @Column(name = "fas_product_failed")
  private Long fasProductFailed;

  @Column(name = "fas_time_taken_ms")
  private Long fasTimeTakenMs;

  @Column(name = "fas_created_time")
  private Date fasCreatedTime;

  @Column(name = "fas_feed_key")
  private String fasFeedKey;

  @Column(name = "fas_feed_status")
  private int fasFeedStatus;

  @Column(name = "fas_feed_total")
  private Long fasFeedTotal;

  @Column(name = "fas_product_parsed")
  private Long fasProductParsed;

  @Column(name = "fas_product_skipped")
  private Long fasProductSkipped;

  
  @Column(name = "fas_feed_id")
  private Long feedId;
  
  public Long getFasId() {
    return fasId;
  }


  public void setFasId(Long fasId) {
    this.fasId = fasId;
  }


  public Long getFasBatchId() {
    return fasBatchId;
  }


  public void setFasBatchId(Long fasBatchId) {
    this.fasBatchId = fasBatchId;
  }


  public Long getFasMerchantId() {
    return fasMerchantId;
  }


  public void setFasMerchantId(Long fasMerchantId) {
    this.fasMerchantId = fasMerchantId;
  }


  public String getFasStatus() {
    return fasStatus;
  }


  public void setFasStatus(String fasStatus) {
    this.fasStatus = fasStatus;
  }


  public Long getFasProductTotal() {
    return fasProductTotal;
  }


  public void setFasProductTotal(Long fasProductTotal) {
    this.fasProductTotal = fasProductTotal;
  }


  public Long getFasProductInserted() {
    return fasProductInserted;
  }


  public void setFasProductInserted(Long fasProductInserted) {
    this.fasProductInserted = fasProductInserted;
  }


  public Long getFasProductUpdated() {
    return fasProductUpdated;
  }


  public void setFasProductUpdated(Long fasProductUpdated) {
    this.fasProductUpdated = fasProductUpdated;
  }


  public Long getFasProductDelete() {
    return fasProductDelete;
  }


  public void setFasProductDelete(Long fasProductDelete) {
    this.fasProductDelete = fasProductDelete;
  }


  public Long getFasProductFailed() {
    return fasProductFailed;
  }


  public void setFasProductFailed(Long fasProductFailed) {
    this.fasProductFailed = fasProductFailed;
  }


  public Long getFasTimeTakenMs() {
    return fasTimeTakenMs;
  }


  public void setFasTimeTakenMs(Long fasTimeTakenMs) {
    this.fasTimeTakenMs = fasTimeTakenMs;
  }


  public Date getFasCreatedTime() {
    return fasCreatedTime;
  }


  public void setFasCreatedTime(Date fasCreatedTime) {
    this.fasCreatedTime = fasCreatedTime;
  }


  public String getFasFeedKey() {
    return fasFeedKey;
  }


  public void setFasFeedKey(String fasFeedKey) {
    this.fasFeedKey = fasFeedKey;
  }


  public int getFasFeedStatus() {
    return fasFeedStatus;
  }


  public void setFasFeedStatus(int fasFeedStatus) {
    this.fasFeedStatus = fasFeedStatus;
  }


  public Long getFasFeedTotal() {
    return fasFeedTotal;
  }


  public void setFasFeedTotal(Long fasFeedTotal) {
    this.fasFeedTotal = fasFeedTotal;
  }


  public Long getFasProductParsed() {
    return fasProductParsed;
  }


  public void setFasProductParsed(Long fasProductParsed) {
    this.fasProductParsed = fasProductParsed;
  }


  public Long getFasProductSkipped() {
    return fasProductSkipped;
  }


  public void setFasProductSkipped(Long fasProductSkipped) {
    this.fasProductSkipped = fasProductSkipped;
  }

  public Long getFeedId() {
    return feedId;
  }


  public void setFeedId(Long feedId) {
    this.feedId = feedId;
  }


  @Override
  public String toString() {
    return "FeedApiStatusEntity [fasId=" + fasId + ", fasBatchId=" + fasBatchId + ", fasMerchantId="
        + fasMerchantId + ", fasStatus=" + fasStatus + ", fasProductTotal=" + fasProductTotal
        + ", fasProductInserted=" + fasProductInserted + ", fasProductUpdated=" + fasProductUpdated
        + ", fasProductDelete=" + fasProductDelete + ", fasProductFailed=" + fasProductFailed
        + ", fasTimeTakenMs=" + fasTimeTakenMs + ", fasCreatedTime=" + fasCreatedTime
        + ", fasFeedKey=" + fasFeedKey + ", fasFeedStatus=" + fasFeedStatus + ", fasFeedTotal="
        + fasFeedTotal + ", fasProductParsed=" + fasProductParsed + ", fasProductSkipped="
        + fasProductSkipped + ", feedId=" + feedId + "]";
  }

}
