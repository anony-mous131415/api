package io.revx.api.controller.strategy;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.revx.api.constants.ApiConstant;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping(ApiConstant.bulkstrategies)
@Api(value = "Bulk Strategy Controller", tags = {"Bulk Strategy Controller"},
    description = "Bulk  Strategy update ")
public class BulkstrategiesController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BulkstrategiesController.class);

  @Autowired
  private BulkStrategiesServiceImpl bulkStrategiesService;


  @ApiOperation("Bulk Strategy Api For export CSV .")
  @RequestMapping(value = ApiConstant.export, method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<FileDownloadResponse>> exportStrategiesData(
      @RequestBody BulkstrategiesRequest bulkstrategiesRequest) throws Exception {
    FileDownloadResponse resp =
        bulkStrategiesService.getBulkStrategiesDataTSV(bulkstrategiesRequest);
    LOGGER.info("bulk strategies export - done");
    ApiResponseObject<FileDownloadResponse> aResp = new ApiResponseObject<>();
    aResp.setRespObject(resp);
    return ResponseEntity.ok().body(aResp);
  }


  /**
   * Export the strategies data in TSV format
   * 
   * @param bulkstrategiesRequest
   * @return
   * @throws Exception
   */
  @ApiOperation("Bulk Strategy Api For Validate CSV .")
  @RequestMapping(value = ApiConstant.VALIDATE, method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<List<BulkstrategiesValidationResponse>>> importAndValidateTSVFile(
      @RequestBody BulkstrategiesValidationRequest request) throws Exception {

    ApiResponseObject<List<BulkstrategiesValidationResponse>> response =
        bulkStrategiesService.validateTSVFile(request.filePath, request.fileContent);

    LOGGER.info("bulk strategies validattion");
    return ResponseEntity.ok().body(response);

  }


  @ApiOperation("Bulk Strategy Api For Validate CSV .")
  @RequestMapping(value = ApiConstant.UPDATE, method = RequestMethod.POST,
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ApiResponseObject<BulkstrategiesUpdateResponse>> confirmUpdate(
      @RequestBody BulkstrategiesValidationRequest request) throws Exception {

    // read the tsv file and create json of all the strategies
    // iterate all strategies and update all the strategies
    ApiResponseObject<BulkstrategiesUpdateResponse> response =
        bulkStrategiesService.updateBulkStrategies(request.filePath, request.fileContent);

    LOGGER.info("bulk strategies update");
    return ResponseEntity.ok().body(response);
  }


}
