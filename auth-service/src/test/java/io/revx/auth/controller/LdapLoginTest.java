
package io.revx.auth.controller;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.revx.auth.constants.ApiConstant;
import io.revx.auth.requests.UserLoginRequest;

// @RunWith(SpringRunner.class)
// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class LdapLoginTest {

  static {
    System.setProperty("jasypt.encryptor.password", "mySecretKey@123");
  }

  protected static Logger logger = LogManager.getLogger(LdapLoginTest.class);
  @Autowired
  protected TestRestTemplate restTemplate;

  // @Test
  public void contextLoads() {
    logger.info("Loading Context  ::");
  }

  // @Test
  public void loginWithValidUserThenAuthenticatedSingle() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    ResponseEntity<Object> entity = testLogin("maurya", "mypass");
    logger.info("Res :" + mapper.writeValueAsString(entity));
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
  }

  // @Test
  public void loginWithValidUserThenAuthenticatedMultiple() throws Exception {
    ResponseEntity<Object> entity = testLogin("maurya", "mypass");
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    String userPrefix = "test";
    String passPrefix = "testPass";
    ObjectMapper mapper = new ObjectMapper();
    List<ResultActions> resResult = new ArrayList<ResultActions>();
    for (int i = 1; i <= 3; i++) {
      entity = testLogin(userPrefix + i, passPrefix + i);
      assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    try {
      for (ResultActions res : resResult) {
        String json = mapper.writeValueAsString(res);
        logger.info("Res :" + json);
      }
    } catch (Exception e) {

    }
  }

  // @Test
  public void loginWithInvalidUserThenUnauthenticated() throws Exception {
    ResponseEntity<Object> entity = testLogin("invalid", "invalid");
    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  private ResponseEntity<Object> testLogin(String userName, String password) {
    HttpHeaders requestHeaders = getRequestHeader();
    UserLoginRequest req = getUser(userName, password);
    HttpEntity<?> requestEntity = new HttpEntity<UserLoginRequest>(req, requestHeaders);
    ResponseEntity<Object> entity = (ResponseEntity<Object>) this.restTemplate
        .exchange(ApiConstant.LOGIN_URL, HttpMethod.POST, requestEntity, Object.class);
    return entity;
  }

  protected HttpHeaders getRequestHeader() {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.set("Accept-Encoding", "gzip");
    return requestHeaders;
  }

  private UserLoginRequest getUser(String username, String password) {
    UserLoginRequest us = new UserLoginRequest();
    us.setUsername(username);
    us.setPassword(password);
    return us;
  }
}
