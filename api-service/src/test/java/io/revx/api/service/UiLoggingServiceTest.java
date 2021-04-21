/**
 * 
 */
package io.revx.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author amaurya
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
public class UiLoggingServiceTest {

  @InjectMocks
  UiLoggingService UiLoggingService;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Test method for
   * {@link io.revx.api.service.UiLoggingService#log(java.lang.String, java.lang.String)}.
   */
  @Test
  public void testLog() throws Exception {
    UiLoggingService.log("Log My Massage", "INFO");
    UiLoggingService.log("Log My Massage", "DEBUG");
    UiLoggingService.log("Log My Massage", null);
    UiLoggingService.log("Log My Massage", "");
    UiLoggingService.log("Log My Massage", "dbHAGFJGEQFG");

    assertThat(true);
  }

}
