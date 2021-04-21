package io.revx.api.constants;

public class ApiConstant {

	public static final String API_VERSION = "/v2";
	public static final String API_BASE = API_VERSION + "/api";
	public static final String THEME_API_BASE = API_VERSION + "/theme";
	public static final String LOGGING_API = API_VERSION + "/logging";
	public static final String THEME_BY_SUBDOMAIN = THEME_API_BASE + "/subdomain/{subdomain}";
	public static final String THEME_BY_LICENSEE_ID = THEME_API_BASE + "/licensee/{licenseeId}";
	public static final String URG_GET_USER_INFO_MAPPING = "/userinfo";

	public static final String DASHBOARD_LIST = API_BASE + "/list/{entity}";
	public static final String DASHBOARD_CHART = API_BASE + "/chart";

	public static final String DASHBOARD_WIDGET = API_BASE + "/widget/{entity}";

	public static final String DICTIONARY = API_BASE + "/dictionary/{tableEntity}";
	public static final String DICTIONARY_DETAIL = API_BASE + "/detaildictionary/{tableEntity}";
	public static final String MENU_CRUMBS = API_BASE + "/menucrumbs";
	public static final String SEARCH_BY_NAME = API_BASE + "/searchbyname/{tableEntity}";
	public static final String GET_BY_ID = API_BASE + "/{tableEntity}/{id}";
	public static final String advertiserRoiTypes = API_BASE + "/advertiserroitypes";
	public static final String pricing = API_BASE + "/pricing/{tableEntity}";

	public static final String DETAIL_BY_ID = API_BASE + "/detail/{tableEntity}/{id}";

	public static final String FILTERS = "filters";
	public static final String SEARCH = "search";
	public static final String FIELDS = "fields";
	public static final String OPTIONS = "options";
	public static final String OFFSET = "offset";
	public static final String NATIVE = "is_native";
	public static final String START = "start";
	public static final String LIMIT = "limit";
	public static final String SORT = "sort";
	public static final String DEFAULT_SORT = "name+";
	public static final String REFRESH = "refresh";
	public static final String PAGENUMBER = "pageNumber";
	public static final String PAGESIZE = "pageSize";

	public static final String PERF_DATA_CACHE = "perfDataCache";

	public static final String API_GRAPHITE_METRIC = "api.metric";

	public static final String ADVERTISERS = API_BASE + "/advertisers";
	public static final String SETTINGS = "/settings";
	public static final String ID = "id";
	public static final String ID_PATH = "/{" + ID + "}";
	public static final String updateAST = "/ast-tag";
	public static final String activate = "/activate";
	public static final String deactivate = "/deactivate";
	public static final String search = "/search";

	public static final String BATCH = "/batch";
	public static final String MOCKUPS = "/mockups";
	public static final String HTML_MOCKUPS = "/htmlmockups";
	public static final String THIRDPARTYTAG = "/adtag";

	public static final String clickDestinations = API_BASE + "/clickdestinations";
	public static final String catalog = API_BASE + "/catalog";
	public static final String macros = "/macros";
	public static final String advertiserId = "advertiser_id";
	public static final String creatives = API_BASE + "/creatives";
	public static final String PERFORMANCE = "/performance/";
	public static final String upload = API_BASE + "/upload";
	public static final String offertypes = "/offertypes";
	public static final String feeds = "/feeds";
	public static final String variableMappings = "/variables";
	public static final String feedsById = "/feeds" + ID_PATH;
	public static final String SMART_CACHING = API_BASE + "/smartcaching";

	public static final String PIXELS = API_BASE + "/pixels";
	public static final String trackingCode = "/trackingcode";
	public static final String CONVERSION_TRACKER = "/convtracker";

	public static final String piggyBackPixels = API_BASE + "/piggybackpixels";

	public static final String UPLOAD = "/upload";

	public static final String AUDIENCE_BASE = API_BASE + "/audience";
	public static final String AUDIENCE_CREATE = AUDIENCE_BASE;
	public static final String AUDIENCE_UPDATE = AUDIENCE_BASE + ID_PATH;
	public static final String AUDIENCE_GET = AUDIENCE_BASE + ID_PATH;
	public static final String AUDIENCE_RULES = AUDIENCE_BASE + "/rules";
	public static final String AUDIENCE_ACCESS = AUDIENCE_BASE + "/access" + ID_PATH;
	public static final String AUDIENCE_ACTIVATE = AUDIENCE_BASE + activate;
	public static final String AUDIENCE_DEACTIVATE = AUDIENCE_BASE + deactivate;
	public static final String AUDIENCE_UPLOAD = AUDIENCE_BASE + UPLOAD;
	public static final String AUDIENCE_GET_ALL = AUDIENCE_BASE + "/all";
	public static final String APPSFLYER_AUDIENCE = API_BASE + "/container";
	public static final String APPSFLYER_AUDIENCE_CREATE = APPSFLYER_AUDIENCE + "/create";
	public static final String APPSFLYER_AUDIENCE_SYNC = APPSFLYER_AUDIENCE + "/sync";
	public static final String VALIDATE_AUTH = API_BASE + "/key/validate";

	public static final String DMP_AUDIENCE = AUDIENCE_BASE + "/dmp";
	public static final String DMP_SEGMENT_TYPE = "stype";
	public static final String DMP_SYNCED_AUDIENCE = DMP_AUDIENCE + "/synced";

	public static final String AUDIENCE_SYNC_REMOTE = AUDIENCE_BASE + ID_PATH + "/sync_remote";
	public static final String AUDIENCE_SYNC = AUDIENCE_BASE + "/sync";
	public static final String CHECK_CONNECTION = AUDIENCE_BASE + "/check_connection";

