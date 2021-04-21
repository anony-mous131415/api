/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.service.creative;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.Valid;

import io.revx.core.model.creative.CreativeHtmlMockupDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.repo.clickdestination.ClickDestinationRepository;
import io.revx.api.mysql.repo.creative.CreativeRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.ValidationService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.Creative;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeMockUpsDTO;
import io.revx.core.model.creative.CreativeThirdPartyAdTag;
import io.revx.core.model.creative.CreativeType;
import io.revx.core.model.creative.FileType;

/**
 * The Class CreativeValidationService.
 */
@Component
public class CreativeValidationService {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(CreativeValidationService.class);

  // ** The validator. */
  @Autowired
  ValidationService validator;

  @Autowired
  ClickDestinationRepository cdRepo;

  @Autowired
  EntityESService elasticSearch;

  /** The properties. */
  @Autowired
  ApplicationProperties properties;

  @Autowired
  CreativeRepository repo;


  /**
   * Validate click destination.
   *
   * @param clickDestination the click destination
   * @param isUpdate the is update
   * @throws ValidationException the validation exception
   */
  public void validateClickDestination(@Valid ClickDestination clickDestination, boolean isUpdate)
      throws ValidationException {

    if (clickDestination == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"click destination is missing"});

    validator.isValidAdvertiserId(clickDestination.getAdvertiserId());
    nullCheckFor(clickDestination.getLicenseeId(), "license ID");

    if (isUpdate)
      nullCheckFor(clickDestination.getId(), "clickDestination Id");

    if (!isUpdate) {
      if (clickDestination.getId() != null)
        throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
            new Object[] {"id is not needed while creation."});

