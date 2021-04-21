/**
 * 
 */
package io.revx.auth.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.utils.LogoutCacheHolder;
import io.revx.auth.utils.UserUtils;
import io.revx.core.enums.RoleName;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */
public class LoginLogoutServiceTest {
  @Mock
  private JwtTokenProvider jwtTokenProvider;


  @Mock
  private LogoutCacheHolder logoutCacheHolder;

  @InjectMocks
  private LoginLogoutService loginLogoutService;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    jwtTokenProvider = new JwtTokenProvider();
    jwtTokenProvider.securityConstants = new SecurityConstants();
    jwtTokenProvider.logoutCacheHolder = logoutCacheHolder;
    loginLogoutService.jwtTokenProvider = jwtTokenProvider;
    loginLogoutService.logoutCacheHolder = logoutCacheHolder;
  }

  /**
   * Test method for {@link io.revx.auth.service.LoginLogoutService#logout(java.lang.String)}.
   */
  @Test
  public void testLogout() throws Exception {
    String username = "akhilesh";
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
      }
    }
    for (String randomToken : tokens) {
      ApiResponseObject<Boolean> resp = loginLogoutService.logout(randomToken);
      assertNotNull(resp);
      assertTrue(resp.getRespObject());
    }
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.LoginLogoutService#logoutUser(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testLogoutUser() throws Exception {
    String username = "akhileshSa";
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    String masterToken = jwtTokenProvider.generateMasterToken(userFromDb);
    ApiResponseObject<Boolean> resp = loginLogoutService.logoutUser(masterToken, username);
    assertNotNull(resp);
    assertTrue(resp.getRespObject());
  }

}
