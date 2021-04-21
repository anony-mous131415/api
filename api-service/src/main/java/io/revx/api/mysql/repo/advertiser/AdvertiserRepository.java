/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.advertiser;

import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.advertiser.AdvertiserEntity;

/**
 * The Interface AdvertiserRepository.
 */
@Repository
public interface AdvertiserRepository extends JpaRepository<AdvertiserEntity, Long> {

  /**
   * Find by is active.
   *
   * @param isActive the is active
   * @return the list
   */
  List<AdvertiserEntity> findByIsActive(boolean isActive);
  
  List<AdvertiserEntity> findByIdIn(Set<Long> ids);
  
  List<AdvertiserEntity> findAllByLicenseeId(Long licenseeId);
  
  AdvertiserEntity findByIdAndLicenseeId(Long id,Long licenseeId);

  
  /**
   * Activate.
   *
   * @param id the id
   * @return the int
   */
  @Modifying
  @Transactional
  @Query("update AdvertiserEntity a set a.isActive = true where a.id = ?1")
  int activate(Long id);
  
  /**
   * De activate.
   *
   * @param id the id
   * @return the int
   */
  @Modifying
  @Transactional
  @Query("update AdvertiserEntity a set a.isActive = false where a.id = ?1")
  int deActivate(Long id);
  
  
  
}
