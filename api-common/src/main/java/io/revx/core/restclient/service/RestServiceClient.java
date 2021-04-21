package io.revx.core.restclient.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import io.revx.core.exception.ApiException;

@Component
public class RestServiceClient implements RestClient {

  private static Logger logger = LogManager.getLogger(RestServiceClient.class);

  public String doGet(String url, MultivaluedMap<String, String> queryParams,
      HashMap<String, String> headers) throws ApiException {
    String responseString = "";
    
    logger.info("Inside doGet : url: {}, queryParams : {}, headers : {} ", url, queryParams, headers);
    int retry = 3;
    while (retry > 0) {
      try {
        Client client = Client.create();
        WebResource webResource = client.resource(url).queryParams(queryParams);

        WebResource.Builder builder = webResource.accept(MediaType.APPLICATION_JSON);
        
        
        if (headers != null) {
          for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            builder.header(key, value);
          }
        }
        logger.info("Builder : {}", builder.toString());
        
        ClientResponse response = builder.get(ClientResponse.class);
        logger.info("doget status for resource url : {}", +response.getStatus());

        if (response.getStatus() != HttpStatus.SC_OK) {
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          try {
            IOUtils.copy(response.getEntityInputStream(), bos);
          } catch (IOException e) {
            e.printStackTrace();
          }

          String errorString = bos.toString();
          throw new ApiException(errorString);
        }

        responseString = response.getEntity(String.class);

        retry = 0;
        return responseString;
      } catch (Exception e) {
        retry--;
        if (retry == 0) {
          logger.error("Error in ServiceClient doGet for Request Url : ", url);
          logger.error("Error in ServiceClient doGet ", e);
          throw new ApiException(e.getMessage());
        }
      }
    }
    return responseString;

  }


  public String doPost(String url, MultivaluedMap<String, String> queryParams,
      HashMap<String, String> headers, String postBody) throws ApiException {
    String responseString = "";
    int retry = 3;
    while (retry > 0) {
      try {


        logger.debug("post url: {} and requestBody: {}", url, postBody);

        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse response =
            webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, postBody);

        if (response.getStatus() != HttpStatus.SC_OK) {
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          try {
            IOUtils.copy(response.getEntityInputStream(), bos);
          } catch (IOException e) {
            e.printStackTrace();
          }

          String errorString = bos.toString();
          throw new ApiException(errorString);
        }

        responseString = response.getEntity(String.class);
        retry = 0;
        return responseString;
      } catch (Exception e) {
        retry--;
        if (retry == 0) {
          logger.error("Error in ServiceClient doPost for Request Url : ", url);
          logger.error("Error in ServiceClient doPost ", e);
          throw new ApiException(e.getMessage());
        }
      }
    }
    return responseString;
  }

  public String doPut(String url, MultivaluedMap<String, String> queryParams,
      HashMap<String, String> headers, String putBody) throws ApiException {
    String responseString = "";
    int retry = 3;
    while (retry > 0) {
      try {

        logger.debug("put url: {} and requestBody: {}", url, putBody);

        Client client = Client.create();
        WebResource webResource = client.resource(url);
        ClientResponse response =
            webResource.type(MediaType.APPLICATION_JSON).put(ClientResponse.class, putBody);

        if (response.getStatus() != HttpStatus.SC_OK) {
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          try {
            IOUtils.copy(response.getEntityInputStream(), bos);
          } catch (IOException e) {
            e.printStackTrace();
          }

          String errorString = bos.toString();
          throw new ApiException(errorString);
        }

        responseString = response.getEntity(String.class);

        retry = 0;
        return responseString;
      } catch (Exception e) {
        retry--;
        if (retry == 0) {
          logger.error("Error in ServiceClient doPost for Request Url : ", url);
          logger.error("Error in ServiceClient doPost ", e);
          throw new ApiException(e.getMessage());
        }
      }
    }
    return responseString;
  }

}
