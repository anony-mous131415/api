package io.revx.core.response;

public class Error {
  private int code;
  private String message;

  public Error() {

  }

  public Error(String meassage) {
    super();
    this.message = meassage;
  }

  public Error(int code) {
    super();
    this.code = code;
  }

  public Error(int code, String meassage) {
    super();
    this.code = code;
    this.message = meassage;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String meassage) {
    this.message = meassage;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Error [code=");
    builder.append(code);
    builder.append(", message=");
    builder.append(message);
    builder.append("]");
    return builder.toString();
  }

}
