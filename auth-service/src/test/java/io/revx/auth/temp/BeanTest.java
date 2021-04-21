package io.revx.auth.temp;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import io.revx.auth.entity.RolesEntity;
import io.revx.auth.pojo.UserInfoMasterPojo;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.core.enums.RoleName;

public class BeanTest {

  static Map<Integer, Set<Integer>> licenseeToAdvMapTemp = new HashMap<Integer, Set<Integer>>();
  static Map<Integer, Map<Integer, Set<RolesEntity>>> licenseeToAdvToRoleMaptemp =
      new HashMap<Integer, Map<Integer, Set<RolesEntity>>>();

  public static String getMd5(String input) {
    try {

      MessageDigest md = MessageDigest.getInstance("MD5");

      byte[] messageDigest = md.digest(input.getBytes());

      BigInteger no = new BigInteger(1, messageDigest);

      String hashtext = no.toString(16);
      while (hashtext.length() < 32) {
        hashtext = "0" + hashtext;
      }
      return hashtext;
    }

    // For specifying wrong message digest algorithms
    catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) {
    System.out.println(getMd5("password"));
  }

  public static void beanTe(String[] args) {

    // EasyRandom easyRandom = new EasyRandom();
    UserInfoModel uim = new UserInfoModel("test", "test", new ArrayList<GrantedAuthority>());

    RolesEntity r1 = new RolesEntity();
    r1.setId(1);
    r1.setDescription(" Demo ");
    r1.setName(RoleName.ADMIN);
    // uim.addLicenseeToAdvToRole(123, 111, r1);
    System.out.println(uim);
    UserInfoMasterPojo newObject = new UserInfoMasterPojo();
    BeanUtils.copyProperties(uim, newObject);
    System.out.println(newObject);
  }

}


class MapBean {
  private HashMap<Integer, HashSet<Integer>> licenseeToAdvMap;
  private HashMap<Integer, HashMap<Integer, HashSet<RolesEntity>>> licenseeToAdvToRoleMap;

  public HashMap<Integer, HashSet<Integer>> getLicenseeToAdvMap() {
    return licenseeToAdvMap;
  }

  public void setLicenseeToAdvMap(HashMap<Integer, HashSet<Integer>> licenseeToAdvMap) {
    this.licenseeToAdvMap = licenseeToAdvMap;
  }

  public HashMap<Integer, HashMap<Integer, HashSet<RolesEntity>>> getLicenseeToAdvToRoleMap() {
    return licenseeToAdvToRoleMap;
  }

  public void setLicenseeToAdvToRoleMap(
      HashMap<Integer, HashMap<Integer, HashSet<RolesEntity>>> licenseeToAdvToRoleMap) {
    this.licenseeToAdvToRoleMap = licenseeToAdvToRoleMap;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("MapBean [licenseeToAdvMap=");
    builder.append(licenseeToAdvMap);
    builder.append(", licenseeToAdvToRoleMap=");
    builder.append(licenseeToAdvToRoleMap);
    builder.append("]");
    return builder.toString();
  }

}
