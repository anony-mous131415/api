package io.revx.api.controller.advertiser;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.advertiser.AdvertiserService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.model.BaseModel;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.model.requests.SkadTargetPrivileges;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class AdvertiserControllerTest extends BaseTestService {

    private MockMvc mockMvc;

    @InjectMocks
    private AdvertiserController advertiserController;

    @Mock
    private AdvertiserService advertiserService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        advertiserController.advertiserService = advertiserService;
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc = MockMvcBuilders.standaloneSetup(advertiserController).setControllerAdvice(handler).build();
    }

    @Test
    public void testGetSkadPrivileges() throws Exception {
        SearchRequest searchRequest = MockDataGenerator.generateValidRequestForSKADPrivileges();
        List<BaseModel> baseModels = new ArrayList<>();
        BaseModel baseModel = new BaseModel();
        baseModel.setId(1L);
        baseModel.setName("testCampaign");
        baseModels.add(baseModel);
        SkadTargetPrivileges targetPrivileges = new SkadTargetPrivileges(1,baseModels);
        targetPrivileges.setAllowed(false);
        ApiResponseObject<SkadTargetPrivileges> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(targetPrivileges);

        Mockito.when(advertiserService.getSkadTargetPrivileges(Mockito.any())).thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS + ApiConstant.SKAD_TARGET_PRIVILEGE)
                .header("token","ydagfyiecbweicwibc")
                .accept(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(searchRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<SkadTargetPrivileges>>() {}.getType();
        ApiResponseObject<SkadTargetPrivileges> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertFalse(apiResp.getRespObject().isAllowed());
    }

}
