package io.revx.core.utils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import io.revx.core.model.creative.CreativeStatus;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

  public static Map<String, String> getPropertyAndMethodMap(Class<?> pojoClass) {
    Map<String, String> map = new HashMap<>();
    try {
      for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(pojoClass)
          .getPropertyDescriptors()) {

        if (propertyDescriptor.getPropertyType() == BigDecimal.class
            || propertyDescriptor.getPropertyType() == BigInteger.class
            || propertyDescriptor.getPropertyType() == Integer.class
            || propertyDescriptor.getPropertyType() == Long.class
            || propertyDescriptor.getPropertyType() == String.class
            || propertyDescriptor.getPropertyType() == Boolean.class
            || propertyDescriptor.getPropertyType() == CreativeStatus.class) {
          map.put(propertyDescriptor.getName(), propertyDescriptor.getReadMethod().getName());
        }

      }
    } catch (Exception e) {
    }

    return map;
  }
}
