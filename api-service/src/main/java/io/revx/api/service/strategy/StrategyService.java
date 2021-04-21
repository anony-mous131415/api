
package io.revx.api.service.strategy;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityExistsException;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.SKADNetworkCampaignMappingEntity;
import io.revx.api.mysql.repo.SKADNetworkCampaignMappingRepo;
import io.revx.api.utils.ServiceUtils;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.querybuilder.enums.Filter;
import io.revx.core.response.ResponseMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.revx.api.audit.AuditServiceListener;
import io.revx.api.audit.StrategyAuditService;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemCreativeEntity;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.pixel.PixelRepository;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemCreativeRepository;
import io.revx.api.mysql.repo.strategy.StrategyRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.StrategyModelConverterService;
import io.revx.api.service.campaign.CampaignService;
import io.revx.api.service.creative.CreativeUtil;
import io.revx.core.constant.Constants;
import io.revx.core.event.EventBusManager;
import io.revx.core.event.MessageObject;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.BaseModel;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.Strategy;
import io.revx.core.model.common.ReadResponse;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.strategy.DuplicateStrategyRequestDTO;
import io.revx.core.model.strategy.LineItemType;
import io.revx.core.model.strategy.StrategyCreativeAssociationRequestDTO;
import io.revx.core.model.strategy.StrategyCreativeAssociationResponseDTO;
import io.revx.core.model.strategy.StrategyCreativeAssociationResponseDTO.CreativeStrategyAssociationStatus;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.StrategyQuickEditDTO;
import io.revx.core.model.strategy.StrategyUpdateDTO;
import io.revx.core.model.targetting.AudienceStrDTO;
import io.revx.core.model.targetting.SiteListDTO;
import io.revx.core.model.targetting.TargetingObject;
import io.revx.core.response.ApiResponseObject;

@Service
public class StrategyService {

	private static final Logger logger = LogManager.getLogger(StrategyService.class);
	private StrategyRepository strategyRepository;
	private LoginUserDetailsService loginUserDetailsService;
	private ModelConverterService modelConverterService;
	private StrategyModelConverterService strategyModelConverterService;
	private StrategyCacheService strategyCacheService;
	private TargetingUtil targetingUtil;
	private EntityESService elasticSearch;
	private AdvertiserLineItemCreativeRepository lineItemCreativeRepository;
	private AdvertiserLineItemPixelRepository lineItemPixelRepository;
	private CreativeRepository creativeRepository;
	private CreativeUtil creativeUtil;
	private PixelRepository pixelRepository;
	private CustomESRepositoryImpl elasticRepo;
	private StrategyAuditService strategyAuditService;
	private CampaignService campaignService;

	@Autowired
	public void setStrategyRepository(StrategyRepository strategyRepository){
		this.strategyRepository = strategyRepository;
	}

	@Autowired
	public void setLoginUserDetailsService(LoginUserDetailsService loginUserDetailsService){
		this.loginUserDetailsService = loginUserDetailsService;
	}

	@Autowired
	public void setModelConverterService(ModelConverterService modelConverterService){
		this.modelConverterService = modelConverterService;
	}

	@Autowired
	public void setStrategyModelConverterService(StrategyModelConverterService strategyModelConverterService){
		this.strategyModelConverterService = strategyModelConverterService;
	}

	@Autowired
	public void setStrategyCacheService(StrategyCacheService strategyCacheService){
		this.strategyCacheService = strategyCacheService;
	}

	@Autowired
	public void setTargetingUtil(TargetingUtil targetingUtil){
		this.targetingUtil = targetingUtil;
	}

	@Autowired
	public void setElasticSearch(EntityESService elasticSearch){
		this.elasticSearch = elasticSearch;
	}

	@Autowired
	public void setAdvertiserLineItemCreativeRepository(AdvertiserLineItemCreativeRepository lineItemCreativeRepository){
		this.lineItemCreativeRepository = lineItemCreativeRepository;
	}

	@Autowired
	public void setAdvertiserLineItemPixelRepository(AdvertiserLineItemPixelRepository lineItemPixelRepository){
		this.lineItemPixelRepository = lineItemPixelRepository;
	}

	@Autowired
	public void setCreativeRepository(CreativeRepository creativeRepository){
		this.creativeRepository = creativeRepository;
	}

	@Autowired
	public void setCreativeUtil(CreativeUtil creativeUtil){
		this.creativeUtil = creativeUtil;
	}

	@Autowired
	public void setPixelRepository(PixelRepository pixelRepository){
		this.pixelRepository = pixelRepository;
	}
	@Autowired
	public void setElasticRepo(CustomESRepositoryImpl elasticRepo){
		this.elasticRepo = elasticRepo;
	}

	@Autowired
	public void setStrategyAuditService(StrategyAuditService strategyAuditService){
		this.strategyAuditService = strategyAuditService;
	}

	@Autowired
	public void setCampaignService(CampaignService campaignService){
		this.campaignService = campaignService;
	}

	@Autowired
    SKADNetworkCampaignMappingRepo skadMappingRepo;

	@Autowired
	ApplicationProperties properties;

	@PostConstruct
	void init() {
		logger.debug("Registering the Audit on listener  strategyAuditService : {} ", strategyAuditService);
		AuditServiceListener.services.put(StrategyDTO.class, strategyAuditService);
		logger.debug("Registering the Audit on listener  Done");
	}

	public ApiResponseObject<StrategyDTO> get(Long id, boolean refresh) throws Exception {
		StrategyDTO strategyPojo = null;
		ApiResponseObject<StrategyDTO> response = null;
		logger.info("Inside getById method. Strategy id : {} ", id);
		if (!refresh) {
			strategyPojo = strategyCacheService.fetchStrategy(id);
		}
		logger.debug("Inside getById method. Got Strategy entity {} : ", strategyPojo);
		if (strategyPojo == null) {
			StrategyEntity strategyEntity = strategyCacheService.getStrategyEntity(id);
			if (strategyEntity == null)
				throw new ValidationException("Strategy id is not valid");
			strategyPojo = new StrategyDTO();
			strategyModelConverterService.populateStrategyFromEntity(strategyEntity, strategyPojo);
			strategyCacheService.saveStrategyDTOToCache(id, strategyPojo);
		}
		response = new ApiResponseObject<>();
		response.setRespObject(strategyPojo);
		return response;
	}

