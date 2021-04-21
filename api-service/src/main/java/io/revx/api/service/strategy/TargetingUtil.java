
package io.revx.api.service.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.strategy.AdvertiserLineItemTargetingExpression;
import io.revx.api.mysql.entity.strategy.BidStrategy;
import io.revx.api.mysql.entity.strategy.InventorySource;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.entity.strategy.TargetingComponent;
import io.revx.api.mysql.repo.strategy.AdvertiserLineItemTargetingExpRepo;
import io.revx.api.mysql.repo.strategy.BidStrategyRepo;
import io.revx.api.mysql.repo.strategy.InventrySourceRepo;
import io.revx.api.mysql.repo.strategy.TargettingComponentRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.core.enums.AudienceType;
import io.revx.core.enums.InventoryType;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.AppCategoryMaster;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.Platform;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.strategy.AuctionType;
import io.revx.core.model.strategy.ConnectionType;
import io.revx.core.model.strategy.DealCategoryDTO;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.model.strategy.StrategyTargetingDetails;
import io.revx.core.model.strategy.TargetingFilter;
import io.revx.core.model.strategy.TargetingOperator;
import io.revx.core.model.targetting.AudienceStrDTO;
import io.revx.core.model.targetting.Day;
import io.revx.core.model.targetting.DayPart;
import io.revx.core.model.targetting.InventoryObject;
import io.revx.core.model.targetting.RTBAggregators;
import io.revx.core.model.targetting.RTBSites;
import io.revx.core.model.targetting.Segments;
import io.revx.core.model.targetting.TargetAppCategories;
import io.revx.core.model.targetting.TargetBrowsers;
import io.revx.core.model.targetting.TargetGeoDTO;
import io.revx.core.model.targetting.TargetSegments;
import io.revx.core.model.targetting.TargetingComponentDTO;
import io.revx.core.model.targetting.TargetingObject;
import io.revx.core.utils.NumberUtils;

@Component
@Transactional
public class TargetingUtil {

	private static final Logger logger = LoggerFactory.getLogger(TargetingUtil.class);
	private MobileTargetingUtil mobileTargetingUtil;
	private AdvertiserLineItemTargetingExpRepo aliTRepo;
	private TargettingComponentRepository tcRepository;
	private EntityESService elasticSearch;
	private Utility utility;
	private  ApplicationProperties properties;
	private PlacementTargetingUtil placementTargetingUtil;
	private InventrySourceRepo inventrySourceRepo;
	private BidStrategyRepo bidStrategyRepo;

	@Autowired
	public void setMobileTargetingUtil(MobileTargetingUtil mobileTargetingUtil) {
		this.mobileTargetingUtil = mobileTargetingUtil;
	}

	@Autowired
	public void setAliTRepo(AdvertiserLineItemTargetingExpRepo aliTRepo) {
		this.aliTRepo = aliTRepo;
	}

	@Autowired
	public void setTcRepository(TargettingComponentRepository tcRepository) {
		this.tcRepository = tcRepository;
	}

	@Autowired
	public void setElasticSearch(EntityESService elasticSearch) {
		this.elasticSearch = elasticSearch;
	}

	@Autowired
	public void setUtility(Utility utility) {
		this.utility = utility;
	}

	@Autowired
	public void setProperties(ApplicationProperties properties) {
		this.properties = properties;
	}

	@Autowired
	public void setPlacementTargetingUtil(PlacementTargetingUtil placementTargetingUtil) {
		this.placementTargetingUtil = placementTargetingUtil;
	}

	@Autowired
	public void setInventrySourceRepo(InventrySourceRepo inventrySourceRepo) {
		this.inventrySourceRepo = inventrySourceRepo;
	}

	@Autowired
	public void setBidStrategyRepo(BidStrategyRepo bidStrategyRepo) {
		this.bidStrategyRepo = bidStrategyRepo;
	}

	Map<String, Long> filterTypeEnumMap = new HashMap<>();

	@PostConstruct
	public void init() {
		filterTypeEnumMap.put(TargetingConstants.COUNTRY, TargetingFilter.GEO_COUNTRY.getId());
		filterTypeEnumMap.put(TargetingConstants.STATE, TargetingFilter.GEO_REGION.getId());
		filterTypeEnumMap.put(TargetingConstants.CITY, TargetingFilter.GEO_CITY.getId());
		filterTypeEnumMap.put(TargetingConstants.SEGMENTS, TargetingFilter.USER_SEGMENT.getId());
	}

	@Transactional
	public void addBasicTargetingToStrategy(StrategyDTO strategy, StrategyEntity strategyDO)
			throws ValidationException {
		String commonTExpr = getTargetingExpressionForStrategy(strategy, strategyDO);
		String placementSpecificTExpr = placementTargetingUtil.createPlacementSpecificTargetingExpression(strategy);
		AdvertiserLineItemTargetingExpression alte = new AdvertiserLineItemTargetingExpression();
		alte.setStrategyId(strategyDO.getId());
		alte.setCommonTargetingExpression(commonTExpr);
		alte.setPlacementTargetingExpression(placementSpecificTExpr);
		aliTRepo.save(alte);
	}

	@Transactional
	public void updateBasicTargetingOnStrategy(StrategyDTO strategy, StrategyEntity strategyDO)
			throws ValidationException {
		String commonTExpr = updateTargetingExpressionOnDO(strategy, strategyDO);
		String placementSpecificTExpr = placementTargetingUtil.updatePlacementTargetingExpression(strategy, strategyDO);
		Optional<AdvertiserLineItemTargetingExpression> alte = aliTRepo.findByStrategyId(strategyDO.getId());
		if (alte.isPresent()) {
			alte.get().setCommonTargetingExpression(commonTExpr);
			alte.get().setPlacementTargetingExpression(placementSpecificTExpr);
			aliTRepo.save(alte.get());
		}
		// TODO: Something to Do
	}

	public void populateStrategyDTOWithBasicTargeting(StrategyEntity strategyDO, StrategyDTO strategy) {
		// TODO
		try {
			Optional<AdvertiserLineItemTargetingExpression> ali = aliTRepo.findByStrategyId(strategyDO.getId());
			if (!ali.isPresent())
				return;
			String tExpr = ali.get().getCommonTargetingExpression();
			populateTargetingParametersInDTO(strategy, tExpr);
			placementTargetingUtil.populatePlacementTargetingInDTO(strategy, strategyDO);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Cannot find getStrategyTargetingExpression for strategy id : {}" , strategyDO.getId());
		}
	}

	/*
	 * Targeting filters : | DAY_OF_WEEK | 0 | | HOUR_OF_DAY | 1 | | GEO_CITY | 2 |
	 * | GEO_REGION | 3 | | GEO_COUNTRY | 4 | | BROWSER | 5 | | OS | 6 | | LANGUAGE
	 * | 7 | | RESOLUTION | 8 | | FOLD_POSITION | 9 | | USER_SEGMENT | 10 | |
	 * RTB_AGGREGATOR | 11 | | RTB_PUBLISHER | 12 | | RTB_SITE | 13 | |
	 * RTB_PUB_CATEGORY | 14 |
	 */

	// Create targeting expression
	public String getTargetingExpressionForStrategy(StrategyDTO strategy, StrategyEntity strategyDO)
			throws ValidationException {
		StringBuilder tExpr = new StringBuilder();
		String dayPartTExpr = "";
		String browserTExpr = "";
		String connTypeTExpr = "";
		String geoTExpr = "";
		String segmentTExpr = "";
		String mobileModelTExpr = "";

		List<String> expressionStrings = new ArrayList<String>();

		if (strategy.targetDays != null) {
			dayPartTExpr = createDayPartTargetingExpression(strategy);
			if (dayPartTExpr != null && dayPartTExpr.length() != 0)
				expressionStrings.add(dayPartTExpr);
		}

		if (strategy.targetBrowsers != null) {
			browserTExpr = createBrowserTargetingExpression(strategy);
			if (browserTExpr != null && browserTExpr.length() != 0)
				expressionStrings.add(browserTExpr);
		}

		if (strategy.getConnectionTypes() != null) {
			connTypeTExpr = createConnTypeTargetingExpression(strategy);
			if (connTypeTExpr != null && connTypeTExpr.length() != 0)
				expressionStrings.add(connTypeTExpr);
		}

		if (strategy.targetGeographies != null) {
			geoTExpr = createNewGeoTargetingExpression(strategy);
			if (geoTExpr != null && geoTExpr.length() != 0)
				expressionStrings.add(geoTExpr);
		}

		/*
		 * Storing APP, WEB and DMP audience in different object. So UI can easily
		 * distinguish it. But while storing into DB, merging the same as same operator
		 * being used in both audiences.
		 */

		CampaignESDTO campaign = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyDO.getCampianId());
		AudienceStrDTO targetSegments = mergeAudienceData(strategy.getTargetAppSegments(),
				strategy.getTargetWebSegments(), strategy.getTargetDmpSegments());
		if (targetSegments != null && targetSegments.isCustomSegmentTargeting()
				&& (!campaign.getSkadTarget() || Boolean.parseBoolean(properties.getAllowAudienceTargetingForSkad()))) {
			segmentTExpr = createAudienceTargetingExpression(targetSegments);
		}

		if (segmentTExpr != null && segmentTExpr.length() != 0)
			expressionStrings.add(segmentTExpr);

		// Construct Final Targeting Expression
		for (int i = 0; i < expressionStrings.size(); i++) {
			if (expressionStrings.get(i) != null && expressionStrings.get(i).trim().length() > 0) {
				String exprStr = "(" + expressionStrings.get(i).trim() + ")";
				tExpr.append(exprStr);
				if (i != expressionStrings.size() - 1)
					tExpr.append("&");
			}
		}

		logger.debug("Strategy targeting expression is : {}" , tExpr);
		return tExpr.toString();
	}

	// Update targeting expression
	@Transactional
	public String updateTargetingExpressionOnDO(StrategyDTO strategy, StrategyEntity strategyDO)
			throws ValidationException {
		String expr = strategyDO.getTargetingExpression();
		StringBuilder tExpr = new StringBuilder();

		StrategyTargetingExpression exprObj = breakExpressionIntoComponents(expr);

		String dayPartTExpr = "";
		String browserTExpr = "";
		String geoTExpr = "";
		String segmentTExpr = "";
		String osTExpr = "";
		String mobileModelsTExpr = "";
		String connTypeTExpr = "";

		List<String> expressionStrings = new ArrayList<String>();

		if (strategy.targetDays != null) {
			// cleanup old dayPartExpr
			if (exprObj.dayPartExpr != null && exprObj.dayPartExpr.length() > 0) {
				List<String> tcIds = utility.getListOfTCIdsInExpr(exprObj.dayPartExpr);
				utility.deleteOldTargetingComponents(tcIds);
			}

			// create new dayPartExpr
			dayPartTExpr = createDayPartTargetingExpression(strategy);
			if (dayPartTExpr != null && dayPartTExpr.length() != 0)
				expressionStrings.add(dayPartTExpr);
		} else {
			if (exprObj.dayPartExpr != null && exprObj.dayPartExpr.length() > 0) {
				String oldExpr = exprObj.dayPartExpr;
				int length = oldExpr.length();
				if (oldExpr.startsWith("(") && oldExpr.endsWith(")")) {
					oldExpr = oldExpr.trim().substring(1, length - 1).trim();
				}
				expressionStrings.add(oldExpr);
			}
		}

		if (strategy.getConnectionTypes() != null) {
			// cleanup old browserExpr
			if (exprObj.connTypeExpr != null && exprObj.connTypeExpr.length() > 0) {
				List<String> tcIds = utility.getListOfTCIdsInExpr(exprObj.connTypeExpr);
				utility.deleteOldTargetingComponents(tcIds);
			}

			// create new browserExpr
			connTypeTExpr = createConnTypeTargetingExpression(strategy);
			if (connTypeTExpr != null && connTypeTExpr.length() != 0)
				expressionStrings.add(connTypeTExpr);
		} else {
			if (exprObj.connTypeExpr != null && exprObj.connTypeExpr.length() > 0) {
				String oldExpr = exprObj.connTypeExpr;
				int length = oldExpr.length();
				if (oldExpr.startsWith("(") && oldExpr.endsWith(")")) {
					oldExpr = oldExpr.trim().substring(1, length - 1).trim();
				}
				expressionStrings.add(oldExpr);
			}
		}

		if (strategy.targetBrowsers != null) {
			// cleanup old browserExpr
			if (exprObj.browserExpr != null && exprObj.browserExpr.length() > 0) {
				List<String> tcIds = utility.getListOfTCIdsInExpr(exprObj.browserExpr);
				utility.deleteOldTargetingComponents(tcIds);
			}

			// create new browserExpr
			browserTExpr = createBrowserTargetingExpression(strategy);
			if (browserTExpr != null && browserTExpr.length() != 0)
				expressionStrings.add(browserTExpr);
		} else {
			if (exprObj.browserExpr != null && exprObj.browserExpr.length() > 0) {
				String oldExpr = exprObj.browserExpr;
				int length = oldExpr.length();
				if (oldExpr.startsWith("(") && oldExpr.endsWith(")")) {
					oldExpr = oldExpr.trim().substring(1, length - 1).trim();
				}
				expressionStrings.add(oldExpr);
			}
		}

		if (strategy.targetGeographies != null) {
			// cleanup old geographiesExpr
			if (exprObj.geoExpr != null && exprObj.geoExpr.length() > 0) {
				List<String> tcIds = utility.getListOfTCIdsInExpr(exprObj.geoExpr);
				utility.deleteOldTargetingComponents(tcIds);
			}

			// create new geographiesExpr
			// TODO : validations for invalid targeting
			geoTExpr = createNewGeoTargetingExpression(strategy);
			if (geoTExpr != null && geoTExpr.length() != 0)
				expressionStrings.add(geoTExpr);
		} else {
			if (exprObj.geoExpr != null && exprObj.geoExpr.length() > 0) {
				String oldExpr = exprObj.geoExpr;
				int length = oldExpr.length();
				if (oldExpr.startsWith("(") && oldExpr.endsWith(")")) {
					oldExpr = oldExpr.trim().substring(1, length - 1).trim();
				}
				expressionStrings.add(oldExpr);
			}
		}

		CampaignESDTO campaign = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, strategyDO.getCampianId());
		if (!campaign.getSkadTarget() || Boolean.parseBoolean(properties.getAllowAudienceTargetingForSkad())) {
			AudienceStrDTO targetSegments = mergeAudienceData(strategy.getTargetAppSegments(),
					strategy.getTargetWebSegments(), strategy.getTargetDmpSegments());
			if (targetSegments != null) {
				boolean deleteOldEx = false;

				segmentTExpr = createAudienceTargetingExpression(targetSegments);

				if (segmentTExpr != null && segmentTExpr.length() != 0) {
					deleteOldEx = true;
					expressionStrings.add(segmentTExpr);
				} else if (StringUtils.isBlank(segmentTExpr) && !targetSegments.isCustomSegmentTargeting()) {
					deleteOldEx = true;
				}
				// cleanup old segmentsExpr
				if (exprObj.segmentExpr != null && exprObj.segmentExpr.length() > 0 && deleteOldEx) {
					List<String> tcIds = utility.getListOfTCIdsInExpr(exprObj.segmentExpr);
					utility.deleteOldTargetingComponents(tcIds);
				}
			} else {
				if (exprObj.segmentExpr != null && exprObj.segmentExpr.length() > 0) {
					String oldExpr = exprObj.segmentExpr;
					int length = oldExpr.length();
					if (oldExpr.startsWith("(") && oldExpr.endsWith(")")) {
						oldExpr = oldExpr.trim().substring(1, length - 1).trim();
					}
					expressionStrings.add(oldExpr);
				}
			}
		}

		// Construct Final Targeting Expression
		for (int i = 0; i < expressionStrings.size(); i++) {
			if (expressionStrings.get(i) != null && expressionStrings.get(i).trim().length() > 0) {
				String exprStr = "(" + expressionStrings.get(i).trim() + ")";
				tExpr.append(exprStr);
				if (i != expressionStrings.size() - 1)
					tExpr.append("&");
			}
		}

		logger.debug("Strategy targeting expression is : {} ", tExpr);

		return tExpr.toString();
	}

	// New Geo targeting functions
	@Transactional
	public String createNewGeoTargetingExpression(StrategyDTO strategy) throws ValidationException {
		String geoTExpr = "", targetedGeoExpr = "", blockExpr = "";
		String countryTExp = "", stateTExp = "", cityTExp = "";
		TargetGeoDTO geoDTO = strategy.targetGeographies;
		boolean customGeoTargeting = geoDTO.customGeoTargeting;

		logger.debug("geoDTO : {} ", geoDTO);

		if (!customGeoTargeting) {
			return geoTExpr;
		}

		if (geoDTO == null || (TargetingObject.isEmptyTargetting(geoDTO.country)
				&& TargetingObject.isEmptyTargetting(geoDTO.state) && TargetingObject.isEmptyTargetting(geoDTO.city))) {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST, new Object[] { "geo targeting type",
					"targeted/blocked list", "geo targeting", "customGeoTargeting" });
		}

		if (!TargetingObject.isEmptyTargetting(geoDTO.country)) {
			targetedGeoExpr = "";
			blockExpr = "";
			if (CollectionUtils.isNotEmpty(geoDTO.country.targetList)) {
				validateExtendedBaseModelList(geoDTO.country.targetList, "geoDTO.country.targetList");
				Set<Long> entitySet = new HashSet<>();
				for (BaseModel bm : geoDTO.country.targetList) {
					entitySet.add(bm.id);
				}
				String idString = convertSetOfIntegerIdsToString(entitySet);

				if (idString.trim().length() > 0) {
					TargetingComponent tc = createTargetingComponent(TargetingConstants.COUNTRY, true, idString);

					if (tc != null)
						targetedGeoExpr = Long.toString(tc.getId());
				}
			}
			if (CollectionUtils.isNotEmpty(geoDTO.country.blockedList)) {
				validateExtendedBaseModelList(geoDTO.country.blockedList, "geoDTO.country.blockedList");
				Set<Long> blockedEntitySet = new HashSet<>();
				for (BaseModel bm : geoDTO.country.blockedList) {
					blockedEntitySet.add(bm.id);
				}
				String blockedIdString = convertSetOfIntegerIdsToString(blockedEntitySet);
				if (blockedIdString.trim().length() > 0) {
					TargetingComponent blockedTc = createTargetingComponent(TargetingConstants.COUNTRY, false,
							blockedIdString);
					if (blockedTc != null)
						blockExpr = Long.toString(blockedTc.getId());
				}
			}
			countryTExp = targetedGeoExpr;
			if (StringUtils.isNotEmpty(countryTExp) && StringUtils.isNotEmpty(blockExpr)) {
				countryTExp = countryTExp + "&" + blockExpr;
			} else {
				countryTExp += blockExpr;
			}
		}
		if (!TargetingObject.isEmptyTargetting(geoDTO.state)) {
			targetedGeoExpr = "";
			blockExpr = "";
			if (CollectionUtils.isNotEmpty(geoDTO.state.targetList)) {
				Set<Long> entitySet = new HashSet<>();
				validateExtendedBaseModelList(geoDTO.state.targetList, "geoDTO.state.targetList");
				for (BaseModel bm : geoDTO.state.targetList) {
					entitySet.add(bm.id);
				}
				String idString = convertSetOfIntegerIdsToString(entitySet);

				if (idString.trim().length() > 0) {
					TargetingComponent tc = createTargetingComponent(TargetingConstants.STATE, true, idString);

					if (tc != null)
						targetedGeoExpr = Long.toString(tc.getId());
				}
			}
			if (CollectionUtils.isNotEmpty(geoDTO.state.blockedList)) {
				Set<Long> blockedentitySet = new HashSet<>();
				validateExtendedBaseModelList(geoDTO.state.blockedList, "geoDTO.state.blockedList");

				for (BaseModel bm : geoDTO.state.blockedList) {
					blockedentitySet.add(bm.id);
				}
				String blockedIdString = convertSetOfIntegerIdsToString(blockedentitySet);
				if (blockedIdString.trim().length() > 0) {
					TargetingComponent blockedTc = createTargetingComponent(TargetingConstants.STATE, false,
							blockedIdString);
					if (blockedTc != null)
						blockExpr = Long.toString(blockedTc.getId());
				}
			}
			stateTExp = targetedGeoExpr;
			if (StringUtils.isNotBlank(stateTExp) && StringUtils.isNotBlank(blockExpr)) {
				stateTExp = stateTExp + "&" + blockExpr;
			} else {
				stateTExp += blockExpr;
			}
		}
		if (!TargetingObject.isEmptyTargetting(geoDTO.city)) {
			targetedGeoExpr = "";
			blockExpr = "";
			if (CollectionUtils.isNotEmpty(geoDTO.city.targetList)) {
				Set<Long> entitySet = new HashSet<>();
				validateExtendedBaseModelList(geoDTO.city.targetList, "geoDTO.city.targetList");

				for (BaseModel bm : geoDTO.city.targetList) {
					entitySet.add(bm.id);
				}
				String idString = convertSetOfIntegerIdsToString(entitySet);

				if (idString.trim().length() > 0) {
					TargetingComponent tc = createTargetingComponent(TargetingConstants.CITY, true, idString);

					if (tc != null)
						targetedGeoExpr = Long.toString(tc.getId());
				}
			}
			if (CollectionUtils.isNotEmpty(geoDTO.city.blockedList)) {
				Set<Long> blockedEntitySet = new HashSet<Long>();
				validateExtendedBaseModelList(geoDTO.city.blockedList, "geoDTO.city.blockedList");
				for (BaseModel bm : geoDTO.city.blockedList) {
					blockedEntitySet.add(bm.id);
				}
				String blockedIdString = convertSetOfIntegerIdsToString(blockedEntitySet);
				if (blockedIdString.trim().length() > 0) {
					TargetingComponent blockedTc = createTargetingComponent(TargetingConstants.CITY, false,
							blockedIdString);
					if (blockedTc != null)
						blockExpr = Long.toString(blockedTc.getId());
				}
			}
			cityTExp = targetedGeoExpr;
			if (StringUtils.isNotBlank(cityTExp) && StringUtils.isNotBlank(blockExpr)) {
				cityTExp = cityTExp + "&" + blockExpr;
			} else {
				cityTExp += blockExpr;
			}
			logger.debug("cityTExp : {} ", cityTExp);
		}
		geoTExpr = countryTExp;
		if (StringUtils.isNotBlank(geoTExpr) && StringUtils.isNotBlank(stateTExp)) {
			geoTExpr = geoTExpr + "&" + stateTExp;
		} else {
			geoTExpr += stateTExp;
		}
		if (StringUtils.isNotBlank(geoTExpr) && StringUtils.isNotBlank(cityTExp)) {
			geoTExpr = geoTExpr + "&" + cityTExp;
		} else {
			geoTExpr += cityTExp;
		}
		logger.debug("geoDTO {} ", geoDTO);

		logger.debug("countryTExp {} , stateTExp {} , cityTExp {} , geoTExpr {} ", countryTExp, stateTExp, cityTExp,
				geoTExpr);

		return geoTExpr;
	}

	// Segments Targeting
	public String createSegmentsTargetingExpression(StrategyDTO strategy) {
		return null;
	}

