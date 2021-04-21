//package io.revx.api.enums;
//
//public enum MetricsType {
//
//
//  revenue("Revenue", "currency", MType.base, 1), cost("Cost", "currency", MType.base, 2), margin(
//      "Margin", "currency", MType.derived,
//      3), ctr("CTR", "percentage", MType.derived, 4), ctc("CTC", "percentage", MType.derived,
//          5), impressions("Impressions", "integer", MType.base, 6), clicks("Clicks", "integer",
//              MType.base, 7), installs("Installs", "integer", MType.derived, 8), impInstalls(
//                  "View Installs", "integer", MType.base,
//                  9), clickInstalls("Click Installs", "integer", MType.base, 10), conversions(
//                      "Conversions", "integer", MType.derived,
//                      11), clickConversions("Click Conversions", "integer", MType.base,
//                          12), impConversions("Imp Conversions", "integer", MType.base, 13), cvr(
//                              "CVR", "percentage", MType.derived,
//                              14), costEcpm("Cost eCPM", "currency", MType.derived, 15), costEcpc(
//                                  "Cost eCPC", "currency", MType.derived,
//                                  16), cpi("CPI", "currency", MType.derived, 17), costEcpa(
//                                      "Cost eCPA", "currency", MType.derived,
//                                      18), iti("ITI", "integer", MType.derived, 19), revenueEcpm(
//                                          "Revenue eCPM", "currency", MType.derived,
//                                          20), revenueEcpc("Revenue eCPC", "currency",
//                                              MType.derived, 21), revenueEcpa("Revenue eCPA",
//                                                  "currency", MType.derived, 22), marginPercentage(
//                                                      "Margin %", "percentage", MType.derived,
//                                                      23), impPerConversion("Imp per Conversion",
//                                                          "integer", MType.derived,
//                                                          24), revenue_in_pc("Revenue", "currency",
//                                                              MType.base, 25), cost_in_pc("Cost",
//                                                                  "currency", MType.base,
//                                                                  26), revenue_in_lc("Revenue",
//                                                                      "currency", MType.base,
//                                                                      27), cost_in_lc("Cost",
//                                                                          "currency", MType.base,
//                                                                          28), revenue_in_ac(
//                                                                              "Revenue", "currency",
//                                                                              MType.base,
//                                                                              29), cost_in_ac(
//                                                                                  "Cost",
//                                                                                  "currency",
//                                                                                  MType.base, 30);
//
//
//
//  private String uiDisplayName;
//  private String displayType;
//  private MType type;
//  private Integer orderInUI;
//
//  private MetricsType(String uiDisplayName, String displayType, MType type, Integer orderInUI) {
//    this.uiDisplayName = uiDisplayName;
//    this.displayType = displayType;
//    this.type = type;
//    this.orderInUI = orderInUI;
//  }
//
//  public String getUiDisplayName() {
//    return uiDisplayName;
//  }
//
//  public String getDisplayType() {
//    return displayType;
//  }
//
//  public MType getMType() {
//    return this.type;
//  }
//
//  public Integer getOrderInUI() {
//    return orderInUI;
//  }
//
//}
//
