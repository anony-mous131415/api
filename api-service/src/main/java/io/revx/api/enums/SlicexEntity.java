package io.revx.api.enums;


public enum SlicexEntity {
  licensee("Licensee", "ln_id", "licensee"), 
  advertiser("Advertiser", "adv_id", "advertiser"), 
  campaign("Campaign", "io_id", "campaign"), 
  strategy("Strategy", "li_id", "strategy"), 
  creativeSize("Creative Size", "cr_size.keyword", "NA"), 
  aggregator("Aggregator", "agg_id", "aggregator"), 
  app("App", "site_id", "site"), 
  os("OS", "os_id", "os"), 
  creative("Creative Name", "cr_id", "creative"), 
  country("Country", "cntr_id", "country"), 
  pricing("Pricing Type", "pricing_type", "pricing"), 
  pixel("Pixel Id", "pixel_id.keyword", "NA"),
  // REVX-300 : new dimesions in slicex
  campaignObjective("Campaign Objective", "camp_obj.keyword", "NA"), 
  advregions("Advertiser Region", "advertiser_region", "advertiser_region");

  private String uiDisplayName;

  private String esColumnName;

  private String entityEsIndexName;


  private SlicexEntity(String uiDisplayName, String esColumnName, String entityEsIndexName) {
    this.uiDisplayName = uiDisplayName;
    this.esColumnName = esColumnName;
    this.entityEsIndexName = entityEsIndexName;
  }

  public String getUiDisplayName() {
    return uiDisplayName;
  }

  public String getEsColumnName() {
    return esColumnName;
  }

  public String getEntityEsIndexName() {
    return entityEsIndexName;
  }

}
