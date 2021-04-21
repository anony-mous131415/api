/**
 * 
 */
package io.revx.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import java.lang.reflect.Type;
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
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.WhitelabelingEntity;
import io.revx.api.service.WhiteLablingService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import io.revx.core.response.ApiResponseObject;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WhiteLablingControllerTest {
  @Mock
  private WhiteLablingService whiteLablingService;
  @InjectMocks
  private WhiteLablingController whiteLablingController;

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
        MockMvcBuilders.standaloneSetup(whiteLablingController).setControllerAdvice(hadler).build();
    WhitelabelingEntity entity = TestDataGenerator.getObject(WhitelabelingEntity.class);
    entity.setLicenseeId(33);
    entity.setSubDomain("affle");
    entity.setId(10);
    ApiResponseObject<WhitelabelingEntity> resp = new ApiResponseObject<>();
    resp.setRespObject(entity);

    when(whiteLablingService.findBySubDomain(Mockito.contains("affle"))).thenReturn(resp);
    when(whiteLablingService.findByLicenseeId(Mockito.eq(33))).thenReturn(resp);
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.WhiteLablingController#themeBySubDomain(java.lang.String)}.
   */
  @Test
  public void testThemeBySubDomain() throws Exception {

    String url = ApiConstant.THEME_BY_SUBDOMAIN.replace("{subdomain}", "affle");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    @SuppressWarnings("serial")
    Type respType = new TypeToken<ApiResponseObject<WhitelabelingEntity>>() {}.getType();
    ApiResponseObject<WhitelabelingEntity> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getLicenseeId()).isEqualTo(33);

  }


  /**
   * Test method for {@link io.revx.api.controller.WhiteLablingController#themeByLicenseeId(int)}.
   */
  @Test
  public void testThemeByLicenseeId() throws Exception {

    String url = ApiConstant.THEME_BY_LICENSEE_ID.replace("{licenseeId}", "33");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(url)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    @SuppressWarnings("serial")
    Type respType = new TypeToken<ApiResponseObject<WhitelabelingEntity>>() {}.getType();
    ApiResponseObject<WhitelabelingEntity> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getLicenseeId()).isEqualTo(33);

  }

}
