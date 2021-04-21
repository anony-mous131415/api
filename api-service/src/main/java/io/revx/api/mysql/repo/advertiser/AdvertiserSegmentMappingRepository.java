package io.revx.api.mysql.repo.advertiser;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.advertiser.AdvertiserSegmentMappingEntity;

@Repository
public interface AdvertiserSegmentMappingRepository extends JpaRepository<AdvertiserSegmentMappingEntity, Long>{

  List<AdvertiserSegmentMappingEntity> findByAdvertiserIdAndLicenseeId(Long advertiserId, Long licenseeId);
  
  @Query("SELECT a.segmentId FROM AdvertiserSegmentMappingEntity a WHERE a.advertiserId = ?1  AND a.licenseeId = ?2")
  List<Long> findSegmentIdByAdvertiserIdAndLicenseeId(Long advertiserId, Long licenseeId);
  
}
