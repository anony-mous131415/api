package io.revx.querybuilder.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import io.revx.core.exception.ErrorCode;
import io.revx.core.exception.QueryBuilderException;
import io.revx.querybuilder.enums.Filter;
import io.revx.querybuilder.enums.GroupBy;
import io.revx.querybuilder.enums.Interval;
import io.revx.querybuilder.objs.FilterComponent;
import io.revx.querybuilder.objs.PerformanceDataTable;

public class PerfTableQuery {

  private static String DB_DATE_FORMAT = "yyyy-MM-dd";
  private Set<FilterComponent> filterList;
  private GroupBy groupBy;

  private long startTime;
  private long endTime;
  private boolean showUU;

  public PerfTableQuery(GroupBy groupby, Set<FilterComponent> filters, long startTime, long endTime, boolean showUU)
      throws QueryBuilderException {
    this.filterList = filters;
    this.groupBy = groupby;
    this.startTime = startTime;
    this.endTime = endTime;
    this.showUU = showUU;
  }

  public String getQuery() throws QueryBuilderException {
    StringBuilder sb = new StringBuilder();

    // Query Structure :-
    // <SELCECT QUERY> FROM <TABLE NAME> <WHERE CLAUSE> <GROUP BY> <GROUp BY COLUMN>

    // Get Select part
    sb.append(getSelectPart());

    // Add from Table name
    sb.append(" from ");
    sb.append(" " + PerformanceDataTable.getInstance().getTableName());
    sb.append(" ");

    // Add where Clause
    sb.append(getWhereClause());
    sb.append(" ");

    // Add GroupBy Clause
    if (groupBy != null && groupBy != GroupBy.NONE) {
      sb.append(" group by 1 ");
    }
    return sb.toString();
  }

  private String getSelectPart() {
    StringBuilder sb = new StringBuilder();
    sb.append("select ");

    if (this.groupBy != null && this.groupBy != GroupBy.NONE) {
      sb.append(getGroupByString());
      sb.append(" AS ");
      sb.append(this.groupBy.getColumn());
      sb.append(", ");
    }

    sb.append(PerformanceDataTable.getInstance().getSelectClause(showUU));

    return sb.toString();
  }

  private String getWhereClause() throws QueryBuilderException {
    StringBuilder sb = new StringBuilder();
    sb.append(" where tst_date >= '");
    sb.append(getFormattedDate(this.startTime));
    sb.append("' and ");
    sb.append(" tst_date < ");
    sb.append("'").append(getFormattedDate(this.endTime)).append("'");
    sb.append(" ");

    if (filterList == null)
      return sb.toString();

    Map<Filter, Set<Long>> filterMap = new HashMap<>();

    for (FilterComponent filterComp : this.filterList) {
      Set<Long> values = filterMap.get(filterComp.getField());
      if (values == null) {
        values = new HashSet<Long>();
        filterMap.put(filterComp.getField(), values);
      }
      try {
        values.add(Long.parseLong(filterComp.getValue()));
      } catch (Exception e) {
      }

    }

    for (Entry<Filter, Set<Long>> filterComp : filterMap.entrySet()) {

      sb.append(" and ");
      sb.append(getFilterString(filterComp.getKey(), new ArrayList<Long>(filterComp.getValue())));
      sb.append(" ");
    }

    return sb.toString();
  }

  private String getFormattedDate(long epocTime) throws QueryBuilderException {
    try {
      Date date = new Date(epocTime * 1000);
      DateFormat format = new SimpleDateFormat(DB_DATE_FORMAT);
      format.setTimeZone(TimeZone.getTimeZone("GMT"));
      return format.format(date);
    } catch (Exception e) {
      throw new QueryBuilderException(ErrorCode.INVALID_PARAMETER_IN_REQUEST, "starttime/endtime");
    }
  }

  private String getFilterString(Filter filter, List<Long> values) {
    StringBuilder sb = new StringBuilder();
    sb.append(filter.getBigQueryPerfTableColumnName());
    if (values.size() == 1) {
      sb.append(" = ");
      sb.append(values.get(0));
    } else {
      sb.append(" in ( ");
      sb.append(StringUtils.join(values, ","));
      sb.append(" ) ");
    }
    return sb.toString();
  }

  private String getGroupByString() {
    switch (this.groupBy) {
      case DAY:
      case HOUR:
        Interval interval = Interval.fromString(this.groupBy.getColumnNameInTable());
        return interval.getFormula();
      default:
        return this.groupBy.getColumnNameInTable();
    }
  }

}
