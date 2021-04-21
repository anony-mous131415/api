/**
 * 
 */
package io.revx.auth.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.pojo.UserInfoModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author amaurya
 *
 */
@SuppressWarnings("deprecation")
@RunWith(SpringJUnit4ClassRunner.class)
public class GoogleTokenValidationServiceTest {
  @Mock
  private RestTemplate restTemplate;


  @Mock
  private SecurityConstants securityConstants;
  @InjectMocks
  private GoogleTokenValidationService googleTokenValidationService;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    securityConstants = new SecurityConstants();
    googleTokenValidationService.securityConstants = securityConstants;
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.MicrosoftTokenValidationService#validateToken(java.lang.String)}.
   */
  @Test
  public void testValidateTokenSuccess() throws Exception {
    String authenticationHeaderValue = "hAGDFBCVASGDFAS6534872TAFHGDAJcJKFVDBCV";
    Map<String, String> mapResp = new HashMap<String, String>();
    mapResp.put("email", "akhilesh");
    Gson builder = new GsonBuilder().create();
    mockRestTemplate(authenticationHeaderValue, builder.toJson(mapResp), HttpStatus.OK);
    UserInfoModel userInfoModel = googleTokenValidationService.validateToken(authenticationHeaderValue);
    assertNotNull(userInfoModel);
  }



  @Test
  public void testValidateTokenFailedWhenBadURI() throws Exception {
    String authenticationHeaderValue = "hAGDFBCVASGDFAS6534872TAFHGDAJcJKFVDBCV";
    securityConstants.setGoogleApiUrl("htt://    googlr.com. . ...");
    mockRestTemplate(authenticationHeaderValue, null, HttpStatus.OK);
    UserInfoModel userInfoModel = googleTokenValidationService.validateToken(authenticationHeaderValue);
    assertNull(userInfoModel);
  }

  @Test
  public void testValidateTokenFailedWhenNOResp() throws Exception {
    String authenticationHeaderValue = "hAGDFBCVASGDFAS6534872TAFHGDAJcJKFVDBCV";
    mockRestTemplate(authenticationHeaderValue, null, HttpStatus.OK);
    UserInfoModel userInfoModel = googleTokenValidationService.validateToken(authenticationHeaderValue);
    assertNull(userInfoModel);
  }

  @Test
  public void testValidateTokenFailedWhenINvalidJson() throws Exception {
    String authenticationHeaderValue = "hAGDFBCVASGDFAS6534872TAFHGDAJcJKFVDBCV";
    mockRestTemplate(authenticationHeaderValue, "invalidJson", HttpStatus.OK);
    UserInfoModel userInfoModel = googleTokenValidationService.validateToken(authenticationHeaderValue);
    assertNull(userInfoModel);
  }


  private void mockRestTemplate(String token, String expectedRespJson, HttpStatus statusCode)
      throws URISyntaxException {
    ResponseEntity<String> resp = new ResponseEntity<String>(expectedRespJson, statusCode);
    when(restTemplate.exchange(Matchers.any(URI.class), Matchers.any(HttpMethod.class),
        Matchers.<HttpEntity<?>>any(), Matchers.<Class<String>>any())).thenReturn(resp);

  }


}
