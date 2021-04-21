package io.revx.api.enums;

import java.util.Arrays;
import java.util.List;

public enum SlicexMetricsEnum {
  // Base metrics
  impressions("impressions", MType.base, "impressions", null, null),

  clicks("clicks", MType.base, "clicks", null, null),

  clickConversions("click_conversions", MType.base, "clickConversions", null, null),

  viewConversions("imp_conversions", MType.base, "viewConversions", null, null),

  revenue("revx_revenue", MType.base, "revenue", null, null),

  cost("revx_cost", MType.base, "cost", null, null),

  impInstalls("imp_installs", MType.base, "impInstalls", null, null),

  clickInstalls("click_installs", MType.base, "clickInstalls", null, null),

  revenue_in_lc("revenue_in_licensee_currency", MType.base, "revenue", null, null),

  cost_in_lc("cost_in_licensee_currency", MType.base, "cost", null, null),

  revenue_in_ac("revenue_in_advertiser_currency", MType.base, "revenue", null, null),

  cost_in_ac("cost_in_advertiser_currency", MType.base, "cost", null, null),
  
  revenue_in_pc("revenue_in_platform_currency", MType.base, "revenue", null, null),

  cost_in_pc("cost_in_platform_currency", MType.base, "cost", null, null),


  // Derived Metrics
  conversions(null, MType.derived, "conversions",
      Arrays.asList(SlicexMetricsEnum.viewConversions, SlicexMetricsEnum.clickConversions),
      "return params." + SlicexMetricsEnum.viewConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + ";"),

  installs(null, MType.derived, "installs",
      Arrays.asList(SlicexMetricsEnum.impInstalls, SlicexMetricsEnum.clickInstalls),
      "return params." + SlicexMetricsEnum.impInstalls.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickInstalls.getEsQueryAlias() + ";"),

  ctr(null, MType.derived, "ctr",
      Arrays.asList(SlicexMetricsEnum.impressions, SlicexMetricsEnum.clicks),
      "if (params." + SlicexMetricsEnum.impressions.getEsQueryAlias()
          + " == 0){return 0.0;} else {return (params." + SlicexMetricsEnum.clicks.getEsQueryAlias()
          + "*100.0)/params." + SlicexMetricsEnum.impressions.getEsQueryAlias() + ";}"),

  ctc(null, MType.derived, "ctc",
      Arrays.asList(SlicexMetricsEnum.viewConversions, SlicexMetricsEnum.clickConversions,
          SlicexMetricsEnum.clicks),
      "if(params." + SlicexMetricsEnum.clicks.getEsQueryAlias()
          + " == 0) {return 0.0;} else {return (params."
          + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.viewConversions.getEsQueryAlias() + ")*100.0/params."
          + SlicexMetricsEnum.clicks.getEsQueryAlias() + ";}"),

  cvr(null, MType.derived, "cvr",
      Arrays.asList(SlicexMetricsEnum.viewConversions, SlicexMetricsEnum.clickConversions,
          SlicexMetricsEnum.impressions),
      "if(params." + SlicexMetricsEnum.impressions.getEsQueryAlias()
          + " == 0){return 0.0;} else {return (params."
          + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.viewConversions.getEsQueryAlias() + ")*100.0/params."
          + SlicexMetricsEnum.impressions.getEsQueryAlias() + ";}"),

  erpm(null, MType.derived, "erpm",
      Arrays.asList(SlicexMetricsEnum.impressions, SlicexMetricsEnum.revenue),
      "if(params." + SlicexMetricsEnum.impressions.getEsQueryAlias()
          + " == 0) {return 0.0;} else {return ((Math.round(params."
          + SlicexMetricsEnum.revenue.getEsQueryAlias() + "*100.0)/100.0)*1000.0)/params."
          + SlicexMetricsEnum.impressions.getEsQueryAlias() + ";}"),

  erpc(null, MType.derived, "erpc",
      Arrays.asList(SlicexMetricsEnum.clicks, SlicexMetricsEnum.revenue),
      "if(params." + SlicexMetricsEnum.clicks.getEsQueryAlias()
          + " == 0) {return 0.0;} else {return (Math.round(params."
          + SlicexMetricsEnum.revenue.getEsQueryAlias() + "*100.0)/100.0)/params."
          + SlicexMetricsEnum.clicks.getEsQueryAlias() + ";}"),

  erpa(null, MType.derived, "erpa",
      Arrays.asList(SlicexMetricsEnum.clickConversions, SlicexMetricsEnum.viewConversions,
          SlicexMetricsEnum.revenue),
      "if((params." + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.viewConversions.getEsQueryAlias()
          + ")==0) {return 0.0;} else { return (Math.round(params."
          + SlicexMetricsEnum.revenue.getEsQueryAlias() + "*100.0)/100.0)/(params."
          + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.viewConversions.getEsQueryAlias() + ");}"),

  erpi(null, MType.derived, "erpi",
      Arrays.asList(SlicexMetricsEnum.clickInstalls, SlicexMetricsEnum.impInstalls,
          SlicexMetricsEnum.revenue),
      "if((params." + SlicexMetricsEnum.impInstalls.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickInstalls.getEsQueryAlias()
          + ")==0) {return 0.0;} else { return (Math.round(params."
          + SlicexMetricsEnum.revenue.getEsQueryAlias() + "*100.0)/100.0)/(params."
          + SlicexMetricsEnum.impInstalls.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickInstalls.getEsQueryAlias() + ");}"),

