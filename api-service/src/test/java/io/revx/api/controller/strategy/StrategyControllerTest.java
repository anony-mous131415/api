package io.revx.api.controller.strategy;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.BaseTestService;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.strategy.StrategyService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
public class StrategyControllerTest extends BaseTestService {

    private MockMvc mockMvc;

    @InjectMocks
    private StrategyController strategyController;

    @Mock
    private StrategyService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        strategyController.strategyService = service;
        ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
        CommonExceptionHandler handler = new CommonExceptionHandler();
        handler.apiErrorCodeResolver = apiErrorCodeResolver;
        mockMvc = MockMvcBuilders.standaloneSetup(strategyController).setControllerAdvice(handler).build();
    }

    @Test
    public void testGetSkadSettings() throws Exception {
        String responseString = "Skad settings json string";
        ApiResponseObject<String> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(responseString);

        Mockito.when(service.getSkadSettings()).thenReturn(responseObject);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.STRATEGIES + ApiConstant.SKAD_SETTINGS)
                .header("token","ydagfyiecbweicwibc")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertNotNull(result.getResponse());
        Type respType = new TypeToken<ApiResponseObject<String>>(){}.getType();
        ApiResponseObject<String> apiResp = new Gson()
                .fromJson(result.getResponse().getContentAsString(), respType);

        assertNotNull(apiResp.getRespObject());
        assertEquals("Skad settings json string",apiResp.getRespObject());
    }
}
