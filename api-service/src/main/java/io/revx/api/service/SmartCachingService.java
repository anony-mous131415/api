package io.revx.api.service;

import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.catalog.CatalogService;
import io.revx.api.service.creative.CreativeService;
import io.revx.api.service.strategy.StrategyService;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.Licensee;
import io.revx.core.model.Strategy;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.core.model.requests.SearchRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.revx.api.utility.Util.getYesterdayDuration;
import static io.revx.api.utility.Util.getLastWeekDuration;
import static io.revx.core.constant.Constants.ADVERTISER_ID;
import static io.revx.core.constant.Constants.LICENSEE_ID;
import static io.revx.core.constant.Constants.STRATEGY_ID;
import static io.revx.core.constant.Constants.CAMPAIGN_ID;
import static io.revx.core.constant.Constants.GROUP_BY_DAY;
import static io.revx.core.constant.Constants.GROUP_BY_HOURLY;

@Component
@PropertySource("application.properties")
public class SmartCachingService {

    private static final Logger logger = LogManager.getLogger(SmartCachingService.class);

    @Autowired
    private EntityESService esService;

    @Autowired
    private DashBoardService dashBoardService;

    @Autowired
    private CreativeService creativeService;

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private CatalogService catalogService;

    @Value("${smartCaching.strategyCount:400}")
    private String strategyCount;

    private static final String STRATEGY_SORT_FILTER = "modifiedTime-";
    private static final int DEFAULT_PAGE_NUMBER = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final boolean IS_REFRESH = true;
    private static final boolean SHOW_UU = true;
    private static final String DEFAULT_SORT = null;
    private static final String SORT_BY_ID = "id-";
    private static final String ACTIVE = "active";
    private static final String TRUE = "true";
    private static final String EMPTY_STRING = "";

    private List<Strategy> strategies;
    private Map<Long, Long> strategyIdToLicenseeId;
    private Map<Long, Long> campaignIdToLicenseeId;
    private Map<Long, Long> advertiserIdToLicenseeId;
    private Set<Long> licenseeIds;
    private Map<Long, String> licenseeCurrencyCodeMapping;
    private Map<Long, String> advertiserCurrencyCodeMapping;
    private Duration yesterdayDuration;
    private Duration weekDuration;
    private String licenseeCurrencyCode;
    private Long licenseeId;

    /**
     *The smart caching has been scheduled as a cron job and as well as REST endpoint
     *
     *It is broken done in two smaller tasks performed by respective methods
     */
    @Scheduled(cron = "${smartCaching.cron.expression:0 0 5 * * *}")
    public void schedulingSmartCaching() {
        initialize();
        getLatestStrategies();
        setEntityMappings();
        setCurrencyCodeMappings();
        setDurations();
        licenseeListData();
        licenseeChartData();
        advertiserListData();
        advertiserChartData();
        campaignListData();
        campaignChartData();
        strategyChartData();
        licenseeCreativeSearch();
        advertiserCreativeSearch();
        advertiserCatalogFeed();
        fetchStrategyData();
        cleanUp();
    }

    private void initialize() {
        strategies = new ArrayList<>();
        strategyIdToLicenseeId = new HashMap<>();
        campaignIdToLicenseeId = new HashMap<>();
        advertiserIdToLicenseeId = new HashMap<>();
        licenseeIds = new HashSet<>();
        licenseeCurrencyCodeMapping = new HashMap<>();
        advertiserCurrencyCodeMapping = new HashMap<>();
        yesterdayDuration = new Duration();
        weekDuration = new Duration();
    }

    private void cleanUp() {
        strategies = null;
        strategyIdToLicenseeId = null;
        campaignIdToLicenseeId = null;
        advertiserIdToLicenseeId = null;
        licenseeIds = null;
        licenseeCurrencyCodeMapping = null;
        advertiserCurrencyCodeMapping = null;
        yesterdayDuration = null;
        weekDuration= null;
    }

