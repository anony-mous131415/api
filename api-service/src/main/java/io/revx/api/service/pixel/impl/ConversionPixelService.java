/*
 * @author: ranjan-pritesh
 * 
 * @date:25th Nov 2019
 */
package io.revx.api.service.pixel.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;

import io.revx.core.response.ResponseMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.QueryType;
import io.revx.api.enums.SlicexEntity;
import io.revx.api.enums.SlicexMetricsEnum;
import io.revx.api.enums.SortOrder;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.api.mysql.repo.pixel.AdvertiserLineItemPixelRepository;
import io.revx.api.mysql.repo.pixel.ConversionPixelRepository;
import io.revx.api.pojo.SlicexFilter;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.EsDataProvider;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.campaign.PixelCacheService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.SlicexData;
import io.revx.core.model.StatusTimeModel;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.pixel.PixelCompact;
import io.revx.core.model.pixel.Tag;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.Duration;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.Filter;

/**
 * The Class PixelService.
 */
@Component
public class ConversionPixelService {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(ConversionPixelService.class);

  /** The model converter. */
  @Autowired
  ModelConverterService modelConverter;

  /** The login user details service. */
  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  /** The validator. */
  @Autowired
  ValidationService validator;

  /** The pixel cache. */
  @Autowired
  PixelCacheService pixelCache;

  /** The elastic search. */
  @Autowired
  EntityESService elasticSearch;

  /** The pixel repo. */
  @Autowired
  ConversionPixelRepository pixelRepo;

  /** The alip repo. */
  @Autowired
  AdvertiserLineItemPixelRepository alipRepo;

  /** The properties. */
  @Autowired
  ApplicationProperties properties;

  /** The cache service. */
  @Autowired
  CacheService cacheService;

  /** The esdata. */
  @Autowired
  private EsDataProvider esdata;

  @Autowired
  CustomESRepositoryImpl elastic;

  /**
   * Creates the.
   *
   * @param pixel the pixel
   * @return the api response object
   * @throws Exception the exception
   */
  public ApiResponseObject<Pixel> create(@Valid Pixel pixel) throws Exception {
    ApiResponseObject<Pixel> response = new ApiResponseObject<>();

    if (pixel.getId() != null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"pixel id should not be present for creation."});

