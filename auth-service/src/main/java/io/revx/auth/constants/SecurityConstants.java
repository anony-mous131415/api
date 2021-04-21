package io.revx.auth.constants;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecurityConstants {

  public static final String AUTH_HEADER = "token";
  public static final String AUTHORITIES_KEY = "scopes";
  public static final String IS_LIFETIME_AUTH = "isLifeTimeAuthToken";

  private String SIGNING_KEY = "5Lmr5JwJP4CSU";

  private long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;

  private long MAX_CACHE_SIZE = 10000;

  private String FACEBOOK_AUTH_API =
      "https://graph.facebook.com/me?fields=id,email,name&access_token=";

  private String GOOGLE_AUTH_API = "https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=";

  private String OFFICE_365_AUTH_API = "https://graph.microsoft.com/v1.0/me";

  @Value("${security.signing.key:5Lmr5JwJP4CSU}")
  public void setSigningKey(String signingKey) {
    if (StringUtils.isNotBlank(signingKey))
      SIGNING_KEY = signingKey;
  }

  @Value("${security.tokenValidityInHour:5}")
  public void setAccessTokenValidity(long tokenValidityInHour) {
    ACCESS_TOKEN_VALIDITY_SECONDS = tokenValidityInHour * 60 * 60;
  }

  @Value("${security.maxCacheSize:10000}")
  public void setMaxCacheSize(long maxCacheSize) {
    if (maxCacheSize > 0)
      MAX_CACHE_SIZE = maxCacheSize;
  }

  @Value("${auth.facebookApiUrl:https://graph.facebook.com/me?fields=id,email,name&access_token=}")
  public void setFaceBookApiUrl(String value) {
    if (StringUtils.isNotBlank(value))
      FACEBOOK_AUTH_API = value;
  }

  @Value("${auth.googleApiUrl:https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=}")
  public void setGoogleApiUrl(String value) {
    if (StringUtils.isNotBlank(value))
      GOOGLE_AUTH_API = value;
  }

  @Value("${auth.officeApiUrl:https://graph.microsoft.com/v1.0/me}")
  public void setOffice365ApiUrl(String value) {
    if (StringUtils.isNotBlank(value))
      OFFICE_365_AUTH_API = value;
  }

  public String getSIGNING_KEY() {
    return SIGNING_KEY;
  }

  public long getACCESS_TOKEN_VALIDITY_SECONDS() {
    return ACCESS_TOKEN_VALIDITY_SECONDS;
  }

  public long getMAX_CACHE_SIZE() {
    return MAX_CACHE_SIZE;
  }

  public String getFACEBOOK_AUTH_API() {
    return FACEBOOK_AUTH_API;
  }

  public String getGOOGLE_AUTH_API() {
    return GOOGLE_AUTH_API;
  }

  public String getOFFICE_365_AUTH_API() {
    return OFFICE_365_AUTH_API;
  }
}
