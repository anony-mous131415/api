package io.revx.api.service.audience.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.amtdb.repo.SegmentsRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.campaign.CampaignCacheService;
import io.revx.core.constant.Constants;
import io.revx.core.enums.AudienceType;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.EResponse;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.Filter;

import static io.revx.api.utility.Util.replaceSpecialCharactersWithSpace;

@Component
@SuppressWarnings({ "rawtypes", "unchecked" })
public class AudienceCacheService {

	private static Logger logger = LogManager.getLogger(CampaignCacheService.class);

	@Autowired
	EntityESService entityESService;

	@Autowired
	CacheService cacheService;

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	@Autowired
	ApplicationProperties applicationProperties;

	@Autowired
	ModelConverterService modelConverterService;

	@Autowired
	SegmentsRepository segmentsRepository;

	public Segments fetchAudience(Long id, Boolean refresh) {
		Long licenseeId = loginUserDetailsService.getLicenseeId();

		logger.debug(
				"Inside fetchAudience method. Getting campaign entity for campaign id : {} and licenseeId : {} and advIds : {}.",
				id, licenseeId);
		Set<DashboardFilters> filters = new HashSet<>();
		DashboardFilters filterId = new DashboardFilters();
		filterId.setColumn(Filter.ID.getColumn());
		filterId.setValue(String.valueOf(id));
		filters.add(filterId);
		DashboardFilters filterLicenseeId = new DashboardFilters();
		filterLicenseeId.setColumn(Filter.LICENSEE_ID.getColumn());
		filterLicenseeId.setValue(String.valueOf(licenseeId));
		filters.add(filterLicenseeId);

		Segments segment = null;
		List audiences = cacheService.fetchListCachedEntityData(getCacheKey(), filters, null);

		if (CollectionUtils.isEmpty(audiences) || refresh) {
			logger.debug(
					"Inside fetchAudience method. Getting audience from DB for audience id : {} and licenseeId {}. Returning it from DB.",
					id, licenseeId);
			segment = segmentsRepository.findByIdAndLicenseeId(id, licenseeId);
			if(segment != null) {
				saveToCache(segment);
			}
		} else if (audiences != null && audiences.size() == 1) {
			logger.debug(
					"Inside fetchAudience method. Got {} audience from cache for audience id : {} and licenseeId {}",
					audiences.size(), id, licenseeId);
			segment = (Segments) audiences.get(0);
		}

		return segment;

	}

