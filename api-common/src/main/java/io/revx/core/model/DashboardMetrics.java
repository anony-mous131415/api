package io.revx.core.model;

import java.math.BigDecimal;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import io.revx.core.aop.HideForReadOnlyAccess;
import io.revx.core.utils.NumberUtils;

public class DashboardMetrics extends BaseModelWithModifiedTime {

  protected static final long serialVersionUID = 1L;

  public DashboardMetrics(long id, String name) {
    super(id, name);
    initialiseWithZero();
  }

  public DashboardMetrics() {
    initialiseWithZero();
  }

  @CsvBindByName(column = "Impressions")
  @CsvBindByPosition(position = 2)
  public BigDecimal impressions = BigDecimal.ZERO;

  @CsvBindByName(column = "Clicks")
  @CsvBindByPosition(position = 3)
  public BigDecimal clicks = BigDecimal.ZERO;

  @CsvBindByName(column = "Conversions")
  @CsvBindByPosition(position = 4)
  public BigDecimal conversions = BigDecimal.ZERO;


  @CsvBindByName(column = "CTR")
  @CsvBindByPosition(position = 5)
  public BigDecimal ctr = BigDecimal.ZERO;

  @CsvBindByName(column = "CTC")
  @CsvBindByPosition(position = 6)
  public BigDecimal ctc = BigDecimal.ZERO;

  @CsvBindByName(column = "CVR")
  @CsvBindByPosition(position = 7)
  public BigDecimal cvr = BigDecimal.ZERO;


  @CsvBindByName(column = "ITI")
  @CsvBindByPosition(position = 8)
  public BigDecimal iti = BigDecimal.ZERO;

  @CsvBindByName(column = "Revenue")
  @CsvBindByPosition(position = 9)
  public BigDecimal revenue = BigDecimal.ZERO;


  @CsvBindByName(column = "View Conversion")
  @CsvBindByPosition(position = 10)
  public BigDecimal viewConversions = BigDecimal.ZERO;

  @CsvBindByName(column = "Click Conversion")
  @CsvBindByPosition(position = 11)
  public BigDecimal clickConversions = BigDecimal.ZERO;

  @CsvBindByName(column = "Currency Code")
  @CsvBindByPosition(position = 12)
  public String currencyId;

  @CsvBindByName(column = "Total Installs")
  @CsvBindByPosition(position = 13)
  public BigDecimal installs = BigDecimal.ZERO;

  @CsvBindByName(column = "Click Installs")
  @CsvBindByPosition(position = 14)
  public BigDecimal clickInstalls = BigDecimal.ZERO;

  @CsvBindByName(column = "View Installs")
  @CsvBindByPosition(position = 15)
  public BigDecimal impInstalls = BigDecimal.ZERO;

  @CsvBindByName(column = "Invalid Clicks")
  @CsvBindByPosition(position = 16)
  public BigDecimal invalidClicks = BigDecimal.ZERO;


  @CsvBindByName(column = "Advertiser Revenue")
  @CsvBindByPosition(position = 17)
  public BigDecimal advRevenue = BigDecimal.ZERO;

  @CsvBindByName(column = "ROI")
  @CsvBindByPosition(position = 18)
  public BigDecimal roi = BigDecimal.ZERO;

  @CsvBindByName(column = "Imp Unique Users")
  @CsvBindByPosition(position = 19)
  public Long impressionUniqUsers = 0l;

  @CsvBindByName(column = "Eligible Unique Users")
  @CsvBindByPosition(position = 20)
  public Long eligibleUniqUsers = 0l;

  @CsvBindByName(column = "User Reach")
  @CsvBindByPosition(position = 21)
  public BigDecimal userReach = BigDecimal.ZERO;

  @CsvBindByName(column = "Eligible Bids")
  @CsvBindByPosition(position = 22)
  public Long eligibleBids = 0l;

  @CsvBindByName(column = "Bid Placed")
  @CsvBindByPosition(position = 23)
  public BigDecimal bidsPlaced = BigDecimal.ZERO;


