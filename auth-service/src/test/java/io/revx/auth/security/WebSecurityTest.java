/**
 * 
 */
package io.revx.auth.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.cors.CorsConfigurationSource;
import io.revx.auth.constants.LDapConstants;
import io.revx.auth.service.UserDetailsServiceImpl;
import io.revx.auth.service.UserService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.RestAuthenticationEntryPoint;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WebSecurityTest {
  @Mock
  private ApiErrorCodeResolver apiErrorCodeResolver;


  @Mock
  private JwtTokenProvider jwtTokenProvider;


  @Mock
  private LDapConstants lDapConstants;


  @Mock
  private Md5PasswordEncoder md5PasswordEncoder;


  @Mock
  private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  ApplicationContext applicationContext;

  @Mock
  private RoleHierarchy roleHierarchy;


  @Mock
  private UserDetailsServiceImpl userDetailsService;


  @Mock
  private UserService userService;
  @InjectMocks
  private WebSecurity webSecurity;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    applicationContext = mock(StaticWebApplicationContext.class);
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.WebSecurity#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}.
   */
  @Test
  public void testConfigureHttpSecurity() throws Exception {
    new RuntimeException("Not Required ");
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.WebSecurity#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)}.
   */
  @Test
  public void testConfigureAuthenticationManagerBuilder() throws Exception {
    AuthenticationManagerBuilder builder = mock(AuthenticationManagerBuilder.class);
    lDapConstants.setLdapAuthEnabled(true);
    webSecurity.configure(builder);
  }

  /**
   * Test method for {@link io.revx.auth.security.WebSecurity#authenticationProvider()}.
   */
  @Test
  public void testAuthenticationProvider() throws Exception {
    AuthenticationProvider ap = webSecurity.authenticationProvider();
    assertNotNull(ap);

  }

  /**
   * Test method for {@link io.revx.auth.security.WebSecurity#getAuthenticationManager()}.
   */
  @Test
  public void testGetAuthenticationManager() throws Exception {
    new RuntimeException("Not Required ");
  }

  /**
   * Test method for {@link io.revx.auth.security.WebSecurity#corsConfigurationSource()}.
   */
  @Test
  public void testCorsConfigurationSource() throws Exception {
    CorsConfigurationSource rVoter = webSecurity.corsConfigurationSource();
    assertNotNull(rVoter);
  }

  /**
   * Test method for {@link io.revx.auth.security.WebSecurity#roleHierarchyVoter()}.
   */
  @Test
  public void testRoleHierarchyVoter() throws Exception {
    RoleHierarchyVoter rVoter = webSecurity.roleHierarchyVoter();
    assertNotNull(rVoter);
  }

  /**
   * Test method for {@link io.revx.auth.security.WebSecurity#authenticationEntryPoint()}.
   */
  @Test
  public void testAuthenticationEntryPoint() throws Exception {
    AuthenticationEntryPoint entryPoint = webSecurity.authenticationEntryPoint();
    assertNotNull(entryPoint);
  }

  @Test
  public void testWebExpressionHandler() {
    webSecurity.applicationContext = applicationContext;
    webSecurity.roleHierarchy = roleHierarchy;
    SecurityExpressionHandler<FilterInvocation> bean = webSecurity.webExpressionHandler();
    assertNotNull(bean);
  }

  @Test
  public void testPermittedUrls() {
    String[] urls = webSecurity.getPermittedUrls();
    assertNotNull(urls);
    assertThat(urls.length).isGreaterThan(1);
  }

}
