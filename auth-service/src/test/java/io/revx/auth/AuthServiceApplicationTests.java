package io.revx.auth;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AuthServiceApplicationTests.Config.class)
@WebAppConfiguration
public class AuthServiceApplicationTests {

  static Logger logger = LogManager.getLogger(AuthServiceApplicationTests.class);


  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())
        .defaultRequest(get("/").accept(MediaType.TEXT_HTML)).build();
  }

  @Test
  public void requiresAuthentication() throws Exception {
    mvc.perform(get("/")).andExpect(status().isFound());
  }

  @Test
  public void httpBasicAuthenticationSuccess() throws Exception {
    mvc.perform(get("/secured/butnotfound").with(httpBasic("akhilesh", "pass")))
        .andExpect(status().isNotFound()).andExpect(authenticated().withUsername("akhilesh"));
  }

  @Test
  public void authenticationFailed() throws Exception {
    mvc.perform(formLogin().user("user").password("invalid")).andExpect(status().isFound())
        .andExpect(redirectedUrl("/login?error")).andExpect(unauthenticated());
  }

  @EnableWebSecurity
  @EnableWebMvc
  static class Config extends WebSecurityConfigurerAdapter {
    @Bean
    public UserDetailsService userDetailsService() {
      @SuppressWarnings("deprecation")
      UserDetails user = User.withDefaultPasswordEncoder().username("akhilesh").password("pass")
          .roles("RW").build();
      return new InMemoryUserDetailsManager(user);
    }

  }
}
