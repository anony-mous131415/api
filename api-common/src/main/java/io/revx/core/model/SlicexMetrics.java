//package io.revx.core.model;
//
//import java.math.BigDecimal;
//import io.revx.core.utils.NumberUtils;
//
//public class SlicexMetrics extends DashboardMetrics {
//
//  private static final long serialVersionUID = 6296410688577963702L;
//
//  public SlicexMetrics(long id, String name) {
//    super(id, name);
//  }
//
//  public SlicexMetrics() {
//    super();
//  }
//
//
//  protected BigDecimal erpa;
//
//  protected BigDecimal erpc;
//
//  protected BigDecimal erpm;
//
//  protected BigDecimal impPerConversion;
//
//  protected BigDecimal marginAbsolute;
//
//  protected BigDecimal rpi;
//
//  public void setCalculatedFields() {
//    super.setCalculatedFields();
//    // TODO: will add marginabsolute in ENum and change properties of margin field (abs margin to %)
//    this.marginAbsolute = NumberUtils.substractBigDecimal(revenue, cost);
//    // TODO Need to review CTC formula (click/conversions OR click/clk_conversion)
//    // TODO need to review CVR also (have to update derived formulas)
//    // TODO need to review CPI also (have to check revenue/installs OR cost/installs)
//    // TODO CPM,
//
//    this.erpa = NumberUtils.divide(revenue, conversions);
//    this.erpc = NumberUtils.divide(revenue, clicks);
//    this.erpm = NumberUtils.divide(revenue, impressions);
//    this.rpi = NumberUtils.divide(revenue, installs);
//
//  }
//
//  public BigDecimal getErpa() {
//    return erpa;
//  }
//
//  public void setErpa(BigDecimal erpa) {
//    this.erpa = erpa;
//  }
//
//  public BigDecimal getErpc() {
//    return erpc;
//  }
//
//  public void setErpc(BigDecimal erpc) {
//    this.erpc = erpc;
//  }
//
//  public BigDecimal getErpm() {
//    return erpm;
//  }
//
//  public void setErpm(BigDecimal erpm) {
//    this.erpm = erpm;
//  }
//
//  public BigDecimal getRpi() {
//    return rpi;
//  }
//
//  public void setRpi(BigDecimal rpi) {
//    this.rpi = rpi;
//  }
//
//  public BigDecimal getImpPerConversion() {
//    return impPerConversion;
//  }
//
//  public void setImpPerConversion(BigDecimal impPerConversion) {
//    this.impPerConversion = impPerConversion;
//  }
//
//  public BigDecimal getMarginAbsolute() {
//    return marginAbsolute;
//  }
//
//  public void setMarginAbsolute(BigDecimal marginAbsolute) {
//    this.marginAbsolute = marginAbsolute;
//  }
//
//}
