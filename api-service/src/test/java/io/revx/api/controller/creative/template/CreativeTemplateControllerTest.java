package io.revx.api.controller.creative.template;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.creative.template.variable.TemplateVariableController;
import io.revx.api.service.creative.template.CreativeTemplateService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeHtmlMockupDTO;
import io.revx.core.model.creative.CreativeTemplateDTO;
import io.revx.core.model.creative.TemplateVariablesDTO;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
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
public class CreativeTemplateControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CreativeTemplateController templateController;

    @Mock
    private CreativeTemplateService templateService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        templateController = new CreativeTemplateController(templateService);
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc =
                MockMvcBuilders.standaloneSetup(templateController).setControllerAdvice(handler).build();
    }

    @Test
    public void testHtmlTemplatesWithSlots() throws Exception {
        List<CreativeTemplateDTO> creativeTemplateDTOS = new ArrayList<>();
        CreativeTemplateDTO templateDTO = new CreativeTemplateDTO();
        templateDTO.setTemplateId(1234L);
        creativeTemplateDTOS.add(templateDTO);
        ApiListResponse<CreativeTemplateDTO> responseObject = new ApiListResponse<>();
        responseObject.setData(creativeTemplateDTOS);

        Mockito.when(templateService.getTemplates(Mockito.anyInt(), Mockito.anyBoolean(),
                Mockito.anyInt(),Mockito.anyInt(), Mockito.anyString(), Mockito.anyLong())).thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.CREATIVE_TEMPLATE)
                .accept(MediaType.APPLICATION_JSON).header("token","ydagfyiecbweicwibc")
                .param("slots","4").param("dynamic","false")
                .param("advertiserId","6745").param("pageNumber","1")
                .param("pageSize","10").param("templateSizes","false");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<CreativeTemplateDTO>>>() {}.getType();
        ApiResponseObject<ApiListResponse<CreativeTemplateDTO>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertEquals(1,apiResp.getRespObject().getData().size());
    }

    @Test
    public void testHtmlTemplatesWithoutSlots() throws Exception {
        List<CreativeTemplateDTO> creativeTemplateDTOS = new ArrayList<>();
        CreativeTemplateDTO templateDTO = new CreativeTemplateDTO();
        templateDTO.setTemplateId(1234L);
        creativeTemplateDTOS.add(templateDTO);
        ApiListResponse<CreativeTemplateDTO> responseObject = new ApiListResponse<>();
        responseObject.setData(creativeTemplateDTOS);

        Mockito.when(templateService.getTemplates(Mockito.anyInt(), Mockito.anyBoolean(),
                Mockito.anyInt(),Mockito.anyInt(), Mockito.anyString(), Mockito.anyLong())).thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.CREATIVE_TEMPLATE)
                .accept(MediaType.APPLICATION_JSON).header("token","ydagfyiecbweicwibc")
                .param("slots","4").param("dynamic","false")
                .param("advertiserId","6745").param("pageNumber","1")
                .param("pageSize","10").param("templateSizes","false");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<CreativeTemplateDTO>>>() {}.getType();
        ApiResponseObject<ApiListResponse<CreativeTemplateDTO>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertEquals(1,apiResp.getRespObject().getData().size());
    }

    @Test
    public void testCopyProductImages() throws Exception {
        List<CreativeFiles> creativeFiles = new ArrayList<>();
        CreativeFiles creativeFile = new CreativeFiles();
        creativeFile.setName("test_images");
        creativeFiles.add(creativeFile);
        ApiResponseObject<List<CreativeFiles>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(creativeFiles);

        Mockito.when(templateService.saveProductImages(Mockito.any())).thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.CREATIVE_TEMPLATE+ApiConstant.PRODUCT_IMAGES)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new CreativeHtmlMockupDTO()))
                .header("token","ydagfyiecbweicwibc");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<List<CreativeFiles>>>() {}.getType();
        ApiResponseObject<List<CreativeFiles>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertEquals(1,apiResp.getRespObject().size());
    }

}
