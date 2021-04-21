package io.revx.core.model.creative;

public class CreativePerformanceData {
  
  private long impressions;
  
  private long clicks;
  
  private long conversions;
  
  private double ctr;
  
  private double ctc;

  public long getImpressions() {
    return impressions;
  }

  public void setImpressions(long impressions) {
    this.impressions = impressions;
  }

  public long getClicks() {
    return clicks;
  }

  public void setClicks(long clicks) {
    this.clicks = clicks;
  }

  public long getConversions() {
    return conversions;
  }

  public void setConversions(long conversions) {
    this.conversions = conversions;
  }

  public double getCtr() {
    return ctr;
  }

  public void setCtr(double ctr) {
    this.ctr = ctr;
  }

  public double getCtc() {
    return ctc;
  }

  public void setCtc(double ctc) {
    this.ctc = ctc;
  }

  @Override
  public String toString() {
    return "CreativePerformanceData [impressions=" + impressions + ", clicks=" + clicks
        + ", conversions=" + conversions + ", ctr=" + ctr + ", ctc=" + ctc + "]";
  }
  
}
