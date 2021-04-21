package io.revx.api.postgres.repo;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;
import io.revx.core.aop.LogMetrics;
import io.revx.core.constant.GraphiteConstants;

@SuppressWarnings({"unchecked", "deprecation"})
@Component
public class PerformanceDataRepositoryImpl {

  @PersistenceContext(unitName = "postgresEntityManager")
  // @PersistenceContext()
  EntityManager entityManager;
  private static Logger logger = LogManager.getLogger(PerformanceDataRepositoryImpl.class);

  @LogMetrics(name = GraphiteConstants.DB + GraphiteConstants.POSTGRES)
  public <T> List<T> queryToDataBase(String sqlQuery, Class<T> resultClass) {
    logger.debug(" entityManager :: " + entityManager);
    Query query = entityManager.createNativeQuery(sqlQuery);
    List<T> list = query.unwrap(org.hibernate.query.Query.class)
        .setResultTransformer(Transformers.aliasToBean(resultClass)).getResultList();
    logger.debug(" Result list :: " + list);
    return list;
  }


}
