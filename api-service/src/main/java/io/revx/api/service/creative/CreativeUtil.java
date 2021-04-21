/*
 * @author: ranjan-pritesh
 * 
 * @date:2 jan 2020
 */
package io.revx.api.service.creative;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;
import com.google.gson.Gson;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.constants.ApiConstant;
import io.revx.api.mysql.entity.creative.CDNEntity;
import io.revx.api.mysql.entity.creative.CreativeAssetEntity;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.entity.creative.CreativeTemplateEntity;
import io.revx.api.mysql.entity.creative.CreativeTemplateThemeEntity;
import io.revx.api.mysql.entity.creative.CreativeTemplateVariablesEntity;
import io.revx.api.mysql.entity.creative.DcoAttributesEntity;
import io.revx.api.mysql.entity.creative.PerformanceDataEntity;
import io.revx.api.mysql.entity.creative.VideoAttributeEntity;
import io.revx.api.mysql.repo.clickdestination.ClickDestinationRepository;
import io.revx.api.mysql.repo.creative.CDNRepository;
import io.revx.api.mysql.repo.creative.CreativeAssetRepository;
import io.revx.api.mysql.repo.creative.DcoAttributeRepository;
import io.revx.api.mysql.repo.creative.PerformanceDataRepository;
import io.revx.api.mysql.repo.creative.VideoAttributesRepository;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.reportbuilder.redshift.BigQueryConnectionUtil;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.ModelConverterService;
import io.revx.api.service.catalog.CatalogUtil;
import io.revx.core.constant.Constants;
import io.revx.core.exception.ApiException;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Creative;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.creative.CompanionAdDetails;
import io.revx.core.model.creative.CompanionFormat;
import io.revx.core.model.creative.CreativeCompactDTO;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeHtmlFile;
import io.revx.core.model.creative.CreativePerformanceData;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.creative.CreativeTemplateDTO;
import io.revx.core.model.creative.CreativeType;
import io.revx.core.model.creative.DcoAttributeType;
import io.revx.core.model.creative.DcoAttributesDTO;
import io.revx.core.model.creative.NativeAssetData;
import io.revx.core.model.creative.NativeAssetPojo;
import io.revx.core.model.creative.Size;
import io.revx.core.model.creative.TemplateThemeDTO;
import io.revx.core.model.creative.TemplateVariablesDTO;
import io.revx.core.model.creative.VastCreative;
import io.revx.core.model.creative.VastProtocol;
import io.revx.core.model.creative.VideoAttributes;
import io.revx.core.model.creative.VideoCampanionType;
import io.revx.core.model.creative.VideoCreativeVastXmlTemplate;
import io.revx.core.model.creative.VideoDetails;
import io.revx.core.model.creative.VideoUploadType;
import io.revx.core.model.requests.DashboardRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.revx.core.constant.Constants.DYNAMIC_NOENCODING;

/**
 * The Class CreativeUtil.
 */
@Component
public class CreativeUtil {

	/** The logger. */
	private static final Logger logger = LogManager.getLogger(CreativeUtil.class);

	/** The user. */
	@Autowired
	LoginUserDetailsService user;

	/** The properties. */
	@Autowired
	ApplicationProperties properties;

	/** The elastic search. */
	@Autowired
	EntityESService elasticSearch;

	/** The xml util. */
	@Autowired
	VastXmlUtil xmlUtil;

	/** The cd repo. */
	@Autowired
	ClickDestinationRepository cdRepo;

	@Autowired
	CDNRepository cdn;

	/** The converter. */
	@Autowired
	ModelConverterService converter;

	/** The catalog util. */
	@Autowired
	CatalogUtil catalogUtil;

	/** The va repo. */
	@Autowired
	VideoAttributesRepository vaRepo;

	/** The ca repo. */
	@Autowired
	CreativeAssetRepository caRepo;

	/** The da repo. */
	@Autowired
	DcoAttributeRepository daRepo;

	/** The pf repo. */
	@Autowired
	PerformanceDataRepository pfRepo;

	@Autowired
	CreativeService service;

	/** The creative validator. */
	@Autowired
	CreativeValidationService creativeValidator;

	@Autowired
	CreativeMockUpUtil creativeMockUpUtil;

	@Autowired
	BigQueryConnectionUtil bigQueryConnectionUtil;

	private static final String PERFORMANCE_QUERY_TEMPLATE_WITH_TIMESTAMP =
			"select creative_id, SUM(impressions) AS impressions, SUM(clicks) AS clicks, SUM(view_conversion) AS view_conversions,"+
					"SUM(click_conversions) AS click_conversions, from |#*TABLE_NAME*#| where creative_id in (|#*CREATIVE_IDS*#|) and " +
					"ts_utc_day >= |#*START_TIME*#| and ts_utc_day < |#*END_TIME*#| group by creative_id";

	private static final String PERFORMANCE_QUERY_TEMPLATE_WITHOUT_TIMESTAMP =
			"select creative_id, SUM(impressions) AS impressions, SUM(clicks) AS clicks, SUM(view_conversion) AS view_conversions,"+
					"SUM(click_conversions) AS click_conversions, from |#*TABLE_NAME*#| where creative_id in (|#*CREATIVE_IDS*#|) " +
					" group by creative_id";

	private static final String TABLE_NAME = "|#*TABLE_NAME*#|";
	private static final String CREATIVE_IDS = "|#*CREATIVE_IDS*#|";
	private static final String START_TIME = "|#*START_TIME*#|";
	private static final String END_TIME = "|#*END_TIME*#|";

	private static final String HTTP_PROTOCOL = "http";
	private static final String HTTPS_PROTOCOL = "https";

	/**
	 * Gets the url path from preview.
	 *
	 * @return the url path from preview
	 */
	private String getUrlPathFromPreview(String previewUrl) {

		if (StringUtils.isNotBlank(previewUrl)) {

			String source = previewUrl.replace(properties.getCreativeUrlPrependTemp(),
					properties.getCreativeDirectoryPath());
			String destination = source.replace("cr_temp/", "creatives/");

			File sorce = new File(source);
			File destn = new File(destination);

			try {
				FileUtils.copyFile(sorce, destn);
			} catch (IOException e) {
				logger.debug("copying file to url path location directory got an Exception {}",
						ExceptionUtils.getStackTrace(e));
			}

			return destn.getPath().replace(properties.getCreativeDirectoryPath() + properties.getCreativesDirectory(),
					"/");

		}

		return null;
	}

	/**
	 * Generate creative name.
	 *
	 * @param file     the file
	 * @param baseName the base name
	 * @return the string
	 */
	public String generateCreativeName(CreativeFiles file, String baseName) {

		if (StringUtils.isBlank(file.getName()))
			return null;

		if (file.getType().equals(CreativeType.html) || file.getType().equals(CreativeType.zippedHTML))
			return baseName + Constants.HYPHEN + generateUniqueId();
		else
			return baseName + Constants.HYPHEN + file.getHeight() + Constants.HYPHEN + file.getWidth()
					+ Constants.HYPHEN + generateUniqueId();

	}

	/**
	 * Generate ad tag creative name.
	 *
	 * @param name  the name
	 * @param advId the adv id
	 * @return the string
	 */
	public String generateAdTagCreativeName(String name, Long advId) {

		if (StringUtils.isBlank(name))
			return null;

		String extension = "html";
		return name + Constants.HYPHEN + Constants.AD_TAG + Constants.HYPHEN + advId + Constants.HYPHEN
				+ generateUniqueId() + "." + extension;
	}

	/**
	 * Generate unique id.
	 *
	 * @return the string
	 */
	public String generateUniqueId() {
		return UUID.randomUUID().toString().split("-")[0];
	}

	/**
	 * Populate creative entity.
	 *
	 * @param creativeDTOs the creative DT os
	 * @return the list
	 */
	@Transactional
	public List<CreativeEntity> populateCreativeEntities(List<CreativeDTO> creativeDTOs) {
		return creativeDTOs.stream().map(c -> {
			try {
				return populateCreativeEntity(c);
			} catch (ApiException e) {
				logger.error("Exception occured while populating creative entites. {}",
						org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(e));
			}
			return null;
		}).collect(Collectors.toList());

	}

