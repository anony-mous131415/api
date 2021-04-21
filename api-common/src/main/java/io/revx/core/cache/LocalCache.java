
package io.revx.core.cache;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.revx.core.constant.Constants;

@SuppressWarnings({"unused"})
public class LocalCache<T> {

  private String name;
  private Long timeToExpire = Constants.DEFAULT_LOCAL_CACHE_EXPIRE_TIME_IN_MILIES;
  private Cache<String, T> cache;

  public LocalCache(String name, Long timeToExpire) {
    this.name = name;
    if (timeToExpire != null && timeToExpire > 0) {
      this.timeToExpire = timeToExpire;
    }
    cache = CacheBuilder.newBuilder().maximumSize(100 * 10)
        .expireAfterWrite(this.timeToExpire, TimeUnit.MILLISECONDS).build();
  }

  public LocalCache(String name) {
    this(name, null);
  }

  public T get(String key) {
    return cache.getIfPresent(key);
  }

  public void clear() {
    cache.invalidateAll();
  }

  public long getCacheSize() {
    return cache.size();
  }

  public void put(String key, T value) {
    cache.put(key, value);
  }

  public void putAll(Map<String, T> map) {
    cache.putAll(map);
  }

  /**
   * Creates a key with reportKey + entity ID + timestamp rounded to nearest CACHE_REFRESH_INTERVAL
   * 
   * @param entityId
   * @return
   */
  public static String constructCacheKey(String reportKey, Long entityId) {
    long keySuffix = getTimeToCache();
    return new StringBuilder(reportKey).append(Constants.CACHE_KEY_SEPRATOR).append(entityId)
        .append(Constants.CACHE_KEY_SEPRATOR).append(keySuffix).toString();
  }

  public static long getTimeToCache() {
    long currentTimestamp = new Date().getTime();
    return currentTimestamp - (currentTimestamp % 10);
  }
}
