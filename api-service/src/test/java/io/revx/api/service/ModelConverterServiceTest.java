/**
 * 
 */
package io.revx.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.*;

import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.CatalogVariableValueType;
import io.revx.api.mysql.dco.entity.catalog.*;
import io.revx.api.mysql.dco.repo.catalog.AdvertiserCatalogVariableMappingRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedApiStatsRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoStatsRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.entity.advertiser.CurrencyEntity;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.service.campaign.CurrencyCacheService;
import io.revx.api.service.catalog.CatalogUtil;
import io.revx.api.service.pixel.impl.ConversionPixelService;
import io.revx.core.model.*;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.response.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.pojo.ChartPerformanceDataMetrics;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.api.pojo.TablesEntity;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.querybuilder.enums.GroupBy;

/**
 * @author amaurya
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ModelConverterServiceTest extends BaseTestService {
  @Mock
  private EntityESService elasticSearch;
  @Mock
  private LoginUserDetailsService loginUserDetailsService;
  @Mock
  private AdvertiserRepository advertiserRepository;
  @Mock
  private CatalogUtil util;
  @Mock
  private AdvertiserCatalogVariableMappingRepository acvmRepo;
  @Mock
  private FeedApiStatsRepository feedApiStatsRepo;
  @Mock
  private CurrencyCacheService currencyCache;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private FeedInfoStatsRepository feedInfoStatsRepo;
  @Mock
  private ConversionPixelService pixelService;
  @InjectMocks
  private ModelConverterService modelConverterService;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    modelConverterService.loginUserDetailsService = loginUserDetailsService;
    modelConverterService.elasticSearch = elasticSearch;
    modelConverterService.currencyCache = currencyCache;
    modelConverterService.applicationProperties = applicationProperties;
    modelConverterService.feedInfoStatsRepo = feedInfoStatsRepo;
    modelConverterService.util = util;
    modelConverterService.acvmRepo = acvmRepo;
    modelConverterService.feedApiStatsRepo = feedApiStatsRepo;
    modelConverterService.advertiserRepo = advertiserRepository;
    modelConverterService.pixelService = pixelService;
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateListData(java.util.List, java.lang.String, java.lang.String, java.util.List, java.util.Map, io.revx.querybuilder.enums.GroupBy)}.
   */
  @Test
  public void testPopulateListDataAdvertiser() throws Exception {
    int products = 20;
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.ADVERTISER_ID;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    List listData = new ArrayList<>();
    Map<Long, AdvertiserPojo> idModelMap = TestDataGenerator.getMapOfObject(products + 5,
        TablesEntity.ADVERTISER.getElasticIndex(), AdvertiserPojo.class);
    List<ListPerformanceDataMetrics> totalResult =
        TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, products);
    modelConverterService.populateListData(totalResult, "INR", "USD", listData, idModelMap, grpBy, null);
    assertNotNull(listData);
    assertThat(listData.size()).isEqualTo(25);
  }

  @Test
  public void testPopulateListDataCampiagn() throws Exception {
    int products = 20;
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.CAMPAIGN_ID;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    List listData = new ArrayList<>();
    Map<Long, Campaign> idModelMap = TestDataGenerator.getMapOfObject(products + 5,
        TablesEntity.CAMPAIGN.getElasticIndex(), Campaign.class);
    List<ListPerformanceDataMetrics> totalResult =
        TestDataGenerator.getListRespFromDB(req, DashBoardEntity.CAMPAIGN, products);
    modelConverterService.populateListData(totalResult, "INR", "USD", listData, idModelMap, grpBy, null);
    assertNotNull(listData);
  }

  @Test
  public void testPopulateListDataStrategy() throws Exception {
    int products = 20;
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.STRATEGY_ID;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    List listData = new ArrayList<>();
    Map<Long, Strategy> idModelMap = TestDataGenerator.getMapOfObject(products + 5,
        TablesEntity.STRATEGY.getElasticIndex(), Strategy.class);
    List<ListPerformanceDataMetrics> totalResult =
        TestDataGenerator.getListRespFromDB(req, DashBoardEntity.STRATEGY, products);
    modelConverterService.populateListData(totalResult, "INR", "USD", listData, idModelMap, grpBy, null);
    assertNotNull(listData);
    assertThat(listData.size()).isEqualTo(25);
  }

  @Test
  public void testPopulateListDataCreative() throws Exception {
    int products = 20;
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.CREATIVE_ID;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    List listData = new ArrayList<>();
    Map<Long, Creative> idModelMap = TestDataGenerator.getMapOfObject(products + 5,
        TablesEntity.CREATIVE.getElasticIndex(), Creative.class);
    List<ListPerformanceDataMetrics> totalResult =
        TestDataGenerator.getListRespFromDB(req, DashBoardEntity.CREATIVE, products);
    when(loginUserDetailsService.isReadOnlyUser()).thenReturn(true);
    modelConverterService.populateListData(totalResult, "INR", "USD", listData, idModelMap, grpBy, null);
    assertNotNull(listData);
    assertThat(listData.size()).isEqualTo(25);
  }

  @Test
  public void testPopulateListDataException() throws Exception {
    int products = 20;
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.ADVERTISER_ID;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    Map<Long, AdvertiserPojo> idModelMap = TestDataGenerator.getMapOfObject(products + 5,
        TablesEntity.ADVERTISER.getElasticIndex(), AdvertiserPojo.class);
    List<ListPerformanceDataMetrics> totalResult =
        TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, products);
    modelConverterService.populateListData(totalResult, "INR", "USD", null, idModelMap, grpBy, null);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateChartData(io.revx.core.model.requests.DashboardRequest, java.util.List, java.lang.String, java.lang.String, java.util.List, java.util.Map)}.
   */
  @Test
  public void testPopulateChartDataHour() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    GroupBy grpBy = GroupBy.HOUR;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    List<DashboardData> chartData = new ArrayList<DashboardData>();
    List<ChartPerformanceDataMetrics> totalResult = TestDataGenerator.getChartRespFromDB(req);
    modelConverterService.populateChartData(req, totalResult, "INR", "USD", chartData, null);
    assertNotNull(chartData);
    assertThat(chartData.size()).isEqualTo(48);
  }

  @Test
  public void testPopulateChartDataDay() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    List<DashboardData> chartData = new ArrayList<DashboardData>();
    List<ChartPerformanceDataMetrics> totalResult = TestDataGenerator.getChartRespFromDB(req);
    modelConverterService.populateChartData(req, totalResult, "INR", "USD", chartData, null);
    assertNotNull(chartData);
    assertThat(chartData.size()).isEqualTo(2);
  }

  @Test
  public void testPopulateChartDataZero() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    List<DashboardData> chartData = new ArrayList<DashboardData>();
    List<ChartPerformanceDataMetrics> totalResult = TestDataGenerator.getChartRespFromDB(req);
    modelConverterService.populateChartData(req, totalResult, "INR", "USD", null, null);
    assertNotNull(chartData);
    assertThat(chartData.size()).isEqualTo(0);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateChartWidgetData(java.util.List, java.lang.String, java.lang.String, io.revx.core.model.DashboardMetrics, java.util.Map)}.
   */
  @Test
  public void testPopulateChartWidgetData() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    DashboardMetrics widgetData = new DashboardMetrics();
    List<PerformanceDataMetrics> totalResult = TestDataGenerator.getChartWidgetRespFromDB(req);
    modelConverterService.populateChartWidgetData(totalResult, "INR", "USD", widgetData, null);
    assertNotNull(widgetData);
  }

  @Test
  public void testPopulateChartWidgetDataWithAdvLogin() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    DashboardMetrics widgetData = new DashboardMetrics();
    List<PerformanceDataMetrics> totalResult = TestDataGenerator.getChartWidgetRespFromDB(req);
    when(loginUserDetailsService.isAdvertiserLogin()).thenReturn(true);
    modelConverterService.populateChartWidgetData(totalResult, "INR", "USD", widgetData, null);
    assertNotNull(widgetData);
  }

  @Test
  public void testPopulateChartWidgetDataWithAdvLoginReadOnly() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    req.setFilters(filters);
    DashboardMetrics widgetData = new DashboardMetrics();
    List<PerformanceDataMetrics> totalResult = TestDataGenerator.getChartWidgetRespFromDB(req);
    when(loginUserDetailsService.isAdvertiserLogin()).thenReturn(true);
    when(loginUserDetailsService.isReadOnlyUser()).thenReturn(true);
    modelConverterService.populateChartWidgetData(totalResult, "INR", "USD", widgetData, null);
    assertNotNull(widgetData);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#makeDashBoardChartData(io.revx.api.pojo.ChartPerformanceDataMetrics, io.revx.core.model.DashboardData, java.lang.String, java.lang.String, java.lang.String, boolean)}.
   */
  @Test
  public void testMakeDashBoardChartData() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#getIntaralOfTime(java.lang.String)}.
   */
  @Test
  public void testGetIntaralOfTime() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateDatshBoradDataFromPerfLi(io.revx.api.pojo.PerformanceDataMetrics, io.revx.core.model.DashboardMetrics, java.lang.String, java.lang.String)}.
   */
  @Test
  public void testPopulateDatshBoradDataFromPerfLi() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#getSubList(java.util.List, java.lang.Integer, java.lang.Integer)}.
   */
  @Test
  public void testGetSubList() throws Exception {
    List<BaseModel> totalResult = TestDataGenerator.getListOfObject(20, BaseModel.class);
    List<BaseModel> tmpList = modelConverterService.getSubList(totalResult, 1, 10);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(10);

    tmpList = modelConverterService.getSubList(totalResult, 1, 30);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(20);

    tmpList = modelConverterService.getSubList(totalResult, null, 30);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(20);

    tmpList = modelConverterService.getSubList(totalResult, 1, null);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(20);

    tmpList = modelConverterService.getSubList(totalResult, null, null);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(20);

    tmpList = modelConverterService.getSubList(totalResult, 5, 10);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(0);

    tmpList = modelConverterService.getSubList(null, 1, 30);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(0);

    tmpList = modelConverterService.getSubList(new ArrayList<>(), 1, 30);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(0);

    tmpList = modelConverterService.getSubList(totalResult, -1, -1);
    assertNotNull(tmpList);
    assertThat(tmpList.size()).isEqualTo(0);

  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateAdvEntity(io.revx.core.model.advertiser.AdvertiserPojo)}.
   */
  @Test
  public void testPopulateAdvEntity() throws Exception{
    mockSecurityContext("akhilesh", false, false);
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    modelConverterService.updateAdvEntity(MockDataGenerator.createAdvertiserPojo(),MockDataGenerator.createAdvertiserEntity());
    AdvertiserEntity response = modelConverterService.populateAdvEntity(MockDataGenerator.createAdvertiserPojo());
    assertNotNull(response);
    assertEquals("Honda",response.getAdvertiserName());
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateAdvertiserFromEntity(io.revx.api.mysql.entity.advertiser.AdvertiserEntity)}.
   */
  @Test
  public void testPopulateAdvertiserFromEntity() throws Exception{
    Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
            .thenReturn(MockDataGenerator.getCurrencyModel());
    Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
    AdvertiserPojo response = modelConverterService.populateAdvertiserFromEntity(MockDataGenerator.createAdvertiserEntity());
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateAdvForElastic(io.revx.api.mysql.entity.advertiser.AdvertiserEntity)}.
   */
  @Test
  public void testPopulateAdvForElastic() throws Exception{
    AdvertiserSettings advertiserSettings = new AdvertiserSettings();
    advertiserSettings.setAdvertiserId(3875L);
    advertiserSettings.setMmp(MockDataGenerator.createBaseModel());
    advertiserSettings.setDateFormat("ddmmyyyy");
    advertiserSettings.setSkuAllowedChars("45");
    advertiserSettings.setFeedKey("3452");
    Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
            .thenReturn(MockDataGenerator.getCurrencyModel());
    Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
    modelConverterService.updateAdvSettings(advertiserSettings,MockDataGenerator.createAdvertiserEntity());
    Advertiser response = modelConverterService.populateAdvForElastic(MockDataGenerator.createAdvertiserEntity());
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateAdvSettingsFromEntity(io.revx.api.mysql.entity.advertiser.AdvertiserEntity)}.
   */
  @Test
  public void testPopulateAdvSettingsFromEntity() throws Exception{
    CurrencyEntity entity = new CurrencyEntity();
    entity.setId(76L);
    entity.setCurrencyCode("1");
    entity.setCurrencyName("INR");
    Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong())).thenReturn(MockDataGenerator.createBaseModel());
    Mockito.when(currencyCache.fetchCurrencyByCode(Mockito.any())).thenReturn(entity);
    AdvertiserSettings response = modelConverterService.populateAdvSettingsFromEntity(MockDataGenerator.createAdvertiserEntity());
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#convertFeedEntityToFeedDTO(io.revx.api.mysql.dco.entity.catalog.FeedInfoEntity)}.
   */
  @Test
  public void testConvertFeedEntityToFeedDTO() throws Exception{
    AdvertiserCatalogVariablesMappingEntity mappingEntity = new AdvertiserCatalogVariablesMappingEntity();
    mappingEntity.setAtomVariable(23145L);
    mappingEntity.setIsMultivalued(true);
    mappingEntity.setValueParsingRule("test");
    List<AdvertiserCatalogVariablesMappingEntity> acvmList  = new ArrayList<>();
    acvmList.add(mappingEntity);
    FeedInfoStatsEntity entity = new FeedInfoStatsEntity();
    entity.setFeedId(76L);
    entity.setId(6L);
    entity.setStatus(4);
    List<FeedInfoStatsEntity> list = new ArrayList<>();
    list.add(entity);
    Mockito.when(acvmRepo.findAllByFeedId(Mockito.any())).thenReturn(acvmList);
    Mockito.when(util.getSuccessRateForFeedInfoStats(Mockito.any(),Mockito.any())).thenReturn(76L);
    Mockito.when(feedInfoStatsRepo.findAllByFeedIdOrderByLastUpdatedDesc(Mockito.anyLong())).thenReturn(list);
    Mockito.when(applicationProperties.getTimePeriodForSuccessRate()).thenReturn(23L);
    CatalogFeed response = modelConverterService.convertFeedEntityToFeedDTO(MockDataGenerator.createFeedInfoEntity());
    assertNotNull(response);
  }

  @Test
  public void testConvertFeedEntityToFeedDTo() throws Exception{
    AdvertiserCatalogVariablesMappingEntity mappingEntity = new AdvertiserCatalogVariablesMappingEntity();
    mappingEntity.setAtomVariable(23145L);
    mappingEntity.setIsMultivalued(true);
    mappingEntity.setValueParsingRule("test");
    List<AdvertiserCatalogVariablesMappingEntity> acvmList  = new ArrayList<>();
    acvmList.add(mappingEntity);
    FeedApiStatusEntity entity = new FeedApiStatusEntity();
    entity.setFeedId(76L);
    List<FeedApiStatusEntity> list = new ArrayList<>();
    list.add(entity);
    Mockito.when(acvmRepo.findAllByFeedId(Mockito.any())).thenReturn(acvmList);
    Mockito.when(util.getSuccessRateForFeedInfoStats(Mockito.any(),Mockito.any())).thenReturn(76L);
    Mockito.when(feedApiStatsRepo.findAllByOrderByFasCreatedTimeDesc(Mockito.anyLong())).thenReturn(list);
    Mockito.when(applicationProperties.getTimePeriodForSuccessRate()).thenReturn(23L);
    CatalogFeed response = modelConverterService.convertFeedEntityToFeedDTO(MockDataGenerator.createFeedInfoEntities());
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#convertAcvmToDTO(io.revx.api.mysql.dco.entity.catalog.AdvertiserCatalogVariablesMappingEntity, java.util.Optional)}.
   */
  @Test
  public void testConvertAcvmToDTO() throws Exception{
    Optional<AtomCatalogVariablesEntity> optional = Optional.of(MockDataGenerator.getAtomCatalogVariablesEntity());
    VariablesMappingDTO response = modelConverterService
            .convertAcvmToDTO(MockDataGenerator.getAdvertiserCatalogVariablesMappingEntity(),optional);
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#convertPixelDTOToEntity(io.revx.core.model.pixel.Pixel, io.revx.api.mysql.entity.pixel.ConversionPixelEntity, boolean)}.
   */
  @Test
  public void testConvertPixelDTOToEntity() throws Exception{
    mockSecurityContext("akhilesh", false, false);
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    Mockito.when(advertiserRepository.findByIdAndLicenseeId(Mockito.anyLong(),Mockito.anyLong()))
            .thenReturn(MockDataGenerator.createAdvertiserEntity());
    ConversionPixelEntity response = modelConverterService.convertPixelDTOToEntity(MockDataGenerator
            .createPixel(),null,false);
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#convertPixelToDTO(io.revx.api.mysql.entity.pixel.ConversionPixelEntity)}.
   */
  @Test
  public void testConvertPixelToDTO() throws Exception{
    Mockito.when(elasticSearch.searchById(Mockito.any(),Mockito.anyLong()))
            .thenReturn(MockDataGenerator.createBaseModel());
    Mockito.when(elasticSearch.searchPojoById(Mockito.any(),Mockito.anyLong()))
            .thenReturn(MockDataGenerator.getCurrencyModel());
    Optional<AdvertiserEntity> optional = Optional.of(MockDataGenerator.createAdvertiserEntity());
    Mockito.when(advertiserRepository.findById(Mockito.any())).thenReturn(optional);
    Pixel response = modelConverterService.convertPixelToDTO(MockDataGenerator.createConversionPixelEntity());
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#getSetOfIds(java.lang.String)}.
   */
  @Test
  public void testGetSetOfIds() throws Exception{
    Set<Long> response = modelConverterService.getSetOfIds("234");
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#convertToClickDestintionEntity(io.revx.core.model.ClickDestination)}.
   */
  @Test
  public void testConvertToClickDestintionEntity() throws Exception{
    mockSecurityContext("akhilesh", false, false);
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    ClickDestinationEntity response = modelConverterService.convertToClickDestintionEntity(MockDataGenerator
            .createClickDestination());
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#convertFromClickDestEntity(io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity)}.
   */
  @Test
  public void testConvertFromClickDestEntity() throws Exception{
    ClickDestination response = modelConverterService.convertFromClickDestEntity(MockDataGenerator
            .createClickDestinationEntity());
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.ModelConverterService#populateClickDestinationForParameters(MobileMeasurementPartner)}.
   */
  @Test
  public void testPopulateClickDestinationForParameters() throws Exception{
    ClickDestinationAutomationUrls response = modelConverterService
            .populateClickDestinationForParameters(MockDataGenerator.getMobileMeasurementPartner());
    assertNotNull(response);
  }
}