	/**
	 * Populate creative entity.
	 *
	 * @return the list
	 * @throws ApiException
	 */
	@Transactional
	public CreativeEntity populateCreativeEntity(CreativeDTO creativeDTO) throws ApiException {

		if (!creativeDTO.getType().equals(CreativeType.video) && !creativeDTO.getType().equals(CreativeType.nativeVideo)
				&& StringUtils.isNotBlank(creativeDTO.getErrorMsg()))
			return null;

		CreativeEntity entity = new CreativeEntity();
		creativeValidator.isvalidClickDestinationId(creativeDTO.getClickDestination().getId());

		entity.setClickDestination(creativeDTO.getClickDestination().getId());

		entity.setStatus(CreativeStatus.get(1));
		entity.setCreationDate(System.currentTimeMillis() / 1000);
		entity.setCreatedBy(user.getUserInfo().getUserId());
		entity.setType(creativeDTO.getType());
		entity.setTemplateBased(creativeDTO.isTemplateBased());

		populateHeightAndWidth(creativeDTO, entity);

		String creativeName = creativeDTO.getName();
		if (creativeDTO.getType() != null && (creativeDTO.getType().equals(CreativeType.video)
				|| creativeDTO.getType().equals(CreativeType.nativeVideo))) {
			if (creativeDTO.getVideoAttributes() != null && creativeDTO.getVideoAttributes().get(0) != null
					&& creativeDTO.getVideoAttributes().get(0).getVideoWidth() != null
					&& creativeDTO.getVideoAttributes().get(0).getVideoHeight() != null) {
				creativeName += Constants.HYPHEN + creativeDTO.getVideoAttributes().get(0).getVideoWidth() + "x"
						+ creativeDTO.getVideoAttributes().get(0).getVideoHeight();
			}
		} else if (creativeDTO.getType() != null && creativeDTO.getType().equals(CreativeType.nativeAd)) {
			if (creativeDTO.getNativeAsset() != null && creativeDTO.getNativeAsset().getWidth() != null
					&& creativeDTO.getNativeAsset().getHeight() != null) {
				creativeName += Constants.HYPHEN + creativeDTO.getNativeAsset().getWidth() + "x"
						+ creativeDTO.getNativeAsset().getHeight();
			}

		} else if (entity.getHeight() != null && entity.getWidth() != null) {
			creativeName += Constants.HYPHEN + entity.getWidth() + "x" + entity.getHeight();
		}
		entity.setName(creativeName);

		if (StringUtils.isNotBlank(creativeDTO.getThirdPartyAdTag())) {
			String adTagCrName = removeSpaceFromName(creativeDTO.getName()) + Constants.HYPHEN
					+ creativeDTO.getAdvertiser().getId() + Constants.HYPHEN + Constants.AD_TAG + Constants.HYPHEN
					+ Constants.HTML;

			entity.setName(adTagCrName);
		}

		if (creativeDTO.getUrlPath() != null && (creativeDTO.getUrlPath().equals("__DEFAULT_IMAGE__")
				|| creativeDTO.getUrlPath().equals("__ADDITIONAL_IMAGES__"))) {
			entity.setName(creativeDTO.getName().replace(Constants.IMAGE, Constants.MACRO));
		}

		entity.setAdvertiserId(creativeDTO.getAdvertiser().getId());
		entity.setLicenseeId(user.getLicenseeId());

		if (creativeDTO.getUrlPath() != null && (creativeDTO.getUrlPath().equals("__DEFAULT_IMAGE__")
				|| creativeDTO.getUrlPath().equals("__ADDITIONAL_IMAGES__"))) {
			entity.setUrlPath(creativeDTO.getUrlPath());
		} else {
			entity.setUrlPath(getUrlPathFromPreview(
					StringUtils.isNotBlank(creativeDTO.getPreviewUrl()) ? creativeDTO.getPreviewUrl() : null));
		}

		if (creativeDTO.getType().equals(CreativeType.video) || creativeDTO.getType().equals(CreativeType.nativeVideo))
			entity.setVideoAttributesId(saveVideoAttributeAndGetId(creativeDTO));

		entity.setContent(populateCreativeContent(creativeDTO,false));

		/*
		 * Setting aelp for html to true since its not coming from UI. We are defaulting
		 * in the UI.
		 */
		if (CreativeType.html.name().equals(creativeDTO.getType().toString()))
			entity.setAelp(true);

		entity.setCdnId(1L);

		if (creativeDTO.isNativeAd())
			entity.setCreativeAsset(saveNativeAssetAndGet(creativeDTO));

		entity.setIsDco(creativeDTO.isDcoAd());
		entity.setIsRefactored(Boolean.TRUE);

		if (entity.getIsDco())
			entity.setDcoAttributesId(saveDcoAttributesAndGetId(creativeDTO, null));

		entity.setAttributesId(0L);
		entity.setIsMraid(Boolean.FALSE);

		return entity;
	}

	public String removeSpaceFromName(String name) {
		return name.trim().replace(' ', '-');
	}

	/**
	 * Populate height and width.
	 *
	 * @param creativeDTO the creative DTO
	 * @param entity      the entity
	 */
	private void populateHeightAndWidth(CreativeDTO creativeDTO, CreativeEntity entity) {

		switch (creativeDTO.getType()) {

		case video:
			entity.setHeight(1);
			entity.setWidth(1);
			break;

		case nativeAd:
			entity.setHeight(0);
			entity.setWidth(0);
			break;

		case nativeVideo:
			entity.setHeight(2);
			entity.setWidth(2);
			break;

		default:
			if (creativeDTO.getSize() != null)
				entity.setHeight(creativeDTO.getSize().getHeight());
			entity.setWidth(creativeDTO.getSize().getWidth());
			break;
		}

	}

	/**
	 * Save dco attributes and get id.
	 *
	 * @param creativeDTO the creative DTO
	 * @return the long
	 */
	@Transactional
	private Long saveDcoAttributesAndGetId(CreativeDTO creativeDTO, Long dcoAttrId) {
		Long daId = null;
		DcoAttributesEntity entity = new DcoAttributesEntity();
		DcoAttributesDTO dcoAttribute = creativeDTO.getDcoAttributes();

		if (dcoAttribute != null) {
			entity.setNoOfSlots(dcoAttribute.getNoOfSlots());
			entity.setMacroList(dcoAttribute.getMacroList());
			entity.setDcoAttribute(DcoAttributeType.html);
		} // if dco native image then dcoAttribute will b null
		else if (creativeDTO.getType().equals(CreativeType.nativeAd)) {
			entity.setNoOfSlots(1);
			entity.setDcoAttribute(DcoAttributeType.image);
		}

		if (dcoAttrId != null) {
			entity.setId(dcoAttrId);
			entity.setCreativeId(creativeDTO.getId());
		}

		entity = daRepo.save(entity);

		if (entity != null)
			daId = entity.getId();
		return daId;
	}

