package io.revx.api.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.revx.api.config.ApplicationProperties;
import io.revx.api.enums.QueryType;
import io.revx.api.enums.SlicexEntity;
import io.revx.api.enums.SlicexInterval;
import io.revx.api.enums.SlicexMetricsEnum;
import io.revx.api.enums.SortOrder;
import io.revx.api.pojo.SlicexFilter;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;
import io.revx.core.model.SlicexData;
import io.revx.core.model.SlicexGridCSVData;
import io.revx.core.model.SlicexGridData;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.Duration;
import io.revx.core.model.requests.FileDownloadResponse;
import io.revx.core.model.requests.SlicexChartResponse;
import io.revx.core.model.requests.SlicexListResponse;
import io.revx.core.model.requests.SlicexRequest;

@Service("slicexServiceImpl")
public class SlicexService implements ISlicexService {

  @Autowired
  EsDataProvider eSDataProvider;

  @Autowired
  LoginUserDetailsService loginService;

  @Autowired
  CSVReaderWriterService csvReaderWriterService;

  @Autowired
  ApplicationProperties applicationProperties;

  private static Logger logger = LogManager.getLogger(SlicexService.class);
  private static String SLICEX_EXPORT_FILENAME_BASE = "SlicexGridDataExport";

  @LogMetrics(
      name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.SERVICE + GraphiteConstants.CHART)
  @SuppressWarnings("unchecked")
  @Override
  public SlicexChartResponse getSlicexChartData(SlicexRequest slicexRequest) {
    SlicexChartResponse result = new SlicexChartResponse();
    Set<SlicexFilter> filters = convertToSlicexFilters(slicexRequest.getFilters());
    SlicexInterval interval = getSlicexInterval(slicexRequest.getGroupBy());

    List<SlicexData> graphData = (List<SlicexData>) eSDataProvider
        .fetchGraphData(QueryType.slicexChart, slicexRequest.getDuration(), filters, interval);

    result.setTotalNoOfRecords(graphData != null ? graphData.size() : 0);
    result.setData(graphData);
    result.setStartTimestamp(slicexRequest.getDuration().getStartTimeStamp());
    result.setEndTimestamp(slicexRequest.getDuration().getEndTimeStamp());

    /*
     * Handled compareTo data in Graph
     */
    if (slicexRequest.getCompareToDuration() != null
        && slicexRequest.getCompareToDuration().getStartTimeStamp() != null
        && slicexRequest.getCompareToDuration().getEndTimeStamp() != null) {
      List<SlicexData> compareGraphData =
          (List<SlicexData>) eSDataProvider.fetchGraphData(QueryType.slicexChart,
              slicexRequest.getCompareToDuration(), filters, interval);
      result.setCompareData(compareGraphData);
      result.setCompareStartTimestamp(slicexRequest.getCompareToDuration().getStartTimeStamp());
      result.setCompareEndTimestamp(slicexRequest.getCompareToDuration().getEndTimeStamp());
    }
    return result;
  }

  @LogMetrics(
      name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.SERVICE + GraphiteConstants.LIST)
  @SuppressWarnings("unchecked")
  @Override
  public SlicexListResponse getSlicexGridData(SlicexRequest slicexRequest, String sort,
      SlicexEntity entity) {
    // TODO need to add validation of request somehow

    SlicexListResponse result = new SlicexListResponse();
    Set<SlicexFilter> filters = convertToSlicexFilters(slicexRequest.getFilters());
    // In grid sorting and compareto column are same.
    SlicexMetricsEnum sortOn = getSortMetric(sort);
    SortOrder sortOrder = null;
    if (sort != null && sort.substring(sort.length() - 1).equals("+")) {
      sortOrder = SortOrder.asc;
    } else {
      sortOrder = SortOrder.desc;
    }

    List<SlicexGridData> gridResult = null;

    gridResult = (List<SlicexGridData>) eSDataProvider.fetchGridData(QueryType.slicexList, entity,
        slicexRequest.getDuration(), filters, sortOn, sortOrder,
        slicexRequest.getCompareToDuration());
    result.setTotalNoOfRecords(gridResult != null ? gridResult.size() : 0);
    result.setData(gridResult);


    return result;
  }

