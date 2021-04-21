package io.revx.core.model;

import java.math.BigDecimal;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import io.revx.core.aop.HideForReadOnlyAccess;

public class SlicexGridCSVData extends BaseModelWithModifiedTime {

  protected static final long serialVersionUID = 1L;

  @CsvBindByName(column = "Name")
  @CsvBindByPosition(position = 0)
  protected String name;
  
  @CsvBindByName(column = "Advertiser Spend")
  @CsvBindByPosition(position = 1)
  protected BigDecimal revenue = BigDecimal.ZERO;
  
  @CsvBindByName(column = "CTR")
  @CsvBindByPosition(position = 2)
  protected BigDecimal ctr = BigDecimal.ZERO;
  
  @CsvBindByName(column = "CTC")
  @CsvBindByPosition(position = 3)
  protected BigDecimal ctc = BigDecimal.ZERO;
  
  @CsvBindByName(column = "Impressions")
  @CsvBindByPosition(position = 4)
  protected BigDecimal impressions = BigDecimal.ZERO;

  @CsvBindByName(column = "Clicks")
  @CsvBindByPosition(position = 5)
  protected BigDecimal clicks = BigDecimal.ZERO;
  
  @CsvBindByName(column = "Installs")
  @CsvBindByPosition(position = 6)
  protected BigDecimal installs = BigDecimal.ZERO;
  
  @CsvBindByName(column = "View Installs")
  @CsvBindByPosition(position = 7)
  protected BigDecimal impInstalls = BigDecimal.ZERO;
  
  @CsvBindByName(column = "Click Installs")
  @CsvBindByPosition(position = 8)
  protected BigDecimal clickInstalls = BigDecimal.ZERO;
  
  @CsvBindByName(column = "Conversions")
  @CsvBindByPosition(position = 9)
  protected BigDecimal conversions = BigDecimal.ZERO;
  
  @CsvBindByName(column = "View Conversion")
  @CsvBindByPosition(position = 10)
  protected BigDecimal viewConversions = BigDecimal.ZERO;

  @CsvBindByName(column = "Click Conversion")
  @CsvBindByPosition(position = 11)
  protected BigDecimal clickConversions = BigDecimal.ZERO;

  @CsvBindByName(column = "eRPM")
  @CsvBindByPosition(position = 12)
  protected BigDecimal erpm = BigDecimal.ZERO;
  
  @CsvBindByName(column = "eRPC")
  @CsvBindByPosition(position = 13)
  protected BigDecimal erpc = BigDecimal.ZERO;
  
  @CsvBindByName(column = "eRPI")
  @CsvBindByPosition(position = 14)
  protected BigDecimal erpi = BigDecimal.ZERO;
  
  @CsvBindByName(column = "eRPA")
  @CsvBindByPosition(position = 15)
  protected BigDecimal erpa = BigDecimal.ZERO;
  
  @CsvBindByName(column = "ITI")
  @CsvBindByPosition(position = 16)
  protected BigDecimal iti = BigDecimal.ZERO;
  
  @CsvBindByName(column = "Currency Code")
  @CsvBindByPosition(position = 17)
  protected String currencyId;

  // Hide For RO users
  @CsvBindByName(column = "Media Spend")
  @CsvBindByPosition(position = 18)
  @HideForReadOnlyAccess
  protected BigDecimal cost = BigDecimal.ZERO;

  @CsvBindByName(column = "Margin")
  @CsvBindByPosition(position = 19)
  @HideForReadOnlyAccess
  protected BigDecimal margin = BigDecimal.ZERO;

  @CsvBindByName(column = "eCPM")
  @CsvBindByPosition(position = 20)
  @HideForReadOnlyAccess
  protected BigDecimal ecpm = BigDecimal.ZERO;
  
  @CsvBindByName(column = "eCPC")
  @CsvBindByPosition(position = 21)
  @HideForReadOnlyAccess
  protected BigDecimal ecpc = BigDecimal.ZERO;
  
  @CsvBindByName(column = "eCPI")
  @CsvBindByPosition(position = 22)
  @HideForReadOnlyAccess
  protected BigDecimal ecpi = BigDecimal.ZERO;
  
  @CsvBindByName(column = "eCPA")
  @CsvBindByPosition(position = 23)
  @HideForReadOnlyAccess
  protected BigDecimal ecpa = BigDecimal.ZERO;

