package io.revx.common;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.gson.Gson;
import io.revx.core.aop.LogMetrics;
import io.revx.core.model.BaseModel;
import io.revx.core.model.DashboardData;
import io.revx.core.model.DashboardDataComparator;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class TestClass {

  private static Instrumentation instrumentation;


  public static void main(String[] args) throws Exception {
    Test Obj = new EasyRandom().nextObject(Test.class);
    System.out.println(new Gson().toJson(Obj));
    BeanMap beanMap = new BeanMap(Obj);
    for (Object str : beanMap.keySet()) {
      System.out.println(new Gson().toJson(str));
    }
  }

  @LogMetrics(name = "myTest")
  public void myTest() {
    try {
      System.out.println("I am here");
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void bloomFiltertestin(String[] args) throws Exception {
    System.out.println("In main Method ");
    long start = System.currentTimeMillis();
    int size = 10000000;
    BloomFilter<String> filter =
        BloomFilter.create(Funnels.unencodedCharsFunnel(), size, 0.00000000001);

    Set<String> gen = new HashSet<>();
    for (int i = 0; i < size; i++) {
      String str = RandomStringUtils.random(64);
      filter.put(str);
      gen.add(str);
    }
    int count = 0;
    int notInSet = 0;
    for (int i = 0; i < size; i++) {
      String str = RandomStringUtils.random(64);
      if (!filter.mightContain(str)) {
        count++;
      }
      if (!gen.contains(str)) {
        notInSet++;
      }

    }
    long end = System.currentTimeMillis();
    System.out
        .println("Not in Filter " + count + " :: " + notInSet + " :: " + (end - start) + " ms");
    System.out.println(
        "Memory Size filter Object : " + getObjectSize(filter) + " , set :" + getObjectSize(gen));
  }

  public static void premain(String args, Instrumentation inst) {
    instrumentation = inst;
  }

  public static long getObjectSize(Object o) {
    return instrumentation.getObjectSize(o);
  }

  public static String spyFields(Object obj) throws IllegalAccessException {
    StringBuffer buffer = new StringBuffer();
    Field[] fields = obj.getClass().getDeclaredFields();
    Method[] method = obj.getClass().getMethods();
    for (Field f : fields) {
      if (!Modifier.isStatic(f.getModifiers())) {
        buffer.append(f.getType().getName());
        buffer.append(" ");
        buffer.append(f.getName());
        buffer.append("\n");
      }
    }
    for (Method f : method) {
      if (!Modifier.isStatic(f.getModifiers())) {
        buffer.append(f.getParameterCount());
        buffer.append(" ");
        buffer.append(f.getName());
        buffer.append("\n");
      }
    }
    return buffer.toString();
  }

  private static Set<String> printFieldsFor(@SuppressWarnings("rawtypes") Class cls) {
    Set<String> fieldSet = new HashSet<String>();
    System.out.println(cls.getTypeName() != Object.class.getTypeName());
    while (!StringUtils.equalsIgnoreCase(cls.getTypeName(), Object.class.getTypeName())) {
      Field[] fields = cls.getDeclaredFields();
      for (Field field : fields) {
        if (field.getType() == BigDecimal.class || field.getType() == BigInteger.class
            || field.getType() == Long.class || field.getType() == String.class) {
          fieldSet.add(field.getName());
          // System.out.println(field.getName() + " :: " + field.getType());
        }

      }
      cls = cls.getSuperclass();
    }
    return fieldSet;
  }

  private static String getSortField(String sort) {
    if (StringUtils.isNotBlank(sort)) {
      if (StringUtils.endsWith(sort, "-")) {
        return StringUtils.substring(sort, 0, sort.lastIndexOf("-"));
      } else if (StringUtils.endsWith(sort, "+")) {
        return StringUtils.substring(sort, 0, sort.lastIndexOf("+"));
      }
    }
    return "id";
  }

  public static void main1(String[] args) {
    System.out.println(DashboardData.class);
    Set<String> fields = printFieldsFor(DashboardData.class);
    System.out.println(fields);
  }

  public static void main2(String[] args) {

    System.out.println(getSortField(""));

    if (true)
      return;

    @SuppressWarnings("unused")
    EasyRandomParameters parameters = new EasyRandomParameters().seed(123L).objectPoolSize(100)
        .stringLengthRange(5, 10).collectionSizeRange(1, 10).scanClasspathForConcreteTypes(true)
        .overrideDefaultInitialization(false).ignoreRandomizationErrors(true);

    EasyRandom easy = new EasyRandom(parameters);
    List<DashboardData> list = new ArrayList<DashboardData>();
    for (int i = 0; i < 5; i++) {
      DashboardData dd = easy.nextObject(DashboardData.class);
      list.add(dd);
    }
    for (DashboardData dashboardData : list) {
      System.out.println(dashboardData);

    }
    Collections.sort(list, new DashboardDataComparator("id"));

    System.out.println(" SORTED ");
    for (DashboardData dashboardData : list) {
      System.out.println(dashboardData);
    }

    Collections.sort(list, new DashboardDataComparator("day", false));

    System.out.println(" SORTED ");
    for (DashboardData dashboardData : list) {
      System.out.println(dashboardData);
    }

    Collections.sort(list, new DashboardDataComparator("name"));

    System.out.println(" SORTED ");
    for (DashboardData dashboardData : list) {
      System.out.println(dashboardData);

    }
    Collections.sort(list, new DashboardDataComparator("test1"));
    System.out.println(" SORTED ");
    for (DashboardData dashboardData : list) {
      System.out.println(dashboardData);
    }
  }

}


class TestTemp {
  BaseModel model;
  BaseModel againModel;
  int id;
  String name;
  Integer age;

  public BaseModel getModel() {
    return model;
  }

  public void setModel(BaseModel model) {
    this.model = model;
  }

  public BaseModel getAgainModel() {
    return againModel;
  }

  public void setAgainModel(BaseModel againModel) {
    this.againModel = againModel;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

}


class Test {
  BaseModel model;
  int id;
  String name;
  Integer age;
  TestTemp testTemp;

  public BaseModel getModel() {
    return model;
  }

  public void setModel(BaseModel model) {
    this.model = model;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public TestTemp getTestTemp() {
    return testTemp;
  }

  public void setTestTemp(TestTemp testTemp) {
    this.testTemp = testTemp;
  }

}
