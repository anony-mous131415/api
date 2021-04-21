/*
 * @author: ranjan-pritesh
 * 
 * @date:27th Nov 2019
 */
package io.revx.api.service.advertiser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import io.revx.api.utils.ServiceUtils;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.SkadTargetPrivileges;

import io.revx.api.service.appsettings.AppSettingsService;
import io.revx.core.enums.AppSettingsKey;
import io.revx.core.model.AppSettingsDTO;

import io.revx.core.response.ResponseMessage;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.Status;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.amtdb.entity.DataPixelsEntity;
import io.revx.api.mysql.amtdb.entity.SegmentType;
import io.revx.api.mysql.amtdb.repo.DataPixelsRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.mysql.repo.advertiser.AdvertiserToPixelRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.audience.impl.AudienceServiceImpl;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.constant.Constants;
import io.revx.core.enums.DataSourceType;
import io.revx.core.enums.DurationUnit;
import io.revx.core.enums.Operator;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.RuleComponentDTO;
import io.revx.core.model.audience.RuleDTO;
import io.revx.core.model.audience.UserDataType;
import io.revx.core.model.pixel.Tag;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;

import static io.revx.core.constant.Constants.ADVERTISER_ID;
import static io.revx.core.constant.Constants.CAMPAIGN_ID;

/**
 * The Class AdvertiserService.
 */
@Component
public class AdvertiserService {


  private static final Logger logger = LogManager.getLogger(AdvertiserService.class);

  /** The advertiser repository. */
  @Autowired
  AdvertiserRepository advertiserRepository;

  @Autowired
  AdvertiserToPixelRepository advToPixelRepo;

  /** The validation service. */
  @Autowired
  ValidationService validationService;

  /** The model converter. */
  @Autowired
  ModelConverterService modelConverter;

  @Autowired
  DataPixelsRepository dataPixelsRepository; 
  
  @Autowired
  DataPixelServiceImpl dataPixelService;
  
  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  /** The properties. */
  @Autowired
  ApplicationProperties properties;

  @Autowired
  CustomESRepositoryImpl elastic;

  @Autowired
  EntityESService elasticSearch;
  
  @Autowired
  AudienceServiceImpl audService;

  /** The ast service. */
  @Autowired
  ASTService astService;

  @Autowired
  AdvertiserCacheService advertiserCacheService;

  @Autowired
  AppSettingsService appSettingsService;

  /**
   * Find all.
   *
   * @return the list
   */
  public List<AdvertiserEntity> findAll() {
    return advertiserRepository.findAll();
  }



  /**
   * Creates the.
   *
   * @param advertiserPojo the advertiser pojo
   * @return the api response object
   * @throws Exception the exception
   */
  @Transactional
  public ApiResponseObject<AdvertiserPojo> create(AdvertiserPojo advertiserPojo) throws Exception {
    validationService.validateAdvertiser(advertiserPojo, false);
    AdvertiserEntity advEntity = modelConverter.populateAdvEntity(advertiserPojo);
    AdvertiserEntity advEntityFromDb = advertiserRepository.save(advEntity);
    logger.debug("Advertiser created : {} ", advEntityFromDb);
    dataPixelService.createAdvertiserToPixel(
        new BaseModel(advEntityFromDb.getId(), advEntityFromDb.getAdvertiserName()),
        DataSourceType.PIXEL_LOG);
    elastic.save(modelConverter.populateAdvForElastic(advEntityFromDb), TablesEntity.ADVERTISER);
    AppSettingsDTO defaultLogoDetails = saveDefaultLogoDetails(advertiserPojo, advEntityFromDb);
    AdvertiserPojo adv = modelConverter.populateAdvertiserFromEntity(advEntityFromDb);
    adv.setDefaultLogoDetails(defaultLogoDetails);
    ApiResponseObject<AdvertiserPojo> resp = new ApiResponseObject<>();
    generateClickerAudience(adv);
    resp.setRespObject(adv);
    return resp;
  }

