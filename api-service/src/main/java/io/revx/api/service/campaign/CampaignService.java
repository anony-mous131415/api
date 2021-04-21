package io.revx.api.service.campaign;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.revx.core.response.ResponseMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.revx.api.audit.AuditServiceListener;
import io.revx.api.audit.CampaignAuditService;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.entity.campaign.AdvertiserIOPixel;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.mysql.repo.campaign.AdvertiserIOPixelRepository;
import io.revx.api.mysql.repo.campaign.CampaignRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ValidationService;
import io.revx.core.constant.Constants;
import io.revx.core.event.EventBusManager;
import io.revx.core.event.MessageObject;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.campaign.CampaignDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.service.CacheService;

@Component
public class CampaignService {

	private static Logger logger = LogManager.getLogger(CampaignService.class);

	@Autowired
	CampaignRepository campaignRepository;

	@Autowired
	AdvertiserIOPixelRepository ioPixelRepository;

	@Autowired
	ValidationService validationService;

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	CacheService cacheService;

	@Autowired
	CustomESRepositoryImpl elastic;

	@Autowired
	CampaignCacheService campaignCacheService;

	@Autowired
	CampaignUtils campaignUtils;

	@Autowired
	CampaignAuditService campaignAuditService;
	
	@PostConstruct
	void init() {
		// StrategyAuditService strategyAuditService = new StrategyAuditService();
		logger.debug("Registering the Audit on listener  strategyAuditService : {} ", campaignAuditService);
		AuditServiceListener.services.put(CampaignDTO.class, campaignAuditService);
		logger.debug("Registering the Audit on listener  Done");

	}
	
	// validation and populating isSKAD campaign property for create/update/get
	@Transactional
	public ApiResponseObject<CampaignDTO> create(CampaignDTO campaignPojo)
			throws ApiException, JsonProcessingException {
		logger.info("Inside getbyId method. Creating campaign for  : {} " + campaignPojo);

		validationService.validateCampaign(campaignPojo, false, null);
		validationService.validateIoPixel(campaignPojo.getPixel());

		CampaignEntity campaignEntity = new CampaignEntity();
		campaignUtils.populateCampaignEntity(campaignEntity, campaignPojo);
		campaignRepository.save(campaignEntity);
		campaignUtils.populateCampaignFromEntity(campaignEntity, campaignPojo);

		AdvertiserIOPixel ioPixelEntity = new AdvertiserIOPixel();
		campaignUtils.populateIOPixelEntity(ioPixelEntity, campaignPojo);
		if (ioPixelEntity != null && ioPixelEntity.getCampaignId() != null) {
			ioPixelRepository.save(ioPixelEntity);
			campaignUtils.populateCampaignPixelFromEntity(ioPixelEntity, campaignPojo);
		}
		elastic.save(campaignUtils.populateCampaignForESDTO(campaignPojo), TablesEntity.CAMPAIGN);
		campaignCacheService.remove();
		ApiResponseObject<CampaignDTO> resp = new ApiResponseObject<>();
		resp.setRespObject(campaignPojo);
		return resp;
	}

	@Transactional
	public ApiResponseObject<CampaignDTO> update(CampaignDTO campaignPojo)
			throws ApiException, JsonProcessingException {
		logger.info("Inside getbyId method. Updating campaign for  : " + campaignPojo);

		Optional<CampaignEntity> entity = campaignRepository.findById(campaignPojo.getId());
		if (!entity.isPresent())
			throw new ValidationException("Campaign id is not valid");

		CampaignEntity campaignEntity = entity.get();
		validationService.validateCampaign(campaignPojo, true, campaignEntity);
		validationService.validateIoPixel(campaignPojo.getPixel());

		CampaignDTO old = new CampaignDTO();
		campaignUtils.populateCampaignFromEntity(campaignEntity , old);

		campaignUtils.updateCampEntity(campaignPojo, campaignEntity);
		campaignRepository.save(campaignEntity);
		campaignUtils.populateCampaignFromEntity(campaignEntity, campaignPojo);
		campaignUtils.populateFcap(campaignPojo);

		AdvertiserIOPixel ioPixelEntity = new AdvertiserIOPixel();
		campaignUtils.populateIOPixelEntity(ioPixelEntity, campaignPojo);
		if (ioPixelEntity != null && ioPixelEntity.getCampaignId() != null) {
			ioPixelRepository.save(ioPixelEntity);
			campaignUtils.populateCampaignPixelFromEntity(ioPixelEntity, campaignPojo);
		}

		elastic.save(campaignUtils.populateCampaignForESDTO(campaignPojo), TablesEntity.CAMPAIGN);
		campaignCacheService.remove();
		campaignCacheService.removeDashboardListCache(campaignPojo.getId());
		old.setPixel(campaignPojo.getPixel());
		old.setFcap(campaignPojo.getFcap());
		postStrategyAuditEvent(old, campaignPojo);
		ApiResponseObject<CampaignDTO> response = new ApiResponseObject<>();
		response.setRespObject(campaignPojo);
		return response;
	}

