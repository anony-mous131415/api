package io.revx.auth.repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jeasy.random.EasyRandom;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import io.revx.auth.entity.LicenseeEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.utils.Md5Utils;
import io.revx.core.enums.RoleName;
import io.revx.core.model.Advertiser;
import io.revx.core.model.Licensee;

public class TestDataGenerator {
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

  public static UserInfoModel getUserInfoModel(String username, String password, RoleName role) {
    UserInfoModel uim = new UserInfoModel(username, password, getAuthority(role));
    Licensee li = easyRandom.nextObject(Licensee.class);
    li.setId(33l);
    li.setActive(true);
    uim.setSelectedLicensee(li);
    Set<Advertiser> advs = new HashSet<Advertiser>();
    Advertiser adv = easyRandom.nextObject(Advertiser.class);
    adv.setId(3245l);
    adv.setActive(true);
    advs.add(adv);
    uim.setAdvertisers(advs);
    return uim;
  }

  public static Set<SimpleGrantedAuthority> getAuthority(RoleName role) {
    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
    return authorities;
  }

  public static LicenseeEntity getLicenseeEntity(int id) {
    LicenseeEntity obj = easyRandom.nextObject(LicenseeEntity.class);
    obj.setId(id);
    obj.setLicenseeName("Licensee Name - " + id);
    obj.getCurrencyEntity().setId(163l);
    obj.getCurrencyEntity().setCurrencyCode("INR");
    obj.getCurrencyEntity().setCurrencyName("INdian Rupees");;
    return obj;
  }

  public static UserInfoEntity getUserEntityObject(int id, String username, boolean isActive) {
    UserInfoEntity obj = getObject(UserInfoEntity.class);
    obj.setId((long)id);
    obj.setUsername(username);
    obj.setActive(isActive);
    obj.setPassword(Md5Utils.getMd5("pass"));
    return obj;
  }

  public static UserInfoEntity getUserEntityObject(String username) {
    return getUserEntityObject(12345, username, true);
  }
}
