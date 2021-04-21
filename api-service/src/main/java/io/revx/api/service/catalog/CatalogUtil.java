/*
 * @author: ranjan-pritesh
 * 
 * @date:
 */
package io.revx.api.service.catalog;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import io.revx.api.mysql.dco.entity.catalog.AdvertiserCatalogVariablesMappingEntity;
import io.revx.api.mysql.dco.entity.catalog.AtomCatalogVariablesEntity;
import io.revx.api.mysql.dco.entity.catalog.CatalogItemEntity;
import io.revx.api.mysql.dco.entity.catalog.FeedApiStatusEntity;
import io.revx.api.mysql.dco.repo.catalog.AdvertiserCatalogVariableMappingRepository;
import io.revx.api.mysql.dco.repo.catalog.AtomCatalogVariableRepository;
import io.revx.api.mysql.dco.repo.catalog.CatalogItemRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedApiStatsRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoRepository;
import io.revx.api.mysql.dco.repo.catalog.FeedInfoStatsRepository;
import io.revx.api.service.ModelConverterService;
import io.revx.core.enums.MacroType;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;

/**
 * The Class CatalogUtil.
 */
@Service
public class CatalogUtil {

  /** The logger. */
  private static Logger logger = LogManager.getLogger(CatalogService.class);

  /** The feed info repo. */
  @Autowired
  FeedInfoRepository feedInfoRepo;

  /** The acvm repo. */
  @Autowired
  AdvertiserCatalogVariableMappingRepository acvmRepo;

  /** The acv repo. */
  @Autowired
  AtomCatalogVariableRepository acvRepo;

  /** The ci repo. */
  @Autowired
  CatalogItemRepository ciRepo;

  /** The fis repo. */
  @Autowired
  FeedInfoStatsRepository fisRepo;

  /** The fas repo. */
  @Autowired
  FeedApiStatsRepository fasRepo;

  /** The converter. */
  @Autowired
  ModelConverterService converter;

  /**
   * Gets the macro DTO list for advertiser.
   *
   * @param advertiserId the advertiser id
   * @return the macro DTO list for advertiser
   */
  public List<Macro> getMacroDTOListForAdvertiser(Long advertiserId) {

    List<Macro> macroDTOList = null;

    List<AtomCatalogVariablesEntity> atomVariables = getAtomVariablesForAdvertiser(advertiserId);
    Map<Long, Macro> macrosMap = new HashMap<>();
    
    if(atomVariables == null || atomVariables.isEmpty())
      return null;

    for (AtomCatalogVariablesEntity av : atomVariables) {
      if (av.getId() != 1) {
        if (macrosMap.containsKey(av.getId())) {
          Macro macroDTO = macrosMap.get(av);
          macroDTO.setIsCustomMacro(Boolean.FALSE);
          if (macroDTO.getMacroType().equals(MacroType.pixel))
            macroDTO.setMacroType(MacroType.hybrid);
          else
            macroDTO.setMacroType(MacroType.feed);
        } else {
          Macro macroDTO = new Macro();
          macroDTO.setId(av.getId());
          macroDTO.setName(av.getName());
          macroDTO.setMacroText(av.getMacroText());
          macroDTO.setAdvertiserId(advertiserId);
          macroDTO.setIsCustomMacro(Boolean.FALSE);
          macroDTO.setMacroType(MacroType.feed);
          macrosMap.put(av.getId(), macroDTO);
        }
      }
    }

    List<CatalogItemEntity> items =
        ciRepo.findAllByAdvertiserId(advertiserId, PageRequest.of(0, 3));
    populateMacroListWithSamples(macrosMap, items);

    macroDTOList = macrosMap.values().stream().collect(Collectors.toList());
    return macroDTOList;
  }



  /**
   * Populate macro list with samples.
   *
   * @param macrosMap the macros map
   * @param items the items
   */
  private void populateMacroListWithSamples(Map<Long, Macro> macrosMap,
      List<CatalogItemEntity> items) {

    for (CatalogItemEntity item : items) {
      for (Map.Entry<Long, Macro> entry : macrosMap.entrySet()) {
        Long key = entry.getKey();
        Macro macro = entry.getValue();
        String name = macro.getName();
        if (macro.getSamples() == null)
          macro.setSamples(new ArrayList());
        if (macro.getIsCustomMacro() == null || macro.getIsCustomMacro().equals(Boolean.FALSE))
          addElementToSamplesList(name, macro.getSamples(), item);
      }
    }

  }



