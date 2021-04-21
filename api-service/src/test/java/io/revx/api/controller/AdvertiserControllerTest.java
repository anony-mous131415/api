/**
 * 
 */
package io.revx.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.revx.api.common.MockDataGenerator;
import io.revx.api.constants.ApiConstant;
import io.revx.api.controller.advertiser.AdvertiserController;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.pixel.Tag;
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
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.advertiser.AdvertiserService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.CommonExceptionHandler;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author ranjan-pritesh
 * @date 25th Nov 2019
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AdvertiserControllerTest {
  @Mock
  private AdvertiserService advertiserService;

  @Mock
  private LoginUserDetailsService loginUserDetailsService;

  @InjectMocks
  private AdvertiserController advertiserController;

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
    mockMvc = MockMvcBuilders.standaloneSetup(advertiserController).setControllerAdvice(hadler).build();
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#createAdvertiser(io.revx.core.model.advertiser.AdvertiserPojo)}.
   */
  @Test
  public void testCreateAdvertiser() throws Exception {
    String url = ApiConstant.ADVERTISERS;
    ApiResponseObject<AdvertiserPojo> response = new ApiResponseObject<>();
    response.setRespObject(MockDataGenerator.createAdvertiserPojo());
    when(advertiserService.create(Mockito.any())).thenReturn(response);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(url)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(MockDataGenerator.createAdvertiserPojo()));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<AdvertiserPojo>>() {
    }.getType();
    ApiResponseObject<AdvertiserPojo> apiResp = new Gson().fromJson(contentString, respType);
    assertThat(apiResp.getRespObject().getEmail()).isEqualTo("tribal@test.com");
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#updateAdvertiser(io.revx.core.model.advertiser.AdvertiserPojo, java.lang.Integer)}.
   */
  @Test
  public void testUpdateAdvertiser() throws Exception {
    ApiResponseObject<AdvertiserPojo> response = new ApiResponseObject<>();
    response.setRespObject(MockDataGenerator.createAdvertiserPojo());
    Mockito.when(advertiserService.update(Mockito.any())).thenReturn(response);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS + ApiConstant.ID_PATH.replace("{id}", "3875"))
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(MockDataGenerator.createAdvertiserPojo()));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<AdvertiserPojo>>() {
    }.getType();
    ApiResponseObject<AdvertiserPojo> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getEmail()).isEqualTo("tribal@test.com");
  }

  @Test
  public void testUpdateAdvertiserNull() throws Exception {
    AdvertiserPojo advertiserPojo = new AdvertiserPojo();
    advertiserPojo.setName("Honda");
    advertiserPojo.setId(3832L);
    advertiserPojo.setEmail("honda@tribal.com");
    advertiserPojo.setActive(true);
    ApiResponseObject<AdvertiserPojo> response = new ApiResponseObject<>();
    response.setRespObject(advertiserPojo);
    Mockito.when(advertiserService.update(Mockito.any())).thenReturn(response);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS + ApiConstant.ID_PATH.replace("{id}", "3832"))
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(advertiserPojo));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<AdvertiserPojo>>() {
    }.getType();
    ApiResponseObject<AdvertiserPojo> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#updateSettings(io.revx.core.model.advertiser.AdvertiserSettings, java.lang.Long)}.
   */
  @Test
  public void testUpdateSettings() throws Exception {
    ApiResponseObject<AdvertiserSettings> apiResponseObject = new ApiResponseObject<>();
    apiResponseObject.setRespObject(MockDataGenerator.createAdvertiserSettings());
    Mockito.when(advertiserService.update(Mockito.any(), Mockito.any())).thenReturn(apiResponseObject);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS + ApiConstant.SETTINGS + ApiConstant.ID_PATH.replace("{id}", "3832"))
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(MockDataGenerator.createAdvertiserSettings()));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<AdvertiserSettings>>() {
    }.getType();
    ApiResponseObject<AdvertiserSettings> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getMmp().getName()).isEqualTo("Honda");
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#getAdvertiserSettings(java.lang.Long)}.
   */
  @Test
  public void testGetAdvertiserSettings() throws Exception {
    ApiResponseObject<AdvertiserSettings> apiResponseObject = new ApiResponseObject<>();
    apiResponseObject.setRespObject(MockDataGenerator.createAdvertiserSettings());
    Mockito.when(advertiserService.getSettingsById(Mockito.any())).thenReturn(apiResponseObject);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.ADVERTISERS + ApiConstant.SETTINGS + ApiConstant.ID_PATH.replace("{id}", "3832"))
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<AdvertiserSettings>>() {
    }.getType();
    ApiResponseObject<AdvertiserSettings> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getMmp().getName()).isEqualTo("Honda");
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#getById(Long, boolean)}.
   */
  @Test
  public void testGetById() throws Exception {
    ApiResponseObject<AdvertiserPojo> response = new ApiResponseObject<>();
    response.setRespObject(MockDataGenerator.createAdvertiserPojo());
    Mockito.when(advertiserService.getById(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(response);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.ADVERTISERS + ApiConstant.ID_PATH.replace("{id}", "3832"))
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<AdvertiserPojo>>() {
    }.getType();
    ApiResponseObject<AdvertiserPojo> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getEmail()).isEqualTo("tribal@test.com");
  }

  /**
   * Test method for
   * {@link AdvertiserController#updateAST()}.
   */
  @Test
  public void testUpdateAstForAdvertiser() throws Exception {
    ApiResponseObject<Boolean> response = new ApiResponseObject<>();
    response.setRespObject(true);
    Mockito.when(advertiserService.generateAstForAdvertiser(Mockito.anyLong())).thenReturn(response);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS + ApiConstant.ID_PATH.replace("{id}", "3832") + ApiConstant.updateAST)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(response));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<Boolean>>() {
    }.getType();
    ApiResponseObject<Boolean> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().booleanValue()).isEqualTo(true);
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#updateAST(Long)}.
   */
  @Test
  public void testUpdateAstForAllAdvertiser() throws Exception {
    ApiResponseObject<Boolean> response = new ApiResponseObject<>();
    response.setRespObject(true);
    Mockito.when(advertiserService.generateAstForAllAdvertiser()).thenReturn(response);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS + ApiConstant.updateAST)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(response));
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<Boolean>>() {
    }.getType();
    ApiResponseObject<Boolean> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().booleanValue()).isEqualTo(true);
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#getSmartTag(java.lang.Long)}.
   */
  @Test
  public void testGetSmartTag() throws Exception {
    Tag tag = new Tag();
    tag.setId(3832L);
    tag.setName("Honda");
    ApiResponseObject<Tag> response = new ApiResponseObject<>();
    response.setRespObject(tag);
    Mockito.when(advertiserService.getSmartTag(Mockito.anyLong())).thenReturn(response);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.ADVERTISERS + ApiConstant.smarttag + ApiConstant.ID_PATH.replace("{id}", "3832"))
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<Tag>>() {}.getType();
    ApiResponseObject<Tag> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
    assertThat(apiResp.getRespObject().getName()).isEqualTo("Honda");
  }

  /**
   * Test method for
   * {@link io.revx.api.controller.advertiser.AdvertiserController#activateAdvertiser(java.lang.String)}.
   */
  @Test
  public void testActivateAdvertiser() throws Exception {
    ResponseMessage responseMessage = new ResponseMessage(33,"Honda");
    Map<Long, ResponseMessage> map = new HashMap<>();
    map.put(33L,responseMessage);
    ApiResponseObject<Map<Long,ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
    apiResponseObject.setRespObject(map);
    String str="Honda";
    Mockito.when( advertiserService.activate(Mockito.any())).thenReturn(apiResponseObject);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS + ApiConstant.activate)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(str)).param("commaSepratedIds","33");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<Map<Long,ResponseMessage>>>() {}.getType();
    ApiResponseObject<Map<Long,ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
  }

  @Test
  public void testDeactivateAdvertiser() throws Exception{
    ResponseMessage responseMessage = new ResponseMessage(3832,"Honda");
    Map<Long,ResponseMessage> map = new HashMap<>();
    map.put(3832L,responseMessage);
    ApiResponseObject<Map<Long,ResponseMessage>> apiResponseObject = new ApiResponseObject<>();
    apiResponseObject.setRespObject(map);
    Mockito.when(advertiserService.deactivate(Mockito.anyString())).thenReturn(apiResponseObject);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.ADVERTISERS +ApiConstant.deactivate)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new Gson().toJson(new String())).param("commaSepratedIds","33");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<Map<Long,ResponseMessage>>>() {
    }.getType();
    ApiResponseObject<Map<Long,ResponseMessage>> apiResp = new Gson().fromJson(contentString, respType);
    assertNotNull(apiResp.getRespObject());
  }
}