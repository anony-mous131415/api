package io.revx.core.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.revx.core.aop.LogMetrics;
import io.revx.core.cache.CommonCache;
import io.revx.core.cache.CommonDTOCache;
import io.revx.core.cache.DTOCache;
import io.revx.core.cache.DTOEntityCache;
import io.revx.core.cache.LogOutCache;
import io.revx.core.constant.Constants;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.UnsortableAttributeException;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.DashboardData;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.querybuilder.objs.FilterComponent;

@Component
@SuppressWarnings({"unused", "rawtypes"})
public class CacheService {

  private static Logger logger = LogManager.getLogger(CacheService.class);

  private static Map<String, DTOCache> cacheMap = new HashMap<>();

  private static Map<String, DTOEntityCache> cacheEntityMap = new HashMap<>();
  
  private static Map<String, CommonDTOCache<CreativeDTO>> creativeDTOCacheMap = new HashMap<>();
  
  private static Map<String , List<Long>> dictionaryCacheMap = new HashMap<>();

	public List<Long> fetchAggregatorDictionaryData(String key) {

		if (dictionaryCacheMap.get(key) == null) {
			return null;
		} else {
			return dictionaryCacheMap.get(key);
		}
	}

	public void populateAggregatorDictionaryData(String key, List<Long> aggregatorIds) {

		dictionaryCacheMap.put(key, aggregatorIds);
	}

  public String getCacheKey(String dashboardEntity, String entity,
      DashboardRequest dashboardRequest, Set<FilterComponent> set) {
    StringBuffer sb = new StringBuffer();
    sb.append(StringUtils.isNotBlank(dashboardEntity) ? dashboardEntity : "");
    sb.append(Constants.CACHE_KEY_SEPRATOR).append(StringUtils.isNotBlank(entity) ? entity : "");
    sb.append(Constants.CACHE_KEY_SEPRATOR).append(getRequestKey(dashboardRequest));
    sb.append(Constants.CACHE_KEY_SEPRATOR).append(getFilterKey(set));
    return sb.toString();
  }


  private String getRequestKey(DashboardRequest dashboardRequest) {
    StringBuffer sb = new StringBuffer();
    sb.append(dashboardRequest.getDuration().getStartTimeStamp());
    sb.append(Constants.CACHE_KEY_SEPRATOR)
        .append(dashboardRequest.getDuration().getEndTimeStamp());
    sb.append(Constants.CACHE_KEY_SEPRATOR).append(dashboardRequest.getGroupBy());
    return sb.toString();
  }

  private String getFilterKey(Set<FilterComponent> set) {
    SortedMap<String, List<String>> sm = new TreeMap<>();
    StringBuffer sb = new StringBuffer();
    if (set != null && set.size() > 0) {
      for (FilterComponent fc : set) {
        List<String> values = sm.get(fc.getField().name());
        if (values == null) {
          values = new ArrayList<String>();
          sm.put(fc.getField().name(), values);
        }
        values.add(fc.getValue());
      }
    }
    for (Entry<String, List<String>> uniqValuesByFilter : sm.entrySet()) {
      sb.append(Constants.CACHE_KEY_SEPRATOR).append(uniqValuesByFilter.getKey());
      sb.append(Constants.CACHE_KEY_SEPRATOR)
          .append(StringUtils.join(uniqValuesByFilter.getValue(), Constants.CACHE_KEY_SEPRATOR));
    }
    return sb.toString();
  }
  
  public String getCreativeCacheKey(DashboardRequest search, Set<FilterComponent> tableFilters , long licenseeId) {
		StringBuffer sb = new StringBuffer();
		sb.append(Constants.CREATIVE_KEY);
		sb.append(Constants.CACHE_KEY_SEPRATOR).append(licenseeId);
		sb.append(Constants.CACHE_KEY_SEPRATOR).append(getRequestKey(search));
		sb.append(Constants.CACHE_KEY_SEPRATOR).append(getFilterKey(tableFilters));
		return sb.toString();

	}

