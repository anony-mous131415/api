package io.revx.api.controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.slicex.SlicexController;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.SlicexService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.requests.*;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class SlicexControllerTest extends BaseTestService {
    @Mock
    private SlicexService slicexService;

    @Mock
    private LoginUserDetailsService loginService;

    private MockMvc mockMvc;

    @InjectMocks
    private SlicexController slicexController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc = MockMvcBuilders.standaloneSetup(slicexController).setControllerAdvice(handler).build();
    }

    @Test
    public void testGetSlicexDataChart() throws Exception{
        Duration duration = new Duration();
        duration.setEndTimeStamp(1614124800L);
        duration.setStartTimeStamp(1614038400L);
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilters());
        SlicexRequest request = new SlicexRequest();
        request.setDuration(duration);
        request.setGroupBy("daily");
        request.setCompareToDuration(null);
        request.setFilters(list);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        Mockito.when(slicexService.getSlicexChartData(Mockito.any()))
                .thenReturn(MockDataGenerator.getSlicexChartResponse());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.SLICEX_CHART)
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request));
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<SlicexChartResponse>>() {}.getType();
        ApiResponseObject<SlicexChartResponse> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(1,apiResp.getRespObject().getTotalNoOfRecords());
    }

    @Test
    public void testGetSlicexDataList() throws Exception{
        Duration duration = new Duration();
        duration.setEndTimeStamp(1614124800L);
        duration.setStartTimeStamp(1614038400L);
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilters());
        SlicexRequest request = new SlicexRequest();
        request.setDuration(duration);
        request.setGroupBy("daily");
        request.setCompareToDuration(null);
        request.setFilters(list);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        Mockito.when(slicexService.getSlicexGridData(Mockito.any(),Mockito.anyString(),Mockito.any()))
                .thenReturn(MockDataGenerator.getSlicexListResponse());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.SLICEX_LIST.replace("{entity}","advertiser"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request)).param(ApiConstant.SORT,"2");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<SlicexListResponse>>() {}.getType();
        ApiResponseObject<SlicexListResponse> apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(1,apiResp.getRespObject().getTotalNoOfRecords());
    }

    @Test
    public void testGetSlicexListDataForExport() throws Exception{
        Duration duration = new Duration();
        duration.setEndTimeStamp(1614124800L);
        duration.setStartTimeStamp(1614038400L);
        List<DashboardFilters> list = new ArrayList<>();
        list.add(MockDataGenerator.getDashBoardFilters());
        SlicexRequest request = new SlicexRequest();
        request.setDuration(duration);
        request.setGroupBy("daily");
        request.setCompareToDuration(null);
        request.setFilters(list);
        mockSecurityContext("akhilesh", false, false);
        UserInfo ui = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(loginService.getUserInfo()).thenReturn(ui);
        Mockito.when(slicexService.getSlicexGridDataForExport(Mockito.any(),Mockito.anyString(),Mockito.any()))
                .thenReturn(MockDataGenerator.setFileDownloadResponse());
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.SLICEX_LIST_EXPORT.replace("{entity}","advertiser"))
                .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(request)).param(ApiConstant.SORT,"sort");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<FileDownloadResponse>() {}.getType();
        FileDownloadResponse apiResp = new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("www.komli.com",apiResp.getFileDownloadUrl());
    }
}
