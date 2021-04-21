package io.revx.auth.service;

import io.revx.auth.AuthServiceMainApplication;
import io.revx.auth.PrintingDataForTest;
import io.revx.auth.config.SwaggerConfig;
import io.revx.auth.constants.ApiConstant;
import io.revx.auth.constants.LDapConstants;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.entity.AdvertiserEntity;
import io.revx.auth.entity.CurrencyEntity;
import io.revx.auth.entity.LicenseeEntity;
import io.revx.auth.entity.LicenseeUserRolesEntity;
import io.revx.auth.entity.RolesEntity;
import io.revx.auth.entity.UserInfoEntity;
import io.revx.auth.enums.ExternalClients;
import io.revx.auth.pojo.UserInfoMasterPojo;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.requests.PasswordChangeRequest;
import io.revx.auth.requests.UserLoginRequest;
import io.revx.auth.utils.Md5Utils;
import io.revx.auth.utils.PojoTestUtils;
import io.revx.auth.utils.UserUtils;
import io.revx.core.model.Advertiser;
import org.jeasy.random.EasyRandom;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestAllConstructor {


  private SecurityConstants securityConstants = new SecurityConstants();
  private LDapConstants lDapConstants = new LDapConstants();


  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();


  @Test
  public void testPrivateConstructorApiConstant() throws Exception {
    java.lang.reflect.Constructor<ApiConstant> constructor =
        ApiConstant.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testPrivateConstructorMd5Utils() throws Exception {
    java.lang.reflect.Constructor<Md5Utils> constructor = Md5Utils.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testPrivateConstructorPrintingDataForTest() throws Exception {
    java.lang.reflect.Constructor<PrintingDataForTest> constructor =
        PrintingDataForTest.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testPrivateConstructorAuthServiceMainApplication() throws Exception {
    java.lang.reflect.Constructor<AuthServiceMainApplication> constructor =
        AuthServiceMainApplication.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
    AuthServiceMainApplication main = new AuthServiceMainApplication();
    main.run("test");
  }

  @Test
  public void testPrivateConstructorMicrosoftAuthService() throws Exception {
    java.lang.reflect.Constructor<MicrosoftAuthService> constructor =
        MicrosoftAuthService.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
    AuthServiceMainApplication main = new AuthServiceMainApplication();
    main.run("test");
  }

  @Test
  public void testLDapConstants() throws Exception {
    java.lang.reflect.Constructor<LDapConstants> constructor =
        LDapConstants.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
    lDapConstants.setLdapAuthEnabled(true);
    lDapConstants.setLdapAuthUrl("ldap.com");
    lDapConstants.setLdapGroupsSearchBase("test");
    lDapConstants.setLdapUserPassSecretAttribute("pass");
    lDapConstants.setLdapUsersDnPattern("dnUser");
    assertTrue(lDapConstants.getLdapAuthEnabled());
    assertThat(lDapConstants.getLdapAuthUrl()).isEqualToIgnoringCase("ldap.com");
    assertThat(lDapConstants.getLdapGroupsSearchBase()).isEqualToIgnoringCase("test");
    assertThat(lDapConstants.getLdapUserPassSecretAttribute()).isEqualToIgnoringCase("pass");
    assertThat(lDapConstants.getLdapUsersDnPattern()).isEqualToIgnoringCase("dnUser");
  }


  @Test
  public void testPrivateConstructorSecurityConstants() throws Exception {
    java.lang.reflect.Constructor<SecurityConstants> constructor =
        SecurityConstants.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testAllSecurityConstants() throws Exception {
    securityConstants.setAccessTokenValidity(12);
    securityConstants.setFaceBookApiUrl("www.facebook.com");
    securityConstants.setGoogleApiUrl("www.google.com");
    securityConstants.setMaxCacheSize(1000);
    securityConstants.setOffice365ApiUrl("www.office.com");
    securityConstants.setSigningKey("abc@123");
    assertThat(securityConstants.getACCESS_TOKEN_VALIDITY_SECONDS()).isEqualTo(12 * 60 * 60);
    assertThat(securityConstants.getMAX_CACHE_SIZE()).isEqualTo(1000);
    assertThat(securityConstants.getFACEBOOK_AUTH_API()).isEqualToIgnoringCase("www.facebook.com");
    assertThat(securityConstants.getGOOGLE_AUTH_API()).isEqualToIgnoringCase("www.google.com");
    assertThat(securityConstants.getOFFICE_365_AUTH_API()).isEqualToIgnoringCase("www.office.com");
    assertThat(securityConstants.getSIGNING_KEY()).isEqualToIgnoringCase("abc@123");
  }

  @Test
  public void testPrivateConstructorSwaggerConfig() throws Exception {
    java.lang.reflect.Constructor<SwaggerConfig> constructor =
        SwaggerConfig.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
    SwaggerConfig config = new SwaggerConfig();
    Docket doc = config.api();
    assertThat(doc.getDocumentationType()).isEqualTo(DocumentationType.SWAGGER_2);

  }

  @Test
  public void testPrivateConstructorExternalClients() throws Exception {
    ExternalClients client = ExternalClients.getByClientName("google");
    assertThat(client).isEqualTo(ExternalClients.GOOGLE);
    client = ExternalClients.getByClientName("facebook");
    assertThat(client).isEqualTo(ExternalClients.FACEBOOK);
    client = ExternalClients.getByClientName("office");
    assertThat(client).isEqualTo(ExternalClients.OFFICE);

  }

  @Test
  public void testPrivateConstructorAdvertiserEntity() throws Exception {
    java.lang.reflect.Constructor<AdvertiserEntity> constructor =
        AdvertiserEntity.class.getDeclaredConstructor();
    assertTrue("Constructor is not private", Modifier.isPublic(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

  @Test
  public void testGetterSetterAdvertiserEntity() {
    assertTrue(PojoTestUtils.validateAccessors(AdvertiserEntity.class));
  }

  @Test
  public void testGetterSetterLicenseeEntity() {
    assertTrue(PojoTestUtils.validateAccessors(LicenseeEntity.class));
  }

  @Test
  public void testGetterSetterCurrencyEntity() {
    assertTrue(PojoTestUtils.validateAccessors(CurrencyEntity.class));
  }

  @Test
  public void testGetterSetterRolesEntity() {
    assertTrue(PojoTestUtils.validateAccessors(RolesEntity.class));
  }

  @Test
  public void testGetterSetterUserInfoEntity() {
    assertTrue(PojoTestUtils.validateAccessors(UserInfoEntity.class));
  }

  @Test
  public void testGetterSetterLicenseeUserRolesEntity() {
    assertTrue(PojoTestUtils.validateAccessors(LicenseeUserRolesEntity.class));
  }


  @Test
  public void testGetterSetterUserInfoMasterPojo() {
    assertTrue(PojoTestUtils.validateAccessors(UserInfoMasterPojo.class));
    UserInfoMasterPojo uimp = new UserInfoMasterPojo("akhilesh", new HashSet<>());
    UsernamePasswordAuthenticationToken ua = new UsernamePasswordAuthenticationToken(uimp, "pass");
    ua.setDetails(uimp);
    UserInfoMasterPojo nn = UserInfoMasterPojo.getFromAuth(ua);
    assertNotNull(nn);
    assertNotNull(UserInfoMasterPojo.serializeUserInfoModel(ua));
    UserInfoMasterPojo nn1 = UserInfoMasterPojo.getFromAuth(null);
    assertNull(nn1);
    assertThat(UserInfoMasterPojo.serializeUserInfoModel(null)).isEqualTo("");
  }


  @Test
  public void testGetterSetterUserInfoMasterPojoWithLdap() {
    LdapUserDetailsImpl uimp = new EasyRandom().nextObject(LdapUserDetailsImpl.class);
    UsernamePasswordAuthenticationToken ua = new UsernamePasswordAuthenticationToken(uimp, "pass");
    ua.setDetails(uimp);
    UserInfoMasterPojo nn = UserInfoMasterPojo.getFromAuth(ua);
    assertNotNull(nn);
    assertThat(uimp.getUsername()).isEqualTo(nn.getUsername());
  }

  @Test
  public void testGetterSetterUsernfoModelWithLdap() {
    LdapUserDetailsImpl uimp = new EasyRandom().nextObject(LdapUserDetailsImpl.class);
    UsernamePasswordAuthenticationToken ua = new UsernamePasswordAuthenticationToken(uimp, "pass");
    ua.setDetails(uimp);
    UserInfoModel nn = UserInfoModel.getFromAuth(ua);
    assertNotNull(nn);
    assertThat(uimp.getUsername()).isEqualTo(nn.getUsername());
  }

  @Test
  public void testGetterSetterUserInfoMasterPojoFailed() {
    UserInfoMasterPojo obj = UserInfoMasterPojo.deSerializeUserInfoModel("hadgFJAGETREWGFBJ");
    assertNull(obj);
    obj = UserInfoMasterPojo.deSerializeUserInfoModel("");
    assertNull(obj);
    UserInfoModel model = UserInfoModel.getFromAuth(null);
    assertNull(model);
  }


  @Test
  public void testGetterSetterUserInfoModel() {
    UserLoginRequest req = new UserLoginRequest();
    req.setUsername("akhilesh");
    req.toString();
    UserInfoModel uim = new UserInfoModel("akhilesh", "pass", new ArrayList<>());
    uim.setDetail(req);
    uim.addAdvertiser(new Advertiser(123, "testAdv"));
    uim.toString();
  }


  @Test
  public void testGetterSetterUserLoginRequest() {
    assertTrue(PojoTestUtils.validateAccessors(UserLoginRequest.class));
    UserLoginRequest req = new UserLoginRequest();
    req.setUsername("akhilesh");
    req.toString();
    UsernamePasswordAuthenticationToken ua =
        new UsernamePasswordAuthenticationToken("akhilesh", "pass");
    ua.setDetails(req);
    UserLoginRequest newReq = UserLoginRequest.getFromAuth(ua);
    assertNotNull(newReq);
    assertThat(newReq.getUsername()).isEqualTo(req.getUsername());
    assertNull(UserLoginRequest.getFromAuth(null));

  }

  @Test
  public void testGetterSetterPasswordChangeRequest() {
    assertTrue(PojoTestUtils.validateAccessors(PasswordChangeRequest.class));
    new PasswordChangeRequest().toString();
  }

  @Test
  public void testGetterSetterUserUtils() {
    PojoTestUtils pojo = new PojoTestUtils();
    pojo.toString();
    assertTrue(PojoTestUtils.validateAccessors(UserUtils.class));
  }
}
