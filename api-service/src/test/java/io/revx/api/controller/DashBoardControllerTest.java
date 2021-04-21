/**
 *
 */
package io.revx.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.revx.api.common.MockDataGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.DashBoardService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.ChartCSVDashboardData;
import io.revx.core.model.DashboardData;
import io.revx.core.model.DashboardMetrics;
import io.revx.core.model.ListCSVDashboardData;
import io.revx.core.model.ParentBasedObject;
import io.revx.core.model.StatusBaseObject;
import io.revx.core.model.requests.ChartDashboardResponse;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.DashboardResponse;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.Duration;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.requests.MenuCrubResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiResponseObject;
import io.revx.querybuilder.enums.GroupBy;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DashBoardControllerTest {
  @Mock
  private DashBoardService dashBoardService;


  @Mock
  private LoginUserDetailsService loginUserDetailsService;
  @InjectMocks
  private DashBoardController dashBoardController;

  private MockMvc mockMvc;


  /**
   * @throws java.lang.Exception
   */
  static {
    System.setProperty("jasypt.encryptor.password", "mySecretKey@123");
  }

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
    CommonExceptionHandler hadler = new CommonExceptionHandler();
    hadler.apiErrorCodeResolver = apiErrorCodeResolver;
    mockMvc =
            MockMvcBuilders.standaloneSetup(dashBoardController).setControllerAdvice(hadler).build();
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDashboardDataChart(boolean, io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testGetDashboardDataChart() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    ChartDashboardResponse resp = new ChartDashboardResponse();
    resp.setData(TestDataGenerator.getListOfObject(20, DashboardData.class));
    resp.setTotalNoOfRecords(20);
    resp.setWidgetData(TestDataGenerator.getObject(DashboardMetrics.class));
    Mockito.when(dashBoardService.getDashboardDataChart(Mockito.any(DashboardRequest.class),
        Mockito.anyBoolean(),Mockito.anyBoolean())).thenReturn(resp);
    RequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(io.revx.api.constants.ApiConstant.DASHBOARD_CHART).accept(MediaType.APPLICATION_JSON)
            .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(req)).param("refresh", "true")
            .param("showuu","false");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<ChartDashboardResponse>>() {}.getType();
    ApiResponseObject<ChartDashboardResponse> apiResp =
            new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertNotNull(apiResp.getRespObject().getData());
    assertNotNull(apiResp.getRespObject().getWidgetData());
    assertThat(apiResp.getRespObject().getData().size()).isEqualTo(20);

  }

  @Test
  public void testGetDashboardDataChartFailed() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    ChartDashboardResponse resp = new ChartDashboardResponse();
    resp.setData(TestDataGenerator.getListOfObject(20, DashboardData.class));
    resp.setTotalNoOfRecords(20);
    resp.setWidgetData(TestDataGenerator.getObject(DashboardMetrics.class));
    Mockito.when(dashBoardService.getDashboardDataChart(Mockito.any(DashboardRequest.class),
            Mockito.anyBoolean(),Mockito.anyBoolean())).thenThrow(new ValidationException("Some error"));

    RequestBuilder requestBuilder = MockMvcRequestBuilders
            .post(io.revx.api.constants.ApiConstant.DASHBOARD_CHART).accept(MediaType.APPLICATION_JSON)
            .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(req)).param("refresh", "true");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<ChartDashboardResponse>>() {}.getType();
    ApiResponseObject<ChartDashboardResponse> apiResp =
            new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getError());
    assertThat(apiResp.getError().getCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getValue());
    assertNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDashboardDataList(java.lang.Integer, java.lang.Integer, java.lang.String, boolean, io.revx.api.pojo.DashBoardEntity, io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testGetDashboardDataList() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.ADVERTISER_ID;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    DashboardResponse resp = new DashboardResponse();
    resp.setData(TestDataGenerator.getListOfObject(20, DashboardData.class));
    resp.setTotalNoOfRecords(20);
    Mockito.when(dashBoardService.getDashboardDataList(Mockito.anyInt(), Mockito.anyInt(),
        Mockito.anyString(), Mockito.any(DashboardRequest.class),
        Mockito.any(DashBoardEntity.class), Mockito.anyBoolean(),Mockito.anyBoolean())).thenReturn(resp);
    String listUrl = io.revx.api.constants.ApiConstant.DASHBOARD_LIST.replace("{entity}",
            DashBoardEntity.ADVERTISER.getName());
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(listUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(req))
            .param(io.revx.api.constants.ApiConstant.REFRESH, "true").param("pageNumber", "1")
            .param("pageSize", "10").param(ApiConstant.SORT, "id")
            .param("showuu","false");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<DashboardResponse>>() {}.getType();
    ApiResponseObject<DashboardResponse> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertNotNull(apiResp.getRespObject().getData());
    assertThat(apiResp.getRespObject().getData().size()).isEqualTo(20);

  }

  @Test
  public void testGetDashboardDataListFailed() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.ADVERTISER_ID;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    DashboardResponse resp = new DashboardResponse();
    resp.setData(TestDataGenerator.getListOfObject(20, DashboardData.class));
    resp.setTotalNoOfRecords(20);
    Mockito.when(dashBoardService.getDashboardDataList(Mockito.anyInt(), Mockito.anyInt(),
                    Mockito.anyString(), Mockito.any(DashboardRequest.class),
                    Mockito.any(DashBoardEntity.class), Mockito.anyBoolean(),Mockito.anyBoolean()))
            .thenThrow(new ValidationException("Some error"));
    String listUrl = io.revx.api.constants.ApiConstant.DASHBOARD_LIST.replace("{entity}",
            DashBoardEntity.ADVERTISER.getName());
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(listUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(req))
            .param(io.revx.api.constants.ApiConstant.REFRESH, "true").param("pageNumber", "1")
            .param("pageSize", "10").param(ApiConstant.SORT, "id")
            .param("showuu","false");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<DashboardResponse>>() {}.getType();
    ApiResponseObject<DashboardResponse> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getError());
    assertThat(apiResp.getError().getCode()).isEqualTo(ErrorCode.BAD_REQUEST.getValue());
    assertNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDictionary(io.revx.api.pojo.TablesEntity, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, boolean)}.
   */
  @Test
  public void testGetDictionary() throws Exception {
    List<DashboardFilters> list = new ArrayList<>();
    List<BaseModel> baseModelList = new ArrayList<>();
    baseModelList.add(MockDataGenerator.createBaseModel());
    list.add(MockDataGenerator.getDashBoardFilters());
    SearchRequest req = new SearchRequest();
    req.setFilters(list);
    DictionaryResponse resp = new DictionaryResponse();
    resp.setData(baseModelList);
    resp.setTotalNoOfRecords(1);
    Mockito.when(dashBoardService.getDictionaryData(Mockito.any(TablesEntity.class), Mockito.anyInt(), Mockito.anyInt(),
            Mockito.any(SearchRequest.class),Mockito.anyString())).thenReturn(resp);
    String dictUrl = io.revx.api.constants.ApiConstant.DICTIONARY.replace("{tableEntity}",
            "ADVERTISER");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(dictUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(req))
            .param(io.revx.api.constants.ApiConstant.REFRESH, "true").param("pageNumber", "1")
            .param("pageSize", "10").param(ApiConstant.SORT, "id");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<DictionaryResponse>>() {}.getType();
    ApiResponseObject<DictionaryResponse> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertNotNull(apiResp.getRespObject().getData());
    assertThat(apiResp.getRespObject().getData().size()).isEqualTo(1);

  }

  /**
   * Test method for {@link io.revx.api.controller.DashBoardController#getMenuCrumbs()}.
   */
  @Test
  public void testGetMenuCrumbs() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.ADVERTISER_ID;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    List<MenuCrubResponse> resp = new ArrayList<MenuCrubResponse>();
    for (TablesEntity entity : TablesEntity.values()) {
      MenuCrubResponse mResp = new MenuCrubResponse(entity.getElasticIndex(),
              TestDataGenerator.getListOfObject(15, StatusBaseObject.class));
      resp.add(mResp);
    }
    Mockito.when(dashBoardService.getMenuCrubResponse()).thenReturn(resp);
    String url = io.revx.api.constants.ApiConstant.MENU_CRUMBS;

    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<List<MenuCrubResponse>>>() {}.getType();
    ApiResponseObject<List<MenuCrubResponse>> apiResp =
            new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().size()).isEqualTo(TablesEntity.values().length);

  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#searchByName(io.revx.api.pojo.TablesEntity, java.lang.String, io.revx.core.model.requests.SearchRequest)}.
   */
  @Test
  public void testSearchByName() throws Exception {
    List<DashboardFilters> filters = new ArrayList<DashboardFilters>();
    filters.add(new DashboardFilters("advertiserId", "1234"));
    SearchRequest req = new SearchRequest();
    req.setFilters(filters);
    MenuCrubResponse resp = new MenuCrubResponse(TablesEntity.ADVERTISER.getElasticIndex(),
            TestDataGenerator.getListOfObject(15, StatusBaseObject.class));
    Mockito.when(dashBoardService.searchByName(Mockito.any(TablesEntity.class), Mockito.anyString(),
            Mockito.any(SearchRequest.class))).thenReturn(resp);
    String listUrl = io.revx.api.constants.ApiConstant.SEARCH_BY_NAME.replace("{tableEntity}",
            TablesEntity.ADVERTISER.name());
    RequestBuilder requestBuilder =
            MockMvcRequestBuilders.post(listUrl).accept(MediaType.APPLICATION_JSON)
                    .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                    .content(new Gson().toJson(req)).param(ApiConstant.SEARCH, "true");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<MenuCrubResponse>>() {}.getType();
    ApiResponseObject<MenuCrubResponse> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertNotNull(apiResp.getRespObject().getMenuList());
    assertThat(apiResp.getRespObject().getMenuList().size()).isEqualTo(15);
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getById(io.revx.api.pojo.TablesEntity, java.lang.Long)}.
   */
  @Test
  public void testGetById() throws Exception {
    BaseModel obj = new BaseModel(12345, "BaseMpdel - 12345");
    long val = 12345;
    Mockito.when(dashBoardService.searchById(Mockito.any(TablesEntity.class), Mockito.eq(val)))
            .thenReturn(obj);
    String listUrl = io.revx.api.constants.ApiConstant.GET_BY_ID
            .replace("{tableEntity}", TablesEntity.ADVERTISER.name()).replace("{id}", "12345");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(listUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<BaseModel>>() {}.getType();
    ApiResponseObject<BaseModel> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getId()).isEqualTo(obj.getId());
  }


  @Test
  public void testGetByIdFailed() throws Exception {
    BaseModel obj = new BaseModel(12345, "BaseMpdel - 12345");
    long val = 12345;
    Mockito.when(dashBoardService.searchById(Mockito.any(TablesEntity.class), Mockito.eq(val)))
            .thenReturn(obj);
    String listUrl = io.revx.api.constants.ApiConstant.GET_BY_ID
            .replace("{tableEntity}", TablesEntity.ADVERTISER.name()).replace("{id}", "12");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(listUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<BaseModel>>() {}.getType();
    ApiResponseObject<BaseModel> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDetailById(io.revx.api.pojo.TablesEntity, java.lang.Long)}.
   */
  @Test
  public void testGetDetailById() throws Exception {
    long val = 12345;
    ParentBasedObject obj = new ParentBasedObject();
    obj.setId(val);
    obj.setName("Parent Obj");
    Mockito
            .when(dashBoardService.searchDetailById(Mockito.any(TablesEntity.class), Mockito.eq(val)))
            .thenReturn(obj);
    String listUrl = io.revx.api.constants.ApiConstant.DETAIL_BY_ID
            .replace("{tableEntity}", TablesEntity.ADVERTISER.name()).replace("{id}", "12345");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(listUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<ParentBasedObject>>() {}.getType();
    ApiResponseObject<ParentBasedObject> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getId()).isEqualTo(val);
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDashboardDataChartCsv1(io.revx.core.model.requests.DashboardRequest, javax.servlet.http.HttpServletResponse)}.
   */
  @Test
  public void testGetDashboardDataChartCsvStream() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    List<ChartCSVDashboardData> data =
            TestDataGenerator.getListOfObject(20, ChartCSVDashboardData.class);
    Mockito
            .when(dashBoardService.getCsvFileName(Mockito.any(DashboardRequest.class),
                    Mockito.any(DashBoardEntity.class), Mockito.anyString()))
            .thenReturn("/tmp/file_" + System.currentTimeMillis());
    Mockito.when(dashBoardService.getCsvDataForChart(Mockito.any(DashboardRequest.class)))
            .thenReturn(data);
    RequestBuilder requestBuilder =
            MockMvcRequestBuilders.post(ApiConstant.DASHBOARD_CHART + "/csv/stream")
                    .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                    .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(req));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    assertNotNull(contentString);

  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDashboardDataListCsv1(io.revx.api.pojo.DashBoardEntity, io.revx.core.model.requests.DashboardRequest, javax.servlet.http.HttpServletResponse)}.
   */
  @Test
  public void testGetDashboardDataListCsvStream() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.ADVERTISER_ID;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    List<ListCSVDashboardData> data =
            TestDataGenerator.getListOfObject(20, ListCSVDashboardData.class);

    Mockito
            .when(dashBoardService.getCsvFileName(Mockito.any(DashboardRequest.class),
                    Mockito.any(DashBoardEntity.class), Mockito.anyString()))
            .thenReturn("/tmp/file_" + System.currentTimeMillis());
    Mockito.when(dashBoardService.getCsvDataForList(Mockito.any(DashBoardEntity.class),
            Mockito.any(DashboardRequest.class))).thenReturn(data);
    String listUrl =
            ApiConstant.DASHBOARD_LIST.replace("{entity}", DashBoardEntity.ADVERTISER.getName())
                    + "/csv/stream";
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(listUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(req));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    assertNotNull(contentString);

  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDashboardDataChartCsv(io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testGetDashboardDataChartCsv() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    FileDownloadResponse resp = new FileDownloadResponse();
    resp.setFileDownloadUrl("www.abc.com");
    resp.setFileName("/tmp/test.txt");
    Mockito.when(dashBoardService.getCsvResponseForChart(Mockito.any(DashboardRequest.class)))
            .thenReturn(resp);
    RequestBuilder requestBuilder =
            MockMvcRequestBuilders.post(ApiConstant.DASHBOARD_CHART + "/csv")
                    .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                    .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(req));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<FileDownloadResponse>>() {}.getType();
    ApiResponseObject<FileDownloadResponse> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getFileDownloadUrl()).startsWith("www.abc.com");
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.DashBoardController#getDashboardDataListCsv(io.revx.api.pojo.DashBoardEntity, io.revx.core.model.requests.DashboardRequest)}.
   */
  @Test
  public void testGetDashboardDataListCsv() throws Exception {
    DashboardRequest req = new DashboardRequest();
    Duration dur = new Duration();
    req.setDuration(dur);
    GroupBy grpBy = GroupBy.DAY;
    req.setGroupBy(grpBy.getColumn());
    dur.setEndTimeStamp(TestDataGenerator.getDayEpoc(0));
    dur.setStartTimeStamp(TestDataGenerator.getDayEpoc(-2));
    FileDownloadResponse resp = new FileDownloadResponse();
    resp.setFileDownloadUrl("www.abc.com");
    resp.setFileName("/tmp/test.txt");
    Mockito.when(dashBoardService.getCsvResponseForList(Mockito.any(DashBoardEntity.class),
            Mockito.any(DashboardRequest.class))).thenReturn(resp);
    String listUrl =
            ApiConstant.DASHBOARD_LIST.replace("{entity}", DashBoardEntity.ADVERTISER.getName())
                    + "/csv";
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(listUrl)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(req));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<FileDownloadResponse>>() {}.getType();
    ApiResponseObject<FileDownloadResponse> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getFileDownloadUrl()).startsWith("www.abc.com");
  }

}
