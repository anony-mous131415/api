package io.revx.auth.service;

import org.springframework.stereotype.Component;

// NOT IN USE
@Component
public class MicrosoftAuthService {
/*
  private final static String AUTHORITY = "https://login.microsoftonline.com/common/";
  private final static String CLIENT_ID = "2e323b51-20a8-45e3-a36b-a1aaf44c7357";
  private final static String RESOURCE = "https://graph.microsoft.com";

  private static Logger logger = LogManager.getLogger(MicrosoftAuthService.class);

  public AuthenticationResult getAccessTokenFromUserCredentials(String username, String password) {
    AuthenticationContext context;
    AuthenticationResult result = null;
    ExecutorService service = null;
    logger.debug(" Getting Token For :" + username);
    try {
      service = Executors.newFixedThreadPool(1);
      context = new AuthenticationContext(AUTHORITY, false, service);
      Future<AuthenticationResult> future =
          context.acquireToken(RESOURCE, CLIENT_ID, username, password, null);

      result = future.get();
    } catch (Exception e) {
      logger.debug("Exception :" + ExceptionUtils.getStackTrace(e));
    } finally {
      service.shutdown();
    }
    return result;
  }*/
}
