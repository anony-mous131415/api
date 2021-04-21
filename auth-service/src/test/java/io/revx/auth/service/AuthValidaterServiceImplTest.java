/**
 * 
 */
package io.revx.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.requests.PasswordChangeRequest;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.utils.UserUtils;
import io.revx.core.enums.RoleName;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthValidaterServiceImplTest extends BaseTestService {


  @Mock
  protected UserService userService;

  @InjectMocks
  private AuthValidaterServiceImpl authValidaterServiceImpl;



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
    authValidaterServiceImpl.jwtTokenProvider = jwtTokenProvider;
    loginLogoutService = new LoginLogoutService();
    loginLogoutService.jwtTokenProvider = jwtTokenProvider;
    loginLogoutService.logoutCacheHolder = logoutCacheHolder;
    userService.licenseeRepository = licenseeRepository;
    userService.licenseeUserRoleRepo = licenseeUserRoleRepo;
    userService.userRepository = userRepository;
    userService.md5PasswordEncoder = md5PasswordEncoder;
    authValidaterServiceImpl.loginLogoutService = loginLogoutService;
    authValidaterServiceImpl.md5PasswordEncoder = md5PasswordEncoder;

  }

  /**
   * Test method for
   * {@link io.revx.auth.service.AuthValidaterServiceImpl#validateToken(java.lang.String)}.
   */
  @Test
  public void testValidateToken() throws Exception {
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
    UserInfo ui = authValidaterServiceImpl.validateToken(token);
    ApiResponseObject<UserInfo> resp = new ApiResponseObject<>();
    resp.setRespObject(ui);
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertNull(" error Object Should be null ", resp.getError());
    assertNotNull(resp.getRespObject().getUsername());
    assertThat(resp.getRespObject().getUsername()).isEqualToIgnoringCase(username);
  }

  @Test
  public void testValidateTokenFailed() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo) + "Failed";
    exceptionRule.expect(ApiException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_ACCESS_TOKEN.name());
    authValidaterServiceImpl.validateToken(token);
  }

  @Test
  public void testValidateTokenExpiredFailed() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    long tokenValidity = securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS();
    securityConstants.setAccessTokenValidity(-1);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    securityConstants.setAccessTokenValidity(tokenValidity);
    exceptionRule.expect(ApiException.class);
    exceptionRule.expectMessage(ErrorCode.ACCESS_TOKEN_EXPIRED.name());
    authValidaterServiceImpl.validateToken(token);
  }


  /**
   * Test method for
   * {@link io.revx.auth.service.AuthValidaterServiceImpl#switchLicensee(java.lang.Long, java.lang.String)}.
   */

  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testSwitchLicensee() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    String masterToken = jwtTokenProvider.generateMasterToken(userFromDb);


    ApiResponseObject<TokenResponse> resp =
        authValidaterServiceImpl.switchLicensee(33l, masterToken);
    assertNotNull(resp);
    assertNotNull(resp.getRespObject());
    assertNull(" error Object Should be null ", resp.getError());
    assertNotNull(resp.getRespObject().getUsername());
    assertNotNull(resp.getRespObject().getToken());
    assertThat(resp.getRespObject().getUsername()).isEqualToIgnoringCase(username);

  }

  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testSwitchLicenseeFailed() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    String masterToken = jwtTokenProvider.generateMasterToken(userFromDb);
    exceptionRule.expect(ApiException.class);
    exceptionRule.expectMessage(ErrorCode.USER_DONT_HAVE_ACCESS_ON_GIVEN_LICENSEE_ERROR.name());
    authValidaterServiceImpl.switchLicensee(758l, masterToken);
  }

  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testSwitchLicenseeFailedWrongToken() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    String masterToken = jwtTokenProvider.generateMasterToken(userFromDb);
    authValidaterServiceImpl.logout(masterToken);
    exceptionRule.expect(ApiException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_ACCESS_TOKEN.name());
    authValidaterServiceImpl.switchLicensee(33l, masterToken);
  }

  @Override
  protected void makeMock(String username, int userRoleCount, int activeLicenseeCount,
      boolean isSuperAdmin) {
    super.makeMock(username, userRoleCount, activeLicenseeCount, isSuperAdmin);
    List<LicenseeUserRolesEntity> licenseeUserRolesEntityList = TestDataGenerator
        .getListOfObject(isSuperAdmin ? 1 : userRoleCount, LicenseeUserRolesEntity.class);
    for (LicenseeUserRolesEntity licenseeUserRolesEntity : licenseeUserRolesEntityList) {
      licenseeUserRolesEntity.setUserId(12345);
      licenseeUserRolesEntity.setLicenseeEntity(TestDataGenerator.getLicenseeEntity(33));;
      if (isSuperAdmin) {
        licenseeUserRolesEntity.setLicenseeEntity(null);
      }
    }
    UserInfoEntity obj = TestDataGenerator.getUserEntityObject(username);

    when(userService.isValidForLicenseeSwitch(username, 33l))
        .thenReturn(licenseeUserRolesEntityList);
    when(userService.findByUsername(username, true)).thenReturn(obj);
    when(userService.findByUsername(username)).thenReturn(obj);
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.AuthValidaterServiceImpl#changePassword(io.revx.auth.requests.PasswordChangeRequest, java.lang.String)}.
   */
  @Test
  @WithMockUser(username = "akhileshTest", authorities = {"ROLE_RW"})
  public void testChangePassword() throws Exception {
    String username = "akhileshTest";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
    passwordChangeRequest.setUsername(username);
    passwordChangeRequest.setOldPassword("pass");
    passwordChangeRequest.setNewPassword("NewPass");
    logger.debug(passwordChangeRequest);
    ApiResponseObject<Boolean> resp =
        authValidaterServiceImpl.changePassword(passwordChangeRequest, token);
    assertNotNull(resp);
    assertTrue(resp.getRespObject());

  }

  @Test
  @WithMockUser(username = "akhileshTest", authorities = {"ROLE_RW"})
  public void testChangePasswordWithoutOldPAss() throws Exception {
    String username = "akhileshTest";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
    passwordChangeRequest.setUsername(username);
    passwordChangeRequest.setNewPassword("NewPass");
    ApiResponseObject<Boolean> resp =
        authValidaterServiceImpl.changePassword(passwordChangeRequest, token);
    assertNotNull(resp);
    assertTrue(resp.getRespObject());

  }

  @Test
  @WithMockUser(username = "akhileshTest", authorities = {"ROLE_RW"})
  public void testChangePasswordWithoutOldPAssWrong() throws Exception {
    String username = "akhileshTest";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
    passwordChangeRequest.setUsername(username);
    passwordChangeRequest.setOldPassword("OladPAssWrong");
    passwordChangeRequest.setNewPassword("NewPass");
    ApiResponseObject<Boolean> resp =
        authValidaterServiceImpl.changePassword(passwordChangeRequest, token);
    assertNotNull(resp);
    assertFalse(resp.getRespObject());
  }

  @Test
  @WithMockUser(username = "akhileshTest", authorities = {"ROLE_RW"})
  public void testChangePasswordFailedWrongUser() throws Exception {
    String username = "akhileshTest";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
    passwordChangeRequest.setUsername(username + "failed");
    passwordChangeRequest.setOldPassword("OladPAssWrong");
    passwordChangeRequest.setNewPassword("NewPass");
    exceptionRule.expect(ApiException.class);
    exceptionRule.expectMessage(ErrorCode.INVALID_ACCESS_TOKEN.name());
    authValidaterServiceImpl.changePassword(passwordChangeRequest, token);

  }

  @Test
  @WithMockUser(username = "akhileshTest", authorities = {"ROLE_RW"})
  public void testChangePasswordFailedExpiredToken() throws Exception {
    String username = "akhileshTest";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    long tokenValidity = securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS();
    securityConstants.setAccessTokenValidity(-1);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    securityConstants.setAccessTokenValidity(tokenValidity);
    PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
    passwordChangeRequest.setUsername(username + "failed");
    passwordChangeRequest.setOldPassword("OladPAssWrong");
    passwordChangeRequest.setNewPassword("NewPass");
    exceptionRule.expect(ApiException.class);
    exceptionRule.expectMessage(ErrorCode.ACCESS_TOKEN_EXPIRED.name());
    authValidaterServiceImpl.changePassword(passwordChangeRequest, token);

  }

  /**
   * Test method for {@link io.revx.auth.service.AuthValidaterServiceImpl#logout(java.lang.String)}.
   */
  @Test
  public void testLogout() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    List<String> tokens = new ArrayList<String>();
    for (int i = 0; i < 4; i++) {
      tokens.add(jwtTokenProvider.generateAccessToken(uInfo));
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        logger.error(e);
      }
    }
    String randomToken = tokens.get(new Random().nextInt(tokens.size()));
    logger.info("randomToken : " + randomToken);
    ApiResponseObject<Boolean> resp = authValidaterServiceImpl.logout(randomToken);
    assertNotNull(resp);
    assertTrue(resp.getRespObject());
    // vAlidating All token After Logout Only One Should Logout Other Should not
    for (String tok : tokens) {
      logger.info("tok : " + tok);
      UserInfo ui = authValidaterServiceImpl.validateToken(tok);
      ApiResponseObject<UserInfo> respvalidate = new ApiResponseObject<>();
      respvalidate.setRespObject(ui);
      logger.info(respvalidate);
      assertNotNull(respvalidate);
      assertNotNull(respvalidate.getRespObject());
      assertNull(" error Object Should be null ", respvalidate.getError());
      assertNotNull(respvalidate.getRespObject().getUsername());
      assertThat(respvalidate.getRespObject().getUsername()).isEqualToIgnoringCase(username);

    }
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.AuthValidaterServiceImpl#logoutUser(java.lang.String, java.lang.String)}.
   */

  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_SADMIN"})
  public void testLogoutUser() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    String masterToken = jwtTokenProvider.generateMasterToken(userFromDb);
    logger.debug("Auth :" + SecurityContextHolder.getContext().getAuthentication());
    authValidaterServiceImpl.logoutUser(masterToken, username);
    logger.debug("After Auth :" + SecurityContextHolder.getContext().getAuthentication());
  }
}