  private AppSettingsDTO saveDefaultLogoDetails(AdvertiserPojo advertiserPojo, AdvertiserEntity entity) throws ValidationException,
          JsonProcessingException {
    List<AppSettingsDTO> defaultLogoDetails = new ArrayList<>();
    AppSettingsDTO settingsDTO = advertiserPojo.getDefaultLogoDetails();
    settingsDTO.setAdvertiserId(entity.getId());
    defaultLogoDetails.add(settingsDTO);
    defaultLogoDetails = appSettingsService.createSettings(defaultLogoDetails).getRespObject();
    if (defaultLogoDetails == null || defaultLogoDetails.size() != 1) {
      throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR, "Unable to save default logo");
    }
    return defaultLogoDetails.get(0);
  }

  private void generateClickerAudience(AdvertiserPojo adv) {

    AudienceDTO audience = new AudienceDTO();
    audience.setName(adv.getName() + " Clickers " + "R1");
    audience.setDescription("Clicker Audience");
    audience.setCreationTime(System.currentTimeMillis() / 1000);
    audience.setCreatedBy(loginUserDetailsService.getUserInfo().getUserId());
    audience.setLicensee(adv.getLicensee());
    audience.setAdvertiser(new BaseModel(adv.getId(), adv.getName()));
    audience.setSegmentType(SegmentType.CLICKER.id);
    audience.setDuration(1L);
    audience.setActive(Boolean.TRUE);
    audience.setDurationUnit(DurationUnit.DAY);
    audience.setUserDataType(UserDataType.MOBILE_APP.id);
    audience.setDataSourceType(DataSourceType.PIXEL_LOG.id);
    
    RuleDTO rule = new  RuleDTO();
    rule.setNegate(Boolean.FALSE);
    rule.setSimpleExpr(Boolean.FALSE);
    rule.setOperator(Operator.OR);
    
    List<RuleDTO> ruleList = new ArrayList<>();   
    RuleDTO ruleInner = new  RuleDTO();
    ruleInner.setNegate(Boolean.FALSE);
    ruleInner.setSimpleExpr(Boolean.FALSE);
    ruleInner.setOperator(Operator.AND);
    ruleInner.setRuleElement(null);
    
    List<RuleDTO> innerruleExpressionList = new ArrayList<>(); 
    RuleDTO ruleInnerMost = new  RuleDTO();
    ruleInnerMost.setNegate(Boolean.FALSE);
    ruleInnerMost.setSimpleExpr(Boolean.TRUE);
    
    RuleComponentDTO ruleElement= new RuleComponentDTO();
    ruleElement.setFilterId(1L); 
    ruleElement.setOperatorId(1L);
    ruleElement.setValue("can-never-be-true");
    
    ruleInnerMost.setRuleElement(ruleElement);
    innerruleExpressionList.add(ruleInnerMost);
    
    ruleInner.setRuleExpressionList(innerruleExpressionList);
    
    ruleList.add(ruleInner);
    rule.setRuleExpressionList(ruleList);
    
    audience.setRuleExpression(rule);
    try {
      audService.createAudience(audience);
      audience.setName(adv.getName() + " Clickers " + "R7");
      audience.setDuration(7L);
      audService.createAudience(audience);
      audience.setName(adv.getName() + " Clickers " + "R30");
      audience.setDuration(30L);
      audService.createAudience(audience);
    } catch (Exception e) {
      logger.debug("Exception occured while creating audience");
      e.printStackTrace();
    }
    
  }



  /**
   * Update.
   *
   * @param advertiserPojo the advertiser pojo
   * @return the api response object
   * @throws Exception the exception
   */
  @Transactional
  public ApiResponseObject<AdvertiserPojo> update(AdvertiserPojo advertiserPojo) throws Exception {
    AdvertiserEntity advEntity = null;
    validationService.validateAdvertiser(advertiserPojo, true);
    Optional<AdvertiserEntity> entity = advertiserRepository.findById(advertiserPojo.getId());
    if (!entity.isPresent())
      throw new ValidationException(Constants.INVALID_ADV_ID);

    advEntity = entity.get();
    modelConverter.updateAdvEntity(advertiserPojo, advEntity);
    AdvertiserEntity advEntityFromDb = advertiserRepository.save(advEntity);
    AppSettingsDTO defaultLogoDetails = null;
    if (advertiserPojo.getDefaultLogoDetails() != null ) {
      if (advertiserPojo.getDefaultLogoDetails().getId() == null) {
        defaultLogoDetails = saveDefaultLogoDetails(advertiserPojo, advEntityFromDb);
      } else {
        defaultLogoDetails = updateDefaultLogoDetails(advertiserPojo);
      }
    }
    logger.debug("Advertiser updated : {} ", advEntityFromDb);
    elastic.save(modelConverter.populateAdvForElastic(advEntityFromDb), TablesEntity.ADVERTISER);
    AdvertiserPojo adv = modelConverter.populateAdvertiserFromEntity(advEntityFromDb);
    advertiserCacheService.removeFromCache(adv.id);
    advertiserCacheService.removeDashboardListCache(adv.id);
    ApiResponseObject<AdvertiserPojo> response = new ApiResponseObject<>();
    adv.setDefaultLogoDetails(defaultLogoDetails);
    response.setRespObject(adv);
    return response;
  }

  private AppSettingsDTO updateDefaultLogoDetails(AdvertiserPojo advertiserPojo) throws ValidationException {
    List<AppSettingsDTO> defaultLogoDetails = new ArrayList<>();
    defaultLogoDetails.add(advertiserPojo.getDefaultLogoDetails());
    defaultLogoDetails = appSettingsService.updateSettings(defaultLogoDetails).getRespObject();
    if (defaultLogoDetails == null || defaultLogoDetails.size() != 1) {
      throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR, "Unable to save/update default logo");
    }
    return defaultLogoDetails.get(0);
  }


  /**
   * Gets the by id.
   *
   * @param id the id
   * @return the by id
   * @throws Exception the exception
   */
  public ApiResponseObject<AdvertiserPojo> getById(Long id, boolean refresh) throws Exception {
    validationService.isValidAdvertiserId(id);
    AdvertiserPojo advertiserPojo = advertiserCacheService.fetchAdvertiser(id, refresh);
    List<AppSettingsKey> settingsKeys = Collections.singletonList(AppSettingsKey.DEFAULT_LOGO);
    List<AppSettingsDTO> defaultLogoDetails = appSettingsService.getSettings(settingsKeys, id).getRespObject();
    if (defaultLogoDetails != null && defaultLogoDetails.size() == 1) {
      advertiserPojo.setDefaultLogoDetails(defaultLogoDetails.get(0));
    }
    ApiResponseObject<AdvertiserPojo> response = new ApiResponseObject<>();
    response.setRespObject(advertiserPojo);
    return response;
  }



  /**
   * Update.
   *
   * @param settings the settings
   * @param id the id
   * @return the api response object
   * @throws Exception the exception
   */
  public ApiResponseObject<AdvertiserSettings> update(AdvertiserSettings settings, Long id)
      throws Exception {
    validationService.validateSettings(settings, id);
    Optional<AdvertiserEntity> entity = advertiserRepository.findById(id);

    if (!entity.isPresent())
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"can not find advertiser for this advertiser Id."});

    AdvertiserEntity advEntity = entity.get();
    modelConverter.updateAdvSettings(settings, advEntity);
    AdvertiserEntity advEntityFromDb = advertiserRepository.save(advEntity);
    AdvertiserSettings settingResp = modelConverter.populateAdvSettingsFromEntity(advEntityFromDb);
    ApiResponseObject<AdvertiserSettings> response = new ApiResponseObject<>();
    response.setRespObject(settingResp);
    return response;
  }


  /**
   * Gets the settings by id.
   *
   * @param id the id
   * @return the settings by id
   * @throws Exception the exception
   */
  public ApiResponseObject<AdvertiserSettings> getSettingsById(Long id) throws Exception {
    if (id == null)
      throw new ValidationException(Constants.INVALID_ADV_ID);
    validationService.isValidAdvertiserId(id);
    Optional<AdvertiserEntity> entity = advertiserRepository.findById(id);
    if (!entity.isPresent())
      throw new ValidationException(Constants.INVALID_ADV_ID);
    AdvertiserSettings settings = modelConverter.populateAdvSettingsFromEntity(entity.get());
    ApiResponseObject<AdvertiserSettings> response = new ApiResponseObject<>();
    response.setRespObject(settings);
    return response;
  }

  /**
   * Activate.
   *
   * @param idList the id list
   * @return the api response object
   * @throws Exception the exception
   */
  @Transactional
  public ApiResponseObject<Map<Long, ResponseMessage>> activate(String idList) throws Exception {

    Set<Long> ids = modelConverter.getSetOfIds(idList);
    validationService.isValidAdvertiserId(ids);
    List<AdvertiserEntity> advertisers = advertiserRepository.findByIdIn(ids);
    Map<Long, ResponseMessage> result = new HashedMap<>();
    List<Long> inactiveIds = new ArrayList<>();
    for (AdvertiserEntity a : advertisers) {
      if (!a.getIsActive()) {
        inactiveIds.add(a.getId());
      } else
        result.put(a.getId(),
            new ResponseMessage(Constants.ID_ALREADY_ACTIVE, Constants.MSG_ID_ALREADY_ACTIVE));
    }

    for (Long id : ids) {
      if (!inactiveIds.contains(id) && !result.containsKey(id))
        result.put(id, new ResponseMessage(Constants.ID_MISSING, Constants.MSG_ID_MISSING));
    }

    if (!inactiveIds.isEmpty()) {
      for (Long i : inactiveIds) {
        advertiserRepository.activate(i);
        activateInElasticSearch(i);
        result.put(i, new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS));
        advertiserCacheService.removeFromCache(i);
        advertiserCacheService.removeDashboardListCache(i);
      }
    }

    ApiResponseObject<Map<Long, ResponseMessage>> response = new ApiResponseObject<>();
    logger.debug("activation result : {} ", result);

    response.setRespObject(result);
    return response;
  }



  private void activateInElasticSearch(Long i) throws JsonProcessingException {
    Advertiser adv = (Advertiser) elastic.findDetailById(TablesEntity.ADVERTISER.getElasticIndex(),
        String.valueOf(i), TablesEntity.ADVERTISER.getElasticPojoClass());

    adv.setActive(Boolean.TRUE);
    elastic.save(adv, TablesEntity.ADVERTISER);
  }

  private void deActivateInElasticSearch(Long i) throws JsonProcessingException {

    Advertiser adv = (Advertiser) elastic.findDetailById(TablesEntity.ADVERTISER.getElasticIndex(),
        String.valueOf(i), TablesEntity.ADVERTISER.getElasticPojoClass());

    adv.setActive(Boolean.FALSE);
    elastic.save(adv, TablesEntity.ADVERTISER);
  }



  /**
   * Deactivate.
   *
   * @param idList the id list
   * @return the api response object
   * @throws Exception the exception
   */
  @Transactional
  public ApiResponseObject<Map<Long, ResponseMessage>> deactivate(String idList) throws Exception {

    Set<Long> ids = modelConverter.getSetOfIds(idList);
    List<AdvertiserEntity> advertisers = advertiserRepository.findByIdIn(ids);
    Map<Long, ResponseMessage> result = new HashedMap<>();
    List<Long> activeIds = new ArrayList<>();


    for (AdvertiserEntity a : advertisers) {
      if (a.getIsActive()) {
        activeIds.add(a.getId());
      } else
        result.put(a.getId(),
            new ResponseMessage(Constants.ID_ALREADY_INACTIVE, Constants.MSG_ID_ALREADY_INACTIVE));
    }
    for (Long id : ids) {
      if (!activeIds.contains(id) && !result.containsKey(id))
        result.put(id, new ResponseMessage(Constants.ID_MISSING, Constants.MSG_ID_MISSING));
    }
    if (!activeIds.isEmpty()) {
      for (Long i : activeIds) {
        advertiserRepository.deActivate(i);
        deActivateInElasticSearch(i);
        result.put(i, new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS));
        advertiserCacheService.removeFromCache(i);
        advertiserCacheService.removeDashboardListCache(i);
      }
    }

    ApiResponseObject<Map<Long, ResponseMessage>> response = new ApiResponseObject<>();
    logger.debug("deactivation result : {} ", result);

    response.setRespObject(result);
    return response;
  }


  /**
   * Generate ast for advertiser.
   *
   * @param advertiserId the advertiser id
   * @return the api response object
   * @throws ApiException the api exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ApiResponseObject<Boolean> generateAstForAdvertiser(Long advertiserId)
      throws ApiException, IOException {
    validationService.isValidAdvertiserId(advertiserId);
    Boolean result = astService.generateAndUploadAstTagToCdn(advertiserId);
    ApiResponseObject<Boolean> response = new ApiResponseObject<>();
    response.setRespObject(result);
    return response;
  }

  /**
   * Generate ast for all advertiser.
   *
   * @return the api response object
   * @throws ApiException the api exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public ApiResponseObject<Boolean> generateAstForAllAdvertiser() throws ApiException, IOException {
    Boolean result = astService.generateAndUploadAst();
    ApiResponseObject<Boolean> response = new ApiResponseObject<>();
    response.setRespObject(result);
    return response;
  }


  /**
   * Gets the all.
   *
   * @param search the search
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param sort the sort
   * @return the all
   */
  public ApiResponseObject<ApiListResponse<AdvertiserPojo>> getAll(SearchRequest search,
      Integer pageNum, Integer resultPerPage, String sort) {
    return null;
  }


  /**
   * Gets the smart tag.
   *
   * @param advertiserId the advertiser id
   * @return the smart tag
   * @throws ValidationException the validation exception
   */
  public ApiResponseObject<Tag> getSmartTag(Long advertiserId) throws Exception {
    validationService.isValidAdvertiserId(advertiserId);
    ApiResponseObject<Tag> response = new ApiResponseObject<>();
    Tag tagDto = new Tag();
    try {
      Optional<AdvertiserToPixelEntity> advertiserToPixelOptional =
          advToPixelRepo.getASTpixelByAdvId(Status.ACTIVE, advertiserId);

      if (!advertiserToPixelOptional.isPresent()) {
        throw new ApiException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
            new Object[] {"No pixel mapped with this advertiser "});
      }

      Optional<DataPixelsEntity> pixels = dataPixelsRepository
          .findByIdAndStatus(advertiserToPixelOptional.get().getPixelId(), Status.ACTIVE);

      if (!pixels.isPresent())
        throw new ApiException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
            new Object[] {"No Amtdb pixels mapped with this pixelId "});

      Map<String, String> context = new HashMap<>();
      context.put(Constants.PIXEL_TMPL, Long.toString(advertiserToPixelOptional.get().getPixelId()));
      context.put(Constants.PIXEL_HASH_TMPL, pixels.get().getHash());
      context.put(Constants.ADVERTISER_TMPL, Long.toString(advertiserId));

      tagDto.setId(advertiserId);
      tagDto.setJsSource(
          getOriginServerSmartTagDirFileValue("/templates/tag/template_web_js_tag.tmpl", context));
      tagDto.setImgSource(getOriginServerSmartTagDirFileValue(
          "/templates/tag/template_web_image_tag.tmpl", context));
      tagDto.setAppImgSource(getOriginServerSmartTagDirFileValue(
          "/templates/tag/template_app_image_tag.tmpl", context));

      logger.trace("Smart Tag generated : {}", tagDto);

    } catch (Exception e) {
      logger.error("Error in generating smart tagfor the advertiser :{} and error message : {}",
          advertiserId, e.getMessage());
      throw e;
    }

    response.setRespObject(tagDto);
    return response;
  }



  /**
   * Gets the origin server smart tag dir file value.
   *
   * @param relativePath the relative path
   * @param context the context
   * @return the origin server smart tag dir file value
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private String getOriginServerSmartTagDirFileValue(String relativePath,
      Map<String, String> context) throws IOException {
    File file = new File(
        new StringBuilder(properties.getSmartTagOriginDirectory()).append(relativePath).toString());
    String out = FileUtils.readFileToString(file);
    for (Map.Entry<String, String> entry : context.entrySet())
      out = out.replace(entry.getKey(), entry.getValue());
    return out;
  }

  /**
   * This method determines which for which entity the skad privileges needs to be checked
   * based on the column in search request. The count against respective entity is made extensible
   * and can be determined from application.properties
   *
   * The response contains the permissions flag and existing entities
   */
    public ApiResponseObject<SkadTargetPrivileges> getSkadTargetPrivileges(SearchRequest searchRequest) throws ValidationException {
        List<DashboardFilters> filters = searchRequest.getFilters();
        TablesEntity tablesEntity = getTableEntityFromRequest(searchRequest);
        if (tablesEntity == null) {
            throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,"Search Request is not valid");
        }
        if (tablesEntity == TablesEntity.CAMPAIGN) {
            filters.add(ServiceUtils.getSkadDashboardFilter());
        }
        DictionaryResponse response = elasticSearch.getDictionaryData(tablesEntity,1,100,searchRequest,"id-");
        SkadTargetPrivileges privileges = new SkadTargetPrivileges(response.getTotalNoOfRecords(), response.getData());
        if (tablesEntity == TablesEntity.CAMPAIGN) {
            privileges.setAllowed(privileges.getTotalNoOfRecords() < Long.parseLong(properties.getSkadCampaignCount()));
        }
        if (tablesEntity == TablesEntity.STRATEGY){
            privileges.setAllowed(privileges.getTotalNoOfRecords() < Long.parseLong(properties.getSkadStrategyCount()));
        }
        ApiResponseObject<SkadTargetPrivileges> responseObject = new ApiResponseObject<>();
        responseObject.setRespObject(privileges);
        return responseObject;
    }

  /**
   * The tableEntity(Elastic Index) is determined from the payload if the column is advertiserId, we need to
   * query the campaign index if skad campaigns are present under such advertiserId , if the column is campaign
   * then we need to query strategy index to get count of such entities
   */
    private TablesEntity getTableEntityFromRequest(SearchRequest searchRequest) throws ValidationException {
        TablesEntity tablesEntity = null;
        if (searchRequest.getFilters().size() != 1) {
            throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,"Search Request is not valid");
        }
        for (DashboardFilters dashboardFilters : searchRequest.getFilters()) {
            if (dashboardFilters.getColumn().equals(ADVERTISER_ID)) {
                tablesEntity = TablesEntity.CAMPAIGN;
            } else if (dashboardFilters.getColumn().equals(CAMPAIGN_ID)) {
                tablesEntity = TablesEntity.STRATEGY;
            }
        }
        return tablesEntity;
    }


}
