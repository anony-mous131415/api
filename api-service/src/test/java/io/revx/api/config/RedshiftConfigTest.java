/**
 * 
 */
package io.revx.api.config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
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

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class RedshiftConfigTest {
  @Mock
  private Environment env;
  @InjectMocks
  private RedshiftConfig rc;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for {@link io.revx.api.config.RedshiftConfig#redshiftEntityManager()}.
   */
  @Test
  public void testRedshiftEntityManager() throws Exception {
    when(env.getProperty(Mockito.eq("redshift.datasource.driver-class-name")))
        .thenReturn("com.amazon.redshift.jdbc.Driver");
    LocalContainerEntityManagerFactoryBean ds = rc.redshiftEntityManager();
    assertNotNull(ds);
  }

  /**
   * Test method for {@link io.revx.api.config.RedshiftConfig#redshiftDataSource()}.
   */
  @Test
  public void testRedshiftDataSource() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for {@link io.revx.api.config.RedshiftConfig#redshiftTransactionManager()}.
   */
  @Test
  public void testRedshiftTransactionManager() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

}
