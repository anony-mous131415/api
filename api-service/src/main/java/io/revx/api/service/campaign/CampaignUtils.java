package io.revx.api.service.campaign;

import io.revx.core.model.BaseModel;
import io.revx.core.model.CurrencyModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.mysql.entity.campaign.AdvertiserIOPixel;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ApiException;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.Strategy;
import io.revx.core.model.campaign.CampaignDTO;
import io.revx.core.model.requests.EResponse;

@Component
public class CampaignUtils {

	private static Logger logger = LogManager.getLogger(CampaignUtils.class);

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	EntityESService elasticSearch;

	public void updateCampEntity(CampaignDTO campaignPojo, CampaignEntity campaignEntity) {
		logger.debug("Inside populateCampaignEntity method. UserInfo  : {}", loginUserDetailsService.getUserInfo());

		campaignEntity.setModifiedBy(loginUserDetailsService.getUserInfo().getUserId());
		campaignEntity.setModifiedTime(System.currentTimeMillis() / 1000);

		campaignEntity.setLicenseeId(loginUserDetailsService.getLicenseeId());

		if (StringUtils.isNotBlank(campaignPojo.getName()))
			campaignEntity.setName(campaignPojo.getName());

		if (Boolean.compare(campaignPojo.isActive(), campaignEntity.isActive()) != 0)
			campaignEntity.setActive(campaignPojo.isActive());

		if (campaignPojo.getEndTime() != null)
			campaignEntity.setEndTime(campaignPojo.getEndTime());

		if (campaignPojo.getLifetimeBudget() != null)
			campaignEntity.setLifetimeBudget(campaignPojo.getLifetimeBudget());

		if (campaignPojo.getPlatformMargin() != null)
			campaignEntity.setPlatformMargin(campaignPojo.getPlatformMargin());

		if (campaignPojo.getIvsDistribution() != null)
			campaignEntity.setIvsDistribution(campaignPojo.getIvsDistribution());

		if (campaignPojo.getPricingId() != null)
			campaignEntity.setPricingId(campaignPojo.getPricingId());

		if (campaignPojo.getFlowRate() != null)
			campaignEntity.setFlowRate(campaignPojo.getFlowRate());

		if (campaignPojo.getAttributionRatio() != null)
			campaignEntity.setAttributionRatio(campaignPojo.getAttributionRatio());

		campaignEntity.setRetargeting(campaignPojo.isRetargeting());

		if (campaignPojo.getDailyUserFcap() != null)
			campaignEntity.setDailyUserFcap(campaignPojo.getDailyUserFcap());

		if (campaignPojo.getUserFcapDuration() != null)
			campaignEntity.setUserFcapDuration(campaignPojo.getUserFcapDuration());

		/*
		 * The pricing/bidding types like CPI/Margin are setting the respective values
		 * to null and those changes are not being saved to db and old values are being
		 * retained itself in the POJO because of null check, so removing the check
		 * 
		 * REVX-231 ---- delivery cap feilds not getting updated in DB
		 */

		campaignEntity.setLifetimeDeliveryCap(campaignPojo.getLifetimeDeliveryCap());

		campaignEntity.setDailyBudget(campaignPojo.getDailyBudget());

		campaignEntity.setDailyDeliveryCap(campaignPojo.getDailyDeliveryCap());

		/*
		 * REVX-565 - updating campaign lifetime frequency cap is failing when from changing
		 * limited to unlimited because UI is passing null which we are discarding with the
		 * existing null check so removing the null check
		 */

		campaignEntity.setLifetimeUserFcap(campaignPojo.getLifetimeUserFcap());

		// REVX-352: update CPA target value
		campaignEntity.setCpaTarget(campaignPojo.getCpaTarget());

	}

	public void populateCampaignFromEntity(CampaignEntity campaignEntity, CampaignDTO campaignPojo)
			throws ApiException {
		BeanUtils.copyProperties(campaignEntity, campaignPojo);

		CurrencyModel currencyCode  = elasticSearch.searchPojoById(TablesEntity.CURRENCY,campaignEntity.getCurrencyId());
		campaignPojo.setId(campaignEntity.getId().longValue());
		campaignPojo.setAdvertiserId(campaignEntity.getAdvertiserId().longValue());
		campaignPojo.setCreatedBy(campaignEntity.getCreatedBy());
		campaignPojo.setModifiedBy(campaignEntity.getModifiedBy());
		campaignPojo.setCurrency(new BaseModel(currencyCode.getId(),currencyCode.getName()));
		campaignPojo.setRegion(elasticSearch.searchById(TablesEntity.COUNTRY, campaignEntity.getRegionId()));
		campaignPojo.setLicensee(elasticSearch.searchById(TablesEntity.LICENSEE, campaignEntity.getLicenseeId()));
		campaignPojo.setCurrencyCode(currencyCode.getCurrencyCode());
	}

	public void populateCampaignPixelFromEntity(AdvertiserIOPixel ioPixelEntity, CampaignDTO campaignPojo)
			throws ApiException {

		if (ioPixelEntity != null && ioPixelEntity.getCampaignId() != null)
			campaignPojo.setPixel(elasticSearch.searchById(TablesEntity.PIXEL, ioPixelEntity.getPixelId()));
	}

