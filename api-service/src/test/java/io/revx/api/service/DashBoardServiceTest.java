/**
 *
 */
package io.revx.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.*;

import com.google.api.services.bigquery.model.TableList;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.entity.AggregatorLicenseeMappingEntity;
import io.revx.api.mysql.repo.AggregatorLicenseeMappingRepository;
import io.revx.api.reportbuilder.redshift.BigQueryConnectionUtil;
import io.revx.core.cache.DTOCache;
import io.revx.core.model.*;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.objs.FilterComponent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.pojo.ChartPerformanceDataMetrics;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.postgres.repo.PerformanceDataRepositoryImpl;
import io.revx.core.exception.ApiException;
import io.revx.core.model.requests.ChartDashboardResponse;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.DashboardResponse;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.Duration;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.requests.MenuCrubResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.UserInfo;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.GroupBy;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DashBoardServiceTest extends BaseTestService {
  @Mock
  private ApplicationProperties applicationProperties;


  @Mock
  private CacheService cacheService;


  @Mock
  private CSVReaderWriterService csvReaderWriterService;


  @Mock
  private DashboardDao dashboardDao;


  @Mock
  private EntityESService entityESService;


  @Mock
  private LoginUserDetailsService loginUserDetailsService;


  @Mock
  private ModelConverterService modelConverterService;


  @Mock
  private CustomESRepositoryImpl customESRepositoryImpl;

  @Mock
  private ValidationService validationService;

  @Mock
  private AggregatorLicenseeMappingRepository aggregatorLicenseeMappingRepository;

  @Mock
  PerformanceDataRepositoryImpl performanceDataRepositoryImpl;

  @InjectMocks
  private DashBoardService dashBoardService;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  private static Map<String, DTOCache> cacheMap = new HashMap<>();

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    entityESService = new EntityESService();
    validationService = new ValidationService();
    cacheService = new CacheService();
    dashBoardService.validationService = validationService;
    entityESService.loginUserDetailsService = loginUserDetailsService;
    entityESService.customESRepositoryImpl = customESRepositoryImpl;
    dashBoardService.entityESService = entityESService;
    dashBoardService.loginUserDetailsService = loginUserDetailsService;
    validationService.loginUserDetailsService = loginUserDetailsService;
    dashBoardService.dashboardDao = dashboardDao;
    dashBoardService.aggregatorLicenseeMappingRepository= aggregatorLicenseeMappingRepository;
    modelConverterService = new ModelConverterService();
    modelConverterService.loginUserDetailsService = loginUserDetailsService;
    dashBoardService.modelConverterService = modelConverterService;
    dashBoardService.cacheService = cacheService;
    csvReaderWriterService.loginUserDetailsService = loginUserDetailsService;
    applicationProperties = new ApplicationProperties();
    applicationProperties.setDefaultSort("id-");
    applicationProperties.setDownloadFilePath("/tmp");
    applicationProperties.setFileDownloadDomain("www.abc.com");
    csvReaderWriterService.applicationProperties = applicationProperties;
    dashBoardService.csvReaderWriterService = csvReaderWriterService;
    dashBoardService.applicationProperties = applicationProperties;
  }

  /**
   * Test method for {@link io.revx.api.service.DashBoardService#getMenuCrubResponse()}.
   */
  @Test
  public void testGetMenuCrubResponse() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    for (TablesEntity entity : TablesEntity.values()) {
      when(
              customESRepositoryImpl.searchAsList(entity.getElasticIndex(), es, StatusBaseObject.class))
              .thenReturn(TestDataGenerator.getListOfObject(10, entity.getElasticIndex(),
                      StatusBaseObject.class));
    }
    List<MenuCrubResponse> mRb = dashBoardService.getMenuCrubResponse();
    assertNotNull(mRb);
    assertThat(mRb.size()).isEqualTo(7);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#getDictionaryData(TablesEntity, Integer, Integer, SearchRequest, String)}.
   */
  @Test
  public void testGetDictionaryData() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    DashboardFilters filters = new DashboardFilters();
    filters.setColumn("3");
    filters.setValue("4");
    filters.setOperator("AND");
    List<DashboardFilters> dlist = new ArrayList<>();
    dlist.add(filters);
    SearchRequest request = new SearchRequest();
    SearchRequest requests = new SearchRequest();
    requests.setFilters(null);
    request.setFilters(dlist);
    List<AggregatorLicenseeMappingEntity> list = new ArrayList<>();
    list.add(MockDataGenerator.aggregatorLicenseeMappingEntity());
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    when(aggregatorLicenseeMappingRepository.findAllByLicenseeId(Mockito.anyInt())).thenReturn(list);
    for (TablesEntity entity : TablesEntity.values()) {
      List<StatusBaseObject> dataTobeReturn =
              TestDataGenerator.getListOfObject(10, entity.getElasticIndex(), StatusBaseObject.class);
      when(customESRepositoryImpl.searchByNameLike(0, 10, entity.getElasticIndex(), es))
              .thenReturn(TestDataGenerator.getElasticResponse(dataTobeReturn));
      when(customESRepositoryImpl.searchByNameLike(0,10,entity.getElasticIndex(),es
              ,null,null, Sort.Direction.ASC)).thenReturn(MockDataGenerator.elasticResponse());
    }
    DictionaryResponse dRb = dashBoardService.getDictionaryData(TablesEntity.ADVERTISER, 1, 10, null, null);
    assertNotNull(dRb);
    assertNotNull(dRb.getData());
    assertThat(dRb.getTotalNoOfRecords()).isEqualTo(20);
    DictionaryResponse response = dashBoardService.getDictionaryData(TablesEntity.AGGREGATOR, 1, 10, null, null);
    assertNotNull(response);
    DictionaryResponse responses = dashBoardService.getDictionaryData(TablesEntity.AGGREGATOR, 1, 10, requests, null);
    assertNotNull(responses);
    DictionaryResponse responsedRb = dashBoardService.getDictionaryData(TablesEntity.ADVERTISER, 1, 10, request, null);
    assertNotNull(responsedRb);
    assertNotNull(responsedRb.getData());
    assertThat(responsedRb.getTotalNoOfRecords()).isEqualTo(20);
  }

  @Test
  public void testGetDictionaryDataAggregatorLicenseeMappingNull() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    DashboardFilters filters = new DashboardFilters();
    filters.setColumn("3");
    filters.setValue("4");
    filters.setOperator("AND");
    SearchRequest requests = new SearchRequest();
    requests.setFilters(null);
    List<AggregatorLicenseeMappingEntity> lists = new ArrayList<>();
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    when(aggregatorLicenseeMappingRepository.findAllByLicenseeId(Mockito.anyInt())).thenReturn(lists);
    for (TablesEntity entity : TablesEntity.values()) {
      List<StatusBaseObject> dataTobeReturn =
              TestDataGenerator.getListOfObject(10, entity.getElasticIndex(), StatusBaseObject.class);
      when(customESRepositoryImpl.searchByNameLike(0, 10, entity.getElasticIndex(), es))
              .thenReturn(TestDataGenerator.getElasticResponse(dataTobeReturn));
      when(customESRepositoryImpl.searchByNameLike(0, 10, entity.getElasticIndex(), es
              , null, null, Sort.Direction.ASC)).thenReturn(MockDataGenerator.elasticResponse());
    }
    DictionaryResponse dRb = dashBoardService.getDictionaryData(TablesEntity.AGGREGATOR, 1, 10, null, null);
    assertNotNull(dRb);
  }

  @Test
  public void testGetDictionaryDataAggregatorLicenseeMapping() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    AggregatorLicenseeMappingEntity mappingEntity = new AggregatorLicenseeMappingEntity();
    mappingEntity.setAggregatorId(-1);
    mappingEntity.setLicenseeId(3421);
    mappingEntity.setId(8881);
    mappingEntity.setIsWhiteListed(true);
    DashboardFilters filters = new DashboardFilters();
    filters.setColumn("3");
    filters.setValue("4");
    filters.setOperator("AND");
    SearchRequest requests = new SearchRequest();
    requests.setFilters(null);
    List<AggregatorLicenseeMappingEntity> lists = new ArrayList<>();
    lists.add(mappingEntity);
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    when(aggregatorLicenseeMappingRepository.findAllByLicenseeId(Mockito.anyInt())).thenReturn(lists);
    for (TablesEntity entity : TablesEntity.values()) {
      List<StatusBaseObject> dataTobeReturn =
              TestDataGenerator.getListOfObject(10, entity.getElasticIndex(), StatusBaseObject.class);
      when(customESRepositoryImpl.searchByNameLike(0, 10, entity.getElasticIndex(), es))
              .thenReturn(TestDataGenerator.getElasticResponse(dataTobeReturn));
      when(customESRepositoryImpl.searchByNameLike(0, 10, entity.getElasticIndex(), es
              , null, null, Sort.Direction.ASC)).thenReturn(MockDataGenerator.elasticResponse());
    }
    DictionaryResponse dRb = dashBoardService.getDictionaryData(TablesEntity.AGGREGATOR, 1, 10, null, null);
    assertNotNull(dRb);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#searchByName(io.revx.api.pojo.TablesEntity, java.lang.String, io.revx.core.model.requests.SearchRequest)}.
   */
  @Test
  public void testSearchByName() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    mockSecurityContext("akhilesh", false, false);
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    for (TablesEntity entity : TablesEntity.values()) {
      List<StatusBaseObject> dataTobeReturn =
              TestDataGenerator.getListOfObject(10, entity.getElasticIndex(), StatusBaseObject.class);
      when(customESRepositoryImpl.searchByNameLike(entity.getElasticIndex(), es, "test"))
              .thenReturn(TestDataGenerator.getElasticResponse(dataTobeReturn));
    }
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("advertiserId", "1234"));
    SearchRequest request = new SearchRequest();
    request.setFilters(filters);
    MenuCrubResponse mcr = dashBoardService.searchByName(TablesEntity.ADVERTISER, "test", request);
    assertNotNull(mcr);
    assertNotNull(mcr.getMenuList());
    assertThat(mcr.getMenuName()).isEqualTo(TablesEntity.ADVERTISER.getElasticIndex());
  }

  @Test
  public void testSearchByNameSuccessWithNUllSearch() throws Exception {
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(es);
    for (TablesEntity entity : TablesEntity.values()) {
      List<StatusBaseObject> dataTobeReturn =
              TestDataGenerator.getListOfObject(10, entity.getElasticIndex(), StatusBaseObject.class);
      when(customESRepositoryImpl.searchByNameLike(entity.getElasticIndex(), es, "test"))
              .thenReturn(TestDataGenerator.getElasticResponse(dataTobeReturn));
    }
    MenuCrubResponse mcr = dashBoardService.searchByName(TablesEntity.ADVERTISER, "test", null);
    assertNotNull(mcr);
    assertNotNull(mcr.getMenuList());
    assertThat(mcr.getMenuName()).isEqualTo(TablesEntity.ADVERTISER.getElasticIndex());
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#searchById(io.revx.api.pojo.TablesEntity, long)}.
   */
  @Test
  public void testSearchById() throws Exception {
    for (TablesEntity entity : TablesEntity.values()) {
      BaseModelWithModifiedTime obj =
              new BaseModelWithModifiedTime(123l, "Test Name " + entity.getElasticIndex());
      List<BaseModelWithModifiedTime> listData = new ArrayList<BaseModelWithModifiedTime>();
      listData.add(obj);
      when(customESRepositoryImpl.findById(entity.getElasticIndex(), "123")).thenReturn(listData);
    }
    BaseModel mcr = dashBoardService.searchById(TablesEntity.ADVERTISER, 123l);
    assertNotNull(mcr);
    assertThat(mcr.getId()).isEqualTo(123);
    assertThat(mcr.getName()).containsIgnoringCase("advertiser");
  }

  @Test
  public void testSearchByIdNotFound() throws Exception {
    for (TablesEntity entity : TablesEntity.values()) {
      BaseModelWithModifiedTime obj =
              new BaseModelWithModifiedTime(123l, "Test Name " + entity.getElasticIndex());
      List<BaseModelWithModifiedTime> listData = new ArrayList<BaseModelWithModifiedTime>();
      listData.add(obj);
      when(customESRepositoryImpl.findById(entity.getElasticIndex(), "123")).thenReturn(listData);
    }
    BaseModel mcr = dashBoardService.searchById(TablesEntity.ADVERTISER, 12366l);
    assertNull(mcr);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#searchDetailById(io.revx.api.pojo.TablesEntity, long)}.
   */
  @Test
  public void testSearchDetailById() throws Exception {
    Licensee li = new Licensee(123, "LI-123");
    Advertiser adv = new Advertiser(234, "Adv-234");
    adv.setLicenseeId(li.getId());
    Campaign camp = new Campaign(345L, "Camp-345");
    camp.setAdvertiserId(adv.getId());
    Strategy strt = new Strategy(5678, "Stra-5678");
    strt.setCampaignId(camp.getId());
    when(customESRepositoryImpl.findDetailById(TablesEntity.LICENSEE.getElasticIndex(),
            String.valueOf(li.getId()), Licensee.class)).thenReturn(li);
    when(customESRepositoryImpl.findDetailById(TablesEntity.ADVERTISER.getElasticIndex(),
            String.valueOf(adv.getId()), Advertiser.class)).thenReturn(adv);
    when(customESRepositoryImpl.findDetailById(TablesEntity.CAMPAIGN.getElasticIndex(),
            String.valueOf(camp.getId()), Campaign.class)).thenReturn(camp);
    when(customESRepositoryImpl.findDetailById(TablesEntity.STRATEGY.getElasticIndex(),
            String.valueOf(strt.getId()), Strategy.class)).thenReturn(strt);
    ParentBasedObject mcr = dashBoardService.searchDetailById(TablesEntity.STRATEGY, strt.getId());
    assertNotNull(mcr);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#getDashboardDataList(int, int, String, DashboardRequest, DashBoardEntity, boolean, boolean)}.
   */
  @Test
  public void testGetDashboardDataList() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    int products = 20;
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    List<Advertiser> advertiserList =
            TestDataGenerator.getListOfObject(products, entity.getElasticIndex(),Advertiser.class);
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(10, entity.getElasticIndex(), Advertiser.class);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(customESRepositoryImpl.findById(Mockito.anyString(),Mockito.anyString(),Mockito.eq(Advertiser.class)))
            .thenReturn(advertiserList);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, products));
    DashboardResponse dResp =
            dashBoardService.getDashboardDataList(1, 10, null, req, DashBoardEntity.ADVERTISER, true, true);
    assertNotNull(dResp);
    assertNotNull(dResp.getData());
    assertThat(dResp.getData().size()).isEqualTo(10);
  }

  @Test
  public void testGetDashboardDataListWIthPage() throws Exception {
    ListPerformanceDataMetrics metrics = new ListPerformanceDataMetrics();
    metrics.setHour(new BigDecimal("7"));
    metrics.setDay(new BigDecimal("2"));
    List<ListPerformanceDataMetrics> list = new ArrayList<>();
    list.add(metrics);
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("campaignId", "6427"));
    filters.add(new DashboardFilters("strategyId", "6427"));
    filters.add(new DashboardFilters("active", "true"));
    req.setFilters(filters);
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    int products = 35;
    int pageSize = 15;
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    List<Advertiser> advertiserList =
            TestDataGenerator.getListOfObject(products, entity.getElasticIndex(),Advertiser.class);
    List<Strategy> strategyList =
            TestDataGenerator.getListOfObject(products, entity.getElasticIndex(),Strategy.class);
    List<CampaignESDTO> campaignESDTOList =
            TestDataGenerator.getListOfObject(products, entity.getElasticIndex(),CampaignESDTO.class);
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(products, entity.getElasticIndex(), Advertiser.class);
    when(customESRepositoryImpl.findById(Mockito.anyString(),Mockito.anyString(),Mockito.eq(Advertiser.class)))
            .thenReturn(advertiserList);
    when(customESRepositoryImpl.findById(Mockito.anyString(),Mockito.anyString(),Mockito.eq(CampaignESDTO.class)))
            .thenReturn(campaignESDTOList);
    when(customESRepositoryImpl.findById(Mockito.anyString(),Mockito.anyString(),Mockito.eq(Strategy.class)))
            .thenReturn(strategyList);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(dashboardDao.getDashboardListData(Mockito.any(),Mockito.any(),Mockito.anySet(),Mockito.anyBoolean()))
            .thenReturn(list);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, products));
    DashboardResponse dResp = dashBoardService.getDashboardDataList(1, pageSize, null, req,
            DashBoardEntity.ADVERTISER, true, true);
    assertNotNull(dResp);
    assertThat(dResp.getTotalNoOfRecords()).isEqualTo(products);
    assertNotNull(dResp.getData());
    assertThat(dResp.getData().size()).isEqualTo(pageSize);
  }

  @Test
  public void testGetDashboardDataListREfreshWithCase() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    List<ListPerformanceDataMetrics> list = new ArrayList<>();
    list.add(MockDataGenerator.listPerformanceDataMetrics());
    int products = 35;
    int pageSize = 15;
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    List<Advertiser> advertiserList =
            TestDataGenerator.getListOfObject(products, entity.getElasticIndex(),Advertiser.class);
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(products, entity.getElasticIndex(), Advertiser.class);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(customESRepositoryImpl.findById(Mockito.anyString(),Mockito.anyString(),Mockito.eq(Advertiser.class)))
            .thenReturn(advertiserList);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(dashboardDao.getDashboardListData(Mockito.any(DashBoardEntity.class),Mockito.any(DashboardRequest.class)
            ,Mockito.anySet(),Mockito.anyBoolean())).thenReturn(list);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, products));
    DashboardResponse dResp = dashBoardService.getDashboardDataList(1, pageSize, null, req,
            DashBoardEntity.ADVERTISER, true, true);
    assertNotNull(dResp);
    assertThat(dResp.getTotalNoOfRecords()).isEqualTo(products);
    assertNotNull(dResp.getData());
    assertThat(dResp.getData().size()).isEqualTo(pageSize);
    DashboardResponse fromCache = dashBoardService.getDashboardDataList(1, pageSize, null, req,
            DashBoardEntity.ADVERTISER, false, true);
    assertNotNull(fromCache);
    assertThat(fromCache.getTotalNoOfRecords()).isEqualTo(products);
    assertNotNull(fromCache.getData());
    assertThat(fromCache.getData().size()).isEqualTo(pageSize);
  }

  @Test
  public void testGetDashboardDataListFailed() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(2));
    int products = 20;
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(10, entity.getElasticIndex(), Advertiser.class);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class))).thenReturn(
            TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, products));
    exceptionRule.expect(ApiException.class);
    dashBoardService.getDashboardDataList(1, 10, null, req, DashBoardEntity.ADVERTISER, true, true);
  }

  private List<Long> getAdvertisers(Set<Advertiser> advertisers) {
    List<Long> advs = new ArrayList<Long>();
    for (Advertiser adv : advertisers) {
      advs.add(adv.getId());
    }
    return advs;
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#getDashboardDataChart(DashboardRequest, boolean, boolean)}.
   */
  @Test
  public void testGetDashboardDataChart() throws Exception {
    ChartPerformanceDataMetrics metrics = new ChartPerformanceDataMetrics();
    metrics.setDay(new BigDecimal("3"));
    metrics.setHour(new BigDecimal("22"));
    List<ChartPerformanceDataMetrics> list = new ArrayList<>();
    list.add(metrics);
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    int products = 20;
    GroupBy grpBy = GroupBy.HOUR;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    List<Advertiser> advertiserList =
            TestDataGenerator.getListOfObject(products, entity.getElasticIndex(),Advertiser.class);
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(10, entity.getElasticIndex(), Advertiser.class);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(customESRepositoryImpl.findById(Mockito.anyString(),Mockito.anyString(),Mockito.eq(Advertiser.class)))
            .thenReturn(advertiserList);
    when(dashboardDao.getDashboardChartData(Mockito.any(),Mockito.anySet(),Mockito.anyBoolean())).thenReturn(list);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ChartPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartRespFromDB(req));
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(PerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartWidgetRespFromDB(req));
    ChartDashboardResponse cResp = dashBoardService.getDashboardDataChart(req, true, true);
    assertNotNull(cResp);
    assertNotNull(cResp.getData());
    assertNotNull(cResp.getWidgetData());
    assertThat(cResp.getTotalNoOfRecords()).isEqualTo(1);
    assertThat(cResp.getData().size()).isEqualTo(1);
  }

  @Test
  public void testGetDashboardDataChartCacheCheck() throws Exception {
    ChartPerformanceDataMetrics metrics = new ChartPerformanceDataMetrics();
    metrics.setDay(new BigDecimal("3"));
    metrics.setHour(new BigDecimal("22"));
    List<ChartPerformanceDataMetrics> list = new ArrayList<>();
    list.add(metrics);
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    int products = 20;
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.HOUR;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(10, entity.getElasticIndex(), Advertiser.class);
    List<Advertiser> advertiserList =
            TestDataGenerator.getListOfObject(products, entity.getElasticIndex(),Advertiser.class);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(customESRepositoryImpl.findById(Mockito.anyString(),Mockito.anyString(),Mockito.eq(Advertiser.class)))
            .thenReturn(advertiserList);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(dashboardDao.getDashboardChartData(Mockito.any(),Mockito.anySet(),Mockito.anyBoolean())).thenReturn(list);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ChartPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartRespFromDB(req));
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(PerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartWidgetRespFromDB(req));
    ChartDashboardResponse cResp = dashBoardService.getDashboardDataChart(req, false, true);
    assertNotNull(cResp);
    assertNotNull(cResp.getData());
    assertNotNull(cResp.getWidgetData());
    assertThat(cResp.getTotalNoOfRecords()).isEqualTo(1);
    assertThat(cResp.getData().size()).isEqualTo(1);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#getCsvResponseForChart(io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testGetCsvResponseForChart() throws Exception {
    ChartPerformanceDataMetrics metrics = new ChartPerformanceDataMetrics();
    metrics.setDay(new BigDecimal("3"));
    metrics.setHour(new BigDecimal("22"));
    List<ChartPerformanceDataMetrics> list = new ArrayList<>();
    list.add(metrics);
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.HOUR;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(10, entity.getElasticIndex(), Advertiser.class);
    when(dashboardDao.getDashboardChartData(Mockito.any(),Mockito.anySet(),Mockito.anyBoolean())).thenReturn(list);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ChartPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartRespFromDB(req));
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(PerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartWidgetRespFromDB(req));
    FileDownloadResponse cResp = dashBoardService.getCsvResponseForChart(req);
    assertNotNull(cResp);
    assertThat(cResp.getFileDownloadUrl()).startsWith("www.abc.com");
    assertThat(cResp.getFileName()).contains("chart");

  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#getCsvDataForChart(io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testGetCsvDataForChart() throws Exception {
    ChartPerformanceDataMetrics metrics = new ChartPerformanceDataMetrics();
    metrics.setDay(new BigDecimal("3"));
    metrics.setHour(new BigDecimal("22"));
    List<ChartPerformanceDataMetrics> list = new ArrayList<>();
    list.add(metrics);
    PerformanceDataMetrics metric = new PerformanceDataMetrics();
    metric.setClickconversions(new BigDecimal("73"));
    metric.setClicks(new BigDecimal("76"));
    List<PerformanceDataMetrics> lists = new ArrayList<>();
    lists.add(metric);
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest dashboardRequest = MockDataGenerator.getDashboardRequest();
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    when(dashboardDao.getDashboardChartData(Mockito.any(),Mockito.anySet(),Mockito.anyBoolean()))
            .thenReturn(list);
    when(dashboardDao.getDashboardChartWidgetData(Mockito.any(),Mockito.anySet(),Mockito.anyBoolean()))
            .thenReturn(lists);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyIdIfAdvLogin()).thenReturn("test");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("test");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    List<ChartCSVDashboardData> response = dashBoardService.getCsvDataForChart(dashboardRequest);
    assertNotNull(response);
    assertEquals("test",response.get(0).currencyId);
  }

  @Test
  public void testGetCsvDataForChartNull() throws Exception {
    DashboardRequest dashboardRequest = MockDataGenerator.getDashboardRequest();
    List<ChartCSVDashboardData> response = dashBoardService.getCsvDataForChart(dashboardRequest);
    assertNotNull(response);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#getCsvResponseForList(io.revx.api.pojo.DashBoardEntity, io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testGetCsvResponseForList() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.HOUR;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ElasticSearchTerm est = new ElasticSearchTerm();
    if (ui != null) {
      est.setLicenseeId(ui.getSelectedLicensee().getId());
      if (ui.getAdvertisers() != null) {
        est.setAdvertisers(getAdvertisers(ui.getAdvertisers()));
      }
    }
    TablesEntity entity = TablesEntity.ADVERTISER;
    Map<Long, Advertiser> idModelMap =
            TestDataGenerator.getMapOfObject(10, entity.getElasticIndex(), Advertiser.class);
    when(customESRepositoryImpl.search(entity.getElasticIndex(), est, Advertiser.class))
            .thenReturn(idModelMap);
    when(loginUserDetailsService.getUserInfo()).thenReturn(ui);
    when(loginUserDetailsService.getAdvertiserCurrencyId()).thenReturn("INR");
    when(loginUserDetailsService.getLicenseeCurrencyId()).thenReturn("USD");
    when(loginUserDetailsService.getElasticSearchTerm()).thenReturn(est);
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ChartPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartRespFromDB(req));
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(PerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getChartWidgetRespFromDB(req));
    when(performanceDataRepositoryImpl.queryToDataBase(Mockito.anyString(),
            Mockito.eq(ListPerformanceDataMetrics.class)))
            .thenReturn(TestDataGenerator.getListRespFromDB(req, DashBoardEntity.ADVERTISER, 20));
    FileDownloadResponse cResp =
            dashBoardService.getCsvResponseForList(DashBoardEntity.ADVERTISER, req);

    assertNotNull(cResp);
    assertThat(cResp.getFileDownloadUrl()).startsWith("www.abc.com");
    assertThat(cResp.getFileName()).contains("list");

  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#filterDataForInternalUserRole(io.revx.api.pojo.TablesEntity, io.revx.core.model.requests.DictionaryResponse)}.
   */
  @Test
  public void testFilterDataForInternalUserRole() throws Exception{
    List<BaseModel> list = new ArrayList<>();
    list.add(MockDataGenerator.createBaseModel());
    DictionaryResponse data = new DictionaryResponse();
    data.setData(list);
    DictionaryResponse response = dashBoardService.filterDataForInternalUserRole(TablesEntity.ADVERTISER_REGION,data);
    assertNotNull(response);
    assertEquals(1L,response.getData().size());
    DictionaryResponse responses = dashBoardService.filterDataForInternalUserRole(TablesEntity.AGGREGATOR,data);
    assertNotNull(responses);
    assertEquals(1L,responses.getData().size());
  }

  /**
   * Test method for
   * {@link io.revx.api.service.DashBoardService#getPricingType(io.revx.api.pojo.TablesEntity)}.
   */
  @Test
  public void testGetPricingType() throws Exception{
    ElasticSearchTerm es = new ElasticSearchTerm();
    es.setLicenseeId(33L);
    TablesEntity entity = TablesEntity.ADVERTISER;
    Mockito.when(customESRepositoryImpl.searchAsList(Mockito.anyString(),Mockito.any(),Mockito.eq(BaseModel.class)))
            .thenReturn(TestDataGenerator.getListOfObject(10, entity.getElasticIndex(),
                    BaseModel.class));
    List<BaseModel> response  = dashBoardService.getPricingType(TablesEntity.STRATEGY);
    assertNotNull(response);
    assertEquals(0L,response.size());
    List<BaseModel> resp  = dashBoardService.getPricingType(TablesEntity.CAMPAIGN);
    assertNotNull(resp);
    assertEquals(0L,resp.size());
    List<BaseModel> r  = dashBoardService.getPricingType(TablesEntity.AGGREGATOR);
    assertNotNull(r);
    assertEquals(0L,r.size());
  }

  /**
   * Test method for
   * {@link DashBoardService#getAdvertiserRoiTypes()}.
   */
  @Test
  public void testGetAdvertiserRoiTypes() throws Exception{
    Map<Long, List<BaseModel>> response = dashBoardService.getAdvertiserRoiTypes();
    assertNotNull(response);
  }
}
