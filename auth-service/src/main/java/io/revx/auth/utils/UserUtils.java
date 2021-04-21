package io.revx.auth.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.RolesEntity;
import io.revx.auth.pojo.UserInfoMasterPojo;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.core.response.UserInfo;

public class UserUtils {

  public static Set<SimpleGrantedAuthority> getAuthority(
      List<LicenseeUserRolesEntity> licenseeUserRolesEntity) {

    Set<SimpleGrantedAuthority> authorities = new HashSet<>();
    if (licenseeUserRolesEntity != null) {
      licenseeUserRolesEntity.forEach(lur -> {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + lur.getRolesEntity().getName()));
      });
    }
    return authorities;
  }

  public static Set<String> getAuthority(Set<RolesEntity> rolesEntities) {
    Set<String> authorities = new HashSet<>();
    if (rolesEntities != null) {
      rolesEntities.forEach(role -> {
        authorities.add("ROLE_" + role.getName());
      });
    }
    return authorities;
  }

  public static Set<String> getAuthoritySet(List<LicenseeUserRolesEntity> licenseeUserRolesEntity) {
    Set<String> authorities = new HashSet<>();
    if (licenseeUserRolesEntity != null) {
      licenseeUserRolesEntity.forEach(lur -> {
        authorities.add("ROLE_" + lur.getRolesEntity().getName());
      });
    }
    return authorities;
  }

  public static Set<String> getAuthoritySet(Collection<GrantedAuthority> grantedAuthority) {
    Set<String> authorities = new HashSet<>();
    if (grantedAuthority != null) {
      grantedAuthority.forEach(lur -> {
        authorities.add(lur.getAuthority());
      });
    }
    return authorities;
  }

  public static void copyProperties(UserInfoModel uim, UserInfoMasterPojo ump) {
    if (uim == null)
      return;
    BeanUtils.copyProperties(uim, ump, "authorities");
    Set<String> authorityRole = new HashSet<String>();
    for (GrantedAuthority authority : uim.getAuthorities()) {
      authorityRole.add(authority.getAuthority());
    }
    ump.setAuthorities(authorityRole);
  }

  public static void populateUserInfoPojoFromModel(UserInfoModel userFromDb, UserInfo uip) {
    if (uip == null || userFromDb == null)
      return;
    BeanUtils.copyProperties(userFromDb, uip, "username", "authorities");
  }
}
