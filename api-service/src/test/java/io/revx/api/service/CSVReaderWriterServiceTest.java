/**
 * 
 */
package io.revx.api.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.api.common.TestDataGenerator;
import io.revx.api.config.ApplicationProperties;
import io.revx.core.enums.RoleName;
import io.revx.core.model.ChartCSVDashboardData;
import io.revx.core.model.ListCSVDashboardData;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CSVReaderWriterServiceTest {
  @Mock
  private ApplicationProperties applicationProperties;


  @Mock
  private LoginUserDetailsService loginUserDetailsService;

  @InjectMocks
  private CSVReaderWriterService cSVReaderWriterService;



  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    applicationProperties = new ApplicationProperties();
    applicationProperties.setDownloadFilePath("/tmp");
    applicationProperties.setFileDownloadDomain("www.abc.com");
    cSVReaderWriterService.applicationProperties = applicationProperties;
  }

  /**
   * Test method for
   * {@link io.revx.api.service.CSVReaderWriterService#writeToCSV(java.lang.String, java.util.List)}.
   */
  @Test
  public void testWriteToCSV() throws Exception {
    List<ChartCSVDashboardData> data =
        TestDataGenerator.getListOfObject(20, ChartCSVDashboardData.class);
    String fileName = "chart" + System.currentTimeMillis();
    when(loginUserDetailsService.getHighestRoleOfLoginUser()).thenReturn(RoleName.RW);
    cSVReaderWriterService.writeToCSV(fileName, data);
    boolean status =
        new File(applicationProperties.getDownloadFilePath() + "/" + fileName).exists();
    assertTrue(status);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.CSVReaderWriterService#writeListToCSV(java.lang.String, java.util.List)}.
   */
  @Test
  public void testWriteListToCSV() throws Exception {
    List<ListCSVDashboardData> data =
        TestDataGenerator.getListOfObject(20, ListCSVDashboardData.class);
    String fileName = "list" + System.currentTimeMillis();
    when(loginUserDetailsService.getHighestRoleOfLoginUser()).thenReturn(RoleName.RW);
    cSVReaderWriterService.writeListToCSV(fileName, data);
    boolean status =
        new File(applicationProperties.getDownloadFilePath() + "/" + fileName).exists();
    assertTrue(status);
  }

}
