/*
 * @author: ranjan-pritesh
 * 
 * @date: 27Dec 2019
 */
package io.revx.api.service.clickdestination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.advertiser.AdvertiserService;
import io.revx.core.model.BaseModel;
import io.revx.core.model.ClickDestinationESDTO;
import io.revx.core.model.MobileMeasurementPartner;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.response.Error;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.repo.clickdestination.ClickDestinationRepository;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.api.service.creative.CreativeValidationService;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.ClickDestinationAutomationUrls;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.querybuilder.enums.Filter;

/**
 * The Class ClickDestinationService.
 */
@Component
public class ClickDestinationService {

  /** The validation service. */
  @Autowired
  ValidationService validationService;

  /** The validator. */
  @Autowired
  CreativeValidationService validator;

  /** The model converter. */
  @Autowired
  ModelConverterService modelConverter;

  /** The cd repo. */
  @Autowired
  ClickDestinationRepository cdRepo;

  /** The login details. */
  @Autowired
  LoginUserDetailsService loginDetails;

  /** The cache service. */
  @Autowired
  ClickDestinationCacheService cacheService;

  @Autowired
  AdvertiserService advertiserService;

  @Autowired
  EntityESService esRepository;

  private static final Logger logger = LogManager.getLogger(ClickDestinationService.class);

  /**
   * Creates the.
   *
   * @param clickDestination the click destination
   * @return the api response object
   * @throws ValidationException the validation exception
   */
  public ApiResponseObject<ClickDestination> create(@Valid ClickDestination clickDestination)
          throws ValidationException, JsonProcessingException {
    ApiResponseObject<ClickDestination> response = new ApiResponseObject<>();
    clickDestination.setLicenseeId(loginDetails.getLicenseeId());
    
    validator.validateClickDestination(clickDestination, false);
    return getClickDestinationApiResponseObject(clickDestination, response);
  }



  /**
   * Update.
   *
   * @param clickDestination the click destination
   * @return the api response object
   * @throws ValidationException the validation exception
   */
  public ApiResponseObject<ClickDestination> update(@Valid ClickDestination clickDestination)
          throws ValidationException, JsonProcessingException {
    validator.validateClickDestination(clickDestination, true);
    ApiResponseObject<ClickDestination> response = new ApiResponseObject<>();
    return getClickDestinationApiResponseObject(clickDestination, response);
  }

  private ApiResponseObject<ClickDestination> getClickDestinationApiResponseObject(@Valid ClickDestination clickDestination, ApiResponseObject<ClickDestination> response) throws JsonProcessingException {
      ClickDestinationEntity entity = modelConverter.convertToClickDestintionEntity(clickDestination);
      ClickDestinationEntity entityfromDB = cdRepo.save(entity);
      ClickDestinationESDTO esdto = modelConverter.populateClickDestinationESDTO(entityfromDB);
      esRepository.save(esdto, TablesEntity.CLICK_DESTINATION);
      cacheService.remove();
      ClickDestination cdResponse = modelConverter.convertFromClickDestEntity(entityfromDB);
      response.setRespObject(cdResponse);
      return response;
  }



  /**
   * Gets the by id.
   *
   * @param id the id
   * @return the by id
   * @throws ValidationException the validation exception
   */
  public ApiResponseObject<ClickDestination> getById(Long id) throws ValidationException {
    ApiResponseObject<ClickDestination> response = new ApiResponseObject<>();
    if (id == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"click destination Id is missing"});

