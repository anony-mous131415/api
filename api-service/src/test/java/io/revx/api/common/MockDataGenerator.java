package io.revx.api.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import io.revx.api.enums.CampaignObjective;
import io.revx.api.pojo.*;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.SearchRequest;

import io.revx.api.audience.pojo.RuleFilterType;
import io.revx.api.audience.pojo.RuleValueDto;
import io.revx.api.audience.pojo.RuleValueType;
import io.revx.api.enums.CatalogVariableValueType;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.mysql.amtdb.entity.*;
import io.revx.api.mysql.amtdb.entity.Segments;
import io.revx.api.mysql.dco.entity.catalog.AdvertiserCatalogVariablesMappingEntity;
import io.revx.api.mysql.dco.entity.catalog.AtomCatalogVariablesEntity;
import io.revx.api.mysql.entity.AggregatorLicenseeMappingEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;
import io.revx.api.mysql.entity.advertiser.CurrencyEntity;
import io.revx.api.mysql.entity.creative.DcoAttributesEntity;
import io.revx.api.mysql.entity.strategy.*;
import io.revx.core.cache.DTOCache;
import io.revx.core.enums.*;
import io.revx.core.enums.Operator;
import io.revx.core.model.audience.*;
import io.revx.core.model.creative.DcoAttributeType;
import io.revx.core.model.requests.*;
import io.revx.core.model.strategy.StrategyCreativeAssociationResponseDTO.CreativeStrategyAssociationStatus;
import io.revx.api.enums.Status;
import io.revx.api.mysql.dco.entity.catalog.FeedInfoEntity;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;
import io.revx.api.mysql.entity.campaign.AdvertiserIOPixel;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.mysql.entity.clickdestination.ClickDestinationEntity;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.api.mysql.entity.pixel.AdvertiserLineItemPixelEntity;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;
import io.revx.core.model.*;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.advertiser.AdvertiserSettings;
import io.revx.core.model.campaign.CampaignDTO;
import io.revx.core.model.catalog.CatalogFeed;
import io.revx.core.model.catalog.Macro;
import io.revx.core.model.catalog.VariablesMappingDTO;
import io.revx.core.model.creative.CreativeDTO;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.creative.CreativeType;
import io.revx.core.model.pixel.DataPixelDTO;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.strategy.*;
import io.revx.core.model.targetting.*;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.objs.FilterComponent;

import io.revx.api.mysql.entity.AppSettingsEntity;
import io.revx.api.mysql.entity.AppSettingsPropertyEntity;
import io.revx.api.mysql.entity.creative.CreativeTemplateEntity;
import io.revx.api.mysql.entity.creative.CreativeTemplateThemeEntity;
import io.revx.api.mysql.entity.creative.CreativeTemplateVariablesEntity;
import io.revx.core.enums.AppSettingsKey;
import io.revx.core.enums.AppSettingsPropertyKey;
import io.revx.core.enums.AppSettingsType;
import io.revx.core.enums.TemplateVariableType;
import io.revx.core.model.AppSettingsDTO;
import io.revx.core.model.AppSettingsPropertyDTO;
import io.revx.core.model.ClickDestination;
import io.revx.core.model.creative.CampaignType;
import io.revx.core.model.creative.CreativeDetails;
import io.revx.core.model.creative.CreativeFiles;
import io.revx.core.model.creative.CreativeHtmlFile;
import io.revx.core.model.creative.CreativeHtmlMockupDTO;
import io.revx.core.model.creative.CreativeMockUpsDTO;
import io.revx.core.model.creative.TemplateThemeDTO;
import org.apache.commons.lang3.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import io.revx.api.enums.SlicexEntity;

@Component
public class MockDataGenerator {

    private static Logger logger = LogManager.getLogger(MockDataGenerator.class);

    protected static long secondInHour = 60 * 60;
    protected static long secondInDay = 24 * 60 * 60;

    public ChartDashboardResponse getDashboardDataChart(Integer pageNumber, Integer pageSize,
                                                        DashboardRequest dashboardRequest, DashBoardEntity entity) {
        ChartDashboardResponse dashboardResponse = new ChartDashboardResponse();
        long startEpoc = getCurrentDayEpoc() - 30 * secondInDay;
        long endEpoc = getCurrentHourEpoc();
        long incrementBy = secondInDay;
        if (dashboardRequest != null && dashboardRequest.getDuration() != null) {
            if (dashboardRequest.getDuration().getStartTimeStamp() > 0)
                startEpoc = dashboardRequest.getDuration().getStartTimeStamp();
            if (dashboardRequest.getDuration().getEndTimeStamp() > 0)
                endEpoc = dashboardRequest.getDuration().getEndTimeStamp();
            if ("hour".equalsIgnoreCase(dashboardRequest.getGroupBy())) {
                incrementBy = secondInHour;
            }
        }

        int totalProduct = (int) (Math.abs(endEpoc - startEpoc) / incrementBy);
        dashboardResponse.setTotalNoOfRecords(totalProduct);
        List<DashboardData> dashboardDataList = new ArrayList<DashboardData>();
        EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).objectPoolSize(100)
                .stringLengthRange(5, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true)
                .overrideDefaultInitialization(false).ignoreRandomizationErrors(true);

