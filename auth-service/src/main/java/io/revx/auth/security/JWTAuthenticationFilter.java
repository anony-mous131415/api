package io.revx.auth.security;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.revx.auth.constants.ApiConstant;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.service.UserService;
import io.revx.core.ApiErrorCodeResolver;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.Constants;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;

public class JWTAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
  private static Logger logger = LogManager.getLogger(JWTAuthenticationFilter.class);
  private static ObjectMapper mapper = new ObjectMapper();

  private final AuthenticationManager authenticationManager;

  private final JwtTokenProvider JwtTokenProvider;

  private UserService userService;

  private ApiErrorCodeResolver apiErrorCodeResolver;

  public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
      JwtTokenProvider JwtTokenProvider, UserService userService,
      ApiErrorCodeResolver apiErrorCodeResolver) {
    super(new AntPathRequestMatcher(ApiConstant.LOGIN_URL));
    this.authenticationManager = authenticationManager;
    this.JwtTokenProvider = JwtTokenProvider;
    this.userService = userService;
    this.apiErrorCodeResolver = apiErrorCodeResolver;
  }

  @Override
  @LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.LOGIN)
  public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
      throws AuthenticationException {
    try {
      ThreadContext.put(Constants.URID_KEY, RandomStringUtils.randomAlphanumeric(32));
      UserLoginRequest user =
          new ObjectMapper().readValue(req.getInputStream(), UserLoginRequest.class);
      logger.debug("Login User :" + user);
      if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
        throw new UserUnAuthenticateException(ErrorCode.USERNAME_OR_PASSWORD_SHOULD_NOT_BLANK);
      }
      UsernamePasswordAuthenticationToken uat = new UsernamePasswordAuthenticationToken(
          user.getUsername(), user.getPassword(), new ArrayList<>());
      uat.setDetails(user);
      Authentication auth = authenticationManager.authenticate(uat);
      return auth;
    } catch (AuthenticationException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      // ThreadContext.remove(SecurityConstants.URID_KEY);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain, Authentication auth) throws IOException, ServletException {
    try {
      final String token = JwtTokenProvider.generateMasterToken(auth);
      logger.debug(" authUser :" + auth.getPrincipal());
      UserInfoModel userFromdb = UserInfoModel.getFromAuth(auth);
      logger.debug(" userFromdb :" + userFromdb);
      res.setContentType("application/json");
      res.setCharacterEncoding("UTF-8");
      TokenResponse resp = new TokenResponse(userFromdb.getUsername(), token);
      UserLoginRequest loginUser = UserLoginRequest.getFromAuth(auth);
      UserInfo uip = userService.getUserInfoIfEligible(userFromdb, loginUser);
      if (uip != null) {    
        userService.populateAdvLicenseeMap(uip, userFromdb.getUserId());
        resp.setToken(JwtTokenProvider.generateAccessToken(uip));
      }
      ApiResponseObject<TokenResponse> apiResp = new ApiResponseObject<TokenResponse>();
      apiResp.setRespObject(resp);
      res.getWriter().write(mapper.writeValueAsString(apiResp));
    } catch (Exception e) {
      logger.error(e);
    } finally {
      ThreadContext.remove(Constants.URID_KEY);
    }
  }

  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException ex)
      throws IOException, ServletException {
    try {
      logger.debug(
          "Delegating to authentication failure handler " + ExceptionUtils.getStackTrace(ex));
      logger.debug("AuthenticationException ex " + ex);
      ApiResponseObject<Object> apiResp = new ApiResponseObject<Object>();
      apiResp.setError(apiErrorCodeResolver.getErrorCode(request, ex));
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.getWriter().write(mapper.writeValueAsString(apiResp));
    } finally {
      ThreadContext.remove(Constants.URID_KEY);
    }
  }
}