	public ApiResponseObject<CampaignDTO> getbyId(Long id, Boolean refresh) throws ApiException {
		CampaignDTO campaignPojo = new CampaignDTO();
		ApiResponseObject<CampaignDTO> response = null;

		logger.info("Inside getbyId method. campaign id : {} ", id);
		CampaignEntity campaignEntity = campaignCacheService.fetchCampaign(id, refresh);

		logger.debug("Inside getbyId method. Got Campaign entity : {}", campaignEntity);
		if (campaignEntity == null)
			throw new ValidationException("Campaign id is not valid");

		Optional<AdvertiserIOPixel> ioPixelEntity = ioPixelRepository.findById(id);

		campaignUtils.populateCampaignFromEntity(campaignEntity, campaignPojo);
		campaignUtils.populateCampaignPixelFromEntity(ioPixelEntity.isPresent() ? ioPixelEntity.get() : null,
				campaignPojo);
		campaignUtils.populateFcap(campaignPojo);

		response = new ApiResponseObject<>();
		response.setRespObject(campaignPojo);
		return response;
	}

	@Transactional
	public ApiResponseObject<Map<Integer, ResponseMessage>> activate(String ids) throws ApiException {
		if (StringUtils.isBlank(ids))
			throw new ApiException("Nothing to update. Campaign id list is empty.");

		List<String> idStrList = Arrays.asList(ids.split(","));
		List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());

