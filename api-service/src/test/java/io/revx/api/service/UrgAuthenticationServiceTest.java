/**
 * 
 */
package io.revx.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import io.revx.api.common.BaseTestService;
import io.revx.api.common.TestDataGenerator;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.Error;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UrgAuthenticationServiceTest extends BaseTestService {
  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private UrgAuthenticationService urgAuthenticationService;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    urgAuthenticationService.restTemplate = restTemplate;
    urgAuthenticationService.urgHostName = "https://urg.atomex.net";
  }

  /**
   * Test method for
   * {@link io.revx.api.service.UrgAuthenticationService#authenticate(java.lang.String)}.
   */
  @Test
  public void testAuthenticate() throws Exception {
    UserInfo uInfoLogin = TestDataGenerator.getUserInfo("akhilsh", false, false);
    ApiResponseObject<UserInfo> urgResp = new ApiResponseObject<UserInfo>();
    urgResp.setRespObject(uInfoLogin);
    ResponseEntity<ApiResponseObject<UserInfo>> resp =
        new ResponseEntity<ApiResponseObject<UserInfo>>(urgResp, HttpStatus.OK);
    when(restTemplate.exchange(ArgumentMatchers.any(String.class),
        ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.<HttpEntity<?>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<ApiResponseObject<UserInfo>>>any()))
            .thenReturn(resp);
    UserInfo uInfo = urgAuthenticationService.authenticate("hjgdfhegyewfb467236184b");
    assertNotNull(uInfo);
    assertNotNull(uInfo.getUsername());
    assertThat(uInfo.getUsername()).isEqualTo(uInfoLogin.getUsername());
  }

  @Test
  public void testAuthenticateFailedWithURGErrror() throws Exception {
    ApiResponseObject<UserInfo> urgResp = new ApiResponseObject<UserInfo>();
    urgResp.setError(new Error(ErrorCode.ACCESS_TOKEN_EXPIRED.getValue(), "Expired Access Token"));
    ResponseEntity<ApiResponseObject<UserInfo>> resp =
        new ResponseEntity<ApiResponseObject<UserInfo>>(urgResp, HttpStatus.OK);
    when(restTemplate.exchange(ArgumentMatchers.any(String.class),
        ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.<HttpEntity<?>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<ApiResponseObject<UserInfo>>>any()))
            .thenReturn(resp);
    exceptionRule.expect(UserUnAuthenticateException.class);
    exceptionRule.expectMessage(ErrorCode.ACCESS_TOKEN_EXPIRED.name());
    urgAuthenticationService.authenticate("hjgdfhegyewfb467236184b");
  }

  @Test
  public void testAuthenticateFailedNUllResponnse() throws Exception {
    when(restTemplate.exchange(ArgumentMatchers.any(String.class),
        ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.<HttpEntity<?>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<ApiResponseObject<UserInfo>>>any()))
            .thenReturn(null);
    exceptionRule.expect(UserUnAuthenticateException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_ACCESS_TOKEN.name());
    urgAuthenticationService.authenticate("hjgdfhegyewfb467236184b");
  }

  @Test
  public void testAuthenticateFailed() throws Exception {
    when(restTemplate.exchange(ArgumentMatchers.any(String.class),
        ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.<HttpEntity<?>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<ApiResponseObject<UserInfo>>>any()))
            .thenThrow(new RestClientException("Some Exception"));
    exceptionRule.expect(UserUnAuthenticateException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_ACCESS_TOKEN.name());
    urgAuthenticationService.authenticate("hjgdfhegyewfb467236184b");
  }

  /**
   * Test method for
   * {@link io.revx.api.service.UrgAuthenticationService#getUrgResponse(java.lang.String)}.
   */
  @Test
  public void testGetUrgResponse() throws Exception {
    UserInfo uInfoLogin = TestDataGenerator.getUserInfo("akhilsh", false, false);
    ApiResponseObject<UserInfo> urgResp = new ApiResponseObject<UserInfo>();
    urgResp.setRespObject(uInfoLogin);
    ResponseEntity<ApiResponseObject<UserInfo>> resp =
        new ResponseEntity<ApiResponseObject<UserInfo>>(urgResp, HttpStatus.OK);
    when(restTemplate.exchange(ArgumentMatchers.any(String.class),
        ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.<HttpEntity<?>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<ApiResponseObject<UserInfo>>>any()))
            .thenReturn(resp);
    ApiResponseObject<UserInfo> apiResp =
        urgAuthenticationService.getUrgResponse("hjgdfhegyewfb467236184b");
    assertNotNull(apiResp);
    assertNotNull(apiResp.getRespObject());
    assertNull(apiResp.getError());
  }



  @Test
  public void testGetUrgResponseFailedResp() throws Exception {
    ApiResponseObject<UserInfo> urgResp = null;
    ResponseEntity<ApiResponseObject<UserInfo>> resp =
        new ResponseEntity<ApiResponseObject<UserInfo>>(urgResp, HttpStatus.OK);
    when(restTemplate.exchange(ArgumentMatchers.any(String.class),
        ArgumentMatchers.any(HttpMethod.class), ArgumentMatchers.<HttpEntity<?>>any(),
        ArgumentMatchers.<ParameterizedTypeReference<ApiResponseObject<UserInfo>>>any()))
            .thenReturn(resp);
    ApiResponseObject<UserInfo> apiResp =
        urgAuthenticationService.getUrgResponse("hjgdfhegyewfb467236184b");
    assertNull(apiResp);
  }

  @Test
  public void testGetUrgParseUserInfo() throws Exception {
    ApiResponseObject<UserInfo> urgResp = null;
    UserInfo apiResp = urgAuthenticationService.parseUserInfo(urgResp);
    assertNull(apiResp);
  }
}