  public SlicexGridCSVData(String name, BigDecimal revenue, BigDecimal ctr, BigDecimal ctc,
      BigDecimal impressions, BigDecimal clicks, BigDecimal installs, BigDecimal impInstalls,
      BigDecimal clickInstalls, BigDecimal conversions, BigDecimal viewConversions,
      BigDecimal clickConversions, BigDecimal erpm, BigDecimal erpc, BigDecimal erpi,
      BigDecimal erpa, BigDecimal iti, String currencyId, BigDecimal cost, BigDecimal margin,
      BigDecimal ecpm, BigDecimal ecpc, BigDecimal ecpi, BigDecimal ecpa) {
    super();
    this.name = name;
    this.revenue = revenue;
    this.ctr = ctr;
    this.ctc = ctc;
    this.impressions = impressions;
    this.clicks = clicks;
    this.installs = installs;
    this.impInstalls = impInstalls;
    this.clickInstalls = clickInstalls;
    this.conversions = conversions;
    this.viewConversions = viewConversions;
    this.clickConversions = clickConversions;
    this.erpm = erpm;
    this.erpc = erpc;
    this.erpi = erpi;
    this.erpa = erpa;
    this.iti = iti;
    this.currencyId = currencyId;
    this.cost = cost;
    this.margin = margin;
    this.ecpm = ecpm;
    this.ecpc = ecpc;
    this.ecpi = ecpi;
    this.ecpa = ecpa;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getRevenue() {
    return revenue;
  }

  public void setRevenue(BigDecimal revenue) {
    this.revenue = revenue;
  }

  public BigDecimal getCtr() {
    return ctr;
  }

  public void setCtr(BigDecimal ctr) {
    this.ctr = ctr;
  }

  public BigDecimal getCtc() {
    return ctc;
  }

  public void setCtc(BigDecimal ctc) {
    this.ctc = ctc;
  }

  public BigDecimal getImpressions() {
    return impressions;
  }

  public void setImpressions(BigDecimal impressions) {
    this.impressions = impressions;
  }

  public BigDecimal getClicks() {
    return clicks;
  }

  public void setClicks(BigDecimal clicks) {
    this.clicks = clicks;
  }

  public BigDecimal getInstalls() {
    return installs;
  }

  public void setInstalls(BigDecimal installs) {
    this.installs = installs;
  }

  public BigDecimal getImpInstalls() {
    return impInstalls;
  }

  public void setImpInstalls(BigDecimal impInstalls) {
    this.impInstalls = impInstalls;
  }

  public BigDecimal getClickInstalls() {
    return clickInstalls;
  }

  public void setClickInstalls(BigDecimal clickInstalls) {
    this.clickInstalls = clickInstalls;
  }

  public BigDecimal getConversions() {
    return conversions;
  }

  public void setConversions(BigDecimal conversions) {
    this.conversions = conversions;
  }

  public BigDecimal getViewConversions() {
    return viewConversions;
  }

  public void setViewConversions(BigDecimal viewConversions) {
    this.viewConversions = viewConversions;
  }

  public BigDecimal getClickConversions() {
    return clickConversions;
  }

  public void setClickConversions(BigDecimal clickConversions) {
    this.clickConversions = clickConversions;
  }

  public BigDecimal getErpm() {
    return erpm;
  }

  public void setErpm(BigDecimal erpm) {
    this.erpm = erpm;
  }

  public BigDecimal getErpc() {
    return erpc;
  }

  public void setErpc(BigDecimal erpc) {
    this.erpc = erpc;
  }

  public BigDecimal getErpi() {
    return erpi;
  }

  public void setErpi(BigDecimal erpi) {
    this.erpi = erpi;
  }

  public BigDecimal getErpa() {
    return erpa;
  }

  public void setErpa(BigDecimal erpa) {
    this.erpa = erpa;
  }

  public BigDecimal getIti() {
    return iti;
  }

  public void setIti(BigDecimal iti) {
    this.iti = iti;
  }

  public String getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(String currencyId) {
    this.currencyId = currencyId;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public BigDecimal getMargin() {
    return margin;
  }

  public void setMargin(BigDecimal margin) {
    this.margin = margin;
  }

  public BigDecimal getEcpm() {
    return ecpm;
  }

  public void setEcpm(BigDecimal ecpm) {
    this.ecpm = ecpm;
  }

  public BigDecimal getEcpc() {
    return ecpc;
  }

  public void setEcpc(BigDecimal ecpc) {
    this.ecpc = ecpc;
  }

  public BigDecimal getEcpi() {
    return ecpi;
  }

  public void setEcpi(BigDecimal ecpi) {
    this.ecpi = ecpi;
  }

  public BigDecimal getEcpa() {
    return ecpa;
  }

  public void setEcpa(BigDecimal ecpa) {
    this.ecpa = ecpa;
  }
  
  
}