//Segments Targeting
	// create createAudienceTargetingExpression
	@Transactional
	public String createDmpAudienceTargetingExpression(StrategyDTO strategy) throws ValidationException {
		String segmentTExpr = "", targetExpr = "", blockExpr = "";

		AudienceStrDTO segmentsObj = strategy.targetDmpSegments;
		logger.debug("segmentsObj  : {} ", segmentsObj);
		if (!segmentsObj.isCustomSegmentTargeting()) {
			logger.debug("No custom segment targeting specified by user");
			return "";
		}

		boolean validationFlag = validateAudienceDTO(segmentsObj);
		logger.debug("validationFlag  : {} ", validationFlag);
		if (validationFlag) {
			TargetingComponent targetTC = null, blockTC = null;
			List<String> tarSgmtStringList = new ArrayList<String>();
			String blockSgmtString = "";
			if (segmentsObj.targetedSegments != null && segmentsObj.targetedSegments.size() > 0) {
				if (segmentsObj.targetedSegmentsOperator == null || (segmentsObj.targetedSegmentsOperator != null
						&& !(segmentsObj.targetedSegmentsOperator.equalsIgnoreCase("AND"))
						&& !(segmentsObj.targetedSegmentsOperator.equalsIgnoreCase("OR")))) {
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE, new Object[] {
							"targeted_segments_operator", strategy.targetDmpSegments.targetedSegmentsOperator });
				} else {
					String tOp = segmentsObj.targetedSegmentsOperator;
					if (tOp.equalsIgnoreCase("OR")) {
						String tarSgmtString = convertListofIdsToString(segmentsObj.targetedSegments);
						tarSgmtStringList.add(tarSgmtString);
					} else if (tOp.equalsIgnoreCase("AND")) {
						// Create targeting expression
						for (BaseModel bm : segmentsObj.targetedSegments) {
							String tarSgmtString = Long.toString(bm.id);
							tarSgmtStringList.add(tarSgmtString);
						}
					}
				}
			}

			logger.debug("tarSgmtStringList  : {} ", tarSgmtStringList);
			if (segmentsObj.blockedSegments != null && segmentsObj.blockedSegments.size() > 0) {
				blockSgmtString = convertListofIdsToString(segmentsObj.blockedSegments);
			}

			logger.debug("blockSgmtString  : {} ", blockSgmtString);
			if (CollectionUtils.isNotEmpty(tarSgmtStringList)) {
				Integer size = tarSgmtStringList.size();
				for (int i = 0; i < size; i++) {
					String str = tarSgmtStringList.get(i);
					TargetingComponent tc = createTargetingComponent(TargetingConstants.SEGMENTS, true, str);
					targetExpr = targetExpr + Long.toString(tc.getId());
					if (i != size - 1)
						targetExpr = targetExpr + "&";
				}
				logger.debug("targetExpr  : {} ", targetExpr);

			}

			if (!StringUtils.isBlank(blockSgmtString)) {
				blockTC = createTargetingComponent(TargetingConstants.SEGMENTS, false, blockSgmtString);
				blockExpr = Long.toString(blockTC.getId());
			}
			logger.debug("blockExpr  : {} ", blockExpr);
			segmentTExpr = targetExpr;
			if (StringUtils.isNotBlank(segmentTExpr) && StringUtils.isNotBlank(blockExpr)) {
				segmentTExpr = segmentTExpr + "&" + blockExpr;
			} else {
				segmentTExpr += blockExpr;
			}
		}

		logger.debug("Final  segmentTExpr : {} ", segmentTExpr);
		return segmentTExpr;
	}

	public AudienceStrDTO mergeAudienceData(AudienceStrDTO appSegment, AudienceStrDTO webSegment,
			AudienceStrDTO dmpSegment) {
		logger.debug("Merging the APP, WEB and DMP audience : APP : {} | WEB : {} | DMP : {}", appSegment, webSegment,
				dmpSegment);
		AudienceStrDTO targetSegments = new AudienceStrDTO();

		combineAudience(targetSegments, appSegment);
		combineAudience(targetSegments, webSegment);
		combineAudience(targetSegments, dmpSegment);

		logger.debug("Post Merging : {}", targetSegments);
		return targetSegments;
	}

	private void combineAudience(AudienceStrDTO target, AudienceStrDTO source) {
		if (source != null) {
			if (!target.customSegmentTargeting) {
				target.customSegmentTargeting = source.customSegmentTargeting;
			}

			if (target.targetedSegmentsOperator == null) {
				target.targetedSegmentsOperator = source.targetedSegmentsOperator;
			}

			if (target.blockedSegmentsOperator == null) {
				target.blockedSegmentsOperator = source.blockedSegmentsOperator;
			}

			if (CollectionUtils.isNotEmpty(source.targetedSegments)) {
				target.getTargetedSegments().addAll(source.targetedSegments);
			}

			if (CollectionUtils.isNotEmpty(source.blockedSegments)) {
				target.getBlockedSegments().addAll(source.blockedSegments);
			}
		}
	}

	// Segments Targeting
	// create createAudienceTargetingExpression
	@Transactional
	public String createAudienceTargetingExpression(AudienceStrDTO segmentsObj) throws ValidationException {
		String segmentTExpr = "", targetExpr = "", blockExpr = "";

		logger.debug("segmentsObj  : {} ", segmentsObj);
		if (!segmentsObj.isCustomSegmentTargeting()) {
			logger.debug("No custom segment targeting specified by user");
			return "";
		}

		boolean validationFlag = validateAudienceDTO(segmentsObj);
		logger.debug("validationFlag  : {} ", validationFlag);
		if (validationFlag) {
			TargetingComponent targetTC = null, blockTC = null;
			List<String> tarSgmtStringList = new ArrayList<String>();
			String blockSgmtString = "";
			if (segmentsObj.targetedSegments != null && segmentsObj.targetedSegments.size() > 0) {
				if (segmentsObj.targetedSegmentsOperator == null || (segmentsObj.targetedSegmentsOperator != null
						&& !(segmentsObj.targetedSegmentsOperator.equalsIgnoreCase("AND"))
						&& !(segmentsObj.targetedSegmentsOperator.equalsIgnoreCase("OR")))) {
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
							new Object[] { "targeted_segments_operator", segmentsObj.targetedSegmentsOperator });
				} else {
					String tOp = segmentsObj.targetedSegmentsOperator;
					if (tOp.equalsIgnoreCase("OR")) {
						String tarSgmtString = convertListofIdsToString(segmentsObj.targetedSegments);
						tarSgmtStringList.add(tarSgmtString);
					} else if (tOp.equalsIgnoreCase("AND")) {
						// Create targeting expression
						for (BaseModel bm : segmentsObj.targetedSegments) {
							String tarSgmtString = Long.toString(bm.id);
							tarSgmtStringList.add(tarSgmtString);
						}
					}
				}
			}

			logger.debug("tarSgmtStringList  : {} ", tarSgmtStringList);
			if (segmentsObj.blockedSegments != null && segmentsObj.blockedSegments.size() > 0) {
				blockSgmtString = convertListofIdsToString(segmentsObj.blockedSegments);
			}

			logger.debug("blockSgmtString  : {} ", blockSgmtString);
			if (CollectionUtils.isNotEmpty(tarSgmtStringList)) {
				Integer size = tarSgmtStringList.size();
				for (int i = 0; i < size; i++) {
					String str = tarSgmtStringList.get(i);
					TargetingComponent tc = createTargetingComponent(TargetingConstants.SEGMENTS, true, str);
					targetExpr = targetExpr + Long.toString(tc.getId());
					if (i != size - 1)
						targetExpr = targetExpr + "&";
				}
				logger.debug("targetExpr  : {} ", targetExpr);

			}

			if (!StringUtils.isBlank(blockSgmtString)) {
				blockTC = createTargetingComponent(TargetingConstants.SEGMENTS, false, blockSgmtString);
				blockExpr = Long.toString(blockTC.getId());
			}
			logger.debug("blockExpr  : {} ", blockExpr);
			segmentTExpr = targetExpr;
			if (StringUtils.isNotBlank(segmentTExpr) && StringUtils.isNotBlank(blockExpr)) {
				segmentTExpr = segmentTExpr + "&" + blockExpr;
			} else {
				segmentTExpr += blockExpr;
			}
		}

		logger.debug("Final  segmentTExpr : {} ", segmentTExpr);
		return segmentTExpr;
	}

	public boolean validateAudienceDTO(AudienceStrDTO segmentsObj) throws ValidationException {
		boolean customSelection = segmentsObj.isCustomSegmentTargeting();

		if (customSelection && (segmentsObj.blockedSegments == null || segmentsObj.blockedSegments.size() == 0)
				&& (segmentsObj.targetedSegments == null || segmentsObj.targetedSegments.size() == 0)) {
			throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR, new Object[] { "blocked_segments",
					"targeted_segments", "target_segments", "target_specific_segments" });
		}

		if (segmentsObj.targetedSegments != null && segmentsObj.targetedSegments.size() > 0)
			validateBaseModelList(segmentsObj.targetedSegments, "target_segments.targeted_segments");

		if (segmentsObj.blockedSegments != null && segmentsObj.blockedSegments.size() > 0)
			validateBaseModelList(segmentsObj.blockedSegments, "target_segments.blocked_segments");

		if (segmentsObj.blockedSegments != null && segmentsObj.blockedSegments.size() > 0
				&& segmentsObj.targetedSegments != null && segmentsObj.targetedSegments.size() > 0) {
			Map<Long, Long> targetedSegmentsMap = new HashMap<>();
			for (BaseModel sgmt : segmentsObj.targetedSegments) {
				targetedSegmentsMap.put(sgmt.id, sgmt.id);
			}

			for (BaseModel sgmt : segmentsObj.blockedSegments) {
				if (targetedSegmentsMap.containsKey(sgmt.id)) {
					throw new ValidationException(ErrorCode.SAME_SEGMENT_CANT_BE_PRESENT_IN_BOTH_LISTS,
							new Object[] { sgmt.id });
				}
			}
		}

		Set<Long> audienceIds = new HashSet<Long>();
		if (segmentsObj.blockedSegments != null && segmentsObj.blockedSegments.size() > 0) {
			for (BaseModel sgmt : segmentsObj.blockedSegments) {
				audienceIds.add(sgmt.getId());
//				Long id = sgmt.id;
//				AudienceESDTO segment = elasticSearch.searchPojoById(TablesEntity.AUDIENCE, id);
//				if (segment == null || segment.getId() == null) {
//					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Segment", id });
//				}
			}
		}

		if (segmentsObj.targetedSegments != null && segmentsObj.targetedSegments.size() > 0) {
			for (BaseModel sgmt : segmentsObj.targetedSegments) {
				audienceIds.add(sgmt.getId());
//				Long id = sgmt.id;
//				AudienceESDTO segment = elasticSearch.searchPojoById(TablesEntity.AUDIENCE, id);
//				if (segment == null || segment.getId() == null) {
//					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Segment", id });
//				}
			}
		}

		List<AudienceESDTO> audienceList = (List<AudienceESDTO>) elasticSearch.searchPojoByIdList(TablesEntity.AUDIENCE,
				new ArrayList<Long>(audienceIds));
		List<AudienceESDTO> dmpAudienceList = (List<AudienceESDTO>) elasticSearch
				.searchPojoByIdList(TablesEntity.DMP_AUDIENCE, new ArrayList<Long>(audienceIds));

		Set<Long> esAudienceIds = new HashSet<Long>();
		for (AudienceESDTO aud : audienceList) {
			esAudienceIds.add(aud.getId());
		}
		for (AudienceESDTO aud : dmpAudienceList) {
			esAudienceIds.add(aud.getId());
		}
		for (Long id : esAudienceIds) {
			if (!audienceIds.contains(id)) {
				throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Audience not found" });
			}
		}

		return true;
	}

	public TargetSegments createSegmentsTargetingObject(Segments segmentsObj) throws ValidationException {
		TargetSegments segments = new TargetSegments();

		boolean selectAllSegments = segmentsObj.selectAllSegments;
		TargetingObject segmentsTarObj = segmentsObj.segments;
		List<BaseModel> includeList = segmentsTarObj.targetList;
		List<BaseModel> excludeList = segmentsTarObj.blockedList;

		segments.selectAllSegments = selectAllSegments;

		// TODO : convert segments to TargetSegments object for supporting
		// hierarchy in segments in future

		if (selectAllSegments == true) {
			// Consider exclude List primarily
			for (BaseModel sgmt : excludeList) {
				Long id = sgmt.id;
				AudienceDTO segment = elasticSearch.searchPojoById(TablesEntity.AUDIENCE, id);
				if (segment != null && segment.getId() != null) {
					segments.excludedSegments.add(id);

				} else {
					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Segment", id });
				}
			}
		} else {
			// Consider include List primarily
			for (BaseModel sgmt : includeList) {
				Long id = sgmt.id;
				AudienceDTO segment = elasticSearch.searchPojoById(TablesEntity.AUDIENCE, id);
				if (segment != null && segment.getId() != null) {

					segments.includedSegments.add(id);

				} else {
					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Segment", id });
				}
			}
		}

		return segments;
	}

	// Browser Targeting Expression
	@Transactional
	public String createBrowserTargetingExpression(StrategyDTO strategy) throws ValidationException {
		String browserTExpr = "";
		String browserIdString = "";
		TargetingComponent browserTC = new TargetingComponent();
		boolean selectAllBrowsers = strategy.targetBrowsers.selectAllBrowsers;
		TargetingObject browsers = strategy.targetBrowsers.browsers;

		if ((browsers.blockedList == null || browsers.blockedList.size() == 0)
				&& (browsers.targetList == null || browsers.targetList.size() == 0))
			return "";

		if (selectAllBrowsers == true) {
			// Consider exclude list
			List<BaseModel> browsersExcludeList = browsers.blockedList;
			if (browsersExcludeList == null || browsersExcludeList.size() == 0)
				return "";

			validateBrowsersIds(browsersExcludeList);

			for (int i = 0; i < browsersExcludeList.size(); i++) {
				browserIdString += Long.toString(browsersExcludeList.get(i).id);
				if (i != browsersExcludeList.size() - 1)
					browserIdString += ",";
			}
			browserTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId());
		} else {
			// Consider include list
			List<BaseModel> browsersIncludeList = browsers.targetList;
			if (browsersIncludeList == null || browsersIncludeList.size() == 0)
				return "";

			validateBrowsersIds(browsersIncludeList);

			for (int i = 0; i < browsersIncludeList.size(); i++) {
				browserIdString += Long.toString(browsersIncludeList.get(i).id);
				if (i != browsersIncludeList.size() - 1)
					browserIdString += ",";
			}
			browserTC.setTargetingOperatorId(TargetingOperator.IS_EQUAL_TO.getId());
		}

		logger.debug("Target Browsers' string is " + browserIdString);

		browserTC.setTargetingFilterId(TargetingFilter.BROWSER.getId());
		browserTC.setCriteria(browserIdString);
		tcRepository.save(browserTC);
		browserTExpr = Long.toString(browserTC.getId());
		return browserTExpr;
	}

	private void validateBrowsersIds(List<BaseModel> list) throws ValidationException {
		if (CollectionUtils.isNotEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i) == null || list.get(i).id == null) {
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
							new Object[] { "browser.id", list.get(i) });
				} else {
					Long id = list.get(i).id;
					if (id < 0) {
						throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
								new Object[] { "browser.id", id });
					} else {
						BaseModel bm = elasticSearch.searchById(TablesEntity.BROWSER, id);
						if (bm == null || bm.getId() == null) {
							throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "Browser", id });
						}
					}
				}
			}
		}
	}

	// DayPart Targeting
	@Transactional
	public String createDayPartTargetingExpression(StrategyDTO strategy) throws ValidationException {
		String dayPartTExpr = "";

		if (strategy.targetDays == null)
			return dayPartTExpr;

		List<Day> dayParts = strategy.targetDays.getDaypart();

		if (dayParts == null || dayParts.size() == 0)
			return dayPartTExpr;

		Map<Integer, Integer> dayMap = new HashMap<Integer, Integer>();
		Map<Integer, Integer> hoursMap = new HashMap<Integer, Integer>();

		for (int i = 0; i < dayParts.size(); i++) {
			Day day = dayParts.get(i);
			int dayNo = day.getDay();
			if (day == null || day.getDay() == null) {
				throw new ValidationException(ErrorCode.PARSING_INVALID_JSON, "Invalid property daypart");
			}
			if (dayNo < 0 || dayNo > 6) {
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE, new Object[] { "daypart.day", dayNo });
			}

			if (dayMap.containsKey(dayNo))
				continue;

			String hrString = "";
			List<Integer> hours = day.getHours();
			hoursMap.clear();
			for (int j = 0; j < hours.size(); j++) {

				int hr = hours.get(j);
				if (hoursMap.containsKey(hr))
					continue;

				if (hr < 0 || hr > 23) {
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE, new Object[] { "daypart.hours", hr });
				}
				hrString += Long.toString(hr);
				hoursMap.put(hr, hr);
				if (j != hours.size() - 1) {
					hrString += ",";
				}
			}
			TargetingComponent dayTC = new TargetingComponent();
			dayTC.setTargetingFilterId(TargetingFilter.DAY_OF_WEEK.getId()); // 0 =
			// DAY
			// OF
			// WEEK
			dayTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1
			// =
			// IS_ANY_OF
			dayTC.setCriteria(Long.toString(dayNo));
			tcRepository.save(dayTC);

			TargetingComponent hourTC = new TargetingComponent();
			hourTC.setTargetingFilterId(TargetingFilter.HOUR_OF_DAY.getId()); // 0
			// =
			// HOUR
			// OF
			// WEEK
			hourTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1
			// =
			// IS_ANY_OF
			hourTC.setCriteria(hrString);
			tcRepository.save(hourTC);

			dayMap.put(dayNo, dayNo);

			dayPartTExpr += "(" + dayTC.getId() + "&" + hourTC.getId() + ")";
			if (i != dayParts.size() - 1)
				dayPartTExpr += "|";
		}

		logger.debug("DayPart targeting expression is : " + dayPartTExpr);

		return dayPartTExpr;
	}

	// General Targeting Helpers

	@Transactional
	public TargetingComponent createTargetingComponent(String entityType, boolean include, String criteria) {
		TargetingComponent tc = new TargetingComponent();

		tc.setTargetingFilterId(filterTypeEnumMap.get(entityType));

		if (entityType.equals("SEGMENTS")) {
			if (include == true) {
				tc.setTargetingOperatorId(TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId());
			} else {
				tc.setTargetingOperatorId(TargetingOperator.HAS_NO_MATCH.getId());
			}
		} else {
			if (include == true) {
				tc.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId());
			} else {
				tc.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId());
			}
		}

		// Create Targeting Component
		tc.setCriteria(criteria);
		tcRepository.save(tc);
		return tc;
	}

	@Transactional
	public String createConnTypeTargetingExpression(StrategyDTO strategy) {
		String connTypeTExpr = "";
		List<Long> idsList = new ArrayList<>();
		TargetingComponent connTypeTC = new TargetingComponent();
		if (!isAllConnTypeSelected(strategy.getConnectionTypes())) {
			for (ConnectionType ct : strategy.getConnectionTypes()) {
				idsList.add((long) ct.ordinal());

			}
		}
		if (idsList.size() > 0) {
			String idList = convertListofIntegerIdsToString(idsList);
			connTypeTC.setTargetingFilterId(TargetingFilter.CONNECTION_TYPE.getId());
			connTypeTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId());
			connTypeTC.setCriteria(idList);
			connTypeTC = tcRepository.save(connTypeTC);
			connTypeTExpr = Long.toString(connTypeTC.getId());
		}
		return connTypeTExpr;
	}

	private boolean isAllConnTypeSelected(Set<ConnectionType> connTypes) {
		if (connTypes == null || connTypes.size() == 0)
			return true;

		Set<ConnectionType> allConnTypesNewFlow = new HashSet<ConnectionType>();
		allConnTypesNewFlow.add(ConnectionType.WIFI);
		allConnTypesNewFlow.add(ConnectionType.CELLULAR_NETWORK_2G);
		allConnTypesNewFlow.add(ConnectionType.CELLULAR_NETWORK_3G);
		allConnTypesNewFlow.add(ConnectionType.CELLULAR_NETWORK_4G);
		allConnTypesNewFlow.add(ConnectionType.CELLULAR_NETWORK_5G);

		Set<ConnectionType> allConnTypesOldFlow = new HashSet<ConnectionType>();
		allConnTypesOldFlow.add(ConnectionType.WIFI);
		allConnTypesOldFlow.add(ConnectionType.CELLULAR_NETWORK);

		if (connTypes.containsAll(allConnTypesNewFlow) || connTypes.containsAll(allConnTypesOldFlow)) {
			return true;
		} else {
			return false;
		}
	}

	// RTB Targeting
	@Transactional
	public void targetRTBInventory(StrategyDTO strategy, StrategyEntity strategyDO) throws ValidationException {
		double bidP = 100.0;
		BidStrategy bidStr = new BidStrategy();
		Integer bidType = 1;
		bidStr.setTypeId(bidType);
		if (strategy.bidPercentage != 0.0)
			bidP = strategy.bidPercentage;
		bidStr.setParams(Double.toString(bidP));
		bidStrategyRepo.save(bidStr);

		List<Long> idsList = new ArrayList<>();
		TargetingComponent aggTC = new TargetingComponent();
		TargetingComponent sitesTC = new TargetingComponent();
		TargetingComponent targetAppRatingsTC = new TargetingComponent();
		TargetingComponent targetOnlyPublishedAppTC = new TargetingComponent();

		String iosTargetingExpression = "";
		String androidTargetingExpression = "";

		String appRatingTargetingExp = "";
		String onlyPublishedTargetingExp = "";

		logger.debug("strategy.targetAppRatings : {} ", strategy.targetAppRatings);
		;
		if (strategy.targetAppRatings != null && strategy.targetAppRatings != 0) {
			// logger.debug("targetOnlyPublishedApp T:::: ",
			// strategy.targetOnlyPublishedApp.toString());
			targetAppRatingsTC.setTargetingFilterId(TargetingFilter.APP_REVIEW_SCORE.getId()); // 26
			// =
			// APP_REVIEW_SCORE
			targetAppRatingsTC.setTargetingOperatorId(TargetingOperator.IS_GREATER_THAN_OR_EQUAL_TO.getId()); // 8
			// =
			// IS_GREATER_THAN_OR_EQUAL_TO
			targetAppRatingsTC.setCriteria(strategy.targetAppRatings.toString());
			targetAppRatingsTC = tcRepository.save(targetAppRatingsTC);

			TargetingComponent targetRatingForOsTC = new TargetingComponent();
			targetRatingForOsTC.setTargetingFilterId(TargetingFilter.OS.getId());
			targetRatingForOsTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId());
			targetRatingForOsTC.setCriteria("3,4");
			targetRatingForOsTC = tcRepository.save(targetRatingForOsTC);
			appRatingTargetingExp = "(" + targetRatingForOsTC.getId() + "|" + targetAppRatingsTC.getId() + ")";
			logger.debug("targetAppRatingsTC : {}, appRatingTargetingExp {}  ", targetAppRatingsTC,
					appRatingTargetingExp);
			;

		}

		if (strategy.targetOnlyPublishedApp) {
			targetOnlyPublishedAppTC.setTargetingFilterId(TargetingFilter.IS_APP_PUBLISHED.getId()); // 24
			targetOnlyPublishedAppTC.setTargetingOperatorId(TargetingOperator.IS_EQUAL_TO.getId()); // 0
			targetOnlyPublishedAppTC.setCriteria(String.valueOf("1"));
			tcRepository.save(targetOnlyPublishedAppTC);

			TargetingComponent targetOnlyPublishedForOsTC = new TargetingComponent();
			targetOnlyPublishedForOsTC.setTargetingFilterId(TargetingFilter.OS.getId());
			targetOnlyPublishedForOsTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId());
			targetOnlyPublishedForOsTC.setCriteria("3,4");
			tcRepository.save(targetOnlyPublishedForOsTC);
			onlyPublishedTargetingExp = "(" + targetOnlyPublishedForOsTC.getId() + "|"
					+ targetOnlyPublishedAppTC.getId() + ")";

		}

		if (strategy.targetIosCategories != null) {
			iosTargetingExpression = targetAppCategories(strategy, 3, false, null, null); // osId = 3
		}

		if (strategy.targetAndroidCategories != null) {
			androidTargetingExpression = targetAppCategories(strategy, 4, false, null, null);
		}
		if (strategy.rtbAggregators != null) {
			RTBAggregators targetAgg = strategy.rtbAggregators;
			if (targetAgg.selectAllAggregators != true) {
				if ((CollectionUtils.isNotEmpty(targetAgg.aggregators.targetList)
						&& CollectionUtils.isNotEmpty(targetAgg.aggregators.blockedList))
						|| (CollectionUtils.isEmpty(targetAgg.aggregators.targetList)
								&& CollectionUtils.isEmpty(targetAgg.aggregators.blockedList))) {
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
							new Object[] { "rtb_aggregators field" });
					// select_all_aggregators is false and both fields have values.
				} else if (CollectionUtils.isNotEmpty(targetAgg.aggregators.targetList)) {
					idsList.clear();
					List<BaseModel> aggregators = targetAgg.aggregators.targetList;
					for (BaseModel agg : aggregators) {
						if (agg == null || agg.id == null)
							throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
									new Object[] { "included aggregator" });
						BaseModel rtbAgg = elasticSearch.searchById(TablesEntity.AGGREGATOR, agg.getId());
						if (rtbAgg == null || rtbAgg.getId() == null)
							throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
									new Object[] { "RTBAggregator", agg.id });
						idsList.add(rtbAgg.getId());
					}
					String idList = convertListofIntegerIdsToString(idsList);
					aggTC.setTargetingFilterId(TargetingFilter.RTB_AGGREGATOR.getId()); // 11
					// =
					// RTB_Aggregator
					aggTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1
					// =
					// IS_ANY_OF
					aggTC.setCriteria(idList);
					tcRepository.save(aggTC);
				} else if (CollectionUtils.isNotEmpty(targetAgg.aggregators.blockedList)) {
					idsList.clear();
					List<BaseModel> excludedAggregators = targetAgg.aggregators.blockedList;
					for (BaseModel agg : excludedAggregators) {
						if (agg == null || agg.id == null)
							throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
									new Object[] { "excluded aggregator" });
						BaseModel rtbAgg = elasticSearch.searchById(TablesEntity.AGGREGATOR, agg.id);
						if (rtbAgg == null || rtbAgg.getId() == null)
							throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
									new Object[] { "RTBAggregator", agg.id });
						idsList.add(rtbAgg.getId());
					}
					String idList = convertListofIntegerIdsToString(idsList);
					aggTC.setTargetingFilterId(TargetingFilter.RTB_AGGREGATOR.getId()); // 11
					// =
					// RTB_Aggregator
					aggTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId()); // 5
					// =
					// IS_NONE_OF
					aggTC.setCriteria(idList);
					tcRepository.save(aggTC);
				}
			} else {
				// select_all_aggregators is true and any of the fields have values.
				if (CollectionUtils.isNotEmpty(targetAgg.aggregators.targetList)
						|| CollectionUtils.isNotEmpty(targetAgg.aggregators.blockedList)) {
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
							new Object[] { "rtb_aggregators field" });
				}
			}
		}

		// save deal category targeting details
		TargetingComponent dealTC = getTargetingComponentForDealCategory(strategy.getTargetDealCategory());
		if (dealTC != null) {
			tcRepository.save(dealTC);
		}

		TargetingComponent auctionTC = getTargetingComponentForAuctionTypeTargeting(strategy.getAuctionTypeTargeting());
		if (auctionTC != null) {
			tcRepository.save(auctionTC);
		}

		if (strategy.rtbSites != null) {
			RTBSites targetSites = strategy.rtbSites;
			if (targetSites.selectAllSites != true) {
				if (targetSites.rtbSites.targetList != null && targetSites.rtbSites.targetList.size() > 0) {
					idsList.clear();
					List<BaseModel> sites = targetSites.rtbSites.targetList;

					for (BaseModel site : sites) {
						if (site == null || site.id == null)
							throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
									new Object[] { "included site" });
						BaseModel rtbSite = elasticSearch.searchById(TablesEntity.SITE, site.id);
						if (rtbSite == null || rtbSite.getId() == null)
							throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
									new Object[] { "RTBSite", site.id });
						idsList.add(rtbSite.getId());
					}
					String idList = convertListofIntegerIdsToString(idsList);
					sitesTC.setTargetingFilterId(TargetingFilter.RTB_SITE.getId());
					// =
					// RTB_Site
					sitesTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId());
					// =
					// IS_ANY_OF
					sitesTC.setCriteria(idList);
					tcRepository.save(sitesTC);
				} else {
					// TODO : Error handling : Can create RTB LI with target as
					// No Sites
				}
			} else {
				if (targetSites.rtbSites.blockedList != null && targetSites.rtbSites.blockedList.size() > 0) {
					idsList.clear();
					List<BaseModel> excludedSites = targetSites.rtbSites.blockedList;
					for (BaseModel site : excludedSites) {
						if (site == null || site.id == null)
							throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
									new Object[] { "excluded site" });
						BaseModel rtbSite = elasticSearch.searchById(TablesEntity.SITE, site.id);
						if (rtbSite == null || rtbSite.getId() == null)
							throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
									new Object[] { "RTBSite", site.id });
						idsList.add(rtbSite.getId());
					}
					String idList = convertListofIntegerIdsToString(idsList);
					sitesTC.setTargetingFilterId(TargetingFilter.RTB_SITE.getId());

					sitesTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId());

					sitesTC.setCriteria(idList);
					tcRepository.save(sitesTC);

				}
			}
		}
		InventoryObject inventoryTargetingObj = buildTargetingObject(aggTC, sitesTC, iosTargetingExpression,
				androidTargetingExpression, appRatingTargetingExp, onlyPublishedTargetingExp, dealTC, auctionTC);
		String invTExpr = inventoryTargetingObj.getInventoryTargetingExpression();
		InventoryType ivsType = inventoryTargetingObj.getInventoryType();

		if (invTExpr != null && invTExpr.length() > 0) {
			InventorySource ivs = new InventorySource();
			ivs.setStrategyId(strategyDO.getId());
			ivs.setBidStrategyId(bidStr.getId());
			ivs.setTargetingExpression(invTExpr);
			ivs.setName("Default Inventory " + Long.toString(strategyDO.getId()));
			ivs.setType(ivsType);
			inventrySourceRepo.save(ivs);
		} else {
			InventorySource ivs = new InventorySource();
			ivs.setStrategyId(strategyDO.getId());
			ivs.setBidStrategyId(bidStr.getId());
			ivs.setTargetingExpression("");
			ivs.setName("Default Inventory " + Long.toString(strategyDO.getId()));
			ivsType = InventoryType.NONE;
			ivs.setType(ivsType);
			inventrySourceRepo.save(ivs);
		}
	}

	/**
	 * Method to save the advance target (deal target) details in inventory source
	 * table
	 * 
	 * @param advanceTarget
	 * @throws ValidationException
	 */
	public void saveAdvanceTargeting(DealCategoryDTO dealCategoryDTO) throws ValidationException {
		TargetingComponent dealTC = getTargetingComponentForDealCategory(dealCategoryDTO);
		if (dealTC != null) {
			tcRepository.save(dealTC);
		}
	}

	/**
	 * Method to form targeting component object to save deals
	 * 
	 * @param advanceTarget
	 * @throws ValidationException
	 */
	private TargetingComponent getTargetingComponentForDealCategory(DealCategoryDTO dealCategoryDTO)
			throws ValidationException {
		// if dealCategoryDTO is null or if selectAll is true, nothing to be done. Just
		// return.
		if (dealCategoryDTO == null || dealCategoryDTO.isSelectAll()) {
			return null;
		}

		List<Long> idsList = new ArrayList<>();
		TargetingComponent dealTC = new TargetingComponent();

		if (CollectionUtils.isNotEmpty(dealCategoryDTO.getDealCategory().targetList)) {
			idsList.clear();
			List<BaseModel> deals = dealCategoryDTO.getDealCategory().targetList;
			for (BaseModel deal : deals) {
				if (deal == null || deal.getId() == null)
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
							new Object[] { "Invalid deal category" });
				BaseModel esDeal = elasticSearch.searchById(TablesEntity.DEAL_CATEGORY, deal.getId());
				if (esDeal == null || esDeal.getId() == null) {
					throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND, new Object[] { "DEAL", deal.getId() });
				}
				idsList.add(esDeal.getId());
			}
			String idList = convertListofIntegerIdsToString(idsList);
			dealTC.setTargetingFilterId(TargetingFilter.DEAL_CATEGORY.getId());
			dealTC.setTargetingOperatorId(TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId());
			dealTC.setCriteria(idList);

			return dealTC;
		}
		return null;
	}

	private TargetingComponent getTargetingComponentForAuctionTypeTargeting(AuctionType auctionType)
			throws ValidationException {

		if (auctionType == null || auctionType.equals(AuctionType.ALL)) {
			return null;
		}

		TargetingComponent auctionTC = new TargetingComponent();

		if (auctionType.equals(AuctionType.FIRST)) {
			auctionTC.setCriteria("1");
		} else if (auctionType.equals(AuctionType.SECOND)) {
			auctionTC.setCriteria("2");
		}

		auctionTC.setTargetingFilterId(TargetingFilter.AUCTION_TYPE.getId());
		auctionTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId());

		return auctionTC;
	}

	private String targetAppCategories(StrategyDTO strategy, Integer osId, boolean deleteOldTC,
			TargetingComponent oldOsTC, TargetingComponent oldCategoriesTC) throws ValidationException {
		String targetingExpression = null;
		List<Long> idsList = new ArrayList<>();
		TargetingComponent appCategoriesTC = new TargetingComponent();
		TargetingComponent osTC = new TargetingComponent();

		long IS_ANY_OF = TargetingOperator.IS_ANY_OF.getId();
		long IS_NONE_OF = TargetingOperator.IS_NONE_OF.getId();
		long TARGETING_FILTER_APP_CATEGORY_ENUM_ORDER = TargetingFilter.APP_CATEGORY.getId();
		long TARGETING_FILTER_OS_ENUM_ORDER = TargetingFilter.OS.getId();

		long HAS_ATLEAST_ONE_MATCH = TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId();
		long HAS_NOMATCH = TargetingOperator.HAS_NO_MATCH.getId();

		TargetAppCategories targetCategories = new TargetAppCategories();
		if (osId == 3) {
			targetCategories = strategy.targetIosCategories;
		}
		if (osId == 4) {
			targetCategories = strategy.targetAndroidCategories;
		}

		if (targetCategories != null && targetCategories.selectAll != true) {
			if (CollectionUtils.isEmpty(targetCategories.appCategories.targetList)
					&& CollectionUtils.isEmpty(targetCategories.appCategories.blockedList)) {
				throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
						new Object[] { "appCategories include and exclude lists are empty" });
			}
			if (targetCategories.appCategories.targetList != null
					&& targetCategories.appCategories.targetList.size() > 0) {
				idsList.clear();
				List<BaseModel> categories = targetCategories.appCategories.targetList;
				for (BaseModel category : categories) {
					if (category == null || category.id == null)
						throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
								new Object[] { "included category" });
					AppCategoryMaster appCategory = elasticSearch.searchPojoById(TablesEntity.APP_CATEGORY,
							category.getId());
					if (appCategory == null || appCategory.getId() == null)
						throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
								new Object[] { "AppCategoryMaster", appCategory.getId() });
					else if (appCategory.getOsId() != osId) {
						throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
								new Object[] { "appCategories lists contains wrong categories" });
					}
					idsList.add(appCategory.getId());
				}
				String idList = convertListofIntegerIdsToString(idsList);
				appCategoriesTC.setTargetingFilterId(TARGETING_FILTER_APP_CATEGORY_ENUM_ORDER);
				appCategoriesTC.setTargetingOperatorId(HAS_ATLEAST_ONE_MATCH);
				appCategoriesTC.setCriteria(idList);
				tcRepository.save(appCategoriesTC);

				osTC.setTargetingFilterId(TARGETING_FILTER_OS_ENUM_ORDER);
				osTC.setTargetingOperatorId(IS_NONE_OF);

				osTC.setCriteria(osId.toString());
				tcRepository.save(osTC);
			}
			if (targetCategories.appCategories.blockedList != null
					&& targetCategories.appCategories.blockedList.size() > 0) {
				idsList.clear();
				List<BaseModel> excludedCategories = targetCategories.appCategories.blockedList;
				for (BaseModel category : excludedCategories) {
					if (category == null || category.id == null)
						throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
								new Object[] { "excluded category" });
					AppCategoryMaster appCategory = elasticSearch.searchPojoById(TablesEntity.APP_CATEGORY,
							category.id);
					if (appCategory == null || appCategory.getId() == null)
						throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
								new Object[] { "AppCategoryMaster", appCategory.getId() });
					else if (appCategory.getOsId() != osId) {
						throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
								new Object[] { "appCategories lists contains wrong categories" });
					}
					idsList.add(appCategory.getId());
				}
				String idList = convertListofIntegerIdsToString(idsList);

				appCategoriesTC.setTargetingFilterId(TARGETING_FILTER_APP_CATEGORY_ENUM_ORDER);
				appCategoriesTC.setTargetingOperatorId(HAS_NOMATCH);

				appCategoriesTC.setCriteria(idList);
				tcRepository.save(appCategoriesTC);

				osTC.setTargetingFilterId(TARGETING_FILTER_OS_ENUM_ORDER);
				osTC.setTargetingOperatorId(IS_NONE_OF);

				osTC.setCriteria(osId.toString());
				tcRepository.save(osTC);
			}

			if (targetCategories.appCategories.targetList != null
					&& targetCategories.appCategories.targetList.size() == 0
					|| targetCategories.appCategories.blockedList != null
							&& targetCategories.appCategories.blockedList.size() == 0) {
				if (oldCategoriesTC != null && oldCategoriesTC.getId() != null) {
					tcRepository.delete(oldCategoriesTC);
					oldCategoriesTC = null;
				}
			}
		} else if (targetCategories != null && targetCategories.selectAll == true) {
			if (deleteOldTC) {
				if (oldCategoriesTC != null && oldCategoriesTC.getId() != null)
					tcRepository.delete(oldCategoriesTC);
				if (oldOsTC != null && oldOsTC.getId() != null) {
					tcRepository.delete(oldOsTC);
				}
			}

			appCategoriesTC = null;
			oldCategoriesTC = null;
			osTC = null;
			oldOsTC = null;
		}

		if (deleteOldTC) {
			if ((appCategoriesTC == null || appCategoriesTC.getId() == null)
					&& (oldCategoriesTC != null && oldCategoriesTC.getId() != null)) {
				appCategoriesTC = oldCategoriesTC;
			} else {
				if (oldCategoriesTC != null && oldCategoriesTC.getId() != null)
					tcRepository.delete(oldCategoriesTC);
			}

			if ((osTC == null || osTC.getId() == null) && (oldOsTC != null && oldOsTC.getId() != null)) {
				osTC = oldOsTC;
			} else {
				if (oldOsTC != null && oldOsTC.getId() != null) {
					tcRepository.delete(oldOsTC);
				}
			}
		}

		if (osTC != null && osTC.getId() != null && appCategoriesTC != null && appCategoriesTC.getId() != null) {
			targetingExpression = "(" + Long.toString(osTC.getId()) + "|" + Long.toString(appCategoriesTC.getId())
					+ ")";
		}

		return targetingExpression;
	}

	public void updateRTBInventory(StrategyDTO strategy, StrategyEntity strategyDO) throws ValidationException {
		long IS_ANY_OF = TargetingOperator.IS_ANY_OF.getId();
		long IS_NONE_OF = TargetingOperator.IS_NONE_OF.getId();
		long TARGETING_FILTER_APP_CATEGORY_ENUM_ORDER = TargetingFilter.APP_CATEGORY.getId();
		long TARGETING_FILTER_OS_ENUM_ORDER = TargetingFilter.OS.getId();
		long HAS_ATLEAST_ONE_MATCH = TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId();
		long HAS_NOMATCH = TargetingOperator.HAS_NO_MATCH.getId();

		Long strategyId = strategyDO.getId();
		List<InventorySource> ivsList = inventrySourceRepo.findByStrategyId(strategyId);
		InventorySource ivs = null;
		logger.debug("ivsList : {} ", ivsList);
		if (ivsList == null || ivsList.size() == 0) {
			// NO RTB inventory yet, create new RTB inventory, may be update
			// happened from managed to rtb
			targetRTBInventory(strategy, strategyDO);
		} else if (ivsList != null && ivsList.size() > 1) {
			// OLD Strategy with multiple Inventory sources.
			// Dangerous. Will create empty targeting expression - Will delete
			// the expressions first
			deleteOldInventory(ivsList);
			targetRTBInventory(strategy, strategyDO);
		} else {
			ivs = ivsList.get(0);
			String targetingExpression = ivs.getTargetingExpression();
			Long bidStrId = ivs.getBidStrategyId();
			InventoryType oldIvsType = ivs.getType();

			BidStrategy bidStr = bidStrategyRepo.getOne(bidStrId);
			String params = bidStr.getParams().trim();

			if (strategy.bidPercentage != 0.0 && !(params.equals(Double.toString(strategy.bidPercentage).trim()))) {
				bidStr.setParams(Double.toString(strategy.bidPercentage).trim());
				bidStrategyRepo.save(bidStr);

			}

			String tcStrings = utility.trimBracketsAndReturnValidExpression(targetingExpression);
			String[] tcStringExp = {};

			if (tcStrings != null) {
				tcStringExp = tcStrings.split("&");
			}
			// spliting with &
			// String[] tcStringExp = tcStrings.split("&");

			TargetingComponent oldAggTC = null;
			TargetingComponent oldSitesTC = null;

			TargetingComponent oldAndroidOsTC = null;
			TargetingComponent oldAndroidCategoriesTCIn = null;
			TargetingComponent oldAndroidCategoriesTCEx = null;

			TargetingComponent oldIosOsTC = null;
			TargetingComponent oldIosCategoriesTCIn = null;
			TargetingComponent oldIosCategoriesTCEx = null;

			TargetingComponent oldAppRatingsTC = null;
			TargetingComponent oldAppRatingsOSTC = null;

			TargetingComponent oldTargetOnlyPublishedAppTC = null;
			TargetingComponent oldTargetOnlyPublishedAppOSTC = null;

			for (String exp : tcStringExp) {
				Long tCompId1 = null;
				Long tCompId2 = null;
				TargetingComponent tc1 = new TargetingComponent();
				TargetingComponent tc2 = new TargetingComponent();

				// for 4 targeting expression
				// appRatings|onlypublished|iosCategories|androidCategories
				if (exp.startsWith("(") || exp.indexOf("|") > -1) {
					List<String> ids = utility.getListOfTCIdsInExpr(exp);
					if (ids.get(0) != null) {
						tCompId1 = Long.parseLong(ids.get(0));
						tc1 = tcRepository.getOne(tCompId1);
					}
					if (ids.get(0) != null) {
						tCompId2 = Long.parseLong(ids.get(1));
						tc2 = tcRepository.getOne(tCompId2);
					}

					// for android
					if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId() && tc1.getCriteria().indexOf("4") > -1
							&& tc1.getCriteria().indexOf("3") == -1
							&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc2.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
						oldAndroidCategoriesTCIn = tc2;
						oldAndroidOsTC = tc1;
					} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc2.getCriteria().indexOf("4") > -1 && tc2.getCriteria().indexOf("3") == -1
							&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc1.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
						oldAndroidCategoriesTCIn = tc1;
						oldAndroidOsTC = tc2;
					} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc1.getCriteria().indexOf("4") > -1 && tc1.getCriteria().indexOf("3") == -1
							&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc2.getTargetingOperatorId() == HAS_NOMATCH) {
						oldAndroidCategoriesTCEx = tc2;
						oldAndroidOsTC = tc1;
					} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc2.getCriteria().indexOf("4") > -1 && tc2.getCriteria().indexOf("3") == -1
							&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc1.getTargetingOperatorId() == HAS_NOMATCH) {
						oldAndroidCategoriesTCEx = tc1;
						oldAndroidOsTC = tc2;
					}

					// for ios
					if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId() && tc1.getCriteria().indexOf("3") > -1
							&& tc1.getCriteria().indexOf("4") == -1
							&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc2.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
						oldIosCategoriesTCIn = tc2;
						oldIosOsTC = tc1;
					} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") == -1
							&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc1.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
						oldIosCategoriesTCIn = tc1;
						oldIosOsTC = tc2;
					} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") == -1
							&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc2.getTargetingOperatorId() == HAS_NOMATCH) {
						oldIosCategoriesTCEx = tc2;
						oldIosOsTC = tc1;
					} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") == -1
							&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
							&& tc1.getTargetingOperatorId() == HAS_NOMATCH) {
						oldIosCategoriesTCEx = tc1;
						oldIosOsTC = tc2;
					}

					// for android && ios
					if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId() && tc1.getCriteria().indexOf("3") > -1
							&& tc1.getCriteria().indexOf("4") > -1
							&& tc2.getTargetingFilterId() == TargetingFilter.APP_REVIEW_SCORE.getId()) {
						oldAppRatingsTC = tc2;
						oldAppRatingsOSTC = tc1;
					} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") > -1
							&& tc1.getTargetingFilterId() == TargetingFilter.APP_REVIEW_SCORE.getId()) {
						oldAppRatingsTC = tc1;
						oldAppRatingsOSTC = tc2;
					} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") > -1
							&& tc2.getTargetingFilterId() == TargetingFilter.IS_APP_PUBLISHED.getId()) {
						oldTargetOnlyPublishedAppTC = tc2;
						oldTargetOnlyPublishedAppOSTC = tc1;
					} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
							&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") > -1
							&& tc1.getTargetingFilterId() == TargetingFilter.IS_APP_PUBLISHED.getId()) {
						oldTargetOnlyPublishedAppTC = tc1;
						oldTargetOnlyPublishedAppOSTC = tc2;
					}
				} else {
					Long tCompId = Long.parseLong(exp);
					if (tCompId != null) {
						TargetingComponent tc = tcRepository.getOne(tCompId);
						if (tc.getTargetingFilterId() == TargetingFilter.RTB_AGGREGATOR.getId()) {
							oldAggTC = tc;
						} else if (tc.getTargetingFilterId() == TargetingFilter.RTB_SITE.getId()) {
							oldSitesTC = tc;
						}
					}
				}
			}

			if (oldIvsType.equals(InventoryType.NONE)) {
				// This means that either sites were targeted OR sites and
				// aggregators were targeted due to a bug in WebUI 1.0.
				// AP-1697 Fix. So anyways, we need to clean up the old
				// targeting components and create new ones.

				logger.debug("Cleaning up as the fix for Ap-1697");

				if (oldAggTC != null) {
					tcRepository.delete(oldAggTC);
					oldAggTC = null;
				}

				if (oldSitesTC != null) {
					tcRepository.delete(oldSitesTC);
					oldSitesTC = null;
				}

				if (oldAndroidCategoriesTCIn != null) {
					tcRepository.delete(oldAndroidCategoriesTCIn);
					oldAndroidCategoriesTCIn = null;
					if (oldAndroidOsTC != null) {
						tcRepository.delete(oldAndroidOsTC);
						oldAndroidOsTC = null;
					}
				}

				if (oldAndroidCategoriesTCEx != null) {
					tcRepository.delete(oldAndroidCategoriesTCEx);
					oldAndroidCategoriesTCEx = null;
					if (oldAndroidOsTC != null) {
						tcRepository.delete(oldAndroidOsTC);
						oldAndroidOsTC = null;
					}
				}

				if (oldIosCategoriesTCIn != null) {
					tcRepository.delete(oldIosCategoriesTCIn);
					oldIosCategoriesTCIn = null;
					if (oldIosOsTC != null) {
						tcRepository.delete(oldIosOsTC);
						oldIosOsTC = null;
					}
				}

				if (oldIosCategoriesTCEx != null) {
					tcRepository.delete(oldIosCategoriesTCEx);
					oldIosCategoriesTCEx = null;
					if (oldIosOsTC != null) {
						tcRepository.delete(oldIosOsTC);
						oldIosOsTC = null;
					}
				}

				if (oldAppRatingsTC != null) {
					tcRepository.delete(oldAppRatingsTC);
					oldAppRatingsTC = null;
					if (oldAppRatingsOSTC != null) {
						tcRepository.delete(oldAppRatingsOSTC);
						oldAppRatingsOSTC = null;
					}
				}

				if (oldTargetOnlyPublishedAppTC != null) {
					tcRepository.delete(oldTargetOnlyPublishedAppTC);
					oldTargetOnlyPublishedAppTC = null;
					if (oldTargetOnlyPublishedAppOSTC != null) {
						tcRepository.delete(oldTargetOnlyPublishedAppOSTC);
						oldTargetOnlyPublishedAppOSTC = null;
					}
				}
			}

			// Creating new targeting components
			List<Long> idsList = new ArrayList<>();
			TargetingComponent aggTC = new TargetingComponent();
			TargetingComponent sitesTC = new TargetingComponent();

			TargetingComponent targetAppRatingsTC = new TargetingComponent();
			TargetingComponent targetAppRatingsOSTC = new TargetingComponent();
			String targetAppRatingExp = "";

			TargetingComponent targetOnlyPublishedAppTC = new TargetingComponent();
			TargetingComponent targetOnlyPublishedAppOSTC = new TargetingComponent();
			String targetOnlyPublishedExp = "";

			if (strategy.targetAppRatings != null && strategy.targetAppRatings != 0) {
				logger.debug("targetAppRatings :::: ", strategy.targetAppRatings.toString());
				targetAppRatingsTC.setTargetingFilterId(TargetingFilter.APP_REVIEW_SCORE.getId()); // 26
				// =
				// APP_REVIEW_SCORE
				targetAppRatingsTC.setTargetingOperatorId(TargetingOperator.IS_GREATER_THAN_OR_EQUAL_TO.getId()); // 8
				// =
				// IS_GREATER_THAN_OR_EQUAL_TO
				targetAppRatingsTC.setCriteria(strategy.targetAppRatings.toString());
				tcRepository.save(targetAppRatingsTC);

				targetAppRatingsOSTC.setTargetingFilterId(TARGETING_FILTER_OS_ENUM_ORDER);
				targetAppRatingsOSTC.setTargetingOperatorId(IS_NONE_OF);
				targetAppRatingsOSTC.setCriteria("3,4");
				tcRepository.save(targetAppRatingsOSTC);

				targetAppRatingExp = "(" + targetAppRatingsOSTC.getId() + "|" + targetAppRatingsTC.getId() + ")";
			}

			if (strategy.targetOnlyPublishedApp) {
				logger.debug("targetOnlyPublishedApp :::: ", strategy.targetOnlyPublishedApp);
				targetOnlyPublishedAppTC.setTargetingFilterId(TargetingFilter.IS_APP_PUBLISHED.getId()); // 24
				// =
				// IS_APP_PUBLISHED
				targetOnlyPublishedAppTC.setTargetingOperatorId(TargetingOperator.IS_EQUAL_TO.getId()); // 0
				// =
				// IS_EQUAL_TO
				targetOnlyPublishedAppTC.setCriteria("1");
				tcRepository.save(targetOnlyPublishedAppTC);

				targetOnlyPublishedAppOSTC.setTargetingFilterId(TARGETING_FILTER_OS_ENUM_ORDER);
				targetOnlyPublishedAppOSTC.setTargetingOperatorId(IS_NONE_OF);
				targetOnlyPublishedAppOSTC.setCriteria("3,4");
				tcRepository.save(targetOnlyPublishedAppOSTC);

				targetOnlyPublishedExp = "(" + targetOnlyPublishedAppOSTC.getId() + "|"
						+ targetOnlyPublishedAppTC.getId() + ")";
			}
