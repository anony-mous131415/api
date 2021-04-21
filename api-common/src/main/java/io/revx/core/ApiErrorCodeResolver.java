package io.revx.core;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.Error;

@Component
public class ApiErrorCodeResolver {

  private static Logger logger = LogManager.getLogger(ApiErrorCodeResolver.class);

  @Autowired
  private MessageSource messageSource;
  @Autowired
  private LocaleResolver localeResolver;

  private static String UNKNOWN_ERROR_MSG = "Unknown Error : %s";

  public Error resolveErrorCode(int errorCode, Object... formatingValues) {
    logger.debug("resolveErrorCode for :" + errorCode);
    if (formatingValues != null) {
      logger.debug("formatingValues :" + Arrays.toString(formatingValues));
    }
    Error error = new Error(errorCode);
    String errMsg = getErrorMsgForLocale(errorCode);
    if (formatingValues != null && formatingValues.length > 0) {
      try {
        errMsg = String.format(errMsg, formatingValues);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    error.setMessage(errMsg);
    return error;
  }

  public Error resolveErrorCode(int errorCode, HttpServletRequest request,
      Object... formatingValues) {
    logger.debug("resolveErrorCode for :" + errorCode);
    if (formatingValues != null) {
      logger.debug("formatingValues :" + Arrays.toString(formatingValues));
    }
    Error error = new Error(errorCode);
    String errMsg = "";
    try {
      errMsg = getErrorMsgForLocale(request, errorCode);
      if (StringUtils.isBlank(errMsg)) {
        errMsg = getErrorMsgForLocale(request, ErrorCode.DEFAULT_ERROR.getValue());
      }
      if (formatingValues != null && formatingValues.length > 0) {
        try {
          errMsg = String.format(errMsg, formatingValues);
        } catch (IllegalFormatException e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
    }
    error.setMessage(errMsg);
    return error;
  }

  private String getErrorMsgForLocale(HttpServletRequest request, int errorCode) {
    return messageSource.getMessage("err." + errorCode, null, UNKNOWN_ERROR_MSG,
        localeResolver.resolveLocale(request));
  }

  private String getErrorMsgForLocale(int errorCode) {
    String msg = null;
    try {
      msg = messageSource.getMessage("err." + errorCode, null, UNKNOWN_ERROR_MSG,
          Locale.getDefault());
    } catch (Exception e) {

    }
    if (StringUtils.isBlank(msg)) {
      try {
        msg = messageSource.getMessage("err." + ErrorCode.DEFAULT_ERROR.getValue(), null,
            UNKNOWN_ERROR_MSG, Locale.getDefault());
      } catch (Exception e) {
      }
    }
    return msg;
  }

  public Error getErrorCode(ErrorCode errorCode) {
    return resolveErrorCode(errorCode.getValue());
  }

  public Error getErrorCode(AuthenticationException ex) {
    if (ex instanceof UserUnAuthenticateException) {
      UserUnAuthenticateException exception = (UserUnAuthenticateException) ex;
      return resolveErrorCode(exception.getErrorCode().getValue(), exception.getFormatingParams());
    } else if (ex instanceof BadCredentialsException) {
      return resolveErrorCode(ErrorCode.USER_PASSWORD_MISMATCH_ERROR.getValue(), ex.getMessage());
    } else {
      //logger.error(ExceptionUtils.getStackTrace(ex));
      return resolveErrorCode(ErrorCode.DEFAULT_ERROR.getValue());
    }
  }

  public Error getErrorCode(HttpServletRequest request, AuthenticationException ex) {
    if (ex instanceof UserUnAuthenticateException) {
      UserUnAuthenticateException exception = (UserUnAuthenticateException) ex;
      return resolveErrorCode(exception.getErrorCode().getValue(), request,
          exception.getFormatingParams());
    } else if (ex instanceof BadCredentialsException) {
      return resolveErrorCode(ErrorCode.USER_PASSWORD_MISMATCH_ERROR.getValue(), request);
    } else {
      //logger.error(ExceptionUtils.getStackTrace(ex));
      return resolveErrorCode(ErrorCode.DEFAULT_ERROR.getValue(), request);
    }

  }

}