	/**
	 * Save video attribute and get id.
	 *
	 * @param creativeDto the creative dto
	 * @return the long
	 */
	@Transactional
	public Long saveVideoAttributeAndGetId(CreativeDTO creativeDto) {

		Long vaId = null;
		StringBuilder videoPath = new StringBuilder();
		StringBuilder companionPath = new StringBuilder();

		VideoAttributeEntity entity = new VideoAttributeEntity();

		if (creativeDto.getVideoAttributes() != null && creativeDto.getVideoUploadType().equals(VideoUploadType.VIDEO)
				&& (creativeDto.getType().equals(CreativeType.video)
						|| creativeDto.getType().equals(CreativeType.nativeVideo))) {
			List<VideoAttributes> videoAttributes = creativeDto.getVideoAttributes();
			entity.setDuration(videoAttributes.get(0).getDurationInSecs());
			entity.setFormat(videoAttributes.get(0).getFormat());
			if (videoAttributes.get(0).getIsSkippable() != null)
				entity.setIsSkippable(videoAttributes.get(0).getIsSkippable() == 1);

			entity.setWidth(videoAttributes.get(0).getVideoWidth());
			entity.setHeight(videoAttributes.get(0).getVideoHeight());
			entity.setVastProtocol(VastProtocol.VAST_3_WRAPPER);

			if (videoAttributes.get(0).getCompanionType() != null)
				entity.setCompanionType(videoAttributes.get(0).getCompanionType());
			else
				entity.setCompanionType(VideoCampanionType.NO_COMPANION);

			if (videoAttributes.get(0).getCompanionCreativeId() != null)
				entity.setCompanionCreativeId(videoAttributes.get(0).getCompanionCreativeId());
			else
				entity.setCompanionCreativeId(-1L);

			if (videoAttributes.get(0).getCompanionHeight() != null)
				entity.setCompanionHeight(videoAttributes.get(0).getCompanionHeight());
			else
				entity.setCompanionHeight(-1);

			if (videoAttributes.get(0).getCompanionWidth() != null)
				entity.setCompanionWidth(videoAttributes.get(0).getCompanionWidth());
			else
				entity.setCompanionWidth(-1);

			videoAttributes.forEach(
					v -> v.setVideoPath(properties.getCreativeUrlPrepend() + getUrlPathFromPreview(v.getVideoPath())));
			videoAttributes.forEach(v -> {
				if (StringUtils.isNotBlank(v.getCompanionPath())) {
					v.setCompanionPath(
							properties.getCreativeUrlPrepend() + getUrlPathFromPreview(v.getCompanionPath()));
				}
			});

			videoAttributes.forEach(v -> videoPath.append(v.getVideoPath())
												  .append("/n"));
			entity.setVideoPath(videoPath.toString());

			videoAttributes.stream()
						   .filter(v -> v.getCompanionPath() != null)
						   .forEach(v -> companionPath.append(v.getCompanionPath())
													  .append("/n"));
			if (StringUtils.isNotBlank(companionPath.toString())) {
				entity.setCompanionPath(companionPath.toString());
			}

		}

		if (creativeDto.getVideoAttributes() == null
				&& creativeDto.getVideoUploadType().equals(VideoUploadType.VAST_WRAPPER)
				&& creativeDto.getType().equals(CreativeType.video) && creativeDto.getVastCreative() != null) {
			VastCreative vastCreative = creativeDto.getVastCreative();
			VideoCampanionType videoCampanionType = VideoCampanionType.NO_COMPANION;
			entity.setDuration(vastCreative.getDuration());
			entity.setFormat(vastCreative.getVideoFormat());
			entity.setVideoPath(vastCreative.getVideoLink());
			entity.setWidth(vastCreative.getSize().getWidth());
			entity.setHeight(vastCreative.getSize().getHeight());
			entity.setVastProtocol(VastProtocol.VAST_3_WRAPPER);

			if (vastCreative.getHasEndCard()) {
				videoCampanionType = VideoCampanionType.COMPANION_IMAGE;
			}

			entity.setCompanionType(videoCampanionType);
			entity.setCompanionCreativeId(-1L);
		}

		entity.setAdvertiserId(creativeDto.getAdvertiser().getId());
		entity.setLicenseeId(user.getLicenseeId());

		entity = vaRepo.save(entity);
		vaId = entity.getId();

		return vaId;
	}

	/**
	 * Save native asset and get id.
	 *
	 * @param creative the creative
	 * @return the long
	 * @throws ValidationException the validation exception
	 */
	@Transactional
	private CreativeAssetEntity saveNativeAssetAndGet(CreativeDTO creative) throws ValidationException {

		Long id = null;
		CreativeAssetEntity entity = new CreativeAssetEntity();
		if (creative.getSize() != null) {
			entity.setHeight(ApiConstant.NATIVE_AD_HEIGHT);
			entity.setWidth(ApiConstant.NATIVE_AD_WIDTH);
		}
		entity.setAdvertiserId(creative.getAdvertiser().getId());
		entity.setLicenseeId(user.getLicenseeId());

		if (creative.getNativeAsset() != null)
			entity.setAssetData(populatedataForNativeAsset(creative.getNativeAsset(), creative.isDcoAd(),
					creative.getAdvertiser().getId()).getBytes());
		else
			entity.setAssetData(null);

		entity = caRepo.save(entity);

		if (entity != null)
			id = entity.getId();

		return entity;
	}

	/**
	 * Populatedata for native asset.
	 *
	 * @param nativeAsset the native asset
	 * @param advId
	 * @return the string
	 * @throws ValidationException the validation exception
	 */
	private String populatedataForNativeAsset(NativeAssetPojo nativeAsset, boolean isDco, Long advId)
			throws ValidationException {

		NativeAssetData data = new NativeAssetData();

		String title = nativeAsset.getTitle();
		String bodyText = nativeAsset.getBody();
		if (nativeAsset.getCallToAction() == null || nativeAsset.getIconurl() == null) {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER_IN_REQUEST,
					new Object[] { "nativeAsset call to Action/icon url" });
		}

		creativeValidator.validateNativeText(title, true, false, isDco);
		creativeValidator.validateNativeText(bodyText, false, true, isDco);
		data.setctaText(nativeAsset.getCallToAction());
		try {
			data.setIcon(getCdnPathForIcon(nativeAsset.getIconurl(), advId));
		} catch (Exception e) {
			logger.error("Exception occured while getting ICon url..........for native");
			e.printStackTrace();
			throw new ValidationException(ErrorCode.INVALID_ICON_PATH, Constants.INVALID_ICON_PATH);
		}
		data.setTitle(title);
		data.setBody(bodyText);

		Gson gson = new Gson();

