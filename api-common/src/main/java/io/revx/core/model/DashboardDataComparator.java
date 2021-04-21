package io.revx.core.model;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class DashboardDataComparator implements Comparator<Object> {
  private static Map<String, Integer> availableFields;
  static {
    availableFields = getAvailableFields(DashboardData.class);
  }
  boolean assendingSort = true;

  public DashboardDataComparator(String fieldToSort) {
    super();
    this.fieldToSort = fieldToSort;
  }

  public DashboardDataComparator(String fieldToSort, boolean assendingSort) {
    super();
    this.fieldToSort = fieldToSort;
    this.assendingSort = assendingSort;
  }

  private String fieldToSort;

  @Override
  public int compare(Object obj1, Object obj2) {
    try {
      if (obj1 == null || obj1 == null) {
        return obj1 != null ? 1 : -1;
      }
      if (!availableFields.containsKey(fieldToSort)) {
        throw new NoSuchFieldException();
      }
      Object value1, value2;
      switch (availableFields.get(fieldToSort)) {
        case 1:
          value1 = obj1.getClass().getSuperclass().getDeclaredField(fieldToSort).get(obj1);
          value2 = obj2.getClass().getSuperclass().getDeclaredField(fieldToSort).get(obj2);
          break;
        case 2:
          value1 = obj1.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldToSort)
              .get(obj1);
          value2 = obj2.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldToSort)
              .get(obj2);
          break;

        default:
          value1 = obj1.getClass().getDeclaredField(fieldToSort).get(obj1);
          value2 = obj2.getClass().getDeclaredField(fieldToSort).get(obj2);
          break;
      }
      return (value1 == value2 ? 0 : campareTo(value1, value2)) * (assendingSort ? 1 : -1);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(" NoSuchFieldException Missing variable " + fieldToSort);
    } catch (ClassCastException e) {
      throw new RuntimeException(fieldToSort + " Mismatch ");
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private int campareTo(Object value1, Object value2) {
    if (value1 == null || value2 == null) {
      return value1 != null ? 1 : -1;
    }
    if (value1 instanceof Long) {
      return ((long) value1 > (long) value2) ? 1 : -1;
    }
    if (value1 instanceof Integer) {
      return ((Integer) value1 > (Integer) value2) ? 1 : -1;
    }
    if (value1 instanceof BigInteger) {
      return (((BigInteger) value1).longValue() > ((BigInteger) value2).longValue()) ? 1 : -1;
    }
    if (value1 instanceof BigDecimal) {
      return (((BigDecimal) value1).doubleValue() > ((BigDecimal) value2).doubleValue()) ? 1 : -1;
    }
    if (value1 instanceof String) {
      return StringUtils.compareIgnoreCase((String) value1, (String) value2);
    }
    return 0;
  }

  public static Map<String, Integer> getAvailableFields(@SuppressWarnings("rawtypes") Class cls) {
    Map<String, Integer> fieldSet = new HashMap<String, Integer>();
    // System.out.println(cls.getTypeName() != Object.class.getTypeName());
    int level = 0;
    while (!StringUtils.equalsIgnoreCase(cls.getTypeName(), Object.class.getTypeName())) {
      Field[] fields = cls.getDeclaredFields();
      for (Field field : fields) {
        if (field.getType() == BigDecimal.class || field.getType() == BigInteger.class
            || field.getType() == Long.class || field.getType() == String.class) {
          fieldSet.put(field.getName(), level);
          // System.out.println(field.getName() + " :: " + field.getType());
        }

      }
      cls = cls.getSuperclass();
      level++;
    }
    return fieldSet;
  }

}
