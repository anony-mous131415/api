package io.revx.api.audience.pojo;

import java.text.ParseException;
import java.util.HashSet;

public enum RuleValueType {
  INTEGER(1),

  LONG(2),

  FLOAT(3),

  DOUBLE(4),

  STRING(5);

  public static final RuleValueType[] TEXT = { STRING };
  public static final RuleValueType[] NUMBER = { INTEGER, LONG, FLOAT, DOUBLE };

  public final int id;

  private RuleValueType(int id) {
      this.id = id;
  }

  public static RuleValueType getRuleValueType(int id) {
      for (RuleValueType ruleValueType : RuleValueType.values()) {
          if (ruleValueType.id == id)
              return ruleValueType;
      }
      throw new RuntimeException("Undefined id.");
  }

  public static RuleValueType getRuleValueType(String literal) {
      for (RuleValueType ruleValueType : RuleValueType.values()) {
          if (ruleValueType.name().equals(literal))
              return ruleValueType;
      }
      throw new RuntimeException("Undefined id.");
  }

  public Object parse(String val, boolean isList) throws ParseException {
      if (isList) {
          switch (this) {
          case INTEGER:
              return new IntegerSet(val);
          case LONG:
              return new LongSet(val);
          case FLOAT:
              return new FloatSet(val);
          case DOUBLE:
              return new DoubleSet(val);
          case STRING:
              return new StringSet(val);
          default:
              throw new RuntimeException(String.format(
                      "Type %s is not yet implemented by this method", this));
          }
      } else {
          switch (this) {
          case INTEGER:
              return Integer.parseInt(val);
          case LONG:
              return Long.parseLong(val);
          case FLOAT:
              return Float.parseFloat(val);
          case DOUBLE:
              return Double.parseDouble(val);
          case STRING:
              return val == null ? null : val.toLowerCase().trim();
          default:
              throw new RuntimeException(String.format(
                      "Type %s is not yet implemented by this method", this));
          }
      }
  }

  public static class LongSet extends HashSet<Long> {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public LongSet(String value) {
          // if (value != null) Let it throw Null Pointer
          for (String s : value.split("\\|")) {
              this.add(Long.parseLong(s));
          }
      }
  }

  public static class IntegerSet extends HashSet<Integer> {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public IntegerSet(String value) {
          // if (value != null) Let it throw Null Pointer
          for (String s : value.split("\\|")) {
              this.add(Integer.parseInt(s));
          }
      }
  }

  public static class DoubleSet extends HashSet<Double> {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public DoubleSet(String value) {
          // if (value != null) Let it throw Null Pointer
          for (String s : value.split("\\|")) {
              this.add(Double.parseDouble(s));
          }
      }
  }

  public static class FloatSet extends HashSet<Float> {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public FloatSet(String value) {
          // if (value != null) Let it throw Null Pointer
          for (String s : value.split("\\|")) {
              this.add(Float.parseFloat(s));
          }
      }
  }

  public static class StringSet extends HashSet<String> {

      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      public StringSet(String value) {
          // if (value != null) Let it throw Null Pointer
          for (String s : value.split("\\|")) {
              this.add(s.toLowerCase().trim());
          }
      }

  }

}