      isBlank(clickDestination.getName(), "clickDestination DstName");
      nullCheckFor(clickDestination.getCampaignType(), "campaign  type");
      nullCheckFor(clickDestination.getGeneratedUrlType() , "generated-url type");

    }
  }

  /**
   * Null check for.
   *
   * @param obj the obj
   * @param logtowrite the logtowrite
   * @throws ValidationException the validation exception
   */
  public void nullCheckFor(Object obj, String logtowrite) throws ValidationException {
    if (obj == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {logtowrite + " is missing"});
  }



  /**
   * Validate uploads.
   *
   * @param creativeFile the creative file
   * @return the string
   */
  public String validateUploads(CreativeFiles creativeFile) {

    if (creativeFile.getContentType().contains("image")
        || creativeFile.getContentType().contains("video/gif"))
      return validateImage(creativeFile);
    else if (creativeFile.getContentType().contains("video"))
      return validateVideo(creativeFile);
    else if (creativeFile.getContentType().contains("zip"))
      return validateZip(creativeFile);
    else
      return Constants.INVALID_FILE_TYPE;
  }



  /**
   * Validate html.
   *
   * @param creativeFile the creative file
   * @return the string
   */
  private String validateZip(CreativeFiles creativeFile) {

    if (!creativeFile.getContentType().equals(FileType.ZIP.getType()))
      return Constants.INVALID_FILE_TYPE;

    creativeFile.setType(CreativeType.zippedHTML);
    return Constants.Creative_Success;
  }



  private void isZipContainImageFile() {

  }

  /**
   * Validate image.
   *
   * @param creativeFile the creative file
   * @return the string
   */
  private String validateImage(CreativeFiles creativeFile) {
    logger.debug("validating image file.........");
    if (!Stream
        .of(FileType.JPEG.getType(), FileType.JPG.getType(), FileType.PNG.getType(),
            FileType.GIF.getType())
        .collect(Collectors.toList()).contains(creativeFile.getContentType()))
      return Constants.INVALID_CONTENT_TYPE;

    creativeFile.setType(CreativeType.image);

    if (isDcoNativeimage(creativeFile)) {
      creativeFile.setNative(true);
      creativeFile.setType(CreativeType.nativeAd);
      creativeFile.setDco(Boolean.TRUE);
    }
    return Constants.Creative_Success;
  }



  private boolean isDcoNativeimage(CreativeFiles creativeFile) {


    logger.debug("checking Dco supported size.........");
    Integer h = creativeFile.getHeight();
    Integer w = creativeFile.getWidth();
    boolean result = false;

    String[] supportedSize = properties.getSupportedDcoNativeImageSize().split(",");
    for (String s : supportedSize) {
      String[] size = s.split("x");
      Integer width = Integer.valueOf(size[0]);
      Integer height = Integer.valueOf(size[1]);

      if (h.compareTo(height) == 0 && w.compareTo(width) == 0)
        result = true;
    }

    return result;
  }

  /**
   * Validate video.
   *
   * @param creativeFile the creative file
   * @return the string
   */
  private String validateVideo(CreativeFiles creativeFile) {
    logger.debug("validating video file.........");

    if (!Stream
        .of(FileType.MP4.getType(), FileType.THREE_GPP.getType(), FileType.MOV.getType(),
            FileType.WEBM.getType(), FileType.DASH.getType(), FileType.HLS.getType())
        .collect(Collectors.toList()).contains(creativeFile.getContentType()))
      return Constants.INVALID_CONTENT_TYPE;

    /*
     * Removing this check as we need to support all size video just like display/banner
    if (!videoHasRequiredDimension(creativeFile) && !isNative(creativeFile))
      return Constants.INVALID_DIMENSION;
    */

    /* if (videoHasRequiredDimension(creativeFile)) */
    creativeFile.setType(CreativeType.video);

    if (isNative(creativeFile)) {
      creativeFile.setNative(true);
      creativeFile.setType(CreativeType.nativeVideo);
    }

    return Constants.Creative_Success;
  }



  /**
   * Video has required dimension.
   *
   * @param creativeFile the creative file
   * @return true, if successful
   */
  public boolean videoHasRequiredDimension(CreativeFiles creativeFile) {
    logger.debug("checking video has supported size.........");
    Integer h = creativeFile.getHeight();
    Integer w = creativeFile.getWidth();
    boolean result = false;

    String[] supportedSize = properties.getSupportedCreativeSize().split(",");
    for (String s : supportedSize) {
      String[] size = s.split("x");
      Integer height = Integer.valueOf(size[0]);
      Integer width = Integer.valueOf(size[1]);

      if (h.compareTo(height) == 0 && w.compareTo(width) == 0)
        result = true;
    }

    return result;
  }



  /**
   * Checks if is native.
   *
   * @param creativeFile the creative file
   * @return true, if is native
   */
  public boolean isNative(CreativeFiles creativeFile) {
    logger.debug("checking is native file Aspects ratio.........");
    Integer h = creativeFile.getHeight();
    Integer w = creativeFile.getWidth();
    boolean result = false;

    String[] supportedNativeAspectsRatio = properties.getSupportedNativeAspectRatio().split(",");

    for (String s : supportedNativeAspectsRatio) {
      String[] size = s.split(":");
      Integer width = Integer.valueOf(size[0]);
      Integer height = Integer.valueOf(size[1]);

      if (width * h == height * w)
        result = true;
    }

    return result;
  }



  /**
   * Checks if is blank.
   *
   * @param str the str
   * @param strToLog the str to log
   * @throws ValidationException the validation exception
   */
  public void isBlank(String str, String strToLog) throws ValidationException {
    if (StringUtils.isBlank(str))
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {strToLog + " is missing"});
  }



  /**
   * Validate mock up DTO.
   *
   * @param mockupDTO the mockup DTO
   * @throws ValidationException the validation exception
   */
  public void validateMockUpDTO(CreativeMockUpsDTO mockupDTO) throws ValidationException {

    if (mockupDTO == null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"invalid request mockups dto"});
    }

    if (mockupDTO.getBasicDetails() == null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"invalid request mockups dto"});
    }

    if (mockupDTO.getUploadedFiles() == null || mockupDTO.getUploadedFiles().isEmpty()) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {" please provide some files details"});
    }

    validator.isValidAdvertiserId(mockupDTO.getBasicDetails().getAdvertiserId());
    nullCheckFor(mockupDTO.getBasicDetails().getName(), "please provide a creative name. ");
    nullCheckFor(mockupDTO.getBasicDetails().getClickDestination(), "Creative click destination");
    validateClickDestination(mockupDTO.getBasicDetails().getClickDestination(), true);

  }

  /**
   * Validate HTML mock up DTO.
   *
   * @param mockupDTO the mockup DTO
   * @throws ValidationException the validation exception
   */
  public void validateHtmlMockUpDTO(CreativeHtmlMockupDTO mockupDTO) throws ValidationException {

    if (mockupDTO == null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
              new Object[] {"invalid request mockups dto"});
    }

    if (mockupDTO.getBasicDetails() == null) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
              new Object[] {"invalid request mockups dto"});
    }

    if (mockupDTO.getCreativeHtmlFiles() == null || mockupDTO.getCreativeHtmlFiles().isEmpty()) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
              new Object[] {" please provide some files details"});
    }

    validator.isValidAdvertiserId(mockupDTO.getBasicDetails().getAdvertiserId());
    nullCheckFor(mockupDTO.getBasicDetails().getName(), "please provide a creative name. ");
    nullCheckFor(mockupDTO.getBasicDetails().getClickDestination(), "Creative click destination");
    validateClickDestination(mockupDTO.getBasicDetails().getClickDestination(), true);

  }

  public void validateCreatives(List<CreativeDTO> creativeDTOs) throws ValidationException {

    for (CreativeDTO c : creativeDTOs) {
      validateCreative(c, false);
    }
  }

  public void validateCreative(CreativeDTO c, boolean isUpdate) throws ValidationException {

    isBlank(c.getName(), "creative Name ");
    nullCheckFor(c.getAdvertiser(), "creative Advertiser");
    nullCheckFor(c.getClickDestination(), "creative clickdestination");
    validator.isValidAdvertiserId(c.getAdvertiser().getId());
    isvalidClickDestinationId(c.getClickDestination().getId());
    nullCheckFor(c.getType(), "creative type can not be null.");
  }



  public void isvalidClickDestinationId(Long id) throws ValidationException {

    Optional<ClickDestinationEntity> cd = cdRepo.findById(id);

    if (!cd.isPresent())
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"invalid click destination id "});
    else {
      validator.isValidAdvertiserId(cd.get().getAdvertiserId());
    }
  }

  public void validateNativeText(String text, boolean isTitle, boolean isBody,boolean isDco)
      throws ValidationException {

    if (isTitle && (text.length() == 0 || text.length() > 25)) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"nativeAsset title"});
    }

    if (isBody && (text.length() == 0 || text.length() > 90)) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"nativeAsset body"});
    }

    // Punctuation
    if(isDco)
      return;
    
    Character c = text.charAt(0);
    if (Character.getType(c) == Character.CONNECTOR_PUNCTUATION
        || Character.getType(c) == Character.DASH_PUNCTUATION
        || Character.getType(c) == Character.START_PUNCTUATION
        || Character.getType(c) == Character.END_PUNCTUATION
        || Character.getType(c) == Character.END_PUNCTUATION
        || Character.getType(c) == Character.INITIAL_QUOTE_PUNCTUATION
        || Character.getType(c) == Character.FINAL_QUOTE_PUNCTUATION
        || Character.getType(c) == Character.OTHER_PUNCTUATION) {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"nativeAsset text"});
    }

  }

  public void isValidCreativeId(Long creativeId) throws ValidationException {

    if (creativeId == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"invalid creative id"});

    Creative cr = (Creative) elasticSearch.searchPojoById(TablesEntity.CREATIVE, creativeId);

    if (cr == null)
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
          new Object[] {"invalid creative id"});

    Long advId = cr.getAdvertiserId();
    validator.isValidAdvertiserId(advId);
  }

  public void validateThirdParyAdtagDTO(CreativeThirdPartyAdTag adTag) throws ValidationException {

    nullCheckFor(adTag, "Third party adtag");
    isBlank(adTag.getAdTag(), "adTag String");
    nullCheckFor(adTag.getBasicDetails(), "adtag Basic Details");
    validator.isValidAdvertiserId(adTag.getBasicDetails().getAdvertiserId());
    isBlank(adTag.getBasicDetails().getName(), "Creative Name");
    nullCheckFor(adTag.getBasicDetails().getClickDestination(), "adtag click destination");
    isvalidClickDestinationId(adTag.getBasicDetails().getClickDestination().getId());
  }



}
