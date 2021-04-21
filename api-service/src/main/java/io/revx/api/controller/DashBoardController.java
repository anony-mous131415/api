package io.revx.api.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.DashBoardService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.BaseModel;
import io.revx.core.model.ChartCSVDashboardData;
import io.revx.core.model.ListCSVDashboardData;
import io.revx.core.model.ParentBasedObject;
import io.revx.core.model.requests.ChartDashboardResponse;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.DashboardResponse;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.requests.MenuCrubResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Dashboard Controller", tags = {"Dashboard Controller"},
    description = "REST API's for populating DashBoard Data")
public class DashBoardController {

  private static final Logger logger = LogManager.getLogger(DashBoardController.class);


  @Autowired
  DashBoardService dashBoardService;


  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CHART)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CHART)
  @ApiOperation("DashBoard Api For TimeLine.")
  @PostMapping(value = ApiConstant.DASHBOARD_CHART, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ChartDashboardResponse>> getDashboardDataChart(
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestParam(name = ApiConstant.SHOW_UU) boolean showUU,
      @RequestBody(required = true) @Valid DashboardRequest dashboardRequest) throws Exception {
    logger.info(" : {} ", dashboardRequest);
    ChartDashboardResponse dashboardResponse =
        dashBoardService.getDashboardDataChart(dashboardRequest, refresh, showUU);
    ApiResponseObject<ChartDashboardResponse> dResp =
        new ApiResponseObject<ChartDashboardResponse>();
    dResp.setRespObject(dashboardResponse);
    return ResponseEntity.ok().body(dResp);
  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.LIST)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.LIST)
  @ApiOperation("DashBoard Api For DashBoard.")
  @PostMapping(value = ApiConstant.DASHBOARD_LIST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<DashboardResponse>> getDashboardDataList(
      @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      @RequestParam(name = ApiConstant.SORT, required = false) String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestParam(name = ApiConstant.SHOW_UU) boolean hideUU,
      @PathVariable("entity") DashBoardEntity entity,
      @RequestBody(required = true) @Valid DashboardRequest dashboardRequest) throws Exception {
    logger.info("Called dashboard api for : {} " , entity);
    DashboardResponse dashboardResponse = dashBoardService.getDashboardDataList(pageNumber,
        pageSize, sort, dashboardRequest, entity, refresh, hideUU);
    ApiResponseObject<DashboardResponse> dResp = new ApiResponseObject<DashboardResponse>();
    dResp.setRespObject(dashboardResponse);
    return ResponseEntity.ok().body(dResp);
  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.DICTIONARY)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.DICTIONARY)
  @ApiOperation("DashBoard Api For Getting Table Name And Id.")
  @PostMapping(value = ApiConstant.DICTIONARY, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<DictionaryResponse>> getDictionary(
      @PathVariable TablesEntity tableEntity,
      @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      @RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestBody(required = false) SearchRequest request) {
    logger.info("Called dashboard getDictionary api for : {} " , tableEntity);
    DictionaryResponse resp =
        dashBoardService.getDictionaryData(tableEntity, pageNumber, pageSize, request,sort );
    ApiResponseObject<DictionaryResponse> dResp = new ApiResponseObject<DictionaryResponse>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);
  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.DICTIONARY)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.DICTIONARY)
  @ApiOperation("DashBoard Api For Getting Table Name And Id.")
  @PostMapping(value = ApiConstant.DICTIONARY_DETAIL, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<EResponse<?>>> getDetailDictionary(
      @PathVariable TablesEntity tableEntity,
      @RequestParam(name = "pageNumber", defaultValue = "1") Integer pageNumber,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      @RequestParam(name = ApiConstant.SORT, defaultValue = "id-") String sort,
      @RequestParam(name = ApiConstant.REFRESH, required = false) boolean refresh,
      @RequestBody(required = false) SearchRequest request) {
    logger.info("Called dashboard getDictionary api for : {} " , tableEntity);
    EResponse<?> resp =
        dashBoardService.getDetailDictionaryData(tableEntity, pageNumber, pageSize, request,sort);
    ApiResponseObject<EResponse<?>> dResp = new ApiResponseObject<>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);
  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.MENU)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.MENU)
  @GetMapping(value = ApiConstant.MENU_CRUMBS, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<List<MenuCrubResponse>>> getMenuCrumbs() {
    List<MenuCrubResponse> resp = dashBoardService.getMenuCrubResponse();
    ApiResponseObject<List<MenuCrubResponse>> dResp =
        new ApiResponseObject<List<MenuCrubResponse>>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);

  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.SEARCH)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.SEARCH)
  // @GetMapping(value = ApiConstant.SEARCH_BY_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
  @PostMapping(value = ApiConstant.SEARCH_BY_NAME, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<MenuCrubResponse>> searchByName(
      @PathVariable TablesEntity tableEntity,
      @RequestParam(name = ApiConstant.SEARCH, required = false) String search,
      @RequestBody(required = false) SearchRequest request) throws Exception {
    MenuCrubResponse resp = dashBoardService.searchByName(tableEntity, search, request);
    ApiResponseObject<MenuCrubResponse> dResp = new ApiResponseObject<MenuCrubResponse>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);

  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.FIND_ID)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.FIND_ID)
  @GetMapping(value = ApiConstant.GET_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<BaseModel>> getById(
      @PathVariable TablesEntity tableEntity, @PathVariable Long id) {
    BaseModel resp = dashBoardService.searchById(tableEntity, id);
    ApiResponseObject<BaseModel> dResp = new ApiResponseObject<BaseModel>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);
  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.FIND_DETAIL_BY_ID)
  @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.FIND_DETAIL_BY_ID)
  @GetMapping(value = ApiConstant.DETAIL_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<ParentBasedObject>> getDetailById(
      @PathVariable TablesEntity tableEntity, @PathVariable Long id) {
    ParentBasedObject resp = dashBoardService.searchDetailById(tableEntity, id);
    ApiResponseObject<ParentBasedObject> dResp = new ApiResponseObject<ParentBasedObject>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);
  }


  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CHART + GraphiteConstants.CSV)
  @ApiOperation("DashBoard Api For download TimeLine CSV BY Stream.")
  @PostMapping(value = ApiConstant.DASHBOARD_CHART + "/csv/stream", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void getDashboardDataChartCsvStream(
      @RequestBody(required = true) @Valid DashboardRequest dashboardRequest,
      HttpServletResponse response) throws Exception {
    logger.info(" : {} ", dashboardRequest);
    // set file name and content type
    String filename = dashBoardService.getCsvFileName(dashboardRequest, null, "chart");
    response.setContentType("text/csv");
    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + filename + "\"");
    // create a csv writer
    StatefulBeanToCsv<ChartCSVDashboardData> writer =
        new StatefulBeanToCsvBuilder<ChartCSVDashboardData>(response.getWriter())
            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withOrderedResults(false).build();
    // write all users to csv file
    List<ChartCSVDashboardData> resultForCsv =
        dashBoardService.getCsvDataForChart(dashboardRequest);
    writer.write(resultForCsv);

  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.LIST + GraphiteConstants.CSV)
  @ApiOperation("DashBoard Api For Download List CSV By Stream.")
  @PostMapping(value = ApiConstant.DASHBOARD_LIST + "/csv/stream")
  public void getDashboardDataListCsvStream(@PathVariable("entity") DashBoardEntity entity,
      @RequestBody(required = true) @Valid DashboardRequest dashboardRequest,
      HttpServletResponse response) throws Exception {
    logger.info("Called dashboard api for : " + entity);
    // set file name and content type
    String filename = dashBoardService.getCsvFileName(dashboardRequest, entity, "list");
    response.setContentType("text/csv");
    response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename=\"" + filename + "\"");
    // create a csv writer
    StatefulBeanToCsv<ListCSVDashboardData> writer =
        new StatefulBeanToCsvBuilder<ListCSVDashboardData>(response.getWriter())
            .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER).withSeparator(CSVWriter.DEFAULT_SEPARATOR)
            .withOrderedResults(false).build();
    // write all users to csv file
    List<ListCSVDashboardData> resultForCsv =
        dashBoardService.getCsvDataForList(entity, dashboardRequest);
    writer.write(resultForCsv);
  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CHART + GraphiteConstants.CSV)
  @ApiOperation("DashBoard Api For download TimeLine CSV .")
  @PostMapping(value = ApiConstant.DASHBOARD_CHART + "/csv",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<FileDownloadResponse>> getDashboardDataChartCsv(
      @RequestBody(required = true) @Valid DashboardRequest dashboardRequest) throws Exception {
    logger.info(" : {} ", dashboardRequest);
    FileDownloadResponse fresp = dashBoardService.getCsvResponseForChart(dashboardRequest);
    ApiResponseObject<FileDownloadResponse> dResp = new ApiResponseObject<FileDownloadResponse>();
    dResp.setRespObject(fresp);
    return ResponseEntity.ok().body(dResp);

  }

  @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.LIST + GraphiteConstants.CSV)
  @ApiOperation("DashBoard Api For Download List CSV.")
  @PostMapping(value = ApiConstant.DASHBOARD_LIST + "/csv",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<FileDownloadResponse>> getDashboardDataListCsv(
      @PathVariable("entity") DashBoardEntity entity,
      @RequestBody(required = true) @Valid DashboardRequest dashboardRequest) throws Exception {
    logger.info("Called dashboard api for : " + entity);
    FileDownloadResponse resp = dashBoardService.getCsvResponseForList(entity, dashboardRequest);
    ApiResponseObject<FileDownloadResponse> dResp = new ApiResponseObject<FileDownloadResponse>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);
  }


  /*
   * @ApiOperation("DashBoard Api For getAdvertiserRoiTypes .")
   * 
   * @GetMapping(value = ApiConstant.advertiserRoiTypes, produces =
   * MediaType.APPLICATION_JSON_VALUE) public ResponseEntity<ApiResponseObject<Map<Long,
   * List<BaseModel>>>> getAdvertiserRoiTypes() { Map<Long, List<BaseModel>> resp =
   * dashBoardService.getAdvertiserRoiTypes(); ApiResponseObject<Map<Long, List<BaseModel>>> dResp =
   * new ApiResponseObject<>(); dResp.setRespObject(resp); return ResponseEntity.ok().body(dResp); }
   * 
   */
  @ApiOperation("DashBoard Api For pricingType .")
  @GetMapping(value = ApiConstant.pricing, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<List<BaseModel>>> getpricingType(
      @PathVariable TablesEntity tableEntity) {
    List<BaseModel> resp = dashBoardService.getPricingType(tableEntity);
    ApiResponseObject<List<BaseModel>> dResp = new ApiResponseObject<>();
    dResp.setRespObject(resp);
    return ResponseEntity.ok().body(dResp);
  }
}
