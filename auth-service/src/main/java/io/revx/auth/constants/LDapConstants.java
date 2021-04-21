package io.revx.auth.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LDapConstants {

  // LDAP Provider Settings
  private Boolean ldapAuthEnabled = false;
  private String ldapAuthUrl = "ldap://localhost:8389/dc=ldaptest,dc=org";
  private String ldapUsersDnPattern = "uid={0},ou=people";
  private String ldapGroupsSearchBase = "ou=groups";
  private String ldapUserPassSecretAttribute = "userPassword";

  public Boolean getLdapAuthEnabled() {
    return ldapAuthEnabled;
  }

  @Value("${ldap.ldapAuthEnabled:false}")
  public void setLdapAuthEnabled(Boolean ldapAuthEnabled) {
    this.ldapAuthEnabled = ldapAuthEnabled;
  }

  public String getLdapAuthUrl() {
    return ldapAuthUrl;
  }

  @Value("${ldap.ldapAuthUrl:ldap://localhost:8389/dc=ldaptest,dc=org}")
  public void setLdapAuthUrl(String ldapAuthUrl) {
    this.ldapAuthUrl = ldapAuthUrl;
  }

  public String getLdapGroupsSearchBase() {
    return ldapGroupsSearchBase;
  }

  @Value("${ldap.ldapGroupsSearchBase:ou=groups}")
  public void setLdapGroupsSearchBase(String ldapGroupsSearchBase) {
    this.ldapGroupsSearchBase = ldapGroupsSearchBase;
  }

  public String getLdapUsersDnPattern() {
    return ldapUsersDnPattern;
  }

  @Value("${ldap.ldapUsersDnPattern:uid={0},ou=people}")
  public void setLdapUsersDnPattern(String ldapUsersDnPattern) {
    this.ldapUsersDnPattern = ldapUsersDnPattern;
  }

  public String getLdapUserPassSecretAttribute() {
    return ldapUserPassSecretAttribute;
  }

  @Value("${ldap.ldapUserPasswordAttribute:userPassword}")
  public void setLdapUserPassSecretAttribute(String ldapUserPassSecretAttribute) {
    this.ldapUserPassSecretAttribute = ldapUserPassSecretAttribute;
  }

}
