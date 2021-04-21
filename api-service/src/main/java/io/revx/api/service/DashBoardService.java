package io.revx.api.service;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.enums.DashboardEntities;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.mysql.entity.AggregatorLicenseeMappingEntity;
import io.revx.api.mysql.repo.AggregatorLicenseeMappingRepository;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.api.pojo.TablesEntity;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.Constants;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.ChartCSVDashboardData;
import io.revx.core.model.DashboardData;
import io.revx.core.model.DashboardMetrics;
import io.revx.core.model.ListCSVDashboardData;
import io.revx.core.model.ParentBasedObject;
import io.revx.core.model.Strategy;
import io.revx.core.model.requests.ChartDashboardResponse;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.DashboardResponse;
import io.revx.core.model.requests.DictionaryResponse;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.requests.MenuCrubResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.model.targetting.Pricing;
import io.revx.core.model.targetting.PricingType;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.objs.FilterComponent;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.revx.api.utility.Util.replaceSpecialCharactersWithSpace;

@Component
@SuppressWarnings("rawtypes")
public class DashBoardService {

	private static Logger logger = LogManager.getLogger(DashBoardService.class);

	@Autowired
	EntityESService entityESService;

	@Autowired
	DashboardDao dashboardDao;

	@Autowired
	AggregatorLicenseeMappingRepository aggregatorLicenseeMappingRepository;

	@Autowired
	ValidationService validationService;

	@Autowired
	ModelConverterService modelConverterService;

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	CacheService cacheService;

	@Autowired
	CSVReaderWriterService csvReaderWriterService;

	@Autowired
	ApplicationProperties applicationProperties;

	@Lazy
	@Autowired
	SmartCachingService smartCaching;

	private Map<PricingType, List<PricingType>> pricingToROITypeMap = new HashMap<>();

	@PostConstruct
	public void init() {
		pricingToROITypeMap.put(PricingType.CPM, Arrays.asList(PricingType.CPC, PricingType.CPA));
		pricingToROITypeMap.put(PricingType.CPC, Arrays.asList(PricingType.CPA));
		pricingToROITypeMap.put(PricingType.Margin, Arrays.asList(PricingType.CPM, PricingType.CPC, PricingType.CPA));
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.MENU)
	public List<MenuCrubResponse> getMenuCrubResponse() {
		return entityESService.getMenuCrubResponse();

	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.DICTIONARY)
	public DictionaryResponse getDictionaryData(TablesEntity tableEntity, Integer pageNumber, Integer pageSize,
			SearchRequest request, String sort) {
		logger.debug(" pageNumber {} , pageSize {} ", pageNumber, pageSize);
		// if tableEntity is AGGREGATOR go to a new function
		// getDictionaryDataForAggregrator
		// else continue the normal execution
		if (tableEntity == TablesEntity.AGGREGATOR) {
			return getDictionaryDataForAggregrator(tableEntity, pageNumber, pageSize, request, sort);
		} else {
			DictionaryResponse resp = entityESService.getDictionaryData(tableEntity, pageNumber, pageSize, request,
					sort);
			// REVX-300 : handle INTERNAL user role
			return (loginUserDetailsService.isInternalUser()) ? filterDataForInternalUserRole(tableEntity, resp) : resp;
		}
	}

	/**
	 * Method to implement custom logic based on INTERNAL user role
	 * 
	 * @param tableEntity
	 * @param data
	 * @return
	 */
	public DictionaryResponse filterDataForInternalUserRole(TablesEntity tableEntity, DictionaryResponse data) {

		switch (tableEntity) {
		case ADVERTISER_REGION:
			if (data != null && data.getData() != null && !data.getData().isEmpty()) {
				List<BaseModel> records = data.getData();
				records = records.stream()
						.filter(rec -> !rec.getName().equalsIgnoreCase(Constants.ADVERTISER_REGION_INTERNAL))
						.collect(Collectors.toList());
				data.setData(records);
			}
			return data;

		default:
			return data;
		}
	}

