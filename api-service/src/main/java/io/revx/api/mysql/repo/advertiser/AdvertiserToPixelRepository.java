/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.advertiser;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.enums.Status;
import io.revx.api.mysql.entity.advertiser.AdvertiserToPixelEntity;

/**
 * The Interface AdvertiserToPixelRepository.
 */
@Repository
public interface AdvertiserToPixelRepository extends JpaRepository<AdvertiserToPixelEntity, Integer> {

  /**
   * Gets the AS tpixel by adv id.
   *
   * @param status the status
   * @param id the id
   * @return the AS tpixel by adv id
   */
  @Query("SELECT a FROM AdvertiserToPixelEntity a WHERE a.status =?1  AND a.advertiserId = ?2")
  Optional<AdvertiserToPixelEntity> getASTpixelByAdvId(Status status, Long id);
  
 
  AdvertiserToPixelEntity findByStatusAndAdvertiserId(Status status, Long advertiserId);
  
  AdvertiserToPixelEntity findByPixelId(Long pixelId);
  
  /**
   * Find all by advertiser id.
   *
   * @param advertiserId the advertiser id
   * @return the list
   */
  List<AdvertiserToPixelEntity> findAllByAdvertiserId(Long advertiserId);
 
  
}