		return gson.toJson(data);
	}

	private String getCdnPathForIcon(String iconurl, Long advId) throws Exception {

		if (StringUtils.isBlank(iconurl))
			return null;

		if (iconurl.contains(properties.getCreativeUrlPrependTemp())) {
			String source = iconurl.replace(properties.getCreativeUrlPrependTemp(),
					properties.getCreativeDirectoryPath());
			String fileName = source.replace(properties.getTemporaryCreativeDirectoryPath(), "");
			String[] fileParts = fileName.split("\\.");
			String ext = fileParts.length > 0 ? fileParts[1] : "";

			String destination = properties.getCreativeDirectoryPath() + properties.getCreativesDirectory() + advId
					+ "/logos/";
			String destnFileNme = System.currentTimeMillis() + "." + ext;

			File sorce = new File(source);
			File destinationDir = new File(destination);

			File destnFilePath = new File(destination + destnFileNme);

			if (!destinationDir.exists()) {
				destinationDir.mkdirs();
				// If you require it to make the entire directory path including parents,
				// use directory.mkdirs(); here instead.
			}

			try {
				if (!sorce.equals(destnFilePath)) {
					FileUtils.copyFile(sorce, destnFilePath);
				}
			} catch (IOException e) {
				logger.debug("copying file to url path location directory got an Exception {}",
						ExceptionUtils.getStackTrace(e));
				throw e;
			}
			boolean changeProtocol = false;
			return destnFilePath.getPath().replace(
					properties.getCreativeDirectoryPath() + properties.getCreativesDirectory(), getCDNHostname(changeProtocol) + "/");

		}

		return iconurl;
	}

	/**
	 * Populate creative content.
	 *
	 * @param creativeDTO the creative DTO
	 * @return the string
	 * @throws ApiException
	 */
	private String populateCreativeContent(CreativeDTO creativeDTO, boolean isUpdate) throws ApiException {

		String vastXml = null;

		if ((creativeDTO.getType().equals(CreativeType.zippedHTML) || creativeDTO.getType().equals(CreativeType.html))
				&& !creativeDTO.isTemplateBased()) {
			return creativeDTO.getContent().trim();
		}

		// When updating the content of HTML based creative a new HTML file needs to be generated which will
		// be used for preview
		if (creativeDTO.getType().equals(CreativeType.zippedHTML) && creativeDTO.isTemplateBased()
				&& StringUtils.isNotBlank(creativeDTO.getContent())) {
			String htmlContent = creativeDTO.getContent().trim();
			if (creativeDTO.isDcoAd()) {
				htmlContent = htmlContent.replace( creativeDTO.getDynamicItemList(),DYNAMIC_NOENCODING);
			}
			if (isUpdate) {
				String preViewUrl = creativeMockUpUtil.getPreviewUrlForTemplate(new CreativeHtmlFile(),
						creativeDTO.getAdvertiserId(), creativeDTO, true);
				creativeDTO.setPreviewUrl(preViewUrl);
			}
			return htmlContent;
		}

		if (creativeDTO.getVideoUploadType() != null && creativeDTO.getVideoUploadType().equals(VideoUploadType.VIDEO)
				&& (creativeDTO.getType().equals(CreativeType.video)
						|| creativeDTO.getType().equals(CreativeType.nativeVideo))) {
			vastXml = xmlUtil.generateVastXml(null, creativeDTO.getVideoUploadType(),
					populateVastXmlTemplate(creativeDTO), creativeDTO.getVastCreative(), creativeDTO.getName())
					.toString();

			return vastXml.trim();
		}

		if (creativeDTO.getVideoUploadType() != null
				&& creativeDTO.getVideoUploadType().equals(VideoUploadType.VAST_WRAPPER)
				&& (creativeDTO.getType().equals(CreativeType.video))) {
			vastXml = xmlUtil.generateVastXml(null, creativeDTO.getVideoUploadType(), null,
					creativeDTO.getVastCreative(), creativeDTO.getName()).toString();
			String unescapedXmlContent = vastXml.trim();
			return StringEscapeUtils.escapeXml(unescapedXmlContent);
		}
		return null;
	}

	/**
	 * Populate vast xml template.
	 *
	 * @param creativeDTO the creative DTO
	 * @return the video creative vast xml template
	 * @throws ApiException
	 */
	private VideoCreativeVastXmlTemplate populateVastXmlTemplate(CreativeDTO creativeDTO) throws ApiException {

		VideoCreativeVastXmlTemplate template = new VideoCreativeVastXmlTemplate();
		template.setCreativeName(creativeDTO.getName());
		template.setDuration(creativeDTO.getVideoAttributes().get(0).getDurationInSecs());
		try {
			template.setVideos(populateVideoDetails(creativeDTO.getVideoAttributes()));
			template.setCompanionAds(populateLandscapeCompanionAds(creativeDTO.getVideoAttributes()));
		} catch (Exception e) {
			logger.error("Cant get CDN hostname from database...........");
			e.printStackTrace();
			throw new ApiException(ErrorCode.VAST_FORMATION_EXCEPTION,
					new Object[] { "Error while creating vast xml." });
		}
		return template;
	}

	/**
	 * Populate landscape companion ads.
	 *
	 * @param videoAttributes the video attributes
	 * @return the sets the
	 * @throws Exception
	 */
	private Set<CompanionAdDetails> populateLandscapeCompanionAds(List<VideoAttributes> videoAttributes)
			throws Exception {

		Set<CompanionAdDetails> companions = new HashSet<>();

		for (VideoAttributes va : videoAttributes) {
			CompanionAdDetails companion = new CompanionAdDetails();

			if (va.getCompanionPath() != null) {
				boolean changeProtocol = changeHostNameProtocol();
				companion.setImageLink(
						va.getCompanionPath().replace(properties.getCreativeUrlPrepend(), getCDNHostname(changeProtocol)));

				if (va.getCompanionContentType() != null)
					companion.setImageFormat(
							CompanionFormat.getByXmlAttributeValue(va.getCompanionContentType().getType()));

				companion.setSize(new Size(va.getCompanionHeight(), va.getCompanionWidth()));
				companions.add(companion);
			}
		}

		return companions;
	}

	/**
	 * Populate potrait companion ads.
	 *
	 * @param videoAttributes the video attributes
	 * @return the sets the
	 * @throws Exception
	 */
	private Set<CompanionAdDetails> populatePotraitCompanionAds(List<VideoAttributes> videoAttributes)
			throws Exception {

		Set<CompanionAdDetails> companions = new HashSet<>();

		for (VideoAttributes va : videoAttributes) {
			CompanionAdDetails companion = new CompanionAdDetails();

			if (va.getCompanionPath() != null) {
				boolean changeProtocol = changeHostNameProtocol();
				companion.setImageLink(
						va.getCompanionPath().replace(properties.getCreativeUrlPrepend(), getCDNHostname(changeProtocol)));

				if (va.getCompanionContentType() != null)
					companion.setImageFormat(
							CompanionFormat.getByXmlAttributeValue(va.getCompanionContentType().getType()));

				companion.setSize(new Size(va.getCompanionHeight(), va.getCompanionWidth()));
				companions.add(companion);
			}
		}
		return companions;
	}

	/**
	 * Populate video details.
	 *
	 * @param videoAttributes the video attributes
	 * @return the sets the
	 * @throws Exception
	 */
	private Set<VideoDetails> populateVideoDetails(List<VideoAttributes> videoAttributes) throws Exception {

		Set<VideoDetails> setOfVideos = new HashSet<>();

		for (VideoAttributes va : videoAttributes) {
			VideoDetails video = new VideoDetails();
			if (va.getBitRate() != null)
				video.setBitrate(Integer.valueOf(va.getBitRate().replaceAll("[^\\d.]", "")));

			if (va.getDurationInSecs() != null)
				video.setDuration(va.getDurationInSecs().intValue());
			video.setSize(new Size(va.getVideoHeight(), va.getVideoWidth()));
			video.setVideoFormat(va.getFormat());
			boolean changeProtocol = changeHostNameProtocol();
			video.setVideoLink(va.getVideoPath().replace(properties.getCreativeUrlPrepend(), getCDNHostname(changeProtocol)));
			setOfVideos.add(video);
		}

		return setOfVideos;
	}

	private boolean changeHostNameProtocol() {
		String hostProtocol = properties.getVideoCreativeHostProtocol();
		boolean changeProtocol = false;
		if (hostProtocol.equals(HTTPS_PROTOCOL)) {
			changeProtocol = true;
		}
		return changeProtocol;
	}

	private String getCDNHostname(boolean changeProtocol) throws Exception {

		Optional<CDNEntity> cdnEntity = cdn.findById(1L);

		if (!cdnEntity.isPresent()) {
			throw new Exception("unable to get CDN hostname from database");
		}
		String baseUrl = cdnEntity.get().getBaseUrl();
		if (changeProtocol) {
			baseUrl = baseUrl.replace(HTTP_PROTOCOL,HTTPS_PROTOCOL);
		}
		return baseUrl;
	}

	/**
	 * Populate creative DTO from entity.
	 *
	 * @param creativefromDb the creativefrom db
	 * @return the list
	 * @throws Exception
	 */
	public List<CreativeDTO> populateCreativeDTOsFromEntities(List<CreativeEntity> creativefromDb) throws Exception {
		List<CreativeDTO> crDTO = new ArrayList<CreativeDTO>();
		try {
			for (CreativeEntity crEntity : creativefromDb) {
				crDTO.add(populateCreativeDTO(crEntity, false));
			}
		} catch (Exception e) {
			throw e;
		}
		return crDTO;
	}

	/**
	 * Populate creative DTO.
	 *
	 * @param c the c
	 * @return the creative DTO
	 * @throws Exception
	 */
	CreativeDTO populateCreativeDTO(CreativeEntity c, boolean needPerfData) throws Exception {

		CreativeDTO creative = new CreativeDTO();
		logger.debug("creative id " + c.getId());
		creative.setId(c.getId());
		creative.setName(c.getName());
		creative.setActive(c.getStatus().getValue() == 1);
		creative.setSize(new Size(c.getHeight(), c.getWidth()));
		creative.setContent(c.getContent());
		creative.setClickDestination(converter.convertFromClickDestEntity(
				cdRepo.findById(c.getClickDestination()).isPresent() ? cdRepo.findById(c.getClickDestination()).get()
						: null));
		creative.setType(c.getType());
		creative.setTemplateBased(c.getTemplateBased());
		creative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, c.getAdvertiserId()));
		creative.setUrlPath(c.getUrlPath());

		if (StringUtils.isNotBlank(c.getUrlPath()))
			creative.setPreviewUrl(properties.getCreativeUrlPrepend() + c.getUrlPath());

		if (c.getVideoAttributesId() != null) {
			creative.setVideoAttributes(populateVideoAttribute(vaRepo.findById(c.getVideoAttributesId()).isPresent()
					? vaRepo.findById(c.getVideoAttributesId()).get()
					: null));
			creative.setVideoUploadType(VideoUploadType.VIDEO); // setting this for now until the vast_wrapper not in
																// picture
		}

		// Setting Native asset
		CreativeAssetEntity caEntity = c.getCreativeAsset();
		if (caEntity != null) {
			creative.setNativeAsset(populateCreativeAsset(caEntity, c.getIsDco(), creative));
		}

		creative.setDcoAd(c.getIsDco());
		creative.setNativeAd(creative.getNativeAsset() != null ? true : false);

		if (c.getIsDco() && c.getDcoAttributesId() != null) {
			Optional<DcoAttributesEntity> dcoEntity = daRepo.findById(c.getDcoAttributesId());
			creative.setDcoAttributes(populateDcoAttrbuteFromEntity(dcoEntity.isPresent() ? dcoEntity.get() : null));
		}

		creative.setCreatedBy(c.getCreationDate());
		creative.setCreationTime(c.getCreationDate());
		creative.setRefactored(c.getIsRefactored());
		if (c.getType().equals(CreativeType.zippedHTML) && c.getTemplateBased() && c.getIsDco()) {
			creative.setDynamicItemList(constructMacroStringFromDcoDatabase(c.getAdvertiserId(),
					creative.getDcoAttributes().getNoOfSlots(), creative.getDcoAttributes().getMacroList()));
		}

		if (needPerfData) {
			try {
				// TODO: wrong implementation. Need to fetch the data from elastic search () or
				// get all creatives data in 1 go
				creative.setPerformanceData(service.fetchPerformanceById(c.getId()).getRespObject());
			} catch (Exception e) {
				logger.debug("Exception occured while getting creative performance data.............. !!!!!!");
				e.printStackTrace();
				throw e;
			}

		}

		return creative;
	}

	/**
	 * Populate creative compact DTO.
	 *
	 * @param c the c
	 * @return the creative compact DTO
	 * @throws Exception
	 */
	CreativeDTO populateCreativeToDTO(CreativeEntity c, boolean needPerfData) throws Exception {
		CreativeDTO creative = new CreativeDTO();
		try {
			creative.setId(c.getId());
			creative.setName(c.getName());
			creative.setActive(c.getStatus().getValue() == 1 ? true : false);
			creative.setSize(new Size(c.getHeight(), c.getWidth()));
			// creative.setContent(c.getContent());
			creative.setType(c.getType());
			creative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, c.getAdvertiserId()));
			creative.setUrlPath(c.getUrlPath());
			creative.setDcoAd(c.getIsDco());
			// creative.setNativeAd(c.getCreativeAsset() != null ? true : false);
			if (StringUtils.isNotBlank(c.getUrlPath()))
				creative.setPreviewUrl(properties.getCreativeUrlPrepend() + c.getUrlPath());
			creative.setCreatedBy(c.getCreationDate());
			creative.setCreationTime(c.getCreationDate());
			creative.setModifiedBy(c.getModifiedBy());
			creative.setModifiedTime(c.getModifiedOn());
			creative.setRefactored(c.getIsRefactored());
		} catch (Exception e) {
			logger.debug("Exception occured populating creative data to creative dto !!!!!!");
			e.printStackTrace();
			throw e;
		}

		if (needPerfData) {
			try {
				// TODO: wrong implementation. Need to fetch the data from elastic search () or
				// get all creatives data in 1 go
				creative.setPerformanceData(service.getPerformanceById(c.getId()).getRespObject());
			} catch (Exception e) {
				logger.error("Exception occured while getting creative performance data.............. !!!!!!");
				e.printStackTrace();
				throw e;
			}

		}
		return creative;
	}

	CreativeDTO populateCreativeToDTO(CreativeEntity c, boolean needPerfData, CreativePerformanceData perfData)
			throws Exception {
		CreativeDTO creative = new CreativeDTO();
		try {
			creative.setId(c.getId());
			creative.setName(c.getName());
			creative.setActive(c.getStatus().getValue() == 1);
			creative.setSize(new Size(c.getHeight(), c.getWidth()));
			// creative.setContent(c.getContent());
			creative.setType(c.getType());
			creative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, c.getAdvertiserId()));
			creative.setAdvertiserId(c.getAdvertiserId());
			creative.setUrlPath(c.getUrlPath());
			creative.setDcoAd(c.getIsDco());
			// creative.setNativeAd(c.getCreativeAsset() != null ? true : false);
			if (StringUtils.isNotBlank(c.getUrlPath()))
				creative.setPreviewUrl(properties.getCreativeUrlPrepend() + c.getUrlPath());
			creative.setCreatedBy(c.getCreationDate());
			creative.setCreationTime(c.getCreationDate());
			creative.setModifiedBy(c.getModifiedBy());
			creative.setModifiedTime(c.getModifiedOn());
			creative.setRefactored(c.getIsRefactored());
			creative.setTemplateBased(c.getTemplateBased());
		} catch (Exception e) {
			logger.debug("Exception occured populating creative data to creative dto !!!!!!");
			e.printStackTrace();
			throw e;
		}

		if (needPerfData) {
			try {
				if (perfData != null) {
					creative.setPerformanceData(perfData);
				}
				// else {
				// creative.setPerformanceData(service.getPerformanceById(c.getId()).getRespObject());
				// }
			} catch (Exception e) {
				logger.error("Exception occured while getting creative performance data.............. !!!!!!");
				e.printStackTrace();
				throw e;
			}

		}
		return creative;
	}

	/**
	 * Populate creative compact DTO.
	 *
	 * @param c the c
	 * @return the creative compact DTO
	 */
	CreativeCompactDTO populateCreativeCompactDTO(CreativeEntity c) {
		CreativeCompactDTO creative = new CreativeCompactDTO();
		creative.setId(c.getId());
		creative.setName(c.getName());
		creative.setActive(c.getStatus().getValue() == 1);
		creative.setSize(new Size(c.getHeight(), c.getWidth()));
		creative.setContent(c.getContent());
		creative.setType(c.getType());
		creative.setAdvertiser(elasticSearch.searchById(TablesEntity.ADVERTISER, c.getAdvertiserId()));
		creative.setUrlPath(c.getUrlPath());
		creative.setDcoAd(c.getIsDco());
		creative.setNativeAd(c.getCreativeAsset() != null);

		creative.setCreatedBy(c.getCreationDate());
		creative.setCreationTime(c.getCreationDate());
		creative.setModifiedBy(c.getModifiedBy());
		creative.setModifiedTime(c.getModifiedOn());
		creative.setRefactored(c.getIsRefactored());
		return creative;
	}

	/**
	 * Populate creative performance data.
	 *
	 * @param creativeid the creativeid
	 * @return the creative performance data
	 */
	public CreativePerformanceData populateCreativePerformanceData(Long creativeid) {

		if (creativeid == null)
			return null;

		List<PerformanceDataEntity> perfData = pfRepo.findAllByCreativeId(creativeid);

		long impressions = 0;
		long clicks = 0;
		long conversions = 0;
		double ctr = 0;
		double ctc = 0;

		for (PerformanceDataEntity p : perfData) {
			clicks += p.getClicks();
			impressions += p.getImpressions();
			conversions += p.getClickConversions() + p.getViewConversions();
		}

		if (impressions > 0) {
			ctr = ((double) clicks / impressions) * 100;
			ctr = Math.round(ctr * 100.0) / 100.0;
		}

		if (clicks != 0) {
			ctc = ((double) conversions / clicks) * 100;
			ctc = Math.round(ctc * 100.0) / 100.0;
		}

		CreativePerformanceData perf = new CreativePerformanceData();
		perf.setClicks(clicks);
		perf.setConversions(conversions);
		perf.setImpressions(impressions);
		perf.setCtc(ctc);
		perf.setCtr(ctr);
		return perf;
	}

	public CreativePerformanceData populateCreativePerformanceDataForCreativeIdAndTimeStamp(Long crId , DashboardRequest search) {

		if (crId == null) {
			return null;
		}
		
		logger.debug("[populateCreativePerformanceDataForCreativeId] Getting Performance Data for creative : {}", crId);

		List<Long> crIds = new ArrayList<>();
		crIds.add(crId);

		CreativePerformanceData crData = null;
		String queryString = generatePerformanceDataQuery(crIds, search.getDuration().getStartTimeStamp(),
				search.getDuration().getEndTimeStamp());
		crData = populatePerformanceData(queryString, crId);
		return crData;
	}

    public Map<Long, CreativePerformanceData> populateCreativePerformanceDataList(List<Long> creativeIds,
                                                                                  DashboardRequest search) {

	    if (creativeIds == null || creativeIds.isEmpty()) {
	        return null;
        }
        logger.debug("Getting Performance Data for creative list : {}", creativeIds);
        Map<Long, CreativePerformanceData> performanceDataMap = null;
        String queryString = generatePerformanceDataQuery(creativeIds, search.getDuration().getStartTimeStamp(),
                search.getDuration().getEndTimeStamp());

        performanceDataMap = populatePerformanceDataMap(queryString, creativeIds);

        if (performanceDataMap.isEmpty()) {
            return null;
        }
        return  performanceDataMap;
    }

	public CreativePerformanceData populateCreativePerformanceDataForCreativeId(Long crId) {

		if (crId == null) {
			return null;
		}
		
		logger.debug("[populateCreativePerformanceDataForCreativeId] Getting Performance Data for creative : {}", crId);

		List<Long> crIds = new ArrayList<>();
		crIds.add(crId);

		CreativePerformanceData crData = null;
		String queryString = generatePerformanceDataQueryWithOutTimestamp(crIds);
		crData = populatePerformanceData(queryString, crId);

		return crData;
	}

	private String generatePerformanceDataQuery(List<Long> ids, long startTime, long endTime) {
		String query = PERFORMANCE_QUERY_TEMPLATE_WITH_TIMESTAMP;

		query = query.replace(TABLE_NAME,properties.getCreativePerformanceTable());
		query = query.replace(START_TIME,String.valueOf(startTime));
		query = query.replace(END_TIME, String.valueOf(endTime));

		String creativeIds = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
		query = query.replace(CREATIVE_IDS, creativeIds);

		return query;
	}

	private String generatePerformanceDataQueryWithOutTimestamp(List<Long> ids) {
		String query = PERFORMANCE_QUERY_TEMPLATE_WITHOUT_TIMESTAMP;

		query = query.replace(TABLE_NAME,properties.getCreativePerformanceTable());

		String creativeIds = ids.stream().map(String::valueOf).collect(Collectors.joining(","));
		query = query.replace(CREATIVE_IDS, creativeIds);

		return query;
	}

	private Map<Long, CreativePerformanceData> populatePerformanceDataMap(String queryString, List<Long> creativeIds) {
        TableResult tableResult = null;
        try {
            tableResult = bigQueryConnectionUtil.
                    fetchQueryResult(properties.getReportingSourceDataSet(), queryString);

        } catch (InterruptedException e) {
            logger.error("Failed to execute the creative performance query: {} ", queryString);
            e.printStackTrace();
        }

        Map<Long, CreativePerformanceData> performanceDataMap = new HashMap<>();
        if (tableResult != null && tableResult.getTotalRows() != 0) {
            for (FieldValueList values : tableResult.getValues()) {
                Long creativeId = values.get(0).isNull() ? null : values.get(0).getLongValue();
                if (creativeId != null && creativeIds.contains(creativeId)) {
                    CreativePerformanceData crData = new CreativePerformanceData();
                    populatePerformanceEntity(values, crData);
                    performanceDataMap.put(creativeId, crData);
                }
            }
        }
        return performanceDataMap;
    }

	private CreativePerformanceData populatePerformanceData(String queryString, long crId) {
		TableResult tableResult = null;
		try {
			tableResult = bigQueryConnectionUtil.
					fetchQueryResult(properties.getReportingSourceDataSet(), queryString);

		} catch (InterruptedException e) {
			logger.error("Failed to execute the creative performance query: {} ", queryString);
			e.printStackTrace();
		}
		CreativePerformanceData crData = null;
		if (tableResult != null && tableResult.getTotalRows() != 0) {

			for (FieldValueList values : tableResult.getValues()) {
				Long creativeId = values.get(0).isNull() ? null : values.get(0).getLongValue();
				if (creativeId != null && creativeId.equals(crId)) {
					crData = new CreativePerformanceData();
					populatePerformanceEntity(values, crData);
					break;
				}
			}
		} else {
			logger.debug("Creative ID {} : No Performance Data for this creative", crId);
		}
		return crData;
	}

	private void populatePerformanceEntity(FieldValueList values, CreativePerformanceData crData) {
		long impressions = values.get(1).isNull() ? 0 : values.get(1).getLongValue();
		long clicks = values.get(2).isNull() ? 0 : values.get(2).getLongValue();
		long viewConversions = values.get(3).isNull() ? 0 : values.get(3).getLongValue();
		long clickConversions = values.get(4).isNull() ? 0 : values.get(4).getLongValue();
		crData.setImpressions(impressions);
		crData.setClicks(clicks);
		crData.setConversions(viewConversions + clickConversions);

		if (impressions > 0) {
			double ctr = ((double) clicks / impressions) * 100;
			ctr = Math.round(ctr * 100.0) / 100.0;
			crData.setCtr(ctr);
		}

		if (clicks != 0) {
			double ctc = ((double) crData.getConversions() / clicks) * 100;
			ctc = Math.round(ctc * 100.0) / 100.0;
			crData.setCtc(ctc);
		}
	}

	public Map<Long, CreativePerformanceData> populateCreativePerformanceDataForCreativeList(List<Long> crIds) {

		if (crIds == null || crIds.isEmpty()) {
			return null;
		}

		logger.debug("crIds : {}", crIds);
		List<PerformanceDataEntity> perfData = pfRepo.findAllByCreativeIdList(crIds);

		Gson g = new Gson();
		logger.debug("findAllByCreativeIdList result perfData : {}", g.toJson(perfData));

		Map<Long, CreativePerformanceData> creativePerfData = null;

		if (perfData != null && !perfData.isEmpty()) {
			creativePerfData = new HashMap<Long, CreativePerformanceData>();
			for (PerformanceDataEntity data : perfData) {
				CreativePerformanceData crData = new CreativePerformanceData();
				crData.setImpressions(data.getImpressions());
				crData.setClicks(data.getClicks());
				crData.setConversions(data.getClickConversions() + data.getViewConversions());

				if (data.getImpressions() > 0) {
					double ctr = ((double) data.getClicks() / data.getImpressions()) * 100;
					ctr = Math.round(ctr * 100.0) / 100.0;
					crData.setCtr(ctr);
				}

				if (data.getClicks() != 0) {
					double ctc = ((double) crData.getConversions() / data.getClicks()) * 100;
					ctc = Math.round(ctc * 100.0) / 100.0;
					crData.setCtc(ctc);
				}

				logger.debug("Adding to HashMap with Key {} : Value: {}", data.getCreativeId(), crData.toString());
				creativePerfData.put(data.getCreativeId(), crData);
			}
		}

		return creativePerfData;
	}

	/**
	 * Populate dco attrbute from entity.
	 *
	 * @param entity the entity
	 * @return the dco attributes DTO
	 */
	private DcoAttributesDTO populateDcoAttrbuteFromEntity(DcoAttributesEntity entity) {
		DcoAttributesDTO dcoAttribute = new DcoAttributesDTO();
		dcoAttribute.setCreativeId(entity.getCreativeId());
		dcoAttribute.setId(entity.getId());
		dcoAttribute.setDcoAttribute(entity.getDcoAttribute());
		dcoAttribute.setFallbackCreativeId(entity.getFallbackCreativeId());
		dcoAttribute.setMacroList(entity.getMacroList());
		dcoAttribute.setNoOfSlots(entity.getNoOfSlots());
		return dcoAttribute;
	}

	/**
	 * Populate creative asset.
	 *
	 * @param caEntity the ca entity
	 * @param isDco
	 * @return the native asset pojo
	 */
	private NativeAssetPojo populateCreativeAsset(CreativeAssetEntity caEntity, boolean isDco, CreativeDTO creative) {
		NativeAssetPojo asset = new NativeAssetPojo();
		asset.setId(caEntity.getId());
		asset.setData(caEntity.getAssetData());

		NativeAssetData data = new NativeAssetData();

		if (caEntity.getAssetData() != null) {
			String assetDataStr = new String(caEntity.getAssetData());

			Gson gson = new Gson();
			data = gson.fromJson(assetDataStr, NativeAssetData.class);

			asset.setTitle(data.getTitle());
			asset.setBody(data.getBody());
			asset.setCallToAction(data.getctaText());
			asset.setIconurl(data.getIcon());
		}

		if (isDco)
			populateNativeWithMacrosReplacedTitleBody(asset, caEntity.getAdvertiserId(), creative);

		if (caEntity.getHeight() != null)
			asset.setHeight(caEntity.getHeight().longValue());

		if (caEntity.getWidth() != null)
			asset.setWidth(caEntity.getWidth().longValue());
		asset.setAdvertiserId(caEntity.getAdvertiserId());
		asset.setLicenseeId(caEntity.getLicenseeId());
		return asset;
	}

	/**
	 * Populate video attribute.
	 *
	 * @param vaEntity the va entity
	 * @return the list
	 */
	private List<VideoAttributes> populateVideoAttribute(VideoAttributeEntity vaEntity) {

		if (vaEntity == null)
			return null;

		String[] videoPaths = null;
		String[] companionPaths = null;

		if (StringUtils.isNotBlank(vaEntity.getVideoPath()))
			videoPaths = vaEntity.getVideoPath().split("/n");

		if (StringUtils.isNotBlank(vaEntity.getCompanionPath()))
			companionPaths = vaEntity.getCompanionPath().split("/n");

		List<VideoAttributes> videoAttributes = new ArrayList<>();
		VideoAttributes va = new VideoAttributes();
		va.setId(vaEntity.getId());
		va.setVideoWidth(vaEntity.getWidth());
		va.setVideoHeight(vaEntity.getHeight());
		va.setVastProtocol(vaEntity.getVastProtocol());

		if (vaEntity.getIsSkippable() != null)
			va.setIsSkippable((vaEntity.getIsSkippable().equals(Boolean.TRUE)) ? 1 : 0);

		va.setDurationInSecs(vaEntity.getDuration());
		va.setFormat(vaEntity.getFormat());
		va.setCompanionType(vaEntity.getCompanionType());
		va.setHasCompanion(StringUtils.isNotBlank(vaEntity.getCompanionPath()));
		va.setCompanionHeight(vaEntity.getHeight());
		va.setCompanionWidth(vaEntity.getWidth());

		if (vaEntity.getVideoPath() != null)
			va.setVideoPath(videoPaths != null ? videoPaths[0] : "");

		if (vaEntity.getCompanionPath() != null)
			va.setCompanionPath(companionPaths != null ? companionPaths[0] : "");
		videoAttributes.add(va);

		return videoAttributes;
	}

	/**
	 * Update creative.
	 *
	 * @param entity      the entity
	 * @param creativeDTO the creative DTO
	 * @throws ValidationException the validation exception
	 */
	@Transactional
	public void updateCreative(CreativeEntity entity, CreativeDTO creativeDTO) throws ApiException {

		creativeValidator.isvalidClickDestinationId(creativeDTO.getClickDestination().getId());
		entity.setClickDestination(creativeDTO.getClickDestination().getId());

		if (StringUtils.isNotBlank(creativeDTO.getName()))
			entity.setName(creativeDTO.getName());

		entity.setStatus(creativeDTO.isActive() ? CreativeStatus.get(1) : CreativeStatus.get(0));
		entity.setModifiedBy(user.getUserInfo().getUserId());
		entity.setModifiedOn(System.currentTimeMillis() / 1000);

		if (creativeDTO.getSize() != null && creativeDTO.getSize().getHeight() != null)
			entity.setHeight(creativeDTO.getSize().getHeight());

		if (creativeDTO.getSize() != null && creativeDTO.getSize().getWidth() != null)
			entity.setWidth(creativeDTO.getSize().getWidth());

		if (creativeDTO.getType() != null)
			entity.setType(creativeDTO.getType());

		entity.setIsRefactored(Boolean.TRUE);
		String content = null;
		if (!(creativeDTO.getType() == CreativeType.zippedHTML && creativeDTO.isTemplateBased()
				&& creativeDTO.getContent().equals(entity.getContent()))) {
			content = populateCreativeContent(creativeDTO, true);
		}
		if (content != null) {
			entity.setContent(content);
		}
		/*
		 * if (creativeDTO.getPreviewUrl() != null)
		 * entity.setUrlPath(getUrlPathFromPreview(
		 * StringUtils.isNotBlank(creativeDTO.getPreviewUrl()) ?
		 * creativeDTO.getPreviewUrl() : null));
		 */
		if (creativeDTO.getUrlPath() != null && (creativeDTO.getUrlPath().equals("__DEFAULT_IMAGE__")
				|| creativeDTO.getUrlPath().equals("__ADDITIONAL_IMAGES__"))) {
			entity.setUrlPath(creativeDTO.getUrlPath());
		} else {
			if (creativeDTO.getPreviewUrl() != null) {
				entity.setUrlPath(getUrlPathFromPreview(
						StringUtils.isNotBlank(creativeDTO.getPreviewUrl()) ? creativeDTO.getPreviewUrl() : null));
			}
		}

		if (creativeDTO.getType().equals(CreativeType.video) || creativeDTO.getType().equals(CreativeType.nativeVideo))
			entity.setVideoAttributesId(saveVideoAttributeAndGetId(creativeDTO));

		/*
		 * Setting aelp for html to true since its not coming from UI. We are defaulting
		 * in the UI.
		 */
		if (CreativeType.html.name().equals(creativeDTO.getType().toString()))
			entity.setAelp(true);

		if (creativeDTO.isNativeAd())
			entity.setCreativeAsset(saveNativeAssetAndGet(creativeDTO));

		entity.setIsDco(creativeDTO.isDcoAd());

		if (entity.getIsDco())
			entity.setDcoAttributesId(saveDcoAttributesAndGetId(creativeDTO, entity.getDcoAttributesId()));

	}

	/**
	 * Populate native with macros replaced title body.
	 *
	 * @param nativeAsset the native asset
	 * @param advId       the adv id
	 */
	public void populateNativeWithMacrosReplacedTitleBody(NativeAssetPojo nativeAsset, Long advId,
			CreativeDTO creative) {

		// Fetch macros associated to a specific advertiser id
		List<Macro> macroDTOList = catalogUtil.getMacroDTOListForAdvertiser(advId);

		Map<String, Macro> macrosMap = new HashMap<>();

		if (macroDTOList == null || macroDTOList.isEmpty())
			return;

		for (Macro dtoOb : macroDTOList) {
			macrosMap.put(dtoOb.getMacroText(), dtoOb);
		}

		// Fbx macro replaced title and body
		replaceMacros(macrosMap, nativeAsset);

		if (creative.getUrlPath().equals(Constants.ADDITIONAL_IMAGES_MACRO)
				&& macrosMap.get(Constants.ADDITIONAL_IMAGES_MACRO) != null
				&& !macrosMap.get(Constants.ADDITIONAL_IMAGES_MACRO).getSamples().isEmpty()) {
			creative.setPreviewUrl(macrosMap.get(Constants.ADDITIONAL_IMAGES_MACRO).getSamples().get(0).toString());
		} else if (creative.getUrlPath().equals(Constants.DEFAULT_IMAGE_MACRO)
				&& macrosMap.get(Constants.DEFAULT_IMAGE_MACRO) != null
				&& !macrosMap.get(Constants.DEFAULT_IMAGE_MACRO).getSamples().isEmpty()) {
			creative.setPreviewUrl(macrosMap.get(Constants.DEFAULT_IMAGE_MACRO).getSamples().get(0).toString());
		}

	}

	/**
	 * Replace macros.
	 *
	 * @param macrosMap   the macros map
	 * @param nativeAsset the native asset
	 */
	public void replaceMacros(Map<String, Macro> macrosMap, NativeAssetPojo nativeAsset) {
		String previewTitle = replaceMacroString(macrosMap, nativeAsset.getTitle());
		String previewBody = replaceMacroString(macrosMap, nativeAsset.getBody());
		nativeAsset.setPreviewBody(previewBody);
		nativeAsset.setPreviewTitle(previewTitle);
	}

	/**
	 * Replace macro string.
	 *
	 * @param macrosMap the macros map
	 * @param str       the str
	 * @return the string
	 */
	private String replaceMacroString(Map<String, Macro> macrosMap, String str) {

		if (str == null || str.length() == 0)
			return str;

		String htmlStr = str;
		Pattern mPattern = null;
		Matcher matcher = null;

		// Replace macros with index 0 sample value
		for (Map.Entry<String, Macro> entry : macrosMap.entrySet()) {
			String macroStr = entry.getKey();
			Macro mDto = entry.getValue();

			mPattern = Pattern.compile(macroStr);
			matcher = mPattern.matcher(htmlStr);
			if (mDto.getSamples() != null && !mDto.getSamples().isEmpty()) {
				if (mDto.getSamples().get(0) != null)
					htmlStr = matcher.replaceAll(mDto.getSamples().get(0).toString());
				else
					htmlStr = matcher.replaceAll("");
			} else {
				// HACK: Adding sample values for offers
				switch (macroStr) {
					case "__OFFER_COUPON_CODE__":
						htmlStr = matcher.replaceAll("EXTRA25");
						break;
					case "__OFFER_PERCENT__":
						htmlStr = matcher.replaceAll("25");
						break;
					case "__OFFER_AMOUNT__":
						htmlStr = matcher.replaceAll("500");
						break;
					case "__MIN_ORDER_VALUE__":
						htmlStr = matcher.replaceAll("1999");
						break;
				}
			}
		}

		return htmlStr;
	}

	public List<Creative> populateCreativesForElastic(List<CreativeEntity> entityList) {

		if (CollectionUtils.isEmpty(entityList))
			return null;

		return entityList.stream().map(this::populateCreativeForElastic).collect(Collectors.toList());

	}

	public Creative populateCreativeForElastic(CreativeEntity entity) {

		if (entity == null)
			return null;

		Creative creative = new Creative();
		creative.setId(entity.getId());
		creative.setName(entity.getName());
		creative.setActive(entity.getStatus().getValue() == 1 ? Boolean.TRUE : Boolean.FALSE);
		creative.setCreationTime(entity.getCreationDate());
		creative.setCreatedBy(entity.getCreatedBy());
		creative.setModifiedBy(entity.getModifiedBy());
		creative.setModifiedTime(entity.getModifiedOn());
		creative.setAdvertiserId(entity.getAdvertiserId());
		creative.setLicenseeId(entity.getLicenseeId());
		creative.setHeight(entity.getHeight());
		creative.setWidth(entity.getWidth());
		creative.setImageUrl(entity.getUrlPath());
		creative.setCreativeType(entity.getType().toString());
		creative.setRefactor(entity.getIsRefactored());
		return creative;
	}

	public String replaceClickNoEncodingString(String tempCrId, String content) {
		// replacing "|CLICK|NOENCODING|" with
		// "https://trk.atomex.net/cgi-bin/tracker.fcgi/verify&url="
		// because the preview image link don't break for DCO native creatives
		content = content.replace("|CLICK|NOENCODING|",
				"https://trk.atomex.net/cgi-bin/tracker.fcgi/verify?cr=" + tempCrId + "&url=");
		return content;
	}

	public String replaceDynamicMacroWithContent(String str, String macroString) {
		String replacedContent = "";

		replacedContent = str.replace(DYNAMIC_NOENCODING, macroString);
		try {
			replacedContent = replacedContent.replace(DYNAMIC_NOENCODING, URLEncoder.encode(macroString, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return replacedContent;
	}

	public String constructMacroStringFromDcoDatabase(Long advertiserId, Integer noOfSlots, String macros ) {

		List<Map<String, String>> list = new ArrayList<>();
		List<Macro> macroDTOList = catalogUtil.getMacroDTOListForAdvertiser(advertiserId);

		if (macroDTOList == null)
			return "";

		String[] macrosArray = macros.split(",");
		List<String> macroList = Arrays.asList(macrosArray);

		if (noOfSlots > macroDTOList.size())
			return "";

		int indx = 0;

		for (int i = 0; i < noOfSlots; i++) {
			Map<String, String> map = new HashMap<>();

			for (Macro macro : macroDTOList) {
				if (macro.getMacroText() != null && macroList.contains(macro.getMacroText())
						&& macro.getSamples() != null && macro.getSamples().get(indx) != null)
					map.put(macro.getMacroText(), macro.getSamples().get(indx).toString());

			}
			indx++;
			if (indx == 2)
				indx = 0;

			list.add(map);
		}

		// Convert java object to json using jackson
		String macroString = javaObjToJson(list);

		return macroString;

	}

	/**
	 * Get JSON object from JavaObject
	 * 
	 * @param advDTO
	 * @return String
	 */
	public static String javaObjToJson(Object advDTO) {

		try {
			ObjectMapper mapper = new ObjectMapper();
			Writer strWriter = new StringWriter();
			mapper.writeValue(strWriter, advDTO);
			String userDataJSON = strWriter.toString();
			logger.debug("serialized string::" + userDataJSON);
			return userDataJSON;
		} catch (JsonGenerationException jse) {
			logger.error("ERROR: " + jse);
			return "fail";
		} catch (JsonMappingException jme) {
			logger.error("ERROR: " + jme);
			return "fail";
		} catch (IOException ioe) {
			logger.error("ERROR: " + ioe);
			return "fail";
		}
	}

	/**
	 * Template DTOs are populated from Template Entities
	 *
	 * @param templateEntities - template entities fetched from DB
	 * @return - List of Template DTOs.
	 */
	public List<CreativeTemplateDTO> populateCreativeTemplateDTO(List<CreativeTemplateEntity> templateEntities, Long advertiserId) {
		List<CreativeTemplateDTO> creativeTemplateDTOS = new ArrayList<>();
		for (CreativeTemplateEntity entity : templateEntities) {
			CreativeTemplateDTO templateDTO = new CreativeTemplateDTO();
			BeanUtils.copyProperties(entity,templateDTO);
			templateDTO.setIsActive(entity.isActive());
			templateDTO.setIsDynamic(entity.isDynamic());
			if (entity.isDynamic()) {
				templateDTO.setHtmlContent(entity.getHtmlContent());
				templateDTO.setDynamicItemList(constructMacroStringFromDcoDatabase(advertiserId, entity.getSlots(), entity.getMacros()));
			}
			creativeTemplateDTOS.add(templateDTO);
		}
		return creativeTemplateDTOS;
	}

	/**
	 * Template variable DTOs are populated from variable entities
	 *
	 * @param entities - template variable entities fetched from DB
	 * @return - List of Template variable DTOs
	 */
    public List<TemplateVariablesDTO> populateTemplateVariables(List<CreativeTemplateVariablesEntity> entities) {
		List<TemplateVariablesDTO> variablesDTOS = new ArrayList<>();
		for (CreativeTemplateVariablesEntity entity : entities) {
			TemplateVariablesDTO variablesDTO = new TemplateVariablesDTO();
			BeanUtils.copyProperties(entity, variablesDTO);
			variablesDTO.setIsActive(entity.isActive());
			variablesDTOS.add(variablesDTO);
		}
		return variablesDTOS;
    }

	/**
	 * Template theme DTO is populated from theme entity
	 *
	 * @param creativeTemplateThemeEntity - template theme entity from DB
	 * @param themeDTO - Template theme DTO which will be returned
	 */
	public void populateThemeFromEntity(CreativeTemplateThemeEntity creativeTemplateThemeEntity,
			TemplateThemeDTO themeDTO) {
		BeanUtils.copyProperties(creativeTemplateThemeEntity, themeDTO);
		themeDTO.setIsActive(creativeTemplateThemeEntity.isActive());
    }

	/**
	 * List of Template theme DTOs is populated from theme entities list.
	 *
	 * @param themeEntities - template theme entities from DB
	 * @return - list of template theme DTOs will be returned
	 */
	public List<TemplateThemeDTO> populateTemplateThemeDTOs(List<CreativeTemplateThemeEntity> themeEntities) {
		List<TemplateThemeDTO> themeDTOS = new ArrayList<>();
		for (CreativeTemplateThemeEntity entity : themeEntities) {
			TemplateThemeDTO themeDTO = new TemplateThemeDTO();
			populateThemeFromEntity(entity, themeDTO);
			themeDTOS.add(themeDTO);
		}
		return themeDTOS;
	}
}
