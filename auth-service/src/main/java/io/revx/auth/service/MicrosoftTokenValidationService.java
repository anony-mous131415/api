package io.revx.auth.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.google.gson.Gson;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.UserUnAuthenticateException;

@Component
@SuppressWarnings("rawtypes")
public class MicrosoftTokenValidationService implements ExternalTokenValidationService {
  private static String OFFICE_EMAIL_KEY = "mail";
  private static String OFFICE_SURNAME_KEY = "surname";
  private static String OFFICE_DIAPLAY_NAME_KEY = "displayName";
  private static Logger logger = LogManager.getLogger(MicrosoftTokenValidationService.class);

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  SecurityConstants securityConstants;

  @LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.SOCIAL + ".office")
  @Override
  public UserInfoModel validateToken(String token) throws UserUnAuthenticateException {
    logger.debug("MicrosoftTokenValidationService  validateToken :" + token);
    StringBuffer sb = new StringBuffer(securityConstants.getOFFICE_365_AUTH_API());
    UserInfoModel uInfo = null;
    try {
      URI uri = new URI(sb.toString());
      HttpHeaders headers = new HttpHeaders();
      headers.add("Authorization", "Bearer " + token);
      headers.add("Accept", "application/json");
      HttpEntity<String> entity = new HttpEntity<>("body", headers);
      ResponseEntity<String> result =
          restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
      Gson gson = new Gson();
      Map map = null;
      try {
        map = gson.fromJson(result.getBody(), Map.class);
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (map != null) {
        uInfo = new UserInfoModel(getUserName(map), "",
            (Collection<? extends GrantedAuthority>) new ArrayList<GrantedAuthority>());
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return uInfo;
  }

  private String getUserName(Map map) {
    if (map.get(OFFICE_EMAIL_KEY) != null)
      return (String) map.get(OFFICE_EMAIL_KEY);
    else if (map.get(OFFICE_DIAPLAY_NAME_KEY) != null)
      return (String) map.get(OFFICE_DIAPLAY_NAME_KEY);
    else
      return (String) map.get(OFFICE_SURNAME_KEY);
  }
}
