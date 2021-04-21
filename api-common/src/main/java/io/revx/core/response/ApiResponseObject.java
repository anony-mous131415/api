package io.revx.core.response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.ThreadContext;
import io.revx.core.constant.Constants;

public class ApiResponseObject<T> {

  private T respObject;

  private String respId;

  private Error error;

  public ApiResponseObject() {
    String urid = ThreadContext.get(Constants.URID_KEY);
    this.respId = (urid != null) ? urid : RandomStringUtils.randomAlphanumeric(32);
  }

  public T getRespObject() {
    return respObject;
  }

  public void setRespObject(T respObject) {
    this.respObject = respObject;
  }

  public String getRespId() {
    return respId;
  }

  public void setRespId(String respId) {
    this.respId = respId;
  }

  public Error getError() {
    return error;
  }

  public void setError(Error error) {
    this.error = error;
    if (this.error != null) {
      erashRespObject();
    }

  }

  private void erashRespObject() {
    this.respObject = null;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ApiResponseObject [respObject=");
    builder.append(respObject);
    builder.append(", respId=");
    builder.append(respId);
    builder.append(", error=");
    builder.append(error);
    builder.append("]");
    return builder.toString();
  }

}