  /**
   * Adds the element to samples list.
   *
   * @param name the name
   * @param list the list
   * @param item the item
   */
  private void addElementToSamplesList(String name, List list, CatalogItemEntity item) {

    // items_xpath
    if (name.equals("business_type")) {
      if (item.getBusinessType() != null)
        list.add(item.getBusinessType());
    } else if (name.equals("title")) {
      if (item.getTitle() != null)
        list.add(URLEncoder.encode(item.getTitle()));
    } else if (name.equals("content_title")) {
      if (item.getContentTitle() != null)
        list.add(URLEncoder.encode(item.getContentTitle()));
    } else if (name.equals("description")) {
      if (item.getDescription() != null)
        list.add(URLEncoder.encode(item.getDescription()));
    } else if (name.equals("content_description")) {
      if (item.getContentDescription() != null)
        list.add(URLEncoder.encode(item.getContentDescription()));
    } else if (name.equals("page_link")) {
      if (item.getPageLink() != null)
        list.add(item.getPageLink());
    } else if (name.equals("encoded_page_link")) {
      if (item.getPageLink() != null)
        list.add(URLEncoder.encode(item.getPageLink()));
    } else if (name.equals("id")) {
      if (item.getStockKeepingUnit() != null)
        list.add(item.getStockKeepingUnit());
    } else if (name.equals("default_image")) {
      if (item.getDefaultImage() != null)
        list.add(item.getDefaultImage());
    } else if (name.equals("additional_images")) {
      if (item.getAdditionalImages() != null)
        list.add(item.getAdditionalImages());
    } else if (name.equals("availability")) {
      if (item.getAvailability() != null)
        list.add(item.getAvailability());
    } else if (name.equals("stock_quantity")) {
      if (item.getStockQuantity() != null)
        list.add(item.getStockQuantity());
    } else if (name.equals("original_price")) {
      if (item.getOriginalPrice() != null)
        list.add(item.getOriginalPrice());
    } else if (name.equals("sale_price")) {
      if (item.getSalePrice() != null)
        list.add(item.getSalePrice());
    } else if (name.equals("offer_start_date")) {
      if (item.getOfferStartDate() != null)
        list.add(item.getOfferStartDate());
    } else if (name.equals("offer_end_date")) {
      if (item.getOfferEndDate() != null)
        list.add(item.getOfferEndDate());
    } else if (name.equals("discount")) {
      if (item.getDiscount() != null)
        list.add(item.getDiscount());
    } else if (name.equals("brand")) {
      if (item.getBrand() != null)
        list.add(URLEncoder.encode(item.getBrand()));
    } else if (name.equals("gender")) {
      if (item.getGender() != null)
        list.add(URLEncoder.encode(item.getGender()));
    } else if (name.equals("age_group")) {
      if (item.getAgeGroup() != null)
        list.add(URLEncoder.encode(item.getAgeGroup()));
    } else if (name.equals("color")) {
      if (item.getColor() != null)
        list.add(URLEncoder.encode(item.getColor()));
    } else if (name.equals("size")) {
      if (item.getSize() != null)
        list.add(item.getSize());
    } else if (name.equals("material")) {
      if (item.getMaterial() != null)
        list.add(URLEncoder.encode(item.getMaterial()));
    } else if (name.equals("pattern")) {
      if (item.getPattern() != null)
        list.add(URLEncoder.encode(item.getPattern()));
    } else if (name.equals("source")) {
      if (item.getSource() != null)
        list.add(URLEncoder.encode(item.getSource()));
    } else if (name.equals("destination")) {
      if (item.getDestination() != null)
        list.add(URLEncoder.encode(item.getDestination()));
    } else if (name.equals("start_date")) {
      if (item.getStartDate() != null)
        list.add(item.getStartDate());
    } else if (name.equals("end_date")) {
      if (item.getEndDate() != null)
        list.add(item.getEndDate());
    } else if (name.equals("duration")) {
      if (item.getDuration() != null)
        list.add(item.getDuration());
    } else if (name.equals("class")) {
      if (item.getClassStr() != null)
        list.add(URLEncoder.encode(item.getClassStr()));
    }
    // Added missing mappings
    else if (name.equals("source_code")) {
      if (item.getSourceCode() != null)
        list.add(item.getSourceCode());
    } else if (name.equals("destination_code")) {
      if (item.getDestinationCode() != null)
        list.add(item.getDestinationCode());
    } else if (name.equals("offer_name")) {
      if (item.getOfferName() != null)
        list.add(item.getOfferName());
    } else if (name.equals("offer_desc")) {
      if (item.getOfferDesc() != null)
        list.add(item.getOfferDesc());
    } else if (name.equals("offer_coupon_code")) {
      if (item.getOfferCouponCode() != null)
        list.add(item.getOfferCouponCode());
    } else if (name.equals("offer_percent")) {
      if (item.getOfferPercent() != null)
        list.add(item.getOfferPercent());
    } else if (name.equals("offer_amount")) {
      if (item.getOfferAmount() != null)
        list.add(item.getOfferAmount());
    } else if (name.equals("best_price")) {
      if (item.getBestPrice() != null)
        list.add(item.getBestPrice());
    } else if (name.equals("coupon_valid")) {
      if (item.getCouponValid() != null)
        list.add(item.getCouponValid());
    } else if (name.equals("min_order_value")) {
      if (item.getMinOrderValue() != null)
        list.add(item.getMinOrderValue());
    } else if (name.equals("android_tablet_click_url")) {
      if (item.getAndTabletClickUrl() != null)
        list.add(item.getAndTabletClickUrl());
    } else if (name.equals("android_phone_click_url")) {
      if (item.getAndPhoneClickUrl() != null)
        list.add(item.getAndPhoneClickUrl());
    } else if (name.equals("ios_tablet_click_url")) {
      if (item.getIosTabletClickUrl() != null)
        list.add(item.getIosTabletClickUrl());
    } else if (name.equals("ios_phone_click_url")) {
      if (item.getIosPhoneClickUrl() != null)
        list.add(item.getIosPhoneClickUrl());
    } else if (name.equals("windows_tablet_click_url")) {
      if (item.getWinTabletClickUrl() != null)
        list.add(item.getWinTabletClickUrl());
    } else if (name.equals("windows_phone_click_url")) {
      if (item.getWinPhoneClickUrl() != null)
        list.add(item.getWinPhoneClickUrl());
    } else if (name.equals("third_party_click_url")) {
      if (item.getThirdPartyClickUrl() != null)
        list.add(item.getThirdPartyClickUrl());
    } else if (name.equals("category_id")) {
      if (item.getCategoryId() != null)
        list.add(item.getCategoryId());
    } else if (name.equals("category_name")) {
      if (item.getCategoryName() != null)
        list.add(item.getCategoryName());
    } else if (name.equals("sub_category_name")) {
      if (item.getSubCategoryName() != null)
        list.add(item.getSubCategoryName());
    } else if (name.equals("sub_category_id")) {
      if (item.getSubCategoryId() != null)
        list.add(item.getSubCategoryId());
    } else if (name.equals("publish_date")) {
      if (item.getPublishDate() != null)
        list.add(item.getPublishDate());
    }


  }



