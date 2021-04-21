package io.revx.api.common;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;

public class PojoTestUtils {
  private static final Validator ACCESSOR_VALIDATOR = ValidatorBuilder.create()
      .with(new GetterTester()).with(new SetterTester())/* .with(new ToStringTester()) */.build();

  public static boolean validateAccessors(final Class<?> clazz) {
    ACCESSOR_VALIDATOR.validate(PojoClassFactory.getPojoClass(clazz));
    return true;
  }

}
