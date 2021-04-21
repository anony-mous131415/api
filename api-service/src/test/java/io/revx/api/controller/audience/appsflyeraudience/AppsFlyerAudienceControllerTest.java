package io.revx.api.controller.audience.appsflyeraudience;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.revx.api.audience.pojo.AppsFlyerAudienceCreateDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceCreateResponseDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceSyncDto;
import io.revx.api.audience.pojo.AppsFlyerAudienceTokenDto;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.audience.impl.AppsFlyerAudienceServiceImpl;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.exception.ValidationException;
import junit.framework.TestCase;
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
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
public class AppsFlyerAudienceControllerTest extends TestCase {

    @Mock
    AppsFlyerAudienceServiceImpl appsFlyerAudienceService;

    @InjectMocks
    AppsFlyerAudienceController appsFlyerAudienceController;

    private MockMvc mockMvc;

    static {
        System.setProperty("jasypt.encryptor.password", "mySecretKey@123");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler hadler = new CommonExceptionHandler();
        hadler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc =
                MockMvcBuilders.standaloneSetup(appsFlyerAudienceController).setControllerAdvice(hadler).build();
    }

    @Test
    public void testCreateAppsFlyerAudience() throws Exception {
        AppsFlyerAudienceCreateDto appsFlyerAudienceCreateDto = TestDataGenerator.getObject(AppsFlyerAudienceCreateDto.class);
        Mockito.when(appsFlyerAudienceService.createAppsFlyerAudience(Mockito.any(AppsFlyerAudienceCreateDto.class))).thenReturn(3432L);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.APPSFLYER_AUDIENCE_CREATE)
                            .accept(MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(new Gson().toJson(appsFlyerAudienceCreateDto));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        assertEquals(200, result.getResponse().getStatus());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<AppsFlyerAudienceCreateResponseDto>() {}.getType();
        AppsFlyerAudienceCreateResponseDto apiResp =
                new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertThat(apiResp.getContainer_id()).isEqualTo(3432);
    }

    @Test
    public void testCreateAppsFlyerAudienceValidationFailed() throws Exception {
        AppsFlyerAudienceCreateDto appsFlyerAudienceCreateDto = TestDataGenerator.getObject(AppsFlyerAudienceCreateDto.class);
        Mockito.when(appsFlyerAudienceService.createAppsFlyerAudience(Mockito.any(AppsFlyerAudienceCreateDto.class))).thenThrow(new ValidationException(""));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.APPSFLYER_AUDIENCE_CREATE)
                .accept(MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(new Gson().toJson(appsFlyerAudienceCreateDto));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        assertEquals(401, result.getResponse().getStatus());
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    public void testSyncAppsFlyerAudience() throws Exception {
        AppsFlyerAudienceSyncDto appsFlyerAudienceSyncDto = TestDataGenerator.getObject(AppsFlyerAudienceSyncDto.class);
             Mockito.doNothing().when(appsFlyerAudienceService).syncAppsFlyerAudience(Mockito.any(AppsFlyerAudienceSyncDto.class));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.APPSFLYER_AUDIENCE_SYNC )
                .accept(MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(new Gson().toJson(appsFlyerAudienceSyncDto));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("", result.getResponse().getContentAsString());
    }

    @Test
    public void testSyncAppsFlyerAudienceValidationFailed() throws Exception {
        AppsFlyerAudienceSyncDto appsFlyerAudienceSyncDto = TestDataGenerator.getObject(AppsFlyerAudienceSyncDto.class);
        Mockito.doNothing().when(appsFlyerAudienceService).syncAppsFlyerAudience(Mockito.any(AppsFlyerAudienceSyncDto.class));
        Mockito.doThrow(ValidationException.class).when(appsFlyerAudienceService).syncAppsFlyerAudience(Mockito.any(AppsFlyerAudienceSyncDto.class));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.APPSFLYER_AUDIENCE_SYNC )
                .accept(MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(new Gson().toJson(appsFlyerAudienceSyncDto));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        assertEquals(500, result.getResponse().getStatus());
    }

    @Test
    public void testValidateToken() throws Exception {
        AppsFlyerAudienceTokenDto appsFlyerAudienceTokenDto = new AppsFlyerAudienceTokenDto();
        appsFlyerAudienceTokenDto.setApi_key("GhG87787SAJKbkkkk_y4w9Ssfjh");
        Mockito.when(appsFlyerAudienceService.validateAuthToken(Mockito.anyString())).thenReturn(true);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.VALIDATE_AUTH)
                .accept(MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(new Gson().toJson(appsFlyerAudienceTokenDto));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        assertEquals(200, result.getResponse().getStatus());
        String contentString = result.getResponse().getContentAsString();
        assertEquals("", contentString);
    }

    @Test
    public void testValidateTokenFailed() throws Exception {
        AppsFlyerAudienceTokenDto appsFlyerAudienceTokenDto = new AppsFlyerAudienceTokenDto();
        appsFlyerAudienceTokenDto.setApi_key("GhG87787SAJKbkkkk_y4w9Ssfjh");
        Mockito.when(appsFlyerAudienceService.validateAuthToken(Mockito.anyString())).thenReturn(false);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.VALIDATE_AUTH)
                .accept(MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON).content(new Gson().toJson(appsFlyerAudienceTokenDto));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        assertEquals(401, result.getResponse().getStatus());
        String contentString = result.getResponse().getContentAsString();
        assertEquals("", contentString);
    }

}