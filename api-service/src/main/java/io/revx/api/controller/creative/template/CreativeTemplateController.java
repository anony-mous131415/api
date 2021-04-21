package io.revx.api.controller.creative.template;

import io.micrometer.core.annotation.Timed;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.creative.template.CreativeTemplateService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeMockUpsDTO;
import io.revx.core.model.creative.CreativeTemplateDTO;
import io.revx.core.model.creative.CreativeTemplatesMetadataDTO;
import io.revx.core.response.ApiListResponse;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.revx.api.constants.ApiConstant.CREATIVE_TEMPLATE;
import static io.revx.api.constants.ApiConstant.METADATA;
import static io.revx.api.constants.ApiConstant.PRODUCT_IMAGES;

@RestController
@RequestMapping(CREATIVE_TEMPLATE)
@Api(value = "Creative templates Controller", tags = {"Creative templates Controller"})
public class CreativeTemplateController {

    private static final Logger logger = LogManager.getLogger(CreativeTemplateController.class);

    private final CreativeTemplateService service;

    public CreativeTemplateController(CreativeTemplateService service) {
        this.service = service;
    }

    /**
     * GET /v2/api/creatives/templates?slots=
     *
     * @param slots - maximum number of slots for the templates being fetched
     * @param isDynamic - this flag is et to fetch dynamic templates
     * @param pageNumber - page number for visualization in UI
     * @param pageSize - the number of templates to show in a single page
     * @return - List of Creative Templates with given slots
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.GET)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.GET)
    @ApiOperation("Fetch HTML templates for creatives")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<ApiListResponse<CreativeTemplateDTO>>> getCreativeTemplates(
            @RequestParam(name = "slots",required = false)Integer slots,
            @RequestParam(name = "dynamic", required = false, defaultValue = "false") Boolean isDynamic,
            @RequestParam(name = "advertiserId", required = false)  Long advertiserId,
            @RequestParam(name = ApiConstant.PAGENUMBER, required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(name = ApiConstant.PAGESIZE, required = false, defaultValue = "10") Integer pageSize,
            @RequestParam(name = ApiConstant.TEMPLATE_SIZES, required = false) String templateSizes) throws ValidationException {
        logger.info("GET creative HTML templates for slots : {} ", slots);
        if (Boolean.TRUE.equals(isDynamic) && advertiserId == null ) {
            throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
                    new Object[] {"Please provide valid parameters"});
        }
        ApiListResponse<CreativeTemplateDTO> creativeTemplates =
                service.getTemplates(slots, isDynamic, pageNumber, pageSize, templateSizes, advertiserId);
        ApiResponseObject<ApiListResponse<CreativeTemplateDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(creativeTemplates);
        return ResponseEntity.ok(responseObject);
    }

    /**
     * POST v2/api/creatives/templates/productimages
     *
     * @param mockupDTO - Incoming payload with product images details
     * @return - Creative files with permanent preview locations
     * @throws ValidationException - thrown if the incoming payload is not valid
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.PRODUCT_IMAGES + GraphiteConstants.CREATE)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.PRODUCT_IMAGES + GraphiteConstants.CREATE)
    @ApiOperation("Save product images")
    @PostMapping(value = PRODUCT_IMAGES,consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<List<CreativeFiles>>> saveProductImages(
            @RequestBody CreativeMockUpsDTO mockupDTO) throws ValidationException {
        logger.info("POST Saving product images for template in permanent location");
        ApiResponseObject<List<CreativeFiles>> responseObject = service.saveProductImages(mockupDTO);
        return ResponseEntity.ok(responseObject);
    }

    /**
     * GET /v2/api/creatives/templates/metadata
     *
     * @return Dynamic templates list of slots and sizes
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.METADATA + GraphiteConstants.GET)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.METADATA + GraphiteConstants.GET)
    @ApiOperation("Fetch creative templates metadata")
    @GetMapping(value = METADATA, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<CreativeTemplatesMetadataDTO>> getTemplatesMetadata(
            @RequestParam(name = "dynamic", required = false, defaultValue = "false") Boolean isDynamic,
            @RequestParam(name = "active", required = false, defaultValue = "true") Boolean isActive){
        logger.info("GET creative HTML templates metadata");
        ApiResponseObject<CreativeTemplatesMetadataDTO> responseObject =
                service.getTemplatesMetadata(isDynamic, isActive);
        return ResponseEntity.ok(responseObject);
    }
}
