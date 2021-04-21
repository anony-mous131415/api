package io.revx.auth.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import io.revx.auth.constants.LDapConstants;
import io.revx.auth.service.UserDetailsServiceImpl;
import io.revx.auth.service.UserService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.RestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  private static Logger logger = LogManager.getLogger(WebSecurity.class);

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  UserService userService;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Autowired
  RoleHierarchy roleHierarchy;

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  Md5PasswordEncoder md5PasswordEncoder;

  @Autowired
  LDapConstants lDapConstants;

  @Autowired
  ApiErrorCodeResolver apiErrorCodeResolver;

  @Autowired
  RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint()).and()
        .csrf().disable().authorizeRequests().antMatchers(getPermittedUrls()).permitAll()
        .expressionHandler(webExpressionHandler()).anyRequest().authenticated().and()
        .addFilterBefore(new JWTAuthenticationFilter(getAuthenticationManager(), jwtTokenProvider,
            userService, apiErrorCodeResolver), UsernamePasswordAuthenticationFilter.class)
        .addFilter(new JWTAuthorizationFilter(getAuthenticationManager(), jwtTokenProvider))
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  public String[] getPermittedUrls() {
    String[] arr = {"/actuator*/**", "/swagger*/**", "/webjars/**", "/csrf/**", "/v2/api-docs/**",
        "/error/**", "/v2/auth/**"};
    return arr;
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    // auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    auth.authenticationProvider(authenticationProvider());
    if (lDapConstants.getLdapAuthEnabled()) {
      logger.debug(
          ">>>>> [LDAP] Authentication Provider enabled: " + lDapConstants.getLdapAuthEnabled());
      auth.ldapAuthentication().userDnPatterns(lDapConstants.getLdapUsersDnPattern())
          .groupSearchBase(lDapConstants.getLdapGroupsSearchBase()).contextSource()
          .url(lDapConstants.getLdapAuthUrl()).and().passwordCompare()
          .passwordAttribute(lDapConstants.getLdapUserPassSecretAttribute());
    }
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider impl = new DaoAuthenticationProvider();
    impl.setUserDetailsService(userDetailsService);
    impl.setPasswordEncoder(md5PasswordEncoder);
    impl.setHideUserNotFoundExceptions(false);
    return impl;
  }

  @Bean
  public AuthenticationManager getAuthenticationManager() throws Exception {
    return authenticationManager();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
    return source;
  }

  public SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
    DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler =
        new DefaultWebSecurityExpressionHandler();
    defaultWebSecurityExpressionHandler.setApplicationContext(applicationContext);
    defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy);
    return defaultWebSecurityExpressionHandler;
  }

  @Bean
  public RoleHierarchyVoter roleHierarchyVoter() {
    return new RoleHierarchyVoter(roleHierarchy);
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return restAuthenticationEntryPoint;
  }
}
