/**
 * 
 */
package io.revx.api.security;

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
import org.springframework.security.web.FilterInvocation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.cors.CorsConfigurationSource;
import io.revx.api.service.UrgAuthenticationService;
import io.revx.core.RestAuthenticationEntryPoint;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WebSecurityTest {

  ApplicationContext applicationContext;


  @Mock
  private RestAuthenticationEntryPoint restAuthenticationEntryPoint;


  @Mock
  private RoleHierarchy roleHierarchy;


  @Mock
  private UrgAuthenticationService urgAuthenticationService;
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
   * Test method for {@link io.revx.api.security.WebSecurity#corsConfigurationSource()}.
   */
  @Test
  public void testCorsConfigurationSource() throws Exception {
    CorsConfigurationSource rVoter = webSecurity.corsConfigurationSource();
    assertNotNull(rVoter);
  }

  /**
   * Test method for {@link io.revx.api.security.WebSecurity#roleHierarchyVoter()}.
   */
  @Test
  public void testRoleHierarchyVoter() throws Exception {
    RoleHierarchyVoter rVoter = webSecurity.roleHierarchyVoter();
    assertNotNull(rVoter);
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
