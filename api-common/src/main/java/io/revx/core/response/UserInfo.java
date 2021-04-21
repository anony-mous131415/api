package io.revx.core.response;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import io.revx.core.enums.RoleName;
import io.revx.core.model.Advertiser;
import io.revx.core.model.Licensee;

public class UserInfo {

  private static Logger logger = LogManager.getLogger(UserInfo.class);

  private Long userId;
  private Set<String> authorities;
  private String username;
  private Licensee selectedLicensee;
  private Set<Advertiser> advertisers;
  private Map<Long, Set<Long>> advLicenseeMap;

  public UserInfo() {}

  public UserInfo(String username, List<String> authoritiesOfUser) {
    this.username = username;
    this.authorities = makeUserInfoAuthority(authoritiesOfUser);
    advLicenseeMap = new HashMap<>();
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  private Set<String> makeUserInfoAuthority(Collection<String> authoritiesOfUser) {
    Set<String> roles = new HashSet<>();
    RoleName role = highestRoleOfLoginUser(authoritiesOfUser);
    if (role != null)
      roles.add(role.getRoleNameForSpringSecurity());
    return roles.size() > 0 ? roles : null;
  }


  public UserInfo(Long userId, String username, Set<String> authoritiesOfUser) {
    this.userId = userId;
    this.username = username;
    this.authorities = makeUserInfoAuthority(authoritiesOfUser);
  }



  /**
   * @return the authorities
   */
  public Set<String> getAuthorities() {
    return authorities;
  }

  /**
   * @param authorities the authorities to set
   */
  public void setAuthorities(Set<String> authorities) {
    this.authorities = authorities;
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the selectedLicensee
   */
  public Licensee getSelectedLicensee() {
    return selectedLicensee;
  }

  /**
   * @param selectedLicensee the selectedLicensee to set
   */
  public void setSelectedLicensee(Licensee selectedLicensee) {
    this.selectedLicensee = selectedLicensee;
  }

  public Set<Advertiser> getAdvertisers() {
    return advertisers;
  }

  public void setAdvertisers(Set<Advertiser> advertisers) {
    this.advertisers = advertisers;
  }

  public void addAdvertiser(Advertiser advertiser) {
    if (this.advertisers == null)
      this.advertisers = new HashSet<Advertiser>();
    this.advertisers.add(advertiser);
  }

  public Map<Long, Set<Long>> getAdvLicenseeMap() {
    return advLicenseeMap;
  }

  public void addAdvertiserAndLicensee(long licenseeId, long advertiserId) {
    if (this.advLicenseeMap == null)
      this.advLicenseeMap = new HashMap<>();
    if (!advLicenseeMap.containsKey(licenseeId)) {
      advLicenseeMap.put(licenseeId, new HashSet<>());
    }
    if (advertiserId > 0)
      advLicenseeMap.get(licenseeId).add(advertiserId);
  }


  public Long getUserId() {
    return userId;
  }

  public RoleName highestRoleOfLoginUser(Collection<String> authorities) {
    RoleName role = null;
    if (authorities != null) {
      for (String roleStr : authorities) {
        RoleName tmpRole = RoleName.getRoleByName(roleStr);
        // SuperAdmin Will have 0 ordinal lowest means Highest Role
        if (tmpRole != null && (role == null || tmpRole.ordinal() < role.ordinal())) {
          role = tmpRole;
        }
      }
    }
    return role;
  }

  public String serialize() {
    logger.debug(" serialize :" + this);
    String serStr = new Gson().toJson(this);

    logger.debug(" serialized serStr :: {} ", serStr);
    return serStr;

  }

  public static UserInfo deSerializeUser(String userInfoJson) {
    logger.debug(" deSerializeUser :" + userInfoJson);
    if (StringUtils.isNoneBlank(userInfoJson)) {
      try {
        return new Gson().fromJson(userInfoJson, UserInfo.class);
      } catch (Exception e) {
        logger.error(ExceptionUtils.getStackTrace(e));
      }
    }
    return null;

  }

  @Override
  public String toString() {
    return "UserInfo [userId=" + userId + ", authorities=" + authorities + ", username=" + username
        + ", selectedLicensee=" + selectedLicensee + ", advertisers=" + advertisers + "]";
  }


}
