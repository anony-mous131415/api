/**
 *
 */
package io.revx.api.service;

import static javax.swing.text.html.HTML.Attribute.ROWS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import com.google.api.gax.paging.Page;
import com.google.cloud.PageImpl;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.reportbuilder.redshift.BigQueryConnectionUtil;
import joptsimple.internal.Rows;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.enums.DashboardEntities;
import io.revx.api.pojo.ChartPerformanceDataMetrics;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.api.postgres.repo.PerformanceDataRepositoryImpl;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.enums.Interval;
import io.revx.querybuilder.objs.FilterComponent;

/**
 * @author amaurya
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class DashboardDaoTest extends BaseTestService {
  @Mock
  private BigQueryConnectionUtil bigQueryConnectionUtil;

  @Mock
  private ApplicationProperties applicationProperties;

  @Mock
  private PerformanceDataRepositoryImpl performanceDataRepositoryImpl;

  @InjectMocks
  private DashboardDao dashboardDao;

  private static String GROUP_COLUMN = " <TABLECOLUMN> AS <COLUMN>,";
  private static String queryTemplate = "select<GROUPCOLUMN> "
          + "sum(impressions) AS impressions, "
          + "sum(clicks) AS clicks, sum(view_conversion) AS viewconversions, "
          + "sum(click_conversion) AS clickconversions, sum(view_install) AS viewinstalls, "
          + "sum(click_install) AS clickinstalls, sum(revenue_in_advertiser_currency) AS revenueinadvertisercurrency, "
          + "sum(revenue_in_licensee_cureency) AS revenueinlicenseecurrency, sum(revenue_in_platform_currency) AS revenueinplatformcurrency, "
          + "sum(cost_in_advertiser_currency) AS costinadvertisercurrency, "
          + "sum(cost_in_licensee_currency) AS costinlicenseecurrency, "
          + "sum(cost_in_platform_currency) AS costinplatformcurrency, "
          + "sum(txn_amt_in_advertiser_currency) AS txnamountinadvertisercurrency, sum(txn_amt_in_license_currency) AS txnamountinlicenseecurrency, "
          + "sum(txn_amt_in_platform_currency) AS txnamountinplatformcurrency, sum(bids_placed) AS bidsplaced, "
          + "sum(invalid_clicks) AS invalidclicks, HLL_COUNT.MERGE(imp_uu_hll) AS impressionuniqusers, "
          + "HLL_COUNT.MERGE(eligible_uu_hll) AS eligibleuniqusers, "
          + "HLL_COUNT.MERGE(eligible_bids_hll) AS eligiblebids  "
          + "from  performance_li_main  where tst_date >= \'<STARTTIME>\' and  tst_date < \'<ENDTIME>\'  and <FILTERCOLUMN> = <FILTERVALUE>   ";
  private static String GROUPBY = "group by 1 ";

  @Before
  public void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
    dashboardDao.bigQueryConnectionUtil = bigQueryConnectionUtil;
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashboardDao#getDashboardChartData(io.revx.core.model.requests.DashboardRequest, java.util.Set)}.
   */

  @Test
  public void testGetDashboardChartDataHour() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.HOUR;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ChartPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartRespFromDB(req));
    List<ChartPerformanceDataMetrics> data = dashboardDao.getDashboardChartData(req, tableFilters, true);
    assertNotNull(data);
    assertThat(data.size()).isEqualTo(48);
  }

  @Test
  public void testGetDashboardChartDataDay() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ChartPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartRespFromDB(req));
    List<ChartPerformanceDataMetrics> data = dashboardDao.getDashboardChartData(req, tableFilters, true);
    assertNotNull(data);
    assertThat(data.size()).isEqualTo(2);
  }

  @Test
  public void testGetDashboardChartDataWidget() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(PerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartWidgetRespFromDB(req));
    List<PerformanceDataMetrics> data = dashboardDao.getDashboardChartWidgetData(req, tableFilters, true);
    assertNotNull(data);
    assertThat(data.size()).isEqualTo(1);
  }

  @Test
  public void testGetDashboardChartDataWidgetFailed() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(PerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartWidgetRespFromDB(req));
    List<PerformanceDataMetrics> data = dashboardDao.getDashboardChartWidgetData(req, tableFilters, true);
    assertNull(data);
  }

  @Test
  public void testGetDashboardChartDataFailed() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ChartPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartRespFromDB(req));
    List<ChartPerformanceDataMetrics> data = dashboardDao.getDashboardChartData(req, tableFilters, true);
    assertNull(data);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashboardDao#getDashboardListData(io.revx.api.pojo.DashBoardEntity, io.revx.core.model.requests.DashboardRequest, java.util.Set)}.
   */

  @Test
  public void testGetDashboardListDataAdvertiser() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    int products = 20;
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, products));
    List<ListPerformanceDataMetrics> data =
            dashboardDao.getDashboardListData(DashBoardEntity.ADVERTISER, req, tableFilters, true);
    assertNotNull(data);
    assertThat(data.size()).isEqualTo(products);
    for (ListPerformanceDataMetrics listPerformanceDataMetrics : data) {
      assertNotNull(listPerformanceDataMetrics.getAdvertiserid());
    }
  }

  @Test
  public void testGetDashboardListDataCampian() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    int products = 20;
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.CAMPAIGN, products));
    List<ListPerformanceDataMetrics> data =
            dashboardDao.getDashboardListData(DashBoardEntity.CAMPAIGN, req, tableFilters, true);
    assertNotNull(data);
    assertThat(data.size()).isEqualTo(products);
    for (ListPerformanceDataMetrics listPerformanceDataMetrics : data) {
      assertNotNull(listPerformanceDataMetrics.getCampaignid());
    }
  }

  @Test
  public void testGetDashboardListDataCreative() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    int products = 20;
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.CREATIVE, products));
    List<ListPerformanceDataMetrics> data =
            dashboardDao.getDashboardListData(DashBoardEntity.CREATIVE, req, tableFilters, true);
    assertNotNull(data);
    assertThat(data.size()).isEqualTo(products);
    for (ListPerformanceDataMetrics listPerformanceDataMetrics : data) {
      assertNotNull(listPerformanceDataMetrics.getCreativeid());
    }
  }

  @Test
  public void testGetDashboardListDataStrategy() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    int products = 20;
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.STRATEGY, products));
    List<ListPerformanceDataMetrics> data =
            dashboardDao.getDashboardListData(DashBoardEntity.STRATEGY, req, tableFilters, true);
    assertNotNull(data);
    assertThat(data.size()).isEqualTo(products);
    for (ListPerformanceDataMetrics listPerformanceDataMetrics : data) {
      assertNotNull(listPerformanceDataMetrics.getStrategyid());
    }
  }


  @Test
  public void testGetDashboardListDataFailed() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(2));
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    int products = 20;
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.STRATEGY, products));
    List<ListPerformanceDataMetrics> data =
            dashboardDao.getDashboardListData(DashBoardEntity.STRATEGY, req, tableFilters, true);
    assertNull(data);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashboardDao#getQueryString(io.revx.core.model.requests.DashboardRequest, io.revx.mysql.querybuilder.impl.PostgresQueryBuilder, java.util.Set, io.revx.api.enums.DashboardEntities, io.revx.api.pojo.DashBoardEntity)}.
   */

  @Test
  public void testGetQueryStringHourChart() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.HOUR;
    req.setGroupBy(grpBy.getColumn());
    long currentTime = System.currentTimeMillis() / 1000;
    dur.setEndTimeStamp(currentTime);
    dur.setStartTimeStamp(currentTime - 1000);
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    FilterComponent fl = new FilterComponent(Filter.ADVERTISER_ID, "123");
    tableFilters.add(fl);
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", getColumnByGroup(grpBy));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "hour");
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(dur.getStartTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(dur.getEndTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>", "advertiser_id");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", fl.getValue());
    String quString = dashboardDao.getQueryString(req, tableFilters, DashboardEntities.chart, null, true);
    Assert.assertEquals(queryShouldBe, quString);
  }



  @Test
  public void testGetQueryStringDayChart() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    long currentTime = System.currentTimeMillis() / 1000;
    dur.setEndTimeStamp(currentTime);
    dur.setStartTimeStamp(currentTime - 1000);
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    FilterComponent fl = new FilterComponent(Filter.ADVERTISER_ID, "123");
    tableFilters.add(fl);
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", getColumnByGroup(grpBy));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "day");
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(dur.getStartTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(dur.getEndTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>", "advertiser_id");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", fl.getValue());
    String quString = dashboardDao.getQueryString(req, tableFilters, DashboardEntities.chart, null, true);
    Assert.assertEquals(queryShouldBe, quString);
  }

  @Test
  public void testGetQueryStringListCampiagn() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    long currentTime = System.currentTimeMillis() / 1000;
    dur.setEndTimeStamp(currentTime);
    dur.setStartTimeStamp(currentTime - 1000);
    DashBoardEntity entity = DashBoardEntity.CAMPAIGN;
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    FilterComponent fl = new FilterComponent(Filter.ADVERTISER_ID, "123");
    tableFilters.add(fl);
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", getColumnByEntity(entity));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "campaignId");
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(dur.getStartTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(dur.getEndTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>", "advertiser_id");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", fl.getValue());
    String quString =
            dashboardDao.getQueryString(req, tableFilters, DashboardEntities.list, entity, true);
    Assert.assertEquals(queryShouldBe, quString);
  }

  @Test
  public void testGetQueryStringListAdvertiser() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    long currentTime = System.currentTimeMillis() / 1000;
    dur.setEndTimeStamp(currentTime);
    dur.setStartTimeStamp(currentTime - 1000);
    DashBoardEntity entity = DashBoardEntity.ADVERTISER;
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    FilterComponent fl = new FilterComponent(Filter.ADVERTISER_ID, "123");
    tableFilters.add(fl);
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", getColumnByEntity(entity));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "advertiserId");
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(dur.getStartTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(dur.getEndTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>", "advertiser_id");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", fl.getValue());
    String quString =
            dashboardDao.getQueryString(req, tableFilters, DashboardEntities.list, entity, true);
    Assert.assertEquals(queryShouldBe, quString);
  }

  @Test
  public void testGetQueryStringListCreative() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    long currentTime = System.currentTimeMillis() / 1000;
    dur.setEndTimeStamp(currentTime);
    dur.setStartTimeStamp(currentTime - 1000);
    DashBoardEntity entity = DashBoardEntity.CREATIVE;
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    FilterComponent fl = new FilterComponent(Filter.ADVERTISER_ID, "123");
    tableFilters.add(fl);
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe,"txn_amount_in_licensee_currency","impressions");
    queryShouldBe = StringUtils.replace(queryShouldBe,"click_installs","clicks");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", getColumnByEntity(entity));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "creativeId");
    queryShouldBe =
        StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(dur.getStartTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(dur.getEndTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>", "advertiser_id");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", fl.getValue());
    String quString =
            dashboardDao.getQueryString(req, tableFilters, DashboardEntities.list, entity, true);
    Assert.assertEquals(queryShouldBe, quString);
  }

  @Test
  public void testGetQueryStringListStrategy() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    long currentTime = System.currentTimeMillis() / 1000;
    dur.setEndTimeStamp(currentTime);
    dur.setStartTimeStamp(currentTime - 1000);
    DashBoardEntity entity = DashBoardEntity.STRATEGY;
    Set<FilterComponent> tableFilters = new HashSet<FilterComponent>();
    FilterComponent fl = new FilterComponent(Filter.ADVERTISER_ID, "123");
    tableFilters.add(fl);
    String queryShouldBe = new String(queryTemplate + GROUPBY);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<GROUPCOLUMN>", GROUP_COLUMN);
    queryShouldBe = StringUtils.replace(queryShouldBe, "<TABLECOLUMN>", getColumnByEntity(entity));
    queryShouldBe = StringUtils.replace(queryShouldBe, "<COLUMN>", "strategyId");
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<STARTTIME>", getTimeFilter(dur.getStartTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<ENDTIME>", getTimeFilter(dur.getEndTimeStamp()));
    queryShouldBe =
            StringUtils.replace(queryShouldBe, "<FILTERCOLUMN>", "advertiser_id");
    queryShouldBe = StringUtils.replace(queryShouldBe, "<FILTERVALUE>", fl.getValue());
    String quString =
            dashboardDao.getQueryString(req, tableFilters, DashboardEntities.list, entity, true);
    Assert.assertEquals(queryShouldBe, quString);
  }


  private String getTimeFilter(long epocTime) {

    Date date = new Date(epocTime * 1000);
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    String formatted = format.format(date);
    return formatted;


  }

  private String getColumnByEntity(DashBoardEntity entity) {
    GroupBy grpBy = GroupBy.fromString(entity.getColumn());
    return getColumnByGroup(grpBy);
  }

  private String getColumnByGroup(GroupBy grpBy) {
    switch (grpBy) {
      case HOUR:
      case DAY:
        return Interval.fromString(grpBy.getColumn()).getFormula();
      default:
        break;
    }
    return grpBy.getColumnNameInTable();
  }

}
