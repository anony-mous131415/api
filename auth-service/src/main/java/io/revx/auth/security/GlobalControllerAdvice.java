package io.revx.auth.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import io.revx.core.service.HandleDemoUserService;

@SuppressWarnings("rawtypes")
@ControllerAdvice
public class GlobalControllerAdvice implements ResponseBodyAdvice {


  @Autowired
  private HandleDemoUserService handleDemoUserService;


  private static Logger logger = LogManager.getLogger(GlobalControllerAdvice.class);

  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType,
      MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request,
      ServerHttpResponse response) {
    logger.debug("Final body {} ", body);
    /*
    if (handleDemoUserService.isDemoUser() && body instanceof ApiResponseObject<?>) {
      return handleDemoUserService.handleDemo(body);
    } */
    return body;
  }

}
