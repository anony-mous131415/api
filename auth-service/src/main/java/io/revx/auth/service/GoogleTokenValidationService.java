package io.revx.auth.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
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
public class GoogleTokenValidationService implements ExternalTokenValidationService {
  private static String GOOGLE_EMAIL_KEY = "email";

  private static Logger logger = LogManager.getLogger(GoogleTokenValidationService.class);

  @Autowired
  RestTemplate restTemplate;
  @Autowired
  SecurityConstants securityConstants;

  @LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.SOCIAL + ".google")
  @Override
  public UserInfoModel validateToken(String token) throws UserUnAuthenticateException {
    logger.debug(" GoogleTokenValidationService validateToken :" + token);

    StringBuffer sb =
        new StringBuffer(securityConstants.getGOOGLE_AUTH_API()).append(StringUtils.trim(token));
    UserInfoModel uInfo = null;
    try {
      URI uri = new URI(sb.toString());
      HttpHeaders headers = new HttpHeaders();
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
    return (String) map.get(GOOGLE_EMAIL_KEY);
  }
}
