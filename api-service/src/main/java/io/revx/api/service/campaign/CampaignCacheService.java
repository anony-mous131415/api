package io.revx.api.service.campaign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.revx.api.enums.DashboardEntities;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.ValidationService;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.Strategy;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.mysql.repo.campaign.CampaignRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.utility.Util;
import io.revx.api.utils.ServiceUtils;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.Filter;

import static io.revx.api.utility.Util.deleteDashboardCache;
import static io.revx.core.constant.Constants.LICENSEE_ID;


@Component
@SuppressWarnings({"rawtypes", "unchecked"})
public class CampaignCacheService {

  private static Logger logger = LogManager.getLogger(CampaignCacheService.class);

  @Autowired
  CampaignRepository campaignRepository;

  @Autowired
  CacheService cacheService;
  
  @Autowired
  LoginUserDetailsService loginUserDetailsService;
  
  @Autowired
  ApplicationProperties applicationProperties;
  
  @Autowired
  ModelConverterService modelConverterService;

  @Autowired
  EntityESService elasticService;

  @Autowired
  ValidationService validationService;
  
  public CampaignEntity findByIdAndLicenseeId(Long id, Boolean refresh){
    Long licenseeId = loginUserDetailsService.getLicenseeId();
    
    logger.debug("Inside findByIdAndLicenseeId method. Getting campaign entity for campaign id : {} and licenseeId {}", id, licenseeId);
    if(!refresh) {
      Set<DashboardFilters> filters = new HashSet<>();
      DashboardFilters filterId = new DashboardFilters();
      filterId.setColumn(Filter.ID.getColumn()); filterId.setValue(String.valueOf(id));
      filters.add(filterId);
      DashboardFilters filterLicenseeId = new DashboardFilters();
      filterLicenseeId.setColumn(Filter.LICENSEE_ID.getColumn()); filterLicenseeId.setValue(String.valueOf(licenseeId));
      filters.add(filterLicenseeId);
      
      List campaigns = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null); 
      if(campaigns != null && campaigns.size() == 1) {
        logger.debug("Inside findByIdAndLicenseeId method. Got {} campaign from cache for campaign id : {} and licenseeId {}", campaigns.size(), id, licenseeId);
        return (CampaignEntity) campaigns.get(0);
      }
    }
    logger.debug("Inside findByIdAndLicenseeId method. Getting campaign from DB for campaign id : {} and licenseeId {}. Returning it from DB.", id, licenseeId);
    return campaignRepository.findByIdAndLicenseeId(id, licenseeId);
    
  }
  
  public CampaignEntity fetchCampaign(Long id, Boolean refresh){
    Long licenseeId = loginUserDetailsService.getLicenseeId();
    List<Long> advIds = loginUserDetailsService.getAdvertisers();
    
    logger.debug("Inside fetchCampaign method. Getting campaign entity for campaign id : {} and licenseeId : {} and advIds : {}.", id, licenseeId, advIds);
    Set<DashboardFilters> filters = new HashSet<>();
    DashboardFilters filterId = new DashboardFilters();
    filterId.setColumn(Filter.ID.getColumn()); filterId.setValue(String.valueOf(id));
    filters.add(filterId);
    DashboardFilters filterLicenseeId = new DashboardFilters();
    filterLicenseeId.setColumn(Filter.LICENSEE_ID.getColumn()); filterLicenseeId.setValue(String.valueOf(licenseeId));
    filters.add(filterLicenseeId);
    
    if(CollectionUtils.isNotEmpty(advIds)) {
      for(Long adv : advIds) {
        DashboardFilters filterAdvId = new DashboardFilters();
        filterAdvId.setColumn(Filter.ADVERTISER_ID.getColumn()); filterAdvId.setValue(String.valueOf(adv));
        filters.add(filterAdvId);
      }
    }
    CampaignEntity campaignEntity = null;
    List campaigns = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null); 
    
    if(CollectionUtils.isEmpty(campaigns) || refresh) {
      logger.debug("Inside fetchCampaign method. Getting campaign from DB for campaign id : {} and licenseeId {} and advIds : {}. Returning it from DB.", id, licenseeId, advIds);
      if(advIds != null && advIds.size() > 0) {
        campaignEntity = campaignRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(id, licenseeId, advIds);
      }else {
        campaignEntity = campaignRepository.findByIdAndLicenseeId(id, licenseeId);
      }
      saveToCache(campaignEntity);
    }else if(campaigns != null && campaigns.size() == 1) {
      logger.debug("Inside fetchCampaign method. Got {} campaign from cache for campaign id : {} and licenseeId {}", campaigns.size(), id, licenseeId);
      campaignEntity = (CampaignEntity) campaigns.get(0);
    }
      
    return campaignEntity;
  }
  
  public List<CampaignEntity> fetchAllCampaign(Long advertiserId, int  pageNumber, int  pageSize, String  sort, String  search, Boolean refresh){
    Long licenseeId = loginUserDetailsService.getLicenseeId();
    List<String> sortList = null;
    if(StringUtils.isNoneBlank(sort))
      sortList = Arrays.asList(sort.split(","));
    
    logger.debug("Inside fetchCampaign method. Getting campaign entity for advertiserId: {} and licenseeId : {} and pageNumber : {} and pageSize : {} and sort : {} and search : {}", advertiserId, licenseeId, pageNumber, pageSize, sort, search);
      Set<DashboardFilters> filters = new HashSet<>();
      filters.add(ServiceUtils.getFilterForKey(Filter.ADVERTISER_ID, String.valueOf(advertiserId)));
      filters.add(ServiceUtils.getFilterForKey(Filter.LICENSEE_ID, String.valueOf(licenseeId)));
      filters.add(ServiceUtils.getFilterForSearch(search));
      
      List campaigns = cacheService.fetchListCachedEntityData(getCacheKey(), filters, sortList); 
      
      if(CollectionUtils.isEmpty(campaigns) || refresh) {
        logger.debug("Inside fetchCampaign method. Getting campaign from DB for advertiser id : {} and licenseeId {}. Returning it from DB.", advertiserId, licenseeId);
        Pageable pageable = ServiceUtils.getPageable(pageNumber, pageSize, sortList);
          
        if(StringUtils.isBlank(search))
          campaigns = campaignRepository.findByAdvertiserId(advertiserId, pageable);
        else if(Util.isNumeric(search))
          campaigns = campaignRepository.findByAdvertiserIdAndId(advertiserId, Long.parseLong(search), pageable);
        else
          campaigns = campaignRepository.findByAdvertiserIdAndNameIgnoreCaseContaining(advertiserId, search, pageable);
        
        saveToCache(campaigns);
        
      }else if(CollectionUtils.isNotEmpty(campaigns)) {
        modelConverterService.getSubList(campaigns, pageNumber, pageSize);
        logger.debug("Inside fetchCampaign method. Got {} campaign from cache for Advertiser id : {} and licenseeId {}", campaigns.size(), advertiserId, licenseeId);
      }
    
      return campaigns;
  }
  
  public CampaignEntity findByIdAndLicenseeIdAndAdvertiserIdIn(Long id, Boolean refresh){
    Long licenseeId = loginUserDetailsService.getLicenseeId();
    List<Long> advIds = loginUserDetailsService.getAdvertisers();
    
    logger.debug("Inside findByIdAndLicenseeId method. Getting campaign entity for campaign id : {} and licenseeId : {} and advIds : {}.", id, licenseeId, advIds);
    Set<DashboardFilters> filters = new HashSet<>();
    DashboardFilters filterId = new DashboardFilters();
    filterId.setColumn(Filter.ID.getColumn()); filterId.setValue(String.valueOf(id));
    filters.add(filterId);
    DashboardFilters filterLicenseeId = new DashboardFilters();
    filterLicenseeId.setColumn(Filter.LICENSEE_ID.getColumn()); filterLicenseeId.setValue(String.valueOf(licenseeId));
    filters.add(filterLicenseeId);

    if(CollectionUtils.isNotEmpty(advIds)) {
      for(Long adv : advIds) {
        DashboardFilters filterAdvId = new DashboardFilters();
        filterAdvId.setColumn(Filter.ADVERTISER_ID.getColumn()); filterAdvId.setValue(String.valueOf(adv));
        filters.add(filterAdvId);
      }
    }
    CampaignEntity campaignEntity = null;
    List campaigns = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null); 

    if(CollectionUtils.isEmpty(campaigns) || refresh) {
      campaignEntity = campaignRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(id, licenseeId, advIds);
      saveToCache(campaignEntity);
    }else if(campaigns != null && campaigns.size() == 1) {
      logger.debug("Inside fetchCampaign method. Got {} campaign from cache for campaign id : {} and licenseeId {}", campaigns.size(), id, licenseeId);
      campaignEntity = (CampaignEntity) campaigns.get(0);
    }
      
    return campaignEntity;
    
  }
  
  public void saveToCache() {
    logger.debug("Inside saveToCache method. Saving all campaign entity:");
    
    List campaigns = campaignRepository.findByLicenseeId(loginUserDetailsService.getLicenseeId());
    
    if(campaigns != null && campaigns.size() > 0) {
      cacheService.populateCache(getCacheKey(), campaigns, applicationProperties.getEhcacheTTLInMillis(), CampaignEntity.class);
      logger.debug("Inside saveToCache method. Saved {} number of campaigns in cache.", campaigns.size());
    }
    
  }

  public void saveToCache(List campaigns) {
    logger.debug("Inside saveToCache method. Saving campaign entity: {}", campaigns);
    
    if(CollectionUtils.isNotEmpty(campaigns)) {
      cacheService.populateCache(getCacheKey(), campaigns, applicationProperties.getEhcacheTTLInMillis(), CampaignEntity.class);
      logger.debug("Inside saveToCache method. Saved {} number of campaigns in cache.", campaigns.size());
    }
    
  }
  
  public void saveToCache(CampaignEntity entity) {
    logger.debug("Inside saveToCache method. Saving campaign entity: {}", entity);
    List campaigns = new ArrayList<>();
    campaigns.add(entity);
    if(CollectionUtils.isNotEmpty(campaigns)) {
      cacheService.populateCache(getCacheKey(), campaigns, applicationProperties.getEhcacheTTLInMillis(), CampaignEntity.class);
      logger.debug("Inside saveToCache method. Saved {} number of campaigns in cache. saved campaigns : {}", campaigns.size(), campaigns);
    }
    
  }

  /**
   * Removes the cache for given key.
   *
   * @param key the key
   */
  public void remove() {
    logger.debug("cache removed for key : {} }", getCacheKey());
    cacheService.removeCache(getCacheKey());
  }

  public String getCacheKey() {
    return ApiConstant.CAMPAIGN_CACHE_KEY+"_"+loginUserDetailsService.getLicenseeId();
  }

  public void removeDashboardListCache(Long id) {
    CampaignESDTO elasticEntity = elasticService.searchPojoById(TablesEntity.CAMPAIGN,id);
    String dashboardEntity = DashboardEntities.list.name();
    String parentEntityKey = LICENSEE_ID;
    long parentEntityValue = elasticEntity.getLicenseeId();
    deleteDashboardCache(dashboardEntity, DashBoardEntity.CAMPAIGN, parentEntityKey, parentEntityValue, validationService, cacheService);
    parentEntityKey = DashBoardEntity.ADVERTISER.getColumn();
    parentEntityValue = elasticEntity.getAdvertiserId();
    deleteDashboardCache(dashboardEntity,DashBoardEntity.CAMPAIGN, parentEntityKey, parentEntityValue, validationService, cacheService);
  }
}
