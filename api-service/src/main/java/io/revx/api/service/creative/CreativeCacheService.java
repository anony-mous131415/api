/*
 * @date: 12 Dec 2019
 */

package io.revx.api.service.creative;

import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.SmartCachingService;
import io.revx.api.service.ValidationService;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.creative.CreativeCompactDTO;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativePerformanceData;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.objs.FilterComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.revx.api.constants.ApiConstant.ADVERTISER_ID;
import static io.revx.api.utility.Util.deleteCreativeList;
import static io.revx.api.utility.Util.deleteCreativeListOfLicensee;

/**
 * The Class CreativeCacheService.
 */
@Component
public class CreativeCacheService {

	/** The logger. */
	private static Logger logger = LogManager.getLogger(CreativeCacheService.class);

	/** The cache service. */
	@Autowired
	CacheService cacheService;

	/** The login user details service. */
	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	/** The elastic search. */
	@Autowired
	EntityESService elasticSearch;

	/** The model converter. */
	@Autowired
	CreativeUtil modelConverter;

	@Autowired
	ModelConverterService modelConverterService;

	@Autowired
	ValidationService validationService;
	
	/** The service. */
	@Autowired
	CreativeService service;

	/** The creative repository. */
	@Autowired
	CreativeRepository creativeRepository;

	@Lazy
	@Autowired
	SmartCachingService smartCachingService;

	/**
	 * Fetch creatives.
	 *
	 * @param search       the search
	 * @param sort         the sort
	 * @param refresh      the refresh
	 * @param advertiserId the advertiser id
	 * @return the list
	 */
//	@SuppressWarnings("unchecked")
//	public List<CreativeDTO> fetchCreatives(SearchRequest search, String sort, boolean refresh,boolean needPerfData) {
//
//		List<CreativeDTO> listofCreative = new ArrayList<>();
//		String cacheKeyCreative = getCacheKey();
//		List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKeyCreative,
//				search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));
//		List<BaseEntity> entities = null;
//
//		if (listData == null || refresh) {
//			entities = (List<BaseEntity>) (List<?>) creativeRepository
//					.findAllByLicenseeIdAndIsRefactored(loginUserDetailsService.getLicenseeId(),true);
//		}
//
//		if (entities!=null && !entities.isEmpty()) {
//			cacheService.populateCache(cacheKeyCreative, entities, 86400L, CreativeEntity.class);
//			listData = cacheService.fetchListCachedEntityData(cacheKeyCreative,
//					search.getFilters().stream().collect(Collectors.toSet()), getSortList(sort));
//		}
//
//		if (listData != null) {
//
//			listData.forEach(c -> {
//				try {
//					listofCreative.add(modelConverter.populateCreativeDTO((CreativeEntity)c,needPerfData));
//				} catch (Exception e) {
//					logger.debug("Exception occured while adding data to creative list {} ",
//							ExceptionUtils.getStackTrace(e));
//				}
//			});
//		}
//
//		return listofCreative;
//	}

