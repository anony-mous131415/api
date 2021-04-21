package io.revx.querybuilder;

import java.util.Set;
import io.revx.core.exception.QueryBuilderException;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.objs.FilterComponent;

public interface QueryBuilder {

  public String buildQuery(Long startTime, Long endTime, Set<FilterComponent> filters,
      GroupBy groupby, boolean hideUU) throws QueryBuilderException;
}
