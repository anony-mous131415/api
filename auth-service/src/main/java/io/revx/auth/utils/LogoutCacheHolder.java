package io.revx.auth.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.auth.constants.SecurityConstants;
import io.revx.core.service.CacheService;

@Component
public class LogoutCacheHolder {

  private static Logger LOGGER = LogManager.getLogger(LogoutCacheHolder.class);

  @Autowired
  CacheService cacheService;

  @Autowired
  SecurityConstants securityConstants;

  public Long getCache(String cacheKey) {

    LOGGER.debug(" Getting Cache {} ", cacheKey);
    if (cacheService != null) {
      return cacheService.getFromLogoutCache(cacheKey);
    }
    return -1l;
  }

  public void setCache(String cacheKey, Long epoc) {
    LOGGER.debug(" Saving Cache {}: {}  ", cacheKey, epoc);
    if (cacheService != null) {
      cacheService.populateInLogoutCache(cacheKey, epoc,
          securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS() * 1000);
    }

  }
}
