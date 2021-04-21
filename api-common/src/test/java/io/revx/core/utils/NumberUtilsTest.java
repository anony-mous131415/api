/**
 * 
 */
package io.revx.core.utils;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author amaurya
 *
 */
public class NumberUtilsTest {

  /**
   * Test method for
   * {@link io.revx.core.utils.NumberUtils#addBigDecimal(java.math.BigDecimal, java.math.BigDecimal)}.
   */
  @Test
  public void testAddBigDecimal() throws Exception {
    BigDecimal first = new BigDecimal(12);
    BigDecimal second = new BigDecimal(10);
    BigDecimal res = NumberUtils.addBigDecimal(first, second);
    Assert.assertNotNull(res);
  }

  /**
   * Test method for
   * {@link io.revx.core.utils.NumberUtils#substractBigDecimal(java.math.BigDecimal, java.math.BigDecimal)}.
   */
  @Test
  public void testSubstractBigDecimal() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.core.utils.NumberUtils#divide(java.math.BigDecimal, java.math.BigDecimal)}.
   */
  @Test
  public void testDivide() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for
   * {@link io.revx.core.utils.NumberUtils#multiply(java.math.BigDecimal, java.math.BigDecimal)}.
   */
  @Test
  public void testMultiply() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

  /**
   * Test method for {@link io.revx.core.utils.NumberUtils#getLongValue(java.math.BigInteger)}.
   */
  @Test
  public void testGetLongValue() throws Exception {
    // TODO
    new RuntimeException("not yet implemented");
  }

}
