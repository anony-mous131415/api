/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.campaign.AdvertiserIOPixel;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.entity.creative.DcoAttributesEntity;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemCreativeEntity;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.campaign.AdvertiserIOPixelRepository;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.mysql.repo.creative.DcoAttributeRepository;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemCreativeRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.campaign.CurrencyCacheService;
import io.revx.api.service.strategy.TargetingUtil;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.Creative;
import io.revx.core.model.ParentBasedObject;
import io.revx.core.model.Strategy;
import io.revx.core.model.strategy.LineItemType;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.StrategyQuickEditDTO;
import io.revx.core.model.strategy.StrategyUpdateDTO;
import io.revx.core.model.targetting.DeliveryPriority;
import io.revx.core.model.targetting.PacingType;
import io.revx.core.model.targetting.Pricing;
import io.revx.core.response.UserInfo;
import io.revx.core.utils.NumberUtils;

@Component
public class StrategyModelConverterService {

  private static final Logger logger = LogManager.getLogger(StrategyModelConverterService.class);

  private LoginUserDetailsService loginUserDetailsService;
  private ApplicationProperties applicationProperties;
  private CurrencyCacheService currencyCache;
  private EntityESService elasticSearch;
  private AdvertiserLineItemPixelRepository lineItemPixelRepository;
  private AdvertiserLineItemCreativeRepository lineItemCreativeRepository;
  private CreativeRepository creativeRepo;
  private DcoAttributeRepository dcoAttributeRepository;
  private AdvertiserIOPixelRepository campPixelRepo;
  private TargetingUtil targetingUtil;

  	@Autowired
	public void setLoginUserDetailsService(LoginUserDetailsService loginUserDetailsService) {
		this.loginUserDetailsService = loginUserDetailsService;
	}

	@Autowired
	public void setApplicationProperties(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	@Autowired
	public void setCurrencyCache(CurrencyCacheService currencyCache) {
		this.currencyCache = currencyCache;
	}

	@Autowired
	public void setElasticSearch(EntityESService elasticSearch) {
		this.elasticSearch = elasticSearch;
	}

	@Autowired
	public void setLineItemPixelRepository(AdvertiserLineItemPixelRepository lineItemPixelRepository) {
		this.lineItemPixelRepository = lineItemPixelRepository;
	}

	@Autowired
	public void setLineItemCreativeRepository(AdvertiserLineItemCreativeRepository lineItemCreativeRepository) {
		this.lineItemCreativeRepository = lineItemCreativeRepository;
	}

	@Autowired
	public void setCreativeRepo(CreativeRepository creativeRepo) {
		this.creativeRepo = creativeRepo;
	}

	@Autowired
	public void setDcoAttributeRepository(DcoAttributeRepository dcoAttributeRepository) {
		this.dcoAttributeRepository = dcoAttributeRepository;
	}

	@Autowired
	public void setCampPixelRepo(AdvertiserIOPixelRepository campPixelRepo) {
		this.campPixelRepo = campPixelRepo;
	}

	@Autowired
	public void setTargetingUtil(TargetingUtil targetingUtil) {
		this.targetingUtil = targetingUtil;
	}

	public void strategyDOFromQuickDTO(StrategyEntity strategyDO, StrategyQuickEditDTO quickDTO)throws ApiException  {

	  CampaignESDTO campaign = null;
	  Pricing pricing = null;
	  if (quickDTO.getCampaignId() != null) {
		  campaign = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, quickDTO.getCampaignId());
		  logger.debug("Now Data is :{} , and strategyDO :  {} ", campaign, strategyDO);
		  if (campaign == null) {
			  throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
					  new Object[] {"campaign.id", quickDTO.getCampaignId()});
		  } 
	  }
	 
