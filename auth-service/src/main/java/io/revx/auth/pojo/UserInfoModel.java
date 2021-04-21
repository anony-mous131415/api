package io.revx.auth.pojo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import io.revx.core.model.Advertiser;
import io.revx.core.model.Licensee;

public class UserInfoModel extends User {
  private static final long serialVersionUID = 1L;
  private Long userId;
  private Licensee selectedLicensee;
  private Set<Advertiser> advertisers;
  private Object detail;

  public UserInfoModel(String username, String password,
      Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  /**
   * @return the userId
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Licensee getSelectedLicensee() {
    return selectedLicensee;
  }

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

  public Object getDetail() {
    return detail;
  }

  public void setDetail(Object detail) {
    this.detail = detail;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */

  public static UserInfoModel getFromAuth(Authentication auth) {
    if (auth != null) {
      if (auth.getPrincipal() instanceof UserInfoModel) {
        return (UserInfoModel) auth.getPrincipal();
      } else if (auth.getPrincipal() instanceof LdapUserDetailsImpl) {
        LdapUserDetailsImpl ldapUser = (LdapUserDetailsImpl) auth.getPrincipal();
        UserInfoModel ui =
            new UserInfoModel(ldapUser.getUsername(), "", new ArrayList<GrantedAuthority>());
        return ui;
      }
    }
    return null;

  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("UserInfoModel [userId=");
    builder.append(userId);
    builder.append(", selectedLicensee=");
    builder.append(selectedLicensee);
    builder.append(", advertisers=");
    builder.append(advertisers);
    builder.append(", detail=");
    builder.append(detail);
    builder.append("]");
    return builder.toString();
  }

}
