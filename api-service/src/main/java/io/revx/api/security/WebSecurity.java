package io.revx.api.security;

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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import io.revx.api.constants.ApiConstant;
import io.revx.api.service.UrgAuthenticationService;
import io.revx.core.RestAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

  protected static Logger logger = LogManager.getLogger(WebSecurity.class);

  @Autowired
  RoleHierarchy roleHierarchy;

  @Autowired
  ApplicationContext applicationContext;

  @Autowired
  UrgAuthenticationService urgAuthenticationService;

  @Autowired
  RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors().and().exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
        .and().csrf().disable().authorizeRequests().antMatchers(getPermittedUrls()).permitAll()
        .expressionHandler(webExpressionHandler()).anyRequest().authenticated().and()
        .addFilter(new ApiAuthorizationFilter(getAuthenticationManager(), urgAuthenticationService))
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  public String[] getPermittedUrls() {
    String[] arr = {"/swagger*/**", "/webjars/**", "/csrf/**", "/v2/api-docs/**", "/error/**",
        ApiConstant.THEME_API_BASE + "/**", ApiConstant.LOGGING_API + "/**",
        ApiConstant.DICTIONARY + "/**", "/actuator/**", ApiConstant.AUDIENCE_SYNC,
            ApiConstant.SMART_CACHING, ApiConstant.APPSFLYER_AUDIENCE + "/**",  ApiConstant.VALIDATE_AUTH};
    return arr;
  }

  @Bean
  AuthenticationManager getAuthenticationManager() throws Exception {
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
}
