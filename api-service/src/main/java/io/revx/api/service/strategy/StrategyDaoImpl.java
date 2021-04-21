package io.revx.api.service.strategy;

import java.util.LinkedHashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.strategy.StrategyRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.model.Aggregator;
import io.revx.core.model.AppCategoryMaster;
import io.revx.core.model.BaseModel;
import io.revx.core.model.Creative;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.targetting.AdSafetyBWType;
import io.revx.core.model.targetting.Site;
import io.revx.core.model.targetting.TargetingComponentDTO;


@Repository
public class StrategyDaoImpl {

  private static Logger logger = LogManager.getLogger(StrategyDaoImpl.class);


  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  @Autowired
  StrategyRepository strategyRepository;


  public StrategyEntity getStrategy(Long id) {
    long licenseeId = loginUserDetailsService.getLicenseeId();
    List<Long> advList = loginUserDetailsService.getAdvertisers();
    return strategyRepository.findByIdAndLicenseeIdAndAdvertiserIdIn(id, licenseeId, advList);
  }

  public List<AdSafetyBWList> getAdSafetyBWList(long licenseeId, AdSafetyBWType type) {
    return null;
  }



  public List<StrategyEntity> getStrategies(List<DashboardFilters> filter, Integer pageNo,
      Integer pageSize, LinkedHashMap<String, String> sortOrderMap) {
    return null;
  }


  public List<BaseModel> getStatesForCountry(Integer countryId) {
    return null;
  }


  public List<BaseModel> getCitiesForState(Integer stateId) {

    return null;
  }


  public List<BaseModel> getInventorySourcesForStrategy(Integer strategyId) {
    return null;
  }


  public List<Creative> getCreativesForStrategy(Integer strategyId, Integer pageNo,
      Integer pageSize, List<String> sort, String order) {
    return null;
  }


  public long getCreativesCount(Integer strategyId) {
    return 0;
  }


  public List<Pixel> getPixelsForStrategy(Integer strategyId, Integer pageNo, Integer pageSize,
      List<String> sort, String order) {
    return null;
  }


  public long getPixelsCount(Integer strategyId) {
    return 0;
  }


  public List<Site> getValidSitesList(String siteNamesString) {
    return null;
  }

  @SuppressWarnings("unchecked")

  public List<Site> getValidSitesList(List<String> siteNames) {
    return null;
  }


  public void activateStrategies(List<Integer> idsToSet) {

  }


  public void deactivateStrategies(List<Integer> idsToSet) {

  }


  public long getStrategiesByROITypeCount(Integer roiTypeId) {
    return 0;
  }


  public List<StrategyEntity> getStrategiesByROIType(Integer roiTypeId, Integer pageNo,
      Integer pageSize, List<String> sortList, String order) {
    return null;
  }


  public long getStrategiesByPricingTypeCount(Integer pricingTypeId) {
    return 0;
  }


  public List<StrategyEntity> getStrategiesByPricingType(Integer pricingTypeId, Integer pageNo,
      Integer pageSize, List<String> sortList, String order) {
    return null;
  }


  public long validateStrategyName(String name, Integer campaignId) {
    return 0;
  }


  public AppCategoryMaster getAppCategoryById(Integer appCategoryId) {
    return null;
  }


  public Aggregator getAggregatorById(Integer aggregatorId) {
    return null;
  }


  public List<TargetingComponentDTO> getExcludedBrowserTargetingComponentList() {
    return null;
  }
}
