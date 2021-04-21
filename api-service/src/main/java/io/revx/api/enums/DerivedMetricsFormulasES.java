//package io.revx.api.enums;
//
//import java.util.Arrays;
//import java.util.List;
//
//public enum DerivedMetricsFormulasES {
//  conversions(MetricsType.conversions, Arrays.asList(MetricsType.impConversions, MetricsType.clickConversions),"return params." + MetricsType.impConversions.toString() + "+params." + MetricsType.clickConversions.toString()+";"), 
//  ctr(MetricsType.ctr, Arrays.asList(MetricsType.impressions, MetricsType.clicks),"if (params."+MetricsType.impressions.toString()+" == 0){return 0.0;} else {return (params."+MetricsType.clicks.toString()+"*100.0)/params."+MetricsType.impressions.toString()+";}"), 
//  cvr(MetricsType.cvr, Arrays.asList(MetricsType.impressions, MetricsType.impConversions, MetricsType.clickConversions),"if (params."+MetricsType.impressions.toString()+" == 0){return 0.0;} else {return (Math.round(((params."+MetricsType.impConversions.toString()+"+params."+MetricsType.clickConversions.toString()+")*100000.0)*100.0)/100.0)/params."+MetricsType.impressions.toString()+";}"), 
//  ctc(MetricsType.ctc,Arrays.asList(MetricsType.clicks, MetricsType.clickConversions) ,"if(params."+MetricsType.clicks.toString()+" == 0) {return 0.0;} else {return params."+MetricsType.clickConversions.toString()+"*100.0/params."+MetricsType.clicks.toString()+"}"), 
//  revenueEcpm(MetricsType.revenueEcpm,Arrays.asList(MetricsType.impressions, MetricsType.revenue) ,"if(params."+ MetricsType.impressions.toString() +" == 0) {return 0.0;} else {return ((Math.round(params."+ MetricsType.revenue.toString() +"*100.0)/100.0)*1000.0)/params."+ MetricsType.impressions.toString() +";}"), 
//  revenueEcpc(MetricsType.revenueEcpc,Arrays.asList(MetricsType.clicks, MetricsType.revenue) ,"if(params."+ MetricsType.clicks.toString() +" == 0) {return 0.0;} else {return (Math.round(params."+ MetricsType.revenue.toString() +"*100.0)/100.0)/params."+ MetricsType.clicks.toString() +";}"), 
//  revenueEcpa(MetricsType.revenueEcpa,Arrays.asList(MetricsType.clickConversions, MetricsType.revenue) ,"if(params."+ MetricsType.clickConversions +" == 0) {return 0.0;} else { return (Math.round(params."+ MetricsType.revenue.toString() +"*100.0)/100.0)/params."+ MetricsType.clickConversions.toString() +";}"), 
//  costEcpm(MetricsType.costEcpm,Arrays.asList(MetricsType.impressions, MetricsType.cost) ,"if(params."+ MetricsType.impressions.toString() +" == 0) {return 0.0;} else {return ((Math.round(params."+ MetricsType.cost.toString() +"*100.0)/100.0)*1000.0)/params."+ MetricsType.impressions.toString() +";}"), 
//  costEcpc(MetricsType.costEcpc,Arrays.asList(MetricsType.clicks, MetricsType.cost) ,"if(params."+ MetricsType.clicks.toString() +" == 0) {return 0.0;} else {return (Math.round(params."+ MetricsType.cost.toString() +"*100.0)/100.0)/params."+ MetricsType.clicks.toString() +";}"), 
//  costEcpa(MetricsType.costEcpa,Arrays.asList(MetricsType.clickConversions, MetricsType.cost) ,"if(params."+ MetricsType.clickConversions +" == 0) {return 0.0;} else { return (Math.round(params."+ MetricsType.cost.toString() +"*100.0)/100.0)/params."+ MetricsType.clickConversions.toString() +";}"), 
//  margin(MetricsType.margin,Arrays.asList(MetricsType.revenue, MetricsType.cost) ,"return params." + MetricsType.revenue.toString() + "-params." + MetricsType.cost.toString()+";"), 
//  marginPercentage(MetricsType.marginPercentage,Arrays.asList(MetricsType.revenue, MetricsType.cost) ,"if(params."+ MetricsType.revenue.toString() +" == 0) {return 0.0;} else { return ((params."+ MetricsType.revenue.toString() +"-params."+ MetricsType.cost.toString() +")*100.0)/(Math.round(params."+ MetricsType.revenue.toString() +"*100.0)/100.0);}"), 
//  impPerConversion(MetricsType.impPerConversion,Arrays.asList(MetricsType.impressions, MetricsType.clickConversions) ,"if(params."+ MetricsType.clickConversions.toString() +" == 0) {return 0.0;} else { return params."+ MetricsType.impressions.toString() +"/params."+ MetricsType.clickConversions.toString() +";}"),
//  installs(MetricsType.installs,Arrays.asList(MetricsType.impInstalls, MetricsType.clickInstalls) ,"return params."+ MetricsType.impInstalls.toString() + "+params."+ MetricsType.clickInstalls.toString() +";"),
//  cpi(MetricsType.cpi, Arrays.asList(MetricsType.impInstalls, MetricsType.clickInstalls, MetricsType.revenue),"if ((params."+ MetricsType.impInstalls.toString() +"+params."+ MetricsType.clickInstalls.toString() +")==0) {return 0;} else {return (Math.round(params."+ MetricsType.revenue.toString() +"*100.0)/100.0)/(params."+ MetricsType.impInstalls.toString() +"+params."+ MetricsType.clickInstalls.toString() +");}"),
//  iti(MetricsType.iti,Arrays.asList(MetricsType.impInstalls, MetricsType.clickInstalls, MetricsType.impressions) ,"if ((params."+MetricsType.impInstalls.toString()+"+params."+MetricsType.clickInstalls.toString()+") == 0){return 0.0;} else {return (params."+MetricsType.impressions.toString()+")/(params."+MetricsType.impInstalls.toString()+"+params."+MetricsType.clickInstalls.toString()+");}");
//  
//  private MetricsType mType;
//
//  private String formula;
//  
//  private List<MetricsType> dependentMetrics;
//  
//  private DerivedMetricsFormulasES(MetricsType mtype, List<MetricsType> dependentMetrics, String formula) {
//      this.mType = mtype;
//      this.dependentMetrics = dependentMetrics;
//      this.formula = formula;
//  }
//
//  public MetricsType getMetricType() {
//      return mType;
//  }
//
//  public String getFormula() {
//      return formula;
//  }
//
//  public static DerivedMetricsFormulasES getDerivedMetricsFormulas(MetricsType mtype) {
//
//      for (DerivedMetricsFormulasES mp : DerivedMetricsFormulasES.values()) {
//          if (mp.mType == mtype)
//              return mp;
//      }
//
//      return null;
//  }
//  
//  public List<MetricsType> getDependentMetrics() {
//      return dependentMetrics;
//  }
//}
