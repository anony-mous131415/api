package io.revx.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.utils.LogoutCacheHolder;
import io.revx.core.response.ApiResponseObject;

@Service
public class LoginLogoutService {

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Autowired
  LogoutCacheHolder logoutCacheHolder;

  public ApiResponseObject<Boolean> logout(String token) {
    if (jwtTokenProvider.validateToken(token)) {
      long epoch = System.currentTimeMillis() / 1000;
      logoutCacheHolder.setCache(token, epoch);
    }
    SecurityContextHolder.getContext().setAuthentication(null);
    ApiResponseObject<Boolean> resp = new ApiResponseObject<Boolean>();
    resp.setRespObject(true);
    return resp;
  }

  public ApiResponseObject<Boolean> logoutUser(String token, String username) {
    if (jwtTokenProvider.validateToken(token)) {
      long epoch = System.currentTimeMillis() / 1000;
      logoutCacheHolder.setCache(token, epoch);
      logoutCacheHolder.setCache(username, epoch);
    }
    ApiResponseObject<Boolean> resp = new ApiResponseObject<Boolean>();
    resp.setRespObject(true);
    return resp;
  }

}
