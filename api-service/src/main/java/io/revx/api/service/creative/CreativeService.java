/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.service.creative;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.controller.advertiser.AdvertiserController;
import io.revx.api.es.repo.CustomESRepositoryImpl;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.ValidationService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Creative;
import io.revx.core.model.creative.CreativeCompactDTO;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeHtmlMockupDTO;
import io.revx.core.model.creative.CreativeMockUpsDTO;
import io.revx.core.model.creative.CreativePerformanceData;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.creative.CreativeThirdPartyAdTag;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.SearchRequest;
import io.revx.core.response.ApiListResponse;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.ResponseMessage;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.revx.api.constants.ApiConstant.ADVERTISER_ID;


/**
 * The Class CreativeService.
 */
@Component
public class CreativeService {

	/** The logger. */
	private static Logger logger = LogManager.getLogger(AdvertiserController.class);

	/** The properties. */
	@Autowired
	ApplicationProperties properties;

	/** The util. */
	@Autowired
	CreativeUtil util;

	@Autowired
	CreativeFileDetailsUtil fileUtil;

	@Autowired
	CreativeMockUpUtil mockUpUtil;

	/** The validator. */
	@Autowired
	CreativeValidationService validator;

	@Autowired
	CustomESRepositoryImpl elastic;

	/** The validation service. */
	@Autowired
	ValidationService validationService;

	/** The repo. */
	@Autowired
	CreativeRepository repo;

	/** The cache. */
	@Autowired
	CreativeCacheService cache;

	/** The model converter. */
	@Autowired
	ModelConverterService modelConverter;

	@Autowired
	LoginUserDetailsService loginUserDetailsService;

	/**
	 * Upload.
	 *
	 * @param uploadingFiles the uploading files
	 * @return the api list response
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ValidationException the validation exception
	 */
	public ApiListResponse<CreativeFiles> upload(MultipartFile[] uploadingFiles)
			throws IOException, ValidationException {

		if (uploadingFiles == null || uploadingFiles.length == 0)
			throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR,
					new Object[] {"Please select a file to upload"});

		String msg = null;
		ApiListResponse<CreativeFiles> response = new ApiListResponse<>();
		List<CreativeFiles> uploadedFiles = new ArrayList<>();

