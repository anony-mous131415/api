package io.revx.api.mysql.dco.repo.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.dco.entity.catalog.FeedInfoStatsEntity;

@Repository
public interface FeedInfoStatsRepository extends JpaRepository<FeedInfoStatsEntity, Long> {

  List<FeedInfoStatsEntity> findAllByFeedIdOrderByLastUpdatedDesc(Long intValue);

  @Query("select count(*) from FeedInfoStatsEntity f where f.feedId =?1  and f.lastUpdated >= ?2")
  Long getTotalCountForFeedId(Long feedId, Long minTime);

  @Query("select count(*) from FeedInfoStatsEntity f where f.status IN (0, 1, 2) and f.feedId =?1  and f.lastUpdated >= ?2")
  Long getSuccessCountForFeedId(Long feedId, Long minTime);


}
