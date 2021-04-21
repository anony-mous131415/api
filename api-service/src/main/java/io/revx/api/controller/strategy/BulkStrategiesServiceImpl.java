package io.revx.api.controller.strategy;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.DataType;
import io.revx.api.mysql.entity.campaign.CampaignEntity;
import io.revx.api.mysql.entity.strategy.StrategyEntity;
import io.revx.api.mysql.repo.campaign.CampaignRepository;
import io.revx.api.mysql.repo.strategy.StrategyRepository;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.TablesEntity;
import io.revx.api.service.CSVReaderWriterService;
import io.revx.api.service.DashBoardService;
import io.revx.api.service.DashboardDao;
import io.revx.api.service.EntityESService;
import io.revx.api.service.LoginUserDetailsService;
import io.revx.api.service.campaign.CampaignCacheService;
import io.revx.api.service.strategy.StrategyCacheService;
import io.revx.api.service.strategy.StrategyService;
import io.revx.api.utils.ArrayOfJSON;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.Advertiser;
import io.revx.core.model.BaseModel;
import io.revx.core.model.Campaign;
import io.revx.core.model.CampaignESDTO;
import io.revx.core.model.DashboardData;
import io.revx.core.model.Licensee;
import io.revx.core.model.Strategy;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.DashboardResponse;
import io.revx.core.model.requests.Duration;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.strategy.StrategyDTO;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.objs.FilterComponent;

@Component
public class BulkStrategiesServiceImpl {


  private static Logger logger = LogManager.getLogger(BulkStrategiesServiceImpl.class);


  @Autowired
  private StrategyCacheService strategyDao;

  @Autowired
  private DashboardDao dashboardDao;
  @Autowired
  private DashBoardService dashboardService;

  @Autowired
  EntityESService elasticSearch;

  @Autowired
  private StrategyService strategyService;

  @Autowired
  StrategyCacheService strategyCacheService;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;

  @Autowired
  StrategyRepository strategyRepository;
  @Autowired
  CampaignRepository campaignRepository;
  @Autowired
  CSVReaderWriterService csvReaderWriterService;
  @Autowired
  ApplicationProperties applicationProperties;

  // @Autowired
  // CampaignCacheService campaignCacheService;


  public FileDownloadResponse getBulkStrategiesDataTSV(BulkstrategiesRequest bulkstrategiesRequest)
      throws Exception {

    FileDownloadResponse fresp = new FileDownloadResponse();
    ArrayList<Long> campaignIds = bulkstrategiesRequest.campaignIds;
    Long startTimeStamp = bulkstrategiesRequest.startTimestamp;
    Long endTimeStamp = bulkstrategiesRequest.endTimestamp;
    String filePath = bulkstrategiesRequest.filePath;

    logger.debug("its working - getBulkStrategiesDataTSV");
    String licenseeCurrId;
    Map<Long, DashboardData> strategyIdAndDataMap = new HashMap<>();

    UserInfo uInfo = loginUserDetailsService.getUserInfo();
    Long licenseeId = uInfo.getSelectedLicensee().getId();

    String csvFileName = null;

    Licensee licenseeDetails = loginUserDetailsService.getSelectedLicensee();

    List<BulkStrategiesDataDTO> bulkStrategiesDataList = new ArrayList<>();

    licenseeCurrId = licenseeDetails.getCurrencyCode();

    Gson g = new Gson();

    Map<Long, ?> pricingMap =
        elasticSearch.search(TablesEntity.CAMPAIGN, loginUserDetailsService.getElasticSearchTerm());
    for (Long campaignId : campaignIds) {
      CampaignESDTO campaignDO = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, campaignId);
      if (campaignDO == null)
        continue;
      String campaignCurrecyId = campaignDO.getCurrencyCode();
      Advertiser advertiser =
          elasticSearch.searchPojoById(TablesEntity.ADVERTISER, campaignDO.getAdvertiserId());
      DashboardRequest requestDTO = new DashboardRequest();

      Duration duration = new Duration();
      duration.startTimeStamp = startTimeStamp;
      duration.endTimeStamp = endTimeStamp;
      requestDTO.duration = duration;

      List<DashboardFilters> tableFilters = new ArrayList<>();
      DashboardFilters dashboardFilter = new DashboardFilters();
      dashboardFilter.column = Filter.CAMPAIGN_ID.getColumn();
      dashboardFilter.value = String.valueOf(campaignId);
      tableFilters.add(dashboardFilter);

      requestDTO.filters = tableFilters;
      requestDTO.groupBy = GroupBy.STRATEGY_ID.getColumn();

      try {
        // fetching dashboard data from mysql
        DashboardResponse resp = dashboardService.getDashboardDataList(1, 10000, "", requestDTO,
            DashBoardEntity.STRATEGY, true,false);
        logger.debug("perf data " + g.toJson(resp, DashboardResponse.class));

        if (resp != null && resp.getData() != null && resp.getData().size() > 0) {
          List<DashboardData> totalResult = resp.getData();
          for (DashboardData obj : totalResult) {
            // creating maps for each strategy
            strategyIdAndDataMap.put(obj.getId(), obj);
          }

          logger.debug("strategyIdAndDataMap " + g.toJson(strategyIdAndDataMap));

          List<Long> cmpgnIds = new ArrayList<Long>();
          cmpgnIds.add(campaignId);
          List<StrategyEntity> strategyDOList =
              strategyRepository.findByLicenseeIdAndCampianIdIn(licenseeId, cmpgnIds);

          // logger.debug("strategyDOList "+ g.toJson(strategyDOList));


          for (StrategyEntity strategyDO : strategyDOList) {
            if (strategyDO.getActive()) {
              BulkStrategiesDataDTO bulkStrategyDataDTO = new BulkStrategiesDataDTO();
              convertToBulkStrategyDTO(strategyDO, bulkStrategyDataDTO, strategyIdAndDataMap,
                  advertiser, campaignDO, pricingMap);
              bulkStrategiesDataList.add(bulkStrategyDataDTO);
            }
          }
        }
      } catch (Exception ex) {
        throw ex;
      }
    }
    try {
      if (bulkStrategiesDataList != null && bulkStrategiesDataList.size() > 0) {
        String filename = getCsvFileName(bulkstrategiesRequest);
        logger.debug("bulkStrategiesDataList : {} ", g.toJson(bulkStrategiesDataList));
        csvReaderWriterService.writeBulkToCSV(filename, bulkStrategiesDataList);
        fresp.setFileName(filename);
        fresp.setFileDownloadUrl(applicationProperties.getFileDownloadDomain() + "/" + filename);
        logger.debug("fresp : " + fresp);
      }
    } catch (Exception ex) {
      throw ex;
    }

