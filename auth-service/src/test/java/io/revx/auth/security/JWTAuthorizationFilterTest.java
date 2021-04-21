/**
 * 
 */
package io.revx.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import javax.servlet.FilterChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.auth.constants.ApiConstant;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.service.BaseTestService;
import io.revx.auth.utils.UserUtils;
import io.revx.core.enums.RoleName;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class JWTAuthorizationFilterTest extends BaseTestService {
  @Mock
  private AuthenticationManager authManager;


  @Mock
  private JwtTokenProvider jwtTokenProvider;
  @InjectMocks
  private JWTAuthorizationFilter jWTAuthorizationFilter;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {

    MockitoAnnotations.initMocks(this);
    securityConstants = new SecurityConstants();
    jwtTokenProvider = new JwtTokenProvider();
    jwtTokenProvider.logoutCacheHolder = logoutCacheHolder;
    jwtTokenProvider.securityConstants = securityConstants;
    SecurityContextHolder.clearContext();
    authManager = mock(AuthenticationManager.class);
    jWTAuthorizationFilter = new JWTAuthorizationFilter(authManager, jwtTokenProvider);


  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JWTAuthorizationFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}.
   */
  @Test
  public void testDoFilterInternalSuccess() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    UserInfoModel userFromDb =
        TestDataGenerator.getUserInfoModel(loginReq.getUsername(), "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(loginReq.getUsername());
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.GET.name());
    request.setServletPath(ApiConstant.USER_INFO);
    request.addHeader(SecurityConstants.AUTH_HEADER, token);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthorizationFilter.doFilterInternal(request, c, chain);
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterInternalInvalidToken() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    UserInfoModel userFromDb =
        TestDataGenerator.getUserInfoModel(loginReq.getUsername(), "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(loginReq.getUsername());
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.GET.name());
    request.setServletPath(ApiConstant.USER_INFO);
    request.addHeader(SecurityConstants.AUTH_HEADER, token + "dhash");
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthorizationFilter.doFilterInternal(request, c, chain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterInternalFailedNoToken() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.GET.name());
    request.setServletPath(ApiConstant.USER_INFO);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthorizationFilter.doFilterInternal(request, c, chain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }


  @Test
  public void testDoFilterInternalExpiredToken() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    UserInfoModel userFromDb =
        TestDataGenerator.getUserInfoModel(loginReq.getUsername(), "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(loginReq.getUsername());
    long time = jwtTokenProvider.securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS();
    jwtTokenProvider.securityConstants.setAccessTokenValidity(-1);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    jwtTokenProvider.securityConstants.setAccessTokenValidity(time / 60 / 60);
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.GET.name());
    request.setServletPath(ApiConstant.USER_INFO);
    request.addHeader(SecurityConstants.AUTH_HEADER, token);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthorizationFilter.doFilterInternal(request, c, chain);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