		ApiResponseObject<Map<Integer, ResponseMessage>> responseObject = new ApiResponseObject<>();
		Map<Integer, ResponseMessage> response = updateStatus(idList, Boolean.TRUE);
		responseObject.setRespObject(response);
		return responseObject;
	}

	@Transactional
	public ApiResponseObject<Map<Integer, ResponseMessage>> deactivate(String ids) throws ApiException {
		if (StringUtils.isBlank(ids))
			throw new ApiException("Nothing to update. Campaign id list is empty.");

		List<String> idStrList = Arrays.asList(ids.split(","));
		List<Long> idList = idStrList.stream().map(Long::parseLong).collect(Collectors.toList());

		ApiResponseObject<Map<Integer, ResponseMessage>> responseObject = new ApiResponseObject<>();
		Map<Integer, ResponseMessage> response = updateStatus(idList, Boolean.FALSE);
		responseObject.setRespObject(response);
		return responseObject;
	}

	public ApiResponseObject<List<CampaignDTO>> getbyAdvertiserId(Long advertiserId, int pageNumber, int pageSize,
			String sort, String search, Boolean refresh) throws ApiException {
		List<CampaignDTO> campaignPojo = new ArrayList<>();
		ApiResponseObject<List<CampaignDTO>> response = null;

		logger.info("Inside getbyAdvertiserId method. campaign id : {}, search : {}, sort : {} ", advertiserId, search,
				sort);
		List<CampaignEntity> campaignEntity = campaignCacheService.fetchAllCampaign(advertiserId, pageNumber, pageSize,
				sort, search, refresh);

		logger.debug("Inside getbyAdvertiserId method. Got Campaign entity : " + campaignEntity);
		if (campaignEntity == null)
			throw new ValidationException("Campaign id is not valid");

		for (CampaignEntity entity : campaignEntity) {
			Optional<AdvertiserIOPixel> ioPixelEntity = ioPixelRepository.findById(entity.getId());
			CampaignDTO dto = new CampaignDTO();
			campaignUtils.populateCampaignFromEntity(entity, dto);
			campaignUtils.populateCampaignPixelFromEntity(ioPixelEntity.isPresent() ? ioPixelEntity.get() : null, dto);
			campaignUtils.populateFcap(dto);
			campaignPojo.add(dto);
		}
		response = new ApiResponseObject<>();
		response.setRespObject(campaignPojo);
		return response;
	}

	@Transactional
	public Map<Integer, ResponseMessage> updateStatus(List<Long> idList, Boolean status) {
		Map<Integer, ResponseMessage> response = new HashMap<>();
		logger.info("Inside updateStatus method. campaign ids : {} ", idList);

		for (Long id : idList) {
			CampaignEntity campaignEntity = campaignCacheService.fetchCampaign(id, true);
			logger.debug("Inside updateStatus method. Got Campaign entity : " + campaignEntity);

			if (campaignEntity == null) {
				ResponseMessage responseMessage = new ResponseMessage(Constants.EC_ID_MISSING,
						Constants.MSG_ID_INVALID);
				response.put(id.intValue(), responseMessage);
				continue;
			}

			if (status == campaignEntity.isActive()) {
				ResponseMessage responseMessage = null;
				if (status)
					responseMessage = new ResponseMessage(Constants.ID_ALREADY_ACTIVE, Constants.MSG_ID_ALREADY_ACTIVE);
				else
					responseMessage = new ResponseMessage(Constants.ID_ALREADY_INACTIVE,
							Constants.MSG_ID_ALREADY_INACTIVE);
				response.put(id.intValue(), responseMessage);
			} else {
				Integer rowUpdated = campaignRepository.updateStatus(status, id);
				logger.debug("Updated campaign status in DB : {}. Total rows updated : {}", status, rowUpdated);
				if (rowUpdated < 1) {
					ResponseMessage responseMessage = new ResponseMessage(Constants.FAILERE, Constants.MSG_DB_ERROR);
					response.put(id.intValue(), responseMessage);
				} else {
					ResponseMessage responseMessage = new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS);
					response.put(id.intValue(), responseMessage);
					updateStatusInES(id, status);
					postActivationMessage(id, status);
				}
			}

		}
		return response;
	}

	private void updateStatusInES(Long i, Boolean status) {
		CampaignESDTO campaign = (CampaignESDTO) elastic.findDetailById(TablesEntity.CAMPAIGN.getElasticIndex(),
				String.valueOf(i), TablesEntity.CAMPAIGN.getElasticPojoClass());
		logger.debug("Updating status with : {} for audinece in ES : {}", status, campaign);
		if (campaign == null)
			return;

		campaign.setActive(status);
		elastic.save(campaign, TablesEntity.CAMPAIGN);
		campaignCacheService.remove();
		campaignCacheService.removeDashboardListCache(i);
	}

	/**
	 * Update the CPA target value for a given campaign id
	 * 
	 * @param campaignId
	 * @param cpaTarget
	 */
	@Transactional
	public void updateCPATargetForCampaign(Long campaignId, BigDecimal cpaTarget) throws ApiException {

		Integer rowUpdated = campaignRepository.updateCPATarget(campaignId, cpaTarget);
		if (rowUpdated == 1) {
			CampaignESDTO campaign = (CampaignESDTO) elastic.findDetailById(TablesEntity.CAMPAIGN.getElasticIndex(),
					String.valueOf(campaignId), TablesEntity.CAMPAIGN.getElasticPojoClass());

			if (campaign != null) {
				campaign.setCpaTarget(cpaTarget);
				elastic.save(campaign, TablesEntity.CAMPAIGN);
			}
			campaignCacheService.remove();
			campaignCacheService.removeDashboardListCache(campaignId);
		} else {
			throw new ApiException(ErrorCode.MULTIPLE_ROW_UPDATED,
					new Object[] { "Multiple rows updated. Reverting... " });

		}
	}

	private void postStrategyAuditEvent(CampaignDTO oldDTO, CampaignDTO newDTO) {
		// TODO write the Kafka Consumer and Producer
		postAuditEvent(oldDTO, newDTO);
	}
	
	private void postActivationMessage(Long id, boolean activated) {
		CampaignDTO o = new CampaignDTO();
		o.id = id;
		o.setActive(!activated);
		CampaignDTO n = new CampaignDTO();
		n.id = id;
		n.setActive(activated);
		postAuditEvent(o, n);

	}

	public void postAuditEvent(CampaignDTO o, CampaignDTO n) {
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

}