	/**
	 * Fetch creatives.
	 *
	 * @param search       the search
	 * @param sort         the sort
	 * @param refresh      the refresh
	 * @param advertiserId the advertiser id
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public ApiListResponse<CreativeDTO> fetchCreativeDTOList(DashboardRequest search, String sort, Integer pageNum,
			Integer resultPerPage, boolean refresh, boolean needPerfData) {

		ApiListResponse<CreativeDTO> response = new ApiListResponse<>();
		long licenseeId;
		if (loginUserDetailsService.getUserInfo() != null) {
			licenseeId = loginUserDetailsService.getLicenseeId();
		} else {
			licenseeId = smartCachingService.getLicenseeId();
		}
		// String cacheKeyCreative = getCacheKey();
		Map<FilterType, Set<FilterComponent>> tableFilters = validationService.getFiltersMap(search.getFilters());
		String cacheKeyCreative = cacheService.getCreativeCacheKey(search, tableFilters.get(FilterType.TABLE_COLUMN),
				licenseeId);

		List<CreativeDTO> creativeDTOList = new ArrayList<>();
		List<CreativeEntity> creativeList = new ArrayList<CreativeEntity>();

		if (!refresh)
			creativeDTOList = cacheService.fetchCreativeCacheForCacheKey(cacheKeyCreative,
					new HashSet<>(search.getFilters()), getSortList(sort));

		if (creativeDTOList == null || creativeDTOList.isEmpty()) {
			if (queryAllCreatives(sort, search)) {
				getAllCreativesAndCache(licenseeId, cacheKeyCreative, needPerfData, search);
				creativeDTOList = cacheService.fetchCreativeCacheForCacheKey(cacheKeyCreative,
						new HashSet<>(search.getFilters()), getSortList(sort));
			} else {
				logger.debug("[fetchCreativeDTOList] No data in cache.");
				// fetch only 1 page from the db and save it into the cache
				Integer offset = (pageNum - 1) * resultPerPage;
				String crStatus = getStatusFromSearchRequest(search);
				logger.debug("[fetchCreativeDTOList] LicenseeId {} | resultPerPage: {} | offset: {} | crStatus: {}",
						licenseeId, resultPerPage, offset, crStatus);
				Set<DashboardFilters> df = new HashSet<>(search.getFilters());
				Long advId = null;
				if (df != null && df.size() >= 0) {
					for (DashboardFilters filter : df) {
						if(filter.getColumn().equals("advertiserId")) {
							advId=Long.parseLong(filter.getValue());
							break;
						}
					}
				}
				Integer creativeListCount= null;
				if(advId != null) {
					creativeList = creativeRepository.findAdvertiserCreativesByNativeQuery(licenseeId,
							resultPerPage, offset, crStatus, advId);
					creativeListCount = creativeRepository
							.findAdvertiserCreativeCountByAdvertiserId(licenseeId, advId, crStatus);
				}else {
					creativeList = creativeRepository.findAllCreativesByNativeQuery(licenseeId,
							resultPerPage, offset, crStatus);
					creativeListCount = creativeRepository
							.findAllCreativeCountByLicenseeId(licenseeId, crStatus);
				}
				
				List<CreativeDTO> crDTOList = new ArrayList<>();
				if (creativeList != null && !creativeList.isEmpty()) {
					mergePerfDataWithCreative(creativeList, crDTOList, needPerfData, search);
				}
				response = setCreativeResponse(crDTOList, creativeListCount);
				Thread newThread = new Thread(() -> {
					logger.debug("[fetchCreativeDTOList] Separate thread, fetching all creatives for licensee: {}",
							licenseeId);
					getAllCreativesAndCache(licenseeId, cacheKeyCreative, needPerfData, search);
				});
				newThread.start();
				return response;
			}
		}
		if (creativeDTOList != null) {
			Integer creativeListCount = creativeDTOList.size();
			response = setCreativeResponse(modelConverterService.getSubList(creativeDTOList, pageNum, resultPerPage),
					creativeListCount);
		}
		return response;
	}

	public String getStatusFromSearchRequest(DashboardRequest search) {
		String crStatus = "active, inactive";

		List<DashboardFilters> filters = search.getFilters();
		if (filters != null && filters.size() > 0) {
			DashboardFilters activeFilter = null;
			for (DashboardFilters filter : filters) {
				if (filter.getColumn().equalsIgnoreCase("active")) {
					activeFilter = filter;
					break;
				}
			}

			if (activeFilter != null && !activeFilter.getValue().isEmpty()) {
				crStatus = (activeFilter.getValue().equalsIgnoreCase("true")) ? "active" : "inactive";
			}
		}

		return crStatus;
	}

	public ApiListResponse<CreativeDTO> setCreativeResponse(List<CreativeDTO> list, Integer count) {
		ApiListResponse<CreativeDTO> response = new ApiListResponse<>();
		if (list != null && !list.isEmpty()) {
			response.setData(list);
			response.setTotalNoOfRecords(count);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	private void getAllCreativesAndCache(long licenseeId, String cacheKeyCreative, boolean needPerfData ,DashboardRequest search) {
		List<CreativeDTO> crDTOList = new ArrayList<>();
		List<CreativeEntity> crList = (List<CreativeEntity>) (List<?>) creativeRepository
				.findAllByLicenseeIdAndIsRefactored(licenseeId, true);

		if (crList != null && !crList.isEmpty()) {
			mergePerfDataWithCreative(crList, crDTOList, needPerfData, search);
		}

		/*
		 * if (crList != null && !crList.isEmpty()) { crList.forEach(c -> { try {
		 * CreativeDTO crDTO = modelConverter.populateCreativeDTO((CreativeEntity) c,
		 * needPerfData); crDTOList.add(crDTO); } catch (Exception e) {
		 * logger.error("Exception occured while adding data to creative list {} ",
		 * ExceptionUtils.getStackTrace(e)); // throw e; } }); }
		 */
		cacheService.populateCreativeCache(cacheKeyCreative, crDTOList, 86400000L);

	}

	private boolean queryAllCreatives(String sort, DashboardRequest search) {
		if (sort == null || sort.isEmpty() || sort.contains("id-")) {
			Set<DashboardFilters> filters = search.getFilters().stream().collect(Collectors.toSet());
			if (filters == null || filters.size() <= 0) {
				return false;
			} else {
				for (DashboardFilters df : filters) {
					logger.debug("DashboardFilters " + df.getColumn() + " :: " + df.getValue());
					if (df.getColumn().equals("active")) {
						if (df.getValue().equals("true")) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void mergePerfDataWithCreative(List<CreativeEntity> creativeList, List<CreativeDTO> creativeDTOList,
			boolean needPerfData , DashboardRequest search) {

		Map<Long, CreativePerformanceData> performanceDataMap = null;
		if (needPerfData) {
			List<Long> creativeIds = creativeList.stream()
					.map(CreativeEntity::getId)
					.collect(Collectors.toList());
			performanceDataMap =
					modelConverter.populateCreativePerformanceDataList(creativeIds, search);
		}

		for (CreativeEntity c : creativeList) {
			try {
				logger.debug("creative ID: {}", c.getId());
				CreativePerformanceData perfData = null;
				if (needPerfData && performanceDataMap != null) {
					perfData = performanceDataMap.get(c.getId());
					if (perfData != null) {
						logger.debug("Perf Data : {}", perfData);
					}
				}
				CreativeDTO crDTO = modelConverter.populateCreativeToDTO(c, needPerfData, perfData);
				creativeDTOList.add(crDTO);
			} catch (Exception e) {
				logger.error("Exception occurred while adding data to creative list {} ",
						ExceptionUtils.getStackTrace(e));
				// throw e;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<CreativeCompactDTO> fetchCompactCreatives(SearchRequest search, String sort,
			boolean refresh, boolean isSkadTarget) {

		List<CreativeCompactDTO> listOfCreatives = new ArrayList<>();
		String cacheKeyCreative = getCacheKeyCompact();
		EResponse<ClickDestination> clickDestinations;
		if (isSkadTarget) {
			clickDestinations = getSkadClickDestinations(search.getFilters());
			if (clickDestinations.getTotalNoOfRecords() == 0) {
				return listOfCreatives;
			}
			updateSkadTargetFilters(search.getFilters(), clickDestinations);
		}
		List<BaseEntity> listData = cacheService.fetchListCachedEntityData(cacheKeyCreative,
				new HashSet<>(search.getFilters()), getSortList(sort));
		List<BaseEntity> entities = null;

		if (listData == null || refresh) {
			try {
				entities = (List<BaseEntity>) (List<?>) creativeRepository
						.findAllByLicenseeIdAndIsRefactored(loginUserDetailsService.getLicenseeId(), true);
			} catch (Exception e) {
				logger.debug("Exception occured while fetching the all creatives ");
				e.printStackTrace();
				throw e;
			}
		}

		if (entities != null && !entities.isEmpty()) {
			cacheService.populateCache(cacheKeyCreative, entities, 86400000L, CreativeEntity.class);
			listData = cacheService.fetchListCachedEntityData(cacheKeyCreative,
					new HashSet<>(search.getFilters()), getSortList(sort));
		}
		if (listData != null) {

			listData.forEach(c -> {
				try {
					listOfCreatives.add(modelConverter.populateCreativeCompactDTO((CreativeEntity) c));
				} catch (Exception e) {
					logger.debug("Exception occured while adding data to creative compact list {} ",
							ExceptionUtils.getStackTrace(e));
					throw e;
				}
			});
		}

		return listOfCreatives;
	}

	/**
	 * We only need get creative attached to skad click destinations. Currently we are retrieving all the
	 * creatives of a licensee, store it in cache and filter on the advertiserId in search request, if the
	 * request is for skad creatives
	 */
	private EResponse<ClickDestination> getSkadClickDestinations(List<DashboardFilters> filters) {
		List<DashboardFilters> skadFilters = new ArrayList<>(filters);
		DashboardFilters skadFilter = new DashboardFilters();
		skadFilter.setColumn("skadTarget");
		skadFilter.setValue("true");
		skadFilters.add(skadFilter);
		return elasticSearch.searchAll(TablesEntity.CLICK_DESTINATION, skadFilters);
	}

	private void updateSkadTargetFilters(List<DashboardFilters> filters, EResponse<ClickDestination> response) {
		for (BaseModel baseModel : response.getData()) {
			DashboardFilters clickDestinationFilter = new DashboardFilters();
			clickDestinationFilter.setColumn("clickDestination");
			clickDestinationFilter.setValue(String.valueOf(baseModel.getId().longValue()));
			filters.add(clickDestinationFilter);
		}
	}

	/**
	 * Gets the cache key.
	 *
	 * @param advertiserId the advertiser id
	 * @return the cache key
	 */
	public String getCacheKey() {
		return ApiConstant.CREATIVE_CACHE_KEY + "_" + loginUserDetailsService.getLicenseeId();
	}

	
	
	public String getCacheKeyCompact() {
		return ApiConstant.CREATIVE_COMPACT_CACHE_KEY + "_" + loginUserDetailsService.getLicenseeId();
	}

	/**
	 * Gets the sort list.
	 *
	 * @param sort the sort
	 * @return the sort list
	 */
	private List<String> getSortList(String sort) {
		List<String> sortList = new ArrayList<>();
		if (StringUtils.isNotBlank(sort)) {
			for (String sortValue : sort.split(",")) {
				sortList.add(StringUtils.trim(sortValue));
			}
		}
		return sortList;
	}

	/**
	 * Removes the cache for given key.
	 *
	 * @param key the key
	 */
	public void remove() {
		logger.debug("cache removed for key : {} }", getCacheKey());
		cacheService.removeCache(getCacheKey());
		cacheService.removeCache(getCacheKeyCompact());
	}

	public void removeListCache(Set<Long> advertiserIdSet) {
		long licenseeId = loginUserDetailsService.getLicenseeId();
		deleteCreativeListOfLicensee(licenseeId, validationService, cacheService);
		for (Long advertiserId : advertiserIdSet) {
			deleteCreativeList(ADVERTISER_ID, advertiserId, licenseeId, validationService, cacheService);
		}
	}
}
