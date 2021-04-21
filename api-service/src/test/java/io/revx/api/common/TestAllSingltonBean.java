package io.revx.api.common;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import io.revx.api.ApiMainApplication;
import io.revx.api.config.SwaggerConfig;
import io.revx.core.RestTemplateErrorHandler;
import springfox.documentation.spring.web.plugins.Docket;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestAllSingltonBean {

  @Test
  public void testRestTemplateBean() throws Exception {
    ApiMainApplication main = new ApiMainApplication();
    main.errorHandler = new RestTemplateErrorHandler();
    main.run("test");
    RestTemplate rt = main.restTemplate();
    assertNotNull(rt);
  }

  @Test
  public void testRoleHierarchy() throws Exception {
    ApiMainApplication main = new ApiMainApplication();
    RoleHierarchy restBean = main.roleHierarchy();
    assertNotNull(restBean);
  }

  @Test
  public void testSwaggerConfig() throws Exception {
    SwaggerConfig main = new SwaggerConfig();
    Docket bean = main.api();
    assertNotNull(bean);
  }
}