        EasyRandom easyRandom = new EasyRandom(parameters);
        DashboardData widgetData = easyRandom.nextObject(DashboardData.class);
        widgetData.setId(0l);
        widgetData.setName(getDashBoardName(entity) + " widgetData  - ");
        widgetData.setStartTimestamp(startEpoc);
        widgetData.setEndTimestamp(endEpoc);
        for (int i = 1; i <= totalProduct; i++) {
            DashboardData data = easyRandom.nextObject(DashboardData.class);
            data.setId((long) i);
            data.setName(getDashBoardName(entity) + " Name - " + i);
            data.setStartTimestamp(startEpoc + (i * incrementBy));
            if (incrementBy == secondInHour)
                data.setHour(new BigDecimal(data.getStartTimestamp()));
            else
                data.setDay(new BigDecimal(data.getStartTimestamp()));
            data.setEndTimestamp(data.getStartTimestamp() + incrementBy);

            overrideSome(data);
            updateWidgetData(widgetData, data);
            dashboardDataList.add(data);
        }
        dashboardResponse.setData(dashboardDataList);
        dashboardResponse.setWidgetData(widgetData);
        logger.info(" SecurityContextHolder.getContext() : "
                + SecurityContextHolder.getContext().getAuthentication());
        return dashboardResponse;
    }

    protected void updateWidgetData(DashboardData widgetData, DashboardData data) {
        widgetData.setImpressions(
                widgetData.getImpressions() == null ? new BigDecimal(data.getImpressions().longValue())
                        : widgetData.getImpressions().add(data.getImpressions()));
        widgetData.setConversions(
                widgetData.getConversions() == null ? new BigDecimal(data.getConversions().longValue())
                        : widgetData.getConversions().add(data.getConversions()));
        widgetData
                .setClicks(widgetData.getClicks() == null ? new BigDecimal(data.getClicks().longValue())
                        : widgetData.getClicks().add(data.getClicks()));
        widgetData.setClickConversions(widgetData.getClickConversions() == null
                ? new BigDecimal(data.getClickConversions().longValue())
                : widgetData.getClickConversions().add(data.getClickConversions()));
        widgetData.setViewConversions(widgetData.getViewConversions() == null
                ? new BigDecimal(data.getViewConversions().longValue())
                : widgetData.getViewConversions().add(data.getViewConversions()));
        widgetData.setInstalls(
                widgetData.getInstalls() == null ? new BigDecimal(data.getInstalls().longValue())
                        : widgetData.getInstalls().add(data.getInstalls()));

    }

    protected static void overrideSome(PerformanceDataMetrics data) {
        data.setImpressions(new BigDecimal(RandomUtils.nextInt(10000, 100000)));
        data.setClicks(new BigDecimal(RandomUtils.nextInt(1000, 10000)));
        data.setClickconversions(new BigDecimal(RandomUtils.nextInt(100, 1000)));
        data.setViewconversions(new BigDecimal(RandomUtils.nextInt(100, 1000)));
        data.setBidsplaced(new BigDecimal(RandomUtils.nextInt(100, 10000)));
        data.setEligiblebids(new BigDecimal(RandomUtils.nextInt(100, 10000)));
        data.setEligibleuniqusers(new BigDecimal(RandomUtils.nextInt(100, 10000)));
        data.setImpressionuniqusers(new BigDecimal(RandomUtils.nextInt(100, 10000)));
    }

    protected void overrideSome(DashboardData data) {
        data.setImpressions(new BigDecimal(RandomUtils.nextInt(10000, 100000)));
        data.setConversions(new BigDecimal(RandomUtils.nextInt(100, 1000)));
        data.setClicks(new BigDecimal(RandomUtils.nextInt(1000, 10000)));
        data.setClickConversions(new BigDecimal(RandomUtils.nextInt(100, 1000)));
        data.setViewConversions(new BigDecimal(RandomUtils.nextInt(100, 1000)));

    }

    protected static String getDashBoardName(DashBoardEntity entity) {
        if (entity == null || entity == DashBoardEntity.HOMEPAGE)
            return "Dashboard ";
        return entity.toString();
    }

    public static long getCurrentHourEpoc() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime().getTime() / 1000;

    }

    public static long getDayEpoc(int noOfDayBefore) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, noOfDayBefore);
        return getDayEpoc(cal);

    }

    public static long getCurrentDayEpoc() {
        Calendar cal = Calendar.getInstance();
        return getDayEpoc(cal);

    }

    protected static long getDayEpoc(Calendar cal) {
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime().getTime() / 1000;

    }

    public DictionaryResponse getDictionaryData(TablesEntity entity, Integer pageNumber,
                                                Integer pageSize) {
        if (pageSize == null || pageSize <= 0)
            pageSize = 10;
        if (pageNumber == null || pageNumber < 0)
            pageNumber = 0;
        DictionaryResponse dashboardResponse = new DictionaryResponse();
        int totalProduct = RandomUtils.nextInt(pageSize * 2, pageSize * 10);
        dashboardResponse.setTotalNoOfRecords(totalProduct);
        List<BaseModel> dashboardDataList = new ArrayList<BaseModel>();
        int startIndex = Math.abs(pageNumber - 1) * pageSize + 1;
        for (int i = startIndex; i <= (startIndex + pageSize); i++) {
            BaseModel data = new BaseModel(i, entity.name() + " - " + i);
            dashboardDataList.add(data);
        }
        dashboardResponse.setData(dashboardDataList);
        return dashboardResponse;

    }

    public DashboardResponse getDashboardDataList(Integer pageNumber, Integer pageSize,
                                                  DashboardRequest dashboardRequest, DashBoardEntity entity) {
        DashboardResponse dashboardResponse = new DashboardResponse();
        long startEpoc = getCurrentDayEpoc() - 30 * secondInDay;
        long endEpoc = getCurrentHourEpoc();
        long incrementBy = secondInDay;
        if (dashboardRequest != null && dashboardRequest.getDuration() != null) {
            if (dashboardRequest.getDuration().getStartTimeStamp() > 0)
                startEpoc = dashboardRequest.getDuration().getStartTimeStamp();
            if (dashboardRequest.getDuration().getEndTimeStamp() > 0)
                endEpoc = dashboardRequest.getDuration().getEndTimeStamp();
            if ("hour".equalsIgnoreCase(dashboardRequest.getGroupBy())) {
                incrementBy = secondInHour;
            }
        }

        int totalProduct = (int) (Math.abs(endEpoc - startEpoc) / incrementBy);
        dashboardResponse.setTotalNoOfRecords(totalProduct);
        List<DashboardData> dashboardDataList = new ArrayList<DashboardData>();
        EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).objectPoolSize(100)
                .stringLengthRange(5, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true)
                .overrideDefaultInitialization(false).ignoreRandomizationErrors(true);
        EasyRandom easyRandom = new EasyRandom(parameters);
        for (int i = 1; i <= totalProduct; i++) {
            DashboardData data = easyRandom.nextObject(DashboardData.class);
            data.setId((long) i);
            data.setName(getDashBoardName(entity) + " Name - " + i);
            data.setStartTimestamp(startEpoc + (i * incrementBy));
            if (incrementBy == secondInHour)
                data.setHour(new BigDecimal(data.getStartTimestamp()));
            else
                data.setDay(new BigDecimal(data.getStartTimestamp()));
            data.setEndTimestamp(data.getStartTimestamp() + incrementBy);
            overrideSome(data);
            dashboardDataList.add(data);
        }

        dashboardResponse.setData(dashboardDataList);
        return dashboardResponse;
    }

    public List<MenuCrubResponse> getMenuCrubResponse() {
        List<MenuCrubResponse> resp = new ArrayList<>();
        for (TablesEntity ele : TablesEntity.values()) {
            List<StatusBaseObject> ll = new ArrayList<StatusBaseObject>();
            for (int i = 1; i <= 15; i++) {
                StatusBaseObject sb = new StatusBaseObject(RandomUtils.nextBoolean());
                sb.setId((long) i);
                sb.setName(ele.toString() + " Name - " + i);
                ll.add(sb);
            }
            resp.add(new MenuCrubResponse(ele.getElasticIndex(), ll));

        }
        return resp;
    }

    public SlicexChartResponse getSlicexChartData(SlicexRequest slicexRequest, boolean refresh) {
        SlicexChartResponse slicexChartResponse = new SlicexChartResponse();

        List<SlicexData> data = new ArrayList<SlicexData>();
        Long dataTimeStamp;

        if (slicexRequest.getGroupBy().equalsIgnoreCase("daily")) {
            slicexChartResponse.setTotalNoOfRecords(31);

            for (int i = 31; i > 0; i--) {
                SlicexData singleRow = null;
                dataTimeStamp = generateTimeStamp(i * 24);
                singleRow = generateGraphRow(dataTimeStamp);
                data.add(singleRow);
            }

            slicexChartResponse.setData(data);
        } else if (slicexRequest.getGroupBy().equalsIgnoreCase("hourly")) {
            Long numberOfresults = (24L * 16L);
            for (int i = numberOfresults.intValue(); i > 0; i--) {
                SlicexData singleRow = null;
                dataTimeStamp = generateTimeStamp(i);
                singleRow = generateGraphRow(dataTimeStamp);
                data.add(singleRow);
            }

            slicexChartResponse.setData(data);
        }
        return slicexChartResponse;
    }

    public SlicexListResponse getSlicexGridData(SlicexRequest slicexRequest, String sort,
                                                boolean refresh, SlicexEntity entity) {
        SlicexListResponse slicexListResponse = new SlicexListResponse();
        List<SlicexGridData> data = new ArrayList<SlicexGridData>();
        int numberOfresults = 150;
        for (int i = 0; i < numberOfresults; i++) {
            SlicexGridData singleRow = null;
            singleRow = generateGridRow(new Long(i + 1), entity);
            data.add(singleRow);
        }
        slicexListResponse.setTotalNoOfRecords(numberOfresults);
        slicexListResponse.setData(data);
        return slicexListResponse;
    }

    private SlicexData generateGraphRow(Long ts) {
        SlicexData data = new SlicexData();
        data.setDay(new BigDecimal(ts));
        data.setHour(new BigDecimal(0));

        data.setImpressions(new BigDecimal(numberGenerator(1000000L, 10000000L)));
        data.setClicks(new BigDecimal(numberGenerator(1000L, 1000000L)));
        data.setViewConversions(new BigDecimal(numberGenerator(10L, 100L)));
        data.setClickConversions(new BigDecimal(numberGenerator(100L, 1000L)));
        data.setRevenue(new BigDecimal(numberGenerator(2000L, 2500L)));
        data.setCost(new BigDecimal(numberGenerator(2000L, 2500L)));
        data.setImpInstalls(new BigDecimal(numberGenerator(1000L, 1000000L)));
        data.setClickInstalls(new BigDecimal(numberGenerator(1000L, 1000000L)));
        data.setCurrencyId("INR");
        data.setCalculatedFields();
        return data;
    }

    private SlicexGridData generateGridRow(Long id, SlicexEntity entity) {
        SlicexGridData slicexGridData = new SlicexGridData();

        slicexGridData.setImpressions(new BigDecimal(numberGenerator(1000000L, 10000000L)));
        slicexGridData.setClicks(new BigDecimal(numberGenerator(1000L, 1000000L)));
        slicexGridData.setViewConversions(new BigDecimal(numberGenerator(10L, 100L)));
        slicexGridData.setClickConversions(new BigDecimal(numberGenerator(100L, 1000L)));
        slicexGridData.setRevenue(new BigDecimal(numberGenerator(2000L, 2500L)));
        slicexGridData.setCost(new BigDecimal(numberGenerator(2000L, 2500L)));
        slicexGridData.setImpInstalls(new BigDecimal(numberGenerator(1000L, 1000000L)));
        slicexGridData.setClickInstalls(new BigDecimal(numberGenerator(1000L, 1000000L)));
        slicexGridData.setCurrencyId("INR");
        slicexGridData.setCalculatedFields();

        slicexGridData.setId(id);
        slicexGridData.setName(entity.getUiDisplayName() + " " + id.toString());

        return slicexGridData;
    }

    public static Long generateTimeStamp(int lastNHours) {
        Long todayMidnightTime = ((System.currentTimeMillis() / 1000) / 86400) * 86400 + 86400;
        Long result = todayMidnightTime - (lastNHours * 60 * 60);
        return result;
    }

    public static Long numberGenerator(Long minimumValue, Long MaximumValue) {
        Long result = ThreadLocalRandom.current().nextLong(minimumValue, MaximumValue + 1);
        return result;
    }

    public static AppSettingsEntity generateAppSettingsEntity() {
        List<AppSettingsPropertyEntity> propertyEntities = new ArrayList<>();
        AppSettingsEntity settingsEntity = new AppSettingsEntity();
        settingsEntity.setActive(true);
        settingsEntity.setValue("test_link");
        settingsEntity.setKey(AppSettingsKey.LOGO_LINK);
        settingsEntity.setAdvertiserId(7146L);
        settingsEntity.setLicenseeId(33L);
        settingsEntity.setType(AppSettingsType.STRING);
        settingsEntity.setId(1L);
        AppSettingsPropertyEntity propertyEntity = generatePropertyEntity();
        propertyEntity.setAppSettingsEntity(settingsEntity);
        settingsEntity.setAppSettingsPropertyEntities(propertyEntities);
        return settingsEntity;
    }

    public static AppSettingsPropertyEntity generatePropertyEntity() {
        AppSettingsPropertyEntity propertyEntity = new AppSettingsPropertyEntity();
        propertyEntity.setId(1L);
        propertyEntity.setPropertyValue("200x200");
        propertyEntity.setPropertyKey(AppSettingsPropertyKey.DIMENSIONS);
        return propertyEntity;
    }

    public static List<AppSettingsEntity> getAppSettingEntityList() {
        List<AppSettingsEntity> entities = new ArrayList<>();
        entities.add(generateAppSettingsEntity());
        return entities;
    }

    public static List<AppSettingsEntity> getAppSettingEntityListWithOutIds() {
        List<AppSettingsEntity> entities = new ArrayList<>();
        entities.add(generateAppSettingsEntityWithOutIds());
        return entities;
    }

    public static List<AppSettingsPropertyEntity> getPropertyEntityListWithIds() {
        List<AppSettingsPropertyEntity> entities = new ArrayList<>();
        entities.add(generatePropertyEntity());
        return entities;
    }

    public static List<AppSettingsPropertyEntity> getPropertyEntityListWithOutIds() {
        List<AppSettingsPropertyEntity> entities = new ArrayList<>();
        entities.add(generatePropertyEntityWithOutIds());
        return entities;
    }

    public static AppSettingsEntity generateAppSettingsEntityWithOutIds() {
        List<AppSettingsPropertyEntity> propertyEntities = new ArrayList<>();
        AppSettingsEntity settingsEntity = new AppSettingsEntity();
        settingsEntity.setActive(true);
        settingsEntity.setValue("test_link");
        settingsEntity.setKey(AppSettingsKey.LOGO_LINK);
        settingsEntity.setAdvertiserId(7146L);
        settingsEntity.setLicenseeId(33L);
        settingsEntity.setType(AppSettingsType.STRING);
        AppSettingsPropertyEntity propertyEntity = generatePropertyEntityWithOutIds();
        propertyEntity.setAppSettingsEntity(settingsEntity);
        settingsEntity.setAppSettingsPropertyEntities(propertyEntities);
        return settingsEntity;
    }

    public static AppSettingsPropertyEntity generatePropertyEntityWithOutIds() {
        AppSettingsPropertyEntity propertyEntity = new AppSettingsPropertyEntity();
        propertyEntity.setPropertyValue("200x200");
        propertyEntity.setPropertyKey(AppSettingsPropertyKey.DIMENSIONS);
        return propertyEntity;
    }

    public static AppSettingsDTO generateAppSettingsDTO() {
        AppSettingsDTO settingsDTO = new AppSettingsDTO();
        AppSettingsPropertyDTO propertyDTO = new AppSettingsPropertyDTO();
        List<AppSettingsPropertyDTO> propertyDTOS = new ArrayList<>();
        propertyDTO.setId(1L);
        propertyDTO.setPropertyValue("200x200");
        propertyDTO.setPropertyKey(AppSettingsPropertyKey.DIMENSIONS);
        propertyDTOS.add(propertyDTO);
        settingsDTO.setId(1L);
        settingsDTO.setActive(true);
        settingsDTO.setSettingsValue("test_link");
        settingsDTO.setLicenseeId(33L);
        settingsDTO.setAdvertiserId(7146L);
        settingsDTO.setSettingsType(AppSettingsType.STRING);
        settingsDTO.setSettingsKey(AppSettingsKey.LOGO_LINK);
        settingsDTO.setAppSettingsProperties(propertyDTOS);
        return settingsDTO;
    }

    public static AppSettingsDTO generateAppSettingsDTOWithOutIds() {
        AppSettingsDTO settingsDTO = new AppSettingsDTO();
        AppSettingsPropertyDTO propertyDTO = new AppSettingsPropertyDTO();
        List<AppSettingsPropertyDTO> propertyDTOS = new ArrayList<>();
        propertyDTO.setPropertyValue("200x200");
        propertyDTO.setPropertyKey(AppSettingsPropertyKey.DIMENSIONS);
        propertyDTOS.add(propertyDTO);
        settingsDTO.setActive(true);
        settingsDTO.setSettingsValue("test_link");
        settingsDTO.setLicenseeId(33L);
        settingsDTO.setAdvertiserId(7146L);
        settingsDTO.setSettingsType(AppSettingsType.STRING);
        settingsDTO.setSettingsKey(AppSettingsKey.LOGO_LINK);
        settingsDTO.setAppSettingsProperties(propertyDTOS);
        return settingsDTO;
    }

    public static List<AppSettingsDTO> generateSettingsDTOList() {
        List<AppSettingsDTO> settingsDTOS = new ArrayList<>();
        settingsDTOS.add(generateAppSettingsDTO());
        return settingsDTOS;
    }

    public static List<AppSettingsDTO> generateSettingsDTOListWithOutIds() {
        List<AppSettingsDTO> settingsDTOS = new ArrayList<>();
        settingsDTOS.add(generateAppSettingsDTOWithOutIds());
        return settingsDTOS;
    }

    public static CreativeHtmlMockupDTO generateHtmlMockup() {
        CreativeHtmlMockupDTO mockupDTO = new CreativeHtmlMockupDTO();
        CreativeDetails details = getCreativeBasicDetails(false);
        mockupDTO.setBasicDetails(details);
        CreativeHtmlFile htmlFile = new CreativeHtmlFile();
        htmlFile.setName("first_creative");
        htmlFile.setContentType("content_type");
        htmlFile.setType(CreativeType.templateHTML);
        htmlFile.setHtmlContent("HTML");
        htmlFile.setDco(false);
        htmlFile.setHeight(300);
        htmlFile.setWidth(300);
        htmlFile.setMacroList(null);
        htmlFile.setNative(false);
        htmlFile.setNoOfSlots(4);
        List<CreativeHtmlFile> htmlFiles = new ArrayList<>();
        htmlFiles.add(htmlFile);
        mockupDTO.setCreativeHtmlFiles(htmlFiles);
        return mockupDTO;
    }

    public static CreativeHtmlMockupDTO generateHtmlDcoMockup() {
        CreativeHtmlMockupDTO mockupDTO = new CreativeHtmlMockupDTO();
        CreativeDetails details = getCreativeBasicDetails(true);
        mockupDTO.setBasicDetails(details);
        CreativeHtmlFile htmlFile = new CreativeHtmlFile();
        htmlFile.setName("first_creative");
        htmlFile.setContentType("content_type");
        htmlFile.setType(CreativeType.templateHTML);
        htmlFile.setHtmlContent("HTML");
        htmlFile.setDco(true);
        htmlFile.setHeight(300);
        htmlFile.setWidth(300);
        htmlFile.setMacroList(null);
        htmlFile.setNative(false);
        htmlFile.setNoOfSlots(4);
        List<CreativeHtmlFile> htmlFiles = new ArrayList<>();
        htmlFiles.add(htmlFile);
        mockupDTO.setCreativeHtmlFiles(htmlFiles);
        return mockupDTO;
    }

    public static CreativeMockUpsDTO generateMockupDTO() {
        CreativeMockUpsDTO mockupDTO = new CreativeMockUpsDTO();
        CreativeDetails details = getCreativeBasicDetails(false);
        mockupDTO.setBasicDetails(details);
        CreativeFiles htmlFile = new CreativeFiles();
        htmlFile.setName("nmitlogo.png");
        htmlFile.setContentType("png");
        htmlFile.setType(CreativeType.image);
        htmlFile.setVideoAttribute(null);
        htmlFile.setFilePath("http://origin.atomex.net/cr_temp/nmitlogo.png");
        htmlFile.setDco(false);
        htmlFile.setHeight(83);
        htmlFile.setWidth(83);
        htmlFile.setMacroList(null);
        htmlFile.setNative(false);
        htmlFile.setNoOfSlots(0);
        List<CreativeFiles> htmlFiles = new ArrayList<>();
        htmlFiles.add(htmlFile);
        mockupDTO.setUploadedFiles(htmlFiles);
        return mockupDTO;
    }

    private static CreativeDetails getCreativeBasicDetails(boolean isDco) {
        CreativeDetails details = new CreativeDetails();
        details.setIsDCO(isDco);
        details.setAdvertiserId(8146L);
        details.setName("Test_creative");
        ClickDestination clickDestination = new ClickDestination();
        clickDestination.setAdvertiserId(8146L);
        clickDestination.setLicenseeId(33L);
        clickDestination.setCampaignType(CampaignType.RT);
        clickDestination.setName("Test_cd");
        clickDestination.setId(1L);
        details.setClickDestination(clickDestination);
        return details;
    }

    public static List<CreativeTemplateEntity> generateCreativeTemplateEntities() {
        List<CreativeTemplateEntity> entities = new ArrayList<>();
        CreativeTemplateEntity entity1 = getTemplateEntity(true, 300, 300, "htmlContent1" ,
                4, "variables_json_1","template_1", 123L);
        CreativeTemplateEntity entity2 = getTemplateEntity(true, 400, 400, "htmlContent2" ,
                3, "variables_json_2","template_2", 124L);
        CreativeTemplateEntity entity3 = getTemplateEntity(false, 450, 450, "htmlContent3" ,
                2, "variables_json_3","template_3", 125L);
        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);
        return entities;
    }

    private static CreativeTemplateEntity getTemplateEntity(boolean isActive, int height, int width,
            String htmlContent, int slots, String variables, String name, long id) {
        CreativeTemplateEntity entity = new CreativeTemplateEntity();
        entity.setActive(isActive);
        entity.setHeight(height);
        entity.setWidth(width);
        entity.setHtmlContent(htmlContent);
        entity.setTemplateId(id);
        entity.setDynamic(false);
        entity.setHasOverlay(false);
        entity.setMacros(null);
        entity.setSlots(slots);
        entity.setTemplateVariables(variables);
        entity.setTemplateName(name);
        return entity;
    }

    public static List<CreativeTemplateVariablesEntity> generateTemplateVariablesList() {
        List<CreativeTemplateVariablesEntity> entities = new ArrayList<>();
        CreativeTemplateVariablesEntity entity1 = getTemplateVariableEntity(true, "key_1",
                "title_1", 1L, TemplateVariableType.COLOR_PICKER, null);
        CreativeTemplateVariablesEntity entity2 = getTemplateVariableEntity(false, "currency",
                "currency", 2L, TemplateVariableType.TEXT_AREA, "currency");
        entities.add(entity1);
        entities.add(entity2);
        return entities;
    }

    private static CreativeTemplateVariablesEntity getTemplateVariableEntity(boolean isActive, String variableKey,
            String title, long id, TemplateVariableType type, String elasticIndex) {
        CreativeTemplateVariablesEntity entity = new CreativeTemplateVariablesEntity();
        entity.setActive(isActive);
        entity.setVariableId(id);
        entity.setVariableKey(variableKey);
        entity.setVariableTitle(title);
        entity.setVariableType(type);
        entity.setElasticSearchIndex(elasticIndex);
        return entity;
    }

    public static List<CreativeTemplateThemeEntity> generateTemplateThemesList() {
        List<CreativeTemplateThemeEntity> entities = new ArrayList<>();
        CreativeTemplateThemeEntity entity1 = getTemplateThemeEntity(true, "theme_1",
                "style_json_1," ,1L,1234L, 12345L,
                16780534L, null, null);
        CreativeTemplateThemeEntity entity2 = getTemplateThemeEntity(false, "theme_2",
                "style_json_2," ,2L,1234L, 12345L,
                16780534L, null, null);
        entities.add(entity1);
        entities.add(entity2);
        return entities;
    }

    public static CreativeTemplateThemeEntity getTemplateThemeEntity(boolean isActive, String name,
            String styleJson, long id, long advertiserId, Long createdBy, Long createdOn,
            Long modifiedOn, Long modifiedBy) {
        CreativeTemplateThemeEntity entity = new CreativeTemplateThemeEntity();
        entity.setThemeName(name);
        entity.setStyleJson(styleJson);
        entity.setCreatedBy(createdBy);
        entity.setCreatedOn(createdOn);
        entity.setModifiedBy(modifiedBy);
        entity.setModifiedOn(modifiedOn);
        entity.setId(id);
        entity.setAdvertiserId(advertiserId);
        entity.setActive(isActive);
        return entity;
    }

    public static TemplateThemeDTO generateTemplateThemeDTO(String name,
            String styleJson, Long advertiserId,Long id, Long createdBy, Long createdOn) {
        TemplateThemeDTO themeDTO = new TemplateThemeDTO();
        themeDTO.setThemeName(name);
        themeDTO.setStyleJson(styleJson);
        themeDTO.setAdvertiserId(advertiserId);
        themeDTO.setId(id);
        themeDTO.setCreatedOn(createdOn);
        themeDTO.setCreatedBy(createdBy);
        return themeDTO;
    }

  public static SearchRequest generateValidRequestForSKADPrivileges() {
    SearchRequest searchRequest = new SearchRequest();
    List<DashboardFilters> filtersList = new ArrayList<>();
    DashboardFilters filters = new DashboardFilters();
    filters.setColumn("advertiserId");
    filters.setValue("7146");
    filtersList.add(filters);
    searchRequest.setFilters(filtersList);
    return searchRequest;
  }

  public static SlicexChartResponse getSlicexChartResponse(){
     List<SlicexData> list = new ArrayList<>();
     list.add(MockDataGenerator.getSlicexData());
     SlicexChartResponse slicexChartResponse = new SlicexChartResponse();
     slicexChartResponse.setData(list);
     slicexChartResponse.setCompareData(list);
     slicexChartResponse.setTotalNoOfRecords(1);

     return slicexChartResponse;
  }

  public static ElasticResponse getElasticResponse(){
     StatusBaseObject object = new StatusBaseObject();
     object.setName("Test");
     object.setId(3425L);
     object.setActive(true);
     List<StatusBaseObject> list = new ArrayList<>();
     list.add(object);
     ElasticResponse elasticResponse = new ElasticResponse();
     elasticResponse.setData(list);
     elasticResponse.setTotalNoOfRecords(1);
     return elasticResponse;
  }

  public static FileDownloadResponse setFileDownloadResponse(){
     FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
     fileDownloadResponse.setFileDownloadUrl("www.komli.com");
     fileDownloadResponse.setFileName("filename.csv");

     return fileDownloadResponse;
  }

  public static SlicexListResponse getSlicexListResponse(){
     SlicexListResponse slicexListResponse = new SlicexListResponse();
     List<SlicexGridData> list = new ArrayList<>();
     list.add(MockDataGenerator.getSlicexGridData());
     slicexListResponse.setTotalNoOfRecords(1);
     slicexListResponse.setData(list);
     return slicexListResponse;
  }

  public static AdvertiserEntity createAdvertiserEntity() {
    AdvertiserEntity advertiserEntity = new AdvertiserEntity();

    advertiserEntity.setAdvertiserName("Honda");
    advertiserEntity.setIsActive(true);
    advertiserEntity.setAdvAddress("tribal");
    advertiserEntity.setAdvContactNumber("3425167");
    advertiserEntity.setMmpId(3412L);
    advertiserEntity.setIsLiftTestActive(true);
    advertiserEntity.setIsEventFilterAllowed(true);
    advertiserEntity.setTransactionCurrency("INR");
    advertiserEntity.setLanguageId(7L);
    advertiserEntity.setRegionId(1L);
    advertiserEntity.setId(3875L);
    advertiserEntity.setCurrencyId(73L);
    advertiserEntity.setTimeZoneId(372L);
    advertiserEntity.setLicenseeId(1L);
    advertiserEntity.setCategoryId(1L);
    advertiserEntity.setCreatedBy(2000L);
    advertiserEntity.setModifiedBy(4231L);
    advertiserEntity.setAdvEmail("tribal@test.com");
    advertiserEntity.setSystemEntryTime(1269848175L);
    advertiserEntity.setIsDmpAudienceSupport(true);
    advertiserEntity.setIsPlatformAudienceSupport(true);
    advertiserEntity.setWebDeclareUrl("http://124.153.85.202/hondajazz/overlord/hitpage.asp?cid=261");
    advertiserEntity.setIosDeclareUrl("http://124.153.85.202/hondajazz/overlord/hitpage.asp?cid=261");
    advertiserEntity.setAndroidDeclareUrl("http://124.153.85.202/hondajazz/overlord/hitpage.asp?cid=261");

    return advertiserEntity;
  }

  public static BaseModel createBaseModel() {
    BaseModel baseModel = new BaseModel();
    baseModel.setId(3875L);
    baseModel.setName("Honda");

    return baseModel;
  }

  public static CurrencyEntity createCurrencyEntity() {
    CurrencyEntity currencyEntity = new CurrencyEntity();
    currencyEntity.setCurrencyName("Test");
    currencyEntity.setCurrencyCode("1");
    currencyEntity.setId(2531L);

    return currencyEntity;
  }

  public static AdvertiserPojo createAdvertiserPojo() {
    AdvertiserPojo advertiserPojo = new AdvertiserPojo();

    advertiserPojo.setActive(true);
    advertiserPojo.setId(3875L);
    advertiserPojo.setName("Honda");
    advertiserPojo.setEmail("tribal@test.com");
    advertiserPojo.setCurrency(MockDataGenerator.createBaseModel());
    advertiserPojo.setLanguage(MockDataGenerator.createBaseModel());
    advertiserPojo.setCategory(MockDataGenerator.createBaseModel());
    advertiserPojo.setRegion(MockDataGenerator.createBaseModel());
    advertiserPojo.setTimeZone(MockDataGenerator.createBaseModel());
    advertiserPojo.setAndroidDeclareUrl("http://124.153.85.202/hondajazz/overlord/hitpage.asp?cid=261");
    advertiserPojo.setIosDeclareUrl("http://124.153.85.202/hondajazz/overlord/hitpage.asp?cid=261");
    advertiserPojo.setAndroidDeclareUrl("http://124.153.85.202/hondajazz/overlord/hitpage.asp?cid=261");
    advertiserPojo.setContactAddress("tribal");
    advertiserPojo.setMMP(MockDataGenerator.createBaseModel());
    advertiserPojo.setDomain("test");
    advertiserPojo.setCategory(MockDataGenerator.createBaseModel());
    advertiserPojo.setDefaultLogoDetails(MockDataGenerator.generateAppSettingsDTO());

    return advertiserPojo;
  }

  public static StatusTimeModel createStatusTimeModel() {
    StatusTimeModel statusTimeModel = new StatusTimeModel();

    statusTimeModel.setCreationTime(20191001095943L);
    statusTimeModel.setId(5528L);
    statusTimeModel.setName("Honda");
    statusTimeModel.setActive(true);
    statusTimeModel.setCreatedBy(20191001095943L);
    statusTimeModel.setModifiedTime(20201001112943L);
    return statusTimeModel;
  }

  public static AudienceDTO createAudienceDTO() {
    AudienceDTO audienceDTO = new AudienceDTO();
    audienceDTO.setDuration(1L);
    audienceDTO.setPixelId(2028L);
    audienceDTO.setId(375L);
    audienceDTO.setName(" Honda "+ " Clickers " + "R1");
    audienceDTO.setDescription("Clicker Audience");
    audienceDTO.setCreationTime(System.currentTimeMillis() / 1000);
    audienceDTO.setCreatedBy(MockDataGenerator.createStatusTimeModel().getCreatedBy());
    audienceDTO.setLicensee(MockDataGenerator.createBaseModel());
    audienceDTO.setAdvertiser(MockDataGenerator.createBaseModel());
    audienceDTO.setSegmentType(SegmentType.CLICKER.id);
    audienceDTO.setActive(Boolean.TRUE);
    audienceDTO.setRuleExpression(MockDataGenerator.getRuleDTO());
    audienceDTO.setDurationUnit(DurationUnit.DAY);
    audienceDTO.setUserDataType(UserDataType.MOBILE_APP.id);
    audienceDTO.setDataSourceType(DataSourceType.PIXEL_LOG.id);

    return audienceDTO;
  }

  public static Advertiser createAdvertiser() {
    Advertiser advertiser = new Advertiser();
    advertiser.setName("Honda");
    advertiser.setId(3875L);
    advertiser.setCreatedBy(20191001095943L);
    advertiser.setCreationTime(20191001095943L);
    advertiser.setActive(true);
    advertiser.setLicensee(MockDataGenerator.createBaseModel());
    advertiser.setLicenseeId(1L);
    advertiser.setCurrency(MockDataGenerator.createBaseModel());
    advertiser.setTimeZoneId(372L);

    return advertiser;
  }

  public static AdvertiserSettings createAdvertiserSettings() {
    AdvertiserSettings advertiserSettings = new AdvertiserSettings();
    advertiserSettings.setAdvertiserId(3875L);
    advertiserSettings.setMmp(MockDataGenerator.createBaseModel());
    advertiserSettings.setDateFormat("ddmmyyyy");
    advertiserSettings.setTransactionCurrency(MockDataGenerator.createBaseModel());
    advertiserSettings.setFeedKey(null);

    return advertiserSettings;
  }

  public static ClickDestination createClickDestination() {
    ClickDestination clickDestination = new ClickDestination();
    clickDestination.setName("Jntuworld 468x60");
    clickDestination.setActive(true);
    clickDestination.setId(6L);
    clickDestination.setAdvertiserId(4084L);
    clickDestination.setAndroidClickUrl("http://komli.com");
    clickDestination.setCreatedBy(1273494711L);
    clickDestination.setClickUrl("http://komli.com");
    clickDestination.setWebClickUrl("http://komli.com");
    clickDestination.setIosCLickUrl("http://komli.com");

    return clickDestination;
  }

  public static ClickDestinationEntity createClickDestinationEntity() {
    ClickDestinationEntity clickDestinationEntity = new ClickDestinationEntity();
    clickDestinationEntity.setName("Jntuworld 468x60");
    clickDestinationEntity.setClickUrl("http://komli.com");
    clickDestinationEntity.setAndroidClickUrl("http://komli.com");
    clickDestinationEntity.setCreatedBy(1273494711L);
    clickDestinationEntity.setStatus(CreativeStatus.active);
    clickDestinationEntity.setIsRefactored(true);
    clickDestinationEntity.setIsDco(true);
    clickDestinationEntity.setWebClickUrl("http://komli.com");
    clickDestinationEntity.setIosClickUrl("http;//komli.com");
    clickDestinationEntity.setSkadTarget(false);

    return clickDestinationEntity;
  }

  public static Pixel createPixel() {
    Pixel pixel = new Pixel();
    pixel.setActive(true);
    pixel.setName("TestPixel");
    pixel.setClickValidityWindow(600L);
    pixel.setFcapDuration(600L);
    pixel.setUserFcap(10L);
    pixel.setViewValidityWindow(600L);
    pixel.setAdvertiserPojo(MockDataGenerator.createAdvertiser());

    return pixel;
  }

  public static Pixel createPixelController() {
    Pixel pixel = new Pixel();
    pixel.setId(6L);
    pixel.setActive(true);
    pixel.setName("TestPixel");
    pixel.setClickValidityWindow(600L);
    pixel.setFcapDuration(600L);
    pixel.setUserFcap(10L);
    pixel.setViewValidityWindow(600L);

    return pixel;
  }

  public static ConversionPixelEntity createConversionPixelEntity() {
   ConversionPixelEntity conversionPixelEntity = new ConversionPixelEntity();
   conversionPixelEntity.setName("TestPixel");
   conversionPixelEntity.setId(8771L);
   conversionPixelEntity.setActive(true);
   conversionPixelEntity.setLicenseeId(1L);
   conversionPixelEntity.setClkValidityWindow(600L);
   conversionPixelEntity.setViewValidityWindow(600L);
   conversionPixelEntity.setType(ConversionPixelEntity.PixelType.HYBRID_CONV);

   return conversionPixelEntity;
  }

  public static FeedInfoEntity createFeedInfoEntity() {
    FeedInfoEntity feedInfoEntity = new FeedInfoEntity();
    feedInfoEntity.setLastModifiedInMillis(4536L);
    feedInfoEntity.setStatus(FeedInfoEntity.ProcessingStatus.COMPLETED);
    feedInfoEntity.setIsApiBased(false);
    feedInfoEntity.setName("Lenskart");
    feedInfoEntity.setId(14L);
    feedInfoEntity.setIsActive(true);
    feedInfoEntity.setAdvertiserId(5870L);
    feedInfoEntity.setFeedLocation("http://52.74.35.207/feed/lk_overall.xml");
    feedInfoEntity.setUpdateFrequencyInSeconds(86400L);

    return feedInfoEntity;
  }

  public static FeedInfoEntity createFeedInfoEntities() {
    FeedInfoEntity feedInfoEntity = new FeedInfoEntity();
    feedInfoEntity.setLastModifiedInMillis(4536L);
    feedInfoEntity.setStatus(FeedInfoEntity.ProcessingStatus.COMPLETED);
    feedInfoEntity.setIsApiBased(true);
    feedInfoEntity.setName("Lenskart");
    feedInfoEntity.setId(14L);
    feedInfoEntity.setIsActive(true);
    feedInfoEntity.setAdvertiserId(5870L);
    feedInfoEntity.setFeedLocation("http://52.74.35.207/feed/lk_overall.xml");
    feedInfoEntity.setUpdateFrequencyInSeconds(86400L);

    return feedInfoEntity;
  }

  public static CatalogFeed createCatalogFeed() {
    CatalogFeed catalogFeed = new CatalogFeed();
    catalogFeed.setName("Honda");
    catalogFeed.setIsApiBased(1);
    catalogFeed.setActive(true);
    catalogFeed.setAdvertiserId(5870L);
    catalogFeed.setUpdateFrequency(86400);

    return catalogFeed;
  }

  public static Macro createMacro() {
    Macro macro = new Macro();
    macro.setName("Honda");
    macro.setId(33L);
    macro.setAdvertiserId(3832L);

    return macro;
  }

  public static VariablesMappingDTO createVariablesMappingDTO() {
    VariablesMappingDTO variablesMappingDTO = new VariablesMappingDTO();
    variablesMappingDTO.setId(33L);
    variablesMappingDTO.setName("Honda");
    variablesMappingDTO.setFeedId(3832L);

    return variablesMappingDTO;
  }

  public static CampaignDTO createCampaignDTO() {
    CampaignDTO campaignDTO = new CampaignDTO();
    campaignDTO.setId(6429L);
    campaignDTO.setPixel(MockDataGenerator.createBaseModel());
    campaignDTO.setAdvertiserId(3874L);
    campaignDTO.setLicenseeId(1L);
    campaignDTO.setActive(true);
    campaignDTO.setEndTime(5531L);
    campaignDTO.setLifetimeBudget(new BigDecimal("23635"));
    campaignDTO.setPlatformMargin(BigDecimal.ONE);
    campaignDTO.setIvsDistribution(BigDecimal.TEN);
    campaignDTO.setPricingId(242365L);
    campaignDTO.setFlowRate(new BigDecimal("25365"));
    campaignDTO.setAttributionRatio(new BigDecimal("786"));
    campaignDTO.setCurrency(MockDataGenerator.createBaseModel());
    campaignDTO.setRegion(MockDataGenerator.createBaseModel());
    campaignDTO.setRetargeting(true);
    campaignDTO.setDailyUserFcap(5326257L);
    campaignDTO.setUserFcapDuration(282L);
    campaignDTO.setLifetimeDeliveryCap(326637L);
    campaignDTO.setDailyBudget(new BigDecimal("464"));
    campaignDTO.setStartTime(-1L);
    campaignDTO.setName("SafariTestIO");
    campaignDTO.setDailyUserFcap(45L);

    return campaignDTO;
  }

  public static DictionaryResponse getDictionaryResponse(){
    List<BaseModel> list = new ArrayList<>();
    list.add(MockDataGenerator.createBaseModel());
    DictionaryResponse dictionaryResponse = new DictionaryResponse();
    dictionaryResponse.setData(list);
    dictionaryResponse.setTotalNoOfRecords(1);

    return dictionaryResponse;
  }

  public static CampaignESDTO createCampaignESDTO() {
    CampaignESDTO campaignESDTO = new CampaignESDTO();
    campaignESDTO.setAdvertiserId(3874L);
    campaignESDTO.setName("SafariTestIO");
    campaignESDTO.setId(6429L);
    campaignESDTO.setEndTime(100L);
    campaignESDTO.setStartTime(2L);
    campaignESDTO.setFcap(2);
    campaignESDTO.setCurrencyCode("1");
    campaignESDTO.setSkadTarget(false);

    return campaignESDTO;
  }

  public static CampaignDTO createCampaignId() {
    CampaignDTO campaignDTO = new CampaignDTO();
    campaignDTO.setId(6429L);
    campaignDTO.setAdvertiserId(3874L);
    campaignDTO.setLicenseeId(1L);
    campaignDTO.setActive(true);
    campaignDTO.setName("SafariTestIO");
    campaignDTO.setPixel(MockDataGenerator.createPixel());
    campaignDTO.setCurrency(MockDataGenerator.createBaseModel());
    campaignDTO.setRegion(MockDataGenerator.createBaseModel());
    campaignDTO.setStartTime(3245L);
    campaignDTO.setDailyUserFcap(2346L);

    return campaignDTO;
  }

  public static CampaignEntity createCampaignEntity() {
    CampaignEntity campaignEntity = new CampaignEntity();
    campaignEntity.setActive(true);
    campaignEntity.setAdvertiserId(3874L);
    campaignEntity.setId(6429L);
    campaignEntity.setLicenseeId(1L);
    campaignEntity.setName("SafariTestIO");
    campaignEntity.setPricingId(256L);
    campaignEntity.setCurrencyId(32562L);
    campaignEntity.setRegionId(8L);

    return campaignEntity;
  }

  public static CampaignEntity createCampaign() {
    CampaignEntity campaignEntity = new CampaignEntity();
    campaignEntity.setAdvertiserId(3874L);
    campaignEntity.setId(6429L);
    campaignEntity.setLicenseeId(1L);
    campaignEntity.setName("SafariTestIO");
    campaignEntity.setPricingId(256L);

    return campaignEntity;
  }

  public static AdvertiserIOPixel createAdvertiserIOPixel() {
    AdvertiserIOPixel advertiserIOPixel = new AdvertiserIOPixel();
    advertiserIOPixel.setPixelId(3422L);
    advertiserIOPixel.setCampaignId(8932L);

    return advertiserIOPixel;
  }

  public static SegmentPixelMap createSegmentPixelMap() {
    SegmentPixelMap segmentPixelMap = new SegmentPixelMap();
    segmentPixelMap.setSegmentId(4321L);
    segmentPixelMap.setPixelId(6L);
    segmentPixelMap.setId(5643L);
    segmentPixelMap.setRuleExpression("(2)");

    return segmentPixelMap;
  }

  public static Segments createSegments() {
    Segments segments = new Segments();
    segments.setLicenseeId(3432L);
    segments.setId(5643L);
    segments.setName("Honda");
    segments.setStatus(Status.ACTIVE);

    return segments;
  }

  public static DataPixelDTO createDataPixelDTO() {
    DataPixelDTO dataPixelDTO = new DataPixelDTO();
    dataPixelDTO.setId(4325L);
    dataPixelDTO.setSourceType(DataSourceType.getById(1));

    return dataPixelDTO;
  }

  public static AudienceESDTO createAudienceESDTO() {
    AudienceESDTO audienceESDTO = new AudienceESDTO();
    audienceESDTO.setId(6L);
    audienceESDTO.setAdvertiserName("Honda");
    audienceESDTO.setName("Honda");
    audienceESDTO.setLicenseeId(3456L);
    audienceESDTO.setPixelId(7659L);

    return audienceESDTO;
  }

  public static DmpAudienceDTO createDmpAudienceDTO() {
    DmpAudienceDTO dmpAudienceDTO = new DmpAudienceDTO();
    dmpAudienceDTO.setLimit(2021);
    dmpAudienceDTO.setMsg("message");
    dmpAudienceDTO.setSegment_count(33L);
    dmpAudienceDTO.setStart(1);
    dmpAudienceDTO.setStatuscode(76);

    return dmpAudienceDTO;
  }

  public static StrategyEntity createStrategyEntity() {
    StrategyEntity strategyEntity = new StrategyEntity();
    LineItemType lineItemType = LineItemType.standard;
    strategyEntity.setName("SafariTest");
    strategyEntity.setActive(true);
    strategyEntity.setId(3875L);
    strategyEntity.setAdvertiserId(6429L);
    strategyEntity.setCreatedBy(1269590326L);
    strategyEntity.setLicenseeId(3435L);
    strategyEntity.setLiBudget(BigDecimal.valueOf(10000.000000000));
    strategyEntity.setCampianId(3875L);
    strategyEntity.setPricingId(2);
    strategyEntity.setDeliveryPriorityId(1);
    strategyEntity.setPacingTypeId(1);
    strategyEntity.setUserFcap(6L);
    strategyEntity.setUserFcapDuration(1440L);
    strategyEntity.setType(lineItemType);
    strategyEntity.setStartTime(new BigInteger("4"));
    strategyEntity.setEndTime(new BigInteger("8"));

    return strategyEntity;
  }

  public static StrategyDTO createStrategyDTO() {
    StrategyDTO strategyDTO = new StrategyDTO();
    AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
    BaseModel baseModel = new BaseModel();
    baseModel.setName("test");
    baseModel.setId(76L);
    List<BaseModel> list = new ArrayList<>();
    List<BaseModel> list1 = new ArrayList<>();
    list.add(MockDataGenerator.createBaseModel());
    list1.add(baseModel);
    audienceStrDTO.setCustomSegmentTargeting(true);
    audienceStrDTO.setCustomSegmentTargeting(true);
    audienceStrDTO.setBlockedSegments(list);
    audienceStrDTO.setTargetedSegments(list1);
    audienceStrDTO.setTargetedSegmentsOperator("and");
    strategyDTO.setName("SafariTest");
    strategyDTO.setId(1920L);
    strategyDTO.setFcap(6);
    strategyDTO.setActive(true);
    strategyDTO.setAdvertiser(MockDataGenerator.createBaseModel());
    strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
    strategyDTO.setStrategyType(LineItemType.standard.name());
    strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
    strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
    strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
    strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
    strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
    strategyDTO.setCampaignFcap(true);
    strategyDTO.setStartTime(new BigInteger("3"));
    strategyDTO.setEndTime(BigInteger.TEN);
    strategyDTO.setTargetWebSegments(audienceStrDTO);
    strategyDTO.setTargetAppSegments(audienceStrDTO);
    strategyDTO.setTargetDmpSegments(audienceStrDTO);
    strategyDTO.setPlacements(list);
    strategyDTO.setAdGroupCount(11);
    strategyDTO.setCampaignFcap(true);
    strategyDTO.setStrategyType("standard");
    strategyDTO.setBudgetBy(26959032);

    return strategyDTO;
  }

  public static StrategyDTO createStrategyDTOStartTime() {
    StrategyDTO strategyDTO = new StrategyDTO();
    BigInteger big = new BigInteger("1");
    BigInteger finalValue = big.negate();
    AudienceStrDTO audienceStrDTO = new AudienceStrDTO();
    List<BaseModel> list = new ArrayList<>();
    list.add(MockDataGenerator.createBaseModel());
    audienceStrDTO.setCustomSegmentTargeting(true);
    audienceStrDTO.setCustomSegmentTargeting(true);
    audienceStrDTO.setBlockedSegments(list);
    audienceStrDTO.setTargetedSegments(list);
    strategyDTO.setName("SafariTest");
    strategyDTO.setId(1920L);
    strategyDTO.setFcap(6);
    strategyDTO.setActive(true);
    strategyDTO.setDeliveryPriority(MockDataGenerator.createBaseModel());
    strategyDTO.setStrategyType(LineItemType.standard.name());
    strategyDTO.setPricingType(MockDataGenerator.createBaseModel());
    strategyDTO.setPacingType(MockDataGenerator.createBaseModel());
    strategyDTO.setCampaign(MockDataGenerator.createBaseModel());
    strategyDTO.setPricingValue(BigDecimal.valueOf(10000.000000000));
    strategyDTO.setBudgetValue(BigDecimal.valueOf(10000.000000000));
    strategyDTO.setStartTime(finalValue);
    strategyDTO.setEndTime(finalValue);
    strategyDTO.setTargetWebSegments(audienceStrDTO);
    strategyDTO.setPlacements(list);
    strategyDTO.setAdGroupCount(11);
    strategyDTO.setCampaignFcap(true);
    strategyDTO.setStrategyType("standard");
    strategyDTO.setBudgetBy(26959032);

    return strategyDTO;
  }

  public static Strategy createStrategy() {
    Strategy strategy = new Strategy();
    strategy.setName("SafariTest");
    strategy.setId(1920L);
    strategy.setAdvertiserId(6429L);
    strategy.setCreatedBy(1269590326L);
    strategy.setCampaign(MockDataGenerator.createBaseModel());
    strategy.setAdvertiser(MockDataGenerator.createBaseModel());

    return strategy;
  }

  public static StrategyUpdateDTO createStrategyUpdateDTO() {
    Set<Long> set = new HashSet<Long>();
    set.add(123L);
    set.add(234L);
    set.add(345L);
    StrategyUpdateDTO strategyUpdateDTO = new StrategyUpdateDTO();
    strategyUpdateDTO.setCreativeIdsToDelete(set);
    strategyUpdateDTO.setCreativeIdsToInsert(set);

    return strategyUpdateDTO;
  }

  public static DuplicateStrategyRequestDTO createDuplicateStrategyRequestDTO() {
    DuplicateStrategyRequestDTO duplicateStrategyRequestDTO = new DuplicateStrategyRequestDTO();
      duplicateStrategyRequestDTO.name = "SafariTest";
      duplicateStrategyRequestDTO.endTime=BigInteger.TEN;
      duplicateStrategyRequestDTO.startTime=BigInteger.ONE;
      duplicateStrategyRequestDTO.isNative = true;
      duplicateStrategyRequestDTO.duplicateAudienceTargeting = false;
      duplicateStrategyRequestDTO.duplicateDmpAudienceTargeting = false;
      duplicateStrategyRequestDTO.duplicateBrowserTargeting = false;
      duplicateStrategyRequestDTO.duplicateGeoTargeting = false;
      duplicateStrategyRequestDTO.duplicateDayPartTargeting = false;
      duplicateStrategyRequestDTO.duplicateMobileTargeting = false;
      duplicateStrategyRequestDTO.duplicatePlacementTargeting = false;
      duplicateStrategyRequestDTO.duplicateConnectionTypeTargeting = false;
      duplicateStrategyRequestDTO.duplicatecreativesAttached = false;
      duplicateStrategyRequestDTO.duplicateInventoryTargeting = false;

      return duplicateStrategyRequestDTO;
  }

  public static ParentBasedObject createParentBasedObject() {
    ParentBasedObject parentBasedObject = new ParentBasedObject();
    ParentBasedObject p1 = new ParentBasedObject(MockDataGenerator.createStatusTimeModel());
    parentBasedObject.setParent(p1);
    parentBasedObject.setName("SafariTest");
    parentBasedObject.setActive(true);
    parentBasedObject.setCreatedBy(1269590326L);
    parentBasedObject.setId(1920L);
    parentBasedObject.setCreationTime(26959032L);
    parentBasedObject.setModifiedTime(1130L);
    parentBasedObject.setModifiedBy(1230L);

    return parentBasedObject;
  }

  public static AdvertiserCatalogVariablesMappingEntity getAdvertiserCatalogVariablesMappingEntity(){
    AdvertiserCatalogVariablesMappingEntity acvmEntity = new AdvertiserCatalogVariablesMappingEntity();
    acvmEntity.setValueParsingRule("test");
    acvmEntity.setId(76L);
    acvmEntity.setIsMultivalued(true);
    acvmEntity.setAtomVariable(45L);
    acvmEntity.setFeedId(3412L);
    acvmEntity.setValueParsingRule("SafariTest");
    acvmEntity.setSelectPosition(6666L);
    acvmEntity.setSortType(AdvertiserCatalogVariablesMappingEntity.SortType.ASC);
    acvmEntity.setValueType(CatalogVariableValueType.DOUBLE);
    acvmEntity.setxPath("Test");

    return acvmEntity;
  }

  public static AdvertiserLineItemCreativeEntity createAdvertiserLineItemCreativeEntity() {
    AdvertiserLineItemCreativeEntity advertiserLineItemCreativeEntity = new AdvertiserLineItemCreativeEntity();
    advertiserLineItemCreativeEntity.setCreativeId(66L);
    advertiserLineItemCreativeEntity.setId(1920L);
    advertiserLineItemCreativeEntity.setStrategyId(1130L);

    return advertiserLineItemCreativeEntity;
  }

  public static CreativeEntity createCreativeEntity() {
    CreativeEntity creativeEntity = new CreativeEntity();
    creativeEntity.setId(3875L);
    creativeEntity.setAdvertiserId(3875L);
    creativeEntity.setName("SafariTestImg");
    creativeEntity.setUrlPath("/3874/windows_earth_flat_ad.png");
    creativeEntity.setWidth(380);
    creativeEntity.setHeight(780);
    creativeEntity.setStatus(CreativeStatus.inactive);
    creativeEntity.setCreatedBy(1269590256L);
    creativeEntity.setIsDco(true);

    return creativeEntity;
  }

  public static CreativeDTO createCreativeDTO() {
    CreativeDTO creativeDTO = new CreativeDTO();
    creativeDTO.setAdvertiser(MockDataGenerator.createBaseModel());
    creativeDTO.setAdvertiserId(6429L);
    creativeDTO.setId(1920L);
    creativeDTO.setCreatedBy(1269590256L);
    creativeDTO.setName("SafariTestImg");
    creativeDTO.setClickDestination(MockDataGenerator.createClickDestination());
    creativeDTO.setContent("content");

    return creativeDTO;
  }

  public static AdvertiserLineItemPixelEntity createAdvertiserLineItemPixelEntity() {
    AdvertiserLineItemPixelEntity advertiserLineItemPixelEntity = new AdvertiserLineItemPixelEntity();
    advertiserLineItemPixelEntity.setPixelId(6L);
    advertiserLineItemPixelEntity.setStrategyId(1L);

    return advertiserLineItemPixelEntity;
  }

  public static StrategyQuickEditDTO createStrategyQuickEditDTO() {
    StrategyQuickEditDTO strategyQuickEditDTO = new StrategyQuickEditDTO();
    strategyQuickEditDTO.setStrategyType(LineItemType.clickTracker);
    strategyQuickEditDTO.setName("standard");
    strategyQuickEditDTO.setBidCapMax(BigDecimal.valueOf(1234L));
    strategyQuickEditDTO.setPricingType(1);
    strategyQuickEditDTO.setCampaignFcap(true);
    strategyQuickEditDTO.setPricingValue(BigDecimal.valueOf(123.45));
    strategyQuickEditDTO.setCampaignId(23L);
    strategyQuickEditDTO.setCpaTargetValue(BigDecimal.valueOf(34.432));
    strategyQuickEditDTO.setId(23L);

    return strategyQuickEditDTO;
  }

  public static SiteListDTO createSiteListDTO() {
    SiteListDTO siteListDTO = new SiteListDTO();
    List<BaseModel> list = new ArrayList<>();
    list.add(MockDataGenerator.createBaseModel());
    siteListDTO.validSites = list;

    return siteListDTO;
  }

  public static CreativeStrategyAssociationStatus creativeStrategyAssociationStatus() {
    return null;
  }

  public static DTOCache createDtoCache() {
    String name = "test";

    return new DTOCache(name,null);
  }

  public static Pricing createPricing(){
    Pricing pricing = new Pricing();
    pricing.setId(3435L);
    pricing.setName("test");
    pricing.setDesc("standard");
    pricing.setFlag(1);
    pricing.setOrder(1);

    return pricing;
  }

  public static PacingType createPacingType(){
    PacingType pacingType = new PacingType();
    pacingType.setDescription("test");
    pacingType.setId(2324L);
    pacingType.setName("test");
    pacingType.setOrder(1);
    pacingType.settTL(2);

    return pacingType;
  }

  public static DeliveryPriority createDeliveryPriority() {
    DeliveryPriority deliveryPriority = new DeliveryPriority();
    deliveryPriority.setDescription("test");
    deliveryPriority.setId(2324L);
    deliveryPriority.setName("test");
    deliveryPriority.setPriorityClass(3);

    return deliveryPriority;
  }

  public static Platform createPlatform() {
    Platform platform = new Platform();
    platform.setId(2324L);
    platform.setName("test");
    platform.setCvrFactor(BigDecimal.ONE);
    platform.setCtrFactor(BigDecimal.TEN);

    return platform;
  }

  public static InventorySource createInventorySource() {
    InventorySource inventorySource = new InventorySource();
    inventorySource.setId(76L);
    inventorySource.setName("test");
    inventorySource.setStrategyId(3L);
    inventorySource.setBidStrategyId(1L);
    inventorySource.setTargetingExpression("test");

    return inventorySource;
  }

  public static BidStrategy createBidStrategy() {
    BidStrategy bidStrategy = new BidStrategy();
    bidStrategy.setId(76L);
    bidStrategy.setParams("76");
    bidStrategy.setTypeId(6);

    return bidStrategy;
  }

  public static AdvertiserLineItemTargetingExpression advertiserLineItemTargetingExpression(){
    AdvertiserLineItemTargetingExpression targetingExpression = new AdvertiserLineItemTargetingExpression();
    targetingExpression.setCommonTargetingExpression("test");
    targetingExpression.setPlacementTargetingExpression("test");
    targetingExpression.setId(2324L);
    targetingExpression.setStrategyId(6L);

    return targetingExpression;
  }

  public static TimeZoneDTO createTimeZoneDTO(){
    TimeZoneDTO timeZoneDTO = new TimeZoneDTO();
    timeZoneDTO.setId(7645L);
    timeZoneDTO.setName("TimeZoneDTOTest");

    return timeZoneDTO;
  }

  public static DcoAttributesEntity createDcoAttributesEntity() {
    DcoAttributeType dcoAttributeType = DcoAttributeType.flash;
    DcoAttributesEntity dcoAttributesEntity = new DcoAttributesEntity();
    dcoAttributesEntity.setDcoAttribute(dcoAttributeType);
    dcoAttributesEntity.setCreativeId(5647L);
    dcoAttributesEntity.setId(4567L);
    dcoAttributesEntity.setNoOfSlots(6);
    dcoAttributesEntity.setMacroList("MacroList");
    dcoAttributesEntity.setFallbackCreativeId(3L);

    return dcoAttributesEntity;
  }

  public static FilterComponent filterComponent(){
    return new FilterComponent(Filter.ID,"test");
  }

  public static OSMaster osMaster(){
    OSMaster osMaster = new OSMaster();
    osMaster.setId(3435L);
    osMaster.setName("SafariTest");
    osMaster.setChannelType(6L);

    return osMaster;
  }

  public static OSVersionMaster osVersionMaster() {
    OSVersionMaster osVersionMaster = new OSVersionMaster();
    osVersionMaster.setVersion(new BigDecimal("100"));
    osVersionMaster.setId(7890L);
    osVersionMaster.setName("SafariTest");
    osVersionMaster.setOsId(7L);

    return osVersionMaster;
  }

  public static ListPerformanceDataMetrics listPerformanceDataMetrics(){
    ListPerformanceDataMetrics list = new ListPerformanceDataMetrics();
    list.setAdvertiserid(new BigInteger("5"));
    list.setDay(new BigDecimal("3"));
    list.setHour(new BigDecimal("9"));
    list.setCampaignid(new BigInteger("7623"));
    list.setBidsplaced(new BigDecimal("5962"));
    list.setCreativeid(new BigInteger("1213"));
    list.setCreativeid(new BigInteger("1478"));

    return list;
  }

  public static ElasticResponse elasticResponse(){
    List<StatusBaseObject> list = new ArrayList<>();
    StatusBaseObject s = new StatusBaseObject();
    s.setActive(true);
    s.setId(33L);
    s.setName("test");
    list.add(s);
    ElasticResponse elasticResponse = new ElasticResponse();
    elasticResponse.setData(list);
    elasticResponse.setTotalNoOfRecords(20);

    return elasticResponse;
  }

  public static DashboardRequest getDashboardRequest(){
    Duration d = new Duration();
    d.setStartTimeStamp(56L);
    d.setEndTimeStamp(45612L);
    DashboardRequest dashboardRequest = new DashboardRequest();
    dashboardRequest.setDuration(d);

    return dashboardRequest;
  }

  public static AdvertiserToPixelEntity advertiserToPixelEntity(){
    AdvertiserToPixelEntity entity = new AdvertiserToPixelEntity();
    entity.setAdvertiserId(33L);
    entity.setPixelId(435L);
    entity.setId(6L);
    entity.setIsAutoUpdate(22L);
    entity.setStatus(Status.ACTIVE);

    return entity;
  }

  public static AggregatorLicenseeMappingEntity aggregatorLicenseeMappingEntity(){
    AggregatorLicenseeMappingEntity entity = new AggregatorLicenseeMappingEntity();
    entity.setAggregatorId(5634);
    entity.setLicenseeId(3421);
    entity.setId(8881);
    entity.setIsWhiteListed(true);

    return entity;
  }

  public static AtomCatalogVariablesEntity getAtomCatalogVariablesEntity(){
    AtomCatalogVariablesEntity atomCatalogVariablesEntity = new AtomCatalogVariablesEntity();
    atomCatalogVariablesEntity.setDescription("test");
    atomCatalogVariablesEntity.setId(3412L);
    atomCatalogVariablesEntity.setAdvertiserVertical("AdvertiserTest");
    atomCatalogVariablesEntity.setName("SafariTest");
    atomCatalogVariablesEntity.setIsMultivalued(true);

    return atomCatalogVariablesEntity;
  }

  public static VariablesMappingDTO setVariablesMappingDTO() {
    VariablesMappingDTO variablesMappingDTO = new VariablesMappingDTO();
    variablesMappingDTO.setDescription("test");
    variablesMappingDTO.setVariablePath("http://komli.com");
    variablesMappingDTO.setId(55L);
    variablesMappingDTO.setName("SafariTest");
    variablesMappingDTO.setFeedId(76L);

    return variablesMappingDTO;
  }

  public static CurrencyModel getCurrencyModel(){
    CurrencyModel currencyModel = new CurrencyModel();
    currencyModel.setCurrencyCode("INR");
    currencyModel.setId(3424L);
    currencyModel.setName("test");

    return currencyModel;
  }

  public static DashboardFilters getDashBoardFilters() {
    DashboardFilters filters = new DashboardFilters();
    filters.setOperator("AND");
    filters.setValue("76");
    filters.setColumn("licensee");

    return filters;
  }

  public static DashboardFilters getDashBoardFilter() {
    DashboardFilters filters = new DashboardFilters();
    filters.setOperator("AND");
    filters.setValue("76");
    filters.setColumn("licensee");

    return filters;
    }

  public static SlicexFilter getSlicexFilter(){
    SlicexFilter slicexFilter = new SlicexFilter();
    Set<Long> set = new HashSet<>();
    set.add(3412L);
    set.add(8978L);
    set.add(7869L);
    slicexFilter.setEntity(SlicexEntity.licensee);
    slicexFilter.setIds(set);

    return slicexFilter;
  }

    public static SlicexFilter getSlicexFilters(){
        SlicexFilter slicexFilter = new SlicexFilter();
        Set<Long> set = new HashSet<>();
        set.add(CampaignObjective.WEBSITE.getId());
        slicexFilter.setEntity(SlicexEntity.campaignObjective);
        slicexFilter.setIds(set);

        return slicexFilter;
    }

  public static SlicexData getSlicexData(){
    SlicexData slicexData = new SlicexData();
    slicexData.setDay(BigDecimal.ONE);

    return slicexData;
  }

  public static SlicexGridData getSlicexGridData(){
    SlicexGridData slicexGridData = new SlicexGridData();
    slicexGridData.setClicks(BigDecimal.ONE);
    slicexGridData.setCost(BigDecimal.TEN);

    return slicexGridData;
  }

  public static DataPixelDTO getDataPixelDTO(){
    DataPixelDTO dataPixelDTO = new DataPixelDTO();
    dataPixelDTO.setSourceType(DataSourceType.FILE_UPLOAD);
    dataPixelDTO.setDescription("test");
    dataPixelDTO.setName("SafariTest");
    dataPixelDTO.setUuCount(2L);
    dataPixelDTO.setPiCount(31415926535898L);

    return dataPixelDTO;
  }

  public static MobileMeasurementPartner getMobileMeasurementPartner(){
    MobileMeasurementPartner mmp = new MobileMeasurementPartner();
    mmp.setId(76L);
    mmp.setName("test");
    mmp.setAndroidClickUrl("http://komli.com");
    mmp.setAndroidS2sUrl("http://komli.com");
    mmp.setIosClickUrl("http://komli.com");
    mmp.setIosS2sUrl("http://komli.com");

    return mmp;
  }

  public static RuleComponent getRuleComponent() {
    RuleComponent ruleComponent = new RuleComponent();
    ruleComponent.setId(7634);
    ruleComponent.setRuleFilterId(7923L);
    ruleComponent.setRuleValue("test");
    ruleComponent.setRuleOperatorId(622L);
    ruleComponent.setSegmentPixelExpressionId(2654L);

    return ruleComponent;
  }

  public static PixelDataFileDTO getPixelDataFileDTO(){
    PixelDataFileDTO dto = new PixelDataFileDTO();
    dto.setPixelId(5627L);
    dto.setId(76L);
    dto.setLicenseeId(6634L);
    dto.setName("SafariTest");
    dto.setStatus(CrmStatus.COMPLETED);
    dto.setCompressionType(CompressionType.BZIP2);
    dto.setCreatedAt(2020L);
    dto.setEncodingType(EncodingType.MD5);
    dto.setLastModifiedAtServer(3025L);
    dto.setUserDataType(UserDataType.CRM_PHONE);

    return dto;
  }

  public static RuleComponentDTO getRuleComponentDTO(){
    RuleComponentDTO ruleComponentDTO = new RuleComponentDTO();
    ruleComponentDTO.setId(7634);
    ruleComponentDTO.setNegate(true);
    ruleComponentDTO.setFilterId(23L);
    ruleComponentDTO.setValue("Test");
    ruleComponentDTO.setOperatorId(2L);

    return ruleComponentDTO;
  }

  public static RuleDTO getRuleDTO(){
    RuleComponentDTO dto = MockDataGenerator.getRuleComponentDTO();
    RuleDTO ruleDTO = new RuleDTO();
    ruleDTO.setNegate(true);
    ruleDTO.setSimpleExpr(true);
    ruleDTO.setRuleElement(dto);
    ruleDTO.setOperator(Operator.AND);

    return ruleDTO;
  }

  public static RuleValueDto getRuleValueDto(){
    RuleValueDto ruleValueDto = new RuleValueDto();
    ruleValueDto.setValue("2314");
    ruleValueDto.setDisplayValue("5463");

    return ruleValueDto;
  }

  public static RuleOperator getRuleOperator(){
    RuleOperator ruleOperator = new RuleOperator();
    ruleOperator.setFbxOperatorName("Test");
    ruleOperator.setId(44L);
    ruleOperator.setOperatorName("AND");
    ruleOperator.setOperatorDisplayName("AND");

    return ruleOperator;
  }

  public static RuleValue getRuleValue(){
    RuleValue ruleValue = new RuleValue();
    ruleValue.setValue("3412");
    ruleValue.setDisplayValue("7856");
    ruleValue.setId(6549L);
    ruleValue.setFilterId(3324L);

    return ruleValue;
  }

  public static RuleFilter getRuleFilter(){
    Set<RuleValue> ruleValueSet = new HashSet<>();
    ruleValueSet.add(MockDataGenerator.getRuleValue());
    Set<RuleOperator> set = new HashSet<>();
    set.add(MockDataGenerator.getRuleOperator());
    RuleFilter ruleFilter = new RuleFilter();
    ruleFilter.setFilterName("Test");
    ruleFilter.setRuleFilterType(RuleFilterType.OPTIONS);
    ruleFilter.setFbxFilterName("FilterTest");
    ruleFilter.setId(33L);
    ruleFilter.setFilterDisplayName("Honda");
    ruleFilter.setRuleValueType(RuleValueType.DOUBLE);
    ruleFilter.setRuleOperatorDto(set);
    ruleFilter.setRuleValueDto(ruleValueSet);

    return ruleFilter;
  }

  public static ElasticSearchTerm getElasticSearchTerm(){
    ElasticSearchTerm elasticSearchTerm = new ElasticSearchTerm();
    elasticSearchTerm.setAdvertisers(4378L);
    elasticSearchTerm.setLicenseeId(672189L);
    elasticSearchTerm.setFilters("Test","Key");
    elasticSearchTerm.setAggregators(685764L);

    return elasticSearchTerm;
  }
}
