package io.revx.api.utility;

import io.revx.api.audience.pojo.RuleFilterDto;
import io.revx.api.audience.pojo.RuleOperatorDto;
import io.revx.api.audience.pojo.RuleValueDto;
import io.revx.api.mysql.amtdb.entity.RuleComponent;
import io.revx.api.mysql.amtdb.entity.RuleFilter;
import io.revx.api.mysql.amtdb.entity.RuleOperator;
import io.revx.api.mysql.amtdb.entity.RuleValue;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.service.ValidationService;
import io.revx.core.model.audience.RuleComponentDTO;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.Duration;
import io.revx.core.service.CacheService;
import io.revx.querybuilder.enums.FilterType;
import io.revx.querybuilder.objs.FilterComponent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class Util {

    private static final Logger logger = LogManager.getLogger(Util.class);

  private Util() {}

  /*
   * Below methods are for encoding of creative size into single number and decoding again.
   * 
   * Cantor Pairing function https://en.wikipedia.org/wiki/Pairing_function#Cantor_pairing_function
   */

  public static Long encode(Long x, Long y) {
    Long z;
    Long w = x + y;
    z = (((w * (w + 1)) / 2) + y);
    return z;
  }

  public static Long[] decode(Long z) {
    Long[] decodedNumbers = new Long[2];
    Long w, t;

    w = Math.floorDiv((long) (Math.sqrt((8 * z) + 1) - 1.0), 2);
    t = ((w * w) + w) / 2;

    decodedNumbers[1] = z - t;
    decodedNumbers[0] = w - decodedNumbers[1];
    return decodedNumbers;
  }

  public static Boolean isMonthlyDataRequired(Duration duration) {
    Boolean isMonthlyRequired = false;

    DateTime startDate =
        new DateTime(duration.getStartTimeStamp() * 1000, DateTimeZone.UTC).withTime(0, 0, 0, 0);
    DateTime endDate =
        new DateTime(duration.getEndTimeStamp() * 1000, DateTimeZone.UTC).withTime(0, 0, 0, 0);
    DateTime tomorrowDate =
        new DateTime((((System.currentTimeMillis() / 1000) / 86400) * 86400 + 86400) * 1000,
            DateTimeZone.UTC).withTime(0, 0, 0, 0);

    int startDateDay = startDate.getDayOfMonth();
    int endDateDay = endDate.getDayOfMonth();
    int startDateMonth = startDate.getMonthOfYear();
    int endDateMonth = endDate.getMonthOfYear();
    int tomorrowDateMonth = tomorrowDate.getMonthOfYear();
    int tomorrowDateDay = tomorrowDate.getDayOfMonth();
    if (startDateDay == 1 && endDateDay == 1 && startDateMonth != endDateMonth) {
      if (tomorrowDateDay == endDateDay && tomorrowDateMonth == endDateMonth) {
        // today is last day of the month. This is case of (this month data) from UI.
        isMonthlyRequired = false;
      } else {
        isMonthlyRequired = true;
      }
    }
    return isMonthlyRequired;
  }

  public static RuleValueDto getVoFromTuple(RuleValue ruleValue) {
    RuleValueDto ruleValueDto = new RuleValueDto();
    ruleValueDto.setValue(ruleValue.getValue());
    ruleValueDto.setDisplayValue(ruleValue.getDisplayValue());
    return ruleValueDto;
  }
  
  public static RuleFilterDto getVoFromTuple(RuleFilter ruleFilter) {
    RuleFilterDto ruleFilterDto = new RuleFilterDto();
    ruleFilterDto.setId(ruleFilter.getId());
    ruleFilterDto.setName(ruleFilter.getFilterName());
    ruleFilterDto.setDisplayName(ruleFilter.getFilterDisplayName());
    ruleFilterDto.setFbxName(ruleFilter.getFbxFilterName());
    ruleFilterDto.setFilterType(ruleFilter.getRuleFilterType());
    ruleFilterDto.setValueType(ruleFilter.getRuleValueType());
    return ruleFilterDto;
  }


  public static RuleComponentDTO getVoFromTuple(RuleComponent ruleComponent) {
      RuleComponentDTO ruleComponentDto = new RuleComponentDTO();
      ruleComponentDto.setId(ruleComponent.getId());
      ruleComponentDto.setFilterId(ruleComponent.getRuleFilterId());
      ruleComponentDto.setOperatorId(ruleComponent.getRuleOperatorId());
      ruleComponentDto.setValue(ruleComponent.getRuleValue());
      return ruleComponentDto;
  }

  public static RuleOperatorDto getVoFromTuple(RuleOperator ruleOperator) {
      RuleOperatorDto ruleOperatorDto = new RuleOperatorDto();
      ruleOperatorDto.setId(ruleOperator.getId());
      ruleOperatorDto.setName(ruleOperator.getOperatorName());
      ruleOperatorDto.setDisplayName(ruleOperator.getOperatorDisplayName());
      ruleOperatorDto.setFbxName(ruleOperator.getFbxOperatorName());
      return ruleOperatorDto;
  }
  
  public static Boolean isNumeric(String text) {
    if(StringUtils.isNotBlank(text) && text.matches("[0-9]+"))
      return true;
    else
      return false;
  }
  

  /**
   * Readable file size.
   *
   * @param size the size
   * @return the string
   */
  public static String readableFileSize(long size) {
    if (size <= 0)
      return "0";
    final String[] units = new String[] {"B", "kB", "MB", "GB", "TB"};
    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
        + units[digitGroups];
  }


  public static String getmd5Hex(String file){
    String checksum = null;
    try {  
        checksum = DigestUtils.md5Hex(new FileInputStream(file));
    } catch (IOException ex) {

    }
    return checksum;
  }
  
  public static int getChildRecordsOfZipFile(String zipFilePath, String destDir) {
    Util.createDirectory(destDir);
    FileInputStream fis;
    //buffer for read and write data to file
    byte[] buffer = new byte[102400];
    int childRecords = 0;
    try {
        fis = new FileInputStream(zipFilePath);
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry ze = zis.getNextEntry();
        while(ze != null){
            childRecords++;
            File newFile = new File(destDir + File.separator + ze.getName());
            String fileName = validateFilenameInDir(newFile,destDir);
            //create directories for sub directories in zip
            new File(new File(fileName).getParent()).mkdirs();
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
            }
            fos.close();
            //close this ZipEntry
            zis.closeEntry();
            ze = zis.getNextEntry();
        }
        //close last ZipEntry
        zis.closeEntry();
        zis.close();
        fis.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return childRecords;
}
  
  public static void createDirectory(String directoryName) {
    if (StringUtils.isNotBlank(directoryName)) {
      File directory = new File(directoryName);
      if (!directory.exists()) {
        directory.mkdirs();
      }
    }else{
      System.out.println("Path is null/blank");
    }
  }
  
  public static void createFile(String filePath) {
    File f = new File(filePath);
    try {
      if (!f.exists()) {
        f.createNewFile();
      } else {
        System.out.println("File already exists");
      }
    } catch (Exception e) {
      System.out.println("Error while creating file." + e.getMessage());
    }
  }
  
  public static void createFileDirectory(String directoryName, String fileName) {
    createDirectory(directoryName);
    createFile(directoryName+File.separator+fileName);
  }
  
  public static void deleteFile(String filePath) {
    File f = new File(filePath);
    try {
      if (f.exists()) {
        f.delete();
      } else {
        System.out.println("File not exist");
      }
    } catch (Exception e) {
      System.out.println("Error while deliting file." + e.getMessage());
    }
  }

    public static String replaceSpecialCharactersWithSpace(String searchText) {
        if (searchText != null && !searchText.isEmpty()) {
            return searchText.trim().replaceAll("[^a-zA-Z0-9_]+", " ");
        }

        return searchText;
    }

    /**
     * Dashboard request requires duration object for formulating the cache key for last day
     * @return Duration : containing start time and end time yesterday duration
     */
    public static Duration getYesterdayDuration() {
      Duration duration = new Duration();
      LocalDateTime now = LocalDateTime.now();
      now = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
      long todayEpoch = now.toEpochSecond(ZoneOffset.UTC);
      long yesterdaysEpoch = now.minus(1, ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
      duration.setStartTimeStamp(yesterdaysEpoch);
      duration.setEndTimeStamp(todayEpoch);
      return duration;
    }

    /**
     * Dashboard request requires duration object for formulating the cache key for today
     * @return Duration : containing start time and end time yesterday duration
     */
    private static Duration getTodayDuration() {
        Duration duration = new Duration();
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
        long todayEpoch = now.toEpochSecond(ZoneOffset.UTC);
        long tomorrowsEpoch = now.plus(1, ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
        duration.setStartTimeStamp(todayEpoch);
        duration.setEndTimeStamp(tomorrowsEpoch);
        return duration;
    }

    /**
     * Dashboard request requires duration object for formulating the cache key for last 7 days
     * @return Duration : containing start time and end time last 7 days duration
     */
    public static Duration getLastWeekDuration() {
        Duration duration = new Duration();
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
        long todayEpoch = now.toEpochSecond(ZoneOffset.UTC);
        long weekAgoEpoch = now.minus(7,ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
        duration.setStartTimeStamp(weekAgoEpoch);
        duration.setEndTimeStamp(todayEpoch);
        return duration;
    }

    /**
     * Dashboard request requires duration object for formulating the cache key for last 14 days
     * @return Duration : containing start time and end time last 14 days duration
     */
    public static Duration getLastTwoWeekDuration() {
        Duration duration = new Duration();
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
        long todayEpoch = now.toEpochSecond(ZoneOffset.UTC);
        long twoWeekAgoEpoch = now.minus(14,ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
        duration.setStartTimeStamp(twoWeekAgoEpoch);
        duration.setEndTimeStamp(todayEpoch);
        return duration;
    }

    /**
     * Dashboard request requires duration object for formulating the cache key for last 30 days
     * @return Duration : containing start time and end time last 30 days duration
     */
    public static Duration getLastThirtyDaysDuration() {
        Duration duration = new Duration();
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
        long todayEpoch = now.toEpochSecond(ZoneOffset.UTC);
        long thirtyDaysAgoEpoch = now.minus(30,ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
        duration.setStartTimeStamp(thirtyDaysAgoEpoch);
        duration.setEndTimeStamp(todayEpoch);
        return duration;
    }

    /**
     * Dashboard request requires duration object for formulating the cache key for this month
     * @return Duration : containing start time and end time this month duration
     */
    public static Duration getThisMonthDuration() {
        Duration duration = new Duration();
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(),now.getMonth(),now.getDayOfMonth(),0,0);
        LocalDateTime firstDay = LocalDateTime.of(now.getYear(),now.getMonth(),1,0,0);
        long tomorrowEpoch = now.plus(1, ChronoUnit.DAYS).toEpochSecond(ZoneOffset.UTC);
        long monthFirstDayEpoch = firstDay.toEpochSecond(ZoneOffset.UTC);
        duration.setStartTimeStamp(monthFirstDayEpoch);
        duration.setEndTimeStamp(tomorrowEpoch);
        return duration;
    }

    /**
     * Populating the dashboard request for obtaining the cache-key of a list dashboard data
     *
     * @param duration - start and end time stamps
     * @param entityKey - like campaign, advertiser , strategy
     * @param entityValue - ids of the respective entity
     * @return - Dashboard request
     */
    public static DashboardRequest getDashBoardRequest(Duration duration,String entityKey, String entityValue) {
        DashboardRequest request = new DashboardRequest();
        DashboardFilters filters = new DashboardFilters();
        filters.setColumn(entityKey);
        filters.setValue(entityValue);
        List<DashboardFilters> filtersList = new ArrayList<>();
        filtersList.add(filters);
        request.setFilters(filtersList);
        request.setDuration(duration);
        return request;
    }

    /**
     * The cache key is removed when the respective entity is updated so the list under
     * respective parent entity will be correctly populated without refreshing
     */
    public static void deleteDashboardCache(String dashboardEntity, DashBoardEntity entityName,String parentKey
            , long parentValue, ValidationService validationService, CacheService cacheService) {
        List<Duration> dashboardRequestDurations = new ArrayList<>();
        dashboardRequestDurations.add(getTodayDuration());
        dashboardRequestDurations.add(getYesterdayDuration());
        dashboardRequestDurations.add(getLastWeekDuration());
        dashboardRequestDurations.add(getLastTwoWeekDuration());
        dashboardRequestDurations.add(getLastThirtyDaysDuration());
        dashboardRequestDurations.add(getThisMonthDuration());
        for (Duration duration : dashboardRequestDurations) {
            DashboardRequest request =
                    getDashBoardRequest(duration, parentKey,String.valueOf(parentValue));
            Map<FilterType, Set<FilterComponent>> tableFilters = validationService
                    .getFiltersMap(request.getFilters());
            String cacheKey = cacheService.getCacheKey(dashboardEntity,entityName.getName(),
                    request,tableFilters.get(FilterType.TABLE_COLUMN));
            cacheService.removeBaseModelCache(cacheKey);
        }
    }

    public static void deleteCreativeList(String parentKey, long parentValue, long licenseeId,
            ValidationService validationService, CacheService cacheService){
        List<Duration> dashboardRequestDurations = getDurationsForCreativeCache();
        for (Duration duration : dashboardRequestDurations) {
            DashboardRequest request =
                    getDashBoardRequest(duration, parentKey,String.valueOf(parentValue));
            request.setGroupBy("");
            Map<FilterType, Set<FilterComponent>> tableFilters = validationService
                    .getFiltersMap(request.getFilters());
            String cacheKey = cacheService.getCreativeCacheKey(request,tableFilters.get(FilterType.TABLE_COLUMN),
                    licenseeId);
            cacheService.removeCreativeCache(cacheKey);
        }
    }

    public static void deleteCreativeListOfLicensee(long licenseeId, ValidationService validationService,
            CacheService cacheService){
        List<Duration> dashboardRequestDurations = getDurationsForCreativeCache();
        for (Duration duration : dashboardRequestDurations) {
            DashboardRequest request = new DashboardRequest();
            request.setDuration(duration);
            request.setGroupBy("");
            Map<FilterType, Set<FilterComponent>> tableFilters = validationService
                    .getFiltersMap(request.getFilters());
            String cacheKey = cacheService.getCreativeCacheKey(request,tableFilters.get(FilterType.TABLE_COLUMN),
                    licenseeId);
            cacheService.removeCreativeCache(cacheKey);
        }
    }

    private static List<Duration> getDurationsForCreativeCache() {
        List<Duration> dashboardRequestDurations = new ArrayList<>();
        dashboardRequestDurations.add(getTodayDuration());
        dashboardRequestDurations.add(getYesterdayDuration());
        dashboardRequestDurations.add(getLastWeekDuration());
        dashboardRequestDurations.add(getLastTwoWeekDuration());
        return dashboardRequestDurations;
    }

    public static String validateFilenameInDir(File checkFile, String
            intendedDirectory) throws IOException{
        String canonicalPathToCheck = checkFile.getCanonicalPath();
        logger.debug("File canonical path : {} ", canonicalPathToCheck);
        File intendedDir = new File(intendedDirectory);
        String canonicalPathToVerify = intendedDir.getCanonicalPath();
        logger.debug("Directory canonical path : {} ", canonicalPathToVerify);
        if(canonicalPathToCheck.startsWith(canonicalPathToVerify)) {
            return canonicalPathToCheck;
        } else {
            throw new IllegalStateException("This file is outside the intended extraction directory.");
        }
    }

}
