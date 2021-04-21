package io.revx.core.restclient.service;

import java.util.HashMap;
import javax.ws.rs.core.MultivaluedMap;
import io.revx.core.exception.ApiException;

public interface RestClient {

  public String doGet(String url, MultivaluedMap<String, String> queryParams, HashMap<String, String> headers) throws ApiException;
  
  public String doPost(String url, MultivaluedMap<String, String> queryParams, HashMap<String, String> headers, String postBody) throws ApiException;
  
  public String doPut(String url, MultivaluedMap<String, String> queryParams, HashMap<String, String> headers, String putBody) throws ApiException;
  
}