    /**
     * Elastic search is queried to get the latest modified / created strategies among all
     * the license
     * strategy count is default at 200 and can be configurable from properties files
     */
    private void getLatestStrategies() {
        ElasticSearchTerm searchTerm = new ElasticSearchTerm();
        searchTerm.setPageSize(Integer.parseInt(strategyCount));
        List<String> sortFilters = new ArrayList<>();
        sortFilters.add(STRATEGY_SORT_FILTER);
        searchTerm.setSortList(sortFilters);
        Map<Long, ?> idModelMap = esService.search(TablesEntity.STRATEGY,searchTerm);
        for (Map.Entry<Long, ?> entry : idModelMap.entrySet()) {
            Strategy strategy = (Strategy) entry.getValue();
            strategies.add(strategy);
        }
    }

    /**
     * For the fetched strategies , the respective strategy->licensee , campaign->licensee , advertiser->licensee
     * mappings are populated.
     * For every entity, licensee mapping is needed because , the dashboard service depends on loginUserDetailsService
     * for determining licensee and currency code. As it is a scheduled job , that bean will not be available.
     * populating entity mapping and currency mappings needed by the service
     */
    private void setEntityMappings() {
        for (Strategy strategy : strategies) {
            strategyIdToLicenseeId.put(strategy.getId(), strategy.getLicenseeId());
            campaignIdToLicenseeId.put(strategy.getCampaignId(),strategy.getLicenseeId());
            advertiserIdToLicenseeId.put(strategy.getAdvertiserId(),strategy.getLicenseeId());
            licenseeIds.add(strategy.getLicenseeId());
        }
        logger.debug("Strategies scheduled for caching are : {} ", strategyIdToLicenseeId.keySet());
        logger.debug("Campaigns scheduled for caching are : {} ", campaignIdToLicenseeId.keySet());
        logger.debug("Advertisers scheduled for caching are : {} ", advertiserIdToLicenseeId.keySet());
        logger.debug("Licensees scheduled for caching are : {} ", licenseeIds);
    }

    /**
     * licensee->currency code and advertiser->currency code mappings are populated from the elasticsearch index
     */
    private void setCurrencyCodeMappings() {
        List<Long> licenseeIdList = new ArrayList<>(licenseeIds);
        List<?> licenseeData = esService.searchPojoByIdList(TablesEntity.LICENSEE,licenseeIdList);
        for (Object entity : licenseeData) {
            Licensee licensee = (Licensee) entity;
            licenseeCurrencyCodeMapping.put(licensee.getId(), licensee.getCurrencyCode());
        }
        List<Long> advertiserIds = new ArrayList<>(advertiserIdToLicenseeId.keySet());
        List<?> advertiserData = esService.searchPojoByIdList(TablesEntity.ADVERTISER,advertiserIds);
        for (Object entity : advertiserData) {
            Advertiser advertiser = (Advertiser) entity;
            advertiserCurrencyCodeMapping.put(advertiser.getId(), advertiser.getCurrencyCode());
        }
    }

    /**
     * Currently smartCaching is considered for populating the cache with yesterday and week long data cache keys,
     * generating the respective time stamps for both durations with UTC as the timeZone
     */
    private void setDurations() {
        yesterdayDuration = getYesterdayDuration();
        weekDuration = getLastWeekDuration();
        logger.debug("Caching timestamps for yesterday duration startTime : {} and endTime : {} "
                , yesterdayDuration.getStartTimeStamp(),yesterdayDuration.getEndTimeStamp());
        logger.debug("Caching timestamps for week duration startTime : {} and endTime : {} "
                , weekDuration.getStartTimeStamp(),weekDuration.getEndTimeStamp());
    }

    /**
     * For each fetched licensee , the performance chart data is populated for yesterday and week long data
     */
    private void licenseeChartData() {
        List<DashboardFilters> filters = new ArrayList<>();
        for (Long licensee : licenseeIds) {
            licenseeId = licensee;
            licenseeCurrencyCode = licenseeCurrencyCodeMapping.get(licensee);
            DashboardRequest request = new DashboardRequest();
            request.setFilters(filters);
            dashBoardChartDataOfYesterday(request,LICENSEE_ID,String.valueOf(licensee));
            request = new DashboardRequest();
            request.setFilters(filters);
            dashBoardChartDataOfWeek(request,LICENSEE_ID,String.valueOf(licensee));
        }
    }

    /**
     * For each fetched Advertiser , the performance chart data is populated for yesterday and week long data
     */
    private void advertiserChartData() {
        requestDashBoardChartData(advertiserIdToLicenseeId, ADVERTISER_ID);
    }

