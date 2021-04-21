package io.revx.api.controller.strategy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;

public class BulkStrategiesDataDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @CsvBindByName(column = "AdvertiserId")
  @CsvBindByPosition(position = 0)
  public Long advertiserId;

  @CsvBindByName(column = "AdvertiserName")
  @CsvBindByPosition(position = 1)
  public String advertiserName;

  @CsvBindByName(column = "CampaignId")
  @CsvBindByPosition(position = 2)
  public Long campaignId;

  @CsvBindByName(column = "CampaignName")
  @CsvBindByPosition(position = 3)
  public String campaignName;

  @CsvBindByName(column = "StrategyId")
  @CsvBindByPosition(position = 4)
  public Long strategyId;

  @CsvBindByName(column = "StrategyName")
  @CsvBindByPosition(position = 5)
  public String strategyName;


  @CsvBindByName(column = "BidType")
  @CsvBindByPosition(position = 6)
  public String bidType;

  @CsvBindByName(column = "BidPrice")
  @CsvBindByPosition(position = 7)
  public BigDecimal bidPrice;

  @CsvBindByName(column = "Currency")
  @CsvBindByPosition(position = 8)
  public String bidPriceCurrency;

  @CsvBindByName(column = "FrequencyCap")
  @CsvBindByPosition(position = 9)
  public Long fCap;

  @CsvBindByName(column = "BidMin")
  @CsvBindByPosition(position = 10)
  public BigDecimal bidCapMin;

  @CsvBindByName(column = "BidMax")
  @CsvBindByPosition(position = 11)
  public BigDecimal bidCapMax;

  @CsvBindByName(column = "Impressions")
  @CsvBindByPosition(position = 12)
  public BigDecimal impressions;

  @CsvBindByName(column = "Clicks")
  @CsvBindByPosition(position = 13)
  public BigDecimal clicks;

  @CsvBindByName(column = "Conversions")
  @CsvBindByPosition(position = 14)
  public BigDecimal conversions;

  @CsvBindByName(column = "ClickConversions")
  @CsvBindByPosition(position = 15)
  public BigDecimal clickConversions;

  @CsvBindByName(column = "ViewConversions")
  @CsvBindByPosition(position = 16)
  public BigDecimal viewConversions;

  @CsvBindByName(column = "ECPM")
  @CsvBindByPosition(position = 17)
  public BigDecimal costECPM;

  @CsvBindByName(column = "ECPC")
  @CsvBindByPosition(position = 18)
  public BigDecimal costECPC;

  @CsvBindByName(column = "ECPA")
  @CsvBindByPosition(position = 19)
  public BigDecimal costECPA;

  @CsvBindByName(column = "AdvertiserSpend")
  @CsvBindByPosition(position = 20)
  public BigDecimal adSpend;

  @CsvBindByName(column = "MediaSpend")
  @CsvBindByPosition(position = 21)
  public BigDecimal mediaSpend;

  @CsvBindByName(column = "Margin")
  @CsvBindByPosition(position = 22)
  public BigDecimal margin;

  @CsvBindByName(column = "Installs")
  @CsvBindByPosition(position = 23)
  public BigDecimal installs;

  @CsvBindByName(column = "CPI")
  @CsvBindByPosition(position = 24)
  public BigDecimal cpi;

  @CsvBindByName(column = "ITI")
  @CsvBindByPosition(position = 25)
  public BigDecimal iti;

  @CsvBindByName(column = "ROI")
  @CsvBindByPosition(position = 26)
  public BigDecimal roi;



  public Long getAdvertiserId() {
    return advertiserId;
  }

  public void setAdvertiserId(Long advertiserId) {
    this.advertiserId = advertiserId;
  }

  public String getAdvertiserName() {
    return advertiserName;
  }

  public void setAdvertiserName(String advertiserName) {
    this.advertiserName = advertiserName;
  }

  public Long getStrategyId() {
    return strategyId;
  }

  public void setStrategyId(Long strategyId) {
    this.strategyId = strategyId;
  }

  public String getStrategyName() {
    return strategyName;
  }

  public void setStrategyName(String strategyName) {
    this.strategyName = strategyName;
  }

  public Long getCampaignId() {
    return campaignId;
  }

  public void setCampaignId(Long campaignId) {
    this.campaignId = campaignId;
  }

  public String getCampaignName() {
    return campaignName;
  }

  public void setCampaignName(String campaignName) {
    this.campaignName = campaignName;
  }

  public String getBidType() {
    return bidType;
  }

  public void setBidType(String bidType) {
    this.bidType = bidType;
  }

  public BigDecimal getBidPrice() {
    return bidPrice;
  }

  public void setBidPrice(BigDecimal bidPrice) {
    this.bidPrice = bidPrice;
  }

  public String getBidPriceCurrency() {
    return bidPriceCurrency;
  }

  public void setBidPriceCurrency(String bidPriceCurrency) {
    this.bidPriceCurrency = bidPriceCurrency;
  }

  public Long getfCap() {
    return fCap;
  }

  public void setfCap(Long fCap) {
    this.fCap = fCap;
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

  public BigDecimal getClickConversions() {
    return clickConversions;
  }

  public void setClickConversions(BigDecimal clickConversions) {
    this.clickConversions = clickConversions;
  }

  public BigDecimal getViewConversions() {
    return viewConversions;
  }

  public void setViewConversions(BigDecimal viewConversions) {
    this.viewConversions = viewConversions;
  }

  public BigDecimal getCostECPM() {
    return costECPM;
  }

  public void setCostECPM(BigDecimal costECPM) {
    this.costECPM = costECPM;
  }

  public BigDecimal getCostECPC() {
    return costECPC;
  }

  public void setCostECPC(BigDecimal costECPC) {
    this.costECPC = costECPC;
  }

  public BigDecimal getCostECPA() {
    return costECPA;
  }

  public void setCostECPA(BigDecimal costECPA) {
    this.costECPA = costECPA;
  }

  public BigDecimal getAdSpend() {
    return adSpend;
  }

  public void setAdSpend(BigDecimal adSpend) {
    this.adSpend = adSpend;
  }

  public BigDecimal getMediaSpend() {
    return mediaSpend;
  }

  public void setMediaSpend(BigDecimal mediaSpend) {
    this.mediaSpend = mediaSpend;
  }

  public BigDecimal getMargin() {
    return margin;
  }

  public void setMargin(BigDecimal margin) {
    this.margin = margin;
  }

  public BigDecimal getInstalls() {
    return installs;
  }

  public void setInstalls(BigDecimal installs) {
    this.installs = installs;
  }

  public BigDecimal getCpi() {
    return cpi;
  }

  public void setCpi(BigDecimal cpi) {
    this.cpi = cpi;
  }

  public BigDecimal getIti() {
    return iti;
  }

  public void setIti(BigDecimal iti) {
    this.iti = iti;
  }

  public BigDecimal getRoi() {
    return roi;
  }

  public void setRoi(BigDecimal roi) {
    this.roi = roi;
  }

  public BigDecimal getBidCapMin() {
    return bidCapMin;
  }

  public void setBidCapMin(BigDecimal bidCapMin) {
    this.bidCapMin = bidCapMin;
  }

  public BigDecimal getBidCapMax() {
    return bidCapMax;
  }

  public void setBidCapMax(BigDecimal bidCapMax) {
    this.bidCapMax = bidCapMax;
  }

  public BulkStrategiesDataDTO() {

  }


  public BulkStrategiesDataDTO(Long advertiserId, String advertiserName, Long strategyId,
      String strategyName, Long campaignId, String campaignName, String bidType,
      BigDecimal bidPrice, String bidPriceCurrency, Long fCap, BigDecimal bidCapMin,
      BigDecimal bidCapMax, BigDecimal impressions, BigDecimal clicks, BigDecimal conversions,
      BigDecimal clickConversions, BigDecimal viewConversions, BigDecimal costECPM,
      BigDecimal costECPC, BigDecimal costECPA, BigDecimal adSpend, BigDecimal mediaSpend,
      BigDecimal margin, BigDecimal installs, BigDecimal cpi, BigDecimal iti, BigDecimal roi) {
    super();
    this.advertiserId = advertiserId;
    this.advertiserName = advertiserName;
    this.strategyId = strategyId;
    this.strategyName = strategyName;
    this.campaignId = campaignId;
    this.campaignName = campaignName;
    this.bidType = bidType;
    this.bidPrice = bidPrice;
    this.bidPriceCurrency = bidPriceCurrency;
    this.fCap = fCap;
    this.bidCapMin = bidCapMin;
    this.bidCapMax = bidCapMax;
    this.impressions = impressions;
    this.clicks = clicks;
    this.conversions = conversions;
    this.clickConversions = clickConversions;
    this.viewConversions = viewConversions;
    this.costECPM = costECPM;
    this.costECPC = costECPC;
    this.costECPA = costECPA;
    this.adSpend = adSpend;
    this.mediaSpend = mediaSpend;
    this.margin = margin;
    this.installs = installs;
    this.cpi = cpi;
    this.iti = iti;
    this.roi = roi;
  }

  /**
   * creating strategydataDTO set to organize the tsv file header
   * 
   * @return
   */
  public Set<String> getstrategiesDataDTOSet() {

    Set<String> strategiesDataDTOSet = new LinkedHashSet<String>();
    strategiesDataDTOSet.add("advertiserId");
    strategiesDataDTOSet.add("advertiserName");
    strategiesDataDTOSet.add("campaignId");
    strategiesDataDTOSet.add("campaignName");
    strategiesDataDTOSet.add("strategyId");
    strategiesDataDTOSet.add("strategyName");
    // strategiesDataDTOSet.add("lastModifiedDate");
    strategiesDataDTOSet.add("fCap");
    strategiesDataDTOSet.add("bidCapMin");
    strategiesDataDTOSet.add("bidCapMax");
    strategiesDataDTOSet.add("bidType");
    strategiesDataDTOSet.add("bidPrice");
    strategiesDataDTOSet.add("bidPriceCurrency");
    strategiesDataDTOSet.add("impressions");
    strategiesDataDTOSet.add("clicks");
    strategiesDataDTOSet.add("installs");
    strategiesDataDTOSet.add("conversions");
    strategiesDataDTOSet.add("clickConversions");
    strategiesDataDTOSet.add("viewConversions");
    strategiesDataDTOSet.add("costECPM");
    strategiesDataDTOSet.add("costECPC");
    strategiesDataDTOSet.add("cpi");
    strategiesDataDTOSet.add("costECPA");
    strategiesDataDTOSet.add("iti");
    strategiesDataDTOSet.add("roi");
    strategiesDataDTOSet.add("adSpend");
    strategiesDataDTOSet.add("mediaSpend");
    strategiesDataDTOSet.add("margin");



    return strategiesDataDTOSet;
  }



}
