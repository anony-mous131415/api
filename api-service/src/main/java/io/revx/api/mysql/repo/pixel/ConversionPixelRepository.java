package io.revx.api.mysql.repo.pixel;

import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.pixel.ConversionPixelEntity;

@Repository
public interface ConversionPixelRepository extends JpaRepository<ConversionPixelEntity, Long> {
  
  List<ConversionPixelEntity> findByIdIn(Set<Long> ids);
  
  @Modifying
  @Transactional
  @Query("update ConversionPixelEntity p set p.active = true where p.id = ?1")
  int activate(Long id);
  
  @Modifying
  @Transactional
  @Query("update ConversionPixelEntity p set p.active = false where p.id = ?1")
  int deActivate(Long id);
  
  
  List<ConversionPixelEntity> findAllByIdIn(List<Long> pixelId);
  
  List<ConversionPixelEntity> findAllByAdvertiserId(Long advertiserId, Pageable page);
  
  List<ConversionPixelEntity> findAllByAdvertiserIdIn(Set<Long> advIds, Pageable page);
  
  List<Long> findAllIdByAdvertiserId(Long AdvertiserId);
  
  ConversionPixelEntity findByIdAndLicenseeId(Long id, Long licenseeId);
  
  ConversionPixelEntity findByIdAndLicenseeIdAndAdvertiserIdIn(Long id, Long licenseeId, List<Long> advertiserId);
}
