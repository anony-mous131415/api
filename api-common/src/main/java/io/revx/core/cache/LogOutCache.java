package io.revx.core.cache;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.revx.core.utils.StringUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

public class LogOutCache extends CommonCache<Long> {

  private static final Logger logger = LogManager.getLogger(LogOutCache.class);
  private static final String logoutCache = "logoutCache";

  public LogOutCache(String name) {
    if (StringUtils.isBlank(name))
      throw new NullPointerException("name cannot be null or empty");
    this.name = name;
    cache = getCache();

  }

  public LogOutCache(String name, Long timeToExpire) {
    this(name);
    if (timeToExpire != null && !timeToExpire.equals(0)) {
      this.timeToExpire = timeToExpire;
      lastPopulateTime = -this.timeToExpire;
    }
  }

  private Cache getCache() {
    Cache c = cacheManager.getCache(name);
    if (c == null) {
      CacheConfiguration cc = config.clone();
      if (cacheManager.getCache(logoutCache) != null) {
        cc = cacheManager.getCache(logoutCache).getCacheConfiguration();
      }
      cc.setName(name);
      c = new Cache(cc);
      cacheManager.addCache(c);
    }
    return c;
  }

  public void populate(String cacheKey, Long data) {
    logger.debug("Saving into cache {}, {} ", cacheKey, data);
    cache.put(new Element(cacheKey, data));
  }

  public Long getFromCache(String cacheKey) {
    logger.debug("Fetching cache {} ", cacheKey);
    Element ele = cache.get(cacheKey);
    return ele == null ? -1l : (Long) ele.getObjectValue();
  }
}
