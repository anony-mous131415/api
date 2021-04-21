package io.revx.api.controller.creative;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.creative.CreativeService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.CreativeCompactDTO;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeHtmlMockupDTO;
import io.revx.core.model.requests.DashboardFilters;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class CreativeControllerTest {

    @Mock
    private CreativeService creativeService;

    @InjectMocks
    private CreativeController creativeController;

    private MockMvc mockMvc;

    static {
        System.setProperty("jasypt.encryptor.password", "mySecretKey@123");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        creativeController = new CreativeController();
        creativeController.creativeService = creativeService;
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc = MockMvcBuilders.standaloneSetup(creativeController)
                .setControllerAdvice(handler).build();
    }

    @Test
    public void testHtmlMockups() throws Exception {
        List<CreativeDTO> creativeDTOS = new ArrayList<>();
        CreativeDTO creativeDTO = new CreativeDTO();
        creativeDTO.setId(1234L);
        creativeDTOS.add(creativeDTO);
        ApiListResponse<CreativeDTO> response = new ApiListResponse<>();
        response.setData(creativeDTOS);
        response.setTotalNoOfRecords(1);
        Mockito.when(creativeService.htmlMockups(Mockito.any())).thenReturn(response);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.creatives+ApiConstant.HTML_MOCKUPS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(new CreativeHtmlMockupDTO()))
                .header("token","ydagfyiecbweicwibc");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiListResponse<CreativeDTO>>() {}.getType();
        ApiListResponse<CreativeDTO> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp);
        assertEquals(1,apiResp.getTotalNoOfRecords());
        assertEquals(1234,apiResp.getData().get(0).id.longValue());
    }

    @Test
    public void testSearchCreativesCompact() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        List<DashboardFilters> dashboardFiltersList = new ArrayList<>();
        DashboardFilters filter1 = new DashboardFilters("advertiserId", "7146");
        DashboardFilters filter2 = new DashboardFilters("status", "active");

        dashboardFiltersList.add(filter1);
        dashboardFiltersList.add(filter2);
        searchRequest.setFilters(dashboardFiltersList);
        List<CreativeCompactDTO> list = new ArrayList<>();
        list.addAll(TestDataGenerator.getListOfObject(20, CreativeCompactDTO.class));
        ApiListResponse<CreativeCompactDTO> apiListResponse = new ApiListResponse<>();

        apiListResponse.setData(list);

        ApiResponseObject<ApiListResponse<CreativeCompactDTO>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(apiListResponse);

        Mockito.when(creativeService.searchCompactCreatives(Mockito.any(SearchRequest.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenReturn(apiListResponse);

        String url = ApiConstant.creatives + ApiConstant.search + ApiConstant.COMPACT;
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(searchRequest))
                .param(ApiConstant.PAGENUMBER, "1").param(ApiConstant.PAGESIZE, "10")
                .param(ApiConstant.SORT, "id");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<CreativeCompactDTO>>>() {}.getType();
        ApiResponseObject<ApiListResponse<CreativeCompactDTO>> apiResp =
                new Gson().fromJson(contentString, respType);

        assertNull(apiResp.getError());
        assertNotNull(apiResp.getRespObject());
        assertNotNull(apiResp.getRespObject().getData());
        assertEquals(apiResp.getRespObject().getData().size(),20);
    }

    @Test
    public void testSearchCreativesCompactFailed() throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        List<DashboardFilters> dashboardFiltersList = new ArrayList<>();
        DashboardFilters filter1 = new DashboardFilters("advertiserId", "7146");
        DashboardFilters filter2 = new DashboardFilters("status", "active");

        dashboardFiltersList.add(filter1);
        dashboardFiltersList.add(filter2);
        searchRequest.setFilters(dashboardFiltersList);

        List<CreativeCompactDTO> list = new ArrayList<>();
        list.addAll(TestDataGenerator.getListOfObject(20, CreativeCompactDTO.class));

        ApiListResponse<CreativeCompactDTO> apiListResponse = new ApiListResponse<>();
        apiListResponse.setData(list);

        ApiResponseObject<ApiListResponse<CreativeCompactDTO>> apiResponseObject = new ApiResponseObject<>();
        apiResponseObject.setRespObject(apiListResponse);

        Mockito.when(creativeService.searchCompactCreatives(Mockito.any(SearchRequest.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean())).thenThrow(new ValidationException("Some error"));;

        String url = ApiConstant.creatives + ApiConstant.search + ApiConstant.COMPACT;
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(searchRequest))
                .param(ApiConstant.PAGENUMBER, "1").param(ApiConstant.PAGESIZE, "10")
                .param(ApiConstant.SORT, "id");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());

        String contentString = result.getResponse().getContentAsString();

        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<CreativeCompactDTO>>>() {}.getType();
        ApiResponseObject<ApiListResponse<CreativeCompactDTO>> apiResp =
                new Gson().fromJson(contentString, respType);

        assertNotNull(apiResp);
        assertNotNull(apiResp.getError());
        assertNull(apiResp.getRespObject());
    }
}