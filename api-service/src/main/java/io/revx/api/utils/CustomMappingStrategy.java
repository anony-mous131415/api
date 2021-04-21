package io.revx.api.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import io.revx.core.aop.HideForReadOnlyAccess;
import io.revx.core.enums.RoleName;

public class CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {

  private RoleName currentRole = null;

  public CustomMappingStrategy(RoleName role) {
    super();
    currentRole = role;
  }

  public void setLoginUserRole(RoleName role) {
    currentRole = role;
  }

  @Override
  public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {

    super.setColumnMapping(new String[getAnnotatedFields(bean)]);
    final int numColumns = getAnnotatedFields(bean);
    final int totalFieldNum = findMaxFieldIndex();
    if (!isAnnotationDriven() || numColumns == -1) {
      return super.generateHeader(bean);
    }

    System.out.println("numColumns " + numColumns + " , totalFieldNum " + totalFieldNum);
    String[] header = new String[numColumns];

    BeanField<T> beanField;
    for (int i = 0; i <= totalFieldNum; i++) {
      beanField = findField(i);
      if (beanField != null && isFieldAnnotated(beanField.getField())) {
        String columnHeaderName = extractHeaderName(beanField);
        header[i] = columnHeaderName;
      }
    }
    return header;
  }

  private int getAnnotatedFields(T bean) {
    return (int) Arrays.stream(FieldUtils.getAllFields(bean.getClass()))
        .filter(this::isFieldAnnotated).count();
  }

  private boolean isFieldAnnotated(Field f) {
    return (f.isAnnotationPresent(CsvBindByName.class)
        || f.isAnnotationPresent(CsvCustomBindByName.class)) && !isAccessForFiled(f);
  }

  private boolean isAccessForFiled(Field f) {
    return (f.isAnnotationPresent(HideForReadOnlyAccess.class)
        && (currentRole == null || RoleName.RO == currentRole));
  }

  private String extractHeaderName(final BeanField<T> beanField) {
    if (beanField == null || beanField.getField() == null) {
      return StringUtils.EMPTY;
    }

    Field field = beanField.getField();

    if (field.getDeclaredAnnotationsByType(CsvBindByName.class).length != 0) {
      final CsvBindByName bindByNameAnnotation =
          field.getDeclaredAnnotationsByType(CsvBindByName.class)[0];
      return bindByNameAnnotation.column();
    }

    if (field.getDeclaredAnnotationsByType(CsvCustomBindByName.class).length != 0) {
      final CsvCustomBindByName bindByNameAnnotation =
          field.getDeclaredAnnotationsByType(CsvCustomBindByName.class)[0];
      return bindByNameAnnotation.column();
    }

    return StringUtils.EMPTY;
  }
}
