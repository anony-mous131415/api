package io.revx.api.controller.audience.appsflyeraudience;

import io.revx.api.audience.pojo.AppsFlyerAudienceCreateDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceCreateResponseDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceSyncDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceTokenDto;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.audience.impl.AppsFlyerAudienceServiceImpl;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ValidationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static io.revx.core.exception.ErrorCode.INVALID_ACCESS_TOKEN;
import static io.revx.core.exception.ErrorCode.INVALID_PARAMETER_IN_REQUEST;

@RestController
@Api(value = "AppsFlyer Audience Controller", tags = {"AppsFlyer Audience Controller"},
        description = "Rest API's for AppsFlyer Audience creation,Sync etc.")
public class AppsFlyerAudienceController {

    @Autowired
    AppsFlyerAudienceServiceImpl appsFlyerAudienceService;

    private static final Logger logger = LogManager.getLogger(AppsFlyerAudienceController.class);

    // controller to create appsFlyer audience
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.APPSFLYERAUDIENCE)
    @ApiOperation("Create Api For AppsFlyer Audience.")
    @PostMapping(value = ApiConstant.APPSFLYER_AUDIENCE_CREATE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAppsFlyerAudience(
            @RequestBody(required = true) AppsFlyerAudienceCreateDto appsFlyerAudienceCreateDto) throws Exception {
        try {
            Long containerId = appsFlyerAudienceService.createAppsFlyerAudience(appsFlyerAudienceCreateDto);
            AppsFlyerAudienceCreateResponseDto appsFlyerAudienceCreateResponseDto = new AppsFlyerAudienceCreateResponseDto();
            appsFlyerAudienceCreateResponseDto.setContainer_id(containerId);
            return ResponseEntity.ok().body(appsFlyerAudienceCreateResponseDto);
        }
        catch(ValidationException ex){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    // controller to sync appsFlyer audience
    @LogMetrics(
            name = GraphiteConstants.CONTROLLER + GraphiteConstants.APPSFLYERAUDIENCE + GraphiteConstants.GETBYID)
    @ApiOperation("Api to syncAppFlyerAudience by Id")
    @PostMapping(path = ApiConstant.APPSFLYER_AUDIENCE_SYNC, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> syncAppsFlyerAudience(
            @RequestBody AppsFlyerAudienceSyncDto appsFlyerAudienceSyncDto) throws Exception {
        try {
            appsFlyerAudienceService.syncAppsFlyerAudience(appsFlyerAudienceSyncDto);
        }
        catch(ValidationException ex){
            if (ex.getErrorCode().equals(INVALID_ACCESS_TOKEN)) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            if (ex.getErrorCode().equals(INVALID_PARAMETER_IN_REQUEST)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // controller to validate life time auth token
    @LogMetrics(name = GraphiteConstants.CONTROLLER_REQUEST + GraphiteConstants.TOKEN_VALIDATE)
    @ApiOperation("Validating LifeTime Auth Token.")
    @PostMapping(value = ApiConstant.VALIDATE_AUTH, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateToken(
            @RequestBody() AppsFlyerAudienceTokenDto appsFlyerAudienceTokenDto) throws Exception {
        logger.debug("appsFlyerAudienceService : {} " , appsFlyerAudienceService);
        boolean valid = appsFlyerAudienceService.validateAuthToken(appsFlyerAudienceTokenDto.getApi_key());
        if(!valid)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
