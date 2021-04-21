package io.revx.api.service.audience;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.Status;
import io.revx.api.mysql.amtdb.entity.RuleComponent;
import io.revx.api.mysql.amtdb.entity.SegmentPixelMap;
import io.revx.api.mysql.amtdb.entity.SegmentType;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.amtdb.repo.RuleComponentRepository;
import io.revx.api.mysql.amtdb.repo.SegmentPixelMapRepository;
import io.revx.api.mysql.entity.advertiser.AdvertiserSegmentMappingEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.crm.impl.CrmServiceImpl;
import io.revx.api.service.pixel.impl.DataPixelServiceImpl;
import io.revx.core.enums.AudienceType;
import io.revx.core.enums.DataSourceType;
import io.revx.core.enums.DurationUnit;
import io.revx.core.enums.Operator;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.audience.AudienceDTO;
import io.revx.core.model.audience.PixelDataFileDTO;
import io.revx.core.model.audience.PixelDataScheduleDTO;
import io.revx.core.model.audience.RuleComponentDTO;
import io.revx.core.model.audience.RuleDTO;
import io.revx.core.model.audience.UserDataType;
import io.revx.core.model.crm.ServerSyncCoordinatorDTO;
import io.revx.core.model.pixel.DataPixelDTO;

@Component
public class AudienceUtils {

	private static final Logger logger = LoggerFactory.getLogger(AudienceUtils.class);

	private static final Pattern expressionIdPattern = Pattern.compile("\\[(\\d+)\\]");
	private DataPixelServiceImpl dataPixelService;
	private EntityESService elasticSearch;
	private CrmServiceImpl crmService;
	private LoginUserDetailsService loginUserDetailsService;
	private RuleComponentRepository ruleComponentRepository;
	private SegmentPixelMapRepository segmentPixelMapRepository;
	private ApplicationProperties applicationProperties;

	@Autowired
	public void setDataPixelService(DataPixelServiceImpl dataPixelService) {
		this.dataPixelService = dataPixelService;
	}

	@Autowired
	public void setElasticSearch(EntityESService elasticSearch) {
		this.elasticSearch = elasticSearch;
	}

	@Autowired
	public void setCrmService(CrmServiceImpl crmService) {
		this.crmService = crmService;
	}

	@Autowired
	public void setLoginUserDetailsService(LoginUserDetailsService loginUserDetailsService) {
		this.loginUserDetailsService = loginUserDetailsService;
	}

	@Autowired
	public void setRuleComponentRepository(RuleComponentRepository ruleComponentRepository) {
		this.ruleComponentRepository = ruleComponentRepository;
	}

	@Autowired
	public void setSegmentPixelMapRepository(SegmentPixelMapRepository segmentPixelMapRepository) {
		this.segmentPixelMapRepository = segmentPixelMapRepository;
	}

