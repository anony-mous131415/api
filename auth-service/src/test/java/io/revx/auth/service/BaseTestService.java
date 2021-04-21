/**
 * 
 */
package io.revx.auth.service;

import static org.mockito.Mockito.when;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.entity.LicenseeEntity;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.repository.LicenseeRepository;
import io.revx.auth.repository.LicenseeUserRoleRepo;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.repository.UserRepository;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.security.Md5PasswordEncoder;
import io.revx.auth.utils.LogoutCacheHolder;

/**
 * @author amaurya
 *
 */

public class BaseTestService {

  protected static Logger logger = LogManager.getLogger(BaseTestService.class);

  @Mock
  protected LicenseeRepository licenseeRepository;



  @Mock
  protected LicenseeUserRoleRepo licenseeUserRoleRepo;

  @Mock
  protected LogoutCacheHolder logoutCacheHolder;



  protected Md5PasswordEncoder md5PasswordEncoder = new Md5PasswordEncoder();


  @Mock
  protected UserRepository userRepository;

  @Mock
  protected JwtTokenProvider jwtTokenProvider;

  protected SecurityConstants securityConstants = new SecurityConstants();

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  @Mock
  LoginLogoutService loginLogoutService;


  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }


  protected void makeMock(String username, int userRoleCount, int activeLicenseeCount,
      boolean isSuperAdmin) {
    UserInfoEntity obj = TestDataGenerator.getUserEntityObject(username);
    List<LicenseeUserRolesEntity> licenseeUserRolesEntityList = TestDataGenerator
        .getListOfObject(isSuperAdmin ? 1 : userRoleCount, LicenseeUserRolesEntity.class);
    for (LicenseeUserRolesEntity licenseeUserRolesEntity : licenseeUserRolesEntityList) {
      licenseeUserRolesEntity.setUserId(obj.getId());
      licenseeUserRolesEntity.setLicenseeEntity(TestDataGenerator.getLicenseeEntity(33));;
      if (isSuperAdmin) {
        licenseeUserRolesEntity.setLicenseeEntity(null);
      }
    }

    List<LicenseeEntity> licenseeEntities =
        TestDataGenerator.getListOfObject(activeLicenseeCount, LicenseeEntity.class);
    LicenseeEntity li = licenseeEntities.get(0);
    li.setId(33);
    when(userRepository.findByUsername(username)).thenReturn(obj);
    when(userRepository.findByUsernameAndIsActive(username, true)).thenReturn(obj);
    when(licenseeUserRoleRepo.findByUserId(obj.getId())).thenReturn(licenseeUserRolesEntityList);
    when(licenseeUserRoleRepo.findByUserIdAndLicenseeId(obj.getId(), 33))
        .thenReturn(licenseeUserRolesEntityList);
    when(licenseeRepository.findByIsActive(true)).thenReturn(licenseeEntities);
    when(licenseeRepository.getOne(33)).thenReturn(li);
    when(userRepository.save(obj)).thenReturn(obj);
    when(logoutCacheHolder.getCache("akhilesh"))
        .thenReturn(System.currentTimeMillis() + 10 * 60 * 60 * 1000);

  }


}
