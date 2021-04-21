package io.revx.auth.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import io.revx.auth.AuthServiceMainApplication;
import io.revx.auth.security.JwtauthConfig;
import io.revx.auth.security.Md5PasswordEncoder;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestAllSingltonBean {

  @Test
  public void testRestTemplateBean() throws Exception {
    AuthServiceMainApplication main = new AuthServiceMainApplication();
    RestTemplate restBean = main.restTemplate();
    assertNotNull(restBean);
  }

  @Test
  public void testRoleHierarchy() throws Exception {
    AuthServiceMainApplication main = new AuthServiceMainApplication();
    RoleHierarchy restBean = main.roleHierarchy();
    assertNotNull(restBean);
  }

  @Test
  public void testMd5PasswordEncoder() throws Exception {
    Md5PasswordEncoder encoder = new Md5PasswordEncoder();
    assertNotNull(encoder);
    String testString = "testMyString";
    String encode = encoder.encode(testString);
    assertTrue(encoder.matches(testString, encode));
  }

  @Test
  public void testJwtauthConfig() throws Exception {
    JwtauthConfig main = new JwtauthConfig();
    BCryptPasswordEncoder restBean = main.bCryptPasswordEncoder();
    assertNotNull(restBean);
  }
}
