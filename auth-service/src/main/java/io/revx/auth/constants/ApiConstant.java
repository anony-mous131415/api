package io.revx.auth.constants;

public class ApiConstant {

  public static final String API_VERSION = "/v2";
  public static final String AUTH_BASE = API_VERSION + "/auth";

  public static final String LOGIN_URL = AUTH_BASE + "/login";
  public static final String LOGOUT_URL = AUTH_BASE + "/logout-token";

  public static final String LOGOUT_URL_USER = AUTH_BASE + "/logout-user/{username}";
  public static final String USER_INFO = AUTH_BASE + "/userinfo";

  public static final String SWITCH_LICENSEE = AUTH_BASE + "/switch-licensee/{licenseeId}";
  public static final String LOGIN_SOCIAL = AUTH_BASE + "/login-social/{client}/{socialToken}";

  public static final String CHANGE_PASS_SECRET = AUTH_BASE + "/change-pass-secret";
  public static final String USER_PRIVILEGE = AUTH_BASE + "/user-privileges";

  public static final String CREATE_LIFE_TIME_AUTH = AUTH_BASE + "/create-lifetime-token";
  public static final String LIFE_TIME_AUTH = AUTH_BASE + "/lifetime-token";
  public static final String DELETE_LIFE_TIME_AUTH = AUTH_BASE + "/lifetime-token/{tokenId}";
}
