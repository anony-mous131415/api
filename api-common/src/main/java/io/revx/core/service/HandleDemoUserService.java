package io.revx.core.service;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jeasy.random.EasyRandom;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import io.revx.core.enums.RoleName;
import io.revx.core.model.BaseModel;
import io.revx.core.response.ApiResponseObject;
import io.revx.core.response.UserInfo;

@SuppressWarnings("rawtypes")
@Component
public class HandleDemoUserService {

  private static Logger logger = LogManager.getLogger(HandleDemoUserService.class);



  public <T> T handleDemoTest(T respObject) {
    if (respObject instanceof ApiResponseObject<?>) {
      Object resp = ((ApiResponseObject) respObject).getRespObject();
      if (resp != null) {
        Field[] fields = resp.getClass().getDeclaredFields();
        for (Field field : fields) {
          updateFiledValueIfKeyIsName(field);

        }
      }

    }
    return respObject;
  }

  public void getPropertyAndMethodMap(Class<?> pojoClass) {
    Map<String, String> map = new HashMap<String, String>();
    try {
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
    } catch (Exception e) {
    }
  }

  private void updateFiledValueIfKeyIsName(Field field) {


  }

  @SuppressWarnings("unchecked")
  public <T> T handleDemo(T respObject) {
   
    if (respObject instanceof UserInfo) {
      UserInfo obj = (UserInfo) respObject;
      updateNameInBaseModelList(obj.getAdvertisers());
      updateNameInBaseModel(obj.getSelectedLicensee(), 1);
      return respObject;
    }
    if (respObject instanceof BaseModel) {
      updateNameInBaseModel((BaseModel) respObject, 1);
      return respObject;
    }
    if (respObject instanceof Collection<?>) {
      updateNameInBaseModelList((Collection<? extends BaseModel>) respObject);
      return respObject;
    }

    return respObject;
  }


  private void updateNameInBaseModelList(Collection<? extends BaseModel> list) {
    if (CollectionUtils.isNotEmpty(list)) {
      AtomicInteger i = new AtomicInteger(1);
      list.forEach(baseModel -> baseModel
          .setName(baseModel.getClass().getSimpleName() + "-" + i.getAndIncrement()));
    }
  }

  private void updateNameInBaseModel(BaseModel baseClass, int index) {
    if (baseClass != null) {
      baseClass.setName(baseClass.getClass().getSimpleName() + "-" + index);
    }
  }


  public boolean isDemoUser() {
    RoleName role = getHighestRoleOfLoginUser();
    return RoleName.DEMO.equals(role);

  }

  private RoleName getHighestRoleOfLoginUser() {
    RoleName role = RoleName.DEMO;
    UsernamePasswordAuthenticationToken auth =
        (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext()
            .getAuthentication();
    if (auth != null && auth.getAuthorities() != null) {
      for (GrantedAuthority roleStr : auth.getAuthorities()) {
        RoleName tmpRole = RoleName.getRoleByName(roleStr.getAuthority());
        // SuperAdmin Will have 0 ordinal lowest means Highest Role
        if (tmpRole != null && tmpRole.ordinal() < role.ordinal()) {
          role = tmpRole;
        }
      }
    }
    logger.debug(" getHighestRoleOfLoginUser : {} ", role);
    return role;
  }

  public static void main(String[] args) {
    HandleDemoUserService serv = new HandleDemoUserService();
    System.out.println("I am Here To check Generic");
    UserInfo ui = new EasyRandom().nextObject(UserInfo.class);
    UserInfo ui2 = serv.handleDemo(ui);
    System.out.println("ui2  " + ui2.getAdvertisers());
  }
}
