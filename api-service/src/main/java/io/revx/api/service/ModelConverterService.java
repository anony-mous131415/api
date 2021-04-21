/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.validation.Valid;

import io.revx.core.model.CurrencyModel;
import io.revx.core.model.ClickDestinationESDTO;
import io.revx.core.model.MobileMeasurementPartner;
import io.revx.core.model.AppSettingsDTO;
import io.revx.core.model.AppSettingsPropertyDTO;
import io.revx.core.model.LogoModel;
import io.revx.core.model.ClickDestinationAutomationUrls;
import io.revx.api.mysql.entity.AppSettingsEntity;
import io.revx.api.mysql.entity.AppSettingsPropertyEntity;
import io.revx.api.service.appsettings.AppSettingsUtil;
import io.revx.core.enums.AppSettingsKey;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.dco.entity.catalog.AdvertiserCatalogVariablesMappingEntity;
import io.revx.api.mysql.dco.entity.catalog.AtomCatalogVariablesEntity;
import io.revx.api.mysql.dco.entity.catalog.FeedApiStatusEntity;
import io.revx.api.mysql.dco.entity.catalog.FeedInfoEntity;
import io.revx.api.mysql.dco.entity.catalog.FeedInfoStatsEntity;
import io.revx.api.mysql.dco.repo.catalog.AdvertiserCatalogVariableMappingRepository;
import io.revx.api.mysql.dco.repo.catalog.AtomCatalogVariableRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedApiStatsRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoStatsRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.entity.advertiser.CurrencyEntity;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity.PixelType;
import io.revx.api.mysql.repo.advertiser.AdvertiserRepository;
import io.revx.api.pojo.ChartPerformanceDataMetrics;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.campaign.CurrencyCacheService;
import io.revx.api.service.catalog.CatalogUtil;
import io.revx.api.service.pixel.impl.ConversionPixelService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.Creative;
import io.revx.core.model.DashboardData;
import io.revx.core.model.DashboardMetrics;
import io.revx.core.model.Strategy;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.utils.NumberUtils;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.objs.FilterComponent;

@Component
public class ModelConverterService {

	private static Logger logger = LogManager.getLogger(ModelConverterService.class);
	private static long secondInHour = 60 * 60;
	private static long secondInDay = 24 * secondInHour;

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	FeedInfoStatsRepository feedInfoStatsRepo;

	@Autowired
	AdvertiserCatalogVariableMappingRepository acvmRepo;

	@Autowired
	AdvertiserRepository advertiserRepo;

	@Autowired
	AtomCatalogVariableRepository acvRepo;

	@Autowired
	ApplicationProperties applicationProperties;

	@Autowired
	FeedApiStatsRepository feedApiStatsRepo;

	@Autowired
	CurrencyCacheService currencyCache;

	@Autowired
	CatalogUtil util;

	@Autowired
	EntityESService elasticSearch;

	@Autowired
	ConversionPixelService pixelService;

	@Autowired
	private AppSettingsUtil appSettingsUtil;

	public void populateListData(List<ListPerformanceDataMetrics> totalResult,
			String advertiserCurrCode, String licenseeCurrCode, List<BaseModel> listData,
			Map<Long, ?> idModelMap, GroupBy groupByInReq,
			Map<FilterType, Set<FilterComponent>> tableFilters) {
		try {
			boolean isAdvCurrency = isAdvDataInCurrency(tableFilters);
			Set<Long> populatedIds = new HashSet<Long>();
			logger.debug(" totalResult.size :" + totalResult.size());
			for (ListPerformanceDataMetrics dbData : totalResult) {
				logger.debug("dbData :" + dbData);
				DashboardData dd = new DashboardData();
				makeDashBoardListData(dbData, dd, advertiserCurrCode, licenseeCurrCode, isAdvCurrency);
				populateGroupByColumn(dbData, dd, groupByInReq, idModelMap);
				// if (dd.getId() != null && dd.getId() > 0)
				listData.add(dd);
				populatedIds.add(dd.getId());
			}
			// Populating data which are in ELastic but not in perf
			for (Entry<Long, ?> keyValue : idModelMap.entrySet()) {
				logger.debug("keyValue Map :" + keyValue.getKey());
				if (!populatedIds.contains(keyValue.getKey())) {
					logger.debug("Populating For  Map Value :" + keyValue.getKey());
					DashboardData dd = new DashboardData();
					dd.initialiseWithZero();
					populateGroupByColumn(dd, groupByInReq, keyValue.getValue());
					if (advertiserCurrCode != null)
						dd.setCurrencyId(advertiserCurrCode);
					else
						dd.setCurrencyId(licenseeCurrCode);
					dd.setCalculatedFields();
					if (loginUserDetailsService.getUserInfo() != null && loginUserDetailsService.isReadOnlyUser()) {
						dd.makeFieldsNullForReadUser();
					}
					listData.add(dd);
				}
			}
		} catch (Exception e) {
			logger.debug("Exception " + ExceptionUtils.getStackTrace(e));
		}

	}

	private void populateGroupByColumn(DashboardData dd, GroupBy groupByInReq, Object value) {
		switch (groupByInReq) {
		case ADVERTISER_ID:
			dd.setAdvertiser((Advertiser) value);
			dd.populateIdAndNameAndModifiedTime(dd.getAdvertiser());
			logger.debug("Inside Group Method {} ", dd.getAdvertiser());
			break;
		case CAMPAIGN_ID:
			dd.setCampaign((CampaignESDTO) value);
			dd.populateIdAndNameAndModifiedTime(dd.getCampaign());
			logger.debug("Inside Group Method {}", dd.getCampaign());
			break;
		case STRATEGY_ID:
			dd.setStrategy((Strategy) value);
			dd.populateIdAndNameAndModifiedTime(dd.getStrategy());
			logger.debug("Inside Group Method {} ", dd.getStrategy());
			break;
		case CREATIVE_ID:
			dd.setCreative((Creative) value);
			dd.populateIdAndNameAndModifiedTime(dd.getCreative());
			logger.debug("Inside Group Method {} ", dd.getCreative());
			break;
		default:
			break;
		}

	}