  /**
   * Gets the atom variables for advertiser.
   *
   * @param advertiserId the advertiser id
   * @return the atom variables for advertiser
   */
  private List<AtomCatalogVariablesEntity> getAtomVariablesForAdvertiser(Long advertiserId) {

    List<Long> feedIdList = feedInfoRepo.findIdByAdvertiserid(advertiserId);
    logger.debug("no. of feedId retrieved for advertiser {}  :  {}", advertiserId,
        feedIdList.size());
    if (feedIdList == null || feedIdList.isEmpty()) {
      logger.debug("FeedID List is null for given advertiser");
      return null;
    }
    List<Long> atomVariableList = acvmRepo.findAtomVariableInFeedIds(feedIdList);
    logger.debug("no. of atom variables retrieved for advertiser {}  :  {}", advertiserId,
        atomVariableList.size());

    if (atomVariableList == null || atomVariableList.isEmpty()) {
      logger.debug("atomVariableList is null for given advertiser");
      return null;
    }
    
    /*
     * Adding encoded_page_link by default if page_link is present as encoded_page_link we never map in feed. 5: PAGE_LINK & 71 : ENCODED_PAGE_LINK
     */
    if(atomVariableList.contains(5L) && !atomVariableList.contains(71L))
      atomVariableList.add(71L);
      
    /*
     * Removing unncessory macro. 46:android_tablet_click_url, 47:android_phone_click_url, 48:ios_tablet_click_url, 49:ios_phone_click_url, 50:windows_tablet_click_url, 51:windows_phone_click_url
     */
    atomVariableList.remove(46L);
    atomVariableList.remove(47L);
    atomVariableList.remove(48L);
    atomVariableList.remove(49L);
    atomVariableList.remove(50L);
    atomVariableList.remove(51L);
    
    List<AtomCatalogVariablesEntity> acvEntities = acvRepo.findAllByIdIn(atomVariableList);
    if (acvEntities == null || acvEntities.isEmpty()) {
      logger.debug("acvEntities is null for given acv_id");
      return null;
    }

    return acvEntities;
  }