	@Transactional
	public ApiResponseObject<StrategyDTO> createStrategy(StrategyDTO strategy) throws Exception {
		ApiResponseObject<StrategyDTO> response = new ApiResponseObject<>();
		try {

			StrategyEntity strategyDO = new StrategyEntity();
			StrategyDTO result = createANewStrategy(strategy, strategyDO);
			elasticRepo.save(strategyModelConverterService.populateStrategyForElasticSearch(result),
					TablesEntity.STRATEGY);

			response.setRespObject(result);
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
		return response;
	}

	@Transactional
	public ApiResponseObject<StrategyDTO> updateStrategy(StrategyDTO strategy) throws Exception {

		ApiResponseObject<StrategyDTO> response = new ApiResponseObject<>();
		try {
			logger.debug(" strategy : {} ", strategy);
			StrategyEntity strategyDO = strategyRepository.getOne(strategy.getId());
			if (strategyDO == null)
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Strategy", strategy.id });

			StrategyDTO old = new StrategyDTO(); // for auditing purpose
			strategyModelConverterService.populateStrategyFromEntity(strategyDO, old);
			StrategyDTO responseDTO = new StrategyDTO();
			responseDTO = updateAnExistingStrategy(strategyDO, strategy);
			postStrategyAuditEvent(old, responseDTO);
			elasticRepo.save(strategyModelConverterService.populateStrategyForElasticSearch(responseDTO),
					TablesEntity.STRATEGY);
			response.setRespObject(responseDTO);
			strategyCacheService.removeCacheKey(responseDTO.getId());
			strategyCacheService.removeDashboardListCache(responseDTO.getId());
			return response;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	private StrategyDTO updateAnExistingStrategy(StrategyEntity strategyDO, StrategyDTO strategy) throws ApiException {
		// If existing strategy is Click Tracker then, say no to edit!
		if (strategyDO.getType() != null
				&& io.revx.core.model.strategy.LineItemType.clickTracker.name().equals(strategyDO.getType().name())) {
			throw new ValidationException(ErrorCode.CREATE_MODIFY_NOT_ALLOWED,
					new Object[] { "Click Tracker Strategy" });
		}
		// If existing strategy is Standard and user trying to change to
		// Click Tracker then, say no to Change!
		else if (strategyDO.getType() != null && strategy.strategyType != null
				&& LineItemType.standard.name().equals(strategyDO.getType().name())
				&& LineItemType.clickTracker.name().equals(strategy.strategyType)) {
			throw new ValidationException(ErrorCode.CLICK_TRACKER_CONV_NOT_ALLOWED);
		}

		validateUpdateDTO(strategy, strategyDO); // TODO change this as validateDTO to not fail
		StrategyUpdateDTO sud = new StrategyUpdateDTO();

		strategyModelConverterService.convertToDO(strategy, strategyDO, true, sud);
		targetingUtil.updateBasicTargetingOnStrategy(strategy, strategyDO);
		strategyRepository.save(strategyDO);
		saveBasedOnStrategyUpdateDTO(strategyDO, sud);

		// REVX-352: update cpa target in Advertiser IO table and elastic search
		updateCampaignCPATarget(strategy.getCampaignId(), strategy.getCpaTargetValue());
		return strategy;
	}

	@Transactional
	public void saveBasedOnStrategyUpdateDTO(StrategyEntity strategyDO, StrategyUpdateDTO sud) throws ApiException {
		logger.debug("saveBasedOnStrategyUpdateDTO : {}  ", sud);
		if (sud != null) {
			if (sud.getCreativeIdsToDelete() != null && !sud.getCreativeIdsToDelete().isEmpty()) {
				List<AdvertiserLineItemCreativeEntity> liCr = lineItemCreativeRepository
						.findAllByStrategyIdAndCreativeIdIn(strategyDO.getId(),
								new ArrayList<>(sud.getCreativeIdsToDelete()));
				lineItemCreativeRepository.deleteAll(liCr);
			}
			if (sud.getPixelIdsToDelete() != null && !sud.getPixelIdsToDelete().isEmpty()) {
				List<AdvertiserLineItemPixelEntity> liCr = lineItemPixelRepository.findAllByStrategyIdAndPixelIdIn(
						strategyDO.getId(), new ArrayList<>(sud.getPixelIdsToDelete()));
				lineItemPixelRepository.deleteAll(liCr);
			}

			if (sud.getCreativeIdsToInsert() != null && !sud.getCreativeIdsToInsert().isEmpty()) {
				List<AdvertiserLineItemCreativeEntity> liCr = new ArrayList<>();
				for (Long id : sud.getCreativeIdsToInsert()) {
					liCr.add(new AdvertiserLineItemCreativeEntity(strategyDO.getId(), id));
				}
				lineItemCreativeRepository.saveAll(liCr);
			}
			if (sud.getPixelIdsToInsert() != null && !sud.getPixelIdsToInsert().isEmpty()) {
				List<AdvertiserLineItemPixelEntity> liCr = new ArrayList<>();
				for (Long id : sud.getPixelIdsToInsert()) {
					liCr.add(new AdvertiserLineItemPixelEntity(strategyDO.getId(), id));
				}
				lineItemPixelRepository.saveAll(liCr);
			}
		}
	}

	private void postStrategyAuditEvent(StrategyDTO oldDTO, StrategyDTO newDTO) {
		// TODO write the Kafka Consumer and Producer
		postAuditEvent(oldDTO, newDTO);
	}

	public ApiResponseObject<StrategyDTO> duplicateStrategy(Long strategyId, DuplicateStrategyRequestDTO dupReqDTO)
			throws Exception {
		try {
			if (dupReqDTO.name == null)
				throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
						new Object[] { "name", "DuplicateStrategyRequestDTO" });

			String name = dupReqDTO.name;

			ApiResponseObject<StrategyDTO> response = new ApiResponseObject<>();
			logger.debug("Fetching strategy details for strategy id : " + strategyId);
			StrategyEntity strategyDO = strategyRepository.getOne(strategyId);
			if (strategyDO != null) {
				StrategyDTO strategy = new StrategyDTO();
				CampaignESDTO campaign = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyDO.getCampianId());
				// Check if campaign end date is in past. If yes, duplication shouldn't be
				// allowed
				if (campaign == null || (campaign.getEndTime() != -1
						&& campaign.getEndTime() < (System.currentTimeMillis() / 1000))) {
					// Campaign already ended, strategy duplication shouldn't be allowed.
					throw new ValidationException(ErrorCode.CAMPAIGN_HAS_EXPIRED, new Object[] {});
				}
				strategyModelConverterService.populateStrategyFromEntity(strategyDO, strategy);
				strategy.setActive(false);
				if (dupReqDTO.startTime == null)
					throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
							new Object[] { "start_date", "DuplicateStrategyRequestDTO" });

				if (dupReqDTO.endTime == null)
					throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
							new Object[] { "end_date", "DuplicateStrategyRequestDTO" });

				if (dupReqDTO.startTime != null)
					strategy.setStartTime(dupReqDTO.startTime);

				if (dupReqDTO.endTime != null)
					strategy.setEndTime(dupReqDTO.endTime);

				if (dupReqDTO.isNative) {
					strategy.isNative = dupReqDTO.isNative;
				}
				strategy.id = null;
				String origStrategyName = strategy.name;
				if (name != null && name.length() > 0)
					strategy.name = name;
				else
					strategy.name = origStrategyName + "-dup";

				if (dupReqDTO.duplicateAudienceTargeting != null
						&& dupReqDTO.duplicateAudienceTargeting.equals(Boolean.FALSE)) {
					strategy.targetAppSegments = null;
					strategy.targetWebSegments = null;
					strategy.targetDmpSegments = null;
				}

				if (dupReqDTO.duplicateDmpAudienceTargeting != null
						&& dupReqDTO.duplicateDmpAudienceTargeting.equals(Boolean.FALSE))
					strategy.targetDmpSegments = null;

				if (dupReqDTO.duplicateBrowserTargeting != null
						&& dupReqDTO.duplicateBrowserTargeting.equals(Boolean.FALSE))
					strategy.targetBrowsers = null;

				if (dupReqDTO.duplicateGeoTargeting != null && dupReqDTO.duplicateGeoTargeting.equals(Boolean.FALSE))
					strategy.targetGeographies = null;

				if (dupReqDTO.duplicateDayPartTargeting != null
						&& dupReqDTO.duplicateDayPartTargeting.equals(Boolean.FALSE))
					strategy.targetDays = null;

				if (dupReqDTO.duplicateMobileTargeting != null
						&& dupReqDTO.duplicateMobileTargeting.equals(Boolean.FALSE)) {
					strategy.targetMobileDevices = null;
				}

				if (dupReqDTO.duplicatePlacementTargeting != null
						&& dupReqDTO.duplicatePlacementTargeting.equals(Boolean.FALSE)) {
					List<BaseModel> placements = new ArrayList<BaseModel>();
					placements.add(new BaseModel(1, "Desktop"));
					placements.add(new BaseModel(2, "Mobile Web"));
					placements.add(new BaseModel(3, "Mobile Applications"));
					strategy.placements = placements;
				}

				if (dupReqDTO.duplicateConnectionTypeTargeting != null
						&& dupReqDTO.duplicateConnectionTypeTargeting.equals(Boolean.FALSE)) {
					// strategy.addConnectionType(ConnectionType.UNKNOWN)
					strategy.connectionTypes = null;
				}

				if (dupReqDTO.duplicatecreativesAttached != null
						&& dupReqDTO.duplicatecreativesAttached.equals(Boolean.FALSE)) {
					if (strategy.creatives != null)
						strategy.creatives.clear();

					strategy.creatives = null;
				}

				if (dupReqDTO.duplicateInventoryTargeting != null
						&& dupReqDTO.duplicateInventoryTargeting.equals(Boolean.FALSE)) {

					if (strategy.rtbAggregators != null)
						strategy.rtbAggregators.cleanUp();
					strategy.rtbAggregators = null;

					if (strategy.rtbSites != null)
						strategy.rtbSites.cleanUp();
					strategy.rtbSites = null;
				}

				StrategyEntity strategyDuplicateDO = new StrategyEntity();
				StrategyDTO result = createANewStrategy(strategy, strategyDuplicateDO);
				elasticRepo.save(strategyModelConverterService.populateStrategyForElasticSearch(result),
						TablesEntity.STRATEGY);
				response.setRespObject(result);
			} else {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "strategy", strategyId });

			}
			return response;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}

	}

	public ApiResponseObject<ReadResponse<CreativeDTO>> getCreativesForStrategy(long strategyId) throws ApiException {
		try {
			ApiResponseObject<ReadResponse<CreativeDTO>> response = new ApiResponseObject<ReadResponse<CreativeDTO>>();
			StrategyEntity strategyDO = strategyRepository.getOne(strategyId);
			if (strategyDO != null) {
				List<AdvertiserLineItemCreativeEntity> creatives = lineItemCreativeRepository
						.findAllByStrategyId(strategyId);
				Set<Long> crIds = new HashSet<>();
				for (AdvertiserLineItemCreativeEntity advCr : creatives) {
					crIds.add(advCr.getCreativeId());
				}
				List<CreativeEntity> creativeDOList = creativeRepository.findByIdIn(crIds);
				List<CreativeDTO> creativeDTOList = null;
				if (creativeDOList != null && !creativeDOList.isEmpty()) {
					try {
						creativeDTOList = creativeUtil.populateCreativeDTOsFromEntities(creativeDOList);
					} catch (Exception e) {
						throw new ApiException(ErrorCode.CRATIVE_PERFORMANCE_DATA_EXCEPTION,
								Constants.CRATIVE_PERFORMANCE_DATA_EXCEPTION);
					}
					ReadResponse<CreativeDTO> creativeDTOPage = new ReadResponse<CreativeDTO>(creativeDTOList,
							creativeDTOList.size());
					response.setRespObject(creativeDTOPage);
				}
				return response;
			} else {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "strategy", strategyId });
			}

		} catch (ApiException e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	public ApiResponseObject<ReadResponse<Pixel>> getPixelsForStrategy(long strategyId) throws Exception {
		try {
			ApiResponseObject<ReadResponse<Pixel>> response = new ApiResponseObject<>();
			StrategyEntity strategyDO = strategyRepository.getOne(strategyId);
			if (strategyDO != null) {
				List<AdvertiserLineItemPixelEntity> advPixels = lineItemPixelRepository.findAllByStrategyId(strategyId);
				List<Long> pixelIds = new ArrayList<>();
				for (AdvertiserLineItemPixelEntity advPix : advPixels) {
					pixelIds.add(advPix.getPixelId());
				}
				List<ConversionPixelEntity> pixelDOList = pixelRepository.findAllByIdIn(pixelIds);
				if (pixelDOList != null && !pixelDOList.isEmpty()) {
					List<Pixel> pixelDTOList = new ArrayList<>();
					for (ConversionPixelEntity pixelDO : pixelDOList) {
						Pixel pi = modelConverterService.convertPixelToDTO(pixelDO);
						if (pi != null)
							pixelDTOList.add(pi);
					}
					ReadResponse<Pixel> pixelDTOPage = new ReadResponse<Pixel>(pixelDTOList, pixelDTOList.size());
					response.setRespObject(pixelDTOPage);

				}
			} else {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "strategy", strategyId });
			}

			return response;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	public ApiResponseObject<SiteListDTO> validateSiteList(List<String> siteNames) throws ApiException {
		logger.debug("Validating list of site names");
		ApiResponseObject<SiteListDTO> response = new ApiResponseObject<>();
		try {
			Map<String, Long> siteMap = new HashMap<>();

			if (CollectionUtils.isNotEmpty(siteNames)) {
				Set<String> sName = new HashSet<>(siteNames);
				Map<Long, ?> siteMapById = elasticSearch.searchAllByNameExactMatch(TablesEntity.SITE, sName);
				if (siteMapById != null) {
					for (Entry<Long, ?> siteIdModel : siteMapById.entrySet()) {
						BaseModel site = (BaseModel) siteIdModel.getValue();
						siteMap.put(site.name, site.getId());
					}
				}
				logger.debug("Creating response");
				SiteListDTO siteListDTO = new SiteListDTO();
				siteListDTO.validSites = new ArrayList<BaseModel>();
				siteListDTO.invalidSites = new ArrayList<String>();
				for (String siteName : siteNames) {
					if (siteMap.containsKey(siteName)) {
						siteListDTO.validSites.add(new BaseModel(siteMap.get(siteName), siteName));
					} else {
						siteListDTO.invalidSites.add(siteName);
					}
				}
				response.setRespObject(siteListDTO);
			}
			return response;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));

			throw new ApiException(ErrorCode.BAD_REQUEST, e.getCause());
		}

	}

	@Transactional
	public ApiResponseObject<Map<Long, ResponseMessage>> activate(String ids) throws Exception {
		if (StringUtils.isBlank(ids))
			throw new ApiException("Nothing to update. Strategy id list is empty.");
		List<String> idStrList = Arrays.asList(ids.split(","));
		List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());
		return activate(idList);
	}

	public ApiResponseObject<Map<Long, ResponseMessage>> activate(List<Long> strategyIds) throws Exception {
		try {
			ApiResponseObject<Map<Long, ResponseMessage>> resp = new ApiResponseObject<>();
			Map<Long, ResponseMessage> result = new HashMap<>();
			List<Long> idsToSet = new ArrayList<>();

			List<StrategyEntity> strategies = strategyRepository.findAllByIdIn(strategyIds);
			for (StrategyEntity a : strategies) {
				if (a.getActive())
					result.put(a.getId(), new ResponseMessage(ErrorCode.MSG_ALREADY_SET.getValue(),
							"strategy " + a.getId() + " active"));
				else {
					idsToSet.add(a.getId());
				}
			}
			for (Long id : strategyIds) {
				if (!(idsToSet.contains(id) || result.containsKey(id)))
					result.put(id, new ResponseMessage(ErrorCode.ENTITY_NOT_FOUND.getValue(), "strategy :" + id));
			}
			if (!idsToSet.isEmpty()) {
				int status = strategyRepository.updateStatusInBulk(true, idsToSet);

				for (Long strategyId : idsToSet) {
					Strategy pojo = elasticSearch.searchPojoById(TablesEntity.STRATEGY, strategyId);
					pojo.setActive(true);
					elasticSearch.save(pojo, TablesEntity.STRATEGY);
					strategyCacheService.removeCacheKey(strategyId);
					strategyCacheService.removeDashboardListCache(strategyId);
					postActivationMessage(strategyId, true);
				}
				logger.debug("Bulk Update status : {} ", status);
			}
			resp.setRespObject(result);
			return resp;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	@Transactional
	public ApiResponseObject<Map<Long, ResponseMessage>> deactivate(String ids) throws Exception {
		if (StringUtils.isBlank(ids))
			throw new ApiException("Nothing to update. Strategy id list is empty.");
		List<String> idStrList = Arrays.asList(ids.split(","));
		List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());
		return deactivate(idList);
	}

	/**
	 *
	 * @param strategyIds - StrategyIds to be deactivated
	 * @return - Response object containing invalid id with respective error
	 *         messages
	 * @throws Exception Exception in case of Mysql and Elastic errors
	 *
	 *                   If any strategy is updated as de-active , but if that
	 *                   status update fails for elastic index rollback the changes
	 *                   in mysql to maintain consistency
	 */
	public ApiResponseObject<Map<Long, ResponseMessage>> deactivate(List<Long> strategyIds) throws Exception {
		try {
			ApiResponseObject<Map<Long, ResponseMessage>> resp = new ApiResponseObject<>();
			Map<Long, ResponseMessage> result = new HashMap<>();
			List<Long> idsToDeactivate = new ArrayList<>();

			List<StrategyEntity> strategies = strategyRepository.findAllByIdIn(strategyIds);
			for (StrategyEntity a : strategies) {
				if (!a.getActive()) {
					result.put(a.getId(), new ResponseMessage(ErrorCode.MSG_ALREADY_UNSET.getValue(),
							"strategy " + a.getId() + " InActive"));
				} else {
					idsToDeactivate.add(a.getId());
				}
			}
			for (Long id : strategyIds) {
				if (!(idsToDeactivate.contains(id) || result.containsKey(id)))
					result.put(id, new ResponseMessage(ErrorCode.ENTITY_NOT_FOUND.getValue(), "strategy :" + id));
			}
			if (!idsToDeactivate.isEmpty()) {
				int status = strategyRepository.updateStatusInBulk(false, idsToDeactivate);
				for (Long strategyId : idsToDeactivate) {
					Strategy pojo = elasticSearch.searchPojoById(TablesEntity.STRATEGY, strategyId);
					pojo.setActive(false);
					elasticSearch.save(pojo, TablesEntity.STRATEGY);
					postActivationMessage(strategyId, false);
					strategyCacheService.removeCacheKey(strategyId);
					strategyCacheService.removeDashboardListCache(strategyId);
				}
				logger.debug("Bulk Update status : {} ", status);
			}
			resp.setRespObject(result);
			return resp;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	private void postActivationMessage(Long id, boolean activated) {
		StrategyDTO o = new StrategyDTO();
		o.id = id;
		o.setActive(!activated);
		StrategyDTO n = new StrategyDTO();
		n.id = id;
		n.setActive(activated);
		postAuditEvent(o, n);

	}

	public ApiResponseObject<Map<String, Object>> getMinMaxSettings() {
		return null;
	}

	private void validateDTO(StrategyDTO strategy, boolean isUpdate) throws ValidationException {

		if (io.revx.core.model.strategy.LineItemType.clickTracker.name().equals(strategy.strategyType)) {
			throw new ValidationException(ErrorCode.CREATE_MODIFY_NOT_ALLOWED,
					new Object[] { "Click Tracker Strategy Update not allowed" });
		}
		if (strategy.getCampaign() == null || strategy.getCampaign().getId() == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
					new Object[] { "campaign is missing", "strategy" });

		CampaignESDTO campaign = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategy.getCampaign().id);

		if (campaign.getSkadTarget() && !isUpdate) {
			DashboardFilters campaignFilter = ServiceUtils.getFilterForKey(
					Filter.CAMPAIGN_ID,String.valueOf(campaign.getId()));
			List<DashboardFilters> dashboardFilters = new ArrayList<>();
			dashboardFilters.add(campaignFilter);
			long strategyCount = elasticSearch.searchByGivenFilter(
					TablesEntity.STRATEGY, dashboardFilters).getTotalNoOfRecords();
			if (strategyCount >= Long.parseLong(properties.getSkadStrategyCount())) {
				throw new ValidationException("Strategy count exceeds campaign count quota");
			}
		}

		if (!isUpdate && strategy.getName() == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED, new Object[] { "name is missing", "strategy" });

		if (strategy.getPricingType() == null || strategy.getPricingType().getId() == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
					new Object[] { "pricingType is missing", "strategy" });

		if (strategy.pacingType == null || strategy.pacingType.id == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
					new Object[] { "pacingType is missing", "strategy" });

		if (strategy.pricingValue == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
					new Object[] { "pricingValue missing", "strategy" });

		if (strategy.budgetValue == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED, new Object[] { "budget", "strategy" });

		if (BigInteger.ZERO.compareTo(strategy.getStartTime()) >= 0)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED, new Object[] { "start_date missing", "strategy" });

		if (strategy.getName() != null) {
			List<StrategyEntity> strategies = strategyRepository.findByNameAndCampianId(strategy.getName(),
					strategy.getCampaign().getId());
			if ((strategies != null && strategies.size() > 0 && !isUpdate))
				throw new ValidationException(ErrorCode.ENTITY_NAME_VALIDATION,
						new Object[] { "Strategy name already exists", "given campaign" });
		}

		if (strategy.targetGeographies != null) {
			logger.debug("strategy.targetGeographies.customGeoTargeting == "
					+ strategy.targetGeographies.customGeoTargeting);
			if (strategy.targetGeographies.customGeoTargeting == true
					&& (TargetingObject.isEmptyTargetting(strategy.targetGeographies.country)
							&& TargetingObject.isEmptyTargetting(strategy.targetGeographies.city)
							&& TargetingObject.isEmptyTargetting(strategy.targetGeographies.state))) {
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE, new Object[] {
						"target_specific_geographies invalid value", strategy.targetGeographies.customGeoTargeting });
			}
		}

		if (!strategy.isNative() && !campaign.getSkadTarget()) {
			boolean isValidAppSegment = validateAudienceData(strategy.targetAppSegments);
			boolean isValidWebSegment = validateAudienceData(strategy.targetWebSegments);
			boolean isValidDmpSegment = validateAudienceData(strategy.targetDmpSegments);

			// at least 1 of the audience types should have some data. All 3 cannot be null
			if (!isValidAppSegment && !isValidWebSegment && !isValidDmpSegment) {
				throw new ValidationException(ErrorCode.RTB_STRATEGY_AUDIENCE_REQUIRED,
						new Object[] { "target_specific_segments is missing" });
			}
		}

		if (strategy.placements == null || strategy.placements.isEmpty()) {
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
					new Object[] { "placements is required", "strategy" });
		} else {
			boolean placementFAN = false;
			boolean placementMobile = false;
			for (BaseModel placement : strategy.placements) {
				if (placement.getId().intValue() == TargetingConstants.MOBILEWEBPLACEMENTID
						|| placement.getId().intValue() == TargetingConstants.MOBILEAPPPLACEMENTID) {
					placementMobile = true;
				} else if (placement.getId().intValue() == TargetingConstants.FANPLACEMENTID) {
					placementFAN = true;
				}
			}
			if (placementFAN && !placementMobile) {
				throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
						new Object[] { "placement mobile missing", "strategy" });
			}
		}

		//REVX-501
		//if isHourlyFcap=true , hourlyUserFcap must be less than userFcap and 1<=hourlyFcapDuration <=23

		// if (strategy.getIsHourlyFcap() == null) {
		// 	throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
		// 			new Object[] { "isHourlyFcap is required", "strategy" });
		// }

		// if (strategy.getIsHourlyFcap().booleanValue() == true) {

		// 	if (strategy.getHourlyUserFcap() == null || strategy.getHourlyFcapDuration() == null) {
		// 		throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
		// 				new Object[] { "hourlyUserFcap or hourlyFcapDuration cannot be null", "strategy" });
		// 	}

		// 	if(strategy.fcapFrequency == null ){
		// 		throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE, new Object[] {"fcapFrequency is null", "strategy"});
		// 	}

		// 	long dailyFcap = strategy.fcapFrequency.longValue();

		// 	if (strategy.getHourlyUserFcap().longValue() >= dailyFcap) {
		// 		throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
		// 				new Object[] { "hourly fcap must be less than daily fcap", "strategy" });
		// 	}

		// 	if (strategy.getHourlyFcapDuration().intValue() < 1 || strategy.getHourlyFcapDuration().intValue() > 23) {
		// 		throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
		// 				new Object[] { "hourly fcap duration is invalid", "strategy" });
		// 	}
		// }

		if (strategy.getFcapInterval() != null
				&& (strategy.getFcapInterval() < 1L || strategy.getFcapInterval() > 168L)) {
			throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
					new Object[] { "fcap duration is invalid", "strategy" });
		}

	}

	private boolean validateAudienceData(AudienceStrDTO segment) {
		if (segment == null || segment.customSegmentTargeting == Boolean.FALSE
				|| ((segment.blockedSegments == null
						|| (segment.blockedSegments != null && segment.blockedSegments.isEmpty()))
						&& (segment.targetedSegments == null
								|| (segment.targetedSegments != null && segment.targetedSegments.isEmpty())))) {

			return false;
		}

		return true;
	}

	private void validateUpdateDTO(StrategyDTO strategy, StrategyEntity strategyDO) throws ValidationException {
		if (strategy.pricingType == null)
			strategy.pricingType = elasticSearch.searchById(TablesEntity.PRICING, strategyDO.getPricingId());
		if (strategy.pricingValue == null && strategyDO.getFlowRate() != null)
			strategy.pricingValue = strategyDO.getFlowRate().setScale(9);
		validateDTO(strategy, true);
	}

	@Transactional
	public StrategyDTO createANewStrategy(StrategyDTO strategy, StrategyEntity strategyDO) throws ApiException {
		StrategyDTO strategyResponse = new StrategyDTO();

		validateDTO(strategy, false);
		logger.debug("Basic validation of strategy succeeded");
		logger.debug("converting strategy presentation object to database object");
		StrategyUpdateDTO sud = new StrategyUpdateDTO();
		strategyModelConverterService.convertToDO(strategy, strategyDO, false, sud);
		logger.debug("converted strategy presentation object to database object. {}", strategyDO);
		strategyRepository.save(strategyDO);
		saveBasedOnStrategyUpdateDTO(strategyDO, sud);
		saveMappingOnSkadTarget(strategyDO);
		targetingUtil.addBasicTargetingToStrategy(strategy, strategyDO);

		// Add Inventory source to RTB strategies
		logger.debug("Adding RTB Inventory");
		targetingUtil.targetRTBInventory(strategy, strategyDO);
		logger.debug("RTB Inventory added for strategy : {}", strategyDO);

		// REVX-352: update cpa target in Advertiser IO table and elastic search
		updateCampaignCPATarget(strategy.getCampaignId(), strategy.getCpaTargetValue());

		logger.debug("Creating response");
		strategyModelConverterService.populateStrategyFromEntity(strategyDO, strategyResponse);
		return strategyResponse;
	}

	/**
	 * REVX-352: Based on cpaTargetValue values in StrategyDTO decide whether to
	 * update ai_cpa_target value in AdvertiserIO table.
	 * 
	 * @param strategyDTO - strategyDTO
	 * @throws ApiException
	 */
	public void updateCampaignCPATarget(long campaignId, BigDecimal cpaTarget) throws ApiException {
		if (cpaTarget != null) {
			campaignService.updateCPATargetForCampaign(campaignId, cpaTarget);
		}
	}

	public void postAuditEvent(StrategyDTO o, StrategyDTO n) {
		logger.debug(" Writing postAuditEvent in Event Bus ");
		try {
			MessageObject mo = new MessageObject(loginUserDetailsService.getUserInfo().getUserId());
			mo.setPrevValue(o);
			mo.setNewValue(n);
			EventBusManager.eventBus.post(mo);
			logger.debug(" Posted  postAuditEvent in Event Bus old {} , newW {} ", o, n);

		} catch (Exception e) {
			logger.debug("Got Exception Error {} ", e);
		}
	}

	public ApiResponseObject<Map<String, Object>> getStrategyTargetingTillNow(Long strategyId) throws Exception {
		Map<String, Object> targetingMap = new HashMap<>();
		try {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public ApiResponseObject<StrategyCreativeAssociationResponseDTO> associateCreativesToStrategies(
			StrategyCreativeAssociationRequestDTO request) throws ApiException {
		Map<Long, Map<Long, String>> responseMap = new HashMap<>();
		try {
			long licenseeId = loginUserDetailsService.getLicenseeId();
			List<BaseModel> creativesIdList = request.creativesList;
			List<CreativeEntity> creativesList = new ArrayList<>();
			Map<Long, CreativeEntity> creativesMap = new HashMap<>();
			Map<Long, StrategyEntity> strategiesMap = new HashMap<>();
			Boolean operationFailed = Boolean.FALSE;
			Boolean operationSucceeded = Boolean.FALSE;
			Set<Long> crIds = new HashSet<>();
			for (BaseModel bm : creativesIdList) {
				crIds.add(bm.getId());
			}
			creativesList = creativeRepository.findByIdIn(crIds);
			for (CreativeEntity creative : creativesList) {
				creativesMap.put(creative.getId(), creative);
			}
			for (BaseModel bm : creativesIdList) {
				if (!creativesMap.containsKey(bm.getId())) {
					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "creative", bm.getId() });
				}
			}

			List<BaseModel> strategyIdList = request.strategyList;
			Set<Long> liIds = new HashSet<>();
			for (BaseModel bm : strategyIdList) {
				liIds.add(bm.getId());
			}
			List<StrategyEntity> liList = strategyRepository.findAllByLicenseeIdAndIdIn(licenseeId,
					new ArrayList<>(liIds));
			for (StrategyEntity li : liList) {
				strategiesMap.put(li.getId(), li);
			}

			for (BaseModel bm : strategyIdList) {
				Long id = bm.id;
				if (!strategiesMap.containsKey(id)) {
					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "strategy", id });
				}
				StrategyEntity strategyDO = strategiesMap.get(id);
				Set<Long> insertedCrIds = associateCreativesToStrategy(strategyDO, creativesList,
						request.replaceExistingCreatives);
				// postStrategyAuditEvent(strategyDTO, responseDTO)
				Map<Long, String> creativesAssociationStatusMap = new HashMap<>();
				for (Long crId : insertedCrIds) {
					creativesAssociationStatusMap.put(crId, "DONE");
				}

				// Remove creatives which were not requested and add ones which failed
				for (BaseModel cm : creativesIdList) {
					Long crId = cm.id;
					if (creativesAssociationStatusMap.containsKey(crId)) {
						creativesAssociationStatusMap.put(crId, "SUCCESSFUL");
					} else {
						creativesAssociationStatusMap.put(crId, "UNSUCCESSFUL");
					}
				}
				Set<Long> keySet = creativesAssociationStatusMap.keySet();
				Iterator<Long> it = keySet.iterator();
				while (it.hasNext()) {
					Long crId = it.next();
					String crStatus = creativesAssociationStatusMap.get(crId);
					if (crStatus.equals("DONE"))
						it.remove();
				}
				responseMap.put(id, creativesAssociationStatusMap);
			}

			StrategyCreativeAssociationResponseDTO respDTO = new StrategyCreativeAssociationResponseDTO();
			respDTO.creatives = new ArrayList<>();
			Map<Long, CreativeStrategyAssociationStatus> statusCreativesMap = new HashMap<>();
			for (Map.Entry<Long, Map<Long, String>> entry : responseMap.entrySet()) {
				long strategyId = entry.getKey();
				Map<Long, String> crRespMap = entry.getValue();
				for (Map.Entry<Long, String> crRespEntry : crRespMap.entrySet()) {
					long crId = crRespEntry.getKey();
					String resp = crRespEntry.getValue();
					CreativeStrategyAssociationStatus crStatus = null;
					if (statusCreativesMap.containsKey(crId)) {
						crStatus = statusCreativesMap.get(crId);
					} else {
						crStatus = respDTO.new CreativeStrategyAssociationStatus();
						crStatus.id = crId;
						crStatus.name = creativesMap.get(crId).getName();
						statusCreativesMap.put(crId, crStatus);
						respDTO.creatives.add(crStatus);
					}

					if (resp.equalsIgnoreCase("SUCCESSFUL")) {
						operationSucceeded = Boolean.TRUE;
						if (crStatus.successfulStrategies == null)
							crStatus.successfulStrategies = new ArrayList<BaseModel>();

						crStatus.successfulStrategies
								.add(new BaseModel(strategyId, strategiesMap.get(strategyId).getName()));
					} else if (resp.equals("UNSUCCESSFUL")) {
						operationSucceeded = Boolean.TRUE;
						if (crStatus.failedStrategies == null)
							crStatus.failedStrategies = new ArrayList<BaseModel>();

						crStatus.failedStrategies
								.add(new BaseModel(strategyId, strategiesMap.get(strategyId).getName()));
					}
				}
			}

			// Hack added to get correct response status for now. Need to refactor some code
			// above to
			// remove this hack
			operationFailed = null;
			operationSucceeded = null;
			for (CreativeStrategyAssociationStatus csas : respDTO.creatives) {
				if (csas.successfulStrategies != null && !csas.successfulStrategies.isEmpty())
					operationSucceeded = Boolean.TRUE;
				if (csas.failedStrategies != null && !csas.failedStrategies.isEmpty())
					operationFailed = Boolean.TRUE;
			}

			if (operationFailed != null && Boolean.TRUE.equals(operationFailed) && operationSucceeded != null
					&& Boolean.TRUE.equals(operationSucceeded))
				respDTO.operationStatus = "Partial Success";
			else if (operationSucceeded != null && Boolean.TRUE.equals(operationSucceeded)
					&& (operationFailed == null || Boolean.FALSE.equals(operationFailed)))
				respDTO.operationStatus = "Successful";
			else if (operationFailed != null && Boolean.TRUE.equals(operationFailed)
					&& (operationSucceeded == null || Boolean.FALSE.equals(operationSucceeded)))
				respDTO.operationStatus = "Failed";
			else
				logger.debug("Unhandled use case in operation status inference");

			responseMap.clear();

			ApiResponseObject<StrategyCreativeAssociationResponseDTO> response = new ApiResponseObject<>();
			response.setRespObject(respDTO);
			return response;
		} catch (Exception e) {
			logger.error(ExceptionUtils.getFullStackTrace(e));
			throw e;
		}
	}

	public Set<Long> associateCreativesToStrategy(StrategyEntity strategyDO, List<CreativeEntity> creativesList,
			boolean replaceExistingCreatives) throws ApiException {
		long strategyId = strategyDO.getId();
		StrategyUpdateDTO sud = new StrategyUpdateDTO();
		if (replaceExistingCreatives) {
			// User want to replace existing creatives
			List<AdvertiserLineItemCreativeEntity> existingCreative = lineItemCreativeRepository
					.findAllByStrategyId(strategyId);
			for (AdvertiserLineItemCreativeEntity advertiserLineItemCreativeEntity : existingCreative) {
				sud.addCreativeIdToDelete(advertiserLineItemCreativeEntity.getCreativeId());
			}
			for (CreativeEntity creativeEntity : creativesList) {
				sud.addCreativeIdToInsert(creativeEntity.getId());
			}
		} else {
			List<AdvertiserLineItemCreativeEntity> existingCreative = lineItemCreativeRepository
					.findAllByStrategyId(strategyId);
			List<Long> existingCreativeIds = new ArrayList<>();
			if (existingCreative != null && !existingCreative.isEmpty()) {
				for (AdvertiserLineItemCreativeEntity creative : existingCreative) {
					existingCreativeIds.add(creative.getCreativeId());
				}

			}
			for (CreativeEntity creativeEntity : creativesList) {
				if (!existingCreativeIds.contains(creativeEntity.getId())) {
					sud.addCreativeIdToInsert(creativeEntity.getId());
				}
			}
		}
		try {
			saveBasedOnStrategyUpdateDTO(strategyDO, sud);
		} catch (Exception e) {
			throw new EntityExistsException("Creative(s) are associated with selected Strategies " + e.getMessage());
		}

		return sud.getCreativeIdsToInsert();
	}

	public ApiResponseObject<StrategyDTO> updateStrategyWithoutStartingTransaction(StrategyDTO strategy) {
		// TODO: BUlk Editing
		return null;
	}

	public StrategyDTO getStrategyWithoutStartingTransaction(Integer id) {
		// TODO: BUlk Editing
		return null;
	}

	// Quick-edit-GET
	public ApiResponseObject<StrategyQuickEditDTO> getStrategyQuickEditDetails(Long id) throws Exception {
		StrategyQuickEditDTO strategyQuickEditDTO = new StrategyQuickEditDTO();
		ApiResponseObject<StrategyQuickEditDTO> response = null;
		logger.info("Inside getStrategyQuickEditDetails method. Strategy id : {} ", id);
		strategyQuickEditDTO = strategyRepository.getQuickEditDetails(id);
		logger.debug("Inside getStrategyQuickEditDetails method. Got Strategy entity {} : ", strategyQuickEditDTO);
		if (strategyQuickEditDTO == null)
			throw new ValidationException("Strategy id is not valid");

		response = new ApiResponseObject<>();
		response.setRespObject(strategyQuickEditDTO);
		return response;
	}

	// Quick-edit-UPDATE
	@Transactional
	public ApiResponseObject<StrategyDTO> updateQuickEditDetails(StrategyQuickEditDTO quickDto) throws ApiException {
		logger.debug("entered update-QuickEdit-Details method");

		logger.debug("Quick Edit Dto :{}", quickDto);

		validateQuickDto(quickDto);
		StrategyDTO oldStrDto = new StrategyDTO();

		Optional<StrategyEntity> strategyDBObject = strategyRepository.findById(quickDto.getId());
		if (!strategyDBObject.isPresent()) {
			throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Strategy", quickDto.getId() });
		}

		StrategyEntity strategyDO = strategyDBObject.get();
		strategyModelConverterService.populateStrategyFromEntity(strategyDO, oldStrDto);
		logger.debug("old strategy Dto :{}", oldStrDto);

		strategyModelConverterService.strategyDOFromQuickDTO(strategyDO, quickDto);

		StrategyEntity savedDO = strategyRepository.save(strategyDO); // save in db

		// REVX-417: update cpa target in Advertiser IO table and elastic search
		updateCampaignCPATarget(quickDto.getCampaignId(), quickDto.getCpaTargetValue());

		StrategyDTO respStrDTO = new StrategyDTO(); // new response object
		strategyModelConverterService.populateStrategyFromEntity(savedDO, respStrDTO); // setup response object
		ApiResponseObject<StrategyDTO> response = new ApiResponseObject<>();

		postStrategyAuditEvent(oldStrDto, respStrDTO);
		logger.debug("audit table entry done");
		elasticRepo.save(strategyModelConverterService.populateStrategyForElasticSearch(respStrDTO),
				TablesEntity.STRATEGY);

		logger.debug("quick-edit strategy saved ");
		// Removing the cache when updating entity through quick edit
		strategyCacheService.removeCacheKey(quickDto.getId());
		response.setRespObject(respStrDTO);
		return response;

	}

	private void validateQuickDto(StrategyQuickEditDTO quickDto) throws ValidationException {

		if (io.revx.core.model.strategy.LineItemType.clickTracker.name().equals(quickDto.getStrategyType())) {
			throw new ValidationException(ErrorCode.CREATE_MODIFY_NOT_ALLOWED,
					new Object[] { "Click Tracker Strategy Update not allowed" });
		}

		if (quickDto.getName() == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED, new Object[] { "name is missing", "strategy" });

		if (quickDto.getPricingType() < 0)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
					new Object[] { "pricingType is invalid", "strategy" });

		if (quickDto.getPricingValue() == null)
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED, new Object[] { "flowRate missing", "strategy" });

		// campaign id to be added in quick-edit-dto for the below check
		// if (quickDto.getName() != null) {
		// List<StrategyEntity> strategies = strategyRepository
		// .findByNameAndCampianId(quickDto.getName(), quickDto.getCampaignId());
		// if ((strategies != null && strategies.size() > 0))
		// throw new ValidationException(ErrorCode.ENTITY_NAME_VALIDATION,
		// new Object[] {"Strategy name already exists", "given campaign"});
		// }

		//revx-656
		if (quickDto.getFcapInterval() != null
				&& (quickDto.getFcapInterval().longValue() < 1l || quickDto.getFcapInterval().longValue() > 168l)) {
			throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
					new Object[] { "fcap duration is invalid", "strategy" });
		}


	}

	/**
	 * these settings are retrieved from a file /atom/origin/skad/settings.json
	 */
    public ApiResponseObject<String> getSkadSettings() {
		String settingsString = "";
		try {
			File settingFile = new File(properties.getSkadSettingsFileLocation());
			settingsString = FileUtils.readFileToString(settingFile, (String) null);

		} catch (IOException e) {
			e.printStackTrace();
		}
		ApiResponseObject<String> responseObject = new ApiResponseObject<>();
		responseObject.setRespObject(settingsString);
		return responseObject;
    }

	/**
	 * If we are creating a strategy under a skad campaign, we populate the mapping in a seperate table.
	 * We check whether the camapign is SKAD. if it satisfies we save advId->campaign->startegy mapping
	 */
	private void saveMappingOnSkadTarget(StrategyEntity strategyEntity) {
        CampaignESDTO campaignESDTO = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyEntity.getCampianId());
        if (Boolean.TRUE.equals(campaignESDTO.getSkadTarget())) {
            long currentCount = skadMappingRepo.getStrategyCountPerCampaign(strategyEntity.getCampianId());
            SKADNetworkCampaignMappingEntity entity = new SKADNetworkCampaignMappingEntity();
            entity.setStrategyId(strategyEntity.getId());
            entity.setAdvertiserId(strategyEntity.getAdvertiserId());
            entity.setCampaignId(strategyEntity.getCampianId());
            entity.setStrategyCount(currentCount);
            skadMappingRepo.save(entity);
        }
    }

}
