package io.revx.api.utils;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.revx.api.utility.Util;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.querybuilder.enums.Filter;

public class ServiceUtils {

  private ServiceUtils() { throw new IllegalStateException("Utility class"); }

  public static DashboardFilters getFilterForSearch(String search) {

    DashboardFilters filterSearch = new DashboardFilters();
    if(Util.isNumeric(search)) {
      filterSearch.setColumn(Filter.ID.getColumn()); 
      filterSearch.setValue(String.valueOf(search));
    }
    else {
      filterSearch.setColumn(Filter.NAME.getColumn()); 
      filterSearch.setValue(String.valueOf(search));
    }
     
    return filterSearch;
  }
  
  public static DashboardFilters getFilterForKey(Filter filter, String value) {
    DashboardFilters filterId = new DashboardFilters();
    filterId.setColumn(filter.getColumn()); filterId.setValue(String.valueOf(value));
    return filterId;
  }
  
  public static Pageable getPageable(int  pageNumber, int  pageSize, List<String> sortList) {
    Pageable pageable = null;
    if(CollectionUtils.isEmpty(sortList)) {
      pageable = PageRequest.of(pageNumber, pageSize,  Sort.by(Filter.ID.getColumn()).ascending());
    }else {
      if(sortList.get(0).startsWith("id")) {
        if(sortList.get(0).endsWith("+"))
          pageable = PageRequest.of(pageNumber, pageSize,  Sort.by(Filter.ID.getColumn()).ascending());
        else
          pageable = PageRequest.of(pageNumber, pageSize,  Sort.by(Filter.ID.getColumn()).descending());
      }else {
        if(sortList.get(0).endsWith("+"))
          pageable = PageRequest.of(pageNumber, pageSize,  Sort.by(Filter.NAME.getColumn()).ascending());
        else
          pageable = PageRequest.of(pageNumber, pageSize,  Sort.by(Filter.NAME.getColumn()).descending());
      }
    }
    return pageable;
  }
  
  public static SortBuilder<?> getSortBuilder(String sortKey) {
    SortBuilder<?> sortb = null;
    if (sortKey.endsWith("-")) {
        sortb = SortBuilders.fieldSort(convertToKeyword(sortKey.substring(0, sortKey.length() - 1))).order(SortOrder.DESC);
    } else {
        sortb = SortBuilders.fieldSort(convertToKeyword(sortKey.substring(0, sortKey.length() - 1))).order(SortOrder.ASC);
    }
    return sortb;
  }
  
  /*
   * Converting name to keyword because sort doesn't work on text/string.
   */
  public static String convertToKeyword(String key) {
    if(StringUtils.isBlank(key))
      return null;
    
    if(key.equals(Filter.NAME.getColumn()))
      return StringUtils.join(key,".keyword");
    
    return key;
  }

  /**
   * This filter is added to fetch only skad campaigns under an advertiser
   */
  public static DashboardFilters getSkadDashboardFilter() {
    DashboardFilters skadFilter = new DashboardFilters();
    skadFilter.setColumn("skadTarget");
    skadFilter.setValue("true");
    return skadFilter;
  }
}
