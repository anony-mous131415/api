package io.revx.api.mysql.dco.repo.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.dco.entity.catalog.FeedInfoEntity;

@Repository
public interface FeedInfoRepository extends JpaRepository<FeedInfoEntity, Long> {
  
  
  
  @Query("select e.id  from FeedInfoEntity e where e.advertiserId=?1" )
  List<Long> findIdByAdvertiserid(Long advId);
  
  
  

  List<FeedInfoEntity> findAllByAdvertiserId(Long advertiserId);
  
}
