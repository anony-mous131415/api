package io.revx.core.model.audience;

import java.util.List;

public class DmpAudience {

  private String sid;
  private String sname;
  private String sdescription;
  private Integer stype;
  private Long scount;
  private List<DmpAudienceCriteria> criteria;
  public Boolean isSynced;
  
  public String getSdescription() {
    return sdescription;
  }
  public void setSdescription(String sdescription) {
    this.sdescription = sdescription;
  }
  public List<DmpAudienceCriteria> getCriteria() {
    return criteria;
  }
  public void setCriteria(List<DmpAudienceCriteria> criteria) {
    this.criteria = criteria;
  }
  public Boolean getIsSynced() {
    return isSynced;
  }
  public void setIsSynced(Boolean isSynced) {
    this.isSynced = isSynced;
  }
  public String getSid() {
    return sid;
  }
  public void setSid(String sid) {
    this.sid = sid;
  }
  public String getSname() {
    return sname;
  }
  public void setSname(String sname) {
    this.sname = sname;
  }
  public Integer getStype() {
    return stype;
  }
  public void setStype(Integer stype) {
    this.stype = stype;
  }
  public Long getScount() {
    return scount;
  }
  public void setScount(Long scount) {
    this.scount = scount;
  }
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("DmpAudience [sid=").append(sid).append(", sname=").append(sname)
        .append(", sdescription=").append(sdescription).append(", stype=").append(stype)
        .append(", scount=").append(scount).append(", criteria=").append(criteria)
        .append(", isSynced=").append(isSynced).append("]");
    return builder.toString();
  }
  
}
