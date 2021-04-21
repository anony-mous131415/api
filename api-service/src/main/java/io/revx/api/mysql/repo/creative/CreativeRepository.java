/*
 * @author: ranjan-pritesh
 * @date: 
 */
package io.revx.api.mysql.repo.creative;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import io.revx.api.mysql.entity.creative.CreativeEntity;
import io.revx.core.model.creative.CreativeStatus;
import io.revx.core.model.requests.SearchRequest;

/**
 * The Interface CreativeRepository.
 */
@Repository
public interface CreativeRepository extends JpaRepository<CreativeEntity, Long> {
  
  
  List<CreativeEntity> findAllByAdvertiserId(Long advId);
  
  List<CreativeEntity> findAllByLicenseeId(Long Id);
  
  List<CreativeEntity> findAllByLicenseeIdAndIsRefactored(Long id,Boolean isRefactor);
  
  List<CreativeEntity> findByIdIn(Set<Long> ids);
  
  Optional<Long> findAdvertiserIdById(Long id);
  
  Optional<CreativeEntity> findByIdAndIsRefactored(Long id,Boolean isRefactor);
  
  @Query(value = "select * from Creative where cr_licensee_id = (?1) and cr_is_refactor = 1 and cr_status in (?4) order by cr_id desc limit ?2 offset ?3", nativeQuery = true)
  List<CreativeEntity> findAllCreativesByNativeQuery(long licenseeId, Integer Limit, Integer offset, String status);
  
  @Query(value = "select count(1) from Creative where cr_licensee_id = (?1) and cr_is_refactor = 1 and cr_status in (?2)", nativeQuery = true)
  Integer findAllCreativeCountByLicenseeId(long licenseeId, String status);
  
  @Query(value = "select count(1) from Creative where cr_media_buyer_id = (?1) and cr_is_refactor = 1", nativeQuery = true)
  Integer findAllCreativeCountByAdvertiserId(Integer advertiserId);
  
  @Query(value = "select * from Creative where cr_licensee_id = (?1) and  cr_media_buyer_id = (?5) and cr_is_refactor = 1 and cr_status in (?4) order by cr_id desc limit ?2 offset ?3", nativeQuery = true)  
  List<CreativeEntity> findAdvertiserCreativesByNativeQuery(long licenseeId, Integer Limit, Integer offset, String status , long advertiserId);
  
  @Query(value = "select count(1) from Creative where cr_licensee_id = (?1) and cr_media_buyer_id = (?2) and cr_is_refactor = 1 and cr_status in (?3)", nativeQuery = true)
  Integer findAdvertiserCreativeCountByAdvertiserId(long licenseeId, long advertiserId, String status);
  
  /**
   * Activate.
   *
   * @param id the id
   * @return the int
   */
  @Modifying
  @Transactional
  @Query("update CreativeEntity c set c.status = ?1 where c.id = ?2")
  int activate(CreativeStatus status,Long id);
  
  /**
   * De activate.
   *
   * @param id the id
   * @return the int
   */
  @Modifying
  @Transactional
  @Query("update CreativeEntity c set c.status = ?1 where c.id = ?2")
  int deActivate(CreativeStatus status,Long id);

  
}
