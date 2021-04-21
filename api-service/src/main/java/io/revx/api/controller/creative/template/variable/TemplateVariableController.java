package io.revx.api.controller.creative.template.variable;

import io.micrometer.core.annotation.Timed;
import io.revx.api.service.creative.template.variable.TemplateVariableService;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.creative.TemplateVariablesDTO;
import io.revx.core.response.ApiResponseObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.revx.api.constants.ApiConstant.CREATIVE_TEMPLATE_VARIABLES;

@RestController
@RequestMapping(CREATIVE_TEMPLATE_VARIABLES)
@Api(value = "Creative template variables Controller", tags = {"Creative template variables Controller"})
public class TemplateVariableController {

    private static final Logger logger = LogManager.getLogger(TemplateVariableController.class);

    private final TemplateVariableService variableService;

    public TemplateVariableController(TemplateVariableService variableService) {
        this.variableService = variableService;
    }

    /**
     * GET /v2/api/creatives/templates/variables
     *
     * @return - List of template variables
     */
    @LogMetrics(name = GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.VARIABLES + GraphiteConstants.GET)
    @Timed(GraphiteConstants.CONTROLLER + GraphiteConstants.CREATIVES
            + GraphiteConstants.TEMPLATES + GraphiteConstants.VARIABLES + GraphiteConstants.GET)
    @ApiOperation("Fetch Creative template variables")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseObject<List<TemplateVariablesDTO>>> getTemplateVariables() {
        logger.info("GET variables for HTML based template creatives");
        List<TemplateVariablesDTO> variables = variableService.getTemplateVariables();
        ApiResponseObject<List<TemplateVariablesDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(variables);
        return ResponseEntity.ok(responseObject);
    }

}
