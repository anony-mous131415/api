package io.revx.api.service;

import org.springframework.stereotype.Component;

@Component
public class FileExportImportService {/*

  private static Logger logger = LogManager.getLogger(FileExportImportService.class);

  @Autowired
  DashboardDao dashboardDao;

  @Autowired
  ValidationService validationService;

  @Autowired
  ModelConverterService modelConverterService;

  @Autowired
  LoginUserDetailsService loginUserDetailsService;


  @LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.CHART + GraphiteConstants.CSV)
  public List<ChartCSVDashboardData> getCsvDataForChart(@Valid DashboardRequest dashboardRequest)
      throws ValidationException {
    validationService.validateRequest(dashboardRequest);
    try {
      Map<FilterType, Set<FilterComponent>> tableFilters =
          validationService.getFiltersMap(dashboardRequest.getFilters());
      String advertiserCurrCode = loginUserDetailsService.getAdvertiserCurrencyId();
      String licenseeCurrCode = loginUserDetailsService.getLicenseeCurrencyId();
      List<ChartPerformanceDataMetrics> totalResult = dashboardDao
          .getDashboardChartData(dashboardRequest, tableFilters.get(FilterType.TABLE_COLUMN));
      List<DashboardData> chartData = new ArrayList<DashboardData>();
      logger.info("totalResult" + totalResult);
      modelConverterService.populateChartData(dashboardRequest, totalResult, advertiserCurrCode,
          licenseeCurrCode, chartData);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @LogMetrics(name = GraphiteConstants.SERVICE + GraphiteConstants.LIST + GraphiteConstants.CSV)
  public List<ChartCSVDashboardData> getCsvDataForList(DashBoardEntity entity,
      @Valid DashboardRequest dashboardRequest) {
    return null;
  }*/
}
