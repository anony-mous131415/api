package io.revx.core.exception;

import java.util.Arrays;

public class ApiException extends Exception {

  private static final long serialVersionUID = 1L;

  private ErrorCode errorCode;
  private Object[] formatingParams;

  public ApiException() {}

  public ApiException(String msg) {
    super(msg);
  }

  public ApiException(ErrorCode errorCode, Object... formatingParams) {
    super(String.valueOf(errorCode));
    this.errorCode = errorCode;
    this.formatingParams = formatingParams;
  }

  public ApiException(ErrorCode errorCode) {
    super(String.valueOf(errorCode));
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
    builder.append("ApiException [errorCode=");
    builder.append(errorCode);
    builder.append(", formatingParams=");
    builder.append(Arrays.toString(formatingParams));
    builder.append("]");
    return builder.toString();
  }

}
