package io.revx.core.model.strategy;

public enum TargetingOperator {
  IS_EQUAL_TO(1),
  IS_ANY_OF(2),
  IS_ALL_OF(3),
  IS_SUBSET_OF(4),
  IS_SUPERSET_OF(5),
  IS_NONE_OF(6),
  HAS_ATLEAST_ONE_MATCH(7),
  HAS_NO_MATCH(8),
  IS_GREATER_THAN_OR_EQUAL_TO(9);

  private long id;

  TargetingOperator(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

}
