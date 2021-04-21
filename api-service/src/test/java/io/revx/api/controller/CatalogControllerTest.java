package io.revx.api.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.catalog.CatalogController;
import io.revx.api.service.catalog.CatalogService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
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

@RunWith(SpringJUnit4ClassRunner.class)
public class CatalogControllerTest {
    @Mock
    private CatalogService catalogService;

    @InjectMocks
    private CatalogController catalogController;

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
        mockMvc = MockMvcBuilders.standaloneSetup(catalogController).setControllerAdvice(hadler).build();
    }

    /**
     * Test method for
     * {@link CatalogController#getMacros(Long, Integer, Integer, String, boolean, SearchRequest)} }.
     */
    @Test
    public void testGetMacros() throws Exception{
        List<Macro> list = new ArrayList<>();
        list.add(MockDataGenerator.createMacro());
        ApiListResponse<Macro> apiListResponse = new ApiListResponse<>();
        apiListResponse.setData(list);
        Mockito.when(catalogService.getMacros(Mockito.anyLong(),Mockito.anyInt(),Mockito.any(),Mockito.anyInt()
        ,Mockito.anyString(),Mockito.anyBoolean())).thenReturn(apiListResponse);
        SearchRequest searchRequest = new SearchRequest();
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.catalog+ApiConstant.macros).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(searchRequest)).param(ApiConstant.advertiserId,"33")
                .param(ApiConstant.PAGENUMBER,"1").param(ApiConstant.PAGESIZE,"10")
                .param(ApiConstant.SORT,"id");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiListResponse<Macro>>() {}.getType();
        ApiListResponse<Macro> apiResp =
                new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals(1,apiResp.getData().size());
    }

    /**
     * Test method for
     * {@link CatalogController#getCatalogFeeds(Integer, Integer, Long, String, boolean, SearchRequest)}  }.
     */
    @Test
    public void testGetCatalogFeeds() throws Exception{
        List<CatalogFeed> list = new ArrayList<>();
        list.add(MockDataGenerator.createCatalogFeed());
        ApiListResponse<CatalogFeed> apiListResponse = new ApiListResponse<>();
        apiListResponse.setData(list);
        Mockito.when(catalogService.getFeeds(Mockito.anyLong(),Mockito.anyInt(),Mockito.any(),Mockito.anyInt()
                ,Mockito.anyString(),Mockito.anyBoolean())).thenReturn(apiListResponse);
        SearchRequest searchRequest = new SearchRequest();
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.catalog+ApiConstant.feeds).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(searchRequest)).param(ApiConstant.advertiserId,"33")
                .param(ApiConstant.PAGENUMBER,"1").param(ApiConstant.PAGESIZE,"10")
                .param(ApiConstant.SORT,"id");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<CatalogFeed>>>() {}.getType();
        ApiResponseObject<ApiListResponse<CatalogFeed>> apiResp =
                new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Honda",apiResp.getRespObject().getData().get(0).getName());
    }

    /**
     * Test method for
     * {@link CatalogController#getVariableMappings(Integer, Integer, String, boolean, SearchRequest, Long)}  }.
     */
    @Test
    public void testGetVariableMappings() throws Exception {
        List<VariablesMappingDTO> list = new ArrayList<>();
        list.add(MockDataGenerator.createVariablesMappingDTO());
        ApiListResponse<VariablesMappingDTO> apiListResponse = new ApiListResponse<>();
        apiListResponse.setData(list);
        Mockito.when(catalogService.getVariableMappings(Mockito.anyLong(),Mockito.anyInt(),Mockito.anyInt()
        ,Mockito.any(),Mockito.anyString(),Mockito.anyBoolean())).thenReturn(apiListResponse);
        SearchRequest searchRequest = new SearchRequest();
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(ApiConstant.catalog+ApiConstant.variableMappings).accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(searchRequest)).param(ApiConstant.advertiserId,"33")
                .param(ApiConstant.PAGENUMBER,"1").param(ApiConstant.PAGESIZE,"10")
                .param(ApiConstant.SORT,"id").param("feedId","33");
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<ApiListResponse<VariablesMappingDTO>>>() {}.getType();
        ApiResponseObject<ApiListResponse<VariablesMappingDTO>> apiResp =
                new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Honda",apiResp.getRespObject().getData().get(0).getName());
    }

    @Test
    public void testGetById() throws  Exception{
        ApiResponseObject<CatalogFeed> apiListResponse = new ApiResponseObject<>();
        apiListResponse.setRespObject(MockDataGenerator.createCatalogFeed());
        Mockito.when(catalogService.getbyId(Mockito.anyLong())).thenReturn(apiListResponse);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(ApiConstant.catalog+"/feeds/33").accept(MediaType.APPLICATION_JSON)
                .header("token", "FGADFJGKEFUQE4368").contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        String contentString = result.getResponse().getContentAsString();
        Type respType = new TypeToken<ApiResponseObject<CatalogFeed>>() {}.getType();
        ApiResponseObject<CatalogFeed> apiResp =
                new Gson().fromJson(contentString, respType);
        assertNotNull(apiResp);
        assertEquals("Honda",apiResp.getRespObject().getName());
    }
}
