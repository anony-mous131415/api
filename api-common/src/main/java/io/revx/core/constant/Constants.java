package io.revx.core.constant;

public class Constants {
	public static final String URID_KEY = "urid";
	public static final String USER_NAME = "userName";
	public static final String USER_ID = "userId";

	public static final String ERROR_CODE = "errorCode";
	public static final String CACHE_KEY_SEPRATOR = "_";

	public static final long DEFAULT_FCAP_DURATION = 1440l;

	public static final long DEFAULT_LOCAL_CACHE_EXPIRE_TIME_IN_MILIES = 15 * 60 * 1000;
	public static final long THREE_DAYS_TIME_IN_MILLI_SECONDS = 3 * 86400 * 1000L;

	public static final Integer SUCCESS = 100;
	public static final Integer FAILERE = 500;
	public static final Integer ID_MISSING = 102;
	public static final Integer ID_ALREADY_ACTIVE = 103;
	public static final Integer ID_ALREADY_INACTIVE = 104;
	public static final Integer EC_ID_MISSING = 105;
	public static final Integer EC_MISSING_VALUE_FOR_CREATE = 106;
	public static final Integer EC_INVALID_VALUE = 122;
	public static final Integer EC_INVALID_RULES = 181;
	public static final Integer EC_MISSING_VALUE = 113;
	public static final Integer EC_OPERATION_NOT_SUPPORTED = 114;

	public static final Integer EC_SLM_SERVICE_CLIENT_GENERIC_ERROR = 176;
	public static final Integer EC_SLM_INVALID_VALUE_FOR_DURATION = 177;
	public static final Integer EC_SLM_WRITE_ACCESS_NOT_ALLOWED = 178;
	public static final Integer EC_SLM_SERVICE_GENERIC_ERROR = 175;

	public static final Integer EC_ADVERTISER_ID_MISSING = 180;

	public static final String MSG_ID_MISSING = "id is missing";
	public static final String MSG_ID_ALREADY_ACTIVE = "id already active";
	public static final String MSG_OPERATION_NOT_SUPPORTED = "Operation not supported";
	public static final String MSG_SUCCESS = "success";
	public static final String MSG_ERROR = "Internal Error";
	public static final String MSG_DB_ERROR = "Internal DB Error";
	public static final String MSG_ID_INVALID = "id is invalid";
	public static final String MSG_EMPTY_RESULT = "Empty Result";

	public static final String MSG_ID_ALREADY_INACTIVE = "id already inactive";

	public static final String ADVERTISER_TMPL = "{CLIENTID}";
	public static final String PIXEL_TMPL = "{PIXELID}";
	public static final String PIXEL_HASH_TMPL = "{HASH}";
	public static final String VERSION_TMPL = "{VERSION}";
	public static final String DATE_TMPL = "{DATE}";
	public static final String FB_TAG = "//{FB_TAG}";
	public static final String ATOMEX_OPTOUT_URL = "http://www.atomex.net/optout\\"
			+ "?optout_req=true&redirecturl=http://www.komli.com/in/content/opt-out-success/";

	public static final String INVALID_ADV_ID = "advertiser id not valid";
	public static final String INVALID_CR_ID = "creative id not valid";
	public static final String PIXEL_KEY_PREFIX = "Pixel";

	// creative Error msg
	public static final String INVALID_FILE_TYPE = "invalid file type";
	public static final String INVALID_CONTENT_TYPE = "invalid content type";
	public static final String Creative_Success = "success";
	public static final String INVALID_DIMENSION = "invalid dimension";

	public static final String NO_IMAGE_IN_ZIP = "No default image file found inside zip";
	public static final String NO_HTML_IN_ZIP = "No html file found inside zip";
	public static final String INVALID_VIDEO_BITRATE = "video has invalid bitrate. More than the limit";
	public static final String DIR_SEPARATOR = "/";
	public static final String AD_TAG = "adTag";

	public static final String MSG_NO_FILE_IN_ZIP = "No file found post decompress";
	public static final String MSG_MULTIPLE_FILE_IN_ZIP = "Multiple file found post decompress";
	public static final String MSG_MULTIPLE_FILE_IN_UPLOAD = "Multiple file found In Upload";
	public static final String MSG_NO_FILE_IN_UPLOAD = "No file found In Upload";
	public static final String NO_IMAGE_COMPANION_FOUND = "No companion Found";
	public static final Long DEFAULT_COMPANION_CR_ID = -1L;
	public static final String IMAGE = "image";
	public static final String UNDERSCORE = "_";
	public static final String HYPHEN = "-";
	public static final String NATIVEIMAGE = "nativeImage";
	public static final String HTML = "html";
	public static final String HTMLFILE = ".html";
	public static final String VIDEO = "video";
	public static final String NATIVEVIDEO = "nativeVideo";
	public static final String DCO = "dco";

	public static final String ADDITIONAL_IMAGES_MACRO = "__ADDITIONAL_IMAGES__";
	public static final String DEFAULT_IMAGE_MACRO = "__DEFAULT_IMAGE__";
	public static final char COMMA_SEPARATOR = ',';
	public static final String NATIVE = "native";
	public static final String DYNAMIC = "dynamic";
	public static final String STATIC = "static";
	public static final CharSequence MACRO = "macro";

	public static final String INVALID_ICON_PATH = "Error while creating icon url for uploaded icon";
	public static final String CRATIVE_PERFORMANCE_DATA_EXCEPTION = "Exception while fetching the performance data of creatives";

	public static final String AND_OPERATOR = "AND";
	public static final String OR_OPERATOR = "OR";
	public static final String REPORT_BUILDER_DIRECTORY = "/atom/api-service/current/conf/reportbuilder/";
	public static final String CREATIVE_KEY = "CREATIVE";

	public static final String APP_SETTING_KEY_WHITELIST_BLACKLIST_INVENTORY = "FETCH_WHITELIST_BLACKLIST_INVENTORY";
	public static final String AGGREGATOR_DICTIONARY_CACHE_KEY = "AGGREGATOR_DICTIONARY_TYPE_1_";
	public static final String AGGREGATOR_TYPE_1 = "1";

	public static final String ADVERTISER_REGION_INTERNAL = "Internal";
	
	

	public static final String GROUP_BY_DAY = "day";
	public static final String GROUP_BY_HOURLY = "hour";
	public static final String LICENSEE_ID = "licenseeId";
	public static final String ADVERTISER_ID = "advertiserId";
	public static final String CAMPAIGN_ID = "campaignId";
	public static final String STRATEGY_ID = "strategyId";

	public static final String DYNAMIC_NOENCODING = "|DYNAMIC|NOENCODING|";

}