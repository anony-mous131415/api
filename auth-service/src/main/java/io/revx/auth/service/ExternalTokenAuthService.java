package io.revx.auth.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.auth.enums.ExternalClients;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.TokenResponse;
import io.revx.core.response.UserInfo;

@Component
public class ExternalTokenAuthService {

  private static Logger logger = LogManager.getLogger(ExternalTokenAuthService.class);
  @Autowired
  GoogleTokenValidationService googleTokenValidationService;

  @Autowired
  FacebookTokenValidationService facebookTokenValidationService;

  @Autowired
  MicrosoftTokenValidationService microsoftTokenValidationService;

  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  UserService userService;

  public ApiResponseObject<TokenResponse> getAccessTokenFromExternalToken(String clientId,
      String token) {
    logger.debug("getAccessTokenFromExternalToken clientId :" + clientId + " :: token :" + token);
    ApiResponseObject<TokenResponse> apiResp = new ApiResponseObject<TokenResponse>();
    ExternalTokenValidationService externalTokenValidationService = provideService(clientId);
    try {
      UserInfoModel uInfo = externalTokenValidationService.validateToken(token);
      if (uInfo != null) {
        UserInfoModel user = userDetailsService.loadUserByUsername(uInfo.getUsername());
        logger.debug("In social UserInfoModel Token {} ", user);
        String masterToken = jwtTokenProvider.generateMasterToken(user);
        TokenResponse resp = new TokenResponse(user.getUsername(), masterToken);
        UserInfo ui = userService.getUserInfoIfEligible(user);
        if (ui != null) {
          logger.debug("Generating Access Token {} ", ui);
          userService.populateAdvLicenseeMap(ui, user.getUserId());
          resp.setToken(jwtTokenProvider.generateAccessToken(ui));
        }
        apiResp.setRespObject(resp);
      } else {
        throw new UserUnAuthenticateException(ErrorCode.SOCIAL_SERVER_ERROR, clientId);
      }
    } catch (UserUnAuthenticateException e) {
      String[] params = {clientId};
      e.setFormatingParams(params);
      throw e;
    } catch (Exception e) {
      logger.error("Got Exception {} ", ExceptionUtils.getStackTrace(e));
      throw new UserUnAuthenticateException(ErrorCode.SOCIAL_SERVER_ERROR, clientId);
    }
    return apiResp;
  }

  private ExternalTokenValidationService provideService(String clientId) {
    ExternalClients client = ExternalClients.getByClientName(clientId);
    ExternalTokenValidationService service = null;
    switch (client) {
      case FACEBOOK:
        service = facebookTokenValidationService;
        break;
      case GOOGLE:
        service = googleTokenValidationService;
        break;
      case OFFICE:
        service = microsoftTokenValidationService;
        break;
      default:
        break;
    }
    return service;
  }

}
