package io.revx.core.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CacheLog {

  private static final Logger cacheLogger = LogManager.getLogger(CacheLog.class);

  /**
   * make an entry for if it is related to the cache
   * 
   * @param cacheType
   * @param getFromCache
   * @param cacheFound
   * @param totalRecords
   */
  public void write(CacheType cacheType, String apiHint, boolean getFromCache, boolean cacheFound,
      Integer totalRecords) {
    StringBuilder sb = new StringBuilder();
    sb.append(cacheType).append(", ");
    sb.append(apiHint).append(", ");
    sb.append(getFromCache ? "1" : "0").append(", ");
    sb.append(cacheFound ? "1" : "0").append(", ");
    sb.append((totalRecords != null) ? totalRecords : "0");
    cacheLogger.info(sb.toString());
  }
}
