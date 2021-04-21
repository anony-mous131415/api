package io.revx.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.UiLoggingService;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "UI Logger Controller ", tags = {"UI Logger Controller"},
    description = "REST API's for Logging UI Log")
public class UiLoggingController {

  private static Logger logger = LogManager.getLogger(UiLoggingController.class);

  @Autowired
  UiLoggingService uiLoggingService;

  @ApiOperation("Logging msg in Server")
  @PostMapping(ApiConstant.LOGGING_API)
  public ResponseEntity<ApiResponseObject<Boolean>> log(
      @RequestParam(name = "logLevel", required = false) String logLevel,
      @RequestBody String logMsg) {
    logger.debug("uiLoggingService " + uiLoggingService);
    uiLoggingService.log(logMsg, logLevel);
    ApiResponseObject<Boolean> resp = new ApiResponseObject<Boolean>();
    resp.setRespObject(true);
    return ResponseEntity.ok().body(resp);
  }
}