	/**
	 * Method to get the list of aggregators after checking the Aggregator-Licensee
	 * Mapping.
	 * 
	 * @param tableEntity
	 * @param pageNumber
	 * @param pageSize
	 * @param request
	 * @param sort
	 * @return
	 */
	private DictionaryResponse getDictionaryDataForAggregrator(TablesEntity tableEntity, Integer pageNumber,
			Integer pageSize, SearchRequest request, String sort) {
		long licenseeId = loginUserDetailsService.getLicenseeId();

		// get aggregator mapping by licensee
		List<AggregatorLicenseeMappingEntity> almEntityList = new ArrayList<AggregatorLicenseeMappingEntity>();
		almEntityList = aggregatorLicenseeMappingRepository.findAllByLicenseeId((int) licenseeId);

		if (request == null)
			request = new SearchRequest();

		if (request.getFilters() == null)
			request.setFilters(new ArrayList<DashboardFilters>());

		if (almEntityList.size() == 0) {
			// no aggregator-licensee mapping. Return all aggregators where rag_type = 1
			request.getFilters()
					.add(new DashboardFilters(Filter.AGGREGATOR_TYPE.getColumn(), Constants.AGGREGATOR_TYPE_1));
		} else {

			// check if any of the records have aggregatorId = -1
			AggregatorLicenseeMappingEntity mappingWithMinus1 = almEntityList.stream()
					.filter(item -> item.getAggregatorId() == -1).findAny().orElse(null);

			if (mappingWithMinus1 == null) {
				// Return all aggregators where rag_type = 1 plus other aggregators which are
				// mapped
				getAggregatorIdsWithOutMinus1Mapping(tableEntity, sort, almEntityList, request);
			} else {
				// Return only those aggregators which are mapped.
				getAggregatorIdsWithMinus1Mapping(request, almEntityList);

			}
		}
		return entityESService.getDictionaryData(tableEntity, pageNumber, pageSize, request, sort);
	}

	private void getAggregatorIdsWithOutMinus1Mapping(TablesEntity tableEntity, String sort,
			List<AggregatorLicenseeMappingEntity> almEntityList, SearchRequest request) {
		List<Long> aggregatorIds = new ArrayList<Long>();
		String cacheDictionaryKey = Constants.AGGREGATOR_DICTIONARY_CACHE_KEY;
		DictionaryResponse response = new DictionaryResponse();
		if (cacheService.fetchAggregatorDictionaryData(cacheDictionaryKey) == null) {
			SearchRequest newRequest = new SearchRequest();
			newRequest.getFilters()
					.add(new DashboardFilters(Filter.AGGREGATOR_TYPE.getColumn(), Constants.AGGREGATOR_TYPE_1));
			response = entityESService.getDictionaryData(tableEntity, 1, 10000, newRequest, sort);
			List<BaseModel> aggregators = response.getData();
			if (aggregators != null && aggregators.size() > 0) {
				aggregatorIds = aggregators.stream().map(item -> item.getId()).collect(Collectors.toList());
				cacheService.populateAggregatorDictionaryData(cacheDictionaryKey, aggregatorIds);
			}

		} else {
			aggregatorIds = cacheService.fetchAggregatorDictionaryData(cacheDictionaryKey);
		}

		aggregatorIds
				.addAll(almEntityList.stream().map(item -> (long) item.getAggregatorId()).collect(Collectors.toList()));
		request.getFilters().add(new DashboardFilters(Filter.AGGREGATOR_ID.getColumn(),
				aggregatorIds.stream().map(String::valueOf).collect(Collectors.joining(","))));

	}

