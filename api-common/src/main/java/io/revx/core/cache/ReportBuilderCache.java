package io.revx.core.cache;

import org.springframework.stereotype.Component;

import io.revx.core.model.BaseEntity;
import io.revx.core.utils.StringUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

@Component
public class ReportBuilderCache extends CommonCache<BaseEntity> {

	public ReportBuilderCache() {

	}

	public ReportBuilderCache(String cacheName) {
		if (StringUtils.isBlank(cacheName)) {
			throw new NullPointerException("CacheName cannot be null or empty");
		}
		this.name = cacheName;
		this.cache = getCache();
	}

	private Cache getCache() {
		Cache cache = cacheManager.getCache(name);
		if (cache == null) {
			CacheConfiguration cacheConfig = config.clone();
			cacheConfig.setName(name);
			cache = new Cache(cacheConfig);
			cacheManager.addCache(cache);
		}
		return cache;
	}

	public String getFromCache(String key) {
		if (cache.isKeyInCache(key) && cache.get(key) !=null) {
			return cache.get(key).getObjectValue().toString();
		}
		return null;
	}

	public void addToCache(String key, String value, int timeToLive) {
		Element element = new Element(key, value);
		element.setTimeToLive(timeToLive);
		cache.put(element);
	}

	public void removeFromCache(String key) {
		if (cache.isKeyInCache(key)) {
			cache.remove(key);
		}
	}
	
	public boolean containsKey(String key) {
		return cache.isKeyInCache(key);
	}
}
