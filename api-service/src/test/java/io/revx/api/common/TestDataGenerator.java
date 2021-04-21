package io.revx.api.common;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchResponseSections;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.springframework.security.core.context.SecurityContextHolder;
import com.google.gson.Gson;
import io.revx.api.mysql.entity.UserInfoEntity;
import io.revx.api.pojo.ChartPerformanceDataMetrics;
import io.revx.api.pojo.DashBoardEntity;
import io.revx.api.pojo.ListPerformanceDataMetrics;
import io.revx.api.pojo.PerformanceDataMetrics;
import io.revx.core.model.BaseModel;
import io.revx.core.model.Licensee;
import io.revx.core.model.StatusBaseObject;
import io.revx.core.model.StatusTimeModel;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.requests.ChartDashboardResponse;
import io.revx.core.model.requests.DashboardRequest;
import io.revx.core.model.requests.ElasticResponse;
import io.revx.core.response.UserInfo;

public class TestDataGenerator extends MockDataGenerator {
  private static Logger logger = LogManager.getLogger(TestDataGenerator.class);


  static EasyRandom easyRandom = new EasyRandom();

  public static <T> List<T> getListOfObject(int size, Class<T> classType) {
    List<T> data = new ArrayList<T>();
    for (int i = 0; i < size; i++) {
      T obj = easyRandom.nextObject(classType);
      data.add(obj);
    }
    return data;
  }

  public static <T> T getObject(Class<T> classType) {
    T obj = easyRandom.nextObject(classType);
    return obj;
  }

  public static <T> SearchResponse getElasticSearchResponse(List<T> dataTobeReturn) {
    SearchHits hits = null;
    if (dataTobeReturn != null) {
      int documents = 10;
      SearchHit[] shits = new SearchHit[dataTobeReturn.size()];
      for (int i = 0; i < dataTobeReturn.size(); i++) {
        shits[i] = new SearchHit(i + 1, String.valueOf(i + 1), null, null);
        shits[i].sourceRef(new BytesArray(new Gson().toJson(dataTobeReturn.get(i))));
      }
      hits = new SearchHits(shits, documents, 10);
    } else {
      SearchHit[] shits = {};
      hits = new SearchHits(shits, 0, 10);
    }
    SearchResponseSections searchResponseSections =
        new SearchResponseSections(hits, null, null, false, null, null, 5);
    SearchResponse searchResponse = new SearchResponse(searchResponseSections, null, 8, 8, 0, 8,
        new ShardSearchFailure[] {}, null);
    return searchResponse;
  }


  public static UserInfoEntity getUserEntityObject(int id, String username, boolean isActive) {
    UserInfoEntity obj = getObject(UserInfoEntity.class);
    obj.setId(id);
    obj.setUsername(username);
    obj.setActive(isActive);
    return obj;
  }

  public static UserInfoEntity getUserEntityObject(String username) {
    return getUserEntityObject(12345, username, true);
  }

  public static UserInfo getUserInfo(String userName, boolean isAdvAccess,
      boolean isAllLicenseeAccess) {
    return getUserInfo(userName, "ROLE_RW", 33, isAdvAccess, isAllLicenseeAccess);
  }

  public static UserInfo getUserInfo(String userName, String role, long selectedLicenseeId,
      boolean isAdvAccess, boolean isAllLicenseeAccess) {
    Set<String> roles = new HashSet<>();
    roles.add(role);
    UserInfo ui = new UserInfo(1234l, userName, roles);
    Licensee li = getObject(Licensee.class);
    li.setId(selectedLicenseeId);
    li.setName("Licensee " + selectedLicenseeId);
    li.setCurrencyCode("INR");
    li.setActive(true);
    ui.setSelectedLicensee(li);
    if (isAdvAccess) {
      AdvertiserPojo adv = new AdvertiserPojo();
      adv.setLicenseeId(selectedLicenseeId);
      adv.setCurrencyCode("USD");
      adv.setId(1234l);
      adv.setName("Adv Test " + 1234);
      adv.setActive(true);
      ui.addAdvertiser(adv);

    } else if (!isAllLicenseeAccess) {
      for (int i = 0; i < 3; i++) {
        AdvertiserPojo adv = new AdvertiserPojo();
        adv.setLicenseeId(selectedLicenseeId);
        adv.setCurrencyCode("USD");
        adv.setId(1234l + i);
        adv.setName("Adv Test " + (1234l + i));
        adv.setActive(true);
        ui.addAdvertiser(adv);
      }
    }
    return ui;
  }

  public static <T> List<T> getListOfObject(int size, String nameLike, Class<T> classType) {
    List<T> listData = getListOfObject(size, classType);
    if (classType == StatusBaseObject.class || classType == BaseModel.class) {
      long i = 1000;
      for (T data : listData) {
        ((BaseModel) data).setName(nameLike + " - " + i);
        ((BaseModel) data).setId(i);
        i++;
      }
    }
    return listData;
  }

