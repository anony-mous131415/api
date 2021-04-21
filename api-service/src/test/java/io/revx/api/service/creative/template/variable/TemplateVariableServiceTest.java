package io.revx.api.service.creative.template.variable;

import io.revx.api.common.MockDataGenerator;
import io.revx.api.mysql.repo.creative.CreativeTemplateVariablesRepo;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.model.creative.TemplateVariablesDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TemplateVariableServiceTest {

    @InjectMocks
    private TemplateVariableService variableService;

    @Mock
    private CreativeUtil creativeUtil;

    @Mock
    private CreativeTemplateVariablesRepo templateVariablesRepo;

    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        creativeUtil = new CreativeUtil();
        variableService = new TemplateVariableService(creativeUtil, templateVariablesRepo);
    }

    @Test
    public void testGetVariables() {
        Mockito.when(templateVariablesRepo.findByActiveTrue())
                .thenReturn(MockDataGenerator.generateTemplateVariablesList());
        List<TemplateVariablesDTO> responseObject = variableService.getTemplateVariables();
        assertNotNull(responseObject);
        assertEquals(2,responseObject.size());
    }
}
