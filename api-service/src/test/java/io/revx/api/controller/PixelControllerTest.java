package io.revx.api.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.pixel.PixelController;
import io.revx.api.service.ValidationService;
import io.revx.api.service.pixel.impl.ConversionPixelService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.pixel.Tag;
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
public class PixelControllerTest {
    @Mock
    private ConversionPixelService pixelService;

    @Mock
    private ValidationService validator;

    @InjectMocks
    private PixelController pixelController;

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
        mockMvc = MockMvcBuilders.standaloneSetup(pixelController).setControllerAdvice(hadler).build();
    }

    /**
     * Test method for
     * {@link PixelController#create(Pixel)} }.
     */
    @Test
    public void testCreate() throws Exception{
        ApiResponseObject<Pixel> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createPixelController());
        Mockito.when(pixelService.create(Mockito.any(Pixel.class))).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.PIXELS)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createPixelController()));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Pixel>>() {
        }.getType();
        ApiResponseObject<Pixel> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("TestPixel",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link PixelController#update(Pixel, Integer) }.
     */
    @Test
    public void testUpdate() throws Exception{
        ApiResponseObject<Pixel> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createPixelController());
        Mockito.when(pixelService.update(Mockito.any(Pixel.class))).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.PIXELS+ApiConstant.ID_PATH.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(MockDataGenerator.createPixelController())).param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Pixel>>() {
        }.getType();
        ApiResponseObject<Pixel> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("TestPixel",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link PixelController#activate(String) }.
     */
    @Test
    public void testActivate() throws Exception{
        ResponseMessage responseMessage = new ResponseMessage(33,"33");
        Map<Long,ResponseMessage> map = new HashMap<>();
        map.put(3832L,responseMessage);
        ApiResponseObject apiResponseObject = new ApiResponseObject();
        apiResponseObject.setRespObject(map);
        Mockito.when(pixelService.activate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.PIXELS+ApiConstant.activate)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new String())).param("commaSepratedIds","33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Long, ResponseMessage>>>() {
        }.getType();
        ApiResponseObject<Map<Long, ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
    }

    /**
     * Test method for
     * {@link PixelController#deactivate(String) }.
     */
    @Test
    public void testDeactivate() throws Exception{
        ResponseMessage responseMessage = new ResponseMessage(33,"33");
        Map<Long,ResponseMessage> map = new HashMap<>();
        map.put(3832L,responseMessage);
        ApiResponseObject apiResponseObject = new ApiResponseObject();
        apiResponseObject.setRespObject(map);
        Mockito.when(pixelService.deactivate(Mockito.anyString())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.PIXELS+ApiConstant.deactivate)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new String())).param("commaSepratedIds","33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<Map<Long, ResponseMessage>>>() {
        }.getType();
        ApiResponseObject<Map<Long, ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
    }

    /**
     * Test method for
     * {@link PixelController#searchPixels(SearchRequest, Integer, Integer, boolean, String, Long)}  }.
     */
    @Test
    public void testSearchPixels() throws Exception{
        List<Pixel> list = new ArrayList<>();
        list.add(MockDataGenerator.createPixelController());
        ApiListResponse<Pixel> apiListResponse = new ApiListResponse<>();
        apiListResponse.setData(list);
        ApiResponseObject<ApiListResponse<Pixel>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(apiListResponse);
        SearchRequest searchRequest = new SearchRequest();
        Mockito.when(pixelService.searchPixels(Mockito.any(),Mockito.anyInt(),Mockito.anyInt(),
                Mockito.anyString(),Mockito.anyBoolean(),Mockito.anyLong())).thenReturn(apiListResponse);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.PIXELS+ApiConstant.search)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(searchRequest))
                .param(ApiConstant.PAGENUMBER,"1").param(ApiConstant.PAGESIZE,"10")
                .param(ApiConstant.SORT,"id").param(ApiConstant.advertiserId,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<Pixel>>>() {
        }.getType();
        ApiResponseObject<ApiListResponse<Pixel>> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("TestPixel",apiResp.getRespObject().getData().get(0).getName());
    }

    /**
     * Test method for
     * {@link PixelController#getById(Long) }.
     */
    @Test
    public void testGetById() throws Exception {
        ApiResponseObject<Pixel> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(MockDataGenerator.createPixelController());
        Mockito.when(pixelService.getbyId(Mockito.anyLong())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.PIXELS+ApiConstant.ID_PATH.replace("{id}","33"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken< ApiResponseObject<Pixel>>() {
        }.getType();
        ApiResponseObject<Pixel> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("TestPixel",apiResp.getRespObject().getName());
    }

    /**
     * Test method for
     * {@link PixelController#getTrackingCode(Long)}  }.
     */
    @Test
    public void testGetTrackingCode() throws Exception{
        Tag tag = new Tag();
        tag.setName("Honda");
        tag.setId(3832L);
        ApiResponseObject<Tag> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(tag);
        Mockito.when(pixelService.getTrackingCode(Mockito.anyLong())).thenReturn(apiResponseObject);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.PIXELS+ApiConstant.ID_PATH.replace("{id}","33")+
                ApiConstant.trackingCode).accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .param(ApiConstant.ID,"33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken< ApiResponseObject<Tag>>() {
        }.getType();
        ApiResponseObject<Tag> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Honda",apiResp.getRespObject().getName());
    }
}
