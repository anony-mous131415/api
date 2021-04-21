package io.revx.api.controller.slicex;

import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.revx.api.constants.ApiConstant;
import io.revx.api.enums.SlicexEntity;
import io.revx.api.service.ISlicexService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.requests.SlicexChartResponse;
import io.revx.core.model.requests.SlicexListResponse;
import io.revx.core.model.requests.SlicexRequest;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "SliceX Controller", tags = {"SliceX Controller"})
public class SlicexController {
  private static Logger logger = LogManager.getLogger(SlicexController.class);

  @Autowired
  @Qualifier("slicexServiceImpl")
  ISlicexService slicexService;

  @LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.CONTROLLER
      + GraphiteConstants.CHART)
  @ApiOperation("Slicex API for graph.")
  @RequestMapping(value = ApiConstant.SLICEX_CHART, method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<SlicexChartResponse>> getSlicexDataChart(
      @RequestBody(required = true) @Valid SlicexRequest slicexRequest) throws Exception {
    logger.info("Called slicex chart api : {} ", slicexRequest);
    SlicexChartResponse slicexChartResponse = slicexService.getSlicexChartData(slicexRequest);

    ApiResponseObject<SlicexChartResponse> slicexChartResp =
        new ApiResponseObject<SlicexChartResponse>();

    slicexChartResp.setRespObject(slicexChartResponse);
    return ResponseEntity.ok().body(slicexChartResp);
  }

  @LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.CONTROLLER
      + GraphiteConstants.LIST)
  @ApiOperation("Slicex API for grid.")
  @RequestMapping(value = ApiConstant.SLICEX_LIST, method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<SlicexListResponse>> getSlicexDataList(
      @RequestParam(name = ApiConstant.SORT, required = false) String sort,
      @PathVariable("entity") SlicexEntity entity,
      @RequestBody(required = true) @Valid SlicexRequest slicexRequest) throws Exception {
    logger.info("Called slicex grid api for : " + entity);

    SlicexListResponse slicexListResponse =
        slicexService.getSlicexGridData(slicexRequest, sort, entity);

    ApiResponseObject<SlicexListResponse> slicexListResp =
        new ApiResponseObject<SlicexListResponse>();
    slicexListResp.setRespObject(slicexListResponse);
    return ResponseEntity.ok().body(slicexListResp);
  }

  @LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.CONTROLLER
      + GraphiteConstants.LIST_EXPORT)
  @ApiOperation("Slicex API for List Export.")
  @RequestMapping(value = ApiConstant.SLICEX_LIST_EXPORT, method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FileDownloadResponse> getSlicexListDataForExport(
      @RequestParam(name = ApiConstant.SORT, required = false) String sort,
      @PathVariable("entity") SlicexEntity entity,
      @RequestBody(required = true) @Valid SlicexRequest slicexRequest) throws Exception {
    logger.info("Called slicex list api for export for entity : " + entity);

    FileDownloadResponse slicexListExportResponse =
        slicexService.getSlicexGridDataForExport(slicexRequest, sort, entity);

    return ResponseEntity.ok().body(slicexListExportResponse);
  }

}
