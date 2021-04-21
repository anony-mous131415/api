/**
 * 
 */
package io.revx.api.config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AdxConfigTest {


  @Mock
  Environment env;

  @InjectMocks
  private AdxConfig ac;


  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for {@link io.revx.api.config.AdxConfig#adxEntityManager()}.
   */
  @Test
  public void testAdxEntityManager() throws Exception {

    when(env.getProperty(Mockito.eq("adx.datasource.driver-class-name")))
        .thenReturn("com.mysql.jdbc.Driver");
    LocalContainerEntityManagerFactoryBean ds = ac.adxEntityManager();
    assertNotNull(ds);
  }

  /**
   * Test method for {@link io.revx.api.config.AdxConfig#adxDataSource()}.
   */
  @Test
  public void testAdxDataSource() throws Exception {

    when(env.getProperty(Mockito.eq("adx.datasource.driver-class-name")))
        .thenReturn("com.mysql.jdbc.Driver");
    DataSource ds = ac.adxDataSource();
    assertNotNull(ds);
  }

  /**
   * Test method for {@link io.revx.api.config.AdxConfig#adxTransactionManager()}.
   */
  @Test
  public void testAdxTransactionManager() throws Exception {

    when(env.getProperty(Mockito.eq("adx.datasource.driver-class-name")))
        .thenReturn("com.mysql.jdbc.Driver");
    PlatformTransactionManager ds = ac.adxTransactionManager();
    assertNotNull(ds);
  }

}
