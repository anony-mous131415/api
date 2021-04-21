package io.revx.auth.pojo;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import com.google.gson.Gson;
import io.revx.core.model.Advertiser;
import io.revx.core.model.Licensee;

public class UserInfoMasterPojo {
  private static Logger logger = LogManager.getLogger(UserInfoMasterPojo.class);
  private Long userId;
  private Licensee selectedLicensee;
  private Advertiser selectedAdvertiser;
  private Set<String> authorities;
  private String username;

  public UserInfoMasterPojo() {}

  public UserInfoMasterPojo(String username, Set<String> authorities) {
    this.username = username;
    this.authorities = authorities;
  }

  
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Licensee getSelectedLicensee() {
    return selectedLicensee;
  }

  public void setSelectedLicensee(Licensee selectedLicensee) {
    this.selectedLicensee = selectedLicensee;
  }

  public Advertiser getSelectedAdvertiser() {
    return selectedAdvertiser;
  }

  public void setSelectedAdvertiser(Advertiser selectedAdvertiser) {
    this.selectedAdvertiser = selectedAdvertiser;
  }


  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserInfoMasterPojo [userId=").append(userId).append(", selectedLicensee=")
        .append(selectedLicensee).append(", selectedAdvertiser=").append(selectedAdvertiser)
        .append(", authorities=").append(authorities).append(", username=").append(username)
        .append("]");
    return builder.toString();
  }

  public Set<String> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(Set<String> authorities) {
    this.authorities = authorities;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public static UserInfoMasterPojo getFromAuth(Authentication auth) {
    if (auth != null) {
      if (auth.getPrincipal() instanceof UserInfoMasterPojo) {
        return (UserInfoMasterPojo) auth.getPrincipal();
      } else if (auth.getPrincipal() instanceof LdapUserDetailsImpl) {
        LdapUserDetailsImpl ldapUser = (LdapUserDetailsImpl) auth.getPrincipal();
        UserInfoMasterPojo ui =
            new UserInfoMasterPojo(ldapUser.getUsername(), new HashSet<String>());
        return ui;
      }
    }
    return null;

  }

  public String serialize() {
    String serJson=new Gson().toJson(this);
    logger.debug("SerJson {}", serJson);
    return serJson;
  }

  public static String serializeUserInfoModel(Authentication auth) {
    UserInfoMasterPojo ui = getFromAuth(auth);
    logger.debug(" serializeUserInfoModel :" + ui);
    if (ui != null) {
      String json = ui.serialize();
      logger.debug(" serializeUserInfoModel :" + json);
      return json;
    }
    return "";

  }

  public static UserInfoMasterPojo deSerializeUserInfoModel(String userInfoJson) {
    logger.debug(" deSerializeUserInfoModel :" + userInfoJson);
    UserInfoMasterPojo model = null;
    if (StringUtils.isNotBlank(userInfoJson)) {
      try {
        model = new Gson().fromJson(userInfoJson, UserInfoMasterPojo.class);
      } catch (Exception e) {
        logger.error(ExceptionUtils.getStackTrace(e));
      }
    }
    logger.debug("After deSerializeUserInfoModel :" + model);
    return model;

  }

}