	public ApiListResponse<List<AudienceESDTO>> fetchAllAudience(Long advertiserId, int pageNumber, int pageSize,
			String sort, SearchRequest search, Boolean refresh) {
		ApiListResponse<List<AudienceESDTO>> response = new ApiListResponse<>();
		Long licenseeId = loginUserDetailsService.getLicenseeId();
		ElasticSearchTerm est = loginUserDetailsService.getElasticSearchTerm();
		if (pageNumber <= 0)
			pageNumber = 1;
		if (pageSize <= 0)
			pageSize = 1000;

		est.setPageNumber(pageNumber);
		est.setPageSize(pageSize);

		if (StringUtils.isNoneBlank(sort)) {
			est.setSortList(Arrays.asList(sort.split(",")));
		}

		logger.debug(
				"Inside fetchAllAudience method. Getting audience entity for advertiserId: {} and licenseeId : {} and pageNumber : {} and pageSize : {} and sort : {} and search : {}",
				advertiserId, licenseeId, pageNumber, pageSize, sort, search);

		est.setLicenseeId(licenseeId);
		if (advertiserId != null && advertiserId != -1)
			est.setAdvertisers(advertiserId);

		if (search != null && CollectionUtils.isNotEmpty(search.getFilters())) {
			for (DashboardFilters searchFilter : search.getFilters()) {
				/*
				 * id and name search we are putting in like search. Rest of the field strict
				 * search.
				 */
				if (StringUtils.isNoneEmpty(searchFilter.getColumn())
						&& (searchFilter.getColumn().equals(Filter.NAME.getColumn())
								|| searchFilter.getColumn().equals(Filter.ID.getColumn()))) {
					String searchTerm = replaceSpecialCharactersWithSpace(searchFilter.getValue());
					est.setSerachInIdOrName(searchTerm);
					continue;
				}

				Map<String, Set<String>> filters = est.getFilters();
				if (filters == null)
					filters = new HashMap<>();
				if (CollectionUtils.isNotEmpty(filters.get(searchFilter.getColumn()))) {
					filters.get(searchFilter.getColumn()).add(searchFilter.getValue());
				} else {
					filters.put(searchFilter.getColumn(), new HashSet<>(Arrays.asList(searchFilter.getValue())));
				}
				est.setFilters(filters);
			}
		}

		logger.info("ElasticSearchTerm {} : ", est);
		TablesEntity tableEntity = TablesEntity.AUDIENCE;
		if (isRequestForDMPAudience(search)) {
			tableEntity = TablesEntity.DMP_AUDIENCE;
		}
		EResponse<AudienceESDTO> eResponse = entityESService.searchAll(tableEntity, est);

		logger.debug("Audience idModelMap.size {}", eResponse != null ? eResponse.getData().size() : 0);
		List audience = new ArrayList<>();
		for (AudienceESDTO audienceESDTO : eResponse.getData()) {
			audience.add(audienceESDTO);
		}

		logger.debug("Audiences : {} ", audience);
		response.setTotalNoOfRecords((int) eResponse.getTotalNoOfRecords());
		response.setData(audience);
		return response;
	}

	/**
	 * Method to check if the request is for data about DMP audience or App and Web Audience
	 * @param request - list of Dashboard filters
	 * @return
	 */
	private boolean isRequestForDMPAudience(SearchRequest request) {

		if (request != null && request.getFilters() != null && !request.getFilters().isEmpty()) {
			for (DashboardFilters filter : request.getFilters()) {
				if (filter.getColumn().equalsIgnoreCase("user_data_type")) {
					if (filter.getValue().equalsIgnoreCase(AudienceType.DMP.getAudienceType())) {
						return true;
					} else {
						return false;
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public void saveToCache() {
		logger.debug("Inside saveToCache method. Saving all audience entity:");

		List audience = segmentsRepository.findByLicenseeId(loginUserDetailsService.getLicenseeId());

		if (audience != null && audience.size() > 0) {
			cacheService.populateCache(getCacheKey(), audience, applicationProperties.getEhcacheTTLInMillis(),
					Segments.class);
			logger.debug("Inside saveToCache method. Saved {} number of audience in cache.", audience.size());
		}

	}

	public void saveToCache(List segments) {
		logger.debug("Inside saveToCache method. Saving segment entity: {}", segments);

		if (CollectionUtils.isNotEmpty(segments)) {
			cacheService.populateCache(getCacheKey(), segments, applicationProperties.getEhcacheTTLInMillis(),
					Segments.class);
			logger.debug("Inside saveToCache method. Saved {} number of segments in cache.", segments.size());
		}

	}

	public void saveToCache(Segments entity) {
		logger.debug("Inside saveToCache method. Saving segment entity: {}", entity);
		List segments = new ArrayList<>();
		segments.add(entity);
		if (CollectionUtils.isNotEmpty(segments)) {
			cacheService.populateCache(getCacheKey(), segments, applicationProperties.getEhcacheTTLInMillis(),
					Segments.class);
			logger.debug("Inside saveToCache method. Saved {} number of segments in cache. saved segments : {}",
					segments.size(), segments);
		}

	}

	public void remove() {
		logger.debug("cache removed for key : {} }", getCacheKey());
		cacheService.removeCache(getCacheKey());
	}

	public String getCacheKey() {
		return ApiConstant.SEGMENT_CACHE_KEY + "_" + loginUserDetailsService.getLicenseeId();
	}

}
