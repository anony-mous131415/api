package io.revx.api.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.campaign.CampaignController;
import io.revx.api.service.campaign.CampaignService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.campaign.CampaignDTO;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class CampaignControllerTest {
    @Mock
    private CampaignService campaignService;

    @InjectMocks
    private CampaignController campaignController;

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
        mockMvc = MockMvcBuilders.standaloneSetup(campaignController).setControllerAdvice(hadler).build();
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.campaign.CampaignController#createCampaign(CampaignDTO)}.
     */
    @Test
    public void testCreateCampaign() throws Exception {
        String url = ApiConstant.CAMPAIGNS;
        ApiResponseObject<CampaignDTO> response = new ApiResponseObject();
        response.setRespObject(MockDataGenerator.createCampaignDTO());
        Mockito.when(campaignService.create(Mockito.any())).thenReturn(response);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createCampaignDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<CampaignDTO>>() {
        }.getType();
        ApiResponseObject<CampaignDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertThat(apiResp.getRespObject().getName()).isEqualTo("SafariTestIO");
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.campaign.CampaignController#updateCampaign(CampaignDTO, Integer)}.
     */
    @Test
    public void testUpdateCampaign() throws Exception{
        String url = ApiConstant.CAMPAIGNS + ApiConstant.ID_PATH.replace("{id}","6429");
        ApiResponseObject<CampaignDTO> response = new ApiResponseObject();
        response.setRespObject(MockDataGenerator.createCampaignDTO());
        Mockito.when(campaignService.update(Mockito.any())).thenReturn(response);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createCampaignDTO()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<CampaignDTO>>() {
        }.getType();
        ApiResponseObject<CampaignDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertThat(apiResp.getRespObject().getName()).isEqualTo("SafariTestIO");
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.campaign.CampaignController#getCampaignById(Long, boolean)}.
     */
    @Test
    public void testGetCampaignById() throws Exception {
        String url = ApiConstant.CAMPAIGNS + ApiConstant.ID_PATH.replace("{id}","6429");
        ApiResponseObject<CampaignDTO> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createCampaignDTO());
        Mockito.when(campaignService.getbyId(Mockito.anyLong(),Mockito.anyBoolean())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<CampaignDTO>>() {
        }.getType();
        ApiResponseObject<CampaignDTO> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertThat(apiResp.getRespObject().getName()).isEqualTo("SafariTestIO");
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.campaign.CampaignController#getAllCampaign(Long, int, int, String, String, boolean)} )}.
     */
    @Test
    public void testGetAllCampaign() throws Exception {
        String url = ApiConstant.CAMPAIGNS;
        List<CampaignDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.createCampaignDTO());
        ApiResponseObject<List<CampaignDTO>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(list);
        Mockito.when(campaignService.getbyAdvertiserId(Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt(),Mockito.anyString()
        ,Mockito.anyString(),Mockito.anyBoolean())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .param(ApiConstant.advertiserId,"33").param(ApiConstant.PAGENUMBER,"1")
                .param(ApiConstant.PAGESIZE,"10").param(ApiConstant.SORT,"1")
                .param(ApiConstant.SEARCH,"1").param(ApiConstant.REFRESH,"true");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<List<CampaignDTO>>>() {
        }.getType();
        ApiResponseObject<List<CampaignDTO>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertThat(apiResp.getRespObject().get(0).getName()).isEqualTo("SafariTestIO");
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.campaign.CampaignController#activateCampaign(String)}.
     */
    @Test
    public void testActivateCampaign() throws Exception {
        String url = ApiConstant.CAMPAIGNS + ApiConstant.activate;
        ResponseMessage responseMessage = new ResponseMessage(33,"Honda");
        Map<Integer,ResponseMessage> map = new HashMap<>();
        map.put(33,responseMessage);
        ApiResponseObject<Map<Integer,ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(map);
        Mockito.when(campaignService.activate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createCampaignDTO()))
                .param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Integer,ResponseMessage>>>() {
        }.getType();
        ApiResponseObject<Map<Integer,ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
    }

    /**
     * Test method for
     * {@link io.revx.api.controller.campaign.CampaignController#activateCampaign(String)}.
     */
    @Test
    public void testDeactivateCampaign() throws Exception {
        String url = ApiConstant.CAMPAIGNS + ApiConstant.deactivate;
        ResponseMessage responseMessage = new ResponseMessage(33,"Honda");
        Map<Integer,ResponseMessage> map = new HashMap<>();
        map.put(33,responseMessage);
        ApiResponseObject<Map<Integer,ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(map);
        Mockito.when(campaignService.deactivate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createCampaignDTO()))
                .param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Integer,ResponseMessage>>>() {
        }.getType();
        ApiResponseObject<Map<Integer,ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
    }
}