	public static final String SLICEX_API_BASE = API_BASE + "/slicex";
	public static final String SLICEX_CHART = SLICEX_API_BASE + "/chart";
	public static final String SLICEX_LIST = SLICEX_API_BASE + "/list/{entity}";
	public static final String SLICEX_LIST_EXPORT = SLICEX_API_BASE + "/list/csv/{entity}";

	public static final String ID_ALREADY_ACTIVE = "Id is already active";

	public static final String CAMPAIGNS = API_BASE + "/campaigns";

	public static final String CAMPAIGN_CACHE_KEY = "CAMPAIGN";

	public static final String STRATEGY_CACHE_KEY = "STRATEGY";

	public static final String ADVERTISER_CACHE_KEY = "ADVERTISER";

	public static final String LICENSEE_CACHE_KEY = "LICENSEE_ID";

	public static final String AUDIENCE_CACHE_KEY = "AUDIENCE";

	public static final String SEGMENT_CACHE_KEY = "SEGMENT";

	public static final String PIXEL_CACHE_KEY = "PIXEL";

	public static final String CREATIVE_CACHE_KEY = "CREATIVE";

	public static final String CREATIVE_COMPACT_CACHE_KEY = "CREATIVE_COMPACT";

	public static final String FEEDINFO_CACHE_KEY = "FEEDINFO";

	public static final String CATALOG_MACROS_CACHE_KEY = "MACROS";

	public static final String CURRENCY_CACHE_KEY = "CURRENCY";

	public static final String ACVM_CACHE_KEY = "ACVM";

	public static final String AMTPIXEL_CACHE_KEY = "PIXELS";

	public static final String CLICK_DESTINATION_CACHE_KEY = "CD";

	public static final Long CPA_PRICING = 3L;
	public static final String smarttag = "/smarttag";

	public static final String STRATEGIES = API_BASE + "/strategies";
	public static final String bulkstrategies = API_BASE + "/strategies";
	public static final String DUPLICATE_STRATEGY = "/duplicate/{" + ID + "}";
	public static final String VALIDATE_DOMAINS = "/validatedomains";
	public static final String COMPACT = "/compact";
	public static final String STRATEGY_BY_ROI = "/roitype/{" + ID + "}";
	public static final String STRATEGY_BY_PRICING_TYPE = "/pricingtype/{" + ID + "}";
	public static final String IS_ADSAFETY_ENABLED = "/isadsafetyenabled";
	public static final String MIN_MAX_SETTINGS = "/getminmaxsettings";
	public static final String STRATEGY_TARGETTING_TILL_NOW = "/getstrategytargetingtillnow/{" + ID + "}";
	public static final String ASSOCIATE_CREATIVE_STRATEGY = "/associatecreativesstrategies";
	public static final String CRETIVE_ATTACHED_TO_MULTI_STRATEGY = "/getmultiplestrategycreativeslist";

	public static final String creative = "/creative";

	public static final long ABSOLUTE_END_TIME = 7258118399L;
	public static final long ROI_TARGET_CPA = 3;
	/* Pricing flag bits */
	public static final Integer PRICING_STRATEGY_BIT = 0;
	public static final Integer PRICING_CAMPAIGN_BIT = 1;
	public static final Integer PRICING_PUBLISHER_BIT = 2;

	public static final String COUNTRY = "COUNTRY";
	public static final String REGION = "REGION";
	public static final String CITY = "CITY";

	public static final String campaignIds = "campaign_ids";
	public static final String export = "/export";
	public static final String UPDATE = "/update";
	public static final String VALIDATE = "/validate";
	public static final String TSV_EXTN = ".tsv";
	public static final String FILE_NAME = "file_name";

	public static final String QUICK_EDIT = "/quickEdit/{" + ID + "}";

	public static final String REPORTING_ENTITY = "entity";
	public static final String REPORTING_OPTIONS = "options";
	public static final String REPORTING_REQUEST_BASE = API_BASE + "/performance";
	public static final String REPORTING_REQUEST_ENTITY = "/{" + REPORTING_ENTITY + "}";
	public static final String REPORTING_REQUEST_ENTITY_EXPORT = "/{" + REPORTING_ENTITY + "}.csv";
	public static final Integer NATIVE_AD_HEIGHT = 627;
	public static final Integer NATIVE_AD_WIDTH = 1200;
	public static final String CONVERSION_REPORT_ENTITY = "conversionreport";
	public static final String RTB_ENTITY = "rtb";
	public static final String CONVERSION_TIME = "conversion_time";
	public static final String AV_ADVERTISER_NAME = "lower(av_advertiser_name::varchar)";

	public static final String ADVERTISER_ID = "advertiserId";
	public static final String ACTIVITY = API_BASE + "/activity";
	public static final String LOG = "/log/{entity}";
	public static final String DETAIL = "/detail/{entity}";

	public static final String APP_SETTINGS = API_BASE + "/appsettings";
	public static final String SETTINGS_KEYS = "settingsKeys";


	public static final String mmpParameters = "/mmpparameters";
    public static final String SHOW_UU = "showuu";

	public static final String CREATIVE_TEMPLATE = creatives + "/templates";
	public static final String CREATIVE_TEMPLATE_THEMES = CREATIVE_TEMPLATE + "/themes";
	public static final String CREATIVE_TEMPLATE_VARIABLES = CREATIVE_TEMPLATE + variableMappings;
	public static final String PRODUCT_IMAGES = "/productimages";
	public static final String METADATA = "/metadata";
	public static final String TEMPLATE_SIZES = "templateSizes";


    public static final String SKAD_TARGET_PRIVILEGE = "/skadtargetprivilege";
	public static final String SKAD_TARGET = "skadtarget";
    public static final String SKAD_SETTINGS = "/skadsettings";
}