  @LogMetrics(name = GraphiteConstants.SLICEX_PREFIX + GraphiteConstants.SERVICE
      + GraphiteConstants.LIST_EXPORT)
  @SuppressWarnings("unchecked")
  @Override
  public FileDownloadResponse getSlicexGridDataForExport(SlicexRequest slicexRequest, String sort,
      SlicexEntity entity) {
    // TODO need to add validation of request somehow

    FileDownloadResponse fresp = new FileDownloadResponse();
    try {
      Set<SlicexFilter> filters = convertToSlicexFilters(slicexRequest.getFilters());
      // In grid sorting and compareto column are same.
      SlicexMetricsEnum sortOn = getSortMetric(sort);
      SortOrder sortOrder = null;
      if (sort != null && sort.substring(sort.length() - 1).equals("+")) {
        sortOrder = SortOrder.asc;
      } else {
        sortOrder = SortOrder.desc;
      }

      List<SlicexGridCSVData> csvGridData = null;
      List<SlicexGridData> gridResult = null;

      gridResult = (List<SlicexGridData>) eSDataProvider.fetchGridData(QueryType.slicexList, entity,
          slicexRequest.getDuration(), filters, sortOn, sortOrder,
          slicexRequest.getCompareToDuration());

      csvGridData = mapGridDataToCSV(gridResult);

      // write to CSV file and return the file path of the created file.
      if (csvGridData != null && csvGridData.size() > 0) {
        String filename = getExportCSVFileName(entity, slicexRequest.getDuration());
        csvReaderWriterService.writeSlicexDataToCSV(filename, csvGridData);
        fresp.setFileName(filename);
        fresp.setFileDownloadUrl(applicationProperties.getFileDownloadDomain() + "/" + filename);
      } else {
        fresp = null;
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("Error while fetching grid data: " + e.getMessage());
    }

    return fresp;
  }

  private Set<SlicexFilter> handleAdminUserFilters(Map<String, Set<Long>> filtersMap) {
    Set<SlicexFilter> slicexFilters = new HashSet<SlicexFilter>();
    /*
     * get list of all LicenseeID and AdvertiserID that login user has access to.
     */
    Map<Long, Set<Long>> advLicenseeMap = loginService.getAllAdvLicenseeMap();
    Set<Long> accessLicenseeList = new HashSet<Long>();
    Set<Long> accessAdvertiserList = new HashSet<Long>();
    for (Map.Entry<Long, Set<Long>> entry : advLicenseeMap.entrySet()) {
      accessLicenseeList.add(entry.getKey());
      accessAdvertiserList.addAll(entry.getValue());
    }

    /*
     * to check if the passed LicenseeIDs and AdvertiserIDs can be accessed by the logged in user
     */
    Set<Long> filterLicenseeList = new HashSet<Long>();
    Set<Long> filterAdvertiserList = new HashSet<Long>();

    for (Map.Entry<String, Set<Long>> entry : filtersMap.entrySet()) {
      if (SlicexEntity.valueOf(entry.getKey()) == SlicexEntity.licensee) {
        filterLicenseeList.addAll(entry.getValue());
      } else if (SlicexEntity.valueOf(entry.getKey()) == SlicexEntity.advertiser) {
        filterAdvertiserList.addAll(entry.getValue());
      } else {
        SlicexFilter singleEntityFilter = new SlicexFilter();
        singleEntityFilter.setEntity(SlicexEntity.valueOf(entry.getKey()));
        singleEntityFilter.setIds(entry.getValue());
        slicexFilters.add(singleEntityFilter);
      }
    }

    if (filterLicenseeList.size() == 0 && filterAdvertiserList.size() == 0) {
      /*
       * if no licensee and advertiser are passed as filters - consider all that you have access to.
       */
      SlicexFilter licenseeFilter = new SlicexFilter();
      licenseeFilter.setEntity(SlicexEntity.licensee);
      licenseeFilter.setIds(accessLicenseeList);
      slicexFilters.add(licenseeFilter);

      SlicexFilter advertiserFilter = new SlicexFilter();
      advertiserFilter.setEntity(SlicexEntity.advertiser);
      advertiserFilter.setIds(accessAdvertiserList);
      slicexFilters.add(advertiserFilter);
    } else {
      /*
       * consider only those values for which you have access
       */
      if (filterLicenseeList.size() != 0) {
        Set<Long> validLicensees = new HashSet<Long>();
        for (long licenseeID : filterLicenseeList) {
          if (accessLicenseeList.contains(licenseeID)) {
            validLicensees.add(licenseeID);
          }
        }
        SlicexFilter licenseeFilter = new SlicexFilter();
        licenseeFilter.setEntity(SlicexEntity.licensee);
        licenseeFilter.setIds(validLicensees);
        slicexFilters.add(licenseeFilter);
      }

      if (filterAdvertiserList.size() != 0) {
        Set<Long> validAdvertisers = new HashSet<Long>();
        for (long advID : filterAdvertiserList) {
          if (accessAdvertiserList.contains(advID)) {
            validAdvertisers.add(advID);
          }
        }
        SlicexFilter advertiserFilter = new SlicexFilter();
        advertiserFilter.setEntity(SlicexEntity.advertiser);
        advertiserFilter.setIds(validAdvertisers);
        slicexFilters.add(advertiserFilter);
      }
    }

    return slicexFilters;
  }

  private Set<SlicexFilter> convertToSlicexFilters(List<DashboardFilters> filters) {
    // TODO Need to revisit this code
    if (null == filters) {
      filters = new ArrayList<DashboardFilters>();
    }

    Map<String, Set<Long>> filtersMap = new HashMap<String, Set<Long>>();
    Set<SlicexFilter> slicexFilters = new HashSet<SlicexFilter>();

    for (DashboardFilters dashboardFilter : filters) {
      if (!filtersMap.containsKey(dashboardFilter.getColumn())) {
        filtersMap.put(dashboardFilter.getColumn(), new HashSet<Long>());
      }
      filtersMap.get(dashboardFilter.getColumn()).add(Long.parseLong(dashboardFilter.getValue()));
    }

    for (Map.Entry<String, Set<Long>> entry : filtersMap.entrySet()) {
      SlicexFilter singleEntityFilter = new SlicexFilter();
      singleEntityFilter.setEntity(SlicexEntity.valueOf(entry.getKey()));
      singleEntityFilter.setIds(entry.getValue());
      slicexFilters.add(singleEntityFilter);
    }

    /*
     * Adding login based filters of licensee/advertiser
     */
    if (loginService.getSelectedLicensee() != null
        && (loginService.isAdminUser() && loginService.getAllLicenseeCount() == 0)) {
      // do nothing - Superadmin or Admin with all licensee access.
    } else if (loginService.getAllLicenseeCount() > 1) {
      slicexFilters = handleAdminUserFilters(filtersMap);
    } else {
      Long licensee = loginService.getLicenseeId();
      Set<Long> licenseeAllowed = new HashSet<Long>();
      licenseeAllowed.add(licensee);

      Set<Long> advertisersAllowed = null;
      if (loginService.getUserInfo().getAdvertisers() != null
          && loginService.getUserInfo().getAdvertisers().size() > 0) {
        List<Long> advertisers =
            loginService.getAdvertisers(loginService.getUserInfo().getAdvertisers());
        advertisersAllowed = new HashSet<Long>(advertisers);
      }

      SlicexFilter oldAdvertiserFilter = null;
      SlicexFilter newLicenseeFilter = new SlicexFilter();
      SlicexFilter newAdvertiserFilter = new SlicexFilter();

      for (SlicexFilter filter : slicexFilters) {
        if (filter.getEntity() == SlicexEntity.advertiser) {
          oldAdvertiserFilter = filter;
        }
      }

      newLicenseeFilter.setEntity(SlicexEntity.licensee);
      newLicenseeFilter.setIds(licenseeAllowed);

      if (advertisersAllowed != null) {
        if (oldAdvertiserFilter != null && oldAdvertiserFilter.getIds().size() > 0) {
          advertisersAllowed.retainAll(oldAdvertiserFilter.getIds());
        }
        newAdvertiserFilter.setEntity(SlicexEntity.advertiser);
        newAdvertiserFilter.setIds(advertisersAllowed);
      } else {
        // Use Advertiser filter only from user, All advertisers are allowed
        if (oldAdvertiserFilter != null && oldAdvertiserFilter.getIds().size() > 0) {
          newAdvertiserFilter.setEntity(SlicexEntity.advertiser);
          newAdvertiserFilter.setIds(oldAdvertiserFilter.getIds());
        }
      }

      /*
       * for (SlicexFilter filter : slicexFilters) { if (filter.getEntity() == SlicexEntity.licensee
       * || filter.getEntity() == SlicexEntity.advertiser) { slicexFilters.remove(filter); } }
       */


      for (Iterator<SlicexFilter> it = slicexFilters.iterator(); it.hasNext();) {
        SlicexFilter filter = it.next();
        if (filter.getEntity() == SlicexEntity.licensee) {
          it.remove();
        }
        if (filter.getEntity() == SlicexEntity.advertiser) {
          it.remove();
        }
      }


      slicexFilters.add(newLicenseeFilter);
      if (newAdvertiserFilter != null && newAdvertiserFilter.getIds() != null
          && newAdvertiserFilter.getIds().size() > 0) {
        slicexFilters.add(newAdvertiserFilter);
      }
    }
    return slicexFilters;
  }

  private SlicexInterval getSlicexInterval(String groupBy) {
    SlicexInterval interval = null;
    if (groupBy != null && groupBy.length() != 0 && (groupBy.equalsIgnoreCase("hourly")
        || groupBy.equalsIgnoreCase("daily") || groupBy.equalsIgnoreCase("monthly"))) {
      interval = SlicexInterval.valueOf(groupBy.trim().toUpperCase());
    } else {
      // TODO need to throw error
      // Have to set error object
    }
    return interval;
  }

  private SlicexMetricsEnum getSortMetric(String sort) {
    SlicexMetricsEnum sortOn = null;
    if (sort != null && sort.length() > 0) {
      if (sort.equalsIgnoreCase("revenue")) {
        if (loginService.isSuperAdminUser()) {
          sortOn = SlicexMetricsEnum.revenue;
        } else if (loginService.isAdminUser() && (loginService.getAllLicenseeCount() == 0
            || loginService.getAllLicenseeCount() > 1)) {
          sortOn = SlicexMetricsEnum.revenue_in_pc;
        } else if (loginService.isAdvertiserLogin()) {
          sortOn = SlicexMetricsEnum.revenue_in_ac;
        } else {
          sortOn = SlicexMetricsEnum.revenue_in_lc;
        }
      } else if (sort.equalsIgnoreCase("cost")) {
        if (loginService.isSuperAdminUser()) {
          sortOn = SlicexMetricsEnum.cost;
        } else if (loginService.isAdminUser() && (loginService.getAllLicenseeCount() == 0
            || loginService.getAllLicenseeCount() > 1)) {
          sortOn = SlicexMetricsEnum.cost_in_pc;
        } else if (loginService.isAdvertiserLogin()) {
          sortOn = SlicexMetricsEnum.cost_in_ac;
        } else {
          sortOn = SlicexMetricsEnum.cost_in_lc;
        }
      } else {
        sortOn = SlicexMetricsEnum.valueOf(sort.substring(0, sort.length() - 1));
      }
    } else {
      // Default sorting
      sortOn = SlicexMetricsEnum.impressions;
    }
    return sortOn;
  }

  public String getExportCSVFileName(SlicexEntity entity, Duration duration) {
    StringBuffer sb = new StringBuffer();
    sb.append(SLICEX_EXPORT_FILENAME_BASE).append("_");
    sb.append(entity.getUiDisplayName()).append("_");
    sb.append(getDateFormat(duration.getStartTimeStamp())).append("_");
    sb.append(getDateFormat(duration.getEndTimeStamp())).append("_");
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

  private List<SlicexGridCSVData> mapGridDataToCSV(List<SlicexGridData> gridData) {


    if (gridData == null || gridData.size() < 0) {
      return null;
    }

    List<SlicexGridCSVData> csvData = new ArrayList<SlicexGridCSVData>();
    for (SlicexGridData row : gridData) {
      csvData.add(new SlicexGridCSVData(row.getName(), row.getRevenue(), row.getCtr(), row.getCtc(),
          row.getImpressions(), row.getClicks(), row.getInstalls(), row.getImpInstalls(),
          row.getClickInstalls(), row.getConversions(), row.getViewConversions(),
          row.getClickConversions(), row.getErpm(), row.getErpc(), row.getErpi(), row.getErpa(),
          row.getIti(), row.getCurrencyId(), row.getCost(), row.getMargin(), row.getEcpm(),
          row.getEcpc(), row.getEcpi(), row.getEcpa()));
    }

    return csvData;
  }

}


