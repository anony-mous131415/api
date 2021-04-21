package io.revx.core.cache;

import io.revx.core.exception.UnsortableAttributeException;
import io.revx.core.exception.ValidationException;
import io.revx.core.model.BaseEntity;
import io.revx.core.model.pixel.Pixel;
import io.revx.core.model.requests.DashboardFilters;
import io.revx.core.search.filter.ComplexExpression;
import io.revx.core.search.filter.Expression;
import io.revx.core.search.filter.SearchFilterConstants;
import io.revx.core.search.filter.Validator;
import io.revx.core.utils.BeanUtils;
import io.revx.core.utils.StringUtils;
import io.revx.querybuilder.enums.Filter;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Direction;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.expression.And;
import net.sf.ehcache.search.expression.Criteria;
import net.sf.ehcache.search.expression.Or;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@SuppressWarnings({"rawtypes", "unchecked"})
public class DTOEntityCache extends CommonCache<BaseEntity> {

  private static final Logger logger = LogManager.getLogger(DTOEntityCache.class);

  private Map<String, String> fieldsAndMethodMap = null;

  public DTOEntityCache(String name, Class<?> classTypeToCache) {
    if (StringUtils.isBlank(name))
      throw new NullPointerException("name cannot be null or empty");
    this.name = name;
    cache = getCache(classTypeToCache);

  }

  public DTOEntityCache(String name, Class<?> classTypeToCache, Long timeToExpire) {
    this(name, classTypeToCache);
    if (timeToExpire != null && timeToExpire > 0) {
      this.timeToExpire = timeToExpire;
      lastPopulateTime = -this.timeToExpire;
    }
  }

  private synchronized  Cache getCache(Class<?> classTypeToCache) {
    Cache c = cacheManager.getCache(name);
    if (c == null) {
      CacheConfiguration cc = config.clone();
      cc.setName(name);
      fieldsAndMethodMap = BeanUtils.getPropertyAndMethodMap(classTypeToCache);
      fieldsAndMethodMap.put("active", "isActive");
      fieldsAndMethodMap.put("type", "getType().toString");
      
      if (classTypeToCache == Pixel.class) {
        fieldsAndMethodMap.put(Filter.ADVERTISER_ID.getColumn(), "getAdvertiserId");
      }
      if (fieldsAndMethodMap.size() > 0) {
        Searchable searchable = new Searchable();
        cc.addSearchable(searchable);
        for (Entry<String, String> fieldAndMethod : fieldsAndMethodMap.entrySet()) {
          searchable.addSearchAttribute(new SearchAttribute().name(fieldAndMethod.getKey())
              .expression("value." + fieldAndMethod.getValue() + "()"));
        }
      }

      c = new Cache(cc);
      cacheManager.addCache(c);
    }
    return c;
  }

  /*
   * if
   * 
   * sorting not asked for
   * 
   * use getAll(), otherwise sort the cache
   */

  public List<BaseEntity> getAll(List<String> sortOn) throws UnsortableAttributeException {
    if (!cacheAvailable())
      return null;

    if (sortOn == null || sortOn.size() == 0)
      return getAll();

    logger.trace("sorting on " + sortOn);

    try {
      Query query = cache.createQuery().includeValues();
      addSortToQuery(query, sortOn);
      Results results = query.execute();
      return getObjectValues(results);
    } catch (Exception e) {
      logger.info("-->Returing null result set. Got exception while getting data from cache : ", e);
      return null;
    }
  }

  public List<BaseEntity> getAll(Set<DashboardFilters> filters, List<String> sortOn)
      throws UnsortableAttributeException {
    if (!cacheAvailable())
      return null;

    if(CollectionUtils.isEmpty(filters) && CollectionUtils.isEmpty(sortOn))
      return getAll();
    
    try {
      Query query = cache.createQuery().includeValues();
      
      if (CollectionUtils.isNotEmpty(filters)) {
        Criteria c = getCriteria(filters);
        if (c != null)
          query.addCriteria(c);
      }
      
      if (CollectionUtils.isNotEmpty(sortOn))
        addSortToQuery(query, sortOn);
      
      logger.debug(" EHCache Query {} :", query);
      Results results = query.execute();

      return getObjectValues(results);
    } catch (Exception e) {
      logger.info("-->Returing null result set. Got exception while getting data from cache : ", e);
      return null;
    }
  }

  public List<BaseEntity> getAll(String filter, List<String> sortOn, String dataType)
      throws UnsortableAttributeException {
    if (!cacheAvailable())
      return null;

    if (sortOn == null || sortOn.isEmpty())
      return getAll();
    if (filter == null || filter.isEmpty())
      return getAll(sortOn);

    try {
      Query query = cache.createQuery().includeValues();
      Criteria c = getCriteria(filter, dataType);
      if (c != null)
        query.addCriteria(c);
      addSortToQuery(query, sortOn);
      Results results = query.execute();

      return getObjectValues(results);
    } catch (Exception e) {
      logger.info("-->Returing null result set. Got exception while getting data from cache : ", e);
      return null;
    }
  }

