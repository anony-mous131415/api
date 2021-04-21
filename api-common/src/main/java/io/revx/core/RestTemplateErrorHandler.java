package io.revx.core;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {

  private static Logger logger = LogManager.getLogger(RestTemplateErrorHandler.class);

  // TODO: Here we can allow Some Status Code Only
  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return false;
  }

  @Override
  public void handleError(ClientHttpResponse response) throws IOException {
    logger.error("Response error: {} {}", response.getStatusCode(), response.getStatusText());
  }



}
