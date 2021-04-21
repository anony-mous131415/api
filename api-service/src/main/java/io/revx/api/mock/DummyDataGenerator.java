package io.revx.api.mock;

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
import com.google.gson.Gson;
import io.revx.api.mysql.entity.UserInfoEntity;
import io.revx.core.model.BaseModel;
import io.revx.core.model.Licensee;
import io.revx.core.model.StatusBaseObject;
import io.revx.core.model.StatusTimeModel;
import io.revx.core.model.advertiser.AdvertiserPojo;
import io.revx.core.model.requests.ElasticResponse;
import io.revx.core.response.UserInfo;

public class DummyDataGenerator {
  private static Logger logger = LogManager.getLogger(DummyDataGenerator.class);


  static EasyRandom easyRandom;

  static {
    EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).objectPoolSize(100)
        .stringLengthRange(5, 15).collectionSizeRange(5, 10).scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(false).ignoreRandomizationErrors(true);
    easyRandom = new EasyRandom(parameters);
  }

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
      // adv.setCurrencyCode("USD");
      adv.setId(1234l);
      adv.setName("Adv Test " + 1234);
      adv.setActive(true);
      ui.addAdvertiser(adv);

    } else if (!isAllLicenseeAccess) {
      for (int i = 0; i < 3; i++) {
        AdvertiserPojo adv = new AdvertiserPojo();
        adv.setLicenseeId(selectedLicenseeId);
        // adv.setCurrencyCode("USD");
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

}
