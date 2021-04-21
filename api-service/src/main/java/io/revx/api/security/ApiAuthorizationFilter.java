package io.revx.api.security;

import io.revx.api.service.UrgAuthenticationService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.UserInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static io.revx.api.security.SecurityConstants.HEADER_STRING;

@Component
public class ApiAuthorizationFilter extends BasicAuthenticationFilter {

  private static Logger logger = LogManager.getLogger(ApiAuthorizationFilter.class);

  private UrgAuthenticationService urgAuthenticationService;

  ApiAuthorizationFilter(AuthenticationManager authManager,
      UrgAuthenticationService urgAuthenticationService) {
    super(authManager);
    this.urgAuthenticationService = urgAuthenticationService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain) throws IOException, ServletException {
    try {
      ThreadContext.put(Constants.URID_KEY, getUniqRespId(req));
      String header = req.getHeader(HEADER_STRING);
      UserInfo ui = getUserInfoFromUrg(header);
      if (ui == null) {
        logger.info("Not a valid token. Filtering the request.");
        chain.doFilter(req, res);
        return;
      }
      ThreadContext.put(Constants.USER_NAME, ui.getUsername());
      UsernamePasswordAuthenticationToken authentication = getAuthentication(ui);
      logger.debug(" doFilterInternal : authentication : " + authentication);
      if (authentication != null)
        SecurityContextHolder.getContext().setAuthentication(authentication);
      logger.info("doFilterInternal :" + SecurityContextHolder.getContext().getAuthentication());
      chain.doFilter(req, res);
    } finally {
      ThreadContext.remove(Constants.URID_KEY);
      ThreadContext.remove(Constants.USER_NAME);
    }
  }

  private String getUniqRespId(HttpServletRequest req) {
    if (StringUtils.isNotBlank(req.getHeader(SecurityConstants.REQUEST_ID))) {
      return req.getHeader(SecurityConstants.REQUEST_ID);
    }
    return RandomStringUtils.randomAlphanumeric(32);
  }

  public UserInfo getUserInfoFromUrg(String jwtToken) {
    if (StringUtils.isBlank(jwtToken))
      return null;
    try {
      UserInfo ui = urgAuthenticationService.authenticate(jwtToken);
      logger.info("UserInfo :" + ui);
      return ui;
    } catch (UserUnAuthenticateException e) {
      logger.error("UserUnAuthenticateException :" + e);
      ThreadContext.put(Constants.ERROR_CODE, String.valueOf(e.getErrorCode().getValue()));
      // throw e;
    }
    return null;
  }

  public UsernamePasswordAuthenticationToken getAuthentication(UserInfo userInfo) {
    UsernamePasswordAuthenticationToken upa = new UsernamePasswordAuthenticationToken(userInfo, "",
        getAuthority(userInfo.getAuthorities()));
    return upa;
  }

  public Collection<? extends GrantedAuthority> getAuthority(Set<String> roles) {
    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    for (String role : roles) {
      authorities.add(new SimpleGrantedAuthority(role));
    }
    return authorities;
  }
}