		for (MultipartFile f : uploadingFiles) {
			logger.debug("filename : === > {}", f.getOriginalFilename());
			String pathName = properties.getTemporaryCreativeDirectoryPath() + f.getOriginalFilename();
			File file = new File(pathName);
			f.transferTo(file);
			logger.debug("File is saved to {} ", pathName);
			CreativeFiles creativeFile = populateCreativeFile(file);
			msg = validator.validateUploads(creativeFile);

			if (!msg.equals(Constants.Creative_Success))
				creativeFile.setErrorMsg(msg);

			uploadedFiles.add(creativeFile);
		}
		logger.debug("Final list of files are : {} ", uploadedFiles);
		response.setData(uploadedFiles);
		response.setTotalNoOfRecords(uploadedFiles.size());
		return response;
	}



	/**
	 * Populate creative file.
	 *
	 * @param f the f
	 * @return the creative files
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private CreativeFiles populateCreativeFile(File f) throws IOException {
		CreativeFiles fileUploaded = new CreativeFiles();
		fileUploaded.setName(f.getName());
		fileUploaded.setFilePath(getLocation(f));
		fileUploaded.setSize(fileUtil.readableFileSize(f.length()));
		fileUploaded.setContentType(Files.probeContentType(f.toPath()));
		fileUtil.fetchFileDetails(f, fileUploaded);
		logger.debug("File type  :   {}", fileUploaded.getContentType());
		logger.debug("File uploaded : {} ", fileUploaded);
		return fileUploaded;
	}



	/**
	 * Gets the location.
	 *
	 * @param f the f
	 * @return the location
	 */
	private String getLocation(File f) {
		return properties.getCreativeUrlPrependTemp() + properties.getTemporaryCreativeDirectory()
		+ f.getName();
	}



	/**
	 * Mockups.
	 *
	 * @param mockupDTO the mockup DTO
	 * @return the api list response
	 * @throws ValidationException the validation exception
	 */
	public ApiListResponse<CreativeDTO> mockups(CreativeMockUpsDTO mockupDTO)
			throws ValidationException {
		validator.validateMockUpDTO(mockupDTO);
		ApiListResponse<CreativeDTO> response = new ApiListResponse<>();
		List<CreativeDTO> creativeList = mockUpUtil.generateCreatives(mockupDTO);
		response.setData(creativeList);
		response.setTotalNoOfRecords(creativeList.size());
		return response;
	}

	/**
	 * The template based flow varies since we don't have any zipped HTMl file uploaded.
	 * We create the file based out of the HTML content field
	 *
	 * @param mockupDTO the HTML mockup DTO
	 * @return the api list response
	 * @throws ValidationException the validation exception
	 */
	public ApiListResponse<CreativeDTO> htmlMockups(CreativeHtmlMockupDTO mockupDTO)
			throws ValidationException {
		validator.validateHtmlMockUpDTO(mockupDTO);
		ApiListResponse<CreativeDTO> response = new ApiListResponse<>();
		List<CreativeDTO> creativeList = mockUpUtil.generateTemplateCreatives(mockupDTO);
		response.setData(creativeList);
		response.setTotalNoOfRecords(creativeList.size());
		return response;
	}

	/**
	 * Creates the.
	 *
	 * @param creativeDTOs the creative DT os
	 * @return the api list response
	 * @throws Exception 
	 */
	@Transactional
	public ApiListResponse<CreativeDTO> create(List<CreativeDTO> creativeDTOs)
			throws Exception {
		validator.validateCreatives(creativeDTOs);
		ApiListResponse<CreativeDTO> response = new ApiListResponse<>();
		List<CreativeDTO> invalidCreativeDTOs = new ArrayList<CreativeDTO>();
		List<CreativeEntity> creativeEntities = util.populateCreativeEntities(creativeDTOs);
		if (!creativeEntities.isEmpty()) {
			
			//loop through and remove null objects in creativeEntities
			ListIterator<CreativeEntity> iter = creativeEntities.listIterator();
			int loopIndex = 0;
			while (iter.hasNext()) {
				CreativeEntity creativeEntity = iter.next();
				if (creativeEntity == null) {
					iter.remove();
					invalidCreativeDTOs.add(creativeDTOs.get(loopIndex));
				}
				loopIndex += 1;
			}
			
			creativeEntities = repo.saveAll(creativeEntities);
			creativeEntities.forEach(c -> elastic.save(util.populateCreativeForElastic(c), TablesEntity.CREATIVE));
			cache.remove();
			creativeDTOs = util.populateCreativeDTOsFromEntities(creativeEntities);
			for(CreativeDTO invalidCreative : invalidCreativeDTOs){
				invalidCreative.setErrorMsg("Unable to save creative");
				creativeDTOs.add(invalidCreative);
			}
			response.setData(creativeDTOs);
			response.setTotalNoOfRecords(creativeDTOs.size());
			Set<Long> advertiserIdSet = creativeEntities.stream().map(CreativeEntity::getAdvertiserId).collect(Collectors.toSet());
			cache.removeListCache(advertiserIdSet);
			creativeEntities.clear();
		}
		return response;
	}



	/**
	 * Update.
	 *
	 * @param creativeDTO the creative DTO
	 * @return the api response object
	 * @throws ValidationException the validation exception
	 * @throws JsonProcessingException
	 */
	@Transactional
	public ApiResponseObject<CreativeDTO> update(CreativeDTO creativeDTO)
			throws ValidationException, JsonProcessingException, ApiException {
		validator.validateCreative(creativeDTO, true);
		ApiResponseObject<CreativeDTO> response = new ApiResponseObject<>();
		Optional<CreativeEntity> optionalEntity =
				repo.findByIdAndIsRefactored(creativeDTO.getId(), true);

		if (!optionalEntity.isPresent())
			throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
					new Object[] {Constants.INVALID_CR_ID});

		CreativeEntity entity = optionalEntity.get();
		util.updateCreative(entity, creativeDTO);

		entity = repo.save(entity);
		elastic.save(util.populateCreativeForElastic(entity), TablesEntity.CREATIVE);
		cache.remove();
		try {
			creativeDTO = util.populateCreativeDTO(entity,false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ApiException(ErrorCode.CRATIVE_PERFORMANCE_DATA_EXCEPTION, Constants.CRATIVE_PERFORMANCE_DATA_EXCEPTION);
		}
		cache.removeListCache(new HashSet<>(Collections.singleton(creativeDTO.getAdvertiserId())));
		response.setRespObject(creativeDTO);
		return response;
	}



	/**
	 * Activate.
	 *
	 * @param commaSepratedIds the comma seprated ids
	 * @return the api response object
	 * @throws Exception the exception
	 */
	public ApiResponseObject<Map<Long, ResponseMessage>> activate(String commaSepratedIds)
			throws Exception {
		Set<Long> ids = modelConverter.getSetOfIds(commaSepratedIds);
		Set<Long> advertiserIdSet = new HashSet<>();

		List<CreativeEntity> creatives = repo.findByIdIn(ids);
		Map<Long, ResponseMessage> result = new HashedMap<>();
		List<Long> inactiveIds = new ArrayList<>();
		for (CreativeEntity c : creatives) {
			if (c.getStatus().getValue() == 0 && c.getIsRefactored()) {
				inactiveIds.add(c.getId());
				advertiserIdSet.add(c.getAdvertiserId());
			} else if (!c.getIsRefactored()) {
				// do nothing for not is refactored.
			} else
				result.put(c.getId(),
						new ResponseMessage(Constants.ID_ALREADY_ACTIVE, Constants.MSG_ID_ALREADY_ACTIVE));
		}
		for (Long id : ids) {
			if (!inactiveIds.contains(id) && !result.containsKey(id))
				result.put(id, new ResponseMessage(Constants.ID_MISSING, Constants.MSG_ID_MISSING));
		}
		if (!inactiveIds.isEmpty()) {
			for (Long i : inactiveIds) {
				repo.activate(CreativeStatus.active, i);
				activateInElasticSearch(i);
				cache.remove();
				result.put(i, new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS));
			}
			cache.removeListCache(advertiserIdSet);
		}
		ApiResponseObject<Map<Long, ResponseMessage>> response = new ApiResponseObject<>();
		response.setRespObject(result);
		return response;
	}



	/**
	 * Deactivate.
	 *
	 * @param commaSepratedIds the comma seprated ids
	 * @return the api response object
	 * @throws Exception the exception
	 */
	public ApiResponseObject<Map<Long, ResponseMessage>> deactivate(String commaSepratedIds)
			throws Exception {

		Set<Long> ids = modelConverter.getSetOfIds(commaSepratedIds);
		Set<Long> advertiserIdSet = new HashSet<>();
		List<CreativeEntity> creatives = repo.findByIdIn(ids);
		Map<Long, ResponseMessage> result = new HashedMap<>();
		List<Long> activeIds = new ArrayList<>();


		for (CreativeEntity c : creatives) {
			if (c.getStatus().getValue() == 1 && c.getIsRefactored()) {
				activeIds.add(c.getId());
				advertiserIdSet.add(c.getAdvertiserId());
			} else if (!c.getIsRefactored()) {
				// do nothing
			} else
				result.put(c.getId(),
						new ResponseMessage(Constants.ID_ALREADY_INACTIVE, Constants.MSG_ID_ALREADY_INACTIVE));
		}
		for (Long id : ids) {
			if (!activeIds.contains(id) && !result.containsKey(id))
				result.put(id, new ResponseMessage(Constants.ID_MISSING, Constants.MSG_ID_MISSING));
		}
		if (!activeIds.isEmpty()) {
			for (Long i : activeIds) {
				repo.activate(CreativeStatus.inactive, i);
				deActivateInElasticSearch(i);
				cache.remove();
				result.put(i, new ResponseMessage(Constants.SUCCESS, Constants.MSG_SUCCESS));
			}
			cache.removeListCache(advertiserIdSet);
		}

		ApiResponseObject<Map<Long, ResponseMessage>> response = new ApiResponseObject<>();
		logger.debug("deactivation result : {} ", result);

		response.setRespObject(result);
		return response;
	}



	/**
	 * Gets the by id.
	 *
	 * @param id the id
	 * @return the by id
	 * @throws Exception the exception
	 */
	public ApiResponseObject<CreativeDTO> getbyId(Long id) throws Exception {
		ApiResponseObject<CreativeDTO> response = new ApiResponseObject<>();

		if (id == null)
			throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
					new Object[] {Constants.INVALID_CR_ID});

		Optional<CreativeEntity> optionalEntity = repo.findByIdAndIsRefactored(id, true);
		if (!optionalEntity.isPresent())
			throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
					new Object[] {Constants.INVALID_CR_ID});
		validationService.isValidAdvertiserId(optionalEntity.get().getAdvertiserId());
		CreativeDTO creativeDTO = util.populateCreativeDTO(optionalEntity.get(),false);
		response.setRespObject(creativeDTO);
		return response;
	}



	/**
	 * Search creatives.
	 *
	 * @param search the search
	 * @param pageNum the page num
	 * @param resultPerPage the result per page
	 * @param sort the sort
	 * @param refresh the refresh
	 * @param advertiserId the advertiser id
	 * @return the api list response
	 * @throws ValidationException the validation exception
	 */
	public ApiListResponse<CreativeDTO> searchCreatives(DashboardRequest search, Integer pageNum,
			Integer resultPerPage, String sort, boolean refresh) throws ValidationException {

		ApiListResponse<CreativeDTO> response = new ApiListResponse<>();

		if (search == null)
			search = new DashboardRequest();

		if (search.getFilters() == null)
			search.setFilters(Collections.emptyList());

		response = cache.fetchCreativeDTOList(search, sort, pageNum, resultPerPage, refresh,true);
		
//		Integer totalCreatives = 0;
//		try {
//			totalCreatives = getTotalCreativeCount(search);
//		}catch(Exception e) {
//			logger.error ("Getting error while fetching the total count of creatives");
//			throw e;
//		}
//		logger.debug("total creatives :::: "+ totalCreatives);
//
//		if (creativeList != null && !creativeList.isEmpty()) {
//			response.setData(modelConverter.getSubList(creativeList, pageNum, resultPerPage));
//			response.setTotalNoOfRecords(totalCreatives);
//		}
		return response;

	}



	/*
	 * private Integer getTotalCreativeCount(SearchRequest search) {
	 * 
	 * Set<DashboardFilters> filters =
	 * search.getFilters().stream().collect(Collectors.toSet()); if (filters == null
	 * || filters.size() <= 0) { return
	 * repo.findAllCreativeCountByLicenseeId(loginUserDetailsService.getLicenseeId()
	 * ); }else { for (DashboardFilters df : filters) {
	 * logger.debug("DashboardFilters "+ df.getColumn() +" :: "+ df.getValue());
	 * if(df.getColumn().equals("advertiserId")) { return
	 * repo.findAllCreativeCountByAdvertiserId(Integer.parseInt(df.getValue())); } }
	 * } return
	 * repo.findAllCreativeCountByLicenseeId(loginUserDetailsService.getLicenseeId()
	 * ); }
	 */



	/**
	 * Gets the performance by id.
	 *
	 * @param id the id
	 * @return the performance by id
	 * @throws ValidationException the validation exception
	 */
	public ApiResponseObject<CreativePerformanceData> getPerformanceById(Long id)
			throws ValidationException {
		validator.isValidCreativeId(id);
//		ApiResponseObject<CreativePerformanceData> response = new ApiResponseObject<>();
//		CreativePerformanceData perfData = util.populateCreativePerformanceData(id);
//		response.setRespObject(perfData);
//		return response;
		return fetchPerformanceById(id);
	}
	
	public Map<Long, CreativePerformanceData> getPerformanceByCreativeList(List<Long> crIds){
		return util.populateCreativePerformanceDataForCreativeList(crIds);
	}

	public ApiResponseObject<CreativePerformanceData> getPerformanceByIdAndTimeStamp(Long id, DashboardRequest search)
			throws ValidationException {
		
		validator.isValidCreativeId(id);
		return fetchPerformanceByIdAndTimeStamp(id, search);
		
	}
	
	public ApiResponseObject<CreativePerformanceData> fetchPerformanceByIdAndTimeStamp(Long id , DashboardRequest search) {
		
		ApiResponseObject<CreativePerformanceData> response = new ApiResponseObject<>();
		CreativePerformanceData perfData = util.populateCreativePerformanceDataForCreativeIdAndTimeStamp(id, search);
		response.setRespObject(perfData);
		return response;
	}
	
	
	public ApiResponseObject<CreativePerformanceData> fetchPerformanceById(Long id) {
		
		ApiResponseObject<CreativePerformanceData> response = new ApiResponseObject<>();
		CreativePerformanceData perfData = util.populateCreativePerformanceDataForCreativeId(id);
		response.setRespObject(perfData);
		return response;
	}



	/**
	 * Gets the ad tag creative.
	 *
	 * @param adTag the ad tag
	 * @return the ad tag creative
	 * @throws ValidationException the validation exception
	 */
	public ApiResponseObject<CreativeDTO> getAdTagCreative(CreativeThirdPartyAdTag adTag)
			throws ValidationException {
		validator.validateThirdParyAdtagDTO(adTag);
		ApiResponseObject<CreativeDTO> response = new ApiResponseObject<CreativeDTO>();
		CreativeDTO creative = mockUpUtil.populateAdTagCreative(adTag);
		response.setRespObject(creative);
		return response;
	}



	/**
	 * Search compact creatives.
	 *
	 * @param search the search
	 * @param pageNum the page num
	 * @param resultPerPage the result per page
	 * @param sort the sort
	 * @param refresh the refresh
	 * @return the api list response
	 */
	public ApiListResponse<CreativeCompactDTO> searchCompactCreatives(SearchRequest search, Integer pageNum,
			Integer resultPerPage, String sort, boolean refresh, boolean isSkadTarget) throws ValidationException{

		ApiListResponse<CreativeCompactDTO> response = new ApiListResponse<>();

		validateSearchCompactRequest(search);

		@SuppressWarnings("unchecked")
		List<CreativeCompactDTO> creativeList =
		(List<CreativeCompactDTO>) (List<?>) cache.fetchCompactCreatives(search, sort, refresh, isSkadTarget);

		if (creativeList != null && !creativeList.isEmpty()) {
			response.setData(modelConverter.getSubList(creativeList, pageNum, resultPerPage));
			response.setTotalNoOfRecords(creativeList.size());
		}
		return response;

	}

	/**
	 *
	 * @param search SearchRequest
	 * @throws ValidationException is thrown when search request does is not instantiated properly or
	 * 							   when filter does not contain advertiser id
	 */
	private void validateSearchCompactRequest(SearchRequest search) throws ValidationException{
		if (search == null || search.getFilters() == null) {
			throw new ValidationException(ErrorCode.BAD_REQUEST,
					new Object[]{"SearchRequest is not instantiated properly"});
		}
		boolean isValidFilter = false;
		List<DashboardFilters> dashboardFilters = search.getFilters();
		for (DashboardFilters filters : dashboardFilters) {
			if (filters.getColumn().equals(ADVERTISER_ID)){
				isValidFilter = true;
				break;
			}
		}
		if (!isValidFilter){
			throw new ValidationException(ErrorCode.BAD_REQUEST,new Object[]{"AdvertiserId filter is missing"});
		}
	}


	private void activateInElasticSearch(Long i) throws JsonProcessingException {
		Creative cr = (Creative) elastic.findDetailById(TablesEntity.CREATIVE.getElasticIndex(),
				String.valueOf(i), TablesEntity.CREATIVE.getElasticPojoClass());

		if (cr == null)
			return;

		cr.setActive(Boolean.TRUE);
		elastic.save(cr, TablesEntity.CREATIVE);
	}

	private void deActivateInElasticSearch(Long i) throws JsonProcessingException {

		Creative cr = (Creative) elastic.findDetailById(TablesEntity.CREATIVE.getElasticIndex(),
				String.valueOf(i), TablesEntity.CREATIVE.getElasticPojoClass());

		if (cr == null)
			return;

		cr.setActive(Boolean.FALSE);
		elastic.save(cr, TablesEntity.CREATIVE);
	}

}
