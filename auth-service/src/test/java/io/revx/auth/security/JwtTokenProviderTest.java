/**
 * 
 */
package io.revx.auth.security;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.pojo.UserInfoModel;
import io.revx.auth.repository.TestDataGenerator;
import io.revx.auth.service.LifeTimeAuthTokenService;
import io.revx.auth.utils.LogoutCacheHolder;
import io.revx.auth.utils.UserUtils;
import io.revx.core.enums.RoleName;
import io.revx.core.response.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.stream.Collectors;

import static io.revx.auth.constants.SecurityConstants.AUTHORITIES_KEY;
import static io.revx.auth.constants.SecurityConstants.IS_LIFETIME_AUTH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class JwtTokenProviderTest {
  @Mock
  private LogoutCacheHolder logoutCacheHolder;


  @Mock
  private SecurityConstants securityConstants;

  @Mock
  LifeTimeAuthTokenService lifeTimeAuthTokenService;

  @InjectMocks
  private JwtTokenProvider jwtTokenProvider;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    securityConstants = new SecurityConstants();;
    jwtTokenProvider.logoutCacheHolder = logoutCacheHolder;
    jwtTokenProvider.securityConstants = securityConstants;
    jwtTokenProvider.lifeTimeAuthTokenService = lifeTimeAuthTokenService;
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#generateMasterToken(org.springframework.security.core.Authentication)}.
   */
  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testGenerateMasterTokenAuthentication() throws Exception {
    String username = "akhilesh";
    UserInfoModel user = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UsernamePasswordAuthenticationToken uat =
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    uat.setDetails(user);
    String masterToken = jwtTokenProvider.generateMasterToken(uat);
    assertNotNull(masterToken);
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#validateToken(java.lang.String, org.springframework.security.core.userdetails.UserDetails)}.
   */

  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testValidateTokenStringUserDetails() throws Exception {
    String username = "akhilesh";
    UserInfoModel user = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UsernamePasswordAuthenticationToken uat =
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    uat.setDetails(user);
    String masterToken = jwtTokenProvider.generateMasterToken(uat);
    assertNotNull(masterToken);
    boolean valid = jwtTokenProvider.validateToken(masterToken, user);
    assertTrue(valid);

  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#validateToken(java.lang.String)}.
   */

  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testValidateTokenLifeTimeToken() throws Exception {
    UserInfo user = TestDataGenerator.getObject(UserInfo.class);
    final String authorities = user.getAuthorities().stream().collect(Collectors.joining(","));
    String lifeTimeToken = Jwts.builder().setSubject(user.getUsername()).claim(AUTHORITIES_KEY, authorities)
            .compressWith(CompressionCodecs.DEFLATE)
            .signWith(SignatureAlgorithm.HS256, securityConstants.getSIGNING_KEY())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .claim(IS_LIFETIME_AUTH, true)
            .setId("1").setIssuer(user.serialize()).compact();

    Mockito.when(lifeTimeAuthTokenService.isActiveLifeTimeToken(Mockito.anyString())).thenReturn(true);
    boolean valid = jwtTokenProvider.validateToken(lifeTimeToken);
    assertTrue(valid);
  }


  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#validateToken(java.lang.String, java.lang.String)}.
   */

  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testValidateTokenLifeTimeTokenWithUsername() throws Exception {
    UserInfo user = TestDataGenerator.getObject(UserInfo.class);
    final String authorities = user.getAuthorities().stream().collect(Collectors.joining(","));
    String lifeTimeToken = Jwts.builder().setSubject(user.getUsername()).claim(AUTHORITIES_KEY, authorities)
            .compressWith(CompressionCodecs.DEFLATE)
            .signWith(SignatureAlgorithm.HS256, securityConstants.getSIGNING_KEY())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .claim(IS_LIFETIME_AUTH, true)
            .setId("1").setIssuer(user.serialize()).compact();

    Mockito.when(lifeTimeAuthTokenService.isActiveLifeTimeToken(Mockito.anyString())).thenReturn(true);
    boolean valid = jwtTokenProvider.validateToken(lifeTimeToken, user.getUsername());
    assertTrue(valid);
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#getUsernameFromTokenAfterValidation(java.lang.String)}.
   */
  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testGetUsernameFromTokenAfterValidation() throws Exception {
    String username = "akhilesh";
    UserInfoModel user = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UsernamePasswordAuthenticationToken uat =
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    uat.setDetails(user);
    String masterToken = jwtTokenProvider.generateMasterToken(uat);
    assertNotNull(masterToken);
    String uName = jwtTokenProvider.getUsernameFromTokenAfterValidation(masterToken);
    assertNotNull(uName);
    assertThat(uName).isEqualTo(username);

  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#getAuthentication(java.lang.String, org.springframework.security.core.userdetails.UserDetails)}.
   */
  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testGetAuthenticationStringUserDetails() throws Exception {
    String username = "akhilesh";
    UserInfoModel user = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UsernamePasswordAuthenticationToken uat =
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    uat.setDetails(user);
    String masterToken = jwtTokenProvider.generateMasterToken(uat);
    assertNotNull(masterToken);
    UsernamePasswordAuthenticationToken uName =
        jwtTokenProvider.getAuthentication(masterToken, user);
    assertNotNull(uName);
    assertThat(uName.getName()).isEqualTo(username);
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#getAuthentication(java.lang.String)}.
   */
  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testGetAuthenticationString() throws Exception {
    String username = "akhilesh";
    UserInfoModel userFromDb = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UserInfo uInfo = new UserInfo();
    UserUtils.populateUserInfoPojoFromModel(userFromDb, uInfo);
    uInfo.setAuthorities(UserUtils.getAuthoritySet(userFromDb.getAuthorities()));
    uInfo.setUsername(username);
    String token = jwtTokenProvider.generateAccessToken(uInfo);
    assertNotNull(token);
    UsernamePasswordAuthenticationToken uName = jwtTokenProvider.getAuthentication(token);
    assertNotNull(uName);
    UserInfo info = (UserInfo) uName.getPrincipal();
    assertNotNull(info);
    assertThat(info.getUsername()).isEqualTo(username);
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#getAuthenticationAfterSwitching(java.lang.String, org.springframework.security.core.userdetails.UserDetails)}.
   */
  @Test
  @WithMockUser(username = "akhilesh", authorities = {"ROLE_RW"})
  public void testGetAuthenticationAfterSwitching() throws Exception {
    String username = "akhilesh";
    UserInfoModel user = TestDataGenerator.getUserInfoModel(username, "pass", RoleName.RW);
    UsernamePasswordAuthenticationToken uat =
        new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    uat.setDetails(user);
    String masterToken = jwtTokenProvider.generateMasterToken(uat);
    assertNotNull(masterToken);
    UsernamePasswordAuthenticationToken uName =
        jwtTokenProvider.getAuthenticationAfterSwitching(masterToken, user);
    assertNotNull(uName);
    assertThat(uName.getName()).isEqualTo(username);
  }

  /**
   * Test method for
   * {@link io.revx.auth.security.JwtTokenProvider#getTokenFromRequest(javax.servlet.http.HttpServletRequest)}.
   */
  @Test
  public void testGetTokenFromRequest() throws Exception {
    String randomAuthValue = "ThisIsmyhdbahdsfjhb23r6bheb";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(SecurityConstants.AUTH_HEADER, randomAuthValue);
    String token = jwtTokenProvider.getTokenFromRequest(request);
    assertNotNull(token);
    assertThat(token).isEqualTo(randomAuthValue);
  }

}
