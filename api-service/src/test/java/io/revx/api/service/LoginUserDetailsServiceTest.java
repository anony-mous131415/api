/**
 * 
 */
package io.revx.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.BaseTestService;
import io.revx.api.es.entity.ElasticSearchTerm;
import io.revx.core.enums.RoleName;
import io.revx.core.response.UserInfo;

/**
 * @author amaurya
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class LoginUserDetailsServiceTest extends BaseTestService {


  @InjectMocks
  private LoginUserDetailsService loginUserDetailsService;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    super.setUp();
    MockitoAnnotations.initMocks(this);
  }



  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#getAdvertiserCurrencyId()}.
   */
  @Test
  public void testGetAdvertiserCurrencyId() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    String advCurrency = loginUserDetailsService.getAdvertiserCurrencyId();
    assertNotNull(advCurrency);
    assertThat(advCurrency).isEqualTo("USD");
  }

  @Test
  public void testGetAdvertiserCurrencyIdWhenLicensee() throws Exception {
    mockSecurityContext("akhilesh", false, true);
    String advCurrency = loginUserDetailsService.getAdvertiserCurrencyId();
    assertNotNull(advCurrency);
    assertThat(advCurrency).isEqualTo("INR");
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#isAdvertiserLogin()}.
   */
  @Test
  public void testIsAdvertiserLoginSuccess() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    boolean advLogin = loginUserDetailsService.isAdvertiserLogin();
    assertTrue(advLogin);
  }

  @Test
  public void testIsAdvertiserLoginFailed() throws Exception {
    mockSecurityContext("akhilesh", false, true);
    boolean advLogin = loginUserDetailsService.isAdvertiserLogin();
    assertFalse(advLogin);
  }


  @Test
  public void testIsAdvertiserLoginFailedWithMultipleAdvAccess() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    boolean advLogin = loginUserDetailsService.isAdvertiserLogin();
    assertFalse(advLogin);
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#getLicenseeId()}.
   */
  @Test
  public void testGetLicenseeId() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    Long liId = loginUserDetailsService.getLicenseeId();
    assertNotNull(liId);
    assertThat(liId).isEqualTo(33);
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#getLicenseeCurrencyId()}.
   */
  @Test
  public void testGetLicenseeCurrencyId() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    String liCurrency = loginUserDetailsService.getLicenseeCurrencyId();
    assertNotNull(liCurrency);
    assertThat(liCurrency).isEqualTo("INR");
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#getUserInfo()}.
   */
  @Test
  public void testGetUserInfo() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    UserInfo ui = loginUserDetailsService.getUserInfo();
    assertNotNull(ui);
    assertThat(ui.getUsername()).isEqualTo("akhilesh");
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#getUserName()}.
   */
  @Test
  public void testGetUserName() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    String uiName = loginUserDetailsService.getUserName();
    assertNotNull(uiName);
    assertThat(uiName).isEqualTo("akhilesh");
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#getLicenseeName()}.
   */
  @Test
  public void testGetLicenseeName() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    String name = loginUserDetailsService.getLicenseeName();
    assertNotNull(name);
    assertThat(name).isEqualTo("Licensee 33");
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#getElasticSearchTerm()}.
   */
  @Test
  public void testGetElasticSearchTerm() throws Exception {
    mockSecurityContext("akhilesh", true, false);
    ElasticSearchTerm name = loginUserDetailsService.getElasticSearchTerm();
    assertNotNull(name);
    assertThat(name.getLicensies()).contains(33l);
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#isSuperAdminUser()}.
   */

  @Test
  public void testIsSuperAdminUserFalse() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    boolean sAdmin = loginUserDetailsService.isSuperAdminUser();
    assertFalse(sAdmin);
  }

  @Test
  public void testIsSuperAdminUser() throws Exception {
    mockSecurityContext("akhilesh", "ROLE_" + RoleName.SADMIN, 33, false, false);
    boolean sAdmin = loginUserDetailsService.isSuperAdminUser();
    assertTrue(sAdmin);
  }


  /**
   * Test method for
   * {@link io.revx.api.service.LoginUserDetailsService#getHighestRoleOfLoginUser()}.
   */
  @Test
  public void testGetHighestRoleOfLoginUser() throws Exception {
    mockSecurityContext("akhilesh", false, false);
    RoleName role = loginUserDetailsService.getHighestRoleOfLoginUser();
    assertNotNull(role);
    assertThat(role).isEqualTo(RoleName.RW);
  }

  /**
   * Test method for {@link io.revx.api.service.LoginUserDetailsService#isReadOnlyUser()}.
   */
  @Test
  public void testIsReadOnlyUser() throws Exception {
    mockSecurityContext("akhilesh", "ROLE_" + RoleName.RO, 33, false, false);
    boolean role = loginUserDetailsService.isReadOnlyUser();
    assertTrue(role);
  }

}
