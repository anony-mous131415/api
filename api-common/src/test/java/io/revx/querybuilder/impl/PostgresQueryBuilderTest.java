/**
 * 
 */
package io.revx.querybuilder.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.revx.core.exception.QueryBuilderException;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.enums.Interval;
import io.revx.querybuilder.objs.FilterComponent;
import io.revx.querybuilder.query.PerfTableQuery;
import mockit.FullVerifications;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;

/**
 * @author amaurya
 *
 */

@RunWith(JMockit.class)
public class PostgresQueryBuilderTest {
  @Injectable
  private PerfTableQuery fQuery;
  @Tested
  private PostgresQueryBuilder postgresQueryBuilder;

  /**
   * Test method for
   * {@link io.revx.querybuilder.impl.PostgresQueryBuilder#buildQuery(java.lang.Long, java.lang.Long, java.util.Set, io.revx.querybuilder.enums.GroupBy)}.
   */

  private static String GROUP_COLUMN = " <TABLECOLUMN> AS <COLUMN>,";
  private static String queryTemplate = "select<GROUPCOLUMN> "
      + "sum(txn_amount_in_licensee_currency) AS txnamountinlicenseecurrency, "
      + "sum(click_installs) AS clickinstalls, sum(revenue_in_advertiser_currency) AS revenueinadvertisercurrency, "
      + "sum(revenue_in_licensee_currency) AS revenueinlicenseecurrency, sum(view_conversions) AS viewconversions, "
      + "cast(hll_cardinality(hll_union_agg(imp_uu)) as numeric) AS impressionuniqusers, sum(txn_amount_in_advertiser_currency) AS txnamountinadvertisercurrency, "
      + "sum(click_conversions) AS clickconversions, sum(impressions) AS impressions, "
      + "sum(txn_amount_in_platform_currency) AS txnamountinplatformcurrency, "
      + "sum(cost_in_advertiser_currency) AS costinadvertisercurrency, sum(view_installs) AS viewinstalls, "
      + "sum(cost_in_platform_currency) AS costinplatformcurrency, sum(clicks) AS clicks, "
      + "cast(hll_cardinality(hll_union_agg(eligible_uu)) as numeric) AS eligibleuniqusers, "
      + "sum(cost_in_licensee_currency) AS costinlicenseecurrency, "
      + "sum(revenue_in_platform_currency) AS revenueinplatformcurrency  from  PerformanceData  "
      + "where ts_date >= \'<STARTTIME>\' and  ts_date < \'<ENDTIME>\'  and <FILTERCOLUMN> = <FILTERVALUE>   ";
  private static String GROUPBY = "group by 1 ";

  @Test
  public void printAll() {
    System.out.println(" fQuery :" + fQuery);
    System.out.println(" postgresQueryBuilder :" + postgresQueryBuilder);

  }

  @Test
  public void assertThatNoMethodHasBeenCalled() {
    try {
      postgresQueryBuilder.buildQuery(0l, 0l, null, null);
      new FullVerifications(fQuery) {};
    } catch (QueryBuilderException e) {
      e.printStackTrace();
    }

  }

  @Test
  public void testBuildQueryForAdvertiser() throws Exception {
    long startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    long endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    Set<FilterComponent> filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    GroupBy groupby = GroupBy.ADVERTISER_ID;

    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe =
        StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", groupby.getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "advertiserId");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());

    String query = postgresQueryBuilder.buildQuery(startTime, endTime, filters, groupby);
    Assert.assertNotNull(query);
   // System.out.println(query);

    Assert.assertEquals(queryShouldBe, query);
    new FullVerifications(fQuery) {};
  }



  @Test
  public void testBuildQueryForDayGroup() throws Exception {
    long startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    long endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 512);
    Set<FilterComponent> filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    GroupBy groupby = GroupBy.DAY;

    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>",
        Interval.fromString(groupby.getColumnNameInTable()).getFormula());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "day");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());

    String query = postgresQueryBuilder.buildQuery(startTime, endTime, filters, groupby);
    Assert.assertNotNull(query);

    Assert.assertEquals(queryShouldBe, query);
    new FullVerifications(fQuery) {};
  }

  @Test
  public void testBuildQueryForHourGroup() throws Exception {
    long startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    long endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 512);
    Set<FilterComponent> filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    GroupBy groupby = GroupBy.HOUR;
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>",
        Interval.fromString(groupby.getColumnNameInTable()).getFormula());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "hour");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());

    String query = postgresQueryBuilder.buildQuery(startTime, endTime, filters, groupby);
    Assert.assertNotNull(query);

    Assert.assertEquals(queryShouldBe, query);
    new FullVerifications(fQuery) {};
  }

  @Test
  public void testBuildQueryForCampianGroup() throws Exception {
    long startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    long endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 512);
    Set<FilterComponent> filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    GroupBy groupby = GroupBy.CAMPAIGN_ID;
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe =
        StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", groupby.getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "campaignId");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());

    String query = postgresQueryBuilder.buildQuery(startTime, endTime, filters, groupby);
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe, query);
    new FullVerifications(fQuery) {};
  }

  @Test
  public void testBuildQueryForStrategyGroup() throws Exception {
    long startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    long endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 512);
    Set<FilterComponent> filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    GroupBy groupby = GroupBy.STRATEGY_ID;
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe =
        StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", groupby.getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "strategyId");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());

    String query = postgresQueryBuilder.buildQuery(startTime, endTime, filters, groupby);
    Assert.assertNotNull(query);
    Assert.assertEquals(queryShouldBe, query);
    new FullVerifications(fQuery) {};
  }

  @Test
  public void testBuildQueryForNoneGroup() throws Exception {
    long startTime = System.currentTimeMillis() / 1000 - 3 * 86400;
    long endTime = System.currentTimeMillis() / 1000;
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 512);
    Set<FilterComponent> filters = new HashSet<FilterComponent>();
    filters.add(licFilter);
    GroupBy groupby = GroupBy.NONE;
    String queryShouldBe = new String(queryTemplate);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", "");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", "");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(startTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(endTime));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>",
        licFilter.getField().getColumnNameInTable());
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", licFilter.getValue());

    String query = postgresQueryBuilder.buildQuery(startTime, endTime, filters, groupby);
    Assert.assertNotNull(query);
    Assert.assertEquals(StringUtils.trim(queryShouldBe), StringUtils.trim(query));
    new FullVerifications(fQuery) {};
  }

  private String getTimeFilter(long epocTime) {

    Date date = new Date(epocTime * 1000);
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    String formatted = format.format(date);
    return formatted;


  }
}