	public CampaignESDTO populateCampaignForESDTO(CampaignDTO campaignPojo) {

		if (campaignPojo == null)
			return null;

		logger.info("In populateCampaignForESDTO method , received CampaignDTO : {} ", campaignPojo);

		CampaignESDTO campaign = new CampaignESDTO();
		CurrencyModel currencyCode  = elasticSearch.searchPojoById(TablesEntity.CURRENCY,campaignPojo.getCurrency().getId());
		campaign.setLicenseeId(campaignPojo.getLicenseeId());
		campaign.setAdvertiserId(campaignPojo.getAdvertiserId());
		campaign.setCurrencyCode(currencyCode.getCurrencyCode());
		campaign.setBudget(campaignPojo.getDailyBudget());
		campaign.setDailyBudget(campaignPojo.getDailyBudget());
		campaign.setLifetimeBudget(campaignPojo.getLifetimeBudget());
		campaign.setFcap(campaignPojo.getDailyUserFcap().intValue());
		campaign.setDailyUserFcap(campaignPojo.getDailyUserFcap());
		campaign.setLifetimeUserFcap(campaignPojo.getLifetimeUserFcap());
		campaign.setDaysDuration(campaignPojo.getDaysDuration());
		campaign.setDaysElapsed(campaignPojo.getDaysElapsed());
		campaign.setStartTime(campaignPojo.getStartTime());
		campaign.setEndTime(campaignPojo.getEndTime());
		campaign.setId(campaignPojo.getId());
		campaign.setName(campaignPojo.getName());
		campaign.setCreatedBy(campaignPojo.getCreatedBy());
		campaign.setCreationTime(campaignPojo.getCreationTime());
		campaign.setModifiedBy(campaignPojo.getModifiedBy());
		campaign.setModifiedTime(campaignPojo.getModifiedTime());
		campaign.setActive(campaignPojo.isActive());
		campaign.setCpaTarget(campaignPojo.getCpaTarget());
		if (campaignPojo.getPixel() == null) {
			campaign.setPixelId(null);
		} else {
			campaign.setPixelId(campaignPojo.getPixel().getId());
		}
		if (campaignPojo.getSkadTarget() == null) {
			campaign.setSkadTarget(false);
		} else {
			campaign.setSkadTarget(campaignPojo.getSkadTarget());
		}

		logger.info("In populateCampaignForESDTO method , setup ES-DTO : {} ", campaign);

		return campaign;
	}

	public void populateCampaignEntity(CampaignEntity campaignEntity, CampaignDTO campaignPojo) {
		logger.debug("Inside populateCampaignEntity method. UserInfo  : " + loginUserDetailsService.getUserInfo());
		BeanUtils.copyProperties(campaignPojo, campaignEntity);

		campaignEntity.setLicenseeId(loginUserDetailsService.getLicenseeId());
		campaignEntity.setAdvertiserId(campaignPojo.getAdvertiserId());
		campaignEntity.setCreationTime(System.currentTimeMillis() / 1000);
		campaignEntity.setCreatedBy(loginUserDetailsService.getUserInfo().getUserId());
		campaignEntity.setModifiedBy(loginUserDetailsService.getUserInfo().getUserId());
		campaignEntity.setModifiedTime(System.currentTimeMillis() / 1000);
		campaignEntity.setCurrencyId(campaignPojo.getCurrency().getId());
		campaignEntity.setRegionId(campaignPojo.getRegion().getId());
		campaignEntity
				.setUserFcapDuration(campaignPojo.getUserFcapDuration() != null ? campaignPojo.getUserFcapDuration()
						: Constants.DEFAULT_FCAP_DURATION);

		// setting appropriate start-time
		if (campaignPojo.getStartTime() == -1L) {
			campaignEntity.setStartTime(campaignEntity.getCreationTime()); // same as Creation Time
		} // else case is not required bcz handled by BeanUtils.copyProperties
		if (campaignPojo.getSkadTarget() == null) {
			campaignEntity.setSkadTarget(false);
		} else {
			campaignEntity.setSkadTarget(campaignPojo.getSkadTarget());
		}

	}

	public void populateIOPixelEntity(AdvertiserIOPixel advertiserIOPixel, CampaignDTO campaignPojo) {
		logger.debug("Inside populateIOPixelEntity method. campaignPojo  : " + campaignPojo);

		if (campaignPojo.getPixel() != null && campaignPojo.getPixel().getId() != null) {
			advertiserIOPixel.setCampaignId(campaignPojo.getId());
			advertiserIOPixel.setPixelId(campaignPojo.getPixel().getId());
		}
		logger.debug("Inside populateIOPixelEntity method. Pixel populated. advertiserIOPixel  : " + advertiserIOPixel);
	}

	public void populateFcap(CampaignDTO campaignPojo) {
		ElasticSearchTerm searchTerm = new ElasticSearchTerm();
		searchTerm.setLicenseeId(loginUserDetailsService.getLicenseeId());
		searchTerm.setCampaigns(campaignPojo.getId());
		searchTerm.setFilters("active", "true");
		EResponse<Strategy> response = elasticSearch.searchAll(TablesEntity.STRATEGY, searchTerm);

		Integer dailyUserFcap = 0;
		logger.debug("Inside populateFcap method. For campaign id : {}. Got the strategy count from ES : {} ",
				campaignPojo.getId(), response.getTotalNoOfRecords());
		if (CollectionUtils.isEmpty(response.getData()))
			return;

		for (Strategy strategy : response.getData()) {
			logger.debug("Inside populateFcap method. Iterating over strategy id : {} & fcap : {}", strategy.getId(),
					strategy.getFcap());
			if (strategy.getFcap() != null)
				if (strategy.getFcap() > dailyUserFcap) {
					dailyUserFcap = strategy.getFcap();
				}
		}
		if (dailyUserFcap > 0)
			campaignPojo.setFcap(dailyUserFcap);

		int cmpFcap = campaignPojo.getFcap().intValue();
		logger.debug("Final campaign level daily fcap : {}", cmpFcap);
	}
}