  @CsvBindByName(column = "eRPA")
  @CsvBindByPosition(position = 24)
  public BigDecimal erpa = BigDecimal.ZERO;

  @CsvBindByName(column = "eRPC")
  @CsvBindByPosition(position = 25)
  public BigDecimal erpc = BigDecimal.ZERO;

  @CsvBindByName(column = "eRPM")
  @CsvBindByPosition(position = 26)
  public BigDecimal erpm = BigDecimal.ZERO;

  @CsvBindByName(column = "eRPI")
  @CsvBindByPosition(position = 27)
  public BigDecimal erpi = BigDecimal.ZERO;


  // Hide For RO users
  @CsvBindByName(column = "Cost")
  @CsvBindByPosition(position = 28)
  @HideForReadOnlyAccess
  public BigDecimal cost = BigDecimal.ZERO;

  @CsvBindByName(column = "Margin")
  @CsvBindByPosition(position = 29)
  @HideForReadOnlyAccess
  public BigDecimal margin = BigDecimal.ZERO;

  @CsvBindByName(column = "CPI")
  @CsvBindByPosition(position = 30)
  @HideForReadOnlyAccess
  public BigDecimal ecpi = BigDecimal.ZERO;

  @CsvBindByName(column = "eCPA")
  @CsvBindByPosition(position = 31)
  @HideForReadOnlyAccess
  public BigDecimal ecpa = BigDecimal.ZERO;

  @CsvBindByName(column = "eCPC")
  @CsvBindByPosition(position = 32)
  @HideForReadOnlyAccess
  public BigDecimal ecpc = BigDecimal.ZERO;

  @CsvBindByName(column = "eCPM")
  @CsvBindByPosition(position = 33)
  @HideForReadOnlyAccess
  public BigDecimal ecpm = BigDecimal.ZERO;



  public void setCalculatedFields() {
    this.conversions = NumberUtils.addBigDecimal(clickConversions, viewConversions);
    this.installs = NumberUtils.addBigDecimal(impInstalls, clickInstalls);
    this.ctr = NumberUtils.divide(NumberUtils.multiply(new BigDecimal(100), clicks), impressions);
    this.ctc = NumberUtils.divide(NumberUtils.multiply(new BigDecimal(100), conversions), clicks);
    this.cvr =
        NumberUtils.divide(NumberUtils.multiply(new BigDecimal(100), conversions), impressions);

    this.ecpm = NumberUtils.divide(NumberUtils.multiply(new BigDecimal(1000), cost), impressions);
    this.ecpc = NumberUtils.divide(cost, clicks);
    this.ecpa = NumberUtils.divide(cost, conversions);

    this.ecpi = NumberUtils.divide(cost, installs);
    this.iti = NumberUtils.divide(impressions, installs);
    BigDecimal marginNum = NumberUtils.substractBigDecimal(revenue, cost);

    this.margin = NumberUtils.divide(NumberUtils.multiply(new BigDecimal(100), marginNum), revenue);
    this.roi = NumberUtils.divide(advRevenue, revenue);
    BigDecimal reach =
        NumberUtils.divide(new BigDecimal(impressionUniqUsers), new BigDecimal(eligibleUniqUsers));
    this.userReach = NumberUtils.multiply(new BigDecimal(100), reach);

    this.erpm =
        NumberUtils.divide(NumberUtils.multiply(new BigDecimal(1000), revenue), impressions);
    this.erpc = NumberUtils.divide(revenue, clicks);
    this.erpa = NumberUtils.divide(revenue, conversions);
    this.erpi = NumberUtils.divide(revenue, installs);
  }

  public void mergeDashBoradData(DashboardData dd) {
    this.impressions = NumberUtils.addBigDecimal(impressions, dd.getImpressions());
    this.clicks = NumberUtils.addBigDecimal(clicks, dd.getClicks());
    this.viewConversions = NumberUtils.addBigDecimal(viewConversions, dd.getViewConversions());
    this.clickConversions = NumberUtils.addBigDecimal(clickConversions, dd.getClickConversions());
    this.impInstalls = NumberUtils.addBigDecimal(impInstalls, dd.impInstalls);
    this.clickInstalls = NumberUtils.addBigDecimal(clickInstalls, dd.clickInstalls);
    this.cost = NumberUtils.addBigDecimal(cost, dd.cost);
    this.revenue = NumberUtils.addBigDecimal(revenue, dd.revenue);
    this.advRevenue = NumberUtils.addBigDecimal(advRevenue, dd.advRevenue);
  }