    ConversionPixelEntity pixelEntity = modelConverter.convertPixelDTOToEntity(pixel, null, false);
    pixel = modelConverter.convertPixelToDTO(pixelRepo.save(pixelEntity));
    elastic.save(getCompactPixel(pixel), TablesEntity.PIXEL);
    pixelCache.remove();
    response.setRespObject(pixel);
    return response;
  }



  private PixelCompact getCompactPixel(@Valid Pixel pixel) {
    
    PixelCompact pixelCompact  = new PixelCompact();
    pixelCompact.setId(pixel.getId());
    pixelCompact.setName(pixel.getName());
    pixelCompact.setActive(pixel.isActive());
    pixelCompact.setCreationTime(pixel.getCreationTime());
    pixelCompact.setCreatedBy(pixel.getCreatedBy());
    pixelCompact.setModifiedBy(pixel.getModifiedBy());
    pixelCompact.setModifiedTime(pixel.getModifiedTime());
    pixelCompact.setAdvertiserId(pixel.getAdvertiserId());
    pixelCompact.setLicenseeId(loginUserDetailsService.getLicenseeId());
    return pixelCompact;
  }



  /**
   * Update.
   *
   * @param pixel the pixel
   * @return the api response object
   * @throws Exception the exception
   */
  public ApiResponseObject<Pixel> update(@Valid Pixel pixel) throws Exception {
    ApiResponseObject<Pixel> response = new ApiResponseObject<>();
    Optional<ConversionPixelEntity> pixelOptional = pixelRepo.findById(pixel.getId());
    if (!pixelOptional.isPresent())
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"can not find pixel data for this pixel id. "});
    validator.isValidAdvertiserId(pixelOptional.get().getAdvertiserId());
    ConversionPixelEntity pixelEntity =
        modelConverter.convertPixelDTOToEntity(pixel, pixelOptional.get(), true);
    pixel = modelConverter.convertPixelToDTO(pixelRepo.save(pixelEntity));
    elastic.save(getCompactPixel(pixel), TablesEntity.PIXEL);
    pixelCache.remove();
    response.setRespObject(pixel);
    return response;
  }


  /**
   * Gets the by id.
   *
   * @param pixelId the pixel id
   * @return the by id
   * @throws Exception the exception
   */
  public ApiResponseObject<Pixel> getbyId(Long pixelId) throws Exception {
    ApiResponseObject<Pixel> response = new ApiResponseObject<>();

    if (pixelId == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"pixelId in query params. "});
    Optional<ConversionPixelEntity> pixelOptional = pixelRepo.findById(pixelId);
    if (!pixelOptional.isPresent())
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"pixelId in query params. "});
    validator.isValidAdvertiserId(pixelOptional.get().getAdvertiserId());
    Pixel pixelDTO = modelConverter.convertPixelToDTO(pixelOptional.get());
    response.setRespObject(pixelDTO);
    return response;
  }



  /**
   * Gets the tracking code.
   *
   * @param pixelId the pixel id
   * @return the tracking code
   * @throws ValidationException the validation exception
   */
  public ApiResponseObject<Tag> getTrackingCode(Long pixelId) throws ValidationException {
    ApiResponseObject<Tag> response = new ApiResponseObject<>();

    validator.isValidPixelId(pixelId);
    Tag tag = new Tag();
    tag.setId(pixelId);

    String trackerUrl = properties.getTrackerAppurl() + "/conv";
    Object[] values = {trackerUrl, pixelId.toString()};

    tag.setImgSource(MessageFormat.format(properties.getImageTrackerCodeTemplate(), values));
    logger.trace("Image Tag Code: \n{}", tag.getImgSource());

    tag.setJsSource(MessageFormat.format(properties.getJsTrackerCodeTemplate(), values));
    logger.trace("JS Tag Code: \n{}", tag.getJsSource());

    tag.setAppImgSource(MessageFormat.format(properties.getAppTrackerCodeTemplate(), values));
    logger.trace("Image Tag Code: \n{}", tag.getAppImgSource());

    response.setRespObject(tag);
    return response;
  }



  /**
   * Search pixels.
   *
   * @param search the search
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param sort the sort
   * @param refresh the refresh
   * @param advertiserId the advertiser id
   * @return the api response object
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public ApiListResponse<Pixel> searchPixels(SearchRequest search, Integer pageNum,
      Integer resultPerPage, String sort, boolean refresh, Long advertiserId) throws Exception {
    ApiListResponse<Pixel> response = new ApiListResponse<>();

    if (search == null)
      search = new SearchRequest();

    if (search.getFilters() == null)
      search.setFilters(Collections.emptyList());

    for (DashboardFilters f : search.getFilters().stream().collect(Collectors.toSet())) {
      if (f.getColumn().equals(Filter.STRATEGY_ID.getColumn())) {
        List<AdvertiserLineItemPixelEntity> alip =
            alipRepo.findAllByStrategyId(Long.valueOf(f.getValue()));
        if (alip != null && !alip.isEmpty())
          search.getFilters().add(
              new DashboardFilters(Filter.ID.getColumn(), alip.get(0).getPixelId().toString()));
      }
    }

    for (Iterator<DashboardFilters> it = search.getFilters().iterator(); it.hasNext();) {
      DashboardFilters filter = it.next();
      if (filter.getColumn().equals(Filter.STRATEGY_ID.getColumn())) {
        it.remove();
      }
    }

    List<Pixel> pixelList = (List<Pixel>) (List<?>) pixelCache.fetchPixelAsync(pageNum, resultPerPage, search, sort, refresh,advertiserId);

    if (pixelList != null && !pixelList.isEmpty()) {
      response.setData(modelConverter.getSubList(pixelList, pageNum, resultPerPage));
      response.setTotalNoOfRecords(pixelList.size());
    }
    return response;
  }


  /**
   * Populate hourly data.
   *
   * @param p the p
   * @return the object
   */
  public void populateHourlyData(Pixel p) {
    @SuppressWarnings("unchecked")
    List<SlicexData> list = (List<SlicexData>) getESHourlyDataForPixel(p.getId());

    if(CollectionUtils.isEmpty(list))
      return;
    
    for (SlicexData data : list) {
      if (data != null) {
        p.setImpressions(data.getImpressions());
        p.setClicks(data.getClicks());
        p.setConversions(data.getViewConversions().add(data.getClickConversions()));
      }
    }
  }


  /**
   * Activate.
   *
   * @param idList the id list
   * @return the api response object
   * @throws Exception the exception
   */
  public ApiResponseObject<Map<Long, ResponseMessage>> activate(String idList) throws Exception {
    Set<Long> ids = modelConverter.getSetOfIds(idList);

    List<ConversionPixelEntity> pixels = pixelRepo.findByIdIn(ids);
    Map<Long, ResponseMessage> result = new HashedMap<>();
    List<Long> inactiveIds = new ArrayList<>();
    for (ConversionPixelEntity p : pixels) {
      if (!p.isActive()) {
        inactiveIds.add(p.getId());
      } else
        result.put(p.getId(),
            new ResponseMessage(Constants.ID_ALREADY_ACTIVE, Constants.MSG_ID_ALREADY_ACTIVE));
    }
    for (Long id : ids) {
      if (!inactiveIds.contains(id) && !result.containsKey(id))
        result.put(id, new ResponseMessage(Constants.ID_MISSING, Constants.MSG_ID_MISSING));
    }
    if (!inactiveIds.isEmpty()) {
      for (Long i : inactiveIds) {
        pixelRepo.activate(i);
        activateInElasticSearch(i);
        result.put(i, new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS));
        pixelCache.remove();
      }
    }
    ApiResponseObject<Map<Long, ResponseMessage>> response = new ApiResponseObject<>();
    response.setRespObject(result);
    return response;
  }

  /**
   * Deactivate.
   *
   * @param idList the id list
   * @return the api response object
   * @throws Exception the exception
   */
  public ApiResponseObject<Map<Long, ResponseMessage>> deactivate(String idList) throws Exception {
    Set<Long> ids = modelConverter.getSetOfIds(idList);

    List<ConversionPixelEntity> pixels = pixelRepo.findByIdIn(ids);
    Map<Long, ResponseMessage> result = new HashedMap<>();
    List<Long> activeIds = new ArrayList<>();
    for (ConversionPixelEntity p : pixels) {
      if (p.isActive()) {
        activeIds.add(p.getId());
      } else
        result.put(p.getId(),
            new ResponseMessage(Constants.ID_ALREADY_INACTIVE, Constants.MSG_ID_ALREADY_INACTIVE));
    }
    for (Long id : ids) {
      if (!activeIds.contains(id) && !result.containsKey(id))
        result.put(id, new ResponseMessage(Constants.ID_MISSING, Constants.MSG_ID_MISSING));
    }
    if (!activeIds.isEmpty()) {
      for (Long i : activeIds) {
        pixelRepo.deActivate(i);
        deActivateInElasticSearch(i);
        result.put(i, new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS));
        pixelCache.remove();
      }
    }
    ApiResponseObject<Map<Long, ResponseMessage>> response = new ApiResponseObject<>();
    response.setRespObject(result);
    return response;
  }


  /**
   * Gets the ES hourly data for pixel.
   *
   * @param pixelId the pixel id
   * @return the ES hourly data for pixel
   */
  public List<? extends SlicexData> getESHourlyDataForPixel(Long pixelId) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MONTH, -1);
    Long result = cal.getTimeInMillis() / 1000;

    Duration duration = new Duration();
    duration.setStartTimeStamp(result);
    duration.setEndTimeStamp(System.currentTimeMillis() / 1000);

    Set<SlicexFilter> filters = new HashSet<>();
    Set<Long> idss = new HashSet<>();
    idss.add(pixelId);

    SlicexFilter filter = new SlicexFilter();
    filter.setEntity(SlicexEntity.pixel);
    filter.setIds(idss);
    filters.add(filter);

    List<? extends SlicexData> data = esdata.fetchHourlyDataForPixel(QueryType.slicexList,
        SlicexEntity.pixel, duration, filters, SlicexMetricsEnum.revenue, SortOrder.desc, null);

    logger.debug("data : {}", data);

    return data;
  }

  private void activateInElasticSearch(Long i) throws JsonProcessingException {
    PixelCompact pixel =   (PixelCompact) elastic.findDetailById(TablesEntity.PIXEL.getElasticIndex(),
        String.valueOf(i), TablesEntity.PIXEL.getElasticPojoClass());

    if (pixel == null)
      return;

    pixel.setActive(Boolean.TRUE);
    elastic.save(pixel, TablesEntity.PIXEL);
  }
  
  private void deActivateInElasticSearch(Long i) throws JsonProcessingException {
    PixelCompact pixel =   (PixelCompact)  elastic.findDetailById(TablesEntity.PIXEL.getElasticIndex(),
        String.valueOf(i), TablesEntity.PIXEL.getElasticPojoClass());

    if (pixel == null)
      return;

    pixel.setActive(Boolean.FALSE);
    elastic.save(pixel, TablesEntity.PIXEL);
  }

}
