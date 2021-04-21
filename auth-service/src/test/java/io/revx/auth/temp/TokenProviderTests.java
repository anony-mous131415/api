
package io.revx.auth.temp;

import org.jeasy.random.EasyRandom;
import org.nustaq.serialization.FSTConfiguration;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class TokenProviderTests {
  private static TestJwtTokenProvider testJwtTokenProvider = new TestJwtTokenProvider();
  private static EasyRandom easyRandom = new EasyRandom();

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < 200; i++) {
      testPOcJwt();
      Thread.sleep(10);
    }

    // System.out.println(pocJwtTokenProvider.getMyInfoFromToken(
    // "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlT010VGh5aFZOTFdVWk5SY0JhUUt4SSIsImlhdCI6MTU2MTExMjUwOSwiaXNzIjoie1widHlwXCI6XCJjb20ucmV2eC5qd3RhdXRoLk15SW5mb1wiLFwib2JqXCI6e1wiYWNjb3VudE5vbkV4cGlyZWRcIjoxLFwiYWNjb3VudE5vbkxvY2tlZFwiOjAsXCJjcmVkZW50aWFsc05vbkV4cGlyZWRcIjoxLFwiZW5hYmxlZFwiOjAsXCJzZWxlY3RlZEFkdmVydGlzZXJcIjoxMDE4OTU0OTAxLFwic2VsZWN0ZWRMaWNlbmNlZVwiOi0xMTg4OTU3NzMxLFwibGljZW5jZWVUb0Fkdk1hcFwiOntcInR5cFwiOlwibWFwXCIsXCJvYmpcIjpbMF19LFwicGFzc3dvcmRcIjpcInllZFVzRndka2VsUWJ4ZVRlUU92YVNjZnFJT09tYWFcIixcInVzZXJuYW1lXCI6XCJlT010VGh5aFZOTFdVWk5SY0JhUUt4SVwifX0iLCJleHAiOjE1NjExMzA1MDksImp0aSI6Itum6oehXHVEODQzXHVEQzlG6b2JIn0.fi-JhfvMkeuOm_qjfdIGPwzijQ-LBVsQFjSz1aDm5BA"));
  }

  public static void testPOcJwt() {
    try {
      MyInfo info = easyRandom.nextObject(MyInfo.class);
      // System.out.println(info);
      long currentTime = System.nanoTime();
      String token = testJwtTokenProvider.generateToken(info);
      long serTime = System.nanoTime();
      // System.out.println(token);
      MyInfo deser = testJwtTokenProvider.getMyInfoFromToken(token);
      long deserTime = System.nanoTime();
      long timeTakenByser = (serTime - currentTime) / 1000;
      long timeTakenBydeser = (deserTime - serTime) / 1000;
      // System.out.println(" deser " + deser);
      if (info.equals(deser)) {
        System.out.println(" Same : " + timeTakenByser + " :: " + timeTakenBydeser);
      } else {
        System.out.println(" Diff : " + timeTakenByser + " :: " + timeTakenBydeser);
      }
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

  public static void testEasyRandom() {
    try {
      MyInfo info = easyRandom.nextObject(MyInfo.class);
      System.out.println(info);

      FSTConfiguration conf = FSTConfiguration.createJsonConfiguration();
      String bytes = new String(conf.asByteArray(info));
      System.out.println(" bytes " + bytes);
      MyInfo deser = (MyInfo) conf.asObject(bytes.getBytes());
      System.out.println(" deser " + deser);
      if (info.equals(deser)) {
        System.out.println(" Same :");
      } else {
        System.out.println(" Diffrent ");
      }
    } catch (Exception e) {
      e.printStackTrace();

    }
  }

}


class MyInfo implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String username;
  private String password;
  // private Set<GrantedAuthority> authorities;
  private boolean accountNonExpired;
  private boolean accountNonLocked;
  private boolean credentialsNonExpired;
  private boolean enabled;
  private Map<Integer, Set<Integer>> licenseeToAdvMap;
  private int selectedLicensee;
  private int selectedAdvertiser;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /*
   * public Set<GrantedAuthority> getAuthorities() { return authorities; }
   * 
   * public void setAuthorities(Set<GrantedAuthority> authorities) { this.authorities = authorities;
   * }
   */

  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  public void setAccountNonExpired(boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
  }

  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  public void setAccountNonLocked(boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
  }

  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  public void setCredentialsNonExpired(boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Map<Integer, Set<Integer>> getLicenseeToAdvMap() {
    return licenseeToAdvMap;
  }

  public void setLicenseeToAdvMap(Map<Integer, Set<Integer>> licenseeToAdvMap) {
    this.licenseeToAdvMap = licenseeToAdvMap;
  }

  public int getSelectedLicensee() {
    return selectedLicensee;
  }

  public void setSelectedLicensee(int selectedLicensee) {
    this.selectedLicensee = selectedLicensee;
  }

  public int getSelectedAdvertiser() {
    return selectedAdvertiser;
  }

  public void setSelectedAdvertiser(int selectedAdvertiser) {
    this.selectedAdvertiser = selectedAdvertiser;
  }

  @Override
  public String toString() {
    return "MyInfo [username=" + username + ", accountNonExpired="
        + accountNonExpired + ", accountNonLocked=" + accountNonLocked + ", credentialsNonExpired="
        + credentialsNonExpired + ", enabled=" + enabled + ", licenseeToAdvMap=" + licenseeToAdvMap
        + ", selectedLicensee=" + selectedLicensee + ", selectedAdvertiser=" + selectedAdvertiser
        + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (accountNonExpired ? 1231 : 1237);
    result = prime * result + (accountNonLocked ? 1231 : 1237);
    result = prime * result + (credentialsNonExpired ? 1231 : 1237);
    result = prime * result + (enabled ? 1231 : 1237);
    result = prime * result + ((licenseeToAdvMap == null) ? 0 : licenseeToAdvMap.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + selectedAdvertiser;
    result = prime * result + selectedLicensee;
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MyInfo other = (MyInfo) obj;
    if (accountNonExpired != other.accountNonExpired)
      return false;
    if (accountNonLocked != other.accountNonLocked)
      return false;
    if (credentialsNonExpired != other.credentialsNonExpired)
      return false;
    if (enabled != other.enabled)
      return false;
    if (licenseeToAdvMap == null) {
      if (other.licenseeToAdvMap != null)
        return false;
    } else if (!licenseeToAdvMap.equals(other.licenseeToAdvMap))
      return false;
    if (password == null) {
      if (other.password != null)
        return false;
    } else if (!password.equals(other.password))
      return false;
    if (selectedAdvertiser != other.selectedAdvertiser)
      return false;
    if (selectedLicensee != other.selectedLicensee)
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    return true;
  }

}