  ecpm(null, MType.derived, "ecpm",
      Arrays.asList(SlicexMetricsEnum.impressions, SlicexMetricsEnum.cost),
      "if(params." + SlicexMetricsEnum.impressions.getEsQueryAlias()
          + " == 0) {return 0.0;} else {return ((Math.round(params."
          + SlicexMetricsEnum.cost.getEsQueryAlias() + "*100.0)/100.0)*1000.0)/params."
          + SlicexMetricsEnum.impressions.getEsQueryAlias() + ";}"),

  ecpc(null, MType.derived, "ecpc", Arrays.asList(SlicexMetricsEnum.clicks, SlicexMetricsEnum.cost),
      "if(params." + SlicexMetricsEnum.clicks.getEsQueryAlias()
          + " == 0) {return 0.0;} else {return (Math.round(params."
          + SlicexMetricsEnum.cost.getEsQueryAlias() + "*100.0)/100.0)/params."
          + SlicexMetricsEnum.clicks.getEsQueryAlias() + ";}"),

  ecpa(null, MType.derived, "ecpa",
      Arrays.asList(SlicexMetricsEnum.clickConversions, SlicexMetricsEnum.viewConversions,
          SlicexMetricsEnum.cost),
      "if((params." + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.viewConversions.getEsQueryAlias()
          + ")==0) {return 0.0;} else { return (Math.round(params."
          + SlicexMetricsEnum.cost.getEsQueryAlias() + "*100.0)/100.0)/(params."
          + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.viewConversions.getEsQueryAlias() + ");}"),

  ecpi(null, MType.derived, "ecpi",
      Arrays.asList(SlicexMetricsEnum.clickInstalls, SlicexMetricsEnum.impInstalls,
          SlicexMetricsEnum.cost),
      "if((params." + SlicexMetricsEnum.impInstalls.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickInstalls.getEsQueryAlias()
          + ")==0) {return 0.0;} else { return (Math.round(params."
          + SlicexMetricsEnum.cost.getEsQueryAlias() + "*100.0)/100.0)/(params."
          + SlicexMetricsEnum.impInstalls.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickInstalls.getEsQueryAlias() + ");}"),

  iti(null, MType.derived, "iti",
      Arrays.asList(SlicexMetricsEnum.clickInstalls, SlicexMetricsEnum.impInstalls,
          SlicexMetricsEnum.impressions),
      "if((params." + SlicexMetricsEnum.impInstalls.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickInstalls.getEsQueryAlias()
          + ") == 0){return 0.0;} else {return (params."
          + SlicexMetricsEnum.impressions.getEsQueryAlias() + ")/(params."
          + SlicexMetricsEnum.impInstalls.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickInstalls.getEsQueryAlias() + ");}"),

  margin(null, MType.derived, "margin",
      Arrays.asList(SlicexMetricsEnum.revenue, SlicexMetricsEnum.cost),
      "return params." + SlicexMetricsEnum.revenue.getEsQueryAlias() + "-params."
          + SlicexMetricsEnum.cost.getEsQueryAlias() + ";"),

  marginPercentage(null, MType.derived, "marginPercentage",
      Arrays.asList(SlicexMetricsEnum.revenue, SlicexMetricsEnum.cost),
      "if(params." + SlicexMetricsEnum.revenue.getEsQueryAlias()
          + "== 0) {return 0.0;} else {return ((params."
          + SlicexMetricsEnum.revenue.getEsQueryAlias() + "-params."
          + SlicexMetricsEnum.cost.getEsQueryAlias() + ")*100.0)/params."
          + SlicexMetricsEnum.revenue.getEsQueryAlias() + ";}"),

  impPerConversion(null, MType.derived, "impPerConversion",
      Arrays.asList(SlicexMetricsEnum.clickConversions, SlicexMetricsEnum.viewConversions,
          SlicexMetricsEnum.impressions),
      "if((params." + SlicexMetricsEnum.viewConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickConversions.getEsQueryAlias()
          + ") == 0){return 0.0;} else {return (params."
          + SlicexMetricsEnum.impressions.getEsQueryAlias() + ")/(params."
          + SlicexMetricsEnum.viewConversions.getEsQueryAlias() + "+params."
          + SlicexMetricsEnum.clickConversions.getEsQueryAlias() + ");}");

  private String esColumnName;
  private MType type;
  private String esQueryAlias;
  private List<SlicexMetricsEnum> dependentMetrics;
  private String formula;


  private SlicexMetricsEnum(String esColumnName, MType type, String esQueryAlias,
      List<SlicexMetricsEnum> dependentMetrics, String formula) {
    this.esColumnName = esColumnName;
    this.type = type;
    this.esQueryAlias = esQueryAlias;
    this.dependentMetrics = dependentMetrics;
    this.formula = formula;
  }

  public String getEsColumnName() {
    return esColumnName;
  }

  public MType getType() {
    return type;
  }

  public String getEsQueryAlias() {
    return esQueryAlias;
  }

  public List<SlicexMetricsEnum> getDependentMetrics() {
    return dependentMetrics;
  }

  public String getFormula() {
    return formula;
  }
}
