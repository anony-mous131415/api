/**
 * 
 */
package io.revx.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import io.revx.core.response.ApiResponseObject;

/**
 * @author amaurya
 *
 */
public class AdminRoleValidationControllerTest {

  @InjectMocks
  private AdminRoleValidationController adminRoleValidationController;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for {@link io.revx.auth.controller.AdminRoleValidationController#checkSAdmin()}.
   */
  @Test
  public void testCheckSAdmin() throws Exception {
    ResponseEntity<ApiResponseObject<String>> resp = adminRoleValidationController.checkSAdmin();
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    assertNotNull(resp.getBody().getRespObject());
    assertThat(resp.getBody().getRespObject()).contains("checkSAdmin");
  }

  /**
   * Test method for {@link io.revx.auth.controller.AdminRoleValidationController#checkAdmin()}.
   */
  @Test
  public void testCheckAdmin() throws Exception {
    ResponseEntity<ApiResponseObject<String>> resp = adminRoleValidationController.checkAdmin();
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    assertNotNull(resp.getBody().getRespObject());
    assertThat(resp.getBody().getRespObject()).contains("checkAdmin");
  }

  /**
   * Test method for {@link io.revx.auth.controller.AdminRoleValidationController#checkRWAccess()}.
   */
  @Test
  public void testCheckRWAccess() throws Exception {
    ResponseEntity<ApiResponseObject<String>> resp = adminRoleValidationController.checkRWAccess();
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    assertNotNull(resp.getBody().getRespObject());
    assertThat(resp.getBody().getRespObject()).contains("checkRWAccess");
  }

  /**
   * Test method for {@link io.revx.auth.controller.AdminRoleValidationController#checkRo()}.
   */
  @Test
  public void testCheckRo() throws Exception {
    ResponseEntity<ApiResponseObject<String>> resp = adminRoleValidationController.checkRo();
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    assertNotNull(resp.getBody().getRespObject());
    assertThat(resp.getBody().getRespObject()).contains("checkRo");
  }

  /**
   * Test method for {@link io.revx.auth.controller.AdminRoleValidationController#checkDemo()}.
   */
  @Test
  public void testCheckDemo() throws Exception {
    ResponseEntity<ApiResponseObject<String>> resp = adminRoleValidationController.checkDemo();
    assertNotNull(resp);
    assertNotNull(resp.getBody());
    assertNotNull(resp.getBody().getRespObject());
    assertThat(resp.getBody().getRespObject()).contains("checkDemo");
  }


}
