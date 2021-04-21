/**
 * 
 */
package io.revx.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.lang.reflect.Type;
import javax.servlet.FilterChain;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.revx.auth.constants.ApiConstant;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.service.BaseTestService;
import io.revx.auth.service.UserService;
import io.revx.auth.utils.UserUtils;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.enums.RoleName;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;


/**
 * @author amaurya
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class JWTAuthenticationFilterTest extends BaseTestService {

  @Mock
  private ApiErrorCodeResolver apiErrorCodeResolver;


  @Mock
  private AuthenticationManager authenticationManager;


  @Mock
  private JwtTokenProvider jwtTokenProvider;


  @Mock
  private UserService userService;

  @InjectMocks
  private JWTAuthenticationFilter jWTAuthenticationFilter;



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
    apiErrorCodeResolver = new ApiErrorCodeResolver();
    SecurityContextHolder.clearContext();

    authenticationManager = mock(AuthenticationManager.class);
    jWTAuthenticationFilter = new JWTAuthenticationFilter(authenticationManager, jwtTokenProvider,
        userService, apiErrorCodeResolver);
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JWTAuthenticationFilter#JWTAuthenticationFilter(org.springframework.security.authentication.AuthenticationManager, io.revx.auth.security.JwtTokenProvider, io.revx.auth.service.UserService, io.revx.core.ApiErrorCodeResolver)}.
   */
  @Test
  public void testGettersSetters() {
    assertNotNull(authenticationManager);

  }

  @Test
  public void testJWTAuthenticationFilter() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JWTAuthenticationFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}.
   */
  @Test
  public void testAttemptAuthenticationSuccess() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), "akhilesh", "pass");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    Type collectionType = new TypeToken<ApiResponseObject<TokenResponse>>() {}.getType();
    ApiResponseObject<TokenResponse> apiResp = new Gson().fromJson(content, collectionType);
    assertNotNull(apiResp);
    TokenResponse resp = apiResp.getRespObject();
    assertNotNull(resp.getMasterToken());
    assertThat(resp.getUsername()).isEqualTo(loginReq.getUsername());
  }

  @Test
  public void testAttemptAuthenticationSuccessWithAccessToken() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), "akhilesh", "pass");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    UserInfoModel userFromDb =
        TestDataGenerator.getUserInfoModel(loginReq.getUsername(), "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(loginReq.getUsername());
    when(userService.getUserInfoIfEligible(Mockito.any(), Mockito.any())).thenReturn(uInfo);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    Type collectionType = new TypeToken<ApiResponseObject<TokenResponse>>() {}.getType();
    ApiResponseObject<TokenResponse> apiResp = new Gson().fromJson(content, collectionType);
    assertNotNull(apiResp);
    TokenResponse resp = apiResp.getRespObject();
    assertNotNull(resp.getMasterToken());
    assertThat(resp.getUsername()).isEqualTo(loginReq.getUsername());
    assertNotNull(resp.getToken());
  }

  @Test
  public void testAttemptAuthenticationFailedWithException() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), "akhilesh", "pass");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    UserInfoModel userFromDb =
        TestDataGenerator.getUserInfoModel(loginReq.getUsername(), "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(loginReq.getUsername());
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter =
        new JWTAuthenticationFilter(authenticationManager, null, userService, apiErrorCodeResolver);
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    assertTrue(StringUtils.isBlank(content));

  }

  @Test
  public void testAttemptAuthenticationFailedWrongUser() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), loginReq.getUsername() + "cABD",
        loginReq.getPassword());
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    Type collectionType = new TypeToken<ApiResponseObject<Object>>() {}.getType();
    ApiResponseObject<Object> apiResp = new Gson().fromJson(content, collectionType);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getError());
  }

  @Test
  public void testAttemptAuthenticationFailedWrongPassword() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    loginReq.setPassword("pass");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), loginReq.getUsername(),
        loginReq.getPassword() + "::jhsd");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    Type collectionType = new TypeToken<ApiResponseObject<Object>>() {}.getType();
    ApiResponseObject<Object> apiResp = new Gson().fromJson(content, collectionType);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getError());
  }

  @Test
  public void testAttemptAuthenticationFailedNoUserNopass() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    mockUser(loginReq.getUsername(), loginReq.getPassword(), loginReq.getUsername() + "test",
        loginReq.getPassword() + "::jhsd");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    Type collectionType = new TypeToken<ApiResponseObject<Object>>() {}.getType();
    ApiResponseObject<Object> apiResp = new Gson().fromJson(content, collectionType);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getError());
  }



  @Test
  public void testAttemptAuthenticationFailedNoUser() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setPassword("pass");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), loginReq.getUsername() + "test",
        loginReq.getPassword() + "::jhsd");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    Type collectionType = new TypeToken<ApiResponseObject<Object>>() {}.getType();
    ApiResponseObject<Object> apiResp = new Gson().fromJson(content, collectionType);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getError());
  }

  @Test
  public void testAttemptAuthenticationFailedNoPass() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), loginReq.getUsername() + "test",
        loginReq.getPassword() + "::jhsd");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent(new Gson().toJson(loginReq).getBytes());
    request.setContentType(MediaType.APPLICATION_JSON_VALUE);
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    jWTAuthenticationFilter.doFilter(request, c, chain);
    String content = c.getContentAsString();
    Type collectionType = new TypeToken<ApiResponseObject<Object>>() {}.getType();
    ApiResponseObject<Object> apiResp = new Gson().fromJson(content, collectionType);
    assertNotNull(apiResp);
    assertNotNull(apiResp.getError());
  }


  @Test
  public void testAttemptAuthenticationFailedException() throws Exception {
    UserLoginRequest loginReq = new UserLoginRequest();
    loginReq.setUsername("akhilesh");
    mockUser(loginReq.getUsername(), loginReq.getPassword(), loginReq.getUsername() + "test",
        loginReq.getPassword());
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setContent("this is a wrong Json".getBytes());
    request.setServletPath(ApiConstant.LOGIN_URL);
    // Test
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    exceptionRule.expect(Exception.class);
    jWTAuthenticationFilter.doFilter(request, c, chain);
  }


  private void mockUser(String loginUser, String loginPassword, String userName, String password) {
    if (StringUtils.equalsAnyIgnoreCase(loginUser, userName)
        && StringUtils.equals(password, loginPassword)) {
      UserInfoModel uim = new UserInfoModel(loginUser, loginPassword,
          AuthorityUtils.createAuthorityList("ROLE_RW"));
      Authentication rod = new UsernamePasswordAuthenticationToken(uim, password,
          AuthorityUtils.createAuthorityList("ROLE_RW"));
      when(authenticationManager.authenticate(Mockito.any())).thenReturn(rod);
    } else {
      when(authenticationManager.authenticate(Mockito.any()))
          .thenThrow(BadCredentialsException.class);
    }

  }

}
