package io.revx.core.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

// IN this Class Any Value null treated as BigDecimal.ZERO
public class NumberUtils {

  public static BigDecimal addBigDecimal(BigDecimal first, BigDecimal second) {
    if (first == null && second == null)
      return BigDecimal.ZERO;
    else if (first == null || second == null)
      return first != null ? first : second;
    return first.add(second);
  }

  public static BigDecimal substractBigDecimal(BigDecimal first, BigDecimal second) {
    if (first == null && second == null)
      return BigDecimal.ZERO;
    else if (first == null || second == null)
      return first != null ? first : second;
    return first.subtract(second);
  }

  public static BigDecimal divide(BigDecimal first, BigDecimal second) {
    try {
      if (first == null || second == null || second == BigDecimal.ZERO)
        return BigDecimal.ZERO;
      return first.divide(second, 9, RoundingMode.HALF_UP);
    } catch (Exception e) {
      // e.printStackTrace();
    }
    return BigDecimal.ZERO;
  }

  public static BigDecimal multiply(BigDecimal first, BigDecimal second) {
    if (first == null || second == null)
      return BigDecimal.ZERO;
    return first.multiply(second);
  }

  public static long getLongValue(BigInteger value) {
    if (value != null) {
      return value.longValue();
    }
    return -1l;
  }

  public static long getLongValue(BigDecimal value) {
    if (value != null) {
      return value.longValue();
    }
    return -1l;
  }

  public static BigDecimal roundToNDecimalPlace(BigDecimal input) {
    if (input != null) {
      return input.setScale(2, RoundingMode.HALF_EVEN);
    }

    return null;
  }

  public static BigDecimal roundToNDecimalPlace(BigDecimal input, int countAfterDecimal) {
    if (input != null) {
      return input.setScale(countAfterDecimal, RoundingMode.HALF_EVEN);
    }

    return null;
  }

}