    Optional<ClickDestinationEntity> cdEntity = cdRepo.findByIdAndIsRefactored(id, Boolean.TRUE);
    if (!cdEntity.isPresent())
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"click destination Id is invalid"});
    ClickDestination cd = modelConverter.convertFromClickDestEntity(cdEntity.get());
    response.setRespObject(cd);
    return response;
  }



  /**
   * Gets the all.
   *
   * @param search the search
   * @param pageNum the page num
   * @param resultPerPage the result per page
   * @param sort the sort
   * @param refresh the refresh
   * @return the all
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  public ApiListResponse<ClickDestination> getAll(SearchRequest search, Integer pageNum,
      Integer resultPerPage, String sort, boolean refresh,Long advertiserId) throws Exception {

    ApiListResponse<ClickDestination> response = new ApiListResponse<>();
    validationService.isValidAdvertiserId(advertiserId);
    
    if (search == null)
      search = new SearchRequest();

    if (search.getFilters() == null)
      search.setFilters(Collections.emptyList());
    
    List<DashboardFilters> filtersList = new ArrayList<>();
    DashboardFilters filter = new DashboardFilters();
    filter.setColumn(Filter.ADVERTISER_ID.getColumn());
    filter.setValue(advertiserId.toString());
    filtersList.add(filter);
    
    search.getFilters().add(filter);

    List<ClickDestination> clickDestinationList = (List<ClickDestination>) (List<?>) cacheService
        .fetchClickDestination(search, sort, refresh);

    if (clickDestinationList != null && !clickDestinationList.isEmpty()) {
      response.setData(modelConverter.getSubList(clickDestinationList, pageNum, resultPerPage));
      response.setTotalNoOfRecords(clickDestinationList.size());
    }
    return response;
  }

  /**
   * When creating a new click destination in the click tracker url for android and ios , most of the
   * parameters are default based on the MMP. The payload contains advertiser id which is used to
   * determine the MMP from /settings API and query elastic to get default parameters
   *
   * @param adveritserId
   * @return
   */
    public ApiResponseObject<ClickDestinationAutomationUrls> getMmpParameters(Long advertiserId) throws Exception{

    	ApiResponseObject<ClickDestinationAutomationUrls> cdAutoUrlResponse = new ApiResponseObject<>();
    	ClickDestinationAutomationUrls cdAutoUrlsPojo;
    	//validationService.validateClickDestForMmpParams(clickDestination);

    	logger.debug("Fetching MMP details for the advertiser : {}",advertiserId.intValue());

    	ApiResponseObject<AdvertiserSettings> advertiserSettings = advertiserService.getSettingsById(advertiserId);

    	if (advertiserSettings.getRespObject() != null) {
    		BaseModel mmp = advertiserSettings.getRespObject().getMmp();
    		if (mmp != null) {
    			logger.debug("Fetching MMP details for the MMP id : {} , name : {}",mmp.getId(),mmp.getName());
    			Long mmpId = mmp.getId();
    			MobileMeasurementPartner measurementPartner = esRepository.searchPojoById(TablesEntity.MMP,mmpId);
    			if (measurementPartner != null) {
    				logger.debug("Default parameters for MMP Android click url : {}",measurementPartner.getAndroidClickUrl());
    				logger.debug("Default parameters for MMP Android s2s url : {}",measurementPartner.getAndroidS2sUrl());
    				logger.debug("Default parameters for MMP ios click url : {}",measurementPartner.getIosClickUrl());
    				logger.debug("Default parameters for MMP ios s2s url : {}",measurementPartner.getIosS2sUrl());
    				cdAutoUrlsPojo = modelConverter.populateClickDestinationForParameters(measurementPartner);
    				cdAutoUrlResponse.setRespObject(cdAutoUrlsPojo);
    			} else {
    				ErrorCode errorCode = ErrorCode.ENTITY_NOT_FOUND;
    				cdAutoUrlResponse.setError(new Error(errorCode.getValue(),errorCode.getErrorMessage()));
    			}
    		} else {
    			ErrorCode errorCode = ErrorCode.ENTITY_NOT_FOUND;
    			cdAutoUrlResponse.setError(new Error(errorCode.getValue(), errorCode.getErrorMessage()));
    		}
    	} else {
    		ErrorCode errorCode = ErrorCode.ENTITY_NOT_FOUND;
    		cdAutoUrlResponse.setError(new Error(errorCode.getValue(),errorCode.getErrorMessage()));
    	}
    	return cdAutoUrlResponse;
    }


}
