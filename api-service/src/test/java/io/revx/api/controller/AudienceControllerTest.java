package io.revx.api.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.revx.api.audience.pojo.AudienceAccessDTO;
import io.revx.api.audience.pojo.MetaRulesDto;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.audience.AudienceController;
import io.revx.api.service.audience.AudienceServiceMockImpl;
import io.revx.api.service.audience.impl.AudienceServiceImpl;
import io.revx.api.service.audience.impl.RuleServiceImpl;
import io.revx.api.service.dmp.DmpService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.DmpAudienceDTO;
import io.revx.core.model.audience.PixelRemoteConfigDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AudienceControllerTest {
    @Mock
    private AudienceServiceMockImpl audienceServiceMock;

    @Mock
    private AudienceServiceImpl audienceService;

    @Mock
    private RuleServiceImpl ruleService;

    @Mock
    private DmpService dmpService;

    @InjectMocks
    private AudienceController audienceController;

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
        mockMvc = MockMvcBuilders.standaloneSetup(audienceController).setControllerAdvice(hadler).build();
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#createAudience(AudienceDTO)}.
     */
    @Test
    public void testCreateAudience() throws Exception {
        ApiResponseObject<AudienceDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createAudienceDTO());
        Mockito.when(audienceService.createAudience(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.AUDIENCE_CREATE).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createAudienceDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<AudienceDTO>>() {}.getType();
        ApiResponseObject<AudienceDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(" Honda  Clickers R1",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#updateAudience(AudienceDTO, Long)}.
     */
    @Test
    public void testAudience() throws Exception {
        ApiResponseObject<AudienceDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createAudienceDTO());
        Mockito.when(audienceService.updateAudience(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.AUDIENCE_UPDATE.replace("{id}","375")).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createAudienceDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<AudienceDTO>>() {}.getType();
        ApiResponseObject<AudienceDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(" Honda  Clickers R1",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#syncRemoteAudience(Long)}.
     */
    @Test
    public void testSyncRemoteAudience() throws Exception {
        ApiResponseObject<BaseModel> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createBaseModel());
        Mockito.when(audienceService.syncRemoteAudience(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.AUDIENCE_SYNC_REMOTE.replace("{id}","375"))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<BaseModel>>() {}.getType();
        ApiResponseObject<BaseModel> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Honda",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#checkConnection(PixelRemoteConfigDTO)}.
     */
    @Test
    public void testCheckConnection() throws Exception {
        PixelRemoteConfigDTO pixelRemoteConfigDTO = new PixelRemoteConfigDTO();
        pixelRemoteConfigDTO.setPassword("password");
        ApiResponseObject<BaseModel> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createBaseModel());
        Mockito.when(audienceServiceMock.checkConnection(Mockito.any())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.CHECK_CONNECTION).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(pixelRemoteConfigDTO));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<BaseModel>>() {}.getType();
        ApiResponseObject<BaseModel> apiResp = new Gson().fromJson(contentString, respType);
        assertEquals("Honda",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#activateAudience(String)}.
     */
    @Test
    public void testActivateAudience() throws Exception {
        ResponseMessage responseMessage = new ResponseMessage(23,"key");
        Map<Integer,ResponseMessage> map = new HashMap<>();
        map.put(33,responseMessage);
        ApiResponseObject<Map<Integer, ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(map);
        Mockito.when(audienceService.activate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.AUDIENCE_ACTIVATE).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson("33")).param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Integer, ResponseMessage>>>() {}.getType();
        ApiResponseObject<Map<Integer, ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertEquals("key",apiResp.getRespObject().get(33).getMessage());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#deactivateAudience(String)}.
     */
    @Test
    public void testDeactivateAudience() throws Exception {
        ResponseMessage responseMessage = new ResponseMessage(23,"key");
        Map<Integer,ResponseMessage> map = new HashMap<>();
        map.put(33,responseMessage);
        ApiResponseObject<Map<Integer, ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(map);
        Mockito.when(audienceService.deactivate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.AUDIENCE_DEACTIVATE).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson("33")).param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Integer, ResponseMessage>>>() {}.getType();
        ApiResponseObject<Map<Integer, ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertEquals("key",apiResp.getRespObject().get(33).getMessage());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#getAllDmpAudience(Long, Integer, Integer, Integer)}.
     */
    @Test
    public void testGetAllDmpAudience() throws Exception {
        ApiResponseObject<DmpAudienceDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createDmpAudienceDTO());
        Mockito.when(dmpService.getDmpAudience(Mockito.anyLong(),Mockito.anyInt()
                ,Mockito.any(),Mockito.anyInt())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.DMP_AUDIENCE).accept(MediaType.APPLICATION_JSON)
                .param(ApiConstant.advertiserId,"33").param(ApiConstant.START,"1")
                .param(ApiConstant.LIMIT,"12").param(ApiConstant.DMP_SEGMENT_TYPE,"76");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<DmpAudienceDTO>>() {}.getType();
        ApiResponseObject<DmpAudienceDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(2021,apiResp.getRespObject().getLimit().longValue());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#getSyncedDmpAudience(Long)}.
     */
    @Test
    public void testGetSyncedDmpAudience() throws Exception {
        ApiResponseObject<List<AudienceDTO>> apiResponseObject = new ApiResponseObject<>();
        List<AudienceDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.createAudienceDTO());
        apiResponseObject.setRespObject(list);
        Mockito.when(audienceService.getSyncedDmpAudience(Mockito.anyLong())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.DMP_SYNCED_AUDIENCE).accept(MediaType.APPLICATION_JSON)
                .param(ApiConstant.advertiserId,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<List<AudienceDTO>>>() {}.getType();
        ApiResponseObject<List<AudienceDTO>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(" Honda  Clickers R1",apiResp.getRespObject().get(0).getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#getById(Long, boolean)}.
     */
    @Test
    public void testGetById() throws Exception {
        ApiResponseObject<AudienceDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createAudienceDTO());
        Mockito.when(audienceService.getAudience(Mockito.anyLong(),Mockito.anyBoolean())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.AUDIENCE_GET.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON).param(ApiConstant.advertiserId,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<AudienceDTO>>() {}.getType();
        ApiResponseObject<AudienceDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(" Honda  Clickers R1",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.audience.AudienceController#getAllAudience(Long, int, int, String, boolean, SearchRequest)}.
     */
    @Test
    public void testGetAllAudience() throws Exception {
        List<AudienceESDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.createAudienceESDTO());
        List<List<AudienceESDTO>> listList = new ArrayList<>();
        listList.add(list);
        ApiListResponse<List<AudienceESDTO>> lists = new ApiListResponse<>();
        lists.setData(listList);
        SearchRequest searchRequest = new SearchRequest();
        Mockito.when(audienceService.getAllAudience(Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt()
                ,Mockito.anyString(),Mockito.any(),Mockito.anyBoolean())).thenReturn(lists);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.AUDIENCE_GET_ALL).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(searchRequest)).param(ApiConstant.advertiserId,"33")
                .param(ApiConstant.PAGENUMBER,"1").param(ApiConstant.PAGESIZE,"10")
                .param(ApiConstant.SORT,"id").param(ApiConstant.REFRESH,"true");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<List<AudienceESDTO>>>>() {}.getType();
        ApiResponseObject<ApiListResponse<List<AudienceESDTO>>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Honda",apiResp.getRespObject().getData().get(0).get(0).getName());
    }

    /**
     * Test method for
     * {@link AudienceController#getMetaRules()}.
     */
    @Test
    public void testGetMetaRules() throws Exception {
        MetaRulesDto metaRulesDto = new MetaRulesDto();
        ApiResponseObject<MetaRulesDto> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(metaRulesDto);
        Mockito.when(ruleService.getAllRules()).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.AUDIENCE_RULES).accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<MetaRulesDto>>() {}.getType();
        ApiResponseObject<MetaRulesDto> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
    }

    /**
     * Test method for
     * {@link AudienceController#getAccess(Long)} ()}.
     */
    @Test
    public void testGetAccess() throws Exception {
        ApiResponseObject<AudienceAccessDTO> apiResponseObject = new ApiResponseObject<>();
        AudienceAccessDTO audienceAccessDTO = new AudienceAccessDTO();
        audienceAccessDTO.setIsPlatformAccess(true);
        apiResponseObject.setRespObject(audienceAccessDTO);
        Mockito.when(audienceService.getAcces(Mockito.anyLong())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.AUDIENCE_ACCESS.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<AudienceAccessDTO>>() {}.getType();
        ApiResponseObject<AudienceAccessDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(true,apiResponseObject.getRespObject().getIsPlatformAccess());
    }
}