	public void populateGroupByColumn(ListPerformanceDataMetrics dbData, DashboardData dd,
			GroupBy groupByInReq, Map<Long, ?> idModelMap) {
		if (idModelMap != null && groupByInReq != null) {
			switch (groupByInReq) {
			case ADVERTISER_ID:
				dd.setAdvertiser(
						(Advertiser) idModelMap.get(NumberUtils.getLongValue(dbData.getAdvertiserid())));
				dd.populateIdAndName(dd.getAdvertiser());
				logger.debug("Inside Group Method {} : {} ", dd.getAdvertiser());
				break;
			case CAMPAIGN_ID:
				dd.setCampaign(
						(CampaignESDTO) idModelMap.get(NumberUtils.getLongValue(dbData.getCampaignid())));
				dd.populateIdAndName(dd.getCampaign());
				logger.debug("Inside Group Method {} ::  {} ", dd.getCampaign());
				break;
			case STRATEGY_ID:
				dd.setStrategy(
						(Strategy) idModelMap.get(NumberUtils.getLongValue(dbData.getStrategyid())));
				dd.populateIdAndName(dd.getStrategy());
				logger.debug("Inside Group Method {} ", dd.getStrategy());
				break;
			case CREATIVE_ID:
				dd.setCreative(
						(Creative) idModelMap.get(NumberUtils.getLongValue(dbData.getCreativeid())));
				dd.populateIdAndName(dd.getCreative());
				logger.debug("Inside Group Method {} ", dd.getCreative());
				break;
			default:
				break;
			}
		}

	}

	public void makeDashBoardListData(ListPerformanceDataMetrics dbData, DashboardData dd,
			String advertiserCurrCode, String licenseeCurrCode, boolean isAdvCurrency) {
		logger.debug("dbData :" + dbData);
		populateDatshBoradDataFromPerfLi(dbData, dd, advertiserCurrCode, licenseeCurrCode);
		dd.setHour(dbData.getHour());
		dd.setDay(dbData.getDay());
	}

