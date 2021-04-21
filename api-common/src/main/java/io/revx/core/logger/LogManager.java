package io.revx.core.logger;

public class LogManager extends org.apache.logging.log4j.LogManager {

  public static Logger getLogger(final Class<?> clazz) {
    return (Logger) org.apache.logging.log4j.LogManager.getLogger(clazz);
  }

  public static Logger getLogger(final String name) {
    return (Logger) org.apache.logging.log4j.LogManager.getLogger(name);
  }

}
