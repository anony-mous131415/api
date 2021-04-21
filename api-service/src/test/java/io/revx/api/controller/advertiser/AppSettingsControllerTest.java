package io.revx.api.controller.advertiser;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.appsettings.AppSettingsController;
import io.revx.api.service.appsettings.AppSettingsService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.enums.AppSettingsKey;
import io.revx.core.enums.AppSettingsType;
import io.revx.core.model.AppSettingsDTO;
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
public class AppSettingsControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private AppSettingsController appSettingsController;

    @Mock
    private AppSettingsService appSettingsService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        appSettingsController = new AppSettingsController(appSettingsService);
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc =
                MockMvcBuilders.standaloneSetup(appSettingsController).setControllerAdvice(handler).build();
    }

    @Test
    public void testGetAppSettings() throws Exception {
        AppSettingsDTO settingsDTO = new AppSettingsDTO();
        settingsDTO.setActive(true);
        settingsDTO.setSettingsKey(AppSettingsKey.LOGO_LINK);
        settingsDTO.setSettingsType(AppSettingsType.STRING);
        settingsDTO.setId(1234L);
        settingsDTO.setAdvertiserId(134L);
        settingsDTO.setLicenseeId(12L);
        settingsDTO.setSettingsValue("http://image.png");
        settingsDTO.setAppSettingsProperties(new ArrayList<>());
        List<AppSettingsDTO> appSettingsDTOS = new ArrayList<>();
        appSettingsDTOS.add(settingsDTO);
        ApiResponseObject<List<AppSettingsDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(appSettingsDTOS);

        Mockito.when(appSettingsService.getSettings(Mockito.any(), Mockito.anyLong()))
                .thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.APP_SETTINGS)
                .header("token","ydagfyiecbweicwibc")
                .param("settingsKey","LOGO_LINK")
                .param("advertiserId","123")
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<List<AppSettingsDTO>>>() {}.getType();
        ApiResponseObject<List<AppSettingsDTO>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertEquals(1,apiResp.getRespObject().size());
    }

    @Test
    public void testCreateAppSettings() throws Exception {
        AppSettingsDTO settingsDTO = new AppSettingsDTO();
        settingsDTO.setActive(true);
        settingsDTO.setSettingsKey(AppSettingsKey.LOGO_LINK);
        settingsDTO.setSettingsType(AppSettingsType.STRING);
        settingsDTO.setId(1234L);
        settingsDTO.setAdvertiserId(134L);
        settingsDTO.setLicenseeId(12L);
        settingsDTO.setSettingsValue("http://image.png");
        settingsDTO.setAppSettingsProperties(new ArrayList<>());
        List<AppSettingsDTO> appSettingsDTOS = new ArrayList<>();
        appSettingsDTOS.add(settingsDTO);
        ApiResponseObject<List<AppSettingsDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(appSettingsDTOS);

        Mockito.when(appSettingsService.createSettings(Mockito.any())).thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.APP_SETTINGS)
                .header("token","ydagfyiecbweicwibc")
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(appSettingsDTOS))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<List<AppSettingsDTO>>>() {}.getType();
        ApiResponseObject<List<AppSettingsDTO>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertEquals(1,apiResp.getRespObject().size());
    }

    @Test
    public void testUpdateAppSettings() throws Exception {
        AppSettingsDTO settingsDTO = new AppSettingsDTO();
        settingsDTO.setActive(true);
        settingsDTO.setSettingsKey(AppSettingsKey.LOGO_LINK);
        settingsDTO.setSettingsType(AppSettingsType.STRING);
        settingsDTO.setId(1234L);
        settingsDTO.setAdvertiserId(134L);
        settingsDTO.setLicenseeId(12L);
        settingsDTO.setSettingsValue("http://image.png");
        settingsDTO.setAppSettingsProperties(new ArrayList<>());
        List<AppSettingsDTO> appSettingsDTOS = new ArrayList<>();
        appSettingsDTOS.add(settingsDTO);
        ApiResponseObject<List<AppSettingsDTO>> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(appSettingsDTOS);

        Mockito.when(appSettingsService.updateSettings(Mockito.any())).thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.APP_SETTINGS+ApiConstant.UPDATE)
                .header("token","ydagfyiecbweicwibc")
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(appSettingsDTOS))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<List<AppSettingsDTO>>>() {}.getType();
        ApiResponseObject<List<AppSettingsDTO>> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertEquals(1,apiResp.getRespObject().size());
    }

}
