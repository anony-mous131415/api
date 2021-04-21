package io.revx.api.controller.creative.template.variable;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.creative.template.variable.TemplateVariableService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.enums.TemplateVariableType;
import io.revx.core.model.creative.TemplateVariablesDTO;
import io.revx.core.response.ApiResponseObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class TemplateVariableControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TemplateVariableController variableController;

    @Mock
    private TemplateVariableService variableService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        variableController = new TemplateVariableController(variableService);
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc =
                MockMvcBuilders.standaloneSetup(variableController).setControllerAdvice(handler).build();
    }

    @Test
    public void testGetVariables() throws Exception {
        List<TemplateVariablesDTO> variablesDTOList = new ArrayList<>();
        TemplateVariablesDTO variablesDTO = new TemplateVariablesDTO();
        variablesDTO.setIsActive(true);
        variablesDTO.setVariableKey("currency");
        variablesDTO.setVariableTitle("Currency");
        variablesDTO.setVariableType(TemplateVariableType.TEXT_AREA);
        variablesDTO.setElasticSearchIndex("currency");
        variablesDTOList.add(variablesDTO);

        Mockito.when(variableService.getTemplateVariables())
                .thenReturn(variablesDTOList);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.CREATIVE_TEMPLATE_VARIABLES)
                .header("token","ydagfyiecbweicwibc");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<List<TemplateVariablesDTO>>>() {}.getType();
        ApiResponseObject<List<TemplateVariablesDTO>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);
        assertEquals(1,apiResp.getRespObject().size());
        assertEquals("currency",apiResp.getRespObject().get(0).getElasticSearchIndex());
    }
}
