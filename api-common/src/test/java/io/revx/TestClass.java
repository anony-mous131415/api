package io.revx;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TestClass {

  public static void main(String[] args) throws Exception {}

  public static Map<String, String> getPropertyAndMethodMap(Class<?> pojoClass) throws Exception {
    Map<String, String> map = new HashMap<String, String>();
    for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(pojoClass)
        .getPropertyDescriptors()) {

      if (propertyDescriptor.getPropertyType() == BigDecimal.class
          || propertyDescriptor.getPropertyType() == BigInteger.class
          || propertyDescriptor.getPropertyType() == Long.class
          || propertyDescriptor.getPropertyType() == String.class
          || propertyDescriptor.getPropertyType() == Boolean.class) {
        map.put(propertyDescriptor.getName(), propertyDescriptor.getReadMethod().getName());
      }

    }

    return map;
  }

}