	  //if (quickDTO.getPricingType()) {
	      pricing = elasticSearch.searchPojoById(TablesEntity.PRICING, quickDTO.getPricingType());
	      if (pricing == null) {
	        throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
	            new Object[] {"pricing_type.id", quickDTO.getPricingType()});
	      }
	    //}


	  //ASSIGNMENT- STARTS
	  if (quickDTO.getName() != null)
		  strategyDO.setName(StringUtils.trim(quickDTO.getName()));

	  if (quickDTO.isCampaignFcap()) {
		  strategyDO.setCampiagnFcap(quickDTO.isCampaignFcap());
		  strategyDO.setUserFcap(Long.valueOf(campaign.getFcap()));
	  } else {
		  //if (strategyDO.isFcapEnabled()) { //hard-coded TRUE from ui 
		  //logger.debug("fcapEnabled = " + strategy.fcapEnabled);
		  if (quickDTO.getFcapFrequency() == null)
			  throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR,
					  new Object[] {"frequency_cap_frequency"});

		  if (quickDTO.getFcapFrequency() <= 0)
			  throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR,
					  new Object[] {"frequency_cap_frequency",quickDTO.getFcapFrequency() });

		  strategyDO.setCampiagnFcap(quickDTO.isCampaignFcap());
		  strategyDO.setUserFcap(quickDTO.getFcapFrequency());

		  //}
	  }
	  
	   if (quickDTO.getPricingValue() != null)
		      strategyDO.setFlowRate(quickDTO.getPricingValue());
	 
	  strategyDO.setPricingId(pricing.getId().intValue());
	  
	  if(quickDTO.getBidCapMin() != null && quickDTO.getBidCapMax() !=null) {
		  strategyDO.setBidCapMinCpm(quickDTO.getBidCapMin());
		  strategyDO.setBidCapMaxCpm(quickDTO.getBidCapMax());
	  }
	  
	  BigInteger currentTime = BigInteger.valueOf(System.currentTimeMillis() / 1000);
	  long userId = loginUserDetailsService.getUserInfo().getUserId();
	  strategyDO.setModifiedOn(currentTime);
	  strategyDO.setModifiedBy(userId);


	  //revx-656
	  //convert hours to minutes
	  if(quickDTO.isCampaignFcap()){
	  strategyDO.setUserFcapDuration(Constants.DEFAULT_FCAP_DURATION);
	  }else{
		strategyDO.setUserFcapDuration(quickDTO.getFcapInterval().longValue()*60);
	  }
  }
  
	public void populateStrategyFromEntity(StrategyEntity strategyDO, StrategyDTO strategyDTO) throws ApiException {
		BeanUtils.copyProperties(strategyDO, strategyDTO);
		strategyDTO.setId(strategyDO.getId());
		strategyDTO.setAdvertiserId(strategyDO.getAdvertiserId());
		strategyDTO.setCreatedBy(strategyDO.getCreatedBy());
		strategyDTO.setModifiedBy(strategyDO.getModifiedBy());
		strategyDTO.setModifiedTime(NumberUtils.getLongValue(strategyDO.getModifiedOn()));
		ParentBasedObject campaign = elasticSearch.searchDetailById(TablesEntity.CAMPAIGN, strategyDO.getCampianId());
		logger.debug("Got parent campaign entity : {}", campaign);
		if (campaign != null) {
			strategyDTO.setCampaign(campaign);
			if (campaign.getParent() != null) {
				strategyDTO.setAdvertiser(campaign.getParent());
				if (campaign.getParent().getParent() != null) {
					strategyDTO.setLicensee(campaign.getParent().getParent());
				}
			}

		}
		CampaignESDTO campianPojo = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyDO.getCampianId());
		strategyDTO.setCampaign(campianPojo);
		strategyDTO.setCurrencyCode(campianPojo.getCurrencyCode());

		strategyDTO.setActive(strategyDO.getActive());
		strategyDTO.setStrategyType(strategyDO.getType().name());

		strategyDTO.setPricingType(elasticSearch.searchById(TablesEntity.PRICING, strategyDO.getPricingId()));

		if (strategyDO.getFlowRate() == null)
			logger.debug("Pricing Value is null for strategy id : {} ", strategyDTO.getId());
		else
			strategyDTO.setPricingValue(strategyDO.getFlowRate().setScale(9));
		if (strategyDO.getRoiPricingId() != null) {
			strategyDTO.setPricingType(elasticSearch.searchById(TablesEntity.PRICING, strategyDO.getRoiPricingId()));
			if (strategyDO.getRoiTargetValue() == null)
				logger.debug("ROI Target Value is null for strategy id : {} ", strategyDTO.getId());
			else
				strategyDTO.setRoiTargetValue(strategyDO.getRoiTargetValue().setScale(9));
		}

		strategyDTO.setBudgetBy(strategyDO.getPricingId());
		if (strategyDO.getLiBudget() == null)
			logger.debug("Budget is null for strategy id : {} ", strategyDTO.getId());
		else
			strategyDTO.setBudgetValue(strategyDO.getLiBudget().setScale(9));

		strategyDTO.setStartTime(strategyDO.getStartTime());

		if (strategyDO.getEndTime() != null && strategyDO.getEndTime().longValue() != ApiConstant.ABSOLUTE_END_TIME)
			strategyDTO.setEndTime(strategyDO.getEndTime());
		else
			strategyDTO.setEndTime(BigInteger.valueOf(-1));

		if (strategyDO.getDeliveryPriorityId() != null)
			strategyDTO.setDeliveryPriority(
					elasticSearch.searchById(TablesEntity.DELIVERY_PRIORITY, strategyDO.getDeliveryPriorityId()));
		if (strategyDO.getPacingTypeId() != null) {
			PacingType pacingType = elasticSearch.searchPojoById(TablesEntity.PACING_TYPE,
					strategyDO.getPacingTypeId());
			strategyDTO.setPacingType(pacingType);
			if (strategyDO.getPacingBudgetLimit() == null) {
				logger.debug("Pacing budget is null for strategy id : {} ", strategyDTO.getId());
			} else {
				if (strategyDO.getPacingBudgetLimit() != null && pacingType != null
						&& (pacingType.getOrder() != 0 && pacingType.getOrder() != 1 && pacingType.getOrder() != 2))
					strategyDTO.setPacingBudgetValue(strategyDO.getPacingBudgetLimit().setScale(9));
			}
		}

		// strategyDTO.setFcapInterval(strategyDO.getUserFcapDuration());

		//convert minutes to hours
		strategyDTO.setFcapInterval(strategyDO.getUserFcapDuration().longValue()/60);

		strategyDTO.setFcapFrequency(strategyDO.getUserFcap());
		strategyDTO.setCampaignFcap(strategyDO.isCampiagnFcap());
		strategyDTO.setFcapEnabled(true);

		List<AdvertiserLineItemPixelEntity> strateyPixels = lineItemPixelRepository
				.findAllByStrategyId(strategyDO.getId());
		List<Long> pixels = new ArrayList<>();
		for (AdvertiserLineItemPixelEntity sp : strateyPixels) {
			pixels.add((long) sp.getPixelId());
		}
		if (pixels.size() > 0) {
			List<BaseModel> pixelDTOList = (List<BaseModel>) elasticSearch.searchPojoByIdList(TablesEntity.PIXEL,
					pixels);
			strategyDTO.setPixels(pixelDTOList);
		}

		List<AdvertiserLineItemCreativeEntity> strateyCreatives = lineItemCreativeRepository
				.findAllByStrategyId(strategyDO.getId());
		List<Long> creatives = new ArrayList<>();
		for (AdvertiserLineItemCreativeEntity sp : strateyCreatives) {
			creatives.add((long) sp.getCreativeId());
		}
		if (creatives.size() > 0) {
			List<BaseModel> creativesDTOList = (List<BaseModel>) (List<?>) elasticSearch
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

		if (strategyDO.getBidCapMaxCpm() == null)
			logger.debug("bid_cap_max is null for strategy id : " + strategyDTO.id);
		else
			strategyDTO.bidCapMax = strategyDO.getBidCapMaxCpm().setScale(9);

		if (strategyDO.getBidCapMinCpm() == null)
			logger.debug("bid_cap_min is null for strategy id : " + strategyDTO.id);
		else
			strategyDTO.bidCapMin = strategyDO.getBidCapMinCpm().setScale(9);

		targetingUtil.populateRTBTargetingParametersInDTO(strategyDTO);
		targetingUtil.populateStrategyDTOWithBasicTargeting(strategyDO, strategyDTO);

		Advertiser adv = elasticSearch.searchPojoById(TablesEntity.ADVERTISER, strategyDTO.getAdvertiser().getId());
		strategyDTO.timezone = elasticSearch.searchPojoById(TablesEntity.TIMEZONE, adv.getTimeZoneId());
		strategyDTO.isEditable = true;

	}

	public void convertToDO(StrategyDTO strategy, StrategyEntity strategyDO, boolean update,
			StrategyUpdateDTO strategyUpdateDTO) throws ApiException {

		CampaignESDTO campaign = null;
		Pricing pricing = null;
		PacingType pacingType = null;
		Pricing roiType = null;
		DeliveryPriority dp = null;
		BaseModel cdType = null;
		UserInfo uInfo = loginUserDetailsService.getUserInfo();
		long licenseeId = uInfo.getSelectedLicensee().getId();
		List<Long> advs = loginUserDetailsService.getAdvertisers(uInfo.getAdvertisers());
		if (strategy.getCampaign() != null && strategy.getCampaign().id != null) {
			campaign = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategy.getCampaign().id);
			logger.debug("Now Data is :{} , and strategyDO :  {} ", campaign, strategyDO);
			if (campaign == null) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
						new Object[] { "campaign.id", strategy.getCampaign().id });
			} else if (update && campaign != null
					&& campaign.getId().intValue() != strategyDO.getCampianId().intValue()) {
				throw new ValidationException(ErrorCode.ENTITY_IMMUTABLE_FIELD,
						new Object[] { "Campaign", "Strategy" });
			}
		}

		if (strategy.pricingType != null && strategy.pricingType.id != null) {
			pricing = elasticSearch.searchPojoById(TablesEntity.PRICING, strategy.pricingType.id);
			if (pricing == null) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
						new Object[] { "pricing_type.id", strategy.pricingType.id });
			}
		}

		if (strategy.pacingType != null && strategy.pacingType.id != null) {
			pacingType = elasticSearch.searchPojoById(TablesEntity.PACING_TYPE, strategy.pacingType.id);
			if (pacingType == null) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
						new Object[] { "pacing_type.id", strategy.pacingType.id });
			}
		}

		if (strategy.deliveryPriority != null) {
			dp = elasticSearch.searchPojoById(TablesEntity.DELIVERY_PRIORITY, strategy.deliveryPriority.id);
			if (dp == null) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
						new Object[] { "delivery_priority.id", strategy.deliveryPriority.id });
			}
		} else {
			if (!update) {
				// For create. set 2 as default value for delivery priority
				dp = elasticSearch.searchPojoById(TablesEntity.DELIVERY_PRIORITY, 2);
			}
		}

		if (update && strategy.getCampaign() == null) {
			long campaignId = strategyDO.getCampianId();
			campaign = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, campaignId);
		}

		if (campaign != null && strategy.getCampaign() != null) {
			/*
			 * if (campaign.getLifetimeBudget() == null) { throw new
			 * ApiException(ErrorCode.MISSING_VARIABLE_ERROR, new Object[]
			 * {"Campaign Budget is NULL"}); }
			 */
			strategyDO.setCampianId(campaign.getId());
		} else {
			logger.debug("Cross licensee data (Strategy & Campaign) mapped in AdX. Strategy id : " + strategy.id
					+ " & Campaign id : " + strategyDO.getCampianId());
		}

		if (pricing != null) {
			strategyDO.setPricingId(pricing.getId().intValue());
		} else {
			throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
					new Object[] { "pricing_type.id", strategy.pricingType.id });
		}

		if (pacingType != null) {
			strategyDO.setPacingTypeId(pacingType.getId().intValue());
		}

		if (dp != null) {
			strategyDO.setDeliveryPriorityId(dp.getId().intValue());
		}

		logger.debug("checking for primary key attributes");

		if (update == true) {
			// Set original strategy budget value from DO, if not being updated
			if (strategy.budgetValue == null)
				strategy.budgetValue = strategyDO.getLiBudget();
		}

		validateGenericRules(strategy, campaign, pricing, pacingType, roiType, dp);

		logger.debug("validated generic rules");

		BigInteger currentTime = BigInteger.valueOf(System.currentTimeMillis() / 1000);
		long userId = loginUserDetailsService.getUserInfo().getUserId();
		strategyDO.setModifiedOn(currentTime);
		strategyDO.setModifiedBy(userId);
		if (update == false) {
			strategyDO.setSystemEntryTime(currentTime);
			strategyDO.setCreatedBy(userId);
		}
		strategyDO.setActive(strategy.isActive());

		if (strategy.name != null)
			strategyDO.setName(StringUtils.trim(strategy.name));

		logger.debug("start date = " + strategy.getStartTime());
		logger.debug("end date = " + strategy.getEndTime());

		boolean startDateUpdated = false, endDateUpdated = false;

		if (update == true) {
			if (strategyDO.getStartTime().equals(strategy.getStartTime()))
				startDateUpdated = false;
			else
				startDateUpdated = true;
			if (strategyDO.getEndTime().equals(strategy.getEndTime()))
				endDateUpdated = false;
			else
				endDateUpdated = true;
		}

		// TODO : Check for update scenarios while updating start_date and end_date
		Long startTime = NumberUtils.getLongValue(strategy.getStartTime());
		Long endTime = NumberUtils.getLongValue(strategy.getEndTime());
		if (startTime != -1 && endTime != -1) {
			Long campaignStartDate = null;
			Long campaignEndDate = null;
			if (campaign != null) {
				campaignStartDate = campaign.getStartTime();
				campaignEndDate = campaign.getEndTime();
			}
			if (startTime.compareTo(endTime) > 0) {
				throw new ValidationException(ErrorCode.LATER_DATE_VALUE, new Object[] { "start_date", "end_date" });
			} else if (startTime.compareTo(endTime) == 0) {
				if (update == false)
					throw new ValidationException(ErrorCode.START_DATE_EQUAL_END_DATE, new Object[] {});
				else {
					if (startTime == 0L && endTime == 0L) {
						// start date and end date not changed. Do nothing
						logger.debug("Update operation. Start date and end date not provided in JSON");
					} else {
						throw new ValidationException(ErrorCode.START_DATE_EQUAL_END_DATE, new Object[] {});
					}
				}
			} else if (campaignStartDate != null && campaignStartDate.compareTo(-1L) != 0
					&& startTime.compareTo(campaignStartDate) < 0) {
				throw new ValidationException(ErrorCode.SOONER_DATE_VALUE,
						new Object[] { "strategy start_date", "campaign start_date" });
			} else if (campaignEndDate != null && campaignEndDate.compareTo(-1L) != 0
					&& endTime.compareTo(campaignEndDate) > 0) {
				logger.error("Error : Exception : strategy end_date greater than campaign end_date");
				/*
				 * throw new ValidationException(ErrorCode.LATER_DATE_VALUE, new Object[]
				 * {"strategy end_date", "campaign end_date"});
				 */
			} else {
				strategyDO.setStartTime(strategy.getStartTime());
				strategyDO.setEndTime(strategy.getEndTime());
			}
		} else {
			if (startTime != -1) {
				Long campaignStartDate = null;
				if (campaign != null) {
					campaignStartDate = campaign.getStartTime();
				}

				if ((update == false || (update == true && startDateUpdated == true))
						&& startTime < System.currentTimeMillis() / 1000) {
					throw new ValidationException(ErrorCode.SOONER_DATE_VALUE,
							new Object[] { "strategy start_date", "current time" });
				} else if (campaignStartDate != null && campaignStartDate.compareTo(-1L) != 0
						&& startTime.compareTo(campaignStartDate) < 0) {
					throw new ValidationException(ErrorCode.SOONER_DATE_VALUE,
							new Object[] { "strategy start_date", "campaign start_date" });
				} else
					strategyDO.setStartTime(strategy.getStartTime());
			} else {
				Long campaignStartTime = campaign.getStartTime();
				if (campaignStartTime != null && campaignStartTime > currentTime.longValueExact())
					strategyDO.setStartTime(BigInteger.valueOf(campaignStartTime));
				else
					strategyDO.setStartTime(currentTime);
			}

			if (endTime != -1) {
				Long campaignEndDate = null;
				if (campaign != null) {
					campaignEndDate = campaign.getEndTime();
				}

				if ((update == false || (update == true && endDateUpdated == true))
						&& endTime < System.currentTimeMillis() / 1000) {
					throw new ValidationException(ErrorCode.SOONER_DATE_VALUE,
							new Object[] { "strategy end_date", "current time" });
				} else if (campaignEndDate != null && campaignEndDate.compareTo(-1L) != 0
						&& endTime.compareTo(campaignEndDate) > 0) {
					throw new ValidationException(ErrorCode.LATER_DATE_VALUE,
							new Object[] { "strategy end_date", "campaign end_date" });
				} else
					strategyDO.setEndTime(strategy.getEndTime());
			} else
				strategyDO.setEndTime(BigInteger.valueOf(ApiConstant.ABSOLUTE_END_TIME));
		}

		if (strategy.pricingValue != null)
			strategyDO.setFlowRate(strategy.pricingValue);

		if (pacingType != null
				&& (pacingType.getOrder() == 0 || pacingType.getOrder() == 1 || pacingType.getOrder() == 2))
			strategyDO.setPacingBudgetLimit(null);

		if (strategy.pacingBudgetValue != null && pacingType != null
				&& (pacingType.getOrder() != 0 && pacingType.getOrder() != 1 && pacingType.getOrder() != 2))
			strategyDO.setPacingBudgetLimit(strategy.pacingBudgetValue);

		if (strategy.isCampaignFcap() /* && campaign.getFcap() != null && campaign.getFcap() > 0 */) {
			// Set fcap values as per campaign level
			strategyDO.setUserFcap(Long.valueOf(campaign.getFcap()));
			strategyDO.setCampiagnFcap(strategy.isCampaignFcap());
			strategyDO.setUserFcapDuration(Constants.DEFAULT_FCAP_DURATION);
		} else {
			if (strategy.isFcapEnabled()) {
				logger.debug("fcapEnabled = " + strategy.fcapEnabled);
				if (strategy.fcapFrequency == null)
					throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR,
							new Object[] { "frequency_cap_frequency" });
				/*
				 * if (strategy.fcapInterval == null) throw new
				 * ValidationException(ErrorCode.MISSING_VARIABLE_ERROR, new Object[]
				 * {"frequency_cap_interval"});
				 */
				if (strategy.fcapFrequency <= 0)
					throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR,
							new Object[] { "frequency_cap_frequency", strategy.fcapFrequency });

				strategyDO.setUserFcap(strategy.fcapFrequency);
				// strategyDO.setUserFcapDuration(Constants.DEFAULT_FCAP_DURATION);
				strategyDO.setCampiagnFcap(strategy.isCampaignFcap());

				//convert hours to minutes
				strategyDO.setUserFcapDuration(strategy.getFcapInterval().longValue()*60);

			}
		}


		//REVX-501
		// strategyDO.setIsHourlyFcap(false);
		// strategyDO.setHourlyUserFcap(null);
		// strategyDO.setHourlyFcapDuration(null);
		// if (strategy.getIsHourlyFcap()) {
		// 	strategyDO.setIsHourlyFcap(true);
		// 	strategyDO.setHourlyUserFcap(strategy.getHourlyUserFcap());
		// 	strategyDO.setHourlyFcapDuration(strategy.getHourlyFcapDuration());
		// }


		strategyDO.setLiBudget(strategy.budgetValue);
		strategyDO.setDeliveryPriorityId(2);
		strategyDO.setType(LineItemType.standard);

		Advertiser advertiser = elasticSearch.searchPojoById(TablesEntity.ADVERTISER, strategy.getAdvertiser().getId());

		boolean isValid = loginUserDetailsService.isValidAdvertiser(advertiser);
		if (!isValid) {
			throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE, new Object[] { "advertiserId" });
		}

		if (update == true) {
			List<AdvertiserLineItemCreativeEntity> liCreatives = lineItemCreativeRepository
					.findAllByStrategyId(strategyDO.getId());
			Set<Long> existingCrIds = new HashSet<>();
			if (liCreatives != null) {
				for (AdvertiserLineItemCreativeEntity advertiserLineItemCreativeEntity : liCreatives) {
					existingCrIds.add(advertiserLineItemCreativeEntity.getCreativeId());
					// strategyUpdateDTO.addCreativeIdToDelete(advertiserLineItemCreativeEntity.getCreativeId());
				}
			}

			if (existingCrIds != null && !existingCrIds.isEmpty()) {
				List<Creative> creativeList = (List<Creative>) elasticSearch.searchPojoByIdList(TablesEntity.CREATIVE,
						new ArrayList<>(existingCrIds));

				for (Creative creative : creativeList) {
					if (creative.isActive() == true && creative.isRefactor() == true) {
						strategyUpdateDTO.addCreativeIdToDelete(creative.getId());
					}
				}
			}

			List<Long> newCreativeIds = new ArrayList<Long>();
			if (strategy.creatives != null && strategy.creatives.size() != 0) {
				newCreativeIds = strategy.creatives.stream().map(cr -> cr.getId()).collect(Collectors.toList());
			}

			List<Long> newCreativesNotToBeInserted = new ArrayList<Long>();

			newCreativeIds.forEach(crId -> {
				if (strategyUpdateDTO.getCreativeIdsToDelete().contains(crId)) {
					strategyUpdateDTO.getCreativeIdsToDelete().remove(crId);
					newCreativesNotToBeInserted.add(crId);
				}
			});

			if (strategy.creatives != null && strategy.creatives.size() != 0) {
				ListIterator<BaseModel> iter = strategy.creatives.listIterator();
				while (iter.hasNext()) {
					BaseModel creative = iter.next();
					if (newCreativesNotToBeInserted.contains(creative.getId())) {
						iter.remove();
					}
				}
			}

			/*
			 * if (existingCrIds != null) { if (existingCrIds.size() != 0) {
			 * List<CreativeEntity> creativesEntitys =
			 * creativeRepo.findByIdIn(existingCrIds); List<DcoAttributesEntity>
			 * dcoAttributes = dcoAttributeRepository.findByCreativeIdIn(existingCrIds);
			 * Map<Long, DcoAttributesEntity> dcoAttrCreativeMap = new HashMap<>(); for
			 * (DcoAttributesEntity dcoAttr : dcoAttributes) {
			 * dcoAttrCreativeMap.put(dcoAttr.getCreativeId(), dcoAttr); } Map<Long, Long>
			 * fallbackCrMap = findFallBackCreatives(dcoAttrCreativeMap, creativesEntitys);
			 * for (CreativeEntity creative : creativesEntitys) {
			 * logger.debug("First I am here to check Bug");
			 * logger.debug("creative :{} ,  advertiser {} ", creative, advertiser); if
			 * (creative == null || creative.getId() == null) { throw new
			 * ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] {"creative",
			 * creative.getId()}); } else if (creative != null &&
			 * creative.getAdvertiserId().intValue() != advertiser.getId().intValue()) {
			 * throw new ValidationException(
			 * ErrorCode.ENTITY_OTHER_ADVERTISER_PIXELS_OR_CREATIVES_NOT_ALLOWED, new
			 * Object[] {"creative", creative.getId()}); } if
			 * (!(fallbackCrMap.containsKey(creative.getId()))) { if
			 * (existingCrIds.contains(creative.getId())) {
			 * strategyUpdateDTO.getCreativeIdsToDelete().remove(creative.getId()); } else {
			 * strategyUpdateDTO.addCreativeIdToInsert(creative.getId()); } if
			 * (creative.getIsDco()) { DcoAttributesEntity dcoAttr =
			 * dcoAttrCreativeMap.get(creative.getId()); if (dcoAttr != null) { if
			 * (dcoAttr.getFallbackCreativeId() != null) { long backUpCreativeId =
			 * dcoAttr.getFallbackCreativeId(); Creative backUpCreative =
			 * elasticSearch.searchPojoById(TablesEntity.CREATIVE, backUpCreativeId); if
			 * (backUpCreative == null || backUpCreative.getId() == null) { throw new
			 * ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[]
			 * {"fall back creative", backUpCreativeId}); } else if (backUpCreative != null
			 * && backUpCreative.getAdvertiserId() != advertiser.getId().intValue()) { throw
			 * new ValidationException(
			 * ErrorCode.ENTITY_OTHER_ADVERTISER_PIXELS_OR_CREATIVES_NOT_ALLOWED, new
			 * Object[] {"fall back creative", backUpCreativeId}); } if
			 * (existingCrIds.contains(backUpCreative.getId())) {
			 * strategyUpdateDTO.getCreativeIdsToDelete().remove(backUpCreative.getId()); }
			 * else { strategyUpdateDTO.addCreativeIdToInsert(backUpCreative.getId()); } } }
			 * 
			 * } } } } }
			 */
		}
		// else {
		if (strategy.creatives != null && !strategy.creatives.isEmpty()) {
			for (BaseModel crt : strategy.creatives) {
				CreativeEntity creative = creativeRepo.getOne(crt.getId());
				logger.debug(" I am here to check Bug");
				logger.debug("creative :{} ,  advertiser {} ", creative, advertiser);
				if (creative == null || creative.getId() == null) {
					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "creative", crt.id });
				} else if (creative != null && creative.getAdvertiserId().intValue() != advertiser.getId().intValue()) {
					throw new ApiException(ErrorCode.ENTITY_OTHER_ADVERTISER_PIXELS_OR_CREATIVES_NOT_ALLOWED,
							new Object[] { "creative", crt.id });
				}
				strategyUpdateDTO.addCreativeIdToInsert(creative.getId());
				if (creative.getIsDco().equals(Boolean.TRUE)) {
					Optional<DcoAttributesEntity> dcoAttr = dcoAttributeRepository.findById(creative.getDcoAttributesId());
					if (dcoAttr != null && dcoAttr.isPresent()) {
						if (dcoAttr.get().getFallbackCreativeId() != null) {
							Long backUpCreativeId = dcoAttr.get().getFallbackCreativeId();
							Creative backUpCreative = elasticSearch.searchPojoById(TablesEntity.CREATIVE,
									backUpCreativeId);
							if (backUpCreative == null || backUpCreative.getId() == null) {
								throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
										new Object[] { "fall back creative", backUpCreativeId });
							} else if (backUpCreative != null
									&& backUpCreative.getAdvertiserId() != advertiser.getId().intValue()) {
								throw new ValidationException(
										ErrorCode.ENTITY_OTHER_ADVERTISER_PIXELS_OR_CREATIVES_NOT_ALLOWED,
										new Object[] { "fall back creative", backUpCreativeId });
							}
							strategyUpdateDTO.addCreativeIdToInsert(backUpCreative.getId());
						}
					}

				}
			}
		}
		// }

		// Attach the pixels on campaign to strategy

		Set<Long> oldPixelIds = new HashSet<>();
		if (update) {
			List<AdvertiserLineItemPixelEntity> liPxel = lineItemPixelRepository.findAllByStrategyId(strategy.getId());
			for (AdvertiserLineItemPixelEntity advertiserIOPixel : liPxel) {
				oldPixelIds.add(advertiserIOPixel.getPixelId());
				strategyUpdateDTO.addPixelIdToDelete(advertiserIOPixel.getPixelId());
			}
		}
		List<AdvertiserIOPixel> campPixel = campPixelRepo.findAllByCampaignId(campaign.getId());
		for (AdvertiserIOPixel advertiserIOPixel : campPixel) {
			if (oldPixelIds.contains(advertiserIOPixel.getPixelId())) {
				strategyUpdateDTO.getPixelIdsToDelete().remove(advertiserIOPixel.getPixelId());
			} else {
				strategyUpdateDTO.addPixelIdToInsert(advertiserIOPixel.getPixelId());
			}
		}

		// Entries indirectly affected on update
		if (update) {
			if (!strategy.fcapEnabled) {
				strategyDO.setUserFcap(null);
				strategyDO.setUserFcapDuration(null);
			}
		}

		// Targeting Code Block START

		// Add targeting options for Geo, browsers, daypart and segments

		// RTB targeting

		// Add RTB targeting for RTB strategies
		// Make strategy a RTB strategy

		if (update) {
			targetingUtil.updateRTBInventory(strategy, strategyDO);
		}
		targetingUtil.setMinMaxBidForRTBStrategy(strategy, strategyDO);

		// Targeting Code Block END

		// Set Licensee
		strategyDO.setLicenseeId(licenseeId);

		// Set AdvertiserId
		strategyDO.setAdvertiserId(advertiser.getId());

	}

	private void validateGenericRules(StrategyDTO strategy, CampaignESDTO campaign, Pricing pricing,
			PacingType pacingType, Pricing roiTargetType, DeliveryPriority dp) throws ValidationException {

		validateDataTypeLimits(strategy);

		BigDecimal campaignBudget = campaign.getBudget();

		// checking for related entities

		if (pricing != null && strategy.pricingValue == null) {
			throw new ValidationException(ErrorCode.ENTITY_REQUIRED, new Object[] { "pricing_value", "strategy" });
		}

		logger.debug("validated pricing setting");

		if (pacingType != null) {
			Integer enumOrder = pacingType.getOrder();

			// Only if pacing type is ASAP or Deliver overall budget evenly or
			// Remaining evenly, pacing budget value is not required.
			// In the rest of the cases, pacing budget value is a REQUIRED
			// entity
			if (enumOrder != 0 && enumOrder != 1 && enumOrder != 2 && strategy.pacingBudgetValue == null) {
				throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
						new Object[] { "pacing_budget_value", "strategy" });
			}
		}

		logger.debug("validated pacing type setting");

		if (roiTargetType != null) {
			Integer enumOrder = roiTargetType.getOrder();
			if (enumOrder != 1 && enumOrder != 2) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
						new Object[] { "roi_target_type", strategy.roiTargetType.id });
			}

			if (strategy.roiTargetValue == null)
				throw new ValidationException(ErrorCode.ENTITY_REQUIRED,
						new Object[] { "roi_target_value", "strategy" });

			// TODO : add roi_target_type validation based on pricing type
		}

		logger.debug("validated roi target type setting");

		BigDecimal zero = new BigDecimal("0.000000000");
		BigDecimal minusOne = new BigDecimal("-1.000000000");

		if (strategy.budgetValue != null) {
			if (strategy.budgetValue.compareTo(minusOne) != 0
					&& (strategy.budgetValue.compareTo(zero) == -1 || strategy.budgetValue.compareTo(zero) == 0))
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
						new Object[] { "budget", strategy.budgetValue });
			else
				validateBudgetValue("budget_value", strategy.budgetValue, campaignBudget);
		}

		logger.debug("validated budget value");

		if (strategy.pacingBudgetValue != null && pacingType != null
				&& (pacingType.getOrder() != 0 && pacingType.getOrder() != 1 && pacingType.getOrder() != 2)) {
			if (strategy.pacingBudgetValue.compareTo(zero) == -1 || strategy.pacingBudgetValue.compareTo(zero) == 0)
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
						new Object[] { "pacing_budget", strategy.pacingBudgetValue });
			else if (strategy.budgetValue.compareTo(minusOne) != 0
					&& strategy.pacingBudgetValue.compareTo(strategy.budgetValue) == 1)
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
						new Object[] { "pacing_budget", "strategy budget" });
			else
				validateBudgetValue("pacing_budget_value", strategy.pacingBudgetValue, campaignBudget);
		}

		logger.debug("validated pacing budget value");

		if (strategy.pricingValue != null) {
			if (strategy.pricingValue.compareTo(zero) == -1 || strategy.pricingValue.compareTo(zero) == 0)
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
						new Object[] { "pricing_value", strategy.pricingValue });
			else
				validateBudgetValue("pricing_value", strategy.pricingValue, campaignBudget);
		}

		logger.debug("validated pricing budget value");

		if (strategy.roiTargetValue != null) {
			if (strategy.roiTargetValue.compareTo(zero) == -1 || strategy.roiTargetValue.compareTo(zero) == 0)
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
						new Object[] { "roi_target_value", strategy.roiTargetValue });
			else
				validateBudgetValue("roi_target_value", strategy.roiTargetValue, campaignBudget);
		}

		logger.debug("validated roi target budget value");

		logger.debug("validated learning budget value");

		// AP-1769. Pacing types DELIVER OVERALL BUDGET EVENLY and REMAINING
		// EVENLY should not be supported for unlimited budget strategies (both
		// campaign and strategy budget being unlimited)

		if (strategy.budgetValue != null && strategy.budgetValue.compareTo(minusOne) == 0 && campaignBudget != null
				&& campaignBudget.compareTo(minusOne) == 0) {
			// This is really an unlimited budget campaign
			if (pacingType != null && (pacingType.getOrder() == 1 || pacingType.getOrder() == 2)) {
				throw new ValidationException(ErrorCode.PACING_TYPE_NOT_SUPPORTED_WITH_UNLIMITED_BUDGET,
						new Object[] { pacingType.getName() });
			}
		}
	}

	private void validateDataTypeLimits(StrategyDTO strategy) throws ValidationException {
		if (strategy.budgetValue != null && strategy.budgetValue.compareTo(new BigDecimal(9999999999.999999999)) == 1) {
			throw new ValidationException(ErrorCode.ENTITY_VALUE_BIGGER_THAN_UPPER_LIMIT,
					new Object[] { "Strategy budget" });
		}
		if (strategy.getBidCapMax() != null
				&& strategy.getBidCapMax().compareTo(new BigDecimal(9999999999.999999999)) == 1) {
			throw new ValidationException(ErrorCode.ENTITY_VALUE_BIGGER_THAN_UPPER_LIMIT,
					new Object[] { "Strategy maximum bid cap" });
		}
		if (strategy.getBidCapMin() != null
				&& strategy.getBidCapMin().compareTo(new BigDecimal(9999999999.999999999)) == 1) {
			throw new ValidationException(ErrorCode.ENTITY_VALUE_BIGGER_THAN_UPPER_LIMIT,
					new Object[] { "Strategy minimum bid cap" });
		}
		if (strategy.pricingValue != null
				&& strategy.pricingValue.compareTo(new BigDecimal(9999999999.999999999)) == 1) {
			throw new ValidationException(ErrorCode.ENTITY_VALUE_BIGGER_THAN_UPPER_LIMIT,
					new Object[] { "Strategy pricing value" });
		}
		if (strategy.roiTargetValue != null
				&& strategy.roiTargetValue.compareTo(new BigDecimal(9999999999.999999999)) == 1) {
			throw new ValidationException(ErrorCode.ENTITY_VALUE_BIGGER_THAN_UPPER_LIMIT,
					new Object[] { "Strategy roi target value" });
		}
		if (strategy.pacingBudgetValue != null
				&& strategy.pacingBudgetValue.compareTo(new BigDecimal(9999999999.999999999)) == 1) {
			throw new ValidationException(ErrorCode.ENTITY_VALUE_BIGGER_THAN_UPPER_LIMIT,
					new Object[] { "Strategy pacing value" });
		}

		// Checking for negative values as well

		if (strategy.getBidCapMax() != null
				&& strategy.getBidCapMax().compareTo(new BigDecimal("0000000000.000000000")) == -1
				&& strategy.getBidCapMax().compareTo(new BigDecimal("-1.000000000")) != 0) {
			throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
					new Object[] { "Strategy bid_cap_max", strategy.getBidCapMax() });
		}

		if (strategy.getBidCapMin() != null
				&& strategy.getBidCapMin().compareTo(new BigDecimal("0000000000.000000000")) == -1) {
			throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
					new Object[] { "Strategy bid_cap_min", strategy.getBidCapMin() });
		}
	}

	private Map<Long, Long> findFallBackCreatives(Map<Long, DcoAttributesEntity> dcpMap,
			List<CreativeEntity> creativesEntitys) {
		Map<Long, Long> crMap = new HashMap<>();
		for (CreativeEntity crEntity : creativesEntitys) {
			DcoAttributesEntity dcoAttributesEntity = dcpMap.get(crEntity.getId());
			if (dcoAttributesEntity != null && dcoAttributesEntity.getFallbackCreativeId() != null
					&& crEntity.getIsDco()) {
				Creative backupCreative = elasticSearch.searchPojoById(TablesEntity.CREATIVE,
						dcoAttributesEntity.getFallbackCreativeId());
				if (backupCreative != null)
					crMap.put(backupCreative.getId(), backupCreative.getId());
			}
		}

		return crMap;
	}

	private void validateBudgetValue(String budgetName, BigDecimal budgetValue, BigDecimal campaignBudget)
			throws ValidationException {
		BigDecimal minusOne = new BigDecimal(-1.000000000);
		// TODO : Need to add more budget validations
		if (campaignBudget != null && campaignBudget.compareTo(minusOne) != 0
				&& budgetValue.compareTo(campaignBudget) == 1) {
			throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE, new Object[] { "Invalid budget value" });
		}
	}

	public Strategy populateStrategyForElasticSearch(StrategyDTO strategyPojo) {

		if (strategyPojo == null)
			return null;

		Strategy strategy = new Strategy();
		strategy.setLicenseeId(strategyPojo.getLicenseeId());
		strategy.setAdvertiserId(strategyPojo.getAdvertiserId());
		strategy.setCampaignId(strategyPojo.getCampaignId());
		strategy.setCurrencyCode(strategyPojo.getCurrencyCode());
		strategy.setLicensee(strategyPojo.getLicensee());
		strategy.setCurrency(strategyPojo.getCurrency());
		strategy.setBudget(strategyPojo.getBudget());
		
		Integer fcap = strategyPojo.getFcapFrequency()!=null ? strategyPojo.getFcapFrequency().intValue() : null;
		strategy.setFcap(fcap);
		
		strategy.setDaysDuration(strategyPojo.getDaysDuration());
		strategy.setDaysElapsed(strategyPojo.getDaysElapsed());
		strategy.setStartTime(strategyPojo.getStartTime());
		strategy.setEndTime(strategyPojo.getEndTime());
		strategy.setId(strategyPojo.getId());
		strategy.setName(strategyPojo.getName());
		strategy.setCreatedBy(strategyPojo.getCreatedBy());
		strategy.setCreationTime(strategyPojo.getCreationTime());
		strategy.setModifiedBy(strategyPojo.getModifiedBy());
		strategy.setModifiedTime(strategyPojo.getModifiedTime());
		strategy.setActive(strategyPojo.isActive());
		return strategy;
	}

}
