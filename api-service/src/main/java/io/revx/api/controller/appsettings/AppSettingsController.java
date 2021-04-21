package io.revx.api.controller.appsettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.appsettings.AppSettingsService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.enums.AppSettingsKey;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.AppSettingsDTO;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(value = "App Settings Controller", tags = {"AppSettings Controller"})
public class AppSettingsController {

    private static final Logger logger = LogManager.getLogger(AppSettingsController.class);

    private final AppSettingsService appSettingsService;

    public AppSettingsController(AppSettingsService appSettingsService) {
        this.appSettingsService = appSettingsService;
    }

    @LogMetrics(
            name = GraphiteConstants.CONTROLLER + GraphiteConstants.APP_SETTINGS + GraphiteConstants.CREATE)
    @ApiOperation(" Api to create App Settings")
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.APP_SETTINGS + GraphiteConstants.CREATE)
    @PostMapping(value = ApiConstant.APP_SETTINGS,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<List<AppSettingsDTO>>> createAppSettings(
            @RequestBody(required = true) List<AppSettingsDTO> appSettings) throws ValidationException, JsonProcessingException {
        logger.info("AppSettings CREATE request : {}",appSettings);
        ApiResponseObject<List<AppSettingsDTO>> response = appSettingsService.createSettings(appSettings);
        return ResponseEntity.ok().body(response);
    }

    @LogMetrics(
            name = GraphiteConstants.CONTROLLER + GraphiteConstants.APP_SETTINGS + GraphiteConstants.GET)
    @ApiOperation(" Api to get App Settings")
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.APP_SETTINGS + GraphiteConstants.GET)
    @GetMapping(value = ApiConstant.APP_SETTINGS,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<List<AppSettingsDTO>>> getAppSettings(
            @RequestParam(name = ApiConstant.SETTINGS_KEYS,required = false) List<AppSettingsKey> settingsKeys,
            @RequestParam(name = ApiConstant.ADVERTISER_ID, required = false) Long advertiserId
    ) throws Exception {
        logger.info("AppSettings GET Request with advertiserId : {} and key : {}"
                ,advertiserId,settingsKeys);
        ApiResponseObject<List<AppSettingsDTO>> body =
                appSettingsService.getSettings(settingsKeys, advertiserId);
        return ResponseEntity.ok().body(body);
    }

    @LogMetrics(
            name = GraphiteConstants.CONTROLLER + GraphiteConstants.APP_SETTINGS + GraphiteConstants.UPDATE)
    @ApiOperation(" Api to update App Settings")
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.APP_SETTINGS + GraphiteConstants.UPDATE)
    @PostMapping(value = ApiConstant.APP_SETTINGS+ApiConstant.UPDATE,consumes = MediaType.APPLICATION_JSON_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<List<AppSettingsDTO>>> updateAppSettings(
            @RequestBody(required = true) List<AppSettingsDTO> appSettings) throws Exception {
        logger.info("AppSettings UPDATE request : {}",appSettings);
        ApiResponseObject<List<AppSettingsDTO>> body =
                appSettingsService.updateSettings(appSettings);
        return ResponseEntity.ok().body(body);
    }
	
}
