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
public class PostgresConfigTest {

  /**
   * @throws java.lang.Exception
   */
  @InjectMocks
  PostgresConfig pc;
  @Mock
  Environment env;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for {@link io.revx.api.config.PostgresConfig#postgresEntityManager()}.
   */
  @Test
  public void testPostgresEntityManager() throws Exception {
    when(env.getProperty(Mockito.eq("postgres.datasource.driver-class-name")))
        .thenReturn("org.postgresql.Driver");
    LocalContainerEntityManagerFactoryBean ds = pc.postgresEntityManager();
    assertNotNull(ds);
  }

  /**
   * Test method for {@link io.revx.api.config.PostgresConfig#postgresDataSource()}.
   */
  @Test
  public void testPostgresDataSource() throws Exception {

    when(env.getProperty(Mockito.eq("postgres.datasource.driver-class-name")))
        .thenReturn("org.postgresql.Driver");
    DataSource ds = pc.postgresDataSource();
    assertNotNull(ds);
  }

  /**
   * Test method for {@link io.revx.api.config.PostgresConfig#postgresTransactionManager()}.
   */
  @Test
  public void testPostgresTransactionManager() throws Exception {
    when(env.getProperty(Mockito.eq("postgres.datasource.driver-class-name")))
        .thenReturn("org.postgresql.Driver");
    PlatformTransactionManager ds = pc.postgresTransactionManager();
    assertNotNull(ds);
  }

}
