package io.revx.common.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jeasy.random.EasyRandom;
import io.revx.core.cache.CommonCache;
import io.revx.core.cache.DTOCache;
import io.revx.core.model.BaseModel;
import io.revx.core.model.DashboardData;
import io.revx.core.model.requests.DashboardFilters;

public class DTOCacheTest {

  private static final Logger logger = LogManager.getLogger(DTOCacheTest.class);

  public static void main(String[] args) {
    testCache();
  }

  public static void testCache() {
    try {
      logger.info(CommonCache.class.getClassLoader().getResource("ehcache.xml").getFile());
      Collection<BaseModel> list = new ArrayList<BaseModel>();

      EasyRandom easyRandom = new EasyRandom();
      for (int i = 0; i <= 10; i++) {
        DashboardData dd = easyRandom.nextObject(DashboardData.class);
        dd.setName((i % 2 == 0 ? "even -" : " odd -") + dd.getName());
        list.add(dd);
      }
      for (BaseModel dashboardData : list) {
        logger.info((DashboardData) dashboardData);
      }
      String cacheKey = "dashboardDataKey";
      DTOCache cacheForKey = new DTOCache(cacheKey, DashboardData.class);
      cacheForKey.populate(list);
      Set<DashboardFilters> sets = new HashSet<DashboardFilters>();
      DashboardFilters dashboardFilter = new DashboardFilters();
      /*
       * for (BaseModel dashboardData : cacheForKey.getAll(Arrays.asList("id+"))) {
       * logger.info((DashboardData) dashboardData); }
       * 
       * for (BaseModel dashboardData : cacheForKey.getAll(Arrays.asList("id-"))) {
       * logger.info((DashboardData) dashboardData); } for (BaseModel dashboardData :
       * cacheForKey.getAll(Arrays.asList("name+"))) { logger.info((DashboardData) dashboardData); }
       * for (BaseModel dashboardData : cacheForKey.getAll(Arrays.asList("name-"))) {
       * logger.info((DashboardData) dashboardData); }
       * 
       * DashboardFilters dashboardFilter = new DashboardFilters();
       * dashboardFilter.setColumn("active"); dashboardFilter.setValue("false");
       * sets.add(dashboardFilter); for (BaseModel dashboardData : cacheForKey.getAll(sets,
       * Arrays.asList("id-"))) { logger.info((DashboardData) dashboardData); }
       */
      dashboardFilter.setColumn("active");
      dashboardFilter.setValue("false");
      sets.clear();
      sets.add(dashboardFilter);
      for (BaseModel dashboardData : cacheForKey.getAll(sets, Arrays.asList("id"))) {
        logger.info((DashboardData) dashboardData);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