  private Criteria getCriteria(Set<DashboardFilters> filters) throws ValidationException {
    ComplexExpression filterExpr = null;
    if (filters != null && filters.size() >= 0)
      filterExpr = Validator.getComplexExpression(filters);

    logger.debug("Filter expression : " + filterExpr);
    return getCriteria(filterExpr);
  }

  private Criteria getCriteria(String filter, String dataType) throws ValidationException {
    ComplexExpression filterExpr = null;
    if (StringUtils.isNotBlank(filter))
      filterExpr = Validator.getComplexExpression(filter, dataType);

    logger.debug("Filter expression : " + filterExpr);
    return getCriteria(filterExpr);
  }

  private Criteria getCriteria(ComplexExpression f) {
    if (f == null)
      return null;

    List<Object> operands = f.getOperands();
    if (operands == null || operands.isEmpty())
      return null;
    if (operands.size() == 1) {
      return getCriteria(operands.get(0));
    } else {
      if (SearchFilterConstants.AND.equals(f.getLogicalOperator())) {
        Criteria c = new And(getCriteria(operands.get(0)), getCriteria(operands.get(1)));
        for (int i = 2; i < operands.size(); i++) {
          c = c.and(getCriteria(operands.get(i)));
        }
        return c;
      } else if (SearchFilterConstants.OR.equals(f.getLogicalOperator())) {
        Criteria c = new Or(getCriteria(operands.get(0)), getCriteria(operands.get(1)));
        for (int i = 2; i < operands.size(); i++) {
          c = c.or(getCriteria(operands.get(i)));
        }
        return c;
      }
      return null;
    }
  }

  private Criteria getCriteria(Object o) {
    if (o instanceof ComplexExpression)
      return getCriteria((ComplexExpression) o);
    else if (o instanceof Expression)
      return getCriteria((Expression) o);
    return null;
  }

  private Criteria getCriteria(Expression e) {
    logger.debug("Expression :" + e);
    Attribute a = cache.getSearchAttribute(e.getDtoField());
    switch (e.getOperator()) {
      case eq:
        return a.eq(e.getValue());
      case ge:
        return a.ge(e.getValue());
      case gt:
        return a.gt(e.getValue());
      case in:
        return a.in((Collection) e.getValue());
      case le:
        return a.le(e.getValue());
      case like:
        String val = (String) e.getValue();
        if ((!(val.startsWith("%")) & !(val.endsWith("%")))) {
          String regexVal = "%" + val + "%";
          return a.ilike(StringUtils.replaceUnEscapedCharacter(regexVal, '%', '*'));
        } else
          return a.ilike(StringUtils.replaceUnEscapedCharacter((String) e.getValue(), '%', '*'));
      case lt:
        return a.lt(e.getValue());
      case ne:
        return a.ne(e.getValue());
      default:
        return null;
    }
  }

  private void addSortToQuery(Query q, List<String> sortOn) throws UnsortableAttributeException {
    for (String s : sortOn) {
      logger.debug("s: " + s + ", name: " + getSortName(s) + ", order: " + getSortOrder(s));
      String sortingEle = getSortName(s);
      if (!isValidElement(sortingEle))
        throw new UnsortableAttributeException(s, "cannot sort on " + sortingEle);
      q.addOrderBy(cache.getSearchAttribute(sortingEle), getSortOrder(s));
    }
  }

  private boolean isValidElement(String sortingEle) {
    logger.debug(" fieldsAndMethodMap {} ", fieldsAndMethodMap);
    if (fieldsAndMethodMap != null) {
      return fieldsAndMethodMap.containsKey(sortingEle);
    }
    return false;
  }

  public void populate(Collection<BaseEntity> data) {
    populateEntity(data);
  }

  public void populate(BaseEntity data) {
    if (data == null)
      return;
    int timeToLive = (int) (timeToExpire/1000);
    Element element = new Element(data.getId(),data);
    element.setTimeToLive(timeToLive);
    cache.put(element);
  }

  public boolean remove(Integer id) {
    if (cache.isElementInMemory(id)) {
      return cache.remove(id);
    }
    return false;
  }

  public void populate(Integer id, BaseEntity data) {
    if (data == null) {
      return;
    }
    int timeToLive = (int) (timeToExpire/1000);
    Element element = new Element(id,data);
    element.setTimeToLive(timeToLive);
    cache.put(element);
  }

  private String getSortName(String s) {
    if (s == null)
      return null;
    if (s.endsWith("+") || s.endsWith("-"))
      return s.substring(0, s.length() - 1);
    return s;
  }

  private Direction getSortOrder(String s) {
    Direction d = Direction.ASCENDING;
    if (s != null) {
      if (s.endsWith("+"))
        return Direction.ASCENDING;
      else if (s.endsWith("-"))
        return Direction.DESCENDING;
      else
        return Direction.ASCENDING;
    }
    return d;
  }

  public void clear() {
    super.clear();
  }
  
  public void removeCacheForKey(String name)
  {
    cacheManager.removeCache(name);
    
  }
}
