/**
 * 
 */
package io.revx.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.pojo.UserInfoMasterPojo;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.utils.UserUtils;
import io.revx.core.enums.RoleName;
import io.revx.core.model.Licensee;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest extends BaseTestService {

  @InjectMocks
  private UserService userService;

  /**
   * Test method for
   * {@link io.revx.auth.service.UserService#create(io.revx.auth.entity.UserInfoEntity)}.
   */

  @Before
  public void setup() {
    super.setup();
    MockitoAnnotations.initMocks(this);
    userService.md5PasswordEncoder = md5PasswordEncoder;
  }


  @Test
  public void testCreate() throws Exception {

    UserInfoEntity obj = TestDataGenerator.getObject(UserInfoEntity.class);
    when(userRepository.save(obj)).thenReturn(obj);
    UserInfoEntity objTemp = userService.create(obj);
    assertNotNull(objTemp);
    assertEquals(obj.getId(), objTemp.getId());
    assertEquals(obj.getLicenseeId(), objTemp.getLicenseeId());

  }

  /**
   * Test method for {@link io.revx.auth.service.UserService#findAll()}.
   */

  @Test
  public void testFindAll() {
    List<UserInfoEntity> toDoList = TestDataGenerator.getListOfObject(10, UserInfoEntity.class);
    when(userRepository.findAll()).thenReturn(toDoList);
    List<UserInfoEntity> result = userService.findAll();
    assertEquals(toDoList.size(), result.size());
  }



  /**
   * Test method for {@link io.revx.auth.service.UserService#findById(int)}.
   */
  @Test
  public void testFindById() throws Exception {

    UserInfoEntity obj = TestDataGenerator.getObject(UserInfoEntity.class);
    obj.setId(123l);
    Optional<UserInfoEntity> uiOpt = Optional.of(obj);
    when(userRepository.findById(123)).thenReturn(uiOpt);
    UserInfoEntity objTemp = userService.findById(123);
    assertNotNull(objTemp);
    assertEquals(obj.getId(), objTemp.getId());
    assertEquals(obj.getLicenseeId(), objTemp.getLicenseeId());

  }

  /**
   * Test method for {@link io.revx.auth.service.UserService#findByUsername(java.lang.String)}.
   */
  @Test
  public void testFindByUsernameString() throws Exception {
    String username = "akhilesh";
    UserInfoEntity obj = TestDataGenerator.getObject(UserInfoEntity.class);
    obj.setUsername(username);
    when(userRepository.findByUsername(username)).thenReturn(obj);
    UserInfoEntity objTemp = userService.findByUsername(username);
    assertNotNull(objTemp);
    assertEquals(obj.getUsername(), objTemp.getUsername());
  }



  /**
   * Test method for
   * {@link io.revx.auth.service.UserService#findByUsername(java.lang.String, boolean)}.
   */
  @Test
  public void testFindByUsernameStringBoolean() throws Exception {
    String username = "akhilesh";
    UserInfoEntity obj = TestDataGenerator.getObject(UserInfoEntity.class);
    obj.setUsername(username);
    obj.setActive(true);
    when(userRepository.findByUsernameAndIsActive(username, true)).thenReturn(obj);
    UserInfoEntity objTemp = userService.findByUsername(username, true);
    assertNotNull(objTemp);
    assertEquals(obj.getUsername(), objTemp.getUsername());
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.UserService#update(io.revx.auth.entity.UserInfoEntity)}.
   */
  @Test
  public void testUpdate() throws Exception {
    String username = "akhilesh";
    UserInfoEntity obj = TestDataGenerator.getObject(UserInfoEntity.class);
    obj.setUsername(username);
    obj.setActive(true);
    when(userRepository.save(obj)).thenReturn(obj);
    UserInfoEntity objTemp = userService.update(obj);
    assertNotNull(objTemp);
    assertEquals(obj.getUsername(), objTemp.getUsername());
  }

  /**
   * Test method for {@link io.revx.auth.service.UserService#fetchUserPrivilige(java.lang.String)}.
   */
  @Test
  public void testFetchUserPriviligeForSuperAdmin() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 10;
    makeMock(username, userRoleCount, activeLicenseeCount, true);
    Set<Licensee> objTemp = userService.fetchUserPrivilige(username);
    assertNotNull(objTemp);
    assertEquals(activeLicenseeCount, objTemp.size());
  }

  @Test
  public void testFetchUserPriviligeForNormal() throws Exception {
    String username = "akhilesh";
    UserInfoEntity obj = TestDataGenerator.getObject(UserInfoEntity.class);
    obj.setUsername(username);
    obj.setActive(true);
    List<LicenseeUserRolesEntity> licenseeUserRolesEntityList =
        TestDataGenerator.getListOfObject(10, LicenseeUserRolesEntity.class);
    int activeLicensee = 0;
    for (LicenseeUserRolesEntity licenseeUserRolesEntity : licenseeUserRolesEntityList) {
      licenseeUserRolesEntity.setUserId(obj.getId());
      if (activeLicensee % 2 == 0)
        licenseeUserRolesEntity.getLicenseeEntity().setCurrencyEntity(null);
      if (licenseeUserRolesEntity.getLicenseeEntity().isActive())
        activeLicensee++;
    }
    when(userRepository.findByUsernameAndIsActive(username, true)).thenReturn(obj);
    when(licenseeUserRoleRepo.findByUserId(obj.getId())).thenReturn(licenseeUserRolesEntityList);
    when(userRepository.save(obj)).thenReturn(obj);
    Set<Licensee> objTemp = userService.fetchUserPrivilige(username);
    assertNotNull(objTemp);
    assertEquals(activeLicensee, objTemp.size());
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.UserService#isValidForLicenseeSwitch(io.revx.auth.pojo.UserInfoMasterPojo, java.lang.Long)}.
   */
  @Test
  public void testIsValidForLicenseeSwitch() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 5;
    int activeLicenseeCount = 10;

    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoMasterPojo ump = TestDataGenerator.getObject(UserInfoMasterPojo.class);
    ump.setUsername(username);
    List<LicenseeUserRolesEntity> objTemp =
        userService.isValidForLicenseeSwitch(ump.getUsername(), 33l);
    assertNotNull(objTemp);
    assertThat(objTemp.size()).isGreaterThan(0);
  }

  /**
   * Test method for
   * {@link io.revx.auth.service.UserService#getUserInfoIfEligible(io.revx.auth.pojo.UserInfoModel, io.revx.auth.requests.UserLoginRequest)}.
   */
  @Test
  public void testGetUserInfoIfEligibleUserInfoModel() throws Exception {

    String username = "akhilesh";
    int userRoleCount = 5;
    int activeLicenseeCount = 10;

    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo objTemp = userService.getUserInfoIfEligible(userFromDb);
    assertNotNull(objTemp);
    assertThat(objTemp.getUsername()).isEqualTo(username);

  }

  @Test
  public void testGetUserInfoIfEligibleUserInfoModel_1() throws Exception {

    String username = "akhilesh";
    int userRoleCount = 5;
    int activeLicenseeCount = 10;

    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.getSelectedLicensee().setId(-123l);
    UserInfo objTemp = userService.getUserInfoIfEligible(userFromDb);
    assertNull(objTemp);

  }

  @Test
  public void testGetUserInfoIfEligibleUserInfoModel_2() throws Exception {

    String username = "akhilesh";
    int userRoleCount = 5;
    int activeLicenseeCount = 10;

    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.setSelectedLicensee(null);
    UserInfo objTemp = userService.getUserInfoIfEligible(userFromDb);
    assertNull(objTemp);

  }

  /**
   * Test method for
   * {@link io.revx.auth.service.UserService#getUserInfoIfEligible(io.revx.auth.pojo.UserInfoModel)}.
   */
  @Test
  public void testGetUserInfoIfEligibleUserInfoModelUserLoginRequest() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 5;
    int activeLicenseeCount = 10;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    loginUser.setLicenseeId(33);
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo objTemp = userService.getUserInfoIfEligible(userFromDb, loginUser);
    assertNotNull(objTemp);
    assertThat(objTemp.getUsername()).isEqualTo(username);
  }

  @Test
  public void testGetUserInfoIfEligibleUserInfoModelUserLoginRequestSelectedLicenseeNull()
      throws Exception {
    String username = "akhilesh";
    int userRoleCount = 10;
    int activeLicenseeCount = 5;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    loginUser.setLicenseeId(33);
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.setUserId(12345l);
    userFromDb.setSelectedLicensee(null);
    UserInfo objTemp = userService.getUserInfoIfEligible(userFromDb, loginUser);
    assertNotNull(objTemp);
    assertThat(objTemp.getUsername()).isEqualTo(username);
  }

  @Test
  public void testGetUserInfoIfEligibleUserInfoModelUserLoginRequestSelectedLicenseeNegative()
      throws Exception {
    String username = "akhilesh";
    int userRoleCount = 10;
    int activeLicenseeCount = 5;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    loginUser.setLicenseeId(33);
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.getSelectedLicensee().setId(-1234l);
    UserInfo objTemp = userService.getUserInfoIfEligible(userFromDb, loginUser);
    assertNull(objTemp);
  }

  @Test
  public void testGetUserInfoIfEligibleUserInfoModelUserLoginRequestWithoutLoginLicensee()
      throws Exception {
    String username = "akhilesh";
    int userRoleCount = 10;
    int activeLicenseeCount = 5;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo objTemp = userService.getUserInfoIfEligible(userFromDb, loginUser);
    assertNotNull(objTemp);
  }

  @Test
  public void testUpdateLicenseeAndAdvertiser() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 10;
    int activeLicenseeCount = 5;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.setSelectedLicensee(null);
    List<LicenseeUserRolesEntity> licenseeUserRolesEntityList =
        TestDataGenerator.getListOfObject(userRoleCount, LicenseeUserRolesEntity.class);
    for (LicenseeUserRolesEntity licenseeUserRolesEntity : licenseeUserRolesEntityList) {
      licenseeUserRolesEntity.setLicenseeEntity(TestDataGenerator.getLicenseeEntity(33));;
    }
    userService.updateLicenseeAndAdvertiser(userFromDb, licenseeUserRolesEntityList);;
    assertNotNull(userFromDb);
    assertThat(userFromDb.getSelectedLicensee().getId()).isEqualTo(33l);
  }

  @Test
  public void testUpdateLicenseeAndAdvertiserWithLicenseeId() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 10;
    int activeLicenseeCount = 5;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.setSelectedLicensee(null);
    List<LicenseeUserRolesEntity> licenseeUserRolesEntityList =
        TestDataGenerator.getListOfObject(userRoleCount, LicenseeUserRolesEntity.class);
    for (LicenseeUserRolesEntity licenseeUserRolesEntity : licenseeUserRolesEntityList) {
      licenseeUserRolesEntity.setLicenseeEntity(TestDataGenerator.getLicenseeEntity(33));;
    }
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    userService.updateLicenseeAndAdvertiser(uInfo, licenseeUserRolesEntityList, 33l);
    assertNotNull(userFromDb);
  }

  @Test
  public void testUpdateLicenseeAndAdvertiserWithLicenseeIdForSuperAdmin() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 5;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    makeMock(username, userRoleCount, activeLicenseeCount, true);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.setSelectedLicensee(null);
    List<LicenseeUserRolesEntity> licenseeUserRolesEntityList =
        licenseeUserRoleRepo.findByUserId(12345);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    userService.updateLicenseeAndAdvertiser(uInfo, licenseeUserRolesEntityList, 33l);
    assertNotNull(userFromDb);
  }

  @Test
  public void testUpdateLicenseeAndAdvertiserWithLicenseeIdForNormal() throws Exception {
    String username = "akhilesh";
    int userRoleCount = 1;
    int activeLicenseeCount = 5;
    UserLoginRequest loginUser = new UserLoginRequest();
    loginUser.setUsername(username);
    loginUser.setPassword("pass");
    makeMock(username, userRoleCount, activeLicenseeCount, false);
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    userFromDb.setSelectedLicensee(null);
    List<LicenseeUserRolesEntity> licenseeUserRolesEntityList =
        licenseeUserRoleRepo.findByUserId(12345);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    userService.updateLicenseeAndAdvertiser(uInfo, licenseeUserRolesEntityList, 33l);
    assertNotNull(userFromDb);
  }

}
