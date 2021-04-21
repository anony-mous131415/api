/*
 * @author: ranjan-pritesh
 *
 * @date:2 jan 2020
 */
package io.revx.api.service.creative;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeHtmlFile;
import io.revx.core.model.creative.CreativeHtmlMockupDTO;
import io.revx.core.model.creative.CreativeMockUpsDTO;
import io.revx.core.model.creative.CreativeThirdPartyAdTag;
import io.revx.core.model.creative.CreativeType;
import io.revx.core.model.creative.CreativeVideoFormat;
import io.revx.core.model.creative.DcoAttributesDTO;
import io.revx.core.model.creative.FileType;
import io.revx.core.model.creative.Size;
import io.revx.core.model.creative.VastProtocol;
import io.revx.core.model.creative.VideoAttributes;
import io.revx.core.model.creative.VideoCampanionType;
import io.revx.core.model.creative.VideoUploadType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static io.revx.core.constant.Constants.DYNAMIC_NOENCODING;

/**
 * The Class CreativeMockUpUtil.
 */
@Component
public class CreativeMockUpUtil {

  /** The logger. */
  private static final Logger logger = LogManager.getLogger(CreativeMockUpUtil.class);

  /** The user. */
  @Autowired
  LoginUserDetailsService user;

  /** The properties. */
  @Autowired
  ApplicationProperties properties;

  @Autowired
  CreativeValidationService crValidate;

  @Autowired
  CreativeUtil util;

  /** The elastic search. */
  @Autowired
  EntityESService elasticSearch;


  /**
   * Generate creatives.
   *
   * @param mockupDTO the mockup DTO
   * @return the list
   */
  public List<CreativeDTO> generateCreatives(CreativeMockUpsDTO mockupDTO) {
    List<CreativeDTO> creativeList = new ArrayList<>();

    if (Boolean.TRUE.equals(mockupDTO.getBasicDetails().getIsDCO()))
      populateDcoCreatives(mockupDTO, creativeList);
    else
      populateNonDcoCreatives(mockupDTO, creativeList);

    return creativeList;
  }

  public List<CreativeDTO> generateTemplateCreatives(CreativeHtmlMockupDTO mockupDTO) {
    List<CreativeDTO> creativeList = new ArrayList<>();
    Long advertiserId = mockupDTO.getBasicDetails().getAdvertiserId();
    String baseName = removeSpaceAndAppendUniqueId(mockupDTO.getBasicDetails().getName()) + Constants.HYPHEN
            + advertiserId;

    for (CreativeHtmlFile file : mockupDTO.getCreativeHtmlFiles()) {
      if (file.getType() == null || file.getType() != CreativeType.zippedHTML) {
        continue;
      }
      if (file.isDco()) {
        populateDcoTemplateHtmlCreative(file, creativeList, advertiserId, baseName);
      } else {
        populateTemplateHtmlCreative(file, creativeList, advertiserId, baseName);
      }
    }

    creativeList.forEach(c -> c.setClickDestination(mockupDTO.getBasicDetails().getClickDestination()));


    return creativeList;
  }

