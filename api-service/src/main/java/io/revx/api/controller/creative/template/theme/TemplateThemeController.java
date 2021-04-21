package io.revx.api.controller.creative.template.theme;

import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.creative.template.theme.TemplateThemeService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.TemplateThemeDTO;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.revx.api.constants.ApiConstant.CREATIVE_TEMPLATE_THEMES;

@RestController
@RequestMapping(CREATIVE_TEMPLATE_THEMES)
@Api(value = "Creative template themes Controller", tags = {"Creative template themes Controller"})
public class TemplateThemeController {

    private static final Logger logger = LogManager.getLogger(TemplateThemeController.class);

    private final TemplateThemeService themesService;

    public TemplateThemeController(TemplateThemeService service) {
        this.themesService = service;
    }

    /**
     * GET /v2/api/creatives/templates?advertiserId=
     *
     * @param advertiserId - advertiser id for the themes needs to be fetched
     * @return - List of corresponding Template themes
     * @throws ValidationException - validation advertiser id
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.GET)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.GET)
    @ApiOperation("Fetch Creative template themes for an advertiser")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<List<TemplateThemeDTO>>> getTemplateThemes(
            @RequestParam(name = "advertiserId") Long advertiserId) throws ValidationException {
        logger.info("GET to fetch creative template themes for an advertiser with id : {} ", advertiserId);
        ApiResponseObject<List<TemplateThemeDTO>> themes = themesService.getThemesForAdvertiser(advertiserId);
        return ResponseEntity.ok(themes);
    }

    /**
     * GET /v2/api/creatives/templates/{id}
     *
     * @param id - Template theme data corresponding to that Id
     * @return - Template theme if present
     * @throws ValidationException throws if id is not valid
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.GET)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.GET)
    @ApiOperation("Fetch Creative template based on id")
    @GetMapping(value = ApiConstant.ID_PATH,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<TemplateThemeDTO>> getTemplateThemeById(
             @PathVariable Long id) throws ValidationException{
        logger.info("GET to fetch creative template themes with id : {}", id);
        ApiResponseObject<TemplateThemeDTO> theme = themesService.getThemeById(id);
        return ResponseEntity.ok(theme);
    }


    /**
     * POST /v2/api/creatives/templates
     *
     * @param themeDTO - Incoming payload for creating a new theme
     * @return - Theme DTO with details
     * @throws ValidationException thrown if payload is not valid
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.CREATE)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.CREATE)
    @ApiOperation("Create Creative template theme")
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<TemplateThemeDTO>> createTemplateTheme(
            @RequestBody TemplateThemeDTO themeDTO) throws ValidationException {
        logger.info("POST to create creative template themes : {} ",themeDTO);
        ApiResponseObject<TemplateThemeDTO> theme = themesService.createTemplateTheme(themeDTO);
        return ResponseEntity.ok(theme);
    }

    /**
     * POST /v2/api/creatives/templates/{id}
     *
     * @param themeDTO - Incoming payload for updating theme
     * @param id - Id for which the data needs to be updated
     * @return - Theme DTO object with the updated values
     * @throws ValidationException - thrown when payload or id is not valid
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.UPDATE)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.THEMES + GraphiteConstants.UPDATE)
    @ApiOperation("Update Creative template theme")
    @PostMapping(value = ApiConstant.ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<TemplateThemeDTO>> updateTemplateTheme(
            @RequestBody TemplateThemeDTO themeDTO, @PathVariable(name = "id") Long id) throws ValidationException {
        logger.info("POST to update creative template themes with id : {} and payload : {} ",id, themeDTO);
        if (!themeDTO.getId().equals(id)) {
            throw new ValidationException("Template theme is missing");
        }
        ApiResponseObject<TemplateThemeDTO> theme = themesService.updateTemplateTheme(themeDTO);
        return ResponseEntity.ok(theme);
    }
}
