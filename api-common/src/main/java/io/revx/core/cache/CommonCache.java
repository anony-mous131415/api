package io.revx.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.revx.core.model.BaseEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

@SuppressWarnings({"unchecked"})
public abstract class CommonCache<T> {

  private static Logger logger = LogManager.getLogger(CommonCache.class);
  long timeToExpire = 3600000L;
  public long lastPopulateTime = -3600000L;
  protected static String ehCacheFileName = "ehcache.xml";
  protected static String defaultCacheNameInFile = "commonCache";
  public String name;
  public Cache cache;
  protected static CacheManager cacheManager = null;
  static {
    cacheManager = CacheManager.create(getEhCacheConfigFilePath());
  }
  static CacheConfiguration config =
      cacheManager.getCache(defaultCacheNameInFile).getCacheConfiguration();

  public List<T> getAll() {
    if (!cacheAvailable())
      return null;
    Collection<Element> elements = cache.getAll(cache.getKeys()).values();
    logger.debug(" elements  {} ", elements);
    List<T> list = getObjectValues(elements);
    return list;
  }

  private static String getEhCacheConfigFilePath() {
    String cacheFile = CommonCache.class.getClassLoader().getResource(ehCacheFileName).getFile();
    logger.debug("Loading cache File , {}  ", cacheFile);

    return cacheFile;
  }

  protected boolean cacheAvailable() {
    return true;
  }

  public Integer getCount() {
    if (!cacheAvailable())
      return null;
    return cache.getSize();
  }

  protected List<T> getObjectValues(Collection<Element> elements) {
    List<T> l = new ArrayList<>();
    for (Element e : elements) {
      if (e != null)
        l.add((T) e.getObjectValue());
    }
    if (l == null || l.size() == 0)
      return null;
    return l;
  }

  protected List<T> getObjectValues(Results results) {
    List<T> l = new ArrayList<T>();
    for (Result r : results.all()) {
      l.add((T) r.getValue());
    }
    if (l == null || l.size() == 0)
      return null;
    return l;
  }

  public void populateEntity(Collection<BaseEntity> data) {
    if (data == null) {
      return;
    }
    int timeToLive = (int) (timeToExpire/1000);
    lastPopulateTime = System.currentTimeMillis();
    for (BaseEntity b : data) {
      logger.debug("--> Adding to cache {} : {} ", b.getId(), b);
      Element element = new Element(b.getId(), b);
      element.setTimeToLive(timeToLive);
      cache.put(element);
    }
  }

  public void update(Integer id, T data) {
    if (data == null) {
      return;
    }
    int timeToLive = (int) (timeToExpire/1000);
    if (cache.isElementInMemory(id)) {
      Element element = new Element(id, data);
      element.setTimeToLive(timeToLive);
      cache.replace(element);
    }
  }

  protected void clear() {
    cache.removeAll();
  }

  /**
   * Invoking ehcache inbuilt method to evict the keys based on expiry check
   */
  public void evictCacheKeys() {
    cache.evictExpiredElements();
  }

}
