package io.revx.core;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.response.ApiResponseObject;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private static Logger logger = LogManager.getLogger(RestAuthenticationEntryPoint.class);

  @Autowired
  private ApiErrorCodeResolver apiErrorCodeResolver;

  private static ObjectMapper mapper = new ObjectMapper();

  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authenticationException) throws IOException, ServletException {
    logger.debug("commence : {} ", ExceptionUtils.getStackTrace(authenticationException));
    ApiResponseObject<Object> apiResp = new ApiResponseObject<Object>();
    int errorCode = -1;
    try {
      errorCode = Integer.parseInt(ThreadContext.get(Constants.ERROR_CODE));
    } catch (Exception e) {
      errorCode = -1;
    }
    if (errorCode > 0) {
      apiResp.setError(apiErrorCodeResolver.getErrorCode(ErrorCode.parseFrom(errorCode)));
    } else {
      apiResp.setError(apiErrorCodeResolver.getErrorCode(request, authenticationException));
    }
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
   // BeanDeserializer
    response.getWriter().write(mapper.writeValueAsString(apiResp));
  }
}
