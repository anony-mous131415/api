package io.revx.api.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.WhitelabelingEntity;
import io.revx.api.service.WhiteLablingService;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "CSSTheme Controller ", tags = {"CSSTheme Controller "},
    description = "REST API's for Theme Selection Based on SubDomain/LicenseeId")
public class WhiteLablingController {

  private static Logger logger = LogManager.getLogger(WhiteLablingController.class);

  @Autowired
  WhiteLablingService whiteLablingService;

  @ApiOperation("CSSTheme By SubDomain")
  @GetMapping(ApiConstant.THEME_BY_SUBDOMAIN)
  public ResponseEntity<ApiResponseObject<WhitelabelingEntity>> themeBySubDomain(
      @PathVariable String subdomain) {
    logger.debug("whiteLablingService " + whiteLablingService);
    return ResponseEntity.ok().body(whiteLablingService.findBySubDomain(subdomain));
  }

  @ApiOperation("CSSTheme By LicenseeId")
  @GetMapping(ApiConstant.THEME_BY_LICENSEE_ID)
  public ResponseEntity<ApiResponseObject<WhitelabelingEntity>> themeByLicenseeId(
      @PathVariable int licenseeId) {
    logger.debug("whiteLablingService " + whiteLablingService);
    return ResponseEntity.ok().body(whiteLablingService.findByLicenseeId(licenseeId));
  }

}
