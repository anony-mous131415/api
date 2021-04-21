/*
 * @author:
 *
 * @date:
 */
package io.revx.api.service;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.repo.pixel.ConversionPixelRepository;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.campaign.PixelCacheService;
import io.revx.api.utils.ServiceUtils;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.UserUnAuthenticateException;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.Licensee;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.campaign.CampaignDTO;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.core.model.requests.ElasticResponse;
import io.revx.core.response.UserInfo;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.objs.FilterComponent;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.revx.core.constant.Constants.LICENSEE_ID;


@Component
public class ValidationService {

  private static Logger logger = LogManager.getLogger(ValidationService.class);

  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  @Autowired
  EntityESService elasticSearch;

  @Autowired
  ConversionPixelRepository pixelRepository;

  @Autowired
  PixelCacheService pixelCacheService;

  @Lazy
  @Autowired
  SmartCachingService caching;

  @Autowired
  ApplicationProperties properties;

  public void validateRequest(DashBoardEntity entity, DashboardRequest requestDTO)
      throws ValidationException {
    if (entity == null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Entity in query params. "});
    }
    validateRequest(requestDTO);
  }

  public void validateRequest(DashboardRequest requestDTO) throws ValidationException {
    UserInfo ui = loginUserDetailsService.getUserInfo();
    isInvalidDuration(requestDTO);
    if (ui != null) {
      validateFilters(requestDTO.getFilters(), ui);
    }
    if (StringUtils.isNotBlank(requestDTO.getGroupBy())) {
      GroupBy groupBy = GroupBy.fromString(requestDTO.getGroupBy());
      if (groupBy == null) {
        throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
            new Object[] {"group_by in the request jsonparams. "});
      }
      if (groupBy == GroupBy.HOUR) {
        if (requestDTO.getDuration().getEndTimeStamp()
            - requestDTO.getDuration().getStartTimeStamp() > 306000) {
          throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
              new Object[] {"starttime and endtime Range. "});
        }
      }
    }
  }

  private void validateFilters(List<DashboardFilters> filters, UserInfo ui)
      throws ValidationException {
    Set<Long> advIds = new HashSet<>();
    if (ui.getAdvertisers() != null)
      for (Advertiser adv : ui.getAdvertisers()) {
        advIds.add(adv.getId());
      }
    if (CollectionUtils.isNotEmpty(filters)) {
      for (DashboardFilters df : filters) {
        Filter filter = Filter.fromString(df.getColumn());
        if (filter == null
            || invalidFilter(ui.getSelectedLicensee(), advIds, filter, df.getValue())) {
          throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
              new Object[] {"TableFilters in the request jsonparams. "});
        }
      }
    }
  }

  public void validateFilters(List<DashboardFilters> filters) throws ValidationException {
    UserInfo ui = loginUserDetailsService.getUserInfo();
    validateFilters(filters, ui);
  }


  private void isInvalidDuration(DashboardRequest requestDTO) throws ValidationException {

    if (requestDTO == null || requestDTO.getDuration() == null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Duration Should be provided "});
    }
    Duration duration = requestDTO.getDuration();
    if (duration.getEndTimeStamp() == null || duration.getStartTimeStamp() == null
        || duration.getEndTimeStamp() < duration.getStartTimeStamp()) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"End time should be greater than starttime "});
    }

  }

  public boolean invalidFilter(Licensee selectedLicensee, Set<Long> advIds, Filter filter,
      String filterValue) {
    long value = 0;
    try {
      value = Long.parseLong(filterValue);
    } catch (Exception e) {
    }
    logger.debug(" selectedLicensee {} ,  advIds {} ::  {}: {} : {} :: {} ", selectedLicensee,
        advIds, filter, value, (value != selectedLicensee.getId()),
        advIds != null ? advIds.contains(value) : false);
    if (filter == Filter.LICENSEE_ID) {
      return value != selectedLicensee.getId();
    } else if (filter == Filter.ADVERTISER_ID) {
      return advIds != null && advIds.size() > 0 && !advIds.contains(value);
    }
    return false;
  }

  public Map<FilterType, Set<FilterComponent>> getFiltersMap(List<DashboardFilters> filterBy) {
    Map<FilterType, Set<FilterComponent>> perfFilterMap =
        new HashMap<FilterType, Set<FilterComponent>>();
    perfFilterMap.put(FilterType.TABLE_COLUMN, new HashSet<>());
    perfFilterMap.put(FilterType.DATA_FIELD, new HashSet<>());
    UserInfo ui = loginUserDetailsService.getUserInfo();
    if (ui != null) {
      perfFilterMap.get(FilterType.TABLE_COLUMN)
              .add(new FilterComponent(Filter.LICENSEE_ID, ui.getSelectedLicensee().getId()));
    } else {
      perfFilterMap.get(FilterType.TABLE_COLUMN)
              .add(new FilterComponent(Filter.LICENSEE_ID, caching.getLicenseeId()));
    }
    boolean isAdvertiserFilterApplied = false;
    if (CollectionUtils.isNotEmpty(filterBy)) {
      for (DashboardFilters df : filterBy) {
        Filter filter = Filter.fromString(df.getColumn());
        if (filter == Filter.ADVERTISER_ID)
          isAdvertiserFilterApplied = true;
        perfFilterMap.get(filter.getFilterType()).add(new FilterComponent(filter, df.getValue()));
      }
    }
    if (ui != null) {
      if (!isAdvertiserFilterApplied && ui.getAdvertisers() != null)
        for (Advertiser loginAdv : ui.getAdvertisers()) {
          perfFilterMap.get(FilterType.TABLE_COLUMN)
                  .add(new FilterComponent(Filter.ADVERTISER_ID, loginAdv.getId()));
        }
    }
    return perfFilterMap;
  }

  public Set<DashboardFilters> getDashBoardFilterForData(Set<FilterComponent> filters) {
    Set<DashboardFilters> filt = new HashSet<DashboardFilters>();
    if (filters != null && filters.size() > 0)
      for (FilterComponent fi : filters) {
        if (fi != null && fi.getField().getFilterType() == FilterType.DATA_FIELD) {
          filt.add(new DashboardFilters(fi));
        }
      }
    return filt;
  }

  public void validateIoPixel(BaseModel pixel) throws ValidationException {

    if (pixel == null || pixel.getId() == null)
      return;

    ConversionPixelEntity pixelEntity = pixelCacheService.fetchPixel(pixel.getId(), false);
    if (pixelEntity == null || pixelEntity.getId() == null)
      throw new ValidationException("Invalid Pixel id");

  }


  public void validateCampaign(CampaignDTO campaignPojo, boolean isUpdate,
      CampaignEntity campEntity) throws ValidationException {

    if (campaignPojo == null)
      throw new ValidationException("Campaign is missing");

    isBlank(campaignPojo.getName(), "Campaign name");

    if (campaignPojo.getAdvertiserId() == null)
      throw new ValidationException("Advertiser id is missing");

    if (isUpdate) {
      if (campaignPojo.getId() == null)
        throw new ValidationException("Campaign id is missing");

      if (campaignPojo.getAdvertiserId().intValue() != campEntity.getAdvertiserId())
        throw new ValidationException("Advertiser id is different for this campaign");
    }

    if (!isUpdate && Boolean.TRUE.equals(campaignPojo.getSkadTarget())) {
      DashboardFilters advertiserFilter = ServiceUtils.getFilterForKey(
              Filter.ADVERTISER_ID,String.valueOf(campaignPojo.getAdvertiserId()));
      List<DashboardFilters> dashboardFilters = new ArrayList<>();
      dashboardFilters.add(advertiserFilter);
      dashboardFilters.add(ServiceUtils.getSkadDashboardFilter());
      long campaignCount = elasticSearch.searchByGivenFilter(TablesEntity.CAMPAIGN,dashboardFilters).getTotalNoOfRecords();
      if (campaignCount >= Long.parseLong(properties.getSkadCampaignCount())) {
        throw new ValidationException("SKAD campaign count exceeds advertiser quota");
      }
    }

    if (campaignPojo.getLicenseeId() != null && campaignPojo.getLicenseeId() > 0)
      validateUserInfo(campaignPojo.getLicenseeId());

    if (isCampaignNameExist(campaignPojo, campEntity,isUpdate))
      throw new ValidationException("Campaign name already exist for given licensee");

    if (!isUpdate) {
      validateAdvFields(campaignPojo.getCurrency(), TablesEntity.CURRENCY);// validate currency
      validateAdvFields(campaignPojo.getRegion(), TablesEntity.COUNTRY);// validate region
    }

    if (campaignPojo.getStartTime() == null)
      throw new ValidationException("Start date is missing");
    if (campaignPojo.getEndTime() == null)
      throw new ValidationException("End date is missing");

    if (campaignPojo.getStartTime() != null && campaignPojo.getStartTime() != -1) {
      if (campaignPojo.getStartTime() < -1) {
        throw new ValidationException("Start date is not in correct format");
      }
      if (!isUpdate || (!campaignPojo.getStartTime().equals(campEntity.getStartTime()))) {
        Long currentTimeStamp = System.currentTimeMillis() / 1000;
        if (currentTimeStamp > campaignPojo.getStartTime()) {
          throw new ValidationException("Start date is less than the current date");
        }
      }
    }

    if (campaignPojo.getEndTime() != null && campaignPojo.getEndTime() != -1) {
      if (campaignPojo.getEndTime() < -1) {
        throw new ValidationException("End date is not in correct format");
      }
      if (campaignPojo.getStartTime() == -1) {
        Long currentTimeStamp = System.currentTimeMillis() / 1000;
        if ((currentTimeStamp) > campaignPojo.getEndTime()) {
          throw new ValidationException("End date is less than the current date");
        }
      }
      if (campaignPojo.getStartTime() > campaignPojo.getEndTime()) {
        throw new ValidationException("Start date is greater than the end date");
      }
    }

    if (campaignPojo.getAttributionRatio() == null) {
      throw new ValidationException("Attribution ratio is missing");
    }

    if (campaignPojo.getLifetimeBudget() == null) {
      throw new ValidationException("Lifetime budget is missing");
    }

    if (campaignPojo.getLifetimeBudget() != null
        && (new BigDecimal(-1).compareTo(campaignPojo.getLifetimeBudget())) != 0) {
      if (campaignPojo.getLifetimeBudget().compareTo(BigDecimal.ZERO) < 0) {
        throw new ValidationException("Lifetime budget is incorrect");
      }
    }

    if (campaignPojo.getDailyBudget() == null) {
      throw new ValidationException("Daily budget is missing");
    }

    if (campaignPojo.getDailyBudget() != null
        && (new BigDecimal(-1).compareTo(campaignPojo.getDailyBudget())) != 0) {
      if (campaignPojo.getDailyBudget().compareTo(BigDecimal.ZERO) < 0) {
        throw new ValidationException("Daily budget is incorrect");
      }
    }

    if (campaignPojo.getPricingId() == null)
      throw new ValidationException("Pricing Type is missing");

    if (campaignPojo.getFlowRate() == null)
      throw new ValidationException("Pricing value(Flow rate) is missing");

    if (campaignPojo.getFlowRate() != null
        && (campaignPojo.getFlowRate().compareTo(BigDecimal.ZERO) < 0)) {
      throw new ValidationException("Pricing value(Flow rate) is incorrect");
    }

    if (campaignPojo.getLifetimeDeliveryCap() != null && campaignPojo.getLifetimeDeliveryCap() <= 0)
      throw new ValidationException("Lifetime delivery cape is incorrect");

    if (campaignPojo.getDailyDeliveryCap() != null && campaignPojo.getDailyDeliveryCap() <= 0)
      throw new ValidationException("Daily delivery cape is incorrect");

    if (campaignPojo.getAttributionRatio() != null
        && ((campaignPojo.getAttributionRatio().compareTo(BigDecimal.ZERO) < 0)
            || (campaignPojo.getAttributionRatio().compareTo(new BigDecimal(100)) > 0)))
      throw new ValidationException("Attribution ratio is incorrect");

    if (campaignPojo.getObjective() == null)
      throw new ValidationException("Campaign objective is missing");

    if (campaignPojo.getPricingId() != null
        && campaignPojo.getPricingId() == ApiConstant.CPA_PRICING) {
      if (campaignPojo.getPixel() == null || campaignPojo.getPixel().getId() == null) {
        throw new ValidationException("Pixel Id is missing for CPA campaign");
      }
    }
  }

  public void validateUserInfo(long licenseeId) {
    if (loginUserDetailsService != null && loginUserDetailsService.getLicenseeId() != licenseeId)
      throw new UserUnAuthenticateException(
          ErrorCode.USER_DONT_HAVE_ACCESS_ON_GIVEN_LICENSEE_ERROR);
  }


  /**
   * Validate advertiser.
   *
   * @param advertiserPojo the advertiser pojo
   * @param isUpdate the is update
   * @throws ValidationException the validation exception
   */
  public void validateAdvertiser(AdvertiserPojo advertiserPojo, boolean isUpdate)
      throws ValidationException {

    if (advertiserPojo == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Advertiser is missing"});

    if (!isUpdate && advertiserPojo.getId() != null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Advertiser Id should not present for creation"});

    if (isUpdate && advertiserPojo.getId() == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Advertiser Id is missing"});

    if (isUpdate)
      isValidAdvertiserId(advertiserPojo.getId());

    // validate name
    isBlank(advertiserPojo.getName(), "Advertiser name");

    // validate licensee
    if (advertiserPojo.getLicenseeId() > 0)
      validateUserInfo(advertiserPojo.getLicenseeId());

    if (!isUpdate) {
      validateAdvFields(advertiserPojo.getCurrency(), TablesEntity.CURRENCY);// validate currency
      validateAdvFields(advertiserPojo.getRegion(), TablesEntity.COUNTRY);// validate region
      validateAdvFields(advertiserPojo.getTimeZone(), TablesEntity.TIMEZONE);// validate timezone
    }

    if (advertiserPojo.getLanguage() != null)
      validateAdvFields(advertiserPojo.getLanguage(), TablesEntity.LANGUAGE);// validate language

    validateAdvFields(advertiserPojo.getCategory(), TablesEntity.CATEGORY);// validate category
    validateAdvFields(advertiserPojo.getMMP(), TablesEntity.MMP);// validate mmp

    // validate email
    if (advertiserPojo.getEmail() != null && !isValidEmail(advertiserPojo.getEmail()))
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Email contact is invalid"});
    // validate domain
    isBlank(advertiserPojo.getDomain(), "Domain");
  }

  public void validateAdvFields(BaseModel field, TablesEntity entity) throws ValidationException {

    if (field == null || field.getId() == null
        || elasticSearch.searchById(entity, field.getId()) == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {entity.name() + " is missing"});
  }

  public boolean isCampaignNameExist(CampaignDTO campaignPojo, CampaignEntity campaignEntity, boolean isUpdate) {

    if(isUpdate && campaignPojo.getName().equalsIgnoreCase(campaignEntity.getName())) {
      return false;
    }

    boolean isDup = false;
    List<DashboardFilters> filters = new ArrayList<>();
    DashboardFilters df =
        new DashboardFilters(LICENSEE_ID, String.valueOf(loginUserDetailsService.getLicenseeId()));
    filters.add(df);

    ElasticResponse res = elasticSearch.searchByGivenFilter(TablesEntity.CAMPAIGN, filters);

    if (res != null && res.getTotalNoOfRecords() > 0) {
      List<String> namesList = res.getData().stream().map(BaseModel::getName).collect(Collectors.toList());
      isDup = isDuplicateName(namesList, campaignPojo);
    }
    return isDup;
  }

  private boolean isDuplicateName(List<String> namesList, CampaignDTO campaignPojo) {
    if (namesList != null && !namesList.isEmpty() && campaignPojo != null && !campaignPojo.getName().isEmpty()) {
      return namesList.contains(campaignPojo.getName());
    }
    return false;
  }

  public boolean isDuplicateName(BaseModel obj1, BaseModel obj2, boolean isUpdate) {
    logger.debug("Comparison : obj1  : {} && obj2 : {} ", obj1, obj2);
    logger.debug("Comparison : obj1 id  : {} & name {} && obj2 id : {} & name : {} ", obj1.getId(),
        obj1.getName(), obj2.getId(), obj2.getName());
    if (obj1.getId() != null) {
      if (isUpdate && obj2.getId() != null) {
        if (!obj1.getId().equals(obj2.getId()) && StringUtils.isNoneBlank(obj1.getName()) && StringUtils.isNoneBlank(obj2.getName())) {
            return StringUtils.equalsIgnoreCase(obj1.getName(), obj2.getName());
        }
      } else {
        if (StringUtils.isNoneBlank(obj1.getName()) && StringUtils.isNoneBlank(obj2.getName())) {
          return StringUtils.equalsIgnoreCase(obj1.getName(), obj2.getName());
        }
      }
    }
    return false;
  }

  public void isBlank(String str, String strToLog) throws ValidationException {
    if (StringUtils.isBlank(str))
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {strToLog + " is missing"});
  }

  public boolean isNotBlank(String str) {
    return StringUtils.isNotBlank(str);

  }

  public boolean isValidEmail(String email) {
    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@"
        + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";

    Pattern pat = Pattern.compile(emailRegex);
    if (email == null)
      return false;
    return pat.matcher(email).matches();
  }

  public void validateSettings(AdvertiserSettings settings, Long id) throws ValidationException {

    if (settings == null)
      throw new ValidationException("settings is null");

    if (settings.getAdvertiserId() != null && !settings.getAdvertiserId().equals(id))
      throw new ValidationException("advertiser id is invalid");

    if (settings.getMmp() != null && settings.getMmp().getId() != null
        && elasticSearch.searchById(TablesEntity.MMP, settings.getMmp().getId()) == null)
      throw new ValidationException("MMP is invalid");

    isValidAdvertiserId(id);

  }


  /**
   * Checks if is valid advertiser id.
   *
   * @param advertiserId the advertiser id
   * @throws ValidationException the validation exception
   */
  public void isValidAdvertiserId(Long advertiserId) throws ValidationException {
    UserInfo ui = loginUserDetailsService.getUserInfo();
    Long licence = null;
    boolean valid = false;

    if (advertiserId == null || advertiserId == 0)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Advertiser id is not valid for this licensee. "});

    try {
      licence =
          elasticSearch.searchDetailById(TablesEntity.ADVERTISER, advertiserId).getParent().getId();
    } catch (Exception e) {
      logger.error("ERROR : cannot find data from elastic for this advertiserId : {} !!  {}",
          advertiserId, ExceptionUtils.getStackTrace(e));
    }

    if (ui != null) {
      if (licence != null && licence.equals(ui.getSelectedLicensee().getId()))
        valid = true;
    } else {
      if (licence != null && licence.equals(caching.getLicenseeId())){
        valid = true;
      }
    }

    if (!valid)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Advertiser id is not valid for this licence. "});
  }


  /**
   * Checks if is valid advertiser id.
   *
   * @param ids the ids
   */
  public void isValidAdvertiserId(Set<Long> ids) {
    ids.forEach(i -> {
      try {
        isValidAdvertiserId(i);
      } catch (ValidationException e) {
        logger.debug("Exception occured during validating advertiser ids");
      }
    });

  }



  /**
   * Validate pixel request.
   *
   * @param pixel the pixel
   * @param id the id
   * @param isupdate the isupdate
   * @throws ValidationException the validation exception
   */
  public void validatePixelRequest(Pixel pixel, Integer id, boolean isupdate)
      throws ValidationException {

    if (pixel == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"pixel in query params. "});

    if (isupdate && pixel.getId() != null && !pixel.getId().equals(id.longValue()))
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"pixelId in query params. "});

    if (pixel.getAdvertiserPojo() == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"Advertiser in query params. "});

    isValidAdvertiserId(pixel.getAdvertiserPojo().getId());
  }



  public void isValidPixelId(Long pixelId) throws ValidationException {
    Optional<ConversionPixelEntity> pixelOptional = pixelRepository.findById(pixelId);
    if (!pixelOptional.isPresent())
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"pixelId in query params. "});

    isValidAdvertiserId(pixelOptional.get().getAdvertiserId());
  }

  /**
   * For populating default parameters based on MMP the payload is validated only against the advertiser,
   * as this is pre-populated before creation the other details need not be validated
   *
   * @param clickDestination
   * @throws ValidationException
   */
  public void validateClickDestForMmpParams(ClickDestination clickDestination) throws ValidationException{
    if (clickDestination == null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
              new Object[] {"click destination is missing"});
    }
    isValidAdvertiserId(clickDestination.getAdvertiserId());
  }

}
