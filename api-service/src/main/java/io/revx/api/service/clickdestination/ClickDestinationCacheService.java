/*
 * @author : ranjan-pritesh
 * 
 * @date: 32 Dec 2019
 */

package io.revx.api.service.clickdestination;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.repo.clickdestination.ClickDestinationRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.service.CacheService;


/**
 * The Class ClickDestinationCacheService.
 */
@Component
public class ClickDestinationCacheService {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(ClickDestinationCacheService.class);

  /** The cache service. */
  @Autowired
  CacheService cacheService;

  /** The login user details service. */
  @Autowired
  LoginUserDetailsService loginUserDetailsService;


  /** The model converter. */
  @Autowired
  ModelConverterService modelConverter;


  /** The repository. */
  @Autowired
  ClickDestinationRepository repository;



  /**
   * Fetch click destination.
   *
   * @param search the search
   * @param sort the sort
   * @param refresh the refresh
   * @return the list
   * @throws Exception the exception
   */
  List<BaseEntity> fetchClickDestination(SearchRequest search, String sort, boolean refresh)
      throws Exception {
    logger.debug("getting click destinations from cache..............");
    List<BaseEntity> listofClickDestination = new ArrayList<>();
    String cacheKeyClickDestination = getCacheKey();
    List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKeyClickDestination,
        search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));

    if (listData == null || refresh) {
      logger.debug("Could Not find click destination in cache...getting from DB.");
      List<ClickDestinationEntity> listofClickDestns =
          repository.findAllByLicenseeIdAndIsRefactored(loginUserDetailsService.getLicenseeId(),Boolean.TRUE);
      listofClickDestns.forEach(cd -> {
        try {
          listofClickDestination.add(modelConverter.convertFromClickDestEntity(cd));
        } catch (Exception e) {
          logger.debug("Exception occured while adding data to click destination cache list {} ",
              ExceptionUtils.getStackTrace(e));
        }
      });
    }

    if (!listofClickDestination.isEmpty()) {
      cacheService.populateCache(cacheKeyClickDestination, listofClickDestination, 86400000L,
          ClickDestination.class);
      listData = cacheService.fetchListCachedEntityData(cacheKeyClickDestination,
          search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));
    }
    listofClickDestination.clear();

    return listData;
  }



  /**
   * Gets the cache key.
   *
   * @return the cache key
   */
  public String getCacheKey() {
    return ApiConstant.CLICK_DESTINATION_CACHE_KEY + "_" + loginUserDetailsService.getLicenseeId();
  }



  /**
   * Gets the sort list.
   *
   * @param sort the sort
   * @return the sort list
   */
  private List<String> getSortList(String sort) {
    List<String> sortList = new ArrayList<>();
    if (StringUtils.isNotBlank(sort)) {
      for (String sortValue : sort.split(",")) {
        sortList.add(StringUtils.trim(sortValue));
      }
    }
    return sortList;
  }


  /**
   * Removes the cache for given key.
   */
  public void remove() {
    logger.debug("cache removed for key : {} }", getCacheKey());
    cacheService.removeCache(getCacheKey());
  }
}
