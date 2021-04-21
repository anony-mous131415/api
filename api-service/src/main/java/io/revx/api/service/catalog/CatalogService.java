/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */

package io.revx.api.service.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.dco.entity.catalog.FeedInfoEntity;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoStatsRepository;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.campaign.CatalogCacheService;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;

/**
 * The Class CatalogService.
 */
@Component
public class CatalogService {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(CatalogService.class);

  /** The feed info repo. */
  @Autowired
  FeedInfoRepository feedInfoRepo;

  /** The model converter. */
  @Autowired
  ModelConverterService modelConverter;

  /** The catalog cache. */
  @Autowired
  CatalogCacheService catalogCache;

  /** The fis repo. */
  @Autowired
  FeedInfoStatsRepository fisRepo;

  /** The catalog util. */
  @Autowired
  CatalogUtil catalogUtil;

  /** The application properties. */
  @Autowired
  ApplicationProperties applicationProperties;

  /** The validator. */
  @Autowired
  ValidationService validator;

  /**
   * Gets the macros.
   *
   * @param advertiserId the advertiser id
   * @param pageNum the page num
   * @param search the search
   * @param resultPerPage the result per page
   * @param sort the sort
   * @param refresh the refresh
   * @return the macros
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public ApiListResponse<Macro> getMacros(Long advertiserId, Integer pageNum, SearchRequest search,
      Integer resultPerPage, String sort, boolean refresh) throws Exception {

    ApiListResponse<Macro> apiListResponse = new ApiListResponse<>();
    List<Macro> macroDTOList = null;
    if (advertiserId == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"AdvertiserId in query params. "});

    macroDTOList =
        (List<Macro>) (List<?>) catalogCache.fetchMacros(advertiserId, search, sort, refresh);
    if (macroDTOList != null && !macroDTOList.isEmpty()) {
      apiListResponse.setTotalNoOfRecords(macroDTOList.size());
      apiListResponse.setData(modelConverter.getSubList(macroDTOList, pageNum, resultPerPage));
    }
    return apiListResponse;
  }


  /**
   * Gets the feeds.
   *
   * @param advertiserId the advertiser id
   * @param pageNum the page num
   * @param request the request
   * @param resultPerPage the result per page
   * @param sort the sort
   * @param refresh the refresh
   * @return the feeds
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public ApiListResponse<CatalogFeed> getFeeds(Long advertiserId, Integer pageNum,
      SearchRequest request, Integer resultPerPage, String sort, boolean refresh) throws Exception {
    List<CatalogFeed> catalogFeedDTOList = null;
    ApiListResponse<CatalogFeed> response = new ApiListResponse<>();
    validator.isValidAdvertiserId(advertiserId);
    catalogFeedDTOList = (List<CatalogFeed>) (List<?>) catalogCache.fetchFeedInfo(advertiserId,
        request, sort, refresh);
    if (catalogFeedDTOList != null && !catalogFeedDTOList.isEmpty()) {
      response.setData(modelConverter.getSubList(catalogFeedDTOList, pageNum, resultPerPage));
      response.setTotalNoOfRecords(catalogFeedDTOList.size());
    }
    return response;
  }



  /**
   * Gets the variable mappings.
   *
   * @param feedId the feed id
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param search the search
   * @param sort the sort
   * @param refresh the refresh
   * @return the variable mappings
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public ApiListResponse<VariablesMappingDTO> getVariableMappings(Long feedId, Integer pageNum,
      Integer resultPerPage, SearchRequest search, String sort, boolean refresh) throws Exception {

    Long advertiserId = null;
    List<VariablesMappingDTO> variableMappingDTOList = null;
    ApiListResponse<VariablesMappingDTO> response = new ApiListResponse<>();
    Optional<FeedInfoEntity> feedInfo = feedInfoRepo.findById(feedId);

    if (!feedInfo.isPresent())
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"feedId in query params. "});

    advertiserId = feedInfo.get().getAdvertiserId();
    validator.isValidAdvertiserId(advertiserId);
    variableMappingDTOList = (List<VariablesMappingDTO>) (List<?>) catalogCache.fetchACVM(feedId,
        advertiserId, search, sort, refresh);

    if (!CollectionUtils.isEmpty(variableMappingDTOList)) {
      response.setTotalNoOfRecords(variableMappingDTOList.size());
      response.setData(modelConverter.getSubList(variableMappingDTOList, pageNum, resultPerPage));
    }
    return response;
  }



  /**
   * Gets the all feeds by advertiser id.
   *
   * @param advertiserId the advertiser id
   * @return the all feeds by advertiser id
   */
  public List<CatalogFeed> getAllFeedsByAdvertiserId(Long advertiserId) {
    List<CatalogFeed> feeds = new ArrayList<>();

    List<FeedInfoEntity> feedInfoList = feedInfoRepo.findAllByAdvertiserId(advertiserId);
    if (feedInfoList != null && !feedInfoList.isEmpty()) {
      for (FeedInfoEntity feed : feedInfoList) {
        CatalogFeed feedDTO = modelConverter.convertFeedEntityToFeedDTO(feed);
        feeds.add(feedDTO);
      }
      feedInfoList.clear();
    }
    return feeds;
  }


  /**
   * Gets the by id.
   *
   * @param id the id
   * @return the by id
   */
  public ApiResponseObject<CatalogFeed> getbyId(Long id) {
    ApiResponseObject<CatalogFeed> response = new ApiResponseObject<>();
    Optional<FeedInfoEntity> feedEntity = feedInfoRepo.findById(id);
    Long timeForSuccessRate = applicationProperties.getTimePeriodForSuccessRate();
    Long minimumTime = (System.currentTimeMillis() / 1000) - timeForSuccessRate;
    CatalogFeed feedDTO = null;
    Long successRate = 0L;

    if (feedEntity.isPresent()) {
      feedDTO = modelConverter.convertFeedEntityToFeedDTO(feedEntity.get());
      if (feedDTO.getIsApiBased() == 0) {
        successRate = catalogUtil.getSuccessRateForFeedInfoStats(id, minimumTime);
        feedDTO.setSuccessRate(successRate);
      } else {
        successRate = catalogUtil.getSuccessRateForFeedApiStatus(id);
        feedDTO.setSuccessRate(successRate);
      }
    }

    response.setRespObject(feedDTO);
    return response;
  }

}

