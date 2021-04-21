/**
 * 
 */
package io.revx.api.config;

import static org.junit.Assert.assertNotNull;
import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class ElasticConfigTest {

  /**
   * @throws java.lang.Exception
   */

  ElasticConfig ec;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ec = new ElasticConfig();
  }

  /**
   * Test method for {@link io.revx.api.config.ElasticConfig#client()}.
   */
  @Test
  public void testClient() throws Exception {
    Client client = ec.client();
    assertNotNull(client);
  }

  /**
   * Test method for {@link io.revx.api.config.ElasticConfig#elasticsearchTemplate()}.
   */
  @Test
  public void testElasticsearchTemplate() throws Exception {
    ElasticsearchTemplate client = ec.elasticsearchTemplate();
    assertNotNull(client);
  }

}