	@Autowired
	public void setApplicationProperties(ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public boolean validateDTO(AudienceDTO audienceDTO, boolean isUpdate) throws ValidationException {

		if (audienceDTO == null)
			throw new ValidationException("Audience is missing");

		if (audienceDTO.getAdvertiser() == null || audienceDTO.getAdvertiser().getId() == null)
			throw new ValidationException("Advertiser Id is missing");

		Advertiser advertiser = elasticSearch.searchPojoById(TablesEntity.ADVERTISER,
				audienceDTO.getAdvertiser().getId());
		if (!loginUserDetailsService.isValidAdvertiser(advertiser))
			throw new ValidationException("Invalid advertiser id : " + audienceDTO.getAdvertiser().getId());

		if (isUpdate) {
			if (audienceDTO.getId() == null)
				throw new ValidationException("Audience Id is missing");
		}

		if (!isUpdate) {
			if (StringUtils.isBlank(audienceDTO.getName()))
				throw new ValidationException("Audience Name is missing");
		}

		if (audienceDTO.getDuration() == null)
			throw new ValidationException("Audience Duration is missing");

		if (audienceDTO.getDuration() != null && audienceDTO.getDuration().compareTo(0L) <= 0)
			throw new ValidationException("Audience Duration is Invalid");

		if (audienceDTO.getDurationUnit() == null) {
			throw new ValidationException("Audience Duration Unit is missing");
		} else {
			if (audienceDTO.getDurationUnit() != null
					&& DurationUnit.fromString(audienceDTO.getDurationUnit().getText()) == null) {
				throw new ValidationException("Audience Duration Unit Value is missing");
			}
		}

		if (audienceDTO.getSegmentType() != null) {
			if (audienceDTO.getSegmentType().compareTo(1) != 0 && audienceDTO.getSegmentType().compareTo(2) != 0
					&& audienceDTO.getSegmentType().compareTo(3) != 0
					&& audienceDTO.getSegmentType().compareTo(4) != 0) {
				throw new ValidationException("Audience Segment Type is Invalid");
			}

		}

		if (audienceDTO.getDataSourceType() == null)
			throw new ValidationException("Audience Data Source Type is missing");

		if (DataSourceType.PIXEL_LOG.id.equals(audienceDTO.getDataSourceType().intValue())
				&& audienceDTO.getSegmentType() == null) {
			/*
			 * Check rule Expression object. It's Rule base audience.
			 */
			if (audienceDTO.getRuleExpression() == null) {
				throw new ValidationException("Audience Rule Expression is missing");
			}

			if (audienceDTO.getRuleExpression().getSimpleExpr() == null) {
				throw new ValidationException("Audience Rule Expression is missing");
			}
		}

		if (!isUpdate) {
			if (DataSourceType.AUDIENCE_FEED.id.equals(audienceDTO.getDataSourceType().intValue())) {
				if (audienceDTO.getPixelDataSchedule() == null)
					throw new ValidationException("Audience Pixel Data Schedule is missing");
			} else if (DataSourceType.FILE_UPLOAD.id.equals(audienceDTO.getDataSourceType().intValue())) {
				if (audienceDTO.getPixelDataFile() == null)
					throw new ValidationException("Audience Pixel Data file is missing");
			}
		}

		if (audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType().equals(SegmentType.DMP.id)) {
			if (StringUtils.isBlank(audienceDTO.getRemoteSegmentId()))
				throw new ValidationException("Remote segment id for DMP Segment is blank");
		}

		return true;
	}

	public Segments getEntityFromDto(AudienceDTO audienceDTO) {
		Segments segmentsEntity = new Segments();
		segmentsEntity.setName(audienceDTO.getName());
		segmentsEntity.setDescription(audienceDTO.getDescription());
		segmentsEntity.setDuration(audienceDTO.getDuration());
		segmentsEntity.setDurationUnit(audienceDTO.getDurationUnit());
		segmentsEntity.setUserDataType(UserDataType.getById(audienceDTO.getUserDataType()));
		segmentsEntity.setCreatedOn(System.currentTimeMillis() / 1000);

		if (audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType().equals(SegmentType.DMP.id))
			segmentsEntity.setStatus(Status.INACTIVE);
		else
			segmentsEntity.setStatus(audienceDTO.isActive() == true ? Status.ACTIVE : Status.INACTIVE);

		if (StringUtils.isNoneBlank(audienceDTO.getRemoteSegmentId()))
			segmentsEntity.setRemoteSegmentId(audienceDTO.getRemoteSegmentId());
		segmentsEntity.setLicenseeId(loginUserDetailsService.getLicenseeId());

		if (audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType() == SegmentType.CLICKER.id) {
			segmentsEntity.setSegmentType(SegmentType.CLICKER);
		} else if (audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType() == SegmentType.HASH_BUCKET.id) {
			segmentsEntity.setSegmentType(SegmentType.HASH_BUCKET);
		} else if (audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType() == SegmentType.DMP.id) {
			segmentsEntity.setSegmentType(SegmentType.DMP);
		} else if (audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType() == SegmentType.PLATFORM.id) {
			segmentsEntity.setSegmentType(SegmentType.PLATFORM);
		}

		return segmentsEntity;
	}

	public SegmentPixelMap getMapEntityFromDto(AudienceDTO audienceDTO, Long segmentId) {
		SegmentPixelMap segmentPixelMap = new SegmentPixelMap();
		segmentPixelMap.setPixelId(audienceDTO.getPixelId());
		segmentPixelMap.setSegmentId(segmentId);
		if (audienceDTO.getRuleExpression() != null) {
			List<RuleComponentDTO> ruleComponents = new ArrayList<RuleComponentDTO>();
			String ruleExpression = getExpression(audienceDTO.getRuleExpression(), ruleComponents);
			segmentPixelMap.setRuleExpression(ruleExpression);
		}
		return segmentPixelMap;
	}

	public List<RuleComponent> getEntityFromDto(AudienceDTO audienceDTO, Long segmentToPixelExpressionId) {
		logger.debug("Inside getEntityFromDto method. audienceDTO : {}, segmentToPixelExpressionId : {}", audienceDTO,
				segmentToPixelExpressionId);
		List<RuleComponentDTO> ruleComponents = new ArrayList<RuleComponentDTO>();
		if (audienceDTO.getRuleExpression() != null) {
			getExpression(audienceDTO.getRuleExpression(), ruleComponents);
		}

		List<RuleComponent> entities = new ArrayList<>();
		for (RuleComponentDTO ruleComponentDTO : ruleComponents) {
			RuleComponent entity = new RuleComponent();
			entity.setRuleFilterId(ruleComponentDTO.getFilterId());
			entity.setRuleOperatorId(ruleComponentDTO.getOperatorId());
			entity.setRuleValue(ruleComponentDTO.getValue());
			entity.setSegmentPixelExpressionId(segmentToPixelExpressionId);
			entities.add(entity);
		}

		return entities;
	}

	/**
	 * This is a wrapper method around the convert rules method
	 * 
	 * @param rule
	 * @param ruleComponents
	 * @return
	 */
	public String getExpression(RuleDTO rule, List<RuleComponentDTO> ruleComponents) {
		logger.debug("Inside getExpression method. For rule : {}, ruleComponents : {}", rule, ruleComponents);
		String ruleExpression = convertRules(rule, ruleComponents);
		if (ruleExpression.charAt(0) != '(') {
			ruleExpression = new StringBuilder("(").append(ruleExpression).append(")").toString();
		}
		StringBuilder sb = new StringBuilder();
		int componentCount = 0;
		for (int idx = 0; idx < ruleExpression.length() - 1; idx++) {
			sb.append(ruleExpression.charAt(idx));
			if (ruleExpression.charAt(idx) == '[' && ruleExpression.charAt(idx + 1) == ']') {
				sb.append(componentCount++);
			}
		}
		sb.append(ruleExpression.charAt(ruleExpression.length() - 1));
		return sb.toString();
	}

	/**
	 * This is a recursive method converts the rule object provided by UI into SLM
	 * service understandable format (components list and expression).
	 * 
	 * @param rule
	 * @param ruleComponents
	 * @param componentCount
	 * @return
	 */
	public String convertRules(RuleDTO rule, List<RuleComponentDTO> ruleComponents) {
		logger.debug(
				"Inside convertRules mothod. Converting rul into expressions for RuleDTO : {} ruleComponent list : {} ",
				rule, ruleComponents);
		try {
			StringBuilder ruleExpression = new StringBuilder();
			if (!rule.getSimpleExpr()) {
				logger.debug("Rule is not with simple expressions : " + rule);
				if (rule.getNegate() != null && rule.getNegate())
					ruleExpression.append(Operator.NOT.getValue());

				ruleExpression.append("(");
				for (RuleDTO childRule : rule.getRuleExpressionList()) {
					logger.debug("Adding child rule : " + childRule);
					logger.debug("ruleExpression start of loop : " + ruleExpression);
					ruleExpression.append(convertRules(childRule, ruleComponents));
					if (rule.getOperator() != null) {
						ruleExpression.append(rule.getOperator().getValue());
					} else {
						ruleExpression.append(Operator.OR.getValue());
						logger.debug(
								"Last eliment in rule. Adding OR value at last which will get delited. ruleExpression : {}",
								ruleExpression);
					}

					logger.debug("ruleExpression end of loop : " + ruleExpression);
				}
				ruleExpression.deleteCharAt(ruleExpression.length() - 1).append(")");
				logger.debug("Final  : ruleExpression" + ruleExpression);
			} else {
				logger.debug("Rule is with simple expressions : " + rule);
				if (rule.getNegate() != null && rule.getNegate())
					ruleExpression.append(Operator.NOT.getValue());
				ruleExpression.append("[]");
				RuleComponentDTO ruleDTO = rule.getRuleElement();
				ruleComponents.add(ruleDTO);
				logger.debug("Final  : ruleExpression" + ruleExpression);
			}
			logger.debug("List of ruleComponents needs to be added  : {}", ruleComponents);
			return ruleExpression.toString();
		} catch (Exception e) {
			logger.error("Either required fields are missing or invalid data provided!", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method get list of rule components and expression from slm service and
	 * convert these into RuleExpressionDTO, which is UI understandable format. This
	 * method is similar to the implementation of Infix to Postfix expression
	 * conversion.
	 * 
	 * @param ruleComponentDtos
	 * @param expression
	 * @return
	 */
	public static RuleDTO parseRuleExpression(Map<Integer, RuleComponentDTO> ruleComponentDtos, String expression) {
		if (ruleComponentDtos == null || ruleComponentDtos.size() == 0 || expression == null) {
			return null;
		}
		try {
			RuleDTO ruleExpression = new RuleDTO();
			List<RuleDTO> ruleList = new ArrayList<RuleDTO>();
			Stack<Character> tokens = new Stack<Character>();
			int number = 0;
			for (char ch : expression.toCharArray()) {
				if (ch == ' ') {
					continue;
				} else if (ch == '!') {
					tokens.push(ch);
				} else if (Character.isDigit(ch)) {
					number = (number * 10) + Character.getNumericValue(ch);
				} else {
					if (number != 0) {
						RuleDTO rule = new RuleDTO();
						if (!tokens.empty() && tokens.lastElement() == '!') {
							rule.setNegate(true);
							tokens.pop();
						} else {
							rule.setNegate(false);
						}
						rule.setSimpleExpr(true);
						rule.setRuleElement(ruleComponentDtos.get(number));
						ruleList.add(rule);
						number = 0;
					}

					if (ch == '(') {
						tokens.push(ch);
					} else if (ch == ')') {
						RuleDTO rule = new RuleDTO();
						rule.setSimpleExpr(false);
						List<RuleDTO> data = new ArrayList<RuleDTO>();
						char lastOperator = ' ';
						while (tokens.lastElement() != '(') {
							data.add(ruleList.get(ruleList.size() - 1));
							ruleList.remove(ruleList.size() - 1);
							lastOperator = tokens.pop();
						}
						data.add(ruleList.get(ruleList.size() - 1));
						ruleList.remove(ruleList.size() - 1);
						Collections.reverse(data);
						rule.setRuleExpressionList(data);
						rule.setOperator(Operator.getOperator(lastOperator + ""));

						if (tokens.pop() != '(') {
							throw new Exception("Invalid expression - " + expression);
						}
						if (!tokens.empty() && tokens.lastElement() == '!') {
							rule.setNegate(true);
							tokens.pop();
						} else {
							rule.setNegate(false);
						}
						ruleList.add(rule);
					} else {
						if (tokens.size() > 0) {
							char previousOperator = tokens.lastElement();
							if (previousOperator != '(' && previousOperator != ch) {
								throw new Exception("Different operators are not supported in same rule!");
							} else {
								tokens.push(ch);
							}
						} else {
							tokens.push(ch);
						}
					}
				}
			}
			if (ruleList.size() == 1) {
				ruleExpression = ruleList.get(0);
			} else if (ruleList.size() > 1 && tokens.size() > 0) {
				// This is to support the expressions created with the old
				// design
				RuleDTO rule = new RuleDTO();
				rule.setSimpleExpr(false);
				rule.setOperator(Operator.getOperator(tokens.pop() + ""));
				rule.setNegate(false);
				rule.setRuleExpressionList(ruleList);
				ruleExpression = rule;
			} else {
				throw new Exception("Error while parsing rule expression!" + expression);
			}
			return ruleExpression;
		} catch (Exception e) {
			logger.error("Invalid expression - " + expression, e);
			throw new RuntimeException(e);
		}
	}

	public void populateAudienceDTO(Segments segmentsEntity, AudienceDTO audienceDTO) throws ApiException {
		populateAudienceFromSegment(segmentsEntity, audienceDTO);
		populateRuleExpression(segmentsEntity, audienceDTO);
		populateAdvertiser(audienceDTO);
		populateDataSourceType(audienceDTO);
		populateCrmDetails(audienceDTO);
	}

	public void populateAudienceFromSegment(Segments segmentsEntity, AudienceDTO audienceDTO) {
		if (audienceDTO == null)
			audienceDTO = new AudienceDTO();
		try {

			Long segmentId = segmentsEntity.getId();
			String segmentName = segmentsEntity.getName();

			audienceDTO.setId(segmentId);
			audienceDTO.setName(segmentName);
			audienceDTO.setDescription(segmentsEntity.getDescription());
			audienceDTO.setDuration(segmentsEntity.getDuration());
			audienceDTO.setDurationUnit(segmentsEntity.getDurationUnit());
			audienceDTO.setActive((segmentsEntity.getStatus() == Status.ACTIVE) ? true
					: (segmentsEntity.getStatus() == Status.INACTIVE) ? false : null);
			audienceDTO.setRemoteSegmentId(segmentsEntity.getRemoteSegmentId());
			audienceDTO.setLicensee(
					elasticSearch.searchById(TablesEntity.LICENSEE, loginUserDetailsService.getLicenseeId()));
			/*
			 * To-Do : Fill this information from CRM
			 */
			// audienceDTO.totalUniqueUsers = segmentsEntity.getUuCount();
			// audienceDTO.dailyUniqueUsers = segmentsEntity.getDailyUUCount();
			UserDataType userDataType = segmentsEntity.getUserDataType() == null ? UserDataType.WEB_BROWSING
					: segmentsEntity.getUserDataType();
			audienceDTO.setUserDataType(userDataType.id);
			if (segmentsEntity.getSegmentType() != null
					&& segmentsEntity.getSegmentType().equals(SegmentType.CLICKER)) {
				audienceDTO.setSegmentType(SegmentType.CLICKER.id);
			} else if (segmentsEntity.getSegmentType() != null
					&& segmentsEntity.getSegmentType().equals(SegmentType.HASH_BUCKET)) {
				audienceDTO.setSegmentType(SegmentType.HASH_BUCKET.id);
			} else if (segmentsEntity.getSegmentType() != null
					&& segmentsEntity.getSegmentType().equals(SegmentType.DMP)) {
				audienceDTO.setSegmentType(SegmentType.DMP.id);
			} else if (segmentsEntity.getSegmentType() != null
					&& segmentsEntity.getSegmentType().equals(SegmentType.PLATFORM)) {
				audienceDTO.setSegmentType(SegmentType.PLATFORM.id);
			}

		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void populateRuleExpression(Segments segmentsEntity, AudienceDTO audienceDTO) {
		List<SegmentPixelMap> segmentPixelExpressions = segmentPixelMapRepository
				.findBySegmentId(segmentsEntity.getId());
		if (segmentPixelExpressions != null && segmentPixelExpressions.size() > 0) {
			SegmentPixelMap pixelExpression = segmentPixelExpressions.get(0);
			Long pixelId = pixelExpression.getPixelId();
			audienceDTO.setPixelId(pixelId);

			Map<Integer, RuleComponentDTO> ruleComponentDtos = new HashMap<Integer, RuleComponentDTO>();
			List<RuleComponent> ruleComponents = ruleComponentRepository
					.findBySegmentPixelExpressionId(pixelExpression.getId());
			if (ruleComponents != null && ruleComponents.size() > 0) {
				for (RuleComponent ruleComponent : ruleComponents) {
					RuleComponentDTO ruleComponentDTO = new RuleComponentDTO();
					ruleComponentDTO.setId(ruleComponent.getId());
					ruleComponentDTO.setFilterId(ruleComponent.getRuleFilterId());
					ruleComponentDTO.setOperatorId(ruleComponent.getRuleOperatorId());
					ruleComponentDTO.setValue(ruleComponent.getRuleValue());

					ruleComponentDtos.put(ruleComponent.getId(), ruleComponentDTO);
				}
			} else {
				logger.debug("Rule component list is null for the segment : {} & Pixel expression id : {}",
						segmentsEntity.getId(), pixelExpression.getId());
			}
			audienceDTO.setRuleExpression(parseRuleExpression(ruleComponentDtos, pixelExpression.getRuleExpression()));
		} else {
			logger.debug("segment pixel expression list is null for the segment {}", segmentsEntity.getId());
		}
	}

	public void populateAdvertiser(AudienceDTO audienceDTO) throws ApiException {
		logger.debug("inside populateAdvertiser to populate advertiser {}", audienceDTO);
		if (audienceDTO.getPixelId() == null)
			throw new ApiException("Internal Error. Couldn't find pixel info for this audience");

		AdvertiserToPixelEntity advertiserToPixel = dataPixelService.getAdvertiserToPixel(audienceDTO.getPixelId());

		if (advertiserToPixel.getAdvertiserId() == null)
			throw new ApiException("Internal Error. Couldn't find advertiser info for this audience");
		audienceDTO
				.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, advertiserToPixel.getAdvertiserId()));

		logger.debug("inside populateAdvertiser. Populated advertiserToPixel {}", advertiserToPixel);
	}

	public void populateDataSourceType(AudienceDTO audienceDTO) throws ApiException {
		logger.debug("inside populateDataSourceType to populate data source type {}", audienceDTO);

		if (audienceDTO.getDataSourceType() != null)
			return;

		DataPixelDTO dataPixel = dataPixelService.getDataPixel(audienceDTO.getPixelId());

		if (dataPixel == null)
			throw new ApiException("Internal Error. Couldn't find pixel info for this audience");

		audienceDTO.setDataSourceType(dataPixel.getSourceType().id);

		logger.debug("inside populateDataSourceType. Populate advertiser {}", dataPixel);
	}

	public void populateCrmDetails(AudienceDTO audienceDTO) throws ApiException {
		logger.debug("inside populateCrmDetails to populate crm data {}", audienceDTO);
		if (audienceDTO != null && audienceDTO.getPixelId() != null) {

			if (DataSourceType.FILE_UPLOAD.id.equals(audienceDTO.getDataSourceType())) {
				PixelDataFileDTO file = crmService.getPixelDataFileByPixelId(audienceDTO.getPixelId());
				audienceDTO.setPixelDataFile(file);
			} else if (DataSourceType.AUDIENCE_FEED.id.equals(audienceDTO.getDataSourceType())) {
				ServerSyncCoordinatorDTO coordinator = crmService.getSyncCoordinatorByPixelId(audienceDTO.getPixelId());
				PixelDataScheduleDTO pixelDataScheduleDTO = null;
				if (coordinator != null) {
					pixelDataScheduleDTO = new PixelDataScheduleDTO(coordinator);
				}
				audienceDTO.setPixelDataSchedule(pixelDataScheduleDTO);
			}

		} else {
			throw new ApiException("Internal Error. Couldn't find pixel info for this audienceDTO");
		}

		logger.debug("inside populateCrmDetails. Populated crm data {}", audienceDTO);
	}

	public static String prepareExpression(String expression, Integer[] ids) {
		if (expression == null)
			return null;
		logger.debug("Inside prepareExpression. ruleExpression : {} & component ids : {}", expression, ids);
		Matcher m = expressionIdPattern.matcher(expression);
		StringBuilder builder = new StringBuilder();
		int lastPosition = 0;
		while (m.find()) {
			logger.debug("Replacing id in expression. lastPosition : {} & Id number to be replaced : {}", lastPosition,
					m.group(1));
			builder.append(expression.substring(lastPosition, m.start()));
			lastPosition = m.end();
			builder.append(ids[Integer.parseInt(m.group(1))]);
		}
		builder.append(expression.substring(lastPosition));
		return builder.toString();
	}

	public static String prepareExpression(String ruleExpression, List<RuleComponent> componentIds) {
		logger.debug("Inside prepareExpression. ruleExpression : {} & componentIds : {}", ruleExpression, componentIds);
		Integer[] integers = new Integer[componentIds.size()];
		int i = 0;
		for (RuleComponent ruleComponentDto : componentIds)
			integers[i++] = ruleComponentDto.getId();
		return prepareExpression(ruleExpression, integers);

	}

	public static void updateEntityFromDto(AudienceDTO dto, Segments entity) {

		entity.setName(dto.getName().trim());
		entity.setDescription(dto.getDescription());
		/*
		 * To-Do check if duration is allowed to update
		 */
		// entity.setDuration(dto.getDuration());
		// entity.setDurationUnit(dto.getDurationUnit());

	}

	public AdvertiserSegmentMappingEntity getEntity(Long advertiserId, Long segmentId, String remoteSegmentId) {
		AdvertiserSegmentMappingEntity entity = new AdvertiserSegmentMappingEntity();
		entity.setAdvertiserId(advertiserId);
		entity.setSegmentId(segmentId);
		entity.setRemoteSegmentId(remoteSegmentId);
		entity.setIsActive(true);
		entity.setLicenseeId(loginUserDetailsService.getLicenseeId());
		return entity;
	}

	public String getPlatformAudienceRelativeUrl(String s3url) {
		return s3url.replace(applicationProperties.getS3SegmentBucketUrlPath(), "");
	}

	public static void main(String[] args) {
		String s3 = "s3://revx-segments/affle-dmp/affle-dmp/1/Segment_1.csv.gz";
		System.out.println(s3.replace("s3://revx-segments/", ""));
	}

	public static AudienceESDTO getESDTO(AudienceDTO audienceDTO) {
		AudienceESDTO audienceESDTO = new AudienceESDTO();
		audienceESDTO.setId(audienceDTO.getId());
		audienceESDTO.setName(audienceDTO.getName());

		if (audienceDTO.getSegmentType() != null && audienceDTO.getSegmentType() == SegmentType.DMP.id) {
			audienceESDTO.setAdvertiserId(-1L);
			audienceESDTO.setAdvertiserName(SegmentType.DMP.name());
			audienceESDTO.setLicenseeId(-1L);
		} else {
			audienceESDTO.setAdvertiserId(audienceDTO.getAdvertiser().getId());
			audienceESDTO.setAdvertiserName(audienceDTO.getAdvertiser().getName());
			audienceESDTO.setLicenseeId(audienceDTO.getLicensee().getId());
		}
		audienceESDTO.setSegmentType(audienceDTO.getSegmentType());
		audienceESDTO.setActive(audienceDTO.isActive());
		audienceESDTO.setCreationTime(audienceDTO.getCreationTime());
		audienceESDTO.setModifiedTime(audienceDTO.getModifiedTime());
		audienceESDTO.setPixelId(audienceDTO.getPixelId());

		audienceESDTO.setUser_data_type(getAudienceType(audienceDTO));

		return audienceESDTO;
	}

	public static AudienceESDTO getDmpESDTO(AudienceDTO audienceDTO) {
		AudienceESDTO audienceESDTO = new AudienceESDTO();
		if (audienceDTO.getSegmentType() == null || audienceDTO.getSegmentType() != SegmentType.DMP.id)
			return audienceESDTO;

		audienceESDTO.setId(audienceDTO.getId());
		audienceESDTO.setName(audienceDTO.getName());
		audienceESDTO.setAdvertiserId(audienceDTO.getAdvertiser().getId());
		audienceESDTO.setAdvertiserName(audienceDTO.getAdvertiser().getName());
		audienceESDTO.setLicenseeId(audienceDTO.getLicensee().getId());
		audienceESDTO.setSegmentType(audienceDTO.getSegmentType());
		audienceESDTO.setActive(audienceDTO.isActive());
		audienceESDTO.setCreationTime(audienceDTO.getCreationTime());
		audienceESDTO.setModifiedTime(audienceDTO.getModifiedTime());
		audienceESDTO.setPixelId(audienceDTO.getPixelId());
		
		audienceESDTO.setUser_data_type(getAudienceType(audienceDTO));

		return audienceESDTO;
	}

	private static String getAudienceType(AudienceDTO audienceDTO) {
		if (audienceDTO.getSegmentType() == SegmentType.DMP.id) {
			return AudienceType.DMP.getAudienceType();
		}

		if (audienceDTO.getUserDataType() == 1) {
			return AudienceType.WEB_BROWSING.getAudienceType();
		} else if (audienceDTO.getUserDataType() == 2) {
			return AudienceType.MOBILE_APP.getAudienceType();
		} else {
			return null;
		}
	}
}
