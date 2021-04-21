/**
 * 
 */
package io.revx.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.core.enums.RoleName;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.model.Licensee;
import io.revx.core.response.ApiResponseObject;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserDetailsServiceImplTest extends BaseTestService {

  @Mock
  private UserService userService;


  @InjectMocks
  private UserDetailsServiceImpl userDetailsServiceImpl;



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
    userDetailsServiceImpl.jwtTokenProvider = jwtTokenProvider;
    userService.licenseeRepository = licenseeRepository;
    userService.licenseeUserRoleRepo = licenseeUserRoleRepo;
    userService.userRepository = userRepository;
    userDetailsServiceImpl.userService = userService;
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.UserDetailsServiceImpl#loadUserByUsername(java.lang.String)}.
   */
  @Test
  public void testLoadUserByUsername() throws Exception {
    makeMock("akhilesh", 1, 5, false);
    UserInfoEntity obj = userRepository.findByUsername("akhilesh");
    when(userService.findByUsername("akhilesh")).thenReturn(obj);
    UserInfoModel uim = userDetailsServiceImpl.loadUserByUsername("akhilesh");
    assertThat(uim.getUsername()).isEqualTo("akhilesh");
  }

  @Test
  public void testLoadUserByUsernameNotActiveFailed() throws Exception {
    makeMock("akhilesh", 1, 5, false);
    UserInfoEntity obj = userRepository.findByUsername("akhilesh");
    obj.setActive(false);
    when(userService.findByUsername("akhilesh")).thenReturn(obj);
    exceptionRule.expect(UserUnAuthenticateException.class);
    exceptionRule.expectMessage(ErrorCode.USER_NOT_ACTIVE_ERROR.name());
    userDetailsServiceImpl.loadUserByUsername("akhilesh");
  }

  @Test
  public void testLoadUserByUsernameNotFoundFailed() throws Exception {
    makeMock("akhilesh", 1, 5, false);
    UserInfoEntity obj = userRepository.findByUsername("akhilesh");
    when(userService.findByUsername("akhilesh")).thenReturn(obj);
    exceptionRule.expect(UserUnAuthenticateException.class);
    exceptionRule.expectMessage(ErrorCode.USER_NAME_NOT_FOUND_ERROR.name());
    userDetailsServiceImpl.loadUserByUsername("akhilesh11");
  }



  /**
   * Test method for
   * {@link io.revx.auth.service.UserDetailsServiceImpl#fetchUserPrivilige(java.lang.String)}.
   */
  @Test
  public void testFetchUserPrivilige() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, true);
    UserInfoEntity obj = userRepository.findByUsername("akhilesh");
    obj.setActive(true);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    String masterToken = jwtTokenProvider.generateMasterToken(userFromDb);
    when(userService.findByUsername("akhilesh")).thenReturn(obj);
    ApiResponseObject<Set<Licensee>> uim = userDetailsServiceImpl.fetchUserPrivilige(masterToken);
    assertThat(userFromDb.getUsername()).isEqualTo("akhilesh");
    assertNotNull(uim);
  }

  @Test
  public void testFetchUserPriviligeFailed() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, true);
    UserInfoEntity obj = userRepository.findByUsername("akhilesh");
    obj.setActive(true);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    long tokenValidity = securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS();
    securityConstants.setAccessTokenValidity(-1);
    String masterToken = jwtTokenProvider.generateMasterToken(userFromDb);
    securityConstants.setAccessTokenValidity(tokenValidity);
    when(userService.findByUsername("akhilesh123")).thenReturn(obj);
    ApiResponseObject<Set<Licensee>> uim = userDetailsServiceImpl.fetchUserPrivilige(masterToken);
    assertThat(userFromDb.getUsername()).isEqualTo("akhilesh");
    assertNotNull(uim);
    assertNotNull(uim.getError());
  }

}
