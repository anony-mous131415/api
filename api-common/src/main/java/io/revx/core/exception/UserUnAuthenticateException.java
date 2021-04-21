package io.revx.core.exception;

import java.util.Arrays;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserUnAuthenticateException extends UsernameNotFoundException {

  private static final long serialVersionUID = 1L;

  private ErrorCode errorCode;
  private Object[] formatingParams;

  public UserUnAuthenticateException(ErrorCode errorCode, Object... formatingParams) {
    super(String.valueOf(errorCode));
    this.formatingParams = formatingParams;
    this.errorCode = errorCode;
  }

  public UserUnAuthenticateException(ErrorCode errorCode) {
    super(String.valueOf(errorCode));
    this.errorCode = errorCode;
  }

  public UserUnAuthenticateException(ErrorCode errorCode, Throwable cause, String message) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(ErrorCode errorCode) {
    this.errorCode = errorCode;
  }

  public Object[] getFormatingParams() {
    return formatingParams;
  }

  public void setFormatingParams(Object[] formatingParams) {
    this.formatingParams = formatingParams;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserUnAuthenticateException [errorCode=");
    builder.append(errorCode);
    builder.append(", formatingParams=");
    builder.append(Arrays.toString(formatingParams));
    builder.append("]");
    return builder.toString();
  }

}