  public static <T> Map<Long, T> getMapOfObject(int size, String nameLike, Class<T> classType) {
    List<T> listData = getListOfObject(size, classType);
    Map<Long, T> mapData = new HashMap<Long, T>();
    if (StatusTimeModel.class.isAssignableFrom(classType)) {
      long i = 1;
      for (T data : listData) {
        ((StatusTimeModel) data).setName(nameLike + " - " + i);
        ((StatusTimeModel) data).setId(i);
        ((StatusTimeModel) data).setActive(true);
        i++;
        mapData.put(((StatusTimeModel) data).getId(), data);
      }
    } else if (BaseModel.class.isAssignableFrom(classType)) {
      long i = 1;
      for (T data : listData) {
        ((BaseModel) data).setName(nameLike + " - " + i);
        ((BaseModel) data).setId(i);
        i++;
        mapData.put(((BaseModel) data).getId(), data);
      }
    } else {
      long i = 1;
      for (T data : listData) {
        i++;
        mapData.put(i, data);
      }
    }
    return mapData;
  }


  public static <T> Map<String, T> getMapOfObjectStringKey(int size, Class<T> classType) {
    List<T> listData = getListOfObject(size, classType);
    Map<String, T> mapData = new HashMap<>();
    long i = 1;
    for (T data : listData) {
      i++;
      mapData.put(String.valueOf(i), data);
    }

    return mapData;
  }

  public static ElasticResponse getElasticResponse(List<StatusBaseObject> dataTobeReturn) {
    ElasticResponse elasticResponse = new ElasticResponse();
    elasticResponse.setData(dataTobeReturn);
    elasticResponse.setTotalNoOfRecords(dataTobeReturn != null ? dataTobeReturn.size() : 0);
    return elasticResponse;
  }

  public static List<ChartPerformanceDataMetrics> getChartRespFromDB(
      DashboardRequest dashboardRequest) {
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
    List<ChartPerformanceDataMetrics> dashboardDataList =
        new ArrayList<ChartPerformanceDataMetrics>();
    EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).objectPoolSize(100)
        .stringLengthRange(5, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(false).ignoreRandomizationErrors(true);

    EasyRandom easyRandom = new EasyRandom(parameters);
    for (int i = 1; i <= totalProduct; i++) {
      ChartPerformanceDataMetrics data = easyRandom.nextObject(ChartPerformanceDataMetrics.class);
      long startTime = startEpoc + (i * incrementBy);
      if (incrementBy == secondInHour)
        data.setHour(new BigDecimal(startTime));
      else
        data.setDay(new BigDecimal(startTime));
      overrideSome(data);
      dashboardDataList.add(data);
    }
    logger.info(" SecurityContextHolder.getContext() : "
        + SecurityContextHolder.getContext().getAuthentication());
    return dashboardDataList;
  }

  public static List<PerformanceDataMetrics> getChartWidgetRespFromDB(
      DashboardRequest dashboardRequest) {
    List<PerformanceDataMetrics> dashboardDataList = new ArrayList<PerformanceDataMetrics>();
    EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).objectPoolSize(100)
        .stringLengthRange(5, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(false).ignoreRandomizationErrors(true);

    EasyRandom easyRandom = new EasyRandom(parameters);
    PerformanceDataMetrics data = easyRandom.nextObject(PerformanceDataMetrics.class);
    overrideSome(data);
    dashboardDataList.add(data);
    logger.info(" SecurityContextHolder.getContext() : "
        + SecurityContextHolder.getContext().getAuthentication());
    return dashboardDataList;
  }

  public static List<ListPerformanceDataMetrics> getListRespFromDB(
      DashboardRequest dashboardRequest, DashBoardEntity entity, int totalProduct) {
    List<ListPerformanceDataMetrics> dashboardDataList =
        new ArrayList<ListPerformanceDataMetrics>();
    EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).objectPoolSize(100)
        .stringLengthRange(5, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(false).ignoreRandomizationErrors(true);
    EasyRandom easyRandom = new EasyRandom(parameters);
    for (int i = 1; i <= totalProduct; i++) {
      ListPerformanceDataMetrics data = easyRandom.nextObject(ListPerformanceDataMetrics.class);
      overrideSome(data, entity, i);
      dashboardDataList.add(data);
    }
    return dashboardDataList;
  }

  private static void overrideSome(ListPerformanceDataMetrics data, DashBoardEntity entity,
      int id) {
    overrideSome(data);
    switch (entity) {
      case ADVERTISER:
        data.setAdvertiserid(new BigInteger(String.valueOf(id)));
        break;
      case CAMPAIGN:
        data.setCampaignid(new BigInteger(String.valueOf(id)));
        break;
      case CREATIVE:
        data.setCreativeid(new BigInteger(String.valueOf(id)));
        break;
      case STRATEGY:
        data.setStrategyid(new BigInteger(String.valueOf(id)));
        break;
      default:
        break;
    }
  }

}
