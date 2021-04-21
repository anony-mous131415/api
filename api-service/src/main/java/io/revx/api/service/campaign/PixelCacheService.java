/*
 * @date: 12 Dec 2019
 */

package io.revx.api.service.campaign;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.repo.pixel.ConversionPixelRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.pixel.impl.ConversionPixelService;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.Filter;

/**
 * The Class PixelCacheService.
 */
@Component
public class PixelCacheService {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(PixelCacheService.class);

  /** The cache service. */
  @Autowired
  CacheService cacheService;

  /** The login user details service. */
  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  /** The elastic search. */
  @Autowired
  EntityESService elasticSearch;

  /** The model converter. */
  @Autowired
  ModelConverterService modelConverter;

  /** The service. */
  @Autowired
  ConversionPixelService service;

  /** The pixel repository. */
  @Autowired
  ConversionPixelRepository pixelRepository;

  /**
   * Fetch pixel.
   *
   * @param id the id
   * @param refresh the refresh
   * @return the pixel entity
   */
  public ConversionPixelEntity fetchPixel(Long id, Boolean refresh) {
    Long licenseeId = loginUserDetailsService.getLicenseeId();
    List<Long> advIds = loginUserDetailsService.getAdvertisers();

    logger.debug(
        "Inside fetchPixel method. Getting Pixel entity for campaign id : {} and licenseeId : {} and advIds : {}.",
        id, licenseeId, advIds);
    if (!refresh) {
      Set<DashboardFilters> filters = new HashSet<>();
      DashboardFilters filterId = new DashboardFilters();
      filterId.setColumn(Filter.ID.getColumn());
      filterId.setValue(String.valueOf(id));
      filters.add(filterId);
      DashboardFilters filterLicenseeId = new DashboardFilters();
      filterLicenseeId.setColumn(Filter.LICENSEE_ID.getColumn());
      filterLicenseeId.setValue(String.valueOf(licenseeId));
      filters.add(filterLicenseeId);

      if (CollectionUtils.isNotEmpty(advIds)) {
        for (Long adv : advIds) {
          DashboardFilters filterAdvId = new DashboardFilters();
          filterAdvId.setColumn(Filter.ADVERTISER_ID.getColumn());
          filterAdvId.setValue(String.valueOf(adv));
          filters.add(filterAdvId);
        }
      }

      @SuppressWarnings("rawtypes")
      List campaigns = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null);
      if (campaigns != null && campaigns.size() == 1) {
        logger.debug(
            "Inside fetchPixel method. Got {} pIXEL from cache for campaign id : {} and licenseeId {}",
            campaigns.size(), id, licenseeId);
        return (ConversionPixelEntity) campaigns.get(0);
      }
    }

    logger.debug(
        "Inside fetchPixel method. Getting Pixel from DB for campaign id : {} and licenseeId {} and advIds : {}. Returning it from DB.",
        id, licenseeId, advIds);
    if (advIds != null && !advIds.isEmpty()) {
      return pixelRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(id, licenseeId, advIds);
    } else {
      return pixelRepository.findByIdAndLicenseeId(id, licenseeId);
    }

  }



  /**
   * Fetch pixel. added by ranjan-pritesh
   *
   * @param search the search
   * @param sort the sort
   * @param refresh the refresh
   * @return the list
   * @throws Exception the exception
   */
  public List<BaseEntity> fetchPixel(SearchRequest search, String sort, boolean refresh)
      throws Exception {

    List<BaseEntity> listofPixel = new ArrayList<>();
    String cacheKeyPixel = getCacheKey();
    List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKeyPixel,
        search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));

    if (listData == null || refresh) {
      @SuppressWarnings("unchecked")
      Map<Long, Advertiser> mapOfAllAdvertiser = (Map<Long, Advertiser>) elasticSearch
          .search(TablesEntity.ADVERTISER, loginUserDetailsService.getElasticSearchTerm());
      for (Long m : mapOfAllAdvertiser.keySet()) {
        List<ConversionPixelEntity> pixels = pixelRepository.findAllByAdvertiserId(m,null);
        pixels.forEach(p -> {
          try {
            listofPixel.add(modelConverter.convertPixelToDTO(p));
          } catch (Exception e) {
            logger.debug("Exception occured while adding data to pixel list {} ", ExceptionUtils.getStackTrace(e));
          }
        });
      }
      listofPixel.forEach(p -> service.populateHourlyData((Pixel) p));
    }
    if (!listofPixel.isEmpty()) {
      cacheService.populateCache(cacheKeyPixel, listofPixel, 86400000L, Pixel.class);
      listData = cacheService.fetchListCachedEntityData(cacheKeyPixel,
          search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));
    }

    return listData;
  }


  
  /**
   * Fetch pixel. added by ranjan-pritesh
   *
   * @param search the search
   * @param sort the sort
   * @param refresh the refresh
   * @param advertiserId 
   * @return the list
   * @throws Exception the exception
   */
  public List<BaseEntity> fetchPixelAsync(Integer pageNum, Integer pageSize ,SearchRequest search, String sort, boolean refresh, Long advertiserId)
      throws Exception {
    
    List<BaseEntity> listOfPixel = new ArrayList<>();
    String cacheKeyPixel = getCacheKey(advertiserId);
     List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKeyPixel,
        search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));

     if(listData!=null && !refresh)
       return listData;
     
      @SuppressWarnings("unchecked")
      Pageable page = PageRequest.of(pageNum-1, pageSize);
      List<ConversionPixelEntity> pixels = pixelRepository.findAllByAdvertiserId(advertiserId,page);
      
      new Thread(() -> loadCache(advertiserId,cacheKeyPixel)).start();
      
      pixels.forEach(p -> {
        try {
          listOfPixel.add(modelConverter.convertPixelToDTO(p));
        } catch (Exception e) {
          logger.debug("Exception occured while adding data to pixel list {} ", ExceptionUtils.getStackTrace(e));
        }
      });
      listOfPixel.forEach(p -> service.populateHourlyData((Pixel) p));

    return listOfPixel;
  }
  

  /**
   * Gets the cache key.
   *
   * @return the cache key
   */
  public String getCacheKey() {
    return ApiConstant.PIXEL_CACHE_KEY + "_" + loginUserDetailsService.getLicenseeId();
  }


  /**
   * Gets the cache key.
   *
   * @return the cache key
   */
  public String getCacheKey(Long advertiser) {
    return ApiConstant.PIXEL_CACHE_KEY + "_" + advertiser;
  }
  
  /**
   * Gets the cache key.
   *
   * @return the cache key
   */
  public String getCacheKeyAsync(Long licenseeId) {
    return ApiConstant.PIXEL_CACHE_KEY + "_" + licenseeId;
  }


  public void loadCache(Long advertisers,String key) {

    logger.debug("Async Call to Load cache started for license : {} ..................",key);
    List<BaseEntity> listofPixel = new ArrayList<>();
    List<ConversionPixelEntity> pixels = pixelRepository.findAllByAdvertiserId(advertisers, null);
    pixels.forEach(p -> {
      try {
        listofPixel.add(modelConverter.convertPixelToDTO(p));
      } catch (Exception e) {
        logger.debug("Exception occured while adding data to pixel list {} ",
            ExceptionUtils.getStackTrace(e));
      }
    });
    listofPixel.forEach(p -> service.populateHourlyData((Pixel) p));

    if (!listofPixel.isEmpty()) {
      logger.debug("updating cache for Key : {} >>>>> ",key);
      cacheService.populateCache(key, listofPixel, 86400000L, Pixel.class);
    }

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
