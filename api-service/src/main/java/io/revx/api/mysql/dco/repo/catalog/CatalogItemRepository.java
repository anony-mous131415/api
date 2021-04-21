package io.revx.api.mysql.dco.repo.catalog;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.dco.entity.catalog.CatalogItemEntity;

@Repository
public interface CatalogItemRepository extends JpaRepository<CatalogItemEntity, Integer> {
  
  
  @Query("select e  from CatalogItemEntity e where e.advertiserId=?1 and length(e.pageLink)>10" )
    List<CatalogItemEntity> findAllByAdvertiserId(Long id,Pageable pageable);
  
  @Query("select e  from CatalogItemEntity e where e.advertiserId=?1 and e.feedId=?2 and length(e.pageLink)>10" )
  List<CatalogItemEntity> findAllByAdvertiserIdAndFeedId(Long id, Long feedId, Pageable pageable);
    
  
}
