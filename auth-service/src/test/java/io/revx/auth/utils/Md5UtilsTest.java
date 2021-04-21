/**
 * 
 */
package io.revx.auth.utils;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author amaurya
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class Md5UtilsTest {

  @Rule
  public ExpectedException exceptionRule = ExpectedException.none();

  /**
   * @throws java.lang.Exception
   * 
   * 
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

  }

  /**
   * Test method for {@link io.revx.auth.utils.Md5Utils#getMd5(java.lang.String)}.
   */
  @Test
  public void testGetMd5() throws Exception {
    String encode = Md5Utils.getMd5("akhilesh");
    assertNotNull(encode);
  }

  @Test
  public void testGetMd51() throws Exception {
    String encode = Md5Utils.getMd5("");
    assertNotNull(encode);
  }


  @Test
  public void testGetMd5NoAlgo() throws Exception {
    exceptionRule.expect(RuntimeException.class);
    Md5Utils.getMd5("akhilesh", "myAlgo");
  }
}
