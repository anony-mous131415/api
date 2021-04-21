package io.revx.querybuilder.objs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class PerformanceDataTable extends CalculatedFields {

  private static PerformanceDataTable instance;
  private final Set<String> hllFields = new HashSet<>();

  private PerformanceDataTable() {
    this.tableName = "performance_li_main";
    this.fields = new LinkedHashMap<String, String>();
    this.columns = new ArrayList<String>();
    this.fields.put("impressions", "sum(impressions)");
    this.fields.put("clicks", "sum(clicks)");
    this.fields.put("viewconversions", "sum(view_conversion)");
    this.fields.put("clickconversions", "sum(click_conversion)");

    this.fields.put("viewinstalls", "sum(view_install)");
    this.fields.put("clickinstalls", "sum(click_install)");

    this.fields.put("revenueinadvertisercurrency", "sum(revenue_in_advertiser_currency)");
    this.fields.put("revenueinlicenseecurrency", "sum(revenue_in_licensee_cureency)");
    this.fields.put("revenueinplatformcurrency", "sum(revenue_in_platform_currency)");

    this.fields.put("costinadvertisercurrency", "sum(cost_in_advertiser_currency)");
    this.fields.put("costinlicenseecurrency", "sum(cost_in_licensee_currency)");
    this.fields.put("costinplatformcurrency", "sum(cost_in_platform_currency)");

    this.fields.put("txnamountinadvertisercurrency", "sum(txn_amt_in_advertiser_currency)");
    this.fields.put("txnamountinlicenseecurrency", "sum(txn_amt_in_license_currency)");
    this.fields.put("txnamountinplatformcurrency", "sum(txn_amt_in_platform_currency)");
    this.fields.put("bidsplaced", "sum(bids_placed)");
    this.fields.put("invalidclicks", "sum(invalid_clicks)");
    this.fields.put("impressionuniqusers",
        "HLL_COUNT.MERGE(imp_uu_hll)");
    hllFields.add("impressionuniqusers");
    this.fields.put("eligibleuniqusers",
        "HLL_COUNT.MERGE(eligible_uu_hll)");
    hllFields.add("eligibleuniqusers");
    this.fields.put("eligiblebids",
        "HLL_COUNT.MERGE(eligible_bids_hll)");
    hllFields.add("eligiblebids");
  }

  public static PerformanceDataTable getInstance() {
    if (instance == null) {
      instance = new PerformanceDataTable();
    }

    return instance;
  }

  public String getSelectClause(boolean showUU) {

    StringBuilder sb = new StringBuilder();

    Set<String> keySet = this.fields.keySet();

    for (String fieldName : keySet) {
      if (!showUU && hllFields.contains(fieldName)) {
        continue;
      }
      String formula = this.fields.get(fieldName);
      sb.append(formula);
      sb.append(" AS ");
      sb.append(fieldName);
      sb.append(", ");
      this.columns.add(fieldName);
    }

    sb.delete(sb.length() - 2, sb.length() - 1);

    return sb.toString();

  }
}
