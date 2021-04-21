package io.revx.core.model.pixel;

import java.util.List;
import io.revx.core.enums.DataSourceType;
import io.revx.core.model.StatusTimeModel;

public class DataPixelDTO extends StatusTimeModel{
  private static final long serialVersionUID = -2258463565419092236L;

  private String description;

  private String hash;

  private List<Long> segmentIds;

  private Long uuCount = 0L;

  private Long piCount = 0L;

  private String partners;

  private DataSourceType type;
  public String getDescription() {
      return description;
  }

  public void setDescription(String description) {
      this.description = description;
  }

  public List<Long> getSegmentIds() {
      return segmentIds;
  }

  public void setSegmentIds(List<Long> segmentIds) {
      this.segmentIds = segmentIds;
  }

  public String getHash() {
      return hash;
  }

  public void setHash(String hash) {
      this.hash = hash;
  }

  /**
   * 
   * @return Unique User count for the pixel
   */
  public Long getUuCount() {
      return uuCount;
  }

  public void setUuCount(Long uuCount) {
      this.uuCount = uuCount;
  }

  /**
   * 
   * @return Page Inpression count for the pixel
   */
  public Long getPiCount() {
      return piCount;
  }

  public void setPiCount(Long piCount) {
      this.piCount = piCount;
  }

  public String getPartners() {
      return partners;
  }

  public void setPartners(String partners) {
      this.partners = partners;
  }

  public DataSourceType getSourceType() {
      return type;
  }

  public void setSourceType(DataSourceType type) {
      this.type = type;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DataPixelDTO [description=").append(description).append(", hash=").append(hash)
        .append(", segmentIds=").append(segmentIds).append(", uuCount=").append(uuCount)
        .append(", piCount=").append(piCount).append(", partners=").append(partners)
        .append(", type=").append(type).append("]");
    return builder.toString();
  }

  
}
