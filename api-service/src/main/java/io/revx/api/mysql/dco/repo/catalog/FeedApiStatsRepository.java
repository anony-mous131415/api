/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.dco.repo.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.dco.entity.catalog.FeedApiStatusEntity;

/**
 * The Interface FeedApiStatsRepository.
 */
@Repository
public interface FeedApiStatsRepository extends JpaRepository<FeedApiStatusEntity, Long> {
  
  /**
   * Find all by order by fas created time desc.
   *
   * @param feedid the feedid
   * @return the list
   */
  @Query(value = "select * from dco.FeedApiStats where fas_feed_id=?1 order by fas_created_time desc", nativeQuery = true)
  List<FeedApiStatusEntity> findAllByOrderByFasCreatedTimeDesc(Long feedid);
  
  
}
