/**
 * 
 */
package io.revx.api.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import javax.servlet.FilterChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.service.UrgAuthenticationService;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ApiAuthorizationFilterTest {
  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UrgAuthenticationService urgAuthenticationService;
  @InjectMocks
  private ApiAuthorizationFilter apiAuthorizationFilter;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    SecurityContextHolder.clearContext();
    authenticationManager = mock(AuthenticationManager.class);
    apiAuthorizationFilter =
        new ApiAuthorizationFilter(authenticationManager, urgAuthenticationService);
  }

  /**
   * Test method for
   * {@link io.revx.api.security.ApiAuthorizationFilter#ApiAuthorizationFilter(org.springframework.security.authentication.AuthenticationManager, io.revx.api.service.UrgAuthenticationService)}.
   */
  @Test
  public void testApiAuthorizationFilter() throws Exception {
    assertNotNull(authenticationManager);
  }

  /**
   * Test method for
   * {@link io.revx.api.security.ApiAuthorizationFilter#doFilterInternal(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)}.
   */
  @Test
  public void testDoFilterInternalSuccess() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setServletPath(io.revx.api.constants.ApiConstant.DASHBOARD_LIST);
    request.addHeader(SecurityConstants.HEADER_STRING, "jadsjDG673452746HBHJ");
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    UserInfo info = TestDataGenerator.getUserInfo("akhilesh", false, false);
    when(urgAuthenticationService.authenticate(Mockito.anyString())).thenReturn(info);
    apiAuthorizationFilter.doFilterInternal(request, c, chain);
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterInternalFailed() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setServletPath(io.revx.api.constants.ApiConstant.DASHBOARD_LIST);
    request.addHeader(SecurityConstants.HEADER_STRING, "jadsjDG673452746HBHJ");
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    when(urgAuthenticationService.authenticate(Mockito.anyString())).thenReturn(null);
    apiAuthorizationFilter.doFilterInternal(request, c, chain);
    String content = c.getContentAsString();
    assertThat(content).isBlank();
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterInternalFailedTokenBlank() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setServletPath(io.revx.api.constants.ApiConstant.DASHBOARD_LIST);
    request.addHeader(SecurityConstants.HEADER_STRING, "");
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    when(urgAuthenticationService.authenticate(Mockito.anyString())).thenReturn(null);
    apiAuthorizationFilter.doFilterInternal(request, c, chain);
    String content = c.getContentAsString();
    assertThat(content).isBlank();
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  public void testDoFilterInternalFailedWhenException() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod(HttpMethod.POST.name());
    request.setServletPath(io.revx.api.constants.ApiConstant.DASHBOARD_LIST);
    request.addHeader(SecurityConstants.HEADER_STRING, "jadsjDG673452746HBHJ");
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    FilterChain chain = mock(FilterChain.class);
    MockHttpServletResponse c = new MockHttpServletResponse();
    when(urgAuthenticationService.authenticate(Mockito.anyString()))
        .thenThrow(new UserUnAuthenticateException(ErrorCode.ACCESS_TOKEN_EXPIRED));
    apiAuthorizationFilter.doFilterInternal(request, c, chain);
    String content = c.getContentAsString();
    assertThat(content).isBlank();
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
