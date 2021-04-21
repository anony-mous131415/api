package io.revx.auth;

import java.lang.reflect.Field;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.revx.auth.constants.SecurityConstants;
import io.revx.auth.security.JwtTokenProvider;
import io.revx.auth.security.Md5PasswordEncoder;
import io.revx.auth.service.AuthValidaterServiceImpl;
import io.revx.auth.service.ExternalTokenAuthService;
import io.revx.auth.service.LoginLogoutService;
import io.revx.auth.service.UserDetailsServiceImpl;
import io.revx.auth.service.UserService;

// @RunWith(SpringRunner.class)
// @SpringBootTest
public class BaseApplicationTest {
  protected static Logger logger = LogManager.getLogger(BaseApplicationTest.class);
  static {
    System.setProperty("jasypt.encryptor.password", "mySecretKey@123");
  }

  protected static ObjectMapper mapper = new ObjectMapper();
  protected static Gson gson = new Gson();

  @Autowired
  protected JwtTokenProvider jwtTokenProvider;

  @Autowired
  protected UserService userService;

  @Autowired
  protected LoginLogoutService loginLogoutService;

  @Autowired
  protected Md5PasswordEncoder md5PasswordEncoder;

  @Autowired
  protected BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  protected AuthValidaterServiceImpl authValidaterServiceImpl;

  @Autowired
  protected ExternalTokenAuthService externalTokenAuthService;

  @Autowired
  protected UserDetailsServiceImpl userDetailsServiceImpl;

  @Autowired
  protected SecurityConstants securityConstants;

  @Test
  public void contextLoads() {
    Field[] fields = BaseApplicationTest.class.getDeclaredFields();
    logger.info("In BaseApplicationTest.contextLoads :" + fields.length);
    try {
      for (Field field : fields) {
        logger.info(field);
      }
    } catch (Exception e) {
    }

  }
}
