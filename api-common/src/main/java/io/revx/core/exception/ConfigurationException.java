package io.revx.core.exception;

public class ConfigurationException extends ApiException {

  private static final long serialVersionUID = 6212936296798023577L;

  public ConfigurationException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }
}
