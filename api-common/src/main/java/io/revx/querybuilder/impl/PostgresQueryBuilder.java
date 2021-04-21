package io.revx.querybuilder.impl;

import java.util.HashSet;
import java.util.Set;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.QueryBuilderException;
import io.revx.querybuilder.QueryBuilder;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.objs.FilterComponent;
import io.revx.querybuilder.query.PerfTableQuery;

public class PostgresQueryBuilder implements QueryBuilder {

  PerfTableQuery fQuery;

  @Override
  public String buildQuery(Long startTime, Long endTime, Set<FilterComponent> filters,
      GroupBy groupby, boolean showUU) throws QueryBuilderException {
    if (startTime > endTime)
      throw new QueryBuilderException(ErrorCode.INVALID_PARAMETER_IN_REQUEST, "startTime");
    this.fQuery = new PerfTableQuery(groupby, filters, startTime, endTime, showUU);
    return fQuery.getQuery();
  }

  public static void main(String[] args) {
    PostgresQueryBuilder mqr = new PostgresQueryBuilder();
    FilterComponent lcFilter = new FilterComponent(Filter.CAMPAIGN_ID, 213);
    FilterComponent advFilter = new FilterComponent(Filter.ADVERTISER_ID, 3213);
    FilterComponent licFilter = new FilterComponent(Filter.LICENSEE_ID, 213);
    Set<FilterComponent> filters = new HashSet<FilterComponent>();
    filters.add(lcFilter);
    filters.add(advFilter);
    filters.add(licFilter);
    try {
      for (GroupBy grp : GroupBy.values()) {
        System.out.println(mqr.buildQuery(12345l, 234454l, filters, grp,true));
      }

    } catch (QueryBuilderException e) {

      e.printStackTrace();
    }
  }
}
