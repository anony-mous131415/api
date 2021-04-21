/**
 * 
 */
package io.revx.auth.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import io.revx.auth.constants.SecurityConstants;
import io.revx.core.service.CacheService;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LogoutCacheHolderTest {
  @Mock
  private CacheService cacheService;

  private SecurityConstants securityConstants;
  @InjectMocks
  private LogoutCacheHolder logoutCacheHolder;



  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    securityConstants = new SecurityConstants();
    logoutCacheHolder.securityConstants = securityConstants;
    long val = 12345;
    when(cacheService.getFromLogoutCache(ArgumentMatchers.contains("akhilesh"))).thenReturn(val);

  }

  /**
   * Test method for {@link io.revx.auth.utils.LogoutCacheHolder#getCache(java.lang.String)}.
   */
  @Test
  public void testGetCache() throws Exception {
    long val = logoutCacheHolder.getCache("akhileshHDFKS87458QJFKDH");
    assertThat(val).isEqualTo(12345);
  }

  @Test
  public void testGetCacheFailed() throws Exception {
    long val = logoutCacheHolder.getCache("failedHDFKS87458QJFKDH");
    assertThat(val).isLessThanOrEqualTo(0);
  }



  /**
   * Test method for
   * {@link io.revx.auth.utils.LogoutCacheHolder#setCache(java.lang.String, java.lang.Long)}.
   */
  @Test
  public void testSetCache() throws Exception {
    logoutCacheHolder.setCache("akhilesh", 12345l);
    assertTrue(true);
  }

  @Test
  public void testGetCacheFailedCacheNull() throws Exception {
    logoutCacheHolder.cacheService = null;
    long val = logoutCacheHolder.getCache("exceptionHDFKS87458QJFKDH");
    assertThat(val).isLessThanOrEqualTo(0);
  }

}
