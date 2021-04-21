/**
 * 
 */
package io.revx.querybuilder.query;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.enums.Interval;
import io.revx.querybuilder.objs.FilterComponent;
import mockit.Injectable;
import mockit.Tested;

/**
 * @author amaurya
 *
 */
public class PerfTableQueryTest {
  @Injectable
  private long endTime;

  @Injectable
  private Set<FilterComponent> filters;

  @Injectable
  private GroupBy groupby;

  @Injectable
  private long startTime;
  @Tested
  private PerfTableQuery perfTableQuery;

  /**
   * Test method for
   * {@link io.revx.querybuilder.query.PerfTableQuery#PerfTableQuery(io.revx.querybuilder.enums.GroupBy, java.util.Set, long, long)}.
   */

  /**
   * Test method for
   * {@link io.revx.querybuilder.query.PerfTableQuery#PerfTableQuery(io.revx.querybuilder.enums.GroupBy, java.util.Set, long, long)}.
   */
  private static String GROUP_COLUMN = " <TABLECOLUMN> AS <COLUMN>,";
  private static String queryTemplate =
      "select<GROUPCOLUMN> sum(conversion_delivery) AS conversiondelivery, "
          + "sum(txn_amount_in_licensee_currency) AS txnamountinlicenseecurrency, sum(click_installs) AS clickinstalls, "
          + "sum(revenue_in_advertiser_currency) AS revenueinadvertisercurrency, sum(revenue_in_licensee_currency) AS revenueinlicenseecurrency, "
          + "sum(view_conversions) AS viewconversions, sum(txn_amount_in_advertiser_currency) AS txnamountinadvertisercurrency, "
          + "sum(click_conversions) AS clickconversions, sum(impressions) AS impressions, sum(txn_amount_in_platform_currency) AS txnamountinplatformcurrency,"
          + " sum(cost_in_advertiser_currency) AS costinadvertisercurrency, sum(cost_in_platform_currency) AS costinplatformcurrency, "
          + "sum(clicks) AS clicks, sum(imp_installs) AS impinstalls, sum(cost_in_licensee_currency) AS costinlicenseecurrency, "
          + "sum(revenue_in_platform_currency) AS revenueinplatformcurrency  from  PerformanceDataLI   "
          + "where timestamp >= <STARTTIME> and  timestamp < <ENDTIME>  and <FILTERCOLUMN> = <FILTERVALUE>   ";
  private static String GROUPBY = "group by 1 ";

  @Test
  public void testBuildQueryForAdvertiser() throws Exception {
    startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    groupby = GroupBy.ADVERTISER_ID;

    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe =
        StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", groupby.getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "advertiserId");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", String.valueOf(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", String.valueOf(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());
    perfTableQuery = new PerfTableQuery(groupby, filters, startTime, endTime);
    String query = perfTableQuery.getQuery();
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe, query);

  }

  @Test
  public void testBuildQueryForDayGroup() throws Exception {
    startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    groupby = GroupBy.DAY;

    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>",
        Interval.fromString(groupby.getColumnNameInTable()).getFormula());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "day");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", String.valueOf(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", String.valueOf(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());
    perfTableQuery = new PerfTableQuery(groupby, filters, startTime, endTime);
    String query = perfTableQuery.getQuery();
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe, query);

  }

  @Test
  public void testBuildQueryForHourGroup() throws Exception {
    startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    groupby = GroupBy.HOUR;

    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>",
        Interval.fromString(groupby.getColumnNameInTable()).getFormula());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "hour");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", String.valueOf(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", String.valueOf(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());
    perfTableQuery = new PerfTableQuery(groupby, filters, startTime, endTime);
    String query = perfTableQuery.getQuery();
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe, query);

  }

  @Test
  public void testBuildQueryForCampianGroup() throws Exception {

    startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    groupby = GroupBy.CAMPAIGN_ID;

    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe =
        StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", groupby.getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "campaignId");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", String.valueOf(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", String.valueOf(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());
    perfTableQuery = new PerfTableQuery(groupby, filters, startTime, endTime);
    String query = perfTableQuery.getQuery();
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe, query);
  }

  @Test
  public void testBuildQueryForStrategyGroup() throws Exception {
    startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    groupby = GroupBy.STRATEGY_ID;
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe =
        StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", groupby.getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "strategyId");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", String.valueOf(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", String.valueOf(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());
    perfTableQuery = new PerfTableQuery(groupby, filters, startTime, endTime);
    String query = perfTableQuery.getQuery();
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe, query);
  }

  @Test
  public void testBuildQueryForNoneGroup() throws Exception {
    startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    groupby = GroupBy.NONE;
    String queryShouldBe = new String(queryTemplate);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", "");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", "");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", String.valueOf(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", String.valueOf(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());
    perfTableQuery = new PerfTableQuery(groupby, filters, startTime, endTime);
    String query = perfTableQuery.getQuery();
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe.trim(), query.trim());
  }

}
