/**
 * 
 */
package io.revx.api.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.lang.reflect.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.UiLoggingService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.response.ApiResponseObject;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UiLoggingControllerTest {
  @Mock
  private UiLoggingService uiLoggingService;
  @InjectMocks
  private UiLoggingController uiLoggingController;

  private MockMvc mockMvc;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ApiErrorCodeResolver apiErrorCodeResolver = new ApiErrorCodeResolver();
    CommonExceptionHandler hadler = new CommonExceptionHandler();
    hadler.apiErrorCodeResolver = apiErrorCodeResolver;
    mockMvc =
        MockMvcBuilders.standaloneSetup(uiLoggingController).setControllerAdvice(hadler).build();
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.UiLoggingController#log(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testLog() throws Exception {

    String url = ApiConstant.LOGGING_API;
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(url).accept(MediaType.APPLICATION_JSON)
            .header("token", "FGADFJGKEFUQE4368").content("Mylog Sample");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    @SuppressWarnings("serial")
    Type respType = new TypeToken<ApiResponseObject<Boolean>>() {}.getType();
    ApiResponseObject<Boolean> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertTrue(apiResp.getRespObject());

  }

}
