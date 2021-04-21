package io.revx.api.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.clickdestination.ClickDestinationController;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.clickdestination.ClickDestinationService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.ClickDestinationAutomationUrls;
import io.revx.core.model.requests.SearchRequest;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ClickDestinationControllerTest {
    @Mock
    private ClickDestinationService clickDestinationService;

    @Mock
    private LoginUserDetailsService loginUserDetailsService;

    @InjectMocks
    private ClickDestinationController clickDestinationController;

    private MockMvc mockMvc;

    /**
     * @throws java.lang.Exception
     */
    static {
        System.setProperty("jasypt.encryptor.password", "mySecretKey@123");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler hadler = new CommonExceptionHandler();
        hadler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc = MockMvcBuilders.standaloneSetup(clickDestinationController).setControllerAdvice(hadler).build();
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.clickdestination.ClickDestinationController#createClickDestination(ClickDestination)}.
     */
    @Test
    public void testCreateClickDestination() throws Exception {
        ApiResponseObject<ClickDestination> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createClickDestination());
        Mockito.when(clickDestinationService.create(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.clickDestinations).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createClickDestination()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ClickDestination>>() {
        }.getType();
        ApiResponseObject<ClickDestination> apiResp =
                new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Jntuworld 468x60", apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.clickdestination.ClickDestinationController#updateClickDestination(ClickDestination, Long)}.
     */
    @Test
    public void testUpdateClickDestination() throws Exception {
        ApiResponseObject<ClickDestination> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createClickDestination());
        Mockito.when(clickDestinationService.update(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.clickDestinations + ApiConstant.ID_PATH.replace("{id}", "6")).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createClickDestination()))
                .param("id", "6");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ClickDestination>>() {
        }.getType();
        ApiResponseObject<ClickDestination> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Jntuworld 468x60", apiResp.getRespObject().getName());
    }

    @Test
    public void testUpdateClickDestinationByInvalidID() throws Exception {
        ApiResponseObject<ClickDestination> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createClickDestination());
        Mockito.when(clickDestinationService.update(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.clickDestinations + ApiConstant.ID_PATH.replace("{id}", "60")).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createClickDestination()))
                .param("id", "66");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ClickDestination>>() {
        }.getType();
        ApiResponseObject<ClickDestination> apiResp = new Gson().fromJson(contentString, respType);
        assertNull(apiResp.getRespObject());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.clickdestination.ClickDestinationController#getClickDestinationById(Long)}.
     */
    @Test
    public void testGetClickDestinationById() throws Exception {
        ApiResponseObject<ClickDestination> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createClickDestination());
        Mockito.when(clickDestinationService.getById(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.clickDestinations + ApiConstant.ID_PATH.replace("{id}", "3832")).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").param(ApiConstant.ID, "33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ClickDestination>>() {
        }.getType();
        ApiResponseObject<ClickDestination> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Jntuworld 468x60", apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.clickdestination.ClickDestinationController#getAllClickDestination(SearchRequest, Integer, Integer, String, boolean, Long)}
     */
    @Test
    public void testGetAllClickDestination() throws Exception {
        List<ClickDestination> list = new ArrayList<>();
        list.add(MockDataGenerator.createClickDestination());
        ApiListResponse<ClickDestination> apiListResponse = new ApiListResponse<>();
        apiListResponse.setData(list);
        SearchRequest searchRequest = new SearchRequest();
        Mockito.when(clickDestinationService.getAll(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString()
                , Mockito.anyBoolean(), Mockito.anyLong())).thenReturn(apiListResponse);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.clickDestinations + ApiConstant.search)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(searchRequest))
                .param(ApiConstant.PAGENUMBER, "1").param(ApiConstant.PAGESIZE, "10")
                .param(ApiConstant.SORT, "id")
                .param(ApiConstant.advertiserId, "3832");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<ClickDestination>>>() {
        }.getType();
        ApiResponseObject<ApiListResponse<ClickDestination>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Jntuworld 468x60", apiResp.getRespObject().getData().get(0).getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.clickdestination.ClickDestinationController#getMmpParameters(Long)}
     */
    @Test
    public void testGetMmpParameters() throws Exception{
        ClickDestinationAutomationUrls urls = new ClickDestinationAutomationUrls();
        urls.setAndroidClickUrl("http://komli.com");
        urls.setAndroidS2sUrl("http://komli.com");
        urls.setIosClickUrl("http://komli.com");
        urls.setMmpId(6L);
        urls.setFallBackUrlStatic("http://komli.com");
        urls.setIosS2sUrl("http://komli.com");
        ApiResponseObject<ClickDestinationAutomationUrls> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(urls);
        Mockito.when(clickDestinationService.getMmpParameters(Mockito.anyLong())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.clickDestinations + ApiConstant.mmpParameters+ ApiConstant.ID_PATH.replace("{id}", "3832"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ClickDestinationAutomationUrls>>() {
        }.getType();
        ApiResponseObject<ClickDestinationAutomationUrls> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("http://komli.com", apiResp.getRespObject().getAndroidClickUrl());
    }
}
