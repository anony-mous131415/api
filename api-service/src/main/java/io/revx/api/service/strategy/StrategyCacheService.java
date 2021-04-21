package io.revx.api.service.strategy;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.enums.DashboardEntities;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemCreativeEntity;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemCreativeRepository;
import io.revx.api.mysql.repo.strategy.StrategyRepository;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.SmartCachingService;
import io.revx.api.service.StrategyModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.utility.Util;
import io.revx.api.utils.ServiceUtils;
import io.revx.core.constant.Constants;
import io.revx.core.model.BaseModel;
import io.revx.core.model.Creative;
import io.revx.core.model.Strategy;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.Filter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;

import static io.revx.api.utility.Util.deleteDashboardCache;
import static io.revx.core.constant.Constants.THREE_DAYS_TIME_IN_MILLI_SECONDS;


@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class StrategyCacheService {

  private static Logger logger = LogManager.getLogger(StrategyCacheService.class);

  @Autowired
  StrategyRepository strategyRepository;

  @Autowired
  CacheService cacheService;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  @Autowired
  ApplicationProperties applicationProperties;

  @Autowired
  ModelConverterService modelConverterService;

  @Autowired
  StrategyModelConverterService strategyModelConverterService;

  @Autowired
  EntityESService elasticSearch;

  @Autowired
  AdvertiserLineItemPixelRepository lineItemPixelRepository;

  @Autowired
  AdvertiserLineItemCreativeRepository lineItemCreativeRepository;

  @Autowired
  @Lazy
  SmartCachingService smartCachingService;

  @Autowired
  ValidationService validationService;

  public StrategyEntity findByIdAndLicenseeId(Long id, Boolean refresh) {
    Long licenseeId = loginUserDetailsService.getLicenseeId();

    logger.debug(
        "Inside findByIdAndLicenseeId method. Getting Strategy entity for Strategy id : {} and licenseeId {}",
        id, licenseeId);
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

      List strategys = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null);
      if (strategys != null && strategys.size() == 1) {
        logger.debug(
            "Inside findByIdAndLicenseeId method. Got {} strategy from cache for strategy id : {} and licenseeId {}",
            strategys.size(), id, licenseeId);
        return (StrategyEntity) strategys.get(0);
      }
    }
    logger.debug(
        "Inside findByIdAndLicenseeId method. Getting strategy from DB for strategy id : {} and licenseeId {}. Returning it from DB.",
        id, licenseeId);
    return strategyRepository.findByIdAndLicenseeId(id, licenseeId);

  }

  public StrategyDTO fetchStrategy(Long id) {
    Long licenseeId;
    List<Long> advIds = null;
    if (loginUserDetailsService.getUserInfo() != null) {
       licenseeId = loginUserDetailsService.getLicenseeId();
       advIds = loginUserDetailsService.getAdvertisers();
    } else {
      licenseeId = smartCachingService.getLicenseeId();
    }
    StrategyDTO strategyDTO = null;

    logger.debug(
        "Inside fetchStrategy method. Getting strategy DTO for strategy id : {} and licenseeId : {} and advIds : {}.",
        id, licenseeId, advIds);

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

    List strategies = cacheService.fetchListCachedData(getCacheKey(id), filters, null);
    if (strategies != null && strategies.size() == 1) {
      logger.debug(
          "Inside fetchStrategy method. Got {} strategy from cache for strategy id : {} and licenseeId {}",
          strategies.size(), id, licenseeId);
      strategyDTO = (StrategyDTO) strategies.get(0);
      strategyDTO.setCampaign(elasticSearch.searchPojoById(TablesEntity.CAMPAIGN,strategyDTO.getCampaignId()));
      strategyDTO.setAdvertiser(elasticSearch.searchPojoById(TablesEntity.ADVERTISER,strategyDTO.getAdvertiserId()));
      strategyDTO.setLicensee(elasticSearch.searchPojoById(TablesEntity.LICENSEE,strategyDTO.getLicenseeId()));
      setPixelAndCreativesData(strategyDTO);
    }
    return strategyDTO;
  }

  private void setPixelAndCreativesData(StrategyDTO strategyDTO) {
    List<AdvertiserLineItemPixelEntity> strategyPixels = lineItemPixelRepository
            .findAllByStrategyId(strategyDTO.getId());
    List<Long> pixels = new ArrayList<>();
    for (AdvertiserLineItemPixelEntity sp : strategyPixels) {
      pixels.add(sp.getPixelId());
    }
    if (!pixels.isEmpty()) {
      List<BaseModel> pixelDTOList = (List<BaseModel>) elasticSearch.searchPojoByIdList(TablesEntity.PIXEL,
              pixels);
      strategyDTO.setPixels(pixelDTOList);
    }

    List<AdvertiserLineItemCreativeEntity> strategyCreatives = lineItemCreativeRepository
            .findAllByStrategyId(strategyDTO.getId());
    List<Long> creatives = new ArrayList<>();
    for (AdvertiserLineItemCreativeEntity sp : strategyCreatives) {
      creatives.add(sp.getCreativeId());
    }
    if (!creatives.isEmpty()) {
      List<BaseModel> creativesDTOList = (List<BaseModel>) elasticSearch
              .searchPojoByIdList(TablesEntity.CREATIVE, creatives);

      ListIterator<BaseModel> iter = creativesDTOList.listIterator();
      while (iter.hasNext()) {
        Creative creative = (Creative) iter.next();
        if (!creative.isActive() || !creative.isRefactor()) {
          iter.remove();
        }
      }
      strategyDTO.setCreatives(creativesDTOList);
    }
  }

  public StrategyEntity getStrategyEntity(Long id) {
    Long licenseeId;
    List<Long> advIds = null;
    if (loginUserDetailsService.getUserInfo() != null) {
      licenseeId = loginUserDetailsService.getLicenseeId();
      advIds = loginUserDetailsService.getAdvertisers();
    } else {
      licenseeId = smartCachingService.getLicenseeId();
    }
    logger.debug(
            "Inside fetchStrategyEntity method. Getting strategy from DB for strategy id : {} and licenseeId {} and advIds : {}. Returning it from DB.",
            id, licenseeId, advIds);
    if (advIds != null && !advIds.isEmpty()) {
      return strategyRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(id, licenseeId, advIds);
    } else {
      return strategyRepository.findByIdAndLicenseeId(id, licenseeId);
    }
  }

  public List<StrategyEntity> fetchAllStrategy(Long advertiserId, int pageNumber, int pageSize,
      String sort, String search, Boolean refresh) {
    Long licenseeId = loginUserDetailsService.getLicenseeId();
    List<String> sortList = null;
    if (StringUtils.isNoneBlank(sort))
      sortList = Arrays.asList(sort.split(","));

    logger.debug(
        "Inside fetchStrategy method. Getting strategy entity for advertiserId: {} and licenseeId : {} and pageNumber : {} and pageSize : {} and sort : {} and search : {}",
        advertiserId, licenseeId, pageNumber, pageSize, sort, search);
    if (!refresh) {
      Set<DashboardFilters> filters = new HashSet<>();
      DashboardFilters filterId = new DashboardFilters();
      filterId.setColumn(Filter.ADVERTISER_ID.getColumn());
      filterId.setValue(String.valueOf(advertiserId));
      filters.add(filterId);
      DashboardFilters filterLicenseeId = new DashboardFilters();
      filterLicenseeId.setColumn(Filter.LICENSEE_ID.getColumn());
      filterLicenseeId.setValue(String.valueOf(licenseeId));
      filters.add(filterLicenseeId);

      DashboardFilters filterSearch = new DashboardFilters();
      if (Util.isNumeric(search)) {
        filterSearch.setColumn(Filter.ID.getColumn());
        filterSearch.setValue(String.valueOf(search));
      } else {
        filterSearch.setColumn(Filter.NAME.getColumn());
        filterSearch.setValue(String.valueOf(search));
      }

      filters.add(filterSearch);

      List strategys = cacheService.fetchListCachedEntityData(getCacheKey(), filters, sortList);
      if (strategys != null && strategys.size() >= 1) {
        modelConverterService.getSubList(strategys, pageNumber, pageSize);
        logger.debug(
            "Inside fetchStrategy method. Got {} strategy from cache for Advertiser id : {} and licenseeId {}",
            strategys.size(), advertiserId, licenseeId);
        return (List<StrategyEntity>) strategys;
      }
    }

    logger.debug(
        "Inside fetchStrategy method. Getting strategy from DB for advertiser id : {} and licenseeId {}. Returning it from DB.",
        advertiserId, licenseeId);
    Pageable pageable = ServiceUtils.getPageable(pageNumber, pageSize, sortList);
    
    if (StringUtils.isBlank(search))
      return strategyRepository.findByAdvertiserId(advertiserId, pageable);

    if (Util.isNumeric(search))
      return strategyRepository.findByAdvertiserIdAndId(advertiserId, Long.parseLong(search),
          pageable);
    else
      return strategyRepository.findByAdvertiserIdAndNameIgnoreCaseContaining(advertiserId, search,
          pageable);

  }

  public StrategyEntity findByIdAndLicenseeIdAndAdvertiserIdIn(Long id, Boolean refresh) {
    Long licenseeId = loginUserDetailsService.getLicenseeId();
    List<Long> advIds = loginUserDetailsService.getAdvertisers();

    logger.debug(
        "Inside findByIdAndLicenseeId method. Getting strategy entity for strategy id : {} and licenseeId : {} and advIds : {}.",
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

      List strategys = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null);
      if (strategys != null && strategys.size() == 1) {
        logger.debug(
            "Inside findByIdAndLicenseeId method. Got {} strategy from cache for strategy id : {} and licenseeId {}",
            strategys.size(), id, licenseeId);
        return (StrategyEntity) strategys.get(0);
      }
    }
    logger.debug(
        "Inside findByIdAndLicenseeId method. Getting strategy from DB for strategy id : {} and licenseeId {} and advIds : {}. Returning it from DB.",
        id, licenseeId, advIds);
    return strategyRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(id, licenseeId, advIds);
  }

  public Optional<StrategyEntity> findById(Long id) {
    logger.debug(
        "Inside findByIdAndLicenseeId method. Getting strategy entity for strategy id : {}", id);
    Set<DashboardFilters> filters = new HashSet<>();
    DashboardFilters filter = new DashboardFilters();
    filter.setColumn(Filter.ID.getColumn());
    filter.setValue(String.valueOf(id));
    filters.add(filter);

    List strategys = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null);
    if (strategys != null && strategys.size() == 1) {
      logger.debug(
          "Inside findByIdAndLicenseeId method. Got {} strategy from cache for strategy id : {}",
          strategys.size(), id);
      return (Optional<StrategyEntity>) strategys.get(0);
    } else {
      logger.debug(
          "Inside findByIdAndLicenseeId method. Couldn't get strategy from cache for strategy id : {}. Returning it from DB.",
          id);
      return strategyRepository.findById(id);
    }
  }


  public void saveToCache() {
    logger.debug("Inside saveToCache method. Saving all strategy entity:");

    List strategys = strategyRepository.findByLicenseeId(loginUserDetailsService.getLicenseeId());

    if (strategys != null && strategys.size() > 0) {
      cacheService.populateCache(getCacheKey(), strategys,
          applicationProperties.getEhcacheTTLInMillis(), StrategyEntity.class);
      logger.debug("Inside saveToCache method. Saved {} number of strategys in cache.",
          strategys.size());
    }

  }

  public String getCacheKey() {
    return ApiConstant.STRATEGY_CACHE_KEY + "_" + loginUserDetailsService.getLicenseeId();
  }

  public String getCacheKey(long id) {
    long licenseeId;
    if (loginUserDetailsService.getUserInfo() != null) {
      licenseeId = loginUserDetailsService.getLicenseeId();
    } else{
      licenseeId = smartCachingService.getLicenseeId();
    }
    StringBuilder stringBuilder;
    stringBuilder = new StringBuilder();
    stringBuilder.append(ApiConstant.STRATEGY_CACHE_KEY)
            .append(Constants.CACHE_KEY_SEPRATOR).append(id)
            .append(Constants.CACHE_KEY_SEPRATOR).append(ApiConstant.LICENSEE_CACHE_KEY)
            .append(Constants.CACHE_KEY_SEPRATOR).append(licenseeId);
    return stringBuilder.toString();
  }

  public void saveStrategyDTOToCache(long id,StrategyDTO strategyDTO) {
    logger.debug("Inside saveStrategyDTOToCache method. Saving strategy DTO object");
    List<BaseModel> strategy = new ArrayList<>();
    strategy.add(strategyDTO);
    if (CollectionUtils.isNotEmpty(strategy)) {
      cacheService.populateCache(getCacheKey(id),strategy,THREE_DAYS_TIME_IN_MILLI_SECONDS);
      logger.debug("Inside saveToCache method. Saved {} number of strategies in cache. saved strategies : {}"
              , strategy.size(), strategy);
    }
  }

  public void removeCacheKey(long id) {
    String cacheKey = getCacheKey(id);
    logger.debug("cache removed for key : {} ",cacheKey);
    cacheService.removeBaseModelCache(cacheKey);
  }

    public void removeDashboardListCache(Long id) {
      Strategy elasticEntity = elasticSearch.searchPojoById(TablesEntity.STRATEGY,id);
      String dashboardEntity = DashboardEntities.list.name();
      String parentEntityKey = DashBoardEntity.CAMPAIGN.getColumn();
      long parentEntityValue = elasticEntity.getCampaignId();
      deleteDashboardCache(dashboardEntity, DashBoardEntity.STRATEGY, parentEntityKey,
              parentEntityValue, validationService, cacheService);
    }

}
