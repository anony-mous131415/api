package io.revx.api.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class UiLoggingService {

  private static Logger logger = LogManager.getLogger("uiLogger");

  public void log(String msg, String logLevel) {
    logger.log(getLogLevel(logLevel), msg);
  }

  private Level getLogLevel(String logLevel) {
    if (StringUtils.isNoneBlank(logLevel)) {
      logLevel = StringUtils.upperCase(logLevel);
      Level level = null;
      try {
        level = Level.getLevel(logLevel);
      } catch (Exception e) {
      }
      return level != null ? level : Level.INFO;
    }
    return Level.INFO;
  }

}
