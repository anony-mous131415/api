/**
 * 
 */
package io.revx.auth.controller;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.reflect.TypeToken;
import io.revx.auth.entity.LifeTimeAuthenticationEntity;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.service.LifeTimeAuthTokenService;
import io.revx.core.constant.Constants;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ResponseMessage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
import org.springframework.web.util.NestedServletException;
import com.google.gson.Gson;
import io.revx.auth.constants.ApiConstant;
import io.revx.auth.requests.PasswordChangeRequest;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.service.AuthValidaterServiceImpl;
import io.revx.auth.service.ExternalTokenAuthService;
import io.revx.auth.service.UserDetailsServiceImpl;
import io.revx.core.model.Licensee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("rawtypes")
public class AuthControllerTest {

  @Mock
  private AuthValidaterServiceImpl authValidaterServiceImpl;


  @Mock
  private ExternalTokenAuthService externalTokenAuthService;


  @Mock
  private UserDetailsServiceImpl userDetailsServiceImpl;

  @Mock
  private LifeTimeAuthTokenService lifeTimeAuthTokenService;

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  private MockMvc mockMvc;


  @InjectMocks
  AuthController authController;

  static {
    System.setProperty("jasypt.encryptor.password", "mySecretKey@123");
  }


  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    // this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    // System.setProperty("jasypt.encryptor.password", "mySecretKey@123");

  }

  /**
   * Test method for {@link io.revx.auth.controller.AuthController#userInfo(java.lang.String)}.
   */
  @Test
  public void testUserInfo() throws Exception {
    UserInfo uInfo = new UserInfo();
    uInfo.setUsername("akhilesh");
    ApiResponseObject<UserInfo> resp = new ApiResponseObject<>();
    resp.setRespObject(uInfo);
    Mockito.when(authValidaterServiceImpl.validateToken(Mockito.anyString())).thenReturn(uInfo);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.USER_INFO)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    ApiResponseObject apiResp =
        new Gson().fromJson(result.getResponse().getContentAsString(), ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());

  }

  @Test
  public void testUserInfoFailed() throws Exception {
    UserInfo uInfo = new UserInfo();
    uInfo.setUsername("akhilesh");
    ApiResponseObject<UserInfo> resp = new ApiResponseObject<>();
    resp.setRespObject(uInfo);
    Mockito.when(authValidaterServiceImpl.validateToken(Mockito.anyString())).thenReturn(uInfo);
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(ApiConstant.USER_INFO).accept(MediaType.APPLICATION_JSON);
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    ApiResponseObject apiResp =
        new Gson().fromJson(result.getResponse().getContentAsString(), ApiResponseObject.class);
    assertNull(apiResp);
  }

  /**
   * Test method for {@link io.revx.auth.controller.AuthController#userPrivilege(java.lang.String)}.
   */
  @Test
  public void testUserPrivilege() throws Exception {
    Set<Licensee> licensee = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      licensee.add(new Licensee(i + 1, "Licensee " + (i + 1)));
    }

    ApiResponseObject<Set<Licensee>> resp = new ApiResponseObject<>();
    resp.setRespObject(licensee);
    Mockito.when(userDetailsServiceImpl.fetchUserPrivilige(Mockito.anyString())).thenReturn(resp);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.USER_PRIVILEGE)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    ApiResponseObject apiResp =
        new Gson().fromJson(result.getResponse().getContentAsString(), ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());

  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#switchLicensee(java.lang.Long, java.lang.String)}.
   */
  @Test
  public void testSwitchLicensee() throws Exception {
    TokenResponse tResp = new TokenResponse("akhilesh", "hjaFG64R6RABD.Q");
    ApiResponseObject<TokenResponse> resp = new ApiResponseObject<>();
    resp.setRespObject(tResp);
    Mockito.when(authValidaterServiceImpl.switchLicensee(Mockito.anyLong(), Mockito.anyString()))
        .thenReturn(resp);
    String licenseeUrl = ApiConstant.SWITCH_LICENSEE.replace("{licenseeId}", "33");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(licenseeUrl)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    ApiResponseObject apiResp = new Gson().fromJson(contentString, ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#loginSocial(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testLoginSocial() throws Exception {
    TokenResponse tResp = new TokenResponse("akhilesh", "hjaFG64R6RABD.Q");
    ApiResponseObject<TokenResponse> resp = new ApiResponseObject<>();
    resp.setRespObject(tResp);
    Mockito.when(externalTokenAuthService.getAccessTokenFromExternalToken(Mockito.anyString(),
        Mockito.anyString())).thenReturn(resp);
    String socialUrl = ApiConstant.LOGIN_SOCIAL.replace("{client}/{socialToken}", "google/rtu426");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(socialUrl)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    ApiResponseObject apiResp = new Gson().fromJson(contentString, ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#logoutWithToken(java.lang.String)}.
   */
  @Test
  public void testLogoutWithToken() throws Exception {
    ApiResponseObject<Boolean> resp = new ApiResponseObject<>();
    resp.setRespObject(true);
    Mockito.when(authValidaterServiceImpl.logout(Mockito.anyString())).thenReturn(resp);
    String logoutTokenUrl = ApiConstant.LOGOUT_URL;
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(logoutTokenUrl)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    ApiResponseObject apiResp = new Gson().fromJson(contentString, ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#logoutUser(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testLogoutUser() throws Exception {
    ApiResponseObject<Boolean> resp = new ApiResponseObject<>();
    resp.setRespObject(true);
    Mockito.when(authValidaterServiceImpl.logoutUser(Mockito.anyString(), Mockito.anyString()))
        .thenReturn(resp);
    String logoutTokenUrl = ApiConstant.LOGOUT_URL_USER.replace("{username}", "akhilesh");
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(logoutTokenUrl)
        .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    ApiResponseObject apiResp = new Gson().fromJson(contentString, ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#changePassword(io.revx.auth.requests.PasswordChangeRequest, java.lang.String)}.
   */
  @Test
  public void testChangePassword() throws Exception {
    PasswordChangeRequest pReq = new PasswordChangeRequest();
    pReq.setUsername("akhilesh");
    pReq.setOldPassword("pass");
    pReq.setNewPassword("newPass");
    ApiResponseObject<Boolean> resp = new ApiResponseObject<>();
    resp.setRespObject(true);
    Mockito.when(authValidaterServiceImpl.changePassword(Mockito.any(), Mockito.anyString()))
        .thenReturn(resp);
    String logoutTokenUrl = ApiConstant.CHANGE_PASS_SECRET;
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(logoutTokenUrl)
        .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
        .content(new Gson().toJson(pReq)).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    ApiResponseObject apiResp = new Gson().fromJson(contentString, ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#login(io.revx.auth.requests.UserLoginRequest)}.
   */
  @Test
  public void testLogin() throws Exception {
    UserLoginRequest pReq = new UserLoginRequest();
    pReq.setUsername("akhilesh");
    pReq.setPassword("pass");
    ApiResponseObject<Boolean> resp = new ApiResponseObject<>();
    resp.setRespObject(true);
    Mockito.when(authValidaterServiceImpl.changePassword(Mockito.any(), Mockito.anyString()))
        .thenReturn(resp);
    String logoutTokenUrl = ApiConstant.LOGIN_URL;
    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post(logoutTokenUrl).accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON).content(new Gson().toJson(pReq));
    exceptionRule.expect(NestedServletException.class);
    mockMvc.perform(requestBuilder).andReturn();
  }


  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#createLifeTimeToken(java.lang.String)}.
   */
  @Test
  public void testCreateLifeTimeToken() throws Exception {
    LifeTimeAuthenticationEntity lifeTimeAuthenticationEntity = new LifeTimeAuthenticationEntity();
    lifeTimeAuthenticationEntity.setUserId(21957L);
    lifeTimeAuthenticationEntity.setLicenseeId(219L);
    lifeTimeAuthenticationEntity.setCreateOn(System.currentTimeMillis()/1000);
    lifeTimeAuthenticationEntity.setLifeTimeAuthToken("someRandomAuthTokensdkfksjdfjsdfkjbsdfkjbskdfjb");

    Mockito.when(lifeTimeAuthTokenService.generateLifeTimeAuthToken(Mockito.anyString())).thenReturn(lifeTimeAuthenticationEntity);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ApiConstant.CREATE_LIFE_TIME_AUTH)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    ApiResponseObject apiResp = new Gson().fromJson(contentString, ApiResponseObject.class);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#getLifeTimeToken(java.lang.String)}  (java.lang.String)}.
   */
  @Test
  public void testGetLifeTimeToken() throws Exception {
    List<LifeTimeAuthenticationEntity> list = TestDataGenerator.getListOfObject(10, LifeTimeAuthenticationEntity.class);
    ApiListResponse<LifeTimeAuthenticationEntity> apiListResponse = new ApiListResponse<>();
    apiListResponse.setData(list);
    apiListResponse.setTotalNoOfRecords(10);
    Mockito.when(lifeTimeAuthTokenService.getLifeTimeAuthToken(Mockito.anyString())).thenReturn(apiListResponse);

    RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ApiConstant.LIFE_TIME_AUTH)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<ApiListResponse<LifeTimeAuthenticationEntity>>>() {}.getType();
    ApiResponseObject<ApiListResponse<LifeTimeAuthenticationEntity>> apiResp =
            new Gson().fromJson(contentString, respType);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertNotNull(apiResp.getRespObject().getData());
    assertThat(apiResp.getRespObject().getTotalNoOfRecords()).isEqualTo(10);
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#deleteLifeTimeToken(String, long)};
   */
  @Test
  public void testDeleteLifeTimeToken() throws Exception {
    ResponseMessage responseMessage = new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS);
    String tokenId = "1";
    Mockito.when(lifeTimeAuthTokenService.deleteLifeTimeAuthToken(Mockito.anyLong(), Mockito.anyString())).thenReturn(responseMessage);

    String url = ApiConstant.DELETE_LIFE_TIME_AUTH.replace("{tokenId}",
            tokenId);

    RequestBuilder requestBuilder = MockMvcRequestBuilders.put(url)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<ResponseMessage>>() {}.getType();
    ApiResponseObject<ResponseMessage> apiResp =
            new Gson().fromJson(contentString, respType);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertEquals(apiResp.getRespObject().getMessage(), Constants.MSG_SUCCESS );
  }

  /**
   * Test method for
   * {@link io.revx.auth.controller.AuthController#deleteLifeTimeToken(String, long)};
   */
  @Test
  public void testDeleteLifeTimeTokenDeletedToken() throws Exception {
    ResponseMessage responseMessage = new ResponseMessage(Constants.ID_ALREADY_INACTIVE, Constants.MSG_ID_ALREADY_INACTIVE);
    String tokenId = "1";
    Mockito.when(lifeTimeAuthTokenService.deleteLifeTimeAuthToken(Mockito.anyLong(), Mockito.anyString())).thenReturn(responseMessage);

    String url = ApiConstant.DELETE_LIFE_TIME_AUTH.replace("{tokenId}",
            tokenId);

    RequestBuilder requestBuilder = MockMvcRequestBuilders.put(url)
            .accept(MediaType.APPLICATION_JSON).header("token", "FGADFJGKEFUQE4368");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    assertNotNull(result.getResponse());
    String contentString = result.getResponse().getContentAsString();
    Type respType = new TypeToken<ApiResponseObject<ResponseMessage>>() {}.getType();
    ApiResponseObject<ResponseMessage> apiResp =
            new Gson().fromJson(contentString, respType);
    assertNull(apiResp.getError());
    assertNotNull(apiResp.getRespObject());
    assertEquals(apiResp.getRespObject().getMessage(), Constants.MSG_ID_ALREADY_INACTIVE);
  }
}
