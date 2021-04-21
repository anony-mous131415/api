package io.revx.core;

import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.exception.ValidationException;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.Error;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LogManager.getLogger(CommonExceptionHandler.class);

  @Autowired
  public ApiErrorCodeResolver apiErrorCodeResolver;

 @ExceptionHandler(Exception.class)
  public final ResponseEntity<ApiResponseObject<Object>> handleAllExceptions(Exception ex) {
    logger.error(ExceptionUtils.getStackTrace(ex));
    ApiResponseObject<Object> respObj = getErrorApiResponse();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respObj);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public final ResponseEntity<ApiResponseObject<Object>> handleAccessDeniedExceptions(
      AccessDeniedException ex, HttpServletRequest request) {
    logger.error(ExceptionUtils.getStackTrace(ex));
    logger.error("Type Of Request : {}" , request);
    ApiResponseObject<Object> respObj = getErrorApiResponse(ErrorCode.INVALID_ACCESS_TOKEN, ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respObj);
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public final ResponseEntity<ApiResponseObject<Object>> handleMissingRequestHeaderExceptions(
      MissingRequestHeaderException ex) {
    logger.error(ExceptionUtils.getStackTrace(ex));
    ApiResponseObject<Object> respObj = getErrorApiResponse(ErrorCode.MISSING_VARIABLE_ERROR, ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respObj);
  }

  @ExceptionHandler(UserUnAuthenticateException.class)
  public final ResponseEntity<ApiResponseObject<Object>> handleUnAutheticateExceptions(
      UserUnAuthenticateException ex) {
    logger.error(ExceptionUtils.getStackTrace(ex));
    ApiResponseObject<Object> respObj = getErrorApiResponse(ex);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respObj);
  }

  @ExceptionHandler(ApiException.class)
  public final ResponseEntity<ApiResponseObject<Object>> handleApiExceptions(ApiException ex) {
    logger.error(ExceptionUtils.getStackTrace(ex));
    ApiResponseObject<Object> respObj = getErrorApiResponse(ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respObj);
  }

  @ExceptionHandler(ValidationException.class)
  public final ResponseEntity<ApiResponseObject<Object>> handleValidationExceptions(
      ValidationException ex) {
    logger.error("handleValidationExceptions  {} ", ExceptionUtils.getStackTrace(ex));
    ApiResponseObject<Object> respObj = getErrorApiResponse(ex);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respObj);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
      request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
    }
    logger.info(" We Are here In error " + ExceptionUtils.getStackTrace(ex));
    ApiResponseObject<Object> respObj = getErrorApiResponse(ErrorCode.INTERNAL_SERVER_ERROR, ex);
    return new ResponseEntity<>(respObj, headers, status);
  }

  private ApiResponseObject<Object> getErrorApiResponse() {
    ApiResponseObject<Object> resp = new ApiResponseObject<Object>();
    return resp;
  }

  private ApiResponseObject<Object> getErrorApiResponse(ApiException ex) {
    return getErrorApiResponse(ex.getErrorCode(), ex.getFormatingParams());
  }

  private ApiResponseObject<Object> getErrorApiResponse(UserUnAuthenticateException ex) {
    return getErrorApiResponse(ex.getErrorCode(), ex.getFormatingParams());
  }

  private ApiResponseObject<Object> getErrorApiResponse(ErrorCode errorCode, Object... params) {
    ApiResponseObject<Object> resp = new ApiResponseObject<Object>();
    logger.info(" Inside getErrorApiResponse method errorCode : {} and message : ", errorCode, params);
    if (errorCode != null) {
      resp.setError(apiErrorCodeResolver.resolveErrorCode(errorCode.getValue(), params));
    }
    return resp;
  }

  private ApiResponseObject<Object> getErrorApiResponse(ErrorCode errorCode, Exception ex) {
    ApiResponseObject<Object> resp = new ApiResponseObject<Object>();
    resp.setError(new Error(errorCode.getValue(), ex.getMessage()));
    return resp;
  }

}