  private void populateTemplateHtmlCreative(CreativeHtmlFile file, List<CreativeDTO> creativeList,
          Long advertiserId, String baseName) {
    CreativeDTO htmlCreative = new CreativeDTO();

    try {
      htmlCreative.setName(baseName + Constants.HYPHEN + Constants.STATIC + Constants.HYPHEN + Constants.HTML);
      htmlCreative.setCreatedBy(user.getUserInfo().getUserId());
      htmlCreative.setCreationTime(System.currentTimeMillis() / 1000);
      htmlCreative.setActive(Boolean.TRUE);
      htmlCreative.setType(CreativeType.zippedHTML);
      htmlCreative.setTemplateBased(true);
      htmlCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      htmlCreative.setContentType(FileType.getFileType(file.getContentType()));
      htmlCreative.setContent(file.getHtmlContent());
      htmlCreative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserId));
      htmlCreative.setPreviewUrl(getPreviewUrlForTemplate(file, advertiserId, htmlCreative, false));
      populateTemplateCreativeErrorMessage(file, htmlCreative);
    } catch (Exception e) {
      logger.debug("Error occurred while creating static Html creative from a template  :  {}   >> {}", file,
              ExceptionUtils.getStackTrace(e));
      htmlCreative.setErrorMsg(e.getMessage());
    }
    creativeList.add(htmlCreative);
  }

  private void populateDcoTemplateHtmlCreative(CreativeHtmlFile file, List<CreativeDTO> creativeList,
          Long advertiserId, String baseName) {

    CreativeDTO dcoHtmlCreative = new CreativeDTO();

    try {
      dcoHtmlCreative.setName(baseName + Constants.HYPHEN + Constants.DYNAMIC + Constants.HYPHEN + Constants.HTML);
      dcoHtmlCreative.setCreatedBy(user.getUserInfo().getUserId());
      dcoHtmlCreative.setCreationTime(System.currentTimeMillis() / 1000);
      dcoHtmlCreative.setActive(Boolean.FALSE);
      dcoHtmlCreative.setType(CreativeType.zippedHTML);
      dcoHtmlCreative.setTemplateBased(true);
      dcoHtmlCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      dcoHtmlCreative.setDcoAd(Boolean.TRUE);
      String htmlContent = file.getHtmlContent();
      if (file.getDynamicItemList() != null) {
        htmlContent = htmlContent.replace(file.getDynamicItemList(), DYNAMIC_NOENCODING);
        dcoHtmlCreative.setDynamicItemList(file.getDynamicItemList());
      }
      dcoHtmlCreative.setContent(htmlContent);
      dcoHtmlCreative.setContentType(FileType.getFileType(file.getContentType()));
      dcoHtmlCreative
              .setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserId));
      dcoHtmlCreative.setPreviewUrl(getPreviewUrlForTemplate(file, advertiserId, dcoHtmlCreative, false));

      DcoAttributesDTO  dcoAttributes = new DcoAttributesDTO();
      dcoAttributes.setNoOfSlots(file.getNoOfSlots());
      dcoAttributes.setMacroList(file.getMacroList());

      dcoHtmlCreative.setDcoAttributes(dcoAttributes);
      dcoHtmlCreative.setOriginalFileName(file.getName());

      populateTemplateCreativeErrorMessage(file, dcoHtmlCreative);
    } catch (Exception e) {
      logger.debug("Error occurred while creating static Html creative from a template  :  {}   >> {}",
              file, ExceptionUtils.getStackTrace(e));
      dcoHtmlCreative.setErrorMsg(e.getMessage());
    }
    creativeList.add(dcoHtmlCreative);

  }


  /**
   * Populate non dco creatives.
   *
   * @param mockupDTO the mockup DTO
   * @param creativeList the creative list
   */
  private void populateNonDcoCreatives(CreativeMockUpsDTO mockupDTO,
      List<CreativeDTO> creativeList) {

    Long advertiserid = mockupDTO.getBasicDetails().getAdvertiserId();
    String baseName = removeSpaceAndAppendUniqueId(mockupDTO.getBasicDetails().getName()) + Constants.HYPHEN
        + advertiserid;

    List<CreativeFiles> videoFileList = new ArrayList<>();
    List<CreativeFiles> nativeVideoFileList = new ArrayList<>();

    for (CreativeFiles file : mockupDTO.getUploadedFiles()) {

      if (file.getType() == null)
        continue;

      if (file.getType().equals(CreativeType.image))
        populateImageCreative(file, creativeList, baseName, advertiserid);

      if (file.getType().equals(CreativeType.video) || file.getType().equals(CreativeType.nativeVideo))
        videoFileList.add(file);

      if (file.isNative() && file.getType().equals(CreativeType.nativeAd)) {
        populateNativeImageCreative(file, creativeList, baseName, advertiserid);
        populateImageCreative(file, creativeList, baseName, advertiserid);
      }

      if (file.isNative() && file.getType().equals(CreativeType.nativeVideo))
        nativeVideoFileList.add(file);

      if (file.getType().equals(CreativeType.zippedHTML))
        populateHtmlCreative(file, creativeList, baseName, advertiserid);
    }

    if (!videoFileList.isEmpty())
      populateVideoCreatives(videoFileList, creativeList, baseName, advertiserid);

    if (!nativeVideoFileList.isEmpty())
      populateNativeVideoCreatives(nativeVideoFileList, creativeList, baseName, advertiserid);

    creativeList
        .forEach(c -> c.setClickDestination(mockupDTO.getBasicDetails().getClickDestination()));

  }



  /**
   * Populate dco creatives.
   *
   * @param mockupDTO the mockup DTO
   * @param creativeList the creative list
   */
  private void populateDcoCreatives(CreativeMockUpsDTO mockupDTO, List<CreativeDTO> creativeList) {

    Long advertiserid = mockupDTO.getBasicDetails().getAdvertiserId();
    String baseName = removeSpaceAndAppendUniqueId(mockupDTO.getBasicDetails().getName()) + Constants.HYPHEN
        + advertiserid;

    for (CreativeFiles file : mockupDTO.getUploadedFiles()) {

      if (file.getType() == null)
        continue;

      if ((file.getType().equals(CreativeType.image)
          || file.getType().equals(CreativeType.nativeAd)) && file.isDco())
        populateDcoImageCreative(file, creativeList, baseName, advertiserid);
      else if (file.getType().equals(CreativeType.zippedHTML))
        populateDcoHtmlCreative(file, creativeList, baseName, advertiserid);
      else
        creativeList.add(new CreativeDTO(Constants.INVALID_FILE_TYPE));

    }
    creativeList
        .forEach(c -> c.setClickDestination(mockupDTO.getBasicDetails().getClickDestination()));

  }



  private String removeSpaceAndAppendUniqueId(String name) {
    return name.trim().replace(' ', '-');
  }



  /**
   * Populate dco html creative.
   *
   * @param file the file
   * @param creativeList the creative list
   * @param baseName the base name
   * @param advertiserid the advertiserid
   */
  private void populateDcoHtmlCreative(CreativeFiles file, List<CreativeDTO> creativeList,
      String baseName, Long advertiserid) {

    CreativeDTO dcoHtmlCreative = new CreativeDTO();

    try {
      dcoHtmlCreative.setName(baseName + Constants.HYPHEN + Constants.DYNAMIC + Constants.HYPHEN + Constants.HTML);
      dcoHtmlCreative.setCreatedBy(user.getUserInfo().getUserId());
      dcoHtmlCreative.setCreationTime(System.currentTimeMillis() / 1000);
      dcoHtmlCreative.setActive(Boolean.FALSE);
      dcoHtmlCreative.setType(CreativeType.zippedHTML);
      dcoHtmlCreative.setTemplateBased(false);
      dcoHtmlCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      dcoHtmlCreative.setDcoAd(Boolean.TRUE);
      dcoHtmlCreative.setContentType(FileType.getFileType(file.getContentType()));
      dcoHtmlCreative
      .setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserid));
      dcoHtmlCreative.setContent(getHtmlContent(file));
      dcoHtmlCreative.setPreviewUrl(getPreviewUrl(file, advertiserid, dcoHtmlCreative));
      
      DcoAttributesDTO  dcoAttributes = new DcoAttributesDTO();
      dcoAttributes.setNoOfSlots(file.getNoOfSlots());
      dcoAttributes.setMacroList(file.getMacroList());
      
      dcoHtmlCreative.setDcoAttributes(dcoAttributes);
      dcoHtmlCreative.setOriginalFileName(file.getName());
      populateErrorMessageIfAny(file, dcoHtmlCreative);
    } catch (Exception e) {
      logger.debug("Error occurred while creating template Creative for given File  :  {}", file);
      dcoHtmlCreative.setErrorMsg(e.getMessage());
    }
    creativeList.add(dcoHtmlCreative);

  }



  /**
   * Populate error message if any.
   *
   * @param file the file
   * @param creative the creative
   */
  private void populateErrorMessageIfAny(CreativeFiles file, CreativeDTO creative) {

    if (StringUtils.isNotBlank(file.getErrorMsg()))
      creative.setErrorMsg(file.getErrorMsg());

  }

  /**
   * Populate error message if any.
   *
   * @param file the file
   * @param creative the creative
   */
  private void populateTemplateCreativeErrorMessage(CreativeHtmlFile file, CreativeDTO creative) {

    if (StringUtils.isNotBlank(file.getErrorMsg()))
      creative.setErrorMsg(file.getErrorMsg());

  }



  /**
   * Populate dco image creative.
   *
   * @param file the file
   * @param creativeList the creative list
   * @param baseName the base name
   * @param advertiserid the advertiserid
   */
  private void populateDcoImageCreative(CreativeFiles file, List<CreativeDTO> creativeList,
      String baseName, Long advertiserid) {

    CreativeDTO dcoNativeImageCreative = new CreativeDTO();

    try {
      dcoNativeImageCreative.setName(baseName + Constants.HYPHEN + Constants.NATIVE + Constants.HYPHEN + Constants.DYNAMIC + Constants.HYPHEN + Constants.IMAGE );
      dcoNativeImageCreative.setCreatedBy(user.getUserInfo().getUserId());
      dcoNativeImageCreative.setCreationTime(System.currentTimeMillis() / 1000);
      dcoNativeImageCreative.setActive(Boolean.TRUE);
      dcoNativeImageCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      dcoNativeImageCreative.setContentType(FileType.getFileType(file.getContentType()));
      dcoNativeImageCreative.setType(CreativeType.nativeAd);
      dcoNativeImageCreative.setTemplateBased(false);
      dcoNativeImageCreative.setNativeAd(Boolean.TRUE);
      dcoNativeImageCreative
          .setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserid));
      dcoNativeImageCreative.setDcoAd(Boolean.TRUE);
      dcoNativeImageCreative
      .setPreviewUrl(getPreviewUrl(file, advertiserid, dcoNativeImageCreative));
      populateErrorMessageIfAny(file, dcoNativeImageCreative);
    } catch (Exception e) {
      logger.debug("Error occurred while creating dco native Image Creative for given File  :  {}",
          file);
      dcoNativeImageCreative.setErrorMsg(e.getMessage());
    }

    creativeList.add(dcoNativeImageCreative);


  }



  /**
   * Populate html creative.
   *
   * @param file the file
   * @param creativeList the creative list
   * @param baseName the base name
   * @param advertiserid the advertiserid
   */
  private void populateHtmlCreative(CreativeFiles file, List<CreativeDTO> creativeList,
      String baseName, Long advertiserid) {

    CreativeDTO htmlCreative = new CreativeDTO();

    try {
      htmlCreative.setName(baseName + Constants.HYPHEN + Constants.STATIC + Constants.HYPHEN + Constants.HTML);
      htmlCreative.setCreatedBy(user.getUserInfo().getUserId());
      htmlCreative.setCreationTime(System.currentTimeMillis() / 1000);
      htmlCreative.setType(CreativeType.zippedHTML);
      htmlCreative.setTemplateBased(false);
      htmlCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      htmlCreative.setContentType(FileType.getFileType(file.getContentType()));
      htmlCreative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserid));
      htmlCreative.setContent(getHtmlContent(file));
      htmlCreative.setPreviewUrl(getPreviewUrl(file, advertiserid, htmlCreative));
      htmlCreative.setOriginalFileName(file.getName());
      populateErrorMessageIfAny(file, htmlCreative);
    } catch (Exception e) {
      logger.debug("Error occurred while creating html Creative for given File  :  {}   >> {}", file,
          ExceptionUtils.getStackTrace(e));
      htmlCreative.setErrorMsg(e.getMessage());
    }
    creativeList.add(htmlCreative);
  }



  /**
   * Gets the html content.
   *
   * @param file the file
   * @return the html content
   */
  private String getHtmlContent(CreativeFiles file) throws IOException {

    if (StringUtils.isBlank(file.getFilePath()))
      return null;

    String locationPrefix =
        properties.getCreativeUrlPrependTemp() + properties.getTemporaryCreativeDirectory();
    String directoryPrefix = properties.getTemporaryCreativeDirectoryPath();
    String path = file.getFilePath().replace(locationPrefix, directoryPrefix);

    StringBuilder sb = new StringBuilder();
    String str;
    String content = null;

    try(ZipFile zipFile = new ZipFile(path)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        InputStream stream = zipFile.getInputStream(entry);

        if (entry.getName().contains(".html")) {
          InputStreamReader isReader = new InputStreamReader(stream);
          BufferedReader reader = new BufferedReader(isReader);
          while ((str = reader.readLine()) != null) {
            sb.append(str).append("\n");
          }
          content = StringUtils.trim(sb.toString());
        }

      }
    } catch (IOException e) {
      logger.debug("Exception occurred while reading Zip File content.....{}", e.getMessage());
      throw new IOException("Failed to parse uploaded zip file");
    }

    return content;
  }



  /**
   * Populate video creative.
   *
   * @param videofiles the videofiles
   * @param creativeList the creative list
   * @param baseName the base name
   * @param advertiserid the advertiserid
   */
  private void populateNativeVideoCreatives(List<CreativeFiles> videofiles,
      List<CreativeDTO> creativeList, String baseName, Long advertiserid) {

    List<CreativeDTO> imageListForCompanion = creativeList.stream()
        .filter(i -> i.getType().equals(CreativeType.image)).collect(Collectors.toList());

    List<CreativeDTO> nativeVideoCreativelist = videofiles.stream()
        .map(f -> populateNativeVideoCreative(f, baseName, advertiserid, imageListForCompanion))
        .collect(Collectors.toList());

    creativeList.addAll(nativeVideoCreativelist);

  }



  /**
   * Populate native video creative.
   *
   * @param file the file
   * @param baseName the base name
   * @param advertiserId the advertiser id
   * @param imageListForCompanion the image list for companion
   * @return the creative DTO
   */
  private CreativeDTO populateNativeVideoCreative(CreativeFiles file, String baseName,
      Long advertiserId, List<CreativeDTO> imageListForCompanion) {

    CreativeDTO nativeVideoCreative = new CreativeDTO();

    try {

      if (!crValidate.isNative(file)) {
        nativeVideoCreative.setErrorMsg(Constants.INVALID_DIMENSION);
        return nativeVideoCreative;
      }

      nativeVideoCreative.setName(baseName +  Constants.HYPHEN + Constants.NATIVE  + Constants.HYPHEN + Constants.STATIC + Constants.HYPHEN + Constants.VIDEO );
      nativeVideoCreative.setCreatedBy(user.getUserInfo().getUserId());
      nativeVideoCreative.setCreationTime(System.currentTimeMillis() / 1000);
      nativeVideoCreative.setActive(Boolean.TRUE);
      nativeVideoCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      nativeVideoCreative.setType(CreativeType.nativeVideo);
      nativeVideoCreative.setTemplateBased(false);
      nativeVideoCreative.setContentType(FileType.getFileType(file.getContentType()));
      nativeVideoCreative
          .setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserId));
      nativeVideoCreative.setPreviewUrl(getPreviewUrl(file, advertiserId, nativeVideoCreative));

      populateVideoAttribute(file, nativeVideoCreative, imageListForCompanion);
      nativeVideoCreative.setNativeAd(Boolean.TRUE);
      nativeVideoCreative.setVideoUploadType(VideoUploadType.VIDEO);
      populateErrorMessageIfAny(file, nativeVideoCreative);
    } catch (Exception e) {
      logger.debug("Error occurred while creating Video Creative for given File  :  {}", file);
      nativeVideoCreative.setErrorMsg(e.getMessage());
    }
    return nativeVideoCreative;

  }



  /**
   * Populate ad tag creative.
   *
   * @param adTag the ad tag
   * @return the creative DTO
   */
  public CreativeDTO populateAdTagCreative(CreativeThirdPartyAdTag adTag) {

    CreativeDTO adTagCreative = new CreativeDTO();

    try {
      adTagCreative.setName(util.generateAdTagCreativeName(adTag.getBasicDetails().getName(),
          adTag.getBasicDetails().getAdvertiserId()));
      adTagCreative.setCreatedBy(user.getUserInfo().getUserId());
      adTagCreative.setCreationTime(System.currentTimeMillis() / 1000);
      adTagCreative.setType(CreativeType.html);
      adTagCreative.setSize(new Size());
      adTagCreative.setContent(adTag.getAdTag().trim());
      adTagCreative.setPreviewUrl(getPreviewUrlForAdTag(adTag, adTagCreative.getName()));
      adTagCreative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER,
          adTag.getBasicDetails().getAdvertiserId()));
      adTagCreative.setClickDestination(adTag.getBasicDetails().getClickDestination());
      adTagCreative.setRefactored(Boolean.TRUE);
    } catch (Exception e) {
      logger.debug("Error occurred while creating adTag Creative for given adTag  :  {}   >> {}",
          adTag, ExceptionUtils.getStackTrace(e));
      adTagCreative.setErrorMsg(e.getMessage());
    }
    return adTagCreative;
  }



  /**
   * Populate native image creative.
   *
   * @param file the file
   * @param creativeList the creative list
   * @param baseName the base name
   * @param advertiserid the advertiserid
   */
  private void populateNativeImageCreative(CreativeFiles file, List<CreativeDTO> creativeList,
      String baseName, Long advertiserid) {

    CreativeDTO nativeImageCreative = new CreativeDTO();

    try {
      nativeImageCreative.setName(baseName + Constants.HYPHEN + Constants.NATIVE + Constants.HYPHEN + Constants.STATIC + Constants.HYPHEN + Constants.IMAGE);
      nativeImageCreative.setCreatedBy(user.getUserInfo().getUserId());
      nativeImageCreative.setCreationTime(System.currentTimeMillis() / 1000);
      nativeImageCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      nativeImageCreative.setType(CreativeType.nativeAd);
      nativeImageCreative.setTemplateBased(false);
      nativeImageCreative.setContentType(FileType.getFileType(file.getContentType()));
      nativeImageCreative.setActive(Boolean.TRUE);
      nativeImageCreative.setPreviewUrl(getPreviewUrl(file, advertiserid, nativeImageCreative));
      nativeImageCreative
          .setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserid));
      nativeImageCreative.setNativeAd(Boolean.TRUE);
      populateErrorMessageIfAny(file, nativeImageCreative);
    } catch (Exception e) {
      logger.debug("Error occurred while creating Image Creative for given File  :  {}", file);
      nativeImageCreative.setErrorMsg(e.getMessage());
    }

    creativeList.add(nativeImageCreative);

  }



  /**
   * Populate video creative.
   *
   * @param videoFiles the videoFiles
   * @param creativeList the creative list
   * @param baseName the base name
   * @param advertiserId the advertiserId
   */
  private void populateVideoCreatives(List<CreativeFiles> videoFiles,
      List<CreativeDTO> creativeList, String baseName, Long advertiserId) {

    List<CreativeDTO> imageListForCompanion = creativeList.stream()
        .filter(i -> i.getType().equals(CreativeType.image)).collect(Collectors.toList());

    List<CreativeDTO> videoCreativeList = videoFiles.stream()
        .map(f -> populateVideoCreative(f, baseName, advertiserId, imageListForCompanion))
        .collect(Collectors.toList());

    creativeList.addAll(videoCreativeList);
    // Earlier we needed to merge video creative of opposite resolution, so as to generate single
    // creative for which the VAST XML will contain both the links to render different videos in portrait
    // and landscape. We dont need to perform that any longer
    /*List<List<CreativeDTO>> mergeCreativeList = getMergingVideoCreativeList(tempVideoCreativeList);

    List<CreativeDTO> videoCreatives = mergeCreativeList.stream()
        .map(this::getFinalVideoCreativesFromMergingList).collect(Collectors.toList());
    creativeList.addAll(videoCreatives);*/

  }

  /**
   * Gets the final video creatives from merging list.
   *
   * @param l the l
   * @return the final video creatives from merging list
   */
  private CreativeDTO getFinalVideoCreativesFromMergingList(List<CreativeDTO> l) {
    List<VideoAttributes> finalVideoAttributes = new ArrayList<>();
    l.forEach(c -> finalVideoAttributes.addAll(c.getVideoAttributes()));
    l.get(0).setVideoAttributes(finalVideoAttributes);
    return l.get(0);
  }



  /**
   * Gets the merging video creative list.
   *
   * @param tempCreativelist the temp creativelist
   * @return the merging video creative list
   */
  private List<List<CreativeDTO>> getMergingVideoCreativeList(List<CreativeDTO> tempCreativelist) {

    List<List<CreativeDTO>> mergeVideoCreativeList = new ArrayList<>();

    while (!tempCreativelist.isEmpty()) {
      CreativeDTO temp = tempCreativelist.get(0);
      List<CreativeDTO> list = new ArrayList<>();
      list.add(temp);
      tempCreativelist.remove(0);

      Iterator<CreativeDTO> itr = tempCreativelist.iterator();
      while (itr.hasNext()) {
        CreativeDTO creativeDTO = itr.next();
        if (hasOppositeResolutionAndSameDuration(temp, creativeDTO)) {
          list.add(creativeDTO);
          itr.remove();
        }
      }
      mergeVideoCreativeList.add(list);
    }

    return mergeVideoCreativeList;
  }



  /**
   * Checks for opposite resolution and same duration.
   *
   * @param first the first
   * @param second the second
   * @return true, if successful
   */
  private boolean hasOppositeResolutionAndSameDuration(CreativeDTO first, CreativeDTO second) {

    Long fistDuration = first.getVideoAttributes().get(0).getDurationInSecs();
    Long secondDuration = second.getVideoAttributes().get(0).getDurationInSecs();
    Integer firstHeight = first.getVideoAttributes().get(0).getVideoHeight();
    Integer secondHeight = second.getVideoAttributes().get(0).getVideoHeight();
    Integer firstWidth = first.getVideoAttributes().get(0).getVideoWidth();
    Integer secondWidth = second.getVideoAttributes().get(0).getVideoWidth();

    return fistDuration == secondDuration && firstHeight.equals(secondWidth) && secondHeight.equals(firstWidth);
  }



  /**
   * Populate video creative.
   *
   * @param file the file
   * @param baseName the base name
   * @param advertiserid the advertiserid
   * @param imageCompanionList the image companion list
   * @return the creative DTO
   */
  private CreativeDTO populateVideoCreative(CreativeFiles file, String baseName, Long advertiserid,
      List<CreativeDTO> imageCompanionList) {
    CreativeDTO videoCreative = new CreativeDTO();


    try {

      /*
       * Removing this check as we need to support all size video just like display/banner
      if (!crValidate.videoHasRequiredDimension(file)) {
        videoCreative.setErrorMsg(Constants.INVALID_DIMENSION);
        return videoCreative;
      }
      */
      
      videoCreative.setName(baseName + Constants.HYPHEN + Constants.STATIC + Constants.HYPHEN + Constants.VIDEO );
      videoCreative.setCreatedBy(user.getUserInfo().getUserId());
      videoCreative.setCreationTime(System.currentTimeMillis() / 1000);
      videoCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      videoCreative.setType(CreativeType.video);
      videoCreative.setTemplateBased(false);
      videoCreative.setContentType(FileType.getFileType(file.getContentType()));
      videoCreative.setPreviewUrl(getPreviewUrl(file, advertiserid, videoCreative));
      populateVideoAttribute(file, videoCreative, imageCompanionList);
      videoCreative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserid));
      videoCreative.setVideoUploadType(VideoUploadType.VIDEO);
      populateErrorMessageIfAny(file, videoCreative);
    } catch (Exception e) {
      logger.debug("Error occured while creating Video Creative for given File  :  {}", file);
      videoCreative.setErrorMsg(e.getMessage());
    }
    return videoCreative;

  }



  /**
   * Populate video attribute.
   *
   * @param file the file
   * @param videoCreative the video creative
   * @param imageCompanionList the image companion list
   *
   */
  private void populateVideoAttribute(CreativeFiles file, CreativeDTO videoCreative,
      List<CreativeDTO> imageCompanionList) {

    List<VideoAttributes> videoAttributes = new ArrayList<>();
    VideoAttributes videoAttribute = new VideoAttributes();
    videoAttribute.setDurationInSecs(
        file.getVideoAttribute() != null ? file.getVideoAttribute().getDurationInSec() : null);
    videoAttribute.setFormat(CreativeVideoFormat.getByXmlAttributeValue(file.getContentType()));
    videoAttribute.setVideoHeight(file.getHeight());
    videoAttribute.setVideoWidth(file.getWidth());
    if (file.getVideoAttribute() != null)
      videoAttribute.setBitRate(file.getVideoAttribute().getBitRate());

    videoAttribute.setVideoPath(videoCreative.getPreviewUrl());
    populateCompanion(videoAttribute, imageCompanionList);
    videoAttributes.add(videoAttribute);

    if (!Boolean.TRUE.equals(videoAttribute.getHasCompanion()))
      videoCreative.setErrorMsg(Constants.NO_IMAGE_COMPANION_FOUND);

    videoCreative.setVideoAttributes(videoAttributes);
  }



  /**
   * Populate companion.
   *
   * @param videoAttribute the video attribute
   * @param imageListForCompanion the image list for companion
   */
  private void populateCompanion(VideoAttributes videoAttribute,
      List<CreativeDTO> imageListForCompanion) {
    boolean companionFound = false;

    for (CreativeDTO image : imageListForCompanion) {
      if (image.getSize().getHeight().equals(videoAttribute.getVideoHeight())
          && image.getSize().getWidth().equals(videoAttribute.getVideoWidth())) {
        videoAttribute.setCompanionType(VideoCampanionType.COMPANION_IMAGE);
        videoAttribute.setCompanionPath(image.getPreviewUrl());
        videoAttribute.setCompanionWidth(image.getSize().getWidth());
        videoAttribute.setCompanionHeight(image.getSize().getHeight());
        videoAttribute.setCompanionCreativeId(Constants.DEFAULT_COMPANION_CR_ID);
        videoAttribute.setVastProtocol(VastProtocol.VAST_3_WRAPPER);
        videoAttribute.setCompanionContentType(image.getContentType());
        companionFound = true;
        break;
      }
    }
    videoAttribute.setHasCompanion(companionFound);
  }



  /**
   * Populate image creative.
   *
   * @param file the file
   * @param creativeList the creative list
   * @param baseName the base name
   * @param advertiserid the advertiserid
   */
  private void populateImageCreative(CreativeFiles file, List<CreativeDTO> creativeList,
      String baseName, Long advertiserid) {

    CreativeDTO imageCreative = new CreativeDTO();

    try {
      imageCreative.setName(baseName + Constants.HYPHEN + Constants.STATIC + Constants.HYPHEN + Constants.IMAGE);
      imageCreative.setCreatedBy(user.getUserInfo().getUserId());
      imageCreative.setCreationTime(System.currentTimeMillis() / 1000);
      imageCreative.setActive(Boolean.TRUE);
      imageCreative.setSize(new Size(file.getHeight(), file.getWidth()));
      imageCreative.setContent(null);
      imageCreative.setContentType(FileType.getFileType(file.getContentType()));
      imageCreative.setClickDestination(null);
      imageCreative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserid));
      imageCreative.setType(CreativeType.image);
      imageCreative.setTemplateBased(false);
      imageCreative.setPreviewUrl(getPreviewUrl(file, advertiserid, imageCreative));
      imageCreative.setUrlPath(null);
      imageCreative.setNativeAd(Boolean.FALSE);
      populateErrorMessageIfAny(file, imageCreative);
    } catch (Exception e) {
      logger.debug("Error occured while creating Image Creative for given File  :  {}", file);
      imageCreative.setErrorMsg(e.getMessage());
    }

    creativeList.add(imageCreative);

  }



  /**
   * Gets the preview url.
   *
   * @param file the file
   * @param advertiserid the advertiserid
   * @param creative the creative
   * @return the preview url
   */
  private String getPreviewUrl(CreativeFiles file, Long advertiserid, CreativeDTO creative) {

    String content = null;
    String ext = null;
    String finalPath = "";
    String previewDirPostFix = "";
    // content for zip\
    if (file.getName() != null)
      ext = file.getName().split("\\.")[1];

    Path src = Paths.get(properties.getTemporaryCreativeDirectoryPath() + file.getName());

    if (file.getType().equals(CreativeType.html) || file.getType().equals(CreativeType.zippedHTML))
      previewDirPostFix = advertiserid + "/";
    else
      previewDirPostFix = advertiserid + "/" + file.getHeight() + "x" + file.getWidth() + "/";


    String destnDirectory = properties.getTemporaryCreativeDirectoryPath() + previewDirPostFix;
    Path dest = Paths.get(destnDirectory);

    try {
      Files.createDirectories(dest);

      // in case of raw html file
      if (file.getType().equals(CreativeType.zippedHTML)
          && StringUtils.isNotBlank(creative.getContent())) {
        content = creative.getContent();
        return getHtmlPreviewUrl(file.getNoOfSlots(), file.getMacroList(),creative, destnDirectory, content, false);
      }

      finalPath = destnDirectory + generateCreativeFileName(advertiserid,creative) + "." + ext;
      Files.copy(src, Paths.get(finalPath));

      logger.debug("File renamed and copied successfully");

    } catch (IOException e) {
      logger.debug("copying file to preview location directory got an Exception {}",
          ExceptionUtils.getStackTrace(e));
    }

    return finalPath.replace(properties.getCreativeDirectoryPath(),
        properties.getCreativeUrlPrependTemp());
  }

  public String getPreviewUrlForTemplate(CreativeHtmlFile file, Long advertiserId,
          CreativeDTO creative, boolean isUpdate) throws ValidationException {

    String content;
    String previewDirPostFix;

    previewDirPostFix = advertiserId + "/";

    String destinationDirectory = properties.getTemporaryCreativeDirectoryPath() + previewDirPostFix;
    Path dest = Paths.get(destinationDirectory);

    try {
      Files.createDirectories(dest);
    } catch (IOException e) {
      logger.debug("Failed to create destination directory for the Html template creative {}",
              ExceptionUtils.getStackTrace(e));
    }
    if (StringUtils.isNotBlank(creative.getContent())) {
      content = creative.getContent();
    } else {
      throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST, "Request DTO requires html content");
    }
    return getHtmlPreviewUrl(file.getNoOfSlots(), file.getMacroList(),creative,
            destinationDirectory, content, isUpdate);
  }



  private String generateCreativeFileName(Long advertiserId, CreativeDTO creative) {
    
    StringBuilder fileName = new StringBuilder();
    fileName.append(advertiserId).append(Constants.HYPHEN);
    
    if(creative.isNativeAd())
      fileName.append(Constants.NATIVE).append(Constants.HYPHEN);
    
    if(creative.isDcoAd())
      fileName.append(Constants.DCO).append(Constants.HYPHEN);
    
    fileName.append(creative.getType().toString()).append(Constants.HYPHEN).append(util.generateUniqueId()).append(Constants.HYPHEN);
    
    if(creative.getSize()!=null && creative.getSize().getHeight()!=null && creative.getSize().getWidth()!=null)
      fileName.append(creative.getSize().getHeight()).append("x").append(creative.getSize().getWidth());
    
    String name = fileName.toString();
    
    if(name.charAt(name.length()-1) == '-')
      name = name.substring(0, name.length()-1);
    
    return name;
  }



  /**
   * Gets the previewUrl for ad tag.
   *
   * @param adTag the ad tag
   * @param name the name
   *
   * @return the previewUrl for ad tag
   *
   */
  public String getPreviewUrlForAdTag(CreativeThirdPartyAdTag adTag, String name) throws IOException {

    if (adTag.getAdTag() == null)
      return null;

    String fileDestination =
        properties.getTemporaryCreativeDirectoryPath() + adTag.getBasicDetails().getAdvertiserId()
            + Constants.DIR_SEPARATOR + Constants.AD_TAG + Constants.DIR_SEPARATOR + name;


    File newFile = new File(fileDestination);
    try {
      newFile.getParentFile().mkdirs();
      Files.createFile(newFile.toPath());
    } catch (IOException e) {
      logger.error("ERROR : EXCEPTION occurred while creating adtag file ..........{}",
          ExceptionUtils.getStackTrace(e));
      throw e;
    }


    try (FileWriter myWriter = new FileWriter(newFile.getPath())){
      myWriter.write((adTag.getAdTag().trim()));
    } catch (IOException e) {
      logger.error("ERROR : EXCEPTION occurred while writing content to Adtag File {}",
          ExceptionUtils.getStackTrace(e));
      throw e;
    }
    return newFile.getPath().replace(properties.getCreativeDirectoryPath(),
        properties.getCreativeUrlPrependTemp());
  }



  /**
   * Gets the html preview url for zip.
   * @param noOfSlots number of slots available for a creative
   * @param macros macros list for a creative
   * @param dest the dest
   * @param content the content
   *
   * @return the html preview url
   */
  private String getHtmlPreviewUrl(Integer noOfSlots, String macros,
          CreativeDTO creative, String dest, String content, boolean isUpdate) {

    String destination = (dest + generateCreativeFileName(creative.getAdvertiser().getId(), creative)) + ".html";
    File htmlFile = new File(destination);
    try {
      if (htmlFile.createNewFile())
        logger.debug("File created: {}", destination);
      else
        logger.debug("File already exists.");

    } catch (IOException e) {
      logger.error("Exception occured while creating  html file {}",
          ExceptionUtils.getStackTrace(e));
    }

    try (FileWriter myWriter = new FileWriter(destination)){
      if (!isUpdate) {
        content = util.replaceClickNoEncodingString(String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000)), content);

        if (creative.isDcoAd()) {
          try {
            String macroJsonString = util.constructMacroStringFromDcoDatabase(creative.getAdvertiser().getId(),
                    noOfSlots, macros);
            content = util.replaceDynamicMacroWithContent(content, macroJsonString);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      
      myWriter.write(content);
      logger.debug("Successfully wrote to the file.{} content : {} ", destination, content);
    } catch (IOException e) {
      logger.error("Exception occurred while write to html file for zip creative {} {}", destination,
          ExceptionUtils.getStackTrace(e));
    }

    return destination.replace(properties.getCreativeDirectoryPath(),
        properties.getCreativeUrlPrependTemp());

  }

}
