package io.revx.auth.service;

import io.revx.auth.pojo.UserInfoModel;
import io.revx.core.exception.UserUnAuthenticateException;

public interface ExternalTokenValidationService {

  public UserInfoModel validateToken(String token) throws UserUnAuthenticateException;

}