    /**
     * For each fetched campaign , the performance chart data is populated for yesterday and week long data
     */
    private void campaignChartData() {
        requestDashBoardChartData(campaignIdToLicenseeId, CAMPAIGN_ID);
    }

    /**
     * For each fetched Strategy , the performance chart data is populated for yesterday and week long data
     */
    private void strategyChartData() {
        requestDashBoardChartData(strategyIdToLicenseeId, STRATEGY_ID);
    }

    private void requestDashBoardChartData(Map<Long, Long> entityToLicensee, String filter) {
        for (Map.Entry<Long, Long> entry : entityToLicensee.entrySet()) {
            DashboardRequest request = getChartDashBoardRequest(filter,entry);
            dashBoardChartDataOfYesterday(request,filter,String.valueOf(entry.getKey()));
            request = getChartDashBoardRequest(filter,entry);
            dashBoardChartDataOfWeek(request,filter,String.valueOf(entry.getKey()));
        }
    }

    /**
     * The dashBoardRequest needed for the dashBoard service chart API is populated with the filter
     * like ADVERTISER_ID->2455 , CAMPAIGN_ID->34455 etc
     *
     * @param filter - entity string like advertiser, campaign,strategy
     * @param entry - entity to licensee mapping record
     *
     * @return - DashboardRequest
     */
    private DashboardRequest getChartDashBoardRequest(String filter, Map.Entry<Long, Long> entry) {
        DashboardRequest request = new DashboardRequest();
        licenseeId = entry.getValue();
        licenseeCurrencyCode = licenseeCurrencyCodeMapping.get(entry.getValue());
        List<DashboardFilters> filters = new ArrayList<>();
        DashboardFilters dashboardFilter = new DashboardFilters();
        dashboardFilter.setColumn(filter);
        dashboardFilter.setValue(String.valueOf(entry.getKey()));
        filters.add(dashboardFilter);
        request.setFilters(filters);
        return request;
    }

    /**
     * DashBoardService chart API is invoked with the request and yesterday time duration and group by based on hour
     *
     * @param request - DashboardRequest
     * @param filter - entity string like advertiser, campaign,strategy
     * @param filterValue - entityId of advertiser,campaign,strategy
     */
    private void dashBoardChartDataOfYesterday(DashboardRequest request,String filter, String filterValue) {
        try {
            request.setDuration(yesterdayDuration);
            request.setGroupBy(GROUP_BY_HOURLY);
            dashBoardService.getDashboardDataChart(request, IS_REFRESH, SHOW_UU);
        } catch (ValidationException validationException) {
            logger.info("Validation failed for {} : {} : during  {} ", filter, filterValue, request.getDuration());
        }
    }

    /**
     * DashBoardService chart API is invoked with the request and week time duration and group by based on day
     *
     * @param request - DashboardRequest
     * @param filter - entity like advertiser, campaign,strategy
     * @param filterValue - entityId of advertiser,campaign,strategy
     */
    private void dashBoardChartDataOfWeek(DashboardRequest request,String filter, String filterValue) {
        try {
            request.setGroupBy(GROUP_BY_DAY);
            request.setDuration(weekDuration);
            dashBoardService.getDashboardDataChart(request,IS_REFRESH, SHOW_UU);
        } catch (ValidationException exception) {
            logger.info("Validation failed for {} : {} : during  {} ",filter,filterValue,request.getDuration());
        }
    }

    /**
     * For each fetched licensee , the performance list data is populated for yesterday and week long data for
     * ADVERTISER , CAMPAIGN , STRATEGY entities
     */
    private void licenseeListData() {
        List<DashBoardEntity> dashBoardEntities = new ArrayList<>();
        dashBoardEntities.add(DashBoardEntity.ADVERTISER);
        dashBoardEntities.add(DashBoardEntity.CAMPAIGN);
        dashBoardEntities.add(DashBoardEntity.STRATEGY);
        for (Long licensee : licenseeIds) {
            for (DashBoardEntity entity : dashBoardEntities) {
                DashboardRequest request = getLicenseeListDashBoardRequest(licensee);
                dashBoardListDataOfYesterday(entity, request, LICENSEE_ID, String.valueOf(licensee));
                request = getLicenseeListDashBoardRequest(licensee);
                dashBoardListDataOfWeek(entity, request, LICENSEE_ID, String.valueOf(licensee));
            }
        }
    }

