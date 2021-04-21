package io.revx.api.mysql.dco.repo.catalog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.dco.entity.catalog.AdvertiserCatalogVariablesMappingEntity;

@Repository
public interface AdvertiserCatalogVariableMappingRepository extends JpaRepository<AdvertiserCatalogVariablesMappingEntity, Integer> {

  List<AdvertiserCatalogVariablesMappingEntity> findAllByFeedId(Long feedId);
  
  @Query("select e.atomVariable  from AdvertiserCatalogVariablesMappingEntity e where e.feedId in :feedids " )
  List<Long> findAtomVariableInFeedIds(List<Long> feedids);
}
