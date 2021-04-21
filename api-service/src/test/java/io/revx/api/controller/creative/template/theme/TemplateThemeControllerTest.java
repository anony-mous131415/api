package io.revx.api.controller.creative.template.theme;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.creative.template.theme.TemplateThemeService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.creative.TemplateThemeDTO;
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
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class TemplateThemeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private TemplateThemeController themeController;

    @Mock
    private TemplateThemeService themeService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        themeController = new TemplateThemeController(themeService);
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc =
                MockMvcBuilders.standaloneSetup(themeController).setControllerAdvice(handler).build();
    }

    @Test
    public void testGetThemesByAdvertiserId() throws Exception {
        List<TemplateThemeDTO> themeDTOS = new ArrayList<>();
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        themeDTO.setId(1234L);
        themeDTO.setAdvertiserId(134L);
        themeDTO.setThemeName("test_theme_name");
        themeDTO.setStyleJson("test_style_json");
        themeDTOS.add(themeDTO);
        ApiResponseObject<List<TemplateThemeDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTOS);

        Mockito.when(themeService.getThemesForAdvertiser(Mockito.anyLong()))
                .thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.CREATIVE_TEMPLATE_THEMES)
                .header("token","yrgfylrbriue")
                .param("advertiserId","134");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<List<TemplateThemeDTO>>>() {}.getType();
        ApiResponseObject<List<TemplateThemeDTO>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);
        assertEquals(1,apiResp.getRespObject().size());
        assertEquals(134,apiResp.getRespObject().get(0).getAdvertiserId().longValue());
    }

    @Test
    public void testGetThemesById() throws Exception {
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        themeDTO.setId(1234L);
        themeDTO.setAdvertiserId(134L);
        themeDTO.setThemeName("test_theme_name");
        themeDTO.setStyleJson("test_style_json");
        ApiResponseObject<TemplateThemeDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);

        Mockito.when(themeService.getThemeById(Mockito.anyLong()))
                .thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.CREATIVE_TEMPLATE_THEMES+"/1234")
                .header("token","yrgfylrbriue")
                .param("id","1234");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<TemplateThemeDTO>>() {}.getType();
        ApiResponseObject<TemplateThemeDTO> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);
        assertEquals(134,apiResp.getRespObject().getAdvertiserId().longValue());
    }

    @Test
    public void testCreateTheme() throws Exception {
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        themeDTO.setId(1234L);
        themeDTO.setAdvertiserId(134L);
        themeDTO.setThemeName("test_theme_name");
        themeDTO.setStyleJson("test_style_json");
        ApiResponseObject<TemplateThemeDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);

        Mockito.when(themeService.createTemplateTheme(Mockito.any()))
                .thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.CREATIVE_TEMPLATE_THEMES)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(themeDTO))
                .header("token","yrgfylrbriue");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<TemplateThemeDTO>>() {}.getType();
        ApiResponseObject<TemplateThemeDTO> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);
        assertEquals(134,apiResp.getRespObject().getAdvertiserId().longValue());
    }

    @Test
    public void testUpdateTheme() throws Exception {
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        themeDTO.setId(1234L);
        themeDTO.setAdvertiserId(134L);
        themeDTO.setThemeName("test_theme_name");
        themeDTO.setStyleJson("test_style_json");
        ApiResponseObject<TemplateThemeDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);

        Mockito.when(themeService.updateTemplateTheme(Mockito.any()))
                .thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.CREATIVE_TEMPLATE_THEMES+"/1234")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(themeDTO))
                .header("token","yrgfylrbriue");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<TemplateThemeDTO>>() {}.getType();
        ApiResponseObject<TemplateThemeDTO> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);
        assertEquals(134,apiResp.getRespObject().getAdvertiserId().longValue());
    }

    @Test
    public void testUpdateThemeWithMismatchingId() throws Exception {
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        themeDTO.setId(1234L);
        themeDTO.setAdvertiserId(134L);
        themeDTO.setThemeName("test_theme_name");
        themeDTO.setStyleJson("test_style_json");
        ApiResponseObject<TemplateThemeDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);

        Mockito.when(themeService.updateTemplateTheme(Mockito.any()))
                .thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.CREATIVE_TEMPLATE_THEMES+"/1231")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(themeDTO))
                .header("token","yrgfylrbriue");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Type respType = new TypeToken<ApiResponseObject<TemplateThemeDTO>>() {}.getType();
        ApiResponseObject<TemplateThemeDTO> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);
        assertEquals(400,result.getResponse().getStatus());
        assertNull(apiResp.getRespObject());
        assertEquals(10014,apiResp.getError().getCode());
    }

    @Test
    public void testUpdateThemeWithMissingPayload() throws Exception {
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        themeDTO.setId(1234L);
        themeDTO.setAdvertiserId(134L);
        themeDTO.setThemeName("test_theme_name");
        themeDTO.setStyleJson("test_style_json");
        ApiResponseObject<TemplateThemeDTO> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(themeDTO);

        Mockito.when(themeService.updateTemplateTheme(Mockito.any()))
                .thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.CREATIVE_TEMPLATE_THEMES+"/1231")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token","yrgfylrbriue");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Type respType = new TypeToken<ApiResponseObject<TemplateThemeDTO>>() {}.getType();
        ApiResponseObject<TemplateThemeDTO> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);
        assertEquals(400,result.getResponse().getStatus());
        assertNull(apiResp.getRespObject());
        assertEquals(10000,apiResp.getError().getCode());
    }
}