    /**
     * For each fetched advertiser , the campaign performance list data is populated for yesterday and week long data
     */
    private void advertiserListData() {
        DashBoardEntity entity = DashBoardEntity.CAMPAIGN;
        requestDashBoardListData(advertiserIdToLicenseeId,entity,ADVERTISER_ID);
    }

    /**
     * For each fetched campaign , the strategy performance list data is populated for yesterday and week long data
     */
    private void campaignListData() {
        DashBoardEntity entity = DashBoardEntity.STRATEGY;
        requestDashBoardListData(campaignIdToLicenseeId,entity,CAMPAIGN_ID);
    }

    /**
     * The dashBoardRequest needed for the dashBoard service list API is populated with the filter of licensee entity
     *
     * @param licensee - licensee id
     * @return - DashboardRequest
     */
    private DashboardRequest getLicenseeListDashBoardRequest(long licensee) {
        DashboardRequest request = new DashboardRequest();
        List<DashboardFilters> filters = new ArrayList<>();
        filters.add(getActiveFilter());
        request.setFilters(filters);
        licenseeId = licensee;
        licenseeCurrencyCode = licenseeCurrencyCodeMapping.get(licensee);
        return request;
    }

    private void requestDashBoardListData(Map<Long, Long> entityToLicensee, DashBoardEntity boardEntity, String filter) {
        for (Map.Entry<Long, Long> entry : entityToLicensee.entrySet()) {
            DashboardRequest request = getListDashBoardRequest(filter, entry);
            dashBoardListDataOfYesterday(boardEntity, request,filter,String.valueOf(entry.getKey()));
            request = getListDashBoardRequest(filter, entry);
            dashBoardListDataOfWeek(boardEntity, request,filter,String.valueOf(entry.getKey()));
        }
    }

    /**
     * The dashBoardRequest needed for the dashBoard service list API is populated with the filter
     *      * like ADVERTISER_ID->2455 , CAMPAIGN_ID->34455 etc
     *
     * @param filter - entity name like advertiser,campaign,strategy
     * @param entry - entity to licensee mapping record
     * @return DashboardRequest
     */
    private DashboardRequest getListDashBoardRequest(String filter, Map.Entry<Long, Long> entry) {
        DashboardRequest request = new DashboardRequest();
        licenseeId = entry.getValue();
        licenseeCurrencyCode = licenseeCurrencyCodeMapping.get(entry.getValue());
        List<DashboardFilters> filters = new ArrayList<>();
        filters.add(getActiveFilter());
        DashboardFilters dashboardFilter = new DashboardFilters();
        dashboardFilter.setColumn(filter);
        dashboardFilter.setValue(String.valueOf(entry.getKey()));
        filters.add(dashboardFilter);
        request.setFilters(filters);
        return request;
    }

    /**
     * DashBoardService List API is invoked with the request and yesterday time duration and group by based on hour
     *
     * @param entity
     * @param request
     * @param filter
     * @param filterValue
     */
    private void dashBoardListDataOfYesterday(DashBoardEntity entity, DashboardRequest request, String filter, String filterValue) {
        try {
            request.setDuration(yesterdayDuration);
            dashBoardService.getDashboardDataList(DEFAULT_PAGE_NUMBER,
                    DEFAULT_PAGE_SIZE, DEFAULT_SORT, request, entity, IS_REFRESH, SHOW_UU);
        } catch (ValidationException validationException) {
            logger.info("Validation failed for {} : {} : during  {} ", filter, filterValue, request.getDuration());
        }
    }

    /**
     * DashBoardService List API is invoked with the request and week time duration and group by based on day
     *
     * @param entity
     * @param request
     * @param filter
     * @param filterValue
     */
    private void dashBoardListDataOfWeek(DashBoardEntity entity, DashboardRequest request, String filter, String filterValue) {
        try {
            request.setDuration(weekDuration);
            dashBoardService.getDashboardDataList(DEFAULT_PAGE_NUMBER,
                    DEFAULT_PAGE_SIZE, DEFAULT_SORT, request, entity, IS_REFRESH, SHOW_UU);
        } catch (ValidationException exception) {
            logger.info("Validation failed for {} : {} : during  {} ",filter,filterValue,request.getDuration());
        }
    }

