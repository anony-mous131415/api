package io.revx.auth.security;

import static io.revx.auth.constants.SecurityConstants.AUTH_HEADER;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ErrorCode;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

  private static Logger logger = LogManager.getLogger(JWTAuthorizationFilter.class);

  private final JwtTokenProvider jwtTokenProvider;

  public JWTAuthorizationFilter(AuthenticationManager authManager,
      JwtTokenProvider jwtTokenProvider) {
    super(authManager);
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain) throws IOException, ServletException {
    try {
      ThreadContext.put(Constants.URID_KEY, RandomStringUtils.randomAlphanumeric(32));
      String accessToken = jwtTokenProvider.getTokenFromRequest(req);
      if (StringUtils.isBlank(accessToken)) {
        logger.debug("{} is Missing in header.", AUTH_HEADER);
        chain.doFilter(req, res);
        return;
      }

      UsernamePasswordAuthenticationToken authentication = getAuthentication(accessToken);
      logger.debug(" doFilterInternal : authentication : " + authentication);
      if (authentication != null)
        SecurityContextHolder.getContext().setAuthentication(authentication);
      chain.doFilter(req, res);

    } finally {
      ThreadContext.clearMap();
    }
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String accessToken) {
    String username = null;
    try {
      username = jwtTokenProvider.getUsernameFromToken(accessToken);
      logger.debug("Validaating Token Against  :" + username);
    } catch (ExpiredJwtException e) {
      logger.debug("the token is expired and not valid anymore" + e);
      ThreadContext.put(Constants.ERROR_CODE,
          String.valueOf(ErrorCode.ACCESS_TOKEN_EXPIRED.getValue()));
    } catch (Exception e) {
      logger.debug("Token is Not Valid");
      ThreadContext.put(Constants.ERROR_CODE,
          String.valueOf(ErrorCode.INVALID_ACCESS_TOKEN.getValue()));
    }
    ThreadContext.put(Constants.USER_NAME, username);
    if (StringUtils.isNotBlank(username)
        && SecurityContextHolder.getContext().getAuthentication() == null) {
      if (jwtTokenProvider.validateToken(accessToken)) {
        return jwtTokenProvider.getAuthentication(accessToken);
      }
    }
    return null;
  }
}