//			else {
//				// handle scenario where we are not targeting published apps
//				targetOnlyPublishedAppTC.setTargetingFilterId(TargetingFilter.IS_APP_PUBLISHED.getId()); 
//				targetOnlyPublishedAppTC.setTargetingOperatorId(TargetingOperator.IS_EQUAL_TO.getId()); 
//				targetOnlyPublishedAppTC.setCriteria("0");
//				tcRepository.save(targetOnlyPublishedAppTC);
//
//				targetOnlyPublishedAppOSTC.setTargetingFilterId(TARGETING_FILTER_OS_ENUM_ORDER);
//				targetOnlyPublishedAppOSTC.setTargetingOperatorId(IS_NONE_OF);
//				targetOnlyPublishedAppOSTC.setCriteria("3,4");
//				tcRepository.save(targetOnlyPublishedAppOSTC);
//
//				targetOnlyPublishedExp = "(" + targetOnlyPublishedAppOSTC.getId() + "|"
//						+ targetOnlyPublishedAppTC.getId() + ")";
//			}

			String androidTagetingExp = (oldAndroidCategoriesTCIn != null)
					? targetAppCategories(strategy, 4, true, oldAndroidOsTC, oldAndroidCategoriesTCIn)
					: targetAppCategories(strategy, 4, true, oldAndroidOsTC, oldAndroidCategoriesTCEx);

			String iosTagetingExp = oldIosCategoriesTCIn != null
					? targetAppCategories(strategy, 3, true, oldIosOsTC, oldIosCategoriesTCIn)
					: targetAppCategories(strategy, 3, true, oldIosOsTC, oldIosCategoriesTCEx);

			if (strategy.rtbAggregators != null) {
				RTBAggregators targetAgg = strategy.rtbAggregators;
				if (targetAgg.selectAllAggregators != true) {
					if (targetAgg.aggregators.targetList != null && targetAgg.aggregators.targetList.size() > 0) {
						idsList.clear();
						List<BaseModel> aggregators = targetAgg.aggregators.targetList;
						for (BaseModel agg : aggregators) {
							if (agg == null || agg.id == null)
								throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
										new Object[] { "included aggregator" });
							BaseModel rtbAgg = elasticSearch.searchById(TablesEntity.AGGREGATOR, agg.id);
							if (rtbAgg == null || rtbAgg.getId() == null)
								throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
										new Object[] { "RTBAggregator", agg.id });
							idsList.add(rtbAgg.getId());
						}
						String idList = convertListofIntegerIdsToString(idsList);
						aggTC.setTargetingFilterId(TargetingFilter.RTB_AGGREGATOR.getId());// 11
						// =
						aggTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId()); // 1
						// IS_ANY_OF
						aggTC.setCriteria(idList);
						tcRepository.save(aggTC);
					} else if (targetAgg.aggregators.blockedList != null
							&& targetAgg.aggregators.blockedList.size() > 0) {
						idsList.clear();
						List<BaseModel> excludedAggregators = targetAgg.aggregators.blockedList;
						for (BaseModel agg : excludedAggregators) {
							if (agg == null || agg.id == null)
								throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
										new Object[] { "excluded aggregator" });
							BaseModel rtbAgg = elasticSearch.searchById(TablesEntity.AGGREGATOR, agg.id);
							if (rtbAgg == null || rtbAgg.getId() == null)
								throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
										new Object[] { "RTBAggregator", agg.id });
							idsList.add(rtbAgg.getId());
						}
						String idList = convertListofIntegerIdsToString(idsList);
						aggTC.setTargetingFilterId(TargetingFilter.RTB_AGGREGATOR.getId()); // 11
						// =
						aggTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId()); // 5
						aggTC.setCriteria(idList);
						tcRepository.save(aggTC);
					} else if (targetAgg.aggregators.targetList != null
							&& targetAgg.aggregators.targetList.size() == 0) {
						// AP-1649 Fix. User wants to remove aggregator
						// targeting

						if (oldAggTC != null && oldAggTC.getId() != null) {
							tcRepository.delete(oldAggTC);
							oldAggTC = null;
						}

						aggTC = null;
					} else if (targetAgg.aggregators.blockedList != null
							&& targetAgg.aggregators.blockedList.size() == 0) {
						// AP-1649 Fix. User wants to remove aggregator
						// targeting

						if (oldAggTC != null && oldAggTC.getId() != null) {
							tcRepository.delete(oldAggTC);
							oldAggTC = null;
						}
						aggTC = null;
					}
				} else {
					// select_all_aggregators is true and any of the fields have values.
					if (targetAgg.aggregators.targetList.size() > 0 || targetAgg.aggregators.blockedList.size() > 0) {
						throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
								new Object[] { "rtb_aggregators field" });
					}

					// All aggregators targeted. No need to create an
					// aggregator targeting component
					if (oldAggTC != null && oldAggTC.getId() != null) {
						tcRepository.delete(oldAggTC);
						oldAggTC = null;
					}
				}
			}

			// save advance targeting details
			// saveAdvanceTargeting(strategy.getTargetAdvance());
			TargetingComponent dealTC = getTargetingComponentForDealCategory(strategy.getTargetDealCategory());
			if (dealTC != null) {
				tcRepository.save(dealTC);
			}

			TargetingComponent auctionTC = getTargetingComponentForAuctionTypeTargeting(
					strategy.getAuctionTypeTargeting());
			if (auctionTC != null) {
				tcRepository.save(auctionTC);
			}

			if (strategy.rtbSites != null) {
				RTBSites targetSites = strategy.rtbSites;
				if (targetSites.selectAllSites != true) {
					if (targetSites.rtbSites.targetList != null && targetSites.rtbSites.targetList.size() > 0) {
						idsList.clear();
						List<BaseModel> sites = targetSites.rtbSites.targetList;
						for (BaseModel site : sites) {
							if (site == null || site.id == null)
								throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
										new Object[] { "included site" });
							BaseModel rtbSite = elasticSearch.searchById(TablesEntity.SITE, site.getId());
							if (rtbSite == null || rtbSite.getId() == null)
								throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
										new Object[] { "RTBSite", site.id });

							idsList.add(rtbSite.getId());
						}
						String idList = convertListofIntegerIdsToString(idsList);
						sitesTC.setTargetingFilterId(TargetingFilter.RTB_SITE.getId()); // 11
						// =
						// RTB_Site
						sitesTC.setTargetingOperatorId(TargetingOperator.IS_ANY_OF.getId());// 1
						// =
						// IS_ANY_OF
						sitesTC.setCriteria(idList);
						tcRepository.save(sitesTC);
					} else if (targetSites.rtbSites.targetList != null && targetSites.rtbSites.targetList.size() == 0) {
						// AP-1649 Fix. User wants to remove site targeting

						if (oldSitesTC != null && oldSitesTC.getId() != null) {
							tcRepository.delete(oldSitesTC);
							oldSitesTC = null;
						}

						sitesTC = null;
					} else {
						// TODO invalid request. show error
					}
				} else {
					if (targetSites.rtbSites.blockedList != null && targetSites.rtbSites.blockedList.size() > 0) {
						idsList.clear();
						List<BaseModel> excludedSites = targetSites.rtbSites.blockedList;
						for (BaseModel site : excludedSites) {
							if (site == null || site.id == null)
								throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
										new Object[] { "excluded site" });
							BaseModel rtbSite = elasticSearch.searchById(TablesEntity.SITE, site.getId());
							if (rtbSite == null || rtbSite.getId() == null)
								throw new ValidationException(ErrorCode.ENTITY_NOT_FOUND,
										new Object[] { "RTBSite", site.id });

							idsList.add(rtbSite.getId());
						}
						String idList = convertListofIntegerIdsToString(idsList);
						sitesTC.setTargetingFilterId(TargetingFilter.RTB_SITE.getId());// 11
						// =
						// RTB_Site
						sitesTC.setTargetingOperatorId(TargetingOperator.IS_NONE_OF.getId()); // 1
						// =
						// IS_ANY_OF
						sitesTC.setCriteria(idList);
						tcRepository.save(sitesTC);
					} else if (targetSites.rtbSites.blockedList != null
							&& targetSites.rtbSites.blockedList.size() == 0) {
						// AP-1649 Fix. User wants to remove site targeting

						if (oldSitesTC != null && oldSitesTC.getId() != null) {
							tcRepository.delete(oldSitesTC);
							oldSitesTC = null;
						}

						sitesTC = null;
					} else {
						// All sites targeted. No need to create a site
						// targeting component
						if (oldSitesTC != null && oldSitesTC.getId() != null) {
							tcRepository.delete(oldSitesTC);
							oldSitesTC = null;
						}
					}
				}
			}

			if (aggTC == null || aggTC.getId() == null) {
				aggTC = oldAggTC;
			} else {
				if (oldAggTC != null && oldAggTC.getId() != null)
					tcRepository.delete(oldAggTC);
			}

			if (sitesTC == null || sitesTC.getId() == null) {
				sitesTC = oldSitesTC;
			} else {
				if (oldSitesTC != null && oldSitesTC.getId() != null)
					tcRepository.delete(oldSitesTC);
			}

			if (strategy.targetAppRatings != null && strategy.targetAppRatings == 0) {
				if (oldAppRatingsTC != null && oldAppRatingsTC.getId() != null)
					tcRepository.delete(oldAppRatingsTC);
				if (oldAppRatingsOSTC != null && oldAppRatingsOSTC.getId() != null)
					tcRepository.delete(oldAppRatingsOSTC);
			} else if (targetAppRatingsTC == null || targetAppRatingsTC.getId() == null) {
				if (oldAppRatingsOSTC != null && oldAppRatingsOSTC.getId() != null)
					targetAppRatingExp = "(" + oldAppRatingsOSTC.getId() + "|" + oldAppRatingsTC.getId() + ")";
			}