    /**
     * List data of an entity like is generated only for active entities , appending the set of filters with
     * active filters
     *
     * @return DashboardFilters
     */
    private DashboardFilters getActiveFilter() {
        DashboardFilters filters = new DashboardFilters();
        filters.setColumn(ACTIVE);
        filters.setValue(TRUE);
        return filters;
    }

    /**
     The cache is pre populated with list of creatives along with performance data
     *   of each creative at a licensee level for weekly and yesterday duration
     */
    private void licenseeCreativeSearch() {
        for (Long licensee : licenseeIds) {
            DashboardRequest request = getLicenseeListDashBoardRequest(licensee);
            request.setDuration(weekDuration);
            request.setGroupBy(EMPTY_STRING);
            try {
                creativeService.searchCreatives(request, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, SORT_BY_ID, IS_REFRESH);
            }catch (ValidationException exception) {
                logger.info("Validation failed for Licensee : {} : during  {} ",licensee,request.getDuration());
            }
            request = getLicenseeListDashBoardRequest(licenseeId);
            request.setDuration(yesterdayDuration);
            request.setGroupBy(EMPTY_STRING);
            try {
                creativeService.searchCreatives(request, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, SORT_BY_ID, IS_REFRESH);
            }catch (ValidationException exception) {
                logger.info("Validation failed for Licensee : {} : during  {} ",licensee,request.getDuration());
            }
        }
    }

    /**
     *  The cache is pre populated with list of creatives along with performance data
     *   of each creative at a advertiser level for weekly and yesterday duration
     */
    private void advertiserCreativeSearch() {
        for (Map.Entry<Long, Long> entry : advertiserIdToLicenseeId.entrySet()) {
            DashboardRequest request = getListDashBoardRequest(ADVERTISER_ID,entry);
            request.setDuration(weekDuration);
            request.setGroupBy(EMPTY_STRING);
            try {
                creativeService.searchCreatives(request, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, SORT_BY_ID, IS_REFRESH);
            }catch (ValidationException exception) {
                logger.info("Validation failed for Advertiser : {} : during  {} ",entry.getValue(),request.getDuration());
            }
            request = getLicenseeListDashBoardRequest(licenseeId);
            request.setDuration(yesterdayDuration);
            request.setGroupBy(EMPTY_STRING);
            try {
                creativeService.searchCreatives(request, DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, SORT_BY_ID, IS_REFRESH);
            }catch (ValidationException exception) {
                logger.info("Validation failed for Advertiser : {} : during  {} ",entry.getValue(),request.getDuration());
            }
        }
    }

    /**
     *  The cache is pre populated with list of catalog feed at a advertiser level daily
     */
    private void advertiserCatalogFeed() {
        logger.info("Smart caching initiated for advertiser catalog feed fetch");
        for (Map.Entry<Long, Long> entry : advertiserIdToLicenseeId.entrySet()) {
            SearchRequest searchRequest = new SearchRequest();
            List<DashboardFilters> filters = new ArrayList<>();
            filters.add(getActiveFilter());
            searchRequest.setFilters(filters);
            try {
                catalogService.getFeeds(entry.getKey(),DEFAULT_PAGE_NUMBER,searchRequest,DEFAULT_PAGE_SIZE,SORT_BY_ID,IS_REFRESH);
            } catch (Exception e) {
                logger.info("Failed to retrieve catalog feed for the advertiser : {} ",entry.getKey());
            }
        }
        logger.info("Smart caching completed for advertiser catalog feed fetch");
    }

    private void fetchStrategyData() {
        logger.info("Smart caching initiated for fetching strategy details");
        for (Map.Entry<Long, Long> entry : strategyIdToLicenseeId.entrySet()) {
            licenseeId = entry.getValue();
            try {
                strategyService.get(entry.getKey(),IS_REFRESH);
            } catch (Exception e) {
                logger.info("Failed to retrieve strategy details for the strategy id : {} ",entry.getKey());
            }

        }
    }

    /**
     * The licensee and currency code are populated for respective dashboard call as instance leve variables because
     * we dont need to calculate these values at dashboard service level invocation
     *
     * @return String
     */
    public String getLicenseeCurrencyCode() {
        return licenseeCurrencyCode;
    }

    public Long getLicenseeId() {
        return licenseeId;
    }
}
