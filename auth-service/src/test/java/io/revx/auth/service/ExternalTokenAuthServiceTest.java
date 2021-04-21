/**
 * 
 */
package io.revx.auth.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.utils.UserUtils;
import io.revx.core.enums.RoleName;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ExternalTokenAuthServiceTest extends BaseTestService {
  @Mock
  private FacebookTokenValidationService facebookTokenValidationService;


  @Mock
  private GoogleTokenValidationService googleTokenValidationService;


  @Mock
  private MicrosoftTokenValidationService microsoftTokenValidationService;


  @Mock
  private UserDetailsServiceImpl userDetailsService;


  @Mock
  private UserService userService;

  @InjectMocks
  private ExternalTokenAuthService externalTokenAuthService;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    super.setup();
    MockitoAnnotations.initMocks(this);
    jwtTokenProvider = new JwtTokenProvider();
    jwtTokenProvider.securityConstants = securityConstants;
    jwtTokenProvider.logoutCacheHolder = logoutCacheHolder;
    externalTokenAuthService.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.ExternalTokenAuthService#getAccessTokenFromExternalToken(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testGetAccessTokenFromExternalTokenGoogleSuccess() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(googleTokenValidationService.validateToken(token)).thenReturn(userFromDb);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    ApiResponseObject<TokenResponse> resp =
        externalTokenAuthService.getAccessTokenFromExternalToken("google", token);
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertNotNull(resp.getRespObject().getToken());
  }


  @Test
  public void testGetAccessTokenFromExternalTokenGoogleFailed() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    exceptionRule.expect(UserUnAuthenticateException.class);
    externalTokenAuthService.getAccessTokenFromExternalToken("google", token);
  }


  @Test
  public void testGetAccessTokenFromExternalTokenGoogleFailedByDbUser() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(googleTokenValidationService.validateToken(token)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    exceptionRule.expect(UserUnAuthenticateException.class);
    externalTokenAuthService.getAccessTokenFromExternalToken("google", token);
  }

  public void testGetAccessTokenFromExternalTokenFacebookSuccess() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(facebookTokenValidationService.validateToken(token)).thenReturn(userFromDb);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    ApiResponseObject<TokenResponse> resp =
        externalTokenAuthService.getAccessTokenFromExternalToken("facebook", token);
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertNotNull(resp.getRespObject().getToken());
  }


  @Test
  public void testGetAccessTokenFromExternalTokenFacebookFailed() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    exceptionRule.expect(UserUnAuthenticateException.class);
    externalTokenAuthService.getAccessTokenFromExternalToken("facebook", token);
  }


  @Test
  public void testGetAccessTokenFromExternalTokenFacebookFailedByDbUser() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(facebookTokenValidationService.validateToken(token)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    exceptionRule.expect(UserUnAuthenticateException.class);
    externalTokenAuthService.getAccessTokenFromExternalToken("facebook", token);
  }



  public void testGetAccessTokenFromExternalTokenOfficeSuccess() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(microsoftTokenValidationService.validateToken(token)).thenReturn(userFromDb);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    ApiResponseObject<TokenResponse> resp =
        externalTokenAuthService.getAccessTokenFromExternalToken("office", token);
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertNotNull(resp.getRespObject().getToken());
  }


  @Test
  public void testGetAccessTokenFromExternalTokenOfficeFailed() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(userDetailsService.loadUserByUsername(username)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    exceptionRule.expect(UserUnAuthenticateException.class);
    externalTokenAuthService.getAccessTokenFromExternalToken("office", token);
  }


  @Test
  public void testGetAccessTokenFromExternalTokenOfficeFailedByDbUser() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    when(microsoftTokenValidationService.validateToken(token)).thenReturn(userFromDb);
    when(userService.getUserInfoIfEligible(userFromDb)).thenReturn(uInfo);
    exceptionRule.expect(UserUnAuthenticateException.class);
    externalTokenAuthService.getAccessTokenFromExternalToken("office", token);
  }


}