  /**
   * Expiring the cache keys of dashboard service based on the current and end time stamps
   *
   * @param duration
   * @return
   */
  public Long getTimeToExpireTheCache(Duration duration) {
    long endTs = duration.getEndTimeStamp();
    long currentTs = System.currentTimeMillis() / 1000;
    LocalDateTime now = LocalDateTime.now();
    now = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
    long todayEpoch = now.toEpochSecond(ZoneOffset.UTC);
    long tomorrowEpoch = now.plus(1, ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
    long yesterdaysEpoch = now.minus(1, ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
    long weekAgoEpoch = now.minus(7,ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
    if(todayEpoch == endTs && ((duration.getStartTimeStamp()==yesterdaysEpoch)||(duration.getStartTimeStamp()==weekAgoEpoch))) {
        return (tomorrowEpoch-currentTs)*1000;
    } else {
        return (long) (3 * 86400 * 1000);
    }
  }

  public Long getTimeToExpireTheCache(Long endTimeSecs) {
    Long expiry = 0L;
    Long currentTimeSecs = System.currentTimeMillis() / 1000;
    if (endTimeSecs > currentTimeSecs)
      expiry = endTimeSecs - currentTimeSecs;

    return expiry;
  }

  @LogMetrics(name = GraphiteConstants.CACHE + GraphiteConstants.CHART + GraphiteConstants.GET)
  public List<BaseModel> fetchChartCachedData(String cacheKey, String groupBy) {

    DTOCache cache = (DTOCache) cacheMap.get(cacheKey);
    logger.debug(" feching data from cacheKey {} , cache {} ", cacheKey, cache);
    if (cache == null)
      return null;
    try {
      return (List<BaseModel>) cache.getAll(groupBy != null ? Arrays.asList(groupBy) : null);
    } catch (UnsortableAttributeException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<BaseModel> fetchListCachedData(String cacheKey, List<String> sortOn) {
    return fetchListCachedData(cacheKey, null, sortOn);
  }

  @LogMetrics(name = GraphiteConstants.CACHE + GraphiteConstants.LIST + GraphiteConstants.GET)
  public List<BaseModel> fetchListCachedData(String cacheKey, Set<DashboardFilters> filters,
      List<String> sortOn) {
    DTOCache cache = cacheMap.get(cacheKey);
    logger.debug(" feching data from cacheKey {} , cache {} ", cacheKey, cache);
    if (cache == null)
      return null;
    try {
      if (filters == null || filters.size() <= 0) {
        return cache.getAll(sortOn);
      }
      return (List<BaseModel>) cache.getAll(filters, sortOn);
    } catch (UnsortableAttributeException e) {
      e.printStackTrace();
    }
    return null;
  }

  @LogMetrics(name = GraphiteConstants.CACHE + GraphiteConstants.LIST + GraphiteConstants.GET)
  public List<BaseEntity> fetchListCachedEntityData(String cacheKey, Set<DashboardFilters> filters,
      List<String> sortOn) {
    DTOEntityCache cache = cacheEntityMap.get(cacheKey);
    logger.debug(" feching data from cacheKey {} , cache {}, filters {}, sort {} ", cacheKey, cache,
        filters, sortOn);
    if (cache == null)
      return null;
    try {
      if (filters == null || filters.size() <= 0) {
        return cache.getAll(sortOn);
      }
      return (List<BaseEntity>) cache.getAll(filters, sortOn);
    } catch (UnsortableAttributeException e) {
      e.printStackTrace();
    }
    return null;
  }

  @LogMetrics(name = GraphiteConstants.CACHE + GraphiteConstants.POPULATE)
  public void populateCache(String cacheKey, Collection<BaseModel> data, Long timeToExpire) {
    DTOCache cacheForKey = getCacheForCacheKey(cacheKey, timeToExpire);
    if (cacheForKey == null) {
      return;
    }
    logger.debug("Saving data to cachekey {} ", cacheKey);
    cacheForKey.populate(data);
  }

  private DTOCache getCacheForCacheKey(String key, Long timeToExpire) {
    DTOCache cache = cacheMap.get(key);
    if (cache == null) {
      cache = new DTOCache(key, DashboardData.class, timeToExpire);
      cacheMap.put(key, cache);
    }
    return (DTOCache) cacheMap.get(key);
  }

  public void populateInLogoutCache(String cacheKey, Long data, Long timeToExpire) {
    try {
      LogOutCache cache = new LogOutCache(cacheKey, timeToExpire);
      if (cache == null)
        return;
      cache.populate(cacheKey, data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Long getFromLogoutCache(String cacheKey) {
    try {
      LogOutCache cache = new LogOutCache(cacheKey);
      if (cache == null)
        return -1l;
      return cache.getFromCache(cacheKey);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1l;
  }

  @LogMetrics(name = GraphiteConstants.CACHE + GraphiteConstants.POPULATE)
  public <T> void populateCache(String cacheKey, Collection<BaseEntity> data, Long timeToExpire,
      Class T) {
    DTOEntityCache cacheForKey = getCacheForCacheKey(cacheKey, timeToExpire, T);
    if (cacheForKey == null) {
      return;
    }
    logger.debug("Saving data to cachekey {} ", cacheKey);
    cacheForKey.populate(data);
  }


  @LogMetrics(name = GraphiteConstants.CACHE + GraphiteConstants.DELETE)
  public void removeCache(String cacheKey) {
    DTOEntityCache cache = cacheEntityMap.get(cacheKey);
    if (cache == null) {
      return;
    }
    cache.removeCacheForKey(cacheKey);
    cacheEntityMap.remove(cacheKey);
  }

  public void removeBaseModelCache(String cacheKey) {
    DTOCache cache = cacheMap.get(cacheKey);
    if (cache == null) {
      return;
    }
    cache.removeCacheForKey(cacheKey);
    cacheMap.remove(cacheKey);
  }

  public void removeCreativeCache(String cacheKey) {
    CommonDTOCache<CreativeDTO> cache = creativeDTOCacheMap.get(cacheKey);
    if (cache == null) {
      return;
    }
    cache.removeCacheForKey(cacheKey);
    creativeDTOCacheMap.remove(cacheKey);
  }

  private <T> DTOEntityCache getCacheForCacheKey(String key, Long timeToExpire, Class T) {
    DTOEntityCache cache = cacheEntityMap.get(key);
    if (cache == null) {
      // TODO:Pass Class Type from Method
      cache = new DTOEntityCache(key, T, timeToExpire);
      cacheEntityMap.put(key, cache);
    }
    return cacheEntityMap.get(key);
  }
  
  public void populateCreativeCache(String cacheKey, List<CreativeDTO> data, Long timeToExpire) {
	  CommonDTOCache<CreativeDTO> cacheForKey = getCreativeCacheForCacheKey(cacheKey, timeToExpire);
	  if (cacheForKey == null) {
		  return;
	  }
	  logger.debug("Saving data to cachekey {} , cache {}", cacheKey, cacheForKey);
	  cacheForKey.populate(data);
  }

  @SuppressWarnings("unchecked")
  private CommonDTOCache<CreativeDTO> getCreativeCacheForCacheKey(String cacheKey, Long timeToExpire) {
	  CommonDTOCache<CreativeDTO> cache = creativeDTOCacheMap.get(cacheKey);
	  logger.debug(" ---- feching data from cacheKey {} , cache {} ", cacheKey, cache);
	  if (cache == null) {
		  cache = new CommonDTOCache(cacheKey, CreativeDTO.class, timeToExpire);
		  creativeDTOCacheMap.put(cacheKey, cache);
	  }
	  return creativeDTOCacheMap.get(cacheKey);
  }


  public List<CreativeDTO> fetchCreativeCacheForCacheKey(String cacheKey, Set<DashboardFilters> filters,
		  List<String> sortOn) {
	  CommonDTOCache<CreativeDTO> cache = creativeDTOCacheMap.get(cacheKey);
	  logger.debug(" feching data from cacheKey {} , cache {} ", cacheKey, cache);
	  if (cache == null)
		  return null;
	  try {
		  if (filters == null || filters.size() <= 0) {
			  return cache.getAll(sortOn);
		  }
		  return (List<CreativeDTO>) cache.getAll(filters, sortOn);
	  } catch (UnsortableAttributeException e) {
		  e.printStackTrace();
	  }
	  return null;
  }

  /**
  * Scheduling a job to evict the keys from the memory as the expired keys will still reside in memory
   * unless eviction policy kicks in or manually cleaning up by invoking each cache key
  * */
  @Scheduled(cron = "0 10 0 * * *")
  public void evictingExpiredCacheKeys() {
    logger.info("Initiating cache eviction job");
    Runtime runtime = Runtime.getRuntime();
    long usedMemory = runtime.totalMemory()-runtime.freeMemory();
    logger.info("Used memory before cache cleanup :{}",usedMemory);
    List<Map<String, ? extends CommonCache>> listOfCaches = new ArrayList<>();
    listOfCaches.add(creativeDTOCacheMap);
    listOfCaches.add(cacheEntityMap);
    listOfCaches.add(cacheMap);
    for (Map<String, ? extends CommonCache> cache : listOfCaches) {
      for (Map.Entry<String, ? extends CommonCache> cacheEntry : cache.entrySet()) {
        CommonCache<?> commonCache = cacheEntry.getValue();
        if (commonCache != null) {
          commonCache.evictCacheKeys();
        }
      }
    }
    usedMemory = runtime.totalMemory()-runtime.freeMemory();
    logger.info("Used memory after cache cleanup: {}",usedMemory);
  }

}
