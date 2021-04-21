package io.revx.api.service.creative.template.variable;

import io.revx.api.mysql.entity.creative.CreativeTemplateVariablesEntity;
import io.revx.api.mysql.repo.creative.CreativeTemplateVariablesRepo;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.model.creative.TemplateVariablesDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateVariableService {

    private static final Logger logger = LogManager.getLogger(TemplateVariableService.class);

    private final CreativeUtil creativeUtil;
    private final CreativeTemplateVariablesRepo templateVariablesRepo;

    public TemplateVariableService(CreativeUtil creativeUtil, CreativeTemplateVariablesRepo variablesRepo) {
        this.creativeUtil = creativeUtil;
        this.templateVariablesRepo = variablesRepo;
    }

    /**
     * All the template variables are returned and the logic which to pick based on the
     * selected templates is carried out at UI end.
     *
     * @return - List of template variables
     */
    public List<TemplateVariablesDTO> getTemplateVariables() {
        List<CreativeTemplateVariablesEntity> entities = templateVariablesRepo.findByActiveTrue();
        List<TemplateVariablesDTO> variablesDTOS = creativeUtil.populateTemplateVariables(entities);
        logger.debug("List of template variables is : {}", variablesDTOS);
        return variablesDTOS;
    }
}
