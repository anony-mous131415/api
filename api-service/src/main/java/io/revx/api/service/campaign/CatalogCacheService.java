/*
 * @author: ranjan-pritesh
 * 
 * @date: 12 dec 2019
 */

package io.revx.api.service.campaign;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.catalog.CatalogService;
import io.revx.api.service.catalog.CatalogUtil;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.service.CacheService;

/**
 * The Class CatalogCacheService.
 */
@Component
public class CatalogCacheService {


  /** The cache service. */
  @Autowired
  CacheService cacheService;

  @Autowired
  CatalogService catalogService;

  @Autowired
  CatalogUtil catalogUtil;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  @Autowired
  ModelConverterService modelConverter;

  @Autowired
  FeedInfoRepository fiRepo;


  /**
   * Fetch pixel.
   *
   * @param search the search
   * @param sort the sort
   * @param refresh the refresh
   * @return the list
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public List<BaseEntity> fetchFeedInfo(Long advId, SearchRequest search, String sort,
      boolean refresh) throws Exception {

    List<BaseEntity> listofCatalogFeed = null;
    String cacheKey = getCacheKey(advId);

    if (search.getFilters() == null)
      search.setFilters(Collections.emptyList());

    List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKey,
        search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));

    if (listData == null || refresh) {
      listofCatalogFeed =
          (List<BaseEntity>) (List<?>) catalogService.getAllFeedsByAdvertiserId(advId);
    }

    if (listofCatalogFeed != null && !listofCatalogFeed.isEmpty()) {
      cacheService.populateCache(cacheKey, listofCatalogFeed, 86400000L, CatalogFeed.class);
      listData = cacheService.fetchListCachedEntityData(cacheKey,
          search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));
    }
    return listData;
  }


  /**
   * Fetch macros.
   *
   * @param advId the adv id
   * @param search the search
   * @param sort the sort
   * @param refresh the refresh
   * @return the list
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public List<BaseEntity> fetchMacros(Long advId, SearchRequest search, String sort,
      boolean refresh) throws Exception {

    List<BaseEntity> listofMacros = null;
    String cacheKey = getCacheKeyForMacros(advId);

    if (search.getFilters() == null)
      search.setFilters(Collections.emptyList());

    List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKey,
        search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));

    if (listData == null || refresh) {
      listofMacros = (List<BaseEntity>) (List<?>) catalogUtil.getMacroDTOListForAdvertiser(advId);
    }

    if (listofMacros != null && !listofMacros.isEmpty()) {
      cacheService.populateCache(cacheKey, listofMacros, 86400000L, Macro.class);
      listData = cacheService.fetchListCachedEntityData(cacheKey,
          search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));
    }
    return listData;
  }

  /**
   * Fetch ACVM.
   *
   * @param feedId the feed id
   * @param advId the adv id
   * @param search the search
   * @param sort the sort
   * @param refresh the refresh
   * @return the list
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public List<BaseEntity> fetchACVM(Long feedId, Long advId, SearchRequest search, String sort,
      boolean refresh) throws Exception {

    List<BaseEntity> listofACVM = null;
    String cacheKey = getCacheKeyForACVM(advId, feedId);

    if (search.getFilters() == null)
      search.setFilters(Collections.emptyList());

    List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKey,
        search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));

    if (listData == null || refresh) {
      listofACVM = (List<BaseEntity>) (List<?>) catalogUtil.getVariableMapping(feedId, advId);
    }

    if (listofACVM != null && !listofACVM.isEmpty()) {
      cacheService.populateCache(cacheKey, listofACVM, 86400000L, VariablesMappingDTO.class);
      listData = cacheService.fetchListCachedEntityData(cacheKey,
          search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));
    }
    return listData;
  }

  /**
   * Gets the cache key.
   *
   * @param advId the adv id
   * @return the cache key
   */
  public String getCacheKey(Long advId) {
    return ApiConstant.FEEDINFO_CACHE_KEY + "_" + advId;
  }

  /**
   * Gets the cache key for macros.
   *
   * @param advId the adv id
   * @return the cache key for macros
   */
  public String getCacheKeyForMacros(Long advId) {
    return ApiConstant.CATALOG_MACROS_CACHE_KEY + "_" + advId;
  }

  /**
   * Gets the cache key for ACVM.
   *
   * @param advId the adv id
   * @param feedId the feed id
   * @return the cache key for ACVM
   */
  public String getCacheKeyForACVM(Long advId, Long feedId) {
    return ApiConstant.ACVM_CACHE_KEY + "_" + advId + "_" + feedId;
  }


  /**
   * Gets the sort list.
   *
   * @param sort the sort
   * @return the sort list
   */
  private List<String> getSortList(String sort) {
    List<String> sortList = new ArrayList<String>();
    if (StringUtils.isNotBlank(sort)) {
      for (String sortValue : sort.split(",")) {
        sortList.add(StringUtils.trim(sortValue));
      }
    }
    return sortList;
  }
}
