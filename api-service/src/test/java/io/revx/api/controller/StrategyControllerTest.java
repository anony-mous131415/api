package io.revx.api.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.strategy.StrategyController;
import io.revx.api.service.strategy.StrategyService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.strategy.StrategyCreativeAssociationRequestDTO;
import io.revx.core.model.strategy.StrategyCreativeAssociationResponseDTO;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.StrategyQuickEditDTO;
import io.revx.core.model.targetting.SiteListDTO;
import io.revx.core.model.targetting.SiteListRequest;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.model.strategy.StrategyCreativeAssociationResponseDTO.CreativeStrategyAssociationStatus;
import io.revx.core.response.ResponseMessage;
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
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class StrategyControllerTest {
    @Mock
    private StrategyService strategyService;

    @InjectMocks
    private StrategyController strategyController;

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
        mockMvc = MockMvcBuilders.standaloneSetup(strategyController).setControllerAdvice(hadler).build();
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#createStrategy(StrategyDTO)}.
     */
    @Test
    public void testCreateStrategy() throws Exception {
        ApiResponseObject<StrategyDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createStrategyDTO());
        Mockito.when(strategyService.createStrategy(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createStrategyDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<StrategyDTO>>() {}.getType();
        ApiResponseObject<StrategyDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("SafariTest",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#updateStrategy(StrategyDTO, Long)}.
     */
    @Test
    public void testUpdateStrategy() throws Exception {
        ApiResponseObject<StrategyDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createStrategyDTO());
        Mockito.when(strategyService.updateStrategy(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES+ApiConstant.ID_PATH.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(MockDataGenerator.createStrategyDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<StrategyDTO>>() {}.getType();
        ApiResponseObject<StrategyDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("SafariTest",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#activateStrategy(String)}.
     */
    @Test
    public void testActivateStrategy() throws Exception {
        ApiResponseObject<Map<Long, ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
        Map<Long,ResponseMessage> map = new HashMap<>();
        ResponseMessage message = new ResponseMessage(33,"test");
        map.put(33L,message);
        apiResponseObject.setRespObject(map);
        Mockito.when(strategyService.activate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES+ApiConstant.activate).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson("33")).param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Long, ResponseMessage>>>() {}.getType();
        ApiResponseObject<Map<Long, ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("test",apiResp.getRespObject().get(33L).getMessage());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#deactivateStrategy(String)}.
     */
    @Test
    public void testDeactivateStrategy() throws Exception {
        ApiResponseObject<Map<Long, ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
        Map<Long,ResponseMessage> map = new HashMap<>();
        ResponseMessage message = new ResponseMessage(33,"test");
        map.put(33L,message);
        apiResponseObject.setRespObject(map);
        Mockito.when(strategyService.deactivate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES+ApiConstant.deactivate).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson("33")).param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Long, ResponseMessage>>>() {}.getType();
        ApiResponseObject<Map<Long, ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("test",apiResp.getRespObject().get(33L).getMessage());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#updateStrategy(StrategyDTO, Long)}.
     */
    @Test
    public void testDuplicateStrategy() throws Exception {
        ApiResponseObject<StrategyDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createStrategyDTO());
        Mockito.when(strategyService.duplicateStrategy(Mockito.anyLong(),Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES+ApiConstant.DUPLICATE_STRATEGY.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(MockDataGenerator.createDuplicateStrategyRequestDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<StrategyDTO>>() {}.getType();
        ApiResponseObject<StrategyDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("SafariTest",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#validateSites(SiteListRequest)}.
     */
    @Test
    public void testValidateSites() throws Exception {
        ApiResponseObject<SiteListDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createSiteListDTO());
        SiteListRequest request = new SiteListRequest();
        Mockito.when(strategyService.validateSiteList(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES+ApiConstant.VALIDATE_DOMAINS)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(request));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<SiteListDTO>>() {}.getType();
        ApiResponseObject<SiteListDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Honda",apiResp.getRespObject().validSites.get(0).getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#getStrategyById(Long, boolean)}.
     */
    @Test
    public void testGetStrategyById() throws Exception {
        ApiResponseObject<StrategyDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createStrategyDTO());
        Mockito.when(strategyService.get(Mockito.anyLong(),Mockito.anyBoolean())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.STRATEGIES+ApiConstant.ID_PATH.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON).param(ApiConstant.REFRESH,"true");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<StrategyDTO>>() {}.getType();
        ApiResponseObject<StrategyDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("SafariTest",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#associateCreativesWithStrategies(StrategyCreativeAssociationRequestDTO)}.
     */
    @Test
    public void testAssociateCreativesWithStrategies() throws Exception {
        ApiResponseObject<StrategyCreativeAssociationResponseDTO> apiResponseObject = new ApiResponseObject<>();
        StrategyCreativeAssociationResponseDTO responseDTO = new StrategyCreativeAssociationResponseDTO();
        apiResponseObject.setRespObject(responseDTO);
        Mockito.when(strategyService.associateCreativesToStrategies(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES+ApiConstant.ASSOCIATE_CREATIVE_STRATEGY)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(responseDTO));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<StrategyCreativeAssociationResponseDTO>>() {}.getType();
        ApiResponseObject<StrategyCreativeAssociationResponseDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#getStrategyQuickEditDetails(Long)}.
     */
    @Test
    public void testGetStrategyQuickEditDetails() throws Exception {
        ApiResponseObject<StrategyQuickEditDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createStrategyQuickEditDTO());
        Mockito.when(strategyService.getStrategyQuickEditDetails(Mockito.anyLong())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.STRATEGIES+ApiConstant.QUICK_EDIT.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<StrategyQuickEditDTO>>() {}.getType();
        ApiResponseObject<StrategyQuickEditDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("standard",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.strategy.StrategyController#saveStrategyQuickEditDetails(Long, StrategyQuickEditDTO)}.
     */
    @Test
    public void testSaveStrategyQuickEditDetails() throws Exception {
        ApiResponseObject<StrategyDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createStrategyDTO());
        SiteListRequest request = new SiteListRequest();
        Mockito.when(strategyService.updateQuickEditDetails(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.STRATEGIES+ApiConstant.QUICK_EDIT.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(MockDataGenerator.createStrategyQuickEditDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<StrategyDTO>>() {}.getType();
        ApiResponseObject<StrategyDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("standard",apiResp.getRespObject().getStrategyType());
    }
}