  public void makeFieldsNullForReadUser() {
    // TODO: Make null by checking Custom RunTime annotation
    this.cost = null;
    this.ecpa = null;
    this.ecpc = null;
   // this.ecpm = null;
    this.ecpi = null;
    this.margin = null;
  }


  public void initialiseWithZero() {
    this.impressions = BigDecimal.ZERO;
    this.clicks = BigDecimal.ZERO;
    this.viewConversions = BigDecimal.ZERO;
    this.clickConversions = BigDecimal.ZERO;
    this.impInstalls = BigDecimal.ZERO;
    this.clickInstalls = BigDecimal.ZERO;
    this.cost = BigDecimal.ZERO;
    this.revenue = BigDecimal.ZERO;
    this.advRevenue = BigDecimal.ZERO;
    this.impressionUniqUsers = 0l;
    this.eligibleUniqUsers = 0l;
    this.eligibleBids = 0l;
    this.bidsPlaced = BigDecimal.ZERO;
    this.invalidClicks = BigDecimal.ZERO;
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

  public BigDecimal getConversions() {
    return conversions;
  }

  public void setConversions(BigDecimal conversions) {
    this.conversions = conversions;
  }

  public BigDecimal getEcpa() {
    return NumberUtils.roundToNDecimalPlace(ecpa);
  }

  public void setEcpa(BigDecimal ecpa) {
    this.ecpa = ecpa;
  }

  public BigDecimal getEcpc() {
    return NumberUtils.roundToNDecimalPlace(ecpc);
  }

  public void setEcpc(BigDecimal ecpc) {
    this.ecpc = ecpc;
  }

  public BigDecimal getEcpm() {
    return NumberUtils.roundToNDecimalPlace(ecpm);
  }

  public void setEcpm(BigDecimal ecpm) {
    this.ecpm = ecpm;
  }

  public BigDecimal getCtr() {
    return NumberUtils.roundToNDecimalPlace(ctr);
  }

  public void setCtr(BigDecimal ctr) {
    this.ctr = ctr;
  }

  public BigDecimal getCtc() {
    return NumberUtils.roundToNDecimalPlace(ctc);
  }

  public void setCtc(BigDecimal ctc) {
    this.ctc = ctc;
  }

  public BigDecimal getCvr() {
    return NumberUtils.roundToNDecimalPlace(cvr);
  }

  public void setCvr(BigDecimal cvr) {
    this.cvr = cvr;
  }

  public BigDecimal getRevenue() {
    return NumberUtils.roundToNDecimalPlace(revenue);
  }

  public void setRevenue(BigDecimal revenue) {
    this.revenue = revenue;
  }

  public BigDecimal getCost() {
    return NumberUtils.roundToNDecimalPlace(cost);
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

  public BigDecimal getMargin() {
    return NumberUtils.roundToNDecimalPlace(margin);
  }

  public void setMargin(BigDecimal margin) {
    this.margin = margin;
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

  public String getCurrencyId() {
    return currencyId;
  }

  public void setCurrencyId(String currencyId) {
    this.currencyId = currencyId;
  }

  public BigDecimal getInstalls() {
    return installs;
  }

  public void setInstalls(BigDecimal installs) {
    this.installs = installs;
  }

  public BigDecimal getEcpi() {
    return NumberUtils.roundToNDecimalPlace(ecpi);
  }

  public void setEcpi(BigDecimal cpi) {
    this.ecpi = cpi;
  }

  public BigDecimal getIti() {
    return NumberUtils.roundToNDecimalPlace(iti);
  }

  public void setIti(BigDecimal iti) {
    this.iti = iti;
  }

  public BigDecimal getClickInstalls() {
    return clickInstalls;
  }

  public void setClickInstalls(BigDecimal clickInstalls) {
    this.clickInstalls = clickInstalls;
  }

  public BigDecimal getImpInstalls() {
    return impInstalls;
  }

  public void setImpInstalls(BigDecimal impInstalls) {
    this.impInstalls = impInstalls;
  }

  public BigDecimal getAdvRevenue() {
    return NumberUtils.roundToNDecimalPlace(advRevenue);
  }

  public void setAdvRevenue(BigDecimal advRevenue) {
    this.advRevenue = advRevenue;
  }

  public BigDecimal getRoi() {
    return NumberUtils.roundToNDecimalPlace(roi);
  }

  public void setRoi(BigDecimal roi) {
    this.roi = roi;
  }

  public Long getImpressionUniqUsers() {
    return impressionUniqUsers;
  }

  public void setImpressionUniqUsers(Long impressionUniqUsers) {
    this.impressionUniqUsers = impressionUniqUsers;
  }

  public Long getEligibleUniqUsers() {
    return eligibleUniqUsers;
  }

  public void setEligibleUniqUsers(Long eligibleUniqUsers) {
    this.eligibleUniqUsers = eligibleUniqUsers;
  }

  public BigDecimal getUserReach() {
    return userReach;
  }

  public void setUserReach(BigDecimal userReach) {
    this.userReach = userReach;
  }

  public Long getEligibleBids() {
    return eligibleBids;
  }

  public void setEligibleBids(Long eligibleBids) {
    this.eligibleBids = eligibleBids;
  }

  public BigDecimal getBidsPlaced() {
    return bidsPlaced;
  }

  public void setBidsPlaced(BigDecimal bidsPlaced) {
    this.bidsPlaced = bidsPlaced;
  }

  public BigDecimal getInvalidClicks() {
    return invalidClicks;
  }

  public void setInvalidClicks(BigDecimal invalidClicks) {
    this.invalidClicks = invalidClicks;
  }

  public BigDecimal getErpa() {
    return NumberUtils.roundToNDecimalPlace(erpa);
  }

  public void setErpa(BigDecimal erpa) {
    this.erpa = erpa;
  }

  public BigDecimal getErpc() {
    return NumberUtils.roundToNDecimalPlace(erpc);
  }

  public void setErpc(BigDecimal erpc) {
    this.erpc = erpc;
  }

  public BigDecimal getErpm() {
    return NumberUtils.roundToNDecimalPlace(erpm);
  }

  public void setErpm(BigDecimal erpm) {
    this.erpm = erpm;
  }

  public BigDecimal getErpi() {
    return NumberUtils.roundToNDecimalPlace(erpi);
  }

  public void setErpi(BigDecimal erpi) {
    this.erpi = erpi;
  }

  @Override
  public String toString() {
    return "DashboardMetrics [impressions=" + impressions + ", clicks=" + clicks + ", conversions="
        + conversions + ", ctr=" + ctr + ", ctc=" + ctc + ", cvr=" + cvr + ", iti=" + iti
        + ", revenue=" + revenue + ", viewConversions=" + viewConversions + ", clickConversions="
        + clickConversions + ", currencyId=" + currencyId + ", installs=" + installs
        + ", clickInstalls=" + clickInstalls + ", impInstalls=" + impInstalls + ", invalidClicks="
        + invalidClicks + ", advRevenue=" + advRevenue + ", roi=" + roi + ", impressionUniqUsers="
        + impressionUniqUsers + ", eligibleUniqUsers=" + eligibleUniqUsers + ", userReach="
        + userReach + ", eligibleBids=" + eligibleBids + ", bidsPlaced=" + bidsPlaced + ", erpa="
        + erpa + ", erpc=" + erpc + ", erpm=" + erpm + ", erpi=" + erpi + ", cost=" + cost
        + ", margin=" + margin + ", ecpi=" + ecpi + ", ecpa=" + ecpa + ", ecpc=" + ecpc + ", ecpm="
        + ecpm + "]";
  }

}
