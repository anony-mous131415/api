/**
 * 
 */
package io.revx.api.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.StaticWebApplicationContext;
import io.revx.api.service.UrgAuthenticationService;
import io.revx.core.RestAuthenticationEntryPoint;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WebSecurityTestConfig {

  ApplicationContext applicationContext;


  @Mock
  private RestAuthenticationEntryPoint restAuthenticationEntryPoint;


  @Mock
  private RoleHierarchy roleHierarchy;


  @Mock
  private UrgAuthenticationService urgAuthenticationService;
  @Mock
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
   * {@link io.revx.api.security.WebSecurity#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)}.
   */
  @Test
  @SuppressWarnings("unchecked")
  public void testConfigure() throws Exception {
    webSecurity.applicationContext = applicationContext;
    webSecurity.roleHierarchy = roleHierarchy;
    AuthenticationManager auth = mock(AuthenticationManager.class);
    when(webSecurity.getAuthenticationManager()).thenReturn(auth);
    AuthenticationManagerBuilder builder = mock(AuthenticationManagerBuilder.class);
    ObjectPostProcessor<Object> objectPostProcessor = mock(ObjectPostProcessor.class);
    HttpSecurity http = new HttpSecurity(objectPostProcessor, builder,
        new HashMap<Class<? extends Object>, Object>());
    webSecurity.configure(http);
  }

}
