/**
 * 
 */
package io.revx.api.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.core.enums.RoleName;
import io.revx.core.model.ChartCSVDashboardData;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomMappingStrategyTest {
  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for
   * {@link io.revx.api.utils.CustomMappingStrategy#generateHeader(java.lang.Object)}.
   */
  @Test
  public void testGenerateHeader() throws Exception {
    CustomMappingStrategy<ChartCSVDashboardData> strategy =
        new CustomMappingStrategy<>(RoleName.ADMIN);
    strategy.setType(ChartCSVDashboardData.class);
    ChartCSVDashboardData bean = new ChartCSVDashboardData(123, "Test-name");
    String[] headers = strategy.generateHeader(bean);
    assertNotNull(headers);
    assertThat(headers.length).isEqualTo(34);
  }

  @Test
  public void testGenerateHeaderForReadOnly() throws Exception {
    CustomMappingStrategy<ChartCSVDashboardData> strategy =
        new CustomMappingStrategy<>(RoleName.RO);
    strategy.setType(ChartCSVDashboardData.class);
    ChartCSVDashboardData bean = new ChartCSVDashboardData(123, "Test-name");
    String[] headers = strategy.generateHeader(bean);
    assertNotNull(headers);
    assertThat(headers.length).isEqualTo(28);
  }

}