	private void getAggregatorIdsWithMinus1Mapping(SearchRequest request,
			List<AggregatorLicenseeMappingEntity> almEntityList) {
		List<Long> aggregatorIds = new ArrayList<Long>();
		aggregatorIds = almEntityList.stream().filter(item -> item.getAggregatorId() != -1)
				.map(item -> (long) item.getAggregatorId()).collect(Collectors.toList());
		request.getFilters().add(new DashboardFilters(Filter.AGGREGATOR_ID.getColumn(),
				aggregatorIds.stream().map(String::valueOf).collect(Collectors.joining(","))));
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.DICTIONARY)
	public EResponse<?> getDetailDictionaryData(TablesEntity tableEntity, Integer pageNumber, Integer pageSize,
			SearchRequest request, String sort) {
		logger.debug(" pageNumber {} , pageSize {} ", pageNumber, pageSize);
		return entityESService.getDetailDictionaryData(tableEntity, pageNumber, pageSize, request, sort);
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.SEARCH)
	public MenuCrubResponse searchByName(TablesEntity tableEntity, String search, SearchRequest request)
			throws ValidationException {
		if (request != null && request.getFilters() != null) {
			validationService.validateFilters(request.getFilters());
		}
		search = replaceSpecialCharactersWithSpace(search);
		return entityESService.searchByName(tableEntity, search, request != null ? request.getFilters() : null);
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.FIND_ID)
	public BaseModel searchById(TablesEntity tableEntity, long id) {
		return entityESService.searchById(tableEntity, id);
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.FIND_DETAIL_BY_ID)
	public ParentBasedObject searchDetailById(TablesEntity tableEntity, long id) {
		return entityESService.searchDetailById(tableEntity, id);
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.LIST)
	@SuppressWarnings("unchecked")
	public DashboardResponse getDashboardDataList(int pageNumber, int pageSize, String sort,
			DashboardRequest dashboardRequest, DashBoardEntity entity
			, boolean refresh, boolean showUU) throws ValidationException {

		validationService.validateRequest(entity, dashboardRequest);
		DashboardResponse dashboardResponse = new DashboardResponse();
		try {
			String dashboardEntity = DashboardEntities.list.name();
			Map<FilterType, Set<FilterComponent>> tableFilters = validationService
					.getFiltersMap(dashboardRequest.getFilters());
			String cacheKey = cacheService.getCacheKey(dashboardEntity, entity.name(), dashboardRequest,
					tableFilters.get(FilterType.TABLE_COLUMN));
			logger.debug("dashboard call, generated cache key : " + cacheKey);
			Long timeToExpireTheCache = cacheService.getTimeToExpireTheCache(dashboardRequest.getDuration());
			logger.debug("dashboard call, timetoexpire the cache : " + timeToExpireTheCache);
			List<ListPerformanceDataMetrics> totalResult = null;
			List filterResult = null;
			List listData = null;
			Set<DashboardFilters> dataFilter = validationService
					.getDashBoardFilterForData(tableFilters.get(FilterType.DATA_FIELD));
			logger.debug("Got sort  Request {} , pageNumber = {} ,  pageSize = {} ", sort, pageNumber, pageSize);
			if (StringUtils.isBlank(sort)) {
				sort = applicationProperties.getDefaultSort();
			}
			if (!refresh) {
				listData = cacheService.fetchListCachedData(cacheKey, dataFilter, getSortList(sort));
			}

			if (listData == null) {
				try {
					String licenseeCurrCode;
					if (loginUserDetailsService.getUserInfo() != null) {
						licenseeCurrCode = loginUserDetailsService.getLicenseeCurrencyId();
					} else {
						licenseeCurrCode = smartCaching.getLicenseeCurrencyCode();
					}
					// fetching data from postgres
					totalResult = dashboardDao.getDashboardListData(entity, dashboardRequest,
							tableFilters.get(FilterType.TABLE_COLUMN), showUU);
					logger.info("totalResult {} ", totalResult);
					logger.info(" In side Method");
					listData = new ArrayList<>();
					String grpBy = entity.getColumn();
					GroupBy groupByInReq = GroupBy.fromString(grpBy);
					TablesEntity te = TablesEntity.getFromGroupBy(groupByInReq);
					ElasticSearchTerm est;
					/**
					 * When scheduling smartcachig the loginUserDetailsService is not available so
					 * populating elasticSearchTerm from the scheduling service
					 */
					if (loginUserDetailsService.getUserInfo() != null) {
						est = loginUserDetailsService.getElasticSearchTerm();
					} else {
						est = new ElasticSearchTerm();
						est.setLicenseeId(smartCaching.getLicenseeId());
					}
					updateElasticSearchTerm(est, tableFilters.get(FilterType.TABLE_COLUMN));
					logger.info(" ElasticSearchTerm {} : ", est);
					Map<Long, ?> idModelMap = entityESService.search(te, est);
//          String advCurrencyIfApplicable = getAdvCurrencyCode(est);
					/*
					 * REVX-247 in the grids we numbers are displaying same for different
					 * currencies. Setting advertiser currency if any filter is present , elase
					 * setting values based on licensee currency code
					 */
					// should get this job from scheduled job
					String advertiserCurrCode = getAdvCurrencyCode(tableFilters.get(FilterType.TABLE_COLUMN));
					logger.info("idModelMap.size " + idModelMap != null ? idModelMap.size() : 0);
					logger.info(" idModelMap : {} " + idModelMap != null ? idModelMap.entrySet() : idModelMap);
					modelConverterService.populateListData(totalResult, advertiserCurrCode, licenseeCurrCode, listData,
							idModelMap, groupByInReq, tableFilters);
					logger.info(" After Populating listData  {} : ", listData);
					refresh = true;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			if (listData != null) {
				if (refresh)
					cacheService.populateCache(cacheKey, listData, timeToExpireTheCache);
				filterResult = cacheService.fetchListCachedData(cacheKey, dataFilter, getSortList(sort));
				if (refresh && !showUU) {
					cacheService.removeBaseModelCache(cacheKey);
				}

			}
			logger.info("filterResult {} ", filterResult);
			if (filterResult != null && !filterResult.isEmpty()) {
				dashboardResponse.setTotalNoOfRecords(filterResult.size());
				dashboardResponse.setData(modelConverterService.getSubList(filterResult, pageNumber, pageSize));
			}
			dashboardResponse.setShowUU(showUU);
			if (!refresh) {
				dashboardResponse.setShowUU(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dashboardResponse;

	}

//  private String getAdvCurrencyCode(ElasticSearchTerm est) {
//    Map<Long, ?> adveritiserModelMap = entityESService.search(TablesEntity.ADVERTISER, est);
//    Set<String> curSet = new HashSet<>();
//    String currCode = null;
//    for (Entry<Long, ?> ele : adveritiserModelMap.entrySet()) {
//      Advertiser adv = (Advertiser) ele.getValue();
//      curSet.add(adv.getCurrencyCode());
//      currCode = adv.getCurrencyCode();
//
//    }
//    return curSet.size() == 1 ? currCode : null;
//  }

	private String getAdvCurrencyCode(Set<FilterComponent> fcs) {
		TablesEntity tbEntity = null;

		Long advId = null, campId = null, strId = null;
		for (FilterComponent filterComponent : fcs) {
			if (filterComponent != null && filterComponent.getField() != null) {
				switch (filterComponent.getField()) {
				case ADVERTISER_ID:
					advId = Long.parseLong(filterComponent.getValue());
					tbEntity = TablesEntity.ADVERTISER;
					break;
				case CAMPAIGN_ID:
					campId = Long.parseLong(filterComponent.getValue());
					tbEntity = TablesEntity.CAMPAIGN;
					break;
				case STRATEGY_ID:
					strId = Long.parseLong(filterComponent.getValue());
					tbEntity = TablesEntity.STRATEGY;
					break;
				default:
					break;
				}
			}
		}

		if (tbEntity == TablesEntity.CAMPAIGN) {
			CampaignESDTO campaignDO = entityESService.searchPojoById(tbEntity, campId);
			advId = campaignDO.getAdvertiserId();
		}

		if (tbEntity == TablesEntity.STRATEGY) {
			Strategy strategy = entityESService.searchPojoById(tbEntity, strId);
			advId = strategy.getAdvertiserId();
		}

		if (advId == null) {
			return null;
		} else {
			Advertiser advertiser = entityESService.searchPojoById(TablesEntity.ADVERTISER, advId);
			return advertiser.getCurrencyCode();
		}
	}

	private List<String> getSortList(String sort) {
		List<String> sortList = new ArrayList<String>();
		if (StringUtils.isNotBlank(sort)) {
			for (String sortValue : sort.split(",")) {
				sortList.add(StringUtils.trim(sortValue));
			}
		}
		return sortList;
	}

	private void updateElasticSearchTerm(ElasticSearchTerm est, Set<FilterComponent> fcs) {
		ElasticSearchTerm tempESt = new ElasticSearchTerm();
		for (FilterComponent filterComponent : fcs) {
			if (filterComponent != null && filterComponent.getField() != null) {
				switch (filterComponent.getField()) {
				case ADVERTISER_ID:
					tempESt.setAdvertisers(Long.parseLong(filterComponent.getValue()));
					break;
				case CAMPAIGN_ID:
					tempESt.setCampaigns(Long.parseLong(filterComponent.getValue()));
					break;
				case STRATEGY_ID:
					tempESt.setStrategies(Long.parseLong(filterComponent.getValue()));
					break;
				default:
					break;
				}
			}
		}
		if (tempESt.getAdvertisers() != null && tempESt.getAdvertisers().size() > 0) {
			est.setAdvertisers(tempESt.getAdvertisers());
		}
		if (tempESt.getCampaigns() != null && tempESt.getCampaigns().size() > 0) {
			est.setCampaigns(tempESt.getCampaigns());
		}
		if (tempESt.getStrategies() != null && tempESt.getStrategies().size() > 0) {
			est.setStrategies(tempESt.getStrategies());
		}
	}

	// We have to work around the loginuserDetailsService for vaildation ,currency
	// code
	// and filter map formation in this method call also
	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.CHART)
	@SuppressWarnings("unchecked")
	public ChartDashboardResponse getDashboardDataChart(DashboardRequest dashboardRequest,
														boolean refresh, boolean showUU)
			throws ValidationException {
		validationService.validateRequest(dashboardRequest);
		ChartDashboardResponse dashboardResponse = new ChartDashboardResponse();
		try {
			String dashboardEntity = DashboardEntities.chart.name();
			Map<FilterType, Set<FilterComponent>> tableFilters = validationService
					.getFiltersMap(dashboardRequest.getFilters());
			String cacheKeyChart = cacheService.getCacheKey(dashboardEntity, null, dashboardRequest,
					tableFilters.get(FilterType.TABLE_COLUMN));
			logger.debug("dashboard call, generated cache key : {}", cacheKeyChart);
			String cacheKeyWidget = cacheService.getCacheKey(dashboardEntity, "widget", dashboardRequest,
					tableFilters.get(FilterType.TABLE_COLUMN));
			Long timeToExpireTheCache = cacheService.getTimeToExpireTheCache(dashboardRequest.getDuration());
			logger.debug("dashboard call, timetoexpire the cache {} :: {} ", timeToExpireTheCache, refresh);
			List totalResult = null;
			List chartData = null;
			DashboardMetrics widgetData = null;
			if (!refresh) {

				chartData = cacheService.fetchChartCachedData(cacheKeyChart, dashboardRequest.getGroupBy());
				List widgetList = cacheService.fetchChartCachedData(cacheKeyWidget, null);
				if (widgetList != null && !widgetList.isEmpty())
					widgetData = (DashboardMetrics) widgetList.get(0);
			}
			logger.debug("chartData {} , widgetData  {} ", chartData, widgetData);
			if (chartData == null || widgetData == null) {
				String advertiserCurrCode = null;
				String licenseeCurrCode;
				if (loginUserDetailsService.getUserInfo() != null) {
					advertiserCurrCode = loginUserDetailsService.getAdvertiserCurrencyIdIfAdvLogin();
					licenseeCurrCode = loginUserDetailsService.getLicenseeCurrencyId();
				} else {
					licenseeCurrCode = smartCaching.getLicenseeCurrencyCode();
				}
				// fetching data from postgres
				totalResult = dashboardDao.getDashboardChartData(dashboardRequest,
						tableFilters.get(FilterType.TABLE_COLUMN), showUU);
				List<PerformanceDataMetrics> metrics = dashboardDao.getDashboardChartWidgetData(dashboardRequest,
						tableFilters.get(FilterType.TABLE_COLUMN), showUU);
				chartData = new ArrayList<DashboardData>();
				logger.info("totalResult : {}", totalResult);
				logger.info("Widget metrics {} ", metrics);
				ElasticSearchTerm est;
				if (loginUserDetailsService.getUserInfo() != null) {
					est = loginUserDetailsService.getElasticSearchTerm();
				} else {
					est = new ElasticSearchTerm();
					est.setLicenseeId(smartCaching.getLicenseeId());
				}
				updateElasticSearchTerm(est, tableFilters.get(FilterType.TABLE_COLUMN));
				if (advertiserCurrCode == null)
					advertiserCurrCode = getAdvCurrencyCode(tableFilters.get(FilterType.TABLE_COLUMN));
				logger.debug("ADVERTISER CURRENCY: {}", advertiserCurrCode);

				modelConverterService.populateChartData(dashboardRequest, totalResult, advertiserCurrCode,
						licenseeCurrCode, chartData, tableFilters);
				widgetData = new DashboardMetrics();
				modelConverterService.populateChartWidgetData(metrics, advertiserCurrCode, licenseeCurrCode, widgetData,
						tableFilters);
				widgetData.setId(dashboardRequest.getDuration().getEndTimeStamp());
				refresh = true;
			}

			if (!chartData.isEmpty()) {
				dashboardResponse.setTotalNoOfRecords(chartData.size());
				dashboardResponse.setData(chartData);
				dashboardResponse.setWidgetData(widgetData);
				if (refresh && showUU) {
					cacheService.populateCache(cacheKeyChart, chartData, timeToExpireTheCache);
					cacheService.populateCache(cacheKeyWidget, Arrays.asList(widgetData), timeToExpireTheCache);
				}
			}
			dashboardResponse.setShowUU(showUU);
			if (!refresh) {
				dashboardResponse.setShowUU(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dashboardResponse;

	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.CHART + GraphiteConstants.CSV)
	public FileDownloadResponse getCsvResponseForChart(@Valid DashboardRequest dashboardRequest) throws Exception {
		String filename = getCsvFileName(dashboardRequest, null, "chart");
		List<ChartCSVDashboardData> resultForCsv = getCsvDataForChart(dashboardRequest);
		logger.debug("resultForCsv : {} ", resultForCsv);
		csvReaderWriterService.writeToCSV(filename, resultForCsv);
		FileDownloadResponse fresp = new FileDownloadResponse();
		fresp.setFileName(filename);
		fresp.setFileDownloadUrl(applicationProperties.getFileDownloadDomain() + "/" + filename);
		return fresp;
	}

	public List<ChartCSVDashboardData> getCsvDataForChart(@Valid DashboardRequest dashboardRequest) {
		DashboardResponse data = null;
		try {
			data = getDashboardDataChart(dashboardRequest, true,true);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		// mockDataGenerator.getDashboardDataChart(0, 100, dashboardRequest, null);
		logger.debug(" data {} ", data);
		List<ChartCSVDashboardData> resultForCsv = populateData(data);
		return resultForCsv;
	}

	private List<ChartCSVDashboardData> populateData(DashboardResponse data) {
		List<ChartCSVDashboardData> list = new ArrayList<ChartCSVDashboardData>();
		if (data != null && data.getData() != null && data.getData().size() > 0) {
			for (DashboardData dd : data.getData()) {
				if (dd != null) {
					ChartCSVDashboardData cdd = new ChartCSVDashboardData();
					populate(cdd, dd);
					if (dd.getStartTimestamp() != null) {
						cdd.setDate(new Date(dd.getStartTimestamp() * 1000));
					}
					cdd.setId(dd.getId());
					cdd.setName(dd.getName());
					list.add(cdd);
				}
			}

		}
		return list;
	}

	private List<ListCSVDashboardData> populateListData(DashboardResponse data) {
		List<ListCSVDashboardData> list = new ArrayList<ListCSVDashboardData>();
		if (data != null && data.getData() != null && data.getData().size() > 0) {
			for (DashboardData dd : data.getData()) {
				if (dd != null) {
					ListCSVDashboardData cdd = new ListCSVDashboardData();
					populate(cdd, dd);
					cdd.setId(dd.getId());
					cdd.setName(dd.getName());
					list.add(cdd);
				}
			}

		}
		return list;
	}

	private void populate(DashboardMetrics cdd, DashboardData dd) {
		try {
			org.springframework.beans.BeanUtils.copyProperties(dd, cdd, "id", "name");
		} catch (Exception e) {

		}
	}

	@LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.LIST + GraphiteConstants.CSV)
	public FileDownloadResponse getCsvResponseForList(DashBoardEntity entity, @Valid DashboardRequest dashboardRequest)
			throws Exception {
		String filename = getCsvFileName(dashboardRequest, entity, "list");
		List<ListCSVDashboardData> resultForCsv = getCsvDataForList(entity, dashboardRequest);
		logger.debug("resultForCsv : {} ", resultForCsv);
		csvReaderWriterService.writeListToCSV(filename, resultForCsv);
		FileDownloadResponse fresp = new FileDownloadResponse();
		fresp.setFileName(filename);
		fresp.setFileDownloadUrl(applicationProperties.getFileDownloadDomain() + "/" + filename);
		return fresp;
	}

	public List<ListCSVDashboardData> getCsvDataForList(DashBoardEntity entity,
			@Valid DashboardRequest dashboardRequest) {
		DashboardResponse data = null;
		try {
			data = getDashboardDataList(1, Integer.MAX_VALUE, null,
					dashboardRequest, entity, true, true);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		List<ListCSVDashboardData> resultForCsv = populateListData(data);
		return resultForCsv;
	}

	public String getCsvFileName(@Valid DashboardRequest dashboardRequest, DashBoardEntity entity, String type) {
		StringBuffer sb = new StringBuffer();
		sb.append(type).append("_");
		sb.append(StringUtils.replace(loginUserDetailsService.getLicenseeName(), " ", "-")).append("_");
		if (entity != null)
			sb.append(entity).append("_");
		sb.append(getDateFormat(dashboardRequest.getDuration().getStartTimeStamp())).append("_");
		sb.append(getDateFormat(dashboardRequest.getDuration().getEndTimeStamp())).append("_");
		sb.append(RandomStringUtils.randomAlphanumeric(8)).append(".csv");
		return StringUtils.lowerCase(sb.toString());
	}

	private String getDateFormat(long epoc) {
		SimpleDateFormat month_date = new SimpleDateFormat("MMM-dd", Locale.ENGLISH);
		String month_name = "";
		try {
			month_name = month_date.format(new Date(epoc * 1000));
		} catch (Exception e) {
		}
		return month_name;
	}

	public Map<Long, List<BaseModel>> getAdvertiserRoiTypes() {
		try {
			List<BaseModel> doList = entityESService.searchList(TablesEntity.PRICING);
			logger.trace("no of pricing types retireved: " + doList.size());
			setPricingTypeIds(doList);
			List<BaseModel> result = new ArrayList<BaseModel>();
			for (PricingType pt : PricingType.values()) {
				result.add(new BaseModel(pt.id, pt.toString()));
			}
		} catch (Exception e) {

		}
		Map<Long, List<BaseModel>> roiTypeMap = new HashMap<>();
		for (PricingType key : pricingToROITypeMap.keySet()) {
			List<BaseModel> result = new ArrayList<BaseModel>();
			List<PricingType> returnTypes = pricingToROITypeMap.get(key);
			logger.trace("pricing type: " + key + "(" + key.id + "), roi types: " + returnTypes);
			if (returnTypes != null) {
				for (PricingType pt : returnTypes) {
					result.add(new BaseModel(pt.id, pt.toString()));
				}
			}
			roiTypeMap.put(key.id, result);
		}
		return roiTypeMap;

	}

	private void setPricingTypeIds(List<BaseModel> doList) {
		if (doList == null)
			return;
		for (BaseModel p : doList) {
			try {
				PricingType pt = PricingType.valueOf(p.getName());
				if (pt != null)
					pt.id = p.getId();
				else
					logger.error("no such pricing type: " + p.getName());
			} catch (IllegalArgumentException e) {
				logger.trace("not using", e);
			}
		}
	}

	public List<BaseModel> getPricingType(TablesEntity tableEntity) {
		List<BaseModel> result = null;
		try {
			List<BaseModel> doList = entityESService.searchList(TablesEntity.PRICING);
			logger.trace("no of pricing types retrieved: " + doList.size());
			setPricingTypeIds(doList);
			result = new ArrayList<BaseModel>();
			int pricingbit = getPricingBit(tableEntity);
			// return all in case of others
			if (pricingbit < 0)
				return doList;
			for (BaseModel bm : doList) {
				Pricing p = entityESService.searchPojoById(TablesEntity.PRICING, bm.getId());
				if (((p.getFlag() >> pricingbit) & 1) != 0)
					result.add(new BaseModel(p.getId(), p.getName()));
			}
		} catch (Exception e) {
		}
		return result;

	}

	private int getPricingBit(TablesEntity tableEntity) {
		switch (tableEntity) {
		case STRATEGY:
			return ApiConstant.PRICING_STRATEGY_BIT;
		case CAMPAIGN:
			return ApiConstant.PRICING_STRATEGY_BIT;
		default:
			break;
		}
		return -1;
	}

}