	public void populateChartData(DashboardRequest dashboardRequest,
			List<ChartPerformanceDataMetrics> totalResult, String advertiserCurrCode,
			String licenseeCurrCode, List<DashboardData> chartData,
			Map<FilterType, Set<FilterComponent>> tableFilters) {
		try {
			boolean isAdvCurrency = isAdvDataInCurrency(tableFilters);
			logger.debug(" totalResult.size :" + totalResult.size());
			for (ChartPerformanceDataMetrics dbData : totalResult) {
				logger.debug("dbData :" + dbData);
				DashboardData dd = new DashboardData();
				makeDashBoardChartData(dbData, dd, advertiserCurrCode, licenseeCurrCode,
						dashboardRequest.getGroupBy(), isAdvCurrency);
				chartData.add(dd);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void populateChartWidgetData(List<PerformanceDataMetrics> result,
			String advertiserCurrCode, String licenseeCurrCode, DashboardMetrics widgetData,
			Map<FilterType, Set<FilterComponent>> tableFilters) {
		try {
//			boolean isAdvCurrency = isAdvDataInCurrency(tableFilters);
			logger.debug(" Widget totalResult.size :" + result.size());
			if (result != null && result.size() > 0) {
				PerformanceDataMetrics mat = result.get(0);
				populateDatshBoradDataFromPerfLi(mat, widgetData, advertiserCurrCode, licenseeCurrCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private boolean isAdvDataInCurrency(Map<FilterType, Set<FilterComponent>> tableFilters) {
		boolean isAdvCurrencyData = false;
		if (tableFilters != null) {
			for (Entry<FilterType, Set<FilterComponent>> performanceDataMetrics : tableFilters
					.entrySet()) {
				for (FilterComponent fc : performanceDataMetrics.getValue()) {
					if (fc != null && fc.getField() != null && (fc.getField() == Filter.ADVERTISER_ID
							|| fc.getField() == Filter.CAMPAIGN_ID || fc.getField() == Filter.STRATEGY_ID)) {
						isAdvCurrencyData = true;
						break;
					}
				}
			}
		}
		return isAdvCurrencyData;
	}

	public void makeDashBoardChartData(ChartPerformanceDataMetrics dbData, DashboardData dd,
			String advertiserCurrCode, String licenseeCurrCode, String groupBy, boolean isAdvCurrency) {
		populateDatshBoradDataFromPerfLi(dbData, dd, advertiserCurrCode, licenseeCurrCode);
		dd.setHour(dbData.getHour());
		dd.setDay(dbData.getDay());
		dd.setId(dd.getHour() != null ? dd.getHour().longValue() : dd.getDay().longValue());
		dd.setStartTimestamp(dd.getHour() != null ? dd.getHour().longValue() : dd.getDay().longValue());
		dd.setEndTimestamp(dd.getStartTimestamp() + getIntaralOfTime(groupBy));
	}

	public Long getIntaralOfTime(String groupBy) {
		if (StringUtils.equalsIgnoreCase("hour", groupBy))
			return secondInHour;
		return secondInDay;
	}

	public void populateDatshBoradDataFromPerfLi(PerformanceDataMetrics dbData, DashboardMetrics dd,
			String advertiserCurrCode, String licenseeCurrCode) {
		BeanUtils.copyProperties(dbData, dd);
		dd.setImpressions(dbData.getImpressions());
		dd.setClicks(dbData.getClicks());
		dd.setClickConversions(dbData.getClickconversions());
		dd.setViewConversions(dbData.getViewconversions());
		dd.setClickInstalls(dbData.getClickinstalls());
		dd.setImpInstalls(dbData.getViewinstalls());
		if (advertiserCurrCode != null) {
			dd.setCost(dbData.getCostinadvertisercurrency());
			dd.setRevenue(dbData.getRevenueinadvertisercurrency());
			dd.setAdvRevenue(dbData.getTxnamountinadvertisercurrency());
			dd.setCurrencyId(advertiserCurrCode);
		} else {
			dd.setCost(dbData.getCostinlicenseecurrency());
			dd.setRevenue(dbData.getRevenueinlicenseecurrency());
			dd.setAdvRevenue(dbData.getTxnamountinlicenseecurrency());
			dd.setCurrencyId(licenseeCurrCode);
		}
		dd.setEligibleUniqUsers(NumberUtils.getLongValue(dbData.getEligibleuniqusers()));
		dd.setImpressionUniqUsers(NumberUtils.getLongValue(dbData.getImpressionuniqusers()));
		dd.setBidsPlaced(dbData.getBidsplaced());
		dd.setInvalidClicks(dbData.getInvalidclicks());
		dd.setInvalidClicks(dbData.getInvalidclicks());
		dd.setEligibleBids(NumberUtils.getLongValue(dbData.getEligiblebids()));
		dd.setCalculatedFields();
		if (loginUserDetailsService.getUserInfo() != null && loginUserDetailsService.isReadOnlyUser()) {
			dd.makeFieldsNullForReadUser();
		}
	}

	private boolean isValidForAdvertiserCurrency(boolean isAdvCurrency) {
		return loginUserDetailsService.isAdvertiserLogin() || isAdvCurrency;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getSubList(List<T> l, Integer offset, Integer limit) {

		if (l == null || l.size() == 0)
			return Collections.EMPTY_LIST;
		if (offset == null || limit == null)
			return l;
		if (offset <= 0 || limit <= 0)
			return Collections.EMPTY_LIST;
		if ((offset - 1) * limit > l.size())
			return Collections.EMPTY_LIST;
		if (offset * limit >= l.size())
			return l.subList((offset - 1) * limit, l.size());
		return l.subList((offset - 1) * limit, offset * limit);
	}

	// should be called only in case of create as=dvertiser
	public AdvertiserEntity populateAdvEntity(AdvertiserPojo advertiserPojo) throws ApiException {
		AdvertiserEntity advEntity = new AdvertiserEntity();

		// generated at runtime
		advEntity.setSystemEntryTime(System.currentTimeMillis() / 1000);
		advEntity.setModifiedOn(System.currentTimeMillis() / 1000);
		advEntity.setCreatedBy(loginUserDetailsService.getUserInfo().getUserId());
		advEntity.setLanguageId(advertiserPojo.getLanguage().getId());

		// mandatory fields
		advEntity.setAdvertiserName(advertiserPojo.getName());
		advEntity.setIsActive(advertiserPojo.isActive());
		advEntity.setCurrencyId(advertiserPojo.getCurrency().getId());
		advEntity.setRegionId(advertiserPojo.getRegion().getId());
		advEntity.setLicenseeId(advertiserPojo.getLicenseeId());
		advEntity.setModifiedBy(loginUserDetailsService.getUserInfo().getUserId());
		advEntity.setTimeZoneId(advertiserPojo.getTimeZone().getId());
		advEntity.setDomain(advertiserPojo.getDomain());
		advEntity.setWebDeclareUrl(trimIt(advertiserPojo.getWebDeclareUrl()));
		advEntity.setAndroidDeclareUrl(trimIt(advertiserPojo.getAndroidDeclareUrl()));
		advEntity.setIosDeclareUrl(trimIt(advertiserPojo.getIosDeclareUrl()));
		advEntity.setIosPhoneBundle(trimIt(advertiserPojo.getIosPhoneBundle()));
		advEntity.setIosTabletBundle(trimIt(advertiserPojo.getIosTabletBundle()));
		advEntity.setAndroidPhoneBundle(trimIt(advertiserPojo.getAndroidPhoneBundle()));
		advEntity.setAndroidTabletBundle(trimIt(advertiserPojo.getAndroidTabletBundle()));
		advEntity.setCategoryId(advertiserPojo.getCategory().getId());
		advEntity.setSkuAllowedCharacters(advertiserPojo.getSkuAllowedChars());
		// non-manadatory fields
		if (advertiserPojo.getLanguage() != null && advertiserPojo.getLanguage().getId() != null)
			advEntity.setLanguageId(advertiserPojo.getLanguage().getId());
		if (isNotBlank(advertiserPojo.getEmail()))
			advEntity.setAdvEmail(trimIt(advertiserPojo.getEmail()));
		if (isNotBlank(advertiserPojo.getContactAddress()))
			advEntity.setAdvAddress(trimIt(advertiserPojo.getContactAddress()));
		if (isNotBlank(advertiserPojo.getContactNumber()))
			advEntity.setAdvContactNumber(trimIt(advertiserPojo.getContactNumber()));
		if (advertiserPojo.getLicenseeId() > 0)
			advEntity.setLicenseeId(advertiserPojo.getLicenseeId());
		else
			advEntity.setLicenseeId(loginUserDetailsService.getLicenseeId());

		advEntity.setSkuAllowedCharacters("");
		advEntity.setIsEventFilterAllowed(Boolean.TRUE);
		advEntity.setIsLiftTestActive(Boolean.FALSE);

		advEntity.setMmpId(advertiserPojo.getMMP().getId());
		return advEntity;
	}

	/**
	 * Update adv entity.
	 *
	 * @param advertiserPojo the advertiser pojo
	 * @param advEntity the adv entity
	 * @throws ApiException the api exception
	 */
	public void updateAdvEntity(AdvertiserPojo advertiserPojo, AdvertiserEntity advEntity)
			throws ApiException {

		if (isNotBlank(advertiserPojo.getName()))
			advEntity.setAdvertiserName(advertiserPojo.getName());

		if (advertiserPojo.getLanguage() != null && advertiserPojo.getLanguage().getId() != null)
			advEntity.setLanguageId(advertiserPojo.getLanguage().getId());

		if (Boolean.compare(advertiserPojo.isActive(), advEntity.getIsActive()) != 0)
			advEntity.setIsActive(advertiserPojo.isActive());

		if (isNotBlank(advertiserPojo.getEmail()))
			advEntity.setAdvEmail(trimIt(advertiserPojo.getEmail()));

		if (isNotBlank(advertiserPojo.getContactAddress()))
			advEntity.setAdvAddress(trimIt(advertiserPojo.getContactAddress()));

		if (isNotBlank(advertiserPojo.getContactNumber()))
			advEntity.setAdvContactNumber(trimIt(advertiserPojo.getContactNumber()));

		advEntity.setModifiedOn(System.currentTimeMillis() / 1000);
		advEntity.setModifiedBy(loginUserDetailsService.getUserInfo().getUserId());
		advEntity.setLicenseeId(advertiserPojo.getLicenseeId());

		if (isNotBlank(advertiserPojo.getDomain()))
			advEntity.setDomain(advertiserPojo.getDomain());

		if (advertiserPojo.getCategory() != null && advertiserPojo.getCategory().getId() != null)
			advEntity.setCategoryId(advertiserPojo.getCategory().getId());

		advEntity.setWebDeclareUrl(trimIt(advertiserPojo.getWebDeclareUrl()));
		advEntity.setAndroidDeclareUrl(trimIt(advertiserPojo.getAndroidDeclareUrl()));
		advEntity.setIosDeclareUrl(trimIt(advertiserPojo.getIosDeclareUrl()));
		advEntity.setIosPhoneBundle(trimIt(advertiserPojo.getIosPhoneBundle()));
		advEntity.setIosTabletBundle(trimIt(advertiserPojo.getIosTabletBundle()));
		advEntity.setAndroidPhoneBundle(trimIt(advertiserPojo.getAndroidPhoneBundle()));
		advEntity.setAndroidTabletBundle(trimIt(advertiserPojo.getAndroidTabletBundle()));
		if(advertiserPojo.getRegion()!=null)
			advEntity.setRegionId(advertiserPojo.getRegion().getId());

		if(advertiserPojo.getTimeZone()!=null)
			advEntity.setTimeZoneId(advertiserPojo.getTimeZone().getId());

		if (advertiserPojo.getMMP() != null && advertiserPojo.getMMP().getId() != null)
			advEntity.setMmpId(advertiserPojo.getMMP().getId());
	}

	public AdvertiserPojo populateAdvertiserFromEntity(AdvertiserEntity advEntity) throws Exception {
		AdvertiserPojo advertiserPojo = new AdvertiserPojo();
		CurrencyModel currencyCode  = elasticSearch.searchPojoById(TablesEntity.CURRENCY,advEntity.getCurrencyId());

		advertiserPojo.setId((long) advEntity.getId());

		advertiserPojo.setName(advEntity.getAdvertiserName());
		advertiserPojo.setActive(advEntity.getIsActive());
		advertiserPojo.setCreationTime(advEntity.getSystemEntryTime());
		advertiserPojo.setModifiedTime(advEntity.getModifiedOn());
		advertiserPojo.setCurrencyCode(currencyCode.getCurrencyCode());

		advertiserPojo
		.setCurrency(elasticSearch.searchById(TablesEntity.CURRENCY, advEntity.getCurrencyId()));
		advertiserPojo
		.setLanguage(elasticSearch.searchById(TablesEntity.LANGUAGE, advEntity.getLanguageId()));

		if (isNotBlank(advEntity.getAdvEmail()))
			advertiserPojo.setEmail(advEntity.getAdvEmail());

		if (isNotBlank(advEntity.getAdvAddress()))
			advertiserPojo.setContactAddress(advEntity.getAdvAddress());

		if (isNotBlank(advEntity.getAdvContactNumber()))
			advertiserPojo.setContactNumber(advEntity.getAdvContactNumber());
		
		// REVX-306: Change entity from COUNTRY to ADVERTISER_REGION
		advertiserPojo
		.setRegion(elasticSearch.searchById(TablesEntity.ADVERTISER_REGION, advEntity.getRegionId()));
		advertiserPojo.setLicenseeId(advEntity.getLicenseeId());
		advertiserPojo
		.setTimeZone(elasticSearch.searchById(TablesEntity.TIMEZONE, advEntity.getTimeZoneId()));
		advertiserPojo.setDomain(advEntity.getDomain());
		advertiserPojo
		.setCategory(elasticSearch.searchById(TablesEntity.CATEGORY, advEntity.getCategoryId()));

		advertiserPojo.setWebDeclareUrl(advEntity.getWebDeclareUrl());
		advertiserPojo.setAndroidDeclareUrl(advEntity.getAndroidDeclareUrl());
		advertiserPojo.setIosDeclareUrl(advEntity.getIosDeclareUrl());
		advertiserPojo.setIosPhoneBundle(advEntity.getIosPhoneBundle());
		advertiserPojo.setIosTabletBundle(advEntity.getIosTabletBundle());
		advertiserPojo.setAndroidPhoneBundle(advEntity.getAndroidPhoneBundle());
		advertiserPojo.setAndroidTabletBundle(advEntity.getAndroidTabletBundle());
		if (advEntity.getCreatedBy() != null)
			advertiserPojo.setCreatedBy(advEntity.getCreatedBy());

		if (advEntity.getModifiedBy() != null)
			advertiserPojo.setModifiedBy(advEntity.getModifiedBy());

		if (advEntity.getMmpId() != null)
			advertiserPojo.setMMP(elasticSearch.searchById(TablesEntity.MMP, advEntity.getMmpId()));
		else
			advertiserPojo.setMMP(null);


		return advertiserPojo;
	}


	public Advertiser populateAdvForElastic(AdvertiserEntity advEntity) {
		Advertiser adv = new Advertiser();
		CurrencyModel currencyCode  = elasticSearch.searchPojoById(TablesEntity.CURRENCY,advEntity.getCurrencyId());
		adv.setId(advEntity.getId());
		adv.setName(advEntity.getAdvertiserName());
		adv.setActive(advEntity.getIsActive());
		adv.setLicenseeId(advEntity.getLicenseeId());
		adv.setCurrency(elasticSearch.searchById(TablesEntity.CURRENCY, advEntity.getCurrencyId()));
		adv.setLicensee(elasticSearch.searchById(TablesEntity.LICENSEE, advEntity.getLicenseeId()));
		adv.setCurrencyCode(currencyCode.getCurrencyCode());
		adv.setCreatedBy(advEntity.getCreatedBy());
		adv.setCreationTime(advEntity.getSystemEntryTime());
		adv.setModifiedBy(advEntity.getModifiedBy());
		adv.setModifiedTime(advEntity.getModifiedOn());
		return adv;
	}


	public void updateAdvSettings(AdvertiserSettings settings, AdvertiserEntity advEntity) throws Exception {

		if (isNotBlank(settings.getFeedKey()))
			advEntity.setFeedKey(settings.getFeedKey());

		if (settings.getMmp() != null && settings.getMmp().getId() != null)
			advEntity.setMmpId(settings.getMmp().getId());

		if (isNotBlank(settings.getDateFormat()))
			advEntity.setDateFormat(settings.getDateFormat());

		advEntity.setIsLiftTestActive(settings.isLiftTestActive());
		advEntity.setIsEventFilterAllowed(settings.isEventFilterAllowed());

		if (isNotBlank(settings.getSkuAllowedChars()))
			advEntity.setSkuAllowedCharacters(settings.getSkuAllowedChars());

		if (settings.getTransactionCurrency() != null && elasticSearch.searchById(TablesEntity.CURRENCY,
				settings.getTransactionCurrency().getId()) != null)
			advEntity.setTransactionCurrency(isNotBlank(settings.getTransactionCurrency().getName())
					?  currencyCache.fetchCurrencyByName(settings.getTransactionCurrency().getName()).getCurrencyCode() 
							: null);


		if(settings.getIsPlatformAudienceSupport() != null)
			advEntity.setIsPlatformAudienceSupport(settings.getIsPlatformAudienceSupport());
		else
			advEntity.setIsPlatformAudienceSupport(Boolean.FALSE);

		if(settings.getIsDmpAudienceSupport() != null)
			advEntity.setIsDmpAudienceSupport(settings.getIsDmpAudienceSupport());
		else
			advEntity.setIsDmpAudienceSupport(Boolean.FALSE);

	}

	public AdvertiserSettings populateAdvSettingsFromEntity(AdvertiserEntity advertiserEntity)
			throws Exception {

		AdvertiserSettings settings = new AdvertiserSettings();
		BaseModel currencyBaseModel = null;

		settings.setAdvertiserId(advertiserEntity.getId());
		settings.setFeedKey(advertiserEntity.getFeedKey());
		settings.setDateFormat(advertiserEntity.getDateFormat());
		if (advertiserEntity.getMmpId() != null)
			settings.setMmp(elasticSearch.searchById(TablesEntity.MMP, advertiserEntity.getMmpId()));
		settings.setLiftTestActive(advertiserEntity.getIsLiftTestActive());
		settings.setEventFilterAllowed(advertiserEntity.getIsEventFilterAllowed());

		if (isNotBlank(advertiserEntity.getTransactionCurrency())) {
			CurrencyEntity currenyFromCache =
					currencyCache.fetchCurrencyByCode(advertiserEntity.getTransactionCurrency());
			if (currenyFromCache != null) {
				settings.setTransactionCurrency(
						new BaseModel(currenyFromCache.getId(), currenyFromCache.getCurrencyName()));
				logger.debug("currency Id : {}  && currency Name = {}", currenyFromCache.getId(),
						currenyFromCache.getCurrencyName());
			}
		}

		settings.setSkuAllowedChars(advertiserEntity.getSkuAllowedCharacters());

		settings.setIsDmpAudienceSupport(advertiserEntity.getIsDmpAudienceSupport());
		settings.setIsPlatformAudienceSupport(advertiserEntity.getIsPlatformAudienceSupport());

		return settings;
	}


	public CatalogFeed convertFeedEntityToFeedDTO(FeedInfoEntity feedEntity) {

		CatalogFeed feedDTO = new CatalogFeed();

		long timeForSuccessRate = 51840000l;
		timeForSuccessRate = applicationProperties.getTimePeriodForSuccessRate();
		long minimumTime = (System.currentTimeMillis() / 1000) - timeForSuccessRate;

		feedDTO.setId(feedEntity.getId().longValue());
		feedDTO.setName(feedEntity.getName());
		feedDTO.setAdvertiserId(feedEntity.getAdvertiserId());
		feedDTO.setActive(feedEntity.getIsActive());
		feedDTO.setSource(feedEntity.getFeedLocation());
		feedDTO.setUpdateFrequency(feedEntity.getUpdateFrequencyInSeconds().intValue());

		// hack for lastupdated field (data mismatch in FeedInfo table) - //REVX-1399: AMAR
		// handling the epoch as well as the timestamp in ms
		long lastUpdated = 0;
		long minTimeStampInMillSec = (feedEntity.getLastModifiedInMillis() != null)
				? (feedEntity.getLastModifiedInMillis() / 1000)
						: null;
				if (minTimeStampInMillSec < 100000000) {
					lastUpdated = feedEntity.getLastModifiedInMillis();
				} else if (minTimeStampInMillSec > 0) {
					lastUpdated = feedEntity.getLastModifiedInMillis() / 1000;
				}

				feedDTO.setLastUpdated(lastUpdated);
				feedDTO.setUpdatedStatus(feedEntity.getStatus().name());
				feedDTO.setIsApiBased(feedEntity.getIsApiBased() ? 1 : 0);

				if (feedDTO.getIsApiBased() == Integer.valueOf(0)) {
					List<FeedInfoStatsEntity> feedInfoStats =
							feedInfoStatsRepo.findAllByFeedIdOrderByLastUpdatedDesc(feedEntity.getId());
					if (feedInfoStats.size() > 0) {
						FeedInfoStatsEntity latestFeedStatus = feedInfoStats.get(0);
						feedDTO.setObjectsFound(latestFeedStatus.getObjectsFound());
						feedDTO.setObjectsParsed(latestFeedStatus.getObjectsParsed());
						feedDTO.setObjectsUpdated(latestFeedStatus.getObjectsUpdated());
						feedDTO.setFeedParserRan((long) feedInfoStats.size());
						feedDTO
						.setSuccessRate(util.getSuccessRateForFeedInfoStats(feedEntity.getId(), minimumTime));
					}

				} else {
					List<FeedApiStatusEntity> feedApiStats =
							feedApiStatsRepo.findAllByOrderByFasCreatedTimeDesc(feedEntity.getId());

					if (feedApiStats.size() > 0) {
						FeedApiStatusEntity latestFeedApiStatus = feedApiStats.get(0);
						feedDTO.setObjectsFound(latestFeedApiStatus.getFasProductTotal());
						feedDTO.setObjectsParsed(latestFeedApiStatus.getFasProductParsed());
						feedDTO.setObjectsUpdated(latestFeedApiStatus.getFasProductUpdated());
						feedDTO.setFeedParserRan((long) feedApiStats.size());
						feedDTO.setSuccessRate(util.findSuccessRateForFeedApiStatus(latestFeedApiStatus));
					}
				}

				List<AdvertiserCatalogVariablesMappingEntity> acvmList =
						acvmRepo.findAllByFeedId(feedEntity.getId());

				feedDTO.setVariablesMapped((long) acvmList.size());
				return feedDTO;
	}

	public VariablesMappingDTO convertAcvmToDTO(AdvertiserCatalogVariablesMappingEntity acvmEntity,
			Optional<AtomCatalogVariablesEntity> acvOptional) {

		VariablesMappingDTO dto = new VariablesMappingDTO();

		dto.setId(acvmEntity.getId());
		dto.setName(acvmEntity.getxPath());
		dto.setFeedId(acvmEntity.getFeedId());
		dto.setStandardVariable(acvOptional.isPresent() ? acvOptional.get().getName() : "");
		dto.setDescription(acvOptional.isPresent() ? acvOptional.get().getDescription() : "");
		dto.samples = new ArrayList<Object>();
		dto.setSampleSize(0L);

		return dto;
	}

	public String trimIt(String str) {
		if (str == null)
			return null;

		return StringUtils.trim(str);
	}

	public boolean isNotBlank(String str) {
		return (StringUtils.isNotBlank(str));
	}

	public ConversionPixelEntity convertPixelDTOToEntity(@Valid Pixel pixelDTO, ConversionPixelEntity pixelDO,
			boolean isUpdate) throws ValidationException {

		if (pixelDO == null)
			pixelDO = new ConversionPixelEntity();

		if (pixelDTO.getName() != null)
			pixelDO.setName(trimIt(pixelDTO.getName()));

		pixelDO.setActive(pixelDTO.isActive());

		if (pixelDTO.getAdvertiserPojo() != null && pixelDTO.getAdvertiserPojo().getId() != null) {
			AdvertiserEntity advertiserDO = advertiserRepo.findByIdAndLicenseeId(
					pixelDTO.getAdvertiserPojo().getId(), loginUserDetailsService.getLicenseeId());
			if (advertiserDO == null) {
				throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
						new Object[] {"advertiser in query params. "});
			} else
				pixelDO.setAdvertiserId(advertiserDO.getId());
		}

		if (pixelDTO.getType() != null && pixelDTO.getType().getId() != null) {
			if (PixelType.getPixelTypeFromId(pixelDTO.getType().getId().intValue()) == null)
				throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
						new Object[] {"advertiser in query params. "});
			pixelDO.setType(PixelType.getPixelTypeFromId(pixelDTO.getType().getId().intValue()));
		}

		if (pixelDTO.getClickValidityWindow() != null) {
			pixelDO.setClkValidityWindow(pixelDTO.getClickValidityWindow());
		}
		if (pixelDTO.getViewValidityWindow() != null) {
			pixelDO.setViewValidityWindow(pixelDTO.getViewValidityWindow());
		}
		if (pixelDTO.getUserFcap() != null) {
			pixelDO.setFcap(pixelDTO.getUserFcap());
		}
		if (pixelDTO.getFcapDuration() != null) {
			pixelDO.setFcapDuration(pixelDTO.getFcapDuration());
		}
		if (!isUpdate) {
			pixelDO.setCreatedBy(loginUserDetailsService.getUserInfo().getUserId());
			pixelDO.setCreatedOn(System.currentTimeMillis() / 1000);
			pixelDO.setModifiedBy(loginUserDetailsService.getUserInfo().getUserId());
			pixelDO.setModifiedOn(System.currentTimeMillis() / 1000);
		} else {
			pixelDO.setModifiedBy(loginUserDetailsService.getUserInfo().getUserId());
			pixelDO.setModifiedOn(System.currentTimeMillis() / 1000);
		}
		pixelDO.setLicenseeId(loginUserDetailsService.getLicenseeId());

		return pixelDO;
	}


	public Pixel convertPixelToDTO(ConversionPixelEntity pixelDO) throws Exception {
		if (pixelDO == null)
			return null;

		Pixel pixelDTO = new Pixel();
		pixelDTO.setId(pixelDO.getId());
		pixelDTO.setName(pixelDO.getName());

		Long advertiserId = pixelDO.getAdvertiserId();
		Optional<AdvertiserEntity> advertiserDO = advertiserRepo.findById(advertiserId);
		if (advertiserDO.isPresent())
			pixelDTO.setAdvertiserPojo(populateAdvForElastic(advertiserDO.get()));

		pixelDTO.setActive(pixelDO.isActive());
		pixelDTO.setClickValidityWindow(pixelDO.getClkValidityWindow());
		pixelDTO.setViewValidityWindow(pixelDO.getViewValidityWindow());
		pixelDTO.setUserFcap(pixelDO.getFcap());
		pixelDTO.setFcapDuration(pixelDO.getFcapDuration());
		pixelDTO.setType(new BaseModel(pixelDO.getType().getId(), pixelDO.getType().toString()));
		pixelDTO.setCreationTime(pixelDO.getCreatedOn());
		pixelDTO.setModifiedBy(pixelDO.getModifiedBy());
		pixelDTO.setModifiedTime(pixelDO.getModifiedOn());
		pixelService.populateHourlyData(pixelDTO);
		return pixelDTO;

	}


	/**
	 * Gets the sets the of ids.
	 *
	 * @param idList the id listdt
	 * @return the sets the of ids
	 */
	public Set<Long> getSetOfIds(String idList) throws Exception {
		String[] strList = idList.split(",");
		Set<Long> setOfIds = new HashSet<>();
		for (String str : strList) {
			setOfIds.add(Long.parseLong(str));
		}
		return setOfIds;
	}

	public ClickDestinationEntity convertToClickDestintionEntity(@Valid ClickDestination dto) {
		ClickDestinationEntity entity = new ClickDestinationEntity();

		if (dto == null)
			return null;

		if (dto.getId() != null)
			entity.setId(dto.getId());

		entity.setAdvertiserId(dto.getAdvertiserId());
		entity.setName(dto.getName());
		entity.setLicenseeId(loginUserDetailsService.getUserInfo().getSelectedLicensee().getId());

		entity.setCreatedOn(System.currentTimeMillis() / 1000);
		entity.setCreatedBy(loginUserDetailsService.getUserInfo().getUserId());
		entity.setModifiedOn(System.currentTimeMillis() / 1000);
		entity.setModifiedBy(loginUserDetailsService.getUserInfo().getUserId());
		entity.setClickUrl(
				StringUtils.isBlank(dto.getClickUrl()) ? "" : dto.getClickUrl().trim());
		entity.setAndroidImpressionTracker(
				StringUtils.isBlank(dto.getAndroidImpressionTracker()) ? "" : dto.getAndroidImpressionTracker().trim());
		entity.setIosClickUrl(
				StringUtils.isBlank(dto.getIosCLickUrl()) ? "" : dto.getIosCLickUrl().trim());
		entity.setAndroidClickUrl(
				StringUtils.isBlank(dto.getAndroidClickUrl()) ? "" : dto.getAndroidClickUrl().trim());
		entity.setWebClickUrl(
				StringUtils.isBlank(dto.getWebClickUrl()) ? "" : dto.getWebClickUrl().trim());

		entity.setWebS2sClickTrackingUrl(StringUtils.isBlank(dto.getWebS2sClickTrackingUrl()) ? ""
				: dto.getWebS2sClickTrackingUrl().trim());
		entity.setIosS2sClickTrackingUrl(StringUtils.isBlank(dto.getIosS2sClickTrackingUrl()) ? ""
				: dto.getIosS2sClickTrackingUrl().trim());
		entity
		.setAndroidS2sClickTrackingUrl(StringUtils.isBlank(dto.getAndroidS2sClickTrackingUrl()) ? ""
				: dto.getAndroidS2sClickTrackingUrl().trim());

		entity.setWebImpressionTracker(StringUtils.isBlank(dto.getWebImpressionTracker()) ? ""
				: dto.getWebImpressionTracker().trim());
		entity.setIosImpressionTracker(StringUtils.isBlank(dto.getIosImpressionTracker()) ? ""
				: dto.getIosImpressionTracker().trim());
		entity
		.setAndroidS2sClickTrackingUrl(StringUtils.isBlank(dto.getAndroidS2sClickTrackingUrl()) ? ""
				: dto.getAndroidS2sClickTrackingUrl().trim());

		entity.setServerTrackingUrl(StringUtils.isBlank(dto.getServerTrackingUrl()) ? ""
				: dto.getServerTrackingUrl().trim());
		entity.setIsRefactored(Boolean.TRUE);
		entity.setIsDco(dto.isDco());
		entity.setCampaignType(dto.getCampaignType());
		entity.setStatus(CreativeStatus.active);
		entity.setSkadTarget(dto.getSkadTarget());

		entity.setGeneratedUrlType(dto.getGeneratedUrlType());
		return entity;
	}

	public ClickDestination convertFromClickDestEntity(ClickDestinationEntity entityfromDB) {

		if(entityfromDB==null)
			return null;

		ClickDestination clickdestination = new ClickDestination();
		clickdestination.setId(entityfromDB.getId());
		clickdestination.setAdvertiserId(entityfromDB.getAdvertiserId());
		clickdestination.setLicenseeId(entityfromDB.getLicenseeId());
		clickdestination.setName(entityfromDB.getName());
		clickdestination.setClickUrl(entityfromDB.getClickUrl());
		clickdestination.setIosCLickUrl(entityfromDB.getIosClickUrl());
		clickdestination.setAndroidClickUrl(entityfromDB.getAndroidClickUrl());
		clickdestination.setWebClickUrl(entityfromDB.getWebClickUrl());
		clickdestination.setActive(entityfromDB.getStatus().getValue() ==1 ? Boolean.TRUE:Boolean.FALSE);

		clickdestination.setWebS2sClickTrackingUrl(entityfromDB.getWebS2sClickTrackingUrl());
		clickdestination.setIosS2sClickTrackingUrl(entityfromDB.getIosS2sClickTrackingUrl());
		clickdestination.setAndroidS2sClickTrackingUrl(entityfromDB.getAndroidS2sClickTrackingUrl());

		clickdestination.setWebImpressionTracker(entityfromDB.getWebImpressionTracker());
		clickdestination.setIosImpressionTracker(entityfromDB.getIosImpressionTracker());
		clickdestination.setAndroidImpressionTracker(entityfromDB.getAndroidImpressionTracker());

		clickdestination.setServerTrackingUrl(entityfromDB.getServerTrackingUrl());
		clickdestination.setRefactored(entityfromDB.getIsRefactored());
		clickdestination.setDco(entityfromDB.getIsDco());
		clickdestination.setCampaignType(entityfromDB.getCampaignType());

		clickdestination.setCreationTime(entityfromDB.getCreatedOn());
		clickdestination.setCreatedBy(entityfromDB.getCreatedBy());

		clickdestination.setModifiedBy(entityfromDB.getModifiedBy());
		clickdestination.setModifiedTime(entityfromDB.getModifiedOn());

		clickdestination.setGeneratedUrlType(entityfromDB.getGeneratedUrlType());
		clickdestination.setSkadTarget(entityfromDB.getSkadTarget());

		return clickdestination;
	}


	/**
	 * The click destination is populated with ios and android click tracker urls based on the
	 * MMP data from elastic search MMP index.
	 *
	 * @param measurementPartner
	 * @return
	 */
	public ClickDestinationAutomationUrls populateClickDestinationForParameters(MobileMeasurementPartner mmp) {
		ClickDestinationAutomationUrls cdUrls = new ClickDestinationAutomationUrls();
		cdUrls.setMmpId(mmp.getId());
		cdUrls.setMmpName(mmp.getName());
		cdUrls.setAndroidClickUrl(mmp.getAndroidClickUrl());
		cdUrls.setAndroidS2sUrl(mmp.getAndroidS2sUrl());
		cdUrls.setIosClickUrl(mmp.getIosClickUrl());
		cdUrls.setIosS2sUrl(mmp.getIosS2sUrl());
		// cdUrls.setFallBackUrl(Constants.FALL_BACK_URL);

		//REVX-515 : dynamic s2s fallback
		cdUrls.setFallBackUrlStatic(applicationProperties.getFallBackUrlStatic());
		cdUrls.setFallBackUrlDynamic(applicationProperties.getFallBackUrlDynamic());


		return cdUrls;
	}

    public ClickDestinationESDTO populateClickDestinationESDTO(ClickDestinationEntity entityFromDB) {
		ClickDestinationESDTO esdto = new ClickDestinationESDTO();
		esdto.setId(entityFromDB.getId());
		esdto.setName(entityFromDB.getName());
		esdto.setAdvertiserId(entityFromDB.getAdvertiserId());
		esdto.setLicenseeId(entityFromDB.getLicenseeId());
		esdto.setCampaignType(entityFromDB.getCampaignType().toString());
		esdto.setSkadTarget(entityFromDB.getSkadTarget());
		esdto.setRefactor(entityFromDB.getIsRefactored());
		boolean isActive = entityFromDB.getStatus().getValue() == 1;
		esdto.setActive(isActive);
		esdto.setCreationTime(entityFromDB.getCreatedOn());
		esdto.setCreatedBy(entityFromDB.getCreatedBy());
		esdto.setModifiedBy(entityFromDB.getModifiedBy());
		esdto.setModifiedTime(entityFromDB.getModifiedOn());
		return esdto;
    }
	public AppSettingsDTO populateAppSettingsFromEntity(AppSettingsEntity appSettingsEntity) {
		AppSettingsDTO appSettingsDTO = new AppSettingsDTO();
		appSettingsDTO.setId(appSettingsEntity.getId());
		appSettingsDTO.setAdvertiserId(appSettingsEntity.getAdvertiserId());
		appSettingsDTO.setLicenseeId(appSettingsEntity.getLicenseeId());
		appSettingsDTO.setSettingsKey(appSettingsEntity.getKey());
		appSettingsDTO.setSettingsValue(appSettingsEntity.getValue());
		appSettingsDTO.setSettingsType(appSettingsEntity.getType());
		appSettingsDTO.setActive(appSettingsEntity.isActive());
		List<AppSettingsPropertyDTO> propertyDTO =
				populateAppSettingPropertyFromEntity(appSettingsEntity.getAppSettingsPropertyEntities());
		appSettingsDTO.setAppSettingsProperties(propertyDTO);
		return appSettingsDTO;
	}

	private List<AppSettingsPropertyDTO>
			populateAppSettingPropertyFromEntity(List<AppSettingsPropertyEntity> appSettingsPropertyEntities) {
		List<AppSettingsPropertyDTO> propertyDTOS = new ArrayList<>();
		for (AppSettingsPropertyEntity propertyEntity : appSettingsPropertyEntities) {
			AppSettingsPropertyDTO propertyDTO = new AppSettingsPropertyDTO();
			propertyDTO.setId(propertyEntity.getId());
			propertyDTO.setPropertyKey(propertyEntity.getPropertyKey());
			propertyDTO.setPropertyValue(propertyEntity.getPropertyValue());
			propertyDTOS.add(propertyDTO);
		}
		return propertyDTOS;
	}

	public AppSettingsEntity populateAppSettingEntity(AppSettingsDTO appSettingsDTO) {
		AppSettingsEntity appSettingsEntity = new AppSettingsEntity();
		appSettingsEntity.setAdvertiserId(appSettingsDTO.getAdvertiserId());
		appSettingsEntity.setLicenseeId(loginUserDetailsService.getLicenseeId());
		appSettingsEntity.setKey(appSettingsDTO.getSettingsKey());
		appSettingsEntity.setValue(appSettingsDTO.getSettingsValue());
		appSettingsEntity.setType(appSettingsDTO.getSettingsType());
		appSettingsEntity.setActive(appSettingsDTO.getActive());
		return appSettingsEntity;
	}

	public AppSettingsPropertyEntity
			populatePropertyEntity(AppSettingsPropertyDTO propertyDTO, AppSettingsEntity settingsEntity) {
		AppSettingsPropertyEntity entity = new AppSettingsPropertyEntity();
		entity.setPropertyKey(propertyDTO.getPropertyKey());
		entity.setPropertyValue(propertyDTO.getPropertyValue());
		entity.setAppSettingsEntity(settingsEntity);
		return entity;
	}

	public void updateAppSettingsPropertyEntity
			(AppSettingsPropertyDTO propertyDTO, AppSettingsPropertyEntity appSettingsPropertyEntity) {
		appSettingsPropertyEntity.setPropertyValue(propertyDTO.getPropertyValue());
		appSettingsPropertyEntity.setPropertyKey(propertyDTO.getPropertyKey());
	}

	public LogoModel populateLogoModel(AppSettingsEntity settingsEntity) {
		LogoModel logoModel = new LogoModel();
		logoModel.setId(settingsEntity.getId());
		logoModel.setLogoKey(settingsEntity.getKey().toString());
		logoModel.setLicenseeId(settingsEntity.getLicenseeId());
		logoModel.setAdvertiserId(settingsEntity.getAdvertiserId());
		logoModel.setLogoLink(settingsEntity.getValue());
		return logoModel;
	}

}
