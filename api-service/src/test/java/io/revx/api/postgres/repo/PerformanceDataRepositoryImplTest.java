/**
 * 
 */
package io.revx.api.postgres.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.pojo.PerformanceDataMetrics;

/**
 * @author amaurya
 *
 */
@SuppressWarnings({"deprecation", "rawtypes"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PerformanceDataRepositoryImplTest {
  @Mock
  private EntityManager entityManager;
  @InjectMocks
  private PerformanceDataRepositoryImpl performanceDataRepositoryImpl;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for
   * {@link io.revx.api.postgres.repo.PerformanceDataRepositoryImpl#queryToDataBase(java.lang.String, java.lang.Class)}.
   */

  @Test
  public void testQueryToDataBase() throws Exception {
    List<PerformanceDataMetrics> results =
        TestDataGenerator.getListOfObject(10, PerformanceDataMetrics.class);
    Query mockedQuery = mock(Query.class);
    org.hibernate.query.Query hQuery = mock(org.hibernate.query.Query.class);
    when(hQuery.setResultTransformer(Mockito.any())).thenReturn(hQuery);
    when(mockedQuery.unwrap(Mockito.any())).thenReturn(hQuery);
    when(hQuery.getResultList()).thenReturn(results);
    when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(mockedQuery);
    List<PerformanceDataMetrics> resultFromDB =
        performanceDataRepositoryImpl.queryToDataBase("", PerformanceDataMetrics.class);
    assertNotNull(resultFromDB);
    assertThat(results.size()).isEqualTo(resultFromDB.size());
  }

}
