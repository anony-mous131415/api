
package io.revx.api.pojo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import io.revx.core.model.CurrencyModel;
import io.revx.core.model.Advertiser;
import io.revx.core.model.AppCategoryMaster;
import io.revx.core.model.AudienceESDTO;
import io.revx.core.model.BaseModel;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.City;
import io.revx.core.model.Country;
import io.revx.core.model.Creative;
import io.revx.core.model.Licensee;
import io.revx.core.model.OSMaster;
import io.revx.core.model.OSVersionMaster;
import io.revx.core.model.Platform;
import io.revx.core.model.RegionMaster;
import io.revx.core.model.State;
import io.revx.core.model.Strategy;
import io.revx.core.model.LogoModel;
import io.revx.core.model.MobileMeasurementPartner;
import io.revx.core.model.pixel.PixelCompact;
import io.revx.core.model.targetting.DeliveryPriority;
import io.revx.core.model.targetting.PacingType;
import io.revx.core.model.targetting.Pricing;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.GroupBy;

public enum TablesEntity {
	LICENSEE("Licensee", "licensee", Licensee.class, null, true),
	ADVERTISER("Advertiser", "advertiser", Advertiser.class, LICENSEE, true),
	CAMPAIGN("AdvertiserIo", "campaign", CampaignESDTO.class, ADVERTISER, true),
	STRATEGY("AdvertiserLineItem", "strategy", Strategy.class, CAMPAIGN, true),
	CREATIVE("Creative", "creative", Creative.class, null, true),
	AGGREGATOR("Aggregator", "aggregator", BaseModel.class, null, false),
	AUDIENCE("Segments", "audience", AudienceESDTO.class, null, true),
	APP_AUDIENCE("Segments", "audience", AudienceESDTO.class, null, true),
	WEB_AUDIENCE("Segments", "audience", AudienceESDTO.class, null, true),
	DMP_AUDIENCE("AdvertiserSegmentMapping", "dmp_audience", AudienceESDTO.class, null, true),
	PIXEL("Pixel", "pixel", PixelCompact.class, null, true), SITE("RTBSite", "site", BaseModel.class, null, false),
	COUNTRY("CountryMaster", "country", Country.class, null, false),
	STATE("StateMaster", "state", State.class, COUNTRY, false), CITY("CityMaster", "city", City.class, STATE, false),
	OS("OSMaster", "os", OSMaster.class, null, false), PRICING("Pricing", "pricing", Pricing.class, null, false),
	CURRENCY("CurrencyMaster", "currency", CurrencyModel.class, null, false),
	LANGUAGE("LanguageMaster", "language", BaseModel.class, null, false),
	TIMEZONE("TimeZoneMaster", "timezone", RegionMaster.class, null, false),
	CATEGORY("CategoryMaster", "category", BaseModel.class, null, false),
	MMP("MobileMeasurementPartner", "mmp", MobileMeasurementPartner.class, null, false),
	CDTYPE("ClickDestinationType", "cdtype", BaseModel.class, null, false),
	CDSUBTYPE("ClickDestinationSubType", "cdsubtype", BaseModel.class, null, false),
	DEVICE("DeviceType", "device", BaseModel.class, null, false),
	DELIVERY_PRIORITY("DeliveryPriority", "delivery_priority", DeliveryPriority.class, null, false),
	PACING_TYPE("PacingType", "pacing_type", PacingType.class, null, false),
	OS_VERSION("OSVersionMaster", "os_version", OSVersionMaster.class, null, false),
	DEVICE_MODEL("DeviceModelMaster", "device_model", BaseModel.class, null, false),
	DEVICE_BRAND("DeviceBrandMaster", "device_brand", BaseModel.class, null, false),
	SOURCE_TYPE("SourceType", "source_type", BaseModel.class, null, false),
	PLATFORM("Platform", "platform", Platform.class, null, false),
	APP_CATEGORY("AppCategoryMaster", "app_category", AppCategoryMaster.class, null, false),
	BROWSER("BrowserMaster", "browser", BaseModel.class, null, false),
	CALL_TO_ACTION("MopubCallToAction", "call_to_action", BaseModel.class, null, false),
	CREATIVE_SIZE("", "creative_size", BaseModel.class, null, false),
	DEAL_CATEGORY("", "deal_category", BaseModel.class, null, false),
	ADVERTISER_REGION("", "advertiser_region", BaseModel.class, null, false),
	CLICK_DESTINATION ("ClickDestination","click_destination",BaseModel.class,null,false),
	ADVERTISER_LOGO("AppSettings", "advertiser_logo", LogoModel.class,null,false);

	private static Map<String, TablesEntity> indexMap = new HashMap<>();
	static {
		for (TablesEntity ele : TablesEntity.values()) {
			indexMap.put(ele.getElasticIndex(), ele);
		}
	}

	String mysqlTableName;
	String elasticIndex;
	Class<?> elasticPojoClass;
	TablesEntity parentTableEntity;
	boolean isLoginFilter;

	private TablesEntity(String mysqlTableName, String elasticIndex, Class<?> elasticPojoClass,
			TablesEntity parentTabEntity, boolean isLoginFilter) {
		this.mysqlTableName = mysqlTableName;
		this.elasticIndex = elasticIndex;
		this.elasticPojoClass = elasticPojoClass;
		this.parentTableEntity = parentTabEntity;
		this.isLoginFilter = isLoginFilter;
	}

	public String getMysqlTableName() {
		return mysqlTableName;
	}

	public String getElasticIndex() {
		return elasticIndex;
	}

	public Class<?> getElasticPojoClass() {
		return elasticPojoClass;
	}

	public TablesEntity getParentTableEntity() {
		return parentTableEntity;
	}

	public boolean isLoginFilter() {
		return isLoginFilter;
	}

	public static TablesEntity getFromGroupBy(GroupBy groupBy) {
		if (groupBy != null) {
			switch (groupBy) {
			case ADVERTISER_ID:
				return ADVERTISER;
			case CAMPAIGN_ID:
				return CAMPAIGN;
			case STRATEGY_ID:
				return STRATEGY;
			case CREATIVE_ID:
				return CREATIVE;
			default:
				break;
			}
		}
		return null;
	}

	public static boolean isValidFilter(String index, String key) {
		if (StringUtils.isBlank(key))
			return false;

		if (key.equals(Filter.NAME.getColumn()) || key.equals(Filter.ID.getColumn())
				|| key.equals(Filter.STATUS.getColumn()))
			return true;

		TablesEntity entity = indexMap.get(index);
		switch (entity) {
		case APP_CATEGORY:
			return "osId".equalsIgnoreCase(key);
		case CITY:
			return "stateId".equalsIgnoreCase(key) || "countryId".equalsIgnoreCase(key);
		case STATE:
			return "countryId".equalsIgnoreCase(key);
		case CREATIVE:
			return "isRefactor".equalsIgnoreCase(key);
		case OS_VERSION:
			return "version".equalsIgnoreCase(key) || "osId".equalsIgnoreCase(key);
		case TIMEZONE:
			return "countryId".equalsIgnoreCase(key);
		case AUDIENCE:
		case APP_AUDIENCE:
		case WEB_AUDIENCE:
		case DMP_AUDIENCE:
			return "user_data_type".equalsIgnoreCase(key);
		case AGGREGATOR: 
			return "ragType".equalsIgnoreCase(key);
		case CAMPAIGN:
		case CLICK_DESTINATION:
			return "skadTarget".equalsIgnoreCase(key);
		default:
			break;
		}
		return false;
	}

}