//			if (strategy.targetOnlyPublishedApp) {
			if (oldTargetOnlyPublishedAppTC != null && oldTargetOnlyPublishedAppTC.getId() != null)
				tcRepository.delete(oldTargetOnlyPublishedAppTC);
			if (oldTargetOnlyPublishedAppOSTC != null && oldTargetOnlyPublishedAppOSTC.getId() != null)
				tcRepository.delete(oldTargetOnlyPublishedAppOSTC);
//			} 
//			else if (targetOnlyPublishedAppTC == null || targetOnlyPublishedAppTC.getId() == null) {
//				if (oldTargetOnlyPublishedAppOSTC != null && oldTargetOnlyPublishedAppOSTC.getId() != null)
//					targetOnlyPublishedExp = "(" + oldTargetOnlyPublishedAppOSTC.getId() + "|"
//							+ oldTargetOnlyPublishedAppTC.getId() + ")";
//			}

			InventoryObject inventoryTargetingObj = buildTargetingObject(aggTC, sitesTC, androidTagetingExp,
					iosTagetingExp, targetAppRatingExp, targetOnlyPublishedExp, dealTC, auctionTC);
			String invTExpr = inventoryTargetingObj.getInventoryTargetingExpression();
			io.revx.core.enums.InventoryType ivsType = inventoryTargetingObj.getInventoryType();

			if (invTExpr.equals("") || invTExpr.equals("()")) {
				// strategyDao.delete(ivs);
				ivs.setStrategyId(strategyDO.getId());
				ivs.setBidStrategyId(bidStr.getId());
				ivs.setTargetingExpression("");
				ivs.setName("Default Inventory " + Long.toString(strategyDO.getId()));
				ivsType = InventoryType.NONE;
				ivs.setType(ivsType);
				inventrySourceRepo.save(ivs);
			} else {
				ivs.setStrategyId(strategyDO.getId());
				ivs.setBidStrategyId(bidStr.getId());
				ivs.setTargetingExpression(invTExpr);
				ivs.setName("Default Inventory " + Long.toString(strategyDO.getId()));
				ivs.setType(ivsType);
				inventrySourceRepo.save(ivs);
			}
		}
	}

	private InventoryObject buildTargetingObject(TargetingComponent aggTC, TargetingComponent sitesTC,
			String iosTargetingExp, String androidTargetingExp, String appRatingTargetExp,
			String onlyPublishedTargetExp, TargetingComponent dealTC, TargetingComponent auctionTC) {

		InventoryObject inventoryObj = new InventoryObject();

		String invTExpr = "";
		InventoryType ivsType = null;

		if ((aggTC != null && aggTC.getId() != null) && (sitesTC == null || sitesTC.getId() == null)
				&& (dealTC == null || dealTC.getId() == null) && (iosTargetingExp == null)
				&& (androidTargetingExp == null) && (appRatingTargetExp == null) && (onlyPublishedTargetExp == null)
				&& (auctionTC == null || auctionTC.getId() == null)) {
			invTExpr = Long.toString(aggTC.getId());
			ivsType = InventoryType.AGGREGATOR;
		} else if ((aggTC == null || aggTC.getId() == null) && (sitesTC != null && sitesTC.getId() != null)
				&& (dealTC == null || dealTC.getId() == null) && (iosTargetingExp == null)
				&& (androidTargetingExp == null) && (appRatingTargetExp == null) && (onlyPublishedTargetExp == null)
				&& (auctionTC == null || auctionTC.getId() == null)) {
			ivsType = InventoryType.SITE;
			invTExpr = Long.toString(sitesTC.getId());
		} else if ((aggTC == null || aggTC.getId() == null) && (sitesTC == null || sitesTC.getId() == null)
				&& (dealTC == null || dealTC.getId() == null) && (iosTargetingExp != null && iosTargetingExp != null)
				&& (androidTargetingExp == null || androidTargetingExp == null) && (appRatingTargetExp == null)
				&& (onlyPublishedTargetExp == null) && (auctionTC == null || auctionTC.getId() == null)) {
			ivsType = InventoryType.APP_CATEGORY;
			invTExpr = iosTargetingExp;
		} else if ((aggTC == null || aggTC.getId() == null) && (sitesTC == null || sitesTC.getId() == null)
				&& (dealTC == null || dealTC.getId() == null) && (iosTargetingExp == null)
				&& (androidTargetingExp != null) && (appRatingTargetExp == null) && (onlyPublishedTargetExp == null)
				&& (auctionTC == null || auctionTC.getId() == null)) {
			ivsType = InventoryType.APP_CATEGORY;
			invTExpr = androidTargetingExp;
		} else if ((aggTC == null || aggTC.getId() == null) && (sitesTC == null || sitesTC.getId() == null)
				&& (dealTC == null || dealTC.getId() == null) && (iosTargetingExp == null)
				&& (androidTargetingExp == null) && (appRatingTargetExp != null) && (onlyPublishedTargetExp == null)
				&& (auctionTC == null || auctionTC.getId() == null)) {
			ivsType = InventoryType.APP_RATING;
			invTExpr = appRatingTargetExp;
		} else if ((aggTC == null || aggTC.getId() == null) && (sitesTC == null || sitesTC.getId() == null)
				&& (dealTC == null || dealTC.getId() == null) && (iosTargetingExp == null)
				&& (androidTargetingExp == null) && (appRatingTargetExp == null) && (onlyPublishedTargetExp != null)
				&& (auctionTC == null || auctionTC.getId() == null)) {
			ivsType = InventoryType.IS_APP_PUBLISHED;
			invTExpr = onlyPublishedTargetExp;
		} else if ((aggTC == null || aggTC.getId() == null) && (sitesTC == null || sitesTC.getId() == null)
				&& (dealTC != null && dealTC.getId() != null) && (iosTargetingExp == null)
				&& (androidTargetingExp == null) && (appRatingTargetExp == null) && (onlyPublishedTargetExp == null)
				&& (auctionTC == null || auctionTC.getId() == null)) {
			ivsType = InventoryType.DEAL_CATEGORY;
			invTExpr = Long.toString(dealTC.getId());
		} else if ((aggTC == null || aggTC.getId() == null) && (sitesTC == null || sitesTC.getId() == null)
				&& (dealTC == null || dealTC.getId() == null) && (iosTargetingExp == null)
				&& (androidTargetingExp == null) && (appRatingTargetExp == null) && (onlyPublishedTargetExp == null)
				&& (auctionTC != null && auctionTC.getId() != null)) {
			ivsType = InventoryType.AUCTION_TYPE;
			invTExpr = Long.toString(auctionTC.getId());
			// need to add another if condition for auctionType targeting
		}

		if (invTExpr.equals("")) {
			invTExpr = "(";
			if ((aggTC != null && aggTC.getId() != null)) {
				ivsType = InventoryType.AGGREGATOR;
				invTExpr += Long.toString(aggTC.getId()) + "&";
			}

			if ((sitesTC != null && sitesTC.getId() != null)) {
				ivsType = InventoryType.SITE;
				invTExpr += Long.toString(sitesTC.getId()) + "&";
			}

			if (androidTargetingExp != null && androidTargetingExp != "") {
				ivsType = InventoryType.APP_CATEGORY;
				invTExpr += androidTargetingExp + "&";
			}

			if (iosTargetingExp != null && iosTargetingExp != "") {
				ivsType = InventoryType.APP_CATEGORY;
				invTExpr += iosTargetingExp + "&";
			}

			if (appRatingTargetExp != null && appRatingTargetExp != "") {
				ivsType = InventoryType.APP_RATING;
				invTExpr += appRatingTargetExp + "&";
			}

			if (onlyPublishedTargetExp != null && onlyPublishedTargetExp != "") {
				ivsType = InventoryType.IS_APP_PUBLISHED;
				invTExpr += onlyPublishedTargetExp + "&";
			}

			/**
			 * This is the pattern for saving inventory source (<aggregator>&<site>&<android
			 * app category>&<ios app category>&<deal category>)
			 */
			if (dealTC != null && dealTC.getId() != null) {
				ivsType = InventoryType.DEAL_CATEGORY;
				invTExpr += Long.toString(dealTC.getId()) + "&";
			}
			if (auctionTC != null && auctionTC.getId() != null) {
				ivsType = InventoryType.AUCTION_TYPE;
				invTExpr += Long.toString(auctionTC.getId()) + "&";
			}

			// remove the last & if exist
			String lastCharacter = invTExpr.substring(invTExpr.length() - 1);
			if (lastCharacter.equals("&")) {
				invTExpr = invTExpr.substring(0, invTExpr.length() - 1);
			}

			invTExpr += ")";

		}

		logger.debug("invTExpr : " + invTExpr);
		if (invTExpr.equals("()")) {
			invTExpr = "";
		}
		inventoryObj.setInventoryTargetingExpression(invTExpr);
		inventoryObj.setInventoryType(ivsType);

		return inventoryObj;
	}

	private void deleteOldInventory(List<InventorySource> ivsList) {
		// TODO
		Iterator<InventorySource> iter = ivsList.iterator();
		while (iter.hasNext()) {
			InventorySource ivs = iter.next();

			// Delete Targeting Components
			String targetExpr = ivs.getTargetingExpression();
			List<String> tcIds = utility.getListOfTCIdsInExpr(targetExpr);
			utility.deleteOldTargetingComponents(tcIds);

			inventrySourceRepo.delete(ivs);

			// Delete BidStrategy object
			bidStrategyRepo.deleteById(ivs.getBidStrategyId());
			iter.remove();
		}
	}

	public void populateRTBTargetingParametersInDTO(StrategyDTO dto) {
		Long strategyId = dto.getId();
		long HAS_ATLEAST_ONE_MATCH = TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId();
		long HAS_NO_MATCH = TargetingOperator.HAS_NO_MATCH.getId();

		List<InventorySource> ivsList = inventrySourceRepo.findByStrategyId(strategyId);

		logger.debug("ivsList : {} ", ivsList);

		if (ivsList == null || ivsList.size() == 0) {
			RTBAggregators targetAggregators = new RTBAggregators();
			targetAggregators.selectAllAggregators = true;

			RTBSites target_sites = new RTBSites();
			target_sites.selectAllSites = true;

			TargetAppCategories targetAndroidCategories = new TargetAppCategories();
			targetAndroidCategories.setSelectAll(true);

			TargetAppCategories targetIosCategories = new TargetAppCategories();
			targetIosCategories.setSelectAll(true);

			DealCategoryDTO dealCategoryDTO = new DealCategoryDTO();
			dealCategoryDTO.setSelectAll(true);

			dto.rtbAggregators = targetAggregators;
			dto.rtbSites = target_sites;
			dto.targetAndroidCategories = targetAndroidCategories;
			dto.targetIosCategories = targetIosCategories;
			dto.targetAppRatings = null;
			dto.targetOnlyPublishedApp = false;
			dto.setTargetDealCategory(dealCategoryDTO);
			dto.setAuctionTypeTargeting(AuctionType.ALL);
			return;
		}

		if (ivsList.size() > 1) {
			// TODO Old LI with more than 1 inventory source. Handle it
			logger.debug("LI with more than one inventory sources. Not supported in UI 2.0");
		} else {
			TargetingComponent aggTC = null;
			TargetingComponent sitesTC = null;
			TargetingComponent dealTC = null;
			TargetingComponent auctionTC = null;

			TargetingComponent androidCategoriesTCIn = null;
			TargetingComponent androidCategoriesTCEx = null;
			TargetingComponent androidOsTC = null;

			TargetingComponent iosCategoriesTCIn = null;
			TargetingComponent iosCategoriesTCEx = null;
			TargetingComponent iosOsTC = null;

			TargetingComponent targetAppRatingTC = null;
			TargetingComponent targetIsAppPublished = null;

			InventorySource ivs = ivsList.get(0);
			logger.debug(" ivs : {} ", ivs);
			String ivsExpr = ivs.getTargetingExpression();
			String targetingExpression = null;
			logger.debug(" ivsExpr : {} ", ivsExpr);

			targetingExpression = ivsExpr.trim();
			Long bidId = ivs.getBidStrategyId();
			InventoryType ivsType = ivs.getType();
			// logger.debug("RTB targeting expression : " + targetingExpression + " ;
			// Inventory type : " +
			// ivsType.name());

			logger.debug(" ivsType : {} ", ivsType);

			if (ivsType == InventoryType.NONE) {
				if (ivsExpr == null || ivsExpr.trim().length() == 0 || ivsExpr.equals("()")) {
					// Inventory source with everything targeted
				} else {

					String tcStrings = utility.trimBracketsAndReturnValidExpression(targetingExpression);
					// split with &
					String[] tcStringExp = tcStrings.split("&");

					for (String exp : tcStringExp) {
						Long tCompId1 = null;
						Long tCompId2 = null;
						TargetingComponent tc1 = new TargetingComponent();
						TargetingComponent tc2 = new TargetingComponent();

						// for 4 targeting expression
						// appRatings|onlypublished|iosCategories|androidCategories
						if (exp.startsWith("(") || exp.indexOf("|") > -1) {
							List<String> ids = utility.getListOfTCIdsInExpr(exp);
							if (ids.get(0) != null) {
								tCompId1 = Long.parseLong(ids.get(0));
								tc1 = tcRepository.getOne(tCompId1);
							}
							if (ids.get(0) != null) {
								tCompId2 = Long.parseLong(ids.get(1));
								tc2 = tcRepository.getOne(tCompId2);
							}

							logger.debug(" 1 : ivsType :{} , tc1 : {} , tc2: {}  ", ivsType, tc1, tc2);
							// for android
							if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc1.getCriteria().indexOf("4") > -1 && tc1.getCriteria().indexOf("3") == -1
									&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc2.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
								androidCategoriesTCIn = tc2;
								androidOsTC = tc1;
							} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc2.getCriteria().indexOf("4") > -1 && tc2.getCriteria().indexOf("3") == -1
									&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc1.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
								androidCategoriesTCIn = tc1;
								androidOsTC = tc2;
							} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc1.getCriteria().indexOf("4") > -1 && tc1.getCriteria().indexOf("3") == -1
									&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc2.getTargetingOperatorId() == HAS_NO_MATCH) {
								androidCategoriesTCEx = tc2;
								androidOsTC = tc1;
							} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc2.getCriteria().indexOf("4") > -1 && tc2.getCriteria().indexOf("3") == -1
									&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc1.getTargetingOperatorId() == HAS_NO_MATCH) {
								androidCategoriesTCEx = tc1;
								androidOsTC = tc2;
							}

							// for ios
							if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") == -1
									&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc2.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
								iosCategoriesTCIn = tc2;
								iosOsTC = tc1;
							} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") == -1
									&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc1.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
								iosCategoriesTCIn = tc1;
								iosOsTC = tc2;
							} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") == -1
									&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc2.getTargetingOperatorId() == HAS_NO_MATCH) {
								iosCategoriesTCEx = tc2;
								iosOsTC = tc1;
							} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") == -1
									&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
									&& tc1.getTargetingOperatorId() == HAS_NO_MATCH) {
								iosCategoriesTCEx = tc1;
								iosOsTC = tc2;
							}

							// for android && ios
							if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") > -1
									&& tc2.getTargetingFilterId() == TargetingFilter.APP_REVIEW_SCORE.getId()) {
								targetAppRatingTC = tc2;
							} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") > -1
									&& tc1.getTargetingFilterId() == TargetingFilter.APP_REVIEW_SCORE.getId()) {
								targetAppRatingTC = tc1;
							} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") > -1
									&& tc2.getTargetingFilterId() == TargetingFilter.IS_APP_PUBLISHED.getId()) {
								targetIsAppPublished = tc2;
							} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
									&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") > -1
									&& tc1.getTargetingFilterId() == TargetingFilter.IS_APP_PUBLISHED.getId()) {
								targetIsAppPublished = tc1;
							}
						} else {
							Long tCompId = Long.parseLong(exp);
							if (tCompId != null) {
								TargetingComponent tc = tcRepository.getOne(tCompId);
								if (tc.getTargetingFilterId() == TargetingFilter.RTB_AGGREGATOR.getId()) {
									aggTC = tc;
								} else if (tc.getTargetingFilterId() == TargetingFilter.RTB_SITE.getId()) {
									sitesTC = tc;
								}
							}
						}
					}
				}
			} else if (ivsType == InventoryType.AGGREGATOR || ivsType == InventoryType.SITE
					|| ivsType == InventoryType.DEAL_CATEGORY || ivsType == InventoryType.APP_CATEGORY
					|| ivsType == InventoryType.APP_RATING || ivsType == InventoryType.IS_APP_PUBLISHED
					|| ivsType == InventoryType.AUCTION_TYPE) {

				String tcStrings = utility.trimBracketsAndReturnValidExpression(targetingExpression);
				// split with &
				logger.debug("ivsType , {} ,  tcStrings : {} ", ivsType, tcStrings);

				String[] tcStringExp = {};

				if (tcStrings != null) {
					tcStringExp = tcStrings.split("&");
				}
				// tcStringExp = tcStrings.split("&");

				for (String exp : tcStringExp) {
					Long tCompId1 = null;
					Long tCompId2 = null;
					TargetingComponent tc1 = new TargetingComponent();
					TargetingComponent tc2 = new TargetingComponent();

					logger.debug("exp  {} ", exp);

					// for 4 targeting expression
					// appRatings|onlypublished|iosCategories|androidCategories
					if (exp.startsWith("(") || exp.indexOf("|") > -1) {
						List<String> ids = utility.getListOfTCIdsInExpr(exp);
						if (ids.get(0) != null) {
							tCompId1 = Long.parseLong(ids.get(0));
							tc1 = tcRepository.getOne(tCompId1);
						}
						if (ids.get(1) != null) {
							tCompId2 = Long.parseLong(ids.get(1));
							tc2 = tcRepository.getOne(tCompId2);
						}

						logger.debug(" 2 : ivsType : {} , tc1 : {} , tc2: {}  ", ivsType, tc1, tc2);

						// for android
						if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc1.getCriteria().indexOf("4") > -1 && tc1.getCriteria().indexOf("3") == -1
								&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc2.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
							androidCategoriesTCIn = tc2;
							androidOsTC = tc1;
						} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc2.getCriteria().indexOf("4") > -1 && tc2.getCriteria().indexOf("3") == -1
								&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc1.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
							androidCategoriesTCIn = tc1;
							androidOsTC = tc2;
						} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc1.getCriteria().indexOf("4") > -1 && tc1.getCriteria().indexOf("3") == -1
								&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc2.getTargetingOperatorId() == HAS_NO_MATCH) {
							androidCategoriesTCEx = tc2;
							androidOsTC = tc1;
						} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc2.getCriteria().indexOf("4") > -1 && tc2.getCriteria().indexOf("3") == -1
								&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc1.getTargetingOperatorId() == HAS_NO_MATCH) {
							androidCategoriesTCEx = tc1;
							androidOsTC = tc2;
						}

						// for ios
						if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") == -1
								&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc2.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
							iosCategoriesTCIn = tc2;
							iosOsTC = tc1;
						} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") == -1
								&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc1.getTargetingOperatorId() == HAS_ATLEAST_ONE_MATCH) {
							iosCategoriesTCIn = tc1;
							iosOsTC = tc2;
						} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") == -1
								&& tc2.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc2.getTargetingOperatorId() == HAS_NO_MATCH) {
							iosCategoriesTCEx = tc2;
							iosOsTC = tc1;
						} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") == -1
								&& tc1.getTargetingFilterId() == TargetingFilter.APP_CATEGORY.getId()
								&& tc1.getTargetingOperatorId() == HAS_NO_MATCH) {
							iosCategoriesTCEx = tc1;
							iosOsTC = tc2;
						}

						// for android && ios
						if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") > -1
								&& tc2.getTargetingFilterId() == TargetingFilter.APP_REVIEW_SCORE.getId()) {
							targetAppRatingTC = tc2;
						} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") > -1
								&& tc1.getTargetingFilterId() == TargetingFilter.APP_REVIEW_SCORE.getId()) {
							targetAppRatingTC = tc1;
						} else if (tc1.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc1.getCriteria().indexOf("3") > -1 && tc1.getCriteria().indexOf("4") > -1
								&& tc2.getTargetingFilterId() == TargetingFilter.IS_APP_PUBLISHED.getId()) {
							targetIsAppPublished = tc2;
						} else if (tc2.getTargetingFilterId() == TargetingFilter.OS.getId()
								&& tc2.getCriteria().indexOf("3") > -1 && tc2.getCriteria().indexOf("4") > -1
								&& tc1.getTargetingFilterId() == TargetingFilter.IS_APP_PUBLISHED.getId()) {
							targetIsAppPublished = tc1;
						}
					} else {
						Long tCompId = Long.parseLong(exp);
						if (tCompId != null) {
							TargetingComponent tc = tcRepository.getOne(tCompId);

							logger.debug(" 3 : ivsType :{} , tc1 : {} ", ivsType, tc);
							if (tc.getTargetingFilterId() == TargetingFilter.RTB_AGGREGATOR.getId()) {
								aggTC = tc;
							} else if (tc.getTargetingFilterId() == TargetingFilter.RTB_SITE.getId()) {
								sitesTC = tc;
							} else if (tc.getTargetingFilterId() == TargetingFilter.DEAL_CATEGORY.getId()) {
								dealTC = tc;
							} else if (tc.getTargetingFilterId() == TargetingFilter.AUCTION_TYPE.getId()) {
								auctionTC = tc;

							}
						}
					}
				}

			} else {
				logger.debug("InventoryType is neither NONE nor AGGREGATOR");
			}

			// populate bid percentage
			BidStrategy bidStrategy = bidStrategyRepo.getOne(bidId);
			String bidParams = bidStrategy.getParams();
			dto.bidPercentage = Float.parseFloat(bidParams);

			if (targetAppRatingTC != null) {
				String criteria = targetAppRatingTC.getCriteria();
				logger.debug("targetAppRatingTC criteria : ", criteria);
				dto.targetAppRatings = Integer.parseInt(criteria);
			}

			if (targetIsAppPublished != null) {
				String criteria = targetIsAppPublished.getCriteria();
				logger.debug("targetIsAppPublished criteria : ", criteria);
				dto.targetOnlyPublishedApp = Integer.parseInt(criteria) > 0 ? true : false;
			}

			TargetAppCategories targetAndroidCategories = new TargetAppCategories();
			TargetAppCategories targetIosCategories = new TargetAppCategories();

			logger.debug(" androidCategoriesTCIn   {} ", androidCategoriesTCIn);

			if (androidCategoriesTCIn != null) {
				try {
					String criteria = androidCategoriesTCIn.getCriteria();
					targetAndroidCategories.selectAll = false;
					List<Long> categoryIds = getListOfIdsFromString(criteria);
					for (Long id : categoryIds) {
						AppCategoryMaster categoryMaster = elasticSearch.searchPojoById(TablesEntity.APP_CATEGORY, id);
						BaseModel category = new BaseModel(categoryMaster.getId(), categoryMaster.getName());
						targetAndroidCategories.appCategories.targetList.add(category);
					}
					dto.targetAndroidCategories = targetAndroidCategories;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Cannot get android categories name id map from singleton cache class.");
				}
			}

			logger.debug(" androidCategoriesTCEx   {} ", androidCategoriesTCEx);

			if (androidCategoriesTCEx != null) {
				try {
					String criteria = androidCategoriesTCEx.getCriteria();

					targetAndroidCategories.selectAll = false;
					List<Long> categoryIds = getListOfIdsFromString(criteria);
					for (Long id : categoryIds) {
						AppCategoryMaster categoryMaster = elasticSearch.searchPojoById(TablesEntity.APP_CATEGORY, id);

						BaseModel category = new BaseModel(categoryMaster.getId(), categoryMaster.getName());
						targetAndroidCategories.appCategories.blockedList.add(category);
					}
					dto.targetAndroidCategories = targetAndroidCategories;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Cannot get app categories name id map from singleton cache class.");
				}

			}

			if (androidCategoriesTCEx == null && androidCategoriesTCIn == null) {
				TargetAppCategories target_android_categories = new TargetAppCategories();
				target_android_categories.selectAll = true;
				dto.targetAndroidCategories = target_android_categories;
			}

			logger.debug(" iosCategoriesTCIn   {} ", iosCategoriesTCIn);

			if (iosCategoriesTCIn != null) {
				try {
					String criteria = iosCategoriesTCIn.getCriteria();
					targetIosCategories.selectAll = false;
					List<Long> categoryIds = getListOfIdsFromString(criteria);
					for (Long id : categoryIds) {
						AppCategoryMaster categoryMaster = elasticSearch.searchPojoById(TablesEntity.APP_CATEGORY, id);
						BaseModel category = new BaseModel(categoryMaster.getId(), categoryMaster.getName());
						targetIosCategories.appCategories.targetList.add(category);
					}
					dto.targetIosCategories = targetIosCategories;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Cannot get android categories name id map from singleton cache class.");
				}

			}
			logger.debug(" iosCategoriesTCEx   {} ", iosCategoriesTCEx);

			if (iosCategoriesTCEx != null) {
				try {
					String criteria = iosCategoriesTCEx.getCriteria();

					targetIosCategories.selectAll = false;
					List<Long> categoryIds = getListOfIdsFromString(criteria);
					for (Long id : categoryIds) {
						AppCategoryMaster categoryMaster = elasticSearch.searchPojoById(TablesEntity.APP_CATEGORY, id);
						BaseModel category = new BaseModel(categoryMaster.getId(), categoryMaster.getName());
						targetIosCategories.appCategories.blockedList.add(category);
					}
					logger.debug(" targetIosCategories   {} ", targetIosCategories);
					dto.targetIosCategories = targetIosCategories;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Cannot get ios categories name id map from singleton cache class.");
				}

			}

			if (iosCategoriesTCIn == null && iosCategoriesTCEx == null) {
				TargetAppCategories target_ios_categories = new TargetAppCategories();
				target_ios_categories.selectAll = true;
				dto.targetIosCategories = target_ios_categories;
			}

			if (aggTC != null) {
				// populate RTB Aggregators
				try {
					RTBAggregators rtbAggregators = new RTBAggregators();
					Long toId = aggTC.getTargetingOperatorId();
					String criteria = aggTC.getCriteria();

					if (toId == 2) {
						rtbAggregators.selectAllAggregators = false;
						List<Long> aggIds = getListOfIdsFromString(criteria);
						for (Long id : aggIds) {
							BaseModel agg = elasticSearch.searchById(TablesEntity.AGGREGATOR, id);
							rtbAggregators.aggregators.targetList.add(agg);
						}
					} else if (toId == 6) {
						rtbAggregators.selectAllAggregators = false;
						List<Long> aggIds = getListOfIdsFromString(criteria);
						for (Long id : aggIds) {
							BaseModel agg = elasticSearch.searchById(TablesEntity.AGGREGATOR, id);
							rtbAggregators.aggregators.blockedList.add(agg);
						}
					}
					dto.rtbAggregators = rtbAggregators;
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Cannot get aggregator name id map from singleton cache class.");
				}

			} else {
				RTBAggregators target_aggregators = new RTBAggregators();
				target_aggregators.selectAllAggregators = true;
				dto.rtbAggregators = target_aggregators;
			}

			if (sitesTC != null) {
				// populates RTB Sites
				RTBSites rtbSites = new RTBSites();
				Long toId = sitesTC.getTargetingOperatorId();
				String criteria = sitesTC.getCriteria();

				if (toId == TargetingOperator.IS_ANY_OF.getId()) {
					rtbSites.selectAllSites = false;
					List<Long> siteIds = getListOfIdsFromString(criteria);
					for (Long id : siteIds) {
						if (id != -1) {
							BaseModel rtbSite = elasticSearch.searchById(TablesEntity.SITE, id);
							if (rtbSite != null) {
								if (rtbSite != null)
									rtbSites.rtbSites.targetList.add(rtbSite);
							} else {
								// REVX-2060 NPE in getById fix
								logger.error("This is not expected. RTBSite not found in DB for rtbsite id = " + id);
							}
						}
					}
				} else if (toId == TargetingOperator.IS_NONE_OF.getId()) {
					rtbSites.selectAllSites = true;
					List<Long> siteIds = getListOfIdsFromString(criteria);
					for (Long id : siteIds) {
						if (id != -1) {
							BaseModel rtbSite = elasticSearch.searchById(TablesEntity.SITE, id);
							if (rtbSite != null)
								rtbSites.rtbSites.blockedList.add(rtbSite);
						}
					}
				}

				dto.rtbSites = rtbSites;
			} else {
				RTBSites target_sites = new RTBSites();
				target_sites.selectAllSites = true;
				dto.rtbSites = target_sites;
			}

			// populate DealCategoryDTO
			populateDealCategoryParametersInDTO(dealTC, dto);
			populateAuctionTypeParametersInDTO(auctionTC, dto);

		}
	}

	// Managed Targeting

	public void targetManagedInventory(StrategyDTO strategy, StrategyEntity strategyDO) {
		// TODO: removing Managed
	}

	public void updateManagedInventory(StrategyDTO strategy, StrategyEntity strategyDO) {
		// TODO: removing Managed
	}

	// populate dealCategory in dto
	public void populateDealCategoryParametersInDTO(TargetingComponent dealTC, StrategyDTO dto) {

		if (dealTC != null) {
			try {
				DealCategoryDTO dealCategoryDTO = new DealCategoryDTO();
				Long toId = dealTC.getTargetingOperatorId();
				String criteria = dealTC.getCriteria();

				if (toId == TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId()) {
					dealCategoryDTO.selectAll = false;
					List<Long> dealIds = getListOfIdsFromString(criteria);
					for (Long id : dealIds) {
						BaseModel deal = elasticSearch.searchById(TablesEntity.DEAL_CATEGORY, id);
						if (deal != null) {
							dealCategoryDTO.getDealCategory().targetList.add(deal);
						}
					}
				}
				dto.setTargetDealCategory(dealCategoryDTO);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Cannot get deal category.");
			}

		} else {
			DealCategoryDTO dealCategoryDTO = new DealCategoryDTO();
			dealCategoryDTO.selectAll = true;
			dto.setTargetDealCategory(dealCategoryDTO);
		}
	}

	// populate auction type in dto
	public void populateAuctionTypeParametersInDTO(TargetingComponent auctionTC, StrategyDTO dto) {
		if (auctionTC != null) {
			// Long toId = auctionTC.getTargetingOperatorId();
			String criteria = auctionTC.getCriteria();
			if (criteria.equals("1")) {
				dto.setAuctionTypeTargeting(AuctionType.FIRST);
			} else if (criteria.equals("2")) {
				dto.setAuctionTypeTargeting(AuctionType.SECOND);
			} else {
				dto.setAuctionTypeTargeting(AuctionType.ALL);
			}
		} else if (auctionTC == null) {
			dto.setAuctionTypeTargeting(AuctionType.ALL);
		}

	}

	// Response creation helper methods

	public void populateTargetingParametersInDTO(StrategyDTO dto, String expr) throws Exception {
		if (expr == null || expr.equals("")) {
			populateDTOWithTargetAllParams(dto);
			return;
		}
		StrategyTargetingExpression exprObj = breakExpressionIntoComponents(expr);

		// populate browser parameters
		String browserExpr = exprObj.browserExpr;
		logger.debug("Browser expression is : " + browserExpr);
		if (browserExpr != null && browserExpr.length() > 0)
			convertSimpleExpressionIntoDTOResponse(dto, browserExpr, TargetingConstants.BROWSERS);
		else {
			dto.targetBrowsers = new TargetBrowsers();
			dto.targetBrowsers.selectAllBrowsers = true;
		}

		// populate segment parameters
		String segmentExpr = exprObj.segmentExpr;
		logger.debug("Segment expression is : " + segmentExpr);
		if (segmentExpr != null && segmentExpr.length() > 0)
			convertAudienceExpressionIntoDTOResponse(dto, segmentExpr);
		else {

			dto.targetAppSegments = new AudienceStrDTO();
			dto.targetWebSegments = new AudienceStrDTO();
			dto.targetDmpSegments = new AudienceStrDTO();
			dto.targetAppSegments.customSegmentTargeting = false;
			dto.targetWebSegments.customSegmentTargeting = false;
			dto.targetDmpSegments.customSegmentTargeting = false;
		}

		// populate geo parameters
		// geoExpr can be like this (880979&880982)
		String geoExpr = exprObj.geoExpr;
		logger.debug("Geo expression is : " + geoExpr);

		if (geoExpr != null && geoExpr.length() > 0) {
			TargetGeoDTO targetGeo = new TargetGeoDTO();
			targetGeo.customGeoTargeting = true;
			List<TargetingComponent> targetedTcList = new ArrayList<TargetingComponent>();
			List<TargetingComponent> blockedTcList = new ArrayList<TargetingComponent>();

			logger.debug("Before targetGeo {} ", targetGeo);
			String[] ids = utility.trimSorroundingBrackets(geoExpr).split("&");

			logger.debug("Getting TCs Of Geo expression is : " + geoExpr);
			for (int i = 0; i < ids.length; i++) {
				String idStr = ids[i].trim();
				Long tcid = Long.parseLong(idStr);
				TargetingComponent tc = tcRepository.getOne(tcid);
				logger.debug("Getting  tc {}  : ", tc);
				Long toId = tc.getTargetingOperatorId();
				if (toId == TargetingOperator.IS_ANY_OF.getId())
					targetedTcList.add(tc);
				else
					blockedTcList.add(tc);
			}

			String geoEntityType = "";

			logger.debug("targetedTcList : {} , blockedTcList {}  ", targetedTcList, blockedTcList);
			for (TargetingComponent tc : targetedTcList) {
				// There should be only one entry in targeted list
				// TargetingComponent tc = targetedTcList.get(0);
				Long tfId = tc.getTargetingFilterId();
				logger.debug("Targeted tfId : {}  ", tfId);

				if (tfId == TargetingFilter.GEO_CITY.getId()) {
					geoEntityType = ApiConstant.CITY;
					List<BaseModel> tList = createGeoExtendedBaseModelListFromTCList(tc);
					targetGeo.city.targetList.addAll(tList);
				} else if (tfId == TargetingFilter.GEO_REGION.getId()) {
					geoEntityType = ApiConstant.REGION;
					List<BaseModel> tList = createGeoExtendedBaseModelListFromTCList(tc);
					targetGeo.state.targetList.addAll(tList);
				} else if (tfId == TargetingFilter.GEO_COUNTRY.getId()) {
					geoEntityType = ApiConstant.COUNTRY;
					List<BaseModel> tList = createGeoExtendedBaseModelListFromTCList(tc);
					targetGeo.country.targetList.addAll(tList);
				}
			}

			for (TargetingComponent tc : blockedTcList) {
				// There should be only one entry in targeted list
				// TargetingComponent tc = blockedTcList.get(0);
				long tfId = tc.getTargetingFilterId();
				logger.debug("Blocked tfId : {}  ", tfId);
				if (tfId == TargetingFilter.GEO_CITY.getId()) {
					geoEntityType = ApiConstant.CITY;
					List<BaseModel> tList = createGeoExtendedBaseModelListFromTCList(tc);
					targetGeo.city.blockedList.addAll(tList);
				} else if (tfId == TargetingFilter.GEO_REGION.getId()) {
					geoEntityType = ApiConstant.REGION;
					List<BaseModel> tList = createGeoExtendedBaseModelListFromTCList(tc);
					targetGeo.state.blockedList.addAll(tList);
				} else if (tfId == TargetingFilter.GEO_COUNTRY.getId()) {
					geoEntityType = ApiConstant.COUNTRY;
					List<BaseModel> tList = createGeoExtendedBaseModelListFromTCList(tc);
					targetGeo.country.blockedList.addAll(tList);
				}
			}

			dto.targetGeographies = targetGeo;
			logger.debug("Before targetGeo {} ", targetGeo);
			logger.debug("Now  dto {} ", dto);

		} else {
			dto.targetGeographies = new TargetGeoDTO();
			dto.targetGeographies.customGeoTargeting = false;
		}

		// populate daypart parameters
		String dayPartExpr = exprObj.dayPartExpr;
		logger.debug("DayPart expression is : " + dayPartExpr);
		if (dayPartExpr != null && dayPartExpr.length() > 0) {
			boolean isTree = isATreeExpression(dayPartExpr);
			if (isTree) {
				populateDayPartTreeParametersInResponse(dayPartExpr, dto);
			} else {
				logger.debug("dayPartExpr = " + dayPartExpr);
				convertSimpleExpressionIntoDTOResponse(dto, dayPartExpr, TargetingConstants.DAYPART);
			}
		} else {
			dto.targetDays = new DayPart();
			dto.targetDays.constructDayPartWithFullWeekTargeted();
		}

		// populate connectiontype parameters
		String connTypeExpr = exprObj.connTypeExpr;
		logger.debug("ConnectionType expression is : " + connTypeExpr);
		if (StringUtils.isNotBlank(connTypeExpr)) {
			connTypeExpr = connTypeExpr.substring(1, connTypeExpr.length() - 1);
			TargetingComponent tc = tcRepository.getOne(Long.parseLong(connTypeExpr));
			String targetCriteria = tc.getCriteria();
			for (ConnectionType type : ConnectionType.values()) {
				if (targetCriteria.contains(String.valueOf(type.ordinal())))
					dto.addConnectionType(type);
			}
		} else {
			for (ConnectionType type : ConnectionType.values()) {
				dto.addConnectionType(type);
			}
		}
		// }
	}

	private void populateDTOWithTargetAllParams(StrategyDTO dto) {
		dto.targetBrowsers = new TargetBrowsers();
		dto.targetBrowsers.selectAllBrowsers = true;

		dto.targetAppSegments = new AudienceStrDTO();
		dto.targetAppSegments.customSegmentTargeting = false;

		// Switching to new Geo format
		dto.targetGeographies = new TargetGeoDTO();
		dto.targetGeographies.customGeoTargeting = false;

		dto.targetDays = new DayPart();
		dto.targetDays.constructDayPartWithFullWeekTargeted();
	}

	private StrategyTargetingExpression breakExpressionIntoComponents(String expr) {
		StrategyTargetingExpression tarExprObj = new StrategyTargetingExpression();

		logger.debug(" breakExpressionIntoComponents expr = {} ", expr);

		List<String> exprList = breakExpressionsIntoComponentExpressions(expr);

		for (int i = 0; i < exprList.size(); i++) {
			String subExpr = exprList.get(i);
			logger.debug("Sub-Expression " + i + " is : " + subExpr);

			boolean isTree = isATreeExpression(subExpr);
			if (isTree) {
				// Currently only GEO, DAYPART and SEGMENTS targeting components can be of tree
				// format

				String idStr = utility.findFirstIdInExpression(subExpr);
				String exprType = utility.findExpressionType(idStr);
				logger.debug("Exp Type  {} , subExpr {}  , idStr=  {}", exprType, subExpr, idStr);

				if (exprType == null || exprType.equals("")) {
					continue;
				}
				// logger.debug("Expression Type for expr : " + idStr + " is : "
				// + exprType);
				if (exprType.equals(TargetingConstants.GEOGRAPHIES)) {
					// GEOGRAPHIES tree
					tarExprObj.setGeoExpr(subExpr);
					logger.debug("Geographies expression is : " + subExpr);

				} else if (exprType.equals(TargetingConstants.DAYPART)) {
					// DAYPART tree
					tarExprObj.setDayPartExpr(subExpr);
					logger.debug("DayPart expression is : " + subExpr);
				} else if (exprType.equals(TargetingConstants.SEGMENTS)) {
					// SEGMENT tree
					tarExprObj.setSegmentExpr(subExpr);
					logger.debug("Segment expression is : " + subExpr);
				} else if (exprType.equals(TargetingConstants.CONNECTIONTYPE)) {
					// SEGMENT tree
					tarExprObj.setconnTypeExpr(subExpr);
					logger.debug("ConnectionType expression is : " + subExpr);
				} else {
					// some other tree
					logger.debug("Expression : " + subExpr + " with first id : " + idStr + " is of type " + exprType);
				}
			} else {
				logger.debug("tcRepository is {}  ", tcRepository);

				String exprType = utility.findExpressionType(subExpr);
				logger.debug("Expression Type for expr : " + subExpr + " is : " + exprType);
				if (exprType == null) {
					// TODO Exception handling
				} else {
					if (exprType.equals(TargetingConstants.GEOGRAPHIES))
						tarExprObj.setGeoExpr(subExpr);
					else if (exprType.equals(TargetingConstants.SEGMENTS))
						tarExprObj.setSegmentExpr(subExpr);
					else if (exprType.equals(TargetingConstants.DAYPART))
						tarExprObj.setDayPartExpr(subExpr);
					else if (exprType.equals(TargetingConstants.CONNECTIONTYPE))
						tarExprObj.setconnTypeExpr(subExpr);
					else if (exprType.equals(TargetingConstants.BROWSERS))
						tarExprObj.setBrowserExpr(subExpr);
					// else if (exprType.equals(OPERATINGSYSTEM))
					// tarExprObj.setOsExpr(subExpr);
					else if (exprType.equals(TargetingConstants.MOBILEDEVICEMODELS))
						tarExprObj.setMobileModelsExpr(subExpr);
				}
			}
		}

		return tarExprObj;
	}

	private void convertAudienceExpressionIntoDTOResponse(StrategyDTO strategy, String exprs) {
		AudienceStrDTO targetAppSegments = new AudienceStrDTO();
		AudienceStrDTO targetWebSegments = new AudienceStrDTO();
		AudienceStrDTO targetDmpSegments = new AudienceStrDTO();

		List<TargetingComponent> targetList = new ArrayList<TargetingComponent>();
		List<TargetingComponent> blockList = new ArrayList<TargetingComponent>();

		String[] ids = utility.trimSorroundingBrackets(exprs).split("&");

		for (int i = 0; i < ids.length; i++) {
			String idStr = ids[i].trim();
			long tcid = Long.parseLong(idStr);
			TargetingComponent tc = tcRepository.getOne(tcid);
			long toId = tc.getTargetingOperatorId();
			if (toId == TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId())
				targetList.add(tc);
			else
				blockList.add(tc);
		}

		String tartgetOperator = "AND";
		String blockOperator = "OR";
		if (targetList.size() == 1) {
			tartgetOperator = "OR";
		}

		if (targetList.size() > 0) {
			for (TargetingComponent tc : targetList) {
				populateAudienceDTOFromTargetingComponent(tc, true, targetAppSegments, targetWebSegments,
						targetDmpSegments);
			}
		}

		if (blockList.size() > 0) {
			for (TargetingComponent tc : blockList) {
				populateAudienceDTOFromTargetingComponent(tc, false, targetAppSegments, targetWebSegments,
						targetDmpSegments);
			}
		}
		setTargetBlockOperator(true, tartgetOperator, targetAppSegments, targetWebSegments, targetDmpSegments);
		setTargetBlockOperator(false, blockOperator, targetAppSegments, targetWebSegments, targetDmpSegments);

		strategy.setTargetAppSegments(targetAppSegments);
		strategy.setTargetWebSegments(targetWebSegments);
		strategy.setTargetDmpSegments(targetDmpSegments);
	}

	private void setTargetBlockOperator(boolean isTarget, String operator, AudienceStrDTO targetAppSegments,
			AudienceStrDTO targetWebSegments, AudienceStrDTO targetDmpSegments) {

		if (isTarget) {
			targetAppSegments.setTargetedSegmentsOperator(operator);
			targetWebSegments.setTargetedSegmentsOperator(operator);
			targetDmpSegments.setTargetedSegmentsOperator(operator);
		} else {
			targetAppSegments.setBlockedSegmentsOperator(operator);
			targetWebSegments.setBlockedSegmentsOperator(operator);
			targetDmpSegments.setBlockedSegmentsOperator(operator);
		}

	}

	private void populateAudienceDTOFromTargetingComponent(TargetingComponent tc, boolean isTarget,
			AudienceStrDTO targetAppSegments, AudienceStrDTO targetWebSegments, AudienceStrDTO targetDmpSegments) {

		String criteria = tc.getCriteria();
		if (!criteria.isEmpty()) {
			String[] audList = criteria.split(",");
			for (String aud : audList) {
				AudienceESDTO esAudience = (AudienceESDTO) elasticSearch.searchPojoById(TablesEntity.AUDIENCE,
						Long.parseLong(aud));
				if (esAudience != null) {
					String audienceType = esAudience.getUser_data_type();
					BaseModel audienceModel = new BaseModel(esAudience.getId(), esAudience.getName());
					if (audienceType.equalsIgnoreCase(AudienceType.MOBILE_APP.getAudienceType())) {
						addAudienceToList(isTarget, audienceModel, targetAppSegments, tc);
					} else if (audienceType.equalsIgnoreCase(AudienceType.WEB_BROWSING.getAudienceType())) {
						addAudienceToList(isTarget, audienceModel, targetWebSegments, tc);
					}
				} else {
					AudienceESDTO esDmpAudience = (AudienceESDTO) elasticSearch
							.searchPojoById(TablesEntity.DMP_AUDIENCE, Long.parseLong(aud));
					if (esDmpAudience != null) {
						BaseModel audienceModel = new BaseModel(esDmpAudience.getId(), esDmpAudience.getName());
						addAudienceToList(isTarget, audienceModel, targetDmpSegments, tc);
					}
				}
			}
		}
	}

	private void addAudienceToList(boolean isTarget, BaseModel audienceModel, AudienceStrDTO segment,
			TargetingComponent tc) {
		if (!segment.isCustomSegmentTargeting()) {
			segment.setCustomSegmentTargeting(true);
		}

		Long operator = tc.getTargetingOperatorId();
		if (isTarget) {
			segment.getTargetedSegments().add(audienceModel);
			// segment.setTargetedSegmentsOperator(operator == 7 ? "AND" : "OR");
		} else {
			segment.getBlockedSegments().add(audienceModel);
			// segment.setBlockedSegmentsOperator(operator == 8 ? "OR" : "AND");
		}

	}

	private List<BaseModel> createGeoExtendedBaseModelListFromTCList(TargetingComponent tc) throws Exception {
		List<BaseModel> ebmList = new ArrayList<BaseModel>();
		try {
			String criteria = tc.getCriteria();
			long tfId = tc.getTargetingFilterId();
			logger.debug(" In Here tfId {} , tc {}  ", tfId, tc);
			if (tfId == TargetingFilter.GEO_CITY.getId()) {
				// CITY
				String[] idsArray = criteria.split(",");
				for (int i = 0; i < idsArray.length; i++) {
					if (idsArray[i].trim().equals(""))
						continue;
					Long id = Long.parseLong(idsArray[i].trim());
					BaseModel cm = elasticSearch.searchPojoById(TablesEntity.CITY, id);
					ebmList.add(cm);
				}
			} else if (tfId == TargetingFilter.GEO_REGION.getId()) {
				// STATE
				String[] idsArray = criteria.split(",");
				for (int i = 0; i < idsArray.length; i++) {
					if (idsArray[i].trim().equals(""))
						continue;
					Long id = Long.parseLong(idsArray[i].trim());
					BaseModel sm = elasticSearch.searchPojoById(TablesEntity.STATE, id);
					ebmList.add(sm);
				}
			} else if (tfId == TargetingFilter.GEO_COUNTRY.getId()) {
				// COUNTRY
				String[] idsArray = criteria.split(",");
				for (int i = 0; i < idsArray.length; i++) {
					if (idsArray[i].trim().equals(""))
						continue;
					Long id = Long.parseLong(idsArray[i].trim());
					BaseModel cm = elasticSearch.searchPojoById(TablesEntity.COUNTRY, id);
					ebmList.add(cm);
				}
			}

			logger.debug(" ebmList {} ", ebmList);
			return ebmList;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	private List<BaseModel> createGeoExtendedBaseModelListFromTCList(List<TargetingComponent> tcList) throws Exception {
		List<BaseModel> ebmList = new ArrayList<BaseModel>();
		try {
			for (TargetingComponent tc : tcList) {
				String criteria = tc.getCriteria();
				long tfId = tc.getTargetingFilterId();
				logger.debug(" In Here tfId {} , tc {}  ", tfId, tc);
				if (tfId == TargetingFilter.GEO_CITY.getId()) {
					// CITY
					String[] idsArray = criteria.split(",");
					for (int i = 0; i < idsArray.length; i++) {
						if (idsArray[i].trim().equals(""))
							continue;
						Long id = Long.parseLong(idsArray[i].trim());
						BaseModel cm = elasticSearch.searchPojoById(TablesEntity.CITY, id);
						ebmList.add(cm);
					}
				} else if (tfId == TargetingFilter.GEO_REGION.getId()) {
					// STATE
					String[] idsArray = criteria.split(",");
					for (int i = 0; i < idsArray.length; i++) {
						if (idsArray[i].trim().equals(""))
							continue;
						Long id = Long.parseLong(idsArray[i].trim());
						BaseModel sm = elasticSearch.searchPojoById(TablesEntity.STATE, id);
						ebmList.add(sm);
					}
				} else if (tfId == TargetingFilter.GEO_COUNTRY.getId()) {
					// COUNTRY
					String[] idsArray = criteria.split(",");
					for (int i = 0; i < idsArray.length; i++) {
						if (idsArray[i].trim().equals(""))
							continue;
						Long id = Long.parseLong(idsArray[i].trim());
						BaseModel cm = elasticSearch.searchPojoById(TablesEntity.COUNTRY, id);
						ebmList.add(cm);
					}
				}
			}
			logger.debug(" ebmList {} ", ebmList);
			return ebmList;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	private void convertSimpleExpressionIntoDTOResponse(StrategyDTO strategy, String expr, String exprType) {
		TargetingComponent tc = getTargetingComponentFromId(utility.trimSorroundingBrackets(expr));
		String criteria = tc.getCriteria();
		long toId = tc.getTargetingOperatorId();
		long tfId = tc.getTargetingFilterId();
		boolean include = false;
		logger.debug("convertSimpleExpressionIntoDTOResponse  strategy {} ", strategy);
		if (exprType.equals(TargetingConstants.SEGMENTS)) {
			if (toId == TargetingOperator.HAS_ATLEAST_ONE_MATCH.getId())
				include = true;
			else if (toId == TargetingOperator.HAS_NO_MATCH.getId())
				include = false;
		} else {
			if (toId == TargetingOperator.IS_ANY_OF.getId())
				include = true;
			else if (toId == TargetingOperator.IS_NONE_OF.getId())
				include = false;
		}

		if (exprType.equals(TargetingConstants.BROWSERS)) {
			TargetBrowsers browsers = new TargetBrowsers();
			if (include) {
				String[] idsArray = criteria.split(",");
				for (int i = 0; i < idsArray.length; i++) {
					Integer id = Integer.parseInt(idsArray[i].trim());
					BaseModel brObj = elasticSearch.searchById(TablesEntity.BROWSER, id);

					browsers.browsers.targetList.add(brObj);
				}
				browsers.selectAllBrowsers = false;
			} else {
				String[] idsArray = criteria.split(",");
				for (int i = 0; i < idsArray.length; i++) {
					Integer id = Integer.parseInt(idsArray[i].trim());
					BaseModel brObj = elasticSearch.searchById(TablesEntity.BROWSER, id);

					browsers.browsers.blockedList.add(brObj);
				}
				browsers.selectAllBrowsers = true;
			}
			strategy.targetBrowsers = browsers;
		} else if (exprType.equals(TargetingConstants.OPERATINGSYSTEM)) {

		} else if (exprType.equals(TargetingConstants.MOBILEDEVICEMODELS)) {

		} else if (exprType.equals(TargetingConstants.DAYPART)) {

			DayPart dayPart = new DayPart();
			if (tfId == TargetingFilter.DAY_OF_WEEK.getId()) {
				// Day
				if (include) {
					List<Long> days = getListOfIdsFromString(criteria);
					for (int i = 0; i < days.size(); i++) {
						Day day = new Day();
						day.setDay(days.get(i).intValue());
						day.setAllHoursForADay();
						dayPart.getDaypart().add(day);
					}
				} else {
					String daysList = invertDaysList(criteria);
					List<Long> days = getListOfIdsFromString(daysList);
					for (int i = 0; i < days.size(); i++) {
						Day day = new Day();
						day.setDay(days.get(i).intValue());
						day.setAllHoursForADay();
						dayPart.getDaypart().add(day);
					}
				}
			} else if (tfId == TargetingFilter.HOUR_OF_DAY.getId()) {
				// Hour
				String hoursList = criteria;
				if (include == false) {
					hoursList = invertHoursList(hoursList);
				}

				for (int i = 0; i < 7; i++) {
					Day dayObj = new Day();
					dayObj.setDay(i);
					List<Integer> hourIdsList = getIntListOfIdsFromString(hoursList);
					dayObj.setHours(hourIdsList);
					dayPart.getDaypart().add(dayObj);
				}
			}

			strategy.targetDays = dayPart;
		} else if (exprType.equals(TargetingConstants.GEOGRAPHIES)) {

			strategy.targetGeographies = new TargetGeoDTO();
			logger.debug("convertSimpleExpressionIntoDTOResponse  strategy.targetGeographies {} ",
					strategy.targetGeographies);

			List<BaseModel> entityList = new ArrayList<>();
			if (include) {
				if (tfId == TargetingFilter.GEO_CITY.getId()) {
					// CITY
					String[] idsArray = criteria.split(",");
					for (int i = 0; i < idsArray.length; i++) {
						if (idsArray[i].trim().equals(""))
							continue;
						Integer id = Integer.parseInt(idsArray[i].trim());
						BaseModel city = elasticSearch.searchPojoById(TablesEntity.CITY, id);
						entityList.add(city);
						strategy.targetGeographies.city.targetList = entityList;
					}
				} else if (tfId == TargetingFilter.GEO_REGION.getId()) {
					// STATE
					String[] idsArray = criteria.split(",");
					for (int i = 0; i < idsArray.length; i++) {
						if (idsArray[i].trim().equals(""))
							continue;
						Integer id = Integer.parseInt(idsArray[i].trim());
						BaseModel state = elasticSearch.searchPojoById(TablesEntity.STATE, id);
						entityList.add(state);
						strategy.targetGeographies.state.targetList = entityList;
					}
				} else if (tfId == TargetingFilter.GEO_COUNTRY.getId()) {
					// COUNTRY
					String[] idsArray = criteria.split(",");
					for (int i = 0; i < idsArray.length; i++) {
						if (idsArray[i].trim().equals(""))
							continue;
						Integer id = Integer.parseInt(idsArray[i].trim());
						BaseModel cm = elasticSearch.searchPojoById(TablesEntity.COUNTRY, id);

						entityList.add(cm);
						strategy.targetGeographies.country.targetList = entityList;
					}
				}
				strategy.targetGeographies.customGeoTargeting = true;
			} else {
				logger.debug("Operator IS_NONE_OF shouldn't be present as we don't support exclude list now.");
				strategy.targetGeographies.customGeoTargeting = false;
				// Added old targeting information to oldGeoTargeting parameter
				// in strategy response
				List<TargetingComponent> tcList = new ArrayList<TargetingComponent>();
				tcList.add(tc);
				strategy.oldGeoTargeting = createOldGeoDataStringFromTargetingComponentList(tcList);
			}
			logger.debug("After populate convertSimpleExpressionIntoDTOResponse  strategy.targetGeographies {} ",
					strategy.targetGeographies);

		}
	}

	private TargetingComponent getTargetingComponentFromId(String tcIdStr) {
		if (tcIdStr.trim().startsWith("("))
			tcIdStr = tcIdStr.substring(1);

		int length = tcIdStr.trim().length();
		if (tcIdStr.trim().endsWith(")"))
			tcIdStr = tcIdStr.substring(0, length - 1);

		logger.debug("Targeting Component id is : " + tcIdStr);

		Long tcId = Long.parseLong(tcIdStr);

		TargetingComponent tc = tcRepository.getOne(tcId);

		return tc;
	}

	// Helper Methods

	private List<String> breakExpressionsIntoComponentExpressions(String expr) {
		// ((14791&14792)|(14793&14794)|(14795&14796)) & (14797) &
		// (14798|(14799&14800)|(14801&14802)) & (14803)
		// 14798|(14799&14800)|(14801&14802)

		List<String> exprList = new ArrayList<String>();
		if (StringUtils.isBlank(expr))
			return exprList;
		if (!(expr.contains(new String("(").subSequence(0, 1)))) {
			exprList.add(expr);
			return exprList;
		}

		int bracketIndex = 0;
		int startIndex = 0, endIndex = 0;
		boolean bracketEncountered = false;

		for (int i = 0; i < expr.length(); i++) {
			char ch = expr.charAt(i);

			if (ch == '(') {
				bracketIndex++;
				if (bracketEncountered == false) {
					bracketEncountered = true;
					if (i > startIndex && endIndex == startIndex) {
						if ((i - endIndex) >= 2) {
							endIndex = i - 1;
							String str = expr.substring(startIndex, endIndex);
							// logger.debug(str);
							exprList.add(str);
							if (i != (expr.length() - 1)) {
								// logger.debug(Long.toString(i));
								// logger.debug("Next character : " +
								// str.charAt(i+1));
								startIndex = endIndex + 1;
								i++;
							}
						} else {
							logger.debug("First bracket doesn't come in beginning but in 2nd location in string");
						}
					}
				}
			} else if (ch == ')')
				bracketIndex--;

			if (bracketIndex == 0) {
				if (bracketEncountered == true) {
					endIndex = i + 1;
					String str = expr.substring(startIndex, endIndex);
					// logger.debug(str);
					exprList.add(str);

					if (i != (expr.length() - 1)) {
						// logger.debug(Long.toString(i));
						// logger.debug("Next character : " + str.charAt(i+1));
						startIndex = endIndex + 1;
						i++;
					}
				} else {
					// Ignore, first tc id getting parsed without any bracket
					// encountered yet
				}
			}
		}

		return exprList;
	}

	private String convertListofIdsToString(List<? extends BaseModel> ids) {
		String idstring = "";
		for (int i = 0; i < ids.size(); i++) {
			// Add validation for invalid entries

			idstring += Long.toString(ids.get(i).id);
			if (i != ids.size() - 1)
				idstring += ",";
		}

		return idstring;
	}

	private String convertListofIntegerIdsToString(List<Long> ids) {
		String idstring = "";
		for (int i = 0; i < ids.size(); i++) {
			// Add validation for invalid entries
			idstring += Long.toString(ids.get(i));
			if (i != ids.size() - 1)
				idstring += ",";
		}

		return idstring;
	}

	private String convertSetOfIntegerIdsToString(Set<Long> idSet) {
		String idstring = "";
		Iterator it = idSet.iterator();
		while (it.hasNext()) {
			long id = (Long) it.next();
			idstring += Long.toString(id);

			if (it.hasNext())
				idstring += ",";
		}

		return idstring;
	}

	private boolean isATreeExpression(String expr) {
		if (expr.contains(new String("|").subSequence(0, 1)) || expr.contains(new String("&").subSequence(0, 1)))
			return true;
		else
			return false;
	}

	private boolean checkIfOldTargetingExpression(String expr) {
		if (expr.contains(new String("|").subSequence(0, 1)) || expr.contains(new String("(").subSequence(0, 1))
				|| expr.contains(new String(")").subSequence(0, 1)))
			return false;
		else
			return true;
	}

	private void populateDayPartTreeParametersInResponse(String expr, StrategyDTO dto) {
		String exprStr = "";
		int length = expr.trim().length();
		if (expr.startsWith("(") && expr.endsWith(")"))
			exprStr = expr.trim().substring(1, length - 1).trim();
		else
			exprStr = expr.trim();

		DayPart dayPartObj = new DayPart();

		logger.debug("DayPart expression before splitting is : " + exprStr);
		boolean oldLiDayExpr = isOldLIDayExpr(exprStr);
		if (oldLiDayExpr == true) {
			populateDayPartObjectFromOldLIDayExpr(exprStr, dayPartObj);
		} else {
			List<String> parts = breakExpressionsIntoComponentExpressions(exprStr);
			for (int i = 0; i < parts.size(); i++) {
				// logger.debug("Partial dayPart string : " + parts.get(i));
				Day day = getDayObjectFromDaypartExpr(parts.get(i));

				dayPartObj.getDaypart().add(day);
			}
		}

		dto.targetDays = dayPartObj;
	}

	private boolean isOldLIDayExpr(String exprStr) {
		if (exprStr.contains(new String("|").subSequence(0, 1))) {
			logger.debug("OR operator present. Must be new UI created day expr");
			return false;
		} else {
			String expr = utility.trimSorroundingBrackets(exprStr);
			String[] parts = expr.split("&");
			boolean hourTarPresent = false, dayTarWithOneDay = false;
			if (parts != null && parts.length > 0) {
				for (int i = 0; i < parts.length; i++) {
					String part = parts[i];
					if (part.contains(new String("&").subSequence(0, 1))
							|| part.contains(new String("(").subSequence(0, 1))
							|| part.contains(new String(")").subSequence(0, 1))
							|| part.contains(new String("|").subSequence(0, 1))) {
						return false;
					} else {
						long tcId = Long.parseLong(part);
						TargetingComponent tc = tcRepository.getOne(tcId);
						if (tc != null && tc.getId() != null && tc.getTargetingFilterId() != null) {
							long filterId = tc.getTargetingFilterId();
							String criteria = tc.getCriteria();
							if (filterId == TargetingFilter.DAY_OF_WEEK.getId()) {
								// Day
								if (criteria.contains(new String(",").subSequence(0, 1))) {
									// more than one day. Old daypart expr
									return true;
								} else {
									dayTarWithOneDay = true;
								}
							} else if (filterId == 2) {
								hourTarPresent = true;
							}
						}
					}
				}
			}

			if (hourTarPresent == true && dayTarWithOneDay == false) {
				return true;
			}
		}

		return false;
	}

	private void populateDayPartObjectFromOldLIDayExpr(String exprStr, DayPart dayPartObj) {
		String defaultHourString = "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23";
		String expr = utility.trimSorroundingBrackets(exprStr);
		String[] parts = expr.split("&");
		TargetingComponent dayTC = null, hourTC = null;
		if (parts != null && parts.length > 0) {
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];

				Long tcId = Long.parseLong(part);
				TargetingComponent tc = tcRepository.getOne(tcId);
				if (tc != null && tc.getId() != null && tc.getTargetingFilterId() != null) {
					long filterId = tc.getTargetingFilterId();
					String criteria = tc.getCriteria();
					if (filterId == TargetingFilter.DAY_OF_WEEK.getId()) {
						dayTC = tc;
					} else if (filterId == TargetingFilter.HOUR_OF_DAY.getId()) {
						hourTC = tc;
					}
				}
			}
		}

		if (dayTC != null && hourTC != null) {
			String hoursList = hourTC.getCriteria();
			String daysList = dayTC.getCriteria();

			if (hourTC.getTargetingOperatorId() == TargetingOperator.IS_NONE_OF.getId()) {
				hoursList = invertHoursList(hoursList);
			}

			if (dayTC.getTargetingOperatorId() == TargetingOperator.IS_NONE_OF.getId()) {
				daysList = invertDaysList(daysList);
			}

			String[] days = daysList.split(",");
			for (int i = 0; i < days.length; i++) {
				String dayStr = days[i];
				Integer dayId = Integer.parseInt(dayStr);
				Day dayObj = new Day();
				dayObj.setDay(dayId);
				List<Integer> hourIdsList = getIntListOfIdsFromString(hoursList);
				dayObj.setHours(hourIdsList);

				dayPartObj.getDaypart().add(dayObj);
			}
		} else if (dayTC != null) {
			String daysList = dayTC.getCriteria();
			if (dayTC.getTargetingOperatorId() == 6) {
				daysList = invertDaysList(daysList);
			}

			String[] days = daysList.split(",");
			for (int i = 0; i < days.length; i++) {
				String dayStr = days[i];
				Integer dayId = Integer.parseInt(dayStr);
				Day dayObj = new Day();
				dayObj.setDay(dayId);
				dayObj.setAllHoursForADay();

				dayPartObj.getDaypart().add(dayObj);
			}
		} else if (hourTC != null) {
			String hoursList = hourTC.getCriteria();
			if (hourTC.getTargetingOperatorId() == 6) {
				hoursList = invertHoursList(hoursList);
			}

			for (int i = 0; i < 7; i++) {
				Day dayObj = new Day();
				dayObj.setDay(i);
				List<Integer> hourIdsList = getIntListOfIdsFromString(hoursList);
				dayObj.setHours(hourIdsList);

				dayPartObj.getDaypart().add(dayObj);
			}
		}
	}

	private String invertHoursList(String hoursList) {
		logger.debug("In invertHoursList. hoursList : " + hoursList);
		Map<Integer, Boolean> hrs = new HashMap<Integer, Boolean>();

		for (int i = 0; i < 24; i++) {
			hrs.put(i, Boolean.FALSE);
		}

		String[] hrsArray = hoursList.split(",");
		String invertedHrsList = "";

		for (int i = 0; i < hrs.size(); i++) {
			String hrStr = hrsArray[i].trim();
			Integer hr = Integer.parseInt(hrStr);
			if (hrs.containsKey(hr)) {
				hrs.put(hr, Boolean.TRUE);
			}
		}

		for (int i = 0; i < 24; i++) {
			Boolean flag = hrs.get(i);

			if (flag.equals(Boolean.FALSE)) {
				invertedHrsList.concat(Long.toString(i));
				invertedHrsList.concat(",");
			}
		}

		if (invertedHrsList.endsWith(","))
			invertedHrsList = invertedHrsList.substring(0, invertedHrsList.length() - 1);

		logger.debug("invertedHrsList : " + invertedHrsList);

		return invertedHrsList;
	}

	private String invertDaysList(String daysList) {
		logger.debug("In invertDaysList. hoursList : " + daysList);
		Map<Integer, Boolean> days = new HashMap<Integer, Boolean>();

		for (int i = 0; i < 7; i++) {
			days.put(i, Boolean.FALSE);
		}

		String[] daysArray = daysList.split(",");
		String invertedDaysList = "";

		for (int i = 0; i < days.size(); i++) {
			String dayStr = daysArray[i].trim();
			Integer day = Integer.parseInt(dayStr);
			if (days.containsKey(day)) {
				days.put(day, Boolean.TRUE);
			}
		}

		for (int i = 0; i < 7; i++) {
			Boolean flag = days.get(i);

			if (flag.equals(Boolean.FALSE)) {
				invertedDaysList.concat(Long.toString(i));
				invertedDaysList.concat(",");
			}
		}

		if (invertedDaysList.endsWith(","))
			invertedDaysList = invertedDaysList.substring(0, invertedDaysList.length() - 1);

		logger.debug("invertedDaysList : " + invertedDaysList);

		return invertedDaysList;
	}

	private Day getDayObjectFromDaypartExpr(String str) {
		Day day = new Day();
		String dayExpr = utility.trimSorroundingBrackets(str);
		String[] tcs = dayExpr.split("&");
		if (tcs.length > 2)
			logger.debug("More than 2 targeting components in 1 day expression");
		else {
			for (int i = 0; i < tcs.length; i++) {
				String tcStr = tcs[i];
				TargetingComponent tc = getTargetingComponentFromId(tcStr);
				String criteria = tc.getCriteria();
				if (tc.getTargetingFilterId() == TargetingFilter.DAY_OF_WEEK.getId()) {
					// DAY
					if (criteria.contains(new String(",").subSequence(0, 1))) {
						logger.debug("Error : criteria contains multiple days");
					} else {
						int id = Integer.parseInt(criteria.trim());
						day.setDay(id);
					}
				} else if (tc.getTargetingFilterId() == TargetingFilter.HOUR_OF_DAY.getId()) {
					// TIME
					List<Integer> hours = getIntListOfIdsFromString(criteria);
					day.setHours(hours);
				}
			}
		}

		return day;
	}

	private List<Long> getListOfIdsFromString(String str) {
		List<Long> idList = new ArrayList<>();
		String[] ids = str.trim().split(",");
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].trim().equals(""))
				break;
			Long id = Long.parseLong(ids[i].trim());
			idList.add(id);
		}

		return idList;
	}

	private List<Integer> getIntListOfIdsFromString(String str) {
		List<Integer> idList = new ArrayList<>();
		String[] ids = str.trim().split(",");
		for (int i = 0; i < ids.length; i++) {
			if (ids[i].trim().equals(""))
				break;
			int id = Integer.parseInt(ids[i].trim());
			idList.add(id);
		}

		return idList;
	}

	public String constructSingleStringFromListofString(List<String> stringList) {
		String result = "";
		StringBuffer sb = new StringBuffer();

		int size = stringList.size();

		for (int i = 0; i < stringList.size(); i++) {
			String stringEntry = stringList.get(i);
			sb.append("'");
			sb.append(stringEntry);
			sb.append("'");
			if (i != size - 1)
				sb.append(",");
		}

		result = sb.toString();
		logger.debug("List of sites in string : " + result);
		return result;
	}

	private void removeDuplicatesFromExtendedBaseModelList(Collection<? extends BaseModel> bmList) {
		Map<Long, Long> idMap = new HashMap<>();
		Iterator<BaseModel> iter = (Iterator<BaseModel>) bmList.iterator();
		while (iter.hasNext()) {
			long id = iter.next().getId();
			if (idMap.containsKey(id)) {
				logger.debug("Found duplicate id : " + id + " in list. Removing now.");
				iter.remove();
			} else {
				idMap.put(id, id);
			}
		}
		idMap.clear();
	}

	private class StrategyTargetingExpression {
		String geoExpr;

		String segmentExpr;

		String browserExpr;

		String dayPartExpr;

		String osExpr;

		String mobileModelsExpr;

		String connTypeExpr;

		public String getconnTypeExpr() {
			return geoExpr;
		}

		public void setconnTypeExpr(String connTypeExpr) {
			this.connTypeExpr = connTypeExpr;
		}

		public String getGeoExpr() {
			return geoExpr;
		}

		public void setGeoExpr(String geoExpr) {
			this.geoExpr = geoExpr;
		}

		public String getSegmentExpr() {
			return segmentExpr;
		}

		public void setSegmentExpr(String segmentExpr) {
			this.segmentExpr = segmentExpr;
		}

		public String getBrowserExpr() {
			return browserExpr;
		}

		public void setBrowserExpr(String browserExpr) {
			this.browserExpr = browserExpr;
		}

		public String getDayPartExpr() {
			return dayPartExpr;
		}

		public void setDayPartExpr(String dayPartExpr) {
			this.dayPartExpr = dayPartExpr;
		}

		public String getOsExpr() {
			return osExpr;
		}

		public void setOsExpr(String osExpr) {
			this.osExpr = osExpr;
		}

		public String getMobileModelsExpr() {
			return mobileModelsExpr;
		}

		public void setMobileModelsExpr(String mobileModelsExpr) {
			this.mobileModelsExpr = mobileModelsExpr;
		}
	}

	private class StateDictionary {
		Map<Integer, List<Integer>> stateCities;

		public StateDictionary() {
			stateCities = new HashMap<Integer, List<Integer>>();
		}

		public Map<Integer, List<Integer>> getStateCities() {
			return stateCities;
		}

		public void setStateCities(Map<Integer, List<Integer>> stateCities) {
			this.stateCities = stateCities;
		}

	}

	public void constuctGeoTargetedStrategiesResponse(List<StrategyEntity> strategiesList,
			List<StrategyTargetingDetails> futureEndingList, List<StrategyTargetingDetails> lessThanAMonthOldList,
			List<StrategyTargetingDetails> moreThanAMonthOldList) {

		for (StrategyEntity al : strategiesList) {
			Optional<AdvertiserLineItemTargetingExpression> ali = aliTRepo.findByStrategyId(al.getId());
			if (!ali.isPresent())
				continue;
			String targetingExpression = ali.get().getCommonTargetingExpression();
			if (targetingExpression == null || targetingExpression.length() == 0 || targetingExpression.equals("NULL"))
				continue;

			logger.debug("Checking for lineItem id : " + al.getId());
			boolean oldExpression = checkIfOldTargetingExpression(targetingExpression);
			if (oldExpression) {
				List<TargetingComponent> geoTCs = new ArrayList<TargetingComponent>();

				String[] ids = targetingExpression.trim().split("&");
				for (int i = 0; i < ids.length; i++) {
					String idStr = ids[i].trim();
					long id = Long.parseLong(idStr);
					boolean include = false;
					TargetingComponent tc = tcRepository.getOne(id);
					long tfId = tc.getTargetingFilterId();

					if (tfId == TargetingFilter.GEO_REGION.getId() || tfId == TargetingFilter.GEO_COUNTRY.getId()) {
						geoTCs.add(tc);
					}
				}

				String geoExpr = "";

				if (geoTCs.size() > 0) {
					for (TargetingComponent tc : geoTCs) {
						geoExpr += Long.toString(tc.getId());
						geoExpr += "&";
					}

					int len = geoExpr.length();
					geoExpr = geoExpr.substring(0, len - 1);
				}

				if (geoExpr != null && geoExpr.length() > 0 && isATreeExpression(geoExpr)) {
					StrategyTargetingDetails strategyDetails = new StrategyTargetingDetails();
					populateStrategyTargetingDetails(al, strategyDetails, geoExpr);
					addStrategyMigrationDataInList(al, strategyDetails, futureEndingList, lessThanAMonthOldList,
							moreThanAMonthOldList);
					// strategyDetailsList.add(strategyDetails);
				} else if (geoExpr != null && geoExpr.length() > 0 && !isATreeExpression(geoExpr)) {
					// This means there will be only 1 targeting component in
					// geoExpr
					if (geoTCs.size() == 1 && geoTCs.get(0).getTargetingOperatorId() == 6) {
						// Single targeting component with exclude list
						StrategyTargetingDetails strategyDetails = new StrategyTargetingDetails();
						populateStrategyTargetingDetails(al, strategyDetails, geoExpr);
						addStrategyMigrationDataInList(al, strategyDetails, futureEndingList, lessThanAMonthOldList,
								moreThanAMonthOldList);
						// strategyDetailsList.add(strategyDetails);
					}
				}
			} else {
				StrategyTargetingExpression exprObj = breakExpressionIntoComponents(targetingExpression);
				if (exprObj.geoExpr != null && exprObj.geoExpr.length() > 0 && isATreeExpression(exprObj.geoExpr)) {
					StrategyTargetingDetails strategyDetails = new StrategyTargetingDetails();
					populateStrategyTargetingDetails(al, strategyDetails, exprObj.geoExpr);
					addStrategyMigrationDataInList(al, strategyDetails, futureEndingList, lessThanAMonthOldList,
							moreThanAMonthOldList);
					// strategyDetailsList.add(strategyDetails);
				}
			}
		}
	}

	private void addStrategyMigrationDataInList(StrategyEntity al, StrategyTargetingDetails strategyDetails,
			List<StrategyTargetingDetails> futureEndingList, List<StrategyTargetingDetails> lessThanAMonthOldList,
			List<StrategyTargetingDetails> moreThanAMonthOldList) {
		if (al.getActive() && NumberUtils.getLongValue(al.getEndTime()) > (System.currentTimeMillis() / 1000)) {
			futureEndingList.add(strategyDetails);
		} else if ((NumberUtils.getLongValue(al.getEndTime()) > ((System.currentTimeMillis() / 1000) - 2592000))
				&& (NumberUtils.getLongValue(al.getEndTime()) < (System.currentTimeMillis() / 1000))) {
			lessThanAMonthOldList.add(strategyDetails);
		} else {
			moreThanAMonthOldList.add(strategyDetails);
		}
	}

	private void populateStrategyTargetingDetails(StrategyEntity al, StrategyTargetingDetails detailObj, String expr) {
		List<String> tcIds = utility.getListOfTCIdsInExpr(expr);
		detailObj.id = al.getId();
		detailObj.geoExpression = expr;
		detailObj.strategyEndDate = al.getEndTime().longValue();
		detailObj.campaignId = al.getCampianId();
		CampaignESDTO camp = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, al.getCampianId());
		detailObj.campaignEndDate = camp.getEndTime();

		for (String tcId : tcIds) {
			long compId = Long.parseLong(tcId);
			TargetingComponent tc = tcRepository.getOne(compId);

			TargetingComponentDTO tcDTO = new TargetingComponentDTO();
			tcDTO.id = tc.getId();
			long tfId = tc.getTargetingFilterId();
			tcDTO.filter.id = tc.getTargetingFilterId();
			tcDTO.operator.id = tc.getTargetingOperatorId();

			String criteria = tc.getCriteria();

			if (criteria != null && criteria.length() > 0) {
				List<Integer> idList = getIntListOfIdsFromString(criteria);
				List<BaseModel> bmList = new ArrayList<BaseModel>();
				if (tfId == 4) {
					for (int id : idList) {
						BaseModel sm = elasticSearch.searchPojoById(TablesEntity.STATE, id);
						if (sm != null && sm.getId() != null)
							bmList.add(new BaseModel(sm.getId(), sm.getName()));
					}
				} else if (tfId == 5) {
					for (int id : idList) {
						BaseModel cm = elasticSearch.searchPojoById(TablesEntity.COUNTRY, id);
						if (cm != null && cm.getId() != null)
							bmList.add(new BaseModel(cm.getId(), cm.getName()));
					}
				}
				tcDTO.criteriaEntities.addAll(bmList);
			}
			detailObj.geoTCList.add(tcDTO);
		}
	}

	public void constuctStrategyTargetedSegmentsResponse(List<StrategyEntity> strategiesList,
			Map<Long, List<Long>> result) {
		for (StrategyEntity al : strategiesList) {
			Optional<AdvertiserLineItemTargetingExpression> ali = aliTRepo.findByStrategyId(al.getId());
			if (!ali.isPresent())
				continue;
			String targetingExpression = ali.get().getCommonTargetingExpression();

			if (targetingExpression == null || targetingExpression.length() == 0 || targetingExpression.equals("NULL"))
				continue;
			logger.debug("Checking for lineItem id : " + al.getId());
			boolean oldExpression = checkIfOldTargetingExpression(targetingExpression);
			if (oldExpression) {
				String[] ids = targetingExpression.trim().split("&");
				for (int i = 0; i < ids.length; i++) {
					String idStr = ids[i].trim();
					long id = Long.parseLong(idStr);

					TargetingComponent tc = tcRepository.getOne(id);
					long tfId = tc.getTargetingFilterId();
					String criteria = tc.getCriteria();

					if (tfId == TargetingFilter.USER_SEGMENT.getId()) {
						// SEGMENTS

						String[] idsArray = criteria.split(",");
						for (int j = 0; j < idsArray.length; j++) {
							Long segId = Long.parseLong(idsArray[j].trim());
							BaseModel sgm = elasticSearch.searchById(TablesEntity.AUDIENCE, segId);

							if (sgm != null && sgm.getId() != null) {
								if (result.containsKey(al.getId()))
									result.get(al.getId()).add(segId);
								else {
									result.put(al.getId(), new ArrayList<>());
									result.get(al.getId()).add(segId);
								}
							}
						}
					}
				}
			} else {
				StrategyTargetingExpression exprObj = breakExpressionIntoComponents(targetingExpression);
				if (exprObj.segmentExpr != null && exprObj.segmentExpr.length() > 0) {
					String[] ids = utility.trimSorroundingBrackets(exprObj.segmentExpr).split("&");
					for (int i = 0; i < ids.length; i++) {
						String idStr = ids[i].trim();
						long tcid = Long.parseLong(idStr);
						TargetingComponent tc = tcRepository.getOne(tcid);
						// int tfId = tc.getTargetingFilterId();
						// int toId = tc.getTargetingOperator().getId();
						String criteria = tc.getCriteria();
						String[] idsArray = criteria.split(",");
						for (int j = 0; j < idsArray.length; j++) {
							if (idsArray[j] == null || idsArray[j].trim().length() == 0)
								continue;
							Long sid = Long.parseLong(idsArray[j].trim());
							BaseModel sgm = elasticSearch.searchById(TablesEntity.AUDIENCE, sid);
							if (sgm != null && sgm.getId() != null) {
								if (result.containsKey(al.getId()))
									result.get(al.getId()).add(sid);
								else {
									result.put(al.getId(), new ArrayList<>());
									result.get(al.getId()).add(sid);
								}
							}
						}
					}
				}
			}
		}
	}

	private String createOldGeoDataStringFromTargetingComponentList(List<TargetingComponent> tcs) {
		String oldGeoData = "";

		for (TargetingComponent tc : tcs) {
			long tfId = tc.getTargetingFilterId();
			long toId = tc.getTargetingOperatorId();
			String criteria = tc.getCriteria();
			List<Integer> entityIds = getIntListOfIdsFromString(criteria);

			if (entityIds == null || entityIds.size() == 0)
				continue;

			if (toId == 6) {
				oldGeoData += "Excluded";
			} else if (toId == 2) {
				oldGeoData += "Included";
			}

			if (tfId == 5) {
				oldGeoData += " Countries : ";
				for (Integer entityId : entityIds) {
					BaseModel cm = elasticSearch.searchPojoById(TablesEntity.COUNTRY, entityId);
					String countryName = cm.getName();
					oldGeoData += countryName;
					oldGeoData += ",";
				}
			} else if (tfId == 4) {
				oldGeoData += " States : ";
				for (Integer entityId : entityIds) {
					BaseModel sm = elasticSearch.searchPojoById(TablesEntity.STATE, entityId);
					String stateName = sm.getName();
					oldGeoData += stateName;
					oldGeoData += ",";
				}
			} else if (tfId == 3) {
				oldGeoData += "Cities : ";
				for (Integer entityId : entityIds) {
					BaseModel cm = elasticSearch.searchPojoById(TablesEntity.CITY, entityId);
					String cityName = cm.getName();
					oldGeoData += cityName;
					oldGeoData += ",";
				}
			}

			int length = oldGeoData.length();
			oldGeoData = oldGeoData.substring(0, length - 1);

			oldGeoData += " ; ";
		}

		return oldGeoData;
	}

	private void validateBaseModelList(List<BaseModel> bmList, String parentParam) throws ValidationException {
		if (CollectionUtils.isNotEmpty(bmList)) {
			for (BaseModel bm : bmList) {
				if (bm == null || bm.id == null) {
					throw new ValidationException(ErrorCode.ENTITY_INVALID_VALUE,
							new Object[] { "id in " + parentParam, bm.id });
				}
			}
		}
	}

	private void validateExtendedBaseModelList(Collection<? extends BaseModel> bmList, String parentParam)
			throws ValidationException {
		if (CollectionUtils.isNotEmpty(bmList)) {
			for (BaseModel bm : bmList) {
				if (bm == null || bm.getId() == null) {
					throw new ValidationException(ErrorCode.MISSING_VARIABLE_ERROR,
							new Object[] { "id in " + parentParam, bm });
				}
			}
		}
	}

	public void populateBrowserTargetingDTOList(List<TargetingComponent> browserTCList,
			List<TargetingComponentDTO> tcDTOList) {

		for (TargetingComponent tc : browserTCList) {
			TargetingComponentDTO tcDTO = new TargetingComponentDTO();
			tcDTO.id = tc.getId();
			Long tfId = tc.getTargetingFilterId();
			tcDTO.filter.id = tc.getTargetingFilterId();
			tcDTO.operator.id = tc.getTargetingOperatorId();
			String criteria = tc.getCriteria();

			if (criteria != null && criteria.length() > 0) {
				List<Integer> idList = getIntListOfIdsFromString(criteria);
				List<BaseModel> bmList = new ArrayList<BaseModel>();
				for (Integer id : idList) {
					BaseModel bm = elasticSearch.searchById(TablesEntity.BROWSER, id);
					bmList.add(bm);
				}
				tcDTO.criteriaEntities.addAll(bmList);
			}
			tcDTOList.add(tcDTO);
		}

	}

	public List<Long> migrateBrowserTCsFromExcludeToInclude(List<TargetingComponent> browserTCList) {

		List<BaseModel> browsersList = elasticSearch.searchList(TablesEntity.BROWSER);
		List<Long> browserIds = new ArrayList<>();
		List<Long> tcIdsUpdated = new ArrayList<>();
		for (BaseModel brm : browsersList) {
			browserIds.add(brm.getId());
		}

		Map<Long, Long> criteriaIdsMap = new HashMap<>();
		for (TargetingComponent tc : browserTCList) {
			Long tcId = tc.getId();
			criteriaIdsMap.clear();
			String cri = tc.getCriteria();

			List<Long> idList = getListOfIdsFromString(cri);
			List<Long> newIdList = new ArrayList<>();

			for (Long id : idList) {
				criteriaIdsMap.put(id, id);
			}

			for (Long id : browserIds) {
				if (criteriaIdsMap.containsKey(id)) {
					// Browser was excluded. Do nothing
				} else {
					newIdList.add(id);
				}
			}

			if (newIdList.size() == 0)
				continue;

			String newCri = convertListofIntegerIdsToString(newIdList);
			logger.debug("Old criteria was : " + cri + " ; " + "New criteria will be : " + newCri);

			tc.setTargetingOperatorId(2l);
			tc.setCriteria(newCri);
			tcRepository.save(tc);
			tcIdsUpdated.add(tcId);

		}

		return tcIdsUpdated;
	}

	private void removeRTBTargetingFromStrategy(StrategyDTO strategy, StrategyEntity strategyDO) {
		Long strategyId = strategyDO.getId();
		List<InventorySource> ivsList = inventrySourceRepo.findByStrategyId(strategyId);
		InventorySource ivs = null;
		if (ivsList == null || ivsList.size() == 0) {
			// NO RTB inventory . Do nothing
		} else if (ivsList != null && ivsList.size() > 0) {
			// Strategy has inventory sources
			deleteOldInventory(ivsList);
		}
	}

	public void setMinMaxBidForRTBStrategy(StrategyDTO strategy, StrategyEntity strategyDO) {
		Platform platform = null;
		if (strategy.bidCapMin == null || strategy.bidCapMax == null) {
			platform = elasticSearch.searchPojoById(TablesEntity.PLATFORM, 1);// 1 is the Id of Atomex
																				// Plateform
		}
		if (strategy.bidCapMin != null)
			strategyDO.setBidCapMinCpm(strategy.bidCapMin);
		else {
			strategyDO.setBidCapMinCpm(new BigDecimal(0.000000000));
			/*
			 * To Do: Check if this logic is required. Set default min cap based on flow
			 * rate and platform level ctr. if (platform != null) { BigDecimal ctrFactor =
			 * platform.getCtrFactor(); BigDecimal cvrFactor = platform.getCvrFactor();
			 * BigDecimal minBidValue = null; MathContext mathContext = new
			 * MathContext(BigDecimal.ROUND_DOWN); if (strategyDO.getPricingId() == 3 &&
			 * strategyDO.getFlowRate() != null) { minBidValue =
			 * strategyDO.getFlowRate().multiply(cvrFactor, mathContext) .multiply(new
			 * BigDecimal(10.000000000), mathContext); } else if (strategyDO.getPricingId()
			 * == 2 && strategyDO.getFlowRate() != null) { minBidValue =
			 * strategyDO.getFlowRate().multiply(ctrFactor, mathContext) .multiply(new
			 * BigDecimal(10.000000000), mathContext); } else { minBidValue = new
			 * BigDecimal(0.000000000); } strategyDO.setBidCapMinCpm(minBidValue); } else {
			 * strategyDO.setBidCapMinCpm(new BigDecimal(0.000000000)); }
			 */
		}

		if (strategy.bidCapMax != null)
			strategyDO.setBidCapMaxCpm(strategy.bidCapMax);
		else {
			// SET -1 by default
			strategyDO.setBidCapMaxCpm(new BigDecimal(-1.000000000));
		}
	}

}
