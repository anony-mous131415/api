package io.revx.api.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import io.revx.api.constants.ApiConstant;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;

@Component
public class UrgAuthenticationService {
  private static Logger logger = LogManager.getLogger(UrgAuthenticationService.class);

  @Value(value = "${urg.authApi:http://localhost:9092/v2/auth}")
  String urgHostName;

  @Autowired
  RestTemplate restTemplate;

  public UserInfo authenticate(String authkey) throws UserUnAuthenticateException {
    logger.debug("******************** Authentication Started ****************************");
    ErrorCode errorCode = ErrorCode.INVALID_ACCESS_TOKEN;
    ApiResponseObject<UserInfo> urgresponse = null;
    try {
      urgresponse = getUrgResponse(authkey);
      logger.info(" urgresponse :" + urgresponse);
    } catch (Exception e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      throw new UserUnAuthenticateException(errorCode);
    }

    UserInfo ui = parseUserInfo(urgresponse);
    if (ui != null && ui.getSelectedLicensee() != null) {
      logger.debug("VALID KEY : urg responds active key :" + authkey);
      return ui;
    } else {
      errorCode = getErrorCodeIfFoundFromURG(urgresponse);
      throw new UserUnAuthenticateException(errorCode);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public ApiResponseObject<UserInfo> getUrgResponse(String authKey) {
    HttpHeaders requestHeaders = getRequestHeader();
    requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    requestHeaders.add("token", authKey);
    HttpEntity requestEntity = new HttpEntity(requestHeaders);
    String uri = urgHostName + ApiConstant.URG_GET_USER_INFO_MAPPING;
    logger.info(" Auth uri {} :", uri);
    ResponseEntity<ApiResponseObject<UserInfo>> entity =
        (ResponseEntity<ApiResponseObject<UserInfo>>) restTemplate.exchange(uri, HttpMethod.GET,
            requestEntity, new ParameterizedTypeReference<ApiResponseObject<UserInfo>>() {});
    logger.debug("URG response  : " + entity);
    return (entity != null && entity.getBody() != null) ? entity.getBody() : null;
  }

  public HttpHeaders getRequestHeader() {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("Accept-Encoding", "gzip");
    return requestHeaders;
  }

  public boolean isValidResponse(ApiResponseObject<UserInfo> urgResponse) {
    logger.info(" urgResponse :" + urgResponse);
    if (urgResponse != null && urgResponse.getRespObject() != null) {
      logger.info(" getRespObject :" + urgResponse.getRespObject());
      return true;
    }
    return false;
  }

  public UserInfo parseUserInfo(ApiResponseObject<UserInfo> apiResponseObject) {
    if (isValidResponse(apiResponseObject)) {
      return apiResponseObject.getRespObject();
    }
    return null;
  }

  private ErrorCode getErrorCodeIfFoundFromURG(ApiResponseObject<UserInfo> urgresponse) {
    ErrorCode errCode = ErrorCode.INVALID_ACCESS_TOKEN;
    logger.debug(" getErrorCodeIfFoundFromURG : {} ", urgresponse);
    if (urgresponse != null && urgresponse.getError() != null) {
      logger.debug(" getErrorCodeIfFoundFromURG  getError : {} ", urgresponse.getError());
      errCode = ErrorCode.parseFrom(urgresponse.getError().getCode());
      logger.debug(" getErrorCodeIfFoundFromURG  errCode : {} ", errCode);
    }
    return errCode != null ? errCode : ErrorCode.INVALID_ACCESS_TOKEN;
  }

}
