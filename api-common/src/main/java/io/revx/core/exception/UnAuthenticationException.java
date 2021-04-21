package io.revx.core.exception;

public class UnAuthenticationException extends ApiException {

  private static final long serialVersionUID = 1L;

  public UnAuthenticationException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