    return fresp;
  }

  private String createCsvFile(String jsonArrayString, String csvFilePath) throws Exception {

    /*
     * try { List<Map<String, String>> flatJson = JSONFlattener.parseJson(jsonArrayString);
     * BulkStrategiesDataDTO dataDto = new BulkStrategiesDataDTO(); Set<String> headers =
     * dataDto.getstrategiesDataDTOSet(); CSVWriter.writeLargeFile(flatJson, "\t", csvFilePath,
     * headers); } catch (Exception e) { throw e; }
     */
    return csvFilePath;
  }

  private void convertToBulkStrategyDTO(StrategyEntity strategyDO,
      BulkStrategiesDataDTO bulkStrategyDataDTO, Map<Long, DashboardData> strategyIdAndDataMap,
      Advertiser advertiser, CampaignESDTO campaignDO, Map<Long, ?> pricingMap) {

    bulkStrategyDataDTO.setStrategyId(strategyDO.getId());
    bulkStrategyDataDTO.setAdvertiserId(strategyDO.getAdvertiserId());
    bulkStrategyDataDTO.setAdvertiserName(advertiser.getName());
    bulkStrategyDataDTO.setStrategyName(strategyDO.getName());

    bulkStrategyDataDTO.setCampaignId(strategyDO.getCampianId());
    bulkStrategyDataDTO.setCampaignName(campaignDO.getName());

    bulkStrategyDataDTO.setBidType(getPricingTypeById(strategyDO.getPricingId()));
    bulkStrategyDataDTO.setBidPrice(strategyDO.getFlowRate().setScale(9));
    bulkStrategyDataDTO.setBidPriceCurrency(campaignDO.getCurrencyCode());
    bulkStrategyDataDTO.setfCap(strategyDO.getUserFcap());

    bulkStrategyDataDTO.setBidCapMax(strategyDO.getBidCapMaxCpm());
    bulkStrategyDataDTO.setBidCapMin(strategyDO.getBidCapMinCpm());

    if (strategyIdAndDataMap.isEmpty() || strategyIdAndDataMap.get(strategyDO.getId()) == null) {
      bulkStrategyDataDTO.setImpressions(BigDecimal.ZERO);
      bulkStrategyDataDTO.setClicks(BigDecimal.ZERO);
      bulkStrategyDataDTO.setConversions(BigDecimal.ZERO);
      bulkStrategyDataDTO.setClickConversions(BigDecimal.ZERO);
      bulkStrategyDataDTO.setViewConversions(BigDecimal.ZERO);
      bulkStrategyDataDTO.setCostECPM(BigDecimal.ZERO);
      bulkStrategyDataDTO.setCostECPA(BigDecimal.ZERO);
      bulkStrategyDataDTO.setCostECPC(BigDecimal.ZERO);
      bulkStrategyDataDTO.setMediaSpend(BigDecimal.ZERO);
      bulkStrategyDataDTO.setAdSpend(BigDecimal.ZERO);
      bulkStrategyDataDTO.setMargin(BigDecimal.ZERO);
      bulkStrategyDataDTO.setInstalls(BigDecimal.ZERO);

      bulkStrategyDataDTO.setCpi(BigDecimal.ZERO);
      bulkStrategyDataDTO.setIti(BigDecimal.ZERO);
      bulkStrategyDataDTO.setRoi(BigDecimal.ZERO);

    } else {
      BigDecimal impressions =
          ((strategyIdAndDataMap.get(strategyDO.getId()).getImpressions() == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).getImpressions());
      bulkStrategyDataDTO.setImpressions(impressions);


      BigDecimal clicks =
          ((strategyIdAndDataMap.get(strategyDO.getId()).getClicks() == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).getClicks());
      bulkStrategyDataDTO.setClicks(clicks);

      BigDecimal conversions =
          ((strategyIdAndDataMap.get(strategyDO.getId()).getConversions() == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).getConversions());
      bulkStrategyDataDTO.setConversions(conversions);

      BigDecimal click_conversions =
          ((strategyIdAndDataMap.get(strategyDO.getId()).getClickConversions() == null)
              ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).getClickConversions());
      bulkStrategyDataDTO.setClickConversions(click_conversions);

      BigDecimal view_conversions =
          ((strategyIdAndDataMap.get(strategyDO.getId()).getViewConversions() == null)
              ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).getViewConversions());
      bulkStrategyDataDTO.setViewConversions(view_conversions);

      BigDecimal ecpm =
          ((strategyIdAndDataMap.get(strategyDO.getId()).getEcpm() == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).getEcpm()).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setCostECPM(ecpm);

      BigDecimal ecpa =
          ((strategyIdAndDataMap.get(strategyDO.getId()).getEcpa() == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).getEcpa()).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setCostECPA(ecpa);

      BigDecimal ecpc =
          ((strategyIdAndDataMap.get(strategyDO.getId()).ecpc == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).ecpc).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setCostECPC(ecpc);

      BigDecimal cost =
          ((strategyIdAndDataMap.get(strategyDO.getId()).cost == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).cost).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setMediaSpend(cost);

      BigDecimal revenue =
          ((strategyIdAndDataMap.get(strategyDO.getId()).revenue == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).revenue).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setAdSpend(revenue);

      BigDecimal margin =
          ((strategyIdAndDataMap.get(strategyDO.getId()).margin == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).margin).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setMargin(margin);

      BigDecimal installs =
          ((strategyIdAndDataMap.get(strategyDO.getId()).installs == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).installs).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setInstalls(installs);

      BigDecimal cpi =
          ((strategyIdAndDataMap.get(strategyDO.getId()).ecpi == null) ? BigDecimal.ZERO
              : strategyIdAndDataMap.get(strategyDO.getId()).ecpi).setScale(2,
                  BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setCpi(cpi);

      BigDecimal iti = ((strategyIdAndDataMap.get(strategyDO.getId()).iti == null) ? BigDecimal.ZERO
          : strategyIdAndDataMap.get(strategyDO.getId()).iti).setScale(0,
              BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setIti(iti);

      BigDecimal roi = ((strategyIdAndDataMap.get(strategyDO.getId()).roi == null) ? BigDecimal.ZERO
          : strategyIdAndDataMap.get(strategyDO.getId()).roi).setScale(0,
              BigDecimal.ROUND_HALF_EVEN);
      bulkStrategyDataDTO.setRoi(roi);
    }
  }

  /**
   * Table filters is the mysql filters : licensee_id filter is default adv_id filter is for
   * advertiser login
   * 
   * @param filterBy
   * @return
   */
  private List<FilterComponent> getFilterList(List<DashboardFilters> filterBy) {

    List<FilterComponent> perfFiltersList = new ArrayList<FilterComponent>();

    Long licenseeId = loginUserDetailsService.getLicenseeId();
    FilterComponent fc = new FilterComponent(Filter.LICENSEE_ID, licenseeId);
    perfFiltersList.add(fc);

    // If advertiser login : adding advertiserId
    if (!isLicenseeLogin()) {
      Long advertiserId = loginUserDetailsService.getLicenseeId();
      FilterComponent fcAdv = new FilterComponent(Filter.ADVERTISER_ID, advertiserId);
      perfFiltersList.add(fcAdv);
    }

    if (filterBy != null && !filterBy.isEmpty()) {
      for (DashboardFilters df : filterBy) {
        Filter filter = Filter.fromString(df.column);
        if (!(filter == Filter.LICENSEE_ID) && !(filter == Filter.ADVERTISER_ID)) {
          FilterComponent fcFilter = new FilterComponent(filter, df.value);
          perfFiltersList.add(fcFilter);
        }

        if (filter == Filter.ADVERTISER_ID && isLicenseeLogin()) {
          FilterComponent fcFilter = new FilterComponent(filter, df.value);
          perfFiltersList.add(fcFilter);
        }
      }
    }
    return perfFiltersList;
  }

  private boolean isLicenseeLogin() {
    return !loginUserDetailsService.isAdvertiserLogin();
  }

  /**
   * read the tsv file and create json of all the strategies iterate all strategies and validate the
   * strategies data return all strategies json with response - having error and no error
   * 
   * @throws Exception
   */

  public ApiResponseObject<List<BulkstrategiesValidationResponse>> validateTSVFile(String filePath,
      String contents) throws Exception {
    ApiResponseObject<List<BulkstrategiesValidationResponse>> apiResponse =
        new ApiResponseObject<List<BulkstrategiesValidationResponse>>();

    List<BulkstrategiesValidationResponse> validationResponseList =
        new ArrayList<BulkstrategiesValidationResponse>();

    try {
      if (contents == null)
        contents = getFileContent(filePath);

      if (contents == null) {
        logger.error("No content found in the imported file, " + filePath);
        return getErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(),
            "Validation failed. No content found.", null, null);
      }

      String jsonContent = tsvToJson(contents);
      logger.debug("jsonContent " + jsonContent);
      validationResponseList = validateFileContents(jsonContent);
      apiResponse.setRespObject(validationResponseList);

    } catch (RuntimeException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      return getErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(),
          "Validation failed. Paste valid content in the text area.", null, null);
    } catch (IOException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      return getErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(),
          "Validation failed. Paste valid content in the text area.", null, null);
    } catch (ValidationException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      return getErrorResponse(Response.Status.UNAUTHORIZED.getStatusCode(), e.getMessage(), null,
          null);
    }

    return apiResponse;
  }


  private Long getPricingTypeId(String pricingType) {
    Map<String, Long> pricingTypeMap = new HashMap<String, Long>();
    pricingTypeMap.put("CPM", 1L);
    pricingTypeMap.put("CPC", 2L);
    pricingTypeMap.put("CPA", 3L);
    pricingTypeMap.put("CPI", 6L);

    return pricingTypeMap.get(pricingType);

  }

  private String getPricingTypeById(Integer pricingId) {
    Map<Integer, String> pricingTypeMap = new HashMap<Integer, String>();
    pricingTypeMap.put(1, "CPM");
    pricingTypeMap.put(2, "CPC");
    pricingTypeMap.put(3, "CPA");
    pricingTypeMap.put(6, "CPI");

    return pricingTypeMap.get(pricingId);

  }

  private ArrayList<String> validateStrategyFields(JsonObject jsonObject, MutableBoolean isCampaignValid)
      throws Exception {
    ArrayList<String> errorMsgs = new ArrayList<String>();
    ArrayList<String> bidTypeArray = new ArrayList<String>();
    bidTypeArray.add("CPM");
    bidTypeArray.add("CPC");
    bidTypeArray.add("CPA");
    bidTypeArray.add("CPI");

    String strategyId = jsonObject.get("StrategyId").getAsString();

    logger.debug("strategy id " + strategyId + " is empty? " + strategyId.isEmpty());
    logger.debug("strategy id int " + Integer.parseInt(strategyId));

    String strategyName = jsonObject.get("StrategyName").toString();
    String bidType = jsonObject.get("BidType").getAsString();
    String bidPrice = jsonObject.get("BidPrice").getAsString();
    String fCap = jsonObject.get("FrequencyCap").getAsString();

    String maxBID = jsonObject.get("BidMax").getAsString();
    String minBID = jsonObject.get("BidMin").getAsString();

    String campaignId = jsonObject.get("CampaignId").getAsString();
    CampaignESDTO campaignESDTO = null;
    Strategy strategyFromES = null;

    if (!strategyId.isEmpty()) {
      strategyFromES =
          elasticSearch.searchPojoById(TablesEntity.STRATEGY, Long.parseLong(strategyId));
    }

    if (campaignId.isEmpty() || (strategyFromES != null
        && strategyFromES.getCampaignId() != Long.parseLong(campaignId))) {
      errorMsgs.add(BulkstrategiesConstants.CAMPAIGN_ID_NOT_VALID);
      isCampaignValid.setFalse();
    } else {
      try {
        Long campId = Long.parseLong(campaignId);
        // campaignDO = campaignRepository.getOne(campId);
        campaignESDTO = elasticSearch.searchPojoById(TablesEntity.CAMPAIGN, campId);
      } catch (Exception e) {
        errorMsgs.add(BulkstrategiesConstants.CAMPAIGN_ID_NOT_VALID);
      }
    }

    // strategy id validation
    if (strategyId.isEmpty()) {
      errorMsgs.add(BulkstrategiesConstants.STRATEGY_ID_NOT_FOUND);
    } else {
      try {
        Integer.parseInt(strategyId);
      } catch (Exception e) {
        errorMsgs.add(BulkstrategiesConstants.STRATEGY_ID_NOT_VALID);
      }
    }

    // strategy name validation
    if (strategyName.isEmpty()) {
      errorMsgs.add(BulkstrategiesConstants.STRATEGY_NAME_NOT_FOUND);
    }

    // add bidType validation msg
    if (bidType.isEmpty()) {
      errorMsgs.add(BulkstrategiesConstants.BID_TYPE_NOT_FOUND);
    } else if (!bidTypeArray.contains(bidType.toUpperCase())) {
      errorMsgs.add(BulkstrategiesConstants.BID_TYPE_NOT_VALID);
    } else if (bidType.toUpperCase().equals("CPA")) {
      if (campaignESDTO == null
          && !errorMsgs.contains(BulkstrategiesConstants.CAMPAIGN_ID_NOT_VALID)) {
        errorMsgs.add(BulkstrategiesConstants.CAMPAIGN_ID_NOT_VALID);
      } else if (campaignESDTO.getPixelId() == null) {
        errorMsgs.add(BulkstrategiesConstants.EC_CAMPAIGN_PIXEL_REQUIRED_FOR_CPA_STRATEGY);
      }

    }

    // add bidprice validation msg
    if (bidPrice.isEmpty()) {
      errorMsgs.add(BulkstrategiesConstants.BID_PRICE_NOT_FOUND);
    } else {
      try {
        BigDecimal bidPriceVal = new BigDecimal(bidPrice);
        if (bidPriceVal.compareTo(BigDecimal.ZERO) < 0) {
          errorMsgs.add(BulkstrategiesConstants.BID_PRICE_NOT_VALID);
        }
      } catch (Exception e) {
        errorMsgs.add(BulkstrategiesConstants.BID_PRICE_NOT_VALID);
      }
    }

    // add fcap validation msg
    if (fCap.isEmpty()) {
      errorMsgs.add(BulkstrategiesConstants.FCAP_NOT_FOUND);
    } else {
      try {
        Integer fCapVal = Integer.parseInt(fCap);
        if (fCapVal <= 0) {
          errorMsgs.add(BulkstrategiesConstants.FCAP_NOT_VALID);
        }
      } catch (Exception e) {
        errorMsgs.add(BulkstrategiesConstants.FCAP_NOT_VALID);
      }
    }

    // add bidprice validation msg
    if (maxBID.isEmpty()) {
      errorMsgs.add(BulkstrategiesConstants.MAX_BID_NOT_VALID);
    } else {
      try {
        BigDecimal bidPriceVal = new BigDecimal(maxBID);
        if (bidPriceVal.compareTo(BigDecimal.valueOf(-1)) < 0) {
          errorMsgs.add(BulkstrategiesConstants.MAX_BID_NOT_VALID);
        }
      } catch (Exception e) {
        errorMsgs.add(BulkstrategiesConstants.MAX_BID_NOT_VALID);
      }
    }

    // add bidprice validation msg
    if (minBID.isEmpty()) {
      errorMsgs.add(BulkstrategiesConstants.MIN_BID_NOT_VALID);
    } else {
      try {
        BigDecimal bidPriceVal = new BigDecimal(minBID);
        if (bidPriceVal.compareTo(BigDecimal.ZERO) < 0) {
          errorMsgs.add(BulkstrategiesConstants.MIN_BID_NOT_VALID);
        }
      } catch (Exception e) {
        errorMsgs.add(BulkstrategiesConstants.MIN_BID_NOT_VALID);
      }
    }

    // min bid should be less than max bid
    if (minBID != null && !minBID.isEmpty() && maxBID != null && !maxBID.isEmpty()) {
      BigDecimal bidMin = new BigDecimal(minBID);
      BigDecimal bidMax = new BigDecimal(maxBID);

      if (bidMin.compareTo(BigDecimal.ZERO) > 0 && bidMax.compareTo(BigDecimal.valueOf(-1)) > 0
          && new BigDecimal(maxBID).compareTo(new BigDecimal(minBID)) < 0) {
        errorMsgs.add(BulkstrategiesConstants.MAX_BID_SHOULD_BE_GREATER_THAN_MIN_BID);
      }

    }

    return errorMsgs;
  }

  private List<BulkstrategiesValidationResponse> validateFileContents(String JSON)
      throws ValidationException, Exception {
    JsonArray jsonArray = new Gson().fromJson(JSON, JsonArray.class);
    logger.debug("json string " + JSON);

    Gson g = new Gson();
    logger.debug("jsonArray string " + g.toJson(jsonArray));

    List<BulkstrategiesValidationResponse> validationResponseList =
        new ArrayList<BulkstrategiesValidationResponse>();

    for (int i = 0; i < jsonArray.size(); i++) {

      JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

      logger.debug("jsonObject string " + g.toJson(jsonObject));

      ArrayList<String> errorMsgs = validateStrategyFields(jsonObject, new MutableBoolean(true));

      String strategyId = jsonObject.get("StrategyId").getAsString();
      String strategyName = "";

      if (errorMsgs.indexOf(BulkstrategiesConstants.STRATEGY_ID_NOT_VALID) > -1
          || errorMsgs.indexOf(BulkstrategiesConstants.STRATEGY_ID_NOT_FOUND) > -1) {
        strategyId = BulkstrategiesConstants.INVALID;
      } else {
        try {
          Integer stId = Integer.parseInt(strategyId);
          Strategy strategy = elasticSearch.searchPojoById(TablesEntity.STRATEGY, stId);

          if (loginUserDetailsService.getAdvertisers() != null && strategy == null) {
            throw new ValidationException(
                "Unauthorized access. You can not update the content of different account.");
          }

          if (strategy == null) {
            strategyName = BulkstrategiesConstants.STRATEGY_ID_NOT_VALID;
            errorMsgs.add(BulkstrategiesConstants.STRATEGY_ID_NOT_VALID);
          } else {
            strategyName = strategy.getName();
          }
        } catch (Exception e) {
          strategyName = BulkstrategiesConstants.INVALID;
          errorMsgs.add(BulkstrategiesConstants.STRATEGY_ID_NOT_VALID);
        }
      }

      String campaignId = jsonObject.get("CampaignId").getAsString();
      try {
        Integer.parseInt(campaignId);
      } catch (Exception e) {
        errorMsgs.add(BulkstrategiesConstants.CAMPAIGN_ID_NOT_VALID);
        campaignId = BulkstrategiesConstants.INVALID;
      }

      BulkstrategiesValidationResponse validationResponse =
          new BulkstrategiesValidationResponse(strategyId, campaignId, strategyName, errorMsgs);
      validationResponseList.add(validationResponse);
    }

    return validationResponseList;
  }

  private String getFileContent(String filePath) throws IOException {

    String fileContent;

    try {
      fileContent = new String(Files.readAllBytes(Paths.get(filePath)));
    } catch (IOException e) {
      throw new IOException(
          "Getting error while reading the file content. Please check the tsv file content");
    }

    return fileContent;
  }

  private String tsvToJson(String tsv) {
    DataType delimiter = checkDataType(tsv);
    ArrayOfJSON arrayJson = new ArrayOfJSON();
    if (delimiter == DataType.TSV) {
      ArrayOfJSON array = arrayJson.buildJSONFromString(tsv, "\t");
      return array.toString();
    } else if (delimiter == DataType.CSV) {
      ArrayOfJSON array = arrayJson.buildJSONFromString(tsv, ",");
      return array.toString();
    } else {
      throw new InputMismatchException("Input String is not in valid format");
    }
  }

  private static DataType checkDataType(String input) {

    try {
      String firstLine = input.substring(0, input.indexOf("\n"));
      if (firstLine.contains(",") && !firstLine.contains("{")) {
        return DataType.CSV;
      }
      if (firstLine.contains("\t") && !firstLine.contains("{")) {
        return DataType.TSV;
      }

    } catch (IndexOutOfBoundsException e) {
      if (input.contains("{") && input.contains("}")) {
        return DataType.JSON;
      }
    }
    if (input.contains("{") && input.contains("}")) {
      return DataType.JSON;
    }
    return null;

  }

  public ApiResponseObject<BulkstrategiesUpdateResponse> updateBulkStrategies(String filePath,
      String contents) throws Exception {
    ApiResponseObject<BulkstrategiesUpdateResponse> apiResponse = new ApiResponseObject<>();
    BulkstrategiesUpdateResponse strategiesUpdatedResponseList = new BulkstrategiesUpdateResponse();

    try {
      if (contents == null)
        contents = getFileContent(filePath);
      logger.debug("Contents : " + contents);

      if (contents == null) {
        logger.error("No content found in the imported file, " + filePath);
        return getErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(),
            "No content found in the imported file, " + filePath, null, null);
      }

      String jsonContent = tsvToJson(contents);
      logger.debug("JsonContent : " + jsonContent);

      strategiesUpdatedResponseList = updateStrategiesWithFileContent(jsonContent);
      apiResponse.setRespObject(strategiesUpdatedResponseList);
    } catch (RuntimeException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
    } catch (IOException e) {
      logger.error(ExceptionUtils.getStackTrace(e));
      return getErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage(), null,
          null);
    }

    return apiResponse;
  }

  private BulkstrategiesUpdateResponse updateStrategiesWithFileContent(String JSON)
      throws Exception {

    BulkstrategiesUpdateResponse strategiesUpdatedResponse = new BulkstrategiesUpdateResponse();

    JsonArray jsonArray = new Gson().fromJson(JSON, JsonArray.class);

    ArrayList<StrategyUpdateResponse> strategiesUpdatedList =
        new ArrayList<StrategyUpdateResponse>();
    ArrayList<StrategyUpdateResponse> strategiesFailedToUpdate =
        new ArrayList<StrategyUpdateResponse>();

    Integer totalProcessedStrategies = jsonArray.size();

    for (int i = 0; i < totalProcessedStrategies; i++) {

      StrategyUpdateResponse strategyUpdateResponse = new StrategyUpdateResponse();

      JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
      String strategyId = jsonObject.get("StrategyId").getAsString();
      MutableBoolean isCampaignValid = new MutableBoolean(true);
      ArrayList<String> errorMsgs = validateStrategyFields(jsonObject, isCampaignValid);

      try {
        long stId = Long.parseLong(strategyId);
        strategyUpdateResponse.setStrategyId(strategyId);
        StrategyEntity strategy = strategyRepository.getOne(stId);
        strategyUpdateResponse.setStrategyName(strategy.getName());

      } catch (Exception e) {
        strategyUpdateResponse.setStrategyId(BulkstrategiesConstants.INVALID);
        strategyUpdateResponse.setStrategyName(BulkstrategiesConstants.INVALID);
      }

      try {

        if (errorMsgs.isEmpty()) {
          strategyUpdateResponse = updateStrategyInDB(jsonObject, strategyUpdateResponse);

          if (strategyUpdateResponse.getIsUpdated()) {
            strategiesUpdatedList.add(strategyUpdateResponse);
          }

          if (strategyUpdateResponse.getIsUpdated() != null
              && !strategyUpdateResponse.getIsUpdated()) {
            strategiesFailedToUpdate.add(strategyUpdateResponse);
          }

        } else {
          // try{
          // String campaignId = jsonObject.get("campaignId").toString();
          // Integer.parseInt(campaignId);
          // strategyUpdateResponse.setCampaignId(campaignId);
          // strategyUpdateResponse.setCampaignName(jsonObject.get("campaignName").toString());
          // }catch(Exception e){
          // errorMsgs.add(BulkstrategiesConstants.CAMPAIGN_ID_NOT_VALID);

          strategyUpdateResponse.setCampaignId(BulkstrategiesConstants.INVALID);
          strategyUpdateResponse.setCampaignName(BulkstrategiesConstants.INVALID);

          if (isCampaignValid.isTrue()) {
            if (jsonObject.has("CampaignId")) {
              String campaignId = jsonObject.get("CampaignId").getAsString();

              if (campaignId == null || campaignId.isEmpty()) {
                strategyUpdateResponse.setCampaignId(BulkstrategiesConstants.INVALID);
              } else {
                strategyUpdateResponse.setCampaignId(campaignId);
              }
            }
            if (jsonObject.has("CampaignName")) {
              String campaignName = jsonObject.get("CampaignName").getAsString();
              if (campaignName == null || campaignName.isEmpty()) {
                strategyUpdateResponse.setCampaignName(BulkstrategiesConstants.INVALID);
              } else {
                strategyUpdateResponse.setCampaignName(campaignName);
              }
            }
          }
          // }
          strategyUpdateResponse.setMessage(errorMsgs);
          strategiesFailedToUpdate.add(strategyUpdateResponse);
        }
      } catch (RuntimeException e) {
        ArrayList<String> message = new ArrayList<String>();
        message.add("Failed to update the strategy");
        strategyUpdateResponse.setMessage(message);
        strategiesFailedToUpdate.add(strategyUpdateResponse);

        logger.error(ExceptionUtils.getStackTrace(e));
      }

      strategiesUpdatedResponse.setStrategiesUpdated(strategiesUpdatedList);
      strategiesUpdatedResponse.setStrategiesFailedToUpdate(strategiesFailedToUpdate);
      strategiesUpdatedResponse.setTotalNumberOfProcessedStrategies(totalProcessedStrategies);

    }

    return strategiesUpdatedResponse;
  }

  private StrategyUpdateResponse updateStrategyInDB(JsonObject jsonObject,
      StrategyUpdateResponse strategyUpdateResponse) throws RuntimeException {

    String strategyId = jsonObject.get("StrategyId").getAsString();
    String campaignId = jsonObject.get("CampaignId").getAsString();
    String strategyName = jsonObject.get("StrategyName").getAsString();
    String campaignName = jsonObject.get("CampaignName").getAsString();
    String bidType = jsonObject.get("BidType").getAsString().toUpperCase();
    String bidPrice = jsonObject.get("BidPrice").getAsString();
    String maxBidPrice = jsonObject.get("BidMax").getAsString();
    String minBidPrice = jsonObject.get("BidMin").getAsString();
    String fCap = jsonObject.get("FrequencyCap").getAsString();

    try {
      Long stId = Long.parseLong(strategyId);
      BigDecimal bidPriceVal = new BigDecimal(bidPrice);
      BigDecimal maxBidPriceVal = new BigDecimal(maxBidPrice);
      BigDecimal minBidPriceVal = new BigDecimal(minBidPrice);
      Long fCapVal = Long.parseLong(fCap);
      ApiResponseObject<StrategyDTO> strategyDTOResp = strategyService.get(stId, false);

      StrategyDTO oldStrategy = strategyDTOResp.getRespObject();

      String savedStrategyName = oldStrategy.getName();
      Long savedfCap = oldStrategy.getFcapFrequency();
      BigDecimal savedBidPrice = oldStrategy.getPricingValue();
      String savedBidType = oldStrategy.getPricingType().getName();
      BigDecimal savedMaxBid = oldStrategy.getBidCapMax();
      BigDecimal savedMinBid = oldStrategy.getBidCapMin();

      ArrayList<String> updateMessage = new ArrayList<String>();
      Boolean fieldsChanged = false;

      StrategyDTO strategyDTO = new StrategyDTO();
      strategyDTO = oldStrategy;

      if (!savedStrategyName.equals(strategyName)) {
        fieldsChanged = true;
        updateMessage.add("Name changed from " + savedStrategyName + " to " + strategyName);
        strategyDTO.name = strategyName;
      }

      // bidType update
      if (!bidType.equals(savedBidType)) {
        fieldsChanged = true;
        updateMessage.add("Bid price type changed from " + savedBidType + " to " + bidType);
        BaseModel basemodel = new BaseModel();
        basemodel.id = getPricingTypeId(bidType);
        basemodel.name = bidType;
        strategyDTO.pricingType = basemodel;
      }

      if (bidPriceVal.compareTo(savedBidPrice.setScale(9)) != 0) {
        fieldsChanged = true;
        updateMessage.add("Bid price changed from " + savedBidPrice.setScale(9) + " to "
            + bidPriceVal.setScale(9));
        strategyDTO.pricingValue = bidPriceVal.setScale(9);
      }

      if (fCapVal != savedfCap) {
        fieldsChanged = true;
        updateMessage.add("Fcap changed from " + savedfCap + " to " + fCapVal);
        strategyDTO.fcapFrequency = fCapVal;
        // CampaignEntity campaignEntity =
        // campaignCacheService.fetchCampaign(oldStrategy.getCampianId(), false);

        // AdvertiserIO campaignDO = campaignDao.getCampaign(oldStrategy.getCampianId());
        strategyDTO.setCampaignFcap(Boolean.FALSE);

      }

      if (maxBidPriceVal.compareTo(savedMaxBid.setScale(9)) != 0) {
        fieldsChanged = true;
        updateMessage.add("Max Bid price changed from " + savedMaxBid.setScale(9) + " to "
            + maxBidPriceVal.setScale(9));
        strategyDTO.bidCapMax = maxBidPriceVal.setScale(9);
      }

      if (minBidPriceVal.compareTo(savedMinBid.setScale(9)) != 0) {
        fieldsChanged = true;
        updateMessage.add("Min Bid price changed from " + savedMinBid.setScale(9) + " to "
            + minBidPriceVal.setScale(9));
        strategyDTO.bidCapMin = minBidPriceVal.setScale(9);
      }

      if (fieldsChanged) {
        ApiResponseObject<StrategyDTO> updateStrategyApiResponse =
            new ApiResponseObject<StrategyDTO>();
        try {
          updateStrategyApiResponse = strategyService.updateStrategy(strategyDTO);
        } catch (Exception e) {
          logger.info("Bulk strategy edit failed. " + e.getMessage());
        }

        if (updateStrategyApiResponse.getRespObject() != null) {
          strategyUpdateResponse.setIsUpdated(true);
          strategyUpdateResponse.setMessage(updateMessage);
        } else {
          ArrayList<String> errorMsg = new ArrayList<String>();
          errorMsg.add(updateStrategyApiResponse.getError().getMessage());

          strategyUpdateResponse.setIsUpdated(false);
          strategyUpdateResponse.setMessage(errorMsg);
        }
      } else {
        strategyUpdateResponse.setNoChange(true);
        strategyUpdateResponse.setIsUpdated(false);
      }

      strategyUpdateResponse.setCampaignId(campaignId);
      strategyUpdateResponse.setCampaignName(campaignName);
      strategyUpdateResponse.setStrategyName(oldStrategy.getName());

    } catch (Exception e) {

      ArrayList<String> errorMsg = new ArrayList<String>();
      // errorMsg.add(e.getMessage());
      errorMsg.add("Failed to update");
      strategyUpdateResponse.setStrategyId(strategyId);
      strategyUpdateResponse.setCampaignId(campaignId);
      strategyUpdateResponse.setCampaignName(campaignName);
      strategyUpdateResponse.setStrategyName("-");
      strategyUpdateResponse.setIsUpdated(false);
      strategyUpdateResponse.setMessage(errorMsg);
    }

    return strategyUpdateResponse;
  }

  public String getCsvFileName(BulkstrategiesRequest dashboardRequest) {
    StringBuffer sb = new StringBuffer();
    sb.append(StringUtils.replace(loginUserDetailsService.getLicenseeName(), " ", "-")).append("_");

    sb.append(getDateFormat(dashboardRequest.startTimestamp)).append("_");
    sb.append(getDateFormat(dashboardRequest.endTimestamp)).append("_");
    sb.append(RandomStringUtils.randomAlphanumeric(8)).append(".csv");
    return StringUtils.lowerCase(sb.toString());
  }

  private String getDateFormat(long epoc) {
    SimpleDateFormat month_date = new SimpleDateFormat("MMM-dd", Locale.ENGLISH);
    String month_name = "";
    try {
      month_name = month_date.format(new Date(epoc * 1000));
    } catch (Exception e) {
    }
    return month_name;
  }

  protected <T> ApiResponseObject<T> getErrorResponse(Integer errorCode, String errorMsg,
      Object[] args, Class<T> clazz) {
    return getErrorResponse(Status.INTERNAL_SERVER_ERROR.getStatusCode(), errorCode, errorMsg, args,
        clazz);
  }

  protected <T> ApiResponseObject<T> getErrorResponse(Integer httpStatusCode, Integer errorCode,
      String errorMsg, Object[] args, Class<T> clazz) {
    StringBuilder message = new StringBuilder();
    if (StringUtils.isNotEmpty(errorMsg)) {
      message.append(errorMsg);
    }
    ApiResponseObject<T> response = new ApiResponseObject<T>();
    return response;
  }
}