  /**
   * Gets the variable mapping.
   *
   * @param feedId the feed id
   * @param advertiserId the advertiser id
   * @return the variable mapping
   */
  public List<VariablesMappingDTO> getVariableMapping(Long feedId, Long advertiserId) {
    if (feedId == null || advertiserId == null)
      return null;
    List<VariablesMappingDTO> acvmDTOList = new ArrayList<>();
    List<AdvertiserCatalogVariablesMappingEntity> acvmList = null;
    Map<Long, String> prefixMap = new HashMap<>();

    acvmList = acvmRepo.findAllByFeedId(feedId);

    if (acvmList == null || acvmList.isEmpty())
      return Collections.emptyList();

    for (AdvertiserCatalogVariablesMappingEntity acvmEntity : acvmList) {
      Optional<AtomCatalogVariablesEntity> acvOptional =
          acvRepo.findById(acvmEntity.getAtomVariable());
      VariablesMappingDTO acvmDTO = converter.convertAcvmToDTO(acvmEntity, acvOptional);
      if (acvmEntity.getAtomVariable() != 1) {
        acvmDTOList.add(acvmDTO);
      } else {
        prefixMap.put(acvmEntity.getFeedId(), acvmEntity.getxPath());
      }
    }

    acvmDTOList.forEach(s -> s.setVariablePath(prefixMap.get(s.feedId) + "/" + s.getName()));

    List<CatalogItemEntity> items =
        ciRepo.findAllByAdvertiserIdAndFeedId(advertiserId, feedId, PageRequest.of(0, 3));
    populateAcvmDTOListWithSamples(acvmDTOList, items);

    return acvmDTOList;
  }



  /**
   * Populate acvm DTO list with samples.
   *
   * @param acvmDTOList the acvm DTO list
   * @param items the items
   */
  private void populateAcvmDTOListWithSamples(List<VariablesMappingDTO> acvmDTOList,
      List<CatalogItemEntity> items) {

    for (CatalogItemEntity item : items) {
      for (VariablesMappingDTO dto : acvmDTOList) {
        dto.setSampleSize((long) items.size());
        addElementToSamplesList(dto.standardVariable, dto.samples, item);
      }
    }
  }

  /**
   * Gets the success rate for feed info stats.
   *
   * @param id the id
   * @param minimumTime the minimum time
   * @return the success rate for feed info stats
   */
  public Long getSuccessRateForFeedInfoStats(Long id, Long minimumTime) {
    Long successRate = 0L;
    Long successCount = fisRepo.getSuccessCountForFeedId(id, minimumTime);
    Long totalCount = fisRepo.getTotalCountForFeedId(id, minimumTime);

    if (successCount != null && totalCount != null && totalCount > 0)
      successRate = ((successCount.intValue() * 100) / totalCount);

    return successRate;
  }


  /**
   * Gets the success rate for feed api status.
   *
   * @param feedId the feed id
   * @return the success rate for feed api status
   */
  public Long getSuccessRateForFeedApiStatus(Long feedId) {

    List<FeedApiStatusEntity> fas = fasRepo.findAllByOrderByFasCreatedTimeDesc(feedId);
    Long successRate = 0L;
    if (fas != null && !fas.isEmpty()) {
      FeedApiStatusEntity fasLatest = fas.get(0);
      findSuccessRateForFeedApiStatus(fasLatest);
    }

    return successRate;
  }

  /**
   * Find success rate for feed api status.
   *
   * @param fasLatest the fas latest
   * @return the long
   */
  public Long findSuccessRateForFeedApiStatus(FeedApiStatusEntity fasLatest) {
    Long success = fasLatest.getFasProductInserted() + fasLatest.getFasProductUpdated()
        + fasLatest.getFasProductDelete();
    Long total = fasLatest.getFasProductTotal();
    
	Long successRate = total == 0 ? 0 : (success * 100) / total;
    return successRate;
  }


}
